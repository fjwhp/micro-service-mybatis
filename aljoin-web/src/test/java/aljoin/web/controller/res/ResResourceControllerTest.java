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
import aljoin.res.iservice.ResResourceService;
import aljoin.util.JsonUtil;
import aljoin.web.BaseTest;

//告诉Junit运行使用Spring 的单元测试支持
@RunWith(SpringRunner.class)
// 带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class ResResourceControllerTest extends BaseTest {

	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;

	@Resource
	ResResourceService resResourceService;

	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	/**
	 *
	 * 资源管理-资源维护-下载
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月11日 上午8:48:22
	 */
	@Test
	public void testDownload() throws Exception {
		String uri = "http://localhost:8080/res/resResource/download";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("资源管理-资源维护-下载");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("path", "String", "不可为空", "文件路径", "不加密"));

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
	 * 资源管理-资源维护-上传
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月12日 下午1:14:53
	 */
	@Test
	public void testUpload() throws Exception {
		
		String uri = "http://localhost:8080/res/resResource/upload";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("资源管理-资源维护-上传");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("allowType", "字符串", "不可为空", "允许上传文件类型", "不加密"));
		ipList.add(new InterfaceParam("limitSize", "数字", "不可为空", "最大上传文件大小", "不加密"));
		
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
	 * 文件上传-获得支持上传的文件类型、大小
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月12日 下午2:47:58
	 */
	@Test
	public void testGetAllowType() throws Exception {
		
		String uri = "http://localhost:8080//util/utilController/getAllowType";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("文件上传-获得支持上传的文件类型、大小");
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
}
