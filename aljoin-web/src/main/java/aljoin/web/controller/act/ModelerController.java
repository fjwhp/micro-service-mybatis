package aljoin.web.controller.act;

import aljoin.act.dao.entity.*;
import aljoin.act.iservice.*;
import aljoin.act.service.util.ActConstant;
import aljoin.dao.config.Where;
import aljoin.object.RetMsg;
import aljoin.util.DateUtil;
import aljoin.util.StringUtil;
import aljoin.web.controller.BaseController;
import aljoin.web.service.act.ActAljoinBpmnWebService;

import com.baomidou.mybatisplus.toolkit.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 *
 * 流程表单控制器
 *
 * @author：zhongjy
 *
 * @date：2017年7月25日 下午4:00:56
 */
@Controller
@RequestMapping("/act/modeler")
public class ModelerController extends BaseController{
	private final static Logger logger = LoggerFactory.getLogger(ModelerController.class);

	@Resource
	private ActAljoinCategoryService actAljoinCategoryService;
	@Resource
	private ActAljoinBpmnService actAljoinBpmnService;
	@Resource
	private ActAljoinFormCategoryService actAljoinFormCategoryService;
	@Resource
	private ActAljoinFormService actAljoinFormService;
	@Resource
	private ActAljoinFormWidgetService actAljoinFormWidgetService;
	@Resource
	private ActAljoinBpmnWebService actAljoinBpmnWebService;

	@RequestMapping("/modelerPage")
	public String modelerPage(HttpServletRequest request, HttpServletResponse response) {
		return "act/modelerPage";
	}

	/**
	 *
	 * 自定义流程页面(流程设计)
	 *
	 * 					@return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年7月6日 上午10:17:48
	 */
	@RequestMapping("/modelerPage2")
	public String modelerPage2(HttpServletRequest request, HttpServletResponse response, ActAljoinBpmn actAljoinBpmn) {
		// 获取一级流程分类
		Where<ActAljoinCategory> where = new Where<ActAljoinCategory>();
		where.eq("is_active", 1);
		where.eq("category_level", 1);
		where.orderBy("category_rank", true);
		where.setSqlSelect("id,category_rank,category_name,parent_id,category_level");
		List<ActAljoinCategory> categoryList = actAljoinCategoryService.selectList(where);
		request.setAttribute("categoryList", categoryList);
		// 获取一级表单分类
		Where<ActAljoinFormCategory> fwhere = new Where<ActAljoinFormCategory>();
		fwhere.eq("is_active", 1);
		fwhere.eq("category_level", 1);
		fwhere.orderBy("category_rank", true);
		fwhere.setSqlSelect("id,category_rank,category_name,parent_id,category_level");
		List<ActAljoinFormCategory> formCategoryList = actAljoinFormCategoryService.selectList(fwhere);
		request.setAttribute("formCategoryList", formCategoryList);

		request.setAttribute("isEdit", 0);
		return "act/modelerPage2";
	}

