package aljoin.mee.iservice;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mee.dao.entity.MeeInsideMeeting;
import aljoin.mee.dao.object.MeeInsideMeetingDO;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.mee.dao.object.MeeLeaderMeetingDO;
import aljoin.mee.dao.object.MeeLeaderMeetingRoomCountDO;
import aljoin.mee.dao.object.MeeMeetingCountDO;
import aljoin.mee.dao.object.MeeMeetingCountVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sms.dao.entity.SmsShortMessage;

/**
 * 
 * 内部会议表(服务类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
public interface MeeInsideMeetingService extends IService<MeeInsideMeeting> {

	/**
	 * 
	 * 内部会议表(分页列表).
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	public Page<MeeInsideMeetingDO> list(PageBean pageBean, MeeInsideMeetingVO obj) throws Exception;

	/**
	 * 
	 * 根据ID删除对象(物理删除)
	 *
	 * 					@return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	public void physicsDeleteById(Long id) throws Exception;

	/**
	 * 
	 * 复制对象(需要完整的对象数据，包括所有的公共字段)
	 *
	 * 								@return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	public void copyObject(MeeInsideMeeting obj) throws Exception;

	/**
	 *
	 *  内部会议新增
	 *
	 * @return： RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-13
	 */
//	 public RetMsg add(MeeInsideMeetingVO obj) throws Exception;

	/**
	 *
	 *  内部会议详情
	 *
	 * @return： RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-13
	 */
	public MeeInsideMeetingVO detail(MeeInsideMeeting obj) throws Exception;

	/**
	 *
	 * @描述:我的内部会议 (分页列表).
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	public Page<MeeInsideMeetingDO> personList(PageBean pageBean, MeeInsideMeetingVO obj) throws Exception;

	/**
	 *
	 * @描述:我的内部会议 (分页列表).
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	public Page<MeeInsideMeetingDO> managerlist(PageBean pageBean, MeeInsideMeetingVO obj) throws Exception;

	/**
	 *
	 *  内部会议变更
	 *
	 * @return： RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-13
	 */
//	 public RetMsg update(MeeInsideMeetingVO obj) throws Exception;

	/**
	 *
	 *  内部会议取消
	 *
	 * @return： RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-13
	 */
	public RetMsg cancel(MeeInsideMeetingVO obj) throws Exception;

	/**
	 *
	 * 内部领导会议.
	 *
	 * 			@return：MeeMeetingRoomCountDO
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	public MeeLeaderMeetingRoomCountDO leaderList(MeeInsideMeetingVO obj) throws Exception;

	/**
	 *
	 * 内部会议表(分页列表).
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	public List<MeeMeetingCountDO> meetingCountList(MeeMeetingCountVO obj, String weets) throws Exception;

	/**
	 *
	 * 会议统计导出
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	public void export(HttpServletResponse response, MeeMeetingCountVO obj, String weets) throws Exception;

	/**
	 *
	 * 自动完成会议
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	 public void autoComplete() throws Exception;
	/**
	 *
	 * 领导会议统计
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	public List<MeeLeaderMeetingDO> leaderCount(MeeInsideMeetingVO obj) throws Exception;

	/**
	 *
	 * 个人会议详情（领导会议查询某个领导详情 ）
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	public Page<MeeInsideMeetingDO> leaderPersonList(PageBean pageBean, MeeLeaderMeetingDO obj) throws Exception;

	/**
	 *
	 * 30天内会议数据统计
	 *
	 * @return：void
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-21
	 */
	public MeeMeetingCountDO meetingCount(MeeMeetingCountVO obj, String weets) throws Exception;
	/**
	 * 
	 * 每日定时推送第二天会议通知
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-11-21
	 */
	public void pushInsideMeeting() throws Exception;
	/**
	 * 
	 * @return 
	 * 在线信息提醒
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-11-21
	 */
	public AutMsgOnlineRequestVO onlineWarn(MeeInsideMeeting obj, AutUser createuser, String type) throws Exception;
	/**
	 * 
	 * @return 
	 * 邮件提醒
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-11-21 
	 */
	public MaiWriteVO mailWarn(MeeInsideMeeting obj, AutUser createuser, String type) throws Exception;

	/**
	 * 
	 * app端会议详情
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-11-21
	 */
	public MeeInsideMeetingVO getDetail4App(MeeInsideMeeting obj) throws Exception;

	/**
	 * 
	 * app会议室
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-11-21
	 */
	public List<MeeLeaderMeetingRoomCountDO> getAppRoom() throws Exception;
	/**
	 * 
	 * @return 
	 * 短信提醒
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-11-21
	 */
	public SmsShortMessage smaWarn(MeeInsideMeeting obj, AutUser createuser, String type) throws Exception;

}
