package aljoin.ioa.iservice;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.object.ActHolidayListVO;
import aljoin.act.dao.object.ActHolidayVO;
import aljoin.ioa.dao.object.ActApprovalVO;
import aljoin.ioa.dao.object.ActRegulationListVO;
import aljoin.ioa.dao.object.ActRegulationVO;
import aljoin.ioa.dao.object.ActWorkingListVO;
import aljoin.ioa.dao.object.ActWorkingVO;
import aljoin.ioa.dao.object.WaitTaskShowVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 流转监控(服务类).
 * 
 * @author：pengsp
 * 
 * @date： 2017-10-19
 */
public interface IoaMonitorWorkService {

  /**
   * 
   * 综合查询
   *
   * @return：Page<WaitTaskShowVO>
   *
   * @author：pengsp
   *
   * @date：2017-10-19
   */
  public Page<WaitTaskShowVO> list(PageBean pageBean, Map<String, Object> map) throws Exception;

  /**
   * 
   * 催办任务
   *
   * @return：RetMsg
   *
   * @author：huangw
   *
   * @date：2017-1
   */
  public RetMsg selectUrgent(Long customUserId,String customUserName,String customNickName, String ids, String msgType) throws Exception;

  /**
   * 
   * 获取领导看板请假程变量
   *
   * @return：
   *
   * @author：huangw
   *
   * @date：2017年11月23日 下午1:45:33
   */
  public List<ActHolidayListVO> getHolidayList(ActHolidayVO obj, PageBean pageBean)
      throws Exception;

  /**
   * 
   * 获取领导看板
   *
   * @return：
   *
   * @author：huangw
   *
   * @date：2017年11月23日 下午1:45:33
   */
  public ActHolidayListVO getHoliday(ActHolidayVO obj) throws Exception;

  /**
   * 
   * 获取工作流统计
   *
   * @return：
   *
   * @author：huangw
   *
   * @date：2017年11月23日 下午1:45:33
   */
  public List<ActWorkingVO> getWorkingList(ActWorkingListVO obj) throws Exception;

  /**
   * 
   * 获取工作流统计
   *
   * @return：
   *
   * @author：huangw
   *
   * @date：2017年11月23日 下午1:45:33
   */
  public List<ActWorkingVO> getWorking(ActWorkingListVO obj) throws Exception;

  /**
   *
   * 工作流统计首页
   *
   * @return：void
   *
   * @author：huangw
   *
   * @date：2017年11月24日
   */
  public List<ActApprovalVO> getApproval(ActWorkingListVO obj) throws Exception;

  /**
   *
   * 工作流统计分页
   *
   * @return：void
   *
   * @author：huangw
   *
   * @date：2017年11月24日
   */
  public List<ActApprovalVO> getApprovalList(ActWorkingListVO obj) throws Exception;

  /**
   *
   * 工作监管首页
   *
   * @return：void
   *
   * @author：huangw
   *
   * @date：2017年11月23日
   */
  public List<ActRegulationVO> getRegulation(ActRegulationListVO obj) throws Exception;

  /**
   *
   * 工作监管分页
   *
   * @return：void
   *
   * @author：huangw
   *
   * @date：2017年11月23日
   */
  public List<ActRegulationVO> getRegulationList(ActRegulationListVO obj, PageBean pageBean)
      throws Exception;
  /**
  *
  * 工作监管批量作废
  *
  * @return：void
  *
  * @author：huangw
  *
  * @date：2017年12月15日
  */
public RetMsg selectInvalid(Long customUserId, String invalid)    throws Exception;

  /**
   * 特送人员(支持批量特送)
   *
   * @param taskKey            任务key
   *
   * @param processInstanceId  流程实例ID
   *
   * @param assignees           特送人员（多个用分号分隔）
   *
   * @return RetMsg
   *
   * @throws Exception
   */
  public RetMsg deliveryPersonnel(String taskKey,String processInstanceId,String assignees) throws Exception;

}
