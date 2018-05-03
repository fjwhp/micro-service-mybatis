package aljoin.web.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import aljoin.file.factory.AljoinFileFactory;
import aljoin.tim.iservice.TimScheduleService;

/**
 * 
 * 项目启动完毕[事件]
 *
 * @author：zhongjy
 *
 * @date：2017年4月29日 上午8:28:34
 */
public class ReadyEvent implements ApplicationListener<ApplicationReadyEvent> {

    private final static Logger logger = LoggerFactory.getLogger(ReadyEvent.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent e) {
        logger.info("\n项目启动完毕[事件]：ReadyEvent");
        /**
         * 获取定时器service
         */
        TimScheduleService timScheduleService = e.getApplicationContext().getBean(TimScheduleService.class);
        /**
         * 获取rocketMQ消费者service
         */
        // ConsumerService consumerService = e.getApplicationContext().getBean(ConsumerService.class);
        /**
         * 文件工厂
         */
        AljoinFileFactory aljoinFileFactory = e.getApplicationContext().getBean(AljoinFileFactory.class);
        try {
            /**
             * 项目启动 同时启动 需要自动启动的定时任务
             */
            timScheduleService.autoStart();
            /**
             * 项目启动 启动rocketMQ消费者
             */
            // consumerService.initConsumer();
            /**
             * 初始化文件配置
             */
            aljoinFileFactory.initConfig();
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

}
