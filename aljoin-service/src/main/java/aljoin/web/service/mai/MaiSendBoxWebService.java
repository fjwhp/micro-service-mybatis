package aljoin.web.service.mai;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.dao.config.Where;
import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mai.iservice.MaiAttachmentService;
import aljoin.mai.iservice.MaiReceiveBoxSearchService;
import aljoin.mai.iservice.MaiReceiveBoxService;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.rocket.object.AljoinMQMsg;
import aljoin.rocket.object.MQProducer;
import aljoin.rocket.object.MQTag;
import aljoin.rocket.service.AljoinMQService;
import aljoin.sma.iservice.SysMsgModuleInfoService;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.sms.iservice.SmsShortMessageService;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;

@Service
public class MaiSendBoxWebService {

	@Resource
	private AutDepartmentUserService autDepartmentUserService;
	@Resource
	private SysParameterService sysParameterService;
	@Resource
	private MaiSendBoxService maiSendBoxService;
	@Resource
	private MaiReceiveBoxService maiReceiveBoxService;
	@Resource
	private MaiAttachmentService maiAttachmentService;
	@Resource
	private SysMsgModuleInfoService sysMsgModuleInfoService;
	@Resource
	private AutMsgOnlineService autMsgOnlineService;
	@Resource
	private SmsShortMessageService smsShortMessageService;
	@Resource
    private MaiReceiveBoxSearchService maiReceiveBoxSearchService;
	@Resource
	private ResResourceService resResourceService;
	@Resource
	private AljoinMQService aljoinMQService;

