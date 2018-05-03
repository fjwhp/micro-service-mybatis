package aljoin.web.controller.act;

import aljoin.object.InterfaceObject;
import aljoin.object.InterfaceParam;
import aljoin.util.JsonUtil;
import aljoin.web.BaseTest;
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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

// 告诉Junit运行使用Spring 的单元测试支持
@RunWith(SpringRunner.class)
// 带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActAljoinBpmnUserTest extends BaseTest {


  @Resource
  private WebApplicationContext context;
  private MockMvc mvc;

  @Before
  public void setupMvc() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  /**
   * 
   * 单元测试例子(根据流程id对用户授权).
   *
   * @return：void
   *
   * @author：pengsp
   *
   * @date：2017年9月1日
   */
  @Test
  public void testAdd() throws Exception {
    String uri = "http://localhost:8080/act/actAljoinBpmnUser/add";
    MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
    mhsrb.param("users","903158809152704512");
    mhsrb.param("bpmnId","910669969267609600");

    MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    String content = mvcResult.getResponse().getContentAsString();
    InterfaceObject io = new InterfaceObject();
    /* 设置版本号 */
    io.setInterfaceVersion("v1");
    /* 设置接口名称 */
    io.setInterfaceName("流程管理-根据流程id对用户授权");
    /* 设置接口地址 */
    io.setInterfaceAddress(uri);
    /* 设置接口参数 */
    List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
    ipList.add(new InterfaceParam("userId", "字符串", "不可为空", "用户id","不加密"));
    ipList.add(new InterfaceParam("bpmnId", "数字", "不可为空", "流程id","不加密"));
    ipList.add(new InterfaceParam("isActive", "字符串", "不可为空", "是否激活","不加密"));
    io.setParamList(ipList);
    /* 设置接口结果集以及加密字段 */
    io.setRetObj(JsonUtil.str2json(content));
    //io.setRetEncryptProps("userName3,userName3");
    
    
    System.out.println("status:" + status);
    System.out.println("===================返回结果beg=======================");
    System.out.println(JsonUtil.obj2str(io));
    System.out.println("===================返回结果end=======================");
    
    /* 生成接口文档 */
    makeItable(io);

  }

  /**
   *
   * 单元测试例子(获取部门列表).
   *
   * @return：void
   *
   * @author：pengsp
   *
   * @date：2017年9月1日
   */
  @Test
  public void testGetDepartmentList() throws Exception {
    String uri = "http://localhost:8080/act/actAljoinFormCategory/getDepartmentList";
    MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);

    MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    String content = mvcResult.getResponse().getContentAsString();
    InterfaceObject io = new InterfaceObject();
    /* 设置版本号 */
    io.setInterfaceVersion("v1");
    /* 设置接口名称 */
    io.setInterfaceName("流程管理-获取部门列表");
    /* 设置接口地址 */
    io.setInterfaceAddress(uri);
    /* 设置接口参数 */
    List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
    ipList.add(new InterfaceParam("id", "字符串", "不可为空", "部门id","不加密"));
    ipList.add(new InterfaceParam("is_active", "字符串", "不可为空", "是否激活","不加密"));
    ipList.add(new InterfaceParam("dept_code", "字符串", "不可为空", "部门编号(唯一)","不加密"));
    ipList.add(new InterfaceParam("dept_level", "数字", "不可为空", "部门级别","不加密"));
    ipList.add(new InterfaceParam("dept_name", "字符串", "不可为空", "部门名称","不加密"));
    ipList.add(new InterfaceParam("dept_rank", "数字", "不可为空", "部门(同级)排序","不加密"));
    ipList.add(new InterfaceParam("parent_id", "数字", "不可为空", "父级部门ID","不加密"));

    io.setParamList(ipList);
    /* 设置接口结果集以及加密字段 */
    io.setRetObj(JsonUtil.str2json(content));
    //io.setRetEncryptProps("userName3,userName3");


    System.out.println("status:" + status);
    System.out.println("===================返回结果beg=======================");
    System.out.println(JsonUtil.obj2str(io));
    System.out.println("===================返回结果end=======================");

    /* 生成接口文档 */
    makeItable(io);

  }

  /**
   *
   * 单元测试例子(根据部门id获取用户).
   *
   * @return：void
   *
   * @author：pengsp
   *
   * @date：2017年9月1日
   */
  @Test
  public void testGetAutDepartmentUserList() throws Exception {
    String uri = "http://localhost:8080/act/actAljoinFormCategory/getAutDepartmentUserList";
    MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
    // mhsrb.param("merchantId", "1098e1ea3bc346c9bc9a284898b8f462");
    mhsrb.param("deptId", "902107669405200384");

    MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    String content = mvcResult.getResponse().getContentAsString();
    InterfaceObject io = new InterfaceObject();
    /* 设置版本号 */
    io.setInterfaceVersion("v1");
    /* 设置接口名称 */
    io.setInterfaceName("流程管理-根据部门id获取用户");
    /* 设置接口地址 */
    io.setInterfaceAddress(uri);
    /* 设置接口参数 */
    List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
    ipList.add(new InterfaceParam("id", "数字", "不可为空", "用户id","不加密"));
    ipList.add(new InterfaceParam("fullName", "字符串", "不可为空", "昵称","不加密"));
    io.setParamList(ipList);
    /* 设置接口结果集以及加密字段 */
    io.setRetObj(JsonUtil.str2json(content));
    //io.setRetEncryptProps("userName3,userName3");


    System.out.println("status:" + status);
    System.out.println("===================返回结果beg=======================");
    System.out.println(JsonUtil.obj2str(io));
    System.out.println("===================返回结果end=======================");

    /* 生成接口文档 */
    makeItable(io);

  }
}
