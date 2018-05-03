package aljoin.sys.dao.mapper;

import aljoin.sys.dao.entity.SysDocumentNumber;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * @描述：公文文号表(Mapper接口).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-26
 */
public interface SysDocumentNumberMapper extends BaseMapper<SysDocumentNumber> {
  /**
   * 
   * @描述：根据ID删除对象(物理删除)
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-26
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
   * @时间：2018-03-26
   */
  public void copyObject(SysDocumentNumber obj) throws Exception;
}