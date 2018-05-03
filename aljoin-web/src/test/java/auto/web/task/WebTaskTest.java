package auto.web.task;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import aljoin.util.JsonUtil;
import aljoin.web.task.WebTask;

//告诉Junit运行使用Spring 的单元测试支持
@RunWith(SpringRunner.class)
// 带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebTaskTest {

	private final static Logger logger = LoggerFactory.getLogger(WebTask.class);

	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;

	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Resource
	private WebTask webTask;

	@Test
	public void demo() throws Exception {
		String uri = "http://localhost:8082/v1/merchantApp/activityController/getActivityList";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("merchantId", "1098e1ea3bc346c9bc9a284898b8f462");
		mhsrb.param("pageNum", "1");
		mhsrb.param("pageSize", "2");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		logger.info("===================返回结果beg=======================");
		logger.info("status:" + status);
		logger.info("content:" + JsonUtil.str2json(content));
		logger.info("===================返回结果end=======================");
	}

	@Test
	public void webLogTaskTest() throws Exception {
		/*for (int i = 0; i < 100; i++) {
			webTask.webLogTask(i);
		}
		for (int i = 0; i < 100; i++) {
			webTask.webLogTask2(i);
		}*/
	}

}
