package aljoin.goo.iservice;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.aut.dao.entity.AutUser;
import aljoin.goo.dao.entity.GooInOut;
import aljoin.goo.dao.object.AppGooInOutVO;
import aljoin.goo.dao.object.GooInOutDO;
import aljoin.goo.dao.object.GooInOutVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 办公用品出入库信息表(服务类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-04
 */
public interface GooInOutService extends IService<GooInOut> {

  /**
   * 
   * 办公用品出入库信息表(分页列表).
   *
   * @return：Page<GooInOut>
   *
   * @author：xuc
   *
   * @date：2018-01-04
   */
	public Page<GooInOut> list(PageBean pageBean, GooInOut obj) throws Exception; 	
	
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
  public void copyObject(GooInOut obj) throws Exception;

  /**
   * 
   * 获取当前信息
   *
   * @return：GooInOutDO
   *
   * @author：xuc
   *
   * @date：2018年1月11日 上午10:46:42
   */
  public GooInOutDO getCurrent(AutUser autUser);

  /**
   * 
   * 出入库详情
   *
   * @return：List<GooInOut>
   *
   * @author：xuc
   *
   * @date：2018年1月19日 下午3:16:41
   */
  public List<GooInOut> getById(GooInOutVO obj);

  /**
   * 
   * @throws Exception 
   * 出入库作废
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月24日 上午9:51:14
   */
  public RetMsg toVoid(String taskId, String bizId, Long userId) throws Exception;

  /**
   * 
   * app待办办公用品出入库详情
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年2月2日 下午4:19:29
   */
  public RetMsg getInOutList(AppGooInOutVO appGooInOutVO);

  /**
   * 
   * @throws Exception 
   * app待办办公用品出入库完成任务
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年2月2日 下午5:02:09
   */
  public RetMsg completeAppTask(Map<String, Object> variables, String taskId, String bizId,
      String userId, String message, AutUser autUser) throws Exception;
}
