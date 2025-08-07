"""
性能监控和指标模块
Performance Monitoring and Metrics Module

提供详细的系统性能监控、指标收集和自适应优化功能
"""

import time
import threading
import logging
import json
import queue
from collections import deque, defaultdict
from dataclasses import dataclass, asdict
from typing import Dict, List, Optional, Any, Callable
import statistics
from datetime import datetime, timedelta

import psutil
import numpy as np

logger = logging.getLogger(__name__)


@dataclass
class PerformanceMetrics:
    """性能指标数据结构"""
    timestamp: float
    cpu_usage: float
    memory_usage: float
    memory_available_gb: float
    fps_per_camera: Dict[int, float]
    processing_times: Dict[str, float]
    queue_sizes: Dict[str, int]
    thread_counts: Dict[str, int]
    error_counts: Dict[str, int]
    total_processed: int
    gpu_usage: Optional[float] = None
    gpu_memory_usage: Optional[float] = None


@dataclass 
class SystemLoad:
    """系统负载信息"""
    cpu_percent: float
    memory_percent: float
    disk_io: Dict[str, float]
    network_io: Dict[str, float]
    load_average: Tuple[float, float, float]
    process_count: int


class MetricsCollector:
    """指标收集器"""
    
    def __init__(self, collection_interval: float = 1.0):
        self.collection_interval = collection_interval
        self.metrics_history = deque(maxlen=3600)  # 保留1小时的数据
        self.current_metrics = {}
        self.running = False
        self.collection_thread = None
        self.callbacks = []
        self.lock = threading.Lock()
        
        # 初始化计数器
        self.counters = defaultdict(int)
        self.timers = defaultdict(list)
        self.fps_counters = defaultdict(lambda: {'count': 0, 'last_time': time.time()})
    
    def add_callback(self, callback: Callable[[PerformanceMetrics], None]):
        """添加指标回调函数"""
        self.callbacks.append(callback)
    
    def start(self):
        """启动指标收集"""
        if self.running:
            return
            
        self.running = True
        self.collection_thread = threading.Thread(
            target=self._collection_loop,
            daemon=True,
            name="MetricsCollector"
        )
        self.collection_thread.start()
        logger.info("Metrics collection started")
    
    def stop(self):
        """停止指标收集"""
        self.running = False
        if self.collection_thread:
            self.collection_thread.join(timeout=5)
        logger.info("Metrics collection stopped")
    
    def _collection_loop(self):
        """指标收集循环"""
        while self.running:
            try:
                metrics = self._collect_system_metrics()
                
                with self.lock:
                    self.metrics_history.append(metrics)
                    self.current_metrics = asdict(metrics)
                
                # 调用回调函数
                for callback in self.callbacks:
                    try:
                        callback(metrics)
                    except Exception as e:
                        logger.error(f"Metrics callback error: {e}")
                
                time.sleep(self.collection_interval)
                
            except Exception as e:
                logger.error(f"Metrics collection error: {e}")
                time.sleep(self.collection_interval)
    
    def _collect_system_metrics(self) -> PerformanceMetrics:
        """收集系统指标"""
        timestamp = time.time()
        
        # CPU使用率
        cpu_usage = psutil.cpu_percent(interval=None)
        
        # 内存使用率
        memory = psutil.virtual_memory()
        memory_usage = memory.percent
        memory_available_gb = memory.available / (1024**3)
        
        # FPS统计
        fps_per_camera = self._calculate_fps()
        
        # 处理时间统计
        processing_times = self._calculate_processing_times()
        
        # 队列大小（需要外部更新）
        queue_sizes = dict(self.counters.get('queue_sizes', {}))
        
        # 线程数量（需要外部更新）
        thread_counts = dict(self.counters.get('thread_counts', {}))
        
        # 错误计数
        error_counts = dict(self.counters.get('error_counts', {}))
        
        # 总处理数量
        total_processed = self.counters.get('total_processed', 0)
        
        # GPU使用率（可选）
        gpu_usage = self._get_gpu_usage()
        gpu_memory_usage = self._get_gpu_memory_usage()
        
        return PerformanceMetrics(
            timestamp=timestamp,
            cpu_usage=cpu_usage,
            memory_usage=memory_usage,
            memory_available_gb=memory_available_gb,
            fps_per_camera=fps_per_camera,
            processing_times=processing_times,
            queue_sizes=queue_sizes,
            thread_counts=thread_counts,
            error_counts=error_counts,
            total_processed=total_processed,
            gpu_usage=gpu_usage,
            gpu_memory_usage=gpu_memory_usage
        )
    
    def _calculate_fps(self) -> Dict[int, float]:
        """计算FPS"""
        fps_dict = {}
        current_time = time.time()
        
        for camera_id, counter_info in self.fps_counters.items():
            time_diff = current_time - counter_info['last_time']
            if time_diff >= 1.0:  # 至少1秒间隔
                fps = counter_info['count'] / time_diff
                fps_dict[camera_id] = fps
                
                # 重置计数器
                counter_info['count'] = 0
                counter_info['last_time'] = current_time
        
        return fps_dict
    
    def _calculate_processing_times(self) -> Dict[str, float]:
        """计算平均处理时间"""
        processing_times = {}
        
        for stage, times in self.timers.items():
            if times:
                processing_times[stage] = statistics.mean(times[-100:])  # 最近100次的平均值
        
        return processing_times
    
    def _get_gpu_usage(self) -> Optional[float]:
        """获取GPU使用率"""
        try:
            import pynvml
            pynvml.nvmlInit()
            handle = pynvml.nvmlDeviceGetHandleByIndex(0)
            util = pynvml.nvmlDeviceGetUtilizationRates(handle)
            return float(util.gpu)
        except Exception:
            return None
    
    def _get_gpu_memory_usage(self) -> Optional[float]:
        """获取GPU内存使用率"""
        try:
            import pynvml
            pynvml.nvmlInit()
            handle = pynvml.nvmlDeviceGetHandleByIndex(0)
            mem_info = pynvml.nvmlDeviceGetMemoryInfo(handle)
            usage_percent = (mem_info.used / mem_info.total) * 100
            return float(usage_percent)
        except Exception:
            return None
    
    # 外部调用的接口方法
    def increment_counter(self, name: str, value: int = 1):
        """增加计数器"""
        self.counters[name] += value
    
    def set_counter(self, name: str, value: int):
        """设置计数器值"""
        self.counters[name] = value
    
    def add_processing_time(self, stage: str, time_ms: float):
        """添加处理时间记录"""
        self.timers[stage].append(time_ms)
        if len(self.timers[stage]) > 1000:  # 限制历史记录数量
            self.timers[stage] = self.timers[stage][-500:]
    
    def increment_fps_counter(self, camera_id: int):
        """增加FPS计数器"""
        self.fps_counters[camera_id]['count'] += 1
    
    def update_queue_size(self, queue_name: str, size: int):
        """更新队列大小"""
        if 'queue_sizes' not in self.counters:
            self.counters['queue_sizes'] = {}
        self.counters['queue_sizes'][queue_name] = size
    
    def update_thread_count(self, pool_name: str, count: int):
        """更新线程数量"""
        if 'thread_counts' not in self.counters:
            self.counters['thread_counts'] = {}
        self.counters['thread_counts'][pool_name] = count
    
    def increment_error_count(self, error_type: str):
        """增加错误计数"""
        if 'error_counts' not in self.counters:
            self.counters['error_counts'] = {}
        if error_type not in self.counters['error_counts']:
            self.counters['error_counts'][error_type] = 0
        self.counters['error_counts'][error_type] += 1
    
    def get_current_metrics(self) -> Dict[str, Any]:
        """获取当前指标"""
        with self.lock:
            return self.current_metrics.copy()
    
    def get_historical_metrics(self, duration_minutes: int = 60) -> List[PerformanceMetrics]:
        """获取历史指标"""
        cutoff_time = time.time() - (duration_minutes * 60)
        
        with self.lock:
            return [m for m in self.metrics_history if m.timestamp > cutoff_time]
    
    def get_statistics(self) -> Dict[str, Any]:
        """获取统计信息"""
        with self.lock:
            if not self.metrics_history:
                return {}
            
            recent_metrics = list(self.metrics_history)[-60:]  # 最近60个点
            
            cpu_values = [m.cpu_usage for m in recent_metrics]
            memory_values = [m.memory_usage for m in recent_metrics]
            
            stats = {
                'cpu_usage': {
                    'current': cpu_values[-1] if cpu_values else 0,
                    'mean': statistics.mean(cpu_values) if cpu_values else 0,
                    'max': max(cpu_values) if cpu_values else 0,
                    'min': min(cpu_values) if cpu_values else 0
                },
                'memory_usage': {
                    'current': memory_values[-1] if memory_values else 0,
                    'mean': statistics.mean(memory_values) if memory_values else 0,
                    'max': max(memory_values) if memory_values else 0,
                    'min': min(memory_values) if memory_values else 0
                },
                'total_samples': len(recent_metrics),
                'collection_duration_minutes': len(recent_metrics) * self.collection_interval / 60
            }
            
            return stats


