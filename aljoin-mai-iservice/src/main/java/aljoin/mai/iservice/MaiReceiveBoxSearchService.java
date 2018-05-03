package aljoin.mai.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * @描述：收件箱表(服务类).
 * 
 * @作者：zhongjy
 * 
 * @时间: 2018-03-30
 */
public interface MaiReceiveBoxSearchService extends IService<MaiReceiveBoxSearch> {

  /**
   * 
   * @描述：收件箱表(分页列表).
   *
   * @返回：Page<MaiReceiveBoxSearch>
   *
   * @作者：zhongjy
   *
   * @时间：2018-03-30
   */
	public Page<MaiReceiveBoxSearch> list(PageBean pageBean, MaiReceiveBoxSearch obj) throws Exception; 	
	
  /**
   * 
   * @描述：根据ID删除对象(物理删除)
   *
   * @返回：void
   *
   * @作者：zhongjy
   *
   * @时间：2018-03-30
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * @描述：复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @返回：void
   *
   * @作者：zhongjy
   *
   * @时间：2018-03-30
   */
  public void copyObject(MaiReceiveBoxSearch obj) throws Exception;
}
