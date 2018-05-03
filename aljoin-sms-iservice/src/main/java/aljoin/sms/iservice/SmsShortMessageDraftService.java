package aljoin.sms.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.sms.dao.entity.SmsShortMessageDraft;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 短信草稿表(服务类).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-10
 */
public interface SmsShortMessageDraftService extends IService<SmsShortMessageDraft> {

  /**
   * 
   * 短信草稿表(分页列表).
   *
   * @return：Page<SmsShortMessageDraft>
   *
   * @author：laijy
   *
   * @date：2017-10-10
   */
	public Page<SmsShortMessageDraft> list(PageBean pageBean, SmsShortMessageDraft obj,String time1,String time2) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：laijy
   *
   * @date：2017-10-10
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * 复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @return：void
   *
   * @author：laijy
   *
   * @date：2017-10-10
   */
  public void copyObject(SmsShortMessageDraft obj) throws Exception;
  
  /**
   * 
   * 手机短信-草稿箱-批量删除
   *
   * @return：RetMsg
   *
   * @author：laijy
   *
   * @date：2017年10月26日 下午4:07:33
   */
  public RetMsg deleteShortMessageDraftList(String ids) throws Exception;

  /**
   * 
   * 短信草稿箱详情
   *
   * @return：SmsShortMessageDraft
   *
   * @author：xuc
   *
   * @date：2017年12月20日 下午5:10:58
   */
  public SmsShortMessageDraft detail(Long id) throws Exception;

  public RetMsg sendDraft(SmsShortMessage obj, String userId) throws Exception;
}
