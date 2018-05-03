package aljoin.app.controller.act;

import aljoin.act.dao.entity.ActAljoinFormDataRun;
import aljoin.act.dao.entity.ActAljoinFormRun;
import aljoin.act.iservice.ActAljoinFormRunService;
import aljoin.app.controller.BaseController;
import aljoin.app.task.WebTask;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.web.service.act.ActAljoinBpmnWebService;
import aljoin.web.service.ioa.IoaCreateWorkWebService;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.ActivitiOptimisticLockingException;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
@Controller
@RequestMapping(value = "/app/ioa/ioaCreateWork", method = RequestMethod.POST)
@Api(value = "公共表单数据操作Controller", description = "流程->公共表单数据操作")
public class AppIoaCreateWorkController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(AppIoaCreateWorkController.class);

	@Resource
	private IoaCreateWorkWebService ioaCreateWorkWebService;
	@Resource
	private WebTask webTask;
	@Resource
	private AutUserService autUserService;
	@Resource
	private ActAljoinFormRunService actAljoinFormRunService;
	@Resource
	private ActAljoinBpmnWebService actAljoinBpmnWebService;
	@Resource
	private TaskService taskService;
	/**
	 * 
	 * 新建工作(并发起流程).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年10月12日 上午8:42:57
	 */
	@RequestMapping("/doCreateWork")
	@ResponseBody
	@ApiOperation(value = "提交")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bpmnId", value = "流程ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "formId", value = "表单ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "taskId", value = "当前任务ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isTask", value = "是否自由流程(用来标记自由流程，当isTask=false时是自由流程)", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "nextTaskKey", value = "下个任务节点Key", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "comment", value = "意见", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isUrgent", value = "缓急(1:一般 2:紧急 3:加急)", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "taskAuth", value = "任务key和用户ID拼接成的字符串,key和用户ID之间用逗号分隔用户ID之间用#号分隔 格式(UserTask_1hawkmi,903158226865868800#903158226865868800#903158226865868800;)", required = false, dataType = "string", paramType = "query") })
	public RetMsg doCreateWork(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataRun entity) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin user = getAppUserLogin(request);
			String formId = request.getParameter("formId");
			String taskId = request.getParameter("taskId");
			String bpmnId = request.getParameter("bpmnId");
			// 根据流程实例ID获取流程实例
			Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
			if (null == task) {
				retMsg.setMessage("该任务已被完成不能重复提交");
				retMsg.setCode(AppConstant.RET_CODE_ERROR);
				return retMsg;
			}

			Map<String, String> paramMap = new HashMap<String, String>();
			ActAljoinFormRun actAljoinFormRun = actAljoinFormRunService.selectById(Long.valueOf(formId));
			// 解析HTMLCODE 编码
			byte[] byteKey = actAljoinFormRun.getHtmlCode().getBytes();
			Map<String, String> formWidgetMap = actAljoinBpmnWebService.bulidFormWidgetValue(bpmnId, taskId,
					actAljoinFormRun, byteKey, user.getUserId(), user.getUserName());
			for (String key : formWidgetMap.keySet()) {
				paramMap.put(key, formWidgetMap.get(key));
			}

			Map<String, String> param = new HashMap<String, String>();
			if (!StringUtils.isEmpty(request.getParameter("isUrgent"))) {
				param.put("isUrgent", request.getParameter("isUrgent"));
			} else {
				param.put("isUrgent", "1");
			}

			param.put("taskId", request.getParameter("taskId"));
			param.put("isTask", request.getParameter("isTask"));
			param.put("nextTaskKey", request.getParameter("nextTaskKey"));
			param.put("userId", request.getParameter("userId"));
			param.put("comment", request.getParameter("comment"));
			param.put("taskAuth", request.getParameter("taskAuth"));

			Where<AutUser> userWhere = new Where<AutUser>();
			userWhere.setSqlSelect("id,user_name,full_name");
			userWhere.eq("id", user.getUserId());
			AutUser autUser = autUserService.selectOne(userWhere);
			entity.setOperateUserId(autUser.getId());
			Map<String, String> resultMap = ioaCreateWorkWebService.doAppCreateWork(entity, paramMap, param,
					autUser.getId(), autUser.getFullName());
			if (null != resultMap) {
				resultMap.put("taskAuth", request.getParameter("taskAuth"));
				webTask.sendOnlineMsg4MulTask(resultMap, autUser);
			}

			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("提交成功");
		} catch (ActivitiOptimisticLockingException e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("该任务正在处理中请稍后处理");
			logger.error("", e);
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;
	}

	@RequestMapping("/doAddSign")
	@ResponseBody
	@ApiOperation(value = "加签")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "bpmnId", value = "流程ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "formId", value = "表单ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "taskId", value = "当前任务ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "comment", value = "意见", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "userIds", value = "选中的用户ID 用分号分隔（单个用户也要加分号分隔）", required = true, dataType = "string", paramType = "query")
	})
	public RetMsg doAddSign(HttpServletRequest request, HttpServletResponse response,
		ActAljoinFormDataRun entity) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin user = getAppUserLogin(request);
			String formId = request.getParameter("formId");
			String taskId = request.getParameter("taskId");
			String bpmnId = request.getParameter("bpmnId");
			// 根据流程实例ID获取流程实例
			Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
			if(null == task){
				retMsg.setMessage("该任务已被完成不能重复提交");
				retMsg.setCode(AppConstant.RET_CODE_ERROR);
				return retMsg;
			}

			Map<String, String> paramMap = new HashMap<String, String>();
			ActAljoinFormRun actAljoinFormRun = actAljoinFormRunService.selectById(Long.valueOf(formId));
			//解析HTMLCODE 编码
			byte[] byteKey = actAljoinFormRun.getHtmlCode().getBytes();
			Map<String,String> formWidgetMap = actAljoinBpmnWebService.bulidFormWidgetValue(bpmnId,taskId,actAljoinFormRun,byteKey,user.getUserId(),user.getUserName());
			for(String key : formWidgetMap.keySet()){
				paramMap.put(key,formWidgetMap.get(key));
			}

			Map<String, String> param = new HashMap<String, String>();

			param.put("taskId", request.getParameter("taskId"));
			param.put("userIds", request.getParameter("userIds"));
			param.put("comment", request.getParameter("comment"));

			Where<AutUser> userWhere = new Where<AutUser>();
			userWhere.setSqlSelect("id,user_name,full_name");
			userWhere.eq("id",user.getUserId());
			AutUser autUser = autUserService.selectOne(userWhere);
			entity.setOperateUserId(autUser.getId());
			Map<String,String> resultMap = ioaCreateWorkWebService.doAppAddSign(entity, paramMap, param, autUser.getId(),
				autUser.getFullName());
			if(null != resultMap){
				webTask.sendOnlineMsg4MulTask(resultMap,autUser);
			}

			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("提交成功");
		} catch (ActivitiOptimisticLockingException e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("该任务正在处理中请稍后处理");
			logger.error("", e);
		}catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;
	}
}
