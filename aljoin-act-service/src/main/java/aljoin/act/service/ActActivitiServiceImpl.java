package aljoin.act.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import aljoin.act.dao.entity.*;
import aljoin.act.iservice.*;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.ExclusiveGatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelGatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.el.JuelExpression;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aljoin.act.custom.CustomProcessDiagramGenerator;
import aljoin.act.service.util.ActConstant;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.util.DateUtil;

/**
 * 
 * 流程服务类(实现类).
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:01:47
 */
@Service

public class ActActivitiServiceImpl implements ActActivitiService {

    private final static Logger logger = LoggerFactory.getLogger(ActActivitiServiceImpl.class);

    @Resource
    private HistoryService historyService;
    @Resource
    private IdentityService identityService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private ProcessEngineConfiguration processEngineConfiguration;
    @Resource
    private ActAljoinBpmnRunService actAljoinBpmnRunService;
    @Resource
    private ActAljoinFormDataRunService actAljoinFormDataRunService;
    @Resource
    private ActAljoinBpmnFormService actAljoinBpmnFormService;
    @Resource
    private ActAljoinFormWidgetRunService actAljoinFormWidgetRunService;
    @Resource
    private ActAljoinFormRunService actAljoinFormRunService;
    @Resource
    private ActAljoinQueryService actAljoinQueryService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;
    @Resource
    private ActAljoinCategoryService actAljoinCategoryService;
    @Resource
    private ActAljoinExecutionHisService actAljoinExecutionHisService;
    @Resource
    private ActAljoinActivityLogService actAljoinActivityLogService;
    @Resource
    private ActAljoinTaskSignInfoService actAljoinTaskSignInfoService;
    @Resource
    private ActTaskAddSignService actTaskAddSignService;

    @Override
    public void addUser(Long userId, String fullName) {
        User user = identityService.newUser(userId.toString());
        user.setFirstName(fullName);
        identityService.saveUser(user);
    }

    @Override
    public void delUser(Long userId) {
        identityService.deleteUser(userId.toString());
    }

    @Override
    public void addGroup(Long groupId) {
        Group group = identityService.newGroup(groupId.toString());
        identityService.saveGroup(group);
    }

    @Override
    public void delGroup(Long groupId) {
        identityService.deleteGroup(groupId.toString());
    }

    @Override
    public void addUserGroup(Long userId, Long groupId) {
        identityService.createMembership(userId.toString(), groupId.toString());
    }

    @Override
    public void delUserGroup(Long userId, Long groupId) {
        identityService.deleteMembership(userId.toString(), groupId.toString());
    }

    @Override
    public Deployment deployBpmn(ActAljoinBpmn bpmn) throws Exception {
        Deployment deployment = repositoryService.createDeployment() // 创建部署
            .addString(bpmn.getProcessId() + ".bpmn", bpmn.getXmlCode())// 流程ID以及代码
            .name(bpmn.getProcessName()) // 流程名称
            .category(bpmn.getCategoryId().toString())// 流程分类ID
            .deploy(); // 部署
        return deployment;
    }

