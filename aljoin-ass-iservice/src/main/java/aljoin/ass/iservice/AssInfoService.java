package aljoin.ass.iservice;

import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.ass.dao.entity.AssInfo;
import aljoin.ass.dao.object.AssInfoVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 固定资产信息表(服务类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-11
 */
public interface AssInfoService extends IService<AssInfo> {

  /**
   * 
   * 固定资产信息表(分页列表).
   *
   * @return：Page<AssInfo>
   *
   * @author：xuc
   *
   * @date：2018-01-11
   */
	public Page<AssInfoVO> list(PageBean pageBean, AssInfo obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：xuc
   *
   * @date：2018-01-11
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
   * @date：2018-01-11
   */
  public void copyObject(AssInfo obj) throws Exception;

  /**
   * 
   * 新增固定资产
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月11日 上午11:22:04
   */
  public RetMsg add(AssInfo obj);

  /**
   * 
   * 修改固定资产信息
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月19日 上午9:49:10
   */
  public RetMsg update(AssInfo obj);

  /**
   * 
   * @throws Exception 
   * 固定资产盘点
   *
   * @return：void
   *
   * @author：xuc
   *
   * @date：2018年1月22日 上午8:56:57
   */
  public void export(HttpServletResponse response, AssInfoVO obj) throws Exception;

  /**
   * 
   * @throws Exception 
 * 固定资产详情
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月19日 上午9:49:10
   */
public AssInfoVO getById(Long id) throws Exception;
}
