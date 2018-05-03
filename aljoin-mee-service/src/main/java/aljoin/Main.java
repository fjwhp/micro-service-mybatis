package aljoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * 项目启动类
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午3:58:37
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
        application.run(args);
    }

}
