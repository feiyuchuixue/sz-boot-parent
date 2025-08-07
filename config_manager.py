"""
配置管理系统
Configuration Management System

提供动态配置加载、验证、热更新等功能
"""

import os
import json
import yaml
import logging
import threading
import time
from pathlib import Path
from typing import Dict, Any, Optional, List, Union, Callable
from dataclasses import dataclass, asdict, field
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler

logger = logging.getLogger(__name__)


@dataclass
class CameraConfig:
    """摄像头配置"""
    id: int
    name: str
    url: str
    regions: List[Dict[str, Any]] = field(default_factory=list)
    enabled: bool = True
    fps: int = 15
    resolution: Dict[str, int] = field(default_factory=lambda: {"width": 640, "height": 480})
    detection_areas: List[Dict[str, Any]] = field(default_factory=list)


@dataclass
class ModelConfig:
    """模型配置"""
    name: str
    path: str
    type: str  # 'vehicle_detection' 或 'plate_recognition'
    input_shape: List[int] = field(default_factory=list)
    confidence_threshold: float = 0.5
    optimization_level: str = "all"
    quantization: bool = True
    warmup_runs: int = 5


@dataclass
class SystemConfig:
    """系统配置"""
    # 基本配置
    num_cameras: int = 4
    target_fps: int = 15
    max_fps: int = 25
    min_fps: int = 8
    
    # CPU优化配置
    cpu_optimization: bool = True
    cpu_affinity_enabled: bool = True
    numa_aware: bool = True
    
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
    enable_zero_copy: bool = True
    
    # 算法配置
    adaptive_resolution: bool = True
    smart_frame_skip: bool = True
    multi_scale_detection: bool = False
    
    # 性能监控配置
    performance_monitoring: bool = True
    metrics_update_interval: int = 5
    max_cpu_usage: float = 85.0
    max_memory_usage: float = 60.0
    
    # API配置
    api_endpoint: str = "http://localhost:8080/api/plate-recognition"
    api_timeout: int = 10
    api_retry_times: int = 3
    api_batch_size: int = 1
    
    # 日志配置
    log_level: str = "INFO"
    log_file: str = "license_plate_recognition.log"
    log_max_size: int = 100  # MB
    log_backup_count: int = 5


@dataclass
class CompleteConfig:
    """完整配置"""
    system: SystemConfig = field(default_factory=SystemConfig)
    cameras: List[CameraConfig] = field(default_factory=list)
    models: List[ModelConfig] = field(default_factory=list)
    version: str = "1.0.0"
    last_updated: Optional[float] = None


