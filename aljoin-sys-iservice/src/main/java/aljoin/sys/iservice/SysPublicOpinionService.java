package aljoin.sys.iservice;

import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysPublicOpinion;
import aljoin.sys.dao.object.SysPublicOpinionVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * @描述：公共意见表(服务类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-14
 */
public interface SysPublicOpinionService extends IService<SysPublicOpinion> {

	
  /**
   * 
   * @描述：根据ID删除对象(物理删除)
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-14
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * @描述：复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-14
   */
  public void copyObject(SysPublicOpinion obj) throws Exception;
  
  /**
   * 
   * @描述：新增公共意见
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-14
   */
  public RetMsg add(SysPublicOpinion obj) throws Exception;
  
  /**
   * 
   * @描述：修改公共意见
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-14
   */
  public RetMsg update(SysPublicOpinion obj);
  
  /**
   * 
   * @描述：公共意见批量删除
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-14
   */
  public RetMsg deleteByIds(SysPublicOpinionVO obj);
  
  /**
   * 
   * @描述：公共意见删除
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-14
   */
  public RetMsg delete(SysPublicOpinion obj);
  
  
}
