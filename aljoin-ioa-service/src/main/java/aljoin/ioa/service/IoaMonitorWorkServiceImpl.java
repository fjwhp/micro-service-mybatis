package aljoin.ioa.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.cmd.DeleteRunningTaskCmd;
import aljoin.act.cmd.StartActivityCmd;
import aljoin.act.creator.ChainedActivitiesCreatorBase;
import aljoin.act.creator.MultiInstanceActivityCreatorBase;
import aljoin.act.creator.RuActivityDefinitionIntepreter;
import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.entity.ActAljoinBpmnUser;
import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.dao.entity.ActAljoinFixedConfig;
import aljoin.act.dao.entity.ActAljoinFormDataHis;
import aljoin.act.dao.entity.ActAljoinFormWidgetRun;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.dao.entity.ActHiActinst;
import aljoin.act.dao.entity.ActIdMembership;
import aljoin.act.dao.entity.ActRuIdentitylink;
import aljoin.act.dao.entity.ActRuTask;
import aljoin.act.dao.entity.ActRunTimeExecution;
import aljoin.act.dao.object.ActHolidayListVO;
import aljoin.act.dao.object.ActHolidayVO;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnRunService;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinBpmnUserService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.act.iservice.ActAljoinFixedConfigService;
import aljoin.act.iservice.ActAljoinFormDataHisService;
import aljoin.act.iservice.ActAljoinFormWidgetRunService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.iservice.ActHiActinstService;
import aljoin.act.iservice.ActHiProcinstService;
import aljoin.act.iservice.ActIdMembershipService;
import aljoin.act.iservice.ActRuIdentitylinkService;
import aljoin.act.iservice.ActRuTaskService;
import aljoin.act.iservice.ActRunTimeExecutionService;
import aljoin.act.service.ActRuActivityDefinitionServiceImpl;
import aljoin.act.util.BaseProcessDefinitionUtils;
import aljoin.att.dao.entity.AttSignInOut;
import aljoin.att.dao.entity.AttSignInOutHis;
import aljoin.att.iservice.AttSignInOutHisService;
import aljoin.att.iservice.AttSignInOutService;
import aljoin.aut.dao.entity.AutDataStatistics;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserRoleService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.ioa.dao.entity.IoaReceiveFile;
import aljoin.ioa.dao.object.ActApprovalVO;
import aljoin.ioa.dao.object.ActRegulationListVO;
import aljoin.ioa.dao.object.ActRegulationVO;
import aljoin.ioa.dao.object.ActWorkingListVO;
import aljoin.ioa.dao.object.ActWorkingVO;
import aljoin.ioa.dao.object.WaitTaskShowVO;
import aljoin.ioa.iservice.IoaMonitorWorkService;
import aljoin.ioa.iservice.IoaReceiveFileService;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.mee.dao.entity.MeeOutsideMeeting;
import aljoin.mee.iservice.MeeOutsideMeetingService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.off.dao.entity.OffMonthReport;
import aljoin.off.iservice.OffMonthReportService;
import aljoin.pub.dao.entity.PubPublicInfo;
import aljoin.pub.iservice.PubPublicInfoService;
import aljoin.sma.iservice.SysMsgModuleInfoService;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.sms.iservice.SmsShortMessageService;
import aljoin.util.DateUtil;

/**
 * 
 * 流转监控(服务实现类).
 * 
 * @author：pengsp
 * 
 *                @date： 2017-10-19
 */
@Service
public class IoaMonitorWorkServiceImpl implements IoaMonitorWorkService {
    @Resource
    private ActActivitiService actActivitiService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private TaskService taskService;
    @Resource
    private ActAljoinCategoryService actAljoinCategoryService;
    @Resource
    private HistoryService historyService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private ActAljoinQueryService actAljoinQueryService;
    @Resource
    private ActAljoinBpmnUserService actAljoinBpmnUserService;
    @Resource
    private ActAljoinBpmnService actAljoinBpmnService;
    @Resource
    private ActAljoinBpmnRunService actAljoinBpmnRunService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;
    @Resource
    private SysMsgModuleInfoService sysMsgModuleInfoService;
    @Resource
    private MaiSendBoxService maiSendBoxService;
    @Resource
    private AutDepartmentService autDepartmentService;
    @Resource
    private AutMsgOnlineService autMsgOnlineService;
    @Resource
    private SmsShortMessageService smsShortMessageService;
    @Resource
    private AutUserRoleService autUserRoleService;
    @Resource
    private AutUserPositionService autUserPositionService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private ActAljoinFixedConfigService actAljoinFixedConfigService;
    @Resource
    private AutPositionService autPositionService;
    @Resource
    private ManagementService managementService;
    @Resource
    private ActAljoinFormDataHisService actAljoinFormDataHisService;
    @Resource
    private ActAljoinFormWidgetRunService actAljoinFormWidgetRunService;
    @Resource
    private ActRuIdentitylinkService actRuIdentitylinkService;
    @Resource
    private ActIdMembershipService actIdMembershipService;
    @Resource
    private AttSignInOutService attSignInOutService;
    @Resource
    private IoaReceiveFileService ioaReceiveFileService;
    @Resource
    private OffMonthReportService offMonthReportService;
    @Resource
    private AutDataStatisticsService autDataStatisticsService;
    @Resource
    private PubPublicInfoService pubPublicInfoService;
    @Resource
    private MeeOutsideMeetingService meeOutsideMeetingService;
    @Resource
    private AttSignInOutHisService attSignInOutHisService;
    @Resource
    private ActHiActinstService actHiActinstService;
    @Resource
    private ProcessEngine processEngine;
    @Resource
    private ActRunTimeExecutionService actRunTimeExecutionService;
    @Resource
    private ActRuTaskService actRuTaskService;
    @Resource
    private IdentityService identityService;
    @Resource
    private ActHiProcinstService actHiProcinstService;
    
    @Override
    public Page<WaitTaskShowVO> list(PageBean pageBean, Map<String, Object> map) throws Exception {
        Page<WaitTaskShowVO> page = new Page<WaitTaskShowVO>();
        Set<String> startIdSet = new HashSet<String>();
        // 获取符合查询条件在运行流程实例ID
        Set<String> processInstanceIds = new HashSet<String>();
        
        // 获取流程授权
        String userID = map.get("UserID").toString();
        Where<ActAljoinBpmnUser> bpmnUserWhere = new Where<ActAljoinBpmnUser>();
        if (map.get("qBpmn") != null && !"".equals(map.get("qBpmn").toString())) {
            Long tmpBpmnId = Long.valueOf(map.get("qBpmn").toString());
            bpmnUserWhere.eq("bpmn_id", tmpBpmnId);
        }
        bpmnUserWhere.eq("is_delete", 0);
        bpmnUserWhere.eq("is_active", 1);
        bpmnUserWhere.eq("auth_type", 1);
        bpmnUserWhere.eq("user_id", userID);
        bpmnUserWhere.setSqlSelect("bpmn_id");
        List<ActAljoinBpmnUser> bpmnUserList = actAljoinBpmnUserService.selectList(bpmnUserWhere);
        
        // 获取管理流程ID
        Set<Long> bpmnIds = new HashSet<Long>();
        for (ActAljoinBpmnUser actAljoinBpmnUser : bpmnUserList) {
            bpmnIds.add(actAljoinBpmnUser.getBpmnId());
        }
        if(bpmnIds.size() == 0){
            return page;
        }
        Where<ActAljoinBpmnRun> bpmnWhere = new Where<ActAljoinBpmnRun>();
        bpmnWhere.in("orgnl_id", bpmnIds);
        bpmnWhere.setSqlSelect("process_name");
        List<ActAljoinBpmnRun> bpmnList = actAljoinBpmnRunService.selectList(bpmnWhere);
        List<String> bpmnNameList = new ArrayList<String>();
        
        // 用名字查
        for (ActAljoinBpmnRun actAljoinBpmn : bpmnList) {
            bpmnNameList.add(actAljoinBpmn.getProcessName());
        }
        
        Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
        // 筛选出符合条件的query数据
        where.in("process_name", bpmnNameList);
        if (map.get("formType") != null && !"".equals(map.get("formType").toString())) {
            String tpyeID = map.get("formType").toString();
            where.like("process_category_ids", tpyeID);
        }
        SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (map.get("isUrgent") != null && !"".equals(map.get("isUrgent").toString())) {
            where.eq("urgent_status", map.get("isUrgent").toString());
        }
        if (map.get("serialNumber") != null && !"".equals(map.get("serialNumber").toString())) {
            where.like("serial_number", map.get("serialNumber").toString());
        }
        if (map.get("referenceNumber") != null && !"".equals(map.get("referenceNumber").toString())) {
            where.like("reference_number", map.get("referenceNumber").toString());
        }
        if (map.get("StatrTime") != null && !"".equals(map.get("StatrTime").toString()) && map.get("EndTime") != null
            && !"".equals(map.get("EndTime").toString())) {
            // 时间集合
            where.between("start_time", formats.parse(map.get("StatrTime").toString() + " 00:00:00"),
                formats.parse(map.get("EndTime").toString() + " 23:59:59"));
        } else if (map.get("StatrTime") != null && !"".equals(map.get("StatrTime").toString())) {
            where.ge("start_time", formats.parse(map.get("StatrTime").toString() + " 00:00:00"));
        } else if (map.get("EndTime") != null && !"".equals(map.get("EndTime").toString())) {
            where.le("start_time", formats.parse(map.get("EndTime").toString() + " 23:59:59"));
        }
        if (map.get("searchKey") != null && !"".equals(map.get("searchKey").toString())) {
            where.andNew().like("process_title", map.get("searchKey").toString())
                .or("create_full_user_name like {0}", "%" + map.get("searchKey").toString() + "%");
        }
        where.isNotNull("current_handle_full_user_name");
        // queryMap
        Map<String, ActAljoinQuery> qMap = new HashMap<String, ActAljoinQuery>();
        where.setSqlSelect("process_instance_id, start_task, process_category_ids, process_title,create_full_user_name,urgent_status,start_time");
        List<ActAljoinQuery> allQueryList = actAljoinQueryService.selectList(where);
        for (ActAljoinQuery actAljoinQuery : allQueryList) {
            processInstanceIds.add(actAljoinQuery.getProcessInstanceId());
            if(null != actAljoinQuery.getStartTask() && !StringUtils.isEmpty(actAljoinQuery.getStartTask())){
                startIdSet.add(actAljoinQuery.getStartTask());
            }
            qMap.put(actAljoinQuery.getProcessInstanceId(), actAljoinQuery);
        }
        
        if (processInstanceIds.size() == 0) {
            return page;
        }
        Where<ActRuTask> taskWhere = new Where<ActRuTask>();
        taskWhere.in("PROC_INST_ID_", processInstanceIds);
        taskWhere.setSqlSelect("id_");
        List<ActRuTask> taskList1 = actRuTaskService.selectList(taskWhere);
        Set<String> taskIdSet = new HashSet<String>();
        for (ActRuTask actRuTask : taskList1) {
            if(!startIdSet.contains(actRuTask.getId())){
                taskIdSet.add(actRuTask.getId());
            }
        }
        
        Where<ActRuIdentitylink> ruIdentitylinkWhere1 = new Where<ActRuIdentitylink>();
        ruIdentitylinkWhere1.setSqlSelect("DISTINCT(TASK_ID_)");
        ruIdentitylinkWhere1.in("TASK_ID_", taskIdSet);
        ruIdentitylinkWhere1.eq("TYPE_", "candidate");
        if ("1".equals(map.get("sorting").toString())) {
            ruIdentitylinkWhere1.orderBy("TASK_ID_", false);
        } 
        Page<ActRuIdentitylink> identitylikePage = actRuIdentitylinkService.selectPage(new Page<ActRuIdentitylink>(pageBean.getPageNum(), pageBean.getPageSize()), ruIdentitylinkWhere1);
        List<ActRuIdentitylink> linkList1 = identitylikePage.getRecords();
        taskIdSet.clear();
        for (ActRuIdentitylink actRuIdentitylink : linkList1) {
            taskIdSet.add(actRuIdentitylink.getTaskId());
        }
        
        Map<String, ActRuTask> taskMap = new HashMap<String, ActRuTask>();
        Where<ActRuTask> taskWhere2 = new Where<ActRuTask>();
        taskWhere2.in("ID_", taskIdSet);
        if ("1".equals(map.get("sorting").toString())) {
            taskWhere2.orderBy("ID_", false);
        } 
        taskWhere2.setSqlSelect("id_,execution_id_,proc_inst_id_,name_,task_def_key_,proc_def_id_,ASSIGNEE_");
        List<ActRuTask> taskList = actRuTaskService.selectList(taskWhere2);
        
        List<WaitTaskShowVO> returnList = new ArrayList<WaitTaskShowVO>();
        Set<String> taskProcinstIdSet = new HashSet<String>();
        Set<String> taskCategorySet = new HashSet<String>();
        List<String> taskIdList = new ArrayList<String>();
        Set<String> userIdSet = new HashSet<String>();
        for(ActRuTask task : taskList){
            WaitTaskShowVO vo = new WaitTaskShowVO();
            String actAljoinCategory = "";
            ActAljoinQuery actAljoinQuery = qMap.get(task.getProcInstId());
            if(null != actAljoinQuery){
                actAljoinCategory = actAljoinQuery.getProcessCategoryIds();
                if(actAljoinCategory.contains(",")){
                    actAljoinCategory = actAljoinCategory.substring(actAljoinCategory.lastIndexOf(",")+1, actAljoinCategory.length());
                }
                vo.setFormType(actAljoinCategory);// 流程分类
                taskCategorySet.add(actAljoinCategory);
                vo.setUrgency(actAljoinQuery.getUrgentStatus());// 缓急
                vo.setTitle(actAljoinQuery.getProcessTitle());// 标题
                vo.setFillingDate(actAljoinQuery.getStartTime());// 流程发起时间
                vo.setFounder(actAljoinQuery.getCreateFullUserName());// 流程发起人
            }
            vo.setLink(task.getName());// 环节
            vo.setProcessInstanceId(task.getProcInstId());
            vo.setTaskId(task.getId());
            vo.setTaskDefKey(task.getTaskDefKey());
            if(task.getAssignee() != null && !StringUtils.isEmpty(task.getAssignee())){
                vo.setFormerManager(task.getAssignee());
                userIdSet.add(task.getAssignee());
            }else{
                taskIdList.add(task.getId());
            }
            taskMap.put(task.getId(), task);
            taskProcinstIdSet.add(task.getProcInstId());
            returnList.add(vo);
        }
        
        
        Set<String> groupIdSet = new HashSet<String>();
        Map<String, List<ActRuIdentitylink>> runIdMap = new HashMap<String, List<ActRuIdentitylink>>();
        Map<String, String> idMemberMap = new HashMap<String, String>();
        if(taskIdList.size() > 0){
            Where<ActRuIdentitylink> ruIdentitylinkWhere = new Where<ActRuIdentitylink>();
            List<ActRuIdentitylink> linkList = new ArrayList<ActRuIdentitylink>();
            ruIdentitylinkWhere.isNull("PROC_INST_ID_");
            ruIdentitylinkWhere.in("TASK_ID_", taskIdList);
            linkList = actRuIdentitylinkService.selectList(ruIdentitylinkWhere); 
            if (linkList != null && linkList.size() > 0) {
                for (ActRuIdentitylink actRuIdentitylink : linkList) {
                    List<ActRuIdentitylink> tmpLinkList = new ArrayList<ActRuIdentitylink>();
                    if (runIdMap.containsKey(actRuIdentitylink.getTaskId())) {
                        tmpLinkList.addAll(runIdMap.get(actRuIdentitylink.getTaskId()));
                    }
                    tmpLinkList.add(actRuIdentitylink);
                    runIdMap.put(actRuIdentitylink.getTaskId(), tmpLinkList);
                    if(!StringUtils.isEmpty(actRuIdentitylink.getUserId())){
                        userIdSet.add(actRuIdentitylink.getUserId());
                    }
                    if(!StringUtils.isEmpty(actRuIdentitylink.getGroupId())){
                        groupIdSet.add(actRuIdentitylink.getGroupId());
                    }
                }
            }
            
            // 获取所有ACT组与对应人员关系
            if(groupIdSet.size()>0){
                Where<ActIdMembership> idMembershipWhere = new Where<ActIdMembership>();
                idMembershipWhere.in("GROUP_ID_",groupIdSet);
                List<ActIdMembership> actIdMembershipList = actIdMembershipService.selectList(idMembershipWhere);
                for (ActIdMembership actIdMembership : actIdMembershipList) {
                    userIdSet.add(actIdMembership.getUserId());
                    String userId = "";
                    if (idMemberMap.containsKey(actIdMembership.getGroupId())) {
                        userId = idMemberMap.get(actIdMembership.getGroupId());
                    }
                    if (userId.indexOf(actIdMembership.getUserId()) ==  -1) {
                        userId += actIdMembership.getUserId() + ",";
                    }
                    idMemberMap.put(actIdMembership.getGroupId(), userId);
                }
            }
        }
        Map<String, String> idUserMap = new HashMap<String, String>();
        if(userIdSet.size()>0){
            Where<AutUser> userwhere = new Where<AutUser>();
            userwhere.in("id", userIdSet);
            userwhere.setSqlSelect("id,user_name,full_name");
            List<AutUser> autList = autUserService.selectList(userwhere);
            for (AutUser autUser : autList) {
                idUserMap.put(autUser.getId().toString(), autUser.getFullName());
            }
            autList.clear();
        }
        // 流程map
        List<HistoricProcessInstance> intlist
            = historyService.createHistoricProcessInstanceQuery().processInstanceIds(taskProcinstIdSet).unfinished().list();
        Map<String, HistoricProcessInstance> IdProcinstMap = new HashMap<String, HistoricProcessInstance>();
        for (HistoricProcessInstance historicProcessInstance : intlist) {
            IdProcinstMap.put(historicProcessInstance.getId(), historicProcessInstance);
        }
        intlist.clear();
        
        // 任务map
        List<HistoricTaskInstance> tinsList = historyService.createHistoricTaskInstanceQuery()
            .processInstanceIdIn(new ArrayList<String>(taskProcinstIdSet)).unfinished().list();
        Map<String, HistoricTaskInstance> procinstidTaskMap = new HashMap<String, HistoricTaskInstance>();
        for (HistoricTaskInstance hisTask : tinsList) {
            procinstidTaskMap.put(hisTask.getId(),hisTask);
        }
        tinsList.clear();
        
        // 流程分类
        Where<ActAljoinCategory> categoryWhere = new Where<ActAljoinCategory>();
        categoryWhere.in("id", taskCategorySet);
        categoryWhere.setSqlSelect("id,category_name");
        List<ActAljoinCategory> categoryList = actAljoinCategoryService.selectList(categoryWhere);
        Map<String, String> categoryMap = new HashMap<String, String>();
        for (ActAljoinCategory actAljoinCategory : categoryList) {
            categoryMap.put(actAljoinCategory.getId().toString(),
                actAljoinCategory.getCategoryName());
        }
        for (WaitTaskShowVO obj : returnList) {
            HistoricProcessInstance historicProcessInstance = IdProcinstMap.get(obj.getProcessInstanceId());
            HistoricTaskInstance hisTask = procinstidTaskMap.get(obj.getTaskId());
            String str = historicProcessInstance.getBusinessKey();
            obj.setBusinessKey(str);
            obj.setBpmnId(str.split(",")[0]);
            String category = categoryMap.get(obj.getFormType());
            obj.setFormType(category);
            if(obj.getFormerManager() == null || StringUtils.isEmpty(obj.getFormerManager())){
                List<ActRuIdentitylink> idenList = runIdMap.get(obj.getTaskId());
                if(idenList != null){
                    Set<String> taskUserIdSet = new HashSet<String>();
                    for (ActRuIdentitylink identityLink : idenList) {
                        if (identityLink.getUserId() != null && !StringUtils.isEmpty(identityLink.getUserId())) {
                            taskUserIdSet.add(identityLink.getUserId());
                        }else  if (identityLink.getGroupId() != null && StringUtils.isEmpty(identityLink.getGroupId())){
                            String userIds = idMemberMap.get(identityLink.getGroupId());
                            if(userIds.contains(",")){
                                taskUserIdSet.addAll(Arrays.asList(userIds.split(",")));
                            }
                        }
                    }
                    if(taskUserIdSet.size() > 0){
                        String userName = "";
                        for (String taskUserId : taskUserIdSet) {
                            userName += idUserMap.get(taskUserId) + ",";
                        }
                        if(userName.endsWith(",")){
                            userName = userName.substring(0, userName.length()-1);
                        }
                        obj.setFormerManager(userName);
                    }
                }
            }else{
                obj.setSignInTime(hisTask.getClaimTime());
                obj.setFormerManager(idUserMap.get(obj.getFormerManager()));
            }
        }
        page.setTotal(identitylikePage.getTotal());
        page.setCurrent(identitylikePage.getCurrent());
        page.setRecords(returnList);
        page.setSize(identitylikePage.getSize());
        return page;
    }

