"""
车牌识别系统安装和配置脚本
License Plate Recognition System Setup and Configuration Script
"""

import os
import sys
import subprocess
import shutil
from pathlib import Path
import json
import logging
import argparse

# 设置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)


class SystemSetup:
    """系统安装和配置类"""
    
    def __init__(self):
        self.base_dir = Path(__file__).parent
        self.models_dir = self.base_dir / "models"
        self.temp_dir = self.base_dir / "temp_models"
        self.logs_dir = self.base_dir / "logs"
        self.cache_dir = self.base_dir / "model_cache"
    
    def check_system_requirements(self):
        """检查系统要求"""
        logger.info("Checking system requirements...")
        
        requirements = {
            'python_version': sys.version_info >= (3, 8),
            'cpu_cores': os.cpu_count() >= 4,
            'available_memory': self._get_available_memory() >= 4  # GB
        }
        
        # 检查Python版本
        if not requirements['python_version']:
            logger.error(f"Python 3.8+ required, current: {sys.version}")
            return False
        
        # 检查CPU核心数
        if not requirements['cpu_cores']:
            logger.warning(f"Recommended 4+ CPU cores, current: {os.cpu_count()}")
        
        # 检查内存
        if not requirements['available_memory']:
            logger.warning(f"Recommended 4GB+ RAM, current: {self._get_available_memory():.1f}GB")
        
        logger.info("System requirements check completed")
        return True
    
    def _get_available_memory(self):
        """获取可用内存大小（GB）"""
        try:
            import psutil
            memory = psutil.virtual_memory()
            return memory.total / (1024 ** 3)
        except ImportError:
            return 0
    
    def install_dependencies(self):
        """安装依赖包"""
        logger.info("Installing Python dependencies...")
        
        requirements_file = self.base_dir / "requirements.txt"
        if not requirements_file.exists():
            logger.error("requirements.txt not found")
            return False
        
        try:
            subprocess.check_call([
                sys.executable, "-m", "pip", "install", "-r", str(requirements_file)
            ])
            logger.info("Dependencies installed successfully")
            return True
        except subprocess.CalledProcessError as e:
            logger.error(f"Failed to install dependencies: {e}")
            return False
    
    def create_directories(self):
        """创建必要的目录"""
        logger.info("Creating directories...")
        
        directories = [
            self.models_dir,
            self.temp_dir,
            self.logs_dir,
            self.cache_dir
        ]
        
        for directory in directories:
            directory.mkdir(exist_ok=True, parents=True)
            logger.info(f"Created directory: {directory}")
        
        return True
    
    def download_sample_models(self):
        """下载示例模型（如果可用）"""
        logger.info("Setting up model files...")
        
        # 创建示例模型占位符
        model_configs = [
            {
                'name': 'vehicle_detection.onnx',
                'description': 'Vehicle detection model (YOLO)',
                'input_shape': [1, 3, 640, 640],
                'output_shape': [1, 25200, 6]
            },
            {
                'name': 'plate_recognition.onnx',
                'description': 'License plate recognition model',
                'input_shape': [1, 3, 48, 168],
                'output_shape': [1, 37, 11]
            }
        ]
        
        for config in model_configs:
            model_path = self.models_dir / config['name']
            if not model_path.exists():
                # 创建模型信息文件
                info_path = self.models_dir / f"{config['name']}.info"
                with open(info_path, 'w') as f:
                    json.dump(config, f, indent=2)
                
                logger.info(f"Created model info: {info_path}")
                logger.warning(f"Please place the actual model file at: {model_path}")
        
        return True
    
    def create_config_files(self):
        """创建配置文件"""
        logger.info("Creating configuration files...")
        
        # 导入配置管理器
        try:
            from config_manager import ConfigManager
            config_manager = ConfigManager("config.yaml")
            # 配置管理器会自动创建默认配置
            logger.info("Configuration file created: config.yaml")
            return True
        except ImportError as e:
            logger.error(f"Failed to import config_manager: {e}")
            return False
    
    def setup_logging(self):
        """设置日志配置"""
        logger.info("Setting up logging configuration...")
        
        log_config = {
            "version": 1,
            "disable_existing_loggers": False,
            "formatters": {
                "standard": {
                    "format": "%(asctime)s - %(name)s - %(levelname)s - [%(threadName)s] - %(message)s"
                },
                "detailed": {
                    "format": "%(asctime)s - %(name)s - %(levelname)s - [%(threadName)s] - %(filename)s:%(lineno)d - %(message)s"
                }
            },
            "handlers": {
                "console": {
                    "class": "logging.StreamHandler",
                    "level": "INFO",
                    "formatter": "standard",
                    "stream": "ext://sys.stdout"
                },
                "file": {
                    "class": "logging.handlers.RotatingFileHandler",
                    "level": "DEBUG",
                    "formatter": "detailed",
                    "filename": str(self.logs_dir / "license_plate_recognition.log"),
                    "maxBytes": 104857600,  # 100MB
                    "backupCount": 5
                },
                "error_file": {
                    "class": "logging.handlers.RotatingFileHandler",
                    "level": "ERROR",
                    "formatter": "detailed",
                    "filename": str(self.logs_dir / "errors.log"),
                    "maxBytes": 10485760,  # 10MB
                    "backupCount": 3
                }
            },
            "loggers": {
                "": {
                    "level": "INFO",
                    "handlers": ["console", "file", "error_file"],
                    "propagate": False
                }
            }
        }
        
        log_config_path = self.base_dir / "logging_config.json"
        with open(log_config_path, 'w') as f:
            json.dump(log_config, f, indent=2)
        
        logger.info(f"Logging configuration saved: {log_config_path}")
        return True
    
    def create_service_scripts(self):
        """创建服务脚本"""
        logger.info("Creating service scripts...")
        
        # 启动脚本
        start_script = self.base_dir / "start.py"
        start_content = '''#!/usr/bin/env python3
"""
启动脚本
Startup script for License Plate Recognition System
"""

import sys
import logging
from pathlib import Path

# 添加项目目录到路径
sys.path.insert(0, str(Path(__file__).parent))

from main import main

if __name__ == "__main__":
    try:
        exit_code = main()
        sys.exit(exit_code)
    except KeyboardInterrupt:
        print("\\nSystem interrupted by user")
        sys.exit(1)
    except Exception as e:
        print(f"System error: {e}")
        sys.exit(1)
'''
        
        with open(start_script, 'w') as f:
            f.write(start_content)
        
        # 使脚本可执行
        if sys.platform != 'win32':
            os.chmod(start_script, 0o755)
        
        # 停止脚本
        stop_script = self.base_dir / "stop.py"
        stop_content = '''#!/usr/bin/env python3
"""
停止脚本
Stop script for License Plate Recognition System
"""

import os
import signal
import psutil
import sys
from pathlib import Path

def find_and_stop_processes():
    """查找并停止相关进程"""
    stopped = []
    
    for proc in psutil.process_iter(['pid', 'name', 'cmdline']):
        try:
            cmdline = ' '.join(proc.info['cmdline'] or [])
            if 'main.py' in cmdline or 'start.py' in cmdline:
                if 'license' in cmdline.lower() or 'plate' in cmdline.lower():
                    print(f"Stopping process {proc.info['pid']}: {proc.info['name']}")
                    proc.terminate()
                    stopped.append(proc.info['pid'])
        except (psutil.NoSuchProcess, psutil.AccessDenied):
            continue
    
    if stopped:
        print(f"Stopped {len(stopped)} processes")
    else:
        print("No running processes found")

if __name__ == "__main__":
    find_and_stop_processes()
'''
        
        with open(stop_script, 'w') as f:
            f.write(stop_content)
        
        if sys.platform != 'win32':
            os.chmod(stop_script, 0o755)
        
        logger.info("Service scripts created: start.py, stop.py")
        return True
    
    def create_systemd_service(self):
        """创建systemd服务文件（Linux）"""
        if sys.platform == 'win32':
            logger.info("Skipping systemd service creation on Windows")
            return True
        
        logger.info("Creating systemd service file...")
        
        service_content = f'''[Unit]
Description=License Plate Recognition System
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory={self.base_dir}
ExecStart={sys.executable} start.py
ExecStop={sys.executable} stop.py
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
'''
        
        service_file = self.base_dir / "license-plate-recognition.service"
        with open(service_file, 'w') as f:
            f.write(service_content)
        
        logger.info(f"Systemd service file created: {service_file}")
        logger.info("To install the service:")
        logger.info(f"  sudo cp {service_file} /etc/systemd/system/")
        logger.info("  sudo systemctl daemon-reload")
        logger.info("  sudo systemctl enable license-plate-recognition")
        logger.info("  sudo systemctl start license-plate-recognition")
        
        return True
    
    def run_system_tests(self):
        """运行系统测试"""
        logger.info("Running system tests...")
        
        tests_passed = 0
        tests_total = 0
        
        # 测试导入
        test_modules = [
            'cpu_optimizer',
            'model_optimizer',
            'performance_monitor',
            'config_manager'
        ]
        
        for module_name in test_modules:
            tests_total += 1
            try:
                __import__(module_name)
                logger.info(f"✓ Module {module_name} imported successfully")
                tests_passed += 1
            except ImportError as e:
                logger.error(f"✗ Failed to import {module_name}: {e}")
        
        # 测试配置文件
        tests_total += 1
        config_file = self.base_dir / "config.yaml"
        if config_file.exists():
            logger.info("✓ Configuration file exists")
            tests_passed += 1
        else:
            logger.error("✗ Configuration file not found")
        
        # 测试目录结构
        tests_total += 1
        required_dirs = [self.models_dir, self.logs_dir, self.cache_dir]
        if all(d.exists() for d in required_dirs):
            logger.info("✓ Directory structure is correct")
            tests_passed += 1
        else:
            logger.error("✗ Some required directories are missing")
        
        logger.info(f"System tests completed: {tests_passed}/{tests_total} passed")
        return tests_passed == tests_total
    
    def setup(self, skip_deps=False):
        """执行完整安装"""
        logger.info("Starting License Plate Recognition System setup...")
        
        steps = [
            ("Checking system requirements", self.check_system_requirements),
            ("Creating directories", self.create_directories),
            ("Setting up model files", self.download_sample_models),
            ("Creating configuration files", self.create_config_files),
            ("Setting up logging", self.setup_logging),
            ("Creating service scripts", self.create_service_scripts),
            ("Creating systemd service", self.create_systemd_service),
        ]
        
        if not skip_deps:
            steps.insert(1, ("Installing dependencies", self.install_dependencies))
        
        steps.append(("Running system tests", self.run_system_tests))
        
        for step_name, step_func in steps:
            logger.info(f"=== {step_name} ===")
            try:
                success = step_func()
                if not success:
                    logger.error(f"Step failed: {step_name}")
                    return False
            except Exception as e:
                logger.error(f"Step error ({step_name}): {e}")
                return False
        
        logger.info("=== Setup completed successfully! ===")
        self.print_next_steps()
        return True
    
    def print_next_steps(self):
        """打印后续步骤"""
        print("\n" + "="*60)
        print("NEXT STEPS:")
        print("="*60)
        print("1. Place your ONNX model files in the models/ directory:")
        print("   - models/vehicle_detection.onnx")
        print("   - models/plate_recognition.onnx")
        print()
        print("2. Edit config.yaml to configure your cameras and settings")
        print()
        print("3. Start the system:")
        print("   python start.py")
        print()
        print("4. (Optional) Install as system service:")
        print("   sudo cp license-plate-recognition.service /etc/systemd/system/")
        print("   sudo systemctl daemon-reload")
        print("   sudo systemctl enable license-plate-recognition")
        print("   sudo systemctl start license-plate-recognition")
        print()
        print("5. Monitor logs:")
        print("   tail -f logs/license_plate_recognition.log")
        print("="*60)


def main():
    """主函数"""
    parser = argparse.ArgumentParser(description="License Plate Recognition System Setup")
    parser.add_argument('--skip-deps', action='store_true', 
                       help='Skip dependency installation')
    parser.add_argument('--test-only', action='store_true',
                       help='Only run system tests')
    
    args = parser.parse_args()
    
    setup = SystemSetup()
    
    if args.test_only:
        success = setup.run_system_tests()
        sys.exit(0 if success else 1)
    
    success = setup.setup(skip_deps=args.skip_deps)
    sys.exit(0 if success else 1)


if __name__ == "__main__":
    main()