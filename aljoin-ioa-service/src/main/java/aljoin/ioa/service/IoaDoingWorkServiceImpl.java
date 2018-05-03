package aljoin.ioa.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.dao.entity.ActAljoinFixedConfig;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActHiTaskinst;
import aljoin.act.iservice.ActAljoinBpmnRunService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.act.iservice.ActAljoinFixedConfigService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.iservice.ActHiTaskinstService;
import aljoin.config.AljoinSetting;
import aljoin.dao.config.Where;
import aljoin.ioa.dao.object.AppDoTaskDO;
import aljoin.ioa.dao.object.AppDoTaskVO;
import aljoin.ioa.dao.object.DoTaskShowVO;
import aljoin.ioa.iservice.IoaDoingWorkService;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.util.DateUtil;

/**
 *
 * 在办工作(服务实现类).
 *
 * @author：pengsp
 *
 * @date： 2017-10-19
 */
@Service
public class IoaDoingWorkServiceImpl implements IoaDoingWorkService {

  @Resource
  private HistoryService historyService;
  @Resource
  private ActAljoinCategoryService actAljoinCategoryService;
  @Resource
  private ActAljoinQueryService actAljoinQueryService;
  @Resource
  private ActAljoinFixedConfigService actAljoinFixedConfigService;
  @Resource
  private RuntimeService runtimeService;
  @Resource
  private ActAljoinBpmnRunService actAljoinBpmnRunService;
  @Resource
  private ActHiTaskinstService actHiTaskinstService;
  @Resource
  private AljoinSetting aljoinSetting;

  /*
   * 构造在查询条件
   */
  private int makeQueryWhere(Where<ActAljoinQuery> where, DoTaskShowVO obj) throws Exception {
    int flag = 0;
    if (!StringUtils.isEmpty(obj.getTitle())) {
      where.like("process_title", obj.getTitle());
      flag++;
    }

    if (!StringUtils.isEmpty(obj.getUrgency())) {
      where.eq("urgent_status", obj.getUrgency());
      flag++;
    }
    if (!StringUtils.isEmpty(obj.getUrgency())) {
        where.eq("urgent_status", obj.getUrgency());
        flag++;
      }
    if (!StringUtils.isEmpty(obj.getReferenceNumber())) {
        where.like("reference_number", obj.getReferenceNumber());
        flag++;
      }
    if (!StringUtils.isEmpty(obj.getSerialNumber())) {
        where.like("serial_number", obj.getSerialNumber());
        flag++;
      }
    if (!StringUtils.isEmpty(obj.getFounder())) {
      where.like("create_full_user_name", obj.getFounder());
      flag++;
    }

    if (!StringUtils.isEmpty(obj.getFormType())) {
      where.like("process_category_ids", obj.getFormType());
      flag++;
    }

    if (!StringUtils.isEmpty(obj.getCreateBegTime()) && StringUtils.isEmpty(obj.getCreateEndTime())) {
      where.ge("create_time", DateUtil.str2dateOrTime(obj.getCreateBegTime()));
      flag++;
    }
    if (StringUtils.isEmpty(obj.getCreateBegTime()) && !StringUtils.isEmpty(obj.getCreateEndTime())) {
      where.le("create_time", DateUtil.str2dateOrTime(obj.getCreateEndTime()));
      flag++;
    }
    if (!StringUtils.isEmpty(obj.getCreateBegTime()) && !StringUtils.isEmpty(obj.getCreateEndTime())) {
      DateTime dateTime = new DateTime(obj.getCreateBegTime());
      DateTime dateTime2 = new DateTime(obj.getCreateEndTime());
      where.between("create_time", dateTime.toDate(),dateTime2.plusDays(1).toDate());
      flag++;
    }
    if(!StringUtils.isEmpty(obj.getProcessName())){
      where.like("process_name", obj.getProcessName());
      flag++;
    }

    return flag;
  }

