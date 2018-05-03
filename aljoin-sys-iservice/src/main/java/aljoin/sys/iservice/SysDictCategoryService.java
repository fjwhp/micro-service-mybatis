package aljoin.sys.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysDictCategory;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * @描述：数据字典分类表(服务类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-18
 */
public interface SysDictCategoryService extends IService<SysDictCategory> {

  /**
   * 
   * @描述：数据字典分类表(分页列表).
   *
   * @返回：Page<SysDictCategory>
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
	public Page<SysDictCategory> list(PageBean pageBean, SysDictCategory obj) throws Exception; 	
	
  /**
   * 
   * @描述：根据ID删除对象(物理删除)
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * @描述：复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public void copyObject(SysDictCategory obj) throws Exception;
  
  /**
   * 
   * @描述：获取数据字典分类列表
   *
   * @返回：List<SysDictCategory>
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public List<SysDictCategory> getCategoryList(SysDictCategory obj);
  
  /**
   * 
   * @描述：数据字典分类表(新增分类)
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public RetMsg add(SysDictCategory obj);
  
  /**
   * 
   * @描述：验证分类是否存在
   *
   * @返回：Boolean(true:表单已经存在 false:表单不存在可以新增)
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public boolean validCategoryName(SysDictCategory obj, boolean isAdd);
  
  /**
   * 
   * @描述：修改分类
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public RetMsg update(SysDictCategory obj);
  
  /**
   * 
   * @描述：删除分类
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public RetMsg delete(SysDictCategory obj);
}
