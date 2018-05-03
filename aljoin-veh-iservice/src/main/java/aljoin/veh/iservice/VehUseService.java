package aljoin.veh.iservice;

import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.veh.dao.entity.VehUse;
import aljoin.veh.dao.object.AppVehInfoVO;
import aljoin.veh.dao.object.VehUseVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 车船使用申请信息表(服务类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-08
 */
public interface VehUseService extends IService<VehUse> {

  /**
   * 
   * 车船使用申请信息表(分页列表).
   *
   * @return：Page<VehUse>
   *
   * @author：xuc
   *
   * @date：2018-01-08
   */
	public Page<VehUse> list(PageBean pageBean, VehUse obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：xuc
   *
   * @date：2018-01-08
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
   * @date：2018-01-08
   */
  public void copyObject(VehUse obj) throws Exception;

  /**
   * 
   * 车船申请详情
   *
   * @return：VehUseVO
   *
   * @author：xuc
   *
   * @date：2018年1月19日 下午3:02:48
   */
  public VehUseVO getById(Long id);

  /**
   * 
   * @throws Exception 
   * 用车申请流程作废
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月24日 上午10:18:05
   */
  public RetMsg toVoid(String taskId, String bizId, Long userId) throws Exception;

  /**
   * 
   * app车船使用申请待办详情
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年2月2日 下午3:42:59
   */
  public RetMsg appDetail(AppVehInfoVO info);

  /**
   * 
   * @throws Exception 
   * 车船申请完成任务
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年2月2日 下午4:41:22
   */
  public RetMsg completeAppTask(Map<String, Object> variables, String taskId, String bizId,
      String userId, String message, AutUser autUser) throws Exception;
}
