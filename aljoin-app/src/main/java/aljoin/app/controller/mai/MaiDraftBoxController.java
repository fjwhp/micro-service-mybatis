package aljoin.app.controller.mai;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
import aljoin.app.object.MaiDraftListVO;
import aljoin.app.object.MaiListVO;
import aljoin.app.object.MaiValListVO;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.mai.dao.entity.MaiDraftBox;
import aljoin.mai.dao.object.AppMaiDraftBoxVO;
import aljoin.mai.dao.object.MaiDraftBoxVO;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.mai.iservice.app.AppMaiDraftBoxService;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.iservice.SysParameterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 草稿箱表(控制器).
 * 
 * @author：laijy
 * 
 * 				@date： 2017-09-20
 */
@Controller
@RequestMapping(value = "/app/mai/maiDraftBox", method = RequestMethod.POST)
@Api(value = "草稿箱表", description = "草稿箱表接口")
public class MaiDraftBoxController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(MaiDraftBoxController.class);

	@Resource
	private AppMaiDraftBoxService appMaiDraftBoxService;
	@Resource
	private MaiSendBoxService maiSendBoxService;
	@Resource
	private SysParameterService sysParameterService;

	/**
	 * 
	 * 草稿箱表(分页列表).
	 *
	 * @return：Page<MaiDraftBox>
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-20
	 */
	@SuppressWarnings("unused")
	@RequestMapping("/list")
	@ResponseBody
	@ApiOperation(value = "草稿箱表(分页列表)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isUrgent", value = "查询是否紧急", required = false, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "staTime", value = "查询开始时间", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "endTime", value = "查询结束时间", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isCcessory", value = "查询是否有附件", required = false, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "title", value = "查询标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "queryName", value = "查询收件人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "页面条数", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "integer", paramType = "query") })
	public RetMsg list(HttpServletRequest request, HttpServletResponse response, MaiListVO ml) {
		Page<MaiValListVO> page = null;
		RetMsg retMsg = new RetMsg();
		try {
			// 获得使用者id,用于过滤，草稿箱仅显示登录用户创建的草稿
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			MaiDraftBox obj = new MaiDraftBox();
			PageBean pageBean = new PageBean();
			pageBean.setPageNum(ml.getPageNum());
			pageBean.setPageSize(ml.getPageSize());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			if (ml.getStaTime() != null && !"".equals(ml.getStaTime())) {
				obj.setCreateTime(format.parse(ml.getStaTime()));// 存储临时查询条件
			}
			if (ml.getEndTime() != null && !"".equals(ml.getEndTime())) {
				obj.setLastUpdateTime(format.parse(ml.getEndTime()));// 存储临时查询条件
			}
			if (ml.getIsUrgent() != null) {
				obj.setIsUrgent(ml.getIsUrgent());// 存储临时查询条件
			}
			if (ml.getIsccessory() != null) {
				obj.setIsDelete(ml.getIsccessory());// 存储临时查询条件
			}
			if (ml.getTitle() != null && !"".equals(ml.getTitle())) {
				obj.setSubjectText(ml.getTitle());// 存储临时查询条件
			}
			if (ml.getQueryName() != null && !"".equals(ml.getQueryName())) {
				obj.setReceiveFullNames(ml.getQueryName());// 存储临时查询条件
			}
			Page<MaiDraftBox> pages = appMaiDraftBoxService.list(pageBean, obj, userId);
			MaiDraftListVO vo = new MaiDraftListVO();
			List<MaiDraftBox> maiList = pages.getRecords();
			List<MaiValListVO> maiValList = new ArrayList<MaiValListVO>();
			for (MaiDraftBox maiDraftBox : maiList) {
				MaiValListVO maiVal = new MaiValListVO();
				maiVal.setMaiID(maiDraftBox.getId().toString());
				maiVal.setIsUrgent(maiDraftBox.getIsUrgent());
				maiVal.setLastUpdateTime(maiDraftBox.getLastUpdateTime());
				maiVal.setReceiveFullNames(maiDraftBox.getReceiveFullNames());
				maiVal.setSubjectText(maiDraftBox.getSubjectText());
				maiValList.add(maiVal);
			}
			// MaiValListVO
			vo.setMaiDraftBoxList(maiValList);
			vo.setPages(pages.getPages());
			vo.setSize(pages.getSize());
			vo.setTotal(pages.getTotal());
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(vo);
		} catch (Exception e) {
			retMsg = new RetMsg();
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("App邮件列表查询错误", e);

		}
		return retMsg;
	}

	/**
	 * 
	 * 草稿箱表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/add")
	@ResponseBody
	@ApiOperation(value = "草稿箱表(添加)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.receiveUserIds", value = "收件人用户ID(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.receiveUserNames", value = "收件人账号(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.receiveFullNames", value = "收件人名称(分号分隔)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "maiSendBox.copyUserIds", value = "抄送人用户ID(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.copyUserNames", value = "抄送人账号(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.copyFullNames", value = "抄送人名称(分号分隔)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "maiSendBox.subjectText", value = "主题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.mailContent", value = "内容", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.isUrgent", value = "是否紧急", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.isImportant", value = "是否重要", required = false, dataType = "int", paramType = "query"),

			@ApiImplicitParam(name = "maiSendBox.isReceiveSmsRemind", value = "是否进行收件人短信提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.receiveUserCount", value = "收件人数", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.isCopySmsRemind", value = "是否进行抄送人短信提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.isReceipt", value = "本邮件是否回执邮件", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachName", value = "附件名称(原文件名)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachPath", value = "上传后的完整路径", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachSize", value = "附件大小(KB)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachName", value = "附件名称(原文件名)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachPath", value = "上传后的完整路径", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachSize", value = "附件大小(KB)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, MaiWriteVO obj) {

		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			retMsg = appMaiDraftBoxService.add(obj, autAppUserLogin);
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
			retMsg.setMessage("操作失败");
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 草稿箱表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, MaiDraftBoxVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			List<MaiDraftBox> draftBoxList = obj.getDraftBoxList();
			List<Long> idList = new ArrayList<Long>();
			for (MaiDraftBox box : draftBoxList) {
				idList.add(box.getId());
			}
			appMaiDraftBoxService.deleteBatchIds(idList);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

	/**
	 * 
	 * 草稿箱表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/deleteID")
	@ResponseBody
	@ApiOperation(value = "草稿箱表(删除)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "delId", value = "删除邮件", required = true, dataType = "String", paramType = "query") })
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {
			String delId = request.getParameter("delId");
			appMaiDraftBoxService.deleteById(delId);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

	/**
	 * 
	 * 草稿箱表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/update")
	@ResponseBody
	@ApiOperation(value = "草稿箱表(更新)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.receiveUserIds", value = "收件人用户ID(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.receiveUserNames", value = "收件人账号(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.receiveFullNames", value = "收件人名称(分号分隔)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "maiDraftBox.copyUserIds", value = "抄送人用户ID(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.copyUserNames", value = "抄送人账号(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.copyFullNames", value = "抄送人名称(分号分隔)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "maiDraftBox.subjectText", value = "主题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.mailContent", value = "内容", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.isUrgent", value = "是否紧急", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.isImportant", value = "是否重要", required = false, dataType = "int", paramType = "query"),

			@ApiImplicitParam(name = "maiDraftBox.isReceiveSmsRemind", value = "是否进行收件人短信提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.receiveUserCount", value = "收件人数", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.isCopySmsRemind", value = "是否进行抄送人短信提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.isReceipt", value = "本邮件是否回执邮件", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachName", value = "附件名称(原文件名)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachPath", value = "上传后的完整路径", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachSize", value = "附件大小(KB)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachName", value = "附件名称(原文件名)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachPath", value = "上传后的完整路径", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachSize", value = "附件大小(KB)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, MaiWriteVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			retMsg = appMaiDraftBoxService.update(obj, autAppUserLogin);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

	/**
	 * 
	 * 草稿箱表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@ApiOperation(value = "草稿箱表(获取邮件详情)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "邮件", required = true, dataType = "String", paramType = "query") })
	public RetMsg getById(HttpServletRequest request, HttpServletResponse response, MaiDraftBox obj) throws Exception {
		AppMaiDraftBoxVO maiDraftBoxVO = new AppMaiDraftBoxVO();
		RetMsg retMsg = new RetMsg();
		try {
			maiDraftBoxVO = appMaiDraftBoxService.getById(obj);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setObject(maiDraftBoxVO);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 * 
	 * 草稿箱-发送（发送后删掉草稿箱中的该记录）
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月28日 下午4:51:27
	 */
	@RequestMapping("/sendDraft")
	@ResponseBody
	@ApiOperation(value = "草稿箱表(发送)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.receiveUserIds", value = "收件人用户ID(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.receiveUserNames", value = "收件人账号(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.receiveFullNames", value = "收件人名称(分号分隔)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "maiDraftBox.copyUserIds", value = "抄送人用户ID(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.copyUserNames", value = "抄送人账号(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.copyFullNames", value = "抄送人名称(分号分隔)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "maiDraftBox.subjectText", value = "主题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.mailContent", value = "内容", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.isUrgent", value = "是否紧急", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.isImportant", value = "是否重要", required = false, dataType = "int", paramType = "query"),

			@ApiImplicitParam(name = "maiDraftBox.isReceiveSmsRemind", value = "是否进行收件人短信提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.receiveUserCount", value = "收件人数", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.isCopySmsRemind", value = "是否进行抄送人短信提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiDraftBox.isReceipt", value = "本邮件是否回执邮件", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachName", value = "附件名称(原文件名)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachPath", value = "上传后的完整路径", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachSize", value = "附件大小(KB)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachName", value = "附件名称(原文件名)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachPath", value = "上传后的完整路径", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachSize", value = "附件大小(KB)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg sendDraft(HttpServletRequest request, HttpServletResponse response, MaiWriteVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			// 添加草稿
			retMsg = appMaiDraftBoxService.add(obj, autAppUserLogin);
			// 删除草稿箱中的该记录
			if (obj.getMaiDraftBox() != null && obj.getMaiDraftBox().getId() != null) {
				appMaiDraftBoxService.deleteById(obj.getMaiDraftBox().getId());
			}
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	/**
	 *
	 * 撰写邮件上传附件.
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-25
	 *//*
		 * @RequestMapping(value = "/uploads")
		 * 
		 * @ResponseBody
		 * 
		 * @ApiOperation(value = "邮件文件上传接口")
		 * 
		 * @ApiImplicitParams({
		 * 
		 * @ApiImplicitParam(name = "timestamp", value = "时间戳", required =
		 * false, dataType = "string", paramType = "query"),
		 * 
		 * @ApiImplicitParam(name = "sign", value = "签名", required = false,
		 * dataType = "string",paramType = "query"),
		 * 
		 * @ApiImplicitParam(name = "token", value = "登录令牌", required = true,
		 * dataType = "string",paramType = "query") }) public RetMsg
		 * uploads(HttpServletRequest request, HttpServletResponse response){
		 * RetMsg retMsg=new RetMsg(); try { Map<String, String> map =
		 * sysParameterService.allowFileType(); int limitSize =
		 * Integer.parseInt(map.get("limitSize")); String allowType =
		 * map.get("allowType"); MaiFile mf=new MaiFile();
		 * mf.setAllowType(allowType); mf.setLimitSize(limitSize); String ymdsfm
		 * = new DateTime().toString("yyyyMMddHHmmss"); String ymd =
		 * ymdsfm.substring(0, 8); String sfm = ymdsfm.substring(8,
		 * ymdsfm.length()); String path = "mai" + File.separator +
		 * File.separator + ymd; SysParameter parameter =
		 * sysParameterService.selectBykey("upload_path"); String rootPath = "";
		 * if (parameter != null) { rootPath =
		 * FileUtil.formatPath(parameter.getParamValue());
		 * retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		 * retMsg.setMessage("操作成功"); String url=rootPath+path;
		 * mf.setUploadPath(url.replaceAll("\\\\", "/").replaceAll("//", "/"));
		 * retMsg.setObject(mf); }else{
		 * retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
		 * retMsg.setMessage("上传文件路径为空"+rootPath); return retMsg; } } catch
		 * (Exception e) { logger.error("", e);
		 * retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
		 * retMsg.setMessage("上传失败"); } return retMsg; }
		 */
}
