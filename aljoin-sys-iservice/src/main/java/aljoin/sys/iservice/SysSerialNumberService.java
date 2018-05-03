package aljoin.sys.iservice;

import java.util.List;

import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysSerialNumber;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * @描述：流水号表(服务类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-23
 */
public interface SysSerialNumberService extends IService<SysSerialNumber> {
	
	
  /**
   * 
   * @描述：根据ID删除对象(物理删除)
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
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
   * @时间：2018-03-23
   */
  public void copyObject(SysSerialNumber obj) throws Exception;
  
  /**
   * 
   * @描述：新增流水号
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public RetMsg add(SysSerialNumber obj);
  
  /**
   * 
   * @描述：1月1日 00:00重置流水号
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public void resetByYear();
  
  /**
   * 
   * @描述：每月1日 00:00重置流水号
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public void resetByMonth();
  
  /**
   * 
   * @描述：修改流水号
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public RetMsg update(SysSerialNumber obj);
  
  /**
   * 
   * @描述：修改流水号状态
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public RetMsg updateStatus(SysSerialNumber obj);
  
  /**
   * 
   * @描述：删除流水号
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public RetMsg delete(SysSerialNumber obj);
  
  /**
   * 
   * @描述：修改流水号当前值
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public RetMsg updateCurrentValue(SysSerialNumber obj);
  
  /**
   * 
   * @描述：获取流水号
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public RetMsg getSerialNumber(SysSerialNumber obj);
  
  /**
   * 
   * @描述：流水号绑定流程
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public void bindBpmn(String processName, List<String> serialNumIds);
  
  /**
   * 
   * @描述：流水号绑定流程
   *
   * @返回：void
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public void unBindBpmn(String bpmnId);
  
  /**
   * 
   * @描述：根据id获取流水号列表
   *
   * @返回：List<SysSerialNumber>
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public List<SysSerialNumber> getList(SysSerialNumber obj);
  
  /**
   * 
   * @描述：获得预览流水号
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public RetMsg getPreviewSerialNumber(SysSerialNumber obj);
  
  /**
   * 
   * @描述：根据规则生成流水号
   *
   * @返回：RetMsg
   *
   * @作者：caizx
   *
   * @时间：2018-03-23
   */
  public String getSerialNum(SysSerialNumber sysSerialNumber);

}
