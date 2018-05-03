package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActAljoinBpmnRun;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * @描述：(Mapper接口).
 * 
 * @作者：zhongjy
 * 
 * @时间: 2018-03-13
 */
public interface ActAljoinBpmnRunMapper extends BaseMapper<ActAljoinBpmnRun> {
  /**
   * 
   * @描述：根据ID删除对象(物理删除)
   *
   * @返回：void
   *
   * @作者：zhongjy
   *
   * @时间：2018-03-13
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * @描述：复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @返回：void
   *
   * @作者：zhongjy
   *
   * @时间：2018-03-13
   */
  public void copyObject(ActAljoinBpmnRun obj) throws Exception;
}