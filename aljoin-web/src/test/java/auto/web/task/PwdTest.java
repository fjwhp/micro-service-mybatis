package auto.web.task;

import org.junit.Test;

import com.alibaba.druid.filter.config.ConfigTools;

public class PwdTest {

	/**
	 * 数据库：获取加密密码以及公钥私钥
	 */
	@Test
	public void test() throws Exception {
		//String[] pwd = new String[] { "1qaz@WSX" };
	    String[] pwd = new String[] { "1qaz2wsx" };
		ConfigTools.main(pwd);
	}

}