	/**
	 *
	 * 自定义流程页面(流程编辑)
	 *
	 * 					@return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年7月6日 上午10:17:48
	 */
	@RequestMapping("/modelerPage3")
	public String modelerPage3(HttpServletRequest request, HttpServletResponse response, ActAljoinBpmn actAljoinBpmn) {
		// 获取一级流程分类
		Where<ActAljoinCategory> where = new Where<ActAljoinCategory>();
		where.eq("is_active", 1);
		where.eq("category_level", 1);
		where.orderBy("category_rank", true);
		where.setSqlSelect("id,category_rank,category_name,parent_id,category_level");
		List<ActAljoinCategory> categoryList = actAljoinCategoryService.selectList(where);
		request.setAttribute("categoryList", categoryList);
		// 获取一级表单分类
		Where<ActAljoinFormCategory> fwhere = new Where<ActAljoinFormCategory>();
		fwhere.eq("is_active", 1);
		fwhere.eq("category_level", 1);
		fwhere.orderBy("category_rank", true);
		fwhere.setSqlSelect("id,category_rank,category_name,parent_id,category_level");
		List<ActAljoinFormCategory> formCategoryList = actAljoinFormCategoryService.selectList(fwhere);
		request.setAttribute("formCategoryList", formCategoryList);

		// 编辑
		actAljoinBpmn = actAljoinBpmnService.selectById(actAljoinBpmn.getId());
		request.setAttribute("orgnlActAljoinBpmn", actAljoinBpmn);
		request.setAttribute("isEdit", 1);
		// 所有所有父级分类ID
		ActAljoinCategory actAljoinCategory = actAljoinCategoryService.selectById(actAljoinBpmn.getCategoryId());
		List<String> categoryIds = new ArrayList<String>();
		if (actAljoinCategory.getCategoryLevel() == 1) {
			categoryIds.add(actAljoinCategory.getId() + "");
		} else if (actAljoinCategory.getCategoryLevel() == 2) {
			categoryIds.add(actAljoinCategory.getId() + "");
			categoryIds.add(actAljoinCategory.getParentId() + "");
		} else {
			// 层级大于2
			categoryIds.add(actAljoinCategory.getId() + "");
			categoryIds.add(actAljoinCategory.getParentId() + "");
			int len = actAljoinCategory.getCategoryLevel() - 2;
			for (int i = 0; i < len; i++) {
				actAljoinCategory = actAljoinCategoryService.selectById(actAljoinCategory.getParentId());
				if (actAljoinCategory != null && actAljoinCategory.getCategoryLevel() > 1) {
					categoryIds.add(actAljoinCategory.getParentId() + "");
				} else {
					break;
				}
			}
		}
		// 倒序
		Collections.reverse(categoryIds);
		request.setAttribute("categoryIds", categoryIds);
		request.setAttribute("categoryIdsArrStr", StringUtil.list2str(categoryIds, ","));
		return "act/modelerPage3";
	}

