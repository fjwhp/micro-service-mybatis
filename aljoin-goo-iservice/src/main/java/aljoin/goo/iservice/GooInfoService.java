package aljoin.goo.iservice;

import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.goo.dao.entity.GooInfo;
import aljoin.goo.dao.object.GooInfoDO;
import aljoin.goo.dao.object.GooInfoVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 办公用品信息表(服务类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-04
 */
public interface GooInfoService extends IService<GooInfo> {

  /**
   * 
   * 办公用品信息表(分页列表).
   *
   * @return：Page<GooInfo>
   *
   * @author：xuc
   *
   * @date：2018-01-04
   */
	public Page<GooInfoDO> list(PageBean pageBean, GooInfoVO obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：xuc
   *
   * @date：2018-01-04
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
   * @date：2018-01-04
   */
  public void copyObject(GooInfo obj) throws Exception;

  /**
   * 
   * 新增物品
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月4日 上午10:16:00
   */
  public RetMsg add(GooInfo obj);

  /**
   * 
   * @throws Exception 
 * 办公用品详情
   *
   * @return：GooInfo
   *
   * @author：xuc
   *
   * @date：2018年1月4日 上午11:26:24
   */
  public GooInfoVO getById(Long id) throws Exception;

  /**
   * 
   * 更改办公用品
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月4日 下午2:52:21
   */
  public RetMsg update(GooInfo obj);

  /**
   * 
   * @throws Exception 
   * 导出办公用品列表（盘点）
   *
   * @return：void
   *
   * @author：xuc
   *
   * @date：2018年1月15日 下午4:12:55
   */
  public void export(HttpServletResponse response, GooInfoVO obj) throws Exception;
}
