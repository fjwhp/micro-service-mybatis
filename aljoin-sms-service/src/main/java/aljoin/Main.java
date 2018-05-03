package aljoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import aljoin.util.SpringContextUtil;

/**
 * 
 * 项目启动类
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:01:55
 */
@SpringBootApplication
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
        // 启动
        ApplicationContext context = application.run(args);
        // 设置上下文到SpringContextUtil
        SpringContextUtil.setApplicationContext(context);

    }

}
