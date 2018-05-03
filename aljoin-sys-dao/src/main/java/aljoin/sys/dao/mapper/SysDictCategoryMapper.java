package aljoin.sys.dao.mapper;

import aljoin.sys.dao.entity.SysDictCategory;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * @描述：数据字典分类表(Mapper接口).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-18
 */
public interface SysDictCategoryMapper extends BaseMapper<SysDictCategory> {
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
}