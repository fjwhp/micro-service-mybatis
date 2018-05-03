package aljoin.sma.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sma.dao.entity.SysMsgModuleCategory;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 消息模板分类表(服务类).
 * 
 * @author：huangw
 * 
 * @date： 2017-11-14
 */
public interface SysMsgModuleCategoryService extends IService<SysMsgModuleCategory> {

  /**
   * 
   * 消息模板分类表(分页列表).
   *
   * @return：Page<SysMsgModuleCategory>
   *
   * @author：huangw
   *
   * @date：2017-11-14
   */
	public Page<SysMsgModuleCategory> list(PageBean pageBean, SysMsgModuleCategory obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：huangw
   *
   * @date：2017-11-14
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
   * @date：2017-11-14
   */
  public void copyObject(SysMsgModuleCategory obj) throws Exception;
  /**
   * 
   * 获取分类下数据
   *
   * @return：RetMsg
   *
   * @author：huangw
   *
   * @date：2017-11-14
   */
  public  RetMsg  classification(String gname)throws Exception;
}
