package aljoin.web.service.ioa;

import aljoin.act.cmd.CreateAndTakeTransitionCmd;
import aljoin.act.creator.MultiInstanceActivityCreatorBase;
import aljoin.act.creator.RuActivityDefinitionIntepreter;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.dao.entity.ActRuIdentitylink;
import aljoin.act.dao.entity.ActRuTask;
import aljoin.act.iservice.*;
import aljoin.act.service.ActRuActivityDefinitionServiceImpl;
import aljoin.act.util.BaseProcessDefinitionUtils;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.web.service.act.ActActivitiWebService;
import com.alibaba.druid.util.StringUtils;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 
 * 流转监控(服务实现类).
 * 
 * @author：pengsp
 * 
 *                @date： 2017-10-19
 */
@Service
public class IoaMonitorWorkWebService{
    @Resource
    private ActActivitiService actActivitiService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private ActRuIdentitylinkService actRuIdentitylinkService;
    @Resource
    private ProcessEngine processEngine;
    @Resource
    private ActRuTaskService actRuTaskService;
    @Resource
    private ActActivitiWebService actActivitiWebService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private ActAljoinQueryService actAljoinQueryService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;
    @Resource
    private ActRuntimeActivityDefinitionService actRuntimeActivityDefinitionService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private ActHisTaskService actHisTaskService;
    @Resource
    private ActAljoinTaskSignInfoService actAljoinTaskSignInfoService;
    /**
     * 特送人员
     * @param taskIds               任务ID (多个用分号隔开)
     * @param assignees             特送人员 (多个用分号隔开)
     * @param message               意见
     * @return RetMsg               响应对象
     * @throws Exception
     */
    @Transactional
    public RetMsg deliveryPersonnel(String taskIds,String processInstanceIds,String message,String assignees,Long userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        //特送人员列表
        List<String> assigneeList = Arrays.asList(assignees.split(";"));
        //选中的任务ID列表
        List<String> taskIdList = Arrays.asList(taskIds.split(";"));
        List<String> processInstanceIdList = Arrays.asList(processInstanceIds.split(";"));;

        Where<ActRuTask> taskWhere = new Where<ActRuTask>();
        taskWhere.setSqlSelect("id_,execution_id_,proc_inst_id_,name_,task_def_key_,proc_def_id_");
        //考虑到万一后面流转监控将多条任务合并成一条的时候所以做了如下判断
        if(!StringUtils.isEmpty(taskIds)){
            taskWhere.in("id_",taskIdList);
        }else{
            taskWhere.in("proc_inst_id_",processInstanceIdList);
        }
        List<ActRuTask> ruTaskList = actRuTaskService.selectList(taskWhere);

        //存放各个不同流程的对应的任务列表
        Map<String,List<ActRuTask>> taskMap = new HashMap<String,List<ActRuTask>>();
        //存放同一个流程的任务（可以批量特送）
        List<ActRuTask> actRuTaskList = new ArrayList<>();
        for(ActRuTask actRuTask : ruTaskList){
            //如果taskMap里面已经存在了流程实例ID 就追加
            if(taskMap.containsKey(actRuTask.getProcInstId())){
                actRuTaskList = taskMap.get(actRuTask.getProcInstId());
                actRuTaskList.add(actRuTask);
            }else{
                actRuTaskList.add(actRuTask);
            }
            taskMap.put(actRuTask.getProcInstId(),actRuTaskList);
        }

        String handle = "";
        String taskAuth = "";

        //存放消息通知需要的参数
        Map<String,Object> map = new HashMap<String,Object>();
        //流程对应的办理人(在线通知需要)
        Map<String,String> proInstIdMap = new HashMap<String,String>();
        //流程对应的businessKey(在线通知需要)
        Map<String,String> businessKeyMap = new HashMap<String,String>();
        //任务对应的办理人(在线通知需要)
        Map<String,String> taskAuthMap = new HashMap<String,String>();

        //任务办理人Id
        Map<String,String> identitylinkMap = new HashMap<>();
        //第一个任务办理人Id
        Map<String,ActRuIdentitylink> assigneeMap = new HashMap<>();
        for(String processInstanceId : taskMap.keySet()){
            List<ActRuTask> actRuTasks = taskMap.get(processInstanceId);
            List<String> tskIdList = new ArrayList<>();
            for(ActRuTask ruTask : actRuTasks){
                tskIdList.add(ruTask.getId());
            }
            Where<ActRuIdentitylink> identitylinkWhere = new Where<ActRuIdentitylink>();
            identitylinkWhere.setSqlSelect("id_,user_id_,task_id_,proc_inst_id_,proc_def_id_");
            if(!StringUtils.isEmpty(taskIds)){
                identitylinkWhere.in("task_id_",tskIdList);
            }else{
                identitylinkWhere.eq("proc_inst_id_",processInstanceId);
                identitylinkWhere.in("type_","starter");
            }

            List<ActRuIdentitylink> identitylinkList = actRuIdentitylinkService.selectList(identitylinkWhere);
            for(ActRuIdentitylink identitylink : identitylinkList){
                handle += identitylink.getUserId()+";";
            }
            identitylinkMap.put(processInstanceId,handle);
            assigneeMap.put(actRuTasks.get(0).getId(),identitylinkList.get(0));
        }
        Map<String,String> ruTaskMap = new HashMap<String,String>();
        for(String processInstanceId : taskMap.keySet()){
            List<ActRuTask> actRuTasks = taskMap.get(processInstanceId);
            ActRuTask actRuTask = actRuTasks.get(0);
            String processDefinitionId = actRuTask.getProcDefId();
            String executionId = actRuTask.getExecutionId();
            String taskDefKey = actRuTask.getTaskDefKey();
            taskAuth = taskDefKey +",";
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

            // 判断下个节点是否多实例（会签）节点
            MultiInstanceLoopCharacteristics nextMulti = actActivitiService.getMultiInstance(processInstanceId, nextActivityId);
            if (nextMulti != null) {
                // 下个节点是多实例节点
                // 流程集合变量${ASSIGNEE_B_KGCYWJQSRY}
                String collectionValue = nextMulti.getInputDataItem();
                if (com.baomidou.mybatisplus.toolkit.StringUtils.isEmpty(collectionValue)) {
                    throw new Exception("多实例(会签)流程集合变量不能为空");
                }
                collectionValue = collectionValue.substring(collectionValue.indexOf("{") + 1,
                    collectionValue.indexOf("}"));
                // 构造流程变量
                Map<String, Object> vars = new HashMap<String, Object>();
                // 给集合流程变量设值
                vars.put(collectionValue, assigneeList);
                runtimeService.setVariables(actRuTask.getExecutionId(), vars);
            }

            ActivityImpl[] activities = null;
            //判断当前会签任务节点是 并行会签还是串行会签
            MultiInstanceLoopCharacteristics currentMulti
                = actActivitiService.getMultiInstance(processInstanceId, taskDefKey);

            //记录流程日志
            actActivitiWebService.insertOrUpdateActivityLog(processInstanceId, WebConstant.PROCESS_OPERATE_STATUS_7,userId,null);
            //如果是会签任务 则生成新的任务 并把原来的任务删掉(会签需要多人办理)
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

                ActivityImpl activity = BaseProcessDefinitionUtils.getActivity(processEngine,
                    processDefinitionId ,activities[0].getId());
                //生成新的任务
                executeCommand(new CreateAndTakeTransitionCmd(executionId, activity));

                //删除原来的任务
                for(ActRuTask oldTask : actRuTasks){
                    ruTaskMap.put(processInstanceId,actRuTask.getId());
                    actHisTaskService.updateHisTask(oldTask.getId(),String.valueOf(userId));
                }
                info.serializeProperties();
                actRuntimeActivityDefinitionService.save(info);
                setDeliveryPersonnel(processInstanceId,message,assigneeList);
            }else{
                //获取第一个任务对应的办理人（普通任务节点只有一个任务）
                ActRuIdentitylink identitylink = assigneeMap.get(actRuTask.getId());
                //如果当前任务已被签收 先解签收
                if(!StringUtils.isEmpty(actRuTask.getAssignee())){
                    taskService.unclaim(actRuTask.getId());
                }
                //删除原来的候选用户
                taskService.deleteCandidateUser(actRuTask.getId(),identitylink.getUserId());
                //新增新的候选用户
                for(String assignee : assigneeList){
                    taskService.addCandidateUser(actRuTask.getId(),assignee);
                }
                //记录意见
                taskService.addComment(actRuTask.getId(),processInstanceId,message);
            }

            //拼接 任务权限参数
            for(String assignee : assigneeList){
                taskAuth += assignee+"#";
            }

            //获取流程对应的当前所有办理人
            handle = identitylinkMap.get(processInstanceId);
            proInstIdMap.put(processInstanceId,handle);

            //获取流程对应的businessKey
            HistoricProcessInstance hiInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();

            businessKeyMap.put(processInstanceId,hiInstance.getBusinessKey());

            taskAuth = taskAuth.substring(0,taskAuth.lastIndexOf("#"));
            taskAuthMap.put(processInstanceId,taskAuth);
        }

