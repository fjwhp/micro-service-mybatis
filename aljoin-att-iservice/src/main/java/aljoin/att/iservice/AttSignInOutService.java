package aljoin.att.iservice;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.att.dao.entity.AttSignInOut;
import aljoin.att.dao.object.AppAttSignInOutHisVO;
import aljoin.att.dao.object.AttSignInCount;
import aljoin.att.dao.object.AttSignInOutHisVO;
import aljoin.att.dao.object.AttSignInOutVO;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 签到、退表(服务类).
 * @author：wangj
 * @date： 2017-09-27
 */
public interface AttSignInOutService extends IService<AttSignInOut> {

  /**
   * 签到、退表(分页列表).
   * @return：Page<AttSignInOut>
   * @author：wangj
   * @date：2017-09-27
   */
  public Page<AttSignInOut> list(PageBean pageBean, AttSignInOutVO obj) throws Exception;

  /**
   * 签到、退表(定时新增).
   * @return：RetMsg
   * @author：wangj
   * @date：2017-09-27
   */
  public RetMsg add() throws Exception;

  /**
   * 删除上个月的考勤记录.
   * @return：void
   * @author：wangj
   * @date：2017-09-28
   */
  public void deleteLastSignInOut() throws Exception;

  /**
   * 推送考勤打卡消息.
   * @return：RetMsg
   * @author：wangj
   * @date：2017-09-28
   */
  public RetMsg pushMessageToUserList(Long userId) throws Exception;

  /**
   * 签到统计
   * @return：List<AttSignInCount>
   * @author：laijy
   * @date：2017年9月28日 上午8:46:12
   */
  public List<AttSignInCount> getAttSignInCountList(AttSignInCount obj) throws Exception;

  /**
   * 签到详细列表.
   * @return：AttSignInOutVO
   * @author：wangj
   * @date：2017-09-28
   */
  public AttSignInOutVO signInDetailList(AttSignInOutVO obj) throws Exception;
  
  /**
   * 
   * 签到签退详细分页列表
   *
   * @return：Page<AttSignInOutVO>
   *
   * @author：huanghz
   *
   * @date：2018年1月11日 下午5:30:08
   */
  public Page<AttSignInOutVO> signInDetailListPage(PageBean pageBean,AttSignInOutVO obj) throws Exception;
  
  /**
   * 根据日期获取是否是工作日(预留接口).
   * @return：boolean
   * @author：wangj
   * @date：2017-09-29
   */
  public boolean getIsWorkDay(Date date) throws Exception;

  /**
   * List排序
   * @return：void
   * @author：laijy
   * @date：2017年10月9日 下午6:11:44
   */
  public void sortList(List<AttSignInCount> list, AttSignInCount obj);

  /**
   * 导出Excel
   * @return：void
   * @author：laijy
   * @date：2017年10月12日 上午8:56:51
   */
  public void export(HttpServletResponse response, AttSignInCount obj) throws Exception;

  /**
   * 个人签到、退详细列表
   * @return：AttSignInOutVO
   * @author：wangj
   * @date：2017-09-28
   */
  public AttSignInOutVO personalSignInList(AttSignInOutVO obj) throws Exception;

  /**
   * 获得登录用户信息
   * @return：AttSignInCount
   * @author：laijy
   * @date：2017年10月24日 下午8:58:18
   */
  public AttSignInCount getUserInfo(Long customUserId) throws Exception;

  /**
   * 确认打卡
   * @return：RetMsg
   * @author：wangj
   * @date：2017-09-28
   */
  public RetMsg confirmSign(Long customUserId, HttpServletRequest request) throws Exception;

  /**
   * 首页返回签到或者签退按钮
   * @return：RetMsg
   * @author：wangj
   * @date：2017-09-28
   */
  public RetMsg getSignInOutStr(Long customUserId, HttpServletRequest request) throws Exception;

  /**
   * 获得补签申请人信息(姓名、岗位、申请时间)
   * @return：AttSignInCount
   * @author：laijy
   * @date：2017年10月27日 下午2:18:12
   */
  public AttSignInCount getSignPathUserInfo(Long customUserId) throws Exception;

  /**
   * 检查当前节点的下一个节点办理人是 个人、多个人、部门、岗位
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-06
   */
//	public RetMsg checkNextTaskInfo(String taskId) throws Exception;
  public RetMsg checkNextTaskInfo(String taskId, String nextStept) throws Exception;

  /**
   * 获取所有节点信息
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-06
   */
  public List<FixedFormProcessLog> getAllTaskInfo(String taskId, String processInstanceId) throws Exception;

  /**
   * 流程审批.挪webService里去了
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-09
   */
//	public RetMsg completeTask(Map<String, Object> variables, String taskId, String bizId, String userId,
//			String message) throws Exception;
//	public RetMsg completeTask(Map<String, Object> variables, String taskId, String bizId, String userId, String message,
//			AutUser createUser) throws Exception;

  /**
   * 回退到上一个节点.挪webService里去了
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-09
   */
//  public RetMsg jump2Task2(String taskId, String bizId, String message, Long createUserId) throws Exception;

  /**
   * 获得补签信息列表
   * @return：List<AttSignInOut>
   * @author：wangj
   * @date：2017年11月09日
   */
  public RetMsg getSignInOutHisPatchList(AttSignInOutHisVO obj) throws Exception;

  /**
   * 流程作废
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-20
   */
  public RetMsg toVoid(String taskId, String bizId, Long userId) throws Exception;

  /**
   * 
   * @return：
   * @author：huangw
   * @date：2017-11-22
   */
  public List<AttSignInCount> getLAttSignInCountList(AttSignInCount obj) throws Exception;

  public List<AttSignInCount> getLAttSignInCount(AttSignInCount obj) throws Exception;

  /**
   * 签到、退表(新增用户时调用).
   * @return：RetMsg
   * @author：wangj
   * @date：2017-09-27
   */
  public RetMsg addSign(Long userId) throws Exception;

  /**
   * 获取下一个任务节点信息（分支节点）
   * @return：RetMsg
   * @author：sln
   * @date：2017-11-07
   */
  public RetMsg getNextTaskInfo2(String taskId, String nextStept) throws Exception;

  /**
   * 查看历史数据
   * @return：List<AttSignInOutHisVO>
   * @author：huangw
   * @date：2017-12-15
   */
  public List<AttSignInOutHisVO> getSignInOutHisDatePatchList(AttSignInOutHisVO obj) throws Exception;

  public List<AttSignInCount> getAttSignInHisCountList(AttSignInCount obj);

  /**
   * App考勤补签详情
   * @return：RetMsg
   * @author：wangj
   * @date：2017-12-21
   */
  public RetMsg getAppSignInOutHisPatchList(AppAttSignInOutHisVO obj) throws Exception;

  /**
   * App检查当前节点的下一个节点办理人是 个人、多个人、部门、岗位
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-06
   */
  public RetMsg checkAppNextTaskInfo(String taskId,Long currentUserId) throws Exception;

  /**
   * app流程审批.挪webService里去了
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-09
   */
//  public RetMsg completeAppTask(Map<String, Object> variables, String taskId, String bizId, String userId, String message,
//                             AutUser autUser) throws Exception;

}
