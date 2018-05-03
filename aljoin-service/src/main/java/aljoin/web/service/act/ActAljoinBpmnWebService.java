package aljoin.web.service.act;

import aljoin.act.dao.entity.*;
import aljoin.act.iservice.*;
import aljoin.att.dao.object.AppAttSignInOutHisVO;
import aljoin.att.iservice.AttSignInOutService;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.goo.dao.object.AppGooInOutVO;
import aljoin.goo.dao.object.AppGooPurchaseVO;
import aljoin.goo.iservice.GooInOutService;
import aljoin.goo.iservice.GooPurchaseService;
import aljoin.ioa.dao.entity.IoaCircula;
import aljoin.ioa.iservice.IoaCirculaService;
import aljoin.mee.dao.object.AppMeeOutsideMeetingVO;
import aljoin.mee.iservice.MeeOutsideMeetingService;
import aljoin.object.AppConstant;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.RetMsg;
import aljoin.off.dao.object.AppOffMonthReportVO;
import aljoin.off.iservice.OffMonthReportService;
import aljoin.pub.dao.object.AppPubPublicInfo;
import aljoin.pub.iservice.PubPublicInfoService;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.dao.entity.SysDataDict;
import aljoin.sys.iservice.SysDataDictService;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;
import aljoin.util.StringUtil;
import aljoin.veh.dao.object.AppVehInfoVO;
import aljoin.veh.iservice.VehUseService;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.*;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Event;
import org.activiti.engine.task.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 自定义流程bpmn表(服务实现类).
 * 
 * @author：zhongjy
 * @date：2017年7月25日 下午4:01:32
 */
@Service
public class ActAljoinBpmnWebService {

    @Resource
    private ActAljoinBpmnFormService actAljoinBpmnFormService;
    @Resource
    private ActAljoinTaskAssigneeService actAljoinTaskAssigneeService;
    @Resource
    private ActAljoinFormRunService actAljoinFormRunService;
    @Resource
    private ActAljoinFormWidgetRunService actAljoinFormWidgetRunService;
    @Resource
    private ActAljoinFormDataRunService actAljoinFormDataRunService;
    @Resource
    private ActAljoinFormDataDraftService actAljoinFormDataDraftService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private TaskService taskService;
    @Resource
    private ActAljoinBpmnRunService actAljoinBpmnRunService;
    @Resource
    private ActAljoinBpmnService actAljoinBpmnService;
    @Resource
    private HistoryService historyService;
    @Resource
    private ResResourceService resResourceService;
    @Resource
    private ActAljoinFormDataHisService actAljoinFormDataHisService;
    @Resource
    private ActAljoinFormService actAljoinFormService;
    @Resource
    private ActAljoinFormWidgetService actAljoinFormWidgetService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private ActActivitiService actActivitiService;
    @Resource
    private AttSignInOutService attSignInOutService;
    @Resource
    private MeeOutsideMeetingService meeOutsideMeetingService;
    @Resource
    private OffMonthReportService offMonthReportService;
    @Resource
    private PubPublicInfoService pubPublicInfoService;
    @Resource
    private ActAljoinDataSourceService actAljoinDataSourceService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private AutDepartmentService autDepartmentService;
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private ActAljoinActivityLogService actAljoinActivityLogService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;
    @Resource
    private VehUseService vehUseService;
    @Resource
    private GooInOutService gooInOutService;
    @Resource
    private GooPurchaseService gooPurchaseService;
    @Resource
    private IoaCirculaService ioaCirculaService;
    @Resource
    private SysDataDictService sysDataDictService;

    @Transactional
    public void openForm(HttpServletRequest request) throws Exception {
        // 流程ID
        String id = request.getParameter("id");
        // 任务ID
        String taskId = request.getParameter("taskId");
        // activitiId
        String activityId = request.getParameter("activityId");
        // 是否是待办 0：否 1：是 用于在办详情判断按钮的显示
        String isWait = request.getParameter("isWait");
        request.setAttribute("isWait", isWait);
        if (StringUtils.isNotEmpty(id)) {
            getWaitTaskInfo(request, id, taskId, activityId);
        } else {
            String processInstanceId = request.getParameter("processInstanceId");
            getDoingTaskInfo(request, processInstanceId, taskId);
        }
    }

    /**
     * 获取待办任务详情.
     * 
     * @return：void
     * @author：wangj @date：2017-11-28
     */
    private void getWaitTaskInfo(HttpServletRequest request, String id, String taskId, String activityId)
        throws Exception {
        // 获取流程或流程run表数据
        ActAljoinBpmn bpmn = new ActAljoinBpmn();
        ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(id);
        List<ActAljoinFormDataRun> runDataList = new ArrayList<ActAljoinFormDataRun>();

        Long formId;
        // 全局表单
        ActAljoinBpmnForm actAljoinBpmnForm = new ActAljoinBpmnForm();
        // 表单运行时
        ActAljoinFormRun actAljoinFormRun = new ActAljoinFormRun();
        if (null != bpmnRun && !StringUtils.isEmpty(activityId)) {
            // 根据流程ID获取运行时的全局表单ID
            String instanceId = taskService.createTaskQuery().taskId(activityId).singleResult().getProcessInstanceId();
            Where<ActAljoinFormDataRun> dataRunWhere = new Where<ActAljoinFormDataRun>();
            dataRunWhere.eq("proc_inst_id", instanceId);
            runDataList = actAljoinFormDataRunService.selectList(dataRunWhere);
        } else {
            // 不满足上述任一条件，则判断按照最新的版本
            bpmn = actAljoinBpmnService.selectById(id);
        }
        // 获取第一个任务节点ID
        if (StringUtils.isEmpty(taskId)) {
            ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(bpmn.getProcessId()).latestVersion().singleResult();
            BpmnModel model = repositoryService.getBpmnModel(definition.getId());
            Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
            for (FlowElement e : flowElements) {
                if (e instanceof StartEvent) {
                    StartEvent startEvent = (StartEvent)e;
                    taskId = startEvent.getOutgoingFlows().get(0).getTargetRef();
                    break;
                }
            }
        }
        if (StringUtils.isEmpty(taskId)) {
            throw new Exception("无用户任务节点");
        }

        ActAljoinTaskAssignee actAljoinTaskAssignee = new ActAljoinTaskAssignee();
        Where<ActAljoinTaskAssignee> actAljoinTaskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
        // 如果是新建的流程，runDataList为空，此时按照最新的查表单方式去查
        if (runDataList.size() == 0) {
            Where<ActAljoinBpmnForm> actAljoinBpmnFormWhere = new Where<ActAljoinBpmnForm>();
            actAljoinBpmnFormWhere.eq("bpmn_id", bpmn.getId());
            actAljoinBpmnFormWhere.eq("element_type", "StartEvent");
            actAljoinBpmnForm = actAljoinBpmnFormService.selectOne(actAljoinBpmnFormWhere);
            actAljoinFormRun = actAljoinFormRunService.selectById(actAljoinBpmnForm.getFormId());
            if (actAljoinFormRun == null) {
                throw new Exception("该流程没有表单");
            } else {
                formId = actAljoinFormRun.getId();
            }

            // 根据流程id获取表单权限，并根据权限显示表单
            actAljoinTaskAssigneeWhere.eq("bpmn_id", id);
            actAljoinTaskAssigneeWhere.eq("task_id", taskId);
            actAljoinTaskAssigneeWhere.orderBy("version", false);
            List<ActAljoinTaskAssignee> assigneeList =
                actAljoinTaskAssigneeService.selectList(actAljoinTaskAssigneeWhere);
            if (assigneeList.size() == 0) {
                throw new Exception("表单权限未配置");
            }
            actAljoinTaskAssignee = assigneeList.get(0);

            // 如果此时流程版本有升级，在run表中插入一条新的记录
            Where<ActAljoinBpmnRun> bpmnRunWhere = new Where<ActAljoinBpmnRun>();
            bpmnRunWhere.eq("orgnl_id", bpmn.getId());
            bpmnRunWhere.eq("version", bpmn.getVersion());
            bpmnRun = actAljoinBpmnRunService.selectOne(bpmnRunWhere);
            if (null == bpmnRun) {
                bpmnRun = new ActAljoinBpmnRun();
                BeanUtils.copyProperties(bpmn, bpmnRun);
                bpmnRun.setId(null);
                bpmnRun.setTaskAssigneeVersion(actAljoinTaskAssignee.getVersion());
                bpmnRun.setOrgnlId(bpmn.getId());
                actAljoinBpmnRunService.insert(bpmnRun);
            }
        } else {
            // 如果runDataList不为空，则为正在流转的流程，查run表
            formId = runDataList.get(0).getFormId();
            actAljoinFormRun = actAljoinFormRunService.selectById(formId);
            bpmnRun = actAljoinBpmnRunService.selectById(id);
            if (bpmnRun != null) {
                actAljoinTaskAssigneeWhere.eq("bpmn_id", bpmnRun.getOrgnlId());
                actAljoinTaskAssigneeWhere.eq("task_id", taskId);
                actAljoinTaskAssigneeWhere.eq("version", bpmnRun.getTaskAssigneeVersion());
                actAljoinTaskAssignee = actAljoinTaskAssigneeService.selectOne(actAljoinTaskAssigneeWhere);
            }
        }
        String instanceId = "";
        List<ActAljoinFormDataDraft> dataDraftList = new ArrayList<ActAljoinFormDataDraft>();
        if (!StringUtils.isEmpty(activityId)) {
            instanceId = taskService.createTaskQuery().taskId(activityId).singleResult().getProcessInstanceId();
            String taskKey = taskService.createTaskQuery().taskId(activityId).singleResult().getTaskDefinitionKey();
            Where<ActAljoinFormDataDraft> dataDraftWhere = new Where<ActAljoinFormDataDraft>();
            dataDraftWhere.eq("proc_inst_id", instanceId);
            dataDraftWhere.eq("proc_task_id", taskKey);
            dataDraftWhere.eq("create_user_id", request.getAttribute("current_user_id"));
            dataDraftList = actAljoinFormDataDraftService.selectList(dataDraftWhere);
        }

        // 如果当前节点草稿箱有数据则读取草稿箱数据
        if (dataDraftList.size() > 0) {
            request.setAttribute("runDataList", dataDraftList);
        } else {
            // 查询运行时附件
            request.setAttribute("runDataList", runDataList);
        }

        request.setAttribute("formId", formId);
        request.setAttribute("html", actAljoinFormRun.getHtmlCode());
        request.setAttribute("activityId", activityId);
        String signInTime = request.getParameter("signInTime");
        request.setAttribute("signInTime", signInTime);
        request.setAttribute("currentTime", DateUtil.datetime2str(new Date()));

        if (null != actAljoinTaskAssignee) {
            request.setAttribute("showWidgetIds", actAljoinTaskAssignee.getShowWidgetIds());
            request.setAttribute("editWidgetIds", actAljoinTaskAssignee.getEditWidgetIds());
            request.setAttribute("notNullWidgetIds", actAljoinTaskAssignee.getNotNullWidgetIds());
            request.setAttribute("commentWidgetIds", actAljoinTaskAssignee.getCommentWidgetIds());
            String operateAuthIds = actAljoinTaskAssignee.getOperateAuthIds();
            if (operateAuthIds.contains("aljoin-task-circulation")) {
                Where<IoaCircula> ioawhere = new Where<IoaCircula>();
                ioawhere.eq("process_instance_id", instanceId);
                int count = ioaCirculaService.selectCount(ioawhere);
                if(count > 0){
                    operateAuthIds += ",aljoin-task-circulation-opinion";
                }
            }
            request.setAttribute("operateAuthIds", operateAuthIds);
            // 新增红头文件和痕迹保留变量
            request.setAttribute("redHeadWidgetIds", actAljoinTaskAssignee.getRedHeadWidgetIds());
            request.setAttribute("saveMarkWidgetIds", actAljoinTaskAssignee.getSaveMarkWidgetIds());
        }
        request.setAttribute("bpmnId", bpmnRun.getId().toString());
        Where<ActAljoinFormWidgetRun> actAljoinFormWidgetRunWhere = new Where<ActAljoinFormWidgetRun>();
        actAljoinFormWidgetRunWhere.eq("form_id", actAljoinFormRun.getOrgnlId());
        List<ActAljoinFormWidgetRun> actAljoinFormWidgetRunList =
            actAljoinFormWidgetRunService.selectList(actAljoinFormWidgetRunWhere);
        request.setAttribute("actAljoinFormWidgetRunList", actAljoinFormWidgetRunList);

        // 附件信息

    }