        // 查询表
        Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
        actAljoinQueryWhere.in("process_instance_id", processInstanceIdList);
        List<ActAljoinQuery> queryList = actAljoinQueryService.selectList(actAljoinQueryWhere);
        // 历史查询表
        Where<ActAljoinQueryHis> actAljoinQueryHisWhere = new Where<ActAljoinQueryHis>();
        actAljoinQueryHisWhere.in("process_instance_id", processInstanceIdList);
        List<ActAljoinQueryHis> queryHisList = actAljoinQueryHisService.selectList(actAljoinQueryHisWhere);
        int i = 0;
        //修改当前流程的办理人
        for(ActAljoinQuery query : queryList){
            ActAljoinQueryHis queryHis = queryHisList.get(i);
            //获取当前流程的所有任务的办理人
            Set<Long> currentHandleUserIdSetfterCompleted = actActivitiWebService.getCurrentHandlers(ruTaskMap.get(query.getProcessInstanceId()),query.getProcessInstanceId(),userId);
            Where<AutUser> autUserWhere = new Where<AutUser>();
            autUserWhere.setSqlSelect("full_name");
            autUserWhere.in("id", currentHandleUserIdSetfterCompleted);
            List<AutUser> autUserList = autUserService.selectList(autUserWhere);
            String currentHandleFullUserName = "";
            for (AutUser autUser : autUserList) {
                currentHandleFullUserName += autUser.getFullName() + ",";
            }
            if (!StringUtils.isEmpty(currentHandleFullUserName)) {
                currentHandleFullUserName = currentHandleFullUserName.substring(0,
                    currentHandleFullUserName.length() - 1);
            }
            query.setCurrentHandleFullUserName(currentHandleFullUserName);
            queryHis.setCurrentHandleFullUserName(currentHandleFullUserName);

            i++;
        }
        actAljoinQueryService.updateBatchById(queryList);
        actAljoinQueryHisService.updateBatchById(queryHisList);

        map.put("taskAuths",taskAuthMap);
        map.put("businessKeys",businessKeyMap);
        map.put("processInstanceIds",proInstIdMap);
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
    public void setDeliveryPersonnel(String processInstanceId,String message,List<String> assigneeList) throws Exception {
        List<Task> taskList
            = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().list();

        for (int i = 0; i < assigneeList.size(); i++) {
            Task task = taskList.get(i);
            String assignee = assigneeList.get(i);
            taskService.setAssignee(task.getId(), assignee);
            taskService.addComment(task.getId(),processInstanceId,message);
            taskService.addUserIdentityLink(task.getId(),assignee,"candidate");
        }
    }

}