    public String getParentId(String parentId) throws Exception {
        String tpyeID = parentId + ",";
        ActAljoinCategory actcategory = new ActAljoinCategory();
        actcategory.setParentId(Long.valueOf(parentId));
        List<ActAljoinCategory> categoryList = actAljoinCategoryService.getByParentId(actcategory);
        if (categoryList != null && categoryList.size() > 0) {
            for (int i = 0; i < categoryList.size(); i++) {
                ActAljoinCategory tmp = categoryList.get(i);
                tpyeID = tpyeID + this.getParentId(tmp.getId().toString());
            }
        }
        return tpyeID;

    }

    @Override
    public RetMsg selectUrgent(Long customUserId, String customUserName, String customNickName, String ids,
        String msgType) throws Exception {
        RetMsg retMsg = new RetMsg();
        RetMsg tmpMsg = new RetMsg();
        retMsg.setMessage("");
        String userId = customUserId.toString();
        AutUser autUser = autUserService.selectById(Long.valueOf(userId));
        if (userId != null && !"".equals(userId) && ids != null && !"".equals(ids) && msgType != null
            && !"".equals(msgType)) {
            ids = ids.replaceAll("'", "");
            msgType = msgType.replaceAll("'", "");
            // AutUser autUser =
            // autUserService.selectById(Long.valueOf(userId));
            String[] taskIds = null;
            if (ids.length() > 0) {
                ids = ids.substring(0, ids.length() - 1);
                taskIds = ids.split(",");
            }
            String[] stype = null;
            if (msgType.length() > 0) {
                msgType = msgType.substring(0, msgType.length() - 1);
                stype = msgType.split(",");
            }
            if (taskIds != null) {
                for (String taskId : taskIds) {
                    String userids = "";
                    String userNames = "";
                    String userfullNames = "";
                    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
                    if (task == null) {
                        List<HistoricTaskInstance> tmpList
                            = historyService.createHistoricTaskInstanceQuery().taskId(taskId).list();
                        if (tmpList != null && tmpList.size() > 0) {
                            String pId = tmpList.get(0).getProcessInstanceId();
                            Where<ActAljoinQueryHis> hisWhere = new Where<ActAljoinQueryHis>();
                            hisWhere.eq("process_instance_id", pId);
                            ActAljoinQueryHis hisTask = actAljoinQueryHisService.selectOne(hisWhere);
                            taskId = hisTask.getProcessTitle();
                        }
                        retMsg.setMessage(retMsg.getMessage() + "任务：" + taskId + "的工作不存在或已经处理，撤回！");
                        continue;
                    }
                    if (task.getAssignee() != null && !"".equals(task.getAssignee())) {
                        // 获取当前环节签收人
                        userids = task.getAssignee() + ",";
                    } else {
                        // 获取当前环节候选人
                        List<IdentityLink> idenList = taskService.getIdentityLinksForTask(task.getId());
                        for (IdentityLink identityLink : idenList) {
                            if (identityLink.getUserId() != null && !"".equals(identityLink.getUserId())) {
                                userids = userids + identityLink.getUserId() + ",";
                                continue;
                            }
                            String deptId = identityLink.getGroupId();
                            AutDepartment dept = new AutDepartment();
                            dept = autDepartmentService.selectById(Long.valueOf(deptId));
                            if (dept != null) {
                                List<AutUser> autUserList = autDepartmentService.getChildDeptUserList(dept);
                                if (autUserList != null) {
                                    for (AutUser autUser2 : autUserList) {
                                        userids = userids + autUser2.getId() + ",";
                                    }
                                    continue;
                                }
                            }
                            Where<AutUserRole> rWhere = new Where<AutUserRole>();
                            rWhere.eq("is_active", 1);
                            rWhere.eq("role_id", deptId);
                            rWhere.setSqlSelect("user_id");
                            List<AutUserRole> urList = autUserRoleService.selectList(rWhere);
                            if (urList != null && urList.size() > 0) {
                                for (AutUserRole autUserRole : urList) {
                                    userids = userids + autUserRole.getUserId() + ",";
                                }
                                continue;
                            }
                            Where<AutUserPosition> uPWhere = new Where<AutUserPosition>();
                            uPWhere.eq("is_active", 1);
                            uPWhere.eq("position_id", deptId);
                            uPWhere.setSqlSelect("user_id");
                            List<AutUserPosition> uPList = autUserPositionService.selectList(uPWhere);
                            if (uPList != null && uPList.size() > 0) {
                                for (AutUserPosition autUserPosition : uPList) {
                                    userids = userids + autUserPosition.getUserId() + ",";
                                }
                                continue;
                            }

                        }
                    }
                    Where<ActAljoinQuery> aqWhere = new Where<ActAljoinQuery>();
                    aqWhere.eq("process_instance_id", task.getProcessInstanceId().toString());
                    ActAljoinQuery actAljoinQuery = actAljoinQueryService.selectOne(aqWhere);
                    task.getProcessInstanceId();
                    if (userids.length() > 0) {
                        userids = userids.substring(0, userids.length() - 1);
                    } else {
                        String msg = retMsg.getMessage();
                        if ("".equals(msg)) {
                            msg = actAljoinQuery.getProcessTitle() + "催办失败,没有后续办理人!";
                        } else {
                            msg += "<br>" + actAljoinQuery.getProcessTitle() + "催办失败,没有后续办理人!";
                        }
                        retMsg.setMessage(msg);
                        continue;
                    }
                    Where<AutUser> userWhere = new Where<AutUser>();
                    userWhere.in("id", userids);
                    userWhere.setSqlSelect("id,user_name,full_name");
                    List<AutUser> sendList = autUserService.selectList(userWhere);

                    for (String string : stype) {
                        List<String> setlist = new ArrayList<String>(); // 接收文件LIST
                        Map<String, String> list = new HashMap<String, String>(); // 构造模版消息
                        list.put("handle", actAljoinQuery.getCurrentHandleFullUserName());// 当前办理人
                        AutUser createUser = autUserService.selectById(actAljoinQuery.getCreateUserId());
                        list.put("create", createUser.getFullName());// 创建人
                        list.put("process", actAljoinQuery.getProcessName());// 流程名称
                        // 缓急程度
                        if (actAljoinQuery.getUrgentStatus() == null && actAljoinQuery.getUrgentStatus() == "") {
                            list.put("priorities", "一般");
                        } else {
                            list.put("priorities", actAljoinQuery.getUrgentStatus());
                        }
                        list.put("title", actAljoinQuery.getProcessTitle());// 标题
                        if ("1".equals(string)) {
                            setlist = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_SMS,
                                WebConstant.TEMPLATE_WORK_NAME,

                                WebConstant.TEMPLATE_BEHAVIOR_CB);
                            if (!"".equals(setlist.get(0).toString())) {
                                if (sendList != null) {
                                    userids = "";
                                    userNames = "";
                                    userfullNames = "";
                                    for (AutUser autUser3 : sendList) {
                                        userids = userids + autUser3.getId() + ";";
                                        userNames = userNames + autUser3.getUserName() + ";";
                                        userfullNames = userfullNames + autUser3.getFullName() + ";";
                                    }
                                    SmsShortMessage smsShortMessage = new SmsShortMessage();
                                    if (userids.length() > 0) {
                                        userids = userids.substring(0, userids.length() - 1);
                                        userNames = userNames.substring(0, userNames.length() - 1);
                                    }
                                    smsShortMessage.setReceiverId(userids);
                                    smsShortMessage.setReceiverName(userfullNames);
                                    smsShortMessage.setContent(setlist.get(0));
                                    smsShortMessage.setSendNumber(sendList.size());
                                    smsShortMessage.setIsActive(1);
                                    smsShortMessage.setSendTime(new Date());
                                    smsShortMessage.setSendStatus(0);
                                    smsShortMessage.setTheme("催办工作");
                                    tmpMsg = smsShortMessageService.add(smsShortMessage, autUser);

                                    if (tmpMsg.getCode() == WebConstant.RETMSG_FAIL_CODE) {
                                        String msg = retMsg.getMessage();
                                        if ("".equals(msg)) {
                                            msg = actAljoinQuery.getProcessTitle() + "短信催办失败!";
                                        } else {
                                            msg += "<br>" + actAljoinQuery.getProcessTitle() + "短信催办失败!";
                                        }
                                        retMsg.setMessage(msg);
                                    }
                                } else {
                                    String msg = retMsg.getMessage();
                                    if ("".equals(msg)) {
                                        msg = actAljoinQuery.getProcessTitle() + "催办人不存在，短信催办失败!";
                                    } else {
                                        msg += "<br>" + actAljoinQuery.getProcessTitle() + "催办人不存在，短信催办失败!";
                                    }
                                    retMsg.setMessage(msg);
                                }

                            } else {
                                String msg = retMsg.getMessage();
                                if ("".equals(msg)) {
                                    retMsg.setMessage(retMsg.getMessage() + "短信模版未设置,短信未发出!");

                                } else {
                                    if (msg.indexOf("短信模版未设置") > -1) {
                                    } else {
                                        retMsg.setMessage("<br>" + retMsg.getMessage() + "短信模版未设置,短信未发出!");
                                    }
                                }

                            }
                        }
                        setlist.clear();
                        // 邮件
                        if ("2".equals(string)) {
                            setlist = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
                                WebConstant.TEMPLATE_WORK_NAME,

                                WebConstant.TEMPLATE_BEHAVIOR_CB);
                            if (!"".equals(setlist.get(0).toString())) {
                                MaiWriteVO vo = new MaiWriteVO();
                                MaiReceiveBox rb = new MaiReceiveBox();
                                MaiReceiveBoxSearch maiReceiveBoxSearch = new MaiReceiveBoxSearch();
                                maiReceiveBoxSearch.setAttachmentCount(0);
                                maiReceiveBoxSearch.setIsUrgent(0);
                                maiReceiveBoxSearch.setSubjectText(setlist.get(1));
                                rb.setMailContent(setlist.get(0));
                                if (setlist.get(0) != null && setlist.get(0).length() > 0) {
                                    rb.setMailSize((setlist.get(0).getBytes().length) / 1024);
                                }

                                userids = "";
                                if (sendList != null) {
                                    for (AutUser autUser3 : sendList) {
                                        userids = userids + autUser3.getId() + ";";
                                        userNames = userNames + autUser3.getUserName() + ";";
                                        userfullNames = userfullNames + autUser3.getFullName() + ";";
                                    }
                                    rb.setReceiveFullNames(userfullNames);
                                    rb.setReceiveUserIds(userids);
                                    rb.setReceiveUserNames(userNames);
                                    vo.setMaiReceiveBox(rb);
                                    vo.setMaiReceiveBoxSearch(maiReceiveBoxSearch);
                                    AutUser user = new AutUser();
                                    user.setUserName(customUserName);
                                    user.setFullName(customNickName);
                                    user.setId(customUserId);
                                    tmpMsg = maiSendBoxService.add(vo, user);
                                    if (tmpMsg.getCode() == WebConstant.RETMSG_FAIL_CODE) {
                                        String msg = retMsg.getMessage();
                                        if ("".equals(msg)) {
                                            msg = actAljoinQuery.getProcessTitle() + "邮件催办失败!";
                                        } else {
                                            msg += "<br>" + actAljoinQuery.getProcessTitle() + "邮件催办失败!";
                                        }
                                        retMsg.setMessage(msg);
                                    }
                                } else {
                                    String msg = retMsg.getMessage();
                                    if ("".equals(msg)) {
                                        msg = actAljoinQuery.getProcessTitle() + "催办人不存在，邮件催办失败!";
                                    } else {
                                        msg += "<br>" + actAljoinQuery.getProcessTitle() + "催办人不存在，邮件催办失败!";
                                    }
                                    retMsg.setMessage(msg);
                                }

                            } else {
                                String msg = retMsg.getMessage();
                                if ("".equals(msg)) {
                                    retMsg.setMessage(retMsg.getMessage() + "邮件模版未设置,邮件未发出!");
                                } else {
                                    if (msg.indexOf("邮件模版未设置,邮件未发出") > -1) {
                                    } else {
                                        retMsg.setMessage("<br>" + retMsg.getMessage() + "邮件模版未设置,邮件未发出!");
                                    }
                                }

                            }
                        }
                        setlist.clear();
                        // 在线消息
                        if ("3".equals(string)) {
                            setlist = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
                                WebConstant.TEMPLATE_WORK_NAME, WebConstant.TEMPLATE_BEHAVIOR_CB);
                            if (!"".equals(setlist.get(0).toString())) {
                                AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
                                // 封装推送信息请求信息
                                requestVO.setFromUserId(customUserId);
                                requestVO.setFromUserFullName(customNickName);
                                requestVO.setFromUserName(customUserName);
                                requestVO.setMsgType(WebConstant.ONLINE_MSG_TOGETHERWORK);
                                requestVO.setMsgContent(setlist.get(0));
                                HistoricProcessInstance historicProcessInstance
                                    = historyService.createHistoricProcessInstanceQuery()
                                        .processInstanceId(task.getProcessInstanceId()).singleResult();
                                HistoricTaskInstance taskInstance = null;
                                taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(task.getId())
                                    .singleResult();
                                String str = historicProcessInstance.getBusinessKey();
                                String bpmnId = "";
                                if (!StringUtils.isEmpty(str)) {
                                    String[] key = str.split(",");
                                    if (key.length >= 1) {
                                        bpmnId = key[0];
                                    }
                                }
                                Date date = taskInstance.getClaimTime();
                                String signInTime = "";
                                if (date != null && taskInstance.getAssignee() != null) {
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    signInTime = formatter.format(date);
                                }
                                Boolean isNoReceive = true;
                                if (historicProcessInstance.getBusinessKey() != null) {
                                    String key = historicProcessInstance.getBusinessKey();
                                    if (key.indexOf("ioaReceiveFile") > -1) {
                                        String keys[] = key.split("ioaReceiveFile,");
                                        requestVO.setGoUrl("ioa/ioaReceiveWork/ioaReceiveWorkPageDetail.html?bizId="
                                            + keys[1] + "&linkName=" + task.getName() + "&processInstanceId="
                                            + task.getProcessInstanceId());
                                        isNoReceive = false;
                                    }
                                }
                                if (isNoReceive) {
                                    if (!"".equals(signInTime)) {
                                        requestVO.setGoUrl("../../act/modeler/openForm.html?id=" + bpmnId
                                            + "&activityId=" + task.getId() + "&taskId=" + task.getTaskDefinitionKey()
                                            + "&businessKey=" + historicProcessInstance.getBusinessKey()
                                            + "&signInTime=" + signInTime + "&wait=1");
                                    } else {
                                        requestVO.setGoUrl("../../act/modeler/openForm.html?id=" + bpmnId
                                            + "&activityId=" + task.getId() + "&taskId=" + task.getTaskDefinitionKey()
                                            + "&businessKey=" + historicProcessInstance.getBusinessKey()
                                            + "&signInTime=" + "&wait=1");
                                    }
                                }
                                List<String> recId = new ArrayList<String>();
                                if (sendList != null && sendList.size() > 0) {
                                    for (AutUser autUser3 : sendList) {
                                        recId.add(autUser3.getId().toString());
                                    }
                                    requestVO.setToUserId(recId);
                                    tmpMsg = autMsgOnlineService.pushMessageToUserList(requestVO);
                                    if (tmpMsg.getCode() == WebConstant.RETMSG_FAIL_CODE) {
                                        String msg = retMsg.getMessage();
                                        if ("".equals(msg)) {
                                            msg = actAljoinQuery.getProcessTitle() + "在线消息催办失败!";
                                        } else {
                                            msg += "<br>" + actAljoinQuery.getProcessTitle() + "在线消息催办失败!";
                                        }
                                        retMsg.setMessage(msg);
                                    }
                                } else {
                                    String msg = retMsg.getMessage();
                                    if ("".equals(msg)) {
                                        msg = actAljoinQuery.getProcessTitle() + "催办人不存在，在线消息催办失败!";
                                    } else {
                                        msg += "<br>" + actAljoinQuery.getProcessTitle() + "催办人不存在，在线消息催办失败!";
                                    }
                                    retMsg.setMessage(msg);
                                }
                            } else {
                                String msg = retMsg.getMessage();
                                if ("".equals(msg)) {
                                    retMsg.setMessage(retMsg.getMessage() + "在线消息模版未设置,在线消息未发出!");

                                } else {
                                    if (msg.indexOf("在线消息模版未设置") > -1) {
                                    } else {
                                        retMsg.setMessage(retMsg.getMessage() + "<br>在线消息模版未设置,在线消息未发出!");
                                    }
                                }
                            }
                        }

                    }
                    // -----

                }
            }
            retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
            if ("".equals(retMsg.getMessage())) {
                retMsg.setMessage("操作成功");
            }

        } else {
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }

    @SuppressWarnings("unused")
    @Override
    public List<ActHolidayListVO> getHolidayList(ActHolidayVO obj, PageBean pageBean) throws Exception {
        // TODO Auto-generated method stub
        List<ActHolidayListVO> holiList = new ArrayList<ActHolidayListVO>();
        Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
        configWhere.eq("is_active", 1);
        configWhere.where("process_code like {0}", WebConstant.RETMSG_OPERATION_LEAVE + "%");
        configWhere.setSqlSelect("process_id");
        List<ActAljoinFixedConfig> configList = actAljoinFixedConfigService.selectList(configWhere);
        List<String> proIds = new ArrayList<String>(); // 查询是否存在请假流程
        Map<String, ActHolidayListVO> uMap = new HashMap<String, ActHolidayListVO>();
        List<AutUser> userList = new ArrayList<AutUser>();
        String uids = "";
        if (obj.getDeptId() != null && !"".equals(obj.getDeptId().toString())) {
            AutDepartment autDepartment = autDepartmentService.selectById(Long.valueOf(obj.getDeptId()));
            Where<AutDepartmentUser> dUserWhere = new Where<AutDepartmentUser>();
            dUserWhere.where("dept_code like {0}", autDepartment.getDeptCode() + "%");
            dUserWhere.setSqlSelect("user_id,dept_id");
            List<AutDepartmentUser> duserlist = autDepartmentUserService.selectList(dUserWhere);
            if (duserlist != null && duserlist.size() > 0) {
                for (AutDepartmentUser autDepartmentUser : duserlist) {
                    uids += autDepartmentUser.getUserId() + ",";
                }
            }

        }
        Where<AutUser> userWhere = new Where<AutUser>();
        if (obj.getDeptId() != null && !"".equals(obj.getDeptId())) {
            if (!"".equals(uids) && uids.length() > 0) {
                userWhere.in("id", uids);
            } else {
                userWhere.eq("id", "1232131222222312");
            }
        }
        userWhere.eq("is_active", 1);
        userWhere.eq("is_account_expired", 0);
        userWhere.eq("is_account_locked", 0);
        userWhere.eq("is_credentials_expired", 0);
        userWhere.setSqlSelect("full_name,id");
        userList = autUserService.selectList(userWhere);
        // ---------------------------------岗位
        Where<AutUserPosition> upWhere = new Where<AutUserPosition>();
        if (!"".equals(uids) && uids.length() > 0) {
            upWhere.in("user_id", uids);
        }
        upWhere.eq("is_active", 1);
        upWhere.setSqlSelect("user_id,position_id");
        List<AutUserPosition> upList = autUserPositionService.selectList(upWhere);
        Map<String, String> upMap = new HashMap<String, String>();
        if (upList != null && upList.size() > 0) {
            String pIds = "";
            for (AutUserPosition autUserPosition : upList) {
                pIds += autUserPosition.getPositionId() + ",";
            }
            Where<AutPosition> pWhere = new Where<AutPosition>();
            pWhere.in("id", pIds);
            pWhere.eq("is_active", 1);
            pWhere.setSqlSelect("id,position_name");
            Map<String, String> pMap = new HashMap<String, String>();
            List<AutPosition> pList = autPositionService.selectList(pWhere);
            for (AutPosition autPosition : pList) {
                pMap.put(autPosition.getId().toString(), autPosition.getPositionName());
            }
            for (AutUserPosition autUserPosition : upList) {
                String tmpName = "";
                if (upMap.containsKey(autUserPosition.getUserId().toString())) {
                    tmpName = upMap.get(autUserPosition.getUserId().toString());
                }
                if (pMap.containsKey(autUserPosition.getPositionId().toString())) {
                    tmpName += pMap.get(autUserPosition.getPositionId().toString()) + ";";
                }
                upMap.put(autUserPosition.getUserId().toString(), tmpName);

            }
        }
        upList.clear();
        String userIds = "";
        if (userList != null && userList.size() > 0) {
            for (AutUser autUser : userList) {
                ActHolidayListVO vo = new ActHolidayListVO();
                userIds += autUser.getId() + ",";
                vo.setUserName(autUser.getFullName());
                if (upMap.containsKey(autUser.getId().toString())) {
                    vo.setJobsName(upMap.get(autUser.getId().toString()));
                }
                Double init = new Double(0);
                vo.setAllocatedLeave(init);
                vo.setAnnualLeave(init);
                vo.setDieLeave(init);
                vo.setDiseaseLeave(init);
                vo.setMarriageLeave(init);
                vo.setMaternityLeave(init);
                vo.setThingsLeave(init);
                vo.setOtherLeave(init);
                uMap.put(autUser.getId().toString(), vo);
            }
        }
        upMap.clear();
        userList.clear();

        if (configList != null && configList.size() > 0) {
            for (ActAljoinFixedConfig actAljoinFixedConfig : configList) {
                proIds.add(actAljoinFixedConfig.getProcessId());
            }
            Where<ActAljoinBpmn> bWhere = new Where<ActAljoinBpmn>();
            bWhere.in("process_id", proIds);
            bWhere.eq("is_active", 1);
            List<ActAljoinBpmn> blist = actAljoinBpmnService.selectList(bWhere);
            proIds.clear();
            if (blist != null && blist.size() > 0) {
                for (ActAljoinBpmn actAljoinBpmn : blist) {
                    proIds.add(actAljoinBpmn.getId().toString());
                }
            }
            // ----------------------------------------------------------------------------------
            Date startTime = null;
            Date endTime = null;
            String weets = obj.getWeeks();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            if ((obj.getStartTimeholi() != "" && "".equals(obj.getStartTimeholi()))
                || (obj.getEndTimeholi() != null && !"".equals(obj.getEndTimeholi()))) {
                startTime = df.parse(obj.getStartTimeholi());
                endTime = df.parse(obj.getEndTimeholi());
                // cal.setTime(endTime);
                // cal.add(Calendar.DAY_OF_MONTH, 1);
                // endTime = cal.getTime();
            } else {
                if ("0".equals(weets)) {
                    // 30天
                    Date newDate = new Date();
                    cal.setTime(df.parse(df.format(newDate)));
                    endTime = cal.getTime();
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.DAY_OF_MONTH, -30);
                    startTime = cal.getTime();

                }
                if ("1".equals(weets)) {
                    // 本周
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
                    startTime = cal.getTime();
                    cal.setTime(df.parse(df.format(startTime)));
                    startTime = cal.getTime();
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                    endTime = cal.getTime();
                }
                if ("2".equals(weets)) {
                    // 本月
                    String newDate = df.format(new Date());
                    String[] newTime = newDate.split("-");
                    newDate = newTime[0] + "-" + newTime[1] + "-01";
                    cal.setTime(df.parse(newDate));
                    // cal.set(Calendar.DAY_OF_MONTH, 0);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.add(Calendar.SECOND, 0);
                    startTime = cal.getTime();
                    cal.add(Calendar.MONTH, 1);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    endTime = cal.getTime();
                }
            }
            // -------------------------------------------------------------------------------

            if (!"".equals(userIds) && userIds.length() > 0) {
                Where<ActAljoinQueryHis> hisWhere = new Where<ActAljoinQueryHis>();
                hisWhere.in("create_user_id", userIds);
                hisWhere.between("create_time", startTime, new Date());
                hisWhere.setSqlSelect("process_instance_id,create_user_id");
                List<ActAljoinQueryHis> hisQueryList = actAljoinQueryHisService.selectList(hisWhere);
                List<String> inList = new ArrayList<String>();
                Set<String> inSet = new HashSet<String>();
                Map<String, String> hisMap = new HashMap<String, String>();
                if (hisQueryList != null && hisQueryList.size() > 0) {
                    for (ActAljoinQueryHis his : hisQueryList) {
                        inList.add(his.getProcessInstanceId());
                        hisMap.put(his.getProcessInstanceId(), his.getCreateUserId().toString());
                    }
                    hisQueryList.clear();
                    Where<ActAljoinFormWidgetRun> fidWhere = new Where<ActAljoinFormWidgetRun>();
                    List<String> ll = new ArrayList<String>();
                    ll.add("select");
                    ll.add("datetime_period");
                    ll.add("decimal");
                    fidWhere.in("widget_type", ll);
                    fidWhere.in("widget_name", "请假天数,请假时间段,请假类型");
                    // fidWhere.eq("widget_type", "datetime_period");
                    fidWhere.setSqlSelect("widget_name,form_id,widget_id");
                    // fidWhere.groupBy("widget_id");
                    List<ActAljoinFormWidgetRun> widetList = actAljoinFormWidgetRunService.selectList(fidWhere);
                    Map<String, String> fidMap = new HashMap<String, String>();
                    String wideID = "";
                    if (widetList != null && widetList.size() > 0) {
                        for (ActAljoinFormWidgetRun actAljoinFormWidget : widetList) {
                            String fMap = "";
                            if (fidMap.containsKey(actAljoinFormWidget.getWidgetName())) {
                                fMap = fidMap.get(actAljoinFormWidget.getWidgetName());
                            }
                            String tmpStr = actAljoinFormWidget.getWidgetId();
                            if (tmpStr.indexOf(",") > -1) {
                                tmpStr.substring(0, tmpStr.length() - 1);
                            }
                            fMap += actAljoinFormWidget.getWidgetId() + ",";
                            wideID += fMap;
                            fidMap.put(actAljoinFormWidget.getWidgetName(), fMap);
                        }
                    }
                    widetList.clear();
                    Where<ActAljoinFormDataHis> hisDataWhere = new Where<ActAljoinFormDataHis>();
                    if (inList.size() > 1000) {
                        int inNo = 0;
                        List<String> inLists = new ArrayList<String>();
                        for (String string : inList) {
                            inNo += 1;
                            inLists.add(string);
                            if (inNo == 1000) {
                                hisDataWhere.in("proc_inst_id", inLists).or();
                                inNo = 0;
                                inLists.clear();
                            }
                        }
                        if (inNo == 0) {
                            hisDataWhere.eq("is_delete", 0).andNew();
                        } else {
                            hisDataWhere.in("proc_inst_id", inLists).andNew();
                        }
                    } else {
                        hisDataWhere.in("proc_inst_id", inList.toArray());
                    }
                    hisDataWhere.in("bpmn_id", proIds.toArray());
                    if (wideID != null) {
                        wideID = wideID.substring(0, wideID.length() - 1);
                        hisDataWhere.in("form_widget_id", wideID.toString());
                    } else {
                        hisDataWhere.in("form_widget_id", "111111111111111111111111");
                    }
                    hisDataWhere.setSqlSelect("form_widget_name,form_widget_value,proc_inst_id,form_widget_id");
                    List<ActAljoinFormDataHis> hisDataList = actAljoinFormDataHisService.selectList(hisDataWhere);

                    /*
                     * Where<ActAljoinFormDataRun> hisDataWhere = new
                     * Where<ActAljoinFormDataRun>();
                     * hisDataWhere.in("proc_inst_id", inList.toArray());
                     * hisDataWhere.in("bpmn_id", proIds.toArray()); if (wideID
                     * != null) { hisDataWhere.in("form_widget_id", wideID); }
                     * hisDataWhere.setSqlSelect(
                     * "form_widget_name,form_widget_value,proc_inst_id,form_widget_id"
                     * ); List<ActAljoinFormDataRun> hisDataList =
                     * actAljoinFormDataRunService.selectList(hisDataWhere);
                     */
                    Map<String, ActHolidayListVO> hisDateMap = new HashMap<String, ActHolidayListVO>();
                    // inList.clear();
                    if (hisDataList != null && hisDataList.size() > 0) {
                        for (ActAljoinFormDataHis actAljoinFormDataHis : hisDataList) {
                            // for (ActAljoinFormDataRun actAljoinFormDataHis :
                            // hisDataList) {
                            ActHolidayListVO tmpvo = new ActHolidayListVO();
                            if (fidMap.size() > 0) {
                                String fday = fidMap.get("请假天数");
                                String ftime = fidMap.get("请假时间段");
                                String ftype = fidMap.get("请假类型");
                                String wid = actAljoinFormDataHis.getFormWidgetId();
                                if (hisDateMap.containsKey(actAljoinFormDataHis.getProcInstId())) {
                                    tmpvo = hisDateMap.get(actAljoinFormDataHis.getProcInstId());
                                }
                                if (fday != null && fday.indexOf(wid) > -1) {
                                    tmpvo.setNo(actAljoinFormDataHis.getFormWidgetValue());
                                }
                                if (ftime != null && ftime.indexOf(wid) > -1) {
                                    Base64 decoder = new Base64();
                                    String tmpStr = actAljoinFormDataHis.getFormWidgetValue();
                                    if (actAljoinFormDataHis.getFormWidgetValue() != null) {
                                        String tmpSt[] = tmpStr.split(",");
                                        String tmpTime = new String(decoder.decode(tmpSt[0]), "UTF-8") + ","
                                            + new String(decoder.decode(tmpSt[1]), "UTF-8");
                                        tmpvo.setUserName(tmpTime);
                                    }
                                }
                                if (ftype != null && ftype.indexOf(wid) > -1) {
                                    tmpvo.setJobsName(actAljoinFormDataHis.getFormWidgetValue());
                                }
                                inSet.add(actAljoinFormDataHis.getProcInstId());
                                /// inList.add(actAljoinFormDataHis.getProcInstId());
                                hisDateMap.put(actAljoinFormDataHis.getProcInstId(), tmpvo);
                            }
                        }
                    }
                    hisDataList.clear();
                    fidMap.clear();

                    /*
                     * HistoricTaskInstanceQuery hisQuery =
                     * historyService.createHistoricTaskInstanceQuery()
                     * .taskDeleteReason("completed");
                     */
                    HistoricProcessInstanceQuery hisQuerys
                        = historyService.createHistoricProcessInstanceQuery().finished();
                    if (inSet != null && inSet.size() > 0) {
                        hisQuerys.processInstanceIds(inSet);
                        List<HistoricProcessInstance> hisList = hisQuerys.list();
                        if (hisList != null && hisList.size() > 0) {
                            String prid = "";
                            for (HistoricProcessInstance historicTaskInstance : hisList) {
                                if (prid.indexOf(historicTaskInstance.getId()) > -1) {
                                    // 预防重复
                                } else {
                                    prid += historicTaskInstance.getId() + ",";
                                    ActHolidayListVO tmp = new ActHolidayListVO();
                                    ActHolidayListVO tmpvo = new ActHolidayListVO();
                                    if (hisDateMap.containsKey(historicTaskInstance.getId())) {
                                        tmp = hisDateMap.get(historicTaskInstance.getId());
                                        if (uMap.containsKey(historicTaskInstance.getStartUserId())) {
                                            tmpvo = uMap.get(historicTaskInstance.getStartUserId());
                                        } else {
                                            continue;
                                        }
                                        String type = tmp.getJobsName();// 类型
                                        String typeDay = tmp.getNo();// 天数
                                        String typeDate = tmp.getUserName();// 时间区域
                                        String str[];
                                        if (typeDate != null && typeDate.indexOf(",") > -1) {
                                            str = typeDate.split(",");
                                        } else {
                                            continue;
                                        }
                                        if (typeDay == null || "".equals(typeDay)) {
                                            continue;
                                        }
                                        if (type == null || "".equals(type)) {
                                            continue;
                                        }
                                        SimpleDateFormat dfd = new SimpleDateFormat("yyyy-MM-dd");
                                        Date stime = dfd.parse(str[0]);
                                        Date etime = dfd.parse(str[1]);
                                        endTime = df.parse(df.format(endTime) + " 23:59:59");
                                        System.out.println(stime.getTime() >= startTime.getTime());
                                        System.out.println(etime.getTime() >= startTime.getTime());
                                        boolean isq = false;
                                        if (etime.getTime() >= startTime.getTime()
                                            && stime.getTime() <= endTime.getTime()) {
                                            isq = true;
                                        }
                                        if (stime.getTime() >= startTime.getTime()
                                            && stime.getTime() <= endTime.getTime()) {
                                            isq = true;
                                        }
                                        if (isq && !"".equals(typeDay)) {
                                            if (type.equals("事假")) {
                                                Double sumNO = tmpvo.getThingsLeave();// 事假
                                                sumNO += Double.valueOf(typeDay);
                                                tmpvo.setThingsLeave(sumNO);
                                            }
                                            if (type.equals("病假")) {
                                                Double sumNO = tmpvo.getDiseaseLeave();// 病假
                                                sumNO += Double.valueOf(typeDay);
                                                tmpvo.setDiseaseLeave(sumNO);
                                            }
                                            if (type.equals("探亲假")) {
                                                Double sumNO = tmpvo.getAnnualLeave();// 年休假
                                                sumNO += Double.valueOf(typeDay);
                                                tmpvo.setAnnualLeave(sumNO);
                                            }
                                            if (type.equals("婚假")) {
                                                Double sumNO = tmpvo.getMarriageLeave();// 婚假
                                                sumNO += Double.valueOf(typeDay);
                                                tmpvo.setMarriageLeave(sumNO);
                                            }
                                            if (type.equals("生育假")) {
                                                Double sumNO = tmpvo.getMaternityLeave();// 产假
                                                sumNO += Double.valueOf(typeDay);
                                                tmpvo.setMaternityLeave(sumNO);
                                            }
                                            if (type.equals("公休假")) {
                                                Double sumNO = tmpvo.getAllocatedLeave();// 公假
                                                sumNO += Double.valueOf(typeDay);
                                                tmpvo.setAllocatedLeave(sumNO);
                                            }
                                            if (type.equals("丧假")) {
                                                Double sumNO = tmpvo.getDieLeave();// 喪假
                                                sumNO += Double.valueOf(typeDay);
                                                tmpvo.setDieLeave(sumNO);
                                            }
                                            if (type.equals("其他假")) {
                                                Double sumNO = tmpvo.getOtherLeave();
                                                sumNO += Double.valueOf(typeDay);
                                                tmpvo.setOtherLeave(sumNO);
                                            }
                                            uMap.put(historicTaskInstance.getStartUserId(), tmpvo);
                                        }
                                    }
                                }
                            }
                        }
                    } /*
                       * if (inList != null && inList.size() > 0) { hisQuery =
                       * hisQuery.processInstanceIdIn(inList); }
                       * List<HistoricTaskInstance> hisList = hisQuery.list();
                       * if (hisList != null && hisList.size() > 0) { String
                       * prid = ""; // Map<String, ActHolidayListVO> hisVOMap
                       * = new // HashMap<String, ActHolidayListVO>(); for
                       * (HistoricTaskInstance historicTaskInstance : hisList)
                       * { if (prid.indexOf(historicTaskInstance.
                       * getProcessInstanceId()) > -1) { // 预防重复 } else { prid
                       * += historicTaskInstance.getProcessInstanceId() + ",";
                       * ActHolidayListVO tmpvo = new ActHolidayListVO();
                       * String user =
                       * hisMap.get(historicTaskInstance.getProcessInstanceId(
                       * )); if (user != null && uMap.containsKey(user)) {
                       * tmpvo = uMap.get(user); ActHolidayListVO tmp = new
                       * ActHolidayListVO(); if
                       * (hisDateMap.containsKey(historicTaskInstance.
                       * getProcessInstanceId())) { tmp =
                       * hisDateMap.get(historicTaskInstance.
                       * getProcessInstanceId()); String type =
                       * tmp.getJobsName();// 类型 String typeDay =
                       * tmp.getNo();// 天数
                       * 
                       * String typeDate = tmp.getUserName();// 时间区域 String
                       * str[] = typeDate.split(","); df = new
                       * SimpleDateFormat("yyyy-MM-dd hh:mm"); Date stime =
                       * df.parse(str[0]); Date etime = df.parse(str[1]); if
                       * (((stime.getTime() >= startTime.getTime() &&
                       * stime.getTime() < endTime.getTime()) ||
                       * (etime.getTime() < endTime.getTime())&&
                       * etime.getTime()>=startTime.getTime()) && typeDay !=
                       * null && !"".equals(typeDay)) { if (type.equals("事假"))
                       * { Double sumNO = tmpvo.getThingsLeave();// 事假 sumNO
                       * += Double.valueOf(typeDay);
                       * tmpvo.setThingsLeave(sumNO); } if (type.equals("病假"))
                       * { Double sumNO = tmpvo.getDiseaseLeave();// 病假 sumNO
                       * += Double.valueOf(typeDay);
                       * tmpvo.setDiseaseLeave(sumNO); } if
                       * (type.equals("探亲假")) { Double sumNO =
                       * tmpvo.getAnnualLeave();// 年休假 sumNO +=
                       * Double.valueOf(typeDay); tmpvo.setAnnualLeave(sumNO);
                       * } if (type.equals("婚假")) { Double sumNO =
                       * tmpvo.getMarriageLeave();// 婚假 sumNO +=
                       * Double.valueOf(typeDay);
                       * tmpvo.setMarriageLeave(sumNO); } if
                       * (type.equals("生育假")) { Double sumNO =
                       * tmpvo.getMaternityLeave();// 产假 sumNO +=
                       * Double.valueOf(typeDay);
                       * tmpvo.setMaternityLeave(sumNO); } if
                       * (type.equals("公休假")) { Double sumNO =
                       * tmpvo.getAllocatedLeave();// 公假 sumNO +=
                       * Double.valueOf(typeDay);
                       * tmpvo.setAllocatedLeave(sumNO); } if
                       * (type.equals("丧假")) { Double sumNO =
                       * tmpvo.getDieLeave();// 喪假 sumNO +=
                       * Double.valueOf(typeDay); tmpvo.setDieLeave(sumNO); }
                       * if (type.equals("其他假")) { Double sumNO =
                       * tmpvo.getOtherLeave();// 喪假 sumNO +=
                       * Double.valueOf(typeDay); tmpvo.setOtherLeave(sumNO);
                       * } uMap.put(user, tmpvo); } } } } }
                       * 
                       * }
                       */
                    // hisDataList.clear();
                }

            }
        }
        if (uMap != null && uMap.size() > 0) {
            for (ActHolidayListVO v : uMap.values()) {
                holiList.add(v);
            }
        }
        return holiList;
    }

    @Override
    public ActHolidayListVO getHoliday(ActHolidayVO obj) throws Exception {
        Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
        ActHolidayListVO tmpvo = new ActHolidayListVO();
        Double init = new Double(0);
        tmpvo.setAllocatedLeave(init);
        tmpvo.setAnnualLeave(init);
        tmpvo.setDieLeave(init);
        tmpvo.setDiseaseLeave(init);
        tmpvo.setMarriageLeave(init);
        tmpvo.setMaternityLeave(init);
        tmpvo.setThingsLeave(init);
        tmpvo.setOtherLeave(init);
        Date startTime = null;
        Date endTime = null;
        String weets = obj.getWeeks();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        if ((obj.getStartTimeholi() != "" && "".equals(obj.getStartTimeholi()))
            || (obj.getEndTimeholi() != null && !"".equals(obj.getEndTimeholi()))) {
            startTime = df.parse(obj.getStartTimeholi());
            endTime = df.parse(obj.getEndTimeholi());

        } else {
            if ("0".equals(weets)) {
                // 30天
                Date newDate = new Date();
                cal.setTime(df.parse(df.format(newDate)));
                endTime = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -30);
                startTime = cal.getTime();

            }
            if ("1".equals(weets)) {
                // 本周
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
                startTime = cal.getTime();
                cal.setTime(df.parse(df.format(startTime)));
                startTime = cal.getTime();
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                endTime = cal.getTime();
            }
            if ("2".equals(weets)) {
                // 本月
                String newDate = df.format(new Date());
                String[] newTime = newDate.split("-");
                newDate = newTime[0] + "-" + newTime[1] + "-01";
                cal.setTime(df.parse(newDate));
                // cal.set(Calendar.DAY_OF_MONTH, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.add(Calendar.SECOND, 0);
                startTime = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                endTime = cal.getTime();
            }
        }
        String showDate = df.format(startTime) + "至" + df.format(endTime);
        configWhere.eq("is_active", 1);
        configWhere.where("process_code like {0}", WebConstant.RETMSG_OPERATION_LEAVE + "%");
        configWhere.setSqlSelect("process_id");
        List<ActAljoinFixedConfig> configList = actAljoinFixedConfigService.selectList(configWhere);
        List<String> proIds = new ArrayList<String>(); // 查询是否存在请假流程
        if (configList != null && configList.size() > 0) {
            for (ActAljoinFixedConfig actAljoinFixedConfig : configList) {
                proIds.add(actAljoinFixedConfig.getProcessId());
            }
            Where<ActAljoinBpmn> bWhere = new Where<ActAljoinBpmn>();
            bWhere.in("process_id", proIds);
            bWhere.eq("is_active", 1);
            List<ActAljoinBpmn> blist = actAljoinBpmnService.selectList(bWhere);
            proIds.clear();
            if (blist != null && blist.size() > 0) {
                for (ActAljoinBpmn actAljoinBpmn : blist) {
                    proIds.add(actAljoinBpmn.getId().toString());
                }
            }
            // ----------------------------------------------------------------------------------

            Where<ActAljoinQueryHis> hisWhere = new Where<ActAljoinQueryHis>();
            hisWhere.between("create_time", startTime, new Date());
            hisWhere.setSqlSelect("process_instance_id,create_user_id");
            List<ActAljoinQueryHis> hisQueryList = actAljoinQueryHisService.selectList(hisWhere);
            List<String> inList = new ArrayList<String>();
            Set<String> inSet = new HashSet<String>();
            if (hisQueryList != null && hisQueryList.size() > 0) {
                for (ActAljoinQueryHis his : hisQueryList) {
                    inList.add(his.getProcessInstanceId());
                }
                hisQueryList.clear();
                Where<ActAljoinFormWidgetRun> fidWhere = new Where<ActAljoinFormWidgetRun>();
                List<String> ll = new ArrayList<String>();
                ll.add("select");
                ll.add("datetime_period");
                ll.add("decimal");
                fidWhere.in("widget_type", ll);
                fidWhere.in("widget_name", "请假天数,请假时间段,请假类型");
                // fidWhere.eq("widget_type", "datetime_period");
                fidWhere.setSqlSelect("widget_name,form_id,widget_id");
                // fidWhere.groupBy("widget_id");
                List<ActAljoinFormWidgetRun> widetList = actAljoinFormWidgetRunService.selectList(fidWhere);
                Map<String, String> fidMap = new HashMap<String, String>();
                String wideID = "";
                if (widetList != null && widetList.size() > 0) {
                    for (ActAljoinFormWidgetRun actAljoinFormWidget : widetList) {
                        String fMap = "";
                        if (fidMap.containsKey(actAljoinFormWidget.getWidgetName())) {
                            fMap = fidMap.get(actAljoinFormWidget.getWidgetName());
                        }
                        String tmpStr = actAljoinFormWidget.getWidgetId();
                        if (tmpStr.indexOf(",") > -1) {
                            tmpStr.substring(0, tmpStr.length() - 1);
                        }
                        fMap += actAljoinFormWidget.getWidgetId() + ",";
                        wideID += fMap;
                        fidMap.put(actAljoinFormWidget.getWidgetName(), fMap);
                    }
                }
                widetList.clear();

                Where<ActAljoinFormDataHis> hisDataWhere = new Where<ActAljoinFormDataHis>();
                // hisDataWhere.in("proc_inst_id", inList.toArray());
                if (inList.size() > 1000) {
                    int inNo = 0;
                    List<String> inLists = new ArrayList<String>();
                    for (String string : inList) {
                        inNo += 1;
                        inLists.add(string);
                        if (inNo == 1000) {
                            hisDataWhere.in("proc_inst_id", inLists).or();
                            inNo = 0;
                            inLists.clear();
                        }
                    }
                    if (inNo == 0) {
                        hisDataWhere.eq("is_delete", 0).andNew();
                    } else {
                        hisDataWhere.in("proc_inst_id", inLists).andNew();
                    }
                } else {
                    hisDataWhere.in("proc_inst_id", inList.toArray());
                }
                hisDataWhere.in("bpmn_id", proIds.toArray());
                if (wideID != null && !"".equals(wideID)) {
                    wideID = wideID.substring(0, wideID.length() - 1);
                    /*
                     * if(wideID.indexOf(",")>-1){ hisDataWhere.andNew(); String
                     * arrWide[]=wideID.split(","); for (int i = 0; i <
                     * arrWide.length; i++) { int s=i+1; if(s==arrWide.length){
                     * hisDataWhere.like("widget_id", arrWide[i]); }else{
                     * hisDataWhere.like("widget_id", arrWide[i]).or(); } } //
                     * }else{ hisDataWhere.like("widget_id", wideID);
                     * 
                     * }
                     */
                    hisDataWhere.in("form_widget_id", wideID);
                } else {
                    hisDataWhere.in("form_widget_id", "111111111111111111111111");
                }
                hisDataWhere.setSqlSelect("form_widget_name,form_widget_value,proc_inst_id,form_widget_id");
                List<ActAljoinFormDataHis> hisDataList = actAljoinFormDataHisService.selectList(hisDataWhere);
                Map<String, ActHolidayListVO> hisDateMap = new HashMap<String, ActHolidayListVO>();
                inList.clear();
                if (hisDataList != null && hisDataList.size() > 0) {
                    for (ActAljoinFormDataHis actAljoinFormDataHis : hisDataList) {
                        // for (ActAljoinFormDataRun actAljoinFormDataHis :
                        // hisDataList) {
                        ActHolidayListVO tmpsvo = new ActHolidayListVO();
                        if (fidMap.size() > 0) {
                            String fday = fidMap.get("请假天数");
                            String ftime = fidMap.get("请假时间段");
                            String ftype = fidMap.get("请假类型");
                            String wid = actAljoinFormDataHis.getFormWidgetId();
                            if (hisDateMap.containsKey(actAljoinFormDataHis.getProcInstId())) {
                                tmpsvo = hisDateMap.get(actAljoinFormDataHis.getProcInstId());
                            }
                            if (fday != null && fday.indexOf(wid) > -1) {
                                tmpsvo.setNo(actAljoinFormDataHis.getFormWidgetValue());
                            }
                            if (ftime != null && ftime.indexOf(wid) > -1) {
                                Base64 decoder = new Base64();
                                String tmpStr = actAljoinFormDataHis.getFormWidgetValue();
                                if (actAljoinFormDataHis.getFormWidgetValue() != null) {
                                    String tmpSt[] = tmpStr.split(",");
                                    String tmpTime = new String(decoder.decode(tmpSt[0]), "UTF-8") + ","
                                        + new String(decoder.decode(tmpSt[1]), "UTF-8");
                                    tmpsvo.setUserName(tmpTime);
                                }
                            }
                            if (ftype != null && ftype.indexOf(wid) > -1) {
                                tmpsvo.setJobsName(actAljoinFormDataHis.getFormWidgetValue());
                            }
                            inSet.add(actAljoinFormDataHis.getProcInstId());
                            inList.add(actAljoinFormDataHis.getProcInstId());
                            hisDateMap.put(actAljoinFormDataHis.getProcInstId(), tmpsvo);
                        }
                    }
                }
                hisDataList.clear();
                fidMap.clear();
                /*
                 * HistoricTaskInstanceQuery hisQuery =
                 * historyService.createHistoricTaskInstanceQuery()
                 * .taskDeleteReason("completed");
                 */
                HistoricProcessInstanceQuery hisQuerys = historyService.createHistoricProcessInstanceQuery().finished();
                if (inSet != null && inSet.size() > 0) {
                    hisQuerys.processInstanceIds(inSet);
                    List<HistoricProcessInstance> hisList = hisQuerys.list();
                    if (hisList != null && hisList.size() > 0) {
                        String prid = "";
                        for (HistoricProcessInstance historicTaskInstance : hisList) {

                            if (prid.indexOf(historicTaskInstance.getId()) > -1) {
                                // 预防重复
                            } else {
                                prid += historicTaskInstance.getId() + ",";
                                ActHolidayListVO tmp = new ActHolidayListVO();
                                if (hisDateMap.containsKey(historicTaskInstance.getId())) {
                                    tmp = hisDateMap.get(historicTaskInstance.getId());
                                    String type = tmp.getJobsName();// 类型
                                    String typeDay = tmp.getNo();// 天数

                                    String typeDate = tmp.getUserName();// 时间区域
                                    String str[];
                                    if (typeDate != null && typeDate.indexOf(",") > -1) {
                                        str = typeDate.split(",");
                                    } else {
                                        continue;
                                    }
                                    if (typeDay == null || "".equals(typeDay)) {
                                        continue;
                                    }
                                    if (type == null || "".equals(type)) {
                                        continue;
                                    }
                                    df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                    Date stime = df.parse(str[0] + " 00:00:00");
                                    Date etime = df.parse(str[1] + " 00:00:00");
                                    endTime = df.parse(df.format(endTime) + " 23:59:59");
                                    if (stime.getTime() >= startTime.getTime() && etime.getTime() >= startTime.getTime()
                                        && !"".equals(typeDay)) {

                                        if (type.equals("事假")) {
                                            Double sumNO = tmpvo.getThingsLeave();// 事假
                                            sumNO += Double.valueOf(typeDay);
                                            tmpvo.setThingsLeave(sumNO);
                                        }
                                        if (type.equals("病假")) {
                                            Double sumNO = tmpvo.getDiseaseLeave();// 病假
                                            sumNO += Double.valueOf(typeDay);
                                            tmpvo.setDiseaseLeave(sumNO);
                                        }
                                        if (type.equals("探亲假")) {
                                            Double sumNO = tmpvo.getAnnualLeave();// 年休假
                                            sumNO += Double.valueOf(typeDay);
                                            tmpvo.setAnnualLeave(sumNO);
                                        }
                                        if (type.equals("婚假")) {
                                            Double sumNO = tmpvo.getMarriageLeave();// 婚假
                                            sumNO += Double.valueOf(typeDay);
                                            tmpvo.setMarriageLeave(sumNO);
                                        }
                                        if (type.equals("生育假")) {
                                            Double sumNO = tmpvo.getMaternityLeave();// 产假
                                            sumNO += Double.valueOf(typeDay);
                                            tmpvo.setMaternityLeave(sumNO);
                                        }
                                        if (type.equals("公休假")) {
                                            Double sumNO = tmpvo.getAllocatedLeave();// 公假
                                            sumNO += Double.valueOf(typeDay);
                                            tmpvo.setAllocatedLeave(sumNO);
                                        }
                                        if (type.equals("丧假")) {
                                            Double sumNO = tmpvo.getDieLeave();// 喪假
                                            sumNO += Double.valueOf(typeDay);
                                            tmpvo.setDieLeave(sumNO);
                                        }
                                        if (type.equals("其他假")) {
                                            Double sumNO = tmpvo.getOtherLeave();
                                            sumNO += Double.valueOf(typeDay);
                                            tmpvo.setOtherLeave(sumNO);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                /*
                 * if (inList != null && inList.size() > 0) { hisQuery =
                 * hisQuery.processInstanceIdIn(inList); }
                 * List<HistoricTaskInstance> hisList = hisQuery.list();
                 * 
                 * if (hisList != null && hisList.size() > 0) { String prid =
                 * ""; // Map<String, ActHolidayListVO> hisVOMap = new //
                 * HashMap<String, ActHolidayListVO>(); for
                 * (HistoricTaskInstance historicTaskInstance : hisList) { if
                 * (prid.indexOf(historicTaskInstance.getProcessInstanceId()) >
                 * -1) { // 预防重复 } else { prid +=
                 * historicTaskInstance.getProcessInstanceId() + ",";
                 * ActHolidayListVO tmp = new ActHolidayListVO(); if
                 * (hisDateMap.containsKey(historicTaskInstance.
                 * getProcessInstanceId())) { tmp =
                 * hisDateMap.get(historicTaskInstance.getProcessInstanceId());
                 * String type = tmp.getJobsName();// 类型 String typeDay =
                 * tmp.getNo();// 天数
                 * 
                 * String typeDate = tmp.getUserName();// 时间区域 String str[] =
                 * typeDate.split(","); df = new SimpleDateFormat(
                 * "yyyy-MM-dd hh:mm"); Date stime = df.parse(str[0]+" 00:00");
                 * Date etime = df.parse(str[1] +" 00:00"); if
                 * (((stime.getTime() >= startTime.getTime()&&
                 * stime.getTime()<endTime.getTime()) || (etime.getTime() >=
                 * startTime.getTime() && etime.getTime() < endTime.getTime()))
                 * && typeDay != null && !"".equals(typeDay)) { if
                 * (type.equals("事假")) { Double sumNO =
                 * tmpvo.getThingsLeave();// 事假 sumNO +=
                 * Double.valueOf(typeDay); tmpvo.setThingsLeave(sumNO); } if
                 * (type.equals("病假")) { Double sumNO =
                 * tmpvo.getDiseaseLeave();// 病假 sumNO +=
                 * Double.valueOf(typeDay); tmpvo.setDiseaseLeave(sumNO); } if
                 * (type.equals("探亲假")) { Double sumNO =
                 * tmpvo.getAnnualLeave();// 年休假 sumNO +=
                 * Double.valueOf(typeDay); tmpvo.setAnnualLeave(sumNO); } if
                 * (type.equals("婚假")) { Double sumNO =
                 * tmpvo.getMarriageLeave();// 婚假 sumNO +=
                 * Double.valueOf(typeDay); tmpvo.setMarriageLeave(sumNO); } if
                 * (type.equals("生育假")) { Double sumNO =
                 * tmpvo.getMaternityLeave();// 产假 sumNO +=
                 * Double.valueOf(typeDay); tmpvo.setMaternityLeave(sumNO); } if
                 * (type.equals("公休假")) { Double sumNO =
                 * tmpvo.getAllocatedLeave();// 公假 sumNO +=
                 * Double.valueOf(typeDay); tmpvo.setAllocatedLeave(sumNO); } if
                 * (type.equals("丧假")) { Double sumNO = tmpvo.getDieLeave();//
                 * 喪假 sumNO += Double.valueOf(typeDay);
                 * tmpvo.setDieLeave(sumNO); } if (type.equals("其他假")) { Double
                 * sumNO = tmpvo.getOtherLeave(); sumNO +=
                 * Double.valueOf(typeDay); tmpvo.setOtherLeave(sumNO); } }
                 * 
                 * } } }
                 * 
                 * }
                 */
            }
        }
        tmpvo.setShowDate(showDate);
        return tmpvo;
    }

    @Override
    public List<ActWorkingVO> getWorkingList(ActWorkingListVO obj) throws Exception {
        // TODO Auto-generated method stub
        List<ActWorkingVO> workingList = new ArrayList<ActWorkingVO>();
        Date startTime = null;
        Date endTime = null;
        String weets = obj.getWeeks();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if ((obj.getTime1() != "" && "".equals(obj.getTime1()))
            || (obj.getTime2() != null && !"".equals(obj.getTime2()))) {
            startTime = df.parse(obj.getTime1());
            endTime = dfs.parse(obj.getTime2() + " 23:59:59");
        } else {
            if ("0".equals(weets)) {
                // 30天
                Date newDate = new Date();
                cal.setTime(df.parse(df.format(newDate)));
                endTime = cal.getTime();
                String tmpend = df.format(endTime) + " 23:59:59";
                endTime = dfs.parse(tmpend);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -30);
                startTime = cal.getTime();

            }
            if ("1".equals(weets)) {
                // 本周
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
                startTime = cal.getTime();
                cal.setTime(df.parse(df.format(startTime)));
                startTime = cal.getTime();
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                endTime = cal.getTime();
                String tmpend = df.format(endTime) + " 23:59:59";
                endTime = dfs.parse(tmpend);
            }
            if ("2".equals(weets)) {
                // 本月
                String newDate = df.format(new Date());
                String[] newTime = newDate.split("-");
                newDate = newTime[0] + "-" + newTime[1] + "-01";
                cal.setTime(df.parse(newDate));
                // cal.set(Calendar.DAY_OF_MONTH, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.add(Calendar.SECOND, 0);
                startTime = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                endTime = cal.getTime();
                String tmpend = df.format(endTime) + " 23:59:59";
                endTime = dfs.parse(tmpend);
            }
        }
        /*
         * cal.setTime(endTime); cal.add(Calendar.DAY_OF_MONTH, -1);
         */
        String showDate = df.format(startTime) + "至" + df.format(endTime.getTime());
        // ------------------------------------
        // 获取流程分类
        List<String> cateIds = new ArrayList<String>();
        if (obj.getDeptId() != null && !"".equals(obj.getDeptId())) {
            cateIds.add(obj.getDeptId());
            List<ActAljoinCategory> cateList = actAljoinCategoryService.getAllChildList(Long.valueOf(obj.getDeptId()));
            if (cateList != null && cateList.size() > 0) {
                for (ActAljoinCategory actAljoinCategory : cateList) {
                    cateIds.add(actAljoinCategory.getId().toString());
                }
            }
        }
        Where<ActAljoinBpmn> abWhere = new Where<ActAljoinBpmn>();
        abWhere.eq("is_active", 1);
        if (obj.getDeptId() != null && !"".equals(obj.getDeptId())) {
            abWhere.in("category_id", cateIds);
        }
        if (obj.getBpmnName() != null && !"".equals(obj.getBpmnName())) {
            abWhere.like("process_name", obj.getBpmnName());
        }
        List<ActAljoinBpmn> abList = actAljoinBpmnService.selectList(abWhere);
        Map<String, ActWorkingVO> tmpMap = new HashMap<String, ActWorkingVO>();
        if (abList != null && abList.size() > 0) {
            for (ActAljoinBpmn actAljoinBpmn : abList) {
                ActWorkingVO vo = new ActWorkingVO();
                vo.setBpmnName(actAljoinBpmn.getProcessName());
                vo.setBpmnNums(0);
                tmpMap.put(actAljoinBpmn.getProcessName(), vo);
            }
            abList.clear();
        }
        // ------------------------------------------------------------
        Where<ActAljoinQueryHis> hisWhere = new Where<ActAljoinQueryHis>();
        hisWhere.eq("is_delete", 0);
        if (obj.getBpmnName() != null && !"".equals(obj.getBpmnName())) {
            hisWhere.like("process_name", obj.getBpmnName());
        }
        if (obj.getDeptId() != null && !"".equals(obj.getDeptId())) {
            String sql = "";
            for (int i = 0; i < cateIds.size(); i++) {
                int s = i + 1;
                if (s == cateIds.size()) {
                    sql += "process_category_ids like '%" + cateIds.get(i) + "%'";

                } else {
                    sql += "process_category_ids like '%" + cateIds.get(i) + "%' or ";
                }
            }
            hisWhere.andNew(sql);
            hisWhere.and();
            /* hisWhere.where(sql, cateIds); */
        }
        hisWhere.ge("create_time", startTime);
        hisWhere.lt("create_time", endTime);
        hisWhere.setSqlSelect("process_instance_id,process_name");
        List<ActAljoinQueryHis> actAljoinQueryHisList = actAljoinQueryHisService.selectList(hisWhere);
        if (actAljoinQueryHisList != null && actAljoinQueryHisList.size() > 0) {
            /*List<String> instaceIds = new ArrayList<String>();
            for (ActAljoinQueryHis his : actAljoinQueryHisList) {
            	instaceIds.add(his.getProcessInstanceId());
            }*/
            /*if (instaceIds != null && instaceIds.size() > 0) {*/
            List<HistoricActivityInstance> activityList
                = historyService.createHistoricActivityInstanceQuery().activityType("startEvent").list();
            for (int hi = 0; hi < activityList.size(); hi++) {
                HistoricActivityInstance historicActivityInstance = activityList.get(hi);
                if (historicActivityInstance.getEndTime() != null) {
                    if (startTime.getTime() < historicActivityInstance.getEndTime().getTime()
                        && endTime.getTime() > historicActivityInstance.getEndTime().getTime()) {
                        for (int i = 0; i < actAljoinQueryHisList.size(); i++) {
                            ActWorkingVO vo = new ActWorkingVO();
                            String tmpA = actAljoinQueryHisList.get(i).getProcessInstanceId();
                            String tmpB = historicActivityInstance.getProcessInstanceId();
                            if (tmpA.equals(tmpB)) {
                                String keyName = actAljoinQueryHisList.get(i).getProcessName();
                                ActWorkingVO tmpVO = tmpMap.get(keyName);
                                if (tmpVO != null) {
                                    int sum = tmpVO.getBpmnNums();
                                    tmpVO.setBpmnNums(sum + 1);
                                    tmpMap.put(actAljoinQueryHisList.get(i).getProcessName(), tmpVO);
                                } else {
                                    vo.setBpmnName(actAljoinQueryHisList.get(i).getProcessName());
                                    vo.setBpmnNums(1);
                                    tmpMap.put(actAljoinQueryHisList.get(i).getProcessName(), vo);
                                }
                                actAljoinQueryHisList.remove(i);
                                i--;
                            }
                        }
                    } else {
                        activityList.remove(hi);
                        hi--;
                    }
                } else {
                    activityList.remove(hi);
                    hi--;
                }

            }

            /*	}*/

        }
        if (tmpMap != null && tmpMap.size() > 0) {
            for (ActWorkingVO v : tmpMap.values()) {
                v.setShowDate(showDate);
                workingList.add(v);
            }
        }
        return workingList;
    }

    @Override
    public List<ActWorkingVO> getWorking(ActWorkingListVO obj) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ActApprovalVO> getApproval(ActWorkingListVO obj) throws Exception {
        // TODO Auto-generated method stub
        List<ActApprovalVO> voList = new ArrayList<ActApprovalVO>();
        List<AutUser> userList = new ArrayList<AutUser>();
        List<AutDepartment> deptList = new ArrayList<AutDepartment>();
        List<AutDepartmentUser> duserlist = new ArrayList<AutDepartmentUser>();
        // List<AutUserPosition> positionUserList = new
        // ArrayList<AutUserPosition>();
        Map<String, String> upMap = new HashMap<String, String>();
        Map<String, String> pnMap = new HashMap<String, String>();
        Map<String, ActApprovalVO> uDMap = new HashMap<String, ActApprovalVO>();
        Map<String, String> dMap = new HashMap<String, String>();
        String userids = "";
        String deptids = "";
        if (obj.getDeptId() != null && !"".equals(obj.getDeptId())) {
            AutDepartment autDepartment = autDepartmentService.selectById(Long.valueOf(obj.getDeptId()));
            Where<AutDepartmentUser> dUserWhere = new Where<AutDepartmentUser>();
            dUserWhere.where("dept_code like {0}", autDepartment.getDeptCode() + "%");
            dUserWhere.setSqlSelect("user_id,dept_id");
            duserlist = autDepartmentUserService.selectList(dUserWhere);
            if (duserlist != null && duserlist.size() > 0) {
                for (AutDepartmentUser autDepartmentUser : duserlist) {
                    userids += autDepartmentUser.getUserId() + ",";
                    deptids += autDepartmentUser.getDeptId() + ",";
                }
                if (userids.length() > 0) {
                    Where<AutUser> userWhere = new Where<AutUser>();
                    userWhere.in("id", userids);
                    userWhere.eq("is_active", 1);
                    userWhere.eq("is_account_expired", 0);
                    userWhere.eq("is_account_locked", 0);
                    userWhere.eq("is_credentials_expired", 0);
                    userWhere.setSqlSelect("full_name,id");
                    userList = autUserService.selectList(userWhere);
                    Where<AutDepartment> userPWhere = new Where<AutDepartment>();
                    userPWhere.in("id", deptids);
                    userPWhere.eq("is_active", 1);
                    userPWhere.setSqlSelect("id,dept_name");
                    deptList = autDepartmentService.selectList(userPWhere);
                }
            } else {
                return voList;
            }
        } else {
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.eq("is_active", 1);
            userWhere.eq("is_account_expired", 0);
            userWhere.eq("is_account_locked", 0);
            userWhere.eq("is_credentials_expired", 0);
            userWhere.setSqlSelect("full_name,id");
            userList = autUserService.selectList(userWhere);
            Where<AutDepartmentUser> dUserWhere = new Where<AutDepartmentUser>();
            dUserWhere.setSqlSelect("user_id,dept_id");
            duserlist = autDepartmentUserService.selectList(dUserWhere);

            Where<AutDepartment> userPWhere = new Where<AutDepartment>();
            userPWhere.eq("is_active", 1);
            userPWhere.setSqlSelect("id,dept_name");
            deptList = autDepartmentService.selectList(userPWhere);
            if (userList != null && userList.size() > 0) {
                for (AutUser autUser : userList) {
                    userids += autUser.getId() + ",";
                }
            } else {
                return voList;
            }
        }

        if (deptList != null && deptList.size() > 0) {
            for (AutDepartment autDepartment : deptList) {
                dMap.put(autDepartment.getId().toString(), autDepartment.getDeptName());
            }
        }
        deptList.clear();
        // ----------------------------------------------------------------------------------------------------------------------------
        Date startTime = null;
        Date endTime = null;
        String weets = obj.getWeeks();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if ((obj.getTime1() != "" && "".equals(obj.getTime1()))
            || (obj.getTime2() != null && !"".equals(obj.getTime2()))) {
            startTime = df.parse(obj.getTime1());
            endTime = df.parse(obj.getTime2());
            // cal.setTime(endTime);
            // cal.add(Calendar.DAY_OF_MONTH, 1);
            // endTime = cal.getTime();
        } else {
            if ("0".equals(weets)) {
                // 30天
                Date newDate = new Date();
                cal.setTime(df.parse(df.format(newDate)));
                endTime = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -30);
                startTime = cal.getTime();

            }
            if ("1".equals(weets)) {
                // 本周
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
                startTime = cal.getTime();
                cal.setTime(df.parse(df.format(startTime)));
                startTime = cal.getTime();
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                endTime = cal.getTime();
            }
            if ("2".equals(weets)) {
                // 本月
                String newDate = df.format(new Date());
                String[] newTime = newDate.split("-");
                newDate = newTime[0] + "-" + newTime[1] + "-01";
                cal.setTime(df.parse(newDate));
                // cal.set(Calendar.DAY_OF_MONTH, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.add(Calendar.SECOND, 0);
                startTime = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                endTime = cal.getTime();
            }
        }
        // setShowDate(df.format(startTime) + "至" + df.format(endTime));
        // cal.setTime(endTime);
        // cal.add(Calendar.DAY_OF_MONTH, -1);
        String showDate = df.format(startTime) + "至" + df.format(endTime.getTime());
        // -----------------------------------------------------------------
        Map<String, ActApprovalVO> returnMap = new HashMap<String, ActApprovalVO>();
        if (userList != null && userList.size() > 0) {
            for (AutUser autUser : userList) {
                int no = 1;
                ActApprovalVO vo = new ActApprovalVO();
                vo.setJobsName(upMap.get(autUser.getId().toString()));
                vo.setUserName(autUser.getFullName());
                vo.setNewNums(0);
                vo.setStayNums(0);
                vo.setHandleNums(0);
                vo.setNo(no);
                returnMap.put(autUser.getId().toString(), vo);
            }
        }
        upMap.clear();
        Where<ActAljoinQueryHis> hisWhere = new Where<ActAljoinQueryHis>();
        hisWhere.eq("is_delete", 0);
        hisWhere.ge("create_time", startTime);
        hisWhere.lt("create_time", DateUtil.str2dateOrTime(df.format(endTime) + " 23:59:59"));
        hisWhere.setSqlSelect("process_instance_id,create_user_id,start_task");
        List<ActAljoinQueryHis> actAljoinQueryHisList = actAljoinQueryHisService.selectList(hisWhere);
        String startTask = "";
        if (actAljoinQueryHisList != null && actAljoinQueryHisList.size() > 0) {
            // taskService.getIdentityLinksForTask(arg0)
            // String instaceIds = "";
            List<String> instaceIdsList = new ArrayList<String>();
            for (ActAljoinQueryHis actAljoinQueryHis : actAljoinQueryHisList) {
                instaceIdsList.add(actAljoinQueryHis.getProcessInstanceId());
                pnMap.put(actAljoinQueryHis.getProcessInstanceId().toString(),
                    actAljoinQueryHis.getCreateUserId().toString());
                startTask += actAljoinQueryHis.getStartTask() + ",";
            }
            if (instaceIdsList.size() > 0) {
                // instaceIds = instaceIds.substring(0, instaceIds.length() -
                // 1);
                /*
                 * List<HistoricActivityInstance> activityList =
                 * historyService.createNativeHistoricActivityInstanceQuery()
                 * .sql("select * from " +
                 * managementService.getTableName(HistoricActivityInstance.
                 * class) + "  H  where H.START_TIME_ >='" +
                 * df.format(startTime) + " 00:00:00" + "' and H.START_TIME_<'"
                 * + df.format(endTime) + " 23:59:59" +
                 * "' AND H.PROC_INST_ID_ in(" + instaceIds + ")") .list();
                 */
                Where<ActHiActinst> hiActWhere = new Where<ActHiActinst>();
                if (instaceIdsList.size() > 1000) {
                    int tmpIdsNo = 0;
                    List<String> instaceIds = new ArrayList<String>();
                    for (String string : instaceIdsList) {
                        tmpIdsNo += 1;
                        instaceIds.add(string);
                        if (tmpIdsNo == 1000) {
                            tmpIdsNo = 0;
                            hiActWhere.in("PROC_INST_ID_", instaceIds).or();
                        }
                    }
                    if (tmpIdsNo == 1000 || tmpIdsNo == 0) {
                        hiActWhere.eq("is_delete", 0).andNew();
                    } else {
                        hiActWhere.in("PROC_INST_ID_", instaceIds).andNew();
                    }
                } else {
                    hiActWhere.in("PROC_INST_ID_", instaceIdsList);
                }
                // hiActWhere.in("PROC_INST_ID_", instaceIds);
                // hiActWhere.setSqlSelect("ACT_TYPE_,TASK_ID_,PROC_INST_ID_");
                hiActWhere.between("START_TIME_", dfs.parse(df.format(startTime) + " 00:00:00"),
                    dfs.parse(df.format(endTime) + " 23:59:59"));
                List<ActHiActinst> activityList = actHiActinstService.selectList(hiActWhere);
                for (int hi = 0; hi < activityList.size(); hi++) {
                    ActHiActinst historicActivityInstance = activityList.get(hi);
                    String type = historicActivityInstance.getActType();
                    String taskId = historicActivityInstance.getTaskId();
                    if ("startEvent".equals(type)) {
                        /*
                         * String inds =
                         * historicActivityInstance.getProcessInstanceId(); if
                         * (pnMap.containsKey(inds)) { String userId =
                         * pnMap.get(inds); if (returnMap.containsKey(userId)) {
                         * ActApprovalVO tmpapp = returnMap.get(userId); int
                         * newNO = tmpapp.getNewNums(); tmpapp.setNewNums(newNO
                         * + 1); returnMap.put(userId, tmpapp); }
                         * pnMap.remove(inds); continue; }
                         */
                        continue;
                    }
                    if ("userTask".equals(type)) {
                        if (taskId != null && startTask.indexOf(taskId) > -1) {
                            String inds = historicActivityInstance.getProcInstId();
                            if (pnMap.containsKey(inds)) {
                                String userId = pnMap.get(inds);
                                if (returnMap.containsKey(userId)) {
                                    ActApprovalVO tmpapp = returnMap.get(userId);
                                    int newNO = tmpapp.getNewNums();
                                    tmpapp.setNewNums(newNO + 1);
                                    returnMap.put(userId, tmpapp);
                                }
                                pnMap.remove(inds);
                            }
                            continue;
                        }
                        String assId = historicActivityInstance.getAssignee();
                        if (assId != null && !"".equals(assId)) {
                            if (returnMap.containsKey(assId)) {
                                ActApprovalVO tmpapp = returnMap.get(assId);
                                // int newNO = tmpapp.getNewNums();
                                int hNO = tmpapp.getHandleNums();
                                int sNO = tmpapp.getStayNums();
                                // 节点结束 说明任务已办理
                                if (historicActivityInstance.getEndTime() != null) {
                                    hNO += 1;
                                    sNO += 1;
                                } else {
                                    sNO += 1;
                                }
                                tmpapp.setHandleNums(hNO);
                                tmpapp.setStayNums(sNO);
                                returnMap.put(assId, tmpapp);
                            }
                        } /*
                           * else { // 未签收任务 List<String> noids = new
                           * ArrayList<String>(); if
                           * (historicActivityInstance.getTaskId() != null) {
                           * List identityLinkList = taskService
                           * .getIdentityLinksForTask(historicActivityInstance
                           * .getTaskId()); if (identityLinkList != null &&
                           * identityLinkList.size() > 0) { for (Iterator
                           * iterator = identityLinkList.iterator();
                           * iterator.hasNext();) { IdentityLink identityLink
                           * = (IdentityLink) iterator.next(); if
                           * (identityLink.getUserId() != null) {
                           * noids.add(identityLink.getUserId()); } if
                           * (identityLink.getGroupId() != null) { //
                           * 根据组获得对应人员 List<User> linkList =
                           * identityService.createUserQuery()
                           * .memberOfGroup(identityLink.getGroupId()).list();
                           * if (linkList != null && linkList.size() > 0) {
                           * for (User user : linkList) {
                           * noids.add(user.getId()); }
                           * 
                           * }
                           * 
                           * } }
                           * 
                           * } } //
                           * -------------------------------------------------
                           * --------------------- if (noids.size() > 0) { for
                           * (String userid : noids) { if
                           * (returnMap.containsKey(userid)) { ActApprovalVO
                           * tmpapp = returnMap.get(userid); int sNO =
                           * tmpapp.getStayNums(); sNO += 1;
                           * tmpapp.setStayNums(sNO); returnMap.put(userid,
                           * tmpapp); } }
                           * 
                           * }
                           * 
                           * }
                           */

                    }
                }

            }

        }
        if (duserlist != null && duserlist.size() > 0) {
            for (AutDepartmentUser autDepartmentUser : duserlist) {
                if (returnMap != null && returnMap.size() > 0) {
                    if (uDMap.containsKey(autDepartmentUser.getDeptId().toString())) {
                        ActApprovalVO tmp = uDMap.get(autDepartmentUser.getDeptId().toString());
                        if (returnMap.containsKey(autDepartmentUser.getUserId().toString())) {
                            ActApprovalVO tmpUserVO = returnMap.get(autDepartmentUser.getUserId().toString());
                            tmp.setNewNums(tmp.getNewNums() + tmpUserVO.getNewNums());
                            tmp.setStayNums(tmpUserVO.getStayNums() + tmp.getStayNums());
                            tmp.setHandleNums(tmpUserVO.getHandleNums() + tmp.getHandleNums());
                            uDMap.put(autDepartmentUser.getDeptId().toString(), tmp);
                        }
                    } else {
                        ActApprovalVO tmp = new ActApprovalVO();
                        tmp.setUserName(dMap.get(autDepartmentUser.getDeptId().toString()));
                        if (returnMap.containsKey(autDepartmentUser.getUserId().toString())) {
                            ActApprovalVO tmpUserVO = returnMap.get(autDepartmentUser.getUserId().toString());
                            tmp.setNewNums(tmpUserVO.getNewNums());
                            tmp.setHandleNums(tmpUserVO.getHandleNums());
                            tmp.setStayNums(tmpUserVO.getStayNums());
                        } else {
                            tmp.setNewNums(0);
                            tmp.setHandleNums(0);
                            tmp.setStayNums(0);
                        }
                        tmp.setShowDate(showDate);
                        uDMap.put(autDepartmentUser.getDeptId().toString(), tmp);
                    }
                }
            }
        }
        if (uDMap != null && uDMap.size() > 0) {
            for (ActApprovalVO v : uDMap.values()) {
                voList.add(v);
            }
        }
        return voList;
    }

    @Override
    public List<ActApprovalVO> getApprovalList(ActWorkingListVO obj) throws Exception {
        // TODO Auto-generated method stub
        List<ActApprovalVO> voList = new ArrayList<ActApprovalVO>();
        List<AutUser> userList = new ArrayList<AutUser>();
        List<AutPosition> positionList = new ArrayList<AutPosition>();
        List<AutUserPosition> positionUserList = new ArrayList<AutUserPosition>();
        Map<String, String> upMap = new HashMap<String, String>();
        Map<String, String> pnMap = new HashMap<String, String>();
        String userids = "";
        if (obj.getDeptId() != null && !"".equals(obj.getDeptId())) {
            AutDepartment autDepartment = autDepartmentService.selectById(Long.valueOf(obj.getDeptId()));
            Where<AutDepartmentUser> dUserWhere = new Where<AutDepartmentUser>();
            dUserWhere.where("dept_code like {0}", autDepartment.getDeptCode() + "%");
            dUserWhere.setSqlSelect("user_id");
            List<AutDepartmentUser> duserlist = autDepartmentUserService.selectList(dUserWhere);
            if (duserlist != null && duserlist.size() > 0) {
                for (AutDepartmentUser autDepartmentUser : duserlist) {
                    if (userids.indexOf(autDepartmentUser.getUserId().toString()) > -1) {
                    } else {
                        userids += autDepartmentUser.getUserId() + ",";
                    }
                }
                if (userids.length() > 0) {
                    Where<AutUser> userWhere = new Where<AutUser>();
                    userWhere.in("id", userids);
                    userWhere.eq("is_active", 1);
                    userWhere.eq("is_account_expired", 0);
                    userWhere.eq("is_account_locked", 0);
                    userWhere.eq("is_credentials_expired", 0);
                    userWhere.setSqlSelect("full_name,id");
                    userList = autUserService.selectList(userWhere);
                    Where<AutUserPosition> userPWhere = new Where<AutUserPosition>();
                    userPWhere.in("user_id", userids);
                    userPWhere.eq("is_active", 1);
                    userPWhere.setSqlSelect("position_id,user_id");
                    positionUserList = autUserPositionService.selectList(userPWhere);
                }
            } else {
                return voList;
            }
        } else {
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.eq("is_active", 1);
            userWhere.eq("is_account_expired", 0);
            userWhere.eq("is_account_locked", 0);
            userWhere.eq("is_credentials_expired", 0);
            userWhere.setSqlSelect("full_name,id");
            userList = autUserService.selectList(userWhere);

            if (userList != null && userList.size() > 0) {
                for (AutUser autUser : userList) {
                    if (userids.indexOf(autUser.getId().toString()) > -1) {
                    } else {
                        userids += autUser.getId() + ",";
                    }
                }
                Where<AutUserPosition> userPWhere = new Where<AutUserPosition>();
                userPWhere.in("user_id", userids);
                userPWhere.eq("is_active", 1);
                userPWhere.setSqlSelect("position_id,user_id");
                positionUserList = autUserPositionService.selectList(userPWhere);
            } else {
                return voList;
            }
        }
        if (positionUserList != null) {
            String positionIds = "";
            for (AutUserPosition autUserPosition : positionUserList) {
                positionIds += autUserPosition.getPositionId() + ",";
            }
            Where<AutPosition> pWhere = new Where<AutPosition>();
            pWhere.in("id", positionIds);
            pWhere.eq("is_active", 1);
            pWhere.setSqlSelect("id,position_name");
            positionList = autPositionService.selectList(pWhere);
        }
        if (positionList != null && positionList.size() > 0) {
            for (AutPosition autPosition : positionList) {
                pnMap.put(autPosition.getId().toString(), autPosition.getPositionName());
            }
        }
        positionList.clear();
        if (positionUserList != null && positionUserList.size() > 0) {
            for (AutUserPosition autUserPosition : positionUserList) {
                String tmpPosName = "";
                if (upMap.containsKey(autUserPosition.getUserId().toString())) {
                    tmpPosName = upMap.get(autUserPosition.getUserId().toString());

                }
                if (pnMap.containsKey(autUserPosition.getPositionId().toString())) {
                    tmpPosName += pnMap.get(autUserPosition.getPositionId().toString()) + ";";
                }
                upMap.put(autUserPosition.getUserId().toString(), tmpPosName);
            }
        }
        pnMap.clear();
        positionUserList.clear();
        // ----------------------------------------------------------------------------------------------------------------------------
        Date startTime = null;
        Date endTime = null;
        String weets = obj.getWeeks();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        if ((obj.getTime1() != "" && "".equals(obj.getTime1()))
            || (obj.getTime2() != null && !"".equals(obj.getTime2()))) {
            startTime = df.parse(obj.getTime1());
            endTime = df.parse(obj.getTime2());
            /*
             * cal.setTime(endTime); cal.add(Calendar.DAY_OF_MONTH, 1); endTime
             * = cal.getTime();
             */
        } else {
            if ("0".equals(weets)) {
                // 30天
                Date newDate = new Date();
                cal.setTime(df.parse(df.format(newDate)));
                endTime = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -30);
                startTime = cal.getTime();

            }
            if ("1".equals(weets)) {
                // 本周
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
                startTime = cal.getTime();
                cal.setTime(df.parse(df.format(startTime)));
                startTime = cal.getTime();
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                endTime = cal.getTime();
            }
            if ("2".equals(weets)) {
                // 本月
                String newDate = df.format(new Date());
                String[] newTime = newDate.split("-");
                newDate = newTime[0] + "-" + newTime[1] + "-01";
                cal.setTime(df.parse(newDate));
                // cal.set(Calendar.DAY_OF_MONTH, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.add(Calendar.SECOND, 0);
                startTime = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                endTime = cal.getTime();
            }
        }
        // -----------------------------------------------------------------
        Map<String, ActApprovalVO> returnMap = new HashMap<String, ActApprovalVO>();
        if (upMap != null && upMap.size() > 0) {
            for (AutUser autUser : userList) {
                int no = 1;
                ActApprovalVO vo = new ActApprovalVO();
                vo.setJobsName(upMap.get(autUser.getId().toString()));
                vo.setUserName(autUser.getFullName());
                vo.setNewNums(0);
                vo.setStayNums(0);
                vo.setHandleNums(0);
                vo.setNo(no);
                returnMap.put(autUser.getId().toString(), vo);
            }
        }
        upMap.clear();
        Where<ActAljoinQueryHis> hisWhere = new Where<ActAljoinQueryHis>();
        hisWhere.eq("is_delete", 0);
        hisWhere.ge("create_time", startTime);
        hisWhere.lt("create_time", DateUtil.str2dateOrTime(df.format(endTime) + " 23:59:59"));
        hisWhere.setSqlSelect("process_instance_id,create_user_id,start_task");
        List<ActAljoinQueryHis> actAljoinQueryHisList = actAljoinQueryHisService.selectList(hisWhere);
        if (actAljoinQueryHisList != null && actAljoinQueryHisList.size() > 0) {
            // String instaceIds = "";
            List<String> instaceIdsList = new ArrayList<String>();
            String startTask = "";
            for (ActAljoinQueryHis actAljoinQueryHis : actAljoinQueryHisList) {
                // instaceIds += actAljoinQueryHis.getProcessInstanceId() + ",";
                instaceIdsList.add(actAljoinQueryHis.getProcessInstanceId());
                startTask += actAljoinQueryHis.getStartTask() + ",";
                pnMap.put(actAljoinQueryHis.getProcessInstanceId().toString(),
                    actAljoinQueryHis.getCreateUserId().toString());
            }
            if (instaceIdsList.size() > 0) {
                // instaceIds = instaceIds.substring(0, instaceIds.length() -
                // 1);
                /*
                 * List<HistoricActivityInstance> activityList =
                 * historyService.createNativeHistoricActivityInstanceQuery()
                 * .sql("select * from " +
                 * managementService.getTableName(HistoricActivityInstance.
                 * class) + "  H  where H.START_TIME_ >='" +
                 * df.format(startTime) + " 00:00:00" + "' and H.START_TIME_<'"
                 * + df.format(endTime) + " 23:59:59" +
                 * "' AND H.PROC_INST_ID_ in(" + instaceIds + ")") .list();
                 */
                Where<ActHiActinst> hiActWhere = new Where<ActHiActinst>();
                if (instaceIdsList.size() < 1000) {
                    hiActWhere.in("PROC_INST_ID_", instaceIdsList);
                } else {
                    int tmpIdsNo = 0;
                    List<String> instaceIds = new ArrayList<String>();
                    for (String string : instaceIdsList) {
                        tmpIdsNo += 1;
                        instaceIds.add(string);
                        if (tmpIdsNo == 1000) {
                            tmpIdsNo = 0;
                            hiActWhere.in("PROC_INST_ID_", instaceIds).or();
                            instaceIds.clear();
                        }
                    }
                    if (tmpIdsNo == 1000 || tmpIdsNo == 0) {
                        hiActWhere.eq("is_delete", 0).andNew();
                    } else {
                        hiActWhere.in("PROC_INST_ID_", instaceIds).andNew();
                    }
                }
                // hiActWhere.setSqlSelect("ACT_TYPE_,TASK_ID_,PROC_INST_ID_");
                hiActWhere.between("START_TIME_", dfs.parse(df.format(startTime) + " 00:00:00"),
                    dfs.parse(df.format(endTime) + " 23:59:59"));
                List<ActHiActinst> activityList = actHiActinstService.selectList(hiActWhere);

                for (int hi = 0; hi < activityList.size(); hi++) {
                    ActHiActinst historicActivityInstance = activityList.get(hi);
                    String type = historicActivityInstance.getActType();
                    if ("startEvent".equals(type)) {
                        continue;
                    }
                    String taskid = historicActivityInstance.getTaskId();
                    if (taskid != null && !"".equals(taskid)
                        && startTask.indexOf(historicActivityInstance.getTaskId()) > -1) {
                        String inds = historicActivityInstance.getProcInstId();
                        if (pnMap.containsKey(inds)) {
                            String userId = pnMap.get(inds);
                            if (returnMap.containsKey(userId)) {
                                ActApprovalVO tmpapp = returnMap.get(userId);
                                int newNO = tmpapp.getNewNums();
                                tmpapp.setNewNums(newNO + 1);
                                returnMap.put(userId, tmpapp);
                            }
                            pnMap.remove(inds);
                        }
                        continue;
                    }
                    if ("userTask".equals(type)) {
                        String assId = historicActivityInstance.getAssignee();

                        if (assId != null && !"".equals(assId)) {
                            if (returnMap.containsKey(assId)) {
                                ActApprovalVO tmpapp = returnMap.get(assId);
                                int hNO = tmpapp.getHandleNums();
                                int sNO = tmpapp.getStayNums();
                                // 节点结束 说明任务已办理
                                if (historicActivityInstance.getEndTime() != null) {
                                    hNO += 1;
                                    sNO += 1;
                                } else {
                                    sNO += 1;
                                }
                                tmpapp.setHandleNums(hNO);
                                tmpapp.setStayNums(sNO);
                                returnMap.put(assId, tmpapp);
                            }
                        } /*
                           * else { // 未签收任务 List<String> noids = new
                           * ArrayList<String>();
                           * 
                           * List identityLinkList = taskService
                           * .getIdentityLinksForTask(historicActivityInstance
                           * .getTaskId()); if (identityLinkList == null) {
                           * continue; } if (identityLinkList != null &&
                           * identityLinkList.size() > 0) { for (Iterator
                           * iterator = identityLinkList.iterator();
                           * iterator.hasNext();) { IdentityLink identityLink
                           * = (IdentityLink) iterator.next(); if
                           * (identityLink.getUserId() != null) {
                           * noids.add(identityLink.getUserId()); } if
                           * (identityLink.getGroupId() != null) { //
                           * 根据组获得对应人员 List<User> linkList =
                           * identityService.createUserQuery()
                           * .memberOfGroup(identityLink.getGroupId()).list();
                           * if (linkList != null && linkList.size() > 0) {
                           * for (User user : linkList) {
                           * noids.add(user.getId()); }
                           * 
                           * }
                           * 
                           * } }
                           * 
                           * } //
                           * -------------------------------------------------
                           * --------------------- if (noids.size() > 0) { for
                           * (String userid : noids) { if
                           * (returnMap.containsKey(userid)) { ActApprovalVO
                           * tmpapp = returnMap.get(userid); int sNO =
                           * tmpapp.getStayNums(); sNO += 1;
                           * tmpapp.setStayNums(sNO); returnMap.put(userid,
                           * tmpapp); } }
                           * 
                           * }
                           * 
                           * }
                           */

                    }
                }

            }

        }
        if (returnMap != null && returnMap.size() > 0) {
            for (ActApprovalVO v : returnMap.values()) {
                voList.add(v);
            }
        }

        return voList;
    }

    @SuppressWarnings("unused")
    @Override
    public List<ActRegulationVO> getRegulation(ActRegulationListVO obj) throws Exception {
        List<ActRegulationVO> vo = new ArrayList<ActRegulationVO>();

        Date startTime = null;
        Date endTime = null;
        String weets = obj.getWeeks();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        if ((obj.getTime1() != "" && "".equals(obj.getTime1()))
            || (obj.getTime2() != null && !"".equals(obj.getTime2()))) {
            startTime = df.parse(obj.getTime1());
            endTime = df.parse(obj.getTime2());
            cal.setTime(endTime);
            // cal.add(Calendar.DAY_OF_MONTH, 1);
            endTime = cal.getTime();
        } else {
            if ("0".equals(weets)) {
                // 30天
                Date newDate = new Date();
                cal.setTime(df.parse(df.format(newDate)));
                endTime = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -30);
                startTime = cal.getTime();

            }
            if ("1".equals(weets)) {
                // 本周
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
                startTime = cal.getTime();
                cal.setTime(df.parse(df.format(startTime)));
                startTime = cal.getTime();
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                endTime = cal.getTime();
            }
            if ("2".equals(weets)) {
                // 本月
                String newDate = df.format(new Date());
                String[] newTime = newDate.split("-");
                newDate = newTime[0] + "-" + newTime[1] + "-01";
                cal.setTime(df.parse(newDate));
                // cal.set(Calendar.DAY_OF_MONTH, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.add(Calendar.SECOND, 0);
                startTime = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                endTime = cal.getTime();
            }
        }
        // cal.setTime(endTime);
        // cal.add(Calendar.DAY_OF_MONTH, -1);
        String showDate = df.format(startTime) + "至" + df.format(endTime);
        // -----------------------------------end
        // time----------------------------------
        List<AutUser> userList = new ArrayList<AutUser>();
        List<AutDepartment> deptList = new ArrayList<AutDepartment>();
        List<AutDepartmentUser> duserlist = new ArrayList<AutDepartmentUser>();
        Map<String, ActRegulationVO> deptMap = new HashMap<String, ActRegulationVO>();
        Where<AutDepartment> dWhere = new Where<AutDepartment>();
        dWhere.eq("is_active", 1);
        deptList = autDepartmentService.selectList(dWhere);
        Where<AutDepartmentUser> udWhere = new Where<AutDepartmentUser>();
        udWhere.eq("is_active", 1);
        duserlist = autDepartmentUserService.selectList(udWhere);
        for (AutDepartment autDepartment : deptList) {
            ActRegulationVO regVO = new ActRegulationVO();
            regVO.setFullName(autDepartment.getDeptName());
            regVO.setPsth(new BigDecimal(0));
            regVO.setSth(new BigDecimal(0));
            regVO.setpTotalTime(new BigDecimal(0));
            regVO.setTotalTime(new BigDecimal(0));
            regVO.setSumNo(0);
            regVO.setNo(0);
            deptMap.put(autDepartment.getId().toString(), regVO);
        }
        deptList.clear();
        String userids = "";
        Where<AutUser> userWhere = new Where<AutUser>();
        userWhere.eq("is_active", 1);
        userWhere.eq("is_account_expired", 0);
        userWhere.eq("is_account_locked", 0);
        userWhere.eq("is_credentials_expired", 0);
        userWhere.setSqlSelect("full_name,id");
        userList = autUserService.selectList(userWhere);
        if (userList != null && userList.size() > 0) {
            for (AutUser autUser : userList) {
                userids += autUser.getId() + ",";
            }
        } else {
            return vo;
        }

        // ----------------------------------------部门数成员据生成----------------------------------------------
        Map<String, ActRegulationVO> returnMap = new HashMap<String, ActRegulationVO>();
        if (userList != null && userList.size() > 0) {
            for (AutUser autUser : userList) {
                int no = 1;
                ActRegulationVO regVO = new ActRegulationVO();
                regVO.setFullName(autUser.getFullName());
                regVO.setPsth(new BigDecimal(0));
                regVO.setSth(new BigDecimal(0));
                regVO.setpTotalTime(new BigDecimal(0));
                regVO.setTotalTime(new BigDecimal(0));
                regVO.setSumNo(0);
                regVO.setNo(no);
                returnMap.put(autUser.getId().toString(), regVO);
            }
        }
        // 第一次过滤
        Where<ActAljoinQueryHis> hisWhere = new Where<ActAljoinQueryHis>();
        hisWhere.between("create_time", startTime, DateUtil.str2datetime(df.format(endTime) + " 23:59:59"));
        hisWhere.setSqlSelect("process_instance_id,start_task");
        List<ActAljoinQueryHis> hisQueryList = actAljoinQueryHisService.selectList(hisWhere);
        /* List<String> inList = new ArrayList<String>(); */
        if (hisQueryList != null && hisQueryList.size() > 0) {
            String instaceIds = "";
            String stratTask = "";
            List<String> instaceIdsList = new ArrayList<String>();
            for (ActAljoinQueryHis actAljoinQueryHis : hisQueryList) {
                // instaceIds += actAljoinQueryHis.getProcessInstanceId() + ",";
                instaceIdsList.add(actAljoinQueryHis.getProcessInstanceId());
                stratTask += actAljoinQueryHis.getStartTask() + ",";
            }
            hisQueryList.clear();
            if (instaceIdsList.size() > 0) {
                // instaceIds = instaceIds.substring(0, instaceIds.length() - 1);
                /*
                 * List<HistoricActivityInstance> activityList =
                 * historyService.createNativeHistoricActivityInstanceQuery()
                 * .sql("select * from " +
                 * managementService.getTableName(HistoricActivityInstance.
                 * class) + "  H  where H.START_TIME_ >=" +
                 * dfs.parse(df.format(startTime) + " 00:00:00").getTime()+
                 * " and H.START_TIME_<=" + dfs.parse(df.format(endTime) +
                 * " 23:59:59").getTime()+" AND H.PROC_INST_ID_ in(" +
                 * instaceIds + ")") .list(); instaceIds = ""; List<String> pids
                 * = new ArrayList<String>(); for (HistoricActivityInstance
                 * historicActivityInstance : activityList) { String type =
                 * historicActivityInstance.getActivityType(); if
                 * ("userTask".equals(type)) { instaceIds +=
                 * historicActivityInstance.getTaskId() + ",";
                 * pids.add(historicActivityInstance.getProcessInstanceId()); }
                 * }
                 */
                Where<ActHiActinst> hiActWhere = new Where<ActHiActinst>();
                // hiActWhere.in("PROC_INST_ID_", instaceIds);
                if (instaceIdsList.size() > 1000) {
                    int instNo = 0;
                    List<String> instList = new ArrayList<String>();
                    for (String string : instaceIdsList) {
                        instList.add(string);
                        instNo += 1;
                        if (instNo == 1000) {
                            hiActWhere.in("PROC_INST_ID_", instList).or();
                            instNo = 0;
                            instList.clear();
                        }
                    }
                    if (instNo == 0) {
                        hiActWhere.where("1={0}", 1);
                    } else {
                        hiActWhere.in("PROC_INST_ID_", instList);
                    }
                } else {
                    hiActWhere.in("PROC_INST_ID_", instaceIdsList);
                }
                hiActWhere.setSqlSelect("ACT_TYPE_,TASK_ID_,PROC_INST_ID_");
                hiActWhere.between("START_TIME_", dfs.parse(df.format(startTime) + " 00:00:00"),
                    dfs.parse(df.format(endTime) + " 23:59:59"));
                List<ActHiActinst> activityList = actHiActinstService.selectList(hiActWhere);

                instaceIds = "";
                List<String> pids = new ArrayList<String>();
                for (ActHiActinst historicActivityInstance : activityList) {
                    String type = historicActivityInstance.getActType();
                    if ("userTask".equals(type)) {
                        instaceIds += historicActivityInstance.getTaskId() + ",";
                        pids.add(historicActivityInstance.getProcInstId());
                    }
                }
                activityList.clear();
                if (instaceIds != null && instaceIds.length() > 0) {
                    HistoricTaskInstanceQuery hisTaskQurey = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceIdIn(pids).taskCreatedAfter(startTime).taskCreatedBefore(endTime);
                    pids.clear();
                    List<HistoricTaskInstance> hisTask = hisTaskQurey.list();
                    if (hisTask != null && hisTask.size() > 0) {
                        for (HistoricTaskInstance historicTaskInstance : hisTask) {
                            String taskid = historicTaskInstance.getId();
                            if (stratTask.indexOf(taskid) > -1) {
                                continue;
                            }
                            if (instaceIds.indexOf(taskid) > -1) {
                                String assId = historicTaskInstance.getAssignee();
                                if (assId != null && !"".equals(assId)) {
                                    if (returnMap.containsKey(assId)) {
                                        ActRegulationVO tmpapp = returnMap.get(assId);
                                        BigDecimal sinTime = tmpapp.getSth();// 签收
                                        BigDecimal tolTime = tmpapp.getTotalTime();// 办理
                                        int sum = tmpapp.getSumNo();// 文件数量
                                        int tolsum = tmpapp.getTotalNo();// 文件数量
                                        if (historicTaskInstance.getClaimTime() != null) {
                                            // 签收任务
                                            sum += 1;
                                            BigDecimal lTime
                                                = BigDecimal.valueOf(historicTaskInstance.getClaimTime().getTime()
                                                    - historicTaskInstance.getStartTime().getTime());
                                            sinTime = sinTime.add(lTime);
                                            if (historicTaskInstance.getEndTime() != null) {
                                                lTime = BigDecimal.valueOf(historicTaskInstance.getEndTime().getTime()
                                                    - historicTaskInstance.getClaimTime().getTime());
                                                tolsum += 1;
                                                tolTime = tolTime.add(lTime);
                                            } else {
                                                /*
                                                 * lTime =
                                                 * BigDecimal.valueOf(new
                                                 * Date().getTime() -
                                                 * historicTaskInstance.
                                                 * getClaimTime().getTime());
                                                 * tolsum += 1; tolTime =
                                                 * tolTime.add(lTime);
                                                 */
                                            }
                                        } else {
                                            /*
                                             * BigDecimal lTime =
                                             * BigDecimal.valueOf(new
                                             * Date().getTime() -
                                             * historicTaskInstance.getStartTime
                                             * ().getTime()); sinTime =
                                             * sinTime.add(lTime);
                                             */
                                        }
                                        tmpapp.setSth(sinTime);
                                        tmpapp.setTotalTime(tolTime);
                                        tmpapp.setSumNo(sum);
                                        tmpapp.setTotalNo(tolsum);
                                        returnMap.put(assId, tmpapp);
                                    }
                                } /*
                                   * else { // 未签收任务 if
                                   * (historicTaskInstance.getEndTime() ==
                                   * null) { List identityLinkList =
                                   * taskService.getIdentityLinksForTask(
                                   * taskid); if (identityLinkList != null &&
                                   * identityLinkList.size() > 0) { for
                                   * (Iterator iterator =
                                   * identityLinkList.iterator();
                                   * iterator.hasNext();) { IdentityLink
                                   * identityLink = (IdentityLink)
                                   * iterator.next(); if
                                   * (identityLink.getUserId() != null) {
                                   * String userId = identityLink.getUserId();
                                   * if (returnMap.containsKey(userId)) {
                                   * ActRegulationVO tmpapp =
                                   * returnMap.get(userId); BigDecimal sinTime
                                   * = tmpapp.getSth();// 签收 int sum =
                                   * tmpapp.getSumNo();// 文件数量 sum += 1;
                                   * BigDecimal lTime = BigDecimal.valueOf(new
                                   * Date().getTime() -
                                   * historicTaskInstance.getStartTime().
                                   * getTime()); sinTime = sinTime.add(lTime);
                                   * tmpapp.setSth(sinTime);
                                   * tmpapp.setSumNo(sum);
                                   * returnMap.put(userId, tmpapp); } } if
                                   * (identityLink.getGroupId() != null) { //
                                   * 根据组获得对应人员 List<User> linkList =
                                   * identityService.createUserQuery()
                                   * .memberOfGroup(identityLink.getGroupId())
                                   * .list(); if (linkList != null &&
                                   * linkList.size() > 0) { for (User user :
                                   * linkList) { String userId = user.getId();
                                   * if (returnMap.containsKey(userId)) {
                                   * ActRegulationVO tmpapp =
                                   * returnMap.get(userId); BigDecimal sinTime
                                   * = tmpapp.getSth();// 签收 BigDecimal
                                   * tolTime = tmpapp.getTotalTime();// 办理 int
                                   * sum = tmpapp.getSumNo();// 文件数量 sum += 1;
                                   * if (historicTaskInstance.getClaimTime()
                                   * != null) { // 签收任务 BigDecimal lTime =
                                   * BigDecimal .valueOf(historicTaskInstance.
                                   * getClaimTime() .getTime() -
                                   * historicTaskInstance
                                   * .getStartTime().getTime()); sinTime =
                                   * sinTime.add(lTime); if
                                   * (historicTaskInstance.getEndTime() !=
                                   * null) { lTime =
                                   * BigDecimal.valueOf(historicTaskInstance
                                   * .getEndTime().getTime() -
                                   * historicTaskInstance.getClaimTime()
                                   * .getTime()); tolTime =
                                   * tolTime.add(lTime); } } else { BigDecimal
                                   * lTime = BigDecimal.valueOf( new
                                   * Date().getTime() - historicTaskInstance
                                   * .getStartTime().getTime()); sinTime =
                                   * sinTime.add(lTime); }
                                   * 
                                   * tmpapp.setSth(sinTime);
                                   * tmpapp.setTotalTime(tolTime);
                                   * tmpapp.setSumNo(sum);
                                   * returnMap.put(userId, tmpapp); } } } } }
                                   * } } }
                                   */
                            }
                        }
                    }
                }
            }

        }
        if (duserlist != null && duserlist.size() > 0 && returnMap != null && returnMap.size() > 0) {
            for (AutDepartmentUser deptUser : duserlist) {
                if (returnMap.containsKey(deptUser.getUserId().toString())) {
                    if (deptMap.containsKey(deptUser.getDeptId().toString())) {
                        ActRegulationVO tmpVo = deptMap.get(deptUser.getDeptId().toString());
                        BigDecimal sth = tmpVo.getSth();
                        BigDecimal total = tmpVo.getTotalTime();
                        int sumNo = tmpVo.getSumNo();
                        int tolNo = tmpVo.getTotalNo();
                        ActRegulationVO retVO = returnMap.get(deptUser.getUserId().toString());
                        sth = sth.add(retVO.getSth());
                        total = total.add(retVO.getTotalTime());
                        sumNo += retVO.getSumNo();
                        tolNo += retVO.getTotalNo();
                        tmpVo.setSth(sth);
                        tmpVo.setSumNo(sumNo);
                        tmpVo.setTotalTime(total);
                        tmpVo.setTotalNo(tolNo);
                        tmpVo.setShowDate(showDate);
                        deptMap.put(deptUser.getDeptId().toString(), tmpVo);
                    }
                }
            }
        }
        for (ActRegulationVO v : deptMap.values()) {
            vo.add(v);
        }
        return vo;
    }

    @Override
    public List<ActRegulationVO> getRegulationList(ActRegulationListVO obj, PageBean pageBean) throws Exception {
        List<ActRegulationVO> vo = new ArrayList<ActRegulationVO>();
        Date startTime = null;
        Date endTime = null;
        String weets = obj.getWeeks();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if ((obj.getTime1() != "" && "".equals(obj.getTime1()))
            || (obj.getTime2() != null && !"".equals(obj.getTime2()))) {
            startTime = df.parse(obj.getTime1());
            endTime = df.parse(obj.getTime2());
            // cal.setTime(endTime);
            // cal.add(Calendar.DAY_OF_MONTH, 1);
            // endTime = cal.getTime();
        } else {
            if ("0".equals(weets)) {
                // 30天
                Date newDate = new Date();
                cal.setTime(df.parse(df.format(newDate)));
                endTime = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -30);
                startTime = cal.getTime();

            }
            if ("1".equals(weets)) {
                // 本周
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
                startTime = cal.getTime();
                cal.setTime(df.parse(df.format(startTime)));
                startTime = cal.getTime();
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                endTime = cal.getTime();
            }
            if ("2".equals(weets)) {
                // 本月
                String newDate = df.format(new Date());
                String[] newTime = newDate.split("-");
                newDate = newTime[0] + "-" + newTime[1] + "-01";
                cal.setTime(df.parse(newDate));
                // cal.set(Calendar.DAY_OF_MONTH, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.add(Calendar.SECOND, 0);
                startTime = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                endTime = cal.getTime();
            }
        }
        // -----------------------------------end
        // time----------------------------------
        List<AutUser> userList = new ArrayList<AutUser>();
        List<AutPosition> positionList = new ArrayList<AutPosition>();
        List<AutUserPosition> positionUserList = new ArrayList<AutUserPosition>();
        Map<String, String> upMap = new HashMap<String, String>();
        Map<String, String> pnMap = new HashMap<String, String>();
        String userids = "";
        if (obj.getDeptId() != null && !"".equals(obj.getDeptId())) {
            AutDepartment autDepartment = autDepartmentService.selectById(Long.valueOf(obj.getDeptId()));
            Where<AutDepartmentUser> dUserWhere = new Where<AutDepartmentUser>();
            dUserWhere.where("dept_code like {0}", autDepartment.getDeptCode() + "%");
            dUserWhere.setSqlSelect("user_id");
            List<AutDepartmentUser> duserlist = autDepartmentUserService.selectList(dUserWhere);
            if (duserlist != null && duserlist.size() > 0) {
                for (AutDepartmentUser autDepartmentUser : duserlist) {
                    userids += autDepartmentUser.getUserId() + ",";
                }
                if (userids.length() > 0) {
                    Where<AutUser> userWhere = new Where<AutUser>();
                    userWhere.in("id", userids);
                    userWhere.eq("is_active", 1);
                    if (obj.getFullName() != null && !"".equals(obj.getFullName())) {
                        userWhere.like("full_name", obj.getFullName());
                    }
                    userWhere.eq("is_account_expired", 0);
                    userWhere.eq("is_account_locked", 0);
                    userWhere.eq("is_credentials_expired", 0);
                    userWhere.setSqlSelect("full_name,id");
                    userList = autUserService.selectList(userWhere);
                    userids = "";
                    if (userList != null && userList.size() > 0) {
                        for (AutUser autUser : userList) {
                            userids += autUser.getId() + ",";
                        }
                    }
                    Where<AutUserPosition> userPWhere = new Where<AutUserPosition>();
                    userPWhere.in("user_id", userids);
                    userPWhere.eq("is_active", 1);
                    userPWhere.setSqlSelect("position_id,user_id");
                    positionUserList = autUserPositionService.selectList(userPWhere);
                }
            } else {
                return vo;
            }
        } else {
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.eq("is_active", 1);
            userWhere.eq("is_account_expired", 0);
            userWhere.eq("is_account_locked", 0);
            if (obj.getFullName() != null && !"".equals(obj.getFullName())) {
                userWhere.like("full_name", obj.getFullName());
            }
            userWhere.eq("is_credentials_expired", 0);
            userWhere.setSqlSelect("full_name,id");
            userList = autUserService.selectList(userWhere);

            if (userList != null && userList.size() > 0) {
                for (AutUser autUser : userList) {
                    userids += autUser.getId() + ",";
                }
                Where<AutUserPosition> userPWhere = new Where<AutUserPosition>();
                userPWhere.in("user_id", userids);
                userPWhere.eq("is_active", 1);
                userPWhere.setSqlSelect("position_id,user_id");
                positionUserList = autUserPositionService.selectList(userPWhere);
            } else {
                return vo;
            }
        }
        if (positionUserList != null) {
            String positionIds = "";
            for (AutUserPosition autUserPosition : positionUserList) {
                positionIds += autUserPosition.getPositionId() + ",";
            }
            Where<AutPosition> pWhere = new Where<AutPosition>();
            pWhere.in("id", positionIds);
            pWhere.eq("is_active", 1);
            pWhere.setSqlSelect("id,position_name");
            positionList = autPositionService.selectList(pWhere);
        }
        if (positionList != null && positionList.size() > 0) {
            for (AutPosition autPosition : positionList) {
                pnMap.put(autPosition.getId().toString(), autPosition.getPositionName());
            }
        }
        positionList.clear();
        if (positionUserList != null && positionUserList.size() > 0) {
            for (AutUserPosition autUserPosition : positionUserList) {
                String tmpPosName = "";
                if (upMap.containsKey(autUserPosition.getUserId().toString())) {
                    tmpPosName = upMap.get(autUserPosition.getUserId().toString());

                }
                if (pnMap.containsKey(autUserPosition.getPositionId().toString())) {
                    tmpPosName += pnMap.get(autUserPosition.getPositionId().toString()) + ";";
                }
                upMap.put(autUserPosition.getUserId().toString(), tmpPosName);
            }
        }
        pnMap.clear();
        positionUserList.clear();
        // ----------------------------------------部门数成员据生成----------------------------------------------
        Map<String, ActRegulationVO> returnMap = new HashMap<String, ActRegulationVO>();
        if (userList != null && userList.size() > 0) {
            for (AutUser autUser : userList) {
                int no = 1;
                ActRegulationVO regVO = new ActRegulationVO();
                regVO.setJobsName(upMap.get(autUser.getId().toString()));
                regVO.setFullName(autUser.getFullName());
                regVO.setPsth(new BigDecimal(0));
                regVO.setSth(new BigDecimal(0));
                regVO.setpTotalTime(new BigDecimal(0));
                regVO.setTotalTime(new BigDecimal(0));
                regVO.setSumNo(0);
                regVO.setNo(no);
                returnMap.put(autUser.getId().toString(), regVO);
            }
        }
        upMap.clear();
        // 第一次过滤
        Where<ActAljoinQueryHis> hisWhere = new Where<ActAljoinQueryHis>();
        hisWhere.between("create_time", startTime, new Date());
        hisWhere.setSqlSelect("process_instance_id,start_task");
        List<ActAljoinQueryHis> hisQueryList = actAljoinQueryHisService.selectList(hisWhere);
        /* List<String> inList = new ArrayList<String>(); */
        if (hisQueryList != null && hisQueryList.size() > 0) {
            String instaceIds = "";
            List<String> instaceIdsList = new ArrayList<String>();
            String stratTask = "";
            for (ActAljoinQueryHis actAljoinQueryHis : hisQueryList) {
                // instaceIds += actAljoinQueryHis.getProcessInstanceId() + ",";
                instaceIdsList.add(actAljoinQueryHis.getProcessInstanceId().toString());
                stratTask += actAljoinQueryHis.getStartTask() + ",";
            }
            hisQueryList.clear();
            if (instaceIdsList.size() > 0) {
                // instaceIds = instaceIds.substring(0, instaceIds.length() - 1);
                Where<ActHiActinst> hiActWhere = new Where<ActHiActinst>();
                if (instaceIdsList.size() > 1000) {
                    int instNo = 0;
                    List<String> instList = new ArrayList<String>();
                    for (String string : instaceIdsList) {
                        instList.add(string);
                        instNo += 1;
                        if (instNo == 1000) {
                            hiActWhere.in("PROC_INST_ID_", instList).or();
                            instNo = 0;
                            instList.clear();
                        }
                    }
                    if (instNo == 0) {
                        hiActWhere.where("1={0}", 1);
                    } else {
                        hiActWhere.in("PROC_INST_ID_", instList);
                    }
                } else {
                    hiActWhere.in("PROC_INST_ID_", instaceIdsList);
                }
                hiActWhere.setSqlSelect("ACT_TYPE_,TASK_ID_,PROC_INST_ID_");
                hiActWhere.between("START_TIME_", dfs.parse(df.format(startTime) + " 00:00:00"),
                    dfs.parse(df.format(endTime) + " 23:59:59"));
                List<ActHiActinst> activityList = actHiActinstService.selectList(hiActWhere);

                // instaceIds = "";
                List<String> pids = new ArrayList<String>();
                for (ActHiActinst historicActivityInstance : activityList) {
                    String type = historicActivityInstance.getActType();
                    if ("userTask".equals(type)) {
                        instaceIds += historicActivityInstance.getTaskId() + ",";
                        pids.add(historicActivityInstance.getProcInstId());
                    }
                }
                activityList.clear();
                if (instaceIds != null && instaceIds.length() > 0) {
                    HistoricTaskInstanceQuery hisTaskQurey = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceIdIn(pids).taskCreatedAfter(dfs.parse(df.format(startTime) + " 00:00:00"))
                        .taskCreatedBefore(dfs.parse(df.format(endTime) + " 23:59:59"));
                    pids.clear();
                    List<HistoricTaskInstance> hisTask = hisTaskQurey.list();
                    if (hisTask != null && hisTask.size() > 0) {
                        for (HistoricTaskInstance historicTaskInstance : hisTask) {
                            String taskid = historicTaskInstance.getId();
                            if (stratTask.indexOf(taskid) > -1) {
                                continue;
                            }
                            if (instaceIds.indexOf(taskid) > -1) {
                                String assId = historicTaskInstance.getAssignee();
                                if (assId != null && !"".equals(assId)) {
                                    if (returnMap.containsKey(assId)) {
                                        ActRegulationVO tmpapp = returnMap.get(assId);
                                        BigDecimal sinTime = tmpapp.getSth();// 签收
                                        BigDecimal tolTime = tmpapp.getTotalTime();// 办理
                                        int sum = tmpapp.getSumNo();// 文件数量
                                        int tolsum = tmpapp.getTotalNo();// 文件数量

                                        if (historicTaskInstance.getClaimTime() != null) {
                                            // 签收任务
                                            sum += 1;
                                            BigDecimal lTime
                                                = BigDecimal.valueOf(historicTaskInstance.getClaimTime().getTime()
                                                    - historicTaskInstance.getStartTime().getTime());
                                            sinTime = sinTime.add(lTime);
                                            if (historicTaskInstance.getEndTime() != null) {
                                                lTime = BigDecimal.valueOf(historicTaskInstance.getEndTime().getTime()
                                                    - historicTaskInstance.getClaimTime().getTime());
                                                tolsum += 1;
                                                tolTime = tolTime.add(lTime);
                                            } else {
                                                /*
                                                 * lTime =
                                                 * BigDecimal.valueOf(new
                                                 * Date().getTime() -
                                                 * historicTaskInstance.
                                                 * getClaimTime().getTime());
                                                 * tolsum += 1; tolTime =
                                                 * tolTime.add(lTime);
                                                 */
                                            }
                                        } else {
                                            /*
                                             * BigDecimal lTime =
                                             * BigDecimal.valueOf(new
                                             * Date().getTime() -
                                             * historicTaskInstance.getStartTime
                                             * ().getTime());
                                             * System.out.println(new
                                             * Date().getTime());
                                             * System.out.println(
                                             * historicTaskInstance.getStartTime
                                             * ().getTime()); sinTime =
                                             * sinTime.add(lTime);
                                             */
                                        }
                                        tmpapp.setSth(sinTime);
                                        tmpapp.setTotalTime(tolTime);
                                        tmpapp.setSumNo(sum);
                                        tmpapp.setTotalNo(tolsum);
                                        returnMap.put(assId, tmpapp);
                                    }
                                }
                                /*
                                 * else { // 未签收任务 if
                                 * (historicTaskInstance.getEndTime() == null) {
                                 * List identityLinkList =
                                 * taskService.getIdentityLinksForTask( taskid);
                                 * if (identityLinkList != null &&
                                 * identityLinkList.size() > 0) { for (Iterator
                                 * iterator = identityLinkList.iterator();
                                 * iterator.hasNext();) { IdentityLink
                                 * identityLink = (IdentityLink)
                                 * iterator.next(); if (identityLink.getUserId()
                                 * != null) { String userId =
                                 * identityLink.getUserId(); if
                                 * (returnMap.containsKey(userId)) {
                                 * ActRegulationVO tmpapp =
                                 * returnMap.get(userId); BigDecimal sinTime =
                                 * tmpapp.getSth();// 签收 int sum =
                                 * tmpapp.getSumNo();// 文件数量 sum += 1;
                                 * BigDecimal lTime = BigDecimal.valueOf(new
                                 * Date().getTime() -
                                 * historicTaskInstance.getStartTime().
                                 * getTime()); sinTime = sinTime.add(lTime);
                                 * tmpapp.setSth(sinTime); tmpapp.setSumNo(sum);
                                 * returnMap.put(userId, tmpapp); } } if
                                 * (identityLink.getGroupId() != null) { //
                                 * 根据组获得对应人员 List<User> linkList =
                                 * identityService.createUserQuery()
                                 * .memberOfGroup(identityLink.getGroupId())
                                 * .list(); if (linkList != null &&
                                 * linkList.size() > 0) { for (User user :
                                 * linkList) { String userId = user.getId(); if
                                 * (returnMap.containsKey(userId)) {
                                 * ActRegulationVO tmpapp =
                                 * returnMap.get(userId); BigDecimal sinTime =
                                 * tmpapp.getSth();// 签收 int sum =
                                 * tmpapp.getSumNo();// 文件数量 sum += 1;
                                 * BigDecimal lTime = BigDecimal.valueOf( new
                                 * Date().getTime() - historicTaskInstance
                                 * .getStartTime().getTime()); sinTime =
                                 * sinTime.add(lTime); tmpapp.setSth(sinTime);
                                 * tmpapp.setSumNo(sum); returnMap.put(userId,
                                 * tmpapp); } } } } } } } }
                                 */
                            }
                        }
                    }
                }
            }
        }
        if (returnMap != null && returnMap.size() > 0) {
            for (ActRegulationVO v : returnMap.values()) {
                vo.add(v);
            }
        }

        return vo;
    }

    @SuppressWarnings("unused")
    @Override
    public RetMsg selectInvalid(Long customUserId, String invalid) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (invalid != null && invalid.length() > 0) {
            invalid = invalid.substring(0, invalid.length() - 1);
            invalid = invalid.replaceAll("'", "");
            String qId[] = invalid.split(",");
            Set<String> pIds = new HashSet<String>();
            Map<String, String> taskMap = new HashMap<String, String>();
            // 获取所有ACT组与对应人员关系
            Where<ActIdMembership> idMembershipWhere = new Where<ActIdMembership>();
            idMembershipWhere.where("1={0}", 1);
            List<ActIdMembership> actIdMembershipList = actIdMembershipService.selectList(idMembershipWhere);
            Map<String, String> idMemberMap = new HashMap<String, String>();
            if (actIdMembershipList != null && actIdMembershipList.size() > 0) {
                for (ActIdMembership actIdMembership : actIdMembershipList) {
                    String userId = "";
                    if (idMemberMap.containsKey(actIdMembership.getGroupId())) {
                        userId = idMemberMap.get(actIdMembership.getGroupId());
                    }
                    if (userId.indexOf(actIdMembership.getUserId()) > -1) {
                        continue;
                    }
                    userId += actIdMembership.getUserId() + ",";
                    idMemberMap.put(actIdMembership.getGroupId(), userId);
                }
            }
            Where<ActRuIdentitylink> ruIdentitylinkWhere = new Where<ActRuIdentitylink>();
            ruIdentitylinkWhere.isNotNull("TASK_ID_");
            List<ActRuIdentitylink> linkList = actRuIdentitylinkService.selectList(ruIdentitylinkWhere);
            Map<String, List<ActRuIdentitylink>> runIdMap = new HashMap<String, List<ActRuIdentitylink>>();
            if (linkList != null && linkList.size() > 0) {
                for (ActRuIdentitylink actRuIdentitylink : linkList) {
                    List<ActRuIdentitylink> tmpLinkList = new ArrayList<ActRuIdentitylink>();
                    if (runIdMap.containsKey(actRuIdentitylink.getTaskId())) {
                        tmpLinkList.addAll(runIdMap.get(actRuIdentitylink.getTaskId()));
                    }
                    tmpLinkList.add(actRuIdentitylink);
                    runIdMap.put(actRuIdentitylink.getTaskId(), tmpLinkList);
                }
            }
            Map<String, List<String>> pUser = new HashMap<String, List<String>>();
            for (String string : qId) {
                List<String> userList = new ArrayList<String>();
                Task task = taskService.createTaskQuery().taskId(string).singleResult();
                pIds.add(task.getProcessInstanceId());
                taskMap.put(task.getProcessInstanceId(), string);
                String userids = "";
                if (task.getAssignee() != null && !"".equals(task.getAssignee())) {
                    // 获取当前环节签收人
                    userids = task.getAssignee() + ",";
                } else {
                    List<ActRuIdentitylink> idenList = runIdMap.get(task.getId());
                    // 获取当前环节候选人
                    if (idenList == null) {
                        continue;
                    }
                    for (ActRuIdentitylink identityLink : idenList) {
                        if (identityLink.getUserId() != null && !"".equals(identityLink.getUserId())) {
                            userids = userids + identityLink.getUserId() + ",";
                            continue;
                        }
                        String deptId = identityLink.getGroupId();
                        if (idMemberMap.containsKey(deptId)) {
                            userids += idMemberMap.get(deptId);

                        }
                    }
                    if (userids.length() > 0) {
                        String arrUser[] = userids.split(",");
                        for (String string2 : arrUser) {
                            userList.add(string2);
                        }
                    }
                    pUser.put(task.getProcessInstanceId(), userList);
                }
            }
            List<HistoricProcessInstance> hisList
                = historyService.createHistoricProcessInstanceQuery().processInstanceIds(pIds).list();
            if (hisList != null && hisList.size() > 0) {
                for (HistoricProcessInstance historicProcessInstance : hisList) {
                    String bizId = "";
                    if (historicProcessInstance.getBusinessKey().indexOf(",mee,") > -1) {
                        runtimeService.deleteProcessInstance(historicProcessInstance.getId(), null);
                        Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
                        queryWhere.eq("process_instance_id", historicProcessInstance.getId());
                        actAljoinQueryService.delete(queryWhere);
                        Where<ActAljoinQueryHis> queryHisWhere = new Where<ActAljoinQueryHis>();
                        queryHisWhere.eq("process_instance_id", historicProcessInstance.getId());
                        actAljoinQueryHisService.delete(queryHisWhere);
                        Date date = new Date();
                        if (org.apache.commons.lang.StringUtils.isNotEmpty(bizId)) {
                            MeeOutsideMeeting meeOutsideMeeting
                                = meeOutsideMeetingService.selectById(Long.parseLong(bizId));
                            if (null != meeOutsideMeeting) {
                                meeOutsideMeeting.setAuditStatus(2);// 审核通过
                                meeOutsideMeeting.setAuditTime(date);
                                meeOutsideMeeting.setAuditReason("流程作废，审核失败");
                                meeOutsideMeetingService.updateById(meeOutsideMeeting);
                            }
                        }

                        // 环节办理人待办数量-1
                        AutDataStatistics aut = new AutDataStatistics();
                        aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
                        aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
                        autDataStatisticsService.minusList(aut, pUser.get(historicProcessInstance.getId()));
                        continue;
                    }
                    if (historicProcessInstance.getBusinessKey().indexOf(",off,") > -1) {
                        runtimeService.deleteProcessInstance(historicProcessInstance.getId(), null);
                        Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
                        queryWhere.eq("process_instance_id", historicProcessInstance.getId());
                        actAljoinQueryService.delete(queryWhere);
                        Where<ActAljoinQueryHis> queryHisWhere = new Where<ActAljoinQueryHis>();
                        queryHisWhere.eq("process_instance_id", historicProcessInstance.getId());
                        actAljoinQueryHisService.delete(queryHisWhere);
                        // 修改原文件状态
                        Date date = new Date();
                        if (org.apache.commons.lang.StringUtils.isNotEmpty(bizId)) {
                            OffMonthReport offMonthReport = offMonthReportService.selectById(Long.parseLong(bizId));
                            if (null != offMonthReport) {
                                offMonthReport.setStatus(0);// 审核失败-原文件可编辑
                                offMonthReport.setAuditStatus(2);// 审核不通过
                                offMonthReport.setAuditTime(date);
                                offMonthReport.setAuditReason("审核不通过");
                                offMonthReportService.updateById(offMonthReport);
                            }
                        }
                        // 环节办理人待办数量-1
                        AutDataStatistics aut = new AutDataStatistics();
                        aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
                        aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
                        autDataStatisticsService.minusList(aut, pUser.get(historicProcessInstance.getId()));
                        continue;
                    }
                    if (historicProcessInstance.getBusinessKey().indexOf(",att,") > -1) {
                        Where<AttSignInOutHis> attWhere = new Where<AttSignInOutHis>();
                        attWhere.eq("am_sign_in_proc_inst_id", historicProcessInstance.getId())
                            .or("am_sign_out_proc_inst_id = {0}", historicProcessInstance.getId())
                            .or("pm_sign_in_proc_inst_id = {0}", historicProcessInstance.getId())
                            .or("pm_sign_out_proc_inst_id={0}", historicProcessInstance.getId());
                        List<AttSignInOutHis> signInOutHisList = attSignInOutHisService.selectList(attWhere);
                        Where<AttSignInOut> signWhere = new Where<AttSignInOut>();
                        signWhere.eq("am_sign_in_proc_inst_id", historicProcessInstance.getId())
                            .or("am_sign_out_proc_inst_id = {0}", historicProcessInstance.getId())
                            .or("pm_sign_in_proc_inst_id = {0}", historicProcessInstance.getId())
                            .or("pm_sign_out_proc_inst_id={0}", historicProcessInstance.getId());
                        List<AttSignInOut> signInOutList = attSignInOutService.selectList(signWhere);
                        runtimeService.deleteProcessInstance(historicProcessInstance.getId(), null);
                        Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
                        queryWhere.eq("process_instance_id", historicProcessInstance.getId());
                        actAljoinQueryService.delete(queryWhere);
                        Where<ActAljoinQueryHis> queryHisWhere = new Where<ActAljoinQueryHis>();
                        queryHisWhere.eq("process_instance_id", historicProcessInstance.getId());
                        actAljoinQueryHisService.delete(queryHisWhere);
                        Date date = new Date();
                        // 更新表数据
                        if (signInOutHisList != null && signInOutHisList.size() > 0 && signInOutList != null
                            && signInOutList.size() > 0) {
                            for (AttSignInOut signInOut : signInOutList) {
                                if (null != signInOut) {
                                    if (null != signInOut.getAmSignInStatus()) {
                                        if (2 == signInOut.getAmSignInPatchStatus()
                                            && signInOut.getAmSignInProcInstId() != null) {
                                            if (signInOut.getAmSignInProcInstId()
                                                .equals(historicProcessInstance.getId())) {
                                                signInOut.setAmSignInPatchAuditTime(date);
                                                signInOut.setAmSignInPatchStatus(4);
                                            }
                                        }
                                    }
                                    if (null != signInOut.getAmSignOutStatus()) {
                                        if (2 == signInOut.getAmSignOutPatchStatus()
                                            && signInOut.getAmSignOutProcInstId() != null) {
                                            if (signInOut.getAmSignOutProcInstId()
                                                .equals(historicProcessInstance.getId())) {
                                                signInOut.setAmSignOutPatchAuditTime(date);
                                                signInOut.setAmSignOutPatchStatus(4);
                                            }
                                        }
                                    }
                                    if (null != signInOut.getPmSignInPatchStatus()) {
                                        if (2 == signInOut.getPmSignInPatchStatus()
                                            && signInOut.getPmSignInProcInstId() != null) {
                                            if (signInOut.getPmSignInProcInstId()
                                                .equals(historicProcessInstance.getId())) {
                                                signInOut.setPmSignInPatchAuditTime(date);
                                                signInOut.setPmSignInPatchStatus(4);
                                            }
                                        }
                                    }
                                    if (null != signInOut.getPmSignOutPatchStatus()) {
                                        if (2 == signInOut.getPmSignOutPatchStatus()
                                            && signInOut.getPmSignOutProcInstId() != null) {
                                            if (signInOut.getPmSignOutProcInstId()
                                                .equals(historicProcessInstance.getId())) {
                                                signInOut.setPmSignOutPatchAuditTime(date);
                                                signInOut.setPmSignOutPatchStatus(4);
                                            }
                                        }
                                    }
                                }

                                attSignInOutService.updateBatchById(signInOutList);
                                for (AttSignInOutHis signInOutHis : signInOutHisList) {
                                    if (null != signInOutHis) {
                                        if (null != signInOutHis.getAmSignInStatus()) {
                                            if (2 == signInOutHis.getAmSignInPatchStatus()
                                                && signInOutHis.getAmSignInProcInstId() != null) {
                                                if (signInOutHis.getAmSignInProcInstId()
                                                    .equals(historicProcessInstance.getId())) {
                                                    signInOutHis.setAmSignInPatchAuditTime(date);
                                                    signInOutHis.setAmSignInPatchStatus(4);
                                                }
                                            }
                                        }
                                        if (null != signInOutHis.getAmSignOutPatchStatus()) {
                                            if (2 == signInOutHis.getAmSignOutPatchStatus()
                                                && signInOutHis.getAmSignOutProcInstId() != null) {
                                                if (signInOutHis.getAmSignOutProcInstId()
                                                    .equals(historicProcessInstance.getId())) {
                                                    signInOutHis.setAmSignOutPatchAuditTime(date);
                                                    signInOutHis.setAmSignOutPatchStatus(4);
                                                }
                                            }
                                        }
                                        if (null != signInOutHis.getPmSignInPatchStatus()) {
                                            if (2 == signInOutHis.getPmSignInPatchStatus()
                                                && signInOutHis.getPmSignInProcInstId() != null) {
                                                if (signInOutHis.getPmSignInProcInstId()
                                                    .equals(historicProcessInstance.getId())) {
                                                    signInOutHis.setPmSignInPatchAuditTime(date);
                                                    signInOutHis.setPmSignInPatchStatus(4);
                                                }
                                            }
                                        }
                                        if (null != signInOutHis.getPmSignOutPatchStatus()) {
                                            if (2 == signInOutHis.getPmSignOutPatchStatus()
                                                && signInOutHis.getPmSignOutProcInstId() != null) {
                                                if (signInOutHis.getPmSignOutProcInstId()
                                                    .equals(historicProcessInstance.getId())) {
                                                    signInOutHis.setPmSignOutPatchAuditTime(date);
                                                    signInOutHis.setPmSignOutPatchStatus(4);
                                                }
                                            }
                                        }
                                    }
                                }
                                attSignInOutHisService.updateBatchById(signInOutHisList);
                            }
                        }
                        AutDataStatistics aut = new AutDataStatistics();
                        aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
                        aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
                        autDataStatisticsService.minusList(aut, pUser.get(historicProcessInstance.getId()));
                        continue;
                    }
                    if (historicProcessInstance.getBusinessKey().indexOf(",pub,") > -1) {
                        runtimeService.deleteProcessInstance(historicProcessInstance.getId(), null);
                        Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
                        queryWhere.eq("process_instance_id", historicProcessInstance.getId());
                        actAljoinQueryService.delete(queryWhere);
                        Where<ActAljoinQueryHis> queryHisWhere = new Where<ActAljoinQueryHis>();
                        queryHisWhere.eq("process_instance_id", historicProcessInstance.getId());
                        actAljoinQueryHisService.delete(queryHisWhere);
                        Date date = new Date();
                        if (org.apache.commons.lang.StringUtils.isNotEmpty(bizId)) {
                            PubPublicInfo publicInfo = pubPublicInfoService.selectById(Long.parseLong(bizId));
                            if (null != publicInfo) {
                                publicInfo.setAuditStatus(2);// 审核通过
                                publicInfo.setAuditTime(date);
                                publicInfo.setAuditReason("审核不通过");
                                pubPublicInfoService.updateById(publicInfo);
                            }
                        }
                        AutDataStatistics aut = new AutDataStatistics();
                        aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
                        aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
                        autDataStatisticsService.minusList(aut, pUser.get(historicProcessInstance.getId()));
                        continue;
                    }
                    if (historicProcessInstance.getBusinessKey().indexOf(",ioaReceiveFile,") > -1) {
                        Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
                        queryWhere.eq("process_instance_id", historicProcessInstance.getId());
                        actAljoinQueryService.delete(queryWhere);
                        Where<ActAljoinQueryHis> queryHisWhere = new Where<ActAljoinQueryHis>();
                        queryHisWhere.eq("process_instance_id", historicProcessInstance.getId());
                        actAljoinQueryHisService.delete(queryHisWhere);
                        runtimeService.deleteProcessInstance(historicProcessInstance.getId(), null);
                        AutDataStatistics aut = new AutDataStatistics();
                        aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
                        aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
                        autDataStatisticsService.minusList(aut, pUser.get(historicProcessInstance.getId()));
                        Where<IoaReceiveFile> receiveWhere = new Where<IoaReceiveFile>();
                        receiveWhere.eq("process_instance_id", historicProcessInstance.getId());
                        List<IoaReceiveFile> fileList = ioaReceiveFileService.selectList(receiveWhere);
                        if (fileList != null && fileList.size() > 0) {
                            String bizIds = "";
                            for (IoaReceiveFile ioaReceiveFile : fileList) {
                                ioaReceiveFileService.deleteById(ioaReceiveFile.getId());
                            }
                            autDataStatisticsService.minusList(aut, pUser.get(historicProcessInstance.getId()));
                        } else {
                            runtimeService.deleteProcessInstance(historicProcessInstance.getId(), null);
                        }
                        continue;
                    }
                    Where<ActAljoinQueryHis> hisWhere = new Where<ActAljoinQueryHis>();
                    hisWhere.eq("process_instance_id", historicProcessInstance.getId());
                    actAljoinQueryHisService.delete(hisWhere);
                    Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
                    where.eq("process_instance_id", historicProcessInstance.getId());
                    actAljoinQueryService.delete(where);
                    // 删除非特殊流程
                    runtimeService.deleteProcessInstance(historicProcessInstance.getId(), null);
                    AutDataStatistics aut = new AutDataStatistics();
                    aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
                    aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
                    autDataStatisticsService.minusList(aut, pUser.get(historicProcessInstance.getId()));
                }
            }
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage("作废流程成功!!!");
        } else {
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage("请选择作废流程!!!");
        }
        return retMsg;
    }

    @Override
    public RetMsg deliveryPersonnel(String taskKey,String processInstanceId,String assignees) throws Exception {
        RetMsg retMsg = new RetMsg();
        List<String> assigneeList = Arrays.asList(assignees.split(";"));


        Where<ActRuTask> taskWhere = new Where<ActRuTask>();
        taskWhere.setSqlSelect("id_,execution_id_,proc_inst_id_,name_,task_def_key_,proc_def_id_");
        taskWhere.eq("proc_inst_id_",processInstanceId);
        List<ActRuTask> ruTaskList = actRuTaskService.selectList(taskWhere);

        Map<String,ActRuTask> taskMap = new HashMap<String,ActRuTask>();
        for(ActRuTask actRuTask : ruTaskList){
            taskMap.put(actRuTask.getTaskDefKey(),actRuTask);
        }

        //通过任务ID查询出任务对应的用户信息
        Where<ActRuIdentitylink> identitylinkWhere = new Where<ActRuIdentitylink>();
        identitylinkWhere.setSqlSelect("id_,user_id_,task_id_,proc_inst_id_,proc_def_id_");
        identitylinkWhere.eq("proc_inst_id_",processInstanceId);
        identitylinkWhere.ne("type_","starter");
        List<ActRuIdentitylink> identitylinkList = actRuIdentitylinkService.selectList(identitylinkWhere);

        //构造推送消息的接收人
        Map<String,String> map = new HashMap<String,String>();
        String handle = "";
//        int i = 0;
        for(ActRuIdentitylink identitylink : identitylinkList){
            handle += identitylink.getUserId()+";";
            map.put(identitylink.getProcInstId(),handle);
//            i++;
        }

        //删除原来的任务
        for(ActRuTask oldTask : ruTaskList){
            Task task = taskService.createTaskQuery().taskId(oldTask.getId()).singleResult();
            ((RuntimeServiceImpl)processEngine.getRuntimeService()).getCommandExecutor().execute(new DeleteRunningTaskCmd((TaskEntity)task));
        }

        for(String taskDefKey : taskMap.keySet()){
            ActRuTask actRuTask = taskMap.get(taskDefKey);
            String processDefinitionId = actRuTask.getProcDefId();
            String executionId = actRuTask.getExecutionId();
            ProcessDefinitionEntity definitionEntity =
                (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);

            ActivityImpl prototypeActivity = BaseProcessDefinitionUtils.getActivity(processEngine,
                definitionEntity.getId(), taskDefKey);

            String nextActivityId = prototypeActivity.getOutgoingTransitions().get(0).getDestination().getId();

            ActRuActivityDefinitionServiceImpl info = new ActRuActivityDefinitionServiceImpl();
            info.setProcessDefinitionId(definitionEntity.getId());
            info.setProcessInstanceId(processInstanceId);

            RuActivityDefinitionIntepreter radei = new RuActivityDefinitionIntepreter(info);
            radei.setPrototypeActivityId(taskDefKey);
            radei.setAssignees(assigneeList);
            radei.setNextActivityId(nextActivityId);

            ActivityImpl[] activities = null;
            //重新分配 选中的人员为办理人 （分：普通任务 多实例任务）
            //判断当前会签任务节点是 并行会签还是串行会签
            MultiInstanceLoopCharacteristics currentMulti
                = actActivitiService.getMultiInstance(processInstanceId, taskDefKey);
            if (null != currentMulti) {
                if (!currentMulti.isSequential()) {
                    // 并行
                    radei.setSequential(false);
                } else {
                    // 串行
                    radei.setSequential(true);
                }

                //创建多实例活动
                activities = new MultiInstanceActivityCreatorBase().createActivities(processEngine, definitionEntity, info);
            }else{
                activities = new ChainedActivitiesCreatorBase().createActivities(processEngine, definitionEntity, info);
            }

            //生成任务
            ActivityImpl activity = BaseProcessDefinitionUtils.getActivity(processEngine,
                processDefinitionId ,activities[0].getId());

            executeCommand(new StartActivityCmd(executionId, activity));

            ActRunTimeExecution runTimeExecution = new ActRunTimeExecution();
            runTimeExecution.setProcInstId(processInstanceId);
            actRunTimeExecutionService.updateExecution(runTimeExecution);

            info.serializeProperties();
            setDeliveryPersonnel(processInstanceId,assigneeList,currentMulti);
        }

        String taskAuth = taskKey +",";
        for(String assignee : assigneeList){
            taskAuth += assignee+"#";
        }

        HistoricProcessInstance hiInstance = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(processInstanceId).singleResult();
        String businessKey = hiInstance.getBusinessKey();

        taskAuth = taskAuth.substring(0,taskAuth.lastIndexOf("#"));
        map.put("taskAuth",taskAuth);
        map.put("processInstanceId",processInstanceId);
        map.put("businessKey",businessKey);
        retMsg.setObject(map);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 执行命令
     *
     * @param command
     */
    private void executeCommand(Command<Void> command) {
        ((RuntimeServiceImpl)processEngine.getRuntimeService()).getCommandExecutor().execute(command);
    }

    /**
     * 给任务指定特送人员
     *
     * @param processInstanceId 流程实例ID
     *
     * @param assigneeList      特送人员列表
     *
     * @throws Exception
     */
    public void setDeliveryPersonnel(String processInstanceId, List<String> assigneeList,MultiInstanceLoopCharacteristics currentMulti) throws Exception {
        List<Task> taskList
            = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().list();

        String assignee = "";
        if(null == currentMulti){
            for (int i = 0; i < assigneeList.size(); i++) {
                assignee = assigneeList.get(i);
                taskService.addUserIdentityLink(taskList.get(0).getId(),assignee,"candidate");
            }
        }else{
            for (int i = 0; i < assigneeList.size(); i++) {
                Task task = taskList.get(i);
                assignee = assigneeList.get(i);
                taskService.setAssignee(task.getId(), assignee);
                taskService.addUserIdentityLink(task.getId(),assignee,"candidate");
            }
        }
    }

}
