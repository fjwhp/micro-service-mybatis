package aljoin.web.controller.per;


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

import aljoin.object.InterfaceObject;
import aljoin.object.InterfaceParam;
import aljoin.util.JsonUtil;
import aljoin.web.BaseTest;

//告诉Junit运行使用Spring 的单元测试支持
@RunWith(SpringRunner.class)
//带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AutUserPubControllerTest extends BaseTest{

	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;
	
	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	/**
	 * 
	 * 个人中心-公共信息-新增
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月13日 下午1:32:16
	 */
	@Test
	public void testAdd() throws Exception {
		String uri = "http://localhost:8080/per/autUserPub/add";
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
	 * 个人中心-通讯录
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月11日 下午3:36:57
	 */
	@Test
	public void testGetAutUserPubVOList() throws Exception {
		String uri = "http://localhost:8080/per/autUserPub/getAutUserPubVOList";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "903160671840415744");
		
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("个人中心-通讯录");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "数字", "不可为空", "部门id", "不加密"));
		io.setParamList(ipList);
		/* 设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.str2jsonArr(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(io));
		System.out.println("===================返回结果end=======================");

		/* 生成接口文档 */
		makeItable(io);

	}
	
	/**
	 * 
	 * 个人中心-通讯录-查询
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月11日 下午3:36:57
	 */
	@Test
	public void testSearchUserPubInfo() throws Exception {
		String uri = "http://localhost:8080/per/autUserPub/searchUserPubInfo";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("个人中心-通讯录-查询");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "数字", "可为空", "部门id", "不加密"));
		ipList.add(new InterfaceParam("autUser.fullName", "数字", "可为空", "部门id", "不加密"));
		ipList.add(new InterfaceParam("autPosition.positionName", "数字", "可为空", "部门id", "不加密"));
		io.setParamList(ipList);
		/* 设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.str2jsonArr(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(io));
		System.out.println("===================返回结果end=======================");

		/* 生成接口文档 */
		makeItable(io);

	}

}
