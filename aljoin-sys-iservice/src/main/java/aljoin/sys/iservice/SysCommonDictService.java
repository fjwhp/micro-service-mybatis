package aljoin.sys.iservice;

import java.util.List;


import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysCommonDict;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * @描述：常用字典表(服务类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-18
 */
public interface SysCommonDictService extends IService<SysCommonDict> {

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
  
  /**
   * 
   * @描述：新增常用字典内容
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public RetMsg add(SysCommonDict obj, String[] dictContent, Integer[] dictContentRank);
  
  /**
   * 
   * @描述：修改常用字典内容
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public RetMsg update(SysCommonDict obj, String[] dictContent, Integer[] dictContentRank, String[] commonDictId);
  
  /**
   * 
   * @描述：根据字典码删除对象
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public RetMsg deleteByDictCode(SysCommonDict obj);

  /**
   * 
   * @描述：常用字典表(根据分类获取该分类下的字典名称).
   *
   * @return：List<SysCommonDict>
   *
   * @author：caizx
   *
   * @date：2018-03-21
   */
  public List<SysCommonDict> getListByCategory(SysCommonDict obj);
  
  /**
   * 
   * @描述：常用字典表(获取常用字典内容).
   *
   * @return：List<SysCommonDict>
   *
   * @author：caizx
   *
   * @date：2018-03-21
   */
  public List<SysCommonDict> getListByDictCode(SysCommonDict obj);

  
}
