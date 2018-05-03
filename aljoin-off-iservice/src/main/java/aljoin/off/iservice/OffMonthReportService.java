package aljoin.off.iservice;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.off.dao.entity.OffMonthReport;
import aljoin.off.dao.object.AppOffMonthReportVO;
import aljoin.off.dao.object.OffMonthReportCountDO;
import aljoin.off.dao.object.OffMonthReportDO;
import aljoin.off.dao.object.OffMonthReportVO;

/**
 * 
 * 工作月报表(服务类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-11
 */
public interface OffMonthReportService extends IService<OffMonthReport> {

	/**
	 * 
	 * 工作月报表(分页列表).
	 *
	 * @return：Page<OffMonthReport>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	public Page<OffMonthReportDO> list(PageBean pageBean, OffMonthReport obj) throws Exception;

	/**
	 * 
	 * 根据ID删除对象(物理删除)
	 *
	 * 					@return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
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
	 * @date：2017-10-11
	 */
	public void copyObject(OffMonthReport obj) throws Exception;

	/**
	 *
	 * 月报详情
	 *
	 * @return：OffMonthReportVO
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	public OffMonthReportVO detail(OffMonthReport obj) throws Exception;


	/**
	 *
	 * 已收月报表(分页列表).
	 *
	 * @return：Page<OffMonthReportDO>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	public Page<OffMonthReportDO> recevieList(PageBean pageBean, OffMonthReportVO obj) throws Exception;

	/**
	 *
	 * 月报管理(分页列表).
	 *
	 * @return：Page<OffMonthReportDO>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	public Page<OffMonthReportDO> managerList(PageBean pageBean, OffMonthReport obj) throws Exception;

	/**
	 *
	 * 已提交月报详情
	 *
	 * @return：OffMonthReportVO
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	public OffMonthReportVO submitDetail(OffMonthReport obj) throws Exception;

	/**
	 *
	 * 工作月报统计列表.
	 *
	 * 				@return：List<OffMonthReportCountDO>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	public List<OffMonthReportCountDO> reportCountList(OffMonthReportVO obj) throws Exception;

	/**
	 *
	 * 工作月报统计导出.
	 *
	 * 				@return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	public void export(HttpServletResponse response, OffMonthReportVO obj) throws Exception;

	/**
	 *
	 * 工作月报更新保存.
	 *
	 * 				@return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-10-31
	 */
	public RetMsg updateMonthReport(OffMonthReportVO obj) throws Exception;

	/**
	 *
	 * 完成当前任务-提交到下一审批环节
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-10-31
	 */
//	public RetMsg completeTask(Map<String, Object> variables, String taskId, String bizId, String userId,
//			AutUser createUser, String message) throws Exception;

	/**
	 *
	 * 保存常用意见
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-10-31
	 */
	public RetMsg addComment(String taskId, String message) throws Exception;

	/**
	 *
	 * 查看流程图
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-10-31
	 */
	public void showImg(HttpServletRequest request, HttpServletResponse response, String taskId) throws Exception;

	/**
	 *
	 * 流程历史日志
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-10-31
	 */
	public List<FixedFormProcessLog> getAllTaskInfo(String taskId, String processName, String processInstanceId)
			throws Exception;

	// public RetMsg jump2Task2(String taskId) throws Exception;
	/**
	 *
	 * 获取上一环节信息（回退时显示）
	 *
	 * 					@return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-10-31
	 */
	public RetMsg getPreTaskInfo(String taskId) throws Exception;

	/**
	 *
	 * 检查是否签收
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-10-31
	 */
	public RetMsg isClaim(String taskId) throws Exception;

	/**
	 *
	 * 获取下已环节信息
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-10-31
	 */
	public RetMsg getNextTaskInfo(String taskId) throws Exception;

	/**
	 *
	 * 检查下一环节信息（提交时显示）
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-10-31
	 */
	public RetMsg checkNextTaskInfo(String taskId) throws Exception;

	/**
	 *
	 * 回退到上一任务节点
	 *
	 * 				@return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-10-31
	 */
//	public RetMsg jump2Task2(String taskId, String bizId, String message, AutUser createUser) throws Exception;

	/**
	 *
	 * 工作月报统计列表.
	 *
	 * 				@return：List<OffMonthReportCountDO>
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-18
	 */
	public List<OffMonthReportCountDO> returnReportCountList(OffMonthReportVO obj) throws Exception;

	/**
	 *
	 * 工作上月月报全局统计.
	 *
	 * 				@return：List<OffMonthReportCountDO>
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-20
	 */
	public OffMonthReportCountDO returnReportCount() throws Exception;

	/**
	 *
	 * 流程作废
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-20
	 */
	public RetMsg toVoid(String taskId, String bizId, Long userId) throws Exception;

	/**
	 *
	 * App月报详情
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-12-21
	 */
	public RetMsg offDetail(AppOffMonthReportVO obj) throws Exception;

	/**
	 *
	 * App完成当前任务-提交到下一审批环节
	 *
	 * @return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-10-31
	 */
//	public RetMsg completeAppTask(Map<String, Object> variables, String taskId, String bizId, String userId,
//														 AutUser createUser, String message) throws Exception;

}
