package aljoin.act.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.dao.entity.ActAljoinDelegateInfo;
import aljoin.act.dao.entity.ActAljoinFormDataDraft;
import aljoin.act.dao.entity.ActAljoinFormDataHis;
import aljoin.act.dao.entity.ActAljoinFormDataRun;
import aljoin.act.dao.entity.ActAljoinFormRun;
import aljoin.act.dao.entity.ActAljoinFormWidgetRun;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.dao.entity.ActAljoinTaskAssignee;
import aljoin.act.dao.entity.ActAljoinTaskSignInfo;
import aljoin.act.dao.mapper.ActAljoinFormDataDraftMapper;
import aljoin.act.dao.object.DraftTaskShowVO;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnRunService;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.act.iservice.ActAljoinDelegateInfoService;
import aljoin.act.iservice.ActAljoinFormDataDraftService;
import aljoin.act.iservice.ActAljoinFormDataHisService;
import aljoin.act.iservice.ActAljoinFormDataRunService;
import aljoin.act.iservice.ActAljoinFormRunService;
import aljoin.act.iservice.ActAljoinFormWidgetRunService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.iservice.ActAljoinTaskAssigneeService;
import aljoin.act.iservice.ActAljoinTaskSignInfoService;
import aljoin.act.iservice.ActHisTaskService;
import aljoin.act.iservice.ActRuTaskService;
import aljoin.act.iservice.ActTaskAddSignService;
import aljoin.act.util.ActUtil;
import aljoin.dao.config.Where;
import aljoin.object.MsgConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.util.DateUtil;

/**
 * 
 * 表单数据表(草稿)(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-30
 */