class ConfigValidator:
    """配置验证器"""
    
    @staticmethod
    def validate_system_config(config: SystemConfig) -> List[str]:
        """验证系统配置"""
        errors = []
        
        # 线程数验证
        if config.frame_reader_threads < 1:
            errors.append("frame_reader_threads must be at least 1")
        if config.vehicle_detection_threads < 1:
            errors.append("vehicle_detection_threads must be at least 1")
        if config.plate_recognition_threads < 1:
            errors.append("plate_recognition_threads must be at least 1")
        if config.api_push_threads < 1:
            errors.append("api_push_threads must be at least 1")
        
        # FPS验证
        if config.min_fps >= config.max_fps:
            errors.append("min_fps must be less than max_fps")
        if config.target_fps < config.min_fps or config.target_fps > config.max_fps:
            errors.append("target_fps must be between min_fps and max_fps")
        
        # 队列大小验证
        if config.max_frame_queue_size < 10:
            errors.append("max_frame_queue_size must be at least 10")
        if config.max_result_queue_size < 10:
            errors.append("max_result_queue_size must be at least 10")
        
        # 阈值验证
        if not 0 <= config.max_cpu_usage <= 100:
            errors.append("max_cpu_usage must be between 0 and 100")
        if not 0 <= config.max_memory_usage <= 100:
            errors.append("max_memory_usage must be between 0 and 100")
        
        return errors
    
    @staticmethod
    def validate_camera_config(config: CameraConfig) -> List[str]:
        """验证摄像头配置"""
        errors = []
        
        if config.id < 0:
            errors.append(f"Camera {config.id}: id must be non-negative")
        
        if not config.name or not config.name.strip():
            errors.append(f"Camera {config.id}: name cannot be empty")
        
        if not config.url or not config.url.strip():
            errors.append(f"Camera {config.id}: url cannot be empty")
        
        if config.fps <= 0:
            errors.append(f"Camera {config.id}: fps must be positive")
        
        if config.resolution["width"] <= 0 or config.resolution["height"] <= 0:
            errors.append(f"Camera {config.id}: resolution must be positive")
        
        return errors
    
    @staticmethod
    def validate_model_config(config: ModelConfig) -> List[str]:
        """验证模型配置"""
        errors = []
        
        if not config.name or not config.name.strip():
            errors.append(f"Model config: name cannot be empty")
        
        if not config.path or not config.path.strip():
            errors.append(f"Model {config.name}: path cannot be empty")
        
        if config.path and not Path(config.path).exists():
            errors.append(f"Model {config.name}: file does not exist: {config.path}")
        
        if config.type not in ["vehicle_detection", "plate_recognition"]:
            errors.append(f"Model {config.name}: type must be 'vehicle_detection' or 'plate_recognition'")
        
        if not 0 <= config.confidence_threshold <= 1:
            errors.append(f"Model {config.name}: confidence_threshold must be between 0 and 1")
        
        if config.optimization_level not in ["basic", "extended", "all"]:
            errors.append(f"Model {config.name}: optimization_level must be 'basic', 'extended', or 'all'")
        
        return errors
    
    @staticmethod
    def validate_complete_config(config: CompleteConfig) -> List[str]:
        """验证完整配置"""
        errors = []
        
        # 验证系统配置
        errors.extend(ConfigValidator.validate_system_config(config.system))
        
        # 验证摄像头配置
        camera_ids = set()
        for camera_config in config.cameras:
            errors.extend(ConfigValidator.validate_camera_config(camera_config))
            
            if camera_config.id in camera_ids:
                errors.append(f"Duplicate camera ID: {camera_config.id}")
            camera_ids.add(camera_config.id)
        
        # 验证模型配置
        model_names = set()
        model_types = set()
        for model_config in config.models:
            errors.extend(ConfigValidator.validate_model_config(model_config))
            
            if model_config.name in model_names:
                errors.append(f"Duplicate model name: {model_config.name}")
            model_names.add(model_config.name)
            model_types.add(model_config.type)
        
        # 检查必需的模型类型
        required_types = {"vehicle_detection", "plate_recognition"}
        missing_types = required_types - model_types
        if missing_types:
            errors.append(f"Missing required model types: {missing_types}")
        
        return errors


class ConfigFileHandler(FileSystemEventHandler):
    """配置文件变更处理器"""
    
    def __init__(self, config_manager: 'ConfigManager'):
        self.config_manager = config_manager
        self.last_modified = {}
    
    def on_modified(self, event):
        """文件修改事件"""
        if not event.is_directory and event.src_path == str(self.config_manager.config_path):
            # 防止重复触发
            current_time = time.time()
            last_time = self.last_modified.get(event.src_path, 0)
            
            if current_time - last_time > 1.0:  # 1秒内的重复事件忽略
                self.last_modified[event.src_path] = current_time
                logger.info(f"Configuration file changed: {event.src_path}")
                self.config_manager._reload_config()


