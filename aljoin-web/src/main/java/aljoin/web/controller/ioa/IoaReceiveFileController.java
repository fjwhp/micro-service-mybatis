package aljoin.web.controller.ioa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.security.CustomUser;
import aljoin.ioa.dao.entity.IoaReceiveFile;
import aljoin.ioa.dao.object.IoaReceiveFileDO;
import aljoin.ioa.dao.object.IoaReceiveFileVO;
import aljoin.ioa.iservice.IoaReceiveFileService;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysDataDict;
import aljoin.web.controller.BaseController;
import aljoin.web.service.ioa.IoaReceiveFileWebService;
import aljoin.web.task.WebTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 收文阅件表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-08
 */
@Controller
@RequestMapping(value = "/ioa/ioaReceiveFile", method = RequestMethod.POST)
@Api(value = "收文阅件Controller", description = "协同办公->收文阅件相关接口")
public class IoaReceiveFileController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(IoaReceiveFileController.class);
	@Resource
	private IoaReceiveFileService ioaReceiveFileService;
	@Resource
	private IoaReceiveFileWebService ioaReceiveFileWebService;
	@Resource
	private WebTask webTask;

	/**
	 * 
	 * 收文阅件表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping(value = "/ioaReceiveFilePage", method = RequestMethod.GET)
	@ApiOperation("收文阅件页面接口")
	public String ioaReceiveFilePage(HttpServletRequest request, HttpServletResponse response) {

		return "ioa/ioaReceiveFilePage";
	}

	/**
	 * 
	 * 收文阅件表(分页列表).
	 *
	 * @return：Page<IoaReceiveFile>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping(value = "/toReadList")
	@ResponseBody
	@ApiOperation("收文阅件分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "fileType", value = "来文类型", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromUnit", value = "来文单位", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fileTitle", value = "来文标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromFileCode", value = "来文文号", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "urgentLevel", value = "紧急程度", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "begDate", value = "填报开始时间(2017-11-11)", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "填报结束时间(2017-11-11)", required = false, dataType = "date", paramType = "query") })
	public Page<IoaReceiveFileDO> toReadList(HttpServletRequest request, HttpServletResponse response,
			PageBean pageBean, IoaReceiveFileVO obj) {
		Page<IoaReceiveFileDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			obj.setCreateUserId(user.getUserId());
			page = ioaReceiveFileService.toReadList(pageBean, obj, user.getUserId() + "");
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 收文阅件表(分页列表).
	 *
	 * @return：Page<IoaReceiveFile>
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping(value = "/inReadList")
	@ResponseBody
	@ApiOperation("收文阅件分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "fileType", value = "来文类型", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromUnit", value = "来文单位", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fileTitle", value = "来文标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromFileCode", value = "来文文号", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "urgentLevel", value = "紧急程度", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "begDate", value = "填报开始时间(2017-11-11)", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "填报结束时间(2017-11-11)", required = false, dataType = "date", paramType = "query") })
	public Page<IoaReceiveFileDO> inReadList(HttpServletRequest request, HttpServletResponse response,
			PageBean pageBean, IoaReceiveFileVO obj) {
		Page<IoaReceiveFileDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			obj.setCreateUserId(user.getUserId());
			page = ioaReceiveFileService.inReadList(pageBean, obj, user.getUserId() + "");
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 收文阅件表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@ApiOperation("收文阅件存稿")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fileType", value = "来文类型", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fileTypeName", value = "来文类型名称", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromUnit", value = "来文单位", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromUnitName", value = "来文单位名称", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "receiveFileTime", value = "收文日期(2017-11-11)", required = true, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "orgnlFileTime", value = "原文日期(2017-11-11)", required = true, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "handleLimitTime", value = "办理时限(2017-11-11)", required = true, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "urgentLevel", value = "缓急", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "urgentLevelName", value = "缓急名称", required = true, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "fromFileCode", value = "来文文号", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "receiveFileCode", value = "收文编号", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fileTitle", value = "来文标题", required = true, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "resResourceList[0].id", value = "资源id", required = false, dataType = "string", paramType = "query"),

            @ApiImplicitParam(name = "resResourceList[1].id", value = "资源id", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query") })
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, IoaReceiveFileVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = ioaReceiveFileWebService.add(obj);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 * 
	 * 收文阅件表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	@ApiOperation("收文阅件删除")
	@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "int", paramType = "query")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, IoaReceiveFile obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = ioaReceiveFileService.delete(obj);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 * 
	 * 收文阅件表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	@ApiOperation("收文阅件编辑")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "fileTypeName", value = "来文类型名称", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromUnit", value = "来文单位", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromUnitName", value = "来文单位名称", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "receiveFileTime", value = "收文日期(2017-11-11)", required = true, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "orgnlFileTime", value = "原文日期(2017-11-11)", required = true, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "handleLimitTime", value = "办理时限(2017-11-11)", required = true, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "urgentLevel", value = "缓急", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "urgentLevelName", value = "缓急名称", required = true, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "fromFileCode", value = "来文文号", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "receiveFileCode", value = "收文编号", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fileTitle", value = "来文标题", required = true, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "resResourceList[0].id", value = "资源id", required = false, dataType = "string", paramType = "query"),

            @ApiImplicitParam(name = "resResourceList[1].id", value = "资源id", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query") })
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, IoaReceiveFileVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = ioaReceiveFileService.update(obj);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 * 
	 * 收文阅件表(根据ID获取对象).
	 *
	 * @return：IoaReceiveFileDO
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@ApiOperation("收文阅件详情")
	@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "int", paramType = "query")
	public Map<String, Object> getById(HttpServletRequest request, HttpServletResponse response, IoaReceiveFile obj) {
		// IoaReceiveFileDO ioaReceiveFileDO = null;
		Map<String, Object> resultMap = null;
		try {
			CustomUser user = getCustomDetail();
			obj.setCreateUserId(user.getUserId());
			resultMap = ioaReceiveFileService.detail(obj);
			// ioaReceiveFileDO = ioaReceiveFileService.detail(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return resultMap;
	}

	/**
	 *
	 * 收文阅件表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping(value = "/submit")
	@ResponseBody
	@ApiOperation("收文阅件新增")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fileType", value = "来文类型", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fileTypeName", value = "来文类型名称", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromUnit", value = "来文单位", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fromUnitName", value = "来文单位名称", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "receiveFileTime", value = "收文日期(2017-11-11)", required = true, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "orgnlFileTime", value = "原文日期(2017-11-11)", required = true, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "handleLimitTime", value = "办理时限(2017-11-11)", required = true, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "urgentLevel", value = "缓急", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "urgentLevelName", value = "缓急名称", required = true, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "fromFileCode", value = "来文文号", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "receiveFileCode", value = "收文编号", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fileTitle", value = "来文标题", required = true, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "resResourceList[0].id", value = "资源id", required = false, dataType = "string", paramType = "query"),

            @ApiImplicitParam(name = "resResourceList[1].id", value = "资源id", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query") })
	public RetMsg submit(HttpServletRequest request, HttpServletResponse response, IoaReceiveFileVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = ioaReceiveFileWebService.submit(obj);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 收文阅件 加载数据字典数据
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping(value = "/loadDictByCode")
	@ResponseBody
	@ApiOperation("加载数据字典数据")
	public Map<String, Object> loadDictByCode(HttpServletRequest request, HttpServletResponse response, String code) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = ioaReceiveFileService.loadDictByCode();
		} catch (Exception e) {
			logger.error("", e);
		}
		return map;
	}

	/**
	 *
	 * 根据流程实例ID获取节点信息.
	 *
	 * 					@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping(value = "/checkNextTaskInfo")
	@ResponseBody
	@ApiOperation("根据流程实例ID获取下个节点信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg checkNextTaskInfo(HttpServletRequest request, HttpServletResponse response, String processInstanceId,
			String receiveUserIds, String isCheckAllUser) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = ioaReceiveFileService.checkNextTaskInfo(processInstanceId, receiveUserIds, isCheckAllUser);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 根据流程实例ID获取下个节点信息.
	 *
	 * 						@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping(value = "/getNextTaskInfo")
	@ResponseBody
	@ApiOperation("根据流程实例ID获取下个节点信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "withCondition", value = "是否带条件", required = true, dataType = "string", paramType = "query") })
	public RetMsg getNextTaskInfo(HttpServletRequest request, HttpServletResponse response, String processInstanceId,
			boolean withCondition) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = ioaReceiveFileService.getNextTaskInfo(processInstanceId, false);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 完成任务.
	 *
	 * 			@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	@RequestMapping(value = "/completeTask")
	@ResponseBody
	@ApiOperation("审批完成任务")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "variables", value = "流程变量", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bizId", value = "主键ID", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "userId", value = "用户ID(多个分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "message", value = "审批意见", required = true, dataType = "string", paramType = "query")

	})
	public RetMsg completeTask(HttpServletRequest request, HttpServletResponse response, Map<String, Object> variables,
			String processInstanceId, String receiveFileId, String readUserIds, String message, String isEnd,String isOffice) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			retMsg = ioaReceiveFileWebService.completeTask(variables, processInstanceId, receiveFileId, readUserIds,
					message, user.getUserId() + "", isEnd,isOffice);
			if(retMsg.getObject()!=null){
			  AutUser autUser = new AutUser();
			  autUser.setId(user.getUserId());
			  autUser.setUserName(user.getUsername());
			  autUser.setFullName(user.getNickName());
			  @SuppressWarnings("unchecked")
			  Map<String,Object> map = (HashMap<String, Object>) retMsg.getObject();
              String handle = (String) map.get("handle");
              
              if(handle != null && StringUtils.isNotEmpty(handle)){//当下一级办理人不为空时发送待办消息
                  webTask.sendOnlineMsg4IoaRead(map, autUser);
              }
              retMsg.setObject(null);
            }
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 检查任务是否被签收.
	 *
	 * 				@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	@RequestMapping(value = "/checkIsClaim")
	@ResponseBody
	@ApiOperation("检查任务是否被签收")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg checkIsClaim(HttpServletRequest request, HttpServletResponse response, String processInstanceId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = ioaReceiveFileService.checkIsClaim(processInstanceId);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 撤回任务到上一节点.
	 *
	 * 				@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	@RequestMapping(value = "/jump2Task2")
	@ResponseBody
	@ApiOperation("退回任务到上一节点")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bizId", value = "主键ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg jump2Task2(HttpServletRequest request, HttpServletResponse response, String processInstanceId,
			String bizId, String message) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			retMsg = ioaReceiveFileWebService.jump2Task2(processInstanceId, bizId, message, user.getUserId());
			if(retMsg.getObject()!=null){
              AutUser autUser = new AutUser();
              autUser.setId(user.getUserId());
              autUser.setUserName(user.getUsername());
              autUser.setFullName(user.getNickName());
              @SuppressWarnings("unchecked")
              Map<String,Object> map = (HashMap<String, Object>) retMsg.getObject();
              String handle = (String) map.get("handle");
              if(handle != null && StringUtils.isNotEmpty(handle)){//当下一级办理人不为空时发送待办消息
                  webTask.sendOnlineMsg4IoaRead(map, autUser);
              }
              retMsg.setObject(null);
            }
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 获取上一节点信息.
	 *
	 * 				@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	@RequestMapping(value = "/getPreTaskInfo")
	@ResponseBody
	@ApiOperation("获取上一节点信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg getPreTaskInfo(HttpServletRequest request, HttpServletResponse response, String processInstanceId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = ioaReceiveFileService.getPreTaskInfo(processInstanceId);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 池主任审批详情
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping(value = "/directorDetail", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("根据流程实例ID获取下个节点信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "int", paramType = "query") })
	public IoaReceiveFileDO directorDetail(HttpServletRequest request, HttpServletResponse response,
			IoaReceiveFile obj) {
		IoaReceiveFileDO ioaReceiveFileDO = null;
		try {
		    CustomUser customUser = getCustomDetail();
		    AutUser user = new AutUser();
		    user.setId(customUser.getUserId());
		    user.setFullName(customUser.getNickName());
		    user.setUserName(customUser.getUsername());
			ioaReceiveFileDO = ioaReceiveFileService.directorDetail(obj,user);
		} catch (Exception e) {
			logger.error("", e);
		}
		return ioaReceiveFileDO;
	}

	/**
	 *
	 * 主任审批保存.
	 *
	 * 			@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	@RequestMapping(value = "/directorAudit")
	@ResponseBody
	@ApiOperation("主任审批保存")
	@ApiImplicitParams({
			// @ApiImplicitParam(name = "processInstanceId", value = "流程实例ID",
			// required = true, dataType = "string", paramType = "query")
	})
	public RetMsg directorAudit(HttpServletRequest request, HttpServletResponse response, IoaReceiveFileVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = ioaReceiveFileService.directorAudit(obj);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 流程日志
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年10月16日 下午5:34:17
	 */
	@RequestMapping(value = "/getAllTaskInfo")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("流程日志")
	public List<FixedFormProcessLog> getAllTaskInfo(String processInstanceId) {
		List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
		try {
			list = ioaReceiveFileService.getTaskLogInfo(processInstanceId);
		} catch (Exception e) {
			logger.error("", e);
		}
		return list;
	}

	/**
	 *
	 * 收文登记详情.
	 *
	 * 			@return：IoaReceiveFileDO
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-18
	 */
	@RequestMapping(value = "/readDetail")
	@ResponseBody
	@ApiOperation("收文阅件详情")
	@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "int", paramType = "query")
	public IoaReceiveFileDO readDetail(HttpServletRequest request, HttpServletResponse response, IoaReceiveFile obj) {
		IoaReceiveFileDO ioaReceiveFileDO = null;
		try {
			CustomUser user = getCustomDetail();
			obj.setCreateUserId(user.getUserId());
			ioaReceiveFileDO = ioaReceiveFileService.readDetail(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return ioaReceiveFileDO;
	}

	/**
	 *
	 * 收文登记详情.
	 *
	 * 			@return：IoaReceiveFileDO
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-18
	 */
	@RequestMapping(value = "/readHisDetail")
	@ResponseBody
	@ApiOperation("收文阅件详情")
	@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "int", paramType = "query")
	public IoaReceiveFileDO readHisDetail(HttpServletRequest request, HttpServletResponse response,
			IoaReceiveFile obj) {
		IoaReceiveFileDO ioaReceiveFileDO = null;
		try {
			CustomUser user = getCustomDetail();
			obj.setCreateUserId(user.getUserId());
			ioaReceiveFileDO = ioaReceiveFileService.readHisDetail(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return ioaReceiveFileDO;
	}

	/**
	 *
	 * 根据code获取人员列表
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年11月20日
	 */
	@RequestMapping(value = "/memberList")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dictCode", value = "数据字典code", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("根据code获取人员列表")
	public List<SysDataDict> memberList(String dictCode) {
		List<SysDataDict> list = new ArrayList<SysDataDict>();
		try {
			list = ioaReceiveFileService.memberList(dictCode);
		} catch (Exception e) {
			logger.error("", e);
		}
		return list;
	}

	/**
	 *
	 * 收文阅件流程作废.
	 *
	 * 				@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	@RequestMapping(value = "/void")
	@ResponseBody
	@ApiOperation("收文阅件流程作废")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bizId", value = "主键ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg toVoid(HttpServletRequest request, HttpServletResponse response, String processInstanceId,
			String bizId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = ioaReceiveFileService.toVoid(processInstanceId, bizId);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 流程日志
	 *
	 * @return：void
	 *
	 * @author：huangw
	 *
	 * @date：2017年12月08日 下午5:34:17
	 */
	@RequestMapping(value = "/getAllHisTaskInfo")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("流程日志")
	public List<FixedFormProcessLog> getAllHisTaskInfo(String processInstanceId) {
		List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
		try {
			list = ioaReceiveFileService.getTaskLogInfo(processInstanceId);
			if (list != null && list.size() > 0) {
				boolean flag = true;
				while (flag) {
					FixedFormProcessLog tmpDo = list.get(0);
					for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
						for (int j = 0; j < list.size() - i - 1; j++) {
							if (list.get(j + 1).getOperationTime().getTime() < list.get(j).getOperationTime()
									.getTime()) {
								tmpDo = list.get(j);
								list.set(j, list.get(j + 1));
								list.set(j + 1, tmpDo);
								flag = true;
							}
						}

					}
					if (!flag) {
						break;// 若果没有发生交换，则退出循环
					}
					flag = false;
				}
			
				FixedFormProcessLog tmplog=list.get(list.size()-1);
				tmplog.setDirection(tmplog.getDirection()+" ----> 归档");
				list.set(list.size()-1, tmplog);	
			}

		} catch (Exception e) {
			logger.error("", e);
		}
		return list;
	}

	/**
	 *
	 * 撤回任务到上一节点.
	 *
	 * 				@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	@RequestMapping(value = "/revoke2Task")
	@ResponseBody
	@ApiOperation("退回任务到上一节点")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bizId", value = "主键ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg revoke2Task(HttpServletRequest request, HttpServletResponse response, String processInstanceId,
			String bizId) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser customUser = getCustomDetail();
			AutUser user = new AutUser();
			user.setId(customUser.getUserId());
			user.setFullName(customUser.getNickName());
			user.setUserName(customUser.getUsername());
			retMsg = ioaReceiveFileService.revoke2Task(processInstanceId, bizId, user);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
}