	/**
	 *
	 * 保存流程文件
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年7月6日 上午10:18:27
	 */
	@RequestMapping("/saveXmlCode")
	@ResponseBody
	public RetMsg saveXmlCode(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		// 流程文件
		String xmlCode = request.getParameter("xmlCode");
		// 流程分类
		String processCategory = request.getParameter("processCategory");
		String[] processCategoryArr = processCategory.split("-");
		processCategory = processCategoryArr[processCategoryArr.length - 1];
		// 流程ID
		String processId = request.getParameter("processId");
		// 流程名称
		String processName = request.getParameter("processName");
		// 流程描述
		String processDesc = request.getParameter("processDesc");

		// 页面表单备份元素html
		String backupHtml = request.getParameter("backupHtml");

		// 表单---表单ID
		String formIds = request.getParameter("formIds");
		// 表单---元素类型
		String elementTypes = request.getParameter("elementTypes");
		// 表单---元素id
		String elementIds = request.getParameter("elementIds");

		// 授权---任务id，逗号分隔
		String taskIds = request.getParameter("taskIds");
		// 授权---部门id，逗号&分号分隔
		String assigneeDepartmentIds = request.getParameter("assigneeDepartmentIds");
		// 授权---岗位id，逗号&分号分隔
		String assigneePositionIds = request.getParameter("assigneePositionIds");
		// 授权---受理人用户id，逗号&分号分隔
		String assigneeUserIds = request.getParameter("assigneeUserIds");
		// 授权---候选用户id，逗号&分号分隔
		String assigneeCandidateIds = request.getParameter("assigneeCandidateIds");
		// 授权---候选组id，逗号&分号分隔
		String assigneeGroupIds = request.getParameter("assigneeGroupIds");
		// 授权---任务表单权限（可视控件ID，逗号&分号分隔）
		String showWidgetIds = request.getParameter("showWidgetIds");
		// 授权---任务表单权限（可编辑控件ID，逗号&分号分隔）
		String editWidgetIds = request.getParameter("editWidgetIds");
		// 授权---任务表单权限（不允许空控件ID，逗号&分号分隔）
		String notNullWidgetIds = request.getParameter("notNullWidgetIds");
		// 授权---任务表单权限（意见域控件ID，逗号&分号分隔）
		String commentWidgetIds = request.getParameter("commentWidgetIds");
		// 授权---办理权限ID，逗号&分号分隔
		String aljoinTaskOperateAuthIds = request.getParameter("aljoinTaskOperateAuthIds");
		// 授权---是否返回流程发起人，分号分割
		String isReturnCreaters = request.getParameter("isReturnCreaters");

		// 红头文件---控件ID，逗号&分号分隔
		String redHeadWidgetIds = request.getParameter("redHeadWidgetIds");
		// 痕迹保留---控件ID，逗号&分号分隔
		String saveMarkWidgetIds = request.getParameter("saveMarkWidgetIds");

		// 是否固定表单
		String aljoinProcessIsfixed = request.getParameter("aljoinProcessIsfixed");
		// 是否自由流程
		String aljoinProcessIsfree = request.getParameter("aljoinProcessIsfree");
		// 授权---任务表单权限（加签意见域控件ID，逗号&分号分隔）
		String signCommentWidgetIds = request.getParameter("addSignCommentWidgetIds");
		// 授权---创建人所在部门人员，分号分割
		String staffMembersDepartments = request.getParameter("staffMembersDepartments");
		// 授权---上一个环节办理人人所在部门人员，分号分割
		String lastlinkDepartments = request.getParameter("lastlinkDepartments");
		// 授权---创建人所在岗位人员办理，分号分割
		String createPersonsJobs = request.getParameter("createPersonsJobs");

		try {
			Map<String, String> param = new HashMap<String, String>();
			param.put("xmlCode", xmlCode);
			param.put("processCategory", processCategory);
			param.put("processId", processId);
			param.put("processName", processName);
			param.put("processDesc", processDesc);
			param.put("formIds", formIds);
			param.put("elementTypes", elementTypes);
			param.put("elementIds", elementIds);
			param.put("taskIds", taskIds);
			param.put("assigneeDepartmentIds", assigneeDepartmentIds);
			param.put("assigneePositionIds", assigneePositionIds);
			param.put("assigneeUserIds", assigneeUserIds);
			param.put("assigneeCandidateIds", assigneeCandidateIds);
			param.put("assigneeGroupIds", assigneeGroupIds);
			param.put("backupHtml", backupHtml);
			param.put("showWidgetIds", showWidgetIds);
			param.put("editWidgetIds", editWidgetIds);
			param.put("notNullWidgetIds", notNullWidgetIds);
			param.put("aljoinTaskOperateAuthIds", aljoinTaskOperateAuthIds);
			param.put("commentWidgetIds", commentWidgetIds);
			param.put("aljoinProcessIsfixed", aljoinProcessIsfixed);
			param.put("aljoinProcessIsfree", aljoinProcessIsfree);
			param.put("redHeadWidgetIds", redHeadWidgetIds);
			param.put("saveMarkWidgetIds", saveMarkWidgetIds);
			param.put("isReturnCreaters", isReturnCreaters);
			param.put("signCommentWidgetIds", signCommentWidgetIds);
			param.put("staffMembersDepartments", staffMembersDepartments);
			param.put("lastlinkDepartments", lastlinkDepartments);
			param.put("createPersonsJobs", createPersonsJobs);
			retMsg = actAljoinBpmnService.saveXmlCode(param);
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
			logger.error("保存流程文件", e);
		}
		return retMsg;
	}

