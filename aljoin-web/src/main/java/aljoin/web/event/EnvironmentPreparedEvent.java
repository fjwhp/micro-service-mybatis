package aljoin.web.event;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 
 * 环境已经准备完毕，上下文还没有创建[事件]
 *
 * @author：zhongjy
 *
 * @date：2017年4月29日 上午7:29:29
 */
public class EnvironmentPreparedEvent implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private final static Logger logger = LoggerFactory.getLogger(EnvironmentPreparedEvent.class);

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent e) {
        logger.info("\n环境已经准备完毕，上下文还没有创建[事件]：EnvironmentPreparedEvent");
        List<String> activeProfilesList = Arrays.asList(e.getEnvironment().getActiveProfiles());
        for (String s : activeProfilesList) {
            logger.info("\n激活配置文件：" + s);
        }
    }

}
