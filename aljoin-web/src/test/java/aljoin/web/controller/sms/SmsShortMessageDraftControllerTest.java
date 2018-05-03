package aljoin.web.controller.sms;

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
public class SmsShortMessageDraftControllerTest extends BaseTest {
	
	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;
	
	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	/**
	 * 
	 * 手机短信-新建短信-存为草稿
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月11日 上午9:44:49
	 */
	@Test
	public void testAdd() throws Exception {
		String uri = "http://localhost:8080/sms/smsShortMessageDraft/add";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("theme", "测试存为草稿");
		mhsrb.param("content", "虚拟运营商是指拥有某种或者某几种能力与电信运营商在某项业务或者某几项业务上形成合作关系的合作方");
		mhsrb.param("receiverId", "11;22;33;44");
		mhsrb.param("receiverName", "张无忌;张家界;张天师;张一山");
		mhsrb.param("sendNumber", "4");
		mhsrb.param("sendStatus", "2");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("手机短信-新建短信-存为草稿");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		
		ipList.add(new InterfaceParam("theme", "字符串", "不可为空", "主题", "不加密"));
		ipList.add(new InterfaceParam("content", "字符串", "不可为空", "是否激活", "内容"));
		ipList.add(new InterfaceParam("receiverId", "字符串", "不可为空", "收信人ID(多个用分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("receiverName", "字符串", "不可为空", "收信人(多个用分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("sendNumber", "数字", "不可为空", "发送人数", "不加密"));
		ipList.add(new InterfaceParam("sendStatus", "数字", "不可为空", "发送状态(0:发送失败 1:发送成功)", "不加密"));
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
	
	@Test
	public void testUpdate() throws Exception {
		String uri = "http://localhost:8080/sms/smsShortMessageDraft/update";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id","918074704236728320");
		mhsrb.param("theme", "草稿修改====");
		mhsrb.param("content", "草稿修改 虚拟运营商是指与电信运营商在某项业务或者某几项业务上形成合作关系的合作方");
		mhsrb.param("receiverId", "11;22;33;44");
		mhsrb.param("receiverName", "张无忌");
		mhsrb.param("sendNumber", "1");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("手机短信-手机草稿-修改");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		
		ipList.add(new InterfaceParam("id", "数字", "不可为空", "主键ID", "不加密"));
		ipList.add(new InterfaceParam("theme", "字符串", "不可为空", "主题", "不加密"));
		ipList.add(new InterfaceParam("content", "字符串", "不可为空", "是否激活", "内容"));
		ipList.add(new InterfaceParam("receiverId", "字符串", "不可为空", "收信人ID(多个用分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("receiverName", "字符串", "不可为空", "收信人(多个用分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("sendNumber", "数字", "不可为空", "发送人数", "不加密"));
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
	 * 手机短信-草稿箱-分页列表
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月11日 下午4:39:44
	 */
	@Test
	public void testList() throws Exception {
		String uri = "http://localhost:8080/sms/smsShortMessageDraft/list";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("pageNum", "1");
		mhsrb.param("pageSize", "10");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("手机短信-手机草稿-分页列表");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("pageNum", "数字", "不可为空", "页数", "不加密"));
		ipList.add(new InterfaceParam("pageSize", "数字", "不可为空", "每页显示数量", "不加密"));
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
	 * 手机短信-手机草稿-详情
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月11日 下午5:04:00
	 */
	@Test
	public void testGetById() throws Exception {
		String uri = "http://localhost:8080/sms/smsShortMessageDraft/getById";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "918074980993626112");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("手机短信-手机草稿-详情");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		
		ipList.add(new InterfaceParam("id", "字符串", "不可为空", "主键ID", "不加密"));
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
	 * 手机短信-已发短信-删除
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月11日 下午5:04:00
	 */
	@Test
	public void testDelete() throws Exception {
		String uri = "http://localhost:8080/sms/smsShortMessageDraft/delete";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "918074704236728320");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("手机短信-手机草稿-删除");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		
		ipList.add(new InterfaceParam("id", "字符串", "不可为空", "主键ID", "不加密"));
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