    @Override
    public ProcessInstance startBpmnByKey(String key) throws Exception {
        // 流程定义表的KEY字段值
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);
        return processInstance;
    }

    @Override
    @Transactional
    public ProcessInstance startBpmn(ActAljoinBpmnRun bpmn, ActAljoinFormRun runForm, Map<String, String> param,
        Long userId) throws Exception {
        // businessKey按顺序：流程ID，表单ID，是否紧急
        // businessKey的内容逗号分隔,如果后面需要加可以在后面添加，但是顺序不能改变：bpmnId,formId,isUrgent,bizType,bizId
        //
        String bpmnId = bpmn.getId() + "";
        String formId = "null";
        if (runForm != null) {
            formId = runForm.getId() + "";
        }
        param.put("formId", formId);
        Map<String, String> map = checkBusinessKey(param);

        String businessKey =
            bpmnId + "," + formId + "," + map.get("isUrgent") + "," + map.get("bizType") + "," + map.get("bizId");
        identityService.setAuthenticatedUserId(userId.toString());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(bpmn.getProcessId(), businessKey);
        return processInstance;
    }

    @Override
    public InputStream getRuImageInputStream(String processInstanceId, boolean isAll) throws Exception {
        InputStream is = null;
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (taskList.size() > 0) {
            // 流程定义
            BpmnModel bpmnModel = repositoryService.getBpmnModel(taskList.get(0).getProcessDefinitionId());
            List<String> highLightedActiveActivityIds = new ArrayList<String>();
            List<String> highLightedActiveFlowIds = new ArrayList<String>();
            List<String> currentActiveActivityIds = new ArrayList<String>();
            for (Task t : taskList) {
                // 当前活动节点
                List<String> activeActivityIds = runtimeService.getActiveActivityIds(t.getExecutionId());
                currentActiveActivityIds.addAll(activeActivityIds);
                if (isAll) {
                    // 已经运行的节点高亮
                    List<HistoricActivityInstance> activityList = historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(t.getProcessInstanceId()).list();
                    for (HistoricActivityInstance historicActivityInstance : activityList) {
                        highLightedActiveActivityIds.add(historicActivityInstance.getActivityId());
                    }
                    // 获取高亮线
                    HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId).singleResult();
                    ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity)repositoryService
                        .getProcessDefinition(processInstance.getProcessDefinitionId());
                    // highLightedActiveFlowIds =
                    // getHighLightedFlows(definitionEntity, activityList);
                    getHighLightedFlows(definitionEntity.getActivities(), highLightedActiveFlowIds,
                        highLightedActiveActivityIds);

                } else {
                    // 只有当前活动节点才高亮
                    highLightedActiveActivityIds.addAll(activeActivityIds);
                }

            }
            // 原来的
            // ProcessDiagramGenerator pdg =
            // processEngineConfiguration.getProcessDiagramGenerator();
            // 自定义的
            CustomProcessDiagramGenerator pdg = new CustomProcessDiagramGenerator();

            // 生成流图片
            is = pdg.generateDiagram2(bpmnModel, "PNG", highLightedActiveActivityIds, highLightedActiveFlowIds,
                processEngineConfiguration.getActivityFontName(), processEngineConfiguration.getLabelFontName(),
                processEngineConfiguration.getClassLoader(), 1.0, currentActiveActivityIds);

        }
        return is;
    }

    /**
     * 
     * 递归查询经过的流
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年11月8日 下午5:10:01
     */
    private void getHighLightedFlows(List<ActivityImpl> activityList, List<String> highLightedFlows,
        List<String> historicActivityInstanceList) {
        for (ActivityImpl activity : activityList) {
            if (activity.getProperty("type").equals("subProcess")) {
                // get flows for the subProcess
                getHighLightedFlows(activity.getActivities(), highLightedFlows, historicActivityInstanceList);
            }

            if (historicActivityInstanceList.contains(activity.getId())) {
                List<PvmTransition> pvmTransitionList = activity.getOutgoingTransitions();
                for (PvmTransition pvmTransition : pvmTransitionList) {
                    String destinationFlowId = pvmTransition.getDestination().getId();
                    if (historicActivityInstanceList.contains(destinationFlowId)) {
                        highLightedFlows.add(pvmTransition.getId());
                    }
                }
            }
        }
    }

    /**
     * 
     * 判断两个节点是否具有起始的执行关系，如A->B（因为不能单单根据连线来决定，要真正走过才是）
     *
     * @return：boolean
     *
     * @author：zhongjy
     *
     * @date：2018年1月26日 上午11:17:47
     */
    public boolean isConnectTask(String begTaskKey, String endTaskKey) {
        Where<ActAljoinActivityLog> logWhere = new Where<ActAljoinActivityLog>();
        logWhere.eq("last_task_def_key", begTaskKey);
        logWhere.eq("current_task_def_key", endTaskKey);
        if (actAljoinActivityLogService.selectCount(logWhere) > 0) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unused")
    private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinitionEntity,
        List<HistoricActivityInstance> historicActivityInstances) {
        List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
            ActivityImpl activityImpl =
                processDefinitionEntity.findActivity(historicActivityInstances.get(i).getActivityId());// 得到节点定义的详细信息
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
            ActivityImpl sameActivityImpl1 =
                processDefinitionEntity.findActivity(historicActivityInstances.get(i + 1).getActivityId());
            // 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);// 后续第一个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);// 后续第二个节点
                if (activityImpl1.getStartTime().equals(activityImpl2.getStartTime())) {
                    // 如果第一个节点和第二个节点开始时间相同保存
                    ActivityImpl sameActivityImpl2 =
                        processDefinitionEntity.findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {
                    // 有不相同跳出循环
                    break;
                }
            }
            List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();// 取出节点的所有出去的线
            for (PvmTransition pvmTransition : pvmTransitions) {
                // 对所有的线进行遍历
                ActivityImpl pvmActivityImpl = (ActivityImpl)pvmTransition.getDestination();
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }

    @Override
    public InputStream getImageInputStream(String deployId, String resourceName) throws Exception {
        InputStream is = repositoryService.getResourceAsStream(deployId, resourceName);
        return is;
    }

    @Override
    public InputStream getImageInputStream(String key, Integer version) throws Exception {
        ProcessDefinition processDefinition = null;
        if (version.intValue() == 0) {
            processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key)
                .latestVersion().singleResult();
        } else {
            processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key)
                .processDefinitionVersion(version).singleResult();
        }
        InputStream is = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
            processDefinition.getDiagramResourceName());
        return is;
    }

    @Override
    public void completeTask(Map<String, Object> variables, String taskId) throws Exception {
        taskService.complete(taskId, variables);
    }

    /**
     * 单人待签收/待办理
     */
    @Override
    public List<Task> selectCandidateTask(String userId, PageBean pageBean) throws Exception {
        // 开始索引
        int firstResult = (pageBean.getPageNum() - 1) * pageBean.getPageSize();
        // 查询记录数
        int maxResults = pageBean.getPageSize();
        return taskService.createTaskQuery().taskCandidateUser(userId).listPage(firstResult, maxResults);
    }

    /**
     * 已经签收过的任务列表，某种意义上我理解为真正的办理人(单人已签收/待办理)
     */
    @Override
    public List<Task> selectAssigneeTask(String userId, PageBean pageBean) throws Exception {
        // 开始索引
        int firstResult = (pageBean.getPageNum() - 1) * pageBean.getPageSize();
        // 查询记录数
        int maxResults = pageBean.getPageSize();
        return taskService.createTaskQuery().taskAssignee(userId).listPage(firstResult, maxResults);
    }

    @Override
    public List<Task> selectInvolvedTask(String userId, PageBean pageBean) throws Exception {
        // 开始索引
        int firstResult = (pageBean.getPageNum() - 1) * pageBean.getPageSize();
        // 查询记录数
        int maxResults = pageBean.getPageSize();
        return taskService.createTaskQuery().taskInvolvedUser(userId).listPage(firstResult, maxResults);
    }

    @Override
    public List<HistoricTaskInstance> selectHisTask(String userId, PageBean pageBean) throws Exception {
        // 开始索引
        int firstResult = (pageBean.getPageNum() - 1) * pageBean.getPageSize();
        // 查询记录数
        int maxResults = pageBean.getPageSize();
        return historyService.createHistoricTaskInstanceQuery().taskInvolvedUser(userId).listPage(firstResult,
            maxResults);
    }

    @Override
    public ProcessInstance startBpmn(ActAljoinBpmn bpmn, ActAljoinFormRun runForm, Map<String, String> param)
        throws Exception {
        // businessKey的内容逗号分隔,如果后面需要加可以在后面添加，但是顺序不能改变：bpmnId,formId
        String bpmnId = bpmn.getId() + "";
        String formId = "null";
        if (runForm != null) {
            formId = runForm.getId() + "";
        }
        String businessKey = bpmnId + "," + formId;
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(bpmn.getProcessId(), businessKey);
        return processInstance;
    }

    @Override
    public List<HistoricProcessInstance> selectInstanceListByStartUser(String userId, PageBean pageBean)
        throws Exception {
        // 开始索引
        int firstResult = (pageBean.getPageNum() - 1) * pageBean.getPageSize();
        // 查询记录数
        int maxResults = pageBean.getPageSize();
        List<HistoricProcessInstance> instanceList =
            historyService.createHistoricProcessInstanceQuery().startedBy(userId).listPage(firstResult, maxResults);
        return instanceList;
    }

    @Override
    public List<HistoricTaskInstance> getCurrentNodeInfo(String taskId) throws Exception {
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        List<HistoricTaskInstance> instanceList = historyService.createHistoricTaskInstanceQuery()// 获取第一个任务节点
            .processInstanceId(processInstanceId).unfinished().list();
        return instanceList;
    }

    @Override
    public List<Comment> getCommentInfo(String taskId) throws Exception {
        List<Comment> historyCommnets = new ArrayList<Comment>();
        // 1) 获取流程实例的ID
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessInstance pi =
            runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        // 2）通过流程实例查询所有的(用户任务类型)历史活动
        List<HistoricActivityInstance> hais = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(pi.getId()).activityType("userTask").list();
        // 3）查询每个历史任务的批注
        for (HistoricActivityInstance hai : hais) {
            String historytaskId = hai.getTaskId();
            List<Comment> comments = taskService.getTaskComments(historytaskId);
            // 4）如果当前任务有批注信息，添加到集合中
            if (comments != null && comments.size() > 0) {
                historyCommnets.addAll(comments);
            }
        }
        // 5）返回
        return historyCommnets;
    }

    public Map<String, String> checkBusinessKey(Map<String, String> param) throws Exception {
        Map<String, String> map = new HashMap<String, String>();

        if (null != param) {
            if (StringUtils.isNotEmpty(param.get("isUrgent"))) {
                if ("1".equals(param.get("isUrgent"))) {
                    map.put("isUrgent", "1");
                }
                if ("2".equals(param.get("isUrgent"))) {
                    map.put("isUrgent", "2");
                }
                if ("3".equals(param.get("isUrgent"))) {
                    map.put("isUrgent", "3");
                }
            }

            if (StringUtils.isNotEmpty(param.get("bizType"))) {
                map.put("bizType", param.get("bizType"));
            } else {
                map.put("bizType", ActConstant.BIZ_TYPE_IOA);
            }

            if (StringUtils.isNotEmpty(param.get("bizId"))) {
                map.put("bizId", param.get("bizId"));
            } else {
                if (StringUtils.isEmpty(param.get("formId"))) {
                    throw new Exception("bizId不能为空");
                }
            }
        } else {
            throw new Exception("param参数不能为空");
        }
        return param;
    }

    private FlowElement getFlowElement(Collection<FlowElement> elements, String activitiId) throws Exception {
        // 根据当前任务，进入下个任务的时候，获取流程设置UEL表达式中的变量名称
        for (FlowElement e : elements) {
            if (activitiId.equals(e.getId())) {
                return e;
            }
        }
        return null;
    }

    /**
     * 
     * 获取当前节点往下执行的时候连线的条件（暂时只支持两级）
     *
     * @return：List<String>
     *
     * @author：zhongjy
     *
     * @date：2017年10月27日 下午2:19:27
     */
    @Override
    public List<String> getToNextCondition(String taskId) throws Exception {
        List<String> retConditionList = new ArrayList<String>();
        // 根据当前任务，进入下个任务的时候，获取流程设置UEL表达式中的变量名称
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        BpmnModel model2 = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Collection<FlowElement> flowElements2 = model2.getMainProcess().getFlowElements();
        for (FlowElement e : flowElements2) {
            if ((task.getTaskDefinitionKey()).equals(e.getId())) {
                // 获取任务对外箭头的直接表达式
                // if (e instanceof UserTask) {
                UserTask userTask = (UserTask)e;
                List<SequenceFlow> flowList = userTask.getOutgoingFlows();
                // System.out.println("任务名称：" + userTask.getName());
                for (SequenceFlow sequenceFlow : flowList) {
                    // #################第1级
                    if (sequenceFlow.getConditionExpression() != null) {
                        retConditionList.add(sequenceFlow.getConditionExpression());
                    }
                    // 如果箭头的目标是一个任务节点,计算结束
                    if (!sequenceFlow.getTargetRef().startsWith("Task_")) {
                        // #################第2级
                        FlowElement targetFlowElement = getFlowElement(flowElements2, sequenceFlow.getTargetRef());
                        if (targetFlowElement instanceof ExclusiveGateway) {
                            // 排他网关
                            ExclusiveGateway ex = (ExclusiveGateway)targetFlowElement;
                            List<SequenceFlow> flowList2 = ex.getOutgoingFlows();
                            for (SequenceFlow sequenceFlow2 : flowList2) {
                                if (sequenceFlow2.getConditionExpression() != null) {
                                    retConditionList.add(sequenceFlow2.getConditionExpression());
                                }
                            }
                        }
                    }
                }
            }
        }
        return retConditionList;
    }

    @Override
    public List<TaskDefinition> getNextTaskInfo(String processInstanceId, boolean withCondition) throws Exception {
        String id = null;
        List<TaskDefinition> taskList = new ArrayList<TaskDefinition>();
        // 获取流程发布Id
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
            .singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity =
            (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                .getDeployedProcessDefinition(definitionId);
        ExecutionEntity execution = (ExecutionEntity)runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId).singleResult();
        // 当前流程节点Id
        String activitiId = execution.getActivityId();
        // 获取流程所有节点
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();
        // 遍历所有节点信息
        for (ActivityImpl activityImpl : activitiList) {
            id = activityImpl.getId();
            if (StringUtils.isNotEmpty(activitiId) && activitiId.equals(id)) {
                // 获取下一个节点信息
                taskList =
                    nextTaskDefinition(activityImpl, activityImpl.getId(), null, processInstanceId, withCondition);
                break;
            }
        }
        return taskList;
    }

    @Override
    public List<TaskDefinition> getNextTaskInfo2(String processInstanceId, boolean withCondition, String activityId)
        throws Exception {
        String id = null;
        List<TaskDefinition> taskList = new ArrayList<TaskDefinition>();
        // 获取流程发布Id
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
            .singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity =
            (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                .getDeployedProcessDefinition(definitionId);
        /*
         * ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery()
         * .processInstanceId(processInstanceId).singleResult();
         */
        // 当前流程节点Id
        // String activitiId = execution.getActivityId();
        // 获取流程所有节点
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();
        // 遍历所有节点信息
        for (ActivityImpl activityImpl : activitiList) {
            id = activityImpl.getId();
            if (activityId.equals(id)) {
                // 获取下一个节点信息
                taskList =
                    nextTaskDefinition(activityImpl, activityImpl.getId(), null, processInstanceId, withCondition);
                break;
            }
        }
        // EndEvent_1efz7il
        if (taskList.size() == 0) {
            // 获取最后一个结束节点
            String endEventId = "";
            for (ActivityImpl activityImpl : activitiList) {
                // 获取结束节点的id
                if (activityImpl.getId().startsWith("EndEvent_")) {
                    endEventId = activityImpl.getId();
                }
            }
            TaskDefinition def = new TaskDefinition(null);
            def.setKey(endEventId);
            Expression expression = new JuelExpression(null, "结束");
            def.setNameExpression(expression);
            taskList.add(def);
        }
        return taskList;
    }

    private List<TaskDefinition> nextTaskDefinition(ActivityImpl activityImpl, String activityId, String elString,
        String processInstanceId, boolean withCondition) throws Exception {

        List<TaskDefinition> resultList = new ArrayList<TaskDefinition>();
        PvmActivity ac = null;

        Object s = null;
        // 如果遍历节点为用户任务并且节点不是当前节点信息
        if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            // 获取该节点下一个节点信息
            TaskDefinition taskDefinition = null;
            if (activityImpl.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                // 一般用户节点
                taskDefinition = ((UserTaskActivityBehavior)activityImpl.getActivityBehavior()).getTaskDefinition();
            } else if (activityImpl.getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
                // 多实例并行
                taskDefinition =
                    ((UserTaskActivityBehavior)(((ParallelMultiInstanceBehavior)(activityImpl).getActivityBehavior())
                        .getInnerActivityBehavior())).getTaskDefinition();
            } else if (activityImpl.getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
                // 多实例串行
                taskDefinition =
                    ((UserTaskActivityBehavior)(((SequentialMultiInstanceBehavior)(activityImpl).getActivityBehavior())
                        .getInnerActivityBehavior())).getTaskDefinition();
            }

            List<TaskDefinition> tempList = new ArrayList<TaskDefinition>();
            tempList.add(taskDefinition);
            return tempList;
        } else if ("endEvent".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            // 获取该节点下一个节点信息
            TaskDefinition def = new TaskDefinition(null);
            def.setKey(activityImpl.getId());
            Expression expression = new JuelExpression(null, "结束");
            def.setNameExpression(expression);
            List<TaskDefinition> tempList = new ArrayList<TaskDefinition>();
            tempList.add(def);
            return tempList;
        } else if ("exclusiveGateway".equals(activityImpl.getProperty("type"))) {
            // 当前节点为exclusiveGateway
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            // 如果排他网关只有一条线路信息
            if (outTransitions.size() == 1) {
                List<TaskDefinition> tempList = nextTaskDefinition((ActivityImpl)outTransitions.get(0).getDestination(),
                    activityId, elString, processInstanceId, withCondition);
                resultList.addAll(tempList);
            } else if (outTransitions.size() > 1) {
                // 如果排他网关有多条线路信息
                for (PvmTransition tr1 : outTransitions) {
                    s = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
                    // 判断el表达式是否成立,线获取流程参数
                    if (withCondition && s != null) {
                        List<ActAljoinFormDataRun> formDataRunList = getFormDataRun(processInstanceId);
                        Map<String, String> paramMap = getConditionParamValue(formDataRunList, (String)s);
                        if (isCondition(paramMap, StringUtils.trim(s.toString()), processInstanceId)) {
                            List<TaskDefinition> tempList = nextTaskDefinition((ActivityImpl)tr1.getDestination(),
                                activityId, elString, processInstanceId, withCondition);
                            resultList.addAll(tempList);
                        }
                    } else {
                        List<TaskDefinition> tempList = nextTaskDefinition((ActivityImpl)tr1.getDestination(),
                            activityId, elString, processInstanceId, withCondition);
                        resultList.addAll(tempList);
                    }
                }
            }
        } else {
            // System.out.println(activityImpl.getProperty("type"));
            // 获取节点所有流向线路信息
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            List<PvmTransition> outTransitionsTemp = null;
            for (PvmTransition tr : outTransitions) {
                ac = tr.getDestination(); // 获取线路的终点节点
                // System.out.println("条件表达式：" +
                // tr.getProperty("conditionText"));
                // 如果箭头有条件，判断条件是否成立
                if (tr.getProperty("conditionText") != null && withCondition) {
                    List<ActAljoinFormDataRun> formDataRunList = getFormDataRun(processInstanceId);
                    Map<String, String> paramMap =
                        getConditionParamValue(formDataRunList, (String)tr.getProperty("conditionText"));
                    if (!isCondition(paramMap, (String)tr.getProperty("conditionText"), processInstanceId)) {
                        // 根据参数判断条件不成立，不返回下一个任务节点
                        continue;
                    }
                }
                // System.out.println("类型："+ac.getProperty("type"));
                // 如果流向线路为排他网关
                if ("exclusiveGateway".equals(ac.getProperty("type"))
                    || "parallelGateway".equals(ac.getProperty("type"))) {
                    outTransitionsTemp = ac.getOutgoingTransitions();
                    // 如果排他网关只有一条线路信息
                    if (outTransitionsTemp.size() == 1) {
                        List<TaskDefinition> tempList =
                            nextTaskDefinition((ActivityImpl)outTransitionsTemp.get(0).getDestination(), activityId,
                                elString, processInstanceId, withCondition);
                        resultList.addAll(tempList);
                    } else if (outTransitionsTemp.size() > 1) { // 如果排他网关有多条线路信息
                        for (PvmTransition tr1 : outTransitionsTemp) {
                            s = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
                            if (withCondition && s != null) {
                                // 判断el表达式是否成立
                                List<ActAljoinFormDataRun> formDataRunList = getFormDataRun(processInstanceId);
                                Map<String, String> paramMap = getConditionParamValue(formDataRunList, (String)s);
                                if (isCondition(paramMap, StringUtils.trim(s.toString()), processInstanceId)) {
                                    List<TaskDefinition> tempList =
                                        nextTaskDefinition((ActivityImpl)tr1.getDestination(), activityId, elString,
                                            processInstanceId, withCondition);
                                    resultList.addAll(tempList);
                                }
                            } else {
                                List<TaskDefinition> tempList = nextTaskDefinition((ActivityImpl)tr1.getDestination(),
                                    activityId, elString, processInstanceId, withCondition);
                                resultList.addAll(tempList);
                            }
                        }
                    }
                } else if ("userTask".equals(ac.getProperty("type"))) {
                    TaskDefinition tdf = null;
                    if (((ActivityImpl)ac).getActivityBehavior() instanceof UserTaskActivityBehavior) {
                        // 普通实例
                        tdf = ((UserTaskActivityBehavior)((ActivityImpl)ac).getActivityBehavior()).getTaskDefinition();
                    } else if (((ActivityImpl)ac).getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
                        // 多实例并行
                        tdf = ((UserTaskActivityBehavior)(((ParallelMultiInstanceBehavior)((ActivityImpl)ac)
                            .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition();
                    } else if (((ActivityImpl)ac).getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
                        // 多实例串行
                        tdf = ((UserTaskActivityBehavior)(((SequentialMultiInstanceBehavior)((ActivityImpl)ac)
                            .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition();
                    }

                    resultList.add(tdf);
                }
            }
        }
        return resultList;
    }

    /**
     * 
     * 根据key和value判断el表达式是否通过信息
     * 
     * @param String key el表达式key信息
     * @param String el el表达式信息
     * @param String value el表达式传入值信息
     *
     * @return：boolean
     *
     * @author：zhongjy
     *
     * @date：2017年10月29日 上午8:56:11
     */
    private boolean isCondition(Map<String, String> param, String el) {
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            context.setVariable(entry.getKey(), factory.createValueExpression(entry.getValue(), String.class));
        }
        ValueExpression e = factory.createValueExpression(context, el, boolean.class);
        return (Boolean)e.getValue(context);
    }

    /**
     * 
     * 根据key和value判断el表达式是否通过信息(条件变量度流程变量)
     * 
     * @param String key el表达式key信息
     * @param String el el表达式信息
     * @param String value el表达式传入值信息
     *
     * @return：boolean
     *
     * @author：zhongjy
     *
     * @date：2017年10月29日 上午8:56:11
     */
    private boolean isCondition(Map<String, String> param, String el, String instanceId) {
        boolean result = false;
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        // 插入系统的流程变量名称
        for (int i = 0; i < ActConstant.SYS_PROCESS_VAL_ARR.length; i++) {
            if (!param.containsKey(ActConstant.SYS_PROCESS_VAL_ARR[i])) {
                param.put(ActConstant.SYS_PROCESS_VAL_ARR[i], null);
            }
        }
        for (Map.Entry<String, String> entry : param.entrySet()) {
            String keyValue = (String)runtimeService.getVariable(instanceId, entry.getKey());
            if (keyValue != null) {
                context.setVariable(entry.getKey(), factory.createValueExpression(keyValue, String.class));
            }
        }
        ValueExpression valueExpression = factory.createValueExpression(context, el, boolean.class);
        try {
            result = (Boolean)valueExpression.getValue(context);
        } catch (Exception e) {
            logger.error("流程变量条件表达式" + el + "没有属性不满足", e);
        }
        return result;
    }

    public static void main(String[] args) {
        ActActivitiServiceImpl a = new ActActivitiServiceImpl();
        Map<String, String> param = new HashMap<String, String>();
        param.put("abc1", "4");
        param.put("abc2", "0");
        System.out.println(a.isCondition(param, "${abc1 > 3 && abc2 < 5}"));
    }

    /**
     * 
     * 获取流程实例的表单的控件，然后查询运行时表，如果表单对应的控件有值，填上
     *
     * @return：List<ActAljoinFormDataRun>
     *
     * @author：zhongjy
     *
     * @date：2017年11月1日 下午4:22:56
     */
    @Override
    public List<ActAljoinFormDataRun> getFormDataRun(String processInstanceId) throws Exception {
        List<ActAljoinFormDataRun> list = new ArrayList<ActAljoinFormDataRun>();
        // 根据流程实例获取运行时表单的控件数据
        ProcessInstance instance =
            runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String bpmnId = instance.getBusinessKey().split(",")[0];
        ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(bpmnId);
        Where<ActAljoinBpmnForm> where0 = new Where<ActAljoinBpmnForm>();
        where0.eq("bpmn_id", bpmnRun.getOrgnlId());
        where0.eq("element_type", "StartEvent");
        where0.setSqlSelect("form_id");
        ActAljoinBpmnForm bpmnForm = actAljoinBpmnFormService.selectOne(where0);
        // 运行时表单
        ActAljoinFormRun actAljoinFormRun = actAljoinFormRunService.selectById(bpmnForm.getFormId());
        // 运行时表单控件
        Where<ActAljoinFormWidgetRun> where1 = new Where<ActAljoinFormWidgetRun>();
        where1.eq("form_id", actAljoinFormRun.getOrgnlId());
        where1.eq("version", actAljoinFormRun.getVersion());
        where1.setSqlSelect("widget_id");
        List<ActAljoinFormWidgetRun> actAljoinFormWidgetRunList = actAljoinFormWidgetRunService.selectList(where1);

        List<String> formWidgetIdList = new ArrayList<String>();
        for (ActAljoinFormWidgetRun actAljoinFormWidgetRun : actAljoinFormWidgetRunList) {
            formWidgetIdList.add(actAljoinFormWidgetRun.getWidgetId());
        }

        // 优化
        // 查询控件对应的内容
        Where<ActAljoinFormDataRun> where = new Where<ActAljoinFormDataRun>();
        where.eq("proc_inst_id", processInstanceId);
        where.in("form_widget_id", formWidgetIdList);
        where.setSqlSelect("form_widget_id,form_widget_value");
        List<ActAljoinFormDataRun> formDataRun2List = actAljoinFormDataRunService.selectList(where);
        Map<String, ActAljoinFormDataRun> actAljoinFormDataRunMap = new HashMap<String, ActAljoinFormDataRun>();
        for (ActAljoinFormDataRun actAljoinFormDataRun : formDataRun2List) {
            actAljoinFormDataRunMap.put(actAljoinFormDataRun.getFormWidgetId(), actAljoinFormDataRun);
        }
        for (ActAljoinFormWidgetRun actAljoinFormWidgetRun : actAljoinFormWidgetRunList) {
            ActAljoinFormDataRun formDataRun = new ActAljoinFormDataRun();
            formDataRun.setFormWidgetId(actAljoinFormWidgetRun.getWidgetId());
            ActAljoinFormDataRun formDataRun2 = actAljoinFormDataRunMap.get(actAljoinFormWidgetRun.getWidgetId());
            if (formDataRun2 != null) {
                formDataRun.setFormWidgetValue(formDataRun2.getFormWidgetValue());
            }
            list.add(formDataRun);
        }
        return list;
    }

    @Override
    public Map<String, String> getConditionParamValue(List<ActAljoinFormDataRun> formDataRunList, String condition)
        throws Exception {
        Map<String, String> retMap = new HashMap<String, String>();
        for (ActAljoinFormDataRun actAljoinFormDataRun : formDataRunList) {
            if (condition.contains(actAljoinFormDataRun.getFormWidgetId().substring(12))) {
                retMap.put(actAljoinFormDataRun.getFormWidgetId().substring(12),
                    actAljoinFormDataRun.getFormWidgetValue());
            }
        }
        return retMap;
    }

    @Override
    @Transactional
    public void insertProcessQuery(ProcessInstance processInstance, String isUrgent, ActAljoinFormRun runForm,
        String processTitle, String createFullUserName, String currentHandleFullUserName, Long categoryId)
        throws Exception {

        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(processInstance.getProcessInstanceId()).singleResult();
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery()
            .processInstanceId(processInstance.getProcessInstanceId()).singleResult();

        // 流程启动成功后，插入流程查询数据
        ActAljoinQuery actAljoinQuery = new ActAljoinQuery();
        actAljoinQuery.setProcessInstanceId(historicProcessInstance.getId());
        actAljoinQuery.setProcessName(historicProcessInstance.getProcessDefinitionName());
        // 获取表单标题控件中的标题
        if (StringUtils.isNotEmpty(processTitle)) {
            // 固定表单标题
            actAljoinQuery.setProcessTitle(processTitle);
        } else if (runForm != null) {
            // 有标题控件的自定表单的标题
            Where<ActAljoinFormDataRun> actAljoinFormDataRunWhere = new Where<ActAljoinFormDataRun>();
            actAljoinFormDataRunWhere.eq("form_id", runForm.getId());
            actAljoinFormDataRunWhere.eq("proc_inst_id", historicProcessInstance.getId());
            List<ActAljoinFormDataRun> actAljoinFormDataRunList =
                actAljoinFormDataRunService.selectList(actAljoinFormDataRunWhere);
            String documentNumber = "";
            for (ActAljoinFormDataRun actAljoinFormDataRun : actAljoinFormDataRunList) {
                if (actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_biz_title_")) {
                    // 标题
                    actAljoinQuery.setProcessTitle(actAljoinFormDataRun.getFormWidgetValue());
                } else if (actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_biz_finish_time_")) {
                    // 办结时间
                    if (!StringUtils.isEmpty(actAljoinFormDataRun.getFormWidgetValue())) {
                        actAljoinQuery.setLimitFinishTime(
                            DateUtil.str2datetime(actAljoinFormDataRun.getFormWidgetValue() + ":00"));
                    } else {
                        actAljoinQuery.setLimitFinishTime(null);
                    }
                } else if (actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_come_text")) {
                    // 来文文号
                    actAljoinQuery.setSerialNumber(actAljoinFormDataRun.getFormWidgetValue());
                } else if (actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_waternum_") 
                    || actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_writing_")){
                    // 文号
                    if(StringUtils.isNotEmpty(documentNumber)){
                        documentNumber += "; " + actAljoinFormDataRun.getFormWidgetValue();
                    }else{
                        documentNumber += actAljoinFormDataRun.getFormWidgetValue();
                    }
                    actAljoinQuery.setReferenceNumber(documentNumber);
                }

            }

            if (StringUtils.isEmpty(actAljoinQuery.getProcessTitle())) {
                actAljoinQuery.setProcessTitle(
                    "(" + createFullUserName + ")" + historicProcessInstance.getProcessDefinitionName());
            }
        }
        actAljoinQuery.setCreateFullUserName(createFullUserName);
        actAljoinQuery.setCurrentHandleFullUserName(currentHandleFullUserName);
        List<ActAljoinCategory> actAljoinCategoryList = actAljoinCategoryService.getAllParentCategoryList(categoryId);
        String categoryIds = "";
        for (ActAljoinCategory actAljoinCategory : actAljoinCategoryList) {
            categoryIds += actAljoinCategory.getId() + ",";
        }
        if (!"".equals(categoryIds)) {
            categoryIds = categoryIds.substring(0, categoryIds.length() - 1);
        }
        actAljoinQuery.setProcessCategoryIds(categoryIds);
        if (StringUtils.isEmpty(isUrgent) || "1".equals(isUrgent)) {
            actAljoinQuery.setUrgentStatus("一般");
        } else if ("2".equals(isUrgent)) {
            actAljoinQuery.setUrgentStatus("紧急");
        } else if ("3".equals(isUrgent)) {
            actAljoinQuery.setUrgentStatus("加急");
        }
        actAljoinQuery.setStartTime(historicProcessInstance.getStartTime());
        ActAljoinQueryHis actAljoinQueryHis = new ActAljoinQueryHis();
        actAljoinQuery.setStartTask(task.getId());
        BeanUtils.copyProperties(actAljoinQuery, actAljoinQueryHis);
        actAljoinQueryService.insert(actAljoinQuery);
        actAljoinQueryHisService.insert(actAljoinQueryHis);
    }

    @Override
    public void jump2Task(String targetTaskKey, String processInstanceId) throws Exception {

        TaskEntity currentTask =
            (TaskEntity)taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        jump(currentTask, targetTaskKey, processInstanceId);
    }

    private void jump(final TaskEntity currentTaskEntity, String targetTaskDefinitionKey, String processInstanceId)
        throws Exception {
        // 获取流程发布Id
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
            .singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity =
            (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                .getDeployedProcessDefinition(definitionId);

        List<ActivityImpl> activitiImplList = processDefinitionEntity.getActivities();
        // 单钱任务节点
        ActivityImpl currentActivitiImpl = null;
        for (ActivityImpl activityImpl : activitiImplList) {
            if (activityImpl.getId().equals(targetTaskDefinitionKey)) {
                currentActivitiImpl = activityImpl;
                break;
            }
        }
        final ActivityImpl activity = currentActivitiImpl;
        final ExecutionEntity execution = (ExecutionEntity)runtimeService.createExecutionQuery()
            .executionId(currentTaskEntity.getExecutionId()).singleResult();

        // Command对象
        ((RuntimeServiceImpl)runtimeService).getCommandExecutor().execute(new Command<Object>() {
            @Override
            public Object execute(CommandContext commandContext) {
                // 创建新任务
                execution.setActivity(activity);
                execution.executeActivity(activity);

                // 删除当前的任务
                // 不能删除当前正在执行的任务，所以要先清除掉关联
                currentTaskEntity.setExecutionId(null);
                taskService.saveTask(currentTaskEntity);
                taskService.deleteTask(currentTaskEntity.getId(), true);
                return execution;
            }
        });
    }

    @Override
    public void jump2Task2(String targetTaskKey, String processInstanceId) throws Exception {
        // 获取流程发布Id
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
            .singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity =
            (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                .getDeployedProcessDefinition(definitionId);

        List<ActivityImpl> activitiImplList = processDefinitionEntity.getActivities();
        TaskEntity currentTask =
            (TaskEntity)taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        // 获取当前任务节点和目标任务节点
        ActivityImpl currentActivitiImpl = null;
        ActivityImpl targetActivitiImpl = null;
        for (ActivityImpl activityImpl : activitiImplList) {
            if (activityImpl.getId().equals(currentTask.getTaskDefinitionKey())) {
                currentActivitiImpl = activityImpl;
            } else if (activityImpl.getId().equals(targetTaskKey)) {
                targetActivitiImpl = activityImpl;
            }
        }
        // 通过活动可以获得流程 将要出去的路线，只要更改出去的目的Activity ，就可以实现自由的跳转
        Map<String, ActivityImpl> activityImplMap = new HashMap<String, ActivityImpl>();
        List<PvmTransition> outgoingTransitions = currentActivitiImpl.getOutgoingTransitions();
        for (PvmTransition pvmTransition : outgoingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl)pvmTransition;
            activityImplMap.put(transitionImpl.getId(), transitionImpl.getDestination());
            if (targetActivitiImpl != null) {
                transitionImpl.setDestination(targetActivitiImpl);
            }
        }
        taskService.complete(currentTask.getId());
        // 恢复原流程出口-虽然是我提交的，但是代码是钟工写的
        for (PvmTransition pvmTransition : outgoingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl)pvmTransition;
            if (activityImplMap.get(transitionImpl.getId()) != null) {
                transitionImpl.setDestination(activityImplMap.get(transitionImpl.getId()));
            }
        }
    }

    @Override
    public void jump2Task3(String targetTaskKey, String processInstanceId, String executionId) throws Exception {
        // 获取流程发布Id
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
            .singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity =
            (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                .getDeployedProcessDefinition(definitionId);

        List<ActivityImpl> activitiImplList = processDefinitionEntity.getActivities();
        TaskEntity currentTask = (TaskEntity)taskService.createTaskQuery().executionId(executionId).singleResult();
        // 获取当前任务节点和目标任务节点
        ActivityImpl currentActivitiImpl = null;
        ActivityImpl targetActivitiImpl = null;
        for (ActivityImpl activityImpl : activitiImplList) {
            if (activityImpl.getId().equals(currentTask.getTaskDefinitionKey())) {
                currentActivitiImpl = activityImpl;
            } else if (activityImpl.getId().equals(targetTaskKey)) {
                targetActivitiImpl = activityImpl;
            }
        }
        // 通过活动可以获得流程 将要出去的路线，只要更改出去的目的Activity ，就可以实现自由的跳转
        Map<String, ActivityImpl> activityImplMap = new HashMap<String, ActivityImpl>();
        List<PvmTransition> outgoingTransitions = currentActivitiImpl.getOutgoingTransitions();
        for (PvmTransition pvmTransition : outgoingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl)pvmTransition;
            activityImplMap.put(transitionImpl.getId(), transitionImpl.getDestination());
            transitionImpl.setDestination(targetActivitiImpl);
        }
        taskService.complete(currentTask.getId());
        // 恢复原流程出口-虽然是我提交的，但是代码是钟工写的
        for (PvmTransition pvmTransition : outgoingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl)pvmTransition;
            if (activityImplMap.get(transitionImpl.getId()) != null) {
                transitionImpl.setDestination(activityImplMap.get(transitionImpl.getId()));
            }
        }
    }

    @Override
    public List<TaskDefinition> getPreTaskInfo(String currentTaskKey, String processInstanceId) throws Exception {
        List<TaskDefinition> taskList = new ArrayList<TaskDefinition>();
        // 获取流程发布Id
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
            .singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity =
            (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                .getDeployedProcessDefinition(definitionId);
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();
        // 遍历所有节点信息
        for (ActivityImpl activityImpl : activitiList) {
            List<PvmTransition> list = activityImpl.getOutgoingTransitions();
            for (PvmTransition pvmTransition : list) {
                if (currentTaskKey.equals(pvmTransition.getDestination().getId())) {
                    if (!activityImpl.getProperty("type").equals("startEvent")) {
                        if (activityImpl.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                            // 上一级是用户节点
                            taskList.add(
                                ((UserTaskActivityBehavior)(activityImpl).getActivityBehavior()).getTaskDefinition());
                        } else if (activityImpl.getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
                            // 上一级是用户节点（多实例并行）
                            taskList.add(((UserTaskActivityBehavior)(((ParallelMultiInstanceBehavior)(activityImpl)
                                .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition());
                        } else if (activityImpl.getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
                            // 上一级是用户节点（多实例串行）
                            taskList.add(((UserTaskActivityBehavior)(((SequentialMultiInstanceBehavior)(activityImpl)
                                .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition());
                        } else if (activityImpl.getActivityBehavior() instanceof ParallelGatewayActivityBehavior) {
                            // 上一级是并行网关节点(不返回上级节点)
                            return taskList;
                        }

                    }
                }
            }
        }
        return taskList;
    }

    /**
     * 
     * 自由流程获取上一个节点，含会签节点的情况
     *
     * @return：List<TaskDefinition>
     *
     * @author：zhongjy
     *
     * @date：2018年1月12日 下午2:11:31
     */
    private List<TaskDefinition> getPreDefList(List<HistoricActivityInstance> hisList, String processInstanceId)
        throws Exception {
        List<TaskDefinition> taskList = new ArrayList<TaskDefinition>();
        if (StringUtils.isNotEmpty(hisList.get(0).getAssignee())) {
            // 不存在撤回的情况
            TaskDefinition def = new TaskDefinition(null);
            def.setKey(hisList.get(0).getActivityId());
            JuelExpression exp = new JuelExpression(null, hisList.get(0).getAssignee());
            def.setAssigneeExpression(exp);
            JuelExpression expName = new JuelExpression(null, hisList.get(0).getActivityName());
            def.setNameExpression(expName);
            taskList.add(def);
            // 如果上个节点是多实例节点，需要取出多个
            if (getMultiInstance(processInstanceId, hisList.get(0).getActivityId()) != null) {
                // 获取多实例节点的个数实际完成的任务个数(防止相同的多实例节点多次执行)
                String exeId = hisList.get(0).getExecutionId();
                HistoricVariableInstance var =
                    historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId)
                        .executionId(exeId).variableName("nrOfCompletedInstances").singleResult();
                if (var == null) {
                    Execution exec = runtimeService.createExecutionQuery().executionId(exeId).singleResult();
                    String mainExeId = "";
                    if (exec != null) {
                        mainExeId = exec.getParentId();
                    } else {
                        Where<ActAljoinExecutionHis> actAljoinExecutionHisWhere = new Where<ActAljoinExecutionHis>();
                        actAljoinExecutionHisWhere.eq("exec_id", exeId);
                        actAljoinExecutionHisWhere.setSqlSelect("parent_id");
                        ActAljoinExecutionHis execHis =
                            actAljoinExecutionHisService.selectOne(actAljoinExecutionHisWhere);
                        if (execHis != null) {
                            mainExeId = execHis.getParentId();
                        }
                    }
                    var = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId)
                        .executionId(mainExeId).variableName("nrOfCompletedInstances").singleResult();
                }
                Integer mCount = 9999;
                if (var != null) {
                    mCount = (Integer)var.getValue();
                }
                // 多实例节点
                for (int i = 1; i < hisList.size(); i++) {
                    if (hisList.get(i).getActivityId().equals(hisList.get(0).getActivityId())
                        && getMultiInstance(processInstanceId, hisList.get(i).getActivityId()) != null) {
                        if (mCount.intValue() > taskList.size()) {
                            TaskDefinition def1 = new TaskDefinition(null);
                            def1.setKey(hisList.get(i).getActivityId());
                            JuelExpression exp1 = new JuelExpression(null, hisList.get(i).getAssignee());
                            def1.setAssigneeExpression(exp1);
                            JuelExpression expName1 = new JuelExpression(null, hisList.get(i).getActivityName());
                            def1.setNameExpression(expName1);
                            taskList.add(def1);
                        }
                    }
                }
            }
        } else {
            // 存在撤回（含多次撤回）的情况，需要获取上一个办理人不为空 并且 不等于当前办理人并且非当前节点
            if (hisList.size() > 1) {
                // 撤回到的节点
                HistoricActivityInstance actInst = hisList.get(1);
                int falg = 0;
                if (hisList.size() > 2) {
                    for (int i = 2; i < hisList.size(); i++) {
                        if (StringUtils.isNotEmpty(hisList.get(i).getAssignee())
                            && !actInst.getActivityId().equals(hisList.get(i).getActivityId())) {
                            falg = i;
                            break;
                        }
                    }

                    TaskDefinition def = new TaskDefinition(null);
                    def.setKey(hisList.get(falg).getActivityId());
                    JuelExpression exp = new JuelExpression(null, hisList.get(falg).getAssignee());
                    def.setAssigneeExpression(exp);
                    JuelExpression expName = new JuelExpression(null, hisList.get(falg).getActivityName());
                    def.setNameExpression(expName);
                    taskList.add(def);

                    // 如果上个节点是多实例节点，需要取出多个
                    if (getMultiInstance(processInstanceId, hisList.get(falg).getActivityId()) != null) {
                        // 获取多实例节点的个数实际完成的任务个数(防止相同的多实例节点多次执行)
                        String exeId = hisList.get(falg).getExecutionId();
                        HistoricVariableInstance var =
                            historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId)
                                .executionId(exeId).variableName("nrOfCompletedInstances").singleResult();
                        if (var == null) {
                            Execution exec = runtimeService.createExecutionQuery().executionId(exeId).singleResult();
                            String mainExeId = "";
                            if (exec != null) {
                                mainExeId = exec.getParentId();
                            } else {
                                Where<ActAljoinExecutionHis> actAljoinExecutionHisWhere =
                                    new Where<ActAljoinExecutionHis>();
                                actAljoinExecutionHisWhere.eq("exec_id", exeId);
                                actAljoinExecutionHisWhere.setSqlSelect("parent_id");
                                ActAljoinExecutionHis execHis =
                                    actAljoinExecutionHisService.selectOne(actAljoinExecutionHisWhere);
                                if (execHis != null) {
                                    mainExeId = execHis.getParentId();
                                }
                            }
                            var = historyService.createHistoricVariableInstanceQuery()
                                .processInstanceId(processInstanceId).executionId(mainExeId)
                                .variableName("nrOfCompletedInstances").singleResult();
                        }
                        Integer mCount = 9999;
                        if (var != null) {
                            mCount = (Integer)var.getValue();
                        }
                        // 多实例节点
                        for (int i = falg; i < hisList.size(); i++) {
                            if (hisList.get(i).getActivityId().equals(hisList.get(falg).getActivityId())
                                && getMultiInstance(processInstanceId, hisList.get(i).getActivityId()) != null) {
                                if (mCount.intValue() > taskList.size()) {
                                    TaskDefinition def1 = new TaskDefinition(null);
                                    def1.setKey(hisList.get(i).getActivityId());
                                    JuelExpression exp1 = new JuelExpression(null, hisList.get(i).getAssignee());
                                    def1.setAssigneeExpression(exp1);
                                    JuelExpression expName1 =
                                        new JuelExpression(null, hisList.get(i).getActivityName());
                                    def1.setNameExpression(expName1);
                                    taskList.add(def1);
                                }
                            }
                        }
                    }
                }
            }
        }
        return taskList;
    }

    @Override
    public List<TaskDefinition> getPreTaskInfo2(String currentTaskKey, String processInstanceId) throws Exception {
        List<TaskDefinition> taskList = new ArrayList<TaskDefinition>();
        // 获取流程发布Id
        ProcessInstance instance =
            runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        // 获取最后一个已经完成的所有节点
        List<HistoricActivityInstance> hisList =
            historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                .activityType("userTask").finished().orderByHistoricActivityInstanceEndTime().desc().list();

        // 如果是自由流程，根据时间到活动历史表去查询
        String keys = instance.getBusinessKey();
        if (StringUtils.isNotEmpty(keys)) {
            // 取得bpmn_run表的数据
            String bpmnId = keys.split(",")[0];
            // ActAljoinBpmn bpmn = actAljoinBpmnService.selectById(bpmnId);
            Where<ActAljoinBpmnRun> bpmnRunWherre = new Where<ActAljoinBpmnRun>();
            bpmnRunWherre.setSqlSelect("is_free");
            bpmnRunWherre.eq("id", bpmnId);
            ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectOne(bpmnRunWherre);
            // if (bpmn != null && bpmn.getIsFree().intValue() == 1) {
            // 修改成非自由流程也通过本方法获取上级办理节点（原来非自由流程时通过箭头获取上级办理人的）
            if (bpmnRun != null && bpmnRun.getIsFree().intValue() == 1) {
                if (hisList.size() > 0) {
                    // 需要考虑有撤回的情况，如有有撤回，最后一个已经完成的节点的办理人为空的，倒数第二个是自己，倒数点三个才是真正的上一个办理节点
                    taskList = getPreDefList(hisList, processInstanceId);
                    return taskList;
                }
            }
        }
        // 以下是处理非自由流程（这个方法理论上不会执行到，上面获取getPreDefList的时候已经去掉了bpmn.getIsFree().intValue() == 1这个条件）
        if (null != instance) {
            String definitionId = instance.getProcessDefinitionId();
            ProcessDefinitionEntity processDefinitionEntity =
                (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                    .getDeployedProcessDefinition(definitionId);
            List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();
            // 遍历所有节点信息
            for (ActivityImpl activityImpl : activitiList) {
                List<PvmTransition> list = activityImpl.getOutgoingTransitions();
                for (PvmTransition pvmTransition : list) {
                    if (currentTaskKey.equals(pvmTransition.getDestination().getId())) {
                        if (!activityImpl.getProperty("type").equals("startEvent")) {
                            if (activityImpl.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                                // 上一级是用户节点
                                // taskList.add(((UserTaskActivityBehavior) (activityImpl).getActivityBehavior())
                                // .getTaskDefinition());
                                TaskDefinition taskDef =
                                    ((UserTaskActivityBehavior)(activityImpl).getActivityBehavior())
                                        .getTaskDefinition();
                                taskList.addAll(getPreDefList4Custom(hisList, processInstanceId, taskDef.getKey()));
                            } else if (activityImpl.getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
                                // 上一级是用户节点（多实例并行）
                                // taskList
                                // .add(((UserTaskActivityBehavior) (((ParallelMultiInstanceBehavior) (activityImpl)
                                // .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition());
                                TaskDefinition taskDef =
                                    ((UserTaskActivityBehavior)(((ParallelMultiInstanceBehavior)(activityImpl)
                                        .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition();
                                taskList.addAll(getPreDefList4Custom(hisList, processInstanceId, taskDef.getKey()));
                            } else if (activityImpl.getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
                                // 上一级是用户节点（多实例串行）
                                // taskList.add(
                                // ((UserTaskActivityBehavior) (((SequentialMultiInstanceBehavior) (activityImpl)
                                // .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition());
                                TaskDefinition taskDef =
                                    ((UserTaskActivityBehavior)(((SequentialMultiInstanceBehavior)(activityImpl)
                                        .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition();
                                taskList.addAll(getPreDefList4Custom(hisList, processInstanceId, taskDef.getKey()));
                            } else if (activityImpl.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior) {
                                // 需要获取执行时间最近的那个并串行网关ID(因为一个节点有可能有多个网关的源头)
                                List<HistoricActivityInstance> queryList = historyService
                                    .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                                    .finished().activityType("exclusiveGateway")
                                    .orderByHistoricActivityInstanceEndTime().desc().list();

                                if (queryList.size() > 0) {
                                    if (activityImpl.getId().equals(queryList.get(0).getActivityId())) {
                                        // 上一级是串行网关节点(不返回上级节点),进一步回去上级用户节点信息
                                        List<PvmTransition> pvmList = activityImpl.getIncomingTransitions();
                                        for (PvmTransition pmv : pvmList) {
                                            PvmActivity pvmActivity = pmv.getSource();
                                            // TaskDefinition def = new TaskDefinition(null);
                                            // def.setKey(pvmActivity.getId());
                                            taskList.addAll(
                                                getPreDefList4Custom(hisList, processInstanceId, pvmActivity.getId()));
                                            break;
                                        }
                                        return taskList;
                                    }
                                }
                            } else if (activityImpl.getActivityBehavior() instanceof ParallelGatewayActivityBehavior) {
                                // 上一级是并行网关节点(不返回上级节点),进一步回去上级用户节点信息
                                List<PvmTransition> pvmList = activityImpl.getIncomingTransitions();
                                for (PvmTransition pmv : pvmList) {
                                    PvmActivity pvmActivity = pmv.getSource();
                                    // TaskDefinition def = new TaskDefinition(null);
                                    // def.setKey(pvmActivity.getId());
                                    taskList
                                        .addAll(getPreDefList4Custom(hisList, processInstanceId, pvmActivity.getId()));
                                    // break;
                                }
                                return taskList;
                            } else {
                                // ....
                            }
                        }
                    }
                }
            }
        }
        return taskList;
    }

    /**
     * 
     * 自由流程获取上一个节点，含会签节点的情况
     *
     * @return：List<TaskDefinition>
     *
     * @author：zhongjy
     *
     * @date：2018年1月12日 下午2:11:31
     */
    private List<TaskDefinition> getPreDefList4Custom(List<HistoricActivityInstance> hisListTemp,
        String processInstanceId, String preTaskKey) throws Exception {
        List<HistoricActivityInstance> hisList = new ArrayList<HistoricActivityInstance>();
        for (HistoricActivityInstance historicActivityInstance : hisListTemp) {
            if (historicActivityInstance.getActivityId().equals(preTaskKey)) {
                hisList.add(historicActivityInstance);
            }
        }

        List<TaskDefinition> taskList = new ArrayList<TaskDefinition>();
        if (hisList.size() > 0 && StringUtils.isNotEmpty(hisList.get(0).getAssignee())) {
            // 不存在撤回的情况
            TaskDefinition def = new TaskDefinition(null);
            def.setKey(hisList.get(0).getActivityId());
            JuelExpression exp = new JuelExpression(null, hisList.get(0).getAssignee());
            def.setAssigneeExpression(exp);
            JuelExpression expName = new JuelExpression(null, hisList.get(0).getActivityName());
            def.setNameExpression(expName);
            taskList.add(def);
            // 如果上个节点是多实例节点，需要取出多个
            if (getMultiInstance(processInstanceId, hisList.get(0).getActivityId()) != null) {
                // 获取多实例节点的个数实际完成的任务个数(防止相同的多实例节点多次执行)
                String exeId = hisList.get(0).getExecutionId();
                HistoricVariableInstance var =
                    historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId)
                        .executionId(exeId).variableName("nrOfCompletedInstances").singleResult();
                if (var == null) {
                    Execution exec = runtimeService.createExecutionQuery().executionId(exeId).singleResult();
                    String mainExeId = "";
                    if (exec != null) {
                        mainExeId = exec.getParentId();
                    } else {
                        Where<ActAljoinExecutionHis> actAljoinExecutionHisWhere = new Where<ActAljoinExecutionHis>();
                        actAljoinExecutionHisWhere.eq("exec_id", exeId);
                        actAljoinExecutionHisWhere.setSqlSelect("parent_id");
                        ActAljoinExecutionHis execHis =
                            actAljoinExecutionHisService.selectOne(actAljoinExecutionHisWhere);
                        if (execHis != null) {
                            mainExeId = execHis.getParentId();
                        }
                    }
                    var = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId)
                        .executionId(mainExeId).variableName("nrOfCompletedInstances").singleResult();
                }
                Integer mCount = 9999;
                if (var != null) {
                    mCount = (Integer)var.getValue();
                }
                // 多实例节点
                for (int i = 1; i < hisList.size(); i++) {
                    if (hisList.get(i).getActivityId().equals(hisList.get(0).getActivityId())
                        && getMultiInstance(processInstanceId, hisList.get(i).getActivityId()) != null) {
                        if (mCount.intValue() > taskList.size()) {
                            TaskDefinition def1 = new TaskDefinition(null);
                            def1.setKey(hisList.get(i).getActivityId());
                            JuelExpression exp1 = new JuelExpression(null, hisList.get(i).getAssignee());
                            def1.setAssigneeExpression(exp1);
                            JuelExpression expName1 = new JuelExpression(null, hisList.get(i).getActivityName());
                            def1.setNameExpression(expName1);
                            taskList.add(def1);
                        }
                    }
                }
            }
        } else {
            // 存在撤回（含多次撤回）的情况，需要获取上一个办理人不为空 并且 不等于当前办理人并且非当前节点
            if (hisList.size() > 1) {
                // 撤回到的节点
                HistoricActivityInstance actInst = hisList.get(1);
                int falg = 0;
                if (hisList.size() > 2) {
                    for (int i = 2; i < hisList.size(); i++) {
                        if (StringUtils.isNotEmpty(hisList.get(i).getAssignee())
                            && !actInst.getActivityId().equals(hisList.get(i).getActivityId())) {
                            falg = i;
                            break;
                        }
                    }

                    TaskDefinition def = new TaskDefinition(null);
                    def.setKey(hisList.get(falg).getActivityId());
                    JuelExpression exp = new JuelExpression(null, hisList.get(falg).getAssignee());
                    def.setAssigneeExpression(exp);
                    JuelExpression expName = new JuelExpression(null, hisList.get(falg).getActivityName());
                    def.setNameExpression(expName);
                    taskList.add(def);

                    // 如果上个节点是多实例节点，需要取出多个
                    if (getMultiInstance(processInstanceId, hisList.get(falg).getActivityId()) != null) {
                        // 获取多实例节点的个数实际完成的任务个数(防止相同的多实例节点多次执行)
                        String exeId = hisList.get(falg).getExecutionId();
                        HistoricVariableInstance var =
                            historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId)
                                .executionId(exeId).variableName("nrOfCompletedInstances").singleResult();
                        if (var == null) {
                            Execution exec = runtimeService.createExecutionQuery().executionId(exeId).singleResult();
                            String mainExeId = "";
                            if (exec != null) {
                                mainExeId = exec.getParentId();
                            } else {
                                Where<ActAljoinExecutionHis> actAljoinExecutionHisWhere =
                                    new Where<ActAljoinExecutionHis>();
                                actAljoinExecutionHisWhere.eq("exec_id", exeId);
                                actAljoinExecutionHisWhere.setSqlSelect("parent_id");
                                ActAljoinExecutionHis execHis =
                                    actAljoinExecutionHisService.selectOne(actAljoinExecutionHisWhere);
                                if (execHis != null) {
                                    mainExeId = execHis.getParentId();
                                }
                            }
                            var = historyService.createHistoricVariableInstanceQuery()
                                .processInstanceId(processInstanceId).executionId(mainExeId)
                                .variableName("nrOfCompletedInstances").singleResult();
                        }
                        Integer mCount = 9999;
                        if (var != null) {
                            mCount = (Integer)var.getValue();
                        }
                        // 多实例节点
                        for (int i = falg; i < hisList.size(); i++) {
                            if (hisList.get(i).getActivityId().equals(hisList.get(falg).getActivityId())
                                && getMultiInstance(processInstanceId, hisList.get(i).getActivityId()) != null) {
                                if (mCount.intValue() > taskList.size()) {
                                    TaskDefinition def1 = new TaskDefinition(null);
                                    def1.setKey(hisList.get(i).getActivityId());
                                    JuelExpression exp1 = new JuelExpression(null, hisList.get(i).getAssignee());
                                    def1.setAssigneeExpression(exp1);
                                    JuelExpression expName1 =
                                        new JuelExpression(null, hisList.get(i).getActivityName());
                                    def1.setNameExpression(expName1);
                                    taskList.add(def1);
                                }
                            }
                        }
                    }
                }
            }
        }
        return taskList;
    }

    /**
     * 
     * 对getPreTaskInfo2进行优化，里面的部分数据先获取在传进来
     *
     * @return：List<TaskDefinition>
     *
     * @author：zhongjy
     *
     * @date：2017年12月13日 下午2:28:52
     */
    @Override
    public List<TaskDefinition> getPreTaskInfo3(String currentTaskKey, ProcessInstance instance, ActAljoinBpmn bpmn)
        throws Exception {
        List<TaskDefinition> taskList = new ArrayList<TaskDefinition>();
        // 如果是自由流程，根据时间到活动历史表去查询
        String keys = instance.getBusinessKey();
        if (StringUtils.isNotEmpty(keys)) {
            if (bpmn != null && bpmn.getIsFree().intValue() == 1) {
                // 获取最后一个已经完成的节点
                List<HistoricActivityInstance> hisList = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(instance.getProcessInstanceId()).finished()
                    .orderByHistoricActivityInstanceEndTime().desc().list();
                if (hisList.size() > 0) {
                    TaskDefinition def = new TaskDefinition(null);
                    def.setKey(hisList.get(0).getActivityId());
                    taskList.add(def);
                    return taskList;
                }
            }
        }
        if (null != instance) {
            String definitionId = instance.getProcessDefinitionId();
            ProcessDefinitionEntity processDefinitionEntity =
                (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                    .getDeployedProcessDefinition(definitionId);
            List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();
            // 遍历所有节点信息
            for (ActivityImpl activityImpl : activitiList) {
                List<PvmTransition> list = activityImpl.getOutgoingTransitions();
                for (PvmTransition pvmTransition : list) {
                    if (currentTaskKey.equals(pvmTransition.getDestination().getId())) {
                        if (!activityImpl.getProperty("type").equals("startEvent")) {
                            if (activityImpl.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                                // 上一级是用户节点
                                taskList.add(((UserTaskActivityBehavior)(activityImpl).getActivityBehavior())
                                    .getTaskDefinition());
                            } else if (activityImpl.getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
                                // 上一级是用户节点（多实例并行）
                                taskList.add(((UserTaskActivityBehavior)(((ParallelMultiInstanceBehavior)(activityImpl)
                                    .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition());
                            } else if (activityImpl.getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
                                // 上一级是用户节点（多实例串行）
                                taskList
                                    .add(((UserTaskActivityBehavior)(((SequentialMultiInstanceBehavior)(activityImpl)
                                        .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition());
                            } else if (activityImpl.getActivityBehavior() instanceof ParallelGatewayActivityBehavior
                                || activityImpl.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior) {
                                // 上一级是并行网关节点(不返回上级节点),进一步回去上级用户节点信息
                                List<PvmTransition> pvmList = activityImpl.getIncomingTransitions();
                                for (PvmTransition pmv : pvmList) {
                                    PvmActivity pvmActivity = pmv.getSource();
                                    TaskDefinition def = new TaskDefinition(null);
                                    def.setKey(pvmActivity.getId());
                                    taskList.add(def);
                                    break;
                                }
                                return taskList;
                            } else {
                                // ....
                            }
                        }
                    }
                }
            }
        }
        return taskList;
    }

    @Override
    public List<TaskDefinition> getAllTaskInfoExcludeSelf(String currentTaskKey, String processInstanceId)
        throws Exception {
        List<TaskDefinition> taskList = new ArrayList<TaskDefinition>();
        // 获取流程发布Id
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
            .singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity =
            (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                .getDeployedProcessDefinition(definitionId);
        // 获取流程所有节点
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();

        // 先对节点进行排序-----begin
        Map<String, ActivityImpl> activitiMap = new HashMap<String, ActivityImpl>();
        List<ActivityImpl> newActivitiList = new ArrayList<ActivityImpl>();
        // 有来源的节点，比如开始节点是没有来源的（这里指针对单线的自由流程）
        Map<String, String> keyAndParentKey = new HashMap<String, String>();
        String startKey = "";
        for (ActivityImpl activityImpl : activitiList) {
            if (activityImpl.getId().startsWith("StartEvent_")) {
                startKey = activityImpl.getId();
            }
            if (activityImpl.getIncomingTransitions() != null && activityImpl.getIncomingTransitions().size() > 0) {
                keyAndParentKey.put(activityImpl.getId(),
                    activityImpl.getIncomingTransitions().get(0).getSource().getId());
            }
            activitiMap.put(activityImpl.getId(), activityImpl);
        }
        int len = keyAndParentKey.size();
        String parentKey = startKey;
        for (int i = 0; i < len; i++) {
            for (Map.Entry<String, String> entry : keyAndParentKey.entrySet()) {
                String key = entry.getKey();
                String pkey = entry.getValue();
                if (parentKey.equals(pkey)) {
                    newActivitiList.add(activitiMap.get(key));
                    parentKey = key;
                    break;
                }
            }
        }
        // 先对节点进行排序-----end

        // 遍历所有节点信息
        for (ActivityImpl activityImpl : newActivitiList) {
            if ("userTask".equals(activityImpl.getProperty("type")) && !currentTaskKey.equals(activityImpl.getId())) {
                if (activityImpl.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                    // 一般节点
                    taskList.add(((UserTaskActivityBehavior)(activityImpl).getActivityBehavior()).getTaskDefinition());
                } else if (activityImpl.getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
                    // 并行多实例
                    taskList.add((((UserTaskActivityBehavior)((ParallelMultiInstanceBehavior)(activityImpl)
                        .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition());
                } else if (activityImpl.getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
                    // 串行多实例
                    taskList.add((((UserTaskActivityBehavior)((SequentialMultiInstanceBehavior)(activityImpl)
                        .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition());
                }
            }
        }
        return taskList;
    }

    @Override
    public List<TaskDefinition> getAllTaskInfo(String processInstanceId) throws Exception {
        List<TaskDefinition> taskList = new ArrayList<TaskDefinition>();
        // 获取流程发布Id
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
            .singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity =
            (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                .getDeployedProcessDefinition(definitionId);
        // 获取流程所有节点
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();
        // 遍历所有节点信息
        for (ActivityImpl activityImpl : activitiList) {
            if ("userTask".equals(activityImpl.getProperty("type"))) {
                if (activityImpl.getActivityBehavior() instanceof UserTaskActivityBehavior) {
                    // 一般节点
                    taskList.add(((UserTaskActivityBehavior)(activityImpl).getActivityBehavior()).getTaskDefinition());
                } else if (activityImpl.getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
                    // 并行多实例
                    taskList.add((((UserTaskActivityBehavior)((ParallelMultiInstanceBehavior)(activityImpl)
                        .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition());
                } else if (activityImpl.getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
                    // 串行多实例
                    taskList.add((((UserTaskActivityBehavior)((SequentialMultiInstanceBehavior)(activityImpl)
                        .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition());
                }
            }
        }
        return taskList;
    }

    @Override
    public List<ActivityImpl> getAllActivity(String processInstanceId) throws Exception {
        // 获取流程发布Id
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
            .singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity =
            (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                .getDeployedProcessDefinition(definitionId);
        // 获取流程所有节点
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();
        return activitiList;

    }

    @Override
    public InputStream getHisImageInputStream(String processInstanceId, String string) {
        List<HistoricTaskInstance> historicList =
            historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
        InputStream is = null;
        if (historicList.size() > 0) {
            // 流程定义
            BpmnModel bpmnModel = repositoryService.getBpmnModel(historicList.get(0).getProcessDefinitionId());
            List<String> highLightedActiveActivityIds = new ArrayList<String>();
            List<String> highLightedActiveFlowIds = new ArrayList<String>();
            List<String> currentActiveActivityIds = new ArrayList<String>();
            for (HistoricTaskInstance t : historicList) {
                // 当前活动节点
                // List<String> activeActivityIds = new arr;
                // HistoricActivityInstance
                // activeActivityIds=historyService.createHistoricActivityInstanceQuery().activityId(t.getExecutionId()).activityType("endEvent").singleResult();

                if (true) {
                    // 已经运行的节点高亮
                    List<HistoricActivityInstance> activityList = historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(t.getProcessInstanceId()).list();
                    for (HistoricActivityInstance historicActivityInstance : activityList) {
                        highLightedActiveActivityIds.add(historicActivityInstance.getActivityId());
                    }
                    // 获取高亮线
                    HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId).singleResult();
                    ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity)repositoryService
                        .getProcessDefinition(processInstance.getProcessDefinitionId());
                    // highLightedActiveFlowIds =
                    // getHighLightedFlows(definitionEntity, activityList);
                    getHighLightedFlows(definitionEntity.getActivities(), highLightedActiveFlowIds,
                        highLightedActiveActivityIds);

                }

            }
            // 原来的
            // ProcessDiagramGenerator pdg =
            // processEngineConfiguration.getProcessDiagramGenerator();
            // 自定义的
            CustomProcessDiagramGenerator pdg = new CustomProcessDiagramGenerator();

            // 生成流图片
            is = pdg.generateDiagram2(bpmnModel, "PNG", highLightedActiveActivityIds, highLightedActiveFlowIds,
                processEngineConfiguration.getActivityFontName(), processEngineConfiguration.getLabelFontName(),
                processEngineConfiguration.getClassLoader(), 1.0, currentActiveActivityIds);

        }
        return is;
    }

    @Override
    public MultiInstanceLoopCharacteristics getMultiInstance(String processInstanceId, String taskKey)
        throws Exception {
        MultiInstanceLoopCharacteristics retObj = null;
        HistoricProcessInstance his =
            historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (his == null) {
            throw new Exception("查询不到流程实例");
        }
        BpmnModel model = repositoryService.getBpmnModel(his.getProcessDefinitionId());
        Collection<FlowElement> eleList = model.getMainProcess().getFlowElements();
        for (FlowElement flowElement : eleList) {
            if (flowElement instanceof UserTask && flowElement.getId().equals(taskKey)) {
                UserTask ut = (UserTask)flowElement;
                MultiInstanceLoopCharacteristics mlc = ut.getLoopCharacteristics();
                if (mlc != null) {
                    retObj = mlc;
                }
            }
        }
        return retObj;
    }

    @Override
    public void jump2Task4AddSign(Task task,String nextTaskDefKey) throws Exception {
        Where<ActAljoinTaskSignInfo> taskSignInfoWhere = new Where<ActAljoinTaskSignInfo>();
        taskSignInfoWhere.setSqlSelect("id,finish_type,task_owner_id,task_owner_name");
        taskSignInfoWhere.eq("sign_task_id", task.getId());
        ActAljoinTaskSignInfo taskSignInfo = actAljoinTaskSignInfoService.selectOne(taskSignInfoWhere);

        if(null == taskSignInfo){
            taskService.complete(task.getId());
            actTaskAddSignService.moveTo((TaskEntity)task,nextTaskDefKey);
        }else{
            // 获取流程发布Id
            String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId())
                .singleResult().getProcessDefinitionId();
            ProcessDefinitionEntity processDefinitionEntity =
                (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                    .getDeployedProcessDefinition(definitionId);

            List<ActivityImpl> activitiImplList = processDefinitionEntity.getActivities();
            TaskEntity currentTask =
                (TaskEntity)taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            // 获取当前任务节点和目标任务节点
            ActivityImpl currentActivitiImpl = null;
            ActivityImpl targetActivitiImpl = null;
            for (ActivityImpl activityImpl : activitiImplList) {
                if (activityImpl.getId().equals(currentTask.getTaskDefinitionKey())) {
                    currentActivitiImpl = activityImpl;
                } else if (activityImpl.getId().equals(nextTaskDefKey)) {
                    targetActivitiImpl = activityImpl;
                }
            }
            // 通过活动可以获得流程 将要出去的路线，只要更改出去的目的Activity ，就可以实现自由的跳转
            Map<String, ActivityImpl> activityImplMap = new HashMap<String, ActivityImpl>();
            List<PvmTransition> outgoingTransitions = currentActivitiImpl.getOutgoingTransitions();
            for (PvmTransition pvmTransition : outgoingTransitions) {
                TransitionImpl transitionImpl = (TransitionImpl)pvmTransition;
                activityImplMap.put(transitionImpl.getId(), transitionImpl.getDestination());
                if (targetActivitiImpl != null) {
                    transitionImpl.setDestination(targetActivitiImpl);
                }
            }

            // 恢复原流程出口-虽然是我提交的，但是代码是钟工写的
            for (PvmTransition pvmTransition : outgoingTransitions) {
                TransitionImpl transitionImpl = (TransitionImpl)pvmTransition;
                if (activityImplMap.get(transitionImpl.getId()) != null) {
                    transitionImpl.setDestination(activityImplMap.get(transitionImpl.getId()));
                }
            }

            if(taskSignInfo.getFinishType() == 1 || taskSignInfo.getFinishType() == 2){
            }

            taskService.complete(currentTask.getId());
        }
    }

}
