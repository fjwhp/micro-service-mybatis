package aljoin.web.controller.pub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.pub.dao.entity.PubPublicInfo;
import aljoin.pub.dao.entity.PubPublicInfoCategory;
import aljoin.pub.dao.object.PubPublicInfoDO;
import aljoin.pub.dao.object.PubPublicInfoVO;
import aljoin.pub.iservice.PubPublicInfoCategoryService;
import aljoin.pub.iservice.PubPublicInfoService;
import aljoin.web.controller.BaseController;
import aljoin.web.service.pub.PubPublicInfoWebService;
import aljoin.web.task.WebTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 公共信息表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-12
 */
@Controller
@RequestMapping(value = "/pub/pubPublicInfo", method = RequestMethod.POST)
@Api(value = "公共信息Controller", description = "公共信息->我的信息相关接口")
public class PubPublicInfoController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(PubPublicInfoController.class);
	@Resource
	private PubPublicInfoService pubPublicInfoService;
	@Resource
	private PubPublicInfoWebService pubPublicInfoWebService;
	@Resource
	private WebTask webTask;
	@Resource
	private PubPublicInfoCategoryService pubPublicInfoCategoryService;
	@Resource
	private ActAljoinBpmnService actAljoinBpmnService;
	/**
	 *
	 * 我的信息(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/pubMinePage", method = RequestMethod.GET)
	@ApiOperation("我的信息页面")
	public String pubPublicInfoPage(HttpServletRequest request, HttpServletResponse response) {

		return "pub/pubMinePage";
	}

	/**
	 * 我的信息(页面).
	 *
	 * @return：String
	 *
	 * @author：zhengls
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping(value = "/pubMinePageEdit", method = RequestMethod.GET)
	@ApiOperation("我的信息草稿箱编辑(页面)")
	public String pubPublicInfoPageEdit(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String isDraft = request.getParameter("isDraft");

		request.setAttribute("t_id", id);
		request.setAttribute("t_isDraft", isDraft);

		return "pub/pubMinePageEdit";
	}

	/**
	 *
	 * 公共信息表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@ApiOperation("公共信息新增")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "title", value = "名称", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "publishName", value = "发布人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "categoryId", value = "类型ID（下拉）", required = false, dataType = "long", paramType = "query"),
			@ApiImplicitParam(name = "period", value = "有效期", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "noticeObjId", value = "公告对象ID （多个用分号分隔", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "noticeObjName", value = "公告对象 （多个用分号分隔）", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "content", value = "公告内容", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "resResourceList[0].id", value = "资源id", required = false, dataType = "string", paramType = "query"),

            @ApiImplicitParam(name = "resResourceList[1].id", value = "资源id", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query")

	})
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, PubPublicInfoVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			if (StringUtils.isEmpty(obj.getContent())) {
				obj.setContent("");
				retMsg.setCode(1);
				retMsg.setMessage("内容不能为空");
				return retMsg;
			}
			if (null == obj.getPeriod()) {
				obj.setPeriod(0);
				retMsg.setCode(1);
				retMsg.setMessage("有效期不能为空");
				return retMsg;
			}
			if (null == obj.getCategoryId()) {
				obj.setCategoryId(0L);
				retMsg.setCode(1);
				retMsg.setMessage("分类不能为空");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getTitle())) {
				obj.setTitle("");
				retMsg.setCode(1);
				retMsg.setMessage("标题不能为空");
				return retMsg;
			} else {
				if (obj.getTitle().length() > 200) {
					retMsg.setCode(1);
					retMsg.setMessage("标题长度不能超过200个字符");
					return retMsg;
				}
			}
			if (StringUtils.isEmpty(obj.getPublishName())) {
				obj.setPublishName("");
				retMsg.setCode(1);
				retMsg.setMessage("发布人不能为空");
				return retMsg;
			} else {
				if (obj.getPublishName().length() > 200) {
					retMsg.setCode(1);
					retMsg.setMessage("发布人长度不能超过200个字符");
					return retMsg;
				}
			}

			if (null == obj.getPeriod()) {
				obj.setPeriod(0);
				retMsg.setCode(1);
				retMsg.setMessage("有效期不能为空");
				return retMsg;
			} else {
				if (String.valueOf(obj.getPeriod()).length() > 10) {
					retMsg.setCode(1);
					retMsg.setMessage("有效期不能超过10位数");
					return retMsg;
				}
			}
			CustomUser user = getCustomDetail();
			if (null != user && null != user.getUserId()) {
				obj.setCreateUserId(user.getUserId());
			}
			ActAljoinBpmn bpmn = null;
			Where<PubPublicInfoCategory> where = new Where<PubPublicInfoCategory>();
			where.eq("id", obj.getCategoryId());
			where.eq("is_use", 1);
			where.setSqlSelect("id,name,category_rank,process_id,process_name,is_use");
			PubPublicInfoCategory category = pubPublicInfoCategoryService.selectOne(where);
			if (null != category) {
				if (category.getProcessId() != null) {// 公告类型是否使用流程
					Where<ActAljoinBpmn> bpmnWhere = new Where<ActAljoinBpmn>();
					bpmnWhere.eq("process_id", category.getProcessId());
					bpmnWhere.eq("is_deploy", 1);
					bpmnWhere.eq("is_active", 1);
					bpmn = actAljoinBpmnService.selectOne(bpmnWhere);
					if (null != bpmn) {
						obj.setAuditStatus(1);// 审核中
						obj.setProcessId(bpmn.getProcessId());
						retMsg = pubPublicInfoWebService.add(obj, bpmn);// 走审核的不需要异步
					} else {
						retMsg.setCode(1);
						retMsg.setMessage("该流程已失效，请更新该分类的流程信息");
						return retMsg;
					}
				}
			} else {
				webTask.pubPublicInfoTask(obj);
			}
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 我的信息表(分页列表).
	 *
	 * @return：Page<PubPublicInfo>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	@ApiOperation("我的信息分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "categoryId", value = "类型ID（下拉）", required = false, dataType = "long", paramType = "query"),
			@ApiImplicitParam(name = "title", value = "名称", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "auditStatus", value = "审核情况", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "periodStatus", value = "有效期", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "publishName", value = "发布人", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "createTime", value = "日期", required = false, dataType = "date", paramType = "query") })
	public Page<PubPublicInfoDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			PubPublicInfoVO obj) {
		Page<PubPublicInfoDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			page = pubPublicInfoService.list(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 公共信息表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@ApiOperation("公共信息删除")
	@ApiImplicitParam(name = "ids", value = "主键(支持批量删除，删除多个用分号分隔)", required = true, dataType = "int", paramType = "query")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, PubPublicInfoVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = pubPublicInfoService.delete(obj);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 公共信息表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	@ApiOperation("公共信息编辑")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "title", value = "名称", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "publishName", value = "发布人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "categoryId", value = "类型ID（下拉）", required = false, dataType = "long", paramType = "query"),
			@ApiImplicitParam(name = "period", value = "有效期", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "noticeObjId", value = "公告对象ID （多个用分号分隔", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "noticeObjName", value = "公告对象 （多个用分号分隔）", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "content", value = "公告内容", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "attachmentList[0].attachName", value = "附件名称(原文件名)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[0].attachPath", value = "上传后的完整路径", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[0].attachSize", value = "附件大小(KB)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "attachmentList[1].attachName", value = "附件名称(原文件名)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[1].attachPath", value = "上传后的完整路径", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[1].attachSize", value = "附件大小(KB)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query")

	})
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, PubPublicInfoVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = pubPublicInfoService.update(obj);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		// PubPublicInfo orgnlObj =
		// pubPublicInfoService.selectById(obj.getId());
		return retMsg;
	}

	/**
	 *
	 * 公共信息表(根据ID获取对象).
	 *
	 * @return：PubPublicInfoVO
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@ApiOperation("公共信息详情")
	@ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "query")
	public PubPublicInfoVO getById(HttpServletRequest request, HttpServletResponse response, PubPublicInfo obj) {
		PubPublicInfoVO pubPublicInfoVO = null;
		try {
			CustomUser user = getCustomDetail();
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			pubPublicInfoVO = pubPublicInfoService.detail(obj);
			AutUser user1 = new AutUser();
			user1.setId(user.getUserId());
			webTask.pubDetail(pubPublicInfoVO, user1);
		} catch (Exception e) {
			logger.error("", e);
		}
		return pubPublicInfoVO;
	}
	/**
	 *
	 * 公共信息表(根据ID获取对象).
	 *
	 * @return：PubPublicInfoVO
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/getByHisId")
	@ResponseBody
	@ApiOperation("公共信息详情")
	@ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "query")
	public PubPublicInfoVO getByHisId(HttpServletRequest request, HttpServletResponse response, PubPublicInfo obj) {
		PubPublicInfoVO pubPublicInfoVO = null;
		try {
			CustomUser user = getCustomDetail();
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			pubPublicInfoVO = pubPublicInfoService.detail(obj);
			AutUser user1 = new AutUser();
			user1.setId(user.getUserId());
			//webTask.pubDetail(pubPublicInfoVO, user1);
		} catch (Exception e) {
			logger.error("", e);
		}
		return pubPublicInfoVO;
	}

	/**
	 *
	 * 获取当前登录用户信息.
	 *
	 * 				@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/getCurUserInfo")
	@ResponseBody
	@ApiOperation("获取当前登录用户信息")
	public RetMsg getCurUserInfo(HttpServletRequest request, HttpServletResponse response, PubPublicInfo obj) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			retMsg.setCode(0);
            retMsg.setMessage("操作成功");
            retMsg.setObject(user);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 综合信息表(分页列表).
	 *
	 * @return：Page<PubPublicInfoDO>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/mexList")
	@ResponseBody
	@ApiOperation("综合信息分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "categoryId", value = "类型ID（下拉）", required = false, dataType = "long", paramType = "query"),
			@ApiImplicitParam(name = "title", value = "名称", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "auditStatus", value = "审核情况", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "periodStatus", value = "有效期", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "publishName", value = "发布人", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "createTime", value = "日期", required = false, dataType = "date", paramType = "query") })
	public Page<PubPublicInfoDO> mexList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			PubPublicInfoVO obj) {
		Page<PubPublicInfoDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			page = pubPublicInfoService.multipleList(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 最新信息(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/pubNewPage", method = RequestMethod.GET)
	@ApiOperation("最新信息页面")
	public String pubNewPage(HttpServletRequest request, HttpServletResponse response) {

		return "pub/pubNewPage";
	}

	/**
	 *
	 * 综合信息(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/pubMexPage", method = RequestMethod.GET)
	@ApiOperation("综合信息页面")
	public String pubMexPage(HttpServletRequest request, HttpServletResponse response) {

		return "pub/pubMexPage";
	}

	/**
	 *
	 * 根据流程分类ID获得对应流程列表.
	 *
	 * 						@return：List<ActAljoinBpmn>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-22
	 */
	@RequestMapping(value = "/bpmnList")
	@ResponseBody
	@ApiOperation(value = "根据流程分类ID获得对应流程列表接口", notes = "根据流程分类ID获得对应流程列表接口")
	@ApiImplicitParam(name = "id", value = "流程分类ID", required = true, dataType = "int", paramType = "query")
	public List<ActAljoinBpmn> bpmnList(HttpServletRequest request, HttpServletResponse response,
			ActAljoinCategory obj) {
		List<ActAljoinBpmn> bpmnList = null;
		try {
			bpmnList = pubPublicInfoService.getBpmnByCategroyId(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return bpmnList;
	}

	/**
	 *
	 * 撰写邮件上传附件.
	 *
	 * 				@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-25
	 */
	/*@RequestMapping(value = "/upload")
	@ResponseBody
	@ApiOperation(value = "新增公共信息文件上传接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "async", value = "是否同步(true或fase)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "fileModuleName", value = "功能对应的模块名称", required = false, dataType = "string", paramType = "query") })
	public RetMsg upload(MultipartHttpServletRequest request, boolean async, String fileModuleName) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = pubPublicInfoService.upload(request, async, fileModuleName);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("上传失败");
		}
		return retMsg;
	}*/

	/**
	 *
	 * 文件下载
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017年9月7日 下午2:04:43
	 */
	/*@RequestMapping(value = "/download")
	@ResponseBody
	public RetMsg download(HttpServletRequest request, HttpServletResponse response, String name, String resourceName) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = pubPublicInfoService.download(response, name, resourceName);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;

	}*/

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
	public RetMsg completeTask(HttpServletRequest request, HttpServletResponse response, Map<String, Object> variables,
			String taskId, String bizId, String userId, String message) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			AutUser autUser = new AutUser();
			autUser.setId(user.getUserId());
			autUser.setFullName(user.getNickName());
			autUser.setUserName(user.getUsername());
			retMsg = pubPublicInfoWebService.completeTask(variables, taskId, bizId, userId, message, autUser);
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
	 * 检查当前节点下一个节点是显示组织机构还是人员
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-06
	 */
	@RequestMapping(value = "/checkNextTaskInfo")
	@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query")
	@ResponseBody
	@ApiOperation("检查当前节点下一个节点是显示组织机构还是人员")
	public RetMsg checkNextTaskInfo(String taskId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = pubPublicInfoService.checkNextTaskInfo(taskId);
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
	 * @date：2017年9月11日 下午5:34:17
	 */
	@RequestMapping(value = "/getAllTaskInfo")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("流程日志")
	public List<FixedFormProcessLog> getAllTaskInfo2(String taskId, String processName, String processInstanceId) {
		List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
		try {
			list = pubPublicInfoService.getAllTaskInfo(taskId, processInstanceId);
		} catch (Exception e) {
			logger.error("", e);
		}
		return list;
	}

	/**
	 *
	 * 最新信息表(分页列表).
	 *
	 * @return：Page<PubPublicInfoDO>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/lastList")
	@ResponseBody
	@ApiOperation("最新信息分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "categoryId", value = "类型ID（下拉）", required = false, dataType = "long", paramType = "query"),
			@ApiImplicitParam(name = "title", value = "名称", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "publishName", value = "发布人", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "auditStatus", value = "审核情况", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "periodStatus", value = "有效期", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "createTime", value = "日期", required = false, dataType = "date", paramType = "query") })
	public Page<PubPublicInfoDO> lastList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			PubPublicInfoVO obj) {
		Page<PubPublicInfoDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			page = pubPublicInfoService.lastList(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 最新信息已读未读
	 *
	 * @return：Page<PubPublicInfoDO>
	 *
	 * @author：xuc
	 *
	 * @date：2017年11月27日 上午10:59:20
	 */
	@RequestMapping(value = "/lastListRead")
	@ResponseBody
	@ApiOperation("最新信息分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "categoryId", value = "类型ID（下拉）", required = false, dataType = "long", paramType = "query"),
			@ApiImplicitParam(name = "title", value = "名称", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "publishName", value = "发布人", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "auditStatus", value = "审核情况", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "periodStatus", value = "有效期", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "createTime", value = "日期", required = false, dataType = "date", paramType = "query") })
	public Page<PubPublicInfoDO> lastListRead(HttpServletRequest request, HttpServletResponse response,
			PageBean pageBean, PubPublicInfoVO obj) {
		Page<PubPublicInfoDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			page = pubPublicInfoService.lastListRead(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 公共信息流程作废.
	 *
	 * 				@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	@RequestMapping(value = "/void")
	@ResponseBody
	@ApiOperation("公共信息流程作废")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bizId", value = "主键ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg toVoid(HttpServletRequest request, HttpServletResponse response, String taskId, String bizId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = pubPublicInfoService.toVoid(taskId, bizId, getCustomDetail().getUserId());
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 获取上一个节点信息
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-06
	 */
	@RequestMapping(value = "/getPreTaskInfo")
	@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query")
	@ResponseBody
	@ApiOperation("获取上一个节点信息")
	public RetMsg getPreTaskInfo(String taskId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = pubPublicInfoService.getPreTaskInfo(taskId);
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
			@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bizId", value = "业务主键ID(多个用分分隔)", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("回退")
	public RetMsg jump2Task2(String taskId, String bizId, String message) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			retMsg = pubPublicInfoWebService.jump2Task2(taskId, bizId, message, user.getUserId());
			if (retMsg.getObject() != null) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) retMsg.getObject();
				String handle = map.get("handle");
				if (handle != null && StringUtils.isNotEmpty(handle)) {// 当下一级办理人不为空时发送待办消息
					String processInstanceId = map.get("processInstanceId");
					AutUser autUser = new AutUser();
					autUser.setId(user.getUserId());
					autUser.setFullName(user.getNickName());
					autUser.setUserName(user.getUsername());
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
	 * 流程日志
	 *
	 * @return：void
	 *
	 * @author：huangw
	 *
	 * @date：2017年9月11日 下午5:34:17
	 */
	@RequestMapping(value = "/getAllHisTaskInfo")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId", value = "实例ID", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("流程日志")
	public List<FixedFormProcessLog> getAllHisTaskInfo(String taskId, String processName) {
		List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
		try {
			// list = pubPublicInfoService.getAllPinsInfo(taskId);
			list = pubPublicInfoService.getAllTaskInfo(null, taskId);
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
	
}