	@Transactional
	public RetMsg add(MaiWriteVO obj, AutUser customUser) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (null != obj) {
			MaiSendBox maiSendBox = obj.getMaiSendBox();
			MaiReceiveBox maiReceiveBox = obj.getMaiReceiveBox();
			MaiReceiveBoxSearch maiReceiveBoxSearch = obj.getMaiReceiveBoxSearch();
			List<MaiAttachment> maiAttachmentList = obj.getMaiAttachmentList();
			List<MaiReceiveBox> maiReceiveBoxList = new ArrayList<MaiReceiveBox>();
			List<MaiReceiveBoxSearch> maiReceiveBoxSearchList = new ArrayList<MaiReceiveBoxSearch>();
			if (null != maiReceiveBox) {
				if (null == maiSendBox) {
					maiSendBox = new MaiSendBox();
				}
				if (null == maiReceiveBox.getIsRevoke()) {
					maiReceiveBox.setIsRevoke(0);
				}
				if (null == maiReceiveBoxSearch.getIsScrap()) {
				    maiReceiveBoxSearch.setIsScrap(0);
				}

				if (null == maiReceiveBoxSearch.getIsRead()) {
				    maiReceiveBoxSearch.setIsRead(0);
				}
				if (null == maiReceiveBoxSearch.getIsUrgent()) {
				    maiReceiveBoxSearch.setIsUrgent(0);
				}
				if (null == maiReceiveBoxSearch.getIsImportant()) {
				    maiReceiveBoxSearch.setIsImportant(0);
				}
				Date date = new Date();
				maiReceiveBoxSearch.setSendTime(date);
				maiReceiveBox.setWeekDay(DateUtil.getWeek(date));
				BeanUtils.copyProperties(maiReceiveBox, maiSendBox);
				BeanUtils.copyProperties(maiReceiveBoxSearch, maiSendBox);
				maiSendBox.setIsSendSuccess(1);
				if (null != customUser) {
					maiSendBox.setSendUserId(customUser.getId());
					maiSendBox.setSendUserName(customUser.getUserName());
					maiSendBox.setSendFullName(customUser.getFullName());
				}
				if (null == maiSendBox.getIsReceiveSmsRemind()) {
					maiSendBox.setIsReceiveSmsRemind(0);
				}
				if (null == maiSendBox.getIsCopySmsRemind()) {
					maiSendBox.setIsCopySmsRemind(0);
				}
				if (null == maiSendBox.getHasReceiveSmsRemind()) {
					maiSendBox.setHasReceiveSmsRemind(0);
				}
				if (null == maiSendBox.getHasCopySmsRemind()) {
					maiSendBox.setHasCopySmsRemind(0);
				}
				if (null == maiSendBox.getIsReceipt()) {
					maiSendBox.setIsReceipt(0);
				} else {
					maiSendBox.setIsReceipt(1);
				}
				if (null == maiSendBox.getIsReceiptMail()) {
					maiSendBox.setIsReceiptMail(0);
				}
				if (null == maiSendBox.getIsCopyOnlineRemind()) {
                  maiSendBox.setIsCopyOnlineRemind(0);
                }
				if (null == maiSendBox.getHasCopyOnlineRemind()) {
                  maiSendBox.setHasCopyOnlineRemind(0);
                }
				if (null == maiSendBox.getHasReceiveOnlineRemind()) {
                  maiSendBox.setHasReceiveOnlineRemind(0);
                }
				if (null == maiSendBox.getIsReceiveOnlineRemind()) {
                  maiSendBox.setIsReceiveOnlineRemind(0);
                }
				if (null == maiSendBox.getHasReceiptReceiveCount()) {
					maiSendBox.setHasReceiptReceiveCount(0);
				}
				if (null == maiSendBox.getHasReceiptCopyCount()) {
					maiSendBox.setHasReceiptCopyCount(0);
				}
				if (0 != maiSendBox.getIsReceiveSmsRemind() || 0 != maiSendBox.getIsCopySmsRemind()) {
					smaWarn(maiSendBox, customUser);
				}
				maiSendBox.setCreateUserId(obj.getCreateUserId());
				String[] receiveUserIds = null;
				String[] receiveUserNames = null;
				String[] receiveFullNames = null;

				if (StringUtils.isNotEmpty(maiReceiveBox.getReceiveUserIds())
						&& StringUtils.isNotEmpty(maiReceiveBox.getReceiveUserNames())
						&& StringUtils.isNotEmpty(maiReceiveBox.getReceiveFullNames())) {
					if (maiReceiveBox.getReceiveUserIds().indexOf(";") > -1) {
						if (maiReceiveBox.getReceiveUserIds().endsWith(";")) {
							receiveUserIds = maiReceiveBox.getReceiveUserIds()
									.substring(0, maiReceiveBox.getReceiveUserIds().lastIndexOf(";")).split(";");
						} else {
							receiveUserIds = maiReceiveBox.getReceiveUserIds().split(";");
						}
					} else {
						receiveUserIds = new String[1];
						receiveUserIds[0] = maiReceiveBox.getReceiveUserIds();
					}
					if (maiReceiveBox.getReceiveUserNames().indexOf(";") > -1) {
						if (maiReceiveBox.getReceiveUserNames().endsWith(";")) {
							receiveUserNames = maiReceiveBox.getReceiveUserNames()
									.substring(0, maiReceiveBox.getReceiveUserNames().lastIndexOf(";")).split(";");
						} else {
							receiveUserNames = maiReceiveBox.getReceiveUserNames().split(";");
						}
					} else {
						receiveUserNames = new String[1];
						receiveUserNames[0] = maiReceiveBox.getReceiveUserNames();
					}
					if (maiReceiveBox.getReceiveFullNames().indexOf(";") > -1) {
						if (maiReceiveBox.getReceiveFullNames().endsWith(";")) {
							receiveFullNames = maiReceiveBox.getReceiveFullNames()
									.substring(0, maiReceiveBox.getReceiveFullNames().lastIndexOf(";")).split(";");
						} else {
							receiveFullNames = maiReceiveBox.getReceiveFullNames().split(";");
						}
					} else {
						receiveFullNames = new String[1];
						receiveFullNames[0] = maiReceiveBox.getReceiveFullNames();
					}
				}
				String[] copyUserIds = null;
				String[] copyUserNames = null;
				String[] copyFullNames = null;
				if (StringUtils.isNotEmpty(maiReceiveBox.getCopyUserIds())
						&& StringUtils.isNotEmpty(maiReceiveBox.getCopyUserNames())
						&& StringUtils.isNotEmpty(maiReceiveBox.getCopyFullNames())) {
					if (maiReceiveBox.getCopyUserIds().indexOf(";") > -1) {
						if (maiReceiveBox.getCopyUserIds().endsWith(";")) {
							copyUserIds = maiReceiveBox.getCopyUserIds()
									.substring(0, maiReceiveBox.getCopyUserIds().lastIndexOf(";")).split(";");
						} else {
							copyUserIds = maiReceiveBox.getCopyUserIds().split(";");
						}
					} else {
						copyUserIds = new String[1];
						copyUserIds[0] = maiReceiveBox.getCopyUserIds();
					}
					if (maiReceiveBox.getCopyUserNames().indexOf(";") > -1) {
						if (maiReceiveBox.getCopyUserNames().endsWith(";")) {
							copyUserNames = maiReceiveBox.getCopyUserNames()
									.substring(0, maiReceiveBox.getCopyUserNames().lastIndexOf(";")).split(";");
						} else {
							copyUserNames = maiReceiveBox.getCopyUserNames().split(";");
						}
					} else {
						copyUserNames = new String[1];
						copyUserNames[0] = maiReceiveBox.getCopyUserNames();
					}
					if (maiReceiveBox.getCopyFullNames().indexOf(";") > -1) {
						if (maiReceiveBox.getCopyFullNames().endsWith(";")) {
							copyFullNames = maiReceiveBox.getCopyFullNames()
									.substring(0, maiReceiveBox.getCopyFullNames().lastIndexOf(";")).split(";");
						} else {
							copyFullNames = maiReceiveBox.getCopyFullNames().split(";");
						}
					} else {
						copyFullNames = new String[1];
						copyFullNames[0] = maiReceiveBox.getCopyFullNames();
					}
				}
				if (null != receiveUserIds) {
					maiSendBox.setReceiveUserCount(receiveUserIds.length);
					maiSendBoxService.insert(maiSendBox);
					for (int i = 0; i <= (receiveUserIds.length - 1); i++) {
						MaiReceiveBox receiveBox = new MaiReceiveBox();
						BeanUtils.copyProperties(maiReceiveBox, receiveBox);
						MaiReceiveBoxSearch receiveBoxSearch = new MaiReceiveBoxSearch();
						BeanUtils.copyProperties(maiReceiveBoxSearch, receiveBoxSearch);
						receiveBoxSearch.setReceiveUserId(Long.parseLong(receiveUserIds[i]));
						receiveBox.setReceiveUserName(receiveUserNames[i]);
						receiveBox.setReceiveFullName(receiveFullNames[i]);
						receiveBox.setSendId(maiSendBox.getId());
						receiveBoxSearch.setSendTime(date);
						if (null != customUser) {
							receiveBox.setSendUserId(customUser.getId());
							receiveBox.setSendUserName(customUser.getUserName());
							receiveBoxSearch.setSendFullName(customUser.getFullName());
						}
						maiReceiveBoxList.add(receiveBox);
						maiReceiveBoxSearchList.add(receiveBoxSearch);
					}
				}
				if (null != copyUserIds) {
					for (int i = 0; i <= (copyUserIds.length - 1); i++) {
						MaiReceiveBox receiveBox = new MaiReceiveBox();
						BeanUtils.copyProperties(maiReceiveBox, receiveBox);
						MaiReceiveBoxSearch receiveBoxSearch = new MaiReceiveBoxSearch();
                        BeanUtils.copyProperties(maiReceiveBoxSearch, receiveBoxSearch);
                        receiveBoxSearch.setReceiveUserId(Long.parseLong(copyUserIds[i]));
						receiveBox.setReceiveUserName(copyUserNames[i]);
						receiveBox.setReceiveFullName(copyFullNames[i]);
						receiveBox.setSendId(maiSendBox.getId());
						receiveBoxSearch.setSendTime(date);
						if (null != customUser) {
							receiveBox.setSendUserId(customUser.getId());
							receiveBox.setSendUserName(customUser.getUserName());
							receiveBoxSearch.setSendFullName(customUser.getFullName());
						}
						maiReceiveBoxList.add(receiveBox);
						maiReceiveBoxSearchList.add(receiveBoxSearch);
					}
				}
				 // 去重
                for (int i = 0; i <= maiReceiveBoxSearchList.size() - 1; i++) {
                    for (int j = maiReceiveBoxSearchList.size() - 1; j > i; j--) {
                        MaiReceiveBoxSearch receiveBox1 = maiReceiveBoxSearchList.get(i);
                        MaiReceiveBoxSearch receiveBox2 = maiReceiveBoxSearchList.get(j);
                        if (null != receiveBox1 && null != receiveBox2) {
                            if ((receiveBox1.getReceiveUserId().equals(receiveBox2.getReceiveUserId())) && (receiveBox1
                                .getReceiveUserId().longValue() == receiveBox1.getReceiveUserId().longValue())) {
                                maiReceiveBoxList.remove(j);
                                maiReceiveBoxSearchList.remove(j);
                            }
                        }
                    }
                }

				if (null != maiReceiveBoxList && !maiReceiveBoxList.isEmpty()) {
					maiReceiveBoxService.insertBatch(maiReceiveBoxList);
					for (int i = 0; i < maiReceiveBoxList.size(); i++) {
                        maiReceiveBoxSearchList.get(i).setId(maiReceiveBoxList.get(i).getId());
                    }
                    maiReceiveBoxSearchService.insertBatch(maiReceiveBoxSearchList);
				}

				List<MaiAttachment> newMaiAttachmentList = new ArrayList<MaiAttachment>();
				if (null != maiAttachmentList && null != maiReceiveBoxList) {
					if (!maiAttachmentList.isEmpty() && !maiReceiveBoxList.isEmpty()) {
						for (MaiAttachment attachment : maiAttachmentList) {
							MaiAttachment maiAttachment = new MaiAttachment();
							BeanUtils.copyProperties(attachment, maiAttachment);
							maiAttachment.setSendId(maiSendBox.getId());
							maiAttachment.setReceiveId(0L);
							maiAttachment.setDraftId(0L);
							newMaiAttachmentList.add(maiAttachment);
						}

						for (MaiReceiveBox receiveBox : maiReceiveBoxList) {
							for (MaiAttachment attachment : maiAttachmentList) {
								MaiAttachment maiAttachment = new MaiAttachment();
								BeanUtils.copyProperties(attachment, maiAttachment);
								maiAttachment.setSendId(0L);
								maiAttachment.setReceiveId(receiveBox.getId());
								maiAttachment.setDraftId(0L);
								newMaiAttachmentList.add(maiAttachment);
							}
						}
					}
				}
				if (null != newMaiAttachmentList && !newMaiAttachmentList.isEmpty()) {
					maiAttachmentService.insertBatch(newMaiAttachmentList);
				}
				
			}

			// ---------------------在线消息调用（start）------------------------------
			List<String> templateList = new ArrayList<String>();
			Map<String, String> list = new HashMap<String, String>();
			if (null != obj.getMaiSendBox()) {
				list.put("recipient", maiSendBox.getReceiveFullNames());
				list.put("refer", maiSendBox.getCopyFullNames());
				list.put("sender", customUser.getFullName());
				list.put("theme", obj.getMaiSendBox().getSubjectText());
				String isUrgent = "";
				if (null != obj.getMaiSendBox().getIsUrgent() && obj.getMaiSendBox().getIsUrgent() != 0) {
					isUrgent = WebConstant.URGENT;
				}
				list.put("emergency", isUrgent);
				list.put("sendTime", DateUtil.datetime2str(obj.getMaiSendBox().getSendTime()));
				templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
						WebConstant.TEMPLATE_MAIL_CODE, WebConstant.TEMPLATE_BEHAVIOR_ZXYJ);
			} else if (null != obj.getMaiDraftBox()) {

			} else if (null != obj.getMaiReceiveBox()) {
				list.put("recipient", maiSendBox.getReceiveFullNames());
				list.put("refer", maiSendBox.getCopyFullNames());
				list.put("sender", customUser.getFullName());
				list.put("theme", obj.getMaiReceiveBoxSearch().getSubjectText());
				String isUrgent = "";
				if (null != obj.getMaiReceiveBoxSearch().getIsUrgent() && obj.getMaiReceiveBoxSearch().getIsUrgent() != 0) {
					isUrgent = WebConstant.URGENT;
				}
				list.put("emergency", isUrgent);
				list.put("sendTime", DateUtil.datetime2str(obj.getMaiReceiveBoxSearch().getSendTime()));
				templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
						WebConstant.TEMPLATE_MAIL_CODE, WebConstant.TEMPLATE_BEHAVIOR_ZXYJ);
			}

			AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
			// 前端传来的1个或多个userId（以';'号隔开）
			if (null != templateList && !templateList.isEmpty()) {
				requestVO.setMsgContent(templateList.get(0));
			}

			// 封装推送信息请求信息
			requestVO.setFromUserId(customUser.getId());
			requestVO.setFromUserFullName(customUser.getFullName());
			requestVO.setFromUserName(customUser.getUserName());
			requestVO.setMsgType(WebConstant.ONLINE_MSG_MAIL);
			for (MaiReceiveBoxSearch mai : maiReceiveBoxSearchList) {
				if (mai != null) {
					if (mai.getReceiveUserId() != null) {
						requestVO.setBusinessKey(String.valueOf(mai.getId()));
						requestVO.setGoUrl("mai/maiReceiveBox/maiReceiveBoxPageDetailPage.html?id=" + mai.getId()
								+ "&isImportant=" + mai.getIsImportant() + "&isRead=" + mai.getIsRead());
						List<String> recId = new ArrayList<String>();
						recId.add(String.valueOf(mai.getReceiveUserId()));
						requestVO.setToUserId(recId);
						autMsgOnlineService.pushMessageToUserList(requestVO);
						recId.clear();
					}
				}
			}
			// ---------------------在线消息调用（end）------------------------------
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	public void smaWarn(MaiSendBox maiSendBox, AutUser user) throws Exception {
		// 调用模板
		Map<String, String> list = new HashMap<String, String>();
		list.put("recipient", maiSendBox.getReceiveFullNames());
		list.put("refer", maiSendBox.getCopyFullNames());
		list.put("sender", user.getFullName());
		list.put("theme", maiSendBox.getSubjectText());
		String isUrgent = "";
		if (null != maiSendBox.getIsUrgent() && maiSendBox.getIsUrgent() != 0) {
			isUrgent = WebConstant.URGENT;
		}
		list.put("emergency", isUrgent);
		list.put("sendTime", DateUtil.date2str(maiSendBox.getSendTime()));
		List<String> templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_SMS,
				WebConstant.TEMPLATE_MAIL_NAME, WebConstant.TEMPLATE_BEHAVIOR_ZXYJ);
		SmsShortMessage smsShortMessage = new SmsShortMessage();
		String userids = maiSendBox.getReceiveUserIds();
		String usercids = maiSendBox.getCopyUserIds();
		String userNames = maiSendBox.getReceiveFullNames();
		String usercNames = maiSendBox.getCopyFullNames();
		String[] ids = userids.split(";");
		if((maiSendBox.getIsReceiveSmsRemind() != 0 && maiSendBox.getIsCopySmsRemind() == 0) || (maiSendBox.getIsReceiveSmsRemind() != 0 && maiSendBox.getIsCopySmsRemind() != 0 && usercids == "")) {
		  smsShortMessage.setSendNumber(ids.length);
		  userids = userids.substring(0, userids.length() - 1);
	      userNames = userNames.substring(0, userNames.length() - 1);
	      smsShortMessage.setReceiverId(userids);
	        smsShortMessage.setReceiverName(userNames);
	        smsShortMessage.setContent(templateList.get(0));
	        
	        smsShortMessage.setIsActive(1);
	        smsShortMessage.setSendTime(new Date());
	        smsShortMessage.setSendStatus(0);
	        smsShortMessage.setCreateUserId(user.getId());
	        if (templateList != null && !templateList.isEmpty()) {
	            smsShortMessage.setTheme(templateList.get(0));
	        }
	        smsShortMessageService.add(smsShortMessage,user); 
		}else if(maiSendBox.getIsReceiveSmsRemind() != 0 && maiSendBox.getIsCopySmsRemind() != 0 && usercids != "") {
		  String[] idcs = usercids.split(";");
		  usercids = usercids.substring(0, usercids.length() - 1);
		  usercNames = usercNames.substring(0, usercNames.length() - 1);
		  smsShortMessage.setSendNumber(ids.length + idcs.length);
		  smsShortMessage.setReceiverId(userids + usercids);
          smsShortMessage.setReceiverName(userNames + usercNames);
          smsShortMessage.setContent(templateList.get(0));
          smsShortMessage.setIsActive(1);
          smsShortMessage.setSendTime(new Date());
          smsShortMessage.setSendStatus(0);
          smsShortMessage.setCreateUserId(user.getId());
          if (templateList != null && !templateList.isEmpty()) {
              smsShortMessage.setTheme(templateList.get(0));
          }
          smsShortMessageService.add(smsShortMessage,user); 
		}else if(maiSendBox.getIsReceiveSmsRemind() == 0 && maiSendBox.getIsCopySmsRemind() != 0 && usercids != "") {
		  String[] idcs = usercids.split(";");
          usercids = usercids.substring(0, usercids.length() - 1);
          usercNames = usercNames.substring(0, usercNames.length() - 1);
          smsShortMessage.setSendNumber(idcs.length);
          smsShortMessage.setReceiverId(usercids);
          smsShortMessage.setReceiverName(usercNames);
          smsShortMessage.setContent(templateList.get(0));
          smsShortMessage.setIsActive(1);
          smsShortMessage.setSendTime(new Date());
          smsShortMessage.setSendStatus(0);
          smsShortMessage.setCreateUserId(user.getId());
          if (templateList != null && !templateList.isEmpty()) {
              smsShortMessage.setTheme(templateList.get(0));
          }
          smsShortMessageService.add(smsShortMessage,user); 
		}
	}

	@Transactional
	public void addMsg(MaiWriteVO obj, AutUser customUser) throws Exception {
		if (null != obj) {
			MaiSendBox maiSendBox = obj.getMaiSendBox();
			MaiReceiveBox maiReceiveBox = obj.getMaiReceiveBox();
			MaiReceiveBoxSearch maiReceiveBoxSearch = obj.getMaiReceiveBoxSearch();
			List<MaiAttachment> maiAttachmentList = obj.getMaiAttachmentList();
			List<MaiReceiveBox> maiReceiveBoxList = new ArrayList<MaiReceiveBox>();
			List<MaiReceiveBoxSearch> maiReceiveBoxSearchList = new ArrayList<MaiReceiveBoxSearch>();
			int size = 0;
			if (null != maiReceiveBox) {
				int allowMailSpace = 0;
				int myMailSpace = 0;
				if (null != customUser && null != customUser.getId()) {
					Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
					departmentUserWhere.eq("user_id", customUser.getId());
					departmentUserWhere.setSqlSelect("id,is_leader");
					List<AutDepartmentUser> departmentUserList = autDepartmentUserService
							.selectList(departmentUserWhere);
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
					maiSendBoxWhere.eq("send_user_id", customUser.getId());
					maiSendBoxWhere.setSqlSelect("id,send_user_id,mail_size");
					List<MaiSendBox> maiSendBoxList = maiSendBoxService.selectList(maiSendBoxWhere);
					if (null != maiSendBoxList && !maiSendBoxList.isEmpty()) {
						for (MaiSendBox sendBox : maiSendBoxList) {
							if (null != sendBox && null != sendBox.getMailSize()) {
								myMailSpace += sendBox.getMailSize();
							}
						}
					}
				}
				if (null == maiSendBox) {
					maiSendBox = new MaiSendBox();
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
				if (null == maiReceiveBox.getIsRevoke()) {
					maiReceiveBox.setIsRevoke(0);
				}
				if (null == maiReceiveBoxSearch.getIsScrap()) {
				    maiReceiveBoxSearch.setIsScrap(0);
				}

				if (null == maiReceiveBoxSearch.getIsRead()) {
				    maiReceiveBoxSearch.setIsRead(0);
				}
				if (null == maiReceiveBoxSearch.getIsUrgent()) {
				    maiReceiveBoxSearch.setIsUrgent(0);
				}
				if (null == maiReceiveBoxSearch.getIsImportant()) {
				    maiReceiveBoxSearch.setIsImportant(0);
				}
				if (StringUtils.isNotEmpty(maiReceiveBox.getMailContent())) {
					int len = (((maiReceiveBox.getMailContent().getBytes("utf-8").length) / 1024));
					// 邮件内容空间校验
					if (((len + size + myMailSpace) / 1024) > allowMailSpace) {
						return;
					}
					maiReceiveBox.setMailSize((size + len));
				} else {
					maiReceiveBox.setMailSize(size);
				}
				Date date = new Date();
				maiReceiveBoxSearch.setSendTime(date);
				maiReceiveBox.setWeekDay(DateUtil.getWeek(date));
				BeanUtils.copyProperties(maiReceiveBox, maiSendBox);
				maiSendBox.setIsSendSuccess(1);
				if (null != customUser) {
					maiSendBox.setSendUserId(customUser.getId());
					maiSendBox.setSendUserName(customUser.getUserName());
					maiSendBox.setSendFullName(customUser.getFullName());
				}
				if (null == maiSendBox.getIsReceiveSmsRemind()) {
					maiSendBox.setIsReceiveSmsRemind(0);
				}
				if (null == maiSendBox.getIsCopySmsRemind()) {
					maiSendBox.setIsCopySmsRemind(0);
				}
				if (null == maiSendBox.getHasReceiveSmsRemind()) {
					maiSendBox.setHasReceiveSmsRemind(0);
				}
				if (null == maiSendBox.getHasCopySmsRemind()) {
					maiSendBox.setHasCopySmsRemind(0);
				}
				if (null == maiSendBox.getIsReceipt()) {
					maiSendBox.setIsReceipt(0);
				} else {
					maiSendBox.setIsReceipt(1);
				}
				if (null == maiSendBox.getIsReceiptMail()) {
					maiSendBox.setIsReceiptMail(0);
				}
				if (null == maiSendBox.getHasReceiptReceiveCount()) {
					maiSendBox.setHasReceiptReceiveCount(0);
				}
				if (null == maiSendBox.getHasReceiptCopyCount()) {
					maiSendBox.setHasReceiptCopyCount(0);
				}
				maiSendBox.setCreateUserId(obj.getCreateUserId());
				String[] receiveUserIds = null;
				String[] receiveUserNames = null;
				String[] receiveFullNames = null;

				if (StringUtils.isNotEmpty(maiReceiveBox.getReceiveUserIds())
						&& StringUtils.isNotEmpty(maiReceiveBox.getReceiveUserNames())
						&& StringUtils.isNotEmpty(maiReceiveBox.getReceiveFullNames())) {
					if (maiReceiveBox.getReceiveUserIds().indexOf(";") > -1) {
						if (maiReceiveBox.getReceiveUserIds().endsWith(";")) {
							receiveUserIds = maiReceiveBox.getReceiveUserIds()
									.substring(0, maiReceiveBox.getReceiveUserIds().lastIndexOf(";")).split(";");
						} else {
							receiveUserIds = maiReceiveBox.getReceiveUserIds().split(";");
						}
					} else {
						receiveUserIds = new String[1];
						receiveUserIds[0] = maiReceiveBox.getReceiveUserIds();
					}
					if (maiReceiveBox.getReceiveUserNames().indexOf(";") > -1) {
						if (maiReceiveBox.getReceiveUserNames().endsWith(";")) {
							receiveUserNames = maiReceiveBox.getReceiveUserNames()
									.substring(0, maiReceiveBox.getReceiveUserNames().lastIndexOf(";")).split(";");
						} else {
							receiveUserNames = maiReceiveBox.getReceiveUserNames().split(";");
						}
					} else {
						receiveUserNames = new String[1];
						receiveUserNames[0] = maiReceiveBox.getReceiveUserNames();
					}
					if (maiReceiveBox.getReceiveFullNames().indexOf(";") > -1) {
						if (maiReceiveBox.getReceiveFullNames().endsWith(";")) {
							receiveFullNames = maiReceiveBox.getReceiveFullNames()
									.substring(0, maiReceiveBox.getReceiveFullNames().lastIndexOf(";")).split(";");
						} else {
							receiveFullNames = maiReceiveBox.getReceiveFullNames().split(";");
						}
					} else {
						receiveFullNames = new String[1];
						receiveFullNames[0] = maiReceiveBox.getReceiveFullNames();
					}
				}
				String[] copyUserIds = null;
				String[] copyUserNames = null;
				String[] copyFullNames = null;
				if (StringUtils.isNotEmpty(maiReceiveBox.getCopyUserIds())
						&& StringUtils.isNotEmpty(maiReceiveBox.getCopyUserNames())
						&& StringUtils.isNotEmpty(maiReceiveBox.getCopyFullNames())) {
					if (maiReceiveBox.getCopyUserIds().indexOf(";") > -1) {
						if (maiReceiveBox.getCopyUserIds().endsWith(";")) {
							copyUserIds = maiReceiveBox.getCopyUserIds()
									.substring(0, maiReceiveBox.getCopyUserIds().lastIndexOf(";")).split(";");
						} else {
							copyUserIds = maiReceiveBox.getCopyUserIds().split(";");
						}
					} else {
						copyUserIds = new String[1];
						copyUserIds[0] = maiReceiveBox.getCopyUserIds();
					}
					if (maiReceiveBox.getCopyUserNames().indexOf(";") > -1) {
						if (maiReceiveBox.getCopyUserNames().endsWith(";")) {
							copyUserNames = maiReceiveBox.getCopyUserNames()
									.substring(0, maiReceiveBox.getCopyUserNames().lastIndexOf(";")).split(";");
						} else {
							copyUserNames = maiReceiveBox.getCopyUserNames().split(";");
						}
					} else {
						copyUserNames = new String[1];
						copyUserNames[0] = maiReceiveBox.getCopyUserNames();
					}
					if (maiReceiveBox.getCopyFullNames().indexOf(";") > -1) {
						if (maiReceiveBox.getCopyFullNames().endsWith(";")) {
							copyFullNames = maiReceiveBox.getCopyFullNames()
									.substring(0, maiReceiveBox.getCopyFullNames().lastIndexOf(";")).split(";");
						} else {
							copyFullNames = maiReceiveBox.getCopyFullNames().split(";");
						}
					} else {
						copyFullNames = new String[1];
						copyFullNames[0] = maiReceiveBox.getCopyFullNames();
					}
				}
				if (null != receiveUserIds) {
					maiSendBox.setReceiveUserCount(receiveUserIds.length);
					for (int i = 0; i <= (receiveUserIds.length - 1); i++) {
						MaiReceiveBox receiveBox = new MaiReceiveBox();
						BeanUtils.copyProperties(maiReceiveBox, receiveBox);
						MaiReceiveBoxSearch receiveBoxSearch = new MaiReceiveBoxSearch();
                        BeanUtils.copyProperties(maiReceiveBoxSearch, receiveBoxSearch);
                        receiveBoxSearch.setReceiveUserId(Long.parseLong(receiveUserIds[i]));
						receiveBox.setReceiveUserName(receiveUserNames[i]);
						receiveBox.setReceiveFullName(receiveFullNames[i]);
						receiveBox.setSendId((long) -1);
						receiveBoxSearch.setSendTime(date);
						if (null != customUser) {
							receiveBox.setSendUserId(customUser.getId());
							receiveBox.setSendUserName(customUser.getUserName());
							receiveBoxSearch.setSendFullName(customUser.getFullName());
						}
						maiReceiveBoxList.add(receiveBox);
						maiReceiveBoxSearchList.add(receiveBoxSearch);
					}
				}
				if (null != copyUserIds) {
					for (int i = 0; i <= (copyUserIds.length - 1); i++) {
						MaiReceiveBox receiveBox = new MaiReceiveBox();
						BeanUtils.copyProperties(maiReceiveBox, receiveBox);
						MaiReceiveBoxSearch receiveBoxSearch = new MaiReceiveBoxSearch();
                        BeanUtils.copyProperties(maiReceiveBoxSearch, receiveBoxSearch);
                        receiveBoxSearch.setReceiveUserId(Long.parseLong(copyUserIds[i]));
						receiveBox.setReceiveUserName(copyUserNames[i]);
						receiveBox.setReceiveFullName(copyFullNames[i]);
						receiveBox.setSendId(maiSendBox.getId());
						receiveBoxSearch.setSendTime(date);
						if (null != customUser) {
							receiveBox.setSendUserId(customUser.getId());
							receiveBox.setSendUserName(customUser.getUserName());
							receiveBoxSearch.setSendFullName(customUser.getFullName());
						}
						maiReceiveBoxList.add(receiveBox);
						maiReceiveBoxSearchList.add(receiveBoxSearch);
					}
				}

				// 去重
                for (int i = 0; i <= maiReceiveBoxList.size() - 1; i++) {
                    for (int j = maiReceiveBoxList.size() - 1; j > i; j--) {
                        MaiReceiveBoxSearch receiveBox1 = maiReceiveBoxSearchList.get(i);
                        MaiReceiveBoxSearch receiveBox2 = maiReceiveBoxSearchList.get(j);
                        if (null != receiveBox1 && null != receiveBox2) {
                            if ((receiveBox1.getReceiveUserId().equals(receiveBox2.getReceiveUserId())) && (receiveBox1
                                .getReceiveUserId().longValue() == receiveBox1.getReceiveUserId().longValue())) {
                                maiReceiveBoxList.remove(j);
                                maiReceiveBoxSearchList.remove(j);
                            }
                        }
                    }
                }

				if (null != maiReceiveBoxList && !maiReceiveBoxList.isEmpty()) {
					maiReceiveBoxService.insertBatch(maiReceiveBoxList);
				}

				List<MaiAttachment> newMaiAttachmentList = new ArrayList<MaiAttachment>();
				if (null != maiAttachmentList && null != maiReceiveBoxList) {
					if (!maiAttachmentList.isEmpty() && !maiReceiveBoxList.isEmpty()) {
						for (MaiAttachment attachment : maiAttachmentList) {
							MaiAttachment maiAttachment = new MaiAttachment();
							BeanUtils.copyProperties(attachment, maiAttachment);
							maiAttachment.setSendId(maiSendBox.getId());
							maiAttachment.setReceiveId(0L);
							maiAttachment.setDraftId(0L);
							newMaiAttachmentList.add(maiAttachment);
						}

						for (MaiReceiveBox receiveBox : maiReceiveBoxList) {
							for (MaiAttachment attachment : maiAttachmentList) {
								MaiAttachment maiAttachment = new MaiAttachment();
								BeanUtils.copyProperties(attachment, maiAttachment);
								maiAttachment.setSendId(0L);
								maiAttachment.setReceiveId(receiveBox.getId());
								maiAttachment.setDraftId(0L);
								newMaiAttachmentList.add(maiAttachment);
							}
						}
					}
				}
				if (null != newMaiAttachmentList && !newMaiAttachmentList.isEmpty()) {
					maiAttachmentService.insertBatch(newMaiAttachmentList);
				}
				
			}

			// ---------------------在线消息调用（start）------------------------------
			List<String> templateList = new ArrayList<String>();
			Map<String, String> list = new HashMap<String, String>();
			if (null != obj.getMaiSendBox()) {
				list.put("recipient", maiSendBox.getReceiveFullNames());
				list.put("refer", maiSendBox.getCopyFullNames());
				list.put("sender", customUser.getFullName());
				list.put("theme", obj.getMaiSendBox().getSubjectText());
				String isUrgent = "";
				if (null != obj.getMaiSendBox().getIsUrgent() && obj.getMaiSendBox().getIsUrgent() != 0) {
					isUrgent = WebConstant.URGENT;
				}
				list.put("emergency", isUrgent);
				list.put("sendTime", DateUtil.datetime2str(obj.getMaiSendBox().getSendTime()));
				templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
						WebConstant.TEMPLATE_MAIL_CODE, WebConstant.TEMPLATE_BEHAVIOR_ZXYJ);
			} else if (null != obj.getMaiDraftBox()) {

			} else if (null != obj.getMaiReceiveBox()) {
				list.put("recipient", maiSendBox.getReceiveFullNames());
				list.put("refer", maiSendBox.getCopyFullNames());
				list.put("sender", customUser.getFullName());
				list.put("theme", obj.getMaiReceiveBoxSearch().getSubjectText());
				String isUrgent = "";
				if (null != obj.getMaiReceiveBoxSearch().getIsUrgent() && obj.getMaiReceiveBoxSearch().getIsUrgent() != 0) {
					isUrgent = WebConstant.URGENT;
				}
				list.put("emergency", isUrgent);
				list.put("sendTime", DateUtil.datetime2str(obj.getMaiReceiveBoxSearch().getSendTime()));
				templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
						WebConstant.TEMPLATE_MAIL_CODE, WebConstant.TEMPLATE_BEHAVIOR_ZXYJ);
			}

			AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
			// 前端传来的1个或多个userId（以';'号隔开）
			if (null != templateList && !templateList.isEmpty()) {
				requestVO.setMsgContent(templateList.get(0));
			}

			// 封装推送信息请求信息
			requestVO.setFromUserId(customUser.getId());
			requestVO.setFromUserFullName(customUser.getFullName());
			requestVO.setFromUserName(customUser.getUserName());
			requestVO.setMsgType(WebConstant.ONLINE_MSG_MAIL);
			List<AljoinMQMsg> messageList = new ArrayList<AljoinMQMsg>();
			for (MaiReceiveBoxSearch mai : maiReceiveBoxSearchList) {
				if (mai != null) {
					if (mai.getReceiveUserId() != null) {
                        AljoinMQMsg msg = new AljoinMQMsg();
                        msg.setTags(MQTag.MQ_TAG_ALJOIN_MAI);
                        msg.setKeys(String.valueOf(mai.getId()));
                        msg.setBody("mai/maiReceiveBox/maiReceiveBoxPageDetailPage.html?id=" + mai.getId()
                        + "&isImportant=" + mai.getIsImportant() + "&isRead=" + mai.getIsRead());
                        msg.setFlag(0);
                        msg.setWaitStoreMsgOK(false);
                        messageList.add(msg);
					}
				}
			}
			aljoinMQService.sendOneWay(messageList, MQProducer.MQ_PRODUCER_ALJOIN_MAI);
			// ---------------------在线消息调用（end）------------------------------
		}

	}

}
