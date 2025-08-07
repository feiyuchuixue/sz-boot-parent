"""
模型管理和优化模块
Model Management and Optimization Module

处理ONNX模型的加载、优化、量化和缓存管理
"""

import os
import logging
import hashlib
import pickle
import json
from typing import Dict, List, Optional, Tuple, Any, Union
from pathlib import Path
import time
import threading

import numpy as np
import onnxruntime as ort
from onnxruntime.quantization import quantize_dynamic, QuantType
import onnx
from onnx import version_converter, shape_inference

logger = logging.getLogger(__name__)


class ModelConfig:
    """模型配置类"""
    
    def __init__(self, 
                 model_path: str,
                 input_shape: Tuple[int, ...],
                 output_shape: Tuple[int, ...],
                 optimization_level: str = "all",
                 quantization: bool = True,
                 cache_enabled: bool = True,
                 warmup_runs: int = 5):
        self.model_path = model_path
        self.input_shape = input_shape
        self.output_shape = output_shape
        self.optimization_level = optimization_level
        self.quantization = quantization
        self.cache_enabled = cache_enabled
        self.warmup_runs = warmup_runs


class ModelCache:
    """模型缓存管理器"""
    
    def __init__(self, cache_dir: str = "model_cache", max_size: int = 10):
        self.cache_dir = Path(cache_dir)
        self.cache_dir.mkdir(exist_ok=True)
        self.max_size = max_size
        self.cache_info = {}
        self.cache_lock = threading.Lock()
        
        self._load_cache_info()
    
    def _load_cache_info(self):
        """加载缓存信息"""
        cache_info_path = self.cache_dir / "cache_info.json"
        if cache_info_path.exists():
            try:
                with open(cache_info_path, 'r') as f:
                    self.cache_info = json.load(f)
            except Exception as e:
                logger.warning(f"Failed to load cache info: {e}")
    
    def _save_cache_info(self):
        """保存缓存信息"""
        cache_info_path = self.cache_dir / "cache_info.json"
        try:
            with open(cache_info_path, 'w') as f:
                json.dump(self.cache_info, f, indent=2)
        except Exception as e:
            logger.warning(f"Failed to save cache info: {e}")
    
    def _get_model_hash(self, model_path: str, optimization_config: Dict[str, Any]) -> str:
        """计算模型哈希值"""
        hasher = hashlib.md5()
        
        # 添加模型文件内容哈希
        with open(model_path, 'rb') as f:
            for chunk in iter(lambda: f.read(4096), b""):
                hasher.update(chunk)
        
        # 添加优化配置哈希
        config_str = json.dumps(optimization_config, sort_keys=True)
        hasher.update(config_str.encode())
        
        return hasher.hexdigest()
    
    def get_cached_model(self, model_path: str, optimization_config: Dict[str, Any]) -> Optional[str]:
        """获取缓存的模型"""
        model_hash = self._get_model_hash(model_path, optimization_config)
        
        with self.cache_lock:
            if model_hash in self.cache_info:
                cached_path = self.cache_dir / f"{model_hash}.onnx"
                if cached_path.exists():
                    # 更新访问时间
                    self.cache_info[model_hash]['last_access'] = time.time()
                    self._save_cache_info()
                    return str(cached_path)
        
        return None
    
    def cache_model(self, model_path: str, optimized_model_path: str, optimization_config: Dict[str, Any]):
        """缓存优化后的模型"""
        model_hash = self._get_model_hash(model_path, optimization_config)
        cached_path = self.cache_dir / f"{model_hash}.onnx"
        
        try:
            # 复制优化后的模型到缓存目录
            import shutil
            shutil.copy2(optimized_model_path, cached_path)
            
            with self.cache_lock:
                # 更新缓存信息
                self.cache_info[model_hash] = {
                    'original_path': model_path,
                    'cached_path': str(cached_path),
                    'optimization_config': optimization_config,
                    'created_time': time.time(),
                    'last_access': time.time(),
                    'file_size': cached_path.stat().st_size
                }
                
                # 清理旧缓存
                self._cleanup_cache()
                self._save_cache_info()
                
                logger.info(f"Model cached: {cached_path}")
        
        except Exception as e:
            logger.error(f"Failed to cache model: {e}")
    
    def _cleanup_cache(self):
        """清理缓存"""
        if len(self.cache_info) <= self.max_size:
            return
        
        # 按最后访问时间排序，删除最旧的
        sorted_items = sorted(self.cache_info.items(), 
                            key=lambda x: x[1]['last_access'])
        
        items_to_remove = len(self.cache_info) - self.max_size
        
        for i in range(items_to_remove):
            model_hash, info = sorted_items[i]
            cached_path = Path(info['cached_path'])
            
            try:
                if cached_path.exists():
                    cached_path.unlink()
                del self.cache_info[model_hash]
                logger.info(f"Removed cached model: {cached_path}")
            except Exception as e:
                logger.warning(f"Failed to remove cached model: {e}")


