package aljoin.tim.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
@Configuration
public class JobConfig {

    @Bean
    public SchedulerFactoryBean getScheduler() {
        return new SchedulerFactoryBean();
    }
}