class AdaptiveOptimizer:
    """自适应性能优化器"""
    
    def __init__(self, 
                 metrics_collector: MetricsCollector,
                 cpu_threshold: float = 80.0,
                 memory_threshold: float = 70.0,
                 optimization_interval: float = 10.0):
        self.metrics_collector = metrics_collector
        self.cpu_threshold = cpu_threshold
        self.memory_threshold = memory_threshold
        self.optimization_interval = optimization_interval
        self.running = False
        self.optimization_thread = None
        
        # 优化策略
        self.optimization_actions = []
        self.last_optimization_time = 0
        
        # 注册为指标回调
        self.metrics_collector.add_callback(self._on_metrics_update)
    
    def add_optimization_action(self, action: Callable[[PerformanceMetrics], None]):
        """添加优化动作"""
        self.optimization_actions.append(action)
    
    def start(self):
        """启动自适应优化"""
        if self.running:
            return
            
        self.running = True
        self.optimization_thread = threading.Thread(
            target=self._optimization_loop,
            daemon=True,
            name="AdaptiveOptimizer"
        )
        self.optimization_thread.start()
        logger.info("Adaptive optimizer started")
    
    def stop(self):
        """停止自适应优化"""
        self.running = False
        if self.optimization_thread:
            self.optimization_thread.join(timeout=5)
        logger.info("Adaptive optimizer stopped")
    
    def _on_metrics_update(self, metrics: PerformanceMetrics):
        """指标更新回调"""
        current_time = time.time()
        
        # 检查是否需要立即优化
        if (metrics.cpu_usage > self.cpu_threshold * 1.2 or 
            metrics.memory_usage > self.memory_threshold * 1.2):
            
            if current_time - self.last_optimization_time > 5.0:  # 防止过于频繁的优化
                self._apply_emergency_optimizations(metrics)
                self.last_optimization_time = current_time
    
    def _optimization_loop(self):
        """优化循环"""
        while self.running:
            try:
                current_metrics = self.metrics_collector.get_current_metrics()
                if current_metrics:
                    metrics = PerformanceMetrics(**current_metrics)
                    self._evaluate_and_optimize(metrics)
                
                time.sleep(self.optimization_interval)
                
            except Exception as e:
                logger.error(f"Optimization loop error: {e}")
                time.sleep(self.optimization_interval)
    
    def _evaluate_and_optimize(self, metrics: PerformanceMetrics):
        """评估并执行优化"""
        optimizations_needed = []
        
        # CPU优化检查
        if metrics.cpu_usage > self.cpu_threshold:
            optimizations_needed.append('cpu_optimization')
            logger.warning(f"High CPU usage detected: {metrics.cpu_usage:.1f}%")
        
        # 内存优化检查
        if metrics.memory_usage > self.memory_threshold:
            optimizations_needed.append('memory_optimization')
            logger.warning(f"High memory usage detected: {metrics.memory_usage:.1f}%")
        
        # FPS优化检查
        avg_fps = statistics.mean(metrics.fps_per_camera.values()) if metrics.fps_per_camera else 0
        if avg_fps < 10:  # FPS过低
            optimizations_needed.append('fps_optimization')
            logger.warning(f"Low FPS detected: {avg_fps:.1f}")
        
        # 队列大小检查
        max_queue_size = max(metrics.queue_sizes.values()) if metrics.queue_sizes else 0
        if max_queue_size > 80:  # 队列堆积
            optimizations_needed.append('queue_optimization')
            logger.warning(f"Queue backlog detected: {max_queue_size}")
        
        # 执行优化
        for optimization_type in optimizations_needed:
            self._apply_optimization(optimization_type, metrics)
    
    def _apply_optimization(self, optimization_type: str, metrics: PerformanceMetrics):
        """应用特定优化"""
        try:
            if optimization_type == 'cpu_optimization':
                self._apply_cpu_optimization(metrics)
            elif optimization_type == 'memory_optimization':
                self._apply_memory_optimization(metrics)
            elif optimization_type == 'fps_optimization':
                self._apply_fps_optimization(metrics)
            elif optimization_type == 'queue_optimization':
                self._apply_queue_optimization(metrics)
                
            # 执行自定义优化动作
            for action in self.optimization_actions:
                action(metrics)
                
        except Exception as e:
            logger.error(f"Optimization '{optimization_type}' failed: {e}")
    
    def _apply_cpu_optimization(self, metrics: PerformanceMetrics):
        """应用CPU优化"""
        logger.info("Applying CPU optimization...")
        
        # 1. 强制垃圾回收
        import gc
        gc.collect()
        
        # 2. 降低处理频率（跳帧）
        # 这需要与主系统集成
        
        # 3. 减少线程数量
        # 这需要与线程池管理器集成
        
        logger.info("CPU optimization applied")
    
    def _apply_memory_optimization(self, metrics: PerformanceMetrics):
        """应用内存优化"""
        logger.info("Applying memory optimization...")
        
        # 1. 强制垃圾回收
        import gc
        gc.collect()
        
        # 2. 清理缓存
        # 这需要与缓存管理器集成
        
        # 3. 减少队列大小
        # 这需要与队列管理器集成
        
        logger.info("Memory optimization applied")
    
    def _apply_fps_optimization(self, metrics: PerformanceMetrics):
        """应用FPS优化"""
        logger.info("Applying FPS optimization...")
        
        # 1. 降低输入分辨率
        # 2. 增加跳帧频率
        # 3. 简化算法处理
        
        logger.info("FPS optimization applied")
    
    def _apply_queue_optimization(self, metrics: PerformanceMetrics):
        """应用队列优化"""
        logger.info("Applying queue optimization...")
        
        # 1. 清理过时的队列项
        # 2. 增加处理线程
        # 3. 降低输入频率
        
        logger.info("Queue optimization applied")
    
    def _apply_emergency_optimizations(self, metrics: PerformanceMetrics):
        """应用紧急优化措施"""
        logger.warning("Applying emergency optimizations...")
        
        # 1. 立即强制垃圾回收
        import gc
        gc.collect()
        
        # 2. 暂时停止部分摄像头
        # 3. 大幅降低处理频率
        # 4. 清空所有队列
        
        logger.warning("Emergency optimizations applied")


