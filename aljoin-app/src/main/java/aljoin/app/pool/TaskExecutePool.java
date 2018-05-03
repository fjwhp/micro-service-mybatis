package aljoin.app.pool;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import aljoin.config.TaskThreadPoolConfig;

/**
 * 
 * 创建线程池
 *
 * @author：zhongjy
 *
 * @date：2017年6月5日 下午6:46:49
 */
@Configuration
@EnableAsync
public class TaskExecutePool {

    @Resource
    private TaskThreadPoolConfig config;

    /**
     * 
     * 创建线程池bean
     *
     * @return：Executor
     *
     * @author：zhongjy
     *
     * @date：2017年6月5日 下午6:54:02
     */
    @Bean
    public Executor taskAsyncPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getCorePoolSize());
        executor.setMaxPoolSize(config.getMaxPoolSize());
        executor.setQueueCapacity(config.getQueueCapacity());
        executor.setKeepAliveSeconds(config.getKeepAliveSeconds());
        executor.setThreadNamePrefix("aljoin(web线程池)_Executor-");
        // 拒绝策略：当pllo达到最大值时,不在新线程中执行任务,由调用者所在的线程来执行。
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
