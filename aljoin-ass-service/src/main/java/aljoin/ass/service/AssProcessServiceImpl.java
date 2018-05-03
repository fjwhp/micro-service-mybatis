package aljoin.ass.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinFixedConfig;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.act.iservice.ActAljoinFixedConfigService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.ass.dao.entity.AssProcess;
import aljoin.ass.dao.mapper.AssProcessMapper;
import aljoin.ass.dao.object.AllTaskShowVO;
import aljoin.ass.dao.object.AssProcessVO;
import aljoin.ass.iservice.AssProcessService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.util.DateUtil;

/**
 * 
 * 固定财产流程表(服务实现类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-12
 */
@Service
public class AssProcessServiceImpl extends ServiceImpl<AssProcessMapper, AssProcess> implements AssProcessService {

  @Resource
  private AssProcessMapper mapper;
  @Resource
  private TaskService taskService;
  @Resource
  private RuntimeService runtimeService;
  @Resource
  private ActAljoinQueryService actAljoinQueryService;
  @Resource
  private ActAljoinQueryHisService actAljoinQueryHisService;
  @Resource
  private ActAljoinFixedConfigService actAljoinFixedConfigService;
  @Resource
  private ActAljoinBpmnService actAljoinBpmnService;
  @Resource
  private ActAljoinCategoryService actAljoinCategoryService;
  @Resource
  private HistoryService historyService;
  @Resource
  private AutUserService autUserService;
  @Resource
  private ActFixedFormServiceImpl actFixedFormService;

