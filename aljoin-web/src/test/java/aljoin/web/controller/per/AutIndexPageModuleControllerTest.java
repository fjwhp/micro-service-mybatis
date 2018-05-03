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
public class AutIndexPageModuleControllerTest extends BaseTest {
	
	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;
	
	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	/**
	 * 
	 * 新增名片分类
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月11日 上午9:44:49
	 */
	@Test
	public void testAdd() throws Exception {
		String uri = "http://localhost:8080/per/autIndexPageModule/add";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("moduleName", "测试模块");
		mhsrb.param("moduleCode", "125864182155");
		mhsrb.param("moduleRank", "1");
		mhsrb.param("isHide", "1");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("个人中心-名片夹-新增名片分类");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("moduleName", "字符串", "不可为空", "模块名称", "不加密"));
		ipList.add(new InterfaceParam("moduleCode", "字符串", "不可为空", "模块编码", "不加密"));
		ipList.add(new InterfaceParam("moduleRank", "数字", "不可为空", "模块排序", "不加密"));
		ipList.add(new InterfaceParam("isHide", "数字", "不可为空", "是否隐藏", "不加密"));
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
	 * 个人中心-个人页面定制-分页列表
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月12日 下午7:26:06
	 */
	@Test
	public void testList() throws Exception {
		String uri = "http://localhost:8080/per/autIndexPageModule/list";
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
		io.setInterfaceName("个人中心-个人页面定制-分页列表");
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
	 * 个人中心-个人页面定制-修改
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月12日 下午7:31:30
	 */
	@Test
	public void testDelete() throws Exception {
		String uri = "http://localhost:8080/per/autIndexPageModule/delete";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "918438031718658048");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("个人中心-个人页面定制-删除");
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
	
	@Test
	public void testUpdate() throws Exception {
		String uri = "http://localhost:8080/per/autIndexPageModule/update";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "918438031718658048");
		mhsrb.param("moduleName", "测试修改模块");
		mhsrb.param("moduleRank", "12");
		mhsrb.param("isHide", "0");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("个人中心-个人页面定制-修改");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "字符串", "不可为空", "主键ID", "不加密"));
		ipList.add(new InterfaceParam("moduleName", "字符串", "不可为空", "模块名称", "不加密"));
		ipList.add(new InterfaceParam("moduleRank", "数字", "不可为空", "模块排序", "不加密"));
		ipList.add(new InterfaceParam("isHide", "数字", "不可为空", "是否隐藏", "不加密"));
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