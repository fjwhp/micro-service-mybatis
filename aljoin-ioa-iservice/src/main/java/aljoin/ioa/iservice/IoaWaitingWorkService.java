package aljoin.ioa.iservice;

import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.entity.ActAljoinFormDataRun;
import aljoin.aut.dao.entity.AutUser;
import aljoin.ioa.dao.object.AllTaskShowVO;
import aljoin.ioa.dao.object.AppWaitTaskVO;
import aljoin.ioa.dao.object.WaitTaskShowVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 待办工作(服务类).
 * 
 * @author：pengsp
 * 
 * @date： 2017-10-19
 */
public interface IoaWaitingWorkService {
	/**
	 * 
	 * 待办工作流程(分页列表).
	 *
	 * @return：Page<DoTaskShowVO>
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-19
	 */
	public Page<WaitTaskShowVO> list(PageBean pageBean, String userId, WaitTaskShowVO obj) throws Exception;

	/**
	 * 
	 * 综合查询
	 *
	 * @return：Page<AllTaskShowVO>
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-19
	 */
	public Page<AllTaskShowVO> getAllTask(PageBean pageBean, AllTaskShowVO obj, Map<String, String> map)
			throws Exception;

	/**
	 * 
	 * 签收任务
	 *
	 * @return：RetMsg
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-19
	 */
	public RetMsg claimTask(String userId, String ids) throws Exception;

	/**
	 * 
	 * 签收完成更新query表
	 *
	 * @return：void
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-19
	 */
	public void updateQuery(String taskId) throws Exception;

	/**
	 *
	 * 获取下一个任务节点信息
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	public Map<String, Object> getNextTaskInfo(String taskId, Map<String, Object> runMap) throws Exception;

	/**
	 *
	 * 回退
	 *
	 * @return：RetMsg
	 *
	 * @author：pengsp
	 *
	 * @date：2017-11-10
	 */
	public RetMsg jumpTask(String taskId, String targetTaskKey, String targetUserId, String thisTaskUserComment,
	    Long cuserId, String userName,String nickName, Map<String, String> paramMap, ActAljoinFormDataRun entity, String isTask, String nextNode)
					throws Exception;

	/**
	 *
	 * 根据任务id获取用户
	 *
	 * @return：RetMsg
	 *
	 * @author：pengsp
	 *
	 * @date：2017-11-10
	 */
	public Map<String, Object> getTaskUser(String taskId, String nextTaskKey,Long currentUserId) throws Exception;

	/**
	 *
	 * App待办工作流程(分页列表).
	 *
	 * @return：Page<DoTaskShowVO>
	 *
	 * @author：wangj
	 *
	 * @date：2017-12-18
	 */
	public RetMsg waitList(PageBean pageBean, String userId, AppWaitTaskVO obj) throws Exception;

	/**
	 *
	 * App签收任务
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-12-22
	 */
	public RetMsg appClaimTask(String userId, String ids) throws Exception;

	/**
	 *
	 * 根据任务id获取审批用户
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-12-22
	 */
	public Map<String, Object> getAppTaskUser(String taskId, String nextTaskKey) throws Exception;

	/**
	 *
	 * App撤回
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-12-28
	 */
	public RetMsg appJump2Task2(String processInstanceId, Long createUserId) throws Exception;
	/**
	 *
	 * 判断任务是否存在
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-12-28
	 */
	public RetMsg isMyTask(String taskid,  String userID) throws Exception;
	/**
	 *
	 * 判断任务是否未完成
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-12-30
	 */
	public RetMsg isOverTask(String pid,  String userID) throws Exception;

	/**
	 *
	 * 获取下一个任务节点信息
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	public Map<String, Object> getNextAppTaskInfo(String taskId, Map<String, Object> runMap) throws Exception;

	/**
	 *
	 * App回退
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-10
	 */
	public RetMsg jumpAppTask(String taskId, String targetTaskKey, String targetUserId,
														String comment, Long userId, Map<String, String> paramMap,
														ActAljoinFormDataRun entity, String isTask, String nextTaskKey) throws Exception;

	/**
	 *
	 * App回退节点信息
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-10
	 */
	public Map<String, Object> getAppPreTaskInfo(String taskId, String bpmnId) throws  Exception;
	/**
	 *
	 *在办任务列表
	 *
	 * @return：Page
	 *
	 * @author：huangw
	 *
	 * @date：2018-3-19
	 */
	public Page<WaitTaskShowVO> waitingList(PageBean pageBean, String userId, WaitTaskShowVO obj) throws Exception;
	/**
	 *
	 * 在办分发
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2018-3-19
	 */
	public Map<String, Object> distribution(String htmlCode,String sendIds,Long userId,String taskId) throws Exception;
	/**
	 *
	 * 在办传阅
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2018-3-19
	 */
	public Map<String, Object> circula(String taskId,String proId,String sendIds,AutUser user) throws Exception;
	
}
