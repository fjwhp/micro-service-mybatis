package aljoin.sma.iservice; 

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.sma.dao.entity.LeaStatisticsModule;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 领导看板统计模块显示排序(服务类). 
 * 
 * @author：huangw
 * 
 * @date： 2017-12-25
 */
public interface LeaStatisticsModuleService extends IService<LeaStatisticsModule> {

  /**
   * 
   * 领导看板统计模块显示排序(分页列表).
   *
   * @return：Page<LeaStatisticsModule>
   *
   * @author：huangw
   *
   * @date：2017-12-25
   */
	public Page<LeaStatisticsModule> list(PageBean pageBean, LeaStatisticsModule obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：huangw
   *
   * @date：2017-12-25
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * 复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @return：void
   *
   * @author：huangw
   *
   * @date：2017-12-25
   */
  public void copyObject(LeaStatisticsModule obj) throws Exception;
}
