package aljoin.web.controller.ioa;

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
public class IoaWaitWorkTest extends BaseTest {


  @Resource
  private WebApplicationContext context;
  private MockMvc mvc;

  @Before
  public void setupMvc() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(context).build();
  }
  
  /**
  *
  * 单元测试(分发).
  *
  * @return：void
  *
  * @author：huangw
  *
  * @date：2018年3月20日
  */
  
 @Test
 public void distribution() throws Exception {
   String uri = "http://localhost:8081/ioa/ioaWaitWork/distribution";
   MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
   mhsrb.param("processInstanceId", "975987470540697601");
   mhsrb.param("htmlCode", "内容页面");
   mhsrb.param("sendIds", "866131328341528576;886127277537140736");
   MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
   int status = mvcResult.getResponse().getStatus();
   String content = mvcResult.getResponse().getContentAsString();
   InterfaceObject io = new InterfaceObject();
   /* 设置版本号 */
   io.setInterfaceVersion("v1");
   /* 设置接口名称 */
   io.setInterfaceName("分发");
   /* 设置接口地址 */
   io.setInterfaceAddress(uri);
   /* 设置接口参数 */
   List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
   ipList.add(new InterfaceParam("processInstanceId", "字符串", "不可为空", "流程实例id","不加密"));
   ipList.add(new InterfaceParam("htmlCode", "字符串", "不可为空", "表单页面代码","不加密"));
   ipList.add(new InterfaceParam("sendIds", "字符串", "不可为空", "接收者ID","不加密"));
   
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
   * 单元测试例子(根据用户权限获取流程分类列表).
   *
   * @return：void
   *
   * @author：pengsp
   *
   * @date：2017年9月1日
   */
  @Test
  public void add() throws Exception {
    String uri = "http://localhost:8080/ioa/ioaEntrustWork/add";
    MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
    MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    String content = mvcResult.getResponse().getContentAsString();
    InterfaceObject io = new InterfaceObject();
    /* 设置版本号 */
    io.setInterfaceVersion("v1");
    /* 设置接口名称 */
    io.setInterfaceName("新建工作-根据用户权限获取流程分类列表");
    /* 设置接口地址 */
    io.setInterfaceAddress(uri);
    /* 设置接口参数 */
    List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
    ipList.add(new InterfaceParam("id", "字符串", "不可为空", "流程分类id","不加密"));
    ipList.add(new InterfaceParam("is_active", "字符串", "不可为空", "是否激活","不加密"));
    ipList.add(new InterfaceParam("category_level", "数字", "不可为空", "分类级别","不加密"));
    ipList.add(new InterfaceParam("category_name", "字符串", "不可为空", "流程分类名称(唯一)","不加密"));
    ipList.add(new InterfaceParam("category_rank", "数字", "不可为空", "(同级)分类排序","不加密"));
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

  
}
