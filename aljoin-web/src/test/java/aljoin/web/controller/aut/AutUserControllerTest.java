package aljoin.web.controller.aut;

import java.util.SortedMap;
import java.util.TreeMap;

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

import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutUserService;
import aljoin.object.RetMsg;
import aljoin.object.SsoData;
import aljoin.util.EncryptUtil;
import aljoin.util.JsonUtil;
import aljoin.util.StringUtil;
import aljoin.web.BaseTest;

//告诉Junit运行使用Spring 的单元测试支持
@RunWith(SpringRunner.class)
//带有Spring Boot支持的引导程序
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class AutUserControllerTest extends BaseTest {
	
	@Resource
	private WebApplicationContext context;
	private MockMvc mvc;

	@Before
	public void setupMvc() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	@Resource
    private AutDepartmentService autDepartmentService;
	@Resource
	private AutUserService autUserService ;
	
	
	/**
	 * 
	 * 权限管理-用户管理-获取用户列表（id和昵称）
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月21日 上午8:52:37
	 */
	/*@Test
	public void testgetUserList() throws Exception {
		String uri = "http://localhost:8080/aut/autUser/getUserList";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		 设置版本号 
		io.setInterfaceVersion("v1");
		 设置接口名称 
		io.setInterfaceName("权限管理-用户管理-获取用户列表（id和昵称）");
		 设置接口地址 
		io.setInterfaceAddress(uri);
		 设置接口参数 
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		io.setParamList(ipList);
		 设置接口结果集以及加密字段 
//		io.setRetObj(JsonUtil.str2json(content));
		io.setRetObj(JsonUtil.str2jsonArr(content));
		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(io));
		System.out.println("===================返回结果end=======================");

		 生成接口文档 
		makeItable(io);

	}
	*/
	/**
	 * 
	 * 获取用户列表
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月24日 上午8:32:39
	 */
	/*	@Test
	public void testList() throws Exception {
		String uri = "http://localhost:8080/aut/autUser/list";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("pageNum", "1");
		mhsrb.param("pageSize", "10");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		 设置版本号 
		io.setInterfaceVersion("v1");
		 设置接口名称 
		io.setInterfaceName("权限管理-用户管理-获取用户列表（id和昵称）");
		 设置接口地址 
		io.setInterfaceAddress(uri);
		 设置接口参数 
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		io.setParamList(ipList);
		 设置接口结果集以及加密字段 
//		io.setRetObj(JsonUtil.str2json(content));
		io.setRetObj(JsonUtil.str2json(content));
		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(io));
		System.out.println("===================返回结果end=======================");

		 生成接口文档 
		makeItable(io);

	}*/
	/**
	 * 
	 * 查询不在【部门-用户】表里的所有用户
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年8月25日 下午4:14:08
	 */
	/*@Test
	public void tesListNoDepartmentUser() throws Exception {
		String uri = "http://localhost:8080/aut/autUser/listNoDepartmentUser";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		mhsrb.param("pageNum", "1");
		mhsrb.param("pageSize", "10");
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		 设置版本号 
		io.setInterfaceVersion("v1");
		 设置接口名称 
		io.setInterfaceName("查询不在【部门-用户】表里的所有用户");
		 设置接口地址 
		io.setInterfaceAddress(uri);
		 设置接口参数 
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("deptId", "Long", "不可为空", "部门ID", "不加密"));
		io.setParamList(ipList);
		 设置接口结果集以及加密字段 
		io.setRetObj(JsonUtil.str2json(content));
		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(io));
		System.out.println("===================返回结果end=======================");

		 生成接口文档 
		makeItable(io);

	}*/
	
	/**
	 * 
	 * 公共方法-对多个(部门的)用户先按照部门后按照用户进行排序
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月15日 上午9:08:18
	 */
	/*@Test
	public void testOrderUserList() throws Exception {
		String uri = "http://localhost:8080/pub/public/orderUserList";
		MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);
		MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		InterfaceObject io = new InterfaceObject();
		 设置版本号 
		io.setInterfaceVersion("v1");
		 设置接口名称 
		io.setInterfaceName("通用接口-多个部门的多个用户排序");
		 设置接口地址 
		io.setInterfaceAddress(uri);
		 设置接口参数 
		List<InterfaceParam> ipList = new ArrayList<InterfaceParam>();
		ipList.add(new InterfaceParam("用户-部门关联表数组", "AutUser", "不可为空", "用户数组(必须包含id,userId,departmentId)", "不加密"));
		io.setParamList(ipList);
		 设置接口结果集以及加密字段 
		io.setRetObj(JsonUtil.str2jsonArr(content));

		System.out.println("status:" + status);
		System.out.println("===================返回结果beg=======================");
		System.out.println(JsonUtil.obj2str(io));
		System.out.println("===================返回结果end=======================");
		 生成接口文档 
		makeItable(io);
	}
	*/
	/**
	mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON))异常，无法调到。
	 */
	  @SuppressWarnings("unused")
    @Test
	  public void testSsoDataSynch() throws Exception{
	    String uri = "http://localhost:8080/sso/dataSynch";
	    MockHttpServletRequestBuilder mhsrb = MockMvcRequestBuilders.post(uri);

	    // 获取ras密钥对
//	    Map<String, String> keyPair = EncryptUtil.getRsaKeyPair();
	    SsoData ssoData = new SsoData();
	    String operationType  = "1"; //操作类型：1-新增，2-修改，3-查询
	    String loginAccount  = "hhz";
	    String loginPwd = "12345678";
	    Long timeStamp = System.currentTimeMillis();
	    String sign = ""; 
	    //密码需要MD5加密且转换为大写
	    loginPwd = EncryptUtil.encryptMD5(loginPwd).toUpperCase();
	    // "2017-12-20 13:33:48.115"
//	     Timestamp ts = new Timestamp(System.currentTimeMillis());
//	     timeStamp = ts.toString();
	    
	    SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
	    parameters.put("operationType", operationType);
	    parameters.put("loginAccount", loginAccount);
	    parameters.put("loginPwd", loginPwd);
	    parameters.put("timeStamp", timeStamp.toString());
	    String loginSceret = "";
	    sign = StringUtil.getSign(parameters, loginSceret);//需要MD5加密且转换为大写
	    
	    mhsrb.param("operationType", operationType);
	    mhsrb.param("loginAccount", loginAccount);
	    mhsrb.param("loginPwd", loginPwd);
	    mhsrb.param("timeStamp", timeStamp.toString());
	    mhsrb.param("sign", sign);
	    System.out.println(mhsrb);
	    //mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON))异常，无法调到。
	    MvcResult mvcResult = mvc.perform(mhsrb.accept(MediaType.APPLICATION_JSON)).andReturn();
	    int status = mvcResult.getResponse().getStatus();
	    String content = mvcResult.getResponse().getContentAsString();

	    System.out.println("===================返回结果beg=======================");
	    System.out.println("status:" + status);
	    System.out.println(JsonUtil.obj2str(JsonUtil.str2json(content)));
	    System.out.println("===================返回结果end=======================");
	    
	  }
	  //直接测试服务层的调用功能。
	  @Test
      public void testSsoDataSynch2() throws Exception{
	    SsoData ssoData = new SsoData();
        String operationType  = "3"; //操作类型：1-新增，2-修改，3-查询
        String loginAccount  = "hhz";
        String loginPwd = "87654321";
        Long timeStamp = System.currentTimeMillis();
        String sign = ""; 
        //密码需要MD5加密且转换为大写
        loginPwd = EncryptUtil.encryptMD5(loginPwd).toUpperCase();
        // "2017-12-20 13:33:48.115"
//       Timestamp ts = new Timestamp(System.currentTimeMillis());
//       timeStamp = ts.toString();
        //获取签名
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("operationType", operationType);
        parameters.put("loginAccount", loginAccount);
        parameters.put("loginPwd", loginPwd);
        parameters.put("timeStamp", timeStamp.toString());
        String loginSceret = "";
        sign = StringUtil.getSign(parameters, loginSceret);//需要MD5加密且转换为大写
        
        //给参数对象赋值
        ssoData.setOperationType(operationType);
        ssoData.setLoginAccount(loginAccount);
        ssoData.setLoginPwd(loginPwd);
        ssoData.setTimeStamp(timeStamp.toString());
        ssoData.setSign(sign);
        
        System.out.println("参数对象值："+ssoData.toString());
	    RetMsg retMsg = new RetMsg();
	    retMsg = autUserService.ssoDataSynch(ssoData);
	    
	    System.out.println("==开始返回信息============");
	    System.out.println(retMsg.getCode().toString());
	    System.out.println(retMsg.getMessage());
	    if(retMsg.getObject() != null){
	      ssoData = (SsoData)retMsg.getObject();
	      System.out.println("返回值帐户："+ssoData.getLoginAccount());
	      System.out.println("返回值密码："+ssoData.getLoginPwd());
	    }
	  }
	  //测试oa调用第三方用户信息接口，维护、查询数据。add by huanghz at 2017-12-20
	  @Test
	  public void testSsoDataSynInvoke() throws Exception{
	  //OA具体模块的新增、修改、查询在调用autUserService中封装的第三方接口方法ssoDataSynInvoke(ssoData)前需要传递的参数；
	    SsoData ssoData = new SsoData();
        String operationType  = "1"; //操作类型：1-新增，2-修改，3-查询
        String loginAccount  = "OAhhz";
        String loginPwd = "11111111"; //OA具体模块的新增、修改、查询 可传文明或者加密密码均可；
        ssoData.setOperationType(operationType);
        ssoData.setLoginAccount(loginAccount);
        ssoData.setLoginPwd(loginPwd);
        
        RetMsg retMsg = autUserService.ssoDataSynInvoke(ssoData);
        
        System.out.println("==oa调用第三方接口新增、修改、查询返回值开始============");
        System.out.println(retMsg.getCode().toString());
        System.out.println(retMsg.getMessage());
        if(retMsg.getObject() != null){
          ssoData = (SsoData)retMsg.getObject();
          System.out.println("返回值帐户："+ssoData.getLoginAccount()+"===");
          System.out.println("返回值密码："+ssoData.getLoginPwd()+"===");
        }
        System.out.println("==oa调用第三方接口新增、修改、查询返回值结束============");
        
	  }
	  
	  
	  
	
	
}