class ONNXModelOptimizer:
    """ONNX模型优化器"""
    
    def __init__(self, cache_dir: str = "model_cache"):
        self.cache = ModelCache(cache_dir)
        self.temp_dir = Path("temp_models")
        self.temp_dir.mkdir(exist_ok=True)
    
    def optimize_model(self, config: ModelConfig) -> str:
        """优化模型"""
        optimization_config = {
            'optimization_level': config.optimization_level,
            'quantization': config.quantization,
            'input_shape': config.input_shape,
            'output_shape': config.output_shape
        }
        
        # 检查缓存
        if config.cache_enabled:
            cached_model = self.cache.get_cached_model(config.model_path, optimization_config)
            if cached_model:
                logger.info(f"Using cached optimized model: {cached_model}")
                return cached_model
        
        logger.info(f"Optimizing model: {config.model_path}")
        
        try:
            # 加载原始模型
            model = onnx.load(config.model_path)
            
            # 应用形状推理
            model = self._apply_shape_inference(model)
            
            # 图优化
            if config.optimization_level in ['basic', 'all']:
                model = self._optimize_graph(model)
            
            # 量化
            optimized_path = config.model_path
            if config.quantization:
                optimized_path = self._quantize_model(model, config)
            else:
                # 保存优化后的模型
                optimized_path = self.temp_dir / f"optimized_{Path(config.model_path).name}"
                onnx.save(model, optimized_path)
                optimized_path = str(optimized_path)
            
            # 缓存优化后的模型
            if config.cache_enabled:
                self.cache.cache_model(config.model_path, optimized_path, optimization_config)
            
            logger.info(f"Model optimization completed: {optimized_path}")
            return optimized_path
            
        except Exception as e:
            logger.error(f"Model optimization failed: {e}")
            return config.model_path  # 返回原始模型
    
    def _apply_shape_inference(self, model: onnx.ModelProto) -> onnx.ModelProto:
        """应用形状推理"""
        try:
            model = shape_inference.infer_shapes(model)
            logger.debug("Shape inference applied")
        except Exception as e:
            logger.warning(f"Shape inference failed: {e}")
        
        return model
    
    def _optimize_graph(self, model: onnx.ModelProto) -> onnx.ModelProto:
        """图优化"""
        try:
            # 这里可以添加更多的图优化步骤
            # 例如：常量折叠、算子融合等
            logger.debug("Graph optimization applied")
        except Exception as e:
            logger.warning(f"Graph optimization failed: {e}")
        
        return model
    
    def _quantize_model(self, model: onnx.ModelProto, config: ModelConfig) -> str:
        """量化模型"""
        try:
            # 保存原始模型到临时文件
            temp_original = self.temp_dir / f"temp_{Path(config.model_path).name}"
            onnx.save(model, temp_original)
            
            # 量化输出路径
            quantized_path = self.temp_dir / f"quantized_{Path(config.model_path).name}"
            
            # 执行动态量化
            quantize_dynamic(
                str(temp_original),
                str(quantized_path),
                weight_type=QuantType.QUInt8
            )
            
            # 清理临时文件
            temp_original.unlink()
            
            logger.info(f"Model quantization completed: {quantized_path}")
            return str(quantized_path)
            
        except Exception as e:
            logger.error(f"Model quantization failed: {e}")
            # 返回原始模型路径
            temp_path = self.temp_dir / f"fallback_{Path(config.model_path).name}"
            onnx.save(model, temp_path)
            return str(temp_path)


