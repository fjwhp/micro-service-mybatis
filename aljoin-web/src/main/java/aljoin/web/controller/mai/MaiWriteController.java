package aljoin.web.controller.mai;

import java.util.List;

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

import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import aljoin.web.controller.BaseController;
import aljoin.web.service.mai.MaiSendBoxWebService;
import aljoin.web.task.WebTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 发件箱表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-20
 */
@Controller
@RequestMapping(value = "/mai/maiWrite", method = RequestMethod.POST)
@Api(value = "发件箱Controller", description = "内部邮件->撰写邮件接口")
public class MaiWriteController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(MaiWriteController.class);
	@Resource
	private MaiSendBoxService maiSendBoxService;
	@Resource
	private AutMsgOnlineService autMsgOnlineService;
	@Resource
	private WebTask webTask;
	@Resource
	private MaiSendBoxWebService maiSendBoxWebService;
	@Resource
	private AutDepartmentUserService autDepartmentUserService;
	@Resource
	private SysParameterService sysParameterService;

	/**
	 * 
	 * 发件箱表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping(value = "/maiWitePage", method = RequestMethod.GET)
	@ApiOperation(value = "撰写邮件页面跳转接口")
	public String maiSendBoxPage(HttpServletRequest request, HttpServletResponse response) {

		return "mai/maiWitePage";
	}

	/**
	 * 
	 * 发件箱表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@ApiOperation(value = "邮件发送接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "maiReceiveBox.receiveUserIds", value = "收件人用户ID(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.receiveUserNames", value = "收件人账号(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.receiveFullNames", value = "收件人名称(分号分隔)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "maiReceiveBox.copyUserIds", value = "抄送人用户ID(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.copyUserNames", value = "抄送人账号(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.copyFullNames", value = "抄送人名称(分号分隔)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "maiReceiveBox.subjectText", value = "主题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.mailContent", value = "内容", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.isUrgent", value = "是否紧急", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.isImportant", value = "是否重要", required = false, dataType = "int", paramType = "query"),

			@ApiImplicitParam(name = "maiSendBox.isReceiveSmsRemind", value = "是否进行收件人短信提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.receiveUserCount", value = "收件人数", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.isCopySmsRemind", value = "是否进行抄送人短信提醒", required = false, dataType = "int", paramType = "query"),

			@ApiImplicitParam(name = "maiSendBox.isReceipt", value = "本邮件是否回执邮件", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachName", value = "附件名称(原文件名)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachPath", value = "上传后的完整路径", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachName", value = "附件名称(原文件名)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachPath", value = "上传后的完整路径", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query") })
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, MaiWriteVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser customUser = getCustomDetail();
			if (null != customUser) {
				obj.setCreateUserId(customUser.getUserId());
			}
			AutUser user = new AutUser();
			user.setFullName(customUser.getNickName());
			user.setId(customUser.getUserId());
			user.setUserName(customUser.getUsername());
			List<MaiAttachment> maiAttachmentList = obj.getMaiAttachmentList();
			MaiReceiveBox maiReceiveBox = obj.getMaiReceiveBox();
			MaiReceiveBoxSearch maiReceiveBoxSearch = obj.getMaiReceiveBoxSearch();
			int allowMailSpace = 0;
			int myMailSpace = 0;
			int size = 0;
			if (StringUtils.isNotEmpty(maiReceiveBoxSearch.getSubjectText())) {
				if (maiReceiveBoxSearch.getSubjectText().length() > 200) {
					retMsg.setCode(1);
					retMsg.setMessage("主题长度不能超过200个字符");
					return retMsg;
				}
			} else {
				retMsg.setCode(1);
				retMsg.setMessage("标题不能为空");
				return retMsg;
			}
			if (StringUtils.isEmpty(maiReceiveBox.getReceiveUserIds())) {
				retMsg.setCode(1);
				retMsg.setMessage("收件人不能为空");
				return retMsg;
			}
			// 获得个人或者领导的空间限制大小
			Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
			departmentUserWhere.eq("user_id", customUser.getUserId());
			departmentUserWhere.setSqlSelect("id,is_leader");
			List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(departmentUserWhere);
			if (null != departmentUserList && !departmentUserList.isEmpty()) {
				String isLeader = "";
				for (AutDepartmentUser departmentUser : departmentUserList) {
					if (null != departmentUser && null != departmentUser.getIsLeader()) {
						isLeader += departmentUser.getIsLeader();
					}
				}
				if (StringUtils.isNotEmpty(isLeader)) {
					String key = "";
					if (isLeader.indexOf("1") > -1) {
						key = "leader_mail_space";
					} else {
						key = "personal_mail_space";
					}
					SysParameter parameter = sysParameterService.selectBykey(key);
					if (null != parameter && StringUtils.isNotEmpty(parameter.getParamValue())) {
						allowMailSpace = Integer.parseInt(parameter.getParamValue());
					}
				}
			}

			Where<MaiSendBox> maiSendBoxWhere = new Where<MaiSendBox>();
			maiSendBoxWhere.eq("send_user_id", customUser.getUserId());
			maiSendBoxWhere.setSqlSelect("id,send_user_id,mail_size");
			List<MaiSendBox> maiSendBoxList = maiSendBoxService.selectList(maiSendBoxWhere);
			if (null != maiSendBoxList && !maiSendBoxList.isEmpty()) {
				for (MaiSendBox sendBox : maiSendBoxList) {
					if (null != sendBox && null != sendBox.getMailSize()) {
						myMailSpace += sendBox.getMailSize();
					}
				}
			}
			if (null != maiAttachmentList && !maiAttachmentList.isEmpty()) {
				if (null != maiAttachmentList && !maiAttachmentList.isEmpty()) {
				    maiReceiveBoxSearch.setAttachmentCount(maiAttachmentList.size());
					for (MaiAttachment attachment : maiAttachmentList) {
						if (null != attachment && null == attachment.getId()) {
							size += attachment.getAttachSize();
						}
					}
				}
			}
			if (StringUtils.isNotEmpty(maiReceiveBox.getMailContent())) {
				int len = (((maiReceiveBox.getMailContent().getBytes("utf-8").length) / 1024));
				// 邮件内容空间校验
				if (((len + size + myMailSpace) / 1024) >= allowMailSpace) {
					retMsg.setCode(1);
					retMsg.setMessage("您的邮件空间超限： 最大限度为(" + allowMailSpace + "MB)");
					return retMsg;
				}
				maiReceiveBox.setMailSize((size + len));
			} else {
				maiReceiveBox.setMailSize(size);
			}
			obj.setMaiReceiveBoxSearch(maiReceiveBoxSearch);
			obj.setMaiReceiveBox(maiReceiveBox);
			webTask.maiSendTask(obj, user);
			// resAttachmentService.add();
			retMsg.setCode(0);
			retMsg.setMessage("发送成功");
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	
}
