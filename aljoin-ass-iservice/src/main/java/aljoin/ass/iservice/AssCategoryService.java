package aljoin.ass.iservice;

import java.util.List;

import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.ass.dao.entity.AssCategory;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 固定资产分类表(服务类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-09
 */
public interface AssCategoryService extends IService<AssCategory> {

  /**
   * 
   * 固定资产分类表(分页列表).
   *
   * @return：Page<AssCategory>
   *
   * @author：xuc
   *
   * @date：2018-01-09
   */
	public List<AssCategory> list(PageBean pageBean, AssCategory obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：xuc
   *
   * @date：2018-01-09
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
   * @date：2018-01-09
   */
  public void copyObject(AssCategory obj) throws Exception;

  /**
   * 
   * 新增固定资产分类
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月10日 下午1:21:35
   */
  public RetMsg add(AssCategory obj);

  /**
   * 
   * 修改固定资产分类
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月10日 下午1:23:31
   */
  public RetMsg update(AssCategory obj);

  /**
   * 
   * 固定资产分类详情
   *
   * @return：AssCategory
   *
   * @author：xuc
   *
   * @date：2018年1月10日 下午1:24:58
   */
  public AssCategory getById(Long id);
  
  /**
   * 
   * 根据父ID获取子分类
   *
   * @return：List<ActAljoinCategory>
   *
   * @author：xuc
   *
   * @date：2018年1月22日 上午10:11:27
   */
  public List<AssCategory> getAllParentCategoryList(Long categoryid) throws Exception;
  
  /**
   * 
   * 根据ID获取所有子分类
   *
   * @return：List<ActAljoinCategory>
   *
   * @author：xuc
   *
   * @date：2018年1月22日 上午10:11:27
   */
  public List<AssCategory> getAllChildList(Long categoryid) throws Exception;
}
