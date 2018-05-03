package aljoin.web.controller;

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

import aljoin.aut.iservice.AutUserService;
import aljoin.object.InterfaceObject;
import aljoin.object.InterfaceParam;
import aljoin.util.JsonUtil;
import aljoin.web.BaseTest;

// 告诉Junit运行使用Spring 的单元测试支持
@RunWith(SpringRunner.class)
// 带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoControllerTest extends BaseTest {


  @Resource
  private WebApplicationContext context;
  private MockMvc mvc;
  
  @Resource
  private AutUserService autUserService;

  @Before
  public void setupMvc() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  /**
   * 
   * 单元测试例子-新增部门.
   *
   * @return：void
   *
   * @author：zhongjy
   *
   * @date：2017年8月4日 下午2:14:32
   */
  @Test
  public void testList() throws Exception {
    String uri = "http://localhost:8080/demo/list";
    MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
    // mhsrb.param("merchantId", "1098e1ea3bc346c9bc9a284898b8f462");
//    mhsrb.param("pageNum", "1");
//    mhsrb.param("pageSize", "1");
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
    ipList.add(new InterfaceParam("pageNum", "int", "不可为空", "页码","不加密"));
    ipList.add(new InterfaceParam("pageSize", "int", "不可为空", "每页大小","不加密"));
    ipList.add(new InterfaceParam("userName3", "int", "可为空", "用户名3","加密"));
    ipList.add(new InterfaceParam("userName4", "int", "不可为空", "用户名4","不加密"));
    io.setParamList(ipList);
    /* 设置接口结果集以及加密字段 */
    io.setRetObj(JsonUtil.str2json(content));
    io.setRetEncryptProps("userName3,userName3");
    
    
    System.out.println("status:" + status);
    System.out.println("===================返回结果beg=======================");
    System.out.println(JsonUtil.obj2str(io));
    System.out.println("===================返回结果end=======================");
    
    /* 生成接口文档 */
    makeItable(io);

  }
  
  
  @Test
  public void testList2() throws Exception {
    autUserService.getMyUserList();
    //autUserService.selectList(null);
  }



}
