package aljoin.web.controller.res;

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
import aljoin.res.dao.entity.ResResourceType;
import aljoin.res.iservice.ResResourceTypeService;
import aljoin.util.JsonUtil;
import aljoin.web.BaseTest;

//告诉Junit运行使用Spring 的单元测试支持
@RunWith(SpringRunner.class)
// 带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class ResResourceTypeControllerTest extends BaseTest {
	
	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;

	@Resource
	ResResourceTypeService resResourceTypeService;
	
	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	/**
	 * 
	 * 资源管理-资源分类-新增
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月5日 下午4:07:19
	 */
	@Test
	public void testAdd() throws Exception {
		String uri = "http://localhost:8080/res/resResourceType/add";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("资源管理-资源分类-新增");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("typeName", "String", "不可为空", "资源分类名称(唯一)", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("typeRank", "Integer", "不可为空", "(同级)分类排序", "不加密"));
		ipList.add(new InterfaceParam("parentId", "Long", "不可为空", "父级分类ID", "不加密"));
		ipList.add(new InterfaceParam("typeLevel", "Integer", "不可为空", "分类级别", "不加密"));
		
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
	 * 资源管理-资源分类-更新
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月5日 下午4:32:23
	 */
	@Test
	public void testUpdate() throws Exception {
		String uri = "http://localhost:8080/res/resResourceType/update";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("资源管理-资源分类-更新");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		
		ipList.add(new InterfaceParam("id", "Long", "不可为空", "资源分类id", "不加密"));
		ipList.add(new InterfaceParam("typeName", "String", "不可为空", "资源分类名称(唯一)", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("typeRank", "Integer", "不可为空", "(同级)分类排序", "不加密"));
		
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
	 * 资源管理-资源分类-详情(根据id)
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月5日 下午4:45:22
	 */
	@Test
	public void testGetById() throws Exception {
		String uri = "http://localhost:8080/res/resResourceType/getById";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "904537215936281111");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("资源管理-资源分类-详情");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		
		ipList.add(new InterfaceParam("id", "Long", "不可为空", "分类id", "不加密"));
		
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
	 * 资源管理-资源分类-删除
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月5日 下午4:58:17
	 */
	@Test
	public void testDelete() throws Exception {
		String uri = "http://localhost:8080/res/resResourceType/delete";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "904537215936281111");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("资源管理-资源分类-删除");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		
		ipList.add(new InterfaceParam("id", "Long", "不可为空", "资源分类id", "不加密"));
		
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
	 * 根据父id获取子资源分类List
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月6日 上午11:43:02
	 */
	@Test
	public void testGetResourceListByPid(){
		ResResourceType obj = new ResResourceType();
		obj.setParentId(904537215936281111L);
		List<ResResourceType> getResourceListByPid = resResourceTypeService.getResourceListByPid(obj);
		for(int i=0;i<getResourceListByPid.size();i++){
			System.out.println("===========Name"+getResourceListByPid.get(i).getTypeName());
		}
	}

}
