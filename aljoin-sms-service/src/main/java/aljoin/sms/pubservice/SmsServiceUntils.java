package aljoin.sms.pubservice;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasson.im.api.APIClient;

import aljoin.config.AljoinSetting;
import aljoin.object.RetMsg;
import aljoin.util.SpringContextUtil;

/*基于厂家赵工提供的ImApi.jar API2.2    提供厂种API接口：MT、MO、RPT:其中MT:1:普通短信； 2:Wap Push 短信; 3:PDU短信;  后期扩展方法，如果在同一个方法中，提供的参数太多；
api返回值参考api文档及api反编译代码；*/

public class SmsServiceUntils {
  private static final Logger logger = LoggerFactory.getLogger(SmsServiceUntils.class);
  
  private static AljoinSetting aljoinSetting ;
    
  private static APIClient apiClient = null ; 
  private static APIClient getInstance(){
    if(apiClient == null){
      apiClient = new APIClient();
    }
    return apiClient;
  }
  
//提交时直接调用短信接口发送短信； 如果是保存则暂时把数据存入oa表而不调用短信接口； init();sendSm();release(); 提取至短信二次开发公用类中；  
  /*  厂家赵工提供：海洋局短信二次开发mas机提供的相关配置参数：ip:端口/dbName = 192.168.2.100:3306/mas  那接口编码就用oaapi，用户名oaapi，密码Abc@123456  */
  //测试关注问题：看提供包的连接表及加载的驱动类；   mas机的db端口默认为3306？  
  private static RetMsg init(){
    RetMsg retMsg = new RetMsg();
    
    Integer liCode = 0;
    String  lsMessage = "";
    //可以考虑系统参数表中取值； 
    aljoinSetting = (AljoinSetting) SpringContextUtil.getBean(AljoinSetting.class);
    String ip = aljoinSetting.getMasIP(); 
    String dbName = aljoinSetting.getMasDbName();
    String loginUserName = aljoinSetting.getMasLoginUserName() ; 
    String loginPwd = aljoinSetting.getMasLoginPwd() ;
    String apiCode = aljoinSetting.getMasApiCode();
    
    try {
      apiClient = getInstance();
      int connectRet = apiClient.init(ip, loginUserName, loginPwd, apiCode, dbName);
      if(connectRet == APIClient.IMAPI_SUCC){
        liCode = 0 ; 
        lsMessage = "初始化连接成功" ; 
      }else if(connectRet == APIClient.IMAPI_CONN_ERR){
        liCode = -1 ; 
        lsMessage = "初始化连接失败" ; 
      }else if(connectRet == APIClient.IMAPI_API_ERR){
        liCode = -7 ; 
        lsMessage = "apiID不存在" ; 
      }
      retMsg.setCode(liCode);
      retMsg.setMessage(lsMessage);
    } catch (Exception e) {
      logger.error("", e);
      e.printStackTrace();
      retMsg.setCode(99);
      retMsg.setMessage("初始化连接异常");
    }
    
    return retMsg ; 
  }
  

 /**
  * 
  * 调用短信api发送普通短信
  * @param mobiles 一个或多个手机号码，必填项
  * @param content 发送内容，必填项 
  * @return：RetMsg
  * @author：huanghz
  * @date：2018年1月9日 下午2:46:12
  */
  private static RetMsg sendSm(String[] mobiles, String content){
    RetMsg retMsg = new RetMsg();
    Integer liCode = 0;
    String  lsMessage = "";

    if(apiClient==null){
      retMsg.setCode(90);
      retMsg.setMessage("发送短信前请先调用init方法初始化");
      return retMsg;
    }
    
    if (mobiles == null || mobiles.length == 0)
    {
      retMsg.setCode(-6);
      retMsg.setMessage("请指定接收短信的一个或多个手机号码");
      return retMsg;
    }
    if(StringUtils.isEmpty(content)){
      retMsg.setCode(-6);
      retMsg.setMessage("请输入短信内容，不能为空");
      return retMsg;
    }
    if (content.trim().length() > 2000 ) {
      retMsg.setCode(-6);
      retMsg.setMessage("短信内容控制在2000个字之内");
      return retMsg;
    }
    long smID = RandomUtils.nextLong(1, 99999999);
    long srcID = RandomUtils.nextLong(1, 99999999);
    int rtn = apiClient.sendSM(mobiles, content, smID, srcID);
    if(rtn == APIClient.IMAPI_SUCC){            
      liCode = 0 ;
      lsMessage = "发送成功"; 
    }else if(rtn == APIClient.IMAPI_INIT_ERR){
      liCode = -9;
      lsMessage = "未初始化或初始化连接mas机失败";
    }else if(rtn == APIClient.IMAPI_CONN_ERR){
      liCode = -1;
      lsMessage = "数据库连接失败";
    }
    else if(rtn == APIClient.IMAPI_DATA_ERR){
      liCode = -6;
      lsMessage = "参数错误";
    }else if(rtn == APIClient.IMAPI_DATA_TOOLONG){
      liCode = -8;
      lsMessage = "参数内容超长";
    }else if(rtn == APIClient.IMAPI_INS_ERR){
      liCode = -3;
      lsMessage = "数据库插入错误";
    }else if(rtn == APIClient.IMAPI_IFSTATUS_INVALID){
      liCode = -10;
      lsMessage = "短信API接口失效";
    }else if(rtn == APIClient.IMAPI_GATEWAY_CONN_ERR){
      liCode = -11;
      lsMessage = "短信网关未连接";
    }
    
    retMsg.setCode(liCode);
    retMsg.setMessage(lsMessage);
    return retMsg ; 
  }
  
  
  private static void release(){
    apiClient.release();
  }
  /**
   * 发送普通短信MT
   * @param mobiles 一个或多个手机号码，必填项
   * @param content 发送内容，必填项 
   * @return：RetMsg
   * @author：huanghz
   * @date：2018年1月9日 下午2:46:12
   */
  public static final RetMsg sendSmMT(String[] mobiles, String content){
    RetMsg retMsg = new RetMsg();
    
    retMsg = init();
    if(retMsg.getCode() != 0 ){
      return retMsg ; 
    }
    retMsg = sendSm(mobiles, content);
    if(retMsg.getCode() != 0 ){
      return retMsg ; 
    }
    release();
    
    //假如有存在超时速度慢的情况，再考虑长连接发送短信，即连接上数据库发送短信后，不要马上释放db连接资源； 
   /* retMsg = sendSm(mobiles, content);
    if(retMsg.getCode() != 0 ){
      release();
      retMsg = init();
      if(retMsg.getCode() != 0 ){
        return retMsg ; 
      }
      retMsg = sendSm(mobiles, content);
      if(retMsg.getCode() != 0 ){
        return retMsg;
      }
    }*/
    
    retMsg.setCode(0);
    retMsg.setMessage("短信发送成功");
    return retMsg ; 
  }
 
  
}
