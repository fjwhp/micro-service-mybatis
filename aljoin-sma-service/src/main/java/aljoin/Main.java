package aljoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * 项目启动类
 *
 * @author：zhongjy *
 * @date：2017年4月21日 上午8:40:51
 */
@SpringBootApplication
public class Main {

	/**
	 * 
	 * 项目启动入口方法
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年4月24日 下午12:29:53
	 */
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Main.class);
		application.run(args);
	}

}
