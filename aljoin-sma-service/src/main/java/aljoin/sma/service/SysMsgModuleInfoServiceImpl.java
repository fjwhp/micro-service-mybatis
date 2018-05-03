package aljoin.sma.service;

import aljoin.sma.dao.entity.SysMsgModuleCategory;
import aljoin.sma.dao.entity.SysMsgModuleInfo;
import aljoin.sma.dao.mapper.SysMsgModuleInfoMapper;
import aljoin.sma.iservice.SysMsgModuleCategoryService;
import aljoin.sma.iservice.SysMsgModuleInfoService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;

/**
 * 
 * 消息模板信息表(服务实现类).
 * 
 * @author：huangw
 * 
 * @date： 2017-11-14
 */
@Service
public class SysMsgModuleInfoServiceImpl extends ServiceImpl<SysMsgModuleInfoMapper, SysMsgModuleInfo>
		implements SysMsgModuleInfoService {

	@Resource
	private SysMsgModuleInfoMapper mapper;
	@Resource
	private SysMsgModuleCategoryService sysMsgModuleCategoryService;

	@Override
	public Page<SysMsgModuleInfo> list(PageBean pageBean, SysMsgModuleInfo obj) throws Exception {
		Where<SysMsgModuleInfo> where = new Where<SysMsgModuleInfo>();
		where.orderBy("create_time", false);
		Page<SysMsgModuleInfo> page = selectPage(
				new Page<SysMsgModuleInfo>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		return page;
	}

	@Override
	public void physicsDeleteById(Long id) throws Exception {
		mapper.physicsDeleteById(id);
	}

	@Override
	public void copyObject(SysMsgModuleInfo obj) throws Exception {
		mapper.copyObject(obj);
	}

	@Override
	public RetMsg addOrUpdata(SysMsgModuleInfo obj, String gropName) throws Exception {
		// TODO Auto-generated method stub
		RetMsg retMsg = new RetMsg();
		if (gropName != null && !"".equals(gropName)) {
			Where<SysMsgModuleCategory> cgWhere = new Where<SysMsgModuleCategory>();
			cgWhere.eq("is_delete", 0);
			cgWhere.eq("is_active", 1);
			cgWhere.eq("is_active", 1);
			cgWhere.eq("module_code", gropName);
			cgWhere.setSqlSelect("id");
			SysMsgModuleCategory cg = sysMsgModuleCategoryService.selectOne(cgWhere);
			if (cg != null) {
				Where<SysMsgModuleInfo> infoWhere = new Where<SysMsgModuleInfo>();
				infoWhere.eq("is_delete", 0);
				infoWhere.eq("is_active", 1);
				infoWhere.eq("attr_code", obj.getAttrCode());
				infoWhere.eq("module_category_id", cg.getId());
				SysMsgModuleInfo info = this.selectOne(infoWhere);
				if (info == null) {
					obj.setModuleCategoryId(cg.getId());
					obj.setIsDelete(0);
					obj.setIsActive(1);
					insert(obj);
				} else {
					info.setAttrValue(obj.getAttrValue());
					info.setAttrDesc(obj.getAttrDesc());
					updateById(info);
				}
				retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
				retMsg.setMessage("操作成功");
			} else {
				retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
				retMsg.setMessage("消息分类为空");
			}

		} else {
			retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
			retMsg.setMessage("消息分类为空");
		}

		return retMsg;
	}

	@Override
	public List<String> contextTemplate(Map<String, String> list, String type, String stype, String behavior)
			throws Exception {
		// TODO Auto-generated method stub
		String context = "";
		String returnText = "";
		List<String> returnlist = new ArrayList<String>();
		// 邮件标题
		String titText = "";
		String qtypes = "";
		if (type != null && !"".equals(type) && stype != null && !"".equals(stype) && behavior != null
				&& !"".equals(behavior)) {
			if (WebConstant.TEMPLATE_TYPE_MSG.equals(type)) {
				qtypes = "notice";
			}
			if (WebConstant.TEMPLATE_TYPE_SMS.equals(type)) {
				qtypes = "sm";
			}
			if (WebConstant.TEMPLATE_TYPE_MAIL.equals(type)) {
				qtypes = "mai";
			}
			Where<SysMsgModuleCategory> cgWHere = new Where<SysMsgModuleCategory>();
			cgWHere.eq("module_name", stype);
			cgWHere.eq("is_delete", 0);
			cgWHere.eq("is_active", 1);
			cgWHere.setSqlSelect("id");
			SysMsgModuleCategory cg = sysMsgModuleCategoryService.selectOne(cgWHere);
			if (cg != null) {
				Where<SysMsgModuleInfo> infoWHere = new Where<SysMsgModuleInfo>();
				infoWHere.eq("is_active", 1);
				infoWHere.eq("is_delete", 0);
				infoWHere.eq("module_category_id", cg.getId());
				infoWHere.like("attr_code", qtypes);
				infoWHere.eq("attr_name", behavior);
				infoWHere.setSqlSelect("id,attr_value,attr_desc");
				SysMsgModuleInfo info = this.selectOne(infoWHere);
				if (info != null) {
					context = info.getAttrValue();
					if ("3".equals(type)) {
						titText = info.getAttrDesc();
					}
				}
			}
		}
		if (!"".equals(context) && context != null) {
			// handle}:当前办理人; {create}:创建人; {process}:流程名称; {priorities}:缓急程度;
			// {title}:标题;
			if ("协同办公".equals(stype)) {
				String handle = list.get("handle");
				String create = list.get("create");
				String process = list.get("process");
				String priorities = list.get("priorities");
				String title = list.get("title");
				if (handle == null) {
					handle = "";
				}
				if (create == null) {
					create = "";
				}
				if (process == null) {
					process = "";
				}
				if (priorities == null) {
					priorities = "";
				}
				if (title == null) {
					title = "";
				}
				context = context.replaceAll("handle", handle);
				context = context.replaceAll("create", create);
				context = context.replaceAll("process", process);
				context = context.replaceAll("priorities", priorities);
				context = context.replaceAll("title", title);
				if ("3".equals(type) && titText != null && !"".equals(titText)) {
					titText = titText.replaceAll("handle", handle);
					titText = titText.replaceAll("create", create);
					titText = titText.replaceAll("process", process);
					titText = titText.replaceAll("priorities", priorities);
					titText = titText.replaceAll("title", title);
				}
			}
			if ("工作计划".equals(stype)) {
				// {theme}:主题; {create}:创建人; {startTime}:开始时间; {endTime}:结束时间;
				// {Share}:被共享人;
				String theme = list.get("theme");
				String create = list.get("create");
				String startTime = list.get("startTime");
				String endTime = list.get("endTime");
				String share = list.get("Share");
				if (theme == null) {
					theme = "";
				}
				if (create == null) {
					create = "";
				}
				if (startTime == null) {
					startTime = "";
				}
				if (endTime == null) {
					endTime = "";
				}
				if (share == null) {
					share = "";
				}
				context = context.replaceAll("theme", theme);
				context = context.replaceAll("create", create);
				context = context.replaceAll("startTime", startTime);
				context = context.replaceAll("endTime", endTime);
				context = context.replaceAll("Share", share);
				if ("3".equals(type) && titText != null && !"".equals(titText)) {
					titText = titText.replaceAll("theme", theme);
					titText = titText.replaceAll("create", create);
					titText = titText.replaceAll("startTime", startTime);
					titText = titText.replaceAll("endTime", endTime);
					titText = titText.replaceAll("Share", share);
				}

			}
			if ("邮箱".equals(stype)) {
				// {recipient}:收件人; {refer}:抄送人; {sender}:发件人; {theme}:主题;
				// {emergency}:紧急状态; {sendTime}:发送时间;
				String recipient = list.get("recipient");
				String refer = list.get("refer");
				String sender = list.get("sender");
				String theme = list.get("theme");
				String emergency = list.get("emergency");
				String sendTime = list.get("sendTime");
				if (theme == null) {
					theme = "";
				}
				if (sendTime == null) {
					sendTime = "";
				}
				if (recipient == null) {
					recipient = "";
				}
				if (refer == null) {
					refer = "";
				}
				if (sender == null) {
					sender = "";
				}
				if (emergency == null) {
					emergency = "";
				}
				context = context.replaceAll("recipient", recipient);
				context = context.replaceAll("refer", refer);
				context = context.replaceAll("sender", sender);
				context = context.replaceAll("theme", theme);
				context = context.replaceAll("emergency", emergency);
				context = context.replaceAll("sendTime", sendTime);
				if ("3".equals(type) && titText != null && !"".equals(titText)) {
					titText = titText.replaceAll("recipient", recipient);
					titText = titText.replaceAll("refer", refer);
					titText = titText.replaceAll("sender", sender);
					titText = titText.replaceAll("theme", theme);
					titText = titText.replaceAll("emergency", emergency);
					titText = titText.replaceAll("sendTime", sendTime);
				}
			}
			if ("会议".equals(stype)) {
				// {userName}:用户名; {meetingRoom}:会议室; {theme}:会议主题;
				// {address}:会议地址; {startTime}:会议开始时间; {endTime}:会议结束时间;
				// {contact}:联系人;
				String userName = list.get("userName");
				String meetingRoom = list.get("meetingRoom");
				String theme = list.get("theme");
				String address = list.get("address");
				String startTime = list.get("startTime");
				String endTime = list.get("endTime");
				String contact = list.get("contact");

				if (userName == null) {
					userName = "";
				}
				if (meetingRoom == null) {
					meetingRoom = "";
				}
				if (theme == null) {
					theme = "";
				}
				if (address == null) {
					address = "";
				}
				if (startTime == null) {
					startTime = "";
				}
				if (contact == null) {
					contact = "";
				}
				if (endTime == null) {
					endTime = "";
				}
				context = context.replaceAll("userName", userName);
				context = context.replaceAll("meetingRoom", meetingRoom);
				context = context.replaceAll("theme", theme);
				context = context.replaceAll("address", address);
				context = context.replaceAll("startTime", startTime);
				context = context.replaceAll("endTime", endTime);
				context = context.replaceAll("contact", contact);
				if ("3".equals(type) && titText != null && !"".equals(titText)) {
					titText = titText.replaceAll("userName", userName);
					titText = titText.replaceAll("meetingRoom", meetingRoom);
					titText = titText.replaceAll("theme", theme);
					titText = titText.replaceAll("address", address);
					titText = titText.replaceAll("startTime", startTime);
					titText = titText.replaceAll("endTime", endTime);
					titText = titText.replaceAll("contact", contact);
				}

			}
			returnText = context;
		}
		returnlist.add(returnText);
		returnlist.add(titText);
		return returnlist;
	}
}
