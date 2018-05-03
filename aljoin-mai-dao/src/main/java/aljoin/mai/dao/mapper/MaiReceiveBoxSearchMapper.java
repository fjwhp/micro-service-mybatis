package aljoin.mai.dao.mapper;

import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * @描述：收件箱表(Mapper接口).
 * 
 * @作者：zhongjy
 * 
 * @时间: 2018-03-30
 */
public interface MaiReceiveBoxSearchMapper extends BaseMapper<MaiReceiveBoxSearch> {
  /**
   * 
   * @描述：根据ID删除对象(物理删除)
   *
   * @返回：void
   *
   * @作者：zhongjy
   *
   * @时间：2018-03-30
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
   * @时间：2018-03-30
   */
  public void copyObject(MaiReceiveBoxSearch obj) throws Exception;
}