package aljoin.web.controller.act;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnRunService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.object.CustomerTaskDefinition;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import aljoin.web.service.act.ActFixedFormWebService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 固定表单流程审批(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-11-07
 */
@Controller
@RequestMapping(value = "/act/actFixedForm", method = RequestMethod.POST)
@Api(value = "固定表单流程审批Controller", description = "固定表单流程审批相关接口")
public class ActFixedFormController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(ActFixedFormController.class);
	@Resource
	private ActFixedFormServiceImpl actFixedFormService;
	@Resource
	private TaskService taskService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private ActAljoinBpmnRunService actAljoinBpmnRunService;
	@Resource
	private ActActivitiService actActivitiService;
	@Resource
	private HistoryService historyService;
	@Resource
	private ActFixedFormWebService actFixFormWebService;
	@Resource
	private ActFixedFormWebService actFixedFormWebService;

	/**
	 *
	 * 根据任务ID查询流程是否被签收
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-06
	 */
	@RequestMapping(value = "/isClaim")
	@ResponseBody
	@ApiOperation("根据任务ID查询流程是否被签收")
	public RetMsg isClaim(HttpServletRequest request, HttpServletResponse response, String taskId,
			String processInstanceId) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			retMsg = actFixedFormService.isClaim(taskId, processInstanceId, user.getUserId() + "");
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	/**
	 *
	 * 根据任务ID查询流程是否被签收
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-06
	 */
	@RequestMapping(value = "/isIoaClaim")
	@ResponseBody
	@ApiOperation("根据任务ID查询流程是否被签收")
	public RetMsg isIoaClaim(HttpServletRequest request, HttpServletResponse response, String taskId,
			String processInstanceId) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			retMsg = actFixedFormService.isIoaClaim(taskId, processInstanceId, user.getUserId() + "");
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 根据任务ID查询下一个流程节点信息
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-06
	 */
	@RequestMapping(value = "/getNextTaskInfo", method = RequestMethod.GET)
	@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query")
	@ResponseBody
	@ApiOperation("根据任务ID查询下一个流程节点信息")
	public RetMsg getNextTaskInfo(String taskId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = actFixedFormService.getNextTaskInfo(taskId);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 填写意见
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-06
	 */
	@RequestMapping(value = "/addComment")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "message", value = "意见", required = true, dataType = "int", paramType = "query") })
	@ResponseBody
	@ApiOperation("填写意见")
	public RetMsg addComment(String taskId, String message) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = actFixedFormService.addComment(taskId, message);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 输出流程图
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年9月11日 下午5:34:17
	 */
	@RequestMapping(value = "/showImg", method = RequestMethod.GET)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query") })
	@ApiOperation("流程图")
	public void showImg(HttpServletRequest request, HttpServletResponse response, String taskId) {
		try {
			actFixedFormService.showImg(request, response, taskId);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 *
	 * 获得上一节点信息
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	@RequestMapping(value = "/getPreTaskInfo")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("获得上一节点信息")
	public RetMsg getPreTaskInfo(String taskId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = actFixedFormService.getPreTaskInfo(taskId);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

	/**
	 *
	 * 回退
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	@RequestMapping(value = "/jump2Task2")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("回退")
	public RetMsg jump2Task2(String processInstanceId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = actFixFormWebService.jump2Task2(processInstanceId, getCustomDetail().getUserId());
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 输出流程图
	 *
	 * @return：void
	 *
	 * @author：huangw
	 *
	 * @date：2017年12月06日 下午5:34:17
	 */
	@RequestMapping(value = "/showHisImg", method = RequestMethod.GET)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processInstanceId", value = "实例ID", required = true, dataType = "string", paramType = "query") })
	@ApiOperation("流程图")
	public void showHisImg(HttpServletRequest request, HttpServletResponse response, String processInstanceId) {
		try {
			actFixedFormService.showHisImg(request, response, processInstanceId);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 *
	 * 撤回
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-12-11
	 */
	@RequestMapping(value = "/revoke")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("撤回")
	public RetMsg revoke(String processInstanceId, String taskId) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			retMsg = actFixedFormWebService.revoke(processInstanceId, taskId, String.valueOf(user.getUserId()));
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 退回操作获得上一节点信息：可以退回的允许情况如下： （1）自由流程可以退回（2）对于非自由流程，
	 *                               当前活动节点只有一个并且上级节点只有一个的可以退回
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年11月28日 下午8:18:14
	 */
	@RequestMapping(value = "/getPreTaskInfo2")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("获得上一节点信息(通过流程轨迹)")
	public RetMsg getPreTaskInfo(HttpServletRequest request, HttpServletResponse response) {
		String taskId = request.getParameter("taskId");
		String bpmnId = request.getParameter("bpmnId");
		RetMsg retMsg = new RetMsg();
		Map<String, String> retMap = new HashMap<String, String>();
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		try {
			if (StringUtils.isEmpty(taskId)) {
				throw new Exception("无上级节点，不能撤回");
			}
			ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(bpmnId);
			if (bpmnRun.getIsFree() == 1) {
				// （1）自由流程可以退回
				List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
						.processInstanceId(task.getProcessInstanceId()).finished()
						.orderByHistoricActivityInstanceEndTime().desc().list();
				if (activities.size() > 0) {
					retMap.put("activityKey", activities.get(0).getActivityId());
					retMap.put("activityName", activities.get(0).getActivityName());
					retMap.put("userId", activities.get(0).getAssignee());
					if (StringUtils.isNotEmpty(activities.get(0).getAssignee())) {
						AutUser user = autUserService.selectById(Long.parseLong(activities.get(0).getAssignee()));
						retMap.put("userFullName", user.getFullName());
					}
				} else {
					throw new Exception("无上级节点，不能撤回");
				}
			} else {
				// （2）对于非自由流程，当前活动节点只有一个并且上级节点只有一个的可以退回
				// 获取上一个节点(有可能有多个)
				List<TaskDefinition> defList = actActivitiService.getPreTaskInfo(task.getTaskDefinitionKey(),
						task.getProcessInstanceId());
				if (defList.size() != 1) {
					throw new Exception("流程不满足撤回条件:无或含有多个上级任务节点");
				} else {
					// 在判断当前流程实例是否同时存在活动节点
					List<Task> taskList =
							// taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
							taskService.createTaskQuery().executionId(task.getExecutionId()).list();
					if (taskList.size() == 1) {
						// 满足条件
						retMap.put("activityKey", defList.get(0).getKey());
						retMap.put("activityName", defList.get(0).getNameExpression().getExpressionText());
						// 查询出是谁办理了的
						List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery()
								.processInstanceId(task.getProcessInstanceId())
								.taskDefinitionKey(defList.get(0).getKey()).orderByHistoricTaskInstanceEndTime().desc()
								.list();
						if (hisTaskList.size() == 0) {
							// 如果是任务重找不到通过连线关系的上级节点，就到历史活动节点查新
							List<HistoricActivityInstance> activityInstanceList = historyService
									.createHistoricActivityInstanceQuery()
									.processInstanceId(task.getProcessInstanceId()).activityType("userTask").finished()
									.orderByHistoricActivityInstanceEndTime().desc().list();
							if (activityInstanceList.size() > 0) {
								HistoricActivityInstance activityInstance = activityInstanceList.get(0);
								retMap.put("activityKey", activityInstance.getActivityId());
								retMap.put("activityName", activityInstance.getActivityName());
								retMap.put("userId", activityInstance.getAssignee());
								if (StringUtils.isNotEmpty(activityInstance.getAssignee())) {
									AutUser user = autUserService.selectById(activityInstance.getAssignee());
									retMap.put("userFullName", user.getFullName());
								}
							}
						} else {
							// 通过连线获取的上级任务
							HistoricTaskInstance hisTask = hisTaskList.get(0);
							retMap.put("userId", hisTask.getAssignee());
							if (StringUtils.isNotEmpty(hisTask.getAssignee())) {
								AutUser user = autUserService.selectById(hisTask.getAssignee());
								retMap.put("userFullName", user.getFullName());
							}
						}
					} else if (taskList.size() > 1) {
						throw new Exception("流程不满足撤回条件:含有多个真正办理的任务节点");
					} else {
						throw new Exception("无上级节点，不能撤回");
					}
				}

			}
			if (retMap.get("activityKey") != null && retMap.get("activityKey").startsWith("StartEvent_")) {
				throw new Exception("不允许撤回到开始");
			}
			retMsg.setCode(0);
			retMsg.setObject(retMap);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

	/**
	 *
	 * 流程实例id查流程图
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017年9月11日 下午5:34:17
	 */
	@RequestMapping(value = "/showImgfromproInsId", method = RequestMethod.GET)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query") })
	@ApiOperation("流程图")
	public void showImgfromproInsId(HttpServletRequest request, HttpServletResponse response,
			String processInstanceId) {
		try {
			actFixedFormService.showImgfromproInsId(request, response, processInstanceId);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 *
	 * 获得上一节点信息（修改版）
	 *
	 * 					@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	@RequestMapping(value = "/getPreTaskInfo4fix")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("获得上一节点信息")
	public RetMsg getPreTaskInfo2(String taskId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = actFixedFormService.getPreTaskInfo2(taskId);
			if (retMsg.getCode() != 0)
				return retMsg;
			else {
				@SuppressWarnings("unchecked")
				List<CustomerTaskDefinition> defitionList = (List<CustomerTaskDefinition>) retMsg.getObject();
				List<String> asignee = new ArrayList<String>();
				if (!defitionList.isEmpty()) {
					for (CustomerTaskDefinition customerTaskDefinition : defitionList) {
						List<String> taskAssignee = Arrays.asList(customerTaskDefinition.getAssignee().split(";"));
						asignee.addAll(taskAssignee);
					}
				}
				if (!asignee.isEmpty()) {
					Where<AutUser> userWhere = new Where<AutUser>();
					userWhere.setSqlSelect("id,user_name,full_name");
					if (asignee.size() > 1) {
						userWhere.in("id", asignee);
					} else {
						userWhere.eq("id", asignee.get(0));
					}
					List<AutUser> userList = autUserService.selectList(userWhere);
					for (CustomerTaskDefinition customerTaskDefinition : defitionList) {
						for (AutUser autUser : userList) {
							if(customerTaskDefinition.getAssignee().equals(String.valueOf(autUser.getId()))){
								customerTaskDefinition.setAssigneeName(autUser.getFullName());
							}
						}
					}
				}
				retMsg.setObject(defitionList);
			}
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

	/**
	 *
	 * 流程日志
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017年9月11日 下午5:34:17
	 */
	@RequestMapping(value="/getLog")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "taskId",value = "任务ID",required = true,dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "processInstanceId",value = "流程实例ID",required = true,dataType = "string",paramType = "query")
	})
	@ResponseBody
	@ApiOperation("流程日志")
	public List<FixedFormProcessLog> getLog(String taskId, String processName, String processInstanceId){
		List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
		try {
			list = actFixedFormService.getActivityLog(taskId,processInstanceId);
		}catch (Exception e){
			logger.error("", e);
		}
		return list;
	}
}