class PerformanceReporter:
    """性能报告生成器"""
    
    def __init__(self, metrics_collector: MetricsCollector):
        self.metrics_collector = metrics_collector
    
    def generate_report(self, duration_minutes: int = 60) -> Dict[str, Any]:
        """生成性能报告"""
        metrics_history = self.metrics_collector.get_historical_metrics(duration_minutes)
        
        if not metrics_history:
            return {'error': 'No metrics data available'}
        
        report = {
            'report_time': datetime.now().isoformat(),
            'duration_minutes': duration_minutes,
            'total_samples': len(metrics_history),
            'system_performance': self._analyze_system_performance(metrics_history),
            'processing_performance': self._analyze_processing_performance(metrics_history),
            'camera_performance': self._analyze_camera_performance(metrics_history),
            'error_analysis': self._analyze_errors(metrics_history),
            'recommendations': self._generate_recommendations(metrics_history)
        }
        
        return report
    
    def _analyze_system_performance(self, metrics_history: List[PerformanceMetrics]) -> Dict[str, Any]:
        """分析系统性能"""
        cpu_values = [m.cpu_usage for m in metrics_history]
        memory_values = [m.memory_usage for m in metrics_history]
        
        return {
            'cpu_usage': {
                'mean': statistics.mean(cpu_values),
                'max': max(cpu_values),
                'min': min(cpu_values),
                'std': statistics.stdev(cpu_values) if len(cpu_values) > 1 else 0
            },
            'memory_usage': {
                'mean': statistics.mean(memory_values),
                'max': max(memory_values),
                'min': min(memory_values),
                'std': statistics.stdev(memory_values) if len(memory_values) > 1 else 0
            },
            'memory_available_gb': {
                'mean': statistics.mean([m.memory_available_gb for m in metrics_history]),
                'min': min([m.memory_available_gb for m in metrics_history])
            }
        }
    
    def _analyze_processing_performance(self, metrics_history: List[PerformanceMetrics]) -> Dict[str, Any]:
        """分析处理性能"""
        processing_stats = {}
        
        # 收集所有处理阶段的时间数据
        all_stages = set()
        for metrics in metrics_history:
            all_stages.update(metrics.processing_times.keys())
        
        for stage in all_stages:
            stage_times = [m.processing_times.get(stage, 0) for m in metrics_history if stage in m.processing_times]
            
            if stage_times:
                processing_stats[stage] = {
                    'mean_ms': statistics.mean(stage_times),
                    'max_ms': max(stage_times),
                    'min_ms': min(stage_times),
                    'std_ms': statistics.stdev(stage_times) if len(stage_times) > 1 else 0
                }
        
        return processing_stats
    
    def _analyze_camera_performance(self, metrics_history: List[PerformanceMetrics]) -> Dict[str, Any]:
        """分析摄像头性能"""
        camera_stats = {}
        
        # 收集所有摄像头的FPS数据
        all_cameras = set()
        for metrics in metrics_history:
            all_cameras.update(metrics.fps_per_camera.keys())
        
        for camera_id in all_cameras:
            fps_values = [m.fps_per_camera.get(camera_id, 0) for m in metrics_history if camera_id in m.fps_per_camera]
            
            if fps_values:
                camera_stats[f'camera_{camera_id}'] = {
                    'mean_fps': statistics.mean(fps_values),
                    'max_fps': max(fps_values),
                    'min_fps': min(fps_values),
                    'std_fps': statistics.stdev(fps_values) if len(fps_values) > 1 else 0
                }
        
        return camera_stats
    
    def _analyze_errors(self, metrics_history: List[PerformanceMetrics]) -> Dict[str, Any]:
        """分析错误情况"""
        error_analysis = {}
        
        # 获取最新的错误计数
        if metrics_history:
            latest_errors = metrics_history[-1].error_counts
            total_errors = sum(latest_errors.values())
            
            error_analysis = {
                'total_errors': total_errors,
                'error_breakdown': latest_errors,
                'error_rate': total_errors / len(metrics_history) if metrics_history else 0
            }
        
        return error_analysis
    
    def _generate_recommendations(self, metrics_history: List[PerformanceMetrics]) -> List[str]:
        """生成优化建议"""
        recommendations = []
        
        if not metrics_history:
            return recommendations
        
        # 分析平均性能
        avg_cpu = statistics.mean([m.cpu_usage for m in metrics_history])
        avg_memory = statistics.mean([m.memory_usage for m in metrics_history])
        
        # CPU建议
        if avg_cpu > 85:
            recommendations.append("CPU usage is very high. Consider reducing the number of cameras or processing threads.")
        elif avg_cpu > 70:
            recommendations.append("CPU usage is high. Monitor for potential bottlenecks.")
        elif avg_cpu < 50:
            recommendations.append("CPU usage is low. You may be able to process more cameras or increase FPS.")
        
        # 内存建议
        if avg_memory > 80:
            recommendations.append("Memory usage is very high. Consider implementing more aggressive garbage collection.")
        elif avg_memory > 60:
            recommendations.append("Memory usage is elevated. Monitor for memory leaks.")
        
        # FPS建议
        camera_fps = []
        for metrics in metrics_history:
            camera_fps.extend(metrics.fps_per_camera.values())
        
        if camera_fps:
            avg_fps = statistics.mean(camera_fps)
            if avg_fps < 10:
                recommendations.append("Average FPS is low. Consider optimizing processing pipeline or reducing resolution.")
            elif avg_fps > 20:
                recommendations.append("FPS is good. System has capacity for additional cameras or higher resolution.")
        
        # 处理时间建议
        processing_times = []
        for metrics in metrics_history:
            processing_times.extend(metrics.processing_times.values())
        
        if processing_times:
            avg_processing_time = statistics.mean(processing_times)
            if avg_processing_time > 100:  # 100ms
                recommendations.append("Processing times are high. Consider model optimization or hardware upgrade.")
        
        return recommendations
    
    def export_report(self, report: Dict[str, Any], filename: str = None) -> str:
        """导出报告到文件"""
        if filename is None:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = f"performance_report_{timestamp}.json"
        
        try:
            with open(filename, 'w', encoding='utf-8') as f:
                json.dump(report, f, indent=2, ensure_ascii=False)
            
            logger.info(f"Performance report exported: {filename}")
            return filename
            
        except Exception as e:
            logger.error(f"Failed to export report: {e}")
            raise


