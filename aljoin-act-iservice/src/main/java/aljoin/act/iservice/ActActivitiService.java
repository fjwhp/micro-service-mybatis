package aljoin.act.iservice;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.entity.ActAljoinFormDataRun;
import aljoin.act.dao.entity.ActAljoinFormRun;
import aljoin.object.PageBean;

/**
 * 
 * 流程服务类(接口).
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:00:34
 */
public interface ActActivitiService {

    /**
     * 
     * 新增用户 
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午4:21:33
     */
    public void addUser(Long userId, String fullName);

    /**
     * 
     * 删除用户
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午4:21:43
     */
    public void delUser(Long userId);

    /**
     * 
     * 新增分组
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午4:21:55
     */
    public void addGroup(Long groupId);

    /**
     * 
     * 删除分组
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午4:22:05
     */
    public void delGroup(Long groupId);

    /**
     * 
     * 新增用户分组
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午4:22:16
     */
    public void addUserGroup(Long userId, Long groupId);

    /**
     * 
     * 删除
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年6月10日 下午4:22:30
     */
    public void delUserGroup(Long userId, Long groupId);

    /**
     * 
     * 部署bpmn
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年7月7日 下午3:32:28
     */
    public Deployment deployBpmn(ActAljoinBpmn bpmn) throws Exception;

    /**
     * 
     * 启动流程
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年7月7日 下午3:32:28
     */
    public ProcessInstance startBpmnByKey(String key) throws Exception;

    /**
     * 
     * 完成任务
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年7月7日 下午3:32:28
     */
    public void completeTask(Map<String, Object> variables, String taskId) throws Exception;

    /**
     * 
     * 单人待签收/待办理
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年7月7日 下午3:32:28
     */
    public List<Task> selectCandidateTask(String userId, PageBean pageBean) throws Exception;

    /**
     * 
     * 已经签收过的任务列表，某种意义上我理解为真正的办理人(单人已签收/待办理)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年7月7日 下午3:32:28
     */
    public List<Task> selectAssigneeTask(String userId, PageBean pageBean) throws Exception;

    /**
     * 
     * 获取待办的相关候选任务(含assignee)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年7月7日 下午3:32:28
     */
    public List<Task> selectInvolvedTask(String userId, PageBean pageBean) throws Exception;

    /**
     * 
     * 根据用户ID查询历史任务列表
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年7月7日 下午3:32:28
     */
    public List<HistoricTaskInstance> selectHisTask(String userId, PageBean pageBean) throws Exception;

    /**
     * 
     * 根据流程实例ID获取当前流程状态的流程图片输入流(状态为执行中)
     *
     * @return：InputStream
     *
     * @author：zhongjy
     *
     * @date：2017年7月7日 下午4:31:15
     */
    public InputStream getRuImageInputStream(String processInstanceId, boolean isAll) throws Exception;

    /**
     * 
     * 根据流程部署id和资源文件名称来查询流程图片(无执行状态)
     *
     * @return：InputStream
     *
     * @author：zhongjy
     *
     * @date：2017年7月7日 下午4:31:15
     */
    public InputStream getImageInputStream(String deployId, String resourceName) throws Exception;

    /**
     * 
     * 根据流程部署id和资源文件名称来查询流程图片(无执行状态).
     *
     * @return：InputStream
     *
     * @author：zhongjy
     *
     * @date：2017年7月7日 下午4:31:15
     */
    public InputStream getImageInputStream(String key, Integer version) throws Exception;

    /**
     * 
     * 启动流程并生成表单数据
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年9月30日 下午1:15:34
     */
    public ProcessInstance startBpmn(ActAljoinBpmn bpmn, ActAljoinFormRun runForm, Map<String, String> param)
        throws Exception;

    /**
     * 
     * 启动流程并生成表单数据
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年9月30日 下午1:15:34
     */
    public ProcessInstance startBpmn(ActAljoinBpmnRun bpmn, ActAljoinFormRun runForm, Map<String, String> param,
        Long userId) throws Exception;

    /**
     * 
     * 获取用户发起的流程实例
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年7月7日 下午3:32:28
     */
    public List<HistoricProcessInstance> selectInstanceListByStartUser(String userId, PageBean pageBean)
        throws Exception;

    /**
     *
     * 获取当前节点的信息
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017年10月25日
     */
    public List<HistoricTaskInstance> getCurrentNodeInfo(String taskId) throws Exception;

    /**
     *
     * 根据任务ID获取审批意见信息
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017年10月25日
     */
    public List<Comment> getCommentInfo(String taskId) throws Exception;

    /**
     *
     * 获取流程往下执行的连线条件
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017年10月25日
     */
    public List<String> getToNextCondition(String taskId) throws Exception;

    /**
     * 
     * 获取下一个任务定义(第二个参数是是否带上条件获取)
     *
     * @return：TaskDefinition
     *
     * @author：zhongjy
     *
     * @date：2017年10月28日 下午9:44:44
     */
    public List<TaskDefinition> getNextTaskInfo(String processInstanceId, boolean withCondition) throws Exception;