  @Override
  public Page<DoTaskShowVO> list(PageBean pageBean, String userId, DoTaskShowVO obj)
    throws Exception {
    Page<DoTaskShowVO> page = new Page<DoTaskShowVO>();
    List<DoTaskShowVO> doTaskShowVOList = new ArrayList<DoTaskShowVO>();

    Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
    int flag = makeQueryWhere(where, obj);
    List<ActAljoinQuery> queryList = new ArrayList<ActAljoinQuery>();
    if (flag > 0) {
      // 有进行条件匹配才去查询
      where.setSqlSelect(
        "process_instance_id,urgent_status,process_title,start_time,create_full_user_name,current_handle_full_user_name,process_category_ids,serial_number,reference_number");
      queryList = actAljoinQueryService.selectList(where);
    }

    Map<String, ActAljoinQuery> actAljoinQueryMap = new HashMap<String, ActAljoinQuery>();
    List<String> processInstanceIds = new ArrayList<String>();
    Set<String> processInstanceIdSet = new HashSet<String>();
    for (ActAljoinQuery query : queryList) {
      processInstanceIds.add(query.getProcessInstanceId());
      processInstanceIdSet.add(query.getProcessInstanceId());
      actAljoinQueryMap.put(query.getProcessInstanceId(), query);
    }
    if(flag>0 && processInstanceIds.size()<=0){
      return page;
    }
    String processId = "";
    //查询收文阅件流程ID
    Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
    configWhere.setSqlSelect("id,process_id");
    configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_READ_RECEIPT);
    List<ActAljoinFixedConfig> configList = actAljoinFixedConfigService.selectList(configWhere);
    if (null != configList && !configList.isEmpty()) {
      processId = configList.get(0).getProcessId();
    }
    //构造查询条件
    ActHiTaskinst hiTaskinst = new ActHiTaskinst();
    if(!StringUtils.isEmpty(userId)){
      hiTaskinst.setAssignee(userId);
    }
    if(!StringUtils.isEmpty(processId)){
      hiTaskinst.setProcDefId(processId);
    }
    if (processInstanceIds.size() > 0) {
      hiTaskinst.setProcessInstanceIds(processInstanceIds);
    }
    hiTaskinst.setDbType(aljoinSetting.getDbType());
    // 查询出自己办理过的任务 分页信息
    Page<ActHiTaskinst> hiTaskinstPage = actHiTaskinstService.list(pageBean,hiTaskinst);
    // 获取在办任务列总记录数
    int count = pageBean.getIsSearchCount().intValue() == 1 ? hiTaskinstPage.getTotal() : 0;
    // 获得在办任务列表
    List<ActHiTaskinst> historicList = hiTaskinstPage.getRecords();

    //单独查询出流程实例
    Set<String> instanceIdSet = new HashSet<String>();
    Map<String,ProcessInstance> processInstanceMap = new HashMap<String,ProcessInstance>();
    Map<String,ActAljoinBpmnRun> instanceIdBpmnMap = new HashMap<String,ActAljoinBpmnRun>();
    if(historicList.size() > 0){
      for (ActHiTaskinst historicTaskInstance : historicList) {
        instanceIdSet.add(historicTaskInstance.getProcInstId());
      }
      List<ProcessInstance> piList = runtimeService.createProcessInstanceQuery().processInstanceIds(instanceIdSet).list();
      List<Long> bpmnIdList = new ArrayList<Long>();
      Map<String,Long> instanceIdBpmnIdMap = new HashMap<String,Long>();
      for (ProcessInstance processInstance : piList) {
        processInstanceMap.put(processInstance.getProcessInstanceId(), processInstance);
        String businessKey = processInstance.getBusinessKey();
        if(!StringUtils.isEmpty(businessKey)){
          String bpmnId = businessKey.split(",")[0];
          bpmnIdList.add(Long.parseLong(bpmnId));
          instanceIdBpmnIdMap.put(processInstance.getProcessInstanceId(), Long.parseLong(bpmnId));
        }
      }
      if(bpmnIdList.size() > 0){
        Where<ActAljoinBpmnRun> actAljoinBpmnRunWhere = new Where<ActAljoinBpmnRun>();
        actAljoinBpmnRunWhere.setSqlSelect("id,is_free");
        actAljoinBpmnRunWhere.in("id", bpmnIdList);
        List<ActAljoinBpmnRun> bpmnList  = actAljoinBpmnRunService.selectList(actAljoinBpmnRunWhere);
        for (Map.Entry<String,Long> entry : instanceIdBpmnIdMap.entrySet()) {
          //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  
          for (ActAljoinBpmnRun actAljoinBpmn : bpmnList) {
            if(entry.getValue().equals(actAljoinBpmn.getId())){
              instanceIdBpmnMap.put(entry.getKey(), actAljoinBpmn);
            }
          }
        }
      }
    }

