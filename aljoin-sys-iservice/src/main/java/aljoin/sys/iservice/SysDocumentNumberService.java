package aljoin.sys.iservice;

import java.util.List;

import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysDocumentNumber;
import aljoin.sys.dao.object.SysDocumentNumberVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * @描述：公文文号表(服务类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-26
 */
public interface SysDocumentNumberService extends IService<SysDocumentNumber> {

	
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
  
  /**
   * 
   * @描述：新增文号
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-26
   */
  public RetMsg add(SysDocumentNumberVO obj);
  
  /**
   * 
   * @描述：按年重置
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-26
   */
  public void resetByYear();
  
  /**
   * 
   * @描述：修改文号
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-26
   */
  public RetMsg update(SysDocumentNumberVO obj);
  

  /**
   * 
   * @描述：修改当前值
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-26
   */
  public RetMsg updateCurrentValue(SysDocumentNumber obj);

  /**
   * 
   * @描述：删除文号
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-26
   */
  public RetMsg delete(SysDocumentNumber obj);
  
  /**
   * 
   * @描述：修改文号状态
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-26
   */
  public RetMsg updateStatus(SysDocumentNumber obj);


  /**
   * 
   * @描述：获取文号
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public RetMsg getDocumentNumber(SysDocumentNumber obj);
  
  /**
   * 
   * @描述：文号绑定流程
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public void bindBpmn(String processName, List<String> documentNumIds);
  
  /**
   * 
   * @描述：文号解绑流程
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public void unBindBpmn(String processName);
  
  /**
   * 
   * @描述：获取文号列表
   *
   * @返回：List<SysDocumentNumber>
   *
   * @作者：caizx
   *
   * @时间：2018-03-18
   */
  public List<SysDocumentNumber> getList(SysDocumentNumber obj);
  
  /**
   * 
   * @描述：获得预览文号
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public RetMsg getPreviewDocumentNumber(SysDocumentNumber obj);
  
  /**
   * 
   * @描述：根据规则生成文号
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public String getDocumentNum(SysDocumentNumber sysDocumentNumber);
}
