package aljoin.web.service.ass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.toolkit.IdWorker;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.entity.ActAljoinFixedConfig;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnRunService;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinFixedConfigService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.ass.dao.entity.AssProcess;
import aljoin.ass.dao.object.AssProcessVO;
import aljoin.ass.iservice.AssProcessService;
import aljoin.aut.dao.entity.AutDataStatistics;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.util.DateUtil;
import aljoin.util.StringUtil;
import aljoin.web.service.act.ActActivitiWebService;

@Component
public class AssProcessWebService {

  @Resource
  private ActAljoinFixedConfigService actAljoinFixedConfigService;
  @Resource
  private ActAljoinBpmnService actAljoinBpmnService;
  @Resource
  private ActActivitiService activitiService;
  @Resource
  private ActAljoinQueryService actAljoinQueryService;
  @Resource
  private TaskService taskService;
  @Resource
  private ActFixedFormServiceImpl actFixedFormService;
  @Resource
  private ActActivitiWebService actActivitiWebService;
  @Resource
  private AutUserService autUserService;
  @Resource
  private RuntimeService runtimeService;
  @Resource
  private AutDataStatisticsService autDataStatisticsService;
  @Resource
  private AssProcessService assProcessService;
  @Resource
  private ActAljoinBpmnRunService actAljoinBpmnRunService;
  @Resource
  private HistoryService historyService;
  
  
  public RetMsg add(AssProcessVO obj, AutUser autuser) throws Exception {
    RetMsg retMsg = new RetMsg();
    ActAljoinBpmn bpmn = null;
    Map<String, String> map = new HashMap<String, String>();
    if(obj != null) {
      Long id = IdWorker.getId();
      if(null == obj.getListCode() || StringUtils.isEmpty(obj.getListCode())) {
        obj.setListCode(id.toString());
      }
      Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();

      if(null == obj.getProcessName()) {
        if(obj.getAssStatus() != null) {
          if(obj.getAssStatus() == 1) {
            obj.setProcessName(autuser.getFullName() + "固定资产购置申请");
            configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_ASS_PUR);
          }
          if(obj.getAssStatus() == 2) {
            obj.setProcessName(autuser.getFullName() + "固定资产验收");
            configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_ASS_REC);
          }
          if(obj.getAssStatus() == 3) {
            obj.setProcessName(autuser.getFullName() + "固定资产领用");
            configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_ASS_USE);
          }
          if(obj.getAssStatus() == 4) {
            obj.setProcessName(autuser.getFullName() + "固定资产移交");
            configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_ASS_CHA);
          }
          if(obj.getAssStatus() == 5) {
            obj.setProcessName(autuser.getFullName() + "固定资产报废");
            configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_ASS_RUI);
          }
        }
      }
      
      configWhere.setSqlSelect("id,process_code,process_id,process_name,is_active");
      ActAljoinFixedConfig config = actAljoinFixedConfigService.selectOne(configWhere);
      if (null != config && null != config.getProcessId()) {
          Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
          where.eq("process_id", config.getProcessId());
          where.eq("is_deploy", 1);
          where.eq("is_active", 1);
          where.setSqlSelect("id,category_id,process_id,process_name");
          bpmn = actAljoinBpmnService.selectOne(where);
          if (null != bpmn) {
              obj.setAuditStatus(1);// 审核中
              obj.setProcessId(bpmn.getProcessId());
          }
      } else {
        retMsg.setCode(1);
        retMsg.setMessage("没有找到固定资产流程，请先新增流程");
        return retMsg;
    }
      AssProcess assProcess = new AssProcess();
      obj.setId(null);
      BeanUtils.copyProperties(obj, assProcess);
      assProcessService.insert(assProcess);
      if (null != bpmn) {
        // 启动流程
        if (null != assProcess.getCreateUserId()) {
            Map<String, String> param = new HashMap<String, String>();
            param.put("bizType", "ass");
            param.put("bizId", assProcess.getId() + "");
            param.put("isUrgent", "1");
            
            // 向bpmn_run表插入记录
            Where<ActAljoinBpmnRun> bpmnRunWhere = new Where<ActAljoinBpmnRun>();
            bpmnRunWhere.eq("orgnl_id", bpmn.getId());
            bpmnRunWhere.eq("version", bpmn.getVersion());
            ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectOne(bpmnRunWhere);
            if(null == bpmnRun){
                bpmnRun = new ActAljoinBpmnRun();
                BeanUtils.copyProperties(bpmn, bpmnRun);
                bpmnRun.setId(null);
                bpmnRun.setOrgnlId(bpmn.getId());
                actAljoinBpmnRunService.insert(bpmnRun);
            }
            // 调用启动流程方法
            ProcessInstance instance = activitiService.startBpmn(bpmnRun, null, param,
                assProcess.getCreateUserId());
            String businessKey = instance.getBusinessKey();
            map.put("businessKey", businessKey);
            String bpmnId = "";
            if (!StringUtils.isEmpty(businessKey)) {
                String[] key = businessKey.split(",");
                if (key.length >= 1) {
                    bpmnId = key[0];
                }
            }
            map.put("bpmnId", bpmnId);
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.setSqlSelect("id,user_name,full_name");
            userWhere.eq("id", assProcess.getCreateUserId());
            AutUser user = autUserService.selectOne(userWhere);
            actActivitiWebService.insertProcessQuery(instance, "1", null, assProcess.getProcessName(),
                    user.getFullName(), user.getFullName(), bpmn.getCategoryId(), false);
            Task task = taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId())
                    .singleResult();
            map.put("id", task.getId());
            map.put("processName", task.getName());
            map.put("taskDefKey", task.getTaskDefinitionKey());
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
            // 本来第一环节不应该指定办理人，为了避免画出这种流程，加了这句判断
            if (null != hisTask.getClaimTime()) {
                actFixedFormService.deleteOrgnlTaskAuth(task);
                taskService.unclaim(task.getId());
            }
            taskService.claim(task.getId(), String.valueOf(assProcess.getCreateUserId()));
            String userFullName = "";
            map.put("signInTime", DateUtil.datetime2str(new Date()));
            if (null != task.getAssignee()) {
                userFullName = autUserService.selectById(task.getAssignee()).getFullName();
            }
            actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);

        }
      }
    }
    retMsg.setCode(0);
    retMsg.setObject(map);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  public RetMsg completeTask(Map<String, Object> variables, String taskId, String bizId,
      String userId, String message, AutUser createUser) throws Exception {
    RetMsg retMsg = new RetMsg();
    HashMap<String, String> map = new HashMap<String, String>();
    String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
    Authentication.setAuthenticatedUserId(createUser.getId().toString());
    taskService.addComment(taskId, processInstanceId, message);
    activitiService.completeTask(variables, taskId);
    Date date = new Date();
    if (StringUtils.isNotEmpty(bizId)) {
        AssProcess assProcess = assProcessService.selectById(Long.parseLong(bizId));
        if (null != assProcess) {
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                    .singleResult();
            if (null == pi) {
                // 如果是结束节点，则记录最后一个办理人
                actFixedFormService.updateLastAsignee(processInstanceId, createUser.getFullName());
                assProcess.setAuditStatus(3);// 审核通过
                assProcess.setAuditTime(date);
                assProcessService.updateById(assProcess);
                map.put("isEnd", "1");
            }
        }
    }
    List<String> userIdList = new ArrayList<String>();
    List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    if (null != taskList && !taskList.isEmpty()) {
        Task task = taskList.get(0);
        if (null != task && StringUtils.isNotEmpty(userId)) {
            userIdList = Arrays.asList(userId.split(";"));
            if (null != userIdList && !userIdList.isEmpty()) {
                actFixedFormService.deleteOrgnlTaskAuth(task);
                // 原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
                HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                if (null != hisTask.getClaimTime()){ //unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
                    taskService.unclaim(task.getId());
                }
                for (String uId : userIdList) {
                    taskService.addCandidateUser(task.getId(), uId);
                }
                actAljoinQueryService.cleanQureyCurrentUser(task.getId());
            }
        }
    }
    String handle = "";
    // 环节办理人待办数量-1
    AutDataStatistics aut = new AutDataStatistics();
    aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
    aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
    aut.setBusinessKey(String.valueOf(createUser.getId()));
    autDataStatisticsService.minus(aut);
    // 下一环节办理人待办数量+1
    if (null != userIdList && !userIdList.isEmpty()) {
        AutDataStatistics aut1 = new AutDataStatistics();
        aut1.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
        aut1.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
        autDataStatisticsService.addOrUpdateList(aut1, userIdList);
        handle = StringUtil.list2str(userIdList, ";");
    }

    map.put("processInstanceId", processInstanceId);// 流程实例id
    map.put("handle", handle);// 下一级办理人
    retMsg.setObject(map);// 返回给controller异步调用在线消息
    retMsg.setCode(0);
    retMsg.setMessage("操作成功!");
    return retMsg;
  }
}

