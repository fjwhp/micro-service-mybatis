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

import aljoin.object.InterfaceObject;
import aljoin.object.InterfaceParam;
import aljoin.util.JsonUtil;
import aljoin.web.BaseTest;

//告诉Junit运行使用Spring 的单元测试支持
@RunWith(SpringRunner.class)
// 带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class AutRoleControllerTest extends BaseTest {

	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;

	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	/**
	 * 
	 * 权限管理-角色管理-根据角色id获取角色下的用户
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月14日 上午8:52:33
	 */
	@Test
	public void testGetUserByRoleId() throws Exception {
		String uri = "http://localhost:8080/aut/autRole/getUserByRoleId";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("[权限管理]-[角色管理]-[查看角色下的用户]");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();

		ipList.add(new InterfaceParam("roleId", "Long", "不可为空", "角色ID", "不加密"));

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
	 * 权限管理-角色管理-(批量)更新角色下的用户排序
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月14日 上午8:53:23
	 */
	@Test
	public void testUpdateRoleUserRank() throws Exception {
		String uri = "http://localhost:8080/aut/autRole/updateRoleUserRank";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("权限管理-角色管理-(批量)更新角色下的用户排序");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();

		ipList.add(new InterfaceParam("id", "Long", "不可为空", "主键ID", "不加密"));
		ipList.add(new InterfaceParam("roleUserRank", "String", "不可为空", "角色用户排序", "不加密"));

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
