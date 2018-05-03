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

import aljoin.object.InterfaceObject;
import aljoin.object.InterfaceParam;
import aljoin.util.JsonUtil;
import aljoin.web.BaseTest;

//告诉Junit运行使用Spring 的单元测试支持
@RunWith(SpringRunner.class)
// 带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class ActAljoinFormControllerTest extends BaseTest{
	
	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;

	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	/**
	 * 
	 * 流程管理-表单管理-分页列表（单元测试）
	 *
	 * @return：void
	 *
	 * @author：pengsp
	 *
	 * @date：2017年8月31日
	 */
	@Test
	public void testList() throws Exception {
		String uri = "http://localhost:8080/act/actAljoinForm/list";
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
		io.setInterfaceName("流程管理-表单管理-分页");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("formName", "String", "不可为空", "表单名称(唯一)", "不加密"));
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
	 * 流程管理-表单管理-新增
	 *
	 * @return：void
	 *
	 * @author：pengsp
	 *
	 * @date：2017年8月31日
	 */
	@Test
	public void testAdd() throws Exception {
		String uri = "http://localhost:8080/act/actAljoinForm/add";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		 /*设置接口名称 */
		io.setInterfaceName("流程管理-表单管理-新增");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("formName", "String", "不可为空", "表单名称(唯一)", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("htmlCode", "String", "不可为空", "html编码(base64编码后)", "不加密"));
		ipList.add(new InterfaceParam("categoryId", "Long", "不可为空", "表单分类ID", "不加密"));
		
		ipList.add(new InterfaceParam("widgetList", "List", "不可为空", "控件LIST", "不加密"));
		ipList.add(new InterfaceParam("widgetType", "String", "不可为空", "控件类型", "不加密"));
		ipList.add(new InterfaceParam("widgetId", "String", "不可为空", "控件ID", "不加密"));
		ipList.add(new InterfaceParam("widgetName", "String", "不可为空", "控件NAME", "不加密"));
		ipList.add(new InterfaceParam("formId", "Long", "不可为空", "归属表单ID", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		
		ipList.add(new InterfaceParam("attributeList", "List", "不可为空", "控件属性LIST", "不加密"));
		ipList.add(new InterfaceParam("widgetId", "Long", "不可为空", "控件ID", "不加密"));
		ipList.add(new InterfaceParam("attrName", "String", "不可为空", "属性名称", "不加密"));
		ipList.add(new InterfaceParam("attrValue", "String", "不可为空", "属性值", "不加密"));
		ipList.add(new InterfaceParam("attrDesc", "String", "不可为空", "属性说明", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		io.setParamList(ipList);
		 /*设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.obj2str(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(io));
		System.out.println("===================返回结果end=======================");

		/*生成接口文档 */
		makeItable(io);

	}

	
	/**
	 * 
	 * 流程管理-表单管理-根据主键id删除表单（单元测试）.
	 * 
	 * @返回:void
	 * 
	 * @author：laijy
	 * 
	 * @date：2017年8月31日
	 */
	@Test
	public void testDelete() throws Exception {
		String uri = "http://localhost:8080/act/actAljoinForm/delete";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "903528962204250112");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("流程管理-表单管理-删除表单");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "Long", "不可为空", "主键ID", "不加密"));
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
	 * 流程管理-表单管理-根据主键id更新表单（单元测试）.
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月31日
	 */
	@Test
	public void testUpdate() throws Exception {

		String uri = "http://localhost:8080/act/actAljoinForm/update";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "903421875486879744");
		mhsrb.param("formName", "文本框");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		System.out.println("content:" + content);
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("流程管理-表单管理-根据ID更新表单");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "Long", "不可为空", "主键ID", "不加密"));
		ipList.add(new InterfaceParam("formName", "String", "不可为空", "表单名称(唯一)", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("htmlCode", "String", "不可为空", "html编码(base64编码后)", "不加密"));
		ipList.add(new InterfaceParam("categoryId", "Long", "不可为空", "表单分类ID", "不加密"));
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
	 * 流程管理-表单管理-预览表单（单元测试）.
	 *
	 * @return：void
	 *
	 * @author：Pengsp
	 *
	 * @date：2017年8月31日
	 */
	@Test
	public void testGetById() throws Exception {

		String uri = "http://localhost:8080/act/actAljoinForm/getById";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "903421875486879744");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		System.out.println("content:" + content);
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("流程管理-表单管理-预览表单");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "Long", "不可为空", "主键ID", "不加密"));
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
	 * 流程管理-表单管理-根据表单分类获取所有表单（单元测试）
	 *
	 * @return：void
	 *
	 * @author：pengsp
	 *
	 * @date：2017年9月5日
	 */
	@Test
	public void testAllList() throws Exception{
		String uri = "http://localhost:8080/act/actAljoinForm/getAllForm";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("categoryId", "904996258317332480");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("流程管理-表单管理-根据表单分类获取所有表单");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();

		ipList.add(new InterfaceParam("categoryId", "Long", "不可为空", "表单名分类ID", "不加密"));

		io.setParamList(ipList);
		/* 设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.str2jsonArr(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.str2jsonArr(content));
		System.out.println("===================返回结果end=======================");

		/* 生成接口文档 */
		makeItable(io);
	}
	
	/**
	 * 
	 * 流程管理-表单管理-根据表单ID获取所有信息（单元测试）
	 *
	 * @return：void
	 *
	 * @author：pengsp
	 *
	 * @date：2017年9月13日
	 */
	@Test
	public void testAll() throws Exception{
		String uri = "http://localhost:8080/act/actAljoinForm/getAllById";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "909974282681344000");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("流程管理-表单管理-根据表单id预览所有信息");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();

		ipList.add(new InterfaceParam("id", "Long", "不可为空", "表单名分类ID", "不加密"));

		io.setParamList(ipList);
		/* 设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.str2jsonArr(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.str2jsonArr(content));
		System.out.println("===================返回结果end=======================");

		/* 生成接口文档 */
		makeItable(io);
	}
	
	/**
	 * 
	 * 流程管理-表单管理-系统数据源列表（单元测试）
	 *
	 * @return：void
	 *
	 * @author：pengsp
	 *
	 * @date：2017年9月5日
	 */
	@Test
	public void testType() throws Exception{
		String uri = "http://localhost:8080/act/actAljoinForm/getAllType";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("type", "contact_select");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("流程管理-表单管理-系统数据源");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();

		ipList.add(new InterfaceParam("type", "String", "不可为空", "控件类型", "不加密"));

		io.setParamList(ipList);
		/* 设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.str2jsonArr(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.str2jsonArr(content));
		System.out.println("===================返回结果end=======================");

		/* 生成接口文档 */
		makeItable(io);
	}
	
	/**
	 * 
	 * 流程管理-表单管理-系统数据源拉取数据（单元测试）
	 *
	 * @return：void
	 *
	 * @author：pengsp
	 *
	 * @date：2017年9月5日
	 */
	@Test
	public void testSource() throws Exception{
		String uri = "http://localhost:8080/act/actAljoinForm/getAllSource";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("type", "ContactCity");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("流程管理-表单管理-系统数据源");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();

		ipList.add(new InterfaceParam("type", "String", "不可为空", "控件类型", "不加密"));

		io.setParamList(ipList);
		/* 设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.str2jsonArr(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.str2jsonArr(content));
		System.out.println("===================返回结果end=======================");

		/* 生成接口文档 */
		makeItable(io);
	}
}