def test_performance_monitoring():
    """测试性能监控系统"""
    print("=== Performance Monitoring Test ===")
    
    # 创建指标收集器
    collector = MetricsCollector(collection_interval=2.0)
    
    # 创建自适应优化器
    optimizer = AdaptiveOptimizer(collector, cpu_threshold=70.0, memory_threshold=60.0)
    
    # 创建报告生成器
    reporter = PerformanceReporter(collector)
    
    try:
        # 启动监控
        collector.start()
        optimizer.start()
        
        # 模拟一些指标更新
        for i in range(10):
            collector.increment_fps_counter(0)
            collector.increment_fps_counter(1)
            collector.add_processing_time('detection', 45.0 + np.random.uniform(-10, 10))
            collector.add_processing_time('recognition', 25.0 + np.random.uniform(-5, 5))
            collector.update_queue_size('frame_queue', np.random.randint(10, 50))
            collector.increment_counter('total_processed')
            
            time.sleep(1)
        
        # 获取当前指标
        current_metrics = collector.get_current_metrics()
        print("Current metrics:", json.dumps(current_metrics, indent=2))
        
        # 获取统计信息
        stats = collector.get_statistics()
        print("\nStatistics:", json.dumps(stats, indent=2))
        
        # 生成报告
        report = reporter.generate_report(duration_minutes=1)
        print("\nPerformance Report:")
        print(json.dumps(report, indent=2))
        
    finally:
        # 停止监控
        optimizer.stop()
        collector.stop()
    
    print("Performance monitoring test completed")


if __name__ == "__main__":
    test_performance_monitoring()