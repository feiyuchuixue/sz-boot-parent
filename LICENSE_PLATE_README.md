# CPU优化版车牌识别系统

一个专为CPU环境优化的高性能车牌识别系统，支持多摄像头并发处理，针对8核16线程服务器进行了深度优化。

## 系统特性

### 🚀 CPU推理优化
- **ONNX Runtime加速**: 集成ONNX Runtime进行CPU推理优化
- **Intel OpenVINO支持**: 可选的Intel CPU专用优化
- **模型量化优化**: 自动模型量化和缓存管理
- **轻量级YOLO**: 支持专为CPU优化的轻量级YOLO模型

### 🔄 多线程架构
- **生产者-消费者模式**: 分离帧读取、车辆检测、车牌识别、API推送
- **CPU亲和性绑定**: 智能线程绑定和NUMA感知调度
- **负载均衡**: 动态负载均衡和线程池管理
- **无锁队列**: 高效的无锁队列实现

### 💾 内存和性能优化
- **对象池化**: 内存对象复用，减少GC压力
- **零拷贝传递**: 最小化内存拷贝开销
- **智能垃圾回收**: 自适应垃圾回收策略
- **缓存预测**: 智能缓存和预测机制

### 🎯 算法优化
- **快速车辆预检测**: 多级检测策略，提升效率
- **自适应分辨率**: 根据CPU负载动态调整分辨率
- **多尺度识别**: 多尺度车牌识别提高准确率
- **智能跳帧**: 基于性能的智能跳帧机制

### 📊 动态调优
- **实时性能监控**: CPU、内存、队列状态监控
- **自适应FPS调整**: 根据系统负载动态调整FPS
- **参数动态优化**: 运行时参数调优
- **详细指标统计**: 全方位性能指标收集

## 性能目标

| 场景 | 目标FPS | CPU使用率 | 内存使用率 | 识别准确率 |
|------|---------|-----------|------------|------------|
| 4摄像头 | 15-20 FPS/摄像头 | 70-85% | <60% | >90% |
| 8摄像头 | 8-12 FPS/摄像头 | 70-85% | <60% | >90% |

## 系统要求

### 硬件要求
- **CPU**: 8核16线程（推荐Intel Xeon或Core i7/i9）
- **内存**: 16GB RAM（最低8GB）
- **存储**: 100GB可用空间
- **网络**: 千兆以太网（多摄像头场景）

### 软件要求
- **操作系统**: Ubuntu 20.04+ / CentOS 8+ / Windows 10+
- **Python**: 3.8+
- **CUDA**: 不需要（CPU专用优化）

## 快速开始

### 1. 安装系统

```bash
# 克隆项目
git clone <repository-url>
cd sz-boot-parent

# 运行安装脚本
python setup.py

# 或者手动安装依赖
pip install -r requirements.txt
```

### 2. 准备模型文件

将ONNX模型文件放置到`models/`目录：
- `models/vehicle_detection.onnx` - 车辆检测模型
- `models/plate_recognition.onnx` - 车牌识别模型

### 3. 配置系统

编辑`config.yaml`配置文件：

```yaml
system:
  num_cameras: 4
  target_fps: 15
  cpu_optimization: true
  performance_monitoring: true

cameras:
  - id: 0
    name: "Camera 1"
    url: "rtsp://admin:admin@192.168.1.100/stream1"
    fps: 15
    enabled: true
  - id: 1
    name: "Camera 2"
    url: "rtsp://admin:admin@192.168.1.101/stream1"
    fps: 15
    enabled: true

models:
  - name: "vehicle_detector"
    path: "models/vehicle_detection.onnx"
    type: "vehicle_detection"
    confidence_threshold: 0.5
  - name: "plate_recognizer"
    path: "models/plate_recognition.onnx"
    type: "plate_recognition"
    confidence_threshold: 0.7
```

### 4. 启动系统

```bash
# 直接启动
python main.py

# 或使用启动脚本
python start.py

# 后台运行
nohup python start.py > output.log 2>&1 &
```

### 5. 监控系统

```bash
# 查看日志
tail -f logs/license_plate_recognition.log

# 查看性能指标
curl http://localhost:8080/api/status

# 停止系统
python stop.py
```

## 详细配置

### 系统配置项

