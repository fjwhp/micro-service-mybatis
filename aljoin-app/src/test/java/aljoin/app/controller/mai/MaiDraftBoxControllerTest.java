package aljoin.app.controller.mai;

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

import aljoin.app.BaseTest;
import aljoin.object.InterfaceObject;
import aljoin.object.InterfaceParam;
import aljoin.util.JsonUtil;

/**
 * 告诉Junit运行使用Spring 的单元测试支持
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MaiDraftBoxControllerTest extends BaseTest {

	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;

	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	/**
	 * 
	 * 内部邮件-草稿箱-新增
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月20日 上午11:17:56
	 */
	@Test
	public void testAdd() throws Exception {
		String uri = "http://localhost:8080/app/mai/maiDraftBox/add";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);

		mhsrb.param("create_user_id", "866131328341528576");
		mhsrb.param("receiveUserIds", "903158226865868800");
		mhsrb.param("receiveUserNames", "sadmin");
		mhsrb.param("receiveFullNames", "超级管理员");
		mhsrb.param("isReceiveSmsRemind", "0");
		mhsrb.param("copyUserIds", "");
		mhsrb.param("copyUserNames", "");
		mhsrb.param("copyFullNames", "");
		mhsrb.param("isCopySmsRemind", "0");
		mhsrb.param("isScrap", "0");
		mhsrb.param("attachmentCount", "5");
		mhsrb.param("subjectText", "Activiti Explorer默认使用H2内存数据库");
		mhsrb.param("mailContent", "我们不预测未来，我们创造未来。Activiti Explorer默认使用H2内存数据库.");
		mhsrb.param("isReceipt", "0");
		mhsrb.param("mailSize", "100");

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("内部邮件-草稿箱-新增");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "数字", "不可为空", "主键ID", "不加密"));
		ipList.add(new InterfaceParam("receiveUserIds", "字符串", "可为空", "收件人用户ID(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("receiveUserNames", "字符串", "可为空", "收件人账号(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("receiveFullNames", "字符串", "可为空", "收件人名称(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("isReceiveSmsRemind", "数字", "不可为空", "是否进行收件人短信提醒", "不加密"));
		ipList.add(new InterfaceParam("copyUserIds", "字符串", "可为空", "抄送人用户ID(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("copyUserNames", "字符串", "可为空", "抄送人账号(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("copyFullNames", "字符串", "可为空", "抄送人名称(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("isCopySmsRemind", "数字", "不可为空", "是否进行抄送人短信提醒", "不加密"));
		ipList.add(new InterfaceParam("isScrap", "数字", "不可为空", "是否设置为废件（即删除操作）", "不加密"));
		ipList.add(new InterfaceParam("scrapTime", "日期", "可为空", "设置为废件时间", "不加密"));
		ipList.add(new InterfaceParam("attachmentCount", "数字", "不可为空", "附件个数", "不加密"));
		ipList.add(new InterfaceParam("subjectText", "字符串", "可为空", "主题", "不加密"));
		ipList.add(new InterfaceParam("mailContent", "字符串", "可为空", "邮件正文", "不加密"));
		ipList.add(new InterfaceParam("isReceipt", "数字", "不可为空", "对方收到后是否要回执", "不加密"));
		ipList.add(new InterfaceParam("mailSize", "数字", "不可为空", "邮件大小(正文+附件的大小)", "不加密"));
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
	 * 内部邮件-草稿箱-列表(分页)
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月20日 上午11:17:56
	 */
	@Test
	public void testList() throws Exception {
		String uri = "http://localhost:8080/app/mai/maiDraftBox/list";
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
		io.setInterfaceName("内部邮件-草稿箱-列表(分页)");
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
	 * 内部邮件-草稿箱-编辑
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月20日 下午1:57:29
	 */
	@Test
	public void testUpdate() throws Exception {
		String uri = "http://localhost:8080/app/mai/maiDraftBox/update";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "917272669656535040");
		mhsrb.param("subjectText", "修改邮件10-17 20:22");
		mhsrb.param("mailContent", "测试修改正文10-17 20:22");

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("内部邮件-草稿箱-编辑");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "数字", "不可为空", "主键ID", "不加密"));
		ipList.add(new InterfaceParam("receiveUserIds", "字符串", "可为空", "收件人用户ID(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("receiveUserNames", "字符串", "可为空", "收件人账号(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("receiveFullNames", "字符串", "可为空", "收件人名称(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("isReceiveSmsRemind", "数字", "不可为空", "是否进行收件人短信提醒", "不加密"));
		ipList.add(new InterfaceParam("sendUserId", "数字", "不可为空", "发件人用户ID", "不加密"));
		ipList.add(new InterfaceParam("sendUserName", "字符串", "不可为空", "发件人账号", "不加密"));
		ipList.add(new InterfaceParam("sendFullName", "字符串", "不可为空", "发件人名称", "不加密"));
		ipList.add(new InterfaceParam("copyUserIds", "字符串", "可为空", "抄送人用户ID(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("copyUserNames", "字符串", "可为空", "抄送人账号(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("copyFullNames", "字符串", "可为空", "抄送人名称(分号分隔)", "不加密"));
		ipList.add(new InterfaceParam("isCopySmsRemind", "数字", "不可为空", "是否进行抄送人短信提醒", "不加密"));
		ipList.add(new InterfaceParam("isScrap", "数字", "不可为空", "是否设置为废件（即删除操作）", "不加密"));
		ipList.add(new InterfaceParam("scrapTime", "日期", "可为空", "设置为废件时间", "不加密"));
		ipList.add(new InterfaceParam("attachmentCount", "数字", "不可为空", "附件个数", "不加密"));
		ipList.add(new InterfaceParam("subjectText", "字符串", "可为空", "主题", "不加密"));
		ipList.add(new InterfaceParam("mailContent", "字符串", "可为空", "邮件正文", "不加密"));
		ipList.add(new InterfaceParam("isReceipt", "数字", "不可为空", "对方收到后是否要回执", "不加密"));
		ipList.add(new InterfaceParam("mailSize", "数字", "不可为空", "邮件大小(正文+附件的大小)", "不加密"));
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
	 * 内部邮件-草稿箱-删除
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月20日 下午2:10:42
	 */
	@Test
	public void testDelete() throws Exception {
		String uri = "http://localhost:8080/app/mai/maiDraftBox/delete";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);

		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("内部邮件-草稿箱-删除");
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
	 * 内部邮件-草稿箱-编辑
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月20日 上午11:17:56
	 */
	@Test
	public void testGetById() throws Exception {
		String uri = "http://localhost:8080/app/mai/maiDraftBox/getById";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("id", "917271897610997760");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("内部邮件-草稿箱-编辑");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("ID", "数字", "不可为空", "主键ID", "不加密"));
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
