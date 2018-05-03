package aljoin.act.service;

import aljoin.act.cmd.CreateAndTakeTransitionCmd;
import aljoin.act.creator.MultiInstanceActivityCreatorBase;
import aljoin.act.creator.RuActivityDefinitionIntepreter;
import aljoin.act.dao.entity.ActAljoinActivityLog;
import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.entity.ActAljoinTaskSignInfo;
import aljoin.act.dao.entity.ActRunTimeExecution;
import aljoin.act.iservice.*;
import aljoin.act.util.BaseProcessDefinitionUtils;
import aljoin.dao.config.Where;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
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
public class ActTaskAddSignServiceImpl implements ActTaskAddSignService {
    @Resource
    private ProcessEngine processEngine;
    @Resource
    private ActActivitiService actActivitiService;
    @Resource
    private TaskService taskService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private ActRunTimeExecutionService actRunTimeExecutionService;
    @Resource
    ActRuntimeActivityDefinitionService actRuntimeActivityDefinitionService;
    @Resource
    private ActHisTaskService actHisTaskService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private ActAljoinTaskSignInfoService actAljoinTaskSignInfoService;
    @Resource
    private ActAljoinActivityLogService actAljoinActivityLogService;

    /**
     * 克隆生成任务链
     *
     * @param prototypeActivityId 当前任务节点key
     * @param oldTaskId 当前任务ID
     * @param assignee 当前任务签收任务
     * @param nextActivityId 下个节点KEY
     * @param processInstanceId 流程实例ID
     * @param processDefinitionEntity 流程定义对象
     * @param assigneeList 被加签人员列表
     * @return ActivityImpl[]
     * @throws Exception
     */
    private ActivityImpl[] cloneAndMakeChain(String prototypeActivityId, String oldTaskId, String assignee,
        String nextActivityId, String processInstanceId, ProcessDefinitionEntity processDefinitionEntity,
        List<String> assigneeList,ActAljoinBpmnRun bpmnRun,Map<Long,String> userMap,List<Long> uIdList,Integer finshType) throws Exception {
        ActRuActivityDefinitionServiceImpl info = new ActRuActivityDefinitionServiceImpl();
        info.setProcessDefinitionId(processDefinitionEntity.getId());
        info.setProcessInstanceId(processInstanceId);

        RuActivityDefinitionIntepreter radei = new RuActivityDefinitionIntepreter(info);
        radei.setPrototypeActivityId(prototypeActivityId);
        radei.setAssignees(assigneeList);

        // 判断当前会签任务节点是 并行会签还是串行会签
        MultiInstanceLoopCharacteristics currentMulti
            = actActivitiService.getMultiInstance(processInstanceId, prototypeActivityId);
        if (null != currentMulti) {
            if (!currentMulti.isSequential()) {
                // 并行
                radei.setSequential(false);
            } else {
                // 串行
                radei.setSequential(true);
            }
        }

        // 生成任务
        Task task = taskService.createTaskQuery().taskId(oldTaskId).singleResult();

        // 判断下个节点是否多实例（会签）节点
        MultiInstanceLoopCharacteristics nextMulti = actActivitiService.getMultiInstance(processInstanceId, nextActivityId);
        if (nextMulti != null) {
            // 下个节点是多实例节点
            // 流程集合变量${ASSIGNEE_B_KGCYWJQSRY}
            String collectionValue = nextMulti.getInputDataItem();
            if (StringUtils.isEmpty(collectionValue)) {
                throw new Exception("多实例(会签)流程集合变量不能为空");
            }
            collectionValue = collectionValue.substring(collectionValue.indexOf("{") + 1,
                collectionValue.indexOf("}"));
            // 构造流程变量
            Map<String, Object> vars = new HashMap<String, Object>();
            // 给集合流程变量设值
            vars.put(collectionValue, assigneeList);
            runtimeService.setVariables(task.getExecutionId(), vars);
        }

        // 创建多实例活动
        ActivityImpl[] activities
            = new MultiInstanceActivityCreatorBase().createActivities(processEngine, processDefinitionEntity, info);

        executeCommand(new CreateAndTakeTransitionCmd(task.getExecutionId(), activities[0]));
        actHisTaskService.updateHisTask(task.getId(),assignee);
        recordActivitiesCreation(info, processInstanceId, assigneeList);

        //记录加签信息
        actAljoinTaskSignInfoService.insertSignTaskInfo(uIdList,task,bpmnRun,userMap,finshType);
        return activities;
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
     * 获得当前任务
     *
     * @param processInstanceId
     * @return TaskEntity
     */
    private TaskEntity getCurrentTask(String processInstanceId) {

        return (TaskEntity)processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId)
            .active().singleResult();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivityImpl[] addSignTasksAfter(Task oldTask, String assignee,
        ProcessDefinitionEntity processDefinitionEntity, List<String> assigneeList,ActAljoinBpmnRun bpmnRun,Map<Long,String> userMap,List<Long> uIdList,Integer finshType) throws Exception {
        ActivityImpl prototypeActivity = BaseProcessDefinitionUtils.getActivity(processEngine,
            processDefinitionEntity.getId(), oldTask.getTaskDefinitionKey());

        return cloneAndMakeChain(oldTask.getTaskDefinitionKey(), oldTask.getId(), assignee,
            prototypeActivity.getOutgoingTransitions().get(0).getDestination().getId(), oldTask.getProcessInstanceId(),
            processDefinitionEntity, assigneeList,bpmnRun,userMap,uIdList,finshType);
    }

    @Override
    public ActivityImpl[] addSignTasksBefore(Task oldTask, String assignee,
        ProcessDefinitionEntity processDefinitionEntity, List<String> assigneeList,ActAljoinBpmnRun bpmnRun,Map<Long,String> userMap,List<Long> uIdList,Integer finshType) throws Exception {
        return cloneAndMakeChain(oldTask.getTaskDefinitionKey(), oldTask.getId(), assignee,
            oldTask.getTaskDefinitionKey(), oldTask.getProcessInstanceId(), processDefinitionEntity, assigneeList,bpmnRun,userMap,uIdList,finshType);
    }

    /**
     * 跳转（包括回退和向前）至指定活动节点
     * 
     * @param
     * @throws Exception
     */
    private void moveTo(TaskEntity taskEntity, ActivityImpl activity, String nextActivityId, List<String> assigneeList,String assignee)
        throws Exception {
        String executionId = taskEntity.getExecutionId();

        // 判断下个节点是否多实例（会签）节点
        MultiInstanceLoopCharacteristics nextMulti
            = actActivitiService.getMultiInstance(taskEntity.getProcessInstanceId(), nextActivityId);

        if (nextMulti != null) {
            // 下个节点是多实例节点
            // 流程集合变量${ASSIGNEE_B_KGCYWJQSRY}
            String collectionValue = nextMulti.getInputDataItem();
            if (StringUtils.isEmpty(collectionValue)) {
                throw new Exception("多实例(会签)流程集合变量不能为空");
            }
            collectionValue = collectionValue.substring(collectionValue.indexOf("{") + 1, collectionValue.indexOf("}"));
            // 构造流程变量
            Map<String, Object> vars = new HashMap<String, Object>(1);
            // 给集合流程变量设值
            vars.put(collectionValue, assigneeList);
            runtimeService.setVariables(taskEntity.getExecutionId(), vars);
        }
        executeCommand(new CreateAndTakeTransitionCmd(executionId, activity));

        ActRunTimeExecution runTimeExecution = new ActRunTimeExecution();
        runTimeExecution.setProcInstId(taskEntity.getProcessInstanceId());
        actRunTimeExecutionService.updateExecution(runTimeExecution);
        // 删除任务
        actHisTaskService.updateHisTask(taskEntity.getId(),assignee);
    }

    /**
     * 
     * @param currentTaskEntity 当前任务节点
     * @param targetTaskDefinitionKey 目标任务节点（在模型定义里面的节点名称）
     * @throws Exception
     */
    @Override
    public void moveTo(TaskEntity currentTaskEntity, String targetTaskDefinitionKey, String processInstanceId,
        String nextActivityId, List<String> assigneeList,String assignee) throws Exception {
        ActivityImpl activity = BaseProcessDefinitionUtils.getActivity(processEngine,
            currentTaskEntity.getProcessDefinitionId(), targetTaskDefinitionKey);

        moveTo(currentTaskEntity, activity, nextActivityId, assigneeList,assignee);
    }

    @Override
    public void moveTo(String targetTaskDefinitionKey, String processInstanceId, String oldTaskId, String assignee,
        String nextActivityId, List<String> assigneeList) throws Exception {
        Task task = taskService.createTaskQuery().taskId(oldTaskId).singleResult();
        moveTo((TaskEntity)task, targetTaskDefinitionKey, processInstanceId, nextActivityId, assigneeList,assignee);
    }

    private void recordActivitiesCreation(ActRuActivityDefinitionServiceImpl info, String processInstanceId,
        List<String> assigneeList) throws Exception {
        info.serializeProperties();
        setSignedTaskassignee(processInstanceId, assigneeList);
        actRuntimeActivityDefinitionService.save(info);
    }

    /**
     * 分裂某节点为多实例节点
     * 
     * @param targetTaskDefinitionKey
     * @param
     * @throws IOException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    @Override
    public ActivityImpl split(String targetTaskDefinitionKey, String processInstanceId,
        ProcessDefinitionEntity processDefinitionEntity, boolean isSequential, List<String> assigneeList,String assignee)
        throws Exception {
        ActRuActivityDefinitionServiceImpl info = new ActRuActivityDefinitionServiceImpl();
        info.setProcessDefinitionId(processDefinitionEntity.getId());
        info.setProcessInstanceId(processInstanceId);

        RuActivityDefinitionIntepreter radei = new RuActivityDefinitionIntepreter(info);

        radei.setPrototypeActivityId(targetTaskDefinitionKey);
        radei.setAssignees(assigneeList);
        radei.setSequential(isSequential);

        ActivityImpl clone
            = new MultiInstanceActivityCreatorBase().createActivities(processEngine, processDefinitionEntity, info)[0];

        TaskEntity currentTaskEntity = getCurrentTask(processInstanceId);
        executeCommand(new CreateAndTakeTransitionCmd(currentTaskEntity.getExecutionId(), clone));
        actHisTaskService.updateHisTask(currentTaskEntity.getId(),assignee);

        recordActivitiesCreation(info, processInstanceId, assigneeList);
        return clone;
    }

    @Override
    public ActivityImpl split(String targetTaskDefinitionKey, String processInstanceId,
        ProcessDefinitionEntity processDefinitionEntity, List<String> assigneeList,String assignee) throws Exception {
        return split(targetTaskDefinitionKey, processInstanceId, processDefinitionEntity, true, assigneeList,assignee);
    }

    @Override
    public ActivityImpl[] deliveryLink(Task oldTask, String assignee, String destTaskDefKey,
        ProcessDefinitionEntity processDefinitionEntity, List<String> assigneeList) throws Exception {
       /* return cloneAndMakeChain(oldTask.getTaskDefinitionKey(), oldTask.getId(), oldTask.getAssignee(), destTaskDefKey,
            oldTask.getProcessInstanceId(), processDefinitionEntity, assigneeList);*/
       return null;
    }

