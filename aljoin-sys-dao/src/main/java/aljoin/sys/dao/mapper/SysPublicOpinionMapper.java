package aljoin.sys.dao.mapper;

import aljoin.sys.dao.entity.SysPublicOpinion;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * @描述：公共意见表(Mapper接口).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-14
 */
public interface SysPublicOpinionMapper extends BaseMapper<SysPublicOpinion> {
  /**
   * 
   * @描述：根据ID删除对象(物理删除)
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-14
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
   * @时间：2018-03-14
   */
  public void copyObject(SysPublicOpinion obj) throws Exception;
}