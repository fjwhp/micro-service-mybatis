package aljoin.web.controller.sma;

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

public class PubPublicInfoCategoryControllerTest extends BaseTest{
	
	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;
	
	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	/**
	 *
	 * 资源维护-公共信息分类-新增
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月11日 上午8:48:22
	 */
	@Test
	public void testAdd() throws Exception {
		String uri = "http://localhost:8080/sma/pubPublicInfoCategory/add";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		
		mhsrb.param("isActive", "1");
		mhsrb.param("name", "测试分类");
		mhsrb.param("rank", "1");
		mhsrb.param("bpmnId", "15361012");
		mhsrb.param("bpmnName", "测试流程名称");
		mhsrb.param("useGroupId", "15351012");
		mhsrb.param("useGroupName", "测试使用群体");

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("资源维护-公共信息分类-新增");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("isActive", "数字", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("name", "字符串", "不可为空", "分类名称", "不加密"));
		ipList.add(new InterfaceParam("rank", "数字", "不可为空", "排序", "不加密"));
		ipList.add(new InterfaceParam("bpmnId", "字符串", "不可为空", "使用流程ID", "不加密"));
		ipList.add(new InterfaceParam("bpmnName", "字符串", "不可为空", "使用流程名称", "不加密"));
		ipList.add(new InterfaceParam("useGroupId", "字符串", "不可为空", "使用群体ID （多个用分号分隔）", "不加密"));
		ipList.add(new InterfaceParam("useGroupName", "字符串", "不可为空", "使用群体 （多个用分号分隔）", "不加密"));
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
	 * 资源维护-公共信息分类-分页列表
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月12日 下午3:49:51
	 */
	@Test
	public void testList() throws Exception {
		String uri = "http://localhost:8080/sma/pubPublicInfoCategory/list";
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
		io.setInterfaceName("资源维护-公共信息分类-分页列表");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("pageNum", "数字", "不可为空", "页数", "不加密"));
		ipList.add(new InterfaceParam("pageSize", "数字", "不可为空", "每页数量", "不加密"));
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
	 * 资源维护-公共信息分类-删除
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月12日 下午3:53:20
	 */
	@Test
	public void testDelete() throws Exception {
		String uri = "http://localhost:8080/sma/pubPublicInfoCategory/delete";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		
		mhsrb.param("id", "918381026517811200");

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("资源维护-公共信息分类-删除");
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
	 * 资源维护-公共信息分类-有效分类
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月12日 下午4:03:15
	 */
	@Test
	public void testListActive() throws Exception {
		String uri = "http://localhost:8080/sma/pubPublicInfoCategory/listActive";
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
		io.setInterfaceName("资源维护-公共信息分类-有效分类");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("pageNum", "数字", "不可为空", "页数", "不加密"));
		ipList.add(new InterfaceParam("pageSize", "数字", "不可为空", "每页数量", "不加密"));
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
	 * 资源维护-公共信息分类-修改
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月12日 下午4:03:08
	 */
	@Test
	public void testUpdate() throws Exception {
		String uri = "http://localhost:8080/sma/pubPublicInfoCategory/update";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		
		mhsrb.param("id","918381026517812300");
		mhsrb.param("isActive", "1");
		mhsrb.param("name", "测试分类");
		mhsrb.param("rank", "1");
		mhsrb.param("bpmnId", "15361012");
		mhsrb.param("bpmnName", "测试流程名称");
		mhsrb.param("useGroupId", "15351012");
		mhsrb.param("useGroupName", "测试使用群体");

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("资源维护-公共信息分类-修改");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "数字", "不可为空", "主键ID", "不加密"));
		ipList.add(new InterfaceParam("isActive", "数字", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("name", "字符串", "不可为空", "分类名称", "不加密"));
		ipList.add(new InterfaceParam("rank", "数字", "不可为空", "排序", "不加密"));
		ipList.add(new InterfaceParam("bpmnId", "字符串", "不可为空", "使用流程ID", "不加密"));
		ipList.add(new InterfaceParam("bpmnName", "字符串", "不可为空", "使用流程名称", "不加密"));
		ipList.add(new InterfaceParam("useGroupId", "字符串", "不可为空", "使用群体ID （多个用分号分隔）", "不加密"));
		ipList.add(new InterfaceParam("useGroupName", "字符串", "不可为空", "使用群体 （多个用分号分隔）", "不加密"));
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