```yaml
system:
  # 基本配置
  num_cameras: 4                    # 摄像头数量
  target_fps: 15                    # 目标FPS
  max_fps: 25                       # 最大FPS
  min_fps: 8                        # 最小FPS
  
  # CPU优化
  cpu_optimization: true            # 启用CPU优化
  cpu_affinity_enabled: true        # 启用CPU亲和性
  numa_aware: true                  # NUMA感知调度
  
  # 线程池配置
  frame_reader_threads: 2           # 帧读取线程数
  vehicle_detection_threads: 4      # 车辆检测线程数
  plate_recognition_threads: 6      # 车牌识别线程数
  api_push_threads: 2               # API推送线程数
  
  # 内存优化
  max_frame_queue_size: 100         # 最大帧队列大小
  max_result_queue_size: 200        # 最大结果队列大小
  object_pool_size: 50              # 对象池大小
  enable_zero_copy: true            # 启用零拷贝
  
  # 算法配置
  adaptive_resolution: true         # 自适应分辨率
  smart_frame_skip: true            # 智能跳帧
  multi_scale_detection: false      # 多尺度检测
  
  # 性能监控
  performance_monitoring: true      # 启用性能监控
  metrics_update_interval: 5        # 指标更新间隔(秒)
  max_cpu_usage: 85.0              # 最大CPU使用率(%)
  max_memory_usage: 60.0           # 最大内存使用率(%)
```

### 摄像头配置

```yaml
cameras:
  - id: 0
    name: "主入口摄像头"
    url: "rtsp://admin:admin@192.168.1.100:554/stream1"
    fps: 15
    enabled: true
    resolution:
      width: 1920
      height: 1080
    regions:
      - id: 0
        name: "车道1"
        area: [0, 400, 960, 1080]    # [x, y, width, height]
      - id: 1
        name: "车道2"
        area: [960, 400, 960, 1080]
    detection_areas:
      - name: "触发区域"
        points: [[100, 500], [860, 500], [860, 800], [100, 800]]
```

### 模型配置

```yaml
models:
  - name: "vehicle_detector"
    path: "models/yolov8n_vehicle.onnx"
    type: "vehicle_detection"
    input_shape: [1, 3, 640, 640]
    confidence_threshold: 0.5
    optimization_level: "all"        # basic/extended/all
    quantization: true
    warmup_runs: 10
    
  - name: "plate_recognizer"
    path: "models/crnn_plate.onnx"
    type: "plate_recognition"
    input_shape: [1, 3, 48, 168]
    confidence_threshold: 0.7
    optimization_level: "all"
    quantization: true
    warmup_runs: 10
```

## API接口

系统提供RESTful API接口用于监控和控制：

### 获取系统状态
```http
GET /api/status
```

### 获取性能指标
```http
GET /api/metrics
```

### 获取摄像头状态
```http
GET /api/cameras
GET /api/cameras/{camera_id}
```

### 更新配置
```http
POST /api/config/system
Content-Type: application/json

{
  "target_fps": 20,
  "max_cpu_usage": 80.0
}
```

### 控制摄像头
```http
POST /api/cameras/{camera_id}/enable
POST /api/cameras/{camera_id}/disable
```

## 性能调优

### CPU优化建议

1. **CPU调速器设置**
```bash
# 设置为性能模式
sudo cpupower frequency-set -g performance

# 禁用CPU空闲状态
sudo cpupower idle-set -D 0
```

2. **NUMA优化**
```bash
# 查看NUMA拓扑
numactl --hardware

# 绑定到指定NUMA节点运行
numactl --cpunodebind=0 --membind=0 python main.py
```

3. **内存优化**
```bash
# 设置内存交换
echo 'vm.swappiness=10' >> /etc/sysctl.conf

# 设置内存脏页比例
echo 'vm.dirty_ratio=15' >> /etc/sysctl.conf
echo 'vm.dirty_background_ratio=5' >> /etc/sysctl.conf
```

### 模型优化

1. **使用量化模型**
   - INT8量化可减少50%推理时间
   - 系统支持自动量化和缓存

2. **选择合适的模型尺寸**
   - YOLOv8n: 速度优先（推荐CPU使用）
   - YOLOv8s: 平衡速度和精度
   - YOLOv8m: 精度优先

3. **输入分辨率调优**
   - 640x640: 标准分辨率
   - 416x416: 高速场景
   - 动态分辨率: 根据CPU负载自动调整

### 多摄像头场景优化

