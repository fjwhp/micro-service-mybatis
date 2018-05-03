package aljoin.app.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * 
 * 项目启动完毕[事件]
 *
 * 				@author：zhongjy
 *
 * @date：2017年4月29日 上午8:28:34
 */
public class ReadyEvent implements ApplicationListener<ApplicationReadyEvent> {

	private final static Logger logger = LoggerFactory.getLogger(ReadyEvent.class);

	@Override
	public void onApplicationEvent(ApplicationReadyEvent e) {
		logger.info("\n项目启动完毕[事件]：ReadyEvent");
		// 项目启动(部署)成功，刚刚项目启动(部署)状态
		// System.out.println(e.getApplicationContext().getBean(UserService.class));
	}

}
