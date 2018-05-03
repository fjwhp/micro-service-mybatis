package aljoin.veh.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.veh.dao.entity.VehMaintain;
import aljoin.veh.dao.object.VehMaintainVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 车船维护信息表(服务类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-08
 */
public interface VehMaintainService extends IService<VehMaintain> {

  /**
   * 
   * 车船维护信息表(分页列表).
   *
   * @return：Page<VehMaintain>
   *
   * @author：xuc
   *
   * @date：2018-01-08
   */
	public Page<VehMaintain> list(PageBean pageBean, VehMaintainVO obj) throws Exception; 	
	
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
  public void copyObject(VehMaintain obj) throws Exception;

  /**
   *  
   * 新增维护信息
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月8日 下午2:15:36
   */
  public RetMsg add(VehMaintain obj);

  /**
   * 
   * 维修记录修改
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月9日 上午11:20:12
   */
  public RetMsg update(VehMaintain obj);
}
