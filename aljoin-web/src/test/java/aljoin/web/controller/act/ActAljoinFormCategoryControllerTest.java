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
public class ActAljoinFormCategoryControllerTest extends BaseTest {


  @Resource
  private WebApplicationContext context;
  private MockMvc mvc;

  @Before
  public void setupMvc() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  /**
   * 
   * 单元测试例子(表单分类列表).
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017年9月1日
   */
  @Test
  public void testList() throws Exception {
    String uri = "http://localhost:8080/act/actAljoinFormCategory/list";
    MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
    // mhsrb.param("merchantId", "1098e1ea3bc346c9bc9a284898b8f462");
    mhsrb.param("pageNum", "1");
    mhsrb.param("pageSize", "100");
    mhsrb.param("categoryName","服装");

    MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    String content = mvcResult.getResponse().getContentAsString();
    InterfaceObject io = new InterfaceObject();
    /* 设置版本号 */
    io.setInterfaceVersion("v1");
    /* 设置接口名称 */
    io.setInterfaceName("表单分类-列表");
    /* 设置接口地址 */
    io.setInterfaceAddress(uri);
    /* 设置接口参数 */
    List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
    ipList.add(new InterfaceParam("pageNum", "数字", "不可为空", "页码","不加密"));
    ipList.add(new InterfaceParam("pageSize", "数字", "不可为空", "每页大小","不加密"));
    ipList.add(new InterfaceParam("categoryName", "字符串", "不可为空", "表单分类名称","不加密"));
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
   * 单元测试例子(表单分类新增).
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017年9月1日
   */
  @Test
  public void testAdd() throws Exception {
    String uri = "http://localhost:8080/act/actAljoinFormCategory/add";
    MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
    // mhsrb.param("merchantId", "1098e1ea3bc346c9bc9a284898b8f462");
    mhsrb.param("categoryName", "运动");
    mhsrb.param("isActive","1");
    mhsrb.param("categoryRank","1");
    mhsrb.param("parentId","903428344984326144");
    mhsrb.param("categoryLevel","1");

    MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    String content = mvcResult.getResponse().getContentAsString();
    InterfaceObject io = new InterfaceObject();
    /* 设置版本号 */
    io.setInterfaceVersion("v1");
    /* 设置接口名称 */
    io.setInterfaceName("表单分类-新增");
    /* 设置接口地址 */
    io.setInterfaceAddress(uri);
    /* 设置接口参数 */
    List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
    ipList.add(new InterfaceParam("categoryName", "字符串", "不可为空", "分类名称","不加密"));
    ipList.add(new InterfaceParam("isActive", "字符串", "不可为空", "是否激活","不加密"));
    ipList.add(new InterfaceParam("categoryRank", "数字", "不可为空", "排序","不加密"));
    ipList.add(new InterfaceParam("parentId", "数字", "不可为空", "分类父ID","不加密"));
    ipList.add(new InterfaceParam("categoryLevel", "数字", "不可为空", "分类级别","不加密"));

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
   * 单元测试例子(表单分类详情).
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017年9月1日
   */
  @Test
  public void testDetail() throws Exception {
    String uri = "http://localhost:8080/act/actAljoinFormCategory/getById";
    MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
    // mhsrb.param("merchantId", "1098e1ea3bc346c9bc9a284898b8f462");
    mhsrb.param("id", "903428344984326144");


    MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    String content = mvcResult.getResponse().getContentAsString();
    InterfaceObject io = new InterfaceObject();
    /* 设置版本号 */
    io.setInterfaceVersion("v1");
    /* 设置接口名称 */
    io.setInterfaceName("表单分类-详情");
    /* 设置接口地址 */
    io.setInterfaceAddress(uri);
    /* 设置接口参数 */
    List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
    ipList.add(new InterfaceParam("id", "数字", "不可为空", "分类ID","不加密"));
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
   * 单元测试例子(表单分类编辑).
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017年9月1日
   */
  @Test
  public void testUpdate() throws Exception {
    String uri = "http://localhost:8080/act/actAljoinFormCategory/update";
    MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
    // mhsrb.param("merchantId", "1098e1ea3bc346c9bc9a284898b8f462");
    mhsrb.param("id", "903428344984326144");
    mhsrb.param("categoryName", "服装2");

    MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    String content = mvcResult.getResponse().getContentAsString();
    InterfaceObject io = new InterfaceObject();
    /* 设置版本号 */
    io.setInterfaceVersion("v1");
    /* 设置接口名称 */
    io.setInterfaceName("表单分类-编辑");
    /* 设置接口地址 */
    io.setInterfaceAddress(uri);
    /* 设置接口参数 */
    List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
    ipList.add(new InterfaceParam("id", "数字", "不可为空", "分类ID","不加密"));

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
   * 单元测试例子(表单分类删除).
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017年9月1日
   */
  @Test
  public void testDelete() throws Exception {
    String uri = "http://localhost:8080/act/actAljoinFormCategory/delete";
    MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
    // mhsrb.param("merchantId", "1098e1ea3bc346c9bc9a284898b8f462");
    mhsrb.param("id", "903428836292534272");


    MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    String content = mvcResult.getResponse().getContentAsString();
    InterfaceObject io = new InterfaceObject();
    /* 设置版本号 */
    io.setInterfaceVersion("v1");
    /* 设置接口名称 */
    io.setInterfaceName("表单分类-删除");
    /* 设置接口地址 */
    io.setInterfaceAddress(uri);
    /* 设置接口参数 */
    List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
    ipList.add(new InterfaceParam("id", "数字", "不可为空", "分类ID","不加密"));

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
   * 单元测试例子(根据父ID查询表单分类列表).
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017年9月1日
   */
  @Test
  public void testByParentId() throws Exception {
    String uri = "http://localhost:8080/act/actAljoinFormCategory/getListByParentId";
    MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
    // mhsrb.param("merchantId", "1098e1ea3bc346c9bc9a284898b8f462");
    mhsrb.param("parentId", "903428344984326144");
    mhsrb.param("pageNum", "1");
    mhsrb.param("pageSize", "100");

    MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    String content = mvcResult.getResponse().getContentAsString();
    InterfaceObject io = new InterfaceObject();
    /* 设置版本号 */
    io.setInterfaceVersion("v1");
    /* 设置接口名称 */
    io.setInterfaceName("根据父ID查询列表");
    /* 设置接口地址 */
    io.setInterfaceAddress(uri);
    /* 设置接口参数 */
    List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
    ipList.add(new InterfaceParam("parentId", "数字", "不可为空", "分类父ID","不加密"));
    ipList.add(new InterfaceParam("pageNum", "数字", "不可为空", "页码","不加密"));
    ipList.add(new InterfaceParam("pageSize", "数字", "不可为空", "每页大小","不加密"));
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
   * 单元测试例子(表单分类列表).
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017年9月1日
   */
  @Test
  public void testList2() throws Exception {
    String uri = "http://localhost:8080/act/actAljoinFormCategory/getList";
    MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
    mhsrb.param("parentId", "903428344984326144");
    MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.ALL)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    String content = mvcResult.getResponse().getContentAsString();
    InterfaceObject io = new InterfaceObject();
    /* 设置版本号 */
    io.setInterfaceVersion("v1");
    /* 设置接口名称 */
    io.setInterfaceName("根据父ID查询列表");
    /* 设置接口地址 */
    io.setInterfaceAddress(uri);
    /* 设置接口参数 */
    List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
    ipList.add(new InterfaceParam("parentId", "数字", "不可为空", "分类父ID","不加密"));
    ipList.add(new InterfaceParam("categoryName", "字符串", "不可为空", "分类名称","不加密"));
    ipList.add(new InterfaceParam("isActive", "字符串", "不可为空", "是否激活","不加密"));
    io.setParamList(ipList);
    /* 设置接口结果集以及加密字段 */
    io.setRetObj(JsonUtil.str2jsonArr(content));

    //io.setRetEncryptProps("userName3,userName3");


    System.out.println("status:" + status);
    System.out.println("===================返回结果beg=======================");
    System.out.println(JsonUtil.obj2str(io));
    System.out.println("===================返回结果end=======================");

    /* 生成接口文档 */
    makeItable(io);

  }

}