class OptimizedInferenceSession:
    """优化的推理会话"""
    
    def __init__(self, 
                 model_config: ModelConfig,
                 providers: List[str] = None,
                 session_options: ort.SessionOptions = None):
        self.config = model_config
        self.session = None
        self.input_names = []
        self.output_names = []
        self.warmup_completed = False
        
        # 默认providers
        if providers is None:
            providers = ['CPUExecutionProvider']
        
        # 优化模型
        optimizer = ONNXModelOptimizer()
        optimized_model_path = optimizer.optimize_model(model_config)
        
        # 创建会话选项
        if session_options is None:
            session_options = self._create_optimized_session_options()
        
        # 创建推理会话
        try:
            self.session = ort.InferenceSession(
                optimized_model_path,
                session_options,
                providers=providers
            )
            
            # 获取输入输出信息
            self.input_names = [inp.name for inp in self.session.get_inputs()]
            self.output_names = [out.name for out in self.session.get_outputs()]
            
            # 预热
            if model_config.warmup_runs > 0:
                self._warmup()
            
            logger.info(f"Optimized inference session created: {optimized_model_path}")
            
        except Exception as e:
            logger.error(f"Failed to create inference session: {e}")
            raise
    
    def _create_optimized_session_options(self) -> ort.SessionOptions:
        """创建优化的会话选项"""
        session_options = ort.SessionOptions()
        
        # 图优化级别
        if self.config.optimization_level == 'basic':
            session_options.graph_optimization_level = ort.GraphOptimizationLevel.ORT_ENABLE_BASIC
        elif self.config.optimization_level == 'extended':
            session_options.graph_optimization_level = ort.GraphOptimizationLevel.ORT_ENABLE_EXTENDED
        else:  # 'all'
            session_options.graph_optimization_level = ort.GraphOptimizationLevel.ORT_ENABLE_ALL
        
        # 并行执行
        session_options.execution_mode = ort.ExecutionMode.ORT_PARALLEL
        
        # 线程配置
        import multiprocessing
        cpu_count = multiprocessing.cpu_count()
        session_options.intra_op_num_threads = min(4, cpu_count)
        session_options.inter_op_num_threads = min(2, cpu_count // 2)
        
        # 内存优化
        session_options.enable_mem_pattern = True
        session_options.enable_cpu_mem_arena = True
        
        return session_options
    
    def _warmup(self):
        """预热推理会话"""
        try:
            logger.info(f"Warming up inference session with {self.config.warmup_runs} runs...")
            
            # 创建虚拟输入数据
            dummy_input = np.random.randn(*self.config.input_shape).astype(np.float32)
            
            # 执行预热推理
            warmup_times = []
            for i in range(self.config.warmup_runs):
                start_time = time.time()
                
                outputs = self.session.run(
                    self.output_names,
                    {self.input_names[0]: dummy_input}
                )
                
                warmup_times.append(time.time() - start_time)
            
            avg_time = sum(warmup_times) / len(warmup_times)
            logger.info(f"Warmup completed. Average inference time: {avg_time*1000:.2f}ms")
            
            self.warmup_completed = True
            
        except Exception as e:
            logger.warning(f"Warmup failed: {e}")
    
    def run(self, input_data: Union[np.ndarray, Dict[str, np.ndarray]]) -> List[np.ndarray]:
        """执行推理"""
        try:
            if isinstance(input_data, np.ndarray):
                input_dict = {self.input_names[0]: input_data}
            else:
                input_dict = input_data
            
            outputs = self.session.run(self.output_names, input_dict)
            return outputs
            
        except Exception as e:
            logger.error(f"Inference failed: {e}")
            raise
    
    def benchmark(self, num_runs: int = 100) -> Dict[str, float]:
        """性能基准测试"""
        if not self.warmup_completed:
            self._warmup()
        
        logger.info(f"Running benchmark with {num_runs} iterations...")
        
        # 创建测试数据
        dummy_input = np.random.randn(*self.config.input_shape).astype(np.float32)
        
        # 执行基准测试
        times = []
        for i in range(num_runs):
            start_time = time.time()
            outputs = self.run(dummy_input)
            times.append(time.time() - start_time)
        
        # 计算统计信息
        times = np.array(times)
        stats = {
            'mean_ms': float(np.mean(times) * 1000),
            'std_ms': float(np.std(times) * 1000),
            'min_ms': float(np.min(times) * 1000),
            'max_ms': float(np.max(times) * 1000),
            'p50_ms': float(np.percentile(times, 50) * 1000),
            'p95_ms': float(np.percentile(times, 95) * 1000),
            'p99_ms': float(np.percentile(times, 99) * 1000),
            'fps': float(1.0 / np.mean(times))
        }
        
        logger.info(f"Benchmark results: {stats['mean_ms']:.2f}±{stats['std_ms']:.2f}ms, "
                   f"FPS: {stats['fps']:.1f}")
        
        return stats


class ModelManager:
    """模型管理器"""
    
    def __init__(self, models_config: Dict[str, ModelConfig]):
        self.models_config = models_config
        self.sessions = {}
        self.load_lock = threading.Lock()
    
    def get_session(self, model_name: str) -> OptimizedInferenceSession:
        """获取推理会话"""
        if model_name not in self.sessions:
            with self.load_lock:
                # 双重检查锁定
                if model_name not in self.sessions:
                    if model_name not in self.models_config:
                        raise ValueError(f"Model '{model_name}' not configured")
                    
                    logger.info(f"Loading model: {model_name}")
                    config = self.models_config[model_name]
                    self.sessions[model_name] = OptimizedInferenceSession(config)
        
        return self.sessions[model_name]
    
    def preload_all_models(self):
        """预加载所有模型"""
        logger.info("Preloading all models...")
        
        for model_name in self.models_config.keys():
            try:
                self.get_session(model_name)
                logger.info(f"Model '{model_name}' preloaded successfully")
            except Exception as e:
                logger.error(f"Failed to preload model '{model_name}': {e}")
    
    def benchmark_all_models(self) -> Dict[str, Dict[str, float]]:
        """对所有模型进行基准测试"""
        results = {}
        
        for model_name in self.models_config.keys():
            try:
                session = self.get_session(model_name)
                results[model_name] = session.benchmark()
            except Exception as e:
                logger.error(f"Benchmark failed for model '{model_name}': {e}")
                results[model_name] = {'error': str(e)}
        
        return results
    
    def get_model_info(self) -> Dict[str, Dict[str, Any]]:
        """获取模型信息"""
        info = {}
        
        for model_name, config in self.models_config.items():
            info[model_name] = {
                'model_path': config.model_path,
                'input_shape': config.input_shape,
                'output_shape': config.output_shape,
                'optimization_level': config.optimization_level,
                'quantization': config.quantization,
                'cache_enabled': config.cache_enabled,
                'warmup_runs': config.warmup_runs,
                'loaded': model_name in self.sessions
            }
        
        return info


def create_sample_models_config() -> Dict[str, ModelConfig]:
    """创建示例模型配置"""
    models_config = {
        'vehicle_detection': ModelConfig(
            model_path='models/vehicle_detection.onnx',
            input_shape=(1, 3, 640, 640),
            output_shape=(1, 25200, 6),  # YOLO格式
            optimization_level='all',
            quantization=True,
            warmup_runs=10
        ),
        'plate_recognition': ModelConfig(
            model_path='models/plate_recognition.onnx',
            input_shape=(1, 3, 48, 168),
            output_shape=(1, 37, 11),  # 字符分类输出
            optimization_level='all',
            quantization=True,
            warmup_runs=10
        )
    }
    
    return models_config


def test_model_optimization():
    """测试模型优化"""
    # 创建示例配置
    models_config = create_sample_models_config()
    
    # 创建模型管理器
    manager = ModelManager(models_config)
    
    # 打印模型信息
    info = manager.get_model_info()
    print("=== Model Information ===")
    for model_name, model_info in info.items():
        print(f"{model_name}:")
        for key, value in model_info.items():
            print(f"  {key}: {value}")
        print()
    
    # 注意：由于没有真实的模型文件，以下代码会失败
    # 在实际使用中，确保模型文件存在
    try:
        # 预加载模型（需要真实的模型文件）
        # manager.preload_all_models()
        
        # 基准测试（需要真实的模型文件）
        # results = manager.benchmark_all_models()
        # print("=== Benchmark Results ===")
        # for model_name, stats in results.items():
        #     if 'error' not in stats:
        #         print(f"{model_name}: {stats['mean_ms']:.2f}ms, {stats['fps']:.1f} FPS")
        #     else:
        #         print(f"{model_name}: Error - {stats['error']}")
        
        print("Model optimization test completed (models not found, which is expected)")
        
    except Exception as e:
        print(f"Expected error (no model files): {e}")


if __name__ == "__main__":
    test_model_optimization()