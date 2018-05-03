package aljoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * 项目启动类
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午3:56:45
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Main.class);
        application.run(args);
    }

}
