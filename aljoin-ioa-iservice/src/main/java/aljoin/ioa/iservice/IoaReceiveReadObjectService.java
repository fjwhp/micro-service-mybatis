package aljoin.ioa.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.ioa.dao.entity.IoaReceiveReadObject;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 收文阅件-传阅对象表(分类)(服务类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-08
 */
public interface IoaReceiveReadObjectService extends IService<IoaReceiveReadObject> {

  /**
   * 
   * 收文阅件-传阅对象表(分类)(分页列表).
   *
   * @return：Page<IoaReceiveReadObject>
   *
   * @author：zhongjy
   *
   * @date：2017-11-08
   */
	public Page<IoaReceiveReadObject> list(PageBean pageBean, IoaReceiveReadObject obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：zhongjy
   *
   * @date：2017-11-08
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * 复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @return：void
   *
   * @author：zhongjy
   *
   * @date：2017-11-08
   */
  public void copyObject(IoaReceiveReadObject obj) throws Exception;
}
