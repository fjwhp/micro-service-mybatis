package aljoin.app.controller.act;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.app.controller.BaseController;
import aljoin.app.task.WebTask;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutUsefulOpinion;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.aut.dao.object.AutOrganVO;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUsefulOpinionService;
import aljoin.aut.iservice.AutUserService;
import aljoin.ioa.dao.entity.IoaReceiveFile;
import aljoin.ioa.dao.entity.IoaReceiveReadUser;
import aljoin.ioa.dao.object.IoaReceiveFileDO;
import aljoin.ioa.dao.object.IoaReceiveFileVO;
import aljoin.ioa.dao.object.IoaReceiveReadObjectDO;
import aljoin.ioa.dao.object.IoaReceiveReadUserDO;
import aljoin.ioa.iservice.IoaReceiveFileService;
import aljoin.ioa.iservice.IoaReceiveReadUserService;
import aljoin.object.AppConstant;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysDataDict;
import aljoin.web.service.ioa.IoaReceiveFileWebService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 收文阅件
 *
 * @author：huangw
 * 
 * @date：2017年12月20日 上午8:43:11
 */
@Controller
@RequestMapping(value = "app/ioa/ioaReceiveWork", method = RequestMethod.POST)
@Api(value = "收文阅件Controller", description = "流程->收文阅件")
public class AppIoaReceiveWorkController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(AppIoaReceiveWorkController.class);
	@Resource
	private IoaReceiveFileService ioaReceiveFileService;
	@Resource
	private IoaReceiveFileWebService ioaReceiveFileWebService;
	@Resource
	private AutUsefulOpinionService autUsefulOpinionService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private IoaReceiveReadUserService ioaReceiveReadUserService;
	@Resource
	private AutDepartmentUserService autDepartmentUserService;
	@Resource
	private WebTask webTask;

	/**
	 * 
	 * 收文阅件待办任务列表(分页列表).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/tolist")
	@ResponseBody
	@ApiOperation(value = "收文阅件待办任务列表(分页列表)") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页数据条数", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "fileType", value = "来文类型", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromUnit", value = "来文单位", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fileTitle", value = "来文标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromFileCode", value = "来文文号", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "urgentLevel", value = "紧急程度", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "begDate", value = "创建开始时间(2017-11-11)", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "创建结束时间(2017-11-11)", required = false, dataType = "date", paramType = "query") })
	public RetMsg tolist(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			IoaReceiveFileVO obj) {
		RetMsg retMsg = new RetMsg();
		Page<IoaReceiveFileDO> page = new Page<IoaReceiveFileDO>();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			obj.setCreateUserId(userId);
			page = ioaReceiveFileService.toReadList(pageBean, obj, userId.toString());
			retMsg.setObject(page);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;

	}

	/**
	 * 
	 * 收文阅件在办任务列表(分页列表).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/dolist")
	@ResponseBody
	@ApiOperation(value = "收文阅件在办任务列表(分页列表)") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页数据条数", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "fileType", value = "来文类型", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromUnit", value = "来文单位", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fileTitle", value = "来文标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromFileCode", value = "来文文号", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "urgentLevel", value = "紧急程度", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "begDate", value = "创建开始时间(2017-11-11)", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "创建结束时间(2017-11-11)", required = false, dataType = "date", paramType = "query") })
	public RetMsg dolist(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			IoaReceiveFileVO obj) {
		RetMsg retMsg = new RetMsg();
		Page<IoaReceiveFileDO> page = new Page<IoaReceiveFileDO>();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			obj.setCreateUserId(userId);
			page = ioaReceiveFileService.inReadList(pageBean, obj, userId.toString());
			retMsg.setObject(page);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 收文阅件(获取流程配置数据).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-09-20
	 */
	@SuppressWarnings("unused")
	@RequestMapping("/loadDictByCode")
	@ResponseBody 
	@ApiOperation(value = "获取流程配置数据") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg loadDictByCode(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			Map<String, Object> map = new HashMap<String, Object>();
			map = ioaReceiveFileService.loadDictByCode();
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setObject(map);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;

	}

	/**
	 * 
	 * 收文阅件(添加常用意见).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/addfulOpinion")
	@ResponseBody
	@ApiOperation(value = "添加常用意见")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "content", value = "意见内容", required = true, dataType = "string", paramType = "query") })
	public RetMsg addfulOpinion(HttpServletRequest request, HttpServletResponse response, AutUsefulOpinion obj) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			if (obj != null) {
				obj.setCreateUserId(userId);
				obj.setUserId(userId);
				retMsg = autUsefulOpinionService.appAdd(obj);
			} else {
				retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
				retMsg.setMessage("操作失败，参数错误！");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;

	}

	/**
	 * 
	 * 收文阅件(常用意见列表).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/fulOpinionList")
	@ResponseBody 
	@ApiOperation(value = "常用意见列表") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg fulOpinionList(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		List<AutUsefulOpinion> retList = new ArrayList<AutUsefulOpinion>();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			retList = autUsefulOpinionService.appList(userId);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setObject(retList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
			retMsg.setMessage("操作失败");
		}
		return retMsg;

	}

	/**
	 * 
	 * 收文阅件(流转日志).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-09-20
	 */
	@SuppressWarnings("unused")
	@RequestMapping("/getAllTaskInfo")
	@ResponseBody 
	@ApiOperation(value = "获取流转日志")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "processInstanceId", value = "流转日志", required = true, dataType = "string", paramType = "query"), })
	public RetMsg getAllTaskInfo(HttpServletRequest request, HttpServletResponse response, String processInstanceId) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
			list = ioaReceiveFileService.getAppTaskLogInfo(processInstanceId);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setObject(list);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 收文阅件(获取流程配置数据).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-09-20
	 */
	@SuppressWarnings("unused")
	@RequestMapping("/memberList")
	@ResponseBody
	@ApiOperation(value = "获取流程配置数据")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "dictCode", value = "获取标识数据字典数据的code(DICT_RECEIVE_FILE_TYPE来文类型)", required = true, dataType = "string", paramType = "query") })
	public RetMsg memberList(HttpServletRequest request, HttpServletResponse response, String dictCode) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			List<SysDataDict> list = new ArrayList<SysDataDict>();
			list = ioaReceiveFileService.memberList(dictCode);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setObject(list);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 收文阅件(根据Id获取详情).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-22
	 */
	@RequestMapping("/getById")
	@ResponseBody 
	@ApiOperation(value = "根据Id获取详情") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "int", paramType = "query") })
	public RetMsg getById(HttpServletRequest request, HttpServletResponse response, IoaReceiveFile obj) {
		RetMsg retMsg = new RetMsg();
		Map<String, Object> resultMap = null;
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			obj.setCreateUserId(userId);
			resultMap = ioaReceiveFileService.appDetail(obj);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			// resultMap.
			retMsg.setObject(resultMap);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 收文阅件(根据流程实例ID获取节点信息.).
	 *
	 * 							@return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-22
	 */
	@RequestMapping("/checkNextTaskInfo")
	@ResponseBody 
	@ApiOperation(value = "根据流程实例ID获取节点信息") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "receiveUserIds", value = "人员ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg checkNextTaskInfo(HttpServletRequest request, HttpServletResponse response, String processInstanceId,
			String receiveUserIds, String isCheckAllUser) {
		RetMsg retMsg = new RetMsg();
		try {
			if("false".equals(receiveUserIds)){
				receiveUserIds="";
			}
			retMsg = ioaReceiveFileService.checkNextTaskInfo(processInstanceId, receiveUserIds, isCheckAllUser);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 收文阅件(根据流程实例ID获取下个节点信息).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-22
	 */
	@RequestMapping("/getNextTaskInfo")
	@ResponseBody 
	@ApiOperation("根据流程实例ID获取下个节点信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "withCondition", value = "是否带条件", required = true, dataType = "string", paramType = "query") })
	public RetMsg getNextTaskInfo(HttpServletRequest request, HttpServletResponse response, String processInstanceId,
			boolean withCondition) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = ioaReceiveFileService.getNextTaskInfo(processInstanceId, false);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 收文阅件(完成任务)
	 *
	 * 				@return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-22
	 */
	@SuppressWarnings("unchecked")
  @RequestMapping("/completeTask")
	@ResponseBody 
	@ApiOperation(value = "完成审批") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "variables", value = "流程变量", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "receiveFileId", value = "主键ID", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "readUserIds", value = "用户ID(多个分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "message", value = "审批意见", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isEnd", value = "是否传阅环节", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isOffice", value = "池主任环节", required = true, dataType = "string", paramType = "query"),
})
	public RetMsg completeTask(HttpServletRequest request, HttpServletResponse response, Map<String, Object> variables,
			String processInstanceId, String receiveFileId, String readUserIds, String message, String isEnd,String isOffice) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			if("0".equals(isOffice)){
				isOffice=null;
			}
			if(isEnd!=null && "0".equals(isEnd)){
				isEnd=null;
			}
			Long userId = autAppUserLogin.getUserId();
			retMsg = ioaReceiveFileWebService.appCompleteTask(variables, processInstanceId, receiveFileId, readUserIds,
					message, userId.toString(), isEnd, userId, isEnd,isOffice);
			if (retMsg.getObject() != null && retMsg.getCode()==AppConstant.RET_CODE_SUCCESS) {
				AutUser autUsers = autUserService.selectById(userId);
				Map<String, Object> maps = (HashMap<String, Object>) retMsg.getObject();
				String handle = (String) maps.get("handle");
				if (handle != null && !"".equals(handle)) {// 当下一级办理人不为空时发送待办消息
					webTask.sendOnlineMsg4IoaRead(maps, autUsers);
				}
				retMsg.setObject(null);
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 收文阅件(撤回任务到上一节点.)
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-22
	 */
	@SuppressWarnings("unchecked")
  @RequestMapping("/jump2Task2")
	@ResponseBody
	@ApiOperation(value = "退回任务到上一节点")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bizId", value = "主键ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg jump2Task2(HttpServletRequest request, HttpServletResponse response, String processInstanceId,
			String bizId, String message) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			retMsg = ioaReceiveFileWebService.jump2Task2(processInstanceId, bizId, message, userId);
			if (retMsg.getObject() != null) {
				AutUser autUsers = autUserService.selectById(userId);
				// @SuppressWarnings("unchecked")
				Map<String, Object> maps = (HashMap<String, Object>) retMsg.getObject();
				String handle = (String) maps.get("handle");
				if (handle != null && !"".equals(handle)) {// 当下一级办理人不为空时发送待办消息
					webTask.sendOnlineMsg4IoaRead(maps, autUsers);
				}
				retMsg.setObject(null);
			}
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 收文阅件(获取上一节点信息.)
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-22
	 */
	@SuppressWarnings("unused")
	@RequestMapping("/getPreTaskInfo")
	@ResponseBody 
	@ApiOperation(value = "获取上一节点信息") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg getPreTaskInfo(HttpServletRequest request, HttpServletResponse response, String processInstanceId) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			retMsg = ioaReceiveFileService.getPreTaskInfo(processInstanceId);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 收文阅件(池主任审批详情)
	 *
	 * 					@return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-22
	 */
	@SuppressWarnings("unused")
	@RequestMapping("/directorDetail")
	@ResponseBody 
	@ApiOperation(value = "池主任审批详情") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "int", paramType = "query") })
	public RetMsg directorDetail(HttpServletRequest request, HttpServletResponse response, IoaReceiveFile obj) {
		RetMsg retMsg = new RetMsg();
		IoaReceiveFileDO ioaReceiveFileDO = null;
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			AutUser user = new AutUser();
			user.setId(autAppUserLogin.getUserId());
			user.setUserName(autAppUserLogin.getUserName());
			ioaReceiveFileDO = ioaReceiveFileService.directorDetails(obj, user);
			List<IoaReceiveReadObjectDO> list=ioaReceiveFileDO.getObjectDOList();
			if(list!=null && list.size()>0){
				for (int i = 0; i < list.size(); i++) {
					IoaReceiveReadObjectDO objcetDo =list.get(i);
					String code=objcetDo.getDictCode();
					if(!(code.indexOf("DICT_READ_")>-1) && !(code.indexOf("-")>-1)){
						if(objcetDo.getDictKeyDetail()==null){
							objcetDo.setDictKeyDetail(objcetDo.getDictCode());
							list.set(i, objcetDo);
						}
					}					
				}
			}
			retMsg.setObject(ioaReceiveFileDO);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 收文登记详情
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-22
	 */
	@RequestMapping("/readDetail")
	@ResponseBody 
	@ApiOperation(value = "收文登记详情") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "int", paramType = "query") })
	public RetMsg readDetail(HttpServletRequest request, HttpServletResponse response, IoaReceiveFile obj) {
		RetMsg retMsg = new RetMsg();
		IoaReceiveFileDO ioaReceiveFileDO = null;
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			obj.setCreateUserId(userId);
			ioaReceiveFileDO = ioaReceiveFileService.readDetails(obj);
			retMsg.setObject(ioaReceiveFileDO);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 撤回任务到上一节点.
	 *
	 * 				@return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-22
	 */
	@SuppressWarnings("unused")
	@RequestMapping("/revoke2Task")
	@ResponseBody
	@ApiOperation(value = "撤回任务到上一节点") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bizId", value = "主键ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg revoke2Task(HttpServletRequest request, HttpServletResponse response, String processInstanceId,
			String bizId) {
		RetMsg retMsg = new RetMsg();
		IoaReceiveFileDO ioaReceiveFileDO = null;
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			AutUser user = new AutUser();
			user = autUserService.selectById(userId);
			user.setId(userId);
			user.setFullName(user.getFullName());
			user.setUserName(user.getUserName());
			retMsg = ioaReceiveFileService.revoke2Task(processInstanceId, bizId, user);
			// retMsg.setObject(ioaReceiveFileDO);
			if (retMsg.getCode() == 0) {
				retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 传阅意见
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-22
	 */
	@SuppressWarnings("unused")
	@RequestMapping("/circulation")
	@ResponseBody 
	@ApiOperation(value = "传阅人员已读未读") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg circulation(HttpServletRequest request, HttpServletResponse response, String processInstanceId,
			String bizId) {
		RetMsg retMsg = new RetMsg();
		IoaReceiveFileDO ioaReceiveFileDO = null;
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			IoaReceiveReadUserDO readUserDO = ioaReceiveReadUserService.circulation(processInstanceId);
			retMsg.setObject(readUserDO);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block batchidea()
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 传阅意见
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-22
	 */
	@RequestMapping("/circulationList")
	@ResponseBody 
	@ApiOperation(value = "传阅意见") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页数据条数", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "integer", paramType = "query") })
	public RetMsg circulationList(HttpServletRequest request, HttpServletResponse response, IoaReceiveReadUser obj,
			PageBean pageBean) {
		RetMsg retMsg = new RetMsg();
		Page<IoaReceiveReadUserDO> page = null;
		try {
			page = ioaReceiveReadUserService.list(pageBean, obj);
			retMsg.setObject(page);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block batchidea()
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 主任审批保存
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-22
	 */
	@RequestMapping("/directorAudit")
	@ResponseBody
	@ApiOperation(value = "主任审批保存") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "收文阅件ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "officeOpinion", value = "办公室拟办意见", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "readObjectList[0].objectId", value = "部门ID", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "readObjectList[0].readUserIds", value = "保存列表对象ID", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "readObjectList[0].objectName", value = "保存列表名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "readObjectList[0].readUserNames", value = "保存列表对应选择用户名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "readObjectList[1].objectId", value = "部门ID", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "readObjectList[1].readUserIds", value = "保存列表对象ID", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "readObjectList[1].objectName", value = "保存列表名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "readObjectList[1].readUserNames", value = "保存列表对应选择用户名", required = false, dataType = "string", paramType = "query")

	})
	public RetMsg directorAudit(HttpServletRequest request, HttpServletResponse response, IoaReceiveFileVO obj) {
		RetMsg retMsg = new RetMsg();
		try {

			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			retMsg = ioaReceiveFileService.appDirectorAudit(obj, userId);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	public AppIoaReceiveWorkController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * 收文阅件获取传阅对象部门人员数据
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-22
	 */
	@RequestMapping("/getOrganList")
	@ResponseBody 
	@ApiOperation(value = "收文阅件获取传阅对象部门人员数据")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "departmentIds", value = "部门ID(多个分号分隔)", required = false, dataType = "string", paramType = "query") })
	public RetMsg getOrganList(HttpServletRequest request, HttpServletResponse response,
			AutDepartmentUserVO departmentUser) {
		RetMsg retMsg = new RetMsg();
		try {
			// AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			// Long userId = autAppUserLogin.getUserId();
			AutOrganVO organVO = autDepartmentUserService.getOrganList(departmentUser);
			retMsg.setObject(organVO);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

}
