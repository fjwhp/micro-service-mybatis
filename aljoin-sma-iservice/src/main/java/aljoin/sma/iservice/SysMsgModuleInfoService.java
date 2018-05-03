package aljoin.sma.iservice;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sma.dao.entity.SysMsgModuleInfo;

/**
 * 
 * 消息模板信息表(服务类).
 * 
 * @author：huangw
 * 
 * @date： 2017-11-14
 */
public interface SysMsgModuleInfoService extends IService<SysMsgModuleInfo> {

  /**
   * 
   * 消息模板信息表(分页列表).
   *
   * @return：Page<SysMsgModuleInfo>
   *
   * @author：huangw
   *
   * @date：2017-11-14
   */
	public Page<SysMsgModuleInfo> list(PageBean pageBean, SysMsgModuleInfo obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：huangw
   *
   * @date：2017-11-14
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * 复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @return：void
   *
   * @author：huangw
   *
   * @date：2017-11-14
   */
  public void copyObject(SysMsgModuleInfo obj) throws Exception;
  

  /**
   * 
   * 添加或修改
   *
   * @return：RetMsg
   *
   * @author：huangw
   *
   * @date：2017-11-14
   */
  public RetMsg addOrUpdata(SysMsgModuleInfo obj,String gropName) throws Exception;
  /**
   * 
   * 更具传入内容替换对应字段属性  type(协同办公|工作计划 |邮箱|会议) stype( 1:在线消息 2： 手机短信 3：邮件  ) behavior (操作：催办、办结时间……)
   *      list为对应消息模版上所对应字段属性(协同办公 list.size=5 ,  工作计划 list.size=5  , 邮件  list.size=6  , 会议  list.size=7 )
   *       例如工作计划：list[0]theme 写入主题   list[1] create  写入创建人   list[2]startTime 写入开始时间   list[3] endTime 结束时间					 	
   *                                       list[4] Share  写入被共享人
   *@return： List<String> list[0]模版替换返回的内容     list【1】邮件模版返回的邮件标题(不是邮件则没有)
   * @author：huangw
   *
   * @date：2017-11-14
   * 
   */
  public  List<String> contextTemplate(Map<String, String> list,String type,String stype,String behavior) throws Exception;


}
