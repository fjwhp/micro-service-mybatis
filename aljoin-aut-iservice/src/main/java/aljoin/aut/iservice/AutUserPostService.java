package aljoin.aut.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.aut.dao.entity.AutUserPost;
import aljoin.aut.dao.object.AutUserPostVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * @描述：用户-岗位表(服务类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-04-09
 */
public interface AutUserPostService extends IService<AutUserPost> {

  /**
   * 
   * @描述：用户-岗位表(分页列表).
   *
   * @返回：Page<AutUserPost>
   *
   * @作者：caizx
   *
   * @时间：2018-04-09
   */
	public Page<AutUserPost> list(PageBean pageBean, AutUserPost obj) throws Exception; 	
	
  /**
   * 
   * @描述：根据ID删除对象(物理删除)
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-04-09
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
   * @时间：2018-04-09
   */
  public void copyObject(AutUserPost obj) throws Exception;
  
  /**
   * 
   * @描述：新增用户岗位
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-04-09
   */
  public RetMsg add(AutUserPostVO obj);
  
}