1. **4摄像头配置（推荐）**
```yaml
system:
  frame_reader_threads: 2
  vehicle_detection_threads: 4
  plate_recognition_threads: 6
  api_push_threads: 2
  target_fps: 18
```

2. **8摄像头配置**
```yaml
system:
  frame_reader_threads: 4
  vehicle_detection_threads: 6
  plate_recognition_threads: 8
  api_push_threads: 3
  target_fps: 12
  smart_frame_skip: true
```

## 故障排除

### 常见问题

1. **CPU使用率过高**
   - 降低target_fps
   - 启用smart_frame_skip
   - 减少摄像头数量
   - 使用更轻量级模型

2. **内存使用率过高**
   - 减少queue_size
   - 启用对象池
   - 调整gc_threshold
   - 检查内存泄漏

3. **FPS过低**
   - 检查模型文件是否正确
   - 优化输入分辨率
   - 增加检测线程数
   - 启用CPU优化

4. **摄像头连接失败**
   - 检查网络连接
   - 验证RTSP URL
   - 调整超时设置
   - 检查摄像头兼容性

### 日志分析

系统日志位于`logs/`目录：
- `license_plate_recognition.log`: 主要系统日志
- `errors.log`: 错误日志
- `performance.log`: 性能指标日志

关键日志信息：
```
# 正常运行
INFO - Camera 0 FPS: 15.2, Detection: 45.3ms, Recognition: 23.1ms

# 性能警告
WARNING - High CPU usage detected: 87.5%

# 错误信息
ERROR - Failed to connect to camera 1: rtsp://...
```

## 系统服务

### 安装为系统服务（Linux）

```bash
# 复制服务文件
sudo cp license-plate-recognition.service /etc/systemd/system/

# 重新加载systemd
sudo systemctl daemon-reload

# 启用自动启动
sudo systemctl enable license-plate-recognition

# 启动服务
sudo systemctl start license-plate-recognition

# 查看服务状态
sudo systemctl status license-plate-recognition

# 查看服务日志
journalctl -u license-plate-recognition -f
```

### Windows服务安装

使用NSSM (Non-Sucking Service Manager)：

```cmd
# 下载并安装NSSM
nssm install "LicensePlateRecognition" "C:\Python\python.exe" "C:\path\to\start.py"

# 启动服务
net start LicensePlateRecognition

# 停止服务
net stop LicensePlateRecognition
```

## 开发指南

### 项目结构

```
├── main.py                     # 主程序入口
├── config_manager.py           # 配置管理
├── cpu_optimizer.py            # CPU优化模块
├── model_optimizer.py          # 模型优化模块
├── performance_monitor.py      # 性能监控模块
├── setup.py                    # 安装脚本
├── requirements.txt            # Python依赖
├── config.yaml                 # 配置文件
├── start.py                    # 启动脚本
├── stop.py                     # 停止脚本
├── models/                     # 模型文件目录
├── logs/                       # 日志文件目录
├── model_cache/                # 模型缓存目录
└── temp_models/                # 临时模型目录
```

### 扩展开发

1. **添加新的检测算法**
```python
class CustomDetector(CPUOptimizedDetector):
    def detect_vehicles(self, frame):
        # 实现自定义检测逻辑
        return vehicles
```

2. **添加新的识别算法**
```python
class CustomRecognizer(LicensePlateRecognizer):
    def recognize_plate(self, frame, bbox):
        # 实现自定义识别逻辑
        return result
```

3. **添加新的性能监控指标**
```python
# 在performance_monitor.py中扩展
def custom_metric_collector(self):
    # 收集自定义指标
    return metrics
```

## 贡献指南

1. Fork本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 许可证

本项目采用Apache 2.0许可证 - 查看[LICENSE](LICENSE)文件了解详情。

## 技术支持

- **问题反馈**: [GitHub Issues](https://github.com/feiyuchuixue/sz-boot-parent/issues)
- **技术交流**: 加入技术交流群
- **商业支持**: 联系邮箱 feiyuchuixue@163.com

## 更新日志

### v1.0.0 (2024-01-XX)
- 🎉 首次发布
- ✨ CPU优化的车牌识别系统
- ✨ 多线程架构支持
- ✨ 自适应性能调优
- ✨ 完整的配置管理系统
- ✨ 详细的性能监控

---

**CPU优化版车牌识别系统** - 让AI在CPU上飞起来！ 🚀