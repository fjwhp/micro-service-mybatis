package aljoin.web.controller.act;

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

import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.object.InterfaceObject;
import aljoin.object.InterfaceParam;
import aljoin.util.JsonUtil;
import aljoin.web.BaseTest;

//告诉Junit运行使用Spring 的单元测试支持
@RunWith(SpringRunner.class)
// 带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class ActAljoinCategoryControllerTest extends BaseTest{
	
	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;

	@Resource
	private ActAljoinCategoryService categoryService;

	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	/**
	 * 
	 * 流程分类-分页列表
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月31日 上午9:12:46
	 */
	@Test
	public void testList() throws Exception {
		String uri = "http://localhost:8080/act/actAljoinCategory/list";
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
		io.setInterfaceName("权限管理-流程分类-分页");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
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
	 * 流程分类-获得所有流程分类表信息
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月31日 上午10:19:47
	 */
	@Test
	public void testGetAllCategoryList() throws Exception {
		
		String uri = "http://localhost:8080/act/actAljoinCategory/getAllCategoryList";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-流程分类-查询所有流程分类");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
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
	 * 流程分类-新增流程分类
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月31日 上午10:00:33
	 */
	@Test
	public void testAdd() throws Exception {
		String uri = "http://localhost:8080/act/actAljoinCategory/add";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-流程分类-新增");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("categoryName", "String", "不可为空", "流程分类名称(唯一)", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("categoryRank", "Integer", "不可为空", "(同级)分类排序", "不加密"));
		ipList.add(new InterfaceParam("parentId", "Long", "不可为空", "父级分类ID", "不加密"));
		ipList.add(new InterfaceParam("categoryLevel", "Integer", "不可为空", "分类级别", "不加密"));
		
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
	 * 流程分类-更新
	 *		测试数据要在接口set
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月31日 下午3:15:46
	 */
	@Test
	public void testUpdate() throws Exception {
		String uri = "http://localhost:8080/act/actAljoinCategory/update";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("流程分类-更新流程分类");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		
		ipList.add(new InterfaceParam("id", "Long", "不可为空", "流程分类id", "不加密"));
		ipList.add(new InterfaceParam("categoryName", "String", "不可为空", "流程分类名称(唯一)", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("categoryRank", "Integer", "不可为空", "(同级)分类排序", "不加密"));
		
		io.setParamList(ipList);
		/* 设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.str2json(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.str2json(content));
		System.out.println("===================返回结果end=======================");

		/* 生成接口文档 */
		makeItable(io);

	}
	
	/**
	 * 
	 * 流程分类-删除流程分类
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月31日 下午4:13:26
	 */
	@Test
	public void testDelete() throws Exception {
		String uri = "http://localhost:8080/act/actAljoinCategory/delete";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "903166677180289024");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("流程分类-删除流程分类");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		
		ipList.add(new InterfaceParam("id", "Long", "不可为空", "流程ID", "不加密"));
		
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
	 * 流程分类-根据id查看流程分类详情
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月31日 下午4:23:29
	 */
	@Test
	public void testGetById() throws Exception {
		String uri = "http://localhost:8080/act/actAljoinCategory/getById";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "903166677180289024");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("流程分类-流程分类详情");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		
		ipList.add(new InterfaceParam("id", "Long", "不可为空", "流程ID", "不加密"));
		
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
	 * 流程管理-流程分类-根据父id获取子流程分类
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月4日 上午10:28:01
	 */
	@Test
	public void testGetByParentId() throws Exception {
		String uri = "http://localhost:8080/act/actAljoinCategory/getByParentId";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("流程管理-流程分类-根据父id获取子流程分类");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		
		ipList.add(new InterfaceParam("parentId", "Long", "不可为空", "父id(选中流程分类的id)", "不加密"));
		
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