    @Override
    @Transactional
    public void returnAssignee(Task task,String userId,ActAljoinBpmnRun bpmnRun,Map<Long,String> userMap,List<Long> uIdList,Integer finshType) throws Exception {
        ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity)repositoryService
            .getProcessDefinition(task.getProcessDefinitionId());

        Where<ActAljoinTaskSignInfo> taskSignInfoWhere = new Where<ActAljoinTaskSignInfo>();
        taskSignInfoWhere.setSqlSelect("id,sign_task_id,task_owner_id,task_owner_name");
        taskSignInfoWhere.eq("sign_task_id", task.getId());
        ActAljoinTaskSignInfo taskSignInfo = actAljoinTaskSignInfoService.selectOne(taskSignInfoWhere);
        String taskOwnerId = "";
        String taskOwnerName = "";
        if(null != taskSignInfo){
            taskOwnerId = String.valueOf(taskSignInfo.getTaskOwnerId());
            taskOwnerName = taskSignInfo.getTaskOwnerName();
        }else{
            Where<ActAljoinActivityLog> activityLogWhere = new Where<ActAljoinActivityLog>();
            activityLogWhere.setSqlSelect(
                "id,operate_user_id,operate_full_name,operate_status,receive_user_ids,current_task_def_key,proc_inst_id,operate_status");
            activityLogWhere.eq("current_task_def_key", task.getTaskDefinitionKey());
            activityLogWhere.eq("proc_inst_id", task.getProcessInstanceId());
            activityLogWhere.orderBy("id",true);
            ActAljoinActivityLog actAljoinActivityLog = actAljoinActivityLogService.selectOne(activityLogWhere);
            if (null != actAljoinActivityLog) {
                taskOwnerId = actAljoinActivityLog.getOperateUserId();
                taskOwnerName = actAljoinActivityLog.getOperateFullName();
            }
        }

