package aljoin.act.service;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.util.JsonUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActivitiServiceImplTest {

    @Resource
    private ActActivitiService activitiService;
    @Resource
    private ActAljoinBpmnService actAljoinBpmnService;

    @Resource
    private FormService formService;
    @Resource
    private HistoryService historyService;
    @Resource
    private IdentityService identityService;
    @Resource
    private ManagementService managementService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;

    @Test
    public void testAddUser() {
        // activitiService.addUser(123123L);
    }

    @Test
    public void testDelUser() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddGroup() {
        fail("Not yet implemented");
    }

    @Test
    public void testDelGroup() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddUserGroup() {
        fail("Not yet implemented");
    }

    @Test
    public void testDelUserGroup() {
        fail("Not yet implemented");
    }

    @Test
    public void testDeployBpmn() throws Exception {
        ActAljoinBpmn actAljoinBpmn = actAljoinBpmnService.selectById(883226654407815168L);
        activitiService.deployBpmn(actAljoinBpmn);
    }

    @Test
    public void testStartBpmnByKey() throws Exception {
        activitiService.startBpmnByKey("Process_RIKLIIXhsA");
    }

    @Test
    public void testStartBpmnByKey2() throws Exception {
        // activitiService.startBpmnByKey("Process_jLMaL10MMe");
        Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
        where.eq("process_id", "Process_trTlFCfmXe");
        Map<String, String> param = new HashMap<String, String>();
        param.put("isUrgent", "1");// 设置为紧急
//        activitiService.startBpmn(actAljoinBpmnService.selectOne(where), null, param, 903158435201142784L);
    }

    @Test
    public void testGetImageInputStream() throws Exception {
        InputStream is = activitiService.getImageInputStream("95001", "Process_2.Process_2.png");
        FileUtils.copyInputStreamToFile(is, new File("C:/Users/zhongjy/Desktop/leave001_11221.png"));
    }

    @Test
    public void testGetImageInputStream2() throws Exception {
        InputStream is = activitiService.getImageInputStream("Process_2", 0);
        FileUtils.copyInputStreamToFile(is, new File("C:/Users/zhongjy/Desktop/leave001_11221.png"));
    }

    @Test
    public void testGetRuImageInputStream() throws Exception {
        InputStream is = activitiService.getRuImageInputStream("695001", true);
        FileUtils.copyInputStreamToFile(is, new File("C:/Users/zhongjy/Desktop/收文阅件8.png"));
    }

    @Test
    public void testCompleteTask() throws Exception {
        List<Long> assigneeList = new ArrayList<Long>(); // 分配任务的人员
        assigneeList.add(903158663987843072L);
        assigneeList.add(903158743402795008L);
        Map<String, Object> vars = new HashMap<String, Object>(); // 参数
        vars.put("aljoinAssigneeList", assigneeList);
        // runtimeService.setVariables("562501", variables);
        runtimeService.setVariables("2582501", vars);

        activitiService.completeTask(null, "2582505");
        // taskService.resolveTask("672502");
    }

    /**
     * 
     * 签收任务
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月18日 下午8:05:39
     */
    @Test
    public void testClaimTask() throws Exception {
        // taskService.setAssignee("300002", "903158435201142784");
        taskService.setAssignee("2582505", "902815909474525184");// 可以多次签收
        // taskService.claim("305004", "902815909474525184");//只能签收一次,第二次签收会抛异常
        // taskService.unclaim("715009");
    }

    /**
     * 
     * 单人待签收/待办理
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月18日 下午6:52:32
     */
    @Test
    public void testSelectCandidateTask() throws Exception {
        PageBean pageBean = new PageBean();
        pageBean.setPageNum(1);
        pageBean.setPageSize(10);
        List<Task> taskList = activitiService.selectCandidateTask("903158435201142784", pageBean);
        for (Task task : taskList) {
            System.out.println("任务名称：" + task.getName());
            System.out.println("任务主键ID：" + task.getId());
            System.out.println("流程定义ID：" + task.getProcessDefinitionId());
            System.out.println("流程实例ID：" + task.getProcessInstanceId());
            System.out.println("xml定义的任务ID：" + task.getTaskDefinitionKey());
            System.out.println("任务创建时间：" + task.getCreateTime());
            System.out.println("任务到期时间：" + task.getDueDate());
            System.out.println("办理人：" + task.getAssignee());
            System.out.println("任务拥有者：" + task.getOwner());
            System.out.println("-------------------------------------");
        }
        // System.out.println(taskList);
    }

    /**
     * 
     * 查询用户的待办任务列表(参与)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月18日 下午6:52:32
     */
    @Test
    public void testSelectInvolvedTask() throws Exception {
        PageBean pageBean = new PageBean();
        pageBean.setPageNum(1);
        pageBean.setPageSize(100);
        List<Task> taskList = activitiService.selectInvolvedTask("903158435201142784", pageBean);
        System.out.println("记录数：" + taskList.size());
        for (Task task : taskList) {
            System.out.println("任务(环节)名称：" + task.getName());
            System.out.println("任务主键ID：" + task.getId());
            System.out.println("流程定义ID：" + task.getProcessDefinitionId());
            System.out.println("xml定义的任务ID：" + task.getTaskDefinitionKey());
            System.out.println("任务创建时间：" + task.getCreateTime());
            System.out.println("任务到期时间：" + task.getDueDate());
            System.out.println("流程实例ID：" + task.getProcessInstanceId());
            System.out.println("签收人ID：" + task.getAssignee());

            // 根据流程实例ID获取流程实例
            ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();
            System.out.println("businessKey(bpmnId，formId，is)：" + instance.getBusinessKey());
            System.out.println("流程名称：" + instance.getProcessDefinitionName());
            // 获取流程定义
            Deployment deployment =
                repositoryService.createDeploymentQuery().deploymentId(instance.getDeploymentId()).singleResult();
            System.out.println("流程分类ID：" + deployment.getCategory());
            System.out.println("-------------------------------------");
        }
    }

    /**
     * 
     * 历史任务列表
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月18日 下午7:34:15
     */
    @Test
    public void testSelectHisTask() throws Exception {
        PageBean pageBean = new PageBean();
        pageBean.setPageNum(1);
        pageBean.setPageSize(100);
        List<HistoricTaskInstance> list = activitiService.selectHisTask("903158435201142784", pageBean);
        for (HistoricTaskInstance instance : list) {
            System.out.println("任务(环节)名称：" + instance.getName());
            System.out.println("任务主键ID：" + instance.getId());
            System.out.println("流程定义ID：" + instance.getProcessDefinitionId());
            System.out.println("xml定义的任务ID：" + instance.getTaskDefinitionKey());
            System.out.println("任务创建时间：" + instance.getCreateTime());
            System.out.println("任务结束时间：" + instance.getEndTime());
            System.out.println("任务到期时间：" + instance.getDueDate());
            System.out.println("流程实例ID：" + instance.getProcessInstanceId());
            System.out.println("签收人(当前办理人，被委托用户ID)ID：" + instance.getAssignee());
            System.out.println("签收时间：" + instance.getClaimTime());
            System.out.println("任务拥有者（委托的用户ID）：" + instance.getOwner());
            System.out.println("执行流ID：" + instance.getExecutionId());
            // 根据流程实例ID获取流程实例
            HistoricProcessInstance pp = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(instance.getProcessInstanceId()).singleResult();
            // ProcessInstance p =
            // historyService.createHistoricProcessInstanceQuery().processInstanceId(instance.getProcessInstanceId()).singleResult();
            System.out.println("businessKey(bpmnId，formId，is)：" + pp.getBusinessKey());
            System.out.println("流程名称：" + pp.getProcessDefinitionName());
            System.out.println("流程发起时间：" + pp.getStartTime());
            System.out.println("流程发起人ID：" + pp.getStartUserId());
            // 获取流程定义
            Deployment deployment =
                repositoryService.createDeploymentQuery().deploymentId(pp.getDeploymentId()).singleResult();
            System.out.println("流程分类ID：" + deployment.getCategory());
            System.out.println("-------------------------------------");
            System.out.println("---------------------------------------------------");
        }

    }

    /**
     * 
     * 获取默认启动的流程
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月18日 下午7:35:26
     */
    @Test
    public void testSelectInstanceListByStartUser() throws Exception {
        PageBean pageBean = new PageBean();
        pageBean.setPageNum(1);
        pageBean.setPageSize(100);
        List<HistoricProcessInstance> list =
            activitiService.selectInstanceListByStartUser("903158435201142784", pageBean);
        for (HistoricProcessInstance historicProcessInstance : list) {
            System.out.println("流程名称：" + historicProcessInstance.getProcessDefinitionName());
            System.out.println("启动用户ID：" + historicProcessInstance.getStartUserId());
            System.out.println("流程启动时间：" + historicProcessInstance.getStartTime());
            System.out.println("流程结束时间：" + historicProcessInstance.getEndTime());
            System.out.println("流程定义ID：" + historicProcessInstance.getProcessDefinitionId());
            System.out.println("流程实例ID：" + historicProcessInstance.getId());
            System.out.println("businessKey：" + historicProcessInstance.getBusinessKey());
        }

    }

    @SuppressWarnings("unused")
    @Test
    public void testDemo() throws Exception {
        PageBean pageBean = new PageBean();
        pageBean.setPageNum(1);
        pageBean.setPageSize(1000);
        // 开始索引
        int firstResult = (pageBean.getPageNum() - 1) * pageBean.getPageSize();
        // 查询记录数
        int maxResults = pageBean.getPageSize();
        // 查询指定用户发起的流程
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().unfinished() // finished
                                                                                                              // 完成的流程
                                                                                                              // ,unfinish
            // 还在运行中的流程
            .startedBy("903158435201142784").orderByProcessInstanceStartTime().desc().listPage(firstResult, maxResults);
        // System.out.println(JsonUtil.obj2str(list));
        // 查询指定用户参与的流程信息 （流程历史 用户参与 ）
        List<HistoricProcessInstance> list2 =
            historyService.createHistoricProcessInstanceQuery().involvedUser("903158435201142784")
                .orderByProcessInstanceStartTime().desc().listPage(firstResult, maxResults);
        // System.out.println(JsonUtil.obj2str(list2));

        // 查询指定流程的任务流转路径 （流程历史 任务 流转 路经）
        List<HistoricTaskInstance> list3 = historyService.createHistoricTaskInstanceQuery()// 何获取第一个任务节点
            .processInstanceId("320001").orderByHistoricTaskInstanceEndTime().asc().list();
        // System.out.println(JsonUtil.obj2str(list3));

        // 获取流程的第一个节点（开始事件的下一个任务节点）
        String firstTaskId = "";
        BpmnModel model = repositoryService.getBpmnModel("Process_jLMaL10MMe:1:317504");
        Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
        for (FlowElement e : flowElements) {
            if (e instanceof StartEvent) {
                StartEvent startEvent = (StartEvent)e;
                firstTaskId = startEvent.getOutgoingFlows().get(0).getTargetRef();
                break;
            }
        }
        // System.out.println("***************"+firstTaskId);

    }

    /**
     * 
     * 获取任务节点后面的表达式（暂时只取到一级和二级），取出变量名称，用于流程表单的的数据抓取
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月20日 上午11:25:23
     */
    @Test
    public void testDemo2() throws Exception {
        // 根据当前任务，进入下个任务的时候，获取流程设置UEL表达式中的变量名称
        BpmnModel model2 = repositoryService.getBpmnModel("Process_eo0NmOOxHo:8:477516");
        Collection<FlowElement> flowElements2 = model2.getMainProcess().getFlowElements();
        for (FlowElement e : flowElements2) {
            // if("Task_095vvkv".equals(e.getId())){
            // 获取任务对外箭头的直接表达式
            if (e instanceof UserTask) {
                UserTask userTask = (UserTask)e;
                List<SequenceFlow> flowList = userTask.getOutgoingFlows();
                System.out.println("任务名称：" + userTask.getName());
                for (SequenceFlow sequenceFlow : flowList) {
                    // #################第1级
                    if (sequenceFlow.getConditionExpression() != null) {
                        System.out.println(sequenceFlow.getConditionExpression());
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
                                    System.out.println(sequenceFlow2.getConditionExpression());
                                }
                            }
                        }
                    }
                }
                System.out.println("******************************");
            }
        }
    }

    public FlowElement getFlowElement(Collection<FlowElement> elements, String activitiId) throws Exception {
        // 根据当前任务，进入下个任务的时候，获取流程设置UEL表达式中的变量名称
        for (FlowElement e : elements) {
            if (activitiId.equals(e.getId())) {
                return e;
            }
        }
        return null;
    }

    @Test
    public void testDemo3() throws Exception {
        // 删除流程实例
        // runtimeService.deleteProcessInstance("735001", "测试删除流程实例");

        // taskService.delegateTask(taskId, userId);
        // 服务委托
        // taskService.delegateTask("485005", "922630700387082240");
        // 获取当前节点的条件表达式列表
        /*List<String> list = activitiService.getToNextCondition("485005");
        System.out.println(JsonUtil.obj2str(list));*/
        // 获取下个节点,
        // Map<String, Object> variables = new HashMap<String,Object>();
        // variables.put("text_Ed2MVMr8e0h1mr2", "123");
        // variables.put("text_A4XWkfXtsHdbzXR", "123b");
        // runtimeService.setVariables("562501", variables);
        // List<TaskDefinition> tf = activitiService.getNextTaskInfo2("1207501",false,"Task_1gle4uq");

        // System.out.println(JsonUtil.obj2str(tf));

        // System.out.println(tf);
        // activitiService.jump2Task(targetTaskKey, instanceId);
    }

    @Test
    public void testDemo4() throws Exception {

        // activitiService.jump2Task("Task_0bl4hqe", "607501");
        // 任务自由跳转
        activitiService.jump2Task2("Task_0ee5f9a", "647501");

        // 确认历史轨迹里已保存
        // List<HistoricActivityInstance> activities =
        // historyService.createHistoricActivityInstanceQuery().processInstanceId("585001").list();
        // System.out.println(JsonUtil.obj2str(activities));
    }

    @Test
    public void testDemo5() throws Exception {
        // List<TaskDefinition> list = activitiService.getPreTaskInfo("Task_1wg0nix", "665001");
        // 获取除了自己之外的所有节点
        try {
            // List<TaskDefinition> list = activitiService.getAllTaskInfoExcludeSelf("Task_02xtrs2", "522703");
            List<ActivityImpl> list = activitiService.getAllActivity("522703");
            for (ActivityImpl activityImpl : list) {
                System.out.println(JsonUtil.obj2str(activityImpl.getId()));
            }
            // System.out.println(JsonUtil.obj2str(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDemo6() throws Exception {
        taskService.delegateTask("672502", "903158000981626880");
        List<HistoricActivityInstance> list =
            historyService.createHistoricActivityInstanceQuery().processInstanceId("665001").list();
        /*taskService.delegateTask(taskId, userId);
        taskService.resolveTask(taskId);*/
        System.out.println(JsonUtil.obj2str(list));
    }

    /**
     * 
     * 多人会签单元测试
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年11月7日 下午4:22:18
     */
    @Test
    public void testDemo7() throws Exception {
        /*朱艺明 903158663987843072
                  黄鹏艺 903158743402795008
                  包希喆 903158607620591616
                  郭娜霜 903158337968787456
                  赖建洋 903158101766557696
                  王杰  903158000981626880
                  测试经理    902815909474525184
                  总工程师    902816117344231424
                  项目经理    902815372075130880
                  产品经理    902815715718651904*/
        List<Long> assigneeList = new ArrayList<Long>(); // 分配任务的人员
        assigneeList.add(903158663987843072L);
        assigneeList.add(903158743402795008L);
        assigneeList.add(903158607620591616L);
        assigneeList.add(903158337968787456L);
        assigneeList.add(903158101766557696L);
        assigneeList.add(903158000981626880L);
        assigneeList.add(902815909474525184L);
        assigneeList.add(902816117344231424L);
        assigneeList.add(902815372075130880L);
        assigneeList.add(902815715718651904L);
        Map<String, Object> vars = new HashMap<String, Object>(); // 参数
        vars.put("assigneeList", assigneeList);
        // runtimeService.setVariables("562501", variables);
        runtimeService.setVariables("695001", vars);

    }

    /**
     * 注意：这个单元测试不能随便用，流程会被删除掉
     */
    @Test
    public void deleteAllProcess() throws Exception {
        // 删除流程实例
        List<Task> taskList = taskService.createTaskQuery().list();
        List<String> processInstanceIdList = new ArrayList<String>();
        for (Task task : taskList) {
            if (!processInstanceIdList.contains(task.getProcessInstanceId())) {
                processInstanceIdList.add(task.getProcessInstanceId());
            }
        }
        /*for (String s : processInstanceIdList) {
          runtimeService.deleteProcessInstance(s, "测试删除流程实例");
        }*/

    }

    @Test
    public void deleteAllProcess2() throws Exception {
        // 删除流程实例

        runtimeService.deleteProcessInstance("2582501", "测试删除流程实例");

    }

    @Test
    public void demo001() throws Exception {
        // 删除流程实例
        // List<TaskDefinition> defList = activitiService.getNextTaskInfo2("2590001", false, "UserTask_0a0iyya");
        // System.out.println(JsonUtil.obj2str(defList));
        HistoricProcessInstance his =
            historyService.createHistoricProcessInstanceQuery().processInstanceId("2590001").singleResult();
        BpmnModel model = repositoryService.getBpmnModel(his.getProcessDefinitionId());
        Collection<FlowElement> eleList = model.getMainProcess().getFlowElements();
        for (FlowElement flowElement : eleList) {
            if (flowElement instanceof UserTask && flowElement.getId().equals("UserTask_1hyqmye")) {
                UserTask ut = (UserTask)flowElement;
                MultiInstanceLoopCharacteristics mlc = ut.getLoopCharacteristics();
                if (mlc != null) {
                    System.out.println(JsonUtil.obj2str(mlc));
                    System.out.println(mlc.getCompletionCondition());
                    System.out.println(mlc.getInputDataItem());
                    System.out.println("=======================");
                }
            }
        }
    }

    @Test
    public void demo002() throws Exception {
        MultiInstanceLoopCharacteristics multi = activitiService.getMultiInstance("2590001", "UserTask_1hyqmyed");

        System.out.println(JsonUtil.obj2str(multi));
    }

    @Test
    public void demo004() throws Exception {

    }

}
