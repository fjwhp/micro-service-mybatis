package aljoin.web.controller.mai;

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
// 带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MaiScrapBoxControllerTest extends BaseTest{
	
	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;
	
	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	/**
	 * 
	 * 内部邮件-废件箱-新增
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月20日 上午11:17:56
	 */
	@Test
	public void testAdd() throws Exception {
		String uri = "http://localhost:8080/mai/maiScrapBox/add";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("createUserId", "903158226865868800");
		mhsrb.param("receiveUserIds", "866131328341528576");
		mhsrb.param("receiveUserNames", "sadmin");
		mhsrb.param("receiveFullNames", "超级管理员");
		mhsrb.param("sendUserId", "903158226865868800");
		mhsrb.param("sendUserName", "yuanxc");
		mhsrb.param("sendFullName", "袁小春");
		mhsrb.param("subjectText", "2袁的封测试草稿");
		mhsrb.param("mailSize", "100");
		mhsrb.param("orgnlType", "1");
		mhsrb.param("orgnlId", "886127277537888888");
		
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("内部邮件-废件箱-新增");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("receiveUserIds", "字符串", "不可为空", "收件人用户ID(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("receiveUserNames", "字符串", "不可为空", "收件人账号(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("receiveFullNames", "字符串", "不可为空", "收件人名称(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("sendUserId", "数字", "不可为空", "发件人用户ID", "不加密"));
		ipList.add(new InterfaceParam("sendUserName", "字符串", "不可为空", "发件人账号", "不加密"));
		ipList.add(new InterfaceParam("sendFullName", "字符串", "不可为空", "发件人名称", "不加密"));
		ipList.add(new InterfaceParam("mailSize", "数字", "不可为空", "邮件大小(正文+附件的大小)", "不加密"));
		ipList.add(new InterfaceParam("subjectText", "字符串", "可为空", "主题", "不加密"));
		ipList.add(new InterfaceParam("orgnlType", "数字", "不可为空", "源类型：1-收件箱，2-发件箱", "不加密"));
		ipList.add(new InterfaceParam("orgnlId", "数字", "不可为空", "源ID，针对orgnl_type,对应相应的表：mai_receive_box,mai_send_box", "不加密"));
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
	 * 内部邮件-废件箱-列表(分页)
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月20日 上午11:17:56
	 */
	@Test
	public void testList() throws Exception {
		String uri = "http://localhost:8080/mai/maiScrapBox/list";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("pageNum","1");
		mhsrb.param("pageSize","10");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("内部邮件-废件箱-列表(分页)");
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
	 * 内部邮件-废件箱-删除
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月20日 下午2:10:42
	 */
	@Test
	public void testDelete() throws Exception {
		String uri = "http://localhost:8080/mai/maiScrapBox/delete";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "910389948292366339");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("内部邮件-废件箱-删除");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "数字", "不可为空", "主键ID", "不加密"));
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
	 * 内部邮件-废件箱-编辑
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月20日 下午1:57:29
	 */
	@Test
	public void testUpdate() throws Exception {
		String uri = "http://localhost:8080/mai/maiScrapBox/update";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "910371090961600512");
		mhsrb.param("subjectText", "编辑主题测试");
		mhsrb.param("mailContent", "编辑草稿正文测试");

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("内部邮件-废件箱-编辑");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "数字", "不可为空", "主键ID", "不加密"));
		ipList.add(new InterfaceParam("receiveUserIds", "字符串", "可为空", "收件人用户ID(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("receiveUserNames", "字符串", "可为空", "收件人账号(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("receiveFullNames", "字符串", "可为空", "收件人名称(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("sendUserId", "数字", "不可为空", "发件人用户ID", "不加密"));
		ipList.add(new InterfaceParam("sendUserName", "字符串", "不可为空", "发件人账号", "不加密"));
		ipList.add(new InterfaceParam("sendFullName", "字符串", "不可为空", "发件人名称", "不加密"));
		ipList.add(new InterfaceParam("subjectText", "字符串", "可为空", "主题", "不加密"));
		ipList.add(new InterfaceParam("mailSize", "数字", "不可为空", "邮件大小(正文+附件的大小)", "不加密"));
		ipList.add(new InterfaceParam("orgnlType", "数字", "不可为空", "源类型：1-收件箱，2-发件箱", "不加密"));
		ipList.add(new InterfaceParam("orgnlId", "数字", "不可为空", "源ID，针对orgnl_type,对应相应的表：mai_receive_box,mai_send_box", "不加密"));
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
	public void testRecover() throws Exception {
		String uri = "http://localhost:8080/mai/maiScrapBox/recover";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("内部邮件-废件箱-恢复");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("orgnlType", "数字", "不可为空", "源类型：1-收件箱，2-发件箱", "不加密"));
		ipList.add(new InterfaceParam("orgnlId", "数字", "不可为空", "源ID，针对orgnl_type,对应相应的表：mai_receive_box,mai_send_box", "不加密"));
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
