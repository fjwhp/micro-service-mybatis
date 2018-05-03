package aljoin.sms.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sms.dao.entity.SmsShortMessageSummary;

/**
 * 
 * 短信明细表(服务类).
 * 
 * @author：huangw
 * 
 * @date： 2018-01-15
 */
public interface SmsShortMessageSummaryService extends IService<SmsShortMessageSummary> {

  /**
   * 
   * 短信明细表(分页列表).
   *
   * @return：Page<SmsShortMessageSummary>
   *
   * @author：huangw
   *
   * @date：2018-01-15
   */
	public Page<SmsShortMessageSummary> list(PageBean pageBean, SmsShortMessageSummary obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：huangw
   *
   * @date：2018-01-15
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
   * @date：2018-01-15
   */
  public void copyObject(SmsShortMessageSummary obj) throws Exception;
  
  /**
   * 
   * 特殊个人模版发送短信
   *
   * @return：RetMsg
   *
   * @author：huangw
   *
   * @date：2017年12月20日 上午10:26:27
   */
  public  RetMsg addOne(SmsShortMessageSummary obj, AutUser user) ;


}