class ConfigManager:
    """配置管理器"""
    
    def __init__(self, config_path: Union[str, Path] = "config.yaml"):
        self.config_path = Path(config_path)
        self.config: CompleteConfig = CompleteConfig()
        self.config_lock = threading.RLock()
        self.change_callbacks: List[Callable[[CompleteConfig], None]] = []
        
        # 文件监控
        self.observer = None
        self.file_handler = None
        self.auto_reload = False
        
        # 加载配置
        self.load_config()
    
    def enable_auto_reload(self):
        """启用自动重新加载"""
        if self.auto_reload:
            return
        
        try:
            self.auto_reload = True
            self.file_handler = ConfigFileHandler(self)
            self.observer = Observer()
            
            # 监控配置文件所在目录
            watch_dir = self.config_path.parent
            self.observer.schedule(self.file_handler, str(watch_dir), recursive=False)
            self.observer.start()
            
            logger.info(f"Auto-reload enabled for config file: {self.config_path}")
            
        except Exception as e:
            logger.error(f"Failed to enable auto-reload: {e}")
            self.auto_reload = False
    
    def disable_auto_reload(self):
        """禁用自动重新加载"""
        if not self.auto_reload:
            return
        
        try:
            if self.observer:
                self.observer.stop()
                self.observer.join(timeout=5)
            
            self.auto_reload = False
            self.observer = None
            self.file_handler = None
            
            logger.info("Auto-reload disabled")
            
        except Exception as e:
            logger.error(f"Failed to disable auto-reload: {e}")
    
    def add_change_callback(self, callback: Callable[[CompleteConfig], None]):
        """添加配置变更回调"""
        self.change_callbacks.append(callback)
    
    def remove_change_callback(self, callback: Callable[[CompleteConfig], None]):
        """移除配置变更回调"""
        if callback in self.change_callbacks:
            self.change_callbacks.remove(callback)
    
    def load_config(self) -> bool:
        """加载配置"""
        try:
            with self.config_lock:
                if not self.config_path.exists():
                    # 创建默认配置
                    self._create_default_config()
                    return True
                
                # 根据文件扩展名选择解析器
                if self.config_path.suffix.lower() in ['.yaml', '.yml']:
                    config_data = self._load_yaml()
                elif self.config_path.suffix.lower() == '.json':
                    config_data = self._load_json()
                else:
                    raise ValueError(f"Unsupported config file format: {self.config_path.suffix}")
                
                # 解析配置数据
                new_config = self._parse_config_data(config_data)
                
                # 验证配置
                errors = ConfigValidator.validate_complete_config(new_config)
                if errors:
                    logger.error("Configuration validation errors:")
                    for error in errors:
                        logger.error(f"  - {error}")
                    return False
                
                # 更新配置
                old_config = self.config
                self.config = new_config
                self.config.last_updated = time.time()
                
                # 触发变更回调
                self._notify_config_changed(old_config, new_config)
                
                logger.info(f"Configuration loaded successfully: {self.config_path}")
                return True
                
        except Exception as e:
            logger.error(f"Failed to load configuration: {e}")
            return False
    
    def save_config(self) -> bool:
        """保存配置"""
        try:
            with self.config_lock:
                self.config.last_updated = time.time()
                
                config_data = self._config_to_dict(self.config)
                
                # 根据文件扩展名选择格式
                if self.config_path.suffix.lower() in ['.yaml', '.yml']:
                    self._save_yaml(config_data)
                elif self.config_path.suffix.lower() == '.json':
                    self._save_json(config_data)
                else:
                    raise ValueError(f"Unsupported config file format: {self.config_path.suffix}")
                
                logger.info(f"Configuration saved successfully: {self.config_path}")
                return True
                
        except Exception as e:
            logger.error(f"Failed to save configuration: {e}")
            return False
    
    def _load_yaml(self) -> Dict[str, Any]:
        """加载YAML配置"""
        with open(self.config_path, 'r', encoding='utf-8') as f:
            return yaml.safe_load(f) or {}
    
    def _load_json(self) -> Dict[str, Any]:
        """加载JSON配置"""
        with open(self.config_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    
    def _save_yaml(self, config_data: Dict[str, Any]):
        """保存YAML配置"""
        with open(self.config_path, 'w', encoding='utf-8') as f:
            yaml.dump(config_data, f, default_flow_style=False, 
                     allow_unicode=True, indent=2)
    
    def _save_json(self, config_data: Dict[str, Any]):
        """保存JSON配置"""
        with open(self.config_path, 'w', encoding='utf-8') as f:
            json.dump(config_data, f, indent=2, ensure_ascii=False)
    
    def _parse_config_data(self, config_data: Dict[str, Any]) -> CompleteConfig:
        """解析配置数据"""
        config = CompleteConfig()
        
        # 解析系统配置
        if 'system' in config_data:
            system_data = config_data['system']
            config.system = SystemConfig(**system_data)
        
        # 解析摄像头配置
        if 'cameras' in config_data:
            config.cameras = []
            for camera_data in config_data['cameras']:
                camera_config = CameraConfig(**camera_data)
                config.cameras.append(camera_config)
        
        # 解析模型配置
        if 'models' in config_data:
            config.models = []
            for model_data in config_data['models']:
                model_config = ModelConfig(**model_data)
                config.models.append(model_config)
        
        # 其他属性
        config.version = config_data.get('version', '1.0.0')
        config.last_updated = config_data.get('last_updated')
        
        return config
    
    def _config_to_dict(self, config: CompleteConfig) -> Dict[str, Any]:
        """配置转换为字典"""
        return {
            'version': config.version,
            'last_updated': config.last_updated,
            'system': asdict(config.system),
            'cameras': [asdict(camera) for camera in config.cameras],
            'models': [asdict(model) for model in config.models]
        }
    
    def _create_default_config(self):
        """创建默认配置"""
        logger.info(f"Creating default configuration: {self.config_path}")
        
        # 默认摄像头配置
        default_cameras = [
            CameraConfig(
                id=0,
                name="Camera 1",
                url="0",  # 本地摄像头
                fps=15,
                regions=[{"id": 0, "name": "Region 1", "area": [0, 0, 640, 480]}]
            ),
            CameraConfig(
                id=1,
                name="Camera 2", 
                url="rtsp://admin:admin@192.168.1.100/stream1",
                fps=15,
                regions=[{"id": 0, "name": "Region 1", "area": [0, 0, 640, 480]}]
            )
        ]
        
        # 默认模型配置
        default_models = [
            ModelConfig(
                name="vehicle_detector",
                path="models/vehicle_detection.onnx",
                type="vehicle_detection",
                input_shape=[1, 3, 640, 640],
                confidence_threshold=0.5
            ),
            ModelConfig(
                name="plate_recognizer",
                path="models/plate_recognition.onnx", 
                type="plate_recognition",
                input_shape=[1, 3, 48, 168],
                confidence_threshold=0.7
            )
        ]
        
        # 创建默认配置
        self.config = CompleteConfig(
            system=SystemConfig(),
            cameras=default_cameras,
            models=default_models,
            version="1.0.0"
        )
        
        # 保存到文件
        self.save_config()
    
    def _reload_config(self):
        """重新加载配置"""
        logger.info("Reloading configuration...")
        
        success = self.load_config()
        if success:
            logger.info("Configuration reloaded successfully")
        else:
            logger.error("Configuration reload failed")
    
    def _notify_config_changed(self, old_config: CompleteConfig, new_config: CompleteConfig):
        """通知配置变更"""
        for callback in self.change_callbacks:
            try:
                callback(new_config)
            except Exception as e:
                logger.error(f"Config change callback error: {e}")
    
    def get_config(self) -> CompleteConfig:
        """获取当前配置"""
        with self.config_lock:
            return self.config
    
    def update_system_config(self, updates: Dict[str, Any]) -> bool:
        """更新系统配置"""
        try:
            with self.config_lock:
                # 创建新的系统配置
                system_dict = asdict(self.config.system)
                system_dict.update(updates)
                new_system_config = SystemConfig(**system_dict)
                
                # 验证新配置
                errors = ConfigValidator.validate_system_config(new_system_config)
                if errors:
                    logger.error("System config update validation errors:")
                    for error in errors:
                        logger.error(f"  - {error}")
                    return False
                
                # 应用更新
                old_config = self.config
                self.config.system = new_system_config
                self.config.last_updated = time.time()
                
                # 保存配置
                self.save_config()
                
                # 通知变更
                self._notify_config_changed(old_config, self.config)
                
                logger.info("System configuration updated successfully")
                return True
                
        except Exception as e:
            logger.error(f"Failed to update system configuration: {e}")
            return False
    
    def add_camera(self, camera_config: CameraConfig) -> bool:
        """添加摄像头配置"""
        try:
            with self.config_lock:
                # 验证配置
                errors = ConfigValidator.validate_camera_config(camera_config)
                if errors:
                    logger.error("Camera config validation errors:")
                    for error in errors:
                        logger.error(f"  - {error}")
                    return False
                
                # 检查ID冲突
                existing_ids = {cam.id for cam in self.config.cameras}
                if camera_config.id in existing_ids:
                    logger.error(f"Camera ID {camera_config.id} already exists")
                    return False
                
                # 添加摄像头
                old_config = self.config
                self.config.cameras.append(camera_config)
                self.config.last_updated = time.time()
                
                # 保存配置
                self.save_config()
                
                # 通知变更
                self._notify_config_changed(old_config, self.config)
                
                logger.info(f"Camera {camera_config.id} added successfully")
                return True
                
        except Exception as e:
            logger.error(f"Failed to add camera: {e}")
            return False
    
    def remove_camera(self, camera_id: int) -> bool:
        """移除摄像头配置"""
        try:
            with self.config_lock:
                # 查找摄像头
                camera_index = None
                for i, camera in enumerate(self.config.cameras):
                    if camera.id == camera_id:
                        camera_index = i
                        break
                
                if camera_index is None:
                    logger.error(f"Camera {camera_id} not found")
                    return False
                
                # 移除摄像头
                old_config = self.config
                removed_camera = self.config.cameras.pop(camera_index)
                self.config.last_updated = time.time()
                
                # 保存配置
                self.save_config()
                
                # 通知变更
                self._notify_config_changed(old_config, self.config)
                
                logger.info(f"Camera {camera_id} removed successfully")
                return True
                
        except Exception as e:
            logger.error(f"Failed to remove camera: {e}")
            return False
    
    def get_camera_config(self, camera_id: int) -> Optional[CameraConfig]:
        """获取摄像头配置"""
        with self.config_lock:
            for camera in self.config.cameras:
                if camera.id == camera_id:
                    return camera
            return None
    
    def get_model_config(self, model_name: str) -> Optional[ModelConfig]:
        """获取模型配置"""
        with self.config_lock:
            for model in self.config.models:
                if model.name == model_name:
                    return model
            return None
    
    def export_config(self, export_path: Union[str, Path]) -> bool:
        """导出配置"""
        try:
            export_path = Path(export_path)
            
            with self.config_lock:
                config_data = self._config_to_dict(self.config)
                
                # 根据文件扩展名选择格式
                if export_path.suffix.lower() in ['.yaml', '.yml']:
                    with open(export_path, 'w', encoding='utf-8') as f:
                        yaml.dump(config_data, f, default_flow_style=False,
                                allow_unicode=True, indent=2)
                elif export_path.suffix.lower() == '.json':
                    with open(export_path, 'w', encoding='utf-8') as f:
                        json.dump(config_data, f, indent=2, ensure_ascii=False)
                else:
                    raise ValueError(f"Unsupported export format: {export_path.suffix}")
                
                logger.info(f"Configuration exported to: {export_path}")
                return True
                
        except Exception as e:
            logger.error(f"Failed to export configuration: {e}")
            return False
    
    def __enter__(self):
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        self.disable_auto_reload()


def test_config_manager():
    """测试配置管理器"""
    print("=== Configuration Manager Test ===")
    
    # 创建配置管理器
    config_manager = ConfigManager("test_config.yaml")
    
    try:
        # 启用自动重新加载
        config_manager.enable_auto_reload()
        
        # 获取当前配置
        config = config_manager.get_config()
        print(f"Current config version: {config.version}")
        print(f"Number of cameras: {len(config.cameras)}")
        print(f"Number of models: {len(config.models)}")
        
        # 添加配置变更回调
        def on_config_changed(new_config):
            print(f"Configuration changed! New version: {new_config.version}")
        
        config_manager.add_change_callback(on_config_changed)
        
        # 更新系统配置
        success = config_manager.update_system_config({
            'target_fps': 20,
            'max_cpu_usage': 75.0
        })
        print(f"System config update: {'Success' if success else 'Failed'}")
        
        # 添加新摄像头
        new_camera = CameraConfig(
            id=99,
            name="Test Camera",
            url="rtsp://test.camera/stream",
            fps=15
        )
        success = config_manager.add_camera(new_camera)
        print(f"Add camera: {'Success' if success else 'Failed'}")
        
        # 导出配置
        success = config_manager.export_config("exported_config.json")
        print(f"Export config: {'Success' if success else 'Failed'}")
        
        # 移除测试摄像头
        success = config_manager.remove_camera(99)
        print(f"Remove camera: {'Success' if success else 'Failed'}")
        
    finally:
        config_manager.disable_auto_reload()
        
        # 清理测试文件
        test_files = ["test_config.yaml", "exported_config.json"]
        for file_path in test_files:
            if Path(file_path).exists():
                Path(file_path).unlink()
                print(f"Cleaned up: {file_path}")
    
    print("Configuration manager test completed")


if __name__ == "__main__":
    test_config_manager()