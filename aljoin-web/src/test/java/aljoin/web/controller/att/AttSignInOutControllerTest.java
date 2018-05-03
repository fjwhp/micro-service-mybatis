package aljoin.web.controller.att;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class AttSignInOutControllerTest extends BaseTest {

	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;

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
	public void testGetAttSignInCountList() throws Exception {
		String uri = "http://localhost:8080/att/attSignInOut/getAttSignInCountList";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		/* 设置版本号 */
		io.setInterfaceVersion("v1");
		/* 设置接口名称 */
		io.setInterfaceName("考勤管理-考勤统计");
		/* 设置接口地址 */
		io.setInterfaceAddress(uri);
		/* 设置接口参数 */
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("id", "数字", "数字", "部门id", "不加密"));
		ipList.add(new InterfaceParam("deptName", "字符", "不可为空", "部门名称", "不加密"));
		ipList.add(new InterfaceParam("thisWeek", "数字", "可为空", "查询本周(传值：null或true)", "不加密"));
		ipList.add(new InterfaceParam("thisMonth", "字符串", "可为空", "查询本月(传值：null或true)", "不加密"));
		ipList.add(new InterfaceParam("time1", "时间", "可为空", "搜索起始日期", "不加密"));
		ipList.add(new InterfaceParam("time2", "时间", "可为空", "搜索结束日期", "不加密"));
		ipList.add(new InterfaceParam("orderByNoneSignIn", "数字", "可为空", "按未签到次数排序(1-降序,2-升序)", "不加密"));
		ipList.add(new InterfaceParam("orderByNoneSignOut", "数字", "可为空", "按未签退次数排序(1-降序,2-升序)", "不加密"));
		ipList.add(new InterfaceParam("orderByLate", "数字", "可为空", "按未迟到次数排序(1-降序,2-升序)", "不加密"));
		ipList.add(new InterfaceParam("orderByLeaveEarly", "数字", "可为空", "按早退次数排序(1-降序,2-升序)", "不加密"));
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
	
	public static void main(String[] args) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String time = sdf.format(date);
		System.out.println(time);
	}
	
}