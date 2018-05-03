package aljoin.aut.dao.mapper;

import aljoin.aut.dao.entity.AutPost;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * @描述：岗位表(Mapper接口).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-04-09
 */
public interface AutPostMapper extends BaseMapper<AutPost> {
  /**
   * 
   * @描述：根据ID删除对象(物理删除)
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-04-09
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
   * @时间：2018-04-09
   */
  public void copyObject(AutPost obj) throws Exception;
}