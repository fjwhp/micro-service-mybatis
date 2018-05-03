package aljoin.act.service;

import aljoin.act.dao.entity.*;
import aljoin.act.dao.mapper.ActAljoinTaskSignInfoMapper;
import aljoin.act.iservice.ActAljoinActivityLogService;
import aljoin.act.iservice.ActAljoinTaskAssigneeService;
import aljoin.act.iservice.ActAljoinTaskSignInfoService;
import aljoin.act.iservice.ActRuTaskService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加签信息 (服务实现类)
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
@Service
public class ActAljoinTaskSignInfoServiceImpl extends ServiceImpl<ActAljoinTaskSignInfoMapper, ActAljoinTaskSignInfo>
    implements ActAljoinTaskSignInfoService {

    @Resource
    private ActAljoinTaskSignInfoMapper mapper;
    @Resource
    private ActAljoinActivityLogService actAljoinActivityLogService;
    @Resource
    private TaskService taskService;
    @Resource
    private ActRuTaskService actRuTaskService;
    @Resource
    private ActAljoinTaskSignInfoService actAljoinTaskSignInfoService;
    @Resource
    private ActAljoinTaskAssigneeService actAljoinTaskAssigneeService;
    @Resource
    private HistoryService historyService;

    @Override
    public Page<ActAljoinTaskSignInfo> list(PageBean pageBean, ActAljoinTaskSignInfo obj) throws Exception {
        Where<ActAljoinTaskSignInfo> where = new Where<ActAljoinTaskSignInfo>();
        where.orderBy("create_time", false);
        Page<ActAljoinTaskSignInfo> page
            = selectPage(new Page<ActAljoinTaskSignInfo>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinTaskSignInfo obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public Map<String, Object> getLastSignTaskIdList(String taskId, String taskDefKey, String processInstanceId,
        String assignee) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        Where<ActAljoinTaskSignInfo> taskSignInfoWhere = new Where<ActAljoinTaskSignInfo>();
        taskSignInfoWhere.setSqlSelect("id,finish_type,task_owner_id,task_owner_name");
        taskSignInfoWhere.eq("sign_task_id", taskId);
        ActAljoinTaskSignInfo taskSignInfo = selectOne(taskSignInfoWhere);
        List<String> allSignTaskIdList = new ArrayList<String>();
        List<String> signRuTaskIdList = new ArrayList<String>();
        allSignTaskIdList.add(taskId);
        String handler = "";
        if(null != taskSignInfo){
            handler = String.valueOf(taskSignInfo.getTaskOwnerId());
        }else{
            Where<ActAljoinActivityLog> activityLogWhere = new Where<ActAljoinActivityLog>();
            activityLogWhere.setSqlSelect(
                "id,operate_user_id,operate_full_name,operate_status,receive_user_ids,current_task_def_key,proc_inst_id,operate_status,current_task_id");
            activityLogWhere.eq("current_task_def_key", taskDefKey);
            activityLogWhere.eq("proc_inst_id", processInstanceId);
            activityLogWhere.orderBy("id",true);
            ActAljoinActivityLog actAljoinActivityLog = actAljoinActivityLogService.selectOne(activityLogWhere);
            if(null != actAljoinActivityLog && null != actAljoinActivityLog.getOperateUserId()){
                handler = actAljoinActivityLog.getOperateUserId();
            }
        }
        List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
        for(HistoricTaskInstance hisTask : hisTaskList){
            allSignTaskIdList.add(hisTask.getId());
        }
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        for(Task tsk : taskList){
            signRuTaskIdList.add(tsk.getId());
        }
        Where<ActAljoinTaskSignInfo> signInfoWhere = new Where<ActAljoinTaskSignInfo>();
        signInfoWhere.setSqlSelect(
            "id,task_key,process_instance_id,finish_type,task_owner_id,task_owner_name,sign_task_id,is_back_owner,task_sign_user_id,task_signed_user_id");
        signInfoWhere.eq("task_key", taskDefKey);
        signInfoWhere.eq("process_instance_id", processInstanceId);
        ActAljoinTaskSignInfo signInfo = selectOne(signInfoWhere);

        // 同一批所有加签任务ID
        map.put("allSignTaskIdList", allSignTaskIdList);
        // 加签运行中任务ID
        map.put("signRuTaskIdList", signRuTaskIdList);
        //加签方式 0：直接提交下一环节 1:返回原办理人
        map.put("signInfo", signInfo);
        //返回原办理人ID
        map.put("handler", handler);
        //fishType 0:未操作 1：加签完成 2：提交完成 3：返回原办理人
        map.put("finshType",null != taskSignInfo ? taskSignInfo.getFinishType() : 1);
        return map;
    }

    @Override
    public boolean isSame(String taskDefKey, String processInstanceId) throws Exception {
        boolean isSame = true;
        Map<Long,Long> map = new HashMap<Long,Long>();
        Where<ActAljoinTaskSignInfo> signInfoWhere = new Where<ActAljoinTaskSignInfo>();
        signInfoWhere.setSqlSelect(
            "id,task_key,process_instance_id,task_owner_id,task_owner_name,sign_task_id,is_back_owner,task_sign_user_id,task_signed_user_id");
        signInfoWhere.eq("task_key", taskDefKey);
        signInfoWhere.eq("process_instance_id", processInstanceId);
        List<ActAljoinTaskSignInfo> signInfoList = selectList(signInfoWhere);
        for (ActAljoinTaskSignInfo signInfo : signInfoList) {
            map.put(signInfo.getTaskOwnerId(),signInfo.getTaskOwnerId());
        }
        if(map.size() > 0){
         isSame = false;
        }
        return isSame;
    }

    @Override
    public Map<String, Object> getSignParam(Task task) throws Exception {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        // 加签标识：0:提交下一环节 1：返回原办理人 2：继续流转
        int isBackOwner = 0;
        // 加签之后是否可以提交 0：否 1：是
        int isSubmit = 1;
        //是否存在加签 0：否 1：是
        int isAddSign = 0;
        //是否可以提交下一环节 0：否 1：是
        int isPass = 0;
        //原办理人
        String handler = "";
        // 查询出最后一级加签任务
        Map<String, Object> signInfoMap = getLastSignTaskIdList(task.getId(), task.getTaskDefinitionKey(),
            task.getProcessInstanceId(), task.getAssignee());
        List<String> signRuTaskIdList = new ArrayList<>();
        if (signInfoMap.size() > 0) {
            signRuTaskIdList = (List<String>)signInfoMap.get("signRuTaskIdList");
            ActAljoinTaskSignInfo signInfo = (ActAljoinTaskSignInfo)signInfoMap.get("signInfo");
            handler = (String)signInfoMap.get("handler");
            Integer finshType = (Integer)signInfoMap.get("finshType");
            if (signRuTaskIdList.size() == 1) {
                String ruTaskId = signRuTaskIdList.get(0);
                if(null != signInfo){
                    if (signInfo.getIsBackOwner() == 1 && ruTaskId.equals(task.getId()) && finshType != 3 ) {
                        // 返回办理人
                        isBackOwner = 1;
                    }
                    if (task.getAssignee().equals(String.valueOf(signInfo.getTaskOwnerId())) && finshType != 3) {
                        isBackOwner = 0;
                    }
                }
                isPass = 1;
            } else {
                // 继续流转
                isBackOwner = 2;
            }
        }
        ActAljoinTaskSignInfo actAljoinTaskSignInfo = actAljoinTaskSignInfoService.getSignTaskInfo(task.getId());
        if(null != actAljoinTaskSignInfo || signRuTaskIdList.size() > 1){
            isAddSign = 1;
        }
        paramMap.put("isSubmit", isSubmit);
        paramMap.put("isBackOwner", isBackOwner);
        paramMap.put("isAddSign", isAddSign);
        paramMap.put("handler",handler);
        paramMap.put("isPass",isPass);
        return paramMap;
    }

    @Override
    public void insertSignTaskInfo(List<Long> uidList,Task task,ActAljoinBpmnRun bpmnRun,Map<Long,String> userMap,Integer finshType) throws Exception {
        //向加签信息表 插入记录(怎么获得插入的这些人的任务)
        Where<ActRuTask> ruTaskWhere = new Where<ActRuTask>();
        ruTaskWhere.setSqlSelect("id_,execution_id_,proc_inst_id_,proc_def_id_,name_,task_def_key_,assignee_");
        ruTaskWhere.in("assignee_",uidList);
        ruTaskWhere.eq("proc_inst_id_",task.getProcessInstanceId());
        ruTaskWhere.eq("task_def_key_",task.getTaskDefinitionKey());
        List<ActRuTask> ruTaskList = actRuTaskService.selectList(ruTaskWhere);

        Integer isBackOwner = 0;
        String taskSignUserIds = "";
        String taskSignUserNames = "";
        String allTaskIds = "";
        Long taskOwnerId = null;
        String taskOwnerName = "";

        Where<ActAljoinTaskAssignee> actAljoinTaskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
        actAljoinTaskAssigneeWhere.setSqlSelect("id,operate_auth_ids,bpmn_id,task_id");
        actAljoinTaskAssigneeWhere.eq("bpmn_id",bpmnRun.getOrgnlId());
        actAljoinTaskAssigneeWhere.eq("version",bpmnRun.getTaskAssigneeVersion());
        actAljoinTaskAssigneeWhere.eq("task_id",task.getTaskDefinitionKey());
        ActAljoinTaskAssignee taskAssignee = actAljoinTaskAssigneeService.selectOne(actAljoinTaskAssigneeWhere);

        if(null != taskAssignee){
            if(taskAssignee.getOperateAuthIds().indexOf("aljoin-task-addsign2") > -1){
                isBackOwner = 1;
            }
        }

        for(ActRuTask ruTask : ruTaskList){
            taskSignUserIds += ruTask.getAssignee()+",";
            taskSignUserNames += userMap.get(Long.valueOf(ruTask.getAssignee()))+",";
            allTaskIds += ruTask.getId()+",";
        }

        Where<ActAljoinTaskSignInfo> signInfoWhere = new Where<ActAljoinTaskSignInfo>();
        signInfoWhere.setSqlSelect("id,task_key,task_owner_id,task_owner_name,sign_task_id");
        signInfoWhere.eq("task_key",task.getTaskDefinitionKey());
        signInfoWhere.eq("process_instance_id",task.getProcessInstanceId());
        List<ActAljoinTaskSignInfo> actAljoinTaskSignInfos = actAljoinTaskSignInfoService.selectList(signInfoWhere);
        if(actAljoinTaskSignInfos.size() == 0){
            taskOwnerId = Long.valueOf(task.getAssignee());
            taskOwnerName = userMap.get(Long.valueOf(task.getAssignee()));
        }else{
            ActAljoinTaskSignInfo actAljoinTaskSignInfo = actAljoinTaskSignInfos.get(0);
            taskOwnerId = actAljoinTaskSignInfo.getTaskOwnerId();
            taskOwnerName = actAljoinTaskSignInfo.getTaskOwnerName();
        }


        List<ActAljoinTaskSignInfo> taskSignInfoList = new ArrayList<ActAljoinTaskSignInfo>();
        for(ActRuTask ruTask : ruTaskList){
            ActAljoinTaskSignInfo actAljoinTaskSignInfo = new ActAljoinTaskSignInfo();
            actAljoinTaskSignInfo.setBpmnId(bpmnRun.getOrgnlId());
            actAljoinTaskSignInfo.setProcessInstanceId(ruTask.getProcInstId());
            actAljoinTaskSignInfo.setProcessDefId(ruTask.getProcDefId());
            actAljoinTaskSignInfo.setExecutionId(ruTask.getExecutionId());
            actAljoinTaskSignInfo.setTaskId(task.getId());
            actAljoinTaskSignInfo.setTaskKey(task.getTaskDefinitionKey());
            actAljoinTaskSignInfo.setTaskName(task.getName());
            actAljoinTaskSignInfo.setSignTaskId(ruTask.getId());
            //1,2 1,3
            actAljoinTaskSignInfo.setTaskIds(task.getId()+","+ruTask.getId());
            //最开始的任务ID
            actAljoinTaskSignInfo.setTaskOwnerId(taskOwnerId);
            //最开始的任务名称
            actAljoinTaskSignInfo.setTaskOwnerName(taskOwnerName);
            actAljoinTaskSignInfo.setTaskSignUserId(Long.valueOf(task.getAssignee()));
            actAljoinTaskSignInfo.setTaskSignUserName(userMap.get(Long.valueOf(task.getAssignee())));
            actAljoinTaskSignInfo.setTaskSignedUserId(StringUtils.isNotEmpty(ruTask.getAssignee()) ? Long.valueOf(ruTask.getAssignee()) : 0);
            actAljoinTaskSignInfo.setTaskSignedUserName(StringUtils.isNotEmpty(ruTask.getAssignee()) ? userMap.get(Long.valueOf(ruTask.getAssignee())) : "");
            actAljoinTaskSignInfo.setTaskSignUserIds(taskSignUserIds);
            actAljoinTaskSignInfo.setTaskSignUserNames(taskSignUserNames);
            actAljoinTaskSignInfo.setIsBackOwner(isBackOwner);
            //加签完成状态
            actAljoinTaskSignInfo.setFinishType(finshType);
            actAljoinTaskSignInfo.setAllTaskIds(allTaskIds);
            taskSignInfoList.add(actAljoinTaskSignInfo);
        }

        //批量插入 加签记录
        if(taskSignInfoList.size() > 0){
            actAljoinTaskSignInfoService.insertBatch(taskSignInfoList);
        }
    }

    @Override
    public ActAljoinTaskSignInfo getSignTaskInfo(String taskId) throws Exception {
        Where<ActAljoinTaskSignInfo> signInfoWhere = new Where<ActAljoinTaskSignInfo>();
        signInfoWhere.setSqlSelect("id,finish_type,sign_task_id,task_owner_id,task_id,task_owner_name,task_sign_user_id,task_sign_user_name,task_signed_user_id,task_signed_user_name");
        signInfoWhere.eq("sign_task_id",taskId);
        ActAljoinTaskSignInfo actAljoinTaskSignInfo = selectOne(signInfoWhere);
        return actAljoinTaskSignInfo;
    }

    public Map<Long,Object> getAllSignTaskInfoByTaskOwnerId(String taskDefKey, String processInstanceId) throws Exception {
        Map<Long,Object> map = new HashMap<Long,Object>();
        //查询所有的加签信息
        Where<ActAljoinTaskSignInfo> signInfoWhere = new Where<ActAljoinTaskSignInfo>();
        signInfoWhere.setSqlSelect(
            "id,task_key,process_instance_id,task_owner_id,task_owner_name,sign_task_id,is_back_owner,task_sign_user_id,task_signed_user_id");
        signInfoWhere.eq("task_key", taskDefKey);
        signInfoWhere.eq("process_instance_id", processInstanceId);
        List<ActAljoinTaskSignInfo> signInfoList = selectList(signInfoWhere);
        List<ActAljoinTaskSignInfo> taskSignInfoList = new ArrayList<ActAljoinTaskSignInfo>();
        //根据根据taskOwnerId分组
        for(ActAljoinTaskSignInfo signInfo : signInfoList){
            if(map.containsKey(signInfo.getTaskOwnerId())){
                taskSignInfoList = ( List<ActAljoinTaskSignInfo>)map.get(signInfo.getTaskOwnerId());
                taskSignInfoList.add(signInfo);
            }else {
                taskSignInfoList.add(signInfo);
            }
            map.put(signInfo.getTaskOwnerId(),taskSignInfoList);
        }

        return map;
    }
}