@Service
public class ActAljoinFormDataDraftServiceImpl extends ServiceImpl<ActAljoinFormDataDraftMapper, ActAljoinFormDataDraft>
    implements ActAljoinFormDataDraftService {

    @Resource
    private ActAljoinFormDataDraftMapper mapper;
    @Resource
    private ActAljoinFormWidgetRunService actAljoinFormWidgetRunService;
    @Resource
    private ActAljoinFormDataRunService actAljoinFormDataRunService;
    @Resource
    private ActActivitiService activitiService;
    @Resource
    private ActAljoinBpmnRunService actAljoinBpmnRunService;
    @Resource
    private ActAljoinBpmnService actAljoinBpmnService;
    @Resource
    private ActAljoinFormRunService actAljoinFormRunService;
    @Resource
    private HistoryService historyService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private ActAljoinFormDataHisService actAljoinFormDataHisService;
    @Resource
    private ActAljoinFormDataDraftService actAljoinFormDataDraftService;
    @Resource
    private ActAljoinTaskAssigneeService actAljoinTaskAssigneeService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;
    @Resource
    private TaskService taskService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private ActAljoinCategoryService categoryService;
    @Resource
    private ActAljoinQueryService actAljoinQueryService;
    @Resource
    private ActTaskAddSignService actTaskAddSignService;
    @Resource
    private ActAljoinTaskSignInfoService actAljoinTaskSignInfoService;
    @Resource
    private ActRuTaskService actRuTaskService;
    @Resource
    private ActAljoinDelegateInfoService actAljoinDelegateInfoService;
    @Resource
    private ActHisTaskService actHiTaskinstService;

    @Override
    public Page<ActAljoinFormDataDraft> list(PageBean pageBean, ActAljoinFormDataDraft obj) throws Exception {
        Where<ActAljoinFormDataDraft> where = new Where<ActAljoinFormDataDraft>();
        where.orderBy("create_time", false);
        Page<ActAljoinFormDataDraft> page
            = selectPage(new Page<ActAljoinFormDataDraft>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinFormDataDraft obj) throws Exception {
        mapper.copyObject(obj);
    }

    private void deleteOrgnlTaskAuth(Task userTask) {
        // 删除原来候选人信息(含分组和候选人)
        List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(userTask.getId());
        for (IdentityLink identityLink : identityLinkList) {
            if ("candidate".equals(identityLink.getType())) {
                if (StringUtils.isNotEmpty(identityLink.getGroupId())) {
                    taskService.deleteGroupIdentityLink(userTask.getId(), identityLink.getGroupId(), "candidate");
                } else if (StringUtils.isNotEmpty(identityLink.getUserId())) {
                    taskService.deleteCandidateUser(userTask.getId(), identityLink.getUserId());
                }
            }
        }
    }

    @Override
    @Transactional
    public Map<String, Object> doCreateWork(ActAljoinFormDataRun entity, Map<String, String> paramMap,
        Map<String, String> param, Long userId, String fullName) throws Exception {
        // 获取流程
        ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(entity.getBpmnId());
        // 获取运行时表单
        ActAljoinFormRun runForm = actAljoinFormRunService.selectById(entity.getFormId());
        // 任务ID
        String activityId = param.get("activityId");
        // 是否自由流程(用来标记自由流程，当isTask=false时是自由流程)
        String isTask = param.get("isTask");
        // 下个节点
        String nextNode = param.get("nextNode");
        // 用户ID
        // String userIds = param.get("userId");
        // 任务权限数据
        String taskAuth = param.get("taskAuth");

        Set<Long> currentHandleUserIdSetfterCompleted = new HashSet<Long>();
        Task task = taskService.createTaskQuery().taskId(activityId).singleResult();

        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("nextNode", nextNode);
        // 如果有触发委托，当前的办理用户会被设置为空，变成候选状态，所有在进行任务进入下一步之前，多进行一次签收操作
        // 还需要先判断一下任务是否存在,同一个用户同时登陆同时操作的情况下会有问题
        if (task == null) {
            throw new Exception("任务已被处理");
        }

        taskService.setAssignee(activityId, userId.toString());
        ProcessInstance instance
            = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();

        // 获取任务的授权数据
        Where<ActAljoinTaskAssignee> taskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
        taskAssigneeWhere.eq("bpmn_id", bpmnRun.getOrgnlId());
        taskAssigneeWhere.eq("task_id", task.getTaskDefinitionKey());
        taskAssigneeWhere.eq("version", bpmnRun.getTaskAssigneeVersion());
        taskAssigneeWhere.setSqlSelect("comment_widget_ids,operate_auth_ids");
        ActAljoinTaskAssignee actAljoinTaskAssignee = actAljoinTaskAssigneeService.selectOne(taskAssigneeWhere);

        // 保存评论数据
        String topButtonComment = param.get("topButtonComment");
        String thisTaskUserComment = param.get("thisTaskUserComment");
        Comment topCommentObj = null;
        Comment thisTaskUserCommentObj = null;
        if (StringUtils.isNotEmpty(topButtonComment)) {
            Authentication.setAuthenticatedUserId(userId.toString());
            topCommentObj = taskService.addComment(task.getId(), instance.getId(), topButtonComment);
            topButtonComment = topButtonComment + "(" + fullName + DateUtil.datetime2str(topCommentObj.getTime()) + ")";
        }

        // 设置评论用户
        Authentication.setAuthenticatedUserId(userId.toString());
        if (StringUtils.isEmpty(thisTaskUserComment)) {
            thisTaskUserComment = "";
            // 构造日志数据
            retMap.put("commont", thisTaskUserComment);
        }
        thisTaskUserCommentObj = taskService.addComment(task.getId(), instance.getId(), thisTaskUserComment);
        thisTaskUserComment = thisTaskUserComment + "(" + fullName + " "
            + DateUtil.datetime2str(thisTaskUserCommentObj.getTime()) + ")";

        // 一次获取获取最新的评论控件内容
        Map<String, String> commentKeyValueMap = new HashMap<String, String>();
        Where<ActAljoinFormDataRun> runDataWhere = new Where<ActAljoinFormDataRun>();
        runDataWhere.eq("proc_inst_id", task.getProcessInstanceId());
        runDataWhere.in("form_widget_id", actAljoinTaskAssignee.getCommentWidgetIds().split(","));
        List<ActAljoinFormDataRun> actAljoinFormDataRunList = actAljoinFormDataRunService.selectList(runDataWhere);
        for (ActAljoinFormDataRun actAljoinFormDataRun : actAljoinFormDataRunList) {
            commentKeyValueMap.put(actAljoinFormDataRun.getFormWidgetId(), actAljoinFormDataRun.getFormWidgetValue());
        }

        // 把评论数据保存到对应的评论控件中
        for (Map.Entry<String, String> map : paramMap.entrySet()) {
            String formWidgetId = map.getKey();
            String formWidgetVal = map.getValue();
            if ((StringUtils.isNotEmpty(actAljoinTaskAssignee.getCommentWidgetIds()))
                && (actAljoinTaskAssignee.getCommentWidgetIds().indexOf(formWidgetId) != -1)
                && (StringUtils.isNotEmpty(thisTaskUserComment))) {
                // 获取最新的评论控件内容
                if (StringUtils.isNotEmpty(commentKeyValueMap.get(formWidgetId))) {
                    formWidgetVal = commentKeyValueMap.get(formWidgetId);
                }
                if (StringUtils.isNotEmpty(formWidgetVal)) {
                    if (StringUtils.isNotEmpty(topButtonComment)) {
                        // 有填写顶部意见
                        if (formWidgetVal.lastIndexOf("\n") != -1) {
                            formWidgetVal = formWidgetVal.substring(0, formWidgetVal.lastIndexOf("\n"));
                            formWidgetVal = formWidgetVal + "\n" + topButtonComment;
                        } else {
                            formWidgetVal = topButtonComment;
                        }
                    }
                    paramMap.put(formWidgetId, formWidgetVal + "\n" + thisTaskUserComment);
                } else {
                    paramMap.put(formWidgetId, thisTaskUserComment);
                }
            }
        }
        // 重新构造taskAuth
        Map<String, List<String>> taskKeyUserMap = new HashMap<String, List<String>>();
        if (StringUtils.isNotEmpty(taskAuth)) {
            String[] taskAuthArr = taskAuth.split(";");
            for (int i = 0; i < taskAuthArr.length; i++) {
                String taskKey = taskAuthArr[i].split(",")[0];
                String tuserIds = taskAuthArr[i].split(",")[1];
                String[] userIdsArr = tuserIds.split("#");
                taskKeyUserMap.put(taskKey, Arrays.asList(userIdsArr));
            }
        }
        retMap.put("taskKeyUserMap", taskKeyUserMap);
        // 获取下个节点的el表达式，然后根据el表达式来设置流程变量
        List<String> elList = activitiService.getToNextCondition(activityId);
        if (elList.size() > 0) {
            for (String el : elList) {
                if (el.startsWith("${targetTask_")) {
                    // 可以选择下级节点的非自由流程
                    // 设置流程变量(决定流程的走向)
                    Map<String, Object> variables = new HashMap<String, Object>();
                    variables.put("targetTask_", nextNode);
                    runtimeService.setVariables(task.getProcessInstanceId(), variables);
                    break;
                } else {
                    // 不可以选择下级节点的非自由流程已经在提交的时候设置了环境变量
                }
            }
        }
        // 判断当前节点是否多实例节点
        MultiInstanceLoopCharacteristics currentMulti
            = activitiService.getMultiInstance(task.getProcessInstanceId(), task.getTaskDefinitionKey());
        // 判断下个节点是否多实例（会签）节点
        MultiInstanceLoopCharacteristics nextMulti
            = activitiService.getMultiInstance(task.getProcessInstanceId(), nextNode);
        // 下个节点有可能含有多个节点（下个节点是并行分支的情况）
        /*if(taskKeyUserMap.size() > 1){
        
        }*/
        // 标志本节点是否继续循环节点，如果是，则无需更新查询表达当前办理人
        String isCycleContinue = "0";

        // 查询加签信息
        Map<String, Object> signInfoMap = actAljoinTaskSignInfoService.getLastSignTaskIdList(task.getId(),
            task.getTaskDefinitionKey(), task.getProcessInstanceId(), task.getAssignee());
        List<String> ruTaskList = new ArrayList<String>();
        ActAljoinTaskSignInfo actAljoinTaskSignInfo = null;
        if (signInfoMap.size() > 0) {
            ruTaskList = (List<String>)signInfoMap.get("signRuTaskIdList");
            actAljoinTaskSignInfo = (ActAljoinTaskSignInfo)signInfoMap.get("signInfo");
        }
        //加签完成状态
        Integer finshType = (Integer)signInfoMap.get("finshType");

       /* if (StringUtils.isEmpty(nextNode) && signedTaskIdList.size() == 1) {
            throw new Exception(MsgConstant.MULTI_PROCESSING);
        }*/
        // 如果是自由流程跳转
        if (isTask.equals("false")) {
            if (currentMulti == null) {
                // 如果当前节点是普通实例节点
                if (nextMulti != null) {
                    // 下个节点是多实例节点
                    // 流程集合变量${ASSIGNEE_B_KGCYWJQSRY}
                    String collectionValue = nextMulti.getInputDataItem();
                    if (StringUtils.isEmpty(collectionValue)) {
                        throw new Exception("多实例(会签)流程集合变量不能为空");
                    }
                    collectionValue
                        = collectionValue.substring(collectionValue.indexOf("{") + 1, collectionValue.indexOf("}"));
                    // 构造流程变量
                    List<Long> assigneeList = new ArrayList<Long>(); // 分配任务的人员
                    List<String> uidList = taskKeyUserMap.get(nextNode);
                    for (String uid : uidList) {
                        assigneeList.add(Long.parseLong(uid));
                    }
                    Map<String, Object> vars = new HashMap<String, Object>(); // 参数
                    // 给集合流程变量设值
                    vars.put(collectionValue, assigneeList);
                    runtimeService.setVariables(task.getExecutionId(), vars);
                    retMap.put("userRankIdList", assigneeList);
                }

                if(null != actAljoinTaskSignInfo){
                    activitiService.jump2Task4AddSign(task,nextNode);
                }else{
                    activitiService.jump2Task2(nextNode, task.getProcessInstanceId());
                }

                if (!nextNode.startsWith("EndEvent_")) {
                    if (nextMulti == null) {
                        // 普通节点
                        // 不是跳转到结束节点，才进行下面的操作
                        Task userTask = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId())
                            .taskDefinitionKey(nextNode).singleResult();
                        // 解签收
                        taskService.unclaim(userTask.getId());
                        // 删除原来候选人信息(含分组和候选人)
                        deleteOrgnlTaskAuth(userTask);
                        List<String> userIdList = taskKeyUserMap.get(userTask.getTaskDefinitionKey());
                        // 重新添加候选人和分组
                        if (userIdList.size() >= 1) {
                            // 如果多个候选人，则添加候选人
                            for (String assignee : userIdList) {
                                taskService.addUserIdentityLink(userTask.getId(), assignee, "candidate");
                                // currentHandleUserIdSetfterCompleted.add(Long.parseLong(assignee));
                            }
                        } else {
                            throw new Exception("没有设置候选人");
                        }
                        retMap.put("userTask", userTask);
                    } else {
                        // 多实例节点
                        // 不是跳转到结束节点，才进行下面的操作
                        List<Task> userTaskList = taskService.createTaskQuery()
                            .processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(nextNode).list();
                        List<String> userIdList = taskKeyUserMap.get(nextNode);
                        if (userIdList == null || userIdList.size() == 0) {
                            throw new Exception("没有设置候选人");
                        }
                        // 如果是并行，userTaskList会有多个，如果是串行userTaskList只有一个，随意这里以userTaskList遍历为准
                        for (int i = 0;
                            i < (userTaskList.size() < userIdList.size() ? userTaskList.size() : userIdList.size());
                            i++) {
                            // 解签收
                            taskService.unclaim(userTaskList.get(i).getId());
                            // 删除原来候选人信息(含分组和候选人)
                            deleteOrgnlTaskAuth(userTaskList.get(i));
                            // 重新添加候选人和分组
                            taskService.addUserIdentityLink(userTaskList.get(i).getId(), userIdList.get(i),
                                "candidate");
                            // currentHandleUserIdSetfterCompleted.add(Long.parseLong(userIdList.get(i)));
                        }
                        retMap.put("taskList", userTaskList);
                    }
                }
            } else {
                // 如果当前节点是多实例节点
                // 查询出当前执行的数量，检查是否将要完成整个节点进入下一个节点
                // nrOfInstances--总数
                // nrOfCompletedInstances--已完成数
                // nrOfActiveInstances--正在执行数据(没完成数)
                // loopCounter--当前循环索引
                int nrOfInstances
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfInstances")).intValue();
                int nrOfCompletedInstances
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfCompletedInstances")).intValue();
                int nrOfActiveInstances
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfActiveInstances")).intValue();
                int loopCounter
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "loopCounter")).intValue();
                Map<String, String> conditionParam = new HashMap<String, String>();
                // 模拟本任务执行完后的参数变化，从而预知流程在本任务执行完后的去向情况，是否达到了往下个节点的条件
                conditionParam.put("nrOfInstances", String.valueOf(nrOfInstances));
                conditionParam.put("nrOfCompletedInstances", String.valueOf(nrOfCompletedInstances + 1));
                if (!currentMulti.isSequential()) {
                    // 并行（执行后会减少）
                    conditionParam.put("nrOfActiveInstances", String.valueOf(nrOfActiveInstances - 1));
                } else {
                    // 串行（总是1）
                    conditionParam.put("nrOfActiveInstances", String.valueOf(nrOfActiveInstances));
                }
                conditionParam.put("loopCounter", String.valueOf(loopCounter + 1));
                // 获取任务完成表达式
                String completionCondition = currentMulti.getCompletionCondition();

                boolean isPass = ActUtil.isCondition(conditionParam, completionCondition);
                if (null != actAljoinTaskSignInfo) {
                    if (ruTaskList.size() > 1) {
                        isPass = false;
                    }
                }
                retMap.put("isPrePass", isPass);
                if (isPass) {
                    if(null != actAljoinTaskSignInfo && actAljoinTaskSignInfo.getIsBackOwner() == 1 && ruTaskList.size() == 1  && finshType != 3){
                        List<Long> uidList = new ArrayList<>();
                        uidList.add(userId);

                        Map<Long,String> userMap = new HashMap<Long,String>();
                        userMap.put(userId,fullName);

                        actTaskAddSignService.returnAssignee(task,userId.toString(),bpmnRun,userMap,uidList,3);
                    }else {
                        // 满足继续往下走的条件，这时候需要提供下一个节点，以及下个节点的办理人
                        if (StringUtils.isEmpty(nextNode)) {
                            throw new Exception(MsgConstant.NEXT_NODE_NOT_NULL);
                        }

                        if (nextMulti != null) {
                            // 下个节点是多实例节点
                            // 流程集合变量${ASSIGNEE_B_KGCYWJQSRY}
                            String collectionValue = nextMulti.getInputDataItem();
                            if (StringUtils.isEmpty(collectionValue)) {
                                throw new Exception("多实例(会签)流程集合变量不能为空");
                            }
                            collectionValue = collectionValue.substring(collectionValue.indexOf("{") + 1,
                                collectionValue.indexOf("}"));
                            List<String> uidList = taskKeyUserMap.get(nextNode);
                            if (uidList == null || uidList.size() == 0) {
                                throw new Exception("多实例(会签)节点必须设置处理人");
                            }
                            // 构造流程变量
                            List<Long> assigneeList = new ArrayList<Long>(); // 分配任务的人员
                            for (String uid : uidList) {
                                assigneeList.add(Long.parseLong(uid));
                            }
                            Map<String, Object> vars = new HashMap<String, Object>(); // 参数
                            // 给集合流程变量设值
                            vars.put(collectionValue, assigneeList);
                            runtimeService.setVariables(task.getExecutionId(), vars);
                            retMap.put("userRankIdList", assigneeList);
                        }

                        if(null != actAljoinTaskSignInfo){
                            activitiService.jump2Task4AddSign(task,nextNode);
                        }else{
                            activitiService.jump2Task2(nextNode, task.getProcessInstanceId());
                        }

                        if (!nextNode.startsWith("EndEvent_")) {
                            if (nextMulti == null) {
                                // 普通节点
                                // 不是跳转到结束节点，才进行下面的操作
                                Task userTask
                                    = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId())
                                        .taskDefinitionKey(nextNode).singleResult();
                                // 解签收
                                taskService.unclaim(userTask.getId());
                                // 删除原来候选人信息(含分组和候选人)
                                deleteOrgnlTaskAuth(userTask);
                                List<String> userIdList = taskKeyUserMap.get(userTask.getTaskDefinitionKey());
                                // 重新添加候选人和分组
                                if (userIdList.size() >= 1) {
                                    // 如果多个候选人，则添加候选人
                                    for (String assignee : userIdList) {
                                        taskService.addUserIdentityLink(userTask.getId(), assignee, "candidate");
                                        // currentHandleUserIdSetfterCompleted.add(Long.parseLong(assignee));
                                    }
                                } else {
                                    throw new Exception("没有设置候选人");
                                }
                                retMap.put("userTask", userTask);
                            } else {
                                // 多实例节点
                                // 不是跳转到结束节点，才进行下面的操作
                                List<Task> userTaskList = taskService.createTaskQuery()
                                    .processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(nextNode).list();
                                List<String> userIdList = taskKeyUserMap.get(nextNode);
                                if (userIdList == null || userIdList.size() == 0) {
                                    throw new Exception("没有设置候选人");
                                }

                                // 并行多实例userTaskList会有多个，并行多实例会有1一个
                                for (int i = 0; i < (userTaskList.size() < userIdList.size() ? userTaskList.size()
                                    : userIdList.size()); i++) {
                                    // 解签收
                                    taskService.unclaim(userTaskList.get(i).getId());
                                    // 删除原来候选人信息(含分组和候选人)
                                    deleteOrgnlTaskAuth(userTaskList.get(i));
                                    // 重新添加候选人和分组
                                    taskService.addUserIdentityLink(userTaskList.get(i).getId(), userIdList.get(i),
                                        "candidate");
                                    // currentHandleUserIdSetfterCompleted.add(Long.parseLong(userIdList.get(i)));
                                }
                                retMap.put("taskList", userTaskList);
                            }
                        }
                    }
                } else {
                    isCycleContinue = "1";
                    // 完成后将继续流转
                    taskService.complete(task.getId());
                    if (currentMulti.isSequential()) {
                        // 多实例顺序，完成后默认会被签收，进行节前在设置候选

                        // 获取流程变量名称
                        String collectionValue = currentMulti.getInputDataItem();
                        if (StringUtils.isEmpty(collectionValue)) {
                            throw new Exception("多实例(会签)流程集合变量不能为空");
                        }
                        collectionValue
                            = collectionValue.substring(collectionValue.indexOf("{") + 1, collectionValue.indexOf("}"));
                        
                        List<Task> userTaskList
                            = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId())
                                .taskDefinitionKey(task.getTaskDefinitionKey()).list();
                        for (Task t : userTaskList) {
                            String assignee = t.getAssignee();
                            if (StringUtils.isEmpty(assignee)) {
                                assignee = getNextSequentialUserId(task, collectionValue);
                            }
                            HistoricTaskInstance hisTask
                                = historyService.createHistoricTaskInstanceQuery().taskId(t.getId()).singleResult();
                            if (null != hisTask.getClaimTime() && StringUtils.isNotEmpty(hisTask.getAssignee())) {
                                taskService.unclaim(t.getId());
                                taskService.addUserIdentityLink(t.getId(), assignee, "candidate");
                                // currentHandleUserIdSetfterCompleted.add(Long.parseLong(assignee));
                                retMap.put("assignee", assignee);
                            }
                        }
                        retMap.put("taskList", userTaskList);
                    }
                }
            }
        } else {
            // ------------------------------------------------ 非自由流程完成任务
            if (currentMulti == null) {
                // 如果当前节点是普通实例节点
                if (nextMulti != null) {
                    // 下个节点是多实例节点
                    // 流程集合变量${ASSIGNEE_B_KGCYWJQSRY}
                    String collectionValue = nextMulti.getInputDataItem();
                    if (StringUtils.isEmpty(collectionValue)) {
                        throw new Exception("多实例(会签)流程集合变量不能为空");
                    }
                    collectionValue
                        = collectionValue.substring(collectionValue.indexOf("{") + 1, collectionValue.indexOf("}"));
                    // 构造流程变量
                    List<Long> assigneeList = new ArrayList<Long>(); // 分配任务的人员
                    List<String> uidList = taskKeyUserMap.get(nextNode);
                    for (String uid : uidList) {
                        assigneeList.add(Long.parseLong(uid));
                    }
                    Map<String, Object> vars = new HashMap<String, Object>(); // 参数
                    // 给集合流程变量设值
                    vars.put(collectionValue, assigneeList);
                    runtimeService.setVariables(task.getExecutionId(), vars);

                    // 完成任务
                    activitiService.completeTask(null, activityId);
                    // 完成任务后设置会签办理人
                    List<Task> userTaskList = taskService.createTaskQuery()
                        .processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(nextNode).list();
                    List<String> userIdList = taskKeyUserMap.get(nextNode);
                    if (userIdList == null || userIdList.size() == 0) {
                        throw new Exception("没有设置候选人");
                    }
                    // 如果是并行，userTaskList会有多个，如果是串行userTaskList只有一个，随意这里以userTaskList遍历为准
                    for (int i = 0;
                        i < (userTaskList.size() < userIdList.size() ? userTaskList.size() : userIdList.size()); i++) {
                        // 解签收
                        taskService.unclaim(userTaskList.get(i).getId());
                        // 删除原来候选人信息(含分组和候选人)
                        deleteOrgnlTaskAuth(userTaskList.get(i));
                        // 重新添加候选人和分组
                        taskService.addUserIdentityLink(userTaskList.get(i).getId(), userIdList.get(i), "candidate");
                        // currentHandleUserIdSetfterCompleted.add(Long.parseLong(userIdList.get(i)));
                    }
                    retMap.put("userRankIdList", assigneeList);
                    retMap.put("taskList", userTaskList);
                } else {
                    // 下个节点是普通节点(原来没有多实例节点情况下的处理方法)
                    activitiService.completeTask(null, activityId);
                    // 完成任务后执行流ID会改变，所以这里通过流程实例ID来获取当前的任务节点
                    List<Task> taskList
                        = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();

                    for (Task assTask : taskList) {
                        if (taskKeyUserMap.get(assTask.getTaskDefinitionKey()) != null) {
                            // 解签收
                            taskService.unclaim(assTask.getId());
                            // 删除任务节点的任务权限数据
                            deleteOrgnlTaskAuth(assTask);
                            // 再新增权限数据
                            List<String> userIdList = taskKeyUserMap.get(assTask.getTaskDefinitionKey());
                            if (userIdList.size() >= 1) {
                                // 多个候选用户
                                for (String assignee : userIdList) {
                                    taskService.addUserIdentityLink(assTask.getId(), assignee, "candidate");
                                    // currentHandleUserIdSetfterCompleted.add(Long.parseLong(assignee));
                                }
                            } else {
                                throw new Exception("没有设置候选人");
                            }
                        }
                    }
                    retMap.put("taskList", taskList);
                }
            } else {
                // 如果当前节点是多实例节点
                // 查询出当前执行的数量，检查是否将要完成整个节点进入下一个节点
                // nrOfInstances--总数
                // nrOfCompletedInstances--已完成数
                // nrOfActiveInstances--正在执行数据(没完成数)
                // loopCounter--当前循环索引
                int nrOfInstances
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfInstances")).intValue();
                int nrOfCompletedInstances
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfCompletedInstances")).intValue();
                int nrOfActiveInstances
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfActiveInstances")).intValue();
                int loopCounter
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "loopCounter")).intValue();
                Map<String, String> conditionParam = new HashMap<String, String>();
                // 模拟本任务执行完后的参数变化，从而预知流程在本任务执行完后的去向情况，是否达到了往下个节点的条件
                conditionParam.put("nrOfInstances", String.valueOf(nrOfInstances));
                conditionParam.put("nrOfCompletedInstances", String.valueOf(nrOfCompletedInstances + 1));
                if (!currentMulti.isSequential()) {
                    // 并行（执行后会减少）
                    conditionParam.put("nrOfActiveInstances", String.valueOf(nrOfActiveInstances - 1));
                } else {
                    // 串行（总是1）
                    conditionParam.put("nrOfActiveInstances", String.valueOf(nrOfActiveInstances));
                }
                conditionParam.put("loopCounter", String.valueOf(loopCounter + 1));
                // 获取任务完成表达式
                String completionCondition = currentMulti.getCompletionCondition();

                boolean isPass = ActUtil.isCondition(conditionParam, completionCondition);
                if (null != actAljoinTaskSignInfo) {
                    if (ruTaskList.size() > 1) {
                        isPass = false;
                    }
                }
                retMap.put("isPrePass", isPass);
                if (isPass) {
                    if(null != actAljoinTaskSignInfo && actAljoinTaskSignInfo.getIsBackOwner() == 1 && ruTaskList.size() == 1  && finshType != 3){
                        List<Long> uidList = new ArrayList<>();
                        uidList.add(userId);

                        Map<Long,String> userMap = new HashMap<Long,String>();
                        userMap.put(userId,fullName);

                        actTaskAddSignService.returnAssignee(task,userId.toString(),bpmnRun,userMap,uidList,3);
                    } else {
                        // 满足继续往下走的条件，这时候需要提供下一个节点，以及下个节点的办理人
                        if (StringUtils.isEmpty(nextNode)) {
                            throw new Exception(MsgConstant.NEXT_NODE_NOT_NULL);
                        }
                        if (nextMulti != null) {
                            // 下个节点是多实例节点
                            // 流程集合变量${ASSIGNEE_B_KGCYWJQSRY}
                            String collectionValue = nextMulti.getInputDataItem();
                            if (StringUtils.isEmpty(collectionValue)) {
                                throw new Exception("多实例(会签)流程集合变量不能为空");
                            }
                            collectionValue = collectionValue.substring(collectionValue.indexOf("{") + 1,
                                collectionValue.indexOf("}"));
                            List<String> uidList = taskKeyUserMap.get(nextNode);
                            if (uidList == null || uidList.size() == 0) {
                                throw new Exception("多实例(会签)节点必须设置处理人");
                            }
                            // 构造流程变量
                            List<Long> assigneeList = new ArrayList<Long>(); // 分配任务的人员
                            for (String uid : uidList) {
                                assigneeList.add(Long.parseLong(uid));
                            }
                            Map<String, Object> vars = new HashMap<String, Object>(); // 参数
                            // 给集合流程变量设值
                            vars.put(collectionValue, assigneeList);
                            runtimeService.setVariables(task.getExecutionId(), vars);
                            activitiService.completeTask(null, activityId);

                            // 完成任务后分配签收
                            List<Task> userTaskList = taskService.createTaskQuery()
                                .processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(nextNode).list();
                            List<String> userIdList = taskKeyUserMap.get(nextNode);
                            if (userIdList == null || userIdList.size() == 0) {
                                throw new Exception("没有设置候选人");
                            }

                            // 并行多实例userTaskList会有多个，并行多实例会有1个
                            for (int i = 0;
                                i < (userTaskList.size() < userIdList.size() ? userTaskList.size() : userIdList.size());
                                i++) {
                                // 解签收
                                taskService.unclaim(userTaskList.get(i).getId());
                                // 删除原来候选人信息(含分组和候选人)
                                deleteOrgnlTaskAuth(userTaskList.get(i));
                                // 重新添加候选人和分组
                                taskService.addUserIdentityLink(userTaskList.get(i).getId(), userIdList.get(i),
                                    "candidate");
                                // currentHandleUserIdSetfterCompleted.add(Long.parseLong(userIdList.get(i)));
                            }
                            retMap.put("userRankIdList", assigneeList);
                            retMap.put("taskList", userTaskList);
                        } else {
                            // 下个节点是普通节点
                            activitiService.completeTask(null, activityId);
                            // 完成任务后执行流ID会改变，所以这里通过流程实例ID来获取当前的任务节点
                            List<Task> taskList
                                = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();

                            for (Task assTask : taskList) {
                                if (taskKeyUserMap.get(assTask.getTaskDefinitionKey()) != null) {
                                    // 解签收
                                    taskService.unclaim(assTask.getId());
                                    // 删除任务节点的任务权限数据
                                    deleteOrgnlTaskAuth(assTask);
                                    // 再新增权限数据
                                    List<String> userIdList = taskKeyUserMap.get(assTask.getTaskDefinitionKey());
                                    if (userIdList.size() >= 1) {
                                        // 多个候选用户
                                        for (String assignee : userIdList) {
                                            taskService.addUserIdentityLink(assTask.getId(), assignee, "candidate");
                                            // currentHandleUserIdSetfterCompleted.add(Long.parseLong(assignee));
                                        }
                                    } else {
                                        throw new Exception("没有设置候选人");
                                    }
                                }
                            }
                            retMap.put("taskList", taskList);
                        }
                    }

                } else {
                    isCycleContinue = "1";
                    if(null != actAljoinTaskSignInfo){
                        Where<ActAljoinDelegateInfo> delegateInfoWhere = new Where<>();
                        delegateInfoWhere.setSqlSelect("id");
                        delegateInfoWhere.eq("task_id",task.getId());
                        delegateInfoWhere.eq("assignee_user_id", userId);
                        Integer count = actAljoinDelegateInfoService.selectCount(delegateInfoWhere);
                        if(0 < count || ruTaskList.size() > 1){
                            actHiTaskinstService.updateHisTask(task.getId(),String.valueOf(userId));
                        }
                    }else{
                        // 当前节点不满足条件，继续流转
                        taskService.complete(task.getId());
                    }
                    if (currentMulti.isSequential()) {
                        // 多实例顺序，完成后默认会被签收，进行节前在设置候选
                        List<Task> userTaskList
                            = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId())
                                .taskDefinitionKey(task.getTaskDefinitionKey()).list();
                        // 获取流程变量名称
                        String collectionValue = currentMulti.getInputDataItem();
                        if (StringUtils.isEmpty(collectionValue)) {
                            throw new Exception("多实例(会签)流程集合变量不能为空");
                        }
                        collectionValue
                            = collectionValue.substring(collectionValue.indexOf("{") + 1, collectionValue.indexOf("}"));
                        retMap.put("taskList", userTaskList);
                        for (Task t : userTaskList) {
                            String assignee = t.getAssignee();
                            if (StringUtils.isEmpty(assignee)) {
                                assignee = getNextSequentialUserId(task, collectionValue);
                            }
                            if (StringUtils.isNotEmpty(assignee)) {
                                taskService.unclaim(t.getId());
                                taskService.addUserIdentityLink(t.getId(), assignee, "candidate");
                                // currentHandleUserIdSetfterCompleted.add(Long.parseLong(assignee));
                            }
                        }
                        retMap.put("taskList", userTaskList);
                    }
                }
            }
        }

        // 更新表单数据
        updateFormData(paramMap, instance, entity, isTask, nextNode, task.getTaskDefinitionKey());

        // 更新 加签信息完成状态
        updateTaskSignData(activityId);

        retMap.put("instance", instance);
        retMap.put("bpmn", bpmnRun);
        retMap.put("runForm", runForm);
        currentHandleUserIdSetfterCompleted = getCandidateUserIdSet(instance.getProcessInstanceId());
        retMap.put("currentHandleUserIdSetfterCompleted", currentHandleUserIdSetfterCompleted);
        // 标记下一步是否继续流转，如果继续流转，不用跟新查询表的当前办理人
        retMap.put("isCycleContinue", isCycleContinue);
        // 以下信息用于构造日志
        retMap.put("preTask", task);
        retMap.put("isPreMult", currentMulti);
        retMap.put("isCurrentMult", nextMulti);
        retMap.put("taskKeyUserMap", taskKeyUserMap);
        return retMap;
    }

    /**
     * 
     * 获取流程实例的当前办理人
     *
     * @return：Set<Long>
     *
     * @author：zhongjy
     *
     * @date：2018年1月15日 上午10:05:34
     */
    private Set<Long> getCandidateUserIdSet(String instanceId) {
        // 返回结果集
        Set<Long> userIdSet = new HashSet<Long>();
        // 获取流程实例的所有任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(instanceId).list();

        for (Task task : taskList) {
            List<IdentityLink> linkList = taskService.getIdentityLinksForTask(task.getId());
            for (IdentityLink identityLink : linkList) {
                if ("candidate".equals(identityLink.getType()) && StringUtils.isNotEmpty(identityLink.getUserId())) {
                    userIdSet.add(Long.parseLong(identityLink.getUserId()));
                }
            }
        }
        return userIdSet;
    }

    /**
     * 
     * 随机获取多实例串行节点的下一个办理人ID
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年12月28日 下午3:51:04
     */
    @SuppressWarnings("unchecked")
    public String getNextSequentialUserId(Task task, String valueName) {
        String retUserId = null;
        // 获取流程变量（所有办理的用户）
        List<Long> allUserIdList = (List<Long>)runtimeService.getVariable(task.getExecutionId(), valueName);
        // 已经办理过的用户id
        List<Long> hasDoUserIdList = new ArrayList<Long>();
        // 获取已经办理过本串行节点用户的ID
        List<HistoricTaskInstance> hisTaskList
            = historyService.createHistoricTaskInstanceQuery().executionId(task.getExecutionId())
                .taskDefinitionKey(task.getTaskDefinitionKey()).processInstanceId(task.getProcessInstanceId()).list();
        for (HistoricTaskInstance his : hisTaskList) {
            // 从所有用户中删除已经办理过的用户ID
            if (StringUtils.isNotEmpty(his.getAssignee()) && null != his.getClaimTime()) {
                hasDoUserIdList.add(Long.parseLong(his.getAssignee()));
            }
        }
        for (Long s : allUserIdList) {
            if (!hasDoUserIdList.contains(s)) {
                retUserId = String.valueOf(s);
                break;
            }
        }
        return retUserId;
    }

    @Override
    public void updateFormData(Map<String, String> paramMap, ProcessInstance instance, ActAljoinFormDataRun entity,
        String isTask, String nextNode, String currentNode) throws Exception {
        List<ActAljoinFormDataRun> runList = new ArrayList<ActAljoinFormDataRun>();
        Map<String, String> dataMap = new HashMap<String, String>();
        Map<String, String> readMap = new HashMap<String, String>();

        // 把数据源和是否已读分别取出来放进map，再把原有的移出
        Iterator<Map.Entry<String, String>> it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> map = it.next();
            String formWidgetId = map.getKey();
            String formWidgetVal = map.getValue();
            if (formWidgetId.startsWith("sourceData_")) {
                dataMap.put(formWidgetId, formWidgetVal);
                it.remove();
                paramMap.remove(formWidgetId);
            }
            if (formWidgetId.startsWith("dataReadIs_")) {
                readMap.put(formWidgetId, formWidgetVal);
                it.remove();
                paramMap.remove(formWidgetId);
            }
        }

        // 保存运行时数据
        for (Map.Entry<String, String> map : paramMap.entrySet()) {
            ActAljoinFormDataRun run = new ActAljoinFormDataRun();
            String formWidgetId = map.getKey();
            String formWidgetVal = map.getValue();

            Where<ActAljoinFormDataRun> formWhere = new Where<ActAljoinFormDataRun>();
            formWhere.eq("proc_inst_id", instance.getProcessInstanceId());
            formWhere.eq("form_widget_id", formWidgetId);
            ActAljoinFormDataRun formDataRun = actAljoinFormDataRunService.selectOne(formWhere);

            if (formDataRun == null) {
                // 运行时
                run.setBpmnId(entity.getBpmnId());
                run.setOperateUserId(entity.getOperateUserId());
                if (StringUtils.isNotEmpty(isTask) && isTask.equals("false")) {
                    run.setProcTaskId(nextNode);
                } else {
                    if (StringUtils.isNotEmpty(instance.getActivityId())) {
                        run.setProcTaskId(instance.getActivityId());
                    } else {
                        run.setProcTaskId(currentNode);
                    }
                }
                run.setProcInstId(instance.getProcessInstanceId());
                run.setProcDefId(instance.getProcessDefinitionId());
                run.setFormId(entity.getFormId());
                // 根据表单控件ID和表单ID获取运行时的控件完整信息
                Where<ActAljoinFormWidgetRun> widgetWhere = new Where<ActAljoinFormWidgetRun>();
                ActAljoinFormRun formRun = actAljoinFormRunService.selectById(entity.getFormId());
                widgetWhere.eq("form_id", formRun.getOrgnlId());
                widgetWhere.eq("widget_id", formWidgetId);
                ActAljoinFormWidgetRun runFormWidget = actAljoinFormWidgetRunService.selectOne(widgetWhere);
                // 数据源
                if (dataMap != null) {
                    for (Map.Entry<String, String> data : dataMap.entrySet()) {
                        String formWidgetId1 = data.getKey();
                        String formWidgetVal1 = data.getValue();
                        if (formWidgetId1.contains(formWidgetId)) {
                            run.setDataResource(formWidgetVal1);
                        }
                    }
                }
                // 是否读取了数据源
                if (readMap != null) {
                    for (Map.Entry<String, String> read : readMap.entrySet()) {
                        String formWidgetId1 = read.getKey();
                        String formWidgetVal1 = read.getValue();
                        if (formWidgetId1.contains(formWidgetId)) {
                            run.setIsRead(Integer.valueOf(formWidgetVal1));
                        }
                    }
                }
                // 运行时
                run.setWidgetId(runFormWidget.getId());
                run.setFormWidgetId(runFormWidget.getWidgetId());
                run.setFormWidgetName(runFormWidget.getWidgetName());
                run.setFormWidgetValue(formWidgetVal);
                runList.add(run);
            } else {
                run = actAljoinFormDataRunService.selectById(formDataRun.getId());
                run.setFormWidgetValue(formWidgetVal);
                actAljoinFormDataRunService.updateById(run);
            }
        }

        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(instance.getProcessInstanceId()).finished().singleResult();
        if (historicProcessInstance != null) {
            Where<ActAljoinFormDataRun> where = new Where<ActAljoinFormDataRun>();
            where.eq("proc_inst_id", instance.getProcessInstanceId());
            List<ActAljoinFormDataRun> dataRunList = actAljoinFormDataRunService.selectList(where);

            for (ActAljoinFormDataRun actAljoinFormDataRun : dataRunList) {
                ActAljoinFormDataHis actAljoinFormDataHis = new ActAljoinFormDataHis();
                actAljoinFormDataHis.setId(actAljoinFormDataRun.getId());
                actAljoinFormDataHis.setCreateTime(actAljoinFormDataRun.getCreateTime());
                actAljoinFormDataHis.setLastUpdateTime(actAljoinFormDataRun.getLastUpdateTime());
                actAljoinFormDataHis.setVersion(actAljoinFormDataRun.getVersion());
                actAljoinFormDataHis.setIsDelete(actAljoinFormDataRun.getIsDelete());
                actAljoinFormDataHis.setLastUpdateUserId(actAljoinFormDataRun.getLastUpdateUserId());
                actAljoinFormDataHis.setLastUpdateUserName(actAljoinFormDataRun.getLastUpdateUserName());
                actAljoinFormDataHis.setCreateUserId(actAljoinFormDataRun.getCreateUserId());
                actAljoinFormDataHis.setCreateUserName(actAljoinFormDataRun.getCreateUserName());
                actAljoinFormDataHis.setBpmnId(actAljoinFormDataRun.getBpmnId());
                actAljoinFormDataHis.setOperateUserId(actAljoinFormDataRun.getOperateUserId());
                actAljoinFormDataHis.setProcTaskId(actAljoinFormDataRun.getProcTaskId());
                actAljoinFormDataHis.setProcDefId(actAljoinFormDataRun.getProcDefId());
                actAljoinFormDataHis.setProcInstId(actAljoinFormDataRun.getProcInstId());
                actAljoinFormDataHis.setFormId(actAljoinFormDataRun.getFormId());
                actAljoinFormDataHis.setWidgetId(actAljoinFormDataRun.getWidgetId());
                actAljoinFormDataHis.setFormWidgetId(actAljoinFormDataRun.getFormWidgetId());
                actAljoinFormDataHis.setFormWidgetName(actAljoinFormDataRun.getFormWidgetName());
                actAljoinFormDataHis.setFormWidgetValue(actAljoinFormDataRun.getFormWidgetValue());
                if (actAljoinFormDataRun.getDataResource() != null) {
                    actAljoinFormDataHis.setDataResource(actAljoinFormDataRun.getDataResource());
                } else {
                    actAljoinFormDataHis.setDataResource("");
                }
                actAljoinFormDataHis.setIsRead(actAljoinFormDataRun.getIsRead());
                actAljoinFormDataHisService.copyObject(actAljoinFormDataHis);
                actAljoinFormDataRunService.physicsDeleteById(actAljoinFormDataRun.getId());
            }
        }

        Where<ActAljoinFormDataDraft> where = new Where<ActAljoinFormDataDraft>();
        where.eq("operate_user_id", entity.getOperateUserId());
        where.eq("proc_task_id",
            StringUtils.isNotEmpty(instance.getActivityId()) ? instance.getActivityId() : currentNode);
        where.eq("proc_inst_id", instance.getProcessInstanceId());
        List<ActAljoinFormDataDraft> dataDraftList = actAljoinFormDataDraftService.selectList(where);

        for (ActAljoinFormDataDraft actAljoinFormDataDraft : dataDraftList) {
            actAljoinFormDataDraftService.physicsDeleteById(actAljoinFormDataDraft.getId());
        }

        if (runList.size() > 0) {
            actAljoinFormDataRunService.insertBatch(runList);
        }
    }

    @Override
    public void doPass(ActAljoinFormDataRun entity, Map<String, String> paramMap) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void doBack(ActAljoinFormDataRun entity, Map<String, String> paramMap) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public RetMsg doNext(ActAljoinFormDataRun entity, Map<String, String> paramMap) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void closeTask(String activityId) throws Exception {
        Task task = taskService.createTaskQuery().taskId(activityId).singleResult();
        // 删除流程实例
        runtimeService.deleteProcessInstance(task.getProcessInstanceId(), "删除流程实例");

        Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
        queryWhere.eq("process_instance_id", task.getProcessInstanceId());
        actAljoinQueryService.delete(queryWhere);

        Where<ActAljoinQueryHis> queryHisWhere = new Where<ActAljoinQueryHis>();
        queryHisWhere.eq("process_instance_id", task.getProcessInstanceId());
        actAljoinQueryHisService.delete(queryHisWhere);

        Where<ActAljoinFormDataDraft> where = new Where<ActAljoinFormDataDraft>();
        where.eq("proc_inst_id", task.getProcessInstanceId());
        actAljoinFormDataDraftService.delete(where);

        Where<ActAljoinFormDataRun> runWhere = new Where<ActAljoinFormDataRun>();
        runWhere.eq("proc_inst_id", task.getProcessInstanceId());
        actAljoinFormDataRunService.delete(runWhere);
    }

    public String getParentId(String parentId) throws Exception {
        String tpyeID = parentId + ",";
        ActAljoinCategory actcategory = new ActAljoinCategory();
        actcategory.setParentId(Long.valueOf(parentId));
        List<ActAljoinCategory> categoryList = categoryService.getByParentId(actcategory);
        if (categoryList != null && categoryList.size() > 0) {
            for (int i = 0; i < categoryList.size(); i++) {
                ActAljoinCategory tmp = categoryList.get(i);
                tpyeID = tpyeID + this.getParentId(tmp.getId().toString());
            }
        }
        return tpyeID;

    }

    @Override
    public Page<DraftTaskShowVO> listPage(PageBean pageBean, Map<String, Object> obj) throws Exception {
        // TODO Auto-generated method stub
        Where<ActAljoinFormDataDraft> where = new Where<ActAljoinFormDataDraft>();
        where.where("create_user_id={0}", obj.get("UserID"));
        where.eq("is_delete", 0);
        if (obj.get("type") != null && !"".equals(obj.get("type"))) {
            // 获取流程类别下流程ID列表
            // ActAljoinCategory actcategory=new ActAljoinCategory();
            // 获取所有子类ID
            String tpyeID = this.getParentId(obj.get("type").toString());
            tpyeID = tpyeID.substring(0, tpyeID.length() - 1);
            Where<ActAljoinBpmn> bpmnWhere = new Where<ActAljoinBpmn>();
            bpmnWhere.where("is_active={0}", 1);
            bpmnWhere.in("category_id", tpyeID);
            bpmnWhere.setSqlSelect("id");
            List<ActAljoinBpmn> bpmnList = actAljoinBpmnService.selectList(bpmnWhere);
            tpyeID = "";
            for (ActAljoinBpmn aab : bpmnList) {
                tpyeID = tpyeID + aab.getId() + ",";
            }
            if (!"".equals(tpyeID)) {
                tpyeID = tpyeID.substring(0, tpyeID.length() - 1);
            } else {
                tpyeID = "没有数据，查询空白";
            }
            where.in("bpmn_id", tpyeID);
        }
        if (obj.get("typeName") != null && !"".equals(obj.get("typeName"))) {
            // 获取模糊流程名称对应ID列表
            Where<ActAljoinBpmn> wherebpmn = new Where<ActAljoinBpmn>();
            wherebpmn.like("process_name", obj.get("typeName").toString());
            wherebpmn.where("is_active={0}", 1);
            wherebpmn.setSqlSelect("id");
            List<ActAljoinBpmn> bpmnList = actAljoinBpmnService.selectList(wherebpmn);
            String typeName = "";
            // 是否存在模糊查询值，不存在不加入列表查询条件
            if (bpmnList != null && bpmnList.size() > 0) {
                for (int j = 0; j < bpmnList.size(); j++) {
                    typeName += bpmnList.get(j).getId() + ",";
                }
                if (typeName.length() > 0) {
                    typeName = typeName.substring(0, typeName.length() - 1);
                }
                where.in("bpmn_id", typeName);
            } else {
                where.in("bpmn_id", "无效数据生成查询条件不成立");
            }
        }
        if ((obj.get("StatrTime") != null && !"".equals(obj.get("StatrTime")))
            && !(obj.get("EndTime") != null && !"".equals(obj.get("EndTime")))) {
            where.ge("last_update_time", DateUtil.str2dateOrTime((String)obj.get("StatrTime")));
        }
        if ((obj.get("EndTime") != null && !"".equals(obj.get("EndTime")))
            && !(obj.get("StatrTime") != null && !"".equals(obj.get("StatrTime")))) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            c.setTime(format.parse(obj.get("EndTime").toString()));
            c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
            where.le("last_update_time", c.getTime());
        }
        if ((obj.get("EndTime") != null && !"".equals(obj.get("EndTime")))
            && (obj.get("StatrTime") != null && !"".equals(obj.get("StatrTime")))) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            c.setTime(format.parse(obj.get("EndTime").toString()));
            c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
            where.between("last_update_time", DateUtil.str2dateOrTime((String)obj.get("StatrTime")), c.getTime());
        }
        where.groupBy("proc_inst_id");
        where.orderBy("last_update_time", false);
        // where.setSqlSelect("id,last_update_time,proc_task_id,bpmn_id,proc_inst_id");
        Page<ActAljoinFormDataDraft> pageDraft = actAljoinFormDataDraftService
            .selectPage(new Page<ActAljoinFormDataDraft>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        List<ActAljoinFormDataDraft> list = pageDraft.getRecords();
        List<DraftTaskShowVO> pageList = new ArrayList<DraftTaskShowVO>();
        for (int i = 0; i < list.size(); i++) {
            DraftTaskShowVO vo = new DraftTaskShowVO();
            // 根据流程实例ID获取在办流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(list.get(i).getProcInstId()).unfinished().singleResult();
            if (historicProcessInstance != null) {
                // 获取流程定义
                Deployment deployment = repositoryService.createDeploymentQuery()
                    .deploymentId(historicProcessInstance.getDeploymentId()).singleResult();
                ActAljoinCategory actAljoinCategory = categoryService.selectById(deployment.getCategory());
                vo.setFormType(actAljoinCategory.getCategoryName()); // 流程类型
                String str = historicProcessInstance.getBusinessKey();
                vo.setBusinessKey(str);
                String isUrgent = "一般";
                if (!StringUtils.isEmpty(str)) {
                    String[] key = str.split(",");
                    if (key.length >= 3) {
                        if (key[2].equals("2")) {
                            isUrgent = "紧急";
                        }
                        if (key[2].equals("3")) {
                            isUrgent = "加急";
                        }
                    }
                    vo.setTitle(historicProcessInstance.getProcessDefinitionName());// 标题
                }
                vo.setUrgency(isUrgent); // 紧急程度
                vo.setTitle(historicProcessInstance.getProcessDefinitionName());// 流程名字
            }
            HistoricTaskInstance historTask = null;
            if (list.get(i).getProcInstId() != null && !"".equals(list.get(i).getProcInstId())) {
                historTask = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(list.get(i).getProcInstId()).unfinished().singleResult();
                vo.setTaskId(historTask.getId());
                vo.setSignInTime(historTask.getClaimTime());
                vo.setProcessInstanceId(list.get(i).getProcInstId());
            }
            Where<ActAljoinQuery> qWhere = new Where<ActAljoinQuery>();
            qWhere.eq("process_instance_id", list.get(i).getProcInstId());
            ActAljoinQuery aq = actAljoinQueryService.selectOne(qWhere);
            if (aq != null && aq.getProcessTitle() != null) {
                vo.setTitle(aq.getProcessTitle());// 标题
            }
            vo.setBid(list.get(i).getBpmnId().toString());
            vo.setTaskDefKey(list.get(i).getProcTaskId());
            vo.setFounder(obj.get("UserName").toString());// 创建者
            vo.setCreateTime(list.get(i).getLastUpdateTime());
            vo.setDraftId(list.get(i).getId().toString());
            pageList.add(vo);
        }
        Page<DraftTaskShowVO> page = new Page<DraftTaskShowVO>(pageBean.getPageNum(), pageBean.getPageSize());
        page.setRecords(pageList);
        page.setSize(pageDraft.getSize());
        page.setTotal(pageDraft.getTotal());

        return page;

    }

    @Override
    public void deleteDraftById(String id) throws Exception {
        // TODO Auto-generated method stub
        Where<ActAljoinFormDataDraft> draftwhere = new Where<ActAljoinFormDataDraft>();
        draftwhere.where("id={0}", id);
        draftwhere.setSqlSelect("proc_inst_id");
        List<ActAljoinFormDataDraft> inst = actAljoinFormDataDraftService.selectList(draftwhere);
        if (inst != null && inst.size() > 0) {
            String instId = inst.get(0).getProcInstId();
            runtimeService.deleteProcessInstance(instId, "删除流程实例");
            Where<ActAljoinFormDataDraft> where = new Where<ActAljoinFormDataDraft>();
            where.eq("proc_inst_id", inst.get(0).getProcInstId());
            actAljoinFormDataDraftService.delete(where);
            Where<ActAljoinFormDataRun> runWhere = new Where<ActAljoinFormDataRun>();
            runWhere.eq("proc_inst_id", id);
            actAljoinFormDataRunService.delete(runWhere);
            Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
            queryWhere.eq("process_instance_id", instId);
            actAljoinQueryService.delete(queryWhere);

        }
    }

    @Override
    @Transactional
    public void addDraft(ActAljoinFormDataDraft entity, Map<String, String> paramMap, Map<String, String> param,
        Long userId) throws Exception {
        List<ActAljoinFormDataDraft> draftList = new ArrayList<ActAljoinFormDataDraft>();

        String activityId = param.get("activityId");
        Task task = taskService.createTaskQuery().taskId(activityId).singleResult();
        /*
         * ProcessInstance instance = runtimeService.createProcessInstanceQuery()
         * .processInstanceId(task.getProcessInstanceId()).singleResult();
         */

        Where<ActAljoinFormDataDraft> where = new Where<ActAljoinFormDataDraft>();
        where.eq("operate_user_id", entity.getOperateUserId());
        where.eq("proc_task_id", task.getTaskDefinitionKey());
        where.eq("proc_inst_id", task.getProcessInstanceId());
        List<ActAljoinFormDataDraft> list = actAljoinFormDataDraftService.selectList(where);
        for (ActAljoinFormDataDraft actAljoinFormDataDraft : list) {
            actAljoinFormDataDraftService.physicsDeleteById(actAljoinFormDataDraft.getId());
        }

        Map<String, String> dataMap = new HashMap<String, String>();
        Map<String, String> readMap = new HashMap<String, String>();
        // 把数据源和是否已读分别取出来放进map，再把原有的移出
        Iterator<Map.Entry<String, String>> it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> map = it.next();
            String formWidgetId = map.getKey();
            String formWidgetVal = map.getValue();
            if (formWidgetId.startsWith("sourceData_")) {
                dataMap.put(formWidgetId, formWidgetVal);
                it.remove();
                paramMap.remove(formWidgetId);
            }
            if (formWidgetId.startsWith("dataReadIs_")) {
                readMap.put(formWidgetId, formWidgetVal);
                it.remove();
                paramMap.remove(formWidgetId);
            }
        }

        // 保存草稿数据
        for (Map.Entry<String, String> map : paramMap.entrySet()) {
            ActAljoinFormDataDraft draft = new ActAljoinFormDataDraft();
            String formWidgetId = map.getKey();
            String formWidgetVal = map.getValue();
            // 草稿
            draft.setBpmnId(entity.getBpmnId());
            draft.setOperateUserId(userId);
            draft.setProcTaskId(task.getTaskDefinitionKey());
            draft.setProcInstId(task.getProcessInstanceId());
            draft.setProcDefId(task.getProcessDefinitionId());
            draft.setFormId(entity.getFormId());
            // 根据表单控件ID和表单ID获取运行时的控件完整信息
            Where<ActAljoinFormWidgetRun> widgetWhere = new Where<ActAljoinFormWidgetRun>();
            ActAljoinFormRun formRun = actAljoinFormRunService.selectById(entity.getFormId());
            widgetWhere.eq("form_id", formRun.getOrgnlId());
            widgetWhere.eq("widget_id", formWidgetId);
            ActAljoinFormWidgetRun runFormWidget = actAljoinFormWidgetRunService.selectOne(widgetWhere);
            // 草稿
            draft.setWidgetId(runFormWidget.getId());
            draft.setFormWidgetId(runFormWidget.getWidgetId());
            draft.setFormWidgetName(runFormWidget.getWidgetName());
            draft.setFormWidgetValue(formWidgetVal);
            draftList.add(draft);
            // 数据源
            if (dataMap != null) {
                for (Map.Entry<String, String> data : dataMap.entrySet()) {
                    String formWidgetId1 = data.getKey();
                    String formWidgetVal1 = data.getValue();
                    if (formWidgetId1.contains(formWidgetId)) {
                        draft.setDataResource(formWidgetVal1);
                    }
                }
            }
            // 是否读取了数据源
            if (readMap != null) {
                for (Map.Entry<String, String> read : readMap.entrySet()) {
                    String formWidgetId1 = read.getKey();
                    String formWidgetVal1 = read.getValue();
                    if (formWidgetId1.contains(formWidgetId)) {
                        draft.setIsRead(Integer.valueOf(formWidgetVal1));
                    }
                }
            }
        }
        insertBatch(draftList);
    }

    @Override
    @Transactional
    public Map<String, Object> doAppCreateWork(ActAljoinFormDataRun entity, Map<String, String> paramMap,
        Map<String, String> param, Long userId, String fullName) throws Exception {
        // 获取流程
        ActAljoinBpmnRun bpmn = actAljoinBpmnRunService.selectById(entity.getBpmnId());
        // 获取运行时表单
        ActAljoinFormRun runForm = actAljoinFormRunService.selectById(entity.getFormId());
        // 任务ID
        String activityId = param.get("taskId");
        // 是否自由流程(用来标记自由流程，当isTask=false时是自由流程)
        String isTask = param.get("isTask");
        // 下个节点
        String nextNode = param.get("nextTaskKey");
        // 用户ID
        // String userIds = param.get("userId");
        // 任务权限数据
        String taskAuth = param.get("taskAuth");

        Set<Long> currentHandleUserIdSetfterCompleted = new HashSet<Long>();

        // 如果有触发委托，当前的办理用户会被设置为空，变成候选状态，所有在进行任务进入下一步之前，多进行一次签收操作
        taskService.setAssignee(activityId, userId.toString());

        // 根据流程实例ID获取流程实例
        Task task = taskService.createTaskQuery().taskId(activityId).singleResult();
        ProcessInstance instance
            = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();

        // 获取任务的授权数据
        Where<ActAljoinTaskAssignee> taskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
        taskAssigneeWhere.eq("bpmn_id", bpmn.getOrgnlId());
        taskAssigneeWhere.eq("task_id", task.getTaskDefinitionKey());
        taskAssigneeWhere.eq("version", bpmn.getTaskAssigneeVersion());
        taskAssigneeWhere.setSqlSelect("comment_widget_ids");
        ActAljoinTaskAssignee actAljoinTaskAssignee = actAljoinTaskAssigneeService.selectOne(taskAssigneeWhere);

        // 保存评论数据
        String thisTaskUserComment = param.get("comment");
        Comment thisTaskUserCommentObj = null;

        // 设置评论用户
        Authentication.setAuthenticatedUserId(userId.toString());
        if (StringUtils.isEmpty(thisTaskUserComment)) {
            thisTaskUserComment = "";
        }
        thisTaskUserCommentObj = taskService.addComment(task.getId(), instance.getId(), thisTaskUserComment);
        thisTaskUserComment = thisTaskUserComment + "(" + fullName + " "
            + DateUtil.datetime2str(thisTaskUserCommentObj.getTime()) + ")";

        // 一次获取获取最新的评论控件内容
        Map<String, String> commentKeyValueMap = new HashMap<String, String>();
        Where<ActAljoinFormDataRun> runDataWhere = new Where<ActAljoinFormDataRun>();
        runDataWhere.eq("proc_inst_id", task.getProcessInstanceId());
        runDataWhere.in("form_widget_id", actAljoinTaskAssignee.getCommentWidgetIds().split(","));
        List<ActAljoinFormDataRun> actAljoinFormDataRunList = actAljoinFormDataRunService.selectList(runDataWhere);
        for (ActAljoinFormDataRun actAljoinFormDataRun : actAljoinFormDataRunList) {
            commentKeyValueMap.put(actAljoinFormDataRun.getFormWidgetId(), actAljoinFormDataRun.getFormWidgetValue());
        }

        // 把评论数据保存到对应的评论控件中
        for (Map.Entry<String, String> map : paramMap.entrySet()) {
            String formWidgetId = map.getKey();
            String formWidgetVal = map.getValue();
            if ((StringUtils.isNotEmpty(actAljoinTaskAssignee.getCommentWidgetIds()))
                && (actAljoinTaskAssignee.getCommentWidgetIds().indexOf(formWidgetId) != -1)
                && (StringUtils.isNotEmpty(thisTaskUserComment))) {
                // 获取最新的评论控件内容
                if (StringUtils.isNotEmpty(commentKeyValueMap.get(formWidgetId))) {
                    formWidgetVal = commentKeyValueMap.get(formWidgetId);
                }
                if (StringUtils.isNotEmpty(formWidgetVal)) {
                    paramMap.put(formWidgetId, formWidgetVal + "\n" + thisTaskUserComment);
                } else {
                    paramMap.put(formWidgetId, thisTaskUserComment);
                }
            }
        }

        // 重新构造taskAuth
        Map<String, List<String>> taskKeyUserMap = new HashMap<String, List<String>>();
        if (StringUtils.isNotEmpty(taskAuth)) {
            String[] taskAuthArr = taskAuth.split(";");
            for (int i = 0; i < taskAuthArr.length; i++) {
                String taskKey = taskAuthArr[i].split(",")[0];
                String tuserIds = taskAuthArr[i].split(",")[1];
                String[] userIdsArr = tuserIds.split("#");
                taskKeyUserMap.put(taskKey, Arrays.asList(userIdsArr));
            }
        }

        // 获取下个节点的el表达式，然后根据el表达式来设置流程变量
        List<String> elList = activitiService.getToNextCondition(activityId);
        if (elList.size() > 0) {
            for (String el : elList) {
                if (el.startsWith("${targetTask_")) {
                    // 可以选择下级节点的非自由流程
                    // 设置流程变量(决定流程的走向)
                    Map<String, Object> variables = new HashMap<String, Object>();
                    variables.put("targetTask_", nextNode);
                    runtimeService.setVariables(task.getProcessInstanceId(), variables);
                    break;
                } else {
                    // 不可以选择下级节点的非自由流程已经在提交的时候设置了环境变量
                }
            }
        }
        // 判断当前节点是否多实例节点
        MultiInstanceLoopCharacteristics currentMulti
            = activitiService.getMultiInstance(task.getProcessInstanceId(), task.getTaskDefinitionKey());
        // 判断下个节点是否多实例（会签）节点
        MultiInstanceLoopCharacteristics nextMulti
            = activitiService.getMultiInstance(task.getProcessInstanceId(), nextNode);
        // 下个节点有可能含有多个节点（下个节点是并行分支的情况）
        /*if(taskKeyUserMap.size() > 1){
        
        }*/

        // 查询加签信息
        Map<String, Object> signInfoMap = actAljoinTaskSignInfoService.getLastSignTaskIdList(task.getId(),
            task.getTaskDefinitionKey(), task.getProcessInstanceId(), task.getAssignee());
        List<String> ruTaskList = new ArrayList<String>();
        ActAljoinTaskSignInfo actAljoinTaskSignInfo = null;
        if (signInfoMap.size() > 0) {
            ruTaskList = (List<String>)signInfoMap.get("signRuTaskIdList");
            actAljoinTaskSignInfo = (ActAljoinTaskSignInfo)signInfoMap.get("signInfo");
        }
        //加签完成状态
        Integer finshType = (Integer)signInfoMap.get("finshType");

        // 标志本节点是否继续循环节点，如果是，则无需更新查询表达当前办理人
        String isCycleContinue = "0";
        // 如果是自由流程跳转
        if (isTask.equals("0")) {
            if (currentMulti == null) {
                // 如果当前节点是普通实例节点
                if (nextMulti != null) {
                    // 下个节点是多实例节点
                    // 流程集合变量${ASSIGNEE_B_KGCYWJQSRY}
                    String collectionValue = nextMulti.getInputDataItem();
                    if (StringUtils.isEmpty(collectionValue)) {
                        throw new Exception("多实例(会签)流程集合变量不能为空");
                    }
                    collectionValue
                        = collectionValue.substring(collectionValue.indexOf("{") + 1, collectionValue.indexOf("}"));
                    // 构造流程变量
                    List<Long> assigneeList = new ArrayList<Long>(); // 分配任务的人员
                    List<String> uidList = taskKeyUserMap.get(nextNode);
                    for (String uid : uidList) {
                        assigneeList.add(Long.parseLong(uid));
                    }
                    Map<String, Object> vars = new HashMap<String, Object>(); // 参数
                    // 给集合流程变量设值
                    vars.put(collectionValue, assigneeList);
                    runtimeService.setVariables(task.getExecutionId(), vars);
                }

                if(null != actAljoinTaskSignInfo){
                    activitiService.jump2Task4AddSign(task,nextNode);
                }else{
                    activitiService.jump2Task2(nextNode, task.getProcessInstanceId());
                }

                if (!nextNode.startsWith("EndEvent_")) {
                    if (nextMulti == null) {
                        // 普通节点
                        // 不是跳转到结束节点，才进行下面的操作
                        Task userTask = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId())
                            .taskDefinitionKey(nextNode).singleResult();
                        // 解签收
                        taskService.unclaim(userTask.getId());
                        // 删除原来候选人信息(含分组和候选人)
                        deleteOrgnlTaskAuth(userTask);
                        List<String> userIdList = taskKeyUserMap.get(userTask.getTaskDefinitionKey());
                        // 重新添加候选人和分组
                        if (userIdList.size() >= 1) {
                            // 如果多个候选人，则添加候选人
                            for (String assignee : userIdList) {
                                taskService.addUserIdentityLink(userTask.getId(), assignee, "candidate");
                                currentHandleUserIdSetfterCompleted.add(Long.parseLong(assignee));
                            }
                        } else {
                            throw new Exception("没有设置候选人");
                        }
                    } else {
                        // 多实例节点
                        // 不是跳转到结束节点，才进行下面的操作
                        List<Task> userTaskList = taskService.createTaskQuery()
                            .processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(nextNode).list();
                        List<String> userIdList = taskKeyUserMap.get(nextNode);
                        if (userIdList == null || userIdList.size() == 0) {
                            throw new Exception("没有设置候选人");
                        }
                        // 如果是并行，userTaskList会有多个，如果是串行userTaskList只有一个，随意这里以userTaskList遍历为准
                        for (int i = 0;
                            i < (userTaskList.size() < userIdList.size() ? userTaskList.size() : userIdList.size());
                            i++) {
                            // 解签收
                            taskService.unclaim(userTaskList.get(i).getId());
                            // 删除原来候选人信息(含分组和候选人)
                            deleteOrgnlTaskAuth(userTaskList.get(i));
                            // 重新添加候选人和分组
                            taskService.addUserIdentityLink(userTaskList.get(i).getId(), userIdList.get(i),
                                "candidate");
                            currentHandleUserIdSetfterCompleted.add(Long.parseLong(userIdList.get(i)));
                        }
                    }
                }
            } else {
                // 如果当前节点是多实例节点
                // 查询出当前执行的数量，检查是否将要完成整个节点进入下一个节点
                // nrOfInstances--总数
                // nrOfCompletedInstances--已完成数
                // nrOfActiveInstances--正在执行数据(没完成数)
                // loopCounter--当前循环索引
                int nrOfInstances
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfInstances")).intValue();
                int nrOfCompletedInstances
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfCompletedInstances")).intValue();
                int nrOfActiveInstances
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfActiveInstances")).intValue();
                int loopCounter
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "loopCounter")).intValue();
                Map<String, String> conditionParam = new HashMap<String, String>();
                // 模拟本任务执行完后的参数变化，从而预知流程在本任务执行完后的去向情况，是否达到了往下个节点的条件
                conditionParam.put("nrOfInstances", String.valueOf(nrOfInstances));
                conditionParam.put("nrOfCompletedInstances", String.valueOf(nrOfCompletedInstances + 1));
                if (!currentMulti.isSequential()) {
                    // 并行（执行后会减少）
                    conditionParam.put("nrOfActiveInstances", String.valueOf(nrOfActiveInstances - 1));
                } else {
                    // 串行（总是1）
                    conditionParam.put("nrOfActiveInstances", String.valueOf(nrOfActiveInstances));
                }
                conditionParam.put("loopCounter", String.valueOf(loopCounter + 1));
                // 获取任务完成表达式
                String completionCondition = currentMulti.getCompletionCondition();

                boolean isPass = ActUtil.isCondition(conditionParam, completionCondition);
                if (null != actAljoinTaskSignInfo) {
                    if (ruTaskList.size() > 1) {
                        isPass = false;
                    }
                }
                if (isPass) {
                    if(null != actAljoinTaskSignInfo && actAljoinTaskSignInfo.getIsBackOwner() == 1 && ruTaskList.size() == 1  && finshType != 3){
                        List<Long> uidList = new ArrayList<>();
                        uidList.add(userId);

                        Map<Long,String> userMap = new HashMap<Long,String>();
                        userMap.put(userId,fullName);

                        actTaskAddSignService.returnAssignee(task,userId.toString(),bpmn,userMap,uidList,3);
                    }else{
                        // 满足继续往下走的条件，这时候需要提供下一个节点，以及下个节点的办理人
                        if (StringUtils.isEmpty(nextNode)) {
                            throw new Exception(MsgConstant.NEXT_NODE_NOT_NULL);
                        }

                        if (nextMulti != null) {
                            // 下个节点是多实例节点
                            // 流程集合变量${ASSIGNEE_B_KGCYWJQSRY}
                            String collectionValue = nextMulti.getInputDataItem();
                            if (StringUtils.isEmpty(collectionValue)) {
                                throw new Exception("多实例(会签)流程集合变量不能为空");
                            }
                            collectionValue
                                = collectionValue.substring(collectionValue.indexOf("{") + 1, collectionValue.indexOf("}"));
                            List<String> uidList = taskKeyUserMap.get(nextNode);
                            if (uidList == null || uidList.size() == 0) {
                                throw new Exception("多实例(会签)节点必须设置处理人");
                            }
                            // 构造流程变量
                            List<Long> assigneeList = new ArrayList<Long>(); // 分配任务的人员
                            for (String uid : uidList) {
                                assigneeList.add(Long.parseLong(uid));
                            }
                            Map<String, Object> vars = new HashMap<String, Object>(); // 参数
                            // 给集合流程变量设值
                            vars.put(collectionValue, assigneeList);
                            runtimeService.setVariables(task.getExecutionId(), vars);
                        }

                        if(null != actAljoinTaskSignInfo){
                            activitiService.jump2Task4AddSign(task,nextNode);
                        }else{
                            activitiService.jump2Task2(nextNode, task.getProcessInstanceId());
                        }

                        if (!nextNode.startsWith("EndEvent_")) {
                            if (nextMulti == null) {
                                // 普通节点
                                // 不是跳转到结束节点，才进行下面的操作
                                Task userTask = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId())
                                    .taskDefinitionKey(nextNode).singleResult();
                                // 解签收
                                taskService.unclaim(userTask.getId());
                                // 删除原来候选人信息(含分组和候选人)
                                deleteOrgnlTaskAuth(userTask);
                                List<String> userIdList = taskKeyUserMap.get(userTask.getTaskDefinitionKey());
                                // 重新添加候选人和分组
                                if (userIdList.size() >= 1) {
                                    // 如果多个候选人，则添加候选人
                                    for (String assignee : userIdList) {
                                        taskService.addUserIdentityLink(userTask.getId(), assignee, "candidate");
                                        currentHandleUserIdSetfterCompleted.add(Long.parseLong(assignee));
                                    }
                                } else {
                                    throw new Exception("没有设置候选人");
                                }
                            } else {
                                // 多实例节点
                                // 不是跳转到结束节点，才进行下面的操作
                                List<Task> userTaskList = taskService.createTaskQuery()
                                    .processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(nextNode).list();
                                List<String> userIdList = taskKeyUserMap.get(nextNode);
                                if (userIdList == null || userIdList.size() == 0) {
                                    throw new Exception("没有设置候选人");
                                }
                                // 并行多实例userTaskList会有多个，并行多实例会有1一个
                                for (int i = 0;
                                     i < (userTaskList.size() < userIdList.size() ? userTaskList.size() : userIdList.size());
                                     i++) {
                                    // 解签收
                                    taskService.unclaim(userTaskList.get(i).getId());
                                    // 删除原来候选人信息(含分组和候选人)
                                    deleteOrgnlTaskAuth(userTaskList.get(i));
                                    // 重新添加候选人和分组
                                    taskService.addUserIdentityLink(userTaskList.get(i).getId(), userIdList.get(i),
                                        "candidate");
                                    currentHandleUserIdSetfterCompleted.add(Long.parseLong(userIdList.get(i)));
                                }
                            }
                        }
                    }

                } else {
                    isCycleContinue = "1";
                    if(null != actAljoinTaskSignInfo){
                        Where<ActAljoinDelegateInfo> delegateInfoWhere = new Where<>();
                        delegateInfoWhere.setSqlSelect("id");
                        delegateInfoWhere.eq("task_id",task.getId());
                        delegateInfoWhere.eq("assignee_user_id", userId);
                        Integer count = actAljoinDelegateInfoService.selectCount(delegateInfoWhere);
                        if(0 < count || ruTaskList.size() > 1){
                            actHiTaskinstService.updateHisTask(task.getId(),String.valueOf(userId));
                        }
                    }else{
                        // 当前节点不满足条件，继续流转
                        taskService.complete(task.getId());
                    }
                    if (currentMulti.isSequential()) {
                        // 多实例顺序，完成后默认会被签收，进行节前在设置候选

                        // 获取流程变量名称
                        String collectionValue = currentMulti.getInputDataItem();
                        if (StringUtils.isEmpty(collectionValue)) {
                            throw new Exception("多实例(会签)流程集合变量不能为空");
                        }
                        collectionValue
                            = collectionValue.substring(collectionValue.indexOf("{") + 1, collectionValue.indexOf("}"));

                        List<Task> userTaskList
                            = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId())
                                .taskDefinitionKey(task.getTaskDefinitionKey()).list();
                        for (Task t : userTaskList) {
                            String assignee = t.getAssignee();
                            if (StringUtils.isEmpty(assignee)) {
                                assignee = getNextSequentialUserId(task, collectionValue);
                            }
                            if (StringUtils.isNotEmpty(assignee)) {
                                taskService.unclaim(t.getId());
                                taskService.addUserIdentityLink(t.getId(), assignee, "candidate");
                                currentHandleUserIdSetfterCompleted.add(Long.parseLong(assignee));
                            }
                        }
                    }
                }
            }
        } else {
            // ------------------------------------------------ 非自由流程完成任务
            if (currentMulti == null) {
                // 如果当前节点是普通实例节点
                if (nextMulti != null) {
                    // 下个节点是多实例节点
                    // 流程集合变量${ASSIGNEE_B_KGCYWJQSRY}
                    String collectionValue = nextMulti.getInputDataItem();
                    if (StringUtils.isEmpty(collectionValue)) {
                        throw new Exception("多实例(会签)流程集合变量不能为空");
                    }
                    collectionValue
                        = collectionValue.substring(collectionValue.indexOf("{") + 1, collectionValue.indexOf("}"));
                    // 构造流程变量
                    List<Long> assigneeList = new ArrayList<Long>(); // 分配任务的人员
                    List<String> uidList = taskKeyUserMap.get(nextNode);
                    for (String uid : uidList) {
                        assigneeList.add(Long.parseLong(uid));
                    }
                    Map<String, Object> vars = new HashMap<String, Object>(); // 参数
                    // 给集合流程变量设值
                    vars.put(collectionValue, assigneeList);
                    runtimeService.setVariables(task.getExecutionId(), vars);

                    // 完成任务
                    activitiService.completeTask(null, activityId);
                    // 完成任务后设置会签办理人
                    List<Task> userTaskList = taskService.createTaskQuery()
                        .processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(nextNode).list();
                    List<String> userIdList = taskKeyUserMap.get(nextNode);
                    if (userIdList == null || userIdList.size() == 0) {
                        throw new Exception("没有设置候选人");
                    }
                    // 如果是并行，userTaskList会有多个，如果是串行userTaskList只有一个，随意这里以userTaskList遍历为准
                    for (int i = 0;
                        i < (userTaskList.size() < userIdList.size() ? userTaskList.size() : userIdList.size()); i++) {
                        // 解签收
                        taskService.unclaim(userTaskList.get(i).getId());
                        // 删除原来候选人信息(含分组和候选人)
                        deleteOrgnlTaskAuth(userTaskList.get(i));
                        // 重新添加候选人和分组
                        taskService.addUserIdentityLink(userTaskList.get(i).getId(), userIdList.get(i), "candidate");
                        currentHandleUserIdSetfterCompleted.add(Long.parseLong(userIdList.get(i)));
                    }

                } else {
                    // 下个节点是普通节点(原来没有多实例节点情况下的处理方法)
                    activitiService.completeTask(null, activityId);
                    // 完成任务后执行流ID会改变，所以这里通过流程实例ID来获取当前的任务节点
                    List<Task> taskList
                        = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();

                    for (Task assTask : taskList) {
                        if (taskKeyUserMap.get(assTask.getTaskDefinitionKey()) != null) {
                            // 解签收
                            taskService.unclaim(assTask.getId());
                            // 删除任务节点的任务权限数据
                            deleteOrgnlTaskAuth(assTask);
                            // 再新增权限数据
                            List<String> userIdList = taskKeyUserMap.get(assTask.getTaskDefinitionKey());
                            if (userIdList.size() >= 1) {
                                // 多个候选用户
                                for (String assignee : userIdList) {
                                    taskService.addUserIdentityLink(assTask.getId(), assignee, "candidate");
                                    currentHandleUserIdSetfterCompleted.add(Long.parseLong(assignee));
                                }
                            } else {
                                throw new Exception("没有设置候选人");
                            }
                        }
                    }
                }
            } else {
                // 如果当前节点是多实例节点
                // 查询出当前执行的数量，检查是否将要完成整个节点进入下一个节点
                // nrOfInstances--总数
                // nrOfCompletedInstances--已完成数
                // nrOfActiveInstances--正在执行数据(没完成数)
                // loopCounter--当前循环索引
                int nrOfInstances
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfInstances")).intValue();
                int nrOfCompletedInstances
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfCompletedInstances")).intValue();
                int nrOfActiveInstances
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfActiveInstances")).intValue();
                int loopCounter
                    = ((Integer)runtimeService.getVariable(task.getExecutionId(), "loopCounter")).intValue();
                Map<String, String> conditionParam = new HashMap<String, String>();
                // 模拟本任务执行完后的参数变化，从而预知流程在本任务执行完后的去向情况，是否达到了往下个节点的条件
                conditionParam.put("nrOfInstances", String.valueOf(nrOfInstances));
                conditionParam.put("nrOfCompletedInstances", String.valueOf(nrOfCompletedInstances + 1));
                if (!currentMulti.isSequential()) {
                    // 并行（执行后会减少）
                    conditionParam.put("nrOfActiveInstances", String.valueOf(nrOfActiveInstances - 1));
                } else {
                    // 串行（总是1）
                    conditionParam.put("nrOfActiveInstances", String.valueOf(nrOfActiveInstances));
                }
                conditionParam.put("loopCounter", String.valueOf(loopCounter + 1));
                // 获取任务完成表达式
                String completionCondition = currentMulti.getCompletionCondition();

                boolean isPass = ActUtil.isCondition(conditionParam, completionCondition);
                if (null != actAljoinTaskSignInfo) {
                    if (ruTaskList.size() > 1) {
                        isPass = false;
                    }
                }
                if (isPass) {
                    if(null != actAljoinTaskSignInfo && actAljoinTaskSignInfo.getIsBackOwner() == 1 && ruTaskList.size() == 1  && finshType != 3){
                        List<Long> uidList = new ArrayList<>();
                        uidList.add(userId);

                        Map<Long,String> userMap = new HashMap<Long,String>();
                        userMap.put(userId,fullName);

                        actTaskAddSignService.returnAssignee(task,userId.toString(),bpmn,userMap,uidList,3);
                    }else{
                        // 满足继续往下走的条件，这时候需要提供下一个节点，以及下个节点的办理人
                        if (StringUtils.isEmpty(nextNode)) {
                            throw new Exception(MsgConstant.NEXT_NODE_NOT_NULL);
                        }

                        if (nextMulti != null) {
                            // 下个节点是多实例节点
                            // 流程集合变量${ASSIGNEE_B_KGCYWJQSRY}
                            String collectionValue = nextMulti.getInputDataItem();
                            if (StringUtils.isEmpty(collectionValue)) {
                                throw new Exception("多实例(会签)流程集合变量不能为空");
                            }
                            collectionValue
                                = collectionValue.substring(collectionValue.indexOf("{") + 1, collectionValue.indexOf("}"));
                            List<String> uidList = taskKeyUserMap.get(nextNode);
                            if (uidList == null || uidList.size() == 0) {
                                throw new Exception("多实例(会签)节点必须设置处理人");
                            }
                            // 构造流程变量
                            List<Long> assigneeList = new ArrayList<Long>(); // 分配任务的人员
                            for (String uid : uidList) {
                                assigneeList.add(Long.parseLong(uid));
                            }
                            Map<String, Object> vars = new HashMap<String, Object>(); // 参数
                            // 给集合流程变量设值
                            vars.put(collectionValue, assigneeList);
                            runtimeService.setVariables(task.getExecutionId(), vars);

                            activitiService.completeTask(null, activityId);

                            // 完成任务后分配签收
                            List<Task> userTaskList = taskService.createTaskQuery()
                                .processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(nextNode).list();
                            List<String> userIdList = taskKeyUserMap.get(nextNode);
                            if (userIdList == null || userIdList.size() == 0) {
                                throw new Exception("没有设置候选人");
                            }
                            // 并行多实例userTaskList会有多个，并行多实例会有1个
                            for (int i = 0;
                                 i < (userTaskList.size() < userIdList.size() ? userTaskList.size() : userIdList.size());
                                 i++) {
                                // 解签收
                                taskService.unclaim(userTaskList.get(i).getId());
                                // 删除原来候选人信息(含分组和候选人)
                                deleteOrgnlTaskAuth(userTaskList.get(i));
                                // 重新添加候选人和分组
                                taskService.addUserIdentityLink(userTaskList.get(i).getId(), userIdList.get(i),
                                    "candidate");
                                currentHandleUserIdSetfterCompleted.add(Long.parseLong(userIdList.get(i)));
                            }
                        } else {
                            // 下个节点是普通节点
                            activitiService.completeTask(null, activityId);
                            // 完成任务后执行流ID会改变，所以这里通过流程实例ID来获取当前的任务节点
                            List<Task> taskList
                                = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();

                            for (Task assTask : taskList) {
                                if (taskKeyUserMap.get(assTask.getTaskDefinitionKey()) != null) {
                                    // 解签收
                                    taskService.unclaim(assTask.getId());
                                    // 删除任务节点的任务权限数据
                                    deleteOrgnlTaskAuth(assTask);
                                    // 再新增权限数据
                                    List<String> userIdList = taskKeyUserMap.get(assTask.getTaskDefinitionKey());
                                    if (userIdList.size() >= 1) {
                                        // 多个候选用户
                                        for (String assignee : userIdList) {
                                            taskService.addUserIdentityLink(assTask.getId(), assignee, "candidate");
                                            currentHandleUserIdSetfterCompleted.add(Long.parseLong(assignee));
                                        }
                                    } else {
                                        throw new Exception("没有设置候选人");
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // 当前节点不满足条件，继续流转
                    taskService.complete(task.getId());
                    if (currentMulti.isSequential()) {
                        // 多实例顺序，完成后默认会被签收，进行节前在设置候选
                        List<Task> userTaskList
                            = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId())
                                .taskDefinitionKey(task.getTaskDefinitionKey()).list();
                        // 获取流程变量名称
                        String collectionValue = currentMulti.getInputDataItem();
                        if (StringUtils.isEmpty(collectionValue)) {
                            throw new Exception("多实例(会签)流程集合变量不能为空");
                        }
                        collectionValue
                            = collectionValue.substring(collectionValue.indexOf("{") + 1, collectionValue.indexOf("}"));

                        for (Task t : userTaskList) {
                            String assignee = t.getAssignee();
                            if (StringUtils.isEmpty(assignee)) {
                                assignee = getNextSequentialUserId(task, collectionValue);
                            }
                            if (StringUtils.isNotEmpty(assignee)) {
                                taskService.unclaim(t.getId());
                                taskService.addUserIdentityLink(t.getId(), assignee, "candidate");
                                currentHandleUserIdSetfterCompleted.add(Long.parseLong(assignee));
                            }
                        }
                    }
                }
            }
        }

        // 更新表单数据
        updateFormData(paramMap, instance, entity, isTask, nextNode, task.getTaskDefinitionKey());

        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("instance", instance);
        retMap.put("bpmn", bpmn);
        retMap.put("runForm", runForm);
        retMap.put("currentHandleUserIdSetfterCompleted", currentHandleUserIdSetfterCompleted);
        // 标记下一步是否继续流转，如果继续流转，不用跟新查询表的当前办理人
        retMap.put("isCycleContinue", isCycleContinue);
        return retMap;
    }

    public void updateTaskSignData(String taskId) throws Exception {
        Where<ActAljoinTaskSignInfo> taskSignInfoWhere = new Where<ActAljoinTaskSignInfo>();
        taskSignInfoWhere.eq("sign_task_id", taskId);
        ActAljoinTaskSignInfo taskSignInfo = actAljoinTaskSignInfoService.selectOne(taskSignInfoWhere);
        if(null != taskSignInfo && taskSignInfo.getFinishType() != 3){
            taskSignInfo.setFinishType(2);
            actAljoinTaskSignInfoService.updateById(taskSignInfo);
        }
    }

}
