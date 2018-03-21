package com.d1m.tbmessage.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class Beans {

	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		// regular amount
		int corePoolSize = 10;
		// max amount
		int maxPoolSize = 500;
		// queue capacity
		int queueCapacity = 10;
		// spend
		int keepAlive = 30;

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		// rejection-policy：当pool已经达到max size的时候，如何处理新任务
		// CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); //对拒绝task的处理策略
		executor.setKeepAliveSeconds(keepAlive);
		executor.initialize();
		return executor;
	}
}
