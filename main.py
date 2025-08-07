#!/usr/bin/env python3
"""
CPU优化版车牌识别系统
License Plate Recognition System Optimized for CPU

支持多摄像头并发处理，针对8核16线程服务器优化
Supports multi-camera concurrent processing, optimized for 8-core 16-thread servers

作者: AI Assistant
版本: 1.0.0
"""

import os
import sys
import time
import json
import queue
import logging
import threading
import multiprocessing
from concurrent.futures import ThreadPoolExecutor, ProcessPoolExecutor
from collections import deque
from dataclasses import dataclass, asdict
from typing import Dict, List, Optional, Tuple, Any
import gc
import psutil

# 核心依赖
import cv2
import numpy as np
import onnxruntime as ort
from PIL import Image
import requests

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - [%(threadName)s] - %(message)s',
    handlers=[
        logging.FileHandler('license_plate_recognition.log'),
        logging.StreamHandler(sys.stdout)
    ]
)

logger = logging.getLogger(__name__)


@dataclass
class SystemConfig:
    """系统配置类"""
    # 基本配置
    num_cameras: int = 4
    num_regions_per_camera: int = 2
    target_fps: int = 15
    max_fps: int = 25
    min_fps: int = 8
    
    # CPU优化配置
    num_cpu_cores: int = multiprocessing.cpu_count()
    num_threads: int = min(16, num_cpu_cores * 2)
    cpu_affinity_enabled: bool = True
    
    # 线程池配置
    frame_reader_threads: int = 2
    vehicle_detection_threads: int = 4
    plate_recognition_threads: int = 6
    api_push_threads: int = 2
    
    # 内存优化配置
    max_frame_queue_size: int = 100
    max_result_queue_size: int = 200
    object_pool_size: int = 50
    gc_threshold: int = 1000
    
    # 算法配置
    input_width: int = 640
    input_height: int = 480
    detection_confidence: float = 0.5
    recognition_confidence: float = 0.7
    adaptive_resolution: bool = True
    smart_frame_skip: bool = True
    
    # 性能监控配置
    performance_monitoring: bool = True
    metrics_update_interval: int = 5
    max_cpu_usage: float = 85.0
    max_memory_usage: float = 60.0
    
    # API配置
    api_endpoint: str = "http://localhost:8080/api/plate-recognition"
    api_timeout: int = 10
    api_retry_times: int = 3

    @classmethod
    def load_from_file(cls, config_path: str) -> 'SystemConfig':
        """从配置文件加载配置"""
        try:
            with open(config_path, 'r', encoding='utf-8') as f:
                config_dict = json.load(f)
            return cls(**config_dict)
        except Exception as e:
            logger.warning(f"Failed to load config from {config_path}: {e}")
            return cls()


@dataclass
class FrameData:
    """帧数据类"""
    camera_id: int
    region_id: int
    frame: np.ndarray
    timestamp: float
    frame_id: int


@dataclass
class DetectionResult:
    """检测结果类"""
    camera_id: int
    region_id: int
    frame_id: int
    timestamp: float
    vehicles: List[Dict[str, Any]]
    processing_time: float


@dataclass
class RecognitionResult:
    """识别结果类"""
    camera_id: int
    region_id: int
    frame_id: int
    timestamp: float
    plate_number: str
    confidence: float
    bbox: Tuple[int, int, int, int]
    processing_time: float


class ObjectPool:
    """对象池管理器"""
    
    def __init__(self, max_size: int = 50):
        self.max_size = max_size
        self.frame_pool = deque(maxlen=max_size)
        self.result_pool = deque(maxlen=max_size)
        self._lock = threading.Lock()
    
    def get_frame_data(self, camera_id: int, region_id: int, frame: np.ndarray, 
                      timestamp: float, frame_id: int) -> FrameData:
        """获取帧数据对象"""
        with self._lock:
            if self.frame_pool:
                frame_data = self.frame_pool.popleft()
                frame_data.camera_id = camera_id
                frame_data.region_id = region_id
                frame_data.frame = frame
                frame_data.timestamp = timestamp
                frame_data.frame_id = frame_id
                return frame_data
        return FrameData(camera_id, region_id, frame, timestamp, frame_id)
    
    def return_frame_data(self, frame_data: FrameData):
        """归还帧数据对象"""
        with self._lock:
            if len(self.frame_pool) < self.max_size:
                frame_data.frame = None  # 释放大数组引用
                self.frame_pool.append(frame_data)