  @Override
  public Page<AssProcess> list(PageBean pageBean, AssProcess obj) throws Exception {
	Where<AssProcess> where = new Where<AssProcess>();
	where.orderBy("create_time", false);
	Page<AssProcess> page = selectPage(new Page<AssProcess>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	
  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }

  @Override
  public void copyObject(AssProcess obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  public RetMsg toVoid(String taskId, String bizId, Long userId) throws Exception {
    RetMsg retMsg = new RetMsg();
    if (StringUtils.isNotEmpty(taskId)) {
      String processInstanceId =
          taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
      runtimeService.deleteProcessInstance(processInstanceId, null);
      Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
      queryWhere.eq("process_instance_id", processInstanceId);
      actAljoinQueryService.delete(queryWhere);
      Where<ActAljoinQueryHis> queryHisWhere = new Where<ActAljoinQueryHis>();
      queryHisWhere.eq("process_instance_id", processInstanceId);
      actAljoinQueryHisService.delete(queryHisWhere);
      // 修改原文件状态
      Date date = new Date();
      if (org.apache.commons.lang.StringUtils.isNotEmpty(bizId)) {
        AssProcess use = selectById(Long.parseLong(bizId));
        if (null != use) {
          use.setAuditStatus(2);// 审核不通过
          use.setAuditTime(date);
          updateById(use);
        }
      }
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public RetMsg getBpmnId(AssProcessVO obj) {

    RetMsg retMsg = new RetMsg();
    ActAljoinBpmn bpmn = null;
    Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();

      if(obj.getAssStatus() != null) {
        if(obj.getAssStatus() == 1) {
          configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_ASS_PUR);
        }
        if(obj.getAssStatus() == 2) {
          configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_ASS_REC);
        }
        if(obj.getAssStatus() == 3) {
          configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_ASS_USE);
        }
        if(obj.getAssStatus() == 4) {
          configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_ASS_CHA);
        }
        if(obj.getAssStatus() == 5) {
          configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_ASS_RUI);
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
  }
    if(bpmn != null) {
      retMsg.setObject(bpmn.getId().toString());
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public Page<ActAljoinQueryHis> getAllTask(PageBean pageBean, AllTaskShowVO obj,
      Map<String, String> map) throws Exception {
    Page<ActAljoinQueryHis> page = null;
    List<HistoricTaskInstance> finishTaskProcess = new ArrayList<HistoricTaskInstance>();
    String userID = map.get("userId").toString();
    List<String> configIdList = new ArrayList<String>();
    List<String> instIdList = new ArrayList<String>();
    Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
//    Where<ActHiActinst> actinstWhere = new Where<ActHiActinst>();
    Where<ActAljoinQueryHis> hisWhere = new Where<ActAljoinQueryHis>();

    configWhere.like("process_code", "ass");
    configWhere.setSqlSelect("id,process_code,process_id,process_name,is_active");
    List<ActAljoinFixedConfig> configList = actAljoinFixedConfigService.selectList(configWhere);
    for (ActAljoinFixedConfig config : configList) {
      
      if (null != config && null != config.getProcessId()) {
        configIdList.add(config.getProcessId());
       } 
    }
    if(configIdList.size()>0){
      for (String string : configIdList) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().finished().taskAssignee(userID).processUnfinished();
        query.processDefinitionKeyLike(string);
        List<HistoricTaskInstance> taskProcess = query.list();
        finishTaskProcess.addAll(taskProcess);
      }
    }
//    List<HistoricTaskInstance> finishTaskProcess = query.list();
    if (finishTaskProcess.isEmpty()) {
      page = new Page<ActAljoinQueryHis>();
      return page;
    }
    for (HistoricTaskInstance finish : finishTaskProcess) {
      instIdList.add(finish.getProcessInstanceId());
    }
    /*for(int i = 0; i < configIdList.size(); i++) {
      if(i != configIdList.size() - 1) {
        actinstWhere.like("proc_def_id_", configIdList.get(i) + ":").or();
      }else {
        actinstWhere.like("proc_def_id_", configIdList.get(i) + ":");
      }
    }
    List<ActHiActinst> actinstList = actHiActinstService.selectList(actinstWhere);
    for (ActHiActinst actHiActinst : actinstList) {
      instIdList.add(actHiActinst.getProcInstId());
    }*/
    if(instIdList != null){
      if(map.get("name") != null && !map.get("name").isEmpty()) {
        hisWhere.like("process_name", map.get("name"));
      }
      if(map.get("title") != null && !map.get("title").isEmpty()) {
        hisWhere.like("process_title", map.get("title"));
      }
      if (StringUtils.isNotEmpty(map.get("startTime")) && StringUtils.isEmpty(map.get("endTime"))) {
        String startTime = map.get("startTime") + " 00:00:00";
        hisWhere.ge("start_time", DateUtil.str2datetime(startTime));
      }
      if (StringUtils.isEmpty(map.get("startTime")) && StringUtils.isNotEmpty(map.get("endTime"))) {
        String endTime = map.get("endTime") + " 23:59:59";
        hisWhere.le("start_time", DateUtil.str2dateOrTime(endTime));
      }
      if (StringUtils.isNotEmpty(map.get("startTime")) && StringUtils.isNotEmpty(map.get("endTime"))) {
        String endTime = map.get("endTime") + " 23:59:59";
        String startTime = map.get("startTime") + " 00:00:00";
        hisWhere.andNew();
        hisWhere.between("start_time", DateUtil.str2dateOrTime(startTime), DateUtil.str2dateOrTime(endTime));
      }
      hisWhere.in("process_instance_id", instIdList);
      hisWhere.eq("create_user_id", userID);
      hisWhere.orderBy("start_time",false);
      page = new Page<ActAljoinQueryHis>();
//      List<ActAljoinQueryHis> myqueryHisList = actAljoinQueryHisService.selectList(hisWhere);
      page = actAljoinQueryHisService.selectPage(new Page<ActAljoinQueryHis>(pageBean.getPageNum(), pageBean.getPageSize()), hisWhere);
    }
    return page;
  }

  @Override
  public RetMsg getBusinessKey(String processInstanceId) {
    RetMsg retMsg = new RetMsg();
    String businessKey = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getBusinessKey();
//    ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    //3 通过流程实例获取业务键
    
    retMsg.setObject(businessKey);
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }
}
