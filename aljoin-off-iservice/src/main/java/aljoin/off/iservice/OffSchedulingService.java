package aljoin.off.iservice;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.off.dao.entity.OffScheduling;
import aljoin.off.dao.object.MeetScheduleDO;
import aljoin.off.dao.object.OffSchedulingDO;
import aljoin.off.dao.object.OffSchedulingVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 日程安排表(服务类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-01
 */
public interface OffSchedulingService extends IService<OffScheduling> {

	/**
	 * 
	 * 日程安排表(分页列表).
	 *
	 * @return：Page<OffScheduling>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	public Page<OffScheduling> list(PageBean pageBean, OffScheduling obj) throws Exception;

	/**
	 * 
	 * 根据ID删除对象(物理删除)
	
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	public void physicsDeleteById(Long id) throws Exception;

	/**
	 * 
	 * 复制对象(需要完整的对象数据，包括所有的公共字段)
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	public void copyObject(OffScheduling obj) throws Exception;

	/**
	 * 
	 * 新增日程对象
	 *
	 * @return：RetMsg
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-01
	 */
//	public RetMsg add(OffSchedulingVO obj) throws Exception;

	/**
	 * 
	 * 日程安排表
	 *
	 * @return：List
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-01
	 */
	public List<OffScheduling> list(OffSchedulingVO obj) throws Exception;

	/**
	 * 
	 * 更新日程对象
	 *
	 * @return：RetMsg
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-01
	 */
	public RetMsg update(OffScheduling obj) throws Exception;

	/**
	 * 
	 * 删除日程对象
	 *
	 * @return：RetMsg
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-02
	 */
	public RetMsg delete(OffScheduling obj) throws Exception;

	/**
	 * 
	 * 导出日程对象
	 *
	 * @return：RetMsg
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-02
	 */
	public void export(HttpServletResponse response, OffSchedulingVO obj) throws Exception;

	/**
	 * 
	 * 领导查看分页列表
	 *
	 * @return：RetMsg
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-02
	 */
	public Page<OffSchedulingVO> leaderList(PageBean pageBean, OffSchedulingVO obj) throws Exception;

	/**
	 * 
	 * 详情
	 *
	 * @return：RetMsg
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-02
	 */
	public OffSchedulingVO detail(OffScheduling obj) throws Exception;

	/**
	 * 
	 * 首页日程(月日程)
	 *
	 * @return：RetMsg
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-02
	 */
	public List<String> indexList4Month(OffSchedulingDO obj) throws Exception;

	/**
	 * 
	 * 首页日程(天日程)
	 *
	 * @return：RetMsg
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-02
	 */
	public List<OffScheduling> indexList4day(OffSchedulingDO obj) throws Exception;

	/**
	 * 
	 * 领导会议内容：月日程列表+领导列表+第一位领导今日会议列表（app）
	 *
	 * @return：RetMsg
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-02
	 */
	public RetMsg leaderScheduleMap(MeetScheduleDO obj) throws Exception;

	/**
	 * 
	 * 领导当日会议（app）
	 *
	 * @return：List<OffScheduling>
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-02
	 */
	public List<OffScheduling> leadDayList(MeetScheduleDO obj,List<String> userIdList) throws Exception;

	/**
	 * 
	 * 领导当月会议列表（app）
	 *
	 * @return：List<OffScheduling>
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-02
	 */
	public List<String> leadMonthList(MeetScheduleDO obj,List<String> userIdList) throws Exception;
	
	/**
	 * 
	 * 删除旧数据
	 *
	 * @return：List<OffScheduling>
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-02
	 */
	public void deleteOldData() throws Exception;

	/**
	 * 
	 * 领导当月+当日会议列表（app）
	 *
	 * @return：List<OffScheduling>
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-02
	 */
	public RetMsg leaderMeetingMap(MeetScheduleDO obj) throws Exception;

	/**
	 * 
	 * 个人会议 月+日 日程（app）
	 *
	 * @return：RetMsg
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-20
	 */
	public RetMsg personMap(MeetScheduleDO obj) throws Exception;
	
	/**
	 * 
	 * 根据bizId删除对应的会议日程（取消会议时使用）
	 *
	 * @return：RetMsg
	 *
	 * @author：sunln
	 *
	 * @date：2017-11-20
	 */
	public void deleteOffSchedule(Long bizId) throws Exception;

}
