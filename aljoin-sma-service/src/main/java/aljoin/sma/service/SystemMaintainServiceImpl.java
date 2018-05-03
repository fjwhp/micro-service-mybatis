package aljoin.sma.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sma.dao.entity.LeaStatisticsModule;
import aljoin.sma.iservice.LeaStatisticsModuleService;
import aljoin.sma.iservice.SystemMaintainService;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.dao.object.SysParameterVO;
import aljoin.sys.iservice.SysParameterService;

/**
 * 
 * 资源管理表(服务实现类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-09-05
 */
@Service
public class SystemMaintainServiceImpl implements SystemMaintainService {
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private AutUserPubService autUserPubService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private LeaStatisticsModuleService leaStatisticsModuleService;

    @Override
    public RetMsg sysSet(SysParameterVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        Map<String, String> params = obj.getParams();
        if (null != params) {
            List<SysParameter> parameterList = new ArrayList<SysParameter>();

            /*            if(StringUtils.isEmpty(params.get("is_mail_sms"))){
                retMsg.setCode(1);
                retMsg.setMessage("请设置[邮件短信]");
                return retMsg;
            }
            */ if (StringUtils.isEmpty(params.get("allow_revoke_time"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[撤回并删除有效时间]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("personal_mail_space"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[个人邮件空间限制]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("leader_mail_space"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[领导邮件空间限制]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("sys_attachment_size"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[系统附件大小]");
                return retMsg;
            }

            // String isMailSms = "";
            String allowRevokeTime = "";
            String personalMailSpace = "";
            String leaderMailSpace = "";
            String sysAttachmentSize = "";
            /*if(StringUtils.isNotEmpty(params.get("is_mail_sms"))){
                isMailSms = String.valueOf(params.get("is_mail_sms"));
            }*/
            if (StringUtils.isNotEmpty(params.get("allow_revoke_time"))) {
                allowRevokeTime = String.valueOf(params.get("allow_revoke_time"));
            }
            if (StringUtils.isNotEmpty(params.get("personal_mail_space"))) {
                personalMailSpace = String.valueOf(params.get("personal_mail_space"));
            }
            if (StringUtils.isNotEmpty(params.get("leader_mail_space"))) {
                leaderMailSpace = String.valueOf(params.get("leader_mail_space"));
            }
            if (StringUtils.isNotEmpty(params.get("sys_attachment_size"))) {
                sysAttachmentSize = String.valueOf(params.get("sys_attachment_size"));
            }

            /*if(StringUtils.isNotEmpty(isMailSms)){
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key","is_mail_sms");
                parameterWhere.eq("is_active",1);
                parameterWhere.eq("is_show",1);
                //parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if(null != parameter){
                    if(null != parameter.getId()){
                        //parameter.setParamValue(isMailSms);
                        parameter.setParamDesc("邮件短信(0:关闭 1:打开)");
                        parameterList.add(parameter);
                    }
                }else{
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("is_mail_sms");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    //sysParameter.setParamValue(isMailSms);
                    sysParameter.setParamDesc("邮件短信(0:关闭 1:打开)");
                    parameterList.add(sysParameter);
                }
            }*/
            if (StringUtils.isNotEmpty(allowRevokeTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "allow_revoke_time");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    parameter.setParamValue(allowRevokeTime);
                    parameter.setParamDesc("撤回并删除有效时间(分钟)");
                    parameterList.add(parameter);
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("allow_revoke_time");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(allowRevokeTime);
                    sysParameter.setParamDesc("撤回并删除有效时间(分钟)");
                    parameterList.add(sysParameter);
                }
            }
            if (StringUtils.isNotEmpty(personalMailSpace)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "personal_mail_space");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    parameter.setParamValue(personalMailSpace);
                    parameter.setParamDesc("个人邮件空间限制(GB)");
                    parameterList.add(parameter);
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("personal_mail_space");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(personalMailSpace);
                    sysParameter.setParamDesc("个人邮件空间限制(GB)");
                    parameterList.add(sysParameter);
                }
            }
            if (StringUtils.isNotEmpty(leaderMailSpace)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "leader_mail_space");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    parameter.setParamValue(leaderMailSpace);
                    parameter.setParamDesc("领导邮件空间限制(GB)");
                    parameterList.add(parameter);
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("leader_mail_space");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(leaderMailSpace);
                    sysParameter.setParamDesc("领导邮件空间限制(GB)");
                    parameterList.add(sysParameter);
                }
            }
            if (StringUtils.isNotEmpty(sysAttachmentSize)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "sys_attachment_size");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    parameter.setParamValue(sysAttachmentSize);
                    parameter.setParamDesc("系统附件大小限制(MB)");
                    parameterList.add(parameter);
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("sys_attachment_size");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(sysAttachmentSize);
                    sysParameter.setParamDesc("系统附件大小限制(MB)");
                    parameterList.add(sysParameter);
                }
            }

