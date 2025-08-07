"""
CPU优化工具模块
CPU Optimization Utilities Module

提供CPU相关的优化功能，包括NUMA支持、CPU亲和性设置、Intel优化等
"""

import os
import sys
import logging
import platform
import subprocess
from typing import List, Optional, Dict, Any
import psutil
import threading
import multiprocessing

logger = logging.getLogger(__name__)


class CPUOptimizer:
    """CPU优化器类"""
    
    def __init__(self):
        self.cpu_count = multiprocessing.cpu_count()
        self.physical_cores = psutil.cpu_count(logical=False)
        self.logical_cores = psutil.cpu_count(logical=True)
        self.cpu_info = self._get_cpu_info()
        self.numa_topology = self._get_numa_topology()
        
    def _get_cpu_info(self) -> Dict[str, Any]:
        """获取CPU信息"""
        cpu_info = {
            'physical_cores': self.physical_cores,
            'logical_cores': self.logical_cores,
            'architecture': platform.machine(),
            'processor': platform.processor(),
            'cpu_freq': None,
            'cache_info': {},
            'features': []
        }
        
        try:
            # 获取CPU频率信息
            cpu_freq = psutil.cpu_freq()
            if cpu_freq:
                cpu_info['cpu_freq'] = {
                    'current': cpu_freq.current,
                    'min': cpu_freq.min,
                    'max': cpu_freq.max
                }
        except Exception as e:
            logger.warning(f"Failed to get CPU frequency: {e}")
        
        # 检测Intel CPU特性
        if self._is_intel_cpu():
            cpu_info['vendor'] = 'Intel'
            cpu_info['features'] = self._detect_intel_features()
        elif self._is_amd_cpu():
            cpu_info['vendor'] = 'AMD'
            cpu_info['features'] = self._detect_amd_features()
        
        return cpu_info
    
    def _is_intel_cpu(self) -> bool:
        """检测是否为Intel CPU"""
        try:
            with open('/proc/cpuinfo', 'r') as f:
                content = f.read().lower()
                return 'intel' in content or 'genuineintel' in content
        except Exception:
            return 'intel' in platform.processor().lower()
    
    def _is_amd_cpu(self) -> bool:
        """检测是否为AMD CPU"""
        try:
            with open('/proc/cpuinfo', 'r') as f:
                content = f.read().lower()
                return 'amd' in content or 'authenticamd' in content
        except Exception:
            return 'amd' in platform.processor().lower()
    
    def _detect_intel_features(self) -> List[str]:
        """检测Intel CPU特性"""
        features = []
        
        try:
            # 检测AVX支持
            result = subprocess.run(['cat', '/proc/cpuinfo'], 
                                  capture_output=True, text=True)
            if result.returncode == 0:
                cpuinfo = result.stdout.lower()
                
                if 'avx512' in cpuinfo:
                    features.append('AVX512')
                elif 'avx2' in cpuinfo:
                    features.append('AVX2')
                elif 'avx' in cpuinfo:
                    features.append('AVX')
                
                if 'sse4_2' in cpuinfo:
                    features.append('SSE4.2')
                if 'sse4_1' in cpuinfo:
                    features.append('SSE4.1')
                if 'ssse3' in cpuinfo:
                    features.append('SSSE3')
                if 'sse3' in cpuinfo:
                    features.append('SSE3')
                
                # Intel特定特性
                if 'intel_pstate' in cpuinfo:
                    features.append('Intel P-State')
                if 'intel_iommu' in cpuinfo:
                    features.append('Intel IOMMU')
                
        except Exception as e:
            logger.warning(f"Failed to detect Intel features: {e}")
        
        return features
    
    def _detect_amd_features(self) -> List[str]:
        """检测AMD CPU特性"""
        features = []
        
        try:
            result = subprocess.run(['cat', '/proc/cpuinfo'], 
                                  capture_output=True, text=True)
            if result.returncode == 0:
                cpuinfo = result.stdout.lower()
                
                if 'avx2' in cpuinfo:
                    features.append('AVX2')
                elif 'avx' in cpuinfo:
                    features.append('AVX')
                
                if 'sse4_2' in cpuinfo:
                    features.append('SSE4.2')
                if 'sse4_1' in cpuinfo:
                    features.append('SSE4.1')
                
        except Exception as e:
            logger.warning(f"Failed to detect AMD features: {e}")
        
        return features
    
    def _get_numa_topology(self) -> Dict[str, Any]:
        """获取NUMA拓扑结构"""
        numa_info = {
            'enabled': False,
            'nodes': [],
            'node_count': 0
        }
        
        try:
            # 检查是否启用NUMA
            if os.path.exists('/sys/devices/system/node'):
                node_dirs = [d for d in os.listdir('/sys/devices/system/node') 
                           if d.startswith('node') and d[4:].isdigit()]
                
                if node_dirs:
                    numa_info['enabled'] = True
                    numa_info['node_count'] = len(node_dirs)
                    
                    for node_dir in sorted(node_dirs):
                        node_id = int(node_dir[4:])
                        node_info = self._get_numa_node_info(node_id)
                        numa_info['nodes'].append(node_info)
                        
        except Exception as e:
            logger.warning(f"Failed to get NUMA topology: {e}")
        
        return numa_info
    
    def _get_numa_node_info(self, node_id: int) -> Dict[str, Any]:
        """获取NUMA节点信息"""
        node_info = {
            'node_id': node_id,
            'cpus': [],
            'memory_mb': 0,
            'distance': []
        }
        
        try:
            # 获取节点CPU列表
            cpu_list_path = f'/sys/devices/system/node/node{node_id}/cpulist'
            if os.path.exists(cpu_list_path):
                with open(cpu_list_path, 'r') as f:
                    cpu_range = f.read().strip()
                    node_info['cpus'] = self._parse_cpu_range(cpu_range)
            
            # 获取节点内存信息
            meminfo_path = f'/sys/devices/system/node/node{node_id}/meminfo'
            if os.path.exists(meminfo_path):
                with open(meminfo_path, 'r') as f:
                    for line in f:
                        if 'MemTotal:' in line:
                            # 提取内存大小（KB转MB）
                            mem_kb = int(line.split()[3])
                            node_info['memory_mb'] = mem_kb // 1024
                            break
                            
        except Exception as e:
            logger.warning(f"Failed to get NUMA node {node_id} info: {e}")
        
        return node_info
    
    def _parse_cpu_range(self, cpu_range: str) -> List[int]:
        """解析CPU范围字符串"""
        cpus = []
        
        try:
            for part in cpu_range.split(','):
                if '-' in part:
                    start, end = map(int, part.split('-'))
                    cpus.extend(range(start, end + 1))
                else:
                    cpus.append(int(part))
        except Exception as e:
            logger.warning(f"Failed to parse CPU range '{cpu_range}': {e}")
        
        return cpus
    
    def set_process_affinity(self, cpu_list: List[int]) -> bool:
        """设置进程CPU亲和性"""
        try:
            process = psutil.Process()
            process.cpu_affinity(cpu_list)
            logger.info(f"Process CPU affinity set to: {cpu_list}")
            return True
        except Exception as e:
            logger.error(f"Failed to set process CPU affinity: {e}")
            return False
    
    def set_thread_affinity(self, thread_id: int, cpu_list: List[int]) -> bool:
        """设置线程CPU亲和性"""
        try:
            # 在Linux上使用pthread_setaffinity_np
            if platform.system() == 'Linux':
                import ctypes
                import ctypes.util
                
                libc = ctypes.CDLL(ctypes.util.find_library('c'))
                
                # 构建CPU集合
                cpu_set_t = ctypes.c_ulong * (1024 // (8 * ctypes.sizeof(ctypes.c_ulong)))
                mask = cpu_set_t()
                
                for cpu in cpu_list:
                    if 0 <= cpu < 1024:
                        word_idx = cpu // (8 * ctypes.sizeof(ctypes.c_ulong))
                        bit_idx = cpu % (8 * ctypes.sizeof(ctypes.c_ulong))
                        mask[word_idx] |= (1 << bit_idx)
                
                # 调用pthread_setaffinity_np
                result = libc.pthread_setaffinity_np(
                    thread_id,
                    ctypes.sizeof(mask),
                    ctypes.byref(mask)
                )
                
                if result == 0:
                    logger.debug(f"Thread {thread_id} CPU affinity set to: {cpu_list}")
                    return True
                else:
                    logger.warning(f"Failed to set thread CPU affinity, error code: {result}")
                    
        except Exception as e:
            logger.warning(f"Failed to set thread CPU affinity: {e}")
        
        return False
    
    def optimize_for_inference(self) -> Dict[str, Any]:
        """为推理优化CPU设置"""
        optimizations = {
            'applied': [],
            'failed': [],
            'recommendations': []
        }
        
        try:
            # 设置CPU governor为performance模式
            if self._set_cpu_governor('performance'):
                optimizations['applied'].append('CPU Governor: performance')
            else:
                optimizations['failed'].append('CPU Governor: performance')
                optimizations['recommendations'].append(
                    'Run: sudo cpupower frequency-set -g performance'
                )
            
            # 禁用CPU空闲状态（减少延迟）
            if self._disable_cpu_idle():
                optimizations['applied'].append('CPU Idle: disabled')
            else:
                optimizations['failed'].append('CPU Idle: disable failed')
                optimizations['recommendations'].append(
                    'Run: sudo cpupower idle-set -D 0'
                )
            
            # 设置进程优先级
            if self._set_process_priority():
                optimizations['applied'].append('Process Priority: high')
            else:
                optimizations['failed'].append('Process Priority: high')
            
            # Intel特定优化
            if self.cpu_info.get('vendor') == 'Intel':
                intel_opts = self._apply_intel_optimizations()
                optimizations['applied'].extend(intel_opts.get('applied', []))
                optimizations['failed'].extend(intel_opts.get('failed', []))
                optimizations['recommendations'].extend(intel_opts.get('recommendations', []))
            
        except Exception as e:
            logger.error(f"CPU optimization error: {e}")
            optimizations['failed'].append(f"General error: {e}")
        
        return optimizations
    
    def _set_cpu_governor(self, governor: str) -> bool:
        """设置CPU调速器"""
        try:
            # 尝试通过cpupower设置
            result = subprocess.run(
                ['cpupower', 'frequency-set', '-g', governor],
                capture_output=True, text=True
            )
            
            if result.returncode == 0:
                return True
            
            # 尝试直接写入sysfs
            for cpu_id in range(self.cpu_count):
                governor_path = f'/sys/devices/system/cpu/cpu{cpu_id}/cpufreq/scaling_governor'
                if os.path.exists(governor_path):
                    with open(governor_path, 'w') as f:
                        f.write(governor)
            
            return True
            
        except Exception as e:
            logger.warning(f"Failed to set CPU governor to {governor}: {e}")
            return False
    
    def _disable_cpu_idle(self) -> bool:
        """禁用CPU空闲状态"""
        try:
            # 尝试通过cpupower禁用
            result = subprocess.run(
                ['cpupower', 'idle-set', '-D', '0'],
                capture_output=True, text=True
            )
            
            return result.returncode == 0
            
        except Exception as e:
            logger.warning(f"Failed to disable CPU idle: {e}")
            return False
    
    def _set_process_priority(self) -> bool:
        """设置进程优先级"""
        try:
            # 设置为高优先级
            os.nice(-10)
            return True
        except Exception as e:
            logger.warning(f"Failed to set process priority: {e}")
            return False
    
    def _apply_intel_optimizations(self) -> Dict[str, List[str]]:
        """应用Intel特定优化"""
        intel_opts = {
            'applied': [],
            'failed': [],
            'recommendations': []
        }
        
        try:
            # 设置Intel P-State驱动参数
            if self._set_intel_pstate_params():
                intel_opts['applied'].append('Intel P-State: optimized')
            else:
                intel_opts['failed'].append('Intel P-State: optimization failed')
            
            # 启用Intel Turbo Boost
            if self._enable_intel_turbo():
                intel_opts['applied'].append('Intel Turbo Boost: enabled')
            else:
                intel_opts['failed'].append('Intel Turbo Boost: enable failed')
            
            # 设置Intel MKL线程数
            mkl_threads = min(self.physical_cores, 8)  # 限制MKL线程数
            os.environ['MKL_NUM_THREADS'] = str(mkl_threads)
            os.environ['INTEL_NUM_THREADS'] = str(mkl_threads)
            intel_opts['applied'].append(f'Intel MKL: {mkl_threads} threads')
            
        except Exception as e:
            logger.warning(f"Intel optimization error: {e}")
            intel_opts['failed'].append(f"Intel optimization error: {e}")
        
        return intel_opts
    
    def _set_intel_pstate_params(self) -> bool:
        """设置Intel P-State参数"""
        try:
            # 设置为性能模式
            pstate_path = '/sys/devices/system/cpu/intel_pstate'
            if os.path.exists(pstate_path):
                # 禁用节能模式
                no_turbo_path = os.path.join(pstate_path, 'no_turbo')
                if os.path.exists(no_turbo_path):
                    with open(no_turbo_path, 'w') as f:
                        f.write('0')
                
                # 设置最小性能比例
                min_perf_path = os.path.join(pstate_path, 'min_perf_pct')
                if os.path.exists(min_perf_path):
                    with open(min_perf_path, 'w') as f:
                        f.write('80')  # 最小80%性能
                
                return True
                
        except Exception as e:
            logger.warning(f"Failed to set Intel P-State parameters: {e}")
        
        return False
    
    def _enable_intel_turbo(self) -> bool:
        """启用Intel Turbo Boost"""
        try:
            turbo_path = '/sys/devices/system/cpu/intel_pstate/no_turbo'
            if os.path.exists(turbo_path):
                with open(turbo_path, 'w') as f:
                    f.write('0')  # 0表示启用turbo
                return True
        except Exception as e:
            logger.warning(f"Failed to enable Intel Turbo: {e}")
        
        return False
    
    def get_optimal_thread_distribution(self, num_workers: int) -> Dict[str, List[int]]:
        """获取最优线程分布"""
        distribution = {
            'thread_pools': {},
            'cpu_assignments': {},
            'numa_aware': self.numa_topology['enabled']
        }
        
        if self.numa_topology['enabled'] and self.numa_topology['node_count'] > 1:
            # NUMA感知的线程分布
            distribution = self._distribute_numa_aware(num_workers)
        else:
            # 简单的线程分布
            distribution = self._distribute_simple(num_workers)
        
        return distribution
    
    def _distribute_numa_aware(self, num_workers: int) -> Dict[str, Any]:
        """NUMA感知的线程分布"""
        distribution = {
            'thread_pools': {},
            'cpu_assignments': {},
            'numa_aware': True
        }
        
        try:
            nodes = self.numa_topology['nodes']
            total_cpus = sum(len(node['cpus']) for node in nodes)
            
            # 为每个NUMA节点分配工作线程
            workers_per_node = max(1, num_workers // len(nodes))
            
            for i, node in enumerate(nodes):
                node_id = node['node_id']
                node_cpus = node['cpus']
                
                if i < len(nodes) - 1:
                    node_workers = workers_per_node
                else:
                    # 最后一个节点分配剩余的工作线程
                    node_workers = num_workers - (workers_per_node * (len(nodes) - 1))
                
                # 为该节点分配CPU
                cpus_per_worker = max(1, len(node_cpus) // node_workers)
                
                for j in range(node_workers):
                    worker_id = f"node{node_id}_worker{j}"
                    start_idx = j * cpus_per_worker
                    end_idx = min(start_idx + cpus_per_worker, len(node_cpus))
                    
                    distribution['cpu_assignments'][worker_id] = node_cpus[start_idx:end_idx]
            
        except Exception as e:
            logger.warning(f"NUMA-aware distribution failed: {e}")
            # 回退到简单分布
            return self._distribute_simple(num_workers)
        
        return distribution
    
    def _distribute_simple(self, num_workers: int) -> Dict[str, Any]:
        """简单的线程分布"""
        distribution = {
            'thread_pools': {},
            'cpu_assignments': {},
            'numa_aware': False
        }
        
        all_cpus = list(range(self.cpu_count))
        cpus_per_worker = max(1, self.cpu_count // num_workers)
        
        for i in range(num_workers):
            worker_id = f"worker{i}"
            start_idx = i * cpus_per_worker
            end_idx = min(start_idx + cpus_per_worker, len(all_cpus))
            
            if start_idx < len(all_cpus):
                distribution['cpu_assignments'][worker_id] = all_cpus[start_idx:end_idx]
        
        return distribution
    
    def print_cpu_info(self):
        """打印CPU信息"""
        print(f"=== CPU Information ===")
        print(f"Physical cores: {self.cpu_info['physical_cores']}")
        print(f"Logical cores: {self.cpu_info['logical_cores']}")
        print(f"Architecture: {self.cpu_info['architecture']}")
        print(f"Processor: {self.cpu_info['processor']}")
        
        if self.cpu_info.get('vendor'):
            print(f"Vendor: {self.cpu_info['vendor']}")
        
        if self.cpu_info.get('cpu_freq'):
            freq = self.cpu_info['cpu_freq']
            print(f"CPU Frequency: {freq['current']:.1f} MHz (min: {freq['min']:.1f}, max: {freq['max']:.1f})")
        
        if self.cpu_info.get('features'):
            print(f"Features: {', '.join(self.cpu_info['features'])}")
        
        print(f"\n=== NUMA Topology ===")
        if self.numa_topology['enabled']:
            print(f"NUMA enabled: {self.numa_topology['node_count']} nodes")
            for node in self.numa_topology['nodes']:
                print(f"  Node {node['node_id']}: CPUs {node['cpus']}, Memory: {node['memory_mb']} MB")
        else:
            print("NUMA not available or disabled")


def test_cpu_optimizer():
    """测试CPU优化器"""
    optimizer = CPUOptimizer()
    
    print("Testing CPU Optimizer...")
    optimizer.print_cpu_info()
    
    print(f"\n=== Optimization Results ===")
    optimizations = optimizer.optimize_for_inference()
    
    if optimizations['applied']:
        print("Applied optimizations:")
        for opt in optimizations['applied']:
            print(f"  ✓ {opt}")
    
    if optimizations['failed']:
        print("\nFailed optimizations:")
        for opt in optimizations['failed']:
            print(f"  ✗ {opt}")
    
    if optimizations['recommendations']:
        print("\nRecommendations:")
        for rec in optimizations['recommendations']:
            print(f"  → {rec}")
    
    print(f"\n=== Thread Distribution Example ===")
    distribution = optimizer.get_optimal_thread_distribution(8)
    print(f"NUMA aware: {distribution['numa_aware']}")
    for worker_id, cpus in distribution['cpu_assignments'].items():
        print(f"  {worker_id}: CPUs {cpus}")


if __name__ == "__main__":
    test_cpu_optimizer()