        List<String> assigneeList = new ArrayList<String>();
        assigneeList.add(taskOwnerId);
        userMap.put(Long.valueOf(taskOwnerId),taskOwnerName);
        uIdList.add(Long.valueOf(taskOwnerId));
        addSignTasksAfter(task, taskOwnerId, definitionEntity,assigneeList,bpmnRun,userMap,uIdList,finshType);
    }

    @Override
    public void moveTo(TaskEntity currentTaskEntity, String targetTaskDefinitionKey) throws Exception {
        ActivityImpl activity = BaseProcessDefinitionUtils.getActivity(processEngine,
            currentTaskEntity.getProcessDefinitionId(), targetTaskDefinitionKey);

        moveTo(currentTaskEntity, activity);
    }

    private void moveTo(TaskEntity currentTaskEntity, ActivityImpl activity) throws Exception{
        executeCommand(new CreateAndTakeTransitionCmd(currentTaskEntity.getExecutionId(), activity));
    }

    public void setSignedTaskassignee(String processInstanceId, List<String> assigneeList) throws Exception {
        List<Task> taskList
            = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().list();
        for (int i = 0; i < assigneeList.size(); i++) {
            Task task = taskList.get(i);
            String assignee = assigneeList.get(i);
            taskService.setAssignee(task.getId(), assignee);
            taskService.addUserIdentityLink(task.getId(), assignee, "candidate");
        }
    }

}
