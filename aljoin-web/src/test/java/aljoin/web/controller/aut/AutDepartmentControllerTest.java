package aljoin.web.controller.aut;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import aljoin.aut.iservice.AutDepartmentService;
import aljoin.object.InterfaceObject;
import aljoin.object.InterfaceParam;
import aljoin.util.JsonUtil;
import aljoin.web.BaseTest;

//告诉Junit运行使用Spring 的单元测试支持
@RunWith(SpringRunner.class)
// 带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class AutDepartmentControllerTest extends BaseTest {
  
  private final static Logger logger = LoggerFactory.getLogger(AutDepartmentControllerTest.class);

	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;

	@Resource
	private AutDepartmentService autDepartmentService;

	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	/**
	 * 
	 * 权限管理-部门管理-分页列表（单元测试）
	 *
	 * 							@return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月21日 上午8:52:37
	 */
	@Test
	public void testList() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartment/list";
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
		io.setInterfaceName("权限管理-部门管理-分页");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "Long", "不可为空", "主键ID", "不加密"));
		ipList.add(new InterfaceParam("deptName", "String", "不可为空", "部门名称", "不加密"));
		ipList.add(new InterfaceParam("deptCode", "String", "不可为空", "部门编号(唯一)", "不加密"));
		ipList.add(new InterfaceParam("deptLevel", "String", "不可为空", "部门级别", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("deptRank", "Integer", "不可为空", "部门(同级)排序", "不加密"));
		ipList.add(new InterfaceParam("parentId", "Long", "不可为空", "父级部门ID", "不加密"));
		ipList.add(new InterfaceParam("parentCode", "String", "不可为空", "父级部门编号", "不加密"));
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
	 * 权限管理-部门管理-新增部门（单元测试）
	 *
	 * 							@return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月21日 上午8:18:25
	 */
	@Test
	public void testAdd() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartment/add";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("deptName", "董事会3");
		mhsrb.param("isActive", "0");
		mhsrb.param("deptLevel", "1");
		mhsrb.param("deptRank", "4");

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-部门管理-新增部门");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("deptName", "String", "不可为空", "部门名称", "不加密"));
		ipList.add(new InterfaceParam("deptLevel", "String", "不可为空", "部门级别", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("deptRank", "Integer", "不可为空", "部门(同级)排序", "不加密"));
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
	 * 权限管理-部门管理-根据部门id删除部门（单元测试）.
	 * 
	 * @返回:void
	 * 
	 * @author：laijy
	 * 
	 * @date：2017年8月17日 上午8:53:47
	 */
	@Test
	public void testDelete() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartment/delete";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-部门管理-删除部门");
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
	 * 权限管理-部门管理-id更新部门信息（单元测试）.
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月21日 上午8:50:22
	 */
	@Test
	public void testUpdateById() throws Exception {

		String uri = "http://localhost:8080/aut/autDepartment/updateById";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "259159");

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		System.out.println("content:" + content);
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-部门管理-根据ID更新部门");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "Long", "不可为空", "主键ID", "不加密"));
		ipList.add(new InterfaceParam("deptName", "String", "不可为空", "部门名称", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("deptRank", "Integer", "不可为空", "部门(同级)排序", "不加密"));
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
	 * 权限管理-部门管理-根据父级部门id查询部门（单元测试）.
	 * 
	 * @返回:void
	 * 
	 * @author：laijy
	 * 
	 * @date：2017年8月17日 上午8:53:47
	 */
	@Test
	public void testSelectDeptByParentId() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartment/selectDeptByParentId";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("parentId", "1");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-部门管理-根据父级部门id查询部门");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("parentId", "Long", "不可为空", "父级部门id", "不加密"));
		ipList.add(new InterfaceParam("id", "Long", "不可为空", "主键ID", "不加密"));
		ipList.add(new InterfaceParam("deptName", "String", "不可为空", "部门名称", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("deptRank", "Integer", "不可为空", "部门(同级)排序", "不加密"));
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

	@Test
	public void testGetById() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartment/getById";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "259159");
		try {
			MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
			int status = mvcResult.getResponse().getStatus();
			String content = mvcResult.getResponse().getContentAsString();
			System.out.println("content:" + content);
			InterfaceObject io = new InterfaceObject();
			/* 设置版本号 */
			io.setInterfaceVersion("v1");
			/* 设置接口名称 */
			io.setInterfaceName("权限管理-部门管理-根据ID查看部门详情");
			/* 设置接口地址 */
			io.setInterfaceAddress(uri);
			/* 设置接口参数 */
			List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
			ipList.add(new InterfaceParam("id", "Long", "不可为空", "主键ID", "不加密"));
			ipList.add(new InterfaceParam("deptName", "String", "不可为空", "部门名称", "不加密"));
			ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
			ipList.add(new InterfaceParam("deptRank", "Integer", "不可为空", "部门(同级)排序", "不加密"));
			io.setParamList(ipList);
			/* 设置接口结果集以及加密字段 */
			io.setRetObj(JsonUtil.str2json(content));

			System.out.println("status:" + status);
			System.out.println("===================返回结果beg=======================");
			System.out.println(JsonUtil.obj2str(io));
			System.out.println("===================返回结果end=======================");
			/* 生成接口文档 */
			makeItable(io);
		} catch (Exception e) {
		  logger.error("", e);
		}

	}

	/**
	 * 
	 * 查询部门列表（部门名称、部门编号、部门id）
	 *
	 * 							@return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月22日 下午3:29:43
	 */
	@Test
	public void testGetDepartmentList() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartment/getDepartmentList";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-部门管理-查询部门列表");
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
	 * 查询某部门的所有下级部门
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月18日 下午1:27:44
	 */
	@Test
	public void testSelectDeptByPid() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartment/selectDeptByPid";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-部门管理-查询某部门的所有下级部门");
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
	 * 获得本部门及下属部门
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月26日 上午8:36:52
	 */
	@Test
	public void testGetChildDeptList() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartment/getChildDeptList";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "909668793411530752");

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-部门管理-获得部门及下属部门");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "String", "不可为空", "部门id", "不加密"));
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
	 * 获得本部门及下属部门的所有用户
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月25日 下午10:49:26
	 */
	@Test
	public void testGetChildDeptUserList() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartment/getChildDeptUserList";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "909668793411530752");

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-部门管理-获得部门及下属部门的所有用户");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "String", "不可为空", "部门id", "不加密"));
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
	 * 获得本部门及下属部门（分页列表）
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月26日 上午8:36:02
	 */
	@Test
	public void testGetChildDeptPage() throws Exception {
		
		String uri = "http://localhost:8080/aut/autDepartment/getChildDeptPage";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "909668793411530752");
		mhsrb.param("pageNum", "1");
		mhsrb.param("pageSize", "10");

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("获得本部门及下属部门（分页列表）");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "数字", "不可为空", "部门id", "不加密"));
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
}