package com.spacebased.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * AsyncConfig - Cấu hình thread pool cho Message Grid
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "messageGridExecutor")
    public Executor messageGridExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("MessageGrid-");
        executor.initialize();
        return executor;
    }
}