            // 保存或修改
            if (null != parameterList && !parameterList.isEmpty()) {
                sysParameterService.insertOrUpdateBatch(parameterList);
            }
            Where<AutUserPub> pubWhere = new Where<AutUserPub>();
            pubWhere
                .setSqlSelect("id,create_time,is_delete,create_user_id,create_user_name,version,user_id,max_mail_size");
            List<AutUserPub> pubList = autUserPubService.selectList(pubWhere);
            List<Long> userIdList = new ArrayList<Long>();
            if (null != pubList && !pubList.isEmpty()) {
                for (AutUserPub pub : pubList) {
                    if (null != pub && null != pub.getUserId()) {
                        userIdList.add(pub.getUserId());
                    }
                }
                if (null != userIdList && !userIdList.isEmpty()) {
                    Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
                    departmentUserWhere.in("user_id", userIdList);
                    departmentUserWhere.setSqlSelect("id,user_id,is_leader");
                    List<AutDepartmentUser> departmentUserList
                        = autDepartmentUserService.selectList(departmentUserWhere);
                    if (null != departmentUserList && !departmentUserList.isEmpty() && null != pubList
                        && !pubList.isEmpty()) {
                        for (AutDepartmentUser departmentUser : departmentUserList) {
                            for (AutUserPub pub : pubList) {
                                if ((null != departmentUser && null != pub)
                                    && (departmentUser.getUserId().equals(pub.getUserId())
                                        && departmentUser.getUserId().intValue() == pub.getUserId().intValue())) {
                                    if (departmentUser.getIsLeader() == 1) {
                                        pub.setMaxMailSize(Integer.valueOf(leaderMailSpace));
                                    } else {
                                        pub.setMaxMailSize(Integer.valueOf(personalMailSpace));
                                    }
                                }
                            }
                        }
                        autUserPubService.updateBatchById(pubList);
                    }
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public List<SysParameter> sysSetDetail(SysParameterVO obj) throws Exception {
        // List<SysParameter> parameterList = null;
        List<String> keyList = new ArrayList<String>();
        keyList.add("is_mail_sms");
        keyList.add("allow_revoke_time");
        keyList.add("personal_mail_space");
        keyList.add("leader_mail_space");
        keyList.add("sys_attachment_size");

        Where<SysParameter> parameterWhere = new Where<SysParameter>();
        parameterWhere.in("param_key", keyList);
        parameterWhere.eq("is_active", 1);
        parameterWhere.eq("is_show", 1);
        parameterWhere.setSqlSelect("id,param_key,param_value,version,param_desc,is_active,is_show");
        return sysParameterService.selectList(parameterWhere);
    }

    @Override
    public RetMsg leaderMetSet(SysParameter obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            String leaderMetMember = obj.getParamKey();
            List<SysParameter> parameterList = new ArrayList<SysParameter>();
            if (StringUtils.isEmpty(leaderMetMember)) {
                leaderMetMember = "leader_meeting_member";
            }
            if (StringUtils.isNotEmpty(leaderMetMember)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", leaderMetMember);
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    parameter.setParamValue(obj.getParamValue());
                    parameter.setParamDesc("领导会议人员设置");
                    parameterList.add(parameter);
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey(leaderMetMember);
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(obj.getParamValue());
                    sysParameter.setParamDesc("领导会议人员设置");
                    parameterList.add(sysParameter);
                }
            }
            if (null != parameterList && !parameterList.isEmpty()) {
                sysParameterService.insertOrUpdateBatch(parameterList);
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public SysParameterVO leaderMetSetDetail(SysParameter obj) throws Exception {
        SysParameterVO parameterVO = null;
        Where<SysParameter> parameterWhere = new Where<SysParameter>();
        parameterWhere.eq("param_key", "leader_meeting_member");
        parameterWhere.eq("is_active", 1);
        parameterWhere.eq("is_show", 1);
        SysParameter parameter = sysParameterService.selectOne(parameterWhere);
        if (null != parameter && null != parameter.getParamValue()) {
            parameterVO = new SysParameterVO();
            BeanUtils.copyProperties(parameter, parameterVO);
            List<String> userIdList = Arrays.asList(parameter.getParamValue().split(";"));
            if (null != userIdList && !userIdList.isEmpty()) {
                Where<AutUser> where = new Where<AutUser>();
                where.in("id", userIdList);
                where.setSqlSelect("id,user_name,full_name");
                List<AutUser> userList = autUserService.selectList(where);
                String leaderMet = "";
                if (null != userList && !userList.isEmpty()) {
                    for (AutUser user : userList) {
                        if (null != user && StringUtils.isNotEmpty(user.getFullName())) {
                            leaderMet += user.getFullName() + ";";
                        }
                    }
                }
                parameterVO.setFullNames(leaderMet);
            }
        }
        return parameterVO;
    }

    @Override
    public RetMsg attCardSet(SysParameterVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        Map<String, String> params = obj.getParams();
        if (null != params) {
            List<SysParameter> parameterList = new ArrayList<SysParameter>();
            if (StringUtils.isEmpty(params.get("am_work_punch_time"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[上午上班打卡时间]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("am_work_punch_begtime"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[上午上班开始打卡时间]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("am_work_punch_endtime"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[上午上班结束打卡时间]");
                return retMsg;
            }

            if (StringUtils.isEmpty(params.get("am_offwork_punch_time"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[上午下班打卡时间]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("am_offwork_punch_begtime"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[上午下班开始打卡时间]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("am_offwork_punch_endtime"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[上午下班结束打卡时间]");
                return retMsg;
            }

            if (StringUtils.isEmpty(params.get("pm_work_punch_time"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[下午上班打卡时间]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("pm_work_punch_begtime"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[下午上班开始打卡时间]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("pm_work_punch_endtime"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[下午上班结束打卡时间]");
                return retMsg;
            }

            if (StringUtils.isEmpty(params.get("pm_offwork_punch_time"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[下午下班打卡时间]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("pm_offwork_punch_begtime"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[下午下班开始打卡时间]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("pm_offwork_punch_endtime"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[下午下班结束打卡时间]");
                return retMsg;
            }

            /*  if(StringUtils.isEmpty(params.get("am_allow_late"))){
                retMsg.setCode(1);
                retMsg.setMessage("请设置[上午允许迟到时间]");
                return retMsg;
            }*/
            if (StringUtils.isEmpty(params.get("am_allow_late"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[上午允许迟到时间]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("am_leave_early"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[上午允许早退时间]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("pm_allow_late"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[下午允许迟到时间]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("pm_leave_early"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[下午允许早退时间]");
                return retMsg;
            }
            if (StringUtils.isEmpty(params.get("am_signin_popup_time"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[上午签到弹窗时间]");
                return retMsg;
            }
            /*if(StringUtils.isEmpty(params.get("am_signin_popup_endtime"))){
                retMsg.setCode(1);
                retMsg.setMessage("请设置[上午签到结束弹窗时间]");
                return retMsg;
            }*/
            if (StringUtils.isEmpty(params.get("am_signout_popup_time"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[上午签退弹窗时间]");
                return retMsg;
            }
            /*if(StringUtils.isEmpty(params.get("am_signout_popup_endtime"))){
                retMsg.setCode(1);
                retMsg.setMessage("请设置[上午签退结束弹窗时间]");
                return retMsg;
            }*/
            if (StringUtils.isEmpty(params.get("pm_signin_popup_time"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[下午签到弹窗时间]");
                return retMsg;
            }
            /*if(StringUtils.isEmpty(params.get("pm_signin_popup_endtime"))){
                retMsg.setCode(1);
                retMsg.setMessage("请设置[下午签到结束弹窗时间]");
                return retMsg;
            }*/
            if (StringUtils.isEmpty(params.get("pm_signout_popup_time"))) {
                retMsg.setCode(1);
                retMsg.setMessage("请设置[下午签退弹窗时间]");
                return retMsg;
            }
            /* if(StringUtils.isEmpty(params.get("pm_signout_popup_endtime"))){
                retMsg.setCode(1);
                retMsg.setMessage("请设置[下午签退结束弹窗时间]");
                return retMsg;
            }*/
            String amAllowLate = "";
            String amLeaveEarly = "";
            String pmAllowLate = "";
            String pmLeaveEarly = "";

            String amSigninPopupTime = "";
            // String amSigninPopupEndTime = "";
            String amSignoutPopupTime = "";
            // String amSignoutPopupEndTime = "";

            String pmSigninPopupTime = "";
            // String pmSigninPopupEndTime = "";
            String pmSignoutPopupTime = "";
            // String pmSignoutPopupEndTime = "";

            String amWorkPunchTime = "";
            String amWorkPunchBegTime = "";
            String amWorkPunchEndTime = "";
            String amOffWorkPunchTime = "";
            String amOffWorkPunchBegTime = "";
            String amOffWorkPunchEndTime = "";

            String pmWorkPunchTime = "";
            String pmWorkPunchBegTime = "";
            String pmWorkPunchEndTime = "";
            String pmOffWorkPunchTime = "";
            String pmOffWorkPunchBegTime = "";
            String pmOffWorkPunchEndTime = "";

            if (StringUtils.isNotEmpty(params.get("am_work_punch_time"))) {
                amWorkPunchTime = String.valueOf(params.get("am_work_punch_time"));
            }
            if (StringUtils.isNotEmpty(params.get("am_work_punch_begtime"))) {
                amWorkPunchBegTime = String.valueOf(params.get("am_work_punch_begtime"));
            }
            if (StringUtils.isNotEmpty(params.get("am_work_punch_endtime"))) {
                amWorkPunchEndTime = String.valueOf(params.get("am_work_punch_endtime"));
            }

            if (StringUtils.isNotEmpty(params.get("am_offwork_punch_time"))) {
                amOffWorkPunchTime = String.valueOf(params.get("am_offwork_punch_time"));
            }
            if (StringUtils.isNotEmpty(params.get("am_offwork_punch_begtime"))) {
                amOffWorkPunchBegTime = String.valueOf(params.get("am_offwork_punch_begtime"));
            }
            if (StringUtils.isNotEmpty(params.get("am_offwork_punch_endtime"))) {
                amOffWorkPunchEndTime = String.valueOf(params.get("am_offwork_punch_endtime"));
            }

            if (StringUtils.isNotEmpty(params.get("pm_work_punch_time"))) {
                pmWorkPunchTime = String.valueOf(params.get("pm_work_punch_time"));
            }
            if (StringUtils.isNotEmpty(params.get("pm_work_punch_begtime"))) {
                pmWorkPunchBegTime = String.valueOf(params.get("pm_work_punch_begtime"));
            }
            if (StringUtils.isNotEmpty(params.get("pm_work_punch_endtime"))) {
                pmWorkPunchEndTime = String.valueOf(params.get("pm_work_punch_endtime"));
            }

            if (StringUtils.isNotEmpty(params.get("pm_offwork_punch_time"))) {
                pmOffWorkPunchTime = String.valueOf(params.get("pm_offwork_punch_time"));
            }
            if (StringUtils.isNotEmpty(params.get("pm_offwork_punch_begtime"))) {
                pmOffWorkPunchBegTime = String.valueOf(params.get("pm_offwork_punch_begtime"));
            }
            if (StringUtils.isNotEmpty(params.get("pm_offwork_punch_endtime"))) {
                pmOffWorkPunchEndTime = String.valueOf(params.get("pm_offwork_punch_endtime"));
            }

            if (StringUtils.isNotEmpty(params.get("am_signin_popup_time"))) {
                amSigninPopupTime = String.valueOf(params.get("am_signin_popup_time"));
            }
            /* if(StringUtils.isNotEmpty(params.get("am_signin_popup_endtime"))){
                amSigninPopupEndTime = String.valueOf(params.get("am_signin_popup_endtime"));
            }*/
            if (StringUtils.isNotEmpty(params.get("am_signout_popup_time"))) {
                amSignoutPopupTime = String.valueOf(params.get("am_signout_popup_time"));
            }
            /*if(StringUtils.isNotEmpty(params.get("am_signout_popup_endtime"))){
                amSignoutPopupEndTime = String.valueOf(params.get("am_signout_popup_endtime"));
            }*/

            if (StringUtils.isNotEmpty(params.get("pm_signin_popup_time"))) {
                pmSigninPopupTime = String.valueOf(params.get("pm_signin_popup_time"));
            }
            /* if(StringUtils.isNotEmpty(params.get("pm_signin_popup_endtime"))){
                pmSigninPopupEndTime = String.valueOf(params.get("pm_signin_popup_endtime"));
            }*/
            if (StringUtils.isNotEmpty(params.get("pm_signout_popup_time"))) {
                pmSignoutPopupTime = String.valueOf(params.get("pm_signout_popup_time"));
            }
            /*if(StringUtils.isNotEmpty(params.get("pm_signout_popup_endtime"))){
                pmSignoutPopupEndTime = String.valueOf(params.get("pm_signout_popup_endtime"));
            }*/

            if (StringUtils.isNotEmpty(params.get("am_allow_late"))) {
                amAllowLate = String.valueOf(params.get("am_allow_late"));
            }
            if (StringUtils.isNotEmpty(params.get("am_leave_early"))) {
                amLeaveEarly = String.valueOf(params.get("am_leave_early"));
            }
            if (StringUtils.isNotEmpty(params.get("pm_allow_late"))) {
                pmAllowLate = String.valueOf(params.get("pm_allow_late"));
            }
            if (StringUtils.isNotEmpty(params.get("pm_leave_early"))) {
                pmLeaveEarly = String.valueOf(params.get("pm_leave_early"));
            }

            // 上午上班打卡时间
            if (StringUtils.isNotEmpty(amWorkPunchTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "am_work_punch_time");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(amWorkPunchTime);
                        parameter.setParamDesc("上午上班打卡时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("am_work_punch_time");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(amWorkPunchTime);
                    sysParameter.setParamDesc("上午上班打卡时间");
                    parameterList.add(sysParameter);
                }
            }

            // 上午上班开始打卡时间
            if (StringUtils.isNotEmpty(amWorkPunchBegTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "am_work_punch_begtime");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(amWorkPunchBegTime);
                        parameter.setParamDesc("上午上班开始打卡时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("am_work_punch_begtime");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(amWorkPunchBegTime);
                    sysParameter.setParamDesc("上午上班开始打卡时间");
                    parameterList.add(sysParameter);
                }
            }

            // 上午上班结束打卡时间
            if (StringUtils.isNotEmpty(amWorkPunchEndTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "am_work_punch_endtime");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(amWorkPunchEndTime);
                        parameter.setParamDesc("上午上班结束打卡时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("am_work_punch_endtime");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(amWorkPunchEndTime);
                    sysParameter.setParamDesc("上午上班结束打卡时间");
                    parameterList.add(sysParameter);
                }
            }

            // 上午下班打卡时间
            if (StringUtils.isNotEmpty(amOffWorkPunchTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "am_offwork_punch_time");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(amOffWorkPunchTime);
                        parameter.setParamDesc("上午下班打卡时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("am_offwork_punch_time");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(amOffWorkPunchTime);
                    sysParameter.setParamDesc("上午下班打卡时间");
                    parameterList.add(sysParameter);
                }
            }

            // 上午下班开始打卡时间
            if (StringUtils.isNotEmpty(amOffWorkPunchBegTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "am_offwork_punch_begtime");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(amOffWorkPunchBegTime);
                        parameter.setParamDesc("上午下班开始打卡时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("am_offwork_punch_begtime");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(amOffWorkPunchBegTime);
                    sysParameter.setParamDesc("上午下班开始打卡时间");
                    parameterList.add(sysParameter);
                }
            }

            // 上午下班结束打卡时间
            if (StringUtils.isNotEmpty(amOffWorkPunchEndTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "am_offwork_punch_endtime");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(amOffWorkPunchEndTime);
                        parameter.setParamDesc("上午下班结束打卡时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("am_offwork_punch_endtime");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(amOffWorkPunchEndTime);
                    sysParameter.setParamDesc("上午下班结束打卡时间");
                    parameterList.add(sysParameter);
                }
            }

            // 下午上班打卡时间
            if (StringUtils.isNotEmpty(pmWorkPunchTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "pm_work_punch_time");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(pmWorkPunchTime);
                        parameter.setParamDesc("下午上班打卡时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("pm_work_punch_time");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(pmWorkPunchTime);
                    sysParameter.setParamDesc("下午上班打卡时间");
                    parameterList.add(sysParameter);
                }
            }

            // 下午上班开始打卡时间
            if (StringUtils.isNotEmpty(pmWorkPunchBegTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "pm_work_punch_begtime");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(pmWorkPunchBegTime);
                        parameter.setParamDesc("下午上班开始打卡时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("pm_work_punch_begtime");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(pmWorkPunchBegTime);
                    sysParameter.setParamDesc("下午上班开始打卡时间");
                    parameterList.add(sysParameter);
                }
            }

            // 下午上班结束打卡时间
            if (StringUtils.isNotEmpty(pmWorkPunchEndTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "pm_work_punch_endtime");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(pmWorkPunchEndTime);
                        parameter.setParamDesc("下午上班结束打卡时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("pm_work_punch_endtime");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(pmWorkPunchEndTime);
                    sysParameter.setParamDesc("下午上班结束打卡时间");
                    parameterList.add(sysParameter);
                }
            }

            // 下午下班打卡时间
            if (StringUtils.isNotEmpty(pmOffWorkPunchTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "pm_offwork_punch_time");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(pmOffWorkPunchTime);
                        parameter.setParamDesc("下午下班打卡时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("pm_offwork_punch_time");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(pmOffWorkPunchTime);
                    sysParameter.setParamDesc("下午下班打卡时间");
                    parameterList.add(sysParameter);
                }
            }

            // 下午下班开始打卡时间
            if (StringUtils.isNotEmpty(pmOffWorkPunchBegTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "pm_offwork_punch_begtime");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(pmOffWorkPunchBegTime);
                        parameter.setParamDesc("下午下班开始打卡时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("pm_offwork_punch_begtime");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(pmOffWorkPunchBegTime);
                    sysParameter.setParamDesc("下午下班开始打卡时间");
                    parameterList.add(sysParameter);
                }
            }

            // 下午下班结束打卡时间
            if (StringUtils.isNotEmpty(pmOffWorkPunchEndTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "pm_offwork_punch_endtime");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(pmOffWorkPunchEndTime);
                        parameter.setParamDesc("下午下班结束打卡时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("pm_offwork_punch_endtime");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(pmOffWorkPunchEndTime);
                    sysParameter.setParamDesc("下午下班结束打卡时间");
                    parameterList.add(sysParameter);
                }
            }

            // 上午签到开始弹窗时间
            if (StringUtils.isNotEmpty(amSigninPopupTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "am_signin_popup_time");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(amSigninPopupTime);
                        parameter.setParamDesc("上午签到弹窗时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("am_signin_popup_time");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(amSigninPopupTime);
                    sysParameter.setParamDesc("上午签到弹窗时间");
                    parameterList.add(sysParameter);
                }
            }

            // 上午签到结束弹窗时间
            /* if(StringUtils.isNotEmpty(amSigninPopupEndTime)){
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key","am_signin_popup_endtime");
                parameterWhere.eq("is_active",1);
                parameterWhere.eq("is_show",1);
                //parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if(null != parameter){
                    if(null != parameter.getId()){
                        parameter.setParamValue(amSigninPopupEndTime);
                        parameter.setParamDesc("上午签到结束弹窗时间");
                        parameterList.add(parameter);
                    }
                }else{
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("am_signin_popup_endtime");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(amSigninPopupEndTime);
                    sysParameter.setParamDesc("上午签到结束弹窗时间");
                    parameterList.add(sysParameter);
                }
            }*/

            // 上午签退开始弹窗时间
            if (StringUtils.isNotEmpty(amSignoutPopupTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "am_signout_popup_time");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(amSignoutPopupTime);
                        parameter.setParamDesc("上午签退弹窗时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("am_signout_popup_time");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(amSignoutPopupTime);
                    sysParameter.setParamDesc("上午签退弹窗时间");
                    parameterList.add(sysParameter);
                }
            }

            // 上午签退结束弹窗时间
            /* if(StringUtils.isNotEmpty(amSignoutPopupEndTime)){
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key","am_signout_popup_endtime");
                parameterWhere.eq("is_active",1);
                parameterWhere.eq("is_show",1);
                //parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if(null != parameter){
                    if(null != parameter.getId()){
                        parameter.setParamValue(amSignoutPopupEndTime);
                        parameter.setParamDesc("上午签退结束弹窗时间");
                        parameterList.add(parameter);
                    }
                }else{
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("am_signout_popup_endtime");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(amSignoutPopupEndTime);
                    sysParameter.setParamDesc("上午签退结束弹窗时间");
                    parameterList.add(sysParameter);
                }
            }*/

            // 下午签到开始弹窗时间
            if (StringUtils.isNotEmpty(pmSigninPopupTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "pm_signin_popup_time");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(pmSigninPopupTime);
                        parameter.setParamDesc("下午签到开始弹窗时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("pm_signin_popup_time");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(pmSigninPopupTime);
                    sysParameter.setParamDesc("下午签到开始弹窗时间");
                    parameterList.add(sysParameter);
                }
            }

            // 下午签到结束弹窗时间
            /* if(StringUtils.isNotEmpty(pmSigninPopupEndTime)){
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key","pm_signin_popup_endtime");
                parameterWhere.eq("is_active",1);
                parameterWhere.eq("is_show",1);
                //parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if(null != parameter){
                    if(null != parameter.getId()){
                        parameter.setParamValue(pmSigninPopupEndTime);
                        parameter.setParamDesc("下午签到结束弹窗时间");
                        parameterList.add(parameter);
                    }
                }else{
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("pm_signin_popup_endtime");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(pmSigninPopupEndTime);
                    sysParameter.setParamDesc("下午签到结束弹窗时间");
                    parameterList.add(sysParameter);
                }
            }
            */
            // 下午签退开始弹窗时间
            if (StringUtils.isNotEmpty(pmSignoutPopupTime)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "pm_signout_popup_time");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(pmSignoutPopupTime);
                        parameter.setParamDesc("下午签退开始弹窗时间");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("pm_signout_popup_time");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(pmSignoutPopupTime);
                    sysParameter.setParamDesc("下午签退开始弹窗时间");
                    parameterList.add(sysParameter);
                }
            }

            // 下午签退结束弹窗时间
            /* if(StringUtils.isNotEmpty(pmSignoutPopupEndTime)){
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key","pm_signout_popup_endtime");
                parameterWhere.eq("is_active",1);
                parameterWhere.eq("is_show",1);
                //parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if(null != parameter){
                    if(null != parameter.getId()){
                        parameter.setParamValue(pmSignoutPopupEndTime);
                        parameter.setParamDesc("下午签退结束弹窗时间");
                        parameterList.add(parameter);
                    }
                }else{
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("pm_signout_popup_endtime");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(pmSignoutPopupEndTime);
                    sysParameter.setParamDesc("下午签退结束弹窗时间");
                    parameterList.add(sysParameter);
                }
            }*/

            // 上午迟到时间(分钟)
            if (StringUtils.isNotEmpty(amAllowLate)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "am_allow_late");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    if (null != parameter.getId()) {
                        parameter.setParamValue(amAllowLate);
                        parameter.setParamDesc("上午允许迟到时间(分钟)");
                        parameterList.add(parameter);
                    }
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("am_allow_late");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(amAllowLate);
                    sysParameter.setParamDesc("上午允许迟到时间(分钟)");
                    parameterList.add(sysParameter);
                }
            }

            // 上午早退时间(分钟)
            if (StringUtils.isNotEmpty(amLeaveEarly)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "am_leave_early");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    parameter.setParamValue(amLeaveEarly);
                    parameter.setParamDesc("上午允许早退时间(分钟)");
                    parameterList.add(parameter);
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("am_leave_early");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(amLeaveEarly);
                    sysParameter.setParamDesc("上午允许早退时间(分钟)");
                    parameterList.add(sysParameter);
                }
            }

            // 下午迟到时间(分钟)
            if (StringUtils.isNotEmpty(pmAllowLate)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "pm_allow_late");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    parameter.setParamValue(pmAllowLate);
                    parameter.setParamDesc("下午允许迟到时间(分钟)");
                    parameterList.add(parameter);
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("pm_allow_late");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(pmAllowLate);
                    sysParameter.setParamDesc("下午允许迟到时间(分钟)");
                    parameterList.add(sysParameter);
                }
            }

            // 下午早退时间(分钟)
            if (StringUtils.isNotEmpty(pmLeaveEarly)) {
                Where<SysParameter> parameterWhere = new Where<SysParameter>();
                parameterWhere.eq("param_key", "pm_leave_early");
                parameterWhere.eq("is_active", 1);
                parameterWhere.eq("is_show", 1);
                // parameterWhere.setSqlSelect("id,param_key,version,param_value,param_desc,is_active,is_show");
                SysParameter parameter = sysParameterService.selectOne(parameterWhere);
                if (null != parameter) {
                    parameter.setParamValue(pmLeaveEarly);
                    parameter.setParamDesc("下午允许早退时间(分钟)");
                    parameterList.add(parameter);
                } else {
                    SysParameter sysParameter = new SysParameter();
                    sysParameter.setParamKey("pm_leave_early");
                    sysParameter.setIsActive(1);
                    sysParameter.setIsShow(1);
                    sysParameter.setParamValue(pmLeaveEarly);
                    sysParameter.setParamDesc("下午允许早退时间(分钟)");
                    parameterList.add(sysParameter);
                }
            }

            // 保存或修改
            if (null != parameterList && !parameterList.isEmpty()) {
                sysParameterService.insertOrUpdateBatch(parameterList);
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public List<SysParameter> attCardSetDetail(SysParameterVO obj) throws Exception {
        List<String> keyList = new ArrayList<String>();
        keyList.add("am_allow_late");
        keyList.add("am_leave_early");
        keyList.add("pm_allow_late");
        keyList.add("pm_leave_early");
        keyList.add("am_signin_popup_time");
        // keyList.add("am_signin_popup_endtime");
        keyList.add("am_signout_popup_time");
        // keyList.add("am_signout_popup_endtime");

        keyList.add("pm_signin_popup_time");
        // keyList.add("pm_signin_popup_endtime");
        keyList.add("pm_signout_popup_time");
        // keyList.add("pm_signout_popup_endtime");

        keyList.add("am_work_punch_time");
        keyList.add("am_work_punch_begtime");
        keyList.add("am_work_punch_endtime");
        keyList.add("am_offwork_punch_time");
        keyList.add("am_offwork_punch_begtime");
        keyList.add("am_offwork_punch_endtime");

        keyList.add("pm_work_punch_time");
        keyList.add("pm_work_punch_begtime");
        keyList.add("pm_work_punch_endtime");
        keyList.add("pm_offwork_punch_time");
        keyList.add("pm_offwork_punch_begtime");
        keyList.add("pm_offwork_punch_endtime");

        Where<SysParameter> parameterWhere = new Where<SysParameter>();
        parameterWhere.in("param_key", keyList);
        parameterWhere.eq("is_active", 1);
        parameterWhere.eq("is_show", 1);
        parameterWhere.setSqlSelect("id,param_key,param_value,version,param_desc,is_active,is_show");
        return sysParameterService.selectList(parameterWhere);
    }

    @Override
    public SysParameterVO attCountPersonDetail(SysParameterVO obj) throws Exception {
        SysParameterVO sysParameterVO = new SysParameterVO();
        String userName = "";
        SysParameter sysParameter = new SysParameter();
        Where<SysParameter> parameterWhere = new Where<SysParameter>();
        parameterWhere.eq("param_key", "not_attendance_person");
        parameterWhere.eq("is_active", 1);
        parameterWhere.eq("is_show", 1);
        parameterWhere.setSqlSelect("id,param_key,param_value,version,param_desc,is_active,is_show");
        sysParameter = sysParameterService.selectOne(parameterWhere);
        if (null != sysParameter) {
            String ids = sysParameter.getParamValue();
            if (null != ids && StringUtils.isNotEmpty(ids)) {
                ids = ids.replaceAll(";", ",");
                Where<AutUser> userWhere = new Where<AutUser>();
                userWhere.setSqlSelect("id,user_name,full_name");
                userWhere.in("id", ids);
                List<AutUser> userList = autUserService.selectList(userWhere);
                if (null != userList && !userList.isEmpty()) {
                    for (AutUser u : userList) {
                        userName += u.getFullName() + "; ";
                    }
                }
            }
            BeanUtils.copyProperties(sysParameter, sysParameterVO);
            sysParameterVO.setFullNames(userName);
        }
        return sysParameterVO;
    }

    @Override
    public RetMsg addOrUpdateAttcountPerson(SysParameterVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            SysParameter sysParameter = new SysParameter();
            Where<SysParameter> parameterWhere = new Where<SysParameter>();
            parameterWhere.eq("param_key", "not_attendance_person");
            parameterWhere.eq("is_active", 1);
            parameterWhere.eq("is_show", 1);
            sysParameter = sysParameterService.selectOne(parameterWhere);
            if (null == sysParameter) {// 无此参数时新增
                sysParameter = new SysParameter();
                BeanUtils.copyProperties(obj, sysParameter);
                sysParameter.setParamDesc("不参与考勤统计人员");
                sysParameter.setParamKey("not_attendance_person");
                sysParameter.setIsShow(1);
                sysParameter.setIsActive(1);
                sysParameter.setParamValue(obj.getParamValue());
                sysParameterService.insert(sysParameter);
            } else {// 有此参数时更新
                sysParameter.setParamValue(obj.getParamValue());
                sysParameterService.updateById(sysParameter);
            }
            retMsg.setCode(1);
            retMsg.setMessage("操作成功");
        }
        return retMsg;
    }

    @Override
    public Page<LeaStatisticsModule> selectUser(String userid, PageBean pb) throws Exception {
        Page<LeaStatisticsModule> ipm = new Page<LeaStatisticsModule>();
        Where<LeaStatisticsModule> where = new Where<LeaStatisticsModule>();
        where.where("create_user_id={0}", userid);
        where.eq("is_delete", 0);
        where.eq("is_active", 1);
        where.orderBy("module_rank", true);
        ipm = leaStatisticsModuleService.selectPage(new Page<LeaStatisticsModule>(pb.getPageNum(), pb.getPageSize()),
            where);
        return ipm;
    }

    @Override
    public RetMsg updateModule(LeaStatisticsModule key, Long userId) throws Exception {
        // TODO Auto-generated method stub
        RetMsg retMsg = new RetMsg();
        if (key != null) {
            Where<LeaStatisticsModule> ipWhere = new Where<LeaStatisticsModule>();
            ipWhere.eq("module_code", key.getModuleCode());
            ipWhere.eq("create_user_id", userId);
            LeaStatisticsModule oldipm = leaStatisticsModuleService.selectOne(ipWhere);
            if (oldipm.getModuleRank() != key.getModuleRank()) {
                ipWhere = new Where<LeaStatisticsModule>();
                ipWhere.eq("module_rank", key.getModuleRank());
                ipWhere.eq("create_user_id", userId);
                LeaStatisticsModule replaceData = leaStatisticsModuleService.selectOne(ipWhere);
                replaceData.setModuleRank(oldipm.getModuleRank());
                leaStatisticsModuleService.updateById(replaceData);
                oldipm.setModuleRank(key.getModuleRank());
            }
            if (key.getIsHide() != null) {
                oldipm.setIsHide(key.getIsHide());
            }
            leaStatisticsModuleService.updateById(oldipm);
            retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
            retMsg.setMessage("操作成功");
        } else {
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage("更改的传递数据为空");
        }

        return retMsg;
    }
}
