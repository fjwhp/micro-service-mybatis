package aljoin.goo.iservice;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.aut.dao.entity.AutUser;
import aljoin.goo.dao.entity.GooPurchase;
import aljoin.goo.dao.object.AppGooPurchaseVO;
import aljoin.goo.dao.object.GooPurchaseVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 办公用品申购信息表(服务类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-04
 */
public interface GooPurchaseService extends IService<GooPurchase> {

  /**
   * 
   * 办公用品申购信息表(分页列表).
   *
   * @return：Page<GooPurchase>
   *
   * @author：xuc
   *
   * @date：2018-01-04
   */
	public Page<GooPurchase> list(PageBean pageBean, GooPurchase obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：xuc
   *
   * @date：2018-01-04
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * 复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @return：void
   *
   * @author：xuc
   *
   * @date：2018-01-04
   */
  public void copyObject(GooPurchase obj) throws Exception;

  /**
   * 
   * 办公用品申购详情
   *
   * @return：GooPurchaseVO
   *
   * @author：xuc
   *
   * @date：2018年1月15日 上午11:07:24
   */
  public List<GooPurchase> getById(GooPurchaseVO obj);

  /**
   * 
   * @throws Exception 
   * 办公用品申购流程作废
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月24日 上午10:12:33
   */
  public RetMsg toVoid(String taskId, String bizId, Long userId) throws Exception;

  /**
   * 
   * app办公用品申购流程待办详情
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年2月6日 下午4:45:11
   */
  public RetMsg getInOutList(AppGooPurchaseVO appGooInOutVO);

  /**
   * 
   * @throws Exception 
   * app办公用品申购完成任务
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年2月6日 下午4:55:47
   */
  public RetMsg completeAppTask(Map<String, Object> variables, String taskId, String bizId,
      String userId, String message, AutUser autUser) throws Exception;
}