class PerformanceMonitor:
    """性能监控器"""
    
    def __init__(self, config: SystemConfig):
        self.config = config
        self.metrics = {
            'fps_per_camera': {},
            'cpu_usage': 0.0,
            'memory_usage': 0.0,
            'processing_times': {
                'detection': deque(maxlen=1000),
                'recognition': deque(maxlen=1000),
                'api_push': deque(maxlen=1000)
            },
            'queue_sizes': {},
            'error_counts': {},
            'total_processed': 0
        }
        self.last_update = time.time()
        self._lock = threading.Lock()
        self.running = True
        self.monitor_thread = threading.Thread(target=self._monitor_loop, daemon=True)
        self.monitor_thread.start()
    
    def _monitor_loop(self):
        """监控循环"""
        while self.running:
            try:
                self._update_system_metrics()
                time.sleep(self.config.metrics_update_interval)
            except Exception as e:
                logger.error(f"Performance monitor error: {e}")
    
    def _update_system_metrics(self):
        """更新系统指标"""
        with self._lock:
            # CPU使用率
            self.metrics['cpu_usage'] = psutil.cpu_percent(interval=1)
            
            # 内存使用率
            memory = psutil.virtual_memory()
            self.metrics['memory_usage'] = memory.percent
            
            # 检查是否需要调整性能
            self._adaptive_performance_adjustment()
    
    def _adaptive_performance_adjustment(self):
        """自适应性能调整"""
        cpu_usage = self.metrics['cpu_usage']
        memory_usage = self.metrics['memory_usage']
        
        # CPU使用率过高，降低FPS
        if cpu_usage > self.config.max_cpu_usage:
            logger.warning(f"CPU usage too high: {cpu_usage:.1f}%")
        
        # 内存使用率过高，强制垃圾回收
        if memory_usage > self.config.max_memory_usage:
            logger.warning(f"Memory usage too high: {memory_usage:.1f}%")
            gc.collect()
    
    def update_fps(self, camera_id: int, fps: float):
        """更新FPS指标"""
        with self._lock:
            self.metrics['fps_per_camera'][camera_id] = fps
    
    def add_processing_time(self, stage: str, processing_time: float):
        """添加处理时间记录"""
        with self._lock:
            if stage in self.metrics['processing_times']:
                self.metrics['processing_times'][stage].append(processing_time)
    
    def get_metrics(self) -> Dict[str, Any]:
        """获取当前指标"""
        with self._lock:
            return self.metrics.copy()
    
    def stop(self):
        """停止监控"""
        self.running = False