	/**
	 *
	 * 导出流程文件(没有保存的时候)
	 *
	 * 					@return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年7月6日 上午10:18:27
	 */
	@RequestMapping("/exportXmlCode")
	@ResponseBody
	public RetMsg exportXmlCode(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		// 流程文件
		String xmlCode = request.getParameter("xmlCode");
		// 流程分类
		String processCategory = request.getParameter("processCategory");
		String[] processCategoryArr = processCategory.split("-");
		processCategory = processCategoryArr[processCategoryArr.length - 1];
		// 流程ID
		String processId = request.getParameter("processId");
		// 流程名称
		String processName = request.getParameter("processName");

		// 授权---任务id，逗号分隔
		String taskIds = request.getParameter("taskIds");
		// 授权---部门id，逗号&分号分隔
		String assigneeDepartmentIds = request.getParameter("assigneeDepartmentIds");
		// 授权---岗位id，逗号&分号分隔
		String assigneePositionIds = request.getParameter("assigneePositionIds");
		// 授权---受理人用户id，逗号&分号分隔
		String assigneeUserIds = request.getParameter("assigneeUserIds");
		// 授权---候选用户id，逗号&分号分隔
		String assigneeCandidateIds = request.getParameter("assigneeCandidateIds");
		// 授权---候选组id，逗号&分号分隔
		String assigneeGroupIds = request.getParameter("assigneeGroupIds");

		try {
			retMsg = actAljoinBpmnService.exportXmlCode(xmlCode, processId, processName, taskIds, assigneeDepartmentIds,
				assigneePositionIds, assigneeUserIds, assigneeCandidateIds, assigneeGroupIds);
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 *
	 * 导出流程文件(已经保存到数据，从数据库取).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年7月6日 上午10:18:27
	 */
	@RequestMapping("/exportXmlCodeFromDb")
	public void exportXmlCodeFromDb(HttpServletRequest request, HttpServletResponse response) {
		OutputStream toClient = null;
		InputStream fis = null;
		try {
			ActAljoinBpmn orgnlBpmn = actAljoinBpmnService.selectById(Long.parseLong(request.getParameter("id")));
			// 以流的形式下载文件。
			byte[] buffer = orgnlBpmn.getXmlCode().getBytes();
			// 清空response
			response.reset();
			// 设置response的Header
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(
				(orgnlBpmn.getProcessName() + "_" + (DateUtil.datetime2numstr(null)) + ".xml").getBytes("gb2312"),
				"ISO8859-1"));
			toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			toClient.write(buffer);
			toClient.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
			if (toClient != null) {
				try {
					toClient.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}

	/**
	 *
	 * 保存流程文件
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年7月6日 上午10:18:27
	 */
	@RequestMapping("/updateXmlCode")
	@ResponseBody
	public RetMsg updateXmlCode(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		// 流程文件
		String xmlCode = request.getParameter("xmlCode");
		// 流程分类
		String processCategory = request.getParameter("processCategory");
		String[] processCategoryArr = processCategory.split("-");
		processCategory = processCategoryArr[processCategoryArr.length - 1];
		// 流程ID
		String processId = request.getParameter("processId");
		// 流程名称
		String processName = request.getParameter("processName");
		// 流程描述
		String processDesc = request.getParameter("processDesc");
		// 主键ID
		String id = request.getParameter("id");

		// 页面表单备份元素html
		String backupHtml = request.getParameter("backupHtml");

		// 表单---表单ID
		String formIds = request.getParameter("formIds");
		// 表单---元素类型
		String elementTypes = request.getParameter("elementTypes");
		// 表单---元素id
		String elementIds = request.getParameter("elementIds");

		// 授权---任务id，逗号分隔
		String taskIds = request.getParameter("taskIds");
		// 授权---部门id，逗号&分号分隔
		String assigneeDepartmentIds = request.getParameter("assigneeDepartmentIds");
		// 授权---岗位id，逗号&分号分隔
		String assigneePositionIds = request.getParameter("assigneePositionIds");
		// 授权---受理人用户id，逗号&分号分隔
		String assigneeUserIds = request.getParameter("assigneeUserIds");
		// 授权---候选用户id，逗号&分号分隔
		String assigneeCandidateIds = request.getParameter("assigneeCandidateIds");
		// 授权---候选组id，逗号&分号分隔
		String assigneeGroupIds = request.getParameter("assigneeGroupIds");
		// 授权---任务表单权限（可视控件ID，逗号&分号分隔）
		String showWidgetIds = request.getParameter("showWidgetIds");
		// 授权---任务表单权限（可编辑控件ID，逗号&分号分隔）
		String editWidgetIds = request.getParameter("editWidgetIds");
		// 授权---任务表单权限（不允许空控件ID，逗号&分号分隔）
		String notNullWidgetIds = request.getParameter("notNullWidgetIds");
		// 授权---任务表单权限（意见域控件ID，逗号&分号分隔）
		String commentWidgetIds = request.getParameter("commentWidgetIds");
		// 授权---办理权限ID，逗号&分号分隔
		String aljoinTaskOperateAuthIds = request.getParameter("aljoinTaskOperateAuthIds");
		// 授权---是否返回流程发起人，分号分割
		String isReturnCreaters = request.getParameter("isReturnCreaters");

		// 红头文件---控件ID，逗号&分号分隔
		String redHeadWidgetIds = request.getParameter("redHeadWidgetIds");
		// 痕迹保留---控件ID，逗号&分号分隔
		String saveMarkWidgetIds = request.getParameter("saveMarkWidgetIds");

		// 是否固定表单
		String aljoinProcessIsfixed = request.getParameter("aljoinProcessIsfixed");
		// 是否自由流程
		String aljoinProcessIsfree = request.getParameter("aljoinProcessIsfree");
		// 授权---任务表单权限（加签意见域控件ID，逗号&分号分隔）
		String signCommentWidgetIds = request.getParameter("addSignCommentWidgetIds");
		// 授权---创建人所在部门人员，分号分割
		String staffMembersDepartments = request.getParameter("staffMembersDepartments");
		// 授权---上一个环节办理人人所在部门人员，分号分割
		String lastlinkDepartments = request.getParameter("lastlinkDepartments");
		// 授权---创建人所在岗位人员办理，分号分割
		String createPersonsJobs = request.getParameter("createPersonsJobs");
		try {
			Map<String, String> param = new HashMap<String, String>();
			param.put("xmlCode", xmlCode);
			param.put("processCategory", processCategory);
			param.put("processId", processId);
			param.put("processName", processName);
			param.put("processDesc", processDesc);
			param.put("formIds", formIds);
			param.put("elementTypes", elementTypes);
			param.put("elementIds", elementIds);
			param.put("taskIds", taskIds);
			param.put("assigneeDepartmentIds", assigneeDepartmentIds);
			param.put("assigneePositionIds", assigneePositionIds);
			param.put("assigneeUserIds", assigneeUserIds);
			param.put("assigneeCandidateIds", assigneeCandidateIds);
			param.put("assigneeGroupIds", assigneeGroupIds);
			param.put("backupHtml", backupHtml);
			param.put("id", id);
			param.put("showWidgetIds", showWidgetIds);
			param.put("editWidgetIds", editWidgetIds);
			param.put("notNullWidgetIds", notNullWidgetIds);
			param.put("aljoinTaskOperateAuthIds", aljoinTaskOperateAuthIds);
			param.put("commentWidgetIds", commentWidgetIds);
			param.put("aljoinProcessIsfixed", aljoinProcessIsfixed);
			param.put("aljoinProcessIsfree", aljoinProcessIsfree);
			param.put("redHeadWidgetIds", redHeadWidgetIds);
			param.put("saveMarkWidgetIds", saveMarkWidgetIds);
			param.put("isReturnCreaters", isReturnCreaters);
			param.put("signCommentWidgetIds", signCommentWidgetIds);
			param.put("staffMembersDepartments", staffMembersDepartments);
			param.put("lastlinkDepartments", lastlinkDepartments);
			param.put("createPersonsJobs", createPersonsJobs);
			retMsg = actAljoinBpmnService.updateXmlCode(param);
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 *
	 * 表单设计
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年7月10日 上午9:43:48
	 */
	@RequestMapping("/formDesign")
	public String formDesign(HttpServletRequest request, HttpServletResponse response) {
		return "act/formDesign";
	}

	/**
	 *
	 * 表单预览
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年7月10日 上午9:43:48
	 */
	@RequestMapping("/formPreview")
	public String formPreview(HttpServletRequest request, HttpServletResponse response) {

		return "act/formPreview";
	}

	/**
	 *
	 * 表单解析
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年7月10日 上午9:43:48
	 */
	@RequestMapping("/formParser")
	public String formParser(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		if (StringUtils.isNotEmpty(id)) {
			ActAljoinForm actAljoinForm = actAljoinFormService.selectById(Long.parseLong(id));
			request.setAttribute("html", actAljoinForm.getHtmlCode());
		} else {
			String html = request.getParameter("html");
			request.setAttribute("html", html);
		}
		// 尝试解码(OK)
		/*
		 * byte[] byteKey = html.getBytes(); byte[] encodeKey =
		 * Base64.decode(byteKey); String retKey = new String(encodeKey);
		 * System.out.println(retKey);
		 */
		request.setAttribute("currentTime", DateUtil.datetime2str(new Date()));
		return "act/formParser";
	}

	/**
	 *
	 * 任务表单授权页面.
	 *
	 * 				@return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/showForm")
	public String showForm(HttpServletRequest request, HttpServletResponse response, ActAljoinForm obj) {
		request.setAttribute("orgnl_id", obj.getId());
		return "act/showForm";
	}

	/**
	 *
	 * 表单解析
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年7月10日 上午9:43:48
	 */
	@RequestMapping("/openForm")
	public String openForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String str = request.getParameter("businessKey");
		String processInstanceId = request.getParameter("processInstanceId");
		String isMonitor = request.getParameter("isMonitor");
		String titleTx  = request.getParameter("titleTx");
		request.setAttribute("titleTx", StringUtils.isNotEmpty(titleTx) ? titleTx : "");
		request.setAttribute("processInstanceId", StringUtils.isNotEmpty(processInstanceId) ? processInstanceId : "");
		request.setAttribute("isMonitor", StringUtils.isNotEmpty(isMonitor) ? isMonitor : "");
		String[] key = null;
		if (!StringUtils.isEmpty(str)) {
			if (str.indexOf(",") > -1) {
				if (str.endsWith(",")) {
					key = str.substring(0, str.lastIndexOf(",")).split(",");
				} else {
					key = str.split(",");
				}
			}
			if (null != key) {
				if (key.length >= 2) {
					if ("null".equals(key[1])) {
						String bizType = key[3];
						String bizId = key[4];
						String taskId = request.getParameter("activityId");
						request.setAttribute("bizType", bizType);
						request.setAttribute("bizId", bizId);
						request.setAttribute("taskId", StringUtils.isNotEmpty(taskId) ? taskId : "");
						if (StringUtils.isNotEmpty(bizType)) {
							return "otherform/" + bizType;
						}
					}
				}
			}
		}

		request.setAttribute("current_user_id", getCustomDetail().getUserId());
		actAljoinBpmnWebService.openForm(request);
		return "act/openForm";
	}

	/**
	 *
	 * 表单解析
	 *
	 * @return：String
	 *
	 * @author：huangw
	 *
	 * @date：2017年7月10日 上午9:43:48
	 */
	@RequestMapping("/openFormData")
	public String openFormData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String str = request.getParameter("businessKey");
		String processInstanceId = request.getParameter("processInstanceId");
		request.setAttribute("processInstanceId", StringUtils.isNotEmpty(processInstanceId) ? processInstanceId : "");
		String[] key = null;
		if (!StringUtils.isEmpty(str)) {
			if (str.indexOf(",") > -1) {
				if (str.endsWith(",")) {
					key = str.substring(0, str.lastIndexOf(",")).split(",");
				} else {
					key = str.split(",");
				}
			}
			if (null != key) {
				if (key.length >= 2) {
					if ("null".equals(key[1])) {
						String bizType = key[3];
						String bizId = key[4];
						String taskId = request.getParameter("activityId");
						request.setAttribute("bizType", bizType);
						request.setAttribute("bizId", bizId);
						request.setAttribute("taskId", StringUtils.isNotEmpty(taskId) ? taskId : "");
						if (StringUtils.isNotEmpty(bizType)) {
							return "otherform/" + bizType + "His";
						}
					}
				}
			}
		}
		actAljoinBpmnWebService.openFormData(request);
		return "act/openFormData";
	}
	/**
	 *
	 * 传阅表单解析
	 *
	 * @return：String
	 *
	 * @author：huangw
	 *
	 * @date：2017年7月10日 上午9:43:48
	 */
	@RequestMapping("/openFormCircula")
	public String openFormCircula(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String str = request.getParameter("businessKey");
		@SuppressWarnings("unused")
        String type = request.getParameter("isType");
		String processInstanceId = request.getParameter("processInstanceId");
		request.setAttribute("processInstanceId", StringUtils.isNotEmpty(processInstanceId) ? processInstanceId : "");
		String[] key = null;
		if (!StringUtils.isEmpty(str)) {
			if (str.indexOf(",") > -1) {
				if (str.endsWith(",")) {
					key = str.substring(0, str.lastIndexOf(",")).split(",");
				} else {
					key = str.split(",");
				}
			}
			if (null != key) {
				if (key.length >= 2) {
					if ("null".equals(key[1])) {
						String bizType = key[3];
						String bizId = key[4];
						String taskId = request.getParameter("activityId");
						request.setAttribute("bizType", bizType);
						request.setAttribute("bizId", bizId);
						request.setAttribute("taskId", StringUtils.isNotEmpty(taskId) ? taskId : "");
						if (StringUtils.isNotEmpty(bizType)) {
							return "otherform/" + bizType + "Cir";
						}
					}
				}
			}
		}
		
	
		actAljoinBpmnWebService.openFormCir(request);	
		return "act/openFormCir";
	}

	/**
	 *
	 * 表单解析
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年7月10日 上午9:43:48
	 */
	@RequestMapping("/getFormWidget")
	@ResponseBody
	public List<ActAljoinFormWidget> getFormWidget(HttpServletRequest request, HttpServletResponse response,
		ActAljoinFormWidget form) throws Exception {
		Where<ActAljoinFormWidget> where = new Where<ActAljoinFormWidget>();
		where.eq("form_id", form.getId());
		where.ne("widget_type", "label");
		List<ActAljoinFormWidget> list = actAljoinFormWidgetService.selectList(where);
		for (ActAljoinFormWidget actAljoinFormWidget : list) {
			// 缩短控件ID
			actAljoinFormWidget.setWidgetId(actAljoinFormWidget.getWidgetId().substring(12));
			actAljoinFormWidget.setWidgetType(ActConstant.WIDGET_MAP.get(actAljoinFormWidget.getWidgetType()));
		}
		return list;
	}

	/**
	 *
	 * 流程用户权限页面
	 *
	 * @return：String
	 *
	 * @author：pengsp
	 *
	 * @date：2017年10月18日
	 */
	@RequestMapping("/actAljoinBpmnUserPage")
	public String actAljoinBpmnUserPage(HttpServletRequest request, HttpServletResponse response, String bpmnId) {
		request.setAttribute("bpmnId", bpmnId);
		return "act/actAljoinBpmnUserPage";
	}
}
