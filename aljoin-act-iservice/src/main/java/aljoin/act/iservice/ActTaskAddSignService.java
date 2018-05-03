package aljoin.act.iservice;

import aljoin.act.dao.entity.ActAljoinBpmnRun;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * 加签信息（服务类）
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public interface ActTaskAddSignService {

    /**
     * 后加签
     *
     * @param oldTask                   原任务ID
     *
     * @param assignee                  当前任务的签收人
     *
     * @param processDefinitionEntity   流程定义对象
     *
     * @param assigneeList              被加签人员列表
     *
     * @return ActivityImpl[]
     * 
     * @throws Exception
     */
    public ActivityImpl[] addSignTasksAfter(Task oldTask, String assignee,
        ProcessDefinitionEntity processDefinitionEntity, List<String> assigneeList,ActAljoinBpmnRun bpmnRun,Map<Long,String> userMap,List<Long> uIdList,Integer finshType) throws Exception;


    /**
     * 前加签
     *
     * @param oldTask                       原任务ID
     *
     * @param assignee                      当前任务的签收人
     *
     * @param processDefinitionEntity       流程定义对象
     *
     * @param assigneeList                  被加签人员列表
     *
     * @return ActivityImpl[]
     *
     * @throws Exception
     */
    public ActivityImpl[] addSignTasksBefore(Task oldTask, String assignee,
        ProcessDefinitionEntity processDefinitionEntity, List<String> assigneeList,ActAljoinBpmnRun bpmnRun,Map<Long,String> userMap,List<Long> uIdList,Integer finshType) throws Exception;


    /**
     * 跳转（包括回退和向前）至指定活动节点
     *
     * @param currentTaskEntity
     *
     * @param targetTaskDefinitionKey
     *
     * @param processInstanceId
     *
     * @param nextActivityId
     *
     * @param assigneeList
     *
     * @throws Exception
     */
    public void moveTo(TaskEntity currentTaskEntity, String targetTaskDefinitionKey, String processInstanceId,
        String nextActivityId, List<String> assigneeList,String assignee) throws Exception;

    /**
     * 跳转（包括回退和向前）至指定活动节点
     * @param targetTaskDefinitionKey    加签任务KEY
     *
     * @param processInstanceId          流程实例ID
     *
     * @param oldTaskId                  加签任务ID
     *
     * @param assignee                   加签任务签收人
     *
     * @param nextActivityId             加签任务的下一个任务key
     *
     * @param assigneeList               被加签人员列表
     *
     * @throws Exception
     */
    public void moveTo(String targetTaskDefinitionKey, String processInstanceId, String oldTaskId, String assignee,
        String nextActivityId, List<String> assigneeList) throws Exception;

    /**
     * 分裂某节点为多实例节点
     *
     * @param targetTaskDefinitionKey
     *
     * @param processInstanceId
     *
     * @param processDefinitionEntity
     *
     * @param isSequential
     *
     * @param assigneeList
     *
     * @return
     *
     * @throws Exception
     */
    public ActivityImpl split(String targetTaskDefinitionKey, String processInstanceId,
        ProcessDefinitionEntity processDefinitionEntity, boolean isSequential, List<String> assigneeList,String assignee)
        throws Exception;


    /**
     * 分裂某节点为多实例节点
     *
     * @param targetTaskDefinitionKey
     *
     * @param processInstanceId
     *
     * @param processDefinitionEntity
     *
     * @param assigneeList
     *
     * @return
     *
     * @throws Exception
     */
    public ActivityImpl split(String targetTaskDefinitionKey, String processInstanceId,
        ProcessDefinitionEntity processDefinitionEntity, List<String> assigneeList,String assignee) throws Exception;

    /**
     * 特送环节
     *
     * @param oldTask                   原任务ID
     *
     * @param assignee                  当前任务的签收人
     *
     * @param destTaskDefKey            目标任务key
     *
     * @param processDefinitionEntity   流程定义对象
     *
     * @param assigneeList              被加签人员列表
     *
     * @return ActivityImpl[]
     *
     * @throws Exception
     */
    public ActivityImpl[] deliveryLink(Task oldTask, String assignee,String destTaskDefKey,
        ProcessDefinitionEntity processDefinitionEntity, List<String> assigneeList) throws Exception;

    /**
     * 返回原办理人
     * @param task
     * @throws Exception
     */
    public void returnAssignee(Task task,String userId,ActAljoinBpmnRun bpmnRun,Map<Long,String> userMap,List<Long> uIdList,Integer finshType) throws Exception;

    /**
     * 跳转（包括回退和向前）至指定活动节点
     * @param currentTaskEntity			当前任务
     * @param targetTaskDefinitionKey	目标任务key
     * @throws Exception
     */
    public void moveTo(TaskEntity currentTaskEntity, String targetTaskDefinitionKey) throws Exception;

}