    public List<TaskDefinition> getNextTaskInfo2(String processInstanceId, boolean withCondition, String activityId)
        throws Exception;

    /**
     * 
     * 插入流程查询数据: processInstance流程实例 isUrgent紧急情况1-一般,2-紧急,3-加急； runForm运行时表单(固定流程不用传) processTitle流程标题(自定义流程不用传)
     * createFullUserName发起人姓名 currentHandleFullUserName当前代办人 categoryId流程分类ID
     * 
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年11月2日 下午8:56:01
     */
    public void insertProcessQuery(ProcessInstance processInstance, String isUrgent, ActAljoinFormRun runForm,
        String processTitle, String createFullUserName, String currentHandleFullUserName, Long categoryId)
        throws Exception;

    /**
     * 
     * 跳转到目标节点
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年11月3日 下午4:30:26
     */
    public void jump2Task(String targetTaskKey, String instanceId) throws Exception;

    /**
     * 
     * 跳转到目标节点,通过通过更改流程路线的方式
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年11月3日 下午4:30:26
     */
    public void jump2Task2(String targetTaskKey, String instanceId) throws Exception;

    /**
     * 
     * 跳转到目标节点,通过通过更改流程路线的方式(第二个参数是执行流id)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年11月3日 下午4:30:26
     */
    public void jump2Task3(String targetTaskKey, String processInstanceId, String executionId) throws Exception;

    /**
     * 
     * 获取上一个节点(有可能有多个)
     *
     * @return：List<TaskDefinition>
     *
     * @author：zhongjy
     *
     * @date：2017年11月6日 上午11:44:16
     */
    public List<TaskDefinition> getPreTaskInfo(String currentTaskKey, String processInstanceId) throws Exception;

    /**
     * 
     * 获取上一个节点(有可能有多个)--上级是并行网关的时候需要继续往上去取
     *
     * @return：List<TaskDefinition>
     *
     * @author：zhongjy
     *
     * @date：2017年11月6日 上午11:44:16
     */
    public List<TaskDefinition> getPreTaskInfo2(String currentTaskKey, String processInstanceId) throws Exception;

    /**
     * 
     * 获取除了自己以外的所有节点
     *
     * @return：List<TaskDefinition>
     *
     * @author：zhongjy
     *
     * @date：2017年11月6日 上午11:44:16
     */
    public List<TaskDefinition> getAllTaskInfoExcludeSelf(String currentTaskKey, String processInstanceId)
        throws Exception;

    /**
     * 
     * 获取除了自己以外的所有节点
     *
     * @return：List<TaskDefinition>
     *
     * @author：zhongjy
     *
     * @date：2017年11月6日 上午11:44:16
     */
    public List<TaskDefinition> getAllTaskInfo(String processInstanceId) throws Exception;

    /**
     * 
     * 获取除了开始的所有节点（含结束节点）
     *
     * @return：List<TaskDefinition>
     *
     * @author：zhongjy
     *
     * @date：2017年11月6日 上午11:44:16
     */
    public List<ActivityImpl> getAllActivity(String processInstanceId) throws Exception;

    /**
     * 
     * 获取流程实例的表单的控件，然后查询运行时表，如果表单对应的控件有值，填上
     *
     * @return：List<ActAljoinFormDataRun>
     *
     * @author：zhongjy
     *
     * @date：2017年11月22日 下午1:45:29
     */
    public List<ActAljoinFormDataRun> getFormDataRun(String processInstanceId) throws Exception;

    /**
     * 
     * 获取表单流程变量
     *
     * @return：Map<String,String>
     *
     * @author：zhongjy
     *
     * @date：2017年11月22日 下午1:45:33
     */
    public Map<String, String> getConditionParamValue(List<ActAljoinFormDataRun> formDataRunList, String condition)
        throws Exception;

    public InputStream getHisImageInputStream(String processInstanceId, String string);

    /**
     * 
     * 获取前节点优化
     *
     * @return：List<TaskDefinition>
     *
     * @author：zhongjy
     *
     * @date：2017年12月13日 下午2:32:30
     */
    public List<TaskDefinition> getPreTaskInfo3(String currentTaskKey, ProcessInstance instance, ActAljoinBpmn bpmn)
        throws Exception;

    /**
     * 
     * 获取多实例流程
     *
     * @return：MultiInstanceLoopCharacteristics
     *
     * @author：zhongjy
     *
     * @date：2017年12月20日 下午5:39:40
     */
    public MultiInstanceLoopCharacteristics getMultiInstance(String processInstanceId, String taskKey) throws Exception;

    /**
     * 加签之后自由跳转环节
     * @param task                当前任务对象
     * @param targetTaskDefKey    目标任务节点KEY
     * @throws Exception
     */
    public void jump2Task4AddSign(Task task,String targetTaskDefKey) throws Exception;
}
