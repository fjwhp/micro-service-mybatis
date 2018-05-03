package aljoin.sys.dao.mapper;

import aljoin.sys.dao.entity.SysCommonDict;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * @描述：常用字典表(Mapper接口).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-18
 */
public interface SysCommonDictMapper extends BaseMapper<SysCommonDict> {
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
  public void copyObject(SysCommonDict obj) throws Exception;
}