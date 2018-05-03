package aljoin.app.controller.aut;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import aljoin.object.AppConstant;
import aljoin.util.EncryptUtil;
import aljoin.util.JsonUtil;
import aljoin.util.StringUtil;

/**
 * 告诉Junit运行使用Spring 的单元测试支持
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AppAutUserControllerTest {
	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;

	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void testAppLogin() throws Exception {
		String uri = "http://localhost:8080/app/aut/autUser/appLogin";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);

		// 获取ras密钥对
		Map<String, String> keyPair = EncryptUtil.getRsaKeyPair();

		String orgnlUserName = "zhongjy";
		String orgnlUserPwd = "zhongjianyu";
		String rsaPubKey = keyPair.get("public");

		String userName = EncryptUtil.encryptDES(orgnlUserName, AppConstant.APP_DES_KEY);
		String userPwd = EncryptUtil.encryptDES(orgnlUserPwd, AppConstant.APP_DES_KEY);
		mhsrb.param("userName", userName);
		mhsrb.param("userPwd", userPwd);
		mhsrb.param("rsaPubKey", rsaPubKey);// 发送公钥，私钥自己保存
		System.out.println(mhsrb);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(JsonUtil.str2json(content)));
		System.out.println("===================返回结果end=======================");

	}

	@Test
	public void testDoSign() throws Exception {
		long timestamp = System.currentTimeMillis();
		String secret = "7434832448";
		String token = "39FC317CACCE44C6B78C9F8EC52EEB20";

		SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
		parameters.put("timestamp", timestamp);
		parameters.put("token", token);
		parameters.put("a1", "政府大幅度");
		parameters.put("f3", "fdfd");
		parameters.put("a2", "2222fdf22222afd");
		parameters.put("gg", "2222fdfd22222er");
		parameters.put("you", "fdfdf");
		String sign = StringUtil.getSign(parameters, secret);
		System.out.println("生成签名：" + sign);
	}

	@Test
	public void testAppDemo() throws Exception {
		String uri = "http://localhost:8080/app/demo/test";

		Long timestamp = System.currentTimeMillis();
		String secret = "7434832448";
		String token = "6A1B28D9CFFE4A19A89E613A5A8E1953";
		// 要传递的参数
		SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
		parameters.put("timestamp", timestamp);
		parameters.put("token", token);
		parameters.put("a1", "政府大幅度");
		parameters.put("f3", "fdfd");
		parameters.put("a2", "2222fdf22222afd");
		parameters.put("gg", "2222fdfd22222er");
		parameters.put("you", "fdfdf");

		String sign = StringUtil.getSign(parameters, secret);
		System.out.println("生成签名：" + sign);
		parameters.put("sign", sign);

		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);

		Set<Entry<Object, Object>> set = parameters.entrySet();
		Iterator<Entry<Object, Object>> it = set.iterator();
		while (it.hasNext()) {
			Map.Entry<Object, Object> entry = it.next();
			String k = (String) entry.getKey();
			Object v = entry.getValue();
			mhsrb.param(k, v.toString());
		}
		System.out.println("#################################提交参数");
		System.out.println(mhsrb);
		System.out.println("#################################提交参数");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(JsonUtil.str2json(content)));
		System.out.println("===================返回结果end=======================");

	}

}
