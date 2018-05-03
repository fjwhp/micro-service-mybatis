package aljoin.app.controller.pub;

import aljoin.app.controller.BaseController;
import aljoin.app.task.WebTask;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.pub.dao.entity.PubPublicInfo;
import aljoin.pub.dao.entity.PubPublicInfoCategory;
import aljoin.pub.dao.object.AppPubPublicInfoDO;
import aljoin.pub.dao.object.AppPubPublicInfoVO;
import aljoin.pub.iservice.PubPublicInfoCategoryService;
import aljoin.pub.iservice.PubPublicInfoService;
import aljoin.web.service.act.ActFixedFormWebService;
import aljoin.web.service.pub.PubPublicInfoWebService;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
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

@Controller
@RequestMapping(value = "/app/pub/pubPublicInfo", method = RequestMethod.POST)
@Api(value = "公共信息Controller", description = "公共信息相关接口")
public class AppPubPublicController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(AppPubPublicController.class);

	@Resource
	private PubPublicInfoService pubPublicInfoService;

	@Resource
	private PubPublicInfoCategoryService pubPublicInfoCategoryService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private WebTask webTask;
	@Resource
	private ActFixedFormWebService actFixedFormWebService;
	@Resource
	private PubPublicInfoWebService pubPublicInfoWebService;
	/**
	 *
	 * 最新信息表(分页列表).
	 *
	 * @return：Page<AppPubPublicInfoDO>
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-09
	 */
	@RequestMapping(value = "/pageList")
	@ResponseBody
	@ApiOperation("最新信息分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "categoryId", value = "类型ID（下拉）", required = false, dataType = "long", paramType = "query"),
			@ApiImplicitParam(name = "title", value = "名称", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "publishName", value = "发布人", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "begDate", value = "开始发布时间", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "结束发布时间", required = false, dataType = "date", paramType = "query") })
	public RetMsg pageList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			AppPubPublicInfoVO obj) {
		RetMsg retMsg = new RetMsg();
		Page<AppPubPublicInfoDO> page = null;
		try {
			AutAppUserLogin user = getAppUserLogin(request);
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			page = pubPublicInfoService.lastList(pageBean, obj);
			retMsg.setObject(page);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("App公共信息查询错误", e);
		}
		return retMsg;
	}

	/**
	 *
	 * 最新信息表(不分页列表).
	 *
	 * @return：List<AppPubPublicInfoDO>
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-09
	 */
	@RequestMapping(value = "/lastList")
	@ResponseBody
	@ApiOperation("最新信息分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "categoryId", value = "类型ID（下拉）", required = false, dataType = "long", paramType = "query"),
			@ApiImplicitParam(name = "title", value = "名称", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "publishName", value = "发布人", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "createTime", value = "发布时间", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "begDate", value = "开始发布时间", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "结束发布时间", required = false, dataType = "date", paramType = "query") })
	public RetMsg lastList(HttpServletRequest request, HttpServletResponse response, AppPubPublicInfoVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin user = getAppUserLogin(request);
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			retMsg = pubPublicInfoService.lastList(obj);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("操作异常");
		}
		return retMsg;
	}

	/**
	 *
	 * 公共信息分类表.
	 *
	 * 				@return：Page<PubPublicInfoCategory>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-16
	 */
	@RequestMapping(value = "/categoryList")
	@ResponseBody
	@ApiOperation("公共信息分类列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(value = "字段说明:", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "分类ID", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "name", value = "分类名称", required = false, dataType = "date", paramType = "query") })
	public RetMsg validList(HttpServletRequest request, HttpServletResponse response, PubPublicInfoCategory obj) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin user = getAppUserLogin(request);
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			retMsg = pubPublicInfoCategoryService.categoryList(obj);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("操作异常");
		}
		return retMsg;
	}

	/**
	 *
	 * 公共信息详情(根据ID获取对象).
	 *
	 * @return：PubPublicInfoVO
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-09
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@ApiOperation("公共信息详情")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "int", paramType = "query") })
	public RetMsg getById(HttpServletRequest request, HttpServletResponse response, PubPublicInfo obj) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin user = getAppUserLogin(request);
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			retMsg = pubPublicInfoService.getDetailById(obj);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("操作异常");
		}
		return retMsg;
	}

	/**
	 *
	 * 检查当前节点下一个节点是显示组织机构还是人员
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-06
	 */
	@RequestMapping(value = "/checkNextTaskInfo")
	@ResponseBody
	@ApiOperation("检查当前节点下一个节点是显示组织机构还是人员")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg checkNextTaskInfo(String taskId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = pubPublicInfoService.checkAppNextTaskInfo(taskId);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 审批
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017年9月7日 下午2:04:43
	 */
	@RequestMapping("/completeTask")
	@ResponseBody
	@ApiOperation("完成任务")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "bizId", value = "业务主键ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "userId", value = "用户ID(单个或多个均要加分号分隔)", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "message", value = "意见", required = false, dataType = "string", paramType = "query")
	})
	public RetMsg completeTask(HttpServletRequest request, HttpServletResponse response,
														 String taskId, String bizId, String userId, String message) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin user = getAppUserLogin(request);
			AutUser autUser = autUserService.selectById(user.getUserId());
			Map<String, Object> variables  = new  HashMap<String,Object>();
			retMsg = pubPublicInfoWebService.completeAppTask(variables, taskId, bizId, userId, message, autUser);
			if (retMsg.getObject() != null) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) retMsg.getObject();
				String handle = map.get("handle");
				if (handle != null && StringUtils.isNotEmpty(handle)) {// 当下一级办理人不为空时发送待办消息
					String processInstanceId = map.get("processInstanceId");
					webTask.sendOnlineMsg(processInstanceId, handle, autUser);
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
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("固定流程回退")
	public RetMsg jump2Task2(HttpServletRequest request,String taskId, String message) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin user = getAppUserLogin(request);
			retMsg = pubPublicInfoWebService.jump2AppTask2(taskId, message, user.getUserId());
			if (retMsg.getObject() != null) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) retMsg.getObject();
				String handle = map.get("handle");
				if (handle != null && StringUtils.isNotEmpty(handle)) {// 当下一级办理人不为空时发送待办消息
					String processInstanceId = map.get("processInstanceId");
					AutUser autUser = autUserService.selectById(user.getUserId());
					webTask.sendOnlineMsg(processInstanceId, handle, autUser);
				}
				retMsg.setObject(null);
			}
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 撤回
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	@RequestMapping(value = "/revoke")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("固定流程撤回")
	public RetMsg revoke(HttpServletRequest request,String processInstanceId) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin user = getAppUserLogin(request);
			retMsg = actFixedFormWebService.jump2AppTask2(processInstanceId, user.getUserId());
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
}
