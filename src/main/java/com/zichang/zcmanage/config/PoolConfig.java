package com.zichang.zcmanage.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class PoolConfig {

    @Bean(name="globalThreadPool")
    public Executor globalThreadPool() {
        return new ThreadPoolExecutor(
                8,                              // 核心线程数
                16,                             // 最大线程数
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),// 队列容量
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
