package aljoin.sms.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserRoleService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.sms.dao.entity.SmsShortMessageDraft;
import aljoin.sms.dao.entity.SmsShortMessageSummary;
import aljoin.sms.dao.mapper.SmsShortMessageDraftMapper;
import aljoin.sms.iservice.SmsShortMessageDraftService;
import aljoin.sms.iservice.SmsShortMessageService;
import aljoin.sms.iservice.SmsShortMessageSummaryService;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;

/**
 * 
 * 短信草稿表(服务实现类).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-10
 */
@Service
public class SmsShortMessageDraftServiceImpl extends ServiceImpl<SmsShortMessageDraftMapper, SmsShortMessageDraft>
		implements SmsShortMessageDraftService {

	private final static Logger logger = LoggerFactory.getLogger(SmsShortMessageDraftServiceImpl.class);

	@Resource
	private SmsShortMessageDraftMapper mapper;
	@Resource
	private AutUserService autUserService;
	@Resource
	private SmsShortMessageDraftService smsShortMessageDraftService;
	@Resource
	private SmsShortMessageService smsShortMessageService;
	@Resource
	private SmsShortMessageSummaryService smsShortMessageSummaryService;
	@Resource
	private AutUserPubService autUserPubService;
	@Resource
	private AutUserRoleService autUserRoleService;
	@Resource
	private AutUserPositionService autUserPositionService;
	@Resource
	private AutPositionService autPositionService;
	@Resource
	private SysParameterService  sysParameterService;

	@Override
	public Page<SmsShortMessageDraft> list(PageBean pageBean, SmsShortMessageDraft obj, String time1, String time2)
			throws Exception {
		Where<SmsShortMessageDraft> where = new Where<SmsShortMessageDraft>();
		where.orderBy("create_time", false);

		if (StringUtils.isNotEmpty(obj.getTheme())) {
			where.like("theme", obj.getTheme());
			where.or("receiver_name LIKE {0}", "%" + obj.getReceiverName() + "%");
		}
		if (StringUtils.isNotEmpty(time1) && StringUtils.isNotEmpty(time2)) {
			where.between("last_update_time", DateUtil.str2datetime(time1 + " 00:00:00"),
					DateUtil.str2datetime(time2 + " 23:59:59"));
		} else if (StringUtils.isNotEmpty(time1)) {
			where.ge("last_update_time", DateUtil.str2datetime(time1 + " 00:00:00"));
		} else if (StringUtils.isNotEmpty(time2)) {
			where.le("last_update_time", DateUtil.str2datetime(time2 + " 23:59:59"));
		}

		Page<SmsShortMessageDraft> page = selectPage(
				new Page<SmsShortMessageDraft>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		return page;
	}

	@Override
	public void physicsDeleteById(Long id) throws Exception {
		mapper.physicsDeleteById(id);
	}

	@Override
	public void copyObject(SmsShortMessageDraft obj) throws Exception {
		mapper.copyObject(obj);
	}

	@Override
	public RetMsg deleteShortMessageDraftList(String ids) throws Exception {

		RetMsg retMsg = new RetMsg();
		try {
			// 把多个shortMessageDraftId分割，放进shortMessageDraftIdList
			List<String> shortMessageDraftIdList = Arrays.asList(ids.split(";"));
			// 批量删除
			deleteBatchIds(shortMessageDraftIdList);
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
		}

		return retMsg;
	}

	@Override
	public SmsShortMessageDraft detail(Long id) throws Exception {
		SmsShortMessageDraft sms = selectById(id);
		// 人员排序（收件人）
		List<String> tmpList = new ArrayList<String>();
		String[] ids = sms.getReceiverId().split(";");
		List<Long> receiveIds = new ArrayList<Long>();
		String allId = "";
		for (int i = 0; i < ids.length; i++) {
			receiveIds.add(Long.valueOf(ids[i]));
		}
		Where<AutUser> where1 = new Where<AutUser>();
		where1.in("id", ids);
		where1.setSqlSelect("id", "user_name", "full_name");
		List<AutUser> userList = autUserService.selectList(where1);
		Map<Long, Integer> rankList = autUserService.getUserRankList(receiveIds, null);
		if (rankList.size() > 0) {
			for (Long entry : rankList.keySet()) {
				String usrid = entry.toString();
				allId += usrid + ";";
				for (AutUser autUser : userList) {
					String tmp = autUser.getId().toString();
					if (tmp.equals(usrid)) {
						tmpList.add(autUser.getFullName());
					}
				}
			}
		}
		String names = "";
		for (String name : tmpList) {
			names += name + ";";
		}
		sms.setReceiverName(names.substring(0, names.length() - 1));
		sms.setReceiverId(allId);
		return sms;
	}

	@Override
	public RetMsg sendDraft(SmsShortMessage obj, String userId) throws Exception {
		// TODO Auto-generated method stub
		RetMsg retMsg = new RetMsg();
		if (obj != null && obj.getId() != null) {
			if (smsShortMessageDraftService.deleteById(obj.getId())) {
				obj.setId(null);
				if (obj.getContent() == null && "".equals(obj.getContent())) {
					retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
					retMsg.setMessage("短信内容为空!");
					return retMsg;
				}
				if (obj.getReceiverId() == null && "".equals(obj.getReceiverId())) {
					retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
					retMsg.setMessage("短信收件人为空!");
					return retMsg;
				}
				if (obj.getTheme() == null && "".equals(obj.getTheme())) {
					retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
					retMsg.setMessage("短信主题不能为空!");
					return retMsg;
				}
				String reIds = obj.getReceiverId();
				if (";".equals(reIds.substring(reIds.length(), reIds.length()))) {
					reIds = reIds.substring(0, reIds.length() - 1);
				}
				String[] resid = reIds.split(";");
				obj.setSendNumber(resid.length);
				obj.setIsActive(1);
				obj.setSendTime(new Date());
				obj.setSendStatus(1);
				SysParameter parameter = sysParameterService.selectBykey("is_send_sms");
				String isTrue = "";
				if (null != parameter && StringUtils.isNotEmpty(parameter.getParamValue())) {						
					isTrue=parameter.getParamValue();
					if("1".equals(isTrue)){
						obj.setSendStatus(1);
					}else{
						obj.setSendStatus(0);
					}
				}
				if (smsShortMessageService.insert(obj) && "1".equals(isTrue)) {
					// 获取用户电话号码
					Where<AutUser> autWhere = new Where<AutUser>();
					autWhere.in("id", resid);
					autWhere.setSqlSelect("id,full_name");
					List<AutUser> autList = autUserService.selectList(autWhere);
					Map<Long, String> autMap = new HashMap<Long, String>();
					if (autList != null && autList.size() > 0) {
						for (AutUser autUser : autList) {
							autMap.put(autUser.getId(), autUser.getFullName());
						}
						autList.clear();
					}
					Where<AutUserPub> pubWhere = new Where<AutUserPub>();
					pubWhere.in("user_id", resid);
					pubWhere.setSqlSelect("user_id,phone_number");
					List<AutUserPub> pubList = autUserPubService.selectList(pubWhere);
					Map<Long, String> pubMap = new HashMap<Long, String>();
					if (pubList != null && pubList.size() > 0) {
						for (AutUserPub autUserPub : pubList) {
							pubMap.put(autUserPub.getUserId(), autUserPub.getPhoneNumber());
						}
						pubList.clear();
					}
					// 获取用户角色
					Where<AutUserRole> roleWhere = new Where<AutUserRole>();
					roleWhere.in("user_id",resid);
					roleWhere.setSqlSelect("user_id,role_name");
					roleWhere.eq("is_active", 1);
					List<AutUserRole> roleList = autUserRoleService.selectList(roleWhere);
					Map<Long, String> roleMap = new HashMap<Long, String>();
					if (roleList != null && roleList.size() > 0) {
						for (AutUserRole autUserRole : roleList) {
							roleMap.put(autUserRole.getUserId(), autUserRole.getRoleName());
						}
						roleList.clear();
					}
					// 获取用户岗位
					Where<AutUserPosition> posWhere = new Where<AutUserPosition>();
					posWhere.in("user_id", resid);
					posWhere.setSqlSelect("user_id,position_id");
					List<AutUserPosition> posList = autUserPositionService.selectList(posWhere);
					Map<Long, String> posMap = new HashMap<Long, String>();
					if (posList != null && posList.size() > 0) {
						String posNameIds = "";
						for (AutUserPosition autUserPos : posList) {
							posNameIds += autUserPos.getPositionId() + ",";
						}
						Map<Long, String> postionMap = new HashMap<Long, String>();
						if (posNameIds.length() > 0) {
							Where<AutPosition> positionWhere = new Where<AutPosition>();
							posNameIds.substring(0, posNameIds.length()-1);
							positionWhere.in("id", posNameIds);
							positionWhere.eq("is_active", 1);
							positionWhere.setSqlSelect("id,position_name");
							List<AutPosition> postionList = autPositionService.selectList(positionWhere);
							if (postionList != null && postionList.size() > 0) {
								for (AutPosition autPosition : postionList) {
									postionMap.put(autPosition.getId(), autPosition.getPositionName());
								}
							}
						}
						for (AutUserPosition autUserPos : posList) {
							String posids = "";
							if (posMap.containsKey(autUserPos.getUserId())) {
								posids = posMap.get(autUserPos.getUserId());
							}
							if (postionMap.containsKey(autUserPos.getPositionId())) {
								posids += postionMap.get(autUserPos.getPositionId()) + ";";
							}
							posMap.put(autUserPos.getUserId(), posids);
						}
					}					
					List<SmsShortMessageSummary> smsList=new ArrayList<SmsShortMessageSummary>();
					
					for (String string : resid) {						
						if (string != null && !"".equals(string)) {
							String content = obj.getContent();
							Long sendId = Long.valueOf(string);
							SmsShortMessageSummary sumVo = new SmsShortMessageSummary();
							sumVo.setIsDelete(0);
							sumVo.setIsActive(1);
							sumVo.setReceiverId(string);
							sumVo.setReceiverName(autMap.get(sendId));
							sumVo.setTheme(obj.getTheme());
							sumVo.setSendTime(new Date());
							if (content.indexOf("$1") > -1) {
								if(autMap.containsKey(sendId)){
									content=content.replaceAll("\\$1", autMap.get(sendId));
								}else{
									content=content.replaceAll("\\$1", "");
								}
							}
							if (content.indexOf("$2") > -1) {
								if(roleMap.containsKey(sendId)){
									content=content.replaceAll("\\$2", roleMap.get(sendId));
								}else{
									content=content.replaceAll("\\$2", "");
								}
							}
							if (content.indexOf("$3") > -1) {
								if(roleMap.containsKey(sendId)){
									content=content.replaceAll("\\$3", posMap.get(sendId));
								}else{
									content=content.replaceAll("\\$3", "");
								}
							}
							sumVo.setContent(content);
							Boolean isSend = false;
							if (pubMap.containsKey(sendId)) {
								String send = pubMap.get(sendId);
								if (send != null && !"".equals(send)) {									
									sumVo.setSendNumber(send);
									isSend = true;
								}
							}
							isSend = false;
							if (isSend) {
								RetMsg ret = smsShortMessageSummaryService.addOne(sumVo,
										autUserService.selectById(Long.valueOf(userId)));
								if (ret.getCode() != 0) {
									sumVo.setSendStatus(0);
								} else {
									sumVo.setSendStatus(1);
								}
							} else {
								sumVo.setSendStatus(0);
							}
							smsList.add(sumVo);
						}
					}
                     if(smsList.size()>0){
                    	 smsShortMessageSummaryService.insertBatch(smsList);
                     }
				}

			}
			retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
			retMsg.setMessage("操作成功");
		} else {
			retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
			retMsg.setMessage("无发送数据内容");
		}
		return retMsg;
	}
}
