package aljoin.off.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.off.dao.entity.OffSchedulingHis;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 日程安排表(历史表)(服务类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-01
 */
public interface OffSchedulingHisService extends IService<OffSchedulingHis> {

  /**
   * 
   * 日程安排表(历史表)(分页列表).
   *
   * @return：Page<OffSchedulingHis>
   *
   * @author：zhongjy
   *
   * @date：2017-11-01
   */
	public Page<OffSchedulingHis> list(PageBean pageBean, OffSchedulingHis obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：zhongjy
   *
   * @date：2017-11-01
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
   * @date：2017-11-01
   */
  public void copyObject(OffSchedulingHis obj) throws Exception;
}
