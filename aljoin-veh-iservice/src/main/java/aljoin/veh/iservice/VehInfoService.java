package aljoin.veh.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.veh.dao.entity.VehInfo;
import aljoin.veh.dao.object.VehInfoDO;
import aljoin.veh.dao.object.VehInfoVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 车船信息表(服务类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-08
 */
public interface VehInfoService extends IService<VehInfo> {

  /**
   * 
   * 车船信息表(分页列表).
   *
   * @return：Page<VehInfo>
   *
   * @author：xuc
   *
   * @date：2018-01-08
   */
	public Page<VehInfoDO> list(PageBean pageBean, VehInfo obj) throws Exception; 	
	
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
  public void copyObject(VehInfo obj) throws Exception;

  /**
   * 
   * 新增车辆信息
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月8日 上午9:11:24
   */
  public RetMsg add(VehInfoVO obj);

  /**
   * 
   * @throws Exception 
   * 车船信息详情
   *
   * @return：VehInfo
   *
   * @author：xuc
   *
   * @date：2018年1月8日 下午2:00:38
   */
  public VehInfo getById(Long id) throws Exception;

  /**
   * 
   * 车船信息修改
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月9日 上午10:35:44
   */
  public RetMsg update(VehInfoVO obj) throws Exception;

  public Page<VehInfoDO> allList(PageBean pageBean, VehInfo obj) throws Exception;

  /**
   * 
   * 批量删除车船信息
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月15日 上午11:47:20
   */
  public RetMsg deleteByIds(VehInfoVO obj) throws Exception;

  /**
   * 
   * 获取所有车船的牌号
   *
   * @return：List<String>
   *
   * @author：xuc
   *
   * @date：2018年1月18日 上午11:30:57
   */
  public List<String> allCode();

  /**
   * 
   * @param string 
   * 通过牌号查车辆
   *
   * @return：VehInfo
   *
   * @author：xuc
   *
   * @date：2018年1月18日 下午1:43:46
   */
  public VehInfo getCarByCode(String string);
}