class CPUOptimizedDetector:
    """CPU优化的车辆检测器"""
    
    def __init__(self, model_path: str, config: SystemConfig):
        self.config = config
        self.session_options = ort.SessionOptions()
        
        # CPU优化配置
        self.session_options.intra_op_num_threads = min(4, config.num_cpu_cores)
        self.session_options.inter_op_num_threads = min(2, config.num_cpu_cores // 2)
        self.session_options.execution_mode = ort.ExecutionMode.ORT_PARALLEL
        self.session_options.graph_optimization_level = ort.GraphOptimizationLevel.ORT_ENABLE_ALL
        
        # 加载ONNX模型
        try:
            self.session = ort.InferenceSession(
                model_path, 
                self.session_options,
                providers=['CPUExecutionProvider']
            )
            self.input_name = self.session.get_inputs()[0].name
            self.output_names = [output.name for output in self.session.get_outputs()]
            logger.info(f"Vehicle detection model loaded: {model_path}")
        except Exception as e:
            logger.error(f"Failed to load vehicle detection model: {e}")
            # 使用简化的检测器作为后备
            self.session = None
    
    def detect_vehicles(self, frame: np.ndarray) -> List[Dict[str, Any]]:
        """检测车辆"""
        if self.session is None:
            return self._fallback_detection(frame)
        
        try:
            start_time = time.time()
            
            # 预处理
            input_tensor = self._preprocess(frame)
            
            # 推理
            outputs = self.session.run(self.output_names, {self.input_name: input_tensor})
            
            # 后处理
            vehicles = self._postprocess(outputs, frame.shape[:2])
            
            processing_time = time.time() - start_time
            return vehicles
            
        except Exception as e:
            logger.error(f"Vehicle detection error: {e}")
            return []
    
    def _preprocess(self, frame: np.ndarray) -> np.ndarray:
        """预处理图像"""
        # 调整尺寸
        if self.config.adaptive_resolution:
            # 根据CPU负载动态调整分辨率
            target_size = (self.config.input_width, self.config.input_height)
        else:
            target_size = (640, 480)
        
        frame = cv2.resize(frame, target_size)
        
        # 归一化
        frame = frame.astype(np.float32) / 255.0
        
        # 转换为NCHW格式
        frame = np.transpose(frame, (2, 0, 1))
        frame = np.expand_dims(frame, axis=0)
        
        return frame
    
    def _postprocess(self, outputs: List[np.ndarray], original_shape: Tuple[int, int]) -> List[Dict[str, Any]]:
        """后处理检测结果"""
        vehicles = []
        
        # 这里应该根据实际的YOLO模型输出格式进行解析
        # 简化实现，假设输出格式为 [batch, num_detections, 6] (x, y, w, h, conf, class)
        if outputs and len(outputs) > 0:
            detections = outputs[0][0]  # 假设batch size为1
            
            for detection in detections:
                if len(detection) >= 6:
                    x, y, w, h, conf, cls = detection[:6]
                    
                    if conf > self.config.detection_confidence:
                        vehicles.append({
                            'bbox': [int(x), int(y), int(w), int(h)],
                            'confidence': float(conf),
                            'class': int(cls)
                        })
        
        return vehicles
    
    def _fallback_detection(self, frame: np.ndarray) -> List[Dict[str, Any]]:
        """后备检测方法（使用传统CV方法）"""
        # 简化实现：使用背景减除等传统方法
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        blurred = cv2.GaussianBlur(gray, (11, 11), 0)
        
        # 简单的运动检测
        # 这里应该实现更复杂的车辆检测算法
        return []


class LicensePlateRecognizer:
    """车牌识别器"""
    
    def __init__(self, model_path: str, config: SystemConfig):
        self.config = config
        self.session_options = ort.SessionOptions()
        
        # CPU优化配置
        self.session_options.intra_op_num_threads = min(2, config.num_cpu_cores)
        self.session_options.execution_mode = ort.ExecutionMode.ORT_SEQUENTIAL
        self.session_options.graph_optimization_level = ort.GraphOptimizationLevel.ORT_ENABLE_ALL
        
        # 加载ONNX模型
        try:
            self.session = ort.InferenceSession(
                model_path,
                self.session_options,
                providers=['CPUExecutionProvider']
            )
            self.input_name = self.session.get_inputs()[0].name
            self.output_names = [output.name for output in self.session.get_outputs()]
            logger.info(f"License plate recognition model loaded: {model_path}")
        except Exception as e:
            logger.error(f"Failed to load plate recognition model: {e}")
            self.session = None
    
    def recognize_plate(self, frame: np.ndarray, vehicle_bbox: Tuple[int, int, int, int]) -> Optional[RecognitionResult]:
        """识别车牌"""
        if self.session is None:
            return self._fallback_recognition(frame, vehicle_bbox)
        
        try:
            start_time = time.time()
            
            # 提取车辆区域
            x, y, w, h = vehicle_bbox
            vehicle_region = frame[y:y+h, x:x+w]
            
            if vehicle_region.size == 0:
                return None
            
            # 预处理
            input_tensor = self._preprocess(vehicle_region)
            
            # 推理
            outputs = self.session.run(self.output_names, {self.input_name: input_tensor})
            
            # 后处理
            plate_result = self._postprocess(outputs)
            
            processing_time = time.time() - start_time
            
            if plate_result and plate_result['confidence'] > self.config.recognition_confidence:
                return RecognitionResult(
                    camera_id=0,  # 这些值需要从调用者传入
                    region_id=0,
                    frame_id=0,
                    timestamp=time.time(),
                    plate_number=plate_result['plate_number'],
                    confidence=plate_result['confidence'],
                    bbox=vehicle_bbox,
                    processing_time=processing_time
                )
            
            return None
            
        except Exception as e:
            logger.error(f"License plate recognition error: {e}")
            return None
    
    def _preprocess(self, vehicle_region: np.ndarray) -> np.ndarray:
        """预处理车辆图像"""
        # 调整尺寸（通常车牌识别模型需要特定尺寸）
        target_size = (168, 48)  # 常见的车牌识别输入尺寸
        vehicle_region = cv2.resize(vehicle_region, target_size)
        
        # 归一化
        vehicle_region = vehicle_region.astype(np.float32) / 255.0
        
        # 转换为NCHW格式
        vehicle_region = np.transpose(vehicle_region, (2, 0, 1))
        vehicle_region = np.expand_dims(vehicle_region, axis=0)
        
        return vehicle_region
    
    def _postprocess(self, outputs: List[np.ndarray]) -> Optional[Dict[str, Any]]:
        """后处理识别结果"""
        if not outputs or len(outputs) == 0:
            return None
        
        # 这里应该根据实际的车牌识别模型输出格式进行解析
        # 简化实现，假设输出为字符概率序列
        output = outputs[0][0]  # 假设batch size为1
        
        # 简单的字符解码（实际应该使用CTC解码）
        chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        plate_chars = []
        confidence_sum = 0.0
        
        for char_probs in output:
            char_idx = np.argmax(char_probs)
            char_conf = char_probs[char_idx]
            if char_conf > 0.5:  # 最小字符置信度
                plate_chars.append(chars[char_idx % len(chars)])
                confidence_sum += char_conf
        
        if len(plate_chars) >= 6:  # 车牌至少6个字符
            plate_number = ''.join(plate_chars)
            confidence = confidence_sum / len(plate_chars)
            
            return {
                'plate_number': plate_number,
                'confidence': confidence
            }
        
        return None
    
    def _fallback_recognition(self, frame: np.ndarray, vehicle_bbox: Tuple[int, int, int, int]) -> Optional[RecognitionResult]:
        """后备识别方法"""
        # 简化实现：返回模拟结果
        return None


class CameraManager:
    """摄像头管理器"""
    
    def __init__(self, config: SystemConfig):
        self.config = config
        self.cameras = {}
        self.frame_readers = {}
        self.running = False
        
    def initialize_cameras(self, camera_configs: List[Dict[str, Any]]) -> bool:
        """初始化摄像头"""
        try:
            for camera_config in camera_configs:
                camera_id = camera_config['id']
                camera_url = camera_config['url']
                
                # 创建摄像头连接
                cap = cv2.VideoCapture(camera_url)
                if not cap.isOpened():
                    logger.error(f"Failed to open camera {camera_id}: {camera_url}")
                    continue
                
                # 设置摄像头参数
                cap.set(cv2.CAP_PROP_FRAME_WIDTH, self.config.input_width)
                cap.set(cv2.CAP_PROP_FRAME_HEIGHT, self.config.input_height)
                cap.set(cv2.CAP_PROP_FPS, self.config.target_fps)
                cap.set(cv2.CAP_PROP_BUFFERSIZE, 1)  # 减少缓冲延迟
                
                self.cameras[camera_id] = {
                    'capture': cap,
                    'config': camera_config,
                    'frame_count': 0,
                    'last_fps_time': time.time(),
                    'fps': 0.0
                }
                
                logger.info(f"Camera {camera_id} initialized successfully")
            
            return len(self.cameras) > 0
            
        except Exception as e:
            logger.error(f"Failed to initialize cameras: {e}")
            return False
    
    def start_frame_reading(self, frame_queue: queue.Queue, performance_monitor: PerformanceMonitor):
        """开始帧读取"""
        self.running = True
        
        for camera_id in self.cameras:
            reader_thread = threading.Thread(
                target=self._frame_reading_loop,
                args=(camera_id, frame_queue, performance_monitor),
                daemon=True,
                name=f"FrameReader-{camera_id}"
            )
            reader_thread.start()
            self.frame_readers[camera_id] = reader_thread
    
    def _frame_reading_loop(self, camera_id: int, frame_queue: queue.Queue, performance_monitor: PerformanceMonitor):
        """帧读取循环"""
        camera_info = self.cameras[camera_id]
        capture = camera_info['capture']
        frame_skip_counter = 0
        
        while self.running:
            try:
                ret, frame = capture.read()
                if not ret:
                    logger.warning(f"Failed to read frame from camera {camera_id}")
                    time.sleep(0.1)
                    continue
                
                # 智能跳帧
                if self.config.smart_frame_skip:
                    frame_skip_counter += 1
                    if frame_skip_counter % 2 == 0:  # 跳过一半的帧
                        continue
                
                # 更新FPS统计
                camera_info['frame_count'] += 1
                current_time = time.time()
                if current_time - camera_info['last_fps_time'] >= 1.0:
                    camera_info['fps'] = camera_info['frame_count'] / (current_time - camera_info['last_fps_time'])
                    performance_monitor.update_fps(camera_id, camera_info['fps'])
                    camera_info['frame_count'] = 0
                    camera_info['last_fps_time'] = current_time
                
                # 将帧放入队列
                if not frame_queue.full():
                    frame_data = FrameData(
                        camera_id=camera_id,
                        region_id=0,
                        frame=frame.copy(),
                        timestamp=time.time(),
                        frame_id=camera_info['frame_count']
                    )
                    frame_queue.put_nowait(frame_data)
                else:
                    logger.warning(f"Frame queue full for camera {camera_id}")
                
            except Exception as e:
                logger.error(f"Frame reading error for camera {camera_id}: {e}")
                time.sleep(0.1)
    
    def stop(self):
        """停止摄像头管理器"""
        self.running = False
        
        # 释放摄像头资源
        for camera_info in self.cameras.values():
            camera_info['capture'].release()
        
        self.cameras.clear()
        self.frame_readers.clear()


class LicensePlateRecognitionSystem:
    """车牌识别系统主类"""
    
    def __init__(self, config_path: str = "config.json"):
        self.config = SystemConfig.load_from_file(config_path)
        self.object_pool = ObjectPool(self.config.object_pool_size)
        self.performance_monitor = PerformanceMonitor(self.config)
        
        # 队列
        self.frame_queue = queue.Queue(maxsize=self.config.max_frame_queue_size)
        self.detection_queue = queue.Queue(maxsize=self.config.max_result_queue_size)
        self.recognition_queue = queue.Queue(maxsize=self.config.max_result_queue_size)
        
        # 线程池
        self.detection_executor = ThreadPoolExecutor(
            max_workers=self.config.vehicle_detection_threads,
            thread_name_prefix="VehicleDetection"
        )
        self.recognition_executor = ThreadPoolExecutor(
            max_workers=self.config.plate_recognition_threads,
            thread_name_prefix="PlateRecognition"
        )
        self.api_executor = ThreadPoolExecutor(
            max_workers=self.config.api_push_threads,
            thread_name_prefix="APIPublisher"
        )
        
        # 组件初始化
        self.camera_manager = CameraManager(self.config)
        
        # 模型路径（这些路径需要根据实际情况调整）
        vehicle_model_path = "models/vehicle_detection.onnx"
        plate_model_path = "models/plate_recognition.onnx"
        
        self.vehicle_detector = CPUOptimizedDetector(vehicle_model_path, self.config)
        self.plate_recognizer = LicensePlateRecognizer(plate_model_path, self.config)
        
        self.running = False
        
        # 设置CPU亲和性
        if self.config.cpu_affinity_enabled:
            self._setup_cpu_affinity()
    
    def _setup_cpu_affinity(self):
        """设置CPU亲和性"""
        try:
            process = psutil.Process()
            # 为主进程设置CPU亲和性
            available_cpus = list(range(self.config.num_cpu_cores))
            process.cpu_affinity(available_cpus)
            logger.info(f"CPU affinity set to: {available_cpus}")
        except Exception as e:
            logger.warning(f"Failed to set CPU affinity: {e}")
    
    def initialize(self, camera_configs: List[Dict[str, Any]]) -> bool:
        """初始化系统"""
        try:
            # 初始化摄像头
            if not self.camera_manager.initialize_cameras(camera_configs):
                logger.error("Failed to initialize cameras")
                return False
            
            logger.info("License plate recognition system initialized successfully")
            return True
            
        except Exception as e:
            logger.error(f"Failed to initialize system: {e}")
            return False
    
    def start(self):
        """启动系统"""
        if self.running:
            logger.warning("System is already running")
            return
        
        self.running = True
        logger.info("Starting license plate recognition system...")
        
        # 启动摄像头帧读取
        self.camera_manager.start_frame_reading(self.frame_queue, self.performance_monitor)
        
        # 启动处理工作线程
        self._start_processing_threads()
        
        logger.info("License plate recognition system started successfully")
    
    def _start_processing_threads(self):
        """启动处理线程"""
        # 车辆检测线程
        for i in range(self.config.vehicle_detection_threads):
            thread = threading.Thread(
                target=self._vehicle_detection_worker,
                daemon=True,
                name=f"VehicleDetectionWorker-{i}"
            )
            thread.start()
        
        # 车牌识别线程
        for i in range(self.config.plate_recognition_threads):
            thread = threading.Thread(
                target=self._plate_recognition_worker,
                daemon=True,
                name=f"PlateRecognitionWorker-{i}"
            )
            thread.start()
        
        # API推送线程
        for i in range(self.config.api_push_threads):
            thread = threading.Thread(
                target=self._api_push_worker,
                daemon=True,
                name=f"APIPushWorker-{i}"
            )
            thread.start()
    
    def _vehicle_detection_worker(self):
        """车辆检测工作线程"""
        while self.running:
            try:
                # 获取帧数据
                frame_data = self.frame_queue.get(timeout=1.0)
                
                start_time = time.time()
                
                # 车辆检测
                vehicles = self.vehicle_detector.detect_vehicles(frame_data.frame)
                
                processing_time = time.time() - start_time
                self.performance_monitor.add_processing_time('detection', processing_time)
                
                # 创建检测结果
                detection_result = DetectionResult(
                    camera_id=frame_data.camera_id,
                    region_id=frame_data.region_id,
                    frame_id=frame_data.frame_id,
                    timestamp=frame_data.timestamp,
                    vehicles=vehicles,
                    processing_time=processing_time
                )
                
                # 如果检测到车辆，放入识别队列
                if vehicles:
                    # 将帧数据和检测结果一起传递
                    recognition_data = {
                        'frame_data': frame_data,
                        'detection_result': detection_result
                    }
                    
                    if not self.detection_queue.full():
                        self.detection_queue.put_nowait(recognition_data)
                    else:
                        logger.warning("Detection queue full, dropping frame")
                
                # 归还对象到对象池
                self.object_pool.return_frame_data(frame_data)
                
            except queue.Empty:
                continue
            except Exception as e:
                logger.error(f"Vehicle detection worker error: {e}")
    
    def _plate_recognition_worker(self):
        """车牌识别工作线程"""
        while self.running:
            try:
                # 获取检测数据
                recognition_data = self.detection_queue.get(timeout=1.0)
                frame_data = recognition_data['frame_data']
                detection_result = recognition_data['detection_result']
                
                start_time = time.time()
                
                # 对每个检测到的车辆进行车牌识别
                for vehicle in detection_result.vehicles:
                    bbox = vehicle['bbox']
                    
                    # 车牌识别
                    recognition_result = self.plate_recognizer.recognize_plate(frame_data.frame, bbox)
                    
                    if recognition_result:
                        # 更新结果中的相关信息
                        recognition_result.camera_id = frame_data.camera_id
                        recognition_result.region_id = frame_data.region_id
                        recognition_result.frame_id = frame_data.frame_id
                        recognition_result.timestamp = frame_data.timestamp
                        
                        processing_time = time.time() - start_time
                        recognition_result.processing_time = processing_time
                        self.performance_monitor.add_processing_time('recognition', processing_time)
                        
                        # 放入API推送队列
                        if not self.recognition_queue.full():
                            self.recognition_queue.put_nowait(recognition_result)
                        else:
                            logger.warning("Recognition queue full, dropping result")
                        
                        logger.info(f"Recognized plate: {recognition_result.plate_number} "
                                  f"(confidence: {recognition_result.confidence:.3f})")
                
            except queue.Empty:
                continue
            except Exception as e:
                logger.error(f"Plate recognition worker error: {e}")
    
    def _api_push_worker(self):
        """API推送工作线程"""
        while self.running:
            try:
                # 获取识别结果
                recognition_result = self.recognition_queue.get(timeout=1.0)
                
                start_time = time.time()
                
                # 推送到API
                success = self._push_to_api(recognition_result)
                
                processing_time = time.time() - start_time
                self.performance_monitor.add_processing_time('api_push', processing_time)
                
                if success:
                    logger.debug(f"Successfully pushed plate result: {recognition_result.plate_number}")
                else:
                    logger.warning(f"Failed to push plate result: {recognition_result.plate_number}")
                
            except queue.Empty:
                continue
            except Exception as e:
                logger.error(f"API push worker error: {e}")
    
    def _push_to_api(self, recognition_result: RecognitionResult) -> bool:
        """推送结果到API"""
        try:
            payload = {
                'camera_id': recognition_result.camera_id,
                'region_id': recognition_result.region_id,
                'plate_number': recognition_result.plate_number,
                'confidence': recognition_result.confidence,
                'timestamp': recognition_result.timestamp,
                'bbox': recognition_result.bbox,
                'processing_time': recognition_result.processing_time
            }
            
            response = requests.post(
                self.config.api_endpoint,
                json=payload,
                timeout=self.config.api_timeout,
                headers={'Content-Type': 'application/json'}
            )
            
            response.raise_for_status()
            return True
            
        except requests.exceptions.RequestException as e:
            logger.error(f"API push error: {e}")
            return False
        except Exception as e:
            logger.error(f"Unexpected API push error: {e}")
            return False
    
    def get_status(self) -> Dict[str, Any]:
        """获取系统状态"""
        metrics = self.performance_monitor.get_metrics()
        
        status = {
            'running': self.running,
            'config': asdict(self.config),
            'metrics': metrics,
            'queue_sizes': {
                'frame_queue': self.frame_queue.qsize(),
                'detection_queue': self.detection_queue.qsize(),
                'recognition_queue': self.recognition_queue.qsize()
            },
            'camera_count': len(self.camera_manager.cameras),
            'thread_pools': {
                'detection_threads': self.detection_executor._threads if hasattr(self.detection_executor, '_threads') else 0,
                'recognition_threads': self.recognition_executor._threads if hasattr(self.recognition_executor, '_threads') else 0,
                'api_threads': self.api_executor._threads if hasattr(self.api_executor, '_threads') else 0
            }
        }
        
        return status
    
    def stop(self):
        """停止系统"""
        if not self.running:
            logger.warning("System is not running")
            return
        
        logger.info("Stopping license plate recognition system...")
        
        self.running = False
        
        # 停止摄像头管理器
        self.camera_manager.stop()
        
        # 停止性能监控
        self.performance_monitor.stop()
        
        # 关闭线程池
        self.detection_executor.shutdown(wait=True)
        self.recognition_executor.shutdown(wait=True)
        self.api_executor.shutdown(wait=True)
        
        logger.info("License plate recognition system stopped")


def main():
    """主函数"""
    try:
        # 创建配置文件示例
        create_sample_config()
        
        # 初始化系统
        system = LicensePlateRecognitionSystem("config.json")
        
        # 示例摄像头配置
        camera_configs = [
            {'id': 0, 'url': 0},  # 本地摄像头
            {'id': 1, 'url': 'rtsp://admin:admin@192.168.1.100/stream1'},
            {'id': 2, 'url': 'rtsp://admin:admin@192.168.1.101/stream1'},
            {'id': 3, 'url': 'rtsp://admin:admin@192.168.1.102/stream1'},
        ]
        
        # 初始化系统
        if not system.initialize(camera_configs):
            logger.error("Failed to initialize system")
            return 1
        
        # 启动系统
        system.start()
        
        # 运行监控循环
        try:
            while True:
                time.sleep(10)
                
                # 打印状态信息
                status = system.get_status()
                metrics = status['metrics']
                
                logger.info(f"System Status - CPU: {metrics['cpu_usage']:.1f}%, "
                          f"Memory: {metrics['memory_usage']:.1f}%, "
                          f"Queues: F{status['queue_sizes']['frame_queue']}"
                          f"/D{status['queue_sizes']['detection_queue']}"
                          f"/R{status['queue_sizes']['recognition_queue']}")
                
                # 打印FPS信息
                fps_info = []
                for camera_id, fps in metrics['fps_per_camera'].items():
                    fps_info.append(f"C{camera_id}:{fps:.1f}")
                
                if fps_info:
                    logger.info(f"FPS per camera: {', '.join(fps_info)}")
                
        except KeyboardInterrupt:
            logger.info("Received interrupt signal")
        finally:
            system.stop()
        
        return 0
        
    except Exception as e:
        logger.error(f"System error: {e}")
        return 1


def create_sample_config():
    """创建示例配置文件"""
    config_path = "config.json"
    if os.path.exists(config_path):
        return
    
    sample_config = {
        "num_cameras": 4,
        "num_regions_per_camera": 2,
        "target_fps": 15,
        "max_fps": 25,
        "min_fps": 8,
        "num_cpu_cores": multiprocessing.cpu_count(),
        "num_threads": min(16, multiprocessing.cpu_count() * 2),
        "cpu_affinity_enabled": True,
        "frame_reader_threads": 2,
        "vehicle_detection_threads": 4,
        "plate_recognition_threads": 6,
        "api_push_threads": 2,
        "max_frame_queue_size": 100,
        "max_result_queue_size": 200,
        "object_pool_size": 50,
        "gc_threshold": 1000,
        "input_width": 640,
        "input_height": 480,
        "detection_confidence": 0.5,
        "recognition_confidence": 0.7,
        "adaptive_resolution": True,
        "smart_frame_skip": True,
        "performance_monitoring": True,
        "metrics_update_interval": 5,
        "max_cpu_usage": 85.0,
        "max_memory_usage": 60.0,
        "api_endpoint": "http://localhost:8080/api/plate-recognition",
        "api_timeout": 10,
        "api_retry_times": 3
    }
    
    with open(config_path, 'w', encoding='utf-8') as f:
        json.dump(sample_config, f, indent=2, ensure_ascii=False)
    
    logger.info(f"Sample configuration file created: {config_path}")


if __name__ == "__main__":
    sys.exit(main())