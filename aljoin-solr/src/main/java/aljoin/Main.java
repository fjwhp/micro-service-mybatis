package aljoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @描述：TODO
 *
 * @作者：wuhp
 * 
 * @时间：2018年4月19日 下午2:32:50
 */
@SpringBootApplication
public class Main {
    /**
     *    
     * @描述：TODO
     *
     * @参数: @param args 
     *
     * @返回：void
     *
     * @作者：wuhp
     *
     * @时间：2018年4月19日 下午2:33:07
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Main.class);
        application.run(args);
    }

}
