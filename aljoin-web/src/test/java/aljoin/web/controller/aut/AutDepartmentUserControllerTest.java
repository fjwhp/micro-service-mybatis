package aljoin.web.controller.aut;

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

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.object.InterfaceObject;
import aljoin.object.InterfaceParam;
import aljoin.util.JsonUtil;
import aljoin.web.BaseTest;

//告诉Junit运行使用Spring 的单元测试支持
@RunWith(SpringRunner.class)
// 带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class AutDepartmentUserControllerTest extends BaseTest {
	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;

	@Resource
	private AutDepartmentService autDepartmentService;
	@Resource
	private AutDepartmentUserService autDepartmentUserService;

	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	/**
	 * 
	 * 权限管理-部门管理-新增部门用户（单元测试）
	 *
	 * 							@return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月21日 上午8:52:37
	 */
	@Test
	public void testaddDepartmentUser() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartmentUser/addDepartmentUser";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-部门管理-新增部门用户");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();

		ipList.add(new InterfaceParam("deptId", "Long", "不可为空", "部门ID", "不加密"));
		ipList.add(new InterfaceParam("deptCode", "String", "不可为空", "部门编号", "不加密"));
		ipList.add(new InterfaceParam("userId", "Long", "不可为空", "用户ID", "不加密"));
		ipList.add(new InterfaceParam("userName", "String", "不可为空", "用户名", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));

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
	 * 权限管理-部门管理-给用户分配部门
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月22日 下午4:11:11
	 */
	@Test
	public void testAddUserDepartment() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartmentUser/addUserDepartment";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-部门管理-给用户分配部门");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("deptId", "Long", "不可为空", "部门ID", "不加密"));
		ipList.add(new InterfaceParam("deptCode", "String", "不可为空", "部门编号", "不加密"));
		ipList.add(new InterfaceParam("userId", "Long", "不可为空", "用户ID", "不加密"));
		ipList.add(new InterfaceParam("userName", "String", "不可为空", "用户名", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		io.setParamList(ipList);
		/* 设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.obj2str(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(io));
		System.out.println("===================返回结果end=======================");

		/* 生成接口文档 */
		makeItable(io);
	}
	
	/**
	 * 
	 * 权限管理-部门管理-给用户分配部门(在新增部门之前，先删除掉数据库里该用户下的所有部门)
	 *      此单元测试需要在接口传userId的值
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月30日 下午3:27:10
	 */
	@Test
	public void testAddUserDepartment2() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartmentUser/addUserDepartment";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-部门管理-给用户分配部门");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("deptId", "Long", "不可为空", "部门ID", "不加密"));
		ipList.add(new InterfaceParam("deptCode", "String", "不可为空", "部门编号", "不加密"));
		ipList.add(new InterfaceParam("userId", "Long", "不可为空", "用户ID", "不加密"));
		ipList.add(new InterfaceParam("userName", "String", "不可为空", "用户名", "不加密"));
		ipList.add(new InterfaceParam("isActive", "Integer", "不可为空", "是否激活", "不加密"));
		io.setParamList(ipList);
		/* 设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.obj2str(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(io));
		System.out.println("===================返回结果end=======================");

		/* 生成接口文档 */
		makeItable(io);
	}

	/**
	 * 
	 * 生成部门编号(单元测试)
	 *
	 * 					@return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月25日 下午2:24:02
	 */
	@SuppressWarnings("unused")
	@Test
	public void getNextDemo() throws Exception {
		AutDepartment autDepartment = new AutDepartment();
		autDepartment = null;
		// autDepartment.setDeptCode("000999");

		String nextCode = null;
		if (autDepartment != null) {
			nextCode = autDepartment.getDeptCode();
			String codeStr = nextCode.substring(nextCode.length() - 3);
			Integer codeNum = Integer.parseInt(codeStr);
			codeNum++;
			if (codeNum > 999) {
				throw new Exception("部门数已经超过999");
			} else {
				if (codeNum > 99) {
					codeStr = String.valueOf(codeNum);
				} else if (codeNum > 9) {
					codeStr = codeNum + "0";
				} else {
					codeStr = "00" + codeNum;
				}
				nextCode = codeStr;
			}
			System.out.println("nextCode================" + nextCode);
		} else {
			// 没有查到1级部门，直接设置一级部门的deptCode为001
			nextCode = "001";
			System.out.println("nextCode+++++++++++++++" + nextCode);
		}
	}

	/**
	 * 
	 * 查询所有AutDepartmentUser表里的数据（用户id）
	 *
	 * 										@return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月25日 下午2:25:36
	 */
	@Test
	public void testGetDepartmentUserList() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartmentUser/getDepartmentUserList";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("查询所有DepartmentUser里的信息（用户i）");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		io.setParamList(ipList);
		/* 设置接口结果集以及加密字段 */
		io.setRetObj(JsonUtil.obj2str(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(io));
		System.out.println("===================返回结果end=======================");

		/* 生成接口文档 */
		makeItable(io);
	}


	/**
	 * 
	 * @throws Exception 
	 * 根据用户id查询AutDepartmentUser表中是否有该部门(注：此单元测试需要在接口处传入userId值)
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月28日 上午11:23:13
	 */
	@Test
	public void testGetDeptByUserId() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartmentUser/getDeptByUserId";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("根据部门用户id查询部门信息");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();

		ipList.add(new InterfaceParam("userId", "Long", "不可为空", "用户ID", "不加密"));

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
	 * 根据deptId查询用户信息(用户id),此接口单元测试时，要在接口处给deptId传值
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月29日 上午10:11:23
	 */
	@Test
	public void testGetUserByDeptId() throws Exception{
		String uri = "http://localhost:8080/aut/autDepartmentUser/getUserByDeptId";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("根据deptId查询用户信息");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();

		ipList.add(new InterfaceParam("deptId", "Long", "不可为空", "部门ID", "不加密"));

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
	 * 权限管理-部门管理-修改某部门用户的排序
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月13日 上午10:28:28
	 */
	@Test
	public void testUpdate() throws Exception {
		String uri = "http://localhost:8080/aut/autDepartmentUser/update";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-部门管理-新增部门用户");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();

		ipList.add(new InterfaceParam("id", "Long", "不可为空", "部门ID", "不加密"));
		ipList.add(new InterfaceParam("departmentUserRank", "Integer", "不可为空", "部门用户的排序", "不加密"));

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
	 * 删除部门用户(须同时删除该用户-岗位表里的记录)
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月25日 上午9:13:15
	 */
	@Test
	public void testDeleteDeptUser() throws Exception{
		String uri = "http://localhost:8080/aut/autDepartmentUser/deleteDeptUser";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-部门岗位-删除部门用户");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();

		ipList.add(new InterfaceParam("deptId", "数字", "不可为空", "部门ID", "不加密"));
		ipList.add(new InterfaceParam("userId", "数字", "不可为空", "用户ID", "不加密"));

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
	
}