    if(historicList.size() > 0){
      //查询
      List<String> instanceIdList = new ArrayList<String>();

      for (ActHiTaskinst historicTaskInstance : historicList) {
        instanceIdList.add(historicTaskInstance.getProcInstId());
      }
      //任务实例
      Map<String, List<HistoricTaskInstance>> instanceIdTaskInstanceListMap = new HashMap<String, List<HistoricTaskInstance>>();
      List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceIdIn(instanceIdList).unfinished().list();
      for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
        List<HistoricTaskInstance> tempList = instanceIdTaskInstanceListMap.get(historicTaskInstance.getProcessInstanceId());
        if(tempList != null && tempList.size() > 0){
          tempList.add(historicTaskInstance);
          instanceIdTaskInstanceListMap.put(historicTaskInstance.getProcessInstanceId(), tempList);
        }else{
          List<HistoricTaskInstance> newList = new ArrayList<HistoricTaskInstance>();
          newList.add(historicTaskInstance);
          instanceIdTaskInstanceListMap.put(historicTaskInstance.getProcessInstanceId(), newList);
        }
      }
      //流程实例
      Map<String, HistoricProcessInstance> historicProcessInstanceMap = new HashMap<String, HistoricProcessInstance>();
      List<HistoricProcessInstance> historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery().processInstanceIds(instanceIdSet).list();
      for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
        historicProcessInstanceMap.put(historicProcessInstance.getId(), historicProcessInstance);
      }

      // 如果上的查询条件没有流程查询表的数据，在去取，如果上面的流程查询map中有数据，则不需要在去查询
      if (actAljoinQueryMap.size() == 0) {
        Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
        queryWhere.setSqlSelect(
          "process_instance_id,urgent_status,process_title,start_time,create_full_user_name,current_handle_full_user_name,process_category_ids,serial_number,reference_number");
        queryWhere.in("process_instance_id", instanceIdList);
        List<ActAljoinQuery> actAljoinQueryList = actAljoinQueryService.selectList(queryWhere);
        for (ActAljoinQuery actAljoinQuery : actAljoinQueryList) {
          actAljoinQueryMap.put(actAljoinQuery.getProcessInstanceId(), actAljoinQuery);
        }
      }

      // 一次性获取流程分类
      List<Long> categoryIdList = new ArrayList<Long>();
      Map<String, String> instanceIdCategoryNameMap = new HashMap<String, String>();

      for (Map.Entry<String, ActAljoinQuery> entry : actAljoinQueryMap.entrySet()) {
        ActAljoinQuery aq = entry.getValue();
        String categoryIds = aq.getProcessCategoryIds();
        if (!StringUtils.isEmpty(categoryIds)) {
          String[] categoryIdsArr = categoryIds.split(",");
          categoryIds = categoryIdsArr[categoryIdsArr.length - 1];
          instanceIdCategoryNameMap.put(entry.getKey(), categoryIds);
          categoryIdList.add(Long.parseLong(categoryIds));
        }

      }
      Where<ActAljoinCategory> actAljoinCategoryWhere = new Where<ActAljoinCategory>();
      actAljoinCategoryWhere.in("id", categoryIdList);
      actAljoinCategoryWhere.setSqlSelect("id,category_name");
      List<ActAljoinCategory> actAljoinCategoryList =
        actAljoinCategoryService.selectList(actAljoinCategoryWhere);
      for (ActAljoinCategory actAljoinCategory : actAljoinCategoryList) {
        for (Map.Entry<String, String> entry : instanceIdCategoryNameMap.entrySet()) {
          if ((actAljoinCategory.getId().toString()).equals(entry.getValue())) {
            instanceIdCategoryNameMap.put(entry.getKey(), actAljoinCategory.getCategoryName());
          }
        }
      }

      // 获取处理时间
      Where<ActHiTaskinst> hiTaskinstWhere = new Where<ActHiTaskinst>();
      hiTaskinstWhere.setSqlSelect("id_,end_time_,proc_inst_id_");
      hiTaskinstWhere.isNotNull("end_time_");
      hiTaskinstWhere.orderBy("end_time_",true);
      List<ActHiTaskinst> hiTaskinstList = actHiTaskinstService.selectList(hiTaskinstWhere);
      Map<String,ActHiTaskinst> taskinstMap = new HashMap<String, ActHiTaskinst>();
      for(ActHiTaskinst taskinst : hiTaskinstList){
        taskinstMap.put(taskinst.getProcInstId(),taskinst);
      }

      for (ActHiTaskinst historic : historicList) {
        // 当前流程的未完成的节点有可能有多个
        List<HistoricTaskInstance> doHistoricList = instanceIdTaskInstanceListMap.get(historic.getProcInstId());

        // 根据流程实例ID获取待办流程实例
        HistoricProcessInstance historicProcessInstance = historicProcessInstanceMap.get(historic.getProcInstId());

        ActAljoinQuery actAljoinQuery = actAljoinQueryMap.get(historic.getProcInstId());

        if(actAljoinQuery != null){
          Integer uegency = 1;
          DoTaskShowVO doTaskShowVO = new DoTaskShowVO();
          doTaskShowVO.setFormType(instanceIdCategoryNameMap.get(historic.getProcInstId())); // 流程类型
          doTaskShowVO.setProcessInstanceId(historic.getProcInstId());
          String str = historicProcessInstance.getBusinessKey();
          doTaskShowVO.setBusinessKey(str);

          String taskNames = "";
          //String handleUserNames = "";
          if(doHistoricList != null){
            Set<String> defKeySet = new HashSet<String>();
            for (HistoricTaskInstance historicTaskInstance : doHistoricList) {
              //对于相同的节点的名称不需要重复获取(多节点实例会有这样的情况)
              if(!defKeySet.contains(historicTaskInstance.getTaskDefinitionKey())){
                taskNames += historicTaskInstance.getName() == null ? "" : historicTaskInstance.getName() + ",";
              }
              /*if (!StringUtils.isEmpty(historicTaskInstance.getAssignee())) {
                //AutUser au = autUserService.selectById(historicTaskInstance.getAssignee());
                Where<AutUser> autUserWhere = new Where<AutUser>();
                autUserWhere.eq("id", historicTaskInstance.getAssignee());
                autUserWhere.setSqlSelect("full_name");
                AutUser au = autUserService.selectOne(autUserWhere);
                if(au != null){
                  String handleUserName = au.getFullName();
                  handleUserNames += handleUserName + ",";
                }
              }*/
              defKeySet.add(historicTaskInstance.getTaskDefinitionKey());
            }
          }
          doTaskShowVO
            .setLink(!"".equals(taskNames) ? (taskNames.substring(0, taskNames.length() - 1)) : "");// 环节
         /* String currentAdmin = !"".equals(handleUserNames)
              ? (handleUserNames.substring(0, handleUserNames.length() - 1)) : "";
          if(StringUtils.isEmpty(currentAdmin)){*/
          //  currentAdmin = actAljoinQuery.getCurrentHandleFullUserName();
          //}    
          doTaskShowVO.setCurrentAdmin(actAljoinQuery.getCurrentHandleFullUserName());// 当前班里人
          //文号
          if(actAljoinQuery.getReferenceNumber() == null || StringUtils.isEmpty(actAljoinQuery.getReferenceNumber())){
              if(actAljoinQuery.getSerialNumber() == null || StringUtils.isEmpty(actAljoinQuery.getSerialNumber())){
                  doTaskShowVO.setReferenceNumber("");
              }else{
                  doTaskShowVO.setReferenceNumber(actAljoinQuery.getSerialNumber());
              }
          }else{
              doTaskShowVO.setReferenceNumber(actAljoinQuery.getReferenceNumber());
          }

          ActHiTaskinst actHiTaskinst = taskinstMap.get(historicProcessInstance.getId());
          doTaskShowVO.setProcessingDate(actHiTaskinst.getEndTime());// 处理时间

          doTaskShowVO.setUrgency(actAljoinQuery.getUrgentStatus());// 缓急
          doTaskShowVO.setTitle(actAljoinQuery.getProcessTitle());// 标题

          doTaskShowVO.setFillingDate(actAljoinQuery.getStartTime());// 填报时间(流程发起时间)
          doTaskShowVO.setFounder(actAljoinQuery.getCreateFullUserName());// 流程发起人
          if(actAljoinQuery.getUrgentStatus().equals(WebConstant.COMMONLY)){
            uegency = 1;
          }else if(actAljoinQuery.getUrgentStatus().equals(WebConstant.URGENT)){
            uegency = 2;
          }else if(actAljoinQuery.getUrgentStatus().equals(WebConstant.ADD_URENT)){
            uegency = 3;
          }
          doTaskShowVO.setUrgencyStatus(uegency);
          doTaskShowVOList.add(doTaskShowVO);
        }else{
          count --;
        }
      }
    }
    sortList(doTaskShowVOList,obj);
    page.setRecords(doTaskShowVOList);
    page.setTotal(count);
    page.setSize(pageBean.getPageSize());
    page.setCurrent(pageBean.getPageNum());
    return page;
  }

  @Override
  public RetMsg doingList(PageBean pageBean, String userId, AppDoTaskVO obj) throws Exception {
    RetMsg retMsg = new RetMsg();
    Page<AppDoTaskDO> page = new Page<AppDoTaskDO>();
    List<AppDoTaskDO> doTaskShowVOList = new ArrayList<AppDoTaskDO>();

    Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
    int flag = makeQueryWhere(where, obj);
    List<ActAljoinQuery> queryList = new ArrayList<ActAljoinQuery>();
    if (flag > 0) {
      // 有进行条件匹配才去查询
      where.setSqlSelect(
        "process_instance_id,urgent_status,process_title,start_time,create_full_user_name,current_handle_full_user_name,process_category_ids");
      queryList = actAljoinQueryService.selectList(where);
    }

    Map<String, ActAljoinQuery> actAljoinQueryMap = new HashMap<String, ActAljoinQuery>();
    List<String> processInstanceIds = new ArrayList<String>();
    Set<String> processInstanceIdSet = new HashSet<String>();
    for (ActAljoinQuery query : queryList) {
      processInstanceIds.add(query.getProcessInstanceId());
      processInstanceIdSet.add(query.getProcessInstanceId());
      actAljoinQueryMap.put(query.getProcessInstanceId(), query);
    }

    String processId = "";
    if(flag>0 && processInstanceIds.size()<=0){
      retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
      retMsg.setObject(page);
      retMsg.setMessage("操作成功");
      return retMsg;
    }
    //查询收文阅件流程ID
    Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
    configWhere.setSqlSelect("id,process_id");
    configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_READ_RECEIPT);
    List<ActAljoinFixedConfig> configList = actAljoinFixedConfigService.selectList(configWhere);
    if (null != configList && !configList.isEmpty()) {
      processId = configList.get(0).getProcessId();
    }

    //构造查询条件
    ActHiTaskinst hiTaskinst = new ActHiTaskinst();
    if(!StringUtils.isEmpty(userId)){
      hiTaskinst.setAssignee(userId);
    }
    if(!StringUtils.isEmpty(processId)){
      hiTaskinst.setProcDefId(processId);
    }
    if (processInstanceIds.size() > 0) {
      hiTaskinst.setProcessInstanceIds(processInstanceIds);
    }
    hiTaskinst.setDbType(aljoinSetting.getDbType());
    // 查询出自己办理过的任务 分页信息
    Page<ActHiTaskinst> hiTaskinstPage = actHiTaskinstService.list(pageBean,hiTaskinst);
    // 获取在办任务列总记录数
    int count = pageBean.getIsSearchCount().intValue() == 1 ? hiTaskinstPage.getTotal() : 0;
    // 获得在办任务列表
    List<ActHiTaskinst> historicList = hiTaskinstPage.getRecords();

    //单独查询出流程实例
    Set<String> instanceIdSet = new HashSet<String>();
    Map<String,ProcessInstance> processInstanceMap = new HashMap<String,ProcessInstance>();
    Map<String,ActAljoinBpmnRun> instanceIdBpmnMap = new HashMap<String,ActAljoinBpmnRun>();
    if(historicList.size() > 0){
      for (ActHiTaskinst historicTaskInstance : historicList) {
        instanceIdSet.add(historicTaskInstance.getProcInstId());
      }
      List<ProcessInstance> piList = runtimeService.createProcessInstanceQuery().processInstanceIds(instanceIdSet).list();
      List<Long> bpmnIdList = new ArrayList<Long>();
      Map<String,Long> instanceIdBpmnIdMap = new HashMap<String,Long>();
      for (ProcessInstance processInstance : piList) {
        processInstanceMap.put(processInstance.getProcessInstanceId(), processInstance);
        String businessKey = processInstance.getBusinessKey();
        if(!StringUtils.isEmpty(businessKey)){
          String bpmnId = businessKey.split(",")[0];
          bpmnIdList.add(Long.parseLong(bpmnId));
          instanceIdBpmnIdMap.put(processInstance.getProcessInstanceId(), Long.parseLong(bpmnId));
        }
      }
      if(bpmnIdList.size() > 0){
        Where<ActAljoinBpmnRun> actAljoinBpmnWhere = new Where<ActAljoinBpmnRun>();
        actAljoinBpmnWhere.setSqlSelect("id,is_free");
        actAljoinBpmnWhere.in("id", bpmnIdList);
        List<ActAljoinBpmnRun> bpmnList  = actAljoinBpmnRunService.selectList(actAljoinBpmnWhere);
        for (Map.Entry<String,Long> entry : instanceIdBpmnIdMap.entrySet()) {
          //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
          for (ActAljoinBpmnRun actAljoinBpmn : bpmnList) {
            if(entry.getValue().equals(actAljoinBpmn.getId())){
              instanceIdBpmnMap.put(entry.getKey(), actAljoinBpmn);
            }
          }
        }
      }
    }

    if(historicList.size() > 0){
      //查询
      List<String> instanceIdList = new ArrayList<String>();

      for (ActHiTaskinst historicTaskInstance : historicList) {
        instanceIdList.add(historicTaskInstance.getProcInstId());
      }
      //任务实例
      Map<String, List<HistoricTaskInstance>> instanceIdTaskInstanceListMap = new HashMap<String, List<HistoricTaskInstance>>();
      List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceIdIn(instanceIdList).unfinished().list();
      for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
        List<HistoricTaskInstance> tempList = instanceIdTaskInstanceListMap.get(historicTaskInstance.getProcessInstanceId());
        if(tempList != null && tempList.size() > 0){
          tempList.add(historicTaskInstance);
          instanceIdTaskInstanceListMap.put(historicTaskInstance.getProcessInstanceId(), tempList);
        }else{
          List<HistoricTaskInstance> newList = new ArrayList<HistoricTaskInstance>();
          newList.add(historicTaskInstance);
          instanceIdTaskInstanceListMap.put(historicTaskInstance.getProcessInstanceId(), newList);
        }
      }
      //流程实例
      Map<String, HistoricProcessInstance> historicProcessInstanceMap = new HashMap<String, HistoricProcessInstance>();
      List<HistoricProcessInstance> historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery().processInstanceIds(instanceIdSet).list();
      for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
        historicProcessInstanceMap.put(historicProcessInstance.getId(), historicProcessInstance);
      }

      // 如果上的查询条件没有流程查询表的数据，在去取，如果上面的流程查询map中有数据，则不需要在去查询
      if (actAljoinQueryMap.size() == 0) {
        Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
        queryWhere.setSqlSelect(
          "process_instance_id,urgent_status,process_title,start_time,create_full_user_name,current_handle_full_user_name,process_category_ids");
        queryWhere.in("process_instance_id", instanceIdList);
        List<ActAljoinQuery> actAljoinQueryList = actAljoinQueryService.selectList(queryWhere);
        for (ActAljoinQuery actAljoinQuery : actAljoinQueryList) {
          actAljoinQueryMap.put(actAljoinQuery.getProcessInstanceId(), actAljoinQuery);
        }
      }

      // 一次性获取流程分类
      List<Long> categoryIdList = new ArrayList<Long>();
      Map<String, String> instanceIdCategoryNameMap = new HashMap<String, String>();

      for (Map.Entry<String, ActAljoinQuery> entry : actAljoinQueryMap.entrySet()) {
        ActAljoinQuery aq = entry.getValue();
        String categoryIds = aq.getProcessCategoryIds();
        if (!StringUtils.isEmpty(categoryIds)) {
          String[] categoryIdsArr = categoryIds.split(",");
          categoryIds = categoryIdsArr[categoryIdsArr.length - 1];
          instanceIdCategoryNameMap.put(entry.getKey(), categoryIds);
          categoryIdList.add(Long.parseLong(categoryIds));
        }

      }
      Where<ActAljoinCategory> actAljoinCategoryWhere = new Where<ActAljoinCategory>();
      actAljoinCategoryWhere.in("id", categoryIdList);
      actAljoinCategoryWhere.setSqlSelect("id,category_name");
      List<ActAljoinCategory> actAljoinCategoryList =
        actAljoinCategoryService.selectList(actAljoinCategoryWhere);
      for (ActAljoinCategory actAljoinCategory : actAljoinCategoryList) {
        for (Map.Entry<String, String> entry : instanceIdCategoryNameMap.entrySet()) {
          if ((actAljoinCategory.getId().toString()).equals(entry.getValue())) {
            instanceIdCategoryNameMap.put(entry.getKey(), actAljoinCategory.getCategoryName());
          }
        }
      }

      // 获取处理时间
      Where<ActHiTaskinst> hiTaskinstWhere = new Where<ActHiTaskinst>();
      hiTaskinstWhere.setSqlSelect("id_,end_time_,proc_inst_id_");
      hiTaskinstWhere.isNotNull("end_time_");
      hiTaskinstWhere.orderBy("end_time_",true);
      List<ActHiTaskinst> hiTaskinstList = actHiTaskinstService.selectList(hiTaskinstWhere);
      Map<String,ActHiTaskinst> taskinstMap = new HashMap<String, ActHiTaskinst>();
      for(ActHiTaskinst taskinst : hiTaskinstList){
        taskinstMap.put(taskinst.getProcInstId(),taskinst);
      }

      for (ActHiTaskinst historic : historicList) {
        // 当前流程的未完成的节点有可能有多个
        List<HistoricTaskInstance> doHistoricList = instanceIdTaskInstanceListMap.get(historic.getProcInstId());

        // 根据流程实例ID获取待办流程实例
        HistoricProcessInstance historicProcessInstance = historicProcessInstanceMap.get(historic.getProcInstId());

        ActAljoinQuery actAljoinQuery = actAljoinQueryMap.get(historic.getProcInstId());

        if(actAljoinQuery != null){
          Integer uegency = 1;
          AppDoTaskDO doTaskShowVO = new AppDoTaskDO();
          doTaskShowVO.setFormType(instanceIdCategoryNameMap.get(historic.getProcInstId())); // 流程类型
          doTaskShowVO.setProcessInstanceId(historic.getProcInstId());
          String str = historicProcessInstance.getBusinessKey();
          doTaskShowVO.setBusinessKey(str);

          String taskNames = "";
          if(doHistoricList != null){
            Set<String> defKeySet = new HashSet<String>();
            for (HistoricTaskInstance historicTaskInstance : doHistoricList) {
              //对于相同的节点的名称不需要重复获取(多节点实例会有这样的情况)
              if(!defKeySet.contains(historicTaskInstance.getTaskDefinitionKey())){
                taskNames += historicTaskInstance.getName() == null ? "" : historicTaskInstance.getName() + ",";
              }
           /*   if (!StringUtils.isEmpty(historicTaskInstance.getAssignee())) {
                //AutUser au = autUserService.selectById(historicTaskInstance.getAssignee());
                Where<AutUser> autUserWhere = new Where<AutUser>();
                autUserWhere.eq("id", historicTaskInstance.getAssignee());
                autUserWhere.setSqlSelect("full_name");
                AutUser au = autUserService.selectOne(autUserWhere);
                if(au != null){
                  String handleUserName = au.getFullName();
                  handleUserNames += handleUserName + ",";
                }
              }*/
              defKeySet.add(historicTaskInstance.getTaskDefinitionKey());
            }
          }
          doTaskShowVO
            .setLink(!"".equals(taskNames) ? (taskNames.substring(0, taskNames.length() - 1)) : "");// 环节

          doTaskShowVO.setCurrentAdmin(actAljoinQuery.getCurrentHandleFullUserName());// 当前班里人

          ActHiTaskinst actHiTaskinst = taskinstMap.get(historicProcessInstance.getId());
          doTaskShowVO.setProcessingDate(actHiTaskinst.getEndTime());// 处理时间

          doTaskShowVO.setUrgency(actAljoinQuery.getUrgentStatus());// 缓急
          doTaskShowVO.setTitle(actAljoinQuery.getProcessTitle());// 标题
          doTaskShowVO.setCreateDate(actAljoinQuery.getStartTime());// 填报时间(流程发起时间)
          doTaskShowVO.setFounder(actAljoinQuery.getCreateFullUserName());// 流程发起人
          if(actAljoinQuery.getUrgentStatus().equals(WebConstant.COMMONLY)){
            uegency = 1;
          }else if(actAljoinQuery.getUrgentStatus().equals(WebConstant.URGENT)){
            uegency = 2;
          }else if(actAljoinQuery.getUrgentStatus().equals(WebConstant.ADD_URENT)){
            uegency = 3;
          }
          doTaskShowVO.setUrgencyStatus(uegency);

          doTaskShowVO.setTaskId(historic.getId());
          doTaskShowVOList.add(doTaskShowVO);
        }else{
          count --;
        }
      }
    }
    sortList(doTaskShowVOList,obj);
    page.setRecords(doTaskShowVOList);
    page.setTotal(count);
    page.setCurrent(pageBean.getPageNum());
    page.setSize(pageBean.getPageSize());
    retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
    retMsg.setObject(page);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  /**
   *
   * 待办列表排序.
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017-12-02
   */
  public void sortList(List<DoTaskShowVO> list, DoTaskShowVO obj) {

    //填报时间升序
    if(!StringUtils.isEmpty(obj.getFillingDateIsAsc()) && obj.getFillingDateIsAsc().equals("1")){
      Comparator<DoTaskShowVO> doTaskShowVOComparator = new Comparator<DoTaskShowVO>() {
        @Override
        public int compare(DoTaskShowVO o1, DoTaskShowVO o2) {
          // 升序
          return o1.getFillingDate().compareTo(o2.getFillingDate());
        }
      };
      Collections.sort(list, doTaskShowVOComparator);
    }
    //填报时间降序
    if(!StringUtils.isEmpty(obj.getFillingDateIsAsc()) && obj.getFillingDateIsAsc().equals("0")){
      Comparator<DoTaskShowVO> doTaskShowVOComparator = new Comparator<DoTaskShowVO>() {
        @Override
        public int compare(DoTaskShowVO o1, DoTaskShowVO o2) {
          // 降序
          return o2.getFillingDate().compareTo(o1.getFillingDate());
        }
      };
      Collections.sort(list, doTaskShowVOComparator);
    }
    // 紧急 降序
    if (!StringUtils.isEmpty(obj.getUrgencyIsAsc()) && obj.getUrgencyIsAsc().equals("0")) {
      Comparator<DoTaskShowVO> doTaskShowVOComparator = new Comparator<DoTaskShowVO>() {
        @Override
        public int compare(DoTaskShowVO o1, DoTaskShowVO o2) {
          return o2.getUrgencyStatus().compareTo(o1.getUrgencyStatus());
        }
      };
      Collections.sort(list, doTaskShowVOComparator);
    } else if (!StringUtils.isEmpty(obj.getUrgencyIsAsc()) && obj.getUrgencyIsAsc().equals("1")){// 紧急 升序
      Comparator<DoTaskShowVO> doTaskShowVOComparator = new Comparator<DoTaskShowVO>() {
        @Override
        public int compare(DoTaskShowVO o1, DoTaskShowVO o2) {
          return o1.getUrgencyStatus().compareTo(o2.getUrgencyStatus());
        }
      };
      Collections.sort(list, doTaskShowVOComparator);
    }
  }

  /*
   * 构造在查询条件
   */
  private int makeQueryWhere(Where<ActAljoinQuery> where, AppDoTaskVO obj) throws Exception{
    int flag = 0;
    if (!StringUtils.isEmpty(obj.getTitle())) {
      where.like("process_title", obj.getTitle());
      flag++;
    }

    if (!StringUtils.isEmpty(obj.getUrgency())) {
      where.eq("urgent_status", obj.getUrgency());
      flag++;
    }

    if (!StringUtils.isEmpty(obj.getFounder())) {
      where.like("create_full_user_name", obj.getFounder());
      flag++;
    }

    if (!StringUtils.isEmpty(obj.getFormTypeId())) {
      List<Long> categorySet = new ArrayList<Long>();
      categorySet = getChildCategoryIds(categorySet, Long.valueOf(obj.getFormTypeId()));
      int i = 0;
      for (Long set : categorySet) {
        if(i == 0){
          where.andNew("process_category_ids like {0}", "%"+set+"%");
        }else if(i > 0){
          where.or("process_category_ids like {0}", "%"+set+"%");
        }
        i++;
      }
      flag++;
    }
    if (!StringUtils.isEmpty(obj.getCreateBegTime()) && StringUtils.isEmpty(obj.getCreateEndTime())) {
      where.ge("create_time", DateUtil.str2dateOrTime(obj.getCreateBegTime()));
      flag++;
    }
    if (StringUtils.isEmpty(obj.getCreateBegTime()) && !StringUtils.isEmpty(obj.getCreateEndTime())) {
      where.le("create_time", DateUtil.str2dateOrTime(obj.getCreateEndTime()));
      flag++;
    }
    if (!StringUtils.isEmpty(obj.getCreateBegTime()) && !StringUtils.isEmpty(obj.getCreateEndTime())) {
      DateTime dateTime = new DateTime(obj.getCreateBegTime());
      DateTime dateTime2 = new DateTime(obj.getCreateEndTime());
      where.between("create_time", dateTime.toDate(),dateTime2.plusDays(1).toDate());
      flag++;
    }
    return flag;
  }

  /**
   *
   * 根据父级分类ID获得所有子分类
   *
   * @return：String
   *
   * @author：wangj
   *
   * @date：2017-12-19
   */
  private List<Long> getChildCategoryIds(List<Long> categorySet,Long categoryId){
    ActAljoinCategory actAljoinCategory = actAljoinCategoryService.selectById(categoryId);
    List<Long> categoryIdList = new ArrayList<Long>();
    if(null != actAljoinCategory){
      categoryIdList.add(actAljoinCategory.getId());

      //获取原来的值和现在的值
      categorySet.addAll(categoryIdList);

      //获取子分类
      Where<ActAljoinCategory> categoryWhere = new Where<ActAljoinCategory>();
      categoryWhere.setSqlSelect("id,parent_id");
      categoryWhere.eq("parent_id",actAljoinCategory.getId());
      categoryWhere.eq("is_active",1);
      List<ActAljoinCategory> categoryList = actAljoinCategoryService.selectList(categoryWhere);
      if(null != categoryList && !categoryList.isEmpty()){
        for(ActAljoinCategory category : categoryList){
          getChildCategoryIds(categorySet,category.getId());
        }
      }
    }
    Map<Long,Long> map = new HashMap<Long,Long>();
    for(Long cid : categorySet){
      map.put(cid,cid);
    }
    categorySet.clear();
    for(Long key : map.keySet()){
      categorySet.add(map.get(key));
    }
    Collections.sort(categorySet);
    return categorySet;
  }

  /**
   *
   * 待办列表排序.
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017-12-02
   */
  public void sortList(List<AppDoTaskDO> list, AppDoTaskVO obj) {

    //填报时间升序
    if(!StringUtils.isEmpty(obj.getCreateDateIsAsc()) && obj.getCreateDateIsAsc().equals("1")){
      Comparator<AppDoTaskDO> doTaskShowVOComparator = new Comparator<AppDoTaskDO>() {
        @Override
        public int compare(AppDoTaskDO o1, AppDoTaskDO o2) {
          // 升序
          return o1.getCreateDate().compareTo(o2.getCreateDate());
        }
      };
      Collections.sort(list, doTaskShowVOComparator);
    }
    //填报时间降序
    if(!StringUtils.isEmpty(obj.getCreateDateIsAsc()) && obj.getCreateDateIsAsc().equals("0")){
      Comparator<AppDoTaskDO> doTaskShowVOComparator = new Comparator<AppDoTaskDO>() {
        @Override
        public int compare(AppDoTaskDO o1, AppDoTaskDO o2) {
          // 降序
          return o2.getCreateDate().compareTo(o1.getCreateDate());
        }
      };
      Collections.sort(list, doTaskShowVOComparator);
    }
    // 紧急 降序
    if (!StringUtils.isEmpty(obj.getUrgencyIsAsc()) && obj.getUrgencyIsAsc().equals("0")) {
      Comparator<AppDoTaskDO> doTaskShowVOComparator = new Comparator<AppDoTaskDO>() {
        @Override
        public int compare(AppDoTaskDO o1, AppDoTaskDO o2) {
          return o2.getUrgencyStatus().compareTo(o1.getUrgencyStatus());
        }
      };
      Collections.sort(list, doTaskShowVOComparator);
    } else if (!StringUtils.isEmpty(obj.getUrgencyIsAsc()) && obj.getUrgencyIsAsc().equals("1")){// 紧急 升序
      Comparator<AppDoTaskDO> doTaskShowVOComparator = new Comparator<AppDoTaskDO>() {
        @Override
        public int compare(AppDoTaskDO o1, AppDoTaskDO o2) {
          return o1.getUrgencyStatus().compareTo(o2.getUrgencyStatus());
        }
      };
      Collections.sort(list, doTaskShowVOComparator);
    }
  }
}
