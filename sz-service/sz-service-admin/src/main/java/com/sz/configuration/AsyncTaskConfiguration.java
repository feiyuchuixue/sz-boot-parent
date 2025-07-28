package com.sz.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * 异步任务线程池配置类
 * 用于配置自定义线程池
 */
@Configuration
public class AsyncTaskConfiguration {

    // 核心线程数：线程池中始终保持活跃的线程数量
    private static final int CORE_POOL_SIZE = 5;
    // 最大线程数：线程池中允许的最大线程数量（当任务队列满时，线程池会创建新线程直到达到该值）
    private static final int MAX_POOL_SIZE = 10;
    // 任务队列容量：用于存放等待执行任务的队列大小
    private static final int QUEUE_CAPACITY = 200;
    // 空闲线程存活时间（秒）：非核心线程在空闲超过该时间后会被销毁
    private static final int KEEP_ALIVE_SECONDS = 60;

    /**
     * 配置返回一个登陆日志记录的线程池
     */
    @Bean("loginLogExecutor")
    public ThreadPoolTaskExecutor loginLogExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix("async-login-log-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }

}
