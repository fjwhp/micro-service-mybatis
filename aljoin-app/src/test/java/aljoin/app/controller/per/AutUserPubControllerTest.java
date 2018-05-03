package aljoin.app.controller.per;

import java.util.ArrayList;
import java.util.List;

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

import aljoin.app.BaseTest;
import aljoin.object.InterfaceObject;
import aljoin.object.InterfaceParam;
import aljoin.util.JsonUtil;

/**
 * 告诉Junit运行使用Spring 的单元测试支持
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AutUserPubControllerTest extends BaseTest {

	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;

	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	/**
	 * 
	 * 个人中心-个人信息-新增
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月13日 下午1:32:16
	 */
	@Test
	public void testAdd() throws Exception {
		String uri = "http://localhost:8080/app/per/autUserPub/add";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("个人中心-个人信息-新增");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("autUser.id", "数字", "不可为空", "用户ID", "不加密"));
		ipList.add(new InterfaceParam("autUser.userEmail", "字符串", "可为空", "用户邮箱", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.phoneNumber", "字符串", "可为空", "手机号码", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.telNumber", "字符串", "可为空", "电话号码", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.faxNumber", "字符串", "可为空", "传真号码", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.lawNumber", "字符串", "可为空", "执法证号", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.chestCardNumber", "字符串", "可为空", "胸牌号", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.userIcon", "字符串", "可为空", "头像", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.maxMailSize", "数字", "不可为空", "最大邮箱容量(KB)", "不加密"));
		io.setParamList(ipList);
		/* 设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.str2json(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(io));
		System.out.println("===================返回结果end=======================");

		/* 生成接口文档 */
		makeItable(io);

	}

	/**
	 * 
	 * 个人中心-个人信息-修改
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月18日 上午11:26:04
	 */
	@Test
	public void testUpdate() throws Exception {
		String uri = "http://localhost:8080/app/per/autUserPub/update";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("个人中心-个人信息-修改");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("autUser.id", "数字", "不可为空", "用户ID", "不加密"));
		ipList.add(new InterfaceParam("autUser.userEmail", "字符串", "可为空", "用户邮箱", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.id", "数字", "可为空", "用户信息表ID", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.phoneNumber", "字符串", "可为空", "手机号码", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.telNumber", "字符串", "可为空", "电话号码", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.faxNumber", "字符串", "可为空", "传真号码", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.lawNumber", "字符串", "可为空", "执法证号", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.chestCardNumber", "字符串", "可为空", "胸牌号", "不加密"));
		ipList.add(new InterfaceParam("autUserPub.userIcon", "字符串", "可为空", "头像", "不加密"));
		io.setParamList(ipList);
		/* 设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.str2json(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(io));
		System.out.println("===================返回结果end=======================");

		/* 生成接口文档 */
		makeItable(io);
	}

	/**
	 * 
	 * 个人中心-个人信息-修改密码
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月18日 上午11:26:04
	 */
	@Test
	public void testUpdatePwd() throws Exception {
		String uri = "http://localhost:8080/app/per/autUserPub/updatePwd";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("个人中心-个人信息-修改密码");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("autUser.userPwd", "字符串", "不可为空", "旧密码", "不加密"));
		ipList.add(new InterfaceParam("AutUserPubVO.newUserPwd", "字符串", "不可为空", "新密码", "不加密"));
		ipList.add(new InterfaceParam("AutUserPubVO.newPwdConfirm", "数字", "不可为空", "确认新密码", "不加密"));
		io.setParamList(ipList);
		/* 设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.str2json(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(io));
		System.out.println("===================返回结果end=======================");

		/* 生成接口文档 */
		makeItable(io);
	}

}
