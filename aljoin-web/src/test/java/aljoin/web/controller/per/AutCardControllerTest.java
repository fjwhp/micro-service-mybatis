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
public class AutCardControllerTest extends BaseTest {
	
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
		String uri = "http://localhost:8080/per/actCard/add";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("isActive", "1");
		mhsrb.param("categoryId", "917931577295273984");
		mhsrb.param("positionName", "创始人、CEO");
		mhsrb.param("userName", "马化腾");
		mhsrb.param("gender", "1");
		mhsrb.param("companyName", "腾讯");
		mhsrb.param("companyTel", "1592-5552132");
		mhsrb.param("company_fax", "1592-5552132");
		mhsrb.param("companyAddress", "深圳");
		mhsrb.param("phoneNumber", "5552132");
		mhsrb.param("wechat_number", "5552132");
		mhsrb.param("qq_number", "5552132");
		mhsrb.param("msn_number", "5552132");
		mhsrb.param("user_mail", "5552132");
		mhsrb.param("remark", "腾讯董事局主席");

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("个人中心-通讯录-新增");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("isActive", "数字", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("categoryId", "数字", "可为空", "名片分类ID", "不加密"));
		ipList.add(new InterfaceParam("positionName", "字符串", "可为空", "职务名称", "不加密"));
		ipList.add(new InterfaceParam("userName", "字符串", "可为空", "姓名", "不加密"));
		ipList.add(new InterfaceParam("gender", "数字", "不可为空", "性别（1-男，2-女）", "不加密"));
		ipList.add(new InterfaceParam("companyName", "字符串", "可为空", "单位名称", "不加密"));
		ipList.add(new InterfaceParam("companyTel", "字符串", "可为空", "单位电话", "不加密"));
		ipList.add(new InterfaceParam("company_fax", "字符串", "可为空", "单位传真", "不加密"));
		ipList.add(new InterfaceParam("companyAddress", "字符串", "可为空", "单位地址", "不加密"));
		ipList.add(new InterfaceParam("phoneNumber", "字符串", "可为空", "手机号码", "不加密"));
		ipList.add(new InterfaceParam("wechat_number", "字符串", "可为空", "微信", "不加密"));
		ipList.add(new InterfaceParam("qq_number", "字符串", "可为空", "单位名称", "qq"));
		ipList.add(new InterfaceParam("msn_number", "字符串", "可为空", "单位名称", "msn"));
		ipList.add(new InterfaceParam("user_mail", "字符串", "可为空", "单位名称", "电子邮件"));
		ipList.add(new InterfaceParam("remark", "字符串", "可为空", "单位名称", "备注"));
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
	 * 修改名片
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月11日 上午9:44:49
	 */
	@Test
	public void testUpdate() throws Exception {
		String uri = "http://localhost:8080/per/actCard/update";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("个人中心-名片夹-名片-修改");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "数字", "不可为空", "主键ID", "不加密"));
		ipList.add(new InterfaceParam("isActive", "数字", "不可为空", "是否激活", "不加密"));
		ipList.add(new InterfaceParam("positionName", "字符串", "可为空", "职务名称", "不加密"));
		ipList.add(new InterfaceParam("userName", "字符串", "可为空", "姓名", "不加密"));
		ipList.add(new InterfaceParam("gender", "数字", "不可为空", "性别（1-男，2-女）", "不加密"));
		ipList.add(new InterfaceParam("companyName", "字符串", "可为空", "单位名称", "不加密"));
		ipList.add(new InterfaceParam("companyTel", "字符串", "可为空", "单位电话", "不加密"));
		ipList.add(new InterfaceParam("company_fax", "字符串", "可为空", "单位传真", "不加密"));
		ipList.add(new InterfaceParam("companyAddress", "字符串", "可为空", "单位地址", "不加密"));
		ipList.add(new InterfaceParam("phoneNumber", "字符串", "可为空", "手机号码", "不加密"));
		ipList.add(new InterfaceParam("wechat_number", "字符串", "可为空", "微信", "不加密"));
		ipList.add(new InterfaceParam("qq_number", "字符串", "可为空", "单位名称", "qq"));
		ipList.add(new InterfaceParam("msn_number", "字符串", "可为空", "单位名称", "msn"));
		ipList.add(new InterfaceParam("user_mail", "字符串", "可为空", "单位名称", "电子邮件"));
		ipList.add(new InterfaceParam("remark", "字符串", "可为空", "单位名称", "备注"));
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
	 * 名片详情
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月11日 下午1:37:13
	 */
	@Test
	public void testGetById() throws Exception {
		String uri = "http://localhost:8080/per/actCard/getById";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id","917982260182683648");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("个人中心-名片夹-名片-详情");
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
	 * 删除名片
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月11日 下午1:41:22
	 */
	@Test
	public void testDelete() throws Exception {
		String uri = "http://localhost:8080/per/actCard/delete";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id","917982260182683655");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("个人中心-名片夹-名片-删除");
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
	
	@Test
	public void testList() throws Exception {
		String uri = "http://localhost:8080/per/actCard/list";
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
		io.setInterfaceName("个人中心-名片夹-名片-分页列表");
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
	

}
