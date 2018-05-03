package aljoin.aut.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.aut.dao.entity.AutPost;
import aljoin.aut.dao.object.AutPostVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * @描述：岗位表(服务类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-04-09
 */
public interface AutPostService extends IService<AutPost> {

  /**
   * 
   * @描述：岗位表(分页列表).
   *
   * @返回：Page<AutPost>
   *
   * @作者：caizx
   *
   * @时间：2018-04-09
   */
	public Page<AutPost> list(PageBean pageBean, AutPost obj) throws Exception; 	
	
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
  public void copyObject(AutPost obj) throws Exception;
  
  /**
   * 
   * @描述：岗位表(获得用户-岗位列表).
   *
   * @返回：List<AutPostVO>
   *
   * @作者：caizx
   *
   * @时间：2018-04-09
   */
  public List<AutPostVO> getUserPostList();
  
  /**
   * 
   * @描述：删除岗位.
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-04-09
   */
  public RetMsg delete(AutPost obj);
}