    /**
     * 获取在办任务详情.
     * 
     * @return：void
     * @author：wangj @date：2017-11-28
     */
    private void getDoingTaskInfo(HttpServletRequest request, String processInstanceId, String taskId) throws Exception {
        if (StringUtils.isNotEmpty(processInstanceId)) {
            // 获取当前任务
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            Task task = taskList.get(0);
            request.setAttribute("activityId", task.getId());
            Where<ActAljoinFormDataRun> dataRunWhere = new Where<ActAljoinFormDataRun>();
            dataRunWhere.eq("proc_inst_id", processInstanceId);
            List<ActAljoinFormDataRun> runDataList = actAljoinFormDataRunService.selectList(dataRunWhere);
            String formId = "";
            if (null != runDataList && !runDataList.isEmpty()) {
                ActAljoinFormDataRun dataRun = runDataList.get(0);
                if (null != dataRun) {
                    formId = String.valueOf(dataRun.getFormId());
                    // 根据流程ID获取运行时的全局表单ID
                    ActAljoinFormRun actAljoinFormRun = actAljoinFormRunService.selectById(Long.valueOf(formId));
                    if (actAljoinFormRun == null) {
                        throw new Exception("该流程没有表单");
                    }
                    request.setAttribute("formId", Long.valueOf(formId));
                    request.setAttribute("html", actAljoinFormRun.getHtmlCode());
                    request.setAttribute("currentTime", DateUtil.datetime2str(new Date()));

                    // 获取bpmn_run_id
                    HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId).singleResult();
                    String bpmnId = processInstance.getBusinessKey().split(",")[0];
                    ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(bpmnId);

                    if (StringUtils.isEmpty(taskId)) {
                        // 获取第一个任务节点ID
                        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                            .processDefinitionKey(bpmnRun.getProcessId()).latestVersion().singleResult();
                        BpmnModel model = repositoryService.getBpmnModel(definition.getId());
                        Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
                        for (FlowElement e : flowElements) {
                            if (e instanceof UserTask) {
                                UserTask userTask = (UserTask)e;
                                String targetDef = userTask.getOutgoingFlows().get(0).getTargetRef();
                                if (targetDef.indexOf("ExclusiveGateway") < 0) {
                                    taskId = userTask.getOutgoingFlows().get(0).getSourceRef();
                                    break;
                                }

                            }
                        }
                    }

                    Where<ActAljoinFormDataDraft> dataDraftWhere = new Where<ActAljoinFormDataDraft>();
                    // String taskKey =
                    // taskService.createTaskQuery().taskId(taskId).singleResult().getTaskDefinitionKey();
                    dataDraftWhere.eq("proc_inst_id", processInstanceId);
                    dataDraftWhere.eq("proc_task_id", taskId);
                    List<ActAljoinFormDataDraft> dataDraftList =
                        actAljoinFormDataDraftService.selectList(dataDraftWhere);

                    // 如果当前节点草稿箱有数据则读取草稿箱数据
                    if (dataDraftList.size() > 0) {
                        // 查询草稿附件
                        request.setAttribute("runDataList", dataDraftList);
                    } else {
                        // 查询运行时附件
                        request.setAttribute("runDataList", runDataList);
                    }

                    if (StringUtils.isEmpty(taskId)) {
                        throw new Exception("无用户任务节点");
                    }

                    // 根据流程id获取表单权限，并根据权限显示表单
                    Where<ActAljoinTaskAssignee> actAljoinTaskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
                    actAljoinTaskAssigneeWhere.eq("bpmn_id", bpmnRun.getOrgnlId());
                    actAljoinTaskAssigneeWhere.eq("task_id", taskId);
                    actAljoinTaskAssigneeWhere.eq("version", bpmnRun.getTaskAssigneeVersion());
                    ActAljoinTaskAssignee actAljoinTaskAssignee =
                        actAljoinTaskAssigneeService.selectOne(actAljoinTaskAssigneeWhere);

                    if (null != actAljoinTaskAssignee) {
                        request.setAttribute("showWidgetIds", actAljoinTaskAssignee.getShowWidgetIds());
                        request.setAttribute("editWidgetIds", actAljoinTaskAssignee.getEditWidgetIds());
                        request.setAttribute("notNullWidgetIds", actAljoinTaskAssignee.getNotNullWidgetIds());
                        request.setAttribute("commentWidgetIds", actAljoinTaskAssignee.getCommentWidgetIds());
                        String operateAuthIds = actAljoinTaskAssignee.getOperateAuthIds();
                        if (operateAuthIds.contains("aljoin-task-circulation")) {
                            Where<IoaCircula> ioawhere = new Where<IoaCircula>();
                            ioawhere.eq("process_instance_id", processInstance.getId());
                            int count = ioaCirculaService.selectCount(ioawhere);
                            if (count > 0) {
                                operateAuthIds += ",aljoin-task-circulation-opinion";
                            }
                        }
                        request.setAttribute("operateAuthIds", operateAuthIds);
                    }

                    Where<ActAljoinFormWidgetRun> actAljoinFormWidgetRunWhere = new Where<ActAljoinFormWidgetRun>();
                    actAljoinFormWidgetRunWhere.eq("form_id", actAljoinFormRun.getOrgnlId());
                    List<ActAljoinFormWidgetRun> actAljoinFormWidgetRunList =
                        actAljoinFormWidgetRunService.selectList(actAljoinFormWidgetRunWhere);

                    request.setAttribute("bpmnId", bpmnRun.getId());
                    request.setAttribute("actAljoinFormWidgetRunList", actAljoinFormWidgetRunList);
                }
            }
        }
    }

    @Transactional
    public void openFormData(HttpServletRequest request) throws Exception {
        // 流程ID
        // String id = request.getParameter("id");
        // 任务ID
        // String taskId = request.getParameter("taskId");
        // activitiId
        // String activityId = request.getParameter("activityId");
        // 是否是待办 0：否 1：是 用于在办详情判断按钮的显示
        String isWait = request.getParameter("isWait");
        request.setAttribute("isWait", isWait);
        String processInstanceId = request.getParameter("processInstanceId");
        getHisTaskInfo(request, processInstanceId);

    }

    @Transactional
    public void openFormCir(HttpServletRequest request) throws Exception {
        String isWait = request.getParameter("isWait");
        request.setAttribute("isWait", isWait);
        String processInstanceId = request.getParameter("processInstanceId");
        getCirTaskInfo(request, processInstanceId);
    }

    /**
     * 获取详情.
     * 
     * @return：void
     * @author：huangw @date：2017-12-06
     */
    private void getHisTaskInfo(HttpServletRequest request, String processInstanceId) throws Exception {
        if (StringUtils.isNotEmpty(processInstanceId)) {
            List<HistoricTaskInstance> historicTaskInstanceList =
                historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();

            if (historicTaskInstanceList != null && historicTaskInstanceList.size() > 0) {
                Where<ActAljoinFormDataHis> datahisWhere = new Where<ActAljoinFormDataHis>();
                datahisWhere.eq("proc_inst_id", processInstanceId);
                List<ActAljoinFormDataHis> hisDataList = actAljoinFormDataHisService.selectList(datahisWhere);
                Map<String, ActAljoinFormDataHis> hisMap = new HashMap<String, ActAljoinFormDataHis>();
                List<Long> runIdList = new ArrayList<Long>();
                String showId = "";
                if (hisDataList != null && hisDataList.size() > 0) {
                    for (ActAljoinFormDataHis actAljoinFormDataHis : hisDataList) {
                        runIdList.add(actAljoinFormDataHis.getId());
                        hisMap.put(actAljoinFormDataHis.getProcInstId(), actAljoinFormDataHis);
                    }
                } else {
                    throw new Exception("该流程没有表单");
                }
                for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                    showId += historicTaskInstance.getTaskDefinitionKey() + ",";
                }
                
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                String bpmnRunId = historicProcessInstance.getBusinessKey().split(",")[0];
                HistoricTaskInstance historicTaskInstance = historicTaskInstanceList.get(0);
                request.setAttribute("activityId", historicTaskInstance.getId());
                String formId = "";
                ActAljoinFormDataHis actAljoinForm = new ActAljoinFormDataHis();
                if (hisMap.containsKey(historicTaskInstance.getProcessInstanceId().toString())) {
                    actAljoinForm = hisMap.get(historicTaskInstance.getProcessInstanceId().toString());
                } else {
                    throw new Exception("该流程没有表单");
                }
                
                formId = String.valueOf(actAljoinForm.getFormId());
                ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(bpmnRunId);
                request.setAttribute("bpmnId", bpmnRunId);
                request.setAttribute("formId", Long.valueOf(formId));
                ActAljoinForm actAljoinFormhis = actAljoinFormService.selectById(Long.valueOf(formId));
                ActAljoinFormRun actAljoinFormRun = new ActAljoinFormRun();

                if (actAljoinFormhis == null) {
                    actAljoinFormRun = actAljoinFormRunService.selectById(Long.valueOf(formId));
                    Where<ActAljoinFormWidgetRun> actAljoinFormWidgetRunWhere = new Where<ActAljoinFormWidgetRun>();
                    actAljoinFormWidgetRunWhere.eq("form_id", actAljoinFormRun.getOrgnlId());
                    List<ActAljoinFormWidgetRun> actAljoinFormWidgetRunList =
                        actAljoinFormWidgetRunService.selectList(actAljoinFormWidgetRunWhere);
                    request.setAttribute("actAljoinFormWidgetRunList", actAljoinFormWidgetRunList);
                    request.setAttribute("html", actAljoinFormRun.getHtmlCode());
                } else {
                    Where<ActAljoinFormWidget> actAljoinFormWidgetRunWhere = new Where<ActAljoinFormWidget>();
                    actAljoinFormWidgetRunWhere.eq("form_id", actAljoinFormRun.getOrgnlId());
                    List<ActAljoinFormWidget> actAljoinFormWidgetRunList =
                        actAljoinFormWidgetService.selectList(actAljoinFormWidgetRunWhere);
                    request.setAttribute("actAljoinFormWidgetRunList", actAljoinFormWidgetRunList);
                    request.setAttribute("html", actAljoinFormhis.getHtmlCode());

                }
                request.setAttribute("currentTime", DateUtil.datetime2str(new Date()));
                Where<ResResource> resAttachmentWhere = new Where<ResResource>();
                resAttachmentWhere.in("biz_id", runIdList);
                List<ResResource> runAttachList = resResourceService.selectList(resAttachmentWhere);
                request.setAttribute("attachList", runAttachList);
                request.setAttribute("runDataList", hisDataList);

                Where<ActAljoinTaskAssignee> actAljoinTaskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
                actAljoinTaskAssigneeWhere.eq("version", bpmnRun.getTaskAssigneeVersion());
                actAljoinTaskAssigneeWhere.eq("bpmn_id", bpmnRun.getOrgnlId());
                actAljoinTaskAssigneeWhere.in("task_id", showId);
                List<ActAljoinTaskAssignee> actAljoinTaskAssignee =
                    actAljoinTaskAssigneeService.selectList(actAljoinTaskAssigneeWhere);
                String showIds = "";
                for (ActAljoinTaskAssignee actAljoinTaskAssignee2 : actAljoinTaskAssignee) {
                    showIds += actAljoinTaskAssignee2.getShowWidgetIds() + ",";
                }
                if (null != actAljoinTaskAssignee) {
                    request.setAttribute("showWidgetIds", showIds);
                    request.setAttribute("editWidgetIds", "");
                    request.setAttribute("notNullWidgetIds", "");
                    request.setAttribute("commentWidgetIds", "");
                    request.setAttribute("operateAuthIds", "");
                }
            }
        }
    }

    /**
     * 获取详情.
     * 
     * @return：void
     * @author：huangw 
     * @date：2017-12-06
     */
    private void getCirTaskInfo(HttpServletRequest request, String processInstanceId) throws Exception {
        if (StringUtils.isNotEmpty(processInstanceId)) {
            List<HistoricTaskInstance> historicTaskInstanceList =
                historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
            if (historicTaskInstanceList != null && historicTaskInstanceList.size() > 0) {
                // 流程是否正在进行中
                Boolean isRun = false;
                Where<ActAljoinFormDataHis> datahisWhere = new Where<ActAljoinFormDataHis>();
                datahisWhere.eq("proc_inst_id", processInstanceId);
                List<ActAljoinFormDataHis> hisDataList = actAljoinFormDataHisService.selectList(datahisWhere);
                Map<String, ActAljoinFormDataHis> hisMap = new HashMap<String, ActAljoinFormDataHis>();
                Map<String, ActAljoinFormDataRun> runMap = new HashMap<String, ActAljoinFormDataRun>();
                List<Long> runIdList = new ArrayList<Long>();
                String showId = "";
                if (hisDataList != null && hisDataList.size() > 0) {
                    request.setAttribute("runDataList", hisDataList);
                    for (ActAljoinFormDataHis actAljoinFormDataHis : hisDataList) {
                        runIdList.add(actAljoinFormDataHis.getId());
                        hisMap.put(actAljoinFormDataHis.getProcInstId(), actAljoinFormDataHis);
                    }
                } else {
                    isRun = true;
                    Where<ActAljoinFormDataRun> where = new Where<ActAljoinFormDataRun>();
                    where.eq("proc_inst_id", processInstanceId);
                    List<ActAljoinFormDataRun> runDataList = actAljoinFormDataRunService.selectList(where);
                    if(runDataList.size()>0){
                        request.setAttribute("runDataList", runDataList);
                        for(ActAljoinFormDataRun dataRun : runDataList){
                            runIdList.add(dataRun.getId());
                            runMap.put(dataRun.getProcInstId(), dataRun);
                        }
                    }else{
                        throw new Exception("该流程没有表单");
                    }
                    
                }
                for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                    showId += historicTaskInstance.getTaskDefinitionKey() + ",";
                }
                
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                String bpmnRunId = historicProcessInstance.getBusinessKey().split(",")[0];
                HistoricTaskInstance historicTaskInstance = historicTaskInstanceList.get(0);
                request.setAttribute("activityId", historicTaskInstance.getId());
                String formId = "";
                ActAljoinFormDataHis actAljoinForm = new ActAljoinFormDataHis();
                ActAljoinFormDataRun actAljoinRun = new ActAljoinFormDataRun();
                if (hisMap.containsKey(historicTaskInstance.getProcessInstanceId().toString())) {
                    actAljoinForm = hisMap.get(historicTaskInstance.getProcessInstanceId().toString());
                    formId = String.valueOf(actAljoinForm.getFormId());
                } else {
                    if(runMap.containsKey(historicTaskInstance.getProcessInstanceId().toString())){
                        actAljoinRun = runMap.get(historicTaskInstance.getProcessInstanceId().toString());
                        formId = String.valueOf(actAljoinRun.getFormId());
                    }else{
                        throw new Exception("该流程没有表单");
                    }
                }
                
                ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(bpmnRunId);
                request.setAttribute("bpmnId", bpmnRunId);
                request.setAttribute("formId", Long.valueOf(formId));
                ActAljoinForm actAljoinFormhis = actAljoinFormService.selectById(Long.valueOf(formId));
                ActAljoinFormRun actAljoinFormRun = new ActAljoinFormRun();

                if (actAljoinFormhis == null) {
                    actAljoinFormRun = actAljoinFormRunService.selectById(Long.valueOf(formId));
                    Where<ActAljoinFormWidgetRun> actAljoinFormWidgetRunWhere = new Where<ActAljoinFormWidgetRun>();
                    actAljoinFormWidgetRunWhere.eq("form_id", actAljoinFormRun.getOrgnlId());
                    List<ActAljoinFormWidgetRun> actAljoinFormWidgetRunList =
                        actAljoinFormWidgetRunService.selectList(actAljoinFormWidgetRunWhere);
                    request.setAttribute("actAljoinFormWidgetRunList", actAljoinFormWidgetRunList);
                    request.setAttribute("html", actAljoinFormRun.getHtmlCode());
                } else {
                    Where<ActAljoinFormWidget> actAljoinFormWidgetRunWhere = new Where<ActAljoinFormWidget>();
                    actAljoinFormWidgetRunWhere.eq("form_id", actAljoinFormRun.getOrgnlId());
                    List<ActAljoinFormWidget> actAljoinFormWidgetRunList =
                        actAljoinFormWidgetService.selectList(actAljoinFormWidgetRunWhere);
                    request.setAttribute("actAljoinFormWidgetRunList", actAljoinFormWidgetRunList);
                    request.setAttribute("html", actAljoinFormhis.getHtmlCode());

                }
                request.setAttribute("currentTime", DateUtil.datetime2str(new Date()));
                Where<ResResource> resAttachmentWhere = new Where<ResResource>();
                resAttachmentWhere.in("biz_id", runIdList);
                List<ResResource> runAttachList = resResourceService.selectList(resAttachmentWhere);
                request.setAttribute("attachList", runAttachList);
                

                Where<ActAljoinTaskAssignee> actAljoinTaskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
                actAljoinTaskAssigneeWhere.eq("version", bpmnRun.getTaskAssigneeVersion());
                actAljoinTaskAssigneeWhere.eq("bpmn_id", bpmnRun.getOrgnlId());
                actAljoinTaskAssigneeWhere.in("task_id", showId);
                List<ActAljoinTaskAssignee> actAljoinTaskAssignee =
                    actAljoinTaskAssigneeService.selectList(actAljoinTaskAssigneeWhere);
                String showIds = "";
                HashSet<String> widgetSet = new HashSet<String>();
                for (ActAljoinTaskAssignee actAljoinTaskAssignee2 : actAljoinTaskAssignee) {
                    widgetSet.add(actAljoinTaskAssignee2.getShowWidgetIds());
                }
                showIds = StringUtil.list2str(new ArrayList<>(widgetSet), ",");
                if (null != actAljoinTaskAssignee) {
                    request.setAttribute("showWidgetIds", showIds);
                    request.setAttribute("editWidgetIds", "");
                    request.setAttribute("notNullWidgetIds", "");
                    request.setAttribute("commentWidgetIds", "");
                    if(isRun){
                        request.setAttribute("operateAuthIds", "aljoin-task-circulation");
                    }else{
                        request.setAttribute("operateAuthIds", "");
                    }
                    
                }
            }
        }
    }

    public List<FixedFormProcessLog> getLog(String tasksId, String processInstanceId) throws Exception {
        if (StringUtils.isEmpty(processInstanceId) && StringUtils.isNotEmpty(tasksId)) {
            processInstanceId = taskService.createTaskQuery().taskId(tasksId).singleResult().getProcessInstanceId();
        }
        List<String> recevieUserIdList = new ArrayList<String>();
        List<Long> assigneeIdList = new ArrayList<Long>();
        List<Comment> list = new ArrayList<Comment>();
        List<HistoricActivityInstance> activitiyList = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        List<Event> events = new ArrayList<Event>();
        // 根据流程实例id取出意见
        list = taskService.getProcessInstanceComments(processInstanceId);

        // 构造日志列表
        List<FixedFormProcessLog> logList = new ArrayList<FixedFormProcessLog>();
        List<HistoricActivityInstance> activitieList = new ArrayList<HistoricActivityInstance>();
        List<TaskDefinition> taskDefinitionList = actActivitiService.getNextTaskInfo(processInstanceId, false);
        for (HistoricActivityInstance historicActivityInstance : activitiyList) {
            if (StringUtils.isNotEmpty(historicActivityInstance.getActivityName())) {
                activitieList.add(historicActivityInstance);
            }
        }
        List<String> activityNameList = new ArrayList<String>();
        if (taskDefinitionList.size() >= 0) {
            for (TaskDefinition taskDefinition : taskDefinitionList) {
                activityNameList.add(String.valueOf(taskDefinition.getNameExpression()));
            }
        }
        for (int i = 0; i < activitieList.size(); i++) {
            HistoricActivityInstance historicActivityInstance = activitieList.get(i);
            getTaskLogInfo(historicActivityInstance, processInstanceId, events, recevieUserIdList, list, assigneeIdList,
                logList, taskDefinitionList, activityNameList);
        }
        List<AutUser> assigneeList = new ArrayList<AutUser>();
        List<AutUser> recevieList = new ArrayList<AutUser>();
        if (null != assigneeIdList && !assigneeIdList.isEmpty()) {
            Where<AutUser> assigneeWhere = new Where<AutUser>();
            assigneeWhere.in("id", assigneeIdList);
            assigneeWhere.setSqlSelect("id,user_name,full_name");
            assigneeList = autUserService.selectList(assigneeWhere);
        }
        if (null != recevieUserIdList && !recevieUserIdList.isEmpty()) {
            Where<AutUser> recevieWhere = new Where<AutUser>();
            recevieWhere.in("id", recevieUserIdList);
            recevieWhere.setSqlSelect("id,user_name,full_name");
            recevieList = autUserService.selectList(recevieWhere);
        }
        for (FixedFormProcessLog log : logList) {
            for (AutUser user : assigneeList) {
                if (null != user && null != user.getId() && null != log && null != log.getOperationId()) {
                    if (String.valueOf(user.getId()).equals(log.getOperationId())) {
                        log.setOperationName(user.getFullName());
                    }
                }
            }
            String recUserName = "";
            for (AutUser user : recevieList) {
                if (null != user && null != user.getId() && null != log && null != log.getRecevieUserId()) {
                    if (log.getRecevieUserId().contains(String.valueOf(user.getId()))) {
                        recUserName += user.getFullName() + ";";

                    }
                }
            }
            log.setRecevieUserName(recUserName);
        }
        return logList;
    }

    public void getTaskLogInfo(HistoricActivityInstance historicActivityInstance, String processInstanceId,
        List<Event> events, List<String> recevieUserIdList, List<Comment> list, List<Long> assigneeIdList,
        List<FixedFormProcessLog> logList, List<TaskDefinition> taskDefinitionList, List<String> activityNameList)
        throws Exception {
        FixedFormProcessLog log = new FixedFormProcessLog();
        List<TaskDefinition> preTaskDefList =
            getPreTaskInfo(historicActivityInstance.getActivityId(), processInstanceId);
        if (preTaskDefList.size() > 0) {
            for (TaskDefinition definition : preTaskDefList) {
                TaskDefinition preTaskDef = definition;
                String nextTaskName = "";
                List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery()
                    .taskDefinitionKey(preTaskDef.getKey()).processInstanceId(processInstanceId).list();
                if (null != taskList && taskList.isEmpty()) {
                    continue;
                }
                String taskName = "";
                if (taskList.size() > 1) {
                    for (TaskDefinition taskDefinition : taskDefinitionList) {
                        if (taskDefinition.getKey().equals(taskList.get(0).getTaskDefinitionKey())) {
                            taskName = String.valueOf(taskDefinition.getNameExpression());
                        } else {
                            HistoricTaskInstance task = taskList.get(taskList.size() - 1);
                            taskName = task.getName();
                        }
                        // 构建日志信息
                        bulidLogInfo(processInstanceId, nextTaskName, taskName, historicActivityInstance, events,
                            recevieUserIdList, logList, list, taskList, assigneeIdList, log);
                    }
                    if (null != taskDefinitionList && taskDefinitionList.isEmpty()) {
                        HistoricTaskInstance task = taskList.get(taskList.size() - 1);
                        // 构建日志信息
                        bulidLogInfo(processInstanceId, nextTaskName, task.getName(), historicActivityInstance, events,
                            recevieUserIdList, logList, list, taskList, assigneeIdList, log);
                    }
                    logList.add(log);
                } else {
                    for (TaskDefinition taskDefinition : taskDefinitionList) {
                        if (activityNameList.contains(historicActivityInstance.getActivityName())) {
                            if (String.valueOf(taskDefinition.getNameExpression())
                                .equals(historicActivityInstance.getActivityName())) {
                                List<TaskDefinition> perTaskDefList =
                                    actActivitiService.getPreTaskInfo2(taskDefinition.getKey(), processInstanceId);
                                for (TaskDefinition perTaskDef : perTaskDefList) {
                                    List<HistoricTaskInstance> historicTaskInstanceList =
                                        new ArrayList<HistoricTaskInstance>();
                                    if (StringUtils.isEmpty(String.valueOf(perTaskDef.getAssigneeExpression()))) {
                                        taskName = String.valueOf(taskDefinition.getNameExpression());
                                        historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                                            .processInstanceId(processInstanceId)
                                            .taskDefinitionKey(taskDefinition.getKey()).list();
                                    } else {
                                        HistoricTaskInstance task = taskList.get(taskList.size() - 1);
                                        taskName = task.getName();
                                        historicTaskInstanceList = taskList;
                                    }
                                    // 构建日志信息
                                    bulidLogInfo(processInstanceId, nextTaskName, taskName, historicActivityInstance,
                                        events, recevieUserIdList, logList, list, historicTaskInstanceList,
                                        assigneeIdList, log);
                                }
                            }
                        } else {
                            if (null != taskList && !taskList.isEmpty()) {
                                List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()
                                    .processInstanceId(processInstanceId)
                                    .taskDefinitionKey(historicActivityInstance.getActivityId()).list();
                                if (StringUtils.isEmpty(historicActivityInstance.getAssignee()) && htiList.size() > 1) {
                                    taskName = String.valueOf(taskDefinition.getNameExpression());
                                    taskList = historyService.createHistoricTaskInstanceQuery()
                                        .processInstanceId(processInstanceId).taskDefinitionKey(taskDefinition.getKey())
                                        .list();
                                } else {
                                    HistoricTaskInstance task = taskList.get(taskList.size() - 1);
                                    taskName = task.getName();
                                }
                                // 构建日志信息
                                bulidLogInfo(processInstanceId, nextTaskName, taskName, historicActivityInstance,
                                    events, recevieUserIdList, logList, list, taskList, assigneeIdList, log);
                            }
                        }
                    }
                    if (null != taskDefinitionList && taskDefinitionList.isEmpty()) {
                        HistoricTaskInstance task = taskList.get(taskList.size() - 1);
                        // 构建日志信息
                        bulidLogInfo(processInstanceId, nextTaskName, task.getName(), historicActivityInstance, events,
                            recevieUserIdList, logList, list, taskList, assigneeIdList, log);
                    }
                    logList.add(log);
                }
            }
        }
    }

    private void bulidLogInfo(String processInstanceId, String nextTaskName, String taskName,
        HistoricActivityInstance historicActivityInstance, List<Event> events, List<String> recevieUserIdList,
        List<FixedFormProcessLog> logList, List<Comment> list, List<HistoricTaskInstance> taskList,
        List<Long> assigneeIdList, FixedFormProcessLog log) {
        nextTaskName = taskName + " ----> " + historicActivityInstance.getActivityName();
        events = taskService.getTaskEvents(historicActivityInstance.getTaskId());
        String receivedIds = "";
        for (Event event : events) {
            if (event.getAction().equals("AddUserLink")) {// 获取任务接收人（候选人）
                String receivedId = event.getMessage().substring(0, event.getMessage().indexOf("_"));
                recevieUserIdList.add(receivedId);
                receivedIds += receivedId + ";";
            }
        }
        log.setRecevieUserId(receivedIds);

        for (Comment comment : list) {
            if (comment.getTaskId().equals(historicActivityInstance.getTaskId())) {
                log.setComment(comment.getFullMessage());
                log.setOperationTime(comment.getTime());
            }
        }
        for (HistoricTaskInstance tsk : taskList) {
            if (tsk.getProcessInstanceId().equals(processInstanceId)) {
                String assignee = "";
                assignee = tsk.getAssignee();
                if (StringUtils.isNotEmpty(assignee)) {
                    log.setOperationId(assignee);
                    assigneeIdList.add(Long.valueOf(assignee));
                }
            }
        }
        log.setTaskId(historicActivityInstance.getTaskId());
        log.setDirection(nextTaskName);
    }

    public List<TaskDefinition> getPreTaskInfo(String currentTaskKey, String processInstanceId) throws Exception {
        List<TaskDefinition> taskList = new ArrayList<TaskDefinition>();
        // 获取流程发布Id
        ProcessInstance instance =
            runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
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

    /**
     * App 在办待办列表打开详情.
     * 
     * @return：void
     * @author：wangj @date：2017-12-20
     */
    public RetMsg openAppForm(HttpServletRequest request, Long currentUserId, String userName) throws Exception {
        RetMsg retMsg = new RetMsg();
        String str = request.getParameter("businessKey");
        String processInstanceId = request.getParameter("processInstanceId");
        request.setAttribute("processInstanceId", StringUtils.isNotEmpty(processInstanceId) ? processInstanceId : "");
        String[] key = null;
        if (!StringUtils.isEmpty(str)) {
            if (str.indexOf(",") > -1) {
                if (str.endsWith(",")) {
                    key = str.substring(0, str.lastIndexOf(",")).split(",");
                } else {
                    key = str.split(",");
                }
            }
            if (null != key) {
                if (key.length >= 2) {
                    if (key[1].equals("null")) {
                        String bizType = key[3];
                        String bizId = key[4];
                        String taskId = request.getParameter("taskId");
                        request.setAttribute("bizType", bizType);
                        request.setAttribute("bizId", bizId);
                        request.setAttribute("taskId", StringUtils.isNotEmpty(taskId) ? taskId : "");
                        if (StringUtils.isEmpty(processInstanceId) && StringUtils.isNotEmpty(taskId)) {
                            processInstanceId =
                                taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
                        }
                        retMsg = getFixedFormData(request, processInstanceId, taskId, bizId, bizType);
                    } else {
                        // 流程ID
                        String bpmnId = request.getParameter("bpmnId");
                        // 任务key
                        String taskDefKey = request.getParameter("taskDefKey");
                        // 当前活动任务id
                        String taskId = request.getParameter("taskId");

                        if (StringUtils.isNotEmpty(bpmnId)) {
                            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
                            if (null == task) {
                                retMsg.setCode(AppConstant.RET_CODE_ERROR);
                                retMsg.setMessage("该笔工作已被撤回!");
                                return retMsg;
                            }
                            retMsg = getAppWaitTaskInfo(request, bpmnId, taskDefKey, taskId, currentUserId, userName);

                        } else {
                            List<ProcessInstance> list =
                                runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).list();
                            if (list != null && list.size() > 0) {
                                retMsg =
                                    getAppDoingTaskInfo(request, processInstanceId, taskId, currentUserId, userName);
                            } else {
                                retMsg.setCode(AppConstant.RET_CODE_ERROR);
                                retMsg.setMessage("该笔工作已经归档结束!");
                                Where<ActAljoinQueryHis> hisWhere = new Where<ActAljoinQueryHis>();
                                hisWhere.eq("process_instance_id", processInstanceId);
                                ActAljoinQueryHis his = actAljoinQueryHisService.selectOne(hisWhere);
                                if (his == null) {
                                    retMsg.setMessage("该笔工作已经被删除!");
                                }
                                return retMsg;
                            }
                        }
                    }
                }
            }
        }
        return retMsg;
    }

    /**
     * 获取待办任务详情.
     * 
     * @return：void
     * @author：wangj @date：2017-12-20
     */
    private RetMsg getAppWaitTaskInfo(HttpServletRequest request, String bpmnId, String taskDefKey, String taskId,
        Long currentUserId, String userName) throws Exception {
        // 根据bpmnRun_id获取bpmn_id
        ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(bpmnId);
        RetMsg retMsg = new RetMsg();
        Map<String, Object> map = new HashMap<String, Object>();
        // 根据流程ID获取运行时的全局表单ID
        Where<ActAljoinBpmnForm> actAljoinBpmnFormWhere = new Where<ActAljoinBpmnForm>();
        actAljoinBpmnFormWhere.eq("bpmn_id", bpmnRun.getOrgnlId());
        actAljoinBpmnFormWhere.eq("element_type", "StartEvent");
        ActAljoinBpmnForm actAljoinBpmnForm = actAljoinBpmnFormService.selectOne(actAljoinBpmnFormWhere);
        ActAljoinFormRun actAljoinFormRun = actAljoinFormRunService.selectById(actAljoinBpmnForm.getFormId());

        if (actAljoinFormRun == null) {
            throw new Exception("该流程没有表单");
        }

        // 根据实例ID查询出 表单运行时控件对应的数据
        List<ActAljoinFormDataRun> runDataList = new ArrayList<ActAljoinFormDataRun>();
        if (!StringUtils.isEmpty(taskId)) {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (null != task) {
                String instanceId = task.getProcessInstanceId();
                Where<ActAljoinFormDataRun> dataRunWhere = new Where<ActAljoinFormDataRun>();
                dataRunWhere.eq("proc_inst_id", instanceId);
                runDataList = actAljoinFormDataRunService.selectList(dataRunWhere);
            }
        }

        List<ActAljoinFormDataDraft> dataDraftList = new ArrayList<ActAljoinFormDataDraft>();
        if (!StringUtils.isEmpty(taskId)) {
            String instanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
            taskDefKey = taskService.createTaskQuery().taskId(taskId).singleResult().getTaskDefinitionKey();
            Where<ActAljoinFormDataDraft> dataDraftWhere = new Where<ActAljoinFormDataDraft>();
            dataDraftWhere.eq("proc_inst_id", instanceId);
            dataDraftWhere.eq("proc_task_id", taskDefKey);
            dataDraftWhere.eq("create_user_id", currentUserId);
            dataDraftList = actAljoinFormDataDraftService.selectList(dataDraftWhere);
        }

        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String signInTime = request.getParameter("signInTime");
        paramMap.put("bpmnId", bpmnRun.getOrgnlId());
        paramMap.put("formId", actAljoinBpmnForm.getFormId());
        paramMap.put("taskId", taskId);
        paramMap.put("signInTime", signInTime);
        paramMap.put("currentTime", DateUtil.datetime2str(new Date()));

        if (StringUtils.isEmpty(taskDefKey)) {
            // 获取第一个任务节点ID
            ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(bpmnRun.getProcessId()).latestVersion().singleResult();
            BpmnModel model = repositoryService.getBpmnModel(definition.getId());
            Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
            for (FlowElement e : flowElements) {
                if (e instanceof StartEvent) {
                    StartEvent startEvent = (StartEvent)e;
                    taskDefKey = startEvent.getOutgoingFlows().get(0).getTargetRef();
                    break;
                }
            }
        }
        if (StringUtils.isEmpty(taskDefKey)) {
            throw new Exception("无用户任务节点");
        }

        // 根据流程id获取表单权限，并根据权限显示表单
        Where<ActAljoinTaskAssignee> actAljoinTaskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
        actAljoinTaskAssigneeWhere.eq("bpmn_id", bpmnRun.getOrgnlId());
        actAljoinTaskAssigneeWhere.eq("version", bpmnRun.getTaskAssigneeVersion());
        actAljoinTaskAssigneeWhere.eq("task_id", taskDefKey);
        ActAljoinTaskAssignee actAljoinTaskAssignee =
            actAljoinTaskAssigneeService.selectOne(actAljoinTaskAssigneeWhere);

        paramMap.put("showWidgetIds", actAljoinTaskAssignee.getShowWidgetIds());
        paramMap.put("editWidgetIds", actAljoinTaskAssignee.getEditWidgetIds());
        paramMap.put("notNullWidgetIds", actAljoinTaskAssignee.getNotNullWidgetIds());
        paramMap.put("commentWidgetIds", actAljoinTaskAssignee.getCommentWidgetIds());
        paramMap.put("operateAuthIds", actAljoinTaskAssignee.getOperateAuthIds());
        paramMap.put("redHeadWidgetIds", actAljoinTaskAssignee.getRedHeadWidgetIds());
        paramMap.put("saveMarkWidgetIds", actAljoinTaskAssignee.getSaveMarkWidgetIds());
        mapList.add(paramMap);
        Where<ActAljoinFormWidgetRun> actAljoinFormWidgetRunWhere = new Where<ActAljoinFormWidgetRun>();
        actAljoinFormWidgetRunWhere.eq("form_id", actAljoinFormRun.getOrgnlId());
        List<ActAljoinFormWidgetRun> actAljoinFormWidgetRunList =
            actAljoinFormWidgetRunService.selectList(actAljoinFormWidgetRunWhere);

        List<ActAljoinFormWidgetRun> formWidgetRunList = new ArrayList<ActAljoinFormWidgetRun>();

        if (null != actAljoinFormWidgetRunList && !actAljoinFormWidgetRunList.isEmpty()) {
            // 解析HTMLCODE 编码
            byte[] byteKey = actAljoinFormRun.getHtmlCode().getBytes();
            byte[] encodeKey = Base64.decode(byteKey);
            String retKey = new String(encodeKey);
            // 根据HtmlCode解析控件
            Document document = Jsoup.parse(retKey);
            Elements elements = document.getElementsByClass("grid-stack-item");
            List<Map.Entry<String, Double>> list = parseHtmlCode(elements);
            List<String> showWidgetIdList = Arrays.asList(actAljoinTaskAssignee.getShowWidgetIds().split(","));
            // 存放 运行时控件 用于比对控件值是否需要解码
            Map<String, ActAljoinFormWidgetRun> formWidgetMap = new HashMap<String, ActAljoinFormWidgetRun>();
            // 表单控件按顺序 存放到list
            for (Map.Entry<String, Double> mapping : list) {
                for (ActAljoinFormWidgetRun widgetRun : actAljoinFormWidgetRunList) {
                    formWidgetMap.put(widgetRun.getWidgetId(), widgetRun);
                    for (String showWidgetId : showWidgetIdList) {
                        if (showWidgetId.equals(widgetRun.getWidgetId())
                            && mapping.getKey().equals(widgetRun.getWidgetId())
                            && actAljoinFormRun.getVersion() == widgetRun.getVersion()) {
                            formWidgetRunList.add(widgetRun);
                        }
                    }
                    /*
                     * if(mapping.getKey().equals(widgetRun.getWidgetId()) &&
                     * actAljoinFormRun.getVersion() == widgetRun.getVersion()){
                     * formWidgetRunList.add(widgetRun); }
                     */
                }
            }

            // 将控件数据 按控件ID存放到 map
            /*
             * Map<String, Object> dataMap = new HashMap<String, Object>(); for
             * (ActAljoinFormDataRun dataRun : runDataList) {
             * dataMap.put(dataRun.getFormWidgetId(), dataRun); }
             */

            // 如果当前节点草稿箱有数据则读取草稿箱数据
            if (dataDraftList.size() > 0) {
                request.setAttribute("runDataList", dataDraftList);
                for (ActAljoinFormDataDraft actAljoinFormDataRun : dataDraftList) {
                    bulidFormWidgetValue(actAljoinFormDataRun, formWidgetMap, document, currentUserId, userName);
                }
                map.put("dataObj", dataDraftList);
            } else {
                // 查询运行时附件
                for (ActAljoinFormDataRun actAljoinFormDataRun : runDataList) {
                    bulidFormWidgetValue(actAljoinFormDataRun, formWidgetMap, document, currentUserId, userName);
                }
                map.put("dataObj", runDataList);
            }

        }
        map.put("formWidgetList", formWidgetRunList);
        map.put("paramList", mapList);
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setMessage("操作成功");
        retMsg.setObject(map);
        return retMsg;
    }

    /**
     * 获取在办任务详情.
     * 
     * @return：void
     * @author：wangj @date：2017-11-28
     */
    private RetMsg getAppDoingTaskInfo(HttpServletRequest request, String processInstanceId, String taskId,
        Long currentUserId, String userName) throws Exception {
        RetMsg retMsg = new RetMsg();
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (StringUtils.isNotEmpty(processInstanceId) && StringUtils.isNotEmpty(taskId)) {
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId)
                .orderByTaskCreateTime().desc().list();
            Task task = taskList.get(0);

            paramMap.put("taskId", task.getId());

            Where<ActAljoinFormDataRun> dataRunWhere = new Where<ActAljoinFormDataRun>();
            dataRunWhere.eq("proc_inst_id", processInstanceId);
            List<ActAljoinFormDataRun> runDataList = actAljoinFormDataRunService.selectList(dataRunWhere);

            String formId = "";
            String bpmnId = "";
            String taskDefKey = "";
            if (null != runDataList && !runDataList.isEmpty()) {
                ActAljoinFormDataRun dataRun = runDataList.get(0);
                if (null != dataRun) {
                    bpmnId = String.valueOf(dataRun.getBpmnId());
                    formId = String.valueOf(dataRun.getFormId());
                    ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(bpmnId);
                    ActAljoinFormRun actAljoinFormRun = actAljoinFormRunService.selectById(Long.valueOf(formId));

                    if (actAljoinFormRun == null) {
                        throw new Exception("该流程没有表单");
                    }
                    paramMap.put("bpmnId", bpmnId);
                    paramMap.put("formId", formId);
                    paramMap.put("currentTime", DateUtil.datetime2str(new Date()));
                    if (StringUtils.isEmpty(taskDefKey)) {
                        // 获取第一个任务节点ID
                        // ActAljoinBpmn bpmn = actAljoinBpmnService.selectById(bpmnId);
                        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                            .processDefinitionKey(bpmnRun.getProcessId()).latestVersion().singleResult();
                        BpmnModel model = repositoryService.getBpmnModel(definition.getId());
                        Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
                        for (FlowElement e : flowElements) {
                            if (e instanceof UserTask) {
                                UserTask userTask = (UserTask)e;
                                String targetDef = userTask.getOutgoingFlows().get(0).getTargetRef();
                                if (targetDef.indexOf("ExclusiveGateway") < 0) {
                                    taskDefKey = userTask.getOutgoingFlows().get(0).getSourceRef();
                                    break;
                                }
                            }
                        }
                    }

                    Where<ActAljoinFormDataDraft> dataDraftWhere = new Where<ActAljoinFormDataDraft>();
                    // String taskKey =
                    // taskService.createTaskQuery().taskId(taskId).singleResult().getTaskDefinitionKey();
                    dataDraftWhere.eq("proc_inst_id", processInstanceId);
                    dataDraftWhere.eq("proc_task_id", taskDefKey);
                    List<ActAljoinFormDataDraft> dataDraftList =
                        actAljoinFormDataDraftService.selectList(dataDraftWhere);

                    if (StringUtils.isEmpty(taskDefKey)) {
                        throw new Exception("无用户任务节点");
                    }

                    // 根据流程id获取表单权限，并根据权限显示表单
                    Where<ActAljoinTaskAssignee> actAljoinTaskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
                    actAljoinTaskAssigneeWhere.eq("bpmn_id", bpmnRun.getOrgnlId());
                    actAljoinTaskAssigneeWhere.eq("version", bpmnRun.getTaskAssigneeVersion());
                    actAljoinTaskAssigneeWhere.eq("task_id", taskDefKey);
                    ActAljoinTaskAssignee actAljoinTaskAssignee =
                        actAljoinTaskAssigneeService.selectOne(actAljoinTaskAssigneeWhere);

                    if (null != actAljoinTaskAssignee) {
                        paramMap.put("showWidgetIds", actAljoinTaskAssignee.getShowWidgetIds());
                        paramMap.put("editWidgetIds", actAljoinTaskAssignee.getEditWidgetIds());
                        paramMap.put("notNullWidgetIds", actAljoinTaskAssignee.getNotNullWidgetIds());
                        paramMap.put("commentWidgetIds", actAljoinTaskAssignee.getCommentWidgetIds());
                        paramMap.put("operateAuthIds", actAljoinTaskAssignee.getOperateAuthIds());
                    }

                    List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
                    mapList.add(paramMap);
                    map.put("paramList", mapList);

                    Where<ActAljoinFormWidgetRun> actAljoinFormWidgetRunWhere = new Where<ActAljoinFormWidgetRun>();
                    actAljoinFormWidgetRunWhere.eq("form_id", actAljoinFormRun.getOrgnlId());
                    List<ActAljoinFormWidgetRun> actAljoinFormWidgetRunList =
                        actAljoinFormWidgetRunService.selectList(actAljoinFormWidgetRunWhere);

                    List<ActAljoinFormWidgetRun> formWidgetRunList = new ArrayList<ActAljoinFormWidgetRun>();
                    if (null != actAljoinFormWidgetRunList && !actAljoinFormWidgetRunList.isEmpty()) {
                        // 解析HTMLCODE 编码
                        byte[] byteKey = actAljoinFormRun.getHtmlCode().getBytes();
                        byte[] encodeKey = Base64.decode(byteKey);
                        String retKey = new String(encodeKey);
                        // 根据HtmlCode解析控件
                        Document document = Jsoup.parse(retKey);
                        Elements elements = document.getElementsByClass("grid-stack-item");
                        List<Map.Entry<String, Double>> list = parseHtmlCode(elements);
                        List<String> showWidgetIdList =
                            Arrays.asList(actAljoinTaskAssignee.getShowWidgetIds().split(","));
                        // 存放 运行时控件 用于比对控件值是否需要解码
                        Map<String, ActAljoinFormWidgetRun> formWidgetMap =
                            new HashMap<String, ActAljoinFormWidgetRun>();
                        // 表单控件按顺序 存放到list
                        for (Map.Entry<String, Double> mapping : list) {
                            for (ActAljoinFormWidgetRun widgetRun : actAljoinFormWidgetRunList) {
                                formWidgetMap.put(widgetRun.getWidgetId(), widgetRun);
                                for (String showWidgetId : showWidgetIdList) {
                                    if (showWidgetId.equals(widgetRun.getWidgetId())
                                        && mapping.getKey().equals(widgetRun.getWidgetId())
                                        && actAljoinFormRun.getVersion() == widgetRun.getVersion()) {
                                        formWidgetRunList.add(widgetRun);
                                    }
                                }
                            }
                        }

                        map.put("formWidgetList", formWidgetRunList);

                        // 将控件数据 按控件ID存放到 map
                        /*
                         * Map<String, Object> dataMap = new HashMap<String,
                         * Object>(); for (ActAljoinFormDataRun formDataRun :
                         * runDataList) {
                         * dataMap.put(formDataRun.getFormWidgetId(),
                         * formDataRun); }
                         */
                        // 如果当前节点草稿箱有数据则读取草稿箱数据
                        if (dataDraftList.size() > 0) {
                            // 查询草稿附件
                            for (ActAljoinFormDataDraft actAljoinFormDataRun : dataDraftList) {
                                bulidFormWidgetValue(actAljoinFormDataRun, formWidgetMap, document, currentUserId,
                                    userName);
                            }
                            map.put("dataObj", dataDraftList);
                        } else {
                            // 查询运行时附件
                            for (ActAljoinFormDataRun actAljoinFormDataRun : runDataList) {
                                bulidFormWidgetValue(actAljoinFormDataRun, formWidgetMap, document, currentUserId,
                                    userName);
                            }
                            map.put("dataObj", runDataList);
                        }

                    }
                }
            }
        }
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setMessage("操作成功");
        retMsg.setObject(map);
        return retMsg;
    }

    /**
     * 固定流程获取在办,待办任务详情.
     * 
     * @return：void
     * @author：wangj @date：2017-12-20
     */
    private RetMsg getFixedFormData(HttpServletRequest request, String processInstanceId, String taskId, String bizId,
        String bizType) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (StringUtils.isNotEmpty(bizType) && StringUtils.isNotEmpty(bizId)) {
            if (bizType.equals(AppConstant.FIXED_BIZ_TYPE_ATT)) {// 考勤补签
                AppAttSignInOutHisVO signInOutHisVO = new AppAttSignInOutHisVO();
                if (StringUtils.isNotEmpty(taskId)) {
                    signInOutHisVO.setTaskId(taskId);
                }
                if (StringUtils.isNotEmpty(processInstanceId)) {
                    signInOutHisVO.setProcessInstanceId(processInstanceId);
                }
                signInOutHisVO.setIds(bizId);
                retMsg = attSignInOutService.getAppSignInOutHisPatchList(signInOutHisVO);
            } else if (bizType.equals(AppConstant.FIXED_BIZ_TYPE_MEE)) {// 外部会议
                AppMeeOutsideMeetingVO meeOutsideMeeting = new AppMeeOutsideMeetingVO();
                meeOutsideMeeting.setId(Long.valueOf(bizId));
                if (StringUtils.isNotEmpty(taskId)) {
                    meeOutsideMeeting.setTaskId(taskId);
                }
                retMsg = meeOutsideMeetingService.meeDetail(meeOutsideMeeting);
            } else if (bizType.equals(AppConstant.FIXED_BIZ_TYPE_OFF)) {// 月报
                AppOffMonthReportVO offMonthReport = new AppOffMonthReportVO();
                if (StringUtils.isNotEmpty(taskId)) {
                    offMonthReport.setTaskId(taskId);
                }
                offMonthReport.setId(Long.valueOf(bizId));
                retMsg = offMonthReportService.offDetail(offMonthReport);
            } else if (bizType.equals(AppConstant.FIXED_BIZ_TYPE_PUB)) {// 公共信息
                AppPubPublicInfo publicInfo = new AppPubPublicInfo();
                publicInfo.setId(Long.valueOf(bizId));
                if (StringUtils.isNotEmpty(taskId)) {
                    publicInfo.setTaskId(taskId);
                }
                retMsg = pubPublicInfoService.pubDetail(publicInfo);
            } else if (bizType.equals(AppConstant.FIXED_BIZ_TYPE_VEH)) {// 车船申请
                AppVehInfoVO info = new AppVehInfoVO();
                info.setId(Long.valueOf(bizId));
                if (StringUtils.isNotEmpty(taskId)) {
                    info.setTaskId(taskId);
                }
                retMsg = vehUseService.appDetail(info);
            } else if (bizType.equals(AppConstant.FIXED_BIZ_TYPE_GOO)) {// 办公用品出入库
                AppGooInOutVO appGooInOutVO = new AppGooInOutVO();
                if (StringUtils.isNotEmpty(taskId)) {
                    appGooInOutVO.setTaskId(taskId);
                }
                if (StringUtils.isNotEmpty(processInstanceId)) {
                    appGooInOutVO.setProcessInstanceId(processInstanceId);
                }
                appGooInOutVO.setIds(bizId);
                retMsg = gooInOutService.getInOutList(appGooInOutVO);
            } else if (bizType.equals(AppConstant.FIXED_BIZ_TYPE_PUR)) {// 办公用品申购
                AppGooPurchaseVO appGooInOutVO = new AppGooPurchaseVO();
                if (StringUtils.isNotEmpty(taskId)) {
                    appGooInOutVO.setTaskId(taskId);
                }
                if (StringUtils.isNotEmpty(processInstanceId)) {
                    appGooInOutVO.setProcessInstanceId(processInstanceId);
                }
                appGooInOutVO.setIds(bizId);
                retMsg = gooPurchaseService.getInOutList(appGooInOutVO);
            }
        }
        return retMsg;
    }

    public List<Map.Entry<String, Double>> parseHtmlCode(Elements elements) {
        Iterator<Element> it = elements.iterator();
        TreeMap<String, Double> positionTreeMap = new TreeMap<String, Double>();

        while (it.hasNext()) {
            String element = it.next().toString();
            Document doc = Jsoup.parse(element);
            Element div = doc.getElementsByClass("grid-stack-item").get(0);
            String x = div.attr("data-gs-x");
            String y = div.attr("data-gs-y");
            String widgetId = "";
            String widgetType = div.children().attr("type");

            // 根据控件类型 获得控件ID
            if (widgetType.equals("radio") || widgetType.equals("checkbox") || widgetType.equals("contact_select")
                || widgetType.equals("contact_select_three") || widgetType.equals("contact_select_four")
                || widgetType.equals("contact_select_five") || widgetType.equals("date_period")
                || widgetType.equals("datetime_period")) {
                widgetId = div.select("input[type=hidden]").get(0).val();
            } else if (widgetType.equals("area_text")) {
                widgetId = div.select("textarea").get(0).attr("id");
            } else if (widgetType.equals("select")) {
                widgetId = div.select("select").get(0).attr("id");
            } else if (widgetType.equals("date") || widgetType.equals("time") || widgetType.equals("date_period")
                || widgetType.equals("editor") || widgetType.equals("datetime") || widgetType.equals("href")
                || widgetType.equals("biz_finish_time")) {
                widgetId = div.children().select("input").get(0).attr("id");
            } else {
                widgetId = div.children().select("input").get(0).attr("id");
            }
            // widgetType.equals("date_period") widgetType.equals("radio")
            // widgetType.equals("input")
            positionTreeMap.put(widgetId, Double.parseDouble((y + "." + x)));
        }
        // positionTreeMap 转成List
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(positionTreeMap.entrySet());

        // 通过比较器进行排序
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        return list;
    }

    @SuppressWarnings("unused")
    private List<Object> getAllSource(Long currentUserId, String userName, String type) throws Exception {
        Map<String, String> paramMap = new HashMap<String, String>();
        // 查询用户所在部门
        Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
        where.eq("user_id", currentUserId);
        where.setSqlSelect("dept_id");
        where.groupBy("dept_id");
        List<AutDepartmentUser> autDepartmentUserList = autDepartmentUserService.selectList(where);

        List<String> deptIds = new ArrayList<String>();
        for (AutDepartmentUser autDepartmentUser : autDepartmentUserList) {
            deptIds.add(autDepartmentUser.getDeptId().toString());
        }
        // 查询部门名称
        if (deptIds.size() > 0) {
            List<AutDepartment> autDepartmentList = autDepartmentService.selectBatchIds(deptIds);
            String deptNames = "";
            for (AutDepartment autDepartment : autDepartmentList) {
                deptNames = autDepartment.getDeptName() + "," + deptNames;
            }
            if (StringUtils.isNotEmpty(deptNames)) {
                deptNames = deptNames.substring(0, deptNames.length() - 1);
                paramMap.put("deptName", deptNames);
            }
        }
        paramMap.put("userName", userName);
        return actAljoinDataSourceService.getAllSource(type, paramMap);
    }

    private void bulidFormWidgetValue(ActAljoinFormDataRun actAljoinFormDataRun,
        Map<String, ActAljoinFormWidgetRun> formWidgetMap, Document document, Long currentUserId, String userName)
        throws Exception {
        ActAljoinFormWidgetRun actAljoinFormWidgetRun = formWidgetMap.get(actAljoinFormDataRun.getFormWidgetId());
        String widgetType = actAljoinFormWidgetRun.getWidgetType();
        if (widgetType.equals("area_text") || widgetType.equals("biz_title")) {// 单行文本,多行文本,标题
                                                                               // encodeURIComponent
            String formWidgetValue = actAljoinFormDataRun.getFormWidgetValue();
            formWidgetValue = formWidgetValue.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            formWidgetValue = java.net.URLDecoder.decode(formWidgetValue, "UTF-8");
            actAljoinFormDataRun.setFormWidgetValue(formWidgetValue);
        } else if (widgetType.equals("datetime_period") || widgetType.equals("date_period")) {
            String formWidgetValueStr = actAljoinFormDataRun.getFormWidgetValue();
            List<String> formWidgetValueList = new ArrayList<String>();
            if (formWidgetValueStr.indexOf(",") > -1) {
                formWidgetValueList = Arrays.asList(formWidgetValueStr.split(","));
            }
            String str = "";
            int i = 0;
            for (String value : formWidgetValueList) {
                byte[] formWidgetValue = value.getBytes();
                byte[] encodeValue = Base64.decode(formWidgetValue);
                String decodeValue = new String(encodeValue);
                if (i == 0) {
                    str = "自 " + decodeValue;
                } else {
                    str += " 至 " + decodeValue;
                }
                i++;
            }
            actAljoinFormDataRun.setFormWidgetValue(str);
        } else if (widgetType.equals("text")) {
            Element element = document.getElementById(actAljoinFormDataRun.getFormWidgetId());
            @SuppressWarnings("unused")
            String dataSourceType = element.parent().attr("aljoin-data-source");
            String value = "";
            /*
             * if(dataSourceType.equals("2")){//等于2 调用数据源 String type =
             * element.parent().attr("aljoin-sys-data-source"); List<Object>
             * sourceList = getAllSource(currentUserId,userName,type);
             * List<String> userNameList = new ArrayList<String>(); for(Object
             * obj : sourceList){ String val = ((TextSource) obj).getValue();
             * if(actAljoinFormDataRun.getFormWidgetValue().equals(val)){
             * userNameList.add(val); } } if(type.equals("TextLoginPerson")){
             *//*
                * Where<AutUser> autUserWhere = new Where<AutUser>();
                * autUserWhere.setSqlSelect("id,user_name,full_name");
                * //autUserWhere.eq("id",Long.valueOf(actAljoinFormDataRun.
                * getFormWidgetValue()));
                * autUserWhere.in("user_name",userNameList); List<AutUser>
                * userList = autUserService.selectList(autUserWhere); String
                * fullName = ""; for(AutUser user : userList){ fullName =
                * user.getFullName(); }
                *//*
                  * value = actAljoinFormDataRun.getFormWidgetValue(); }else{
                  * String str = ""; for(Object obj : sourceList){ String val =
                  * ((TextSource) obj).getValue();
                  * if(actAljoinFormDataRun.getFormWidgetValue().equals(val)){
                  * str += val + ","; } } value = str; } }else{
                  */
            // value =
            // java.net.URLDecoder.decode(actAljoinFormDataRun.getFormWidgetValue(),"UTF-8");
            String formWidgetValue = actAljoinFormDataRun.getFormWidgetValue();
            formWidgetValue = formWidgetValue.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            value = java.net.URLDecoder.decode(formWidgetValue, "UTF-8");
            // value = actAljoinFormDataRun.getFormWidgetValue();
            // }
            actAljoinFormDataRun.setFormWidgetValue(value);
        } else if (widgetType.equals("select")) {
            Element element = document.getElementById(actAljoinFormDataRun.getFormWidgetId());
            @SuppressWarnings("unused")
            String dataSourceType = element.parent().attr("aljoin-data-source");
            String value = "";
            /*
             * if(dataSourceType.equals("2")){//等于2 调用数据源 String type =
             * element.parent().attr("aljoin-sys-data-source"); List<Object>
             * sourceList = getAllSource(currentUserId,userName,type); //value =
             * JSONArray.toJSON(sourceList).toString(); String str = "";
             * for(Object obj : sourceList){ String val = ((CheckBoxSource)
             * obj).getValue(); String text = ((CheckBoxSource) obj).getText();
             * if(actAljoinFormDataRun.getFormWidgetValue().equals(val)){ str +=
             * text; } } value = str; }else{
             */
            Elements elements = element.select("option");
            Iterator<Element> iterator = elements.iterator();
            String str = "";
            while (iterator.hasNext()) {
                String ele = iterator.next().toString();
                Document doc = Jsoup.parse(ele);
                Element elmt = doc.select("option").get(0);
                String val = elmt.val();
                if (val.equals(actAljoinFormDataRun.getFormWidgetValue())) {
                    str = elmt.text();
                }
            }
            value = str;
            // }
            actAljoinFormDataRun.setFormWidgetValue(value);
        } else if (widgetType.equals("checkbox")) {
            Element element = document.select("input[value=" + actAljoinFormDataRun.getFormWidgetId() + "]").get(0);
            // String dataSourceType = element.parent().attr("aljoin-data-source");
            String value = "";
            String[] values = actAljoinFormDataRun.getFormWidgetValue().split(",");
            Elements elements = element.parent().children().select("label");
            Iterator<Element> it = elements.iterator();
            Map<String, String> checkBoxMap = new HashMap<String, String>();
            while (it.hasNext()) {
                String ele = it.next().toString();
                Document doc = Jsoup.parse(ele);
                String val = doc.select("input").get(0).val();
                if (StringUtils.isNotEmpty(val)) {
                    String str = doc.select("span").get(0).text();
                    checkBoxMap.put(val, str);
                }
            }
            String str = "";
            for (String val : values) {
                byte[] formWidgetValue = val.getBytes();
                byte[] encodeValue = Base64.decode(formWidgetValue);
                String decodeValue = new String(encodeValue);
                for (String key : checkBoxMap.keySet()) {
                    if (decodeValue.equals(key)) {
                        str += checkBoxMap.get(key) + ",";
                    }
                }
            }
            value = str;
            // }
            actAljoinFormDataRun.setFormWidgetValue(value);
        } else if (widgetType.equals("radio")) {
            // Element element = document.select("[name=" + actAljoinFormDataRun.getFormWidgetId() + "]").get(0);
            // String dataSourceType = element.parent().attr("aljoin-data-source");
            String value = "";
            value = actAljoinFormDataRun.getFormWidgetValue().equals("0") ? "女" : "男";
            actAljoinFormDataRun.setFormWidgetValue(value);
        } else if (widgetType.equals("image") || widgetType.equals("attach")) {
            String decodeValue = "";
            if (actAljoinFormDataRun.getFormWidgetValue().indexOf("[]") < 0) {
                byte[] formWidgetValue = actAljoinFormDataRun.getFormWidgetValue().getBytes();
                byte[] encodeValue = Base64.decode(formWidgetValue);
                encodeValue = Base64.decode(encodeValue);
                decodeValue = new String(encodeValue);
            }
            actAljoinFormDataRun.setFormWidgetValue(decodeValue);
        } else if (widgetType.equals("biz_text_office")) {
            String attachment = "";
            if (actAljoinFormDataRun.getFormWidgetValue().indexOf("[]") < 0) {
                Long attmentId = StringUtils.isNotEmpty(actAljoinFormDataRun.getFormWidgetValue())
                    ? Long.valueOf(actAljoinFormDataRun.getFormWidgetValue()) : 0;
                Where<ResResource> where = new Where<ResResource>();
                where.setSqlSelect("id,orgnl_file_name,group_name,file_name");
                where.eq("id", attmentId);
                ResResource resResource = resResourceService.selectOne(where);

                if (null != resResource) {
                    Map<String, String> attMap = new HashMap<String, String>();
                    attMap.put("dalWorkId", String.valueOf(resResource.getId()));
                    attMap.put("name", resResource.getOrgnlFileName());
                    attMap.put("src", "/res/file/download?groupName="+resResource.getGroupName()+"&fileName="+resResource.getFileName());
                    attMap.put("groupName", resResource.getGroupName());
                    attMap.put("fileName", resResource.getFileName());
                    List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
                    mapList.add(attMap);
                    attachment = JSONArray.toJSON(mapList).toString();
                }
            }
            actAljoinFormDataRun.setFormWidgetValue(attachment);
        }
    }

    private void bulidFormWidgetValue(ActAljoinFormDataDraft actAljoinFormDataRun,
        Map<String, ActAljoinFormWidgetRun> formWidgetMap, Document document, Long currentUserId, String userName)
        throws Exception {
        ActAljoinFormWidgetRun actAljoinFormWidgetRun = formWidgetMap.get(actAljoinFormDataRun.getFormWidgetId());
        String widgetType = actAljoinFormWidgetRun.getWidgetType();
        if (widgetType.equals("area_text") || widgetType.equals("biz_title")) {// 单行文本,多行文本,标题
                                                                               // encodeURIComponent
            String formWidgetValue = actAljoinFormDataRun.getFormWidgetValue();
            formWidgetValue = formWidgetValue.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            formWidgetValue = java.net.URLDecoder.decode(formWidgetValue, "UTF-8");
            actAljoinFormDataRun.setFormWidgetValue(formWidgetValue);
        } else if (widgetType.equals("datetime_period") || widgetType.equals("date_period")) {
            String formWidgetValueStr = actAljoinFormDataRun.getFormWidgetValue();
            List<String> formWidgetValueList = new ArrayList<String>();
            if (formWidgetValueStr.indexOf(",") > -1) {
                formWidgetValueList = Arrays.asList(formWidgetValueStr.split(","));
            }
            String str = "";
            int i = 0;
            for (String value : formWidgetValueList) {
                byte[] formWidgetValue = value.getBytes();
                byte[] encodeValue = Base64.decode(formWidgetValue);
                String decodeValue = new String(encodeValue);
                if (i == 0) {
                    str = "自 " + decodeValue;
                } else {
                    str += " 至 " + decodeValue;
                }
                i++;
            }
            actAljoinFormDataRun.setFormWidgetValue(str);
        } else if (widgetType.equals("text")) {
            // Element element = document.getElementById(actAljoinFormDataRun.getFormWidgetId());
            // String dataSourceType = element.parent().attr("aljoin-data-source");
            String value = "";
            String formWidgetValue = actAljoinFormDataRun.getFormWidgetValue();
            formWidgetValue = formWidgetValue.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            value = java.net.URLDecoder.decode(formWidgetValue, "UTF-8");
            actAljoinFormDataRun.setFormWidgetValue(value);
        } else if (widgetType.equals("select")) {
            Element element = document.getElementById(actAljoinFormDataRun.getFormWidgetId());
            // String dataSourceType = element.parent().attr("aljoin-data-source");
            String value = "";
            /*
             * if(dataSourceType.equals("2")){//等于2 调用数据源 String type =
             * element.parent().attr("aljoin-sys-data-source"); List<Object>
             * sourceList = getAllSource(currentUserId,userName,type); //value =
             * JSONArray.toJSON(sourceList).toString(); String str = "";
             * for(Object obj : sourceList){ String val = ((CheckBoxSource)
             * obj).getValue(); String text = ((CheckBoxSource) obj).getText();
             * if(actAljoinFormDataRun.getFormWidgetValue().equals(val)){ str +=
             * text; } } value = str; }else{
             */
            Elements elements = element.select("option");
            Iterator<Element> iterator = elements.iterator();
            String str = "";
            while (iterator.hasNext()) {
                String ele = iterator.next().toString();
                Document doc = Jsoup.parse(ele);
                Element elmt = doc.select("option").get(0);
                String val = elmt.val();
                if (val.equals(actAljoinFormDataRun.getFormWidgetValue())) {
                    str = elmt.text();
                }
            }
            value = str;
            // }
            actAljoinFormDataRun.setFormWidgetValue(value);
        } else if (widgetType.equals("checkbox")) {
            Element element = document.select("input[value=" + actAljoinFormDataRun.getFormWidgetId() + "]").get(0);
            // String dataSourceType = element.parent().attr("aljoin-data-source");
            String value = "";
            /*
             * if(dataSourceType.equals("2")){//等于2 调用数据源 String type =
             * element.parent().attr("aljoin-sys-data-source"); List<Object>
             * sourceList = getAllSource(currentUserId,userName,type); String
             * str = ""; String[] values =
             * actAljoinFormDataRun.getFormWidgetValue().split(","); for(Object
             * obj : sourceList){ for(String val : values){ byte[]
             * formWidgetValue = val.getBytes(); byte[] encodeValue =
             * Base64.decode(formWidgetValue); String decodeValue = new
             * String(encodeValue); if(decodeValue.equals(((CheckBoxSource)
             * obj).getValue())){ str += ((CheckBoxSource) obj).getText() + ",";
             * } } } value = str; }else{
             */
            String[] values = actAljoinFormDataRun.getFormWidgetValue().split(",");
            Elements elements = element.parent().children().select("label");
            Iterator<Element> it = elements.iterator();
            Map<String, String> checkBoxMap = new HashMap<String, String>();
            while (it.hasNext()) {
                String ele = it.next().toString();
                Document doc = Jsoup.parse(ele);
                String val = doc.select("input").get(0).val();
                if (StringUtils.isNotEmpty(val)) {
                    String str = doc.select("span").get(0).text();
                    checkBoxMap.put(val, str);
                }
            }
            String str = "";
            for (String val : values) {
                byte[] formWidgetValue = val.getBytes();
                byte[] encodeValue = Base64.decode(formWidgetValue);
                String decodeValue = new String(encodeValue);
                for (String key : checkBoxMap.keySet()) {
                    if (decodeValue.equals(key)) {
                        str += checkBoxMap.get(key) + ",";
                    }
                }
            }
            value = str;
            // }
            actAljoinFormDataRun.setFormWidgetValue(value);
        } else if (widgetType.equals("radio")) {
            // Element element = document.select("[name=" + actAljoinFormDataRun.getFormWidgetId() + "]").get(0);
            // String dataSourceType = element.parent().attr("aljoin-data-source");
            String value = "";
            /*
             * if(dataSourceType.equals("2")){//等于2 调用数据源 String type =
             * element.parent().attr("aljoin-sys-data-source"); List<Object>
             * sourceList = getAllSource(currentUserId,userName,type); //value =
             * JSONArray.toJSON(sourceList).toString(); String str = "";
             * for(Object obj : sourceList){
             * if(actAljoinFormDataRun.getFormWidgetValue().equals(((
             * CheckBoxSource) obj).getValue())){ str += ((CheckBoxSource)
             * obj).getText(); } } value = str; }else{
             */
            value = actAljoinFormDataRun.getFormWidgetValue().equals("0") ? "女" : "男";
            // }
            actAljoinFormDataRun.setFormWidgetValue(value);
        } else if (widgetType.equals("image") || widgetType.equals("attach")) {
            String decodeValue = "";
            if (actAljoinFormDataRun.getFormWidgetValue().indexOf("[]") < 0) {
                byte[] formWidgetValue = actAljoinFormDataRun.getFormWidgetValue().getBytes();
                byte[] encodeValue = Base64.decode(formWidgetValue);
                encodeValue = Base64.decode(encodeValue);
                decodeValue = new String(encodeValue);
            }
            actAljoinFormDataRun.setFormWidgetValue(decodeValue);
        } else if (widgetType.equals("biz_text_office")) {
            String attachment = "";
            if (actAljoinFormDataRun.getFormWidgetValue().indexOf("[]") < 0) {
                Long attmentId = StringUtils.isNotEmpty(actAljoinFormDataRun.getFormWidgetValue())
                    ? Long.valueOf(actAljoinFormDataRun.getFormWidgetValue()) : 0;
                Where<ResResource> where = new Where<ResResource>();
                where.setSqlSelect("id,orgnl_file_name,group_name,file_name");
                where.eq("id", attmentId);
                ResResource resResource = resResourceService.selectOne(where);

                if (null != resResource) {
                    Map<String, String> attMap = new HashMap<String, String>();
                    attMap.put("dalWorkId", String.valueOf(resResource.getId()));
                    attMap.put("name", resResource.getOrgnlFileName());
                    attMap.put("src", "/res/file/download?groupName="+resResource.getGroupName()+"&fileName="+resResource.getFileName());
                    attMap.put("groupName", resResource.getGroupName());
                    attMap.put("fileName", resResource.getFileName());
                    List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
                    mapList.add(attMap);
                    attachment = JSONArray.toJSON(mapList).toString();
                }
            }
            actAljoinFormDataRun.setFormWidgetValue(attachment);
        }
    }

    public List<FixedFormProcessLog> getHisLog(String processInstanceId) throws Exception {
        List<FixedFormProcessLog> hisLog = new ArrayList<FixedFormProcessLog>();
        Where<ActAljoinActivityLog> logWhere = new Where<ActAljoinActivityLog>();
        logWhere.eq("proc_inst_id", processInstanceId);
        logWhere.orderBy("operate_time", true);
        List<ActAljoinActivityLog> logList = actAljoinActivityLogService.selectList(logWhere);
        if (logList != null && logList.size() > 0) {
            for (ActAljoinActivityLog actAljoinActivityLog : logList) {
                FixedFormProcessLog log = new FixedFormProcessLog();
                log.setOperationName(actAljoinActivityLog.getOperateFullName());
                log.setOperationTime(actAljoinActivityLog.getOperateTime());
                log.setProcessInstanceId(actAljoinActivityLog.getProcInstId());
                String currentText = "";
                if (actAljoinActivityLog.getCurrentTaskName() != null
                    && !"".equals(actAljoinActivityLog.getCurrentTaskName())) {
                    currentText = actAljoinActivityLog.getCurrentTaskName();
                }
                String lastText = "";
                if (actAljoinActivityLog.getLastTaskName() != null
                    && !"".equals(actAljoinActivityLog.getLastTaskName())) {
                    lastText = actAljoinActivityLog.getLastTaskName();
                } else {
                    lastText = "归档";
                }
                log.setDirection(currentText + "(" + actAljoinActivityLog.getOperateStatus() + ")—>" + lastText);
                if (actAljoinActivityLog.getComment() != null && !"".equals(actAljoinActivityLog.getComment())) {
                    log.setComment(actAljoinActivityLog.getComment());
                } else {
                    log.setComment("");
                }
                log.setOperationId(actAljoinActivityLog.getOperateUserId().toString());
                log.setRecevieUserId(log.getRecevieUserId());
                log.setRecevieUserName(log.getRecevieUserName());
                hisLog.add(log);
            }
        }
        return hisLog;

    }

    public Map<String, String> bulidFormWidgetValue(String bpmnId, String taskId, ActAljoinFormRun actAljoinFormRun,
        byte[] byteKey, Long currentUserId, String userName) throws Exception {
        Map<String, String> map = new HashMap<String, String>();

        // 根据实例ID查询出 表单运行时控件对应的数据
        List<ActAljoinFormDataRun> runDataList = new ArrayList<ActAljoinFormDataRun>();
        if (!StringUtils.isEmpty(taskId)) {
            String instanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
            Where<ActAljoinFormDataRun> dataRunWhere = new Where<ActAljoinFormDataRun>();
            dataRunWhere.eq("proc_inst_id", instanceId);
            runDataList = actAljoinFormDataRunService.selectList(dataRunWhere);
        }

        List<ActAljoinFormDataDraft> dataDraftList = new ArrayList<ActAljoinFormDataDraft>();
        String taskDefKey = "";
        if (!StringUtils.isEmpty(taskId)) {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (null != task) {
                String instanceId = task.getProcessInstanceId();
                taskDefKey = task.getTaskDefinitionKey();
                Where<ActAljoinFormDataDraft> dataDraftWhere = new Where<ActAljoinFormDataDraft>();
                dataDraftWhere.eq("proc_inst_id", instanceId);
                dataDraftWhere.eq("proc_task_id", taskDefKey);
                dataDraftWhere.eq("create_user_id", currentUserId);
                dataDraftList = actAljoinFormDataDraftService.selectList(dataDraftWhere);
            }
        }

        List<ActAljoinFormDataRun> formDataRunList = runDataList;
        if (dataDraftList.size() > 0) {
            for (ActAljoinFormDataDraft actAljoinFormDataRun : dataDraftList) {
                map.put(actAljoinFormDataRun.getFormWidgetId(), actAljoinFormDataRun.getFormWidgetValue());
            }
        } else {
            for (ActAljoinFormDataRun actAljoinFormDataRun : formDataRunList) {
                map.put(actAljoinFormDataRun.getFormWidgetId(), actAljoinFormDataRun.getFormWidgetValue());
            }
        }

        return map;
    }

    public RetMsg getDictListByCode(String code) throws  Exception{
        RetMsg retMsg = new RetMsg();
        Where<SysDataDict> where = new Where<SysDataDict>();
        where.eq("dict_code", code);
        where.orderBy("dict_rank");
        List<SysDataDict> list = sysDataDictService.selectList(where);
        retMsg.setObject(list);
        retMsg.setCode(0);
        return retMsg;
    }
}
