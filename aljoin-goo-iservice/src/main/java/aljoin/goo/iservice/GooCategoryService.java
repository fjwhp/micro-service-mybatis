package aljoin.goo.iservice;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;

import aljoin.goo.dao.entity.GooCategory;
import aljoin.object.RetMsg;

/**
 * 
 * 办公用品分类表(服务类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-03
 */
public interface GooCategoryService extends IService<GooCategory> {

  /**
   * 
   * 办公用品分类表(分页列表).
   *
   * @return：List<GooCategory>
   *
   * @author：xuc
   *
   * @date：2018-01-03
   */
	public List<GooCategory> list() throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：xuc
   *
   * @date：2018-01-03
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
   * @date：2018-01-03
   */
  public void copyObject(GooCategory obj) throws Exception;

  /**
   * 
   * 新增分类
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月3日 下午1:32:35
   */
  public RetMsg add(GooCategory obj);

  /**
   * 
   * 编辑分类
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月3日 下午1:40:54
   */
  public RetMsg update(GooCategory obj);

  /**
   * 
   * 通过id查找
   *
   * @return：GooCategory
   *
   * @author：xuc
   *
   * @date：2018年1月3日 下午6:36:01
   */
  public GooCategory getById(Long id);
  
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
  public List<GooCategory> getAllParentCategoryList(Long categoryid) throws Exception;
  
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
  public List<GooCategory> getAllChildList(Long categoryid) throws Exception;
}
