package aljoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import aljoin.app.event.EnvironmentPreparedEvent;
import aljoin.app.event.PreparedEvent;
import aljoin.app.event.ReadyEvent;
import aljoin.util.SpringContextUtil;

/**
 * 
 * 项目启动类
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午3:58:37
 */
@EnableTransactionManagement
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class Main {
  /**
   * 
   * 项目启动类入口方法
   *
   * @return：void
   *
   * @author：zhongjy
   *
   * @date：2017年4月24日 下午12:31:32
   */
  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(Main.class);
    // 环境已经准备完毕，上下文还没有创建[事件]
    application.addListeners(new EnvironmentPreparedEvent());
    // 上下文创建完成[事件]
    application.addListeners(new PreparedEvent());
    // 项目启动完毕[事件]
    application.addListeners(new ReadyEvent());
    // 启动
    ApplicationContext context = application.run(args);
    // 设置上下文到SpringContextUtil
    SpringContextUtil.setApplicationContext(context);
  }

}
