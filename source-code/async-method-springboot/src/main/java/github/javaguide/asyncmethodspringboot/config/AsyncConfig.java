package github.javaguide.asyncmethodspringboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/** @author shuang.kou */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

  private static final int CORE_POOL_SIZE = 6;
  private static final int MAX_POOL_SIZE = 10;
  private static final int QUEUE_CAPACITY = 100;

  @Bean
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    // 核心线程数
    executor.setCorePoolSize(CORE_POOL_SIZE);
    // 最大线程数
    executor.setMaxPoolSize(MAX_POOL_SIZE);
    // 队列大小
    executor.setQueueCapacity(QUEUE_CAPACITY);
    // 当最大池已满时，此策略保证不会丢失任务请求，但是可能会影响应用程序整体性能。
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setThreadNamePrefix("My ThreadPoolTaskExecutor-");
    executor.initialize();
    return executor;
  }
}
