package org.example.echo.article.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    @Bean(name = "customThreadPool")
    public Executor customThreadPool() {
        return new ThreadPoolExecutor(
                40,
                48,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
