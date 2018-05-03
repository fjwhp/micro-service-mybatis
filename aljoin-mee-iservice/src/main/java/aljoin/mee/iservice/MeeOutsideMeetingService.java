package aljoin.mee.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mee.dao.entity.MeeOutsideMeeting;
import aljoin.mee.dao.object.AppMeeOutsideMeetingVO;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.mee.dao.object.MeeLeaderMeetingDO;
import aljoin.mee.dao.object.MeeMeetingRoomCountDO;
import aljoin.mee.dao.object.MeeOutsideMeetingDO;
import aljoin.mee.dao.object.MeeOutsideMeetingVO;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sms.dao.entity.SmsShortMessage;

/**
 * 外部会议表(服务类).
 * @author：wangj
 * @date： 2017-10-12
 */
public interface MeeOutsideMeetingService extends IService<MeeOutsideMeeting> {

  /**
   * 外部会议表(分页列表).
   * @return：Page<MeeOutsideMeeting>
   * @author：wangj
   * @date：2017-10-12
   */
  public Page<MeeOutsideMeetingDO> list(PageBean pageBean, MeeOutsideMeetingVO obj) throws Exception;

  /**
   * 根据ID删除对象(物理删除)
   * @return：void
   * @author：wangj
   * @date：2017-10-12
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 复制对象(需要完整的对象数据，包括所有的公共字段)
   * @return：void
   * @author：wangj
   * @date：2017-10-12
   */
  public void copyObject(MeeOutsideMeeting obj) throws Exception;

  /**
   *
   * 外部会议新增
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017-10-12
   */
  // public RetMsg add(MeeOutsideMeetingVO obj) throws Exception;

  /**
   *  外部会议详情
   * @return： RetMsg
   * @author：wangj
   * @date：2017-10-13
   */
  public MeeOutsideMeetingVO detail(MeeOutsideMeeting obj) throws Exception;

  /**
   * 外部会议表(分页列表).
   * @return：Page<MeeOutsideMeeting>
   * @author：wangj
   * @date：2017-10-12
   */
  public Page<MeeOutsideMeetingDO> personList(PageBean pageBean, MeeOutsideMeetingVO obj) throws Exception;

  /**
   * 外部会议申请管理 审核中分页列表.
   * @return：Page<MeeOutsideMeetingDO>
   * @author：wangj
   * @date：2017-10-12
   */
  public Page<MeeOutsideMeetingDO> auditlist(PageBean pageBean, MeeOutsideMeetingVO obj) throws Exception;

  /**
   * 外部会议申请管理 审核通过分页列表.
   * @return：Page<MeeOutsideMeetingDO>
   * @author：wangj
   * @date：2017-10-12
   */
  public Page<MeeOutsideMeetingDO> auditPassList(PageBean pageBean, MeeOutsideMeetingVO obj) throws Exception;

  /**
   *
   * 外部会议变更
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017-10-12
   */
  // public RetMsg update(MeeOutsideMeetingVO obj) throws Exception;

  /**
   * 外部会议取消
   * @return：void
   * @author：wangj
   * @date：2017-10-12
   */
  public RetMsg cancel(MeeOutsideMeetingVO obj) throws Exception;

  /**
   * 外部会议使用完成
   * @return：void
   * @author：wangj
   * @date：2017-10-12
   */
  public void autoComplete(MeeOutsideMeetingVO obj) throws Exception;

  /**
   * 外部会议提交流程
   * @return：void
   * @author：wangj
   * @date：2017-10-12
   */
  public ActAljoinBpmn submitProcess() throws Exception;

  /**
   * 外部领导会议.
   * @return：MeeMeetingRoomCountDO
   * @author：wangj
   * @date：2017-10-12
   */
  public MeeMeetingRoomCountDO leaderList(MeeInsideMeetingVO obj) throws Exception;

  /**
   *
   * 流程审批.
   *
   * 			@return：RetMsg
   *
   * @author：wangj
   *
   * @date：2017-11-2
   */
  // public RetMsg completeTask(Map<String, Object> variables, String taskId,
  // String bizId,String userId) throws Exception;

  /**
   * 自动完成会议
   * @return：void
   * @author：wangj
   * @date：2017-10-18
   */
  public void autoComplete() throws Exception;

  /**
   * 检查当前节点的下一个节点办理人是 个人、多个人、部门、岗位
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-06
   */
  public RetMsg checkNextTaskInfo(String taskId) throws Exception;

  /**
   * 获取所有节点信息
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-06
   */
  public List<FixedFormProcessLog> getAllTaskInfo(String taskId, String processInstanceId) throws Exception;

  /**
   * 回退到上一个节点.
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-09
   */
//	public RetMsg jump2Task2(String taskId, String bizId) throws Exception;
//  public RetMsg jump2Task2(String taskId, String bizId, String message, Long createUserId) throws Exception;

  /**
   * 领导会议-个人详情列表.
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-09
   */
  public Page<MeeOutsideMeetingDO> leaderPersonList(PageBean pageBean, MeeLeaderMeetingDO obj) throws Exception;

  /**
   * 流程作废
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-20
   */
  public RetMsg toVoid(String taskId, String bizId, Long userId) throws Exception;

  /**
   * @return
   * 在线消息提醒
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-20
   */
  public AutMsgOnlineRequestVO onlineWarn(MeeOutsideMeeting obj, AutUser createuser, String type) throws Exception;

  /**
   * @return
   * 邮件提醒
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-20
   */
  public MaiWriteVO mailWarn(MeeOutsideMeeting obj, AutUser createuser, String type) throws Exception;

  /**
   * 每日定时推送第二天会议通知
   * @return：void
   * @author：sln
   * @date：2017-11-21
   */
  public void pushOutsideMeeting() throws Exception;

  /**
   * app端会议详情
   * @return：void
   * @author：sln
   * @date：2017-11-21
   */
  public MeeOutsideMeetingVO getDetail4App(MeeOutsideMeeting obj) throws Exception;

  /**
   * @return
   * 短信提醒
   * @return：RetMsg
   * @author：wangj
   * @date：2017-11-20
   */
  public SmsShortMessage smaWarn(MeeOutsideMeeting obj, AutUser createuser, String type) throws Exception;

  /**
   *  App外部会议详情
   * @return： RetMsg
   * @author：wangj
   * @date：2017-12-21pD
   */
  public RetMsg meeDetail(AppMeeOutsideMeetingVO obj) throws Exception;


}
