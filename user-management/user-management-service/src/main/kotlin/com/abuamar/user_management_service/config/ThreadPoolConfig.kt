package com.abuamar.user_management_service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class ThreadPoolConfig {
    @Bean(name = ["DeleteUserExecutor"])
    fun deleteUserExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 3
        executor.maxPoolSize = 10
        executor.queueCapacity = 3
        executor.keepAliveSeconds = 60
        executor.initialize()
        return executor
    }
}