package aljoin.web.service.goo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.springframework.transaction.annotation.Transactional;

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
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.goo.dao.entity.GooInOut;
import aljoin.goo.dao.entity.GooInfo;
import aljoin.goo.dao.object.GooInOutVO;
import aljoin.goo.iservice.GooInOutService;
import aljoin.goo.iservice.GooInfoService;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.util.DateUtil;
import aljoin.util.StringUtil;
import aljoin.web.service.act.ActActivitiWebService;

@Component
public class GooInOutWebService {
  
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
  private GooInOutService gooInOutService;
  @Resource
  private GooInfoService gooInfoService;
  @Resource
  private ActAljoinBpmnRunService actAljoinBpmnRunService;
  @Resource
  private HistoryService historyService;

  @Transactional
  public RetMsg add(GooInOutVO obj, AutUser autuser) throws Exception {
    RetMsg retMsg = new RetMsg();
    ActAljoinBpmn bpmn = null;
    Map<String, String> map = new HashMap<String, String>();
    String processInstanceId = null;
    if(obj != null) {
      
      List<GooInOut> gooInOutList = obj.getGooInfoList();
      Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
      if(null == obj.getProcessName()) {
        if(obj.getInOutStatus() != null) {
          if(obj.getInOutStatus() == 1) {
            obj.setProcessName(autuser.getFullName() + "办公用品入库");
            configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_GOO_IN);
          }
          if(obj.getInOutStatus() == 2) {
            obj.setProcessName(autuser.getFullName() + "办公用品报溢");
            configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_GOO_MORE);
          }
          if(obj.getInOutStatus() == 3) {
            obj.setProcessName(autuser.getFullName() + "办公用品领用");
            configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_GOO_OUT);
          }
          if(obj.getInOutStatus() == 4) {
            obj.setProcessName(autuser.getFullName() + "办公用品报损");
            configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_GOO_LESS);
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
          /*if (null != bpmn) {
              obj.setAuditStatus(1);// 审核中
              obj.setProcessId(bpmn.getProcessId());
          }*/
      } else {
        retMsg.setCode(1);
        retMsg.setMessage("没有找到出入库流程，请先新增流程");
        return retMsg;
    }
      if (null != gooInOutList && !gooInOutList.isEmpty()) {
        String ids = "";
        Long userId = autuser.getId();
//        String processName = gooInOutList.get(0).getTitle();
        for (GooInOut gooInOut : gooInOutList) {

            Long id = IdWorker.getId();
            if(null == gooInOut.getListCode() || StringUtils.isEmpty(gooInOut.getListCode())) {
                gooInOut.setListCode(id.toString());
            }
            if(null != bpmn) {
              if (StringUtils.isNotEmpty(processInstanceId)) {
                gooInOut.setGooInOutProcInstId(processInstanceId);
            } else {
              gooInOut.setAuditStatus(1);
              gooInOut.setProcessId(bpmn.getProcessId());
            }
            gooInOutService.insert(gooInOut);
            if (null != gooInOut && null != gooInOut.getId()) {
              ids += gooInOut.getId() + ";";
            }
          }
        }
      if (null != bpmn) {
        if (null != userId) {
            // 如果此时流程版本有升级，在run表中插入一条新的记录
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
            Map<String, String> param = new HashMap<String, String>();
            param.put("bizType", "goo");
            param.put("bizId", ids);
            param.put("isUrgent", "1");
            // 启动流程
            ProcessInstance instance = activitiService.startBpmn(bpmnRun, null, param,
                userId);
            processInstanceId = instance.getProcessInstanceId();
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
            for (GooInOut gooInOut : gooInOutList) {
              if(null != bpmn) {
                if (StringUtils.isNotEmpty(processInstanceId)) {
                  gooInOut.setGooInOutProcInstId(processInstanceId);
              } else {
                gooInOut.setAuditStatus(2);
                gooInOut.setProcessId(bpmn.getProcessId());
              }
              gooInOutService.updateById(gooInOut);
              }
          }
            
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.setSqlSelect("id,user_name,full_name");
            userWhere.eq("id", userId);
            AutUser user = autUserService.selectOne(userWhere);
            actActivitiWebService.insertProcessQuery(instance, "1", null, obj.getProcessName(),
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
            taskService.claim(task.getId(), String.valueOf(userId));
            String userFullName = "";
            map.put("signInTime", DateUtil.datetime2str(new Date()));
            if (null != task.getAssignee()) {
                userFullName = autUserService.selectById(task.getAssignee()).getFullName();
            }
            actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);

        }
      }
      }else {
        retMsg.setCode(1);
        retMsg.setMessage("没有获取到出入库记录");
      }
    }
    retMsg.setCode(0);
    retMsg.setObject(map);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Transactional
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
      if (bizId.indexOf(";") > -1) {
        Where<GooInOut> where = new Where<GooInOut>();
        where.andNew().eq("goo_in_out_proc_inst_id",processInstanceId);
        List<GooInOut> gooInOutList = gooInOutService.selectList(where);
        
//        GooInOut gooInOut = gooInOutService.selectById(Long.parseLong(bizId));
        if (null != gooInOutList && !gooInOutList.isEmpty()) {
          List<GooInfo> inOutList = new ArrayList<GooInfo>();
          List<GooInOut> inOutList1 = new ArrayList<GooInOut>();
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                    .singleResult();
            if (null == pi) {
                // 如果是结束节点，则记录最后一个办理人
                actFixedFormService.updateLastAsignee(processInstanceId, createUser.getFullName());
                for (GooInOut gooInOut : gooInOutList) {
                  GooInfo inOut = new GooInfo();
                  GooInOut inOut1 = new GooInOut();
                  if(StringUtils.isNotEmpty(gooInOut.getGooInOutProcInstId())){
                    if (1 == gooInOut.getAuditStatus()
                        && processInstanceId.equals(gooInOut.getGooInOutProcInstId())) {
                      gooInOut.setAuditTime(date);
                      gooInOut.setAuditStatus(3);
                    }
                }
                  //维护办公用品库存
                  if(gooInOut != null) {
                    if(gooInOut.getInOutStatus() == 1 || gooInOut.getInOutStatus() == 2) {
                      Where<GooInfo> info = new Where<GooInfo>(); 
                      info.eq("id", gooInOut.getGooId());
                      GooInfo gooInfo = gooInfoService.selectOne(info);
                      gooInfo.setNumber(gooInfo.getNumber() + gooInOut.getNumber());
                      if(gooInfo.getStatus() == 1) {
                        if(gooInfo.getNumber() >= gooInfo.getEmerNum()) {
                          gooInfo.setStatus(0);
                        }
                      }

                      BeanUtils.copyProperties(gooInfo, inOut);
                    }
                    if(gooInOut.getInOutStatus() == 3 || gooInOut.getInOutStatus() == 4) {
                      Where<GooInfo> info = new Where<GooInfo>(); 
                      info.eq("id", gooInOut.getGooId());
                      GooInfo gooInfo = gooInfoService.selectOne(info);
                      gooInfo.setNumber(gooInfo.getNumber() - gooInOut.getNumber());
                      if(gooInfo.getNumber() <= gooInfo.getEmerNum()) {
                        gooInfo.setStatus(1);
                      }
                      BeanUtils.copyProperties(gooInfo, inOut);
                    }
                  }
                  BeanUtils.copyProperties(gooInOut, inOut1);
                  inOutList.add(inOut);
                  inOutList1.add(inOut1);
                }
                gooInOutService.updateBatchById(inOutList1);
                gooInfoService.updateBatchById(inOutList);
                map.put("isEnd", "1");
                /*if(gooInOut != null) {
                  if(gooInOut.getInOutStatus() == 1 || gooInOut.getInOutStatus() == 2) {
                    Where<GooInfo> info = new Where<GooInfo>(); 
                    info.eq("id", gooInOut.getGooId());
                    GooInfo gooInfo = gooInfoService.selectOne(info);
                    gooInfo.setNumber(gooInfo.getNumber() + gooInOut.getNumber());
                  }
                  if(gooInOut.getInOutStatus() == 3 || gooInOut.getInOutStatus() == 4) {
                    Where<GooInfo> info = new Where<GooInfo>(); 
                    info.eq("id", gooInOut.getGooId());
                    GooInfo gooInfo = gooInfoService.selectOne(info);
                    gooInfo.setNumber(gooInfo.getNumber() - gooInOut.getNumber());
                    if(gooInfo.getNumber() <= gooInfo.getEmerNum()) {
                      gooInfo.setStatus(1);
                    }
                  }
                }*/
            }
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
                taskService.unclaim(task.getId());
                HashSet<Long> userIdSet = new HashSet<Long>();
                for (String uId : userIdList) {
                  userIdSet.add(Long.valueOf(uId));
                  taskService.addCandidateUser(task.getId(), uId);
                }
                Where<AutUser> uwhere = new Where<AutUser>();
                uwhere.setSqlSelect("id,full_name");
                uwhere.in("id", userIdSet);
                List<AutUser> userList = autUserService.selectList(uwhere);
                String userName = "";
                for (AutUser autUser : userList) {
                  userName += autUser.getFullName() + ";";
                }
                actAljoinQueryService.updateCurrentUserName(task.getId(), userName);
              }
        }
    }
    String handle = "";
    /*// 环节办理人待办数量-1
    AutDataStatistics aut = new AutDataStatistics();
    aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
    aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
    aut.setBusinessKey(String.valueOf(createUser.getId()));
    autDataStatisticsService.minus(aut);*/
    // 下一环节办理人待办数量+1
    if (null != userIdList && !userIdList.isEmpty()) {
        /*AutDataStatistics aut1 = new AutDataStatistics();
        aut1.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
        aut1.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
        autDataStatisticsService.addOrUpdateList(aut1, userIdList);*/
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