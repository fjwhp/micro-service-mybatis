package aljoin.ioa.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinFixedConfig;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinFixedConfigService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.entity.AutUserRank;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserRankService;
import aljoin.aut.iservice.AutUserRoleService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.ioa.dao.entity.IoaReceiveFile;
import aljoin.ioa.dao.entity.IoaReceiveReadObject;
import aljoin.ioa.dao.entity.IoaReceiveReadUser;
import aljoin.ioa.dao.mapper.IoaReceiveFileMapper;
import aljoin.ioa.dao.object.IoaReceiveFileDO;
import aljoin.ioa.dao.object.IoaReceiveFileVO;
import aljoin.ioa.dao.object.IoaReceiveReadObjectDO;
import aljoin.ioa.iservice.IoaReceiveFileService;
import aljoin.ioa.iservice.IoaReceiveReadObjectService;
import aljoin.ioa.iservice.IoaReceiveReadUserService;
import aljoin.object.AppConstant;
import aljoin.object.CustomerTaskDefinition;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.dao.entity.SysDataDict;
import aljoin.sys.iservice.SysDataDictService;
import aljoin.util.DateUtil;

/**
 * 收文阅件表(服务实现类).
 * 
 * @author：zhongjy
 * @date： 2017-11-08
 */
@Service
public class IoaReceiveFileServiceImpl extends ServiceImpl<IoaReceiveFileMapper, IoaReceiveFile>
    implements IoaReceiveFileService {

  @Resource
  private IoaReceiveFileMapper mapper;
  @Resource
  private ResResourceService resResourceService;
  @Resource
  private ActActivitiService activitiService;
  @Resource
  private ActAljoinBpmnService actAljoinBpmnService;
  @Resource
  private ActAljoinFixedConfigService actAljoinFixedConfigService;
  @Resource
  private TaskService taskService;
  @Resource
  private AutUserService autUserService;
  @Resource
  private IoaReceiveReadUserService ioaReceiveReadUserService;
  @Resource
  private IoaReceiveReadObjectService ioaReceiveReadObjectService;
  @Resource
  private SysDataDictService sysDataDictService;
  @Resource
  private AutDepartmentUserService autDepartmentUserService;
  @Resource
  private AutDepartmentService autDepartmentService;
  @Resource
  private HistoryService historyService;
  @Resource
  private RuntimeService runtimeService;
  @Resource
  private AutUserPositionService autUserPositionService;
  @Resource
  private ActFixedFormServiceImpl actFixedFormService;
  @Resource
  private AutUserRoleService autUserRoleService;
  @Resource
  private ProcessEngineConfigurationImpl processEngineConfigurationImpl;
  @Resource
  private ActAljoinQueryService actAljoinQueryService;
  @Resource
  private ActActivitiService actActivitiService;
  @Resource
  private IdentityService identityService;
  @Resource
  private ActAljoinQueryHisService actAljoinQueryHisService;
  @Resource
  private AutUserRankService autUserRankService;

  @Override
  public Page<IoaReceiveFileDO> toReadList(PageBean pageBean, IoaReceiveFileVO obj, String userId)
      throws Exception {
    Page<IoaReceiveFileDO> page = new Page<IoaReceiveFileDO>();
    Where<IoaReceiveFile> where = new Where<IoaReceiveFile>();

    // 收文阅卷流程
    String processId = "";
    Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
    configWhere.setSqlSelect("id,process_id");
    configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_READ_RECEIPT);
    List<ActAljoinFixedConfig> configList = actAljoinFixedConfigService.selectList(configWhere);
    processId = configList.get(0).getProcessId();
    // 查询出自己作为候选或者已经签收的收文阅卷任务
    List<Task> readTaskList = taskService.createTaskQuery().taskCandidateOrAssigned(userId)
        .processDefinitionKeyLike(processId).list();
    Set<String> proinstId = new HashSet<String>();

    // 当前无待阅任务
    if (readTaskList.size() == 0) {
      return page;
    }
    for (Task task : readTaskList) {
      proinstId.add(task.getProcessInstanceId());
    }
    /*
     * instanceList = historyService.createHistoricTaskInstanceQuery()
     * .processInstanceIdIn(processInstanceIdList).unfinished().list(); for (HistoricTaskInstance
     * historicTaskInstance : instanceList) {
     * historicTaskInstanceMap.put(historicTaskInstance.getProcessInstanceId(),
     * historicTaskInstance); }
     */
    // 收文阅卷流程未设置
    if (null == configList || configList.isEmpty()) {
      return page;
    }
    if (null != obj) {
      if (StringUtils.isNotEmpty(obj.getFileType())) {
        where.eq("file_type", obj.getFileType());
      }
      if (StringUtils.isNotEmpty(obj.getFromUnit())) {
        where.eq("from_unit", obj.getFromUnit());
      }
      if (StringUtils.isNotEmpty(obj.getFileTitle())) {
        where.like("file_title", obj.getFileTitle());
      }
      if (StringUtils.isNotEmpty(obj.getFromFileCode())) {
        where.like("from_file_code", obj.getFromFileCode());
      }
      if (StringUtils.isNotEmpty(obj.getUrgentLevel())) {
        where.eq("urgent_level", obj.getUrgentLevel());
      }
      if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isEmpty(obj.getEndDate())) {
        where.ge("create_time", DateUtil.str2dateOrTime(obj.getBegDate()));
      }
      if (StringUtils.isEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        where.le("create_time", DateUtil.str2dateOrTime(obj.getEndDate()));
      }
      if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        DateTime dateTime = new DateTime(obj.getEndDate());
        where.between("create_time", DateUtil.str2dateOrTime(obj.getBegDate()), DateUtil
            .str2dateOrTime(dateTime.plusDays(1).toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
      }
      if (StringUtils.isNotEmpty(obj.getSearchKey())) {
        where.andNew(" from_file_code like {0} and file_title like {1}",
            "%" + obj.getSearchKey() + "%", "%" + obj.getSearchKey() + "%");
      }
    }
    where.in("process_instance_id", proinstId);
    where.eq("is_close", 0);
    // where.eq("create_user_id",userId);
    where.setSqlSelect(
        "id,create_time,from_file_code,file_title,file_type_name,urgent_level_name,process_id,bpmn_id,process_instance_id");

    if (obj.getFillingTimeIsAsc() != null && obj.getFillingTimeIsAsc().equals("1")) {
      where.orderBy("id", true);
    } else {
      where.orderBy("id", false);
    }
    // List<IoaReceiveFile> receiveFileList = selectList(where);
    // Map<String, IoaReceiveFile> ioaReceiveFileMap = new HashMap<String, IoaReceiveFile>();
    // List<IoaReceiveFileDO> receiveFileDOList = new ArrayList<IoaReceiveFileDO>();
    // if (null != receiveFileList && !receiveFileList.isEmpty()) {
    // for (IoaReceiveFile ioaReceiveFile : receiveFileList) {
    // processInstanceIdList.add(ioaReceiveFile.getProcessInstanceId());
    // ioaReceiveFileMap.put(ioaReceiveFile.getProcessInstanceId(), ioaReceiveFile);
    // }
    // }
    // List<HistoricTaskInstance> instanceList = new ArrayList<HistoricTaskInstance>();
    // Map<String, HistoricTaskInstance> historicTaskInstanceMap =
    // new HashMap<String, HistoricTaskInstance>();
    List<IoaReceiveFileDO> receiveFileDOList = new ArrayList<IoaReceiveFileDO>();
    Page<IoaReceiveFile> receivePage =
        selectPage(new Page<IoaReceiveFile>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    List<IoaReceiveFile> receiveFileList = receivePage.getRecords();
    for (IoaReceiveFile receiveFile : receiveFileList) {
      IoaReceiveFileDO ioaReceiveFileDO = new IoaReceiveFileDO();
      BeanUtils.copyProperties(receiveFile, ioaReceiveFileDO);
      // DateTime dateTime = new
      // DateTime(historicTaskInstance.getStartTime());
      DateTime dateTime = new DateTime(receiveFile.getCreateTime());
      ioaReceiveFileDO.setFillingTime(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
      for (Task task : readTaskList) {
        // 只有收文阅卷可以这么干，因为没有并行节点任务
        if (task.getProcessInstanceId().equals(receiveFile.getProcessInstanceId())) {
          ioaReceiveFileDO.setLinkName(task.getName() == null ? "" : task.getName());
        }
      }
      receiveFileDOList.add(ioaReceiveFileDO);
    }
    // sortList(receiveFileDOList, obj);
    page.setRecords(receiveFileDOList);
    page.setSize(receivePage.getSize());
    page.setCurrent(receivePage.getCurrent());
    page.setTotal(receivePage.getTotal());
    return page;
  }

  @Override
  public Page<IoaReceiveFileDO> inReadList(PageBean pageBean, IoaReceiveFileVO obj, String userId)
      throws Exception {
    Page<IoaReceiveFileDO> page = new Page<IoaReceiveFileDO>();
    Set<String> processIds1 = new HashSet<String>();
    // 收文阅卷流程
    String processId = "";
    Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
    configWhere.setSqlSelect("id,process_id");
    configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_READ_RECEIPT);
    List<ActAljoinFixedConfig> configList = actAljoinFixedConfigService.selectList(configWhere);
    // 收文阅卷流程未设置
    if (null == configList || configList.isEmpty()) {
      return page;
    }

    processId = configList.get(0).getProcessId();
    // 查出用户完成的收文阅卷流程任务
    List<HistoricTaskInstance> finishTaskProcess =
        historyService.createHistoricTaskInstanceQuery().finished().taskAssignee(userId)
            .processUnfinished().processDefinitionKeyLike(processId).list();
    // 用户没有完成的收文阅卷流程任务
    if (finishTaskProcess.isEmpty()) {
      return page;
    }
    for (HistoricTaskInstance finish : finishTaskProcess) {
      processIds1.add(finish.getProcessInstanceId());
    }
    // 查出该流程user参与的最新的任务环节
    // List<Task> currentTask = taskService.createTaskQuery().taskCandidateOrAssigned(userId)
    // .processInstanceIdIn(processIds1).list();
    // for (HistoricTaskInstance finished : finishTaskProcess) {
    // Boolean isunfinishedtask = false;// 这个流程的当前任务是否是user待办
    // for (Task curtask : currentTask) {
    // if (finished.getProcessInstanceId().equals(curtask.getProcessInstanceId())) {
    // isunfinishedtask = true;
    // break;
    // }
    // }
    // if (!isunfinishedtask) {
    // processIds.add(finished.getProcessInstanceId());
    // }
    //
    // }

    Where<IoaReceiveFile> where = new Where<IoaReceiveFile>();
    List<String> processInstanceIdList = new ArrayList<String>();
    if (null != obj) {
      where.in("process_instance_id", processIds1);
      if (StringUtils.isNotEmpty(obj.getFileType())) {
        where.eq("file_type", obj.getFileType());
      }
      if (StringUtils.isNotEmpty(obj.getFromUnit())) {
        // where.eq("from_unit", obj.getFromUnit());
        where.like("from_unit_name", obj.getFromUnit());
      }
      if (StringUtils.isNotEmpty(obj.getFileTitle())) {
        where.like("file_title", obj.getFileTitle());
      }
      if (StringUtils.isNotEmpty(obj.getFromFileCode())) {
        where.like("from_file_code", obj.getFromFileCode());
      }
      if (StringUtils.isNotEmpty(obj.getUrgentLevel())) {
        where.eq("urgent_level", obj.getUrgentLevel());
      }
      if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isEmpty(obj.getEndDate())) {
        where.ge("create_time", DateUtil.str2dateOrTime(obj.getBegDate()));
      }
      if (StringUtils.isEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        where.le("create_time", DateUtil.str2dateOrTime(obj.getEndDate()));
      }
      if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        DateTime dateTime = new DateTime(obj.getEndDate());
        where.between("create_time", DateUtil.str2dateOrTime(obj.getBegDate()), DateUtil
            .str2dateOrTime(dateTime.plusDays(1).toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
      }
      where.eq("is_close", 0);
      if (StringUtils.isNotEmpty(obj.getSearchKey())) {
        where.andNew(" from_file_code like {0} or file_title like {1}",
            "%" + obj.getSearchKey() + "%", "%" + obj.getSearchKey() + "%");
      }
    }
    where.setSqlSelect(
        "id,from_file_code,file_title,file_type_name,urgent_level_name,process_id,bpmn_id,process_instance_id,create_time");
    if (obj.getFillingTimeIsAsc() != null && obj.getFillingTimeIsAsc().equals("1")) {
      where.orderBy("id", true);
    } else {
      where.orderBy("id", false);
    }
    Page<IoaReceiveFile> receivePage =
        selectPage(new Page<IoaReceiveFile>(pageBean.getPageNum(), pageBean.getPageSize()), where);

    List<IoaReceiveFile> receiveFileList = receivePage.getRecords();
    Map<String, IoaReceiveFile> ioaReceiveFileMap = new HashMap<String, IoaReceiveFile>();
    if (null != receiveFileList && !receiveFileList.isEmpty()) {
      for (IoaReceiveFile ioaReceiveFile : receiveFileList) {
        processInstanceIdList.add(ioaReceiveFile.getProcessInstanceId());
        ioaReceiveFileMap.put(ioaReceiveFile.getProcessInstanceId(), ioaReceiveFile);
      }
    }
    // 如果id列表为空，说明没有符合条件的记录
    if (processInstanceIdList.isEmpty()) {
      return page;
    }
    // 查出该流程最新的任务环节
    List<Task> unfininshTaskProcess =
        taskService.createTaskQuery().processInstanceIdIn(processInstanceIdList).list();
    // 要让记录只有一条，就得以历史流程为主要依据
    Set<String> result = new HashSet<String>(processInstanceIdList);
    HistoricProcessInstanceQuery instance =
        historyService.createHistoricProcessInstanceQuery().processInstanceIds(result);
    /*
     * if (obj.getFillingTimeIsAsc() != null && obj.getFillingTimeIsAsc().equals("1")) {
     * instance.orderByProcessInstanceId().asc(); } else {
     * instance.orderByProcessInstanceId().desc(); }
     */
    List<HistoricProcessInstance> historicList1 = instance.list();
    List<IoaReceiveFileDO> receiveFileDOList = new ArrayList<IoaReceiveFileDO>();
    for (IoaReceiveFile ioaReceiveFile : receiveFileList) {
      IoaReceiveFileDO receiveFileDO = new IoaReceiveFileDO();
      BeanUtils.copyProperties(ioaReceiveFile, receiveFileDO);
      DateTime dateTime = new DateTime(ioaReceiveFile.getCreateTime());
      receiveFileDO.setFillingTime(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
      for (HistoricProcessInstance historicProcessInstance : historicList1) {
        if (ioaReceiveFile.getProcessInstanceId().equals(historicProcessInstance.getId())) {
          for (Task task : unfininshTaskProcess) {
            if (task.getProcessInstanceId().equals(historicProcessInstance.getId())) {
              receiveFileDO.setLinkName(task.getName());
            }
          }
        }
      }
      receiveFileDOList.add(receiveFileDO);
    }
    int count = (int) receivePage.getTotal();
    page.setRecords(receiveFileDOList);
    page.setTotal(count);
    page.setSize(pageBean.getPageSize());
    return page;
  }

  @Override
  public void physicsDeleteById(Long id) throws Exception {
    mapper.physicsDeleteById(id);
  }

  @Override
  public void copyObject(IoaReceiveFile obj) throws Exception {
    mapper.copyObject(obj);
  }

  @SuppressWarnings("unused")
  @Transactional
  @Override
  public RetMsg update(IoaReceiveFileVO obj) throws Exception {
    RetMsg retMsg = new RetMsg();
    if (null != obj && null != obj.getId()) {
      Boolean istitleChange = false;
      IoaReceiveFile ioaReceiveFile = selectById(obj.getId());
      if (null != ioaReceiveFile) {
        if (StringUtils.isNotEmpty(obj.getFileTitle())) {
          istitleChange = true;
          ioaReceiveFile.setFileTitle(obj.getFileTitle());
        }
        if (StringUtils.isNotEmpty(obj.getFileType())) {
          ioaReceiveFile.setFileType(obj.getFileType());
          Where<SysDataDict> where = new Where<SysDataDict>();
          where.eq("dict_key", obj.getFileType());
          where.eq("dict_code", WebConstant.DICT_RECEIVE_FILE_TYPE);
          where.setSqlSelect("id,dict_key,dict_value");
          SysDataDict allDict = sysDataDictService.selectOne(where);
          if (allDict != null) {
            ioaReceiveFile.setFileTypeName(allDict.getDictValue());
          }
        }
        if (StringUtils.isNotEmpty(obj.getFromUnit())) {
          ioaReceiveFile.setFromUnit(obj.getFromUnit());
          ioaReceiveFile.setFromUnitName(obj.getFromUnit());
        }
        /*
         * if (StringUtils.isNotEmpty(obj.getFromUnitName())) {
         * ioaReceiveFile.setFromUnitName(obj.getFromUnitName()); }
         */
        if (StringUtils.isNotEmpty(obj.getFromFileCode())) {
          ioaReceiveFile.setFromFileCode(obj.getFromFileCode());
        }
        if (StringUtils.isNotEmpty(obj.getReceiveFileCode())) {
          ioaReceiveFile.setReceiveFileCode(obj.getReceiveFileCode());
        }
        // Date date = new Date();
        if (null != obj.getOrgnlFileTime()) {
          ioaReceiveFile.setOrgnlFileTime(obj.getOrgnlFileTime());
        }
        if (null != obj.getReceiveFileTime()) {
          ioaReceiveFile.setReceiveFileTime(obj.getReceiveFileTime());
        }
        if (null != obj.getHandleLimitTime()) {
          ioaReceiveFile.setHandleLimitTime(obj.getHandleLimitTime());
        }
        if (obj.getUrgentLevel()!=null) {
          ioaReceiveFile.setUrgentLevel(obj.getUrgentLevel());
          /*Where<SysDataDict> where = new Where<SysDataDict>();
          where.eq("dict_code", WebConstant.DICT_URGENT_LEVEL);
          where.eq("dict_key", obj.getUrgentLevel());
          where.setSqlSelect("id,dict_code,dict_name,dict_key,dict_value,dict_rank");
          where.orderBy("dict_rank");
          SysDataDict urgent = sysDataDictService.selectOne(where);
          if(null != urgent){
            ioaReceiveFile.setUrgentLevelName(urgent.getDictValue());
          }*/
        }
        if (obj.getUrgentLevelName()!=null) {
          ioaReceiveFile.setUrgentLevelName(obj.getUrgentLevelName());
        }
        if (StringUtils.isNotEmpty(obj.getOfficeOpinion())) {
          ioaReceiveFile.setOfficeOpinion(obj.getOfficeOpinion());
        }
        if (null != obj.getIsClose()) {
          ioaReceiveFile.setIsClose(obj.getIsClose());
        }
        if (StringUtils.isNotEmpty(obj.getReadUserIds())) {
          ioaReceiveFile.setReadUserIds(obj.getReadUserIds());
        }
        if (StringUtils.isEmpty(obj.getProcessId())) {
          ioaReceiveFile.setProcessId(obj.getProcessId());
        }
        if (null == obj.getBpmnId()) {
          ioaReceiveFile.setBpmnId(obj.getBpmnId());
        }
        if (StringUtils.isEmpty(obj.getProcessInstanceId())) {
          ioaReceiveFile.setProcessInstanceId(obj.getProcessInstanceId());
        }
      }
      if(ioaReceiveFile.getId()!=null){
    	 Where <IoaReceiveFile> flieWhere=new Where<IoaReceiveFile>();
    	 flieWhere.eq("id", ioaReceiveFile.getId());
      IoaReceiveFile ifile= this.selectOne(flieWhere);
      if(ifile.getProcessInstanceId()!=null){
      Where<ActAljoinQuery> querywhere = new Where<ActAljoinQuery>();
      querywhere.eq("process_instance_id", ifile.getProcessInstanceId());
      ActAljoinQuery actAljoinQuery = actAljoinQueryService.selectOne(querywhere);
      if(null != actAljoinQuery){
        actAljoinQuery.setProcessTitle(ioaReceiveFile.getFileTitle());
        actAljoinQueryService.updateById(actAljoinQuery);
        Where<ActAljoinQueryHis> querywhereHis = new Where<ActAljoinQueryHis>();
        querywhereHis.eq("process_instance_id", ifile.getProcessInstanceId());
        ActAljoinQueryHis actAljoinQueryHis = actAljoinQueryHisService.selectOne(querywhereHis);
        actAljoinQueryHis.setProcessTitle(ioaReceiveFile.getFileTitle());
        actAljoinQueryHisService.updateById(actAljoinQueryHis);
       }
      }
      //如果标题改了，更新query表中的 标题
      //if(istitleChange && StringUtils.isNotEmpty(ioaReceiveFile.getProcessInstanceId())){
       
        //}
      }
      // 修改收文阅件
      updateById(ioaReceiveFile);
      
      // 保存附件
      List<ResResource> newResourceList = obj.getResResourceList();
      List<Long> newResourceIds = new ArrayList<Long>();
      if (null != newResourceList && newResourceList.size() > 0) {
          for (ResResource resResource : newResourceList) {
              newResourceIds.add(resResource.getId());
          }
      }
      if (null != newResourceIds && newResourceIds.size() > 0) {
          List<ResResource> addResource = resResourceService.selectBatchIds(newResourceIds);
          for (ResResource resResource : addResource) {
              resResource.setBizId(ioaReceiveFile.getId());
              resResource.setFileDesc("待阅文件详情附件上传");
          }
          resResourceService.updateBatchById(addResource);
      }
      
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  @Transactional
  public RetMsg delete(IoaReceiveFile obj) throws Exception {
    RetMsg retMsg = new RetMsg();
    if (null != obj && null != obj.getId()) {
      IoaReceiveFile ioaReceiveFile = selectById(obj.getId());
      if (null != ioaReceiveFile) {
        deleteById(obj.getId());
        
        //删除附件(暂时不删除)
        /*Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id",obj.getId());
        List<ResResource> resourcesList = resResourceService.selectList(where);
        List<Long> resourcesIds = new ArrayList<Long>();
        for (ResResource resResource : resourcesList) {
            resourcesIds.add(resResource.getId());
        }
        if(null != resourcesList && !resourcesList.isEmpty()){
            resResourceService.deleteBatchById(resourcesIds);
        }*/
      }
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public Map<String, Object> detail(IoaReceiveFile obj) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    Boolean isAssignee = false; // 当前用户是否该文件签收人
    IoaReceiveFileDO ioaReceiveFileVO = null;
    if (null != obj && null != obj.getId()) {
      IoaReceiveFile ioaReceiveFile = selectById(obj.getId());
      if (null != ioaReceiveFile) {
        ioaReceiveFileVO = new IoaReceiveFileDO();
        BeanUtils.copyProperties(ioaReceiveFile, ioaReceiveFileVO);
        Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id", obj.getId());
        List<ResResource> resourceList = resResourceService.selectList(where);
        if (null != resourceList && !resourceList.isEmpty()) {
            ioaReceiveFileVO.setResResourceList(resourceList);
          }      
        if (null != ioaReceiveFile.getProcessInstanceId()) {
          List<Task> tskList = taskService.createTaskQuery()
              .processInstanceId(ioaReceiveFile.getProcessInstanceId()).list();
          if (null != tskList && !tskList.isEmpty()) {
            Task task = tskList.get(0);
            if (null != task) {
              Task tsk = taskService.createTaskQuery().taskId(task.getId()).singleResult();
              if (null != tsk) {
                String isClaim =
                    taskService.createTaskQuery().taskId(task.getId()).singleResult().getAssignee();
                HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                if (null == hisTask.getClaimTime() || StringUtils.isEmpty(hisTask.getAssignee())) {
                  // 文件未被签收，被当前用户签收，当前用户是该环节办理人
                  isAssignee = true;
                  taskService.claim(task.getId(), obj.getCreateUserId() + "");
                  String userFullName = "";
                  if (null != task.getAssignee()) {
                    userFullName = autUserService.selectById(task.getAssignee()).getFullName();
                  }
                  actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);
                } else {
                  if (isClaim.equals(String.valueOf(obj.getCreateUserId()))) {
                    isAssignee = true;
                  }
                }
              }
            }
          }
        }
      }
    }
    map.put("isAssignee", isAssignee);
    map.put("ioaReceiveFileVO", ioaReceiveFileVO);
    return map;
  }

  @Override
  @Transactional
  public RetMsg directorAudit(IoaReceiveFileVO obj) throws Exception {// 池主任审批（提交或保存）
    RetMsg retMsg = new RetMsg();
    if (null != obj && null != obj.getId()) {
      IoaReceiveFile ioaReceiveFile = selectById(obj.getId());
      if (null != ioaReceiveFile) {
        if (StringUtils.isNotEmpty(obj.getOfficeOpinion())) {// 办公室拟办意见officeOpinion
          ioaReceiveFile.setOfficeOpinion(obj.getOfficeOpinion());
        }
        Where<IoaReceiveReadObject> objectWhere = new Where<IoaReceiveReadObject>();
        objectWhere.setSqlSelect("id,receive_file_id");
        objectWhere.eq("receive_file_id", obj.getId());
        List<IoaReceiveReadObject> objectList = ioaReceiveReadObjectService.selectList(objectWhere);
        if (null != objectList && !objectList.isEmpty()) {
          // 旧的通通删掉
          List<Long> ids = new ArrayList<Long>();
          Where<IoaReceiveReadUser> where1 = new Where<IoaReceiveReadUser>();
          where1.in("object_id", ids);
          where1.eq("receive_file_id", obj.getId());
          ioaReceiveReadUserService.delete(where1);
        }
        // 配合前端修改（前端每次传的都是完整的所选择的所有人）
        List<IoaReceiveReadObject> readObjectList = obj.getReadObjectList();
        List<IoaReceiveReadObject> receiveReadObjectList = new ArrayList<IoaReceiveReadObject>();
        List<IoaReceiveReadUser> readUserList = new ArrayList<IoaReceiveReadUser>();
        Set<Long> userIdList = new HashSet<Long>();
        String receiveUserIds = "";
        List<String> readUserIdList = new ArrayList<String>();
        List<String> readUserNameList = new ArrayList<String>();
        if (null != readObjectList && !readObjectList.isEmpty()) {
          for (IoaReceiveReadObject ioaReceiveReadObject : readObjectList) {
            if (null != ioaReceiveReadObject
                && StringUtils.isNotEmpty(ioaReceiveReadObject.getReadUserNames())) {
              IoaReceiveReadObject object = new IoaReceiveReadObject();
              String readUserIds = "";
              if (StringUtils.isNotEmpty(ioaReceiveReadObject.getReadUserIds())) {
                readUserIds = ioaReceiveReadObject.getReadUserIds();
                receiveUserIds += ioaReceiveReadObject.getReadUserIds();
              }

              String readUserNames = "";
              if (StringUtils.isNotEmpty(ioaReceiveReadObject.getReadUserNames())) {
                readUserNames = ioaReceiveReadObject.getReadUserNames();
              }
              object.setObjectId(null != ioaReceiveReadObject.getObjectId()
                  ? ioaReceiveReadObject.getObjectId() : 0L);
              object.setObjectName(StringUtils.isNotEmpty(ioaReceiveReadObject.getObjectName())
                  ? ioaReceiveReadObject.getObjectName() : "");
              object.setReceiveFileId(ioaReceiveFile.getId());
              receiveReadObjectList.add(object);
              if (StringUtils.isNotEmpty(readUserIds) && StringUtils.isNotEmpty(readUserNames)) {
                object.setReadUserIds(readUserIds);
                object.setReadUserNames(readUserNames);
              }
              if (StringUtils.isNotEmpty(obj.getIsCheckAllUser())
                  && obj.getIsCheckAllUser().equals("1")) {
                object.setReadUserIds("");
                object.setReadUserNames("");
              }
              ioaReceiveReadObjectService.insert(object);
              if (readUserIds.indexOf(";") > -1 && readUserNames.indexOf(";") > -1) {
                readUserIdList = Arrays.asList(readUserIds.split(";"));
                readUserNameList = Arrays.asList(readUserNames.split(";"));
                if (null != readUserIdList && !readUserIdList.isEmpty()) {
                  int i = 0;
                  for (String readUserId : readUserIdList) {
                    IoaReceiveReadUser ioaReceiveReadUser = new IoaReceiveReadUser();
                    ioaReceiveReadUser.setIsRead(0);
                    ioaReceiveReadUser.setReceiveFileId(ioaReceiveFile.getId());
                    ioaReceiveReadUser.setReceiveReadObjectId(object.getId());
                    ioaReceiveReadUser.setReadUserId(Long.valueOf(readUserId));
                    userIdList.add(ioaReceiveReadUser.getReadUserId());
                    ioaReceiveReadUser.setReadUserFullName(readUserNameList.get(i));
                    readUserList.add(ioaReceiveReadUser);
                    i++;
                  }
                }
              }
              if (StringUtils.isNotEmpty(obj.getIsCheckAllUser())
                  && obj.getIsCheckAllUser().equals("1")) {
                Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
                departmentUserWhere.setSqlSelect("id,dept_id,user_id");
                List<AutDepartmentUser> departmentUserList =
                    autDepartmentUserService.selectList(departmentUserWhere);
                Set<Long> uIdList = new HashSet<Long>();
                if (null != departmentUserList && !departmentUserList.isEmpty()) {
                  for (AutDepartmentUser departmentUser : departmentUserList) {
                    uIdList.add(departmentUser.getUserId());
                  }
                }
                if (null != uIdList && !uIdList.isEmpty()) {
                  Where<AutUser> userWhere = new Where<AutUser>();
                  userWhere.setSqlSelect("id,user_name,full_name");
                  userWhere.in("id", uIdList);
                  List<AutUser> userList = autUserService.selectList(userWhere);
                  if (null != userList && !userList.isEmpty()) {
                    for (AutUser user : userList) {
                      IoaReceiveReadUser ioaReceiveReadUser = new IoaReceiveReadUser();
                      ioaReceiveReadUser.setIsRead(0);
                      ioaReceiveReadUser.setReceiveFileId(ioaReceiveFile.getId());
                      ioaReceiveReadUser.setReceiveReadObjectId(object.getId());
                      ioaReceiveReadUser.setReadUserId(user.getId());
                      userIdList.add(user.getId());
                      ioaReceiveReadUser.setReadUserFullName(user.getFullName());
                      readUserList.add(ioaReceiveReadUser);
                    }
                  }
                }
              }
            }
          }
        }
        if (StringUtils.isNotEmpty(receiveUserIds)) {
          ioaReceiveFile.setReadUserIds(receiveUserIds);
        }
        // 修改 传阅文件
        updateById(ioaReceiveFile);
        // 批量插入传阅用户记录
        if (null != readUserList && !readUserList.isEmpty()) {
          if (null != userIdList && !userIdList.isEmpty()) {
            Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
            where.setSqlSelect("id,dept_id,user_id");
            where.in("user_id", userIdList);
            List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(where);
            List<Long> deptIdList = new ArrayList<Long>();
            if ((null != departmentUserList && !departmentUserList.isEmpty())) {
              for (AutDepartmentUser departmentUser : departmentUserList) {
                for (IoaReceiveReadUser user : readUserList) {
                  if (user.getReadUserId().equals(departmentUser.getUserId())
                      && user.getReadUserId().intValue() == departmentUser.getUserId().intValue()) {
                    user.setReadDeptId(
                        null != departmentUser.getDeptId() ? departmentUser.getDeptId() : 0L);
                    deptIdList.add(user.getReadDeptId());
                  }
                }
              }
            }
            if (null != deptIdList && !deptIdList.isEmpty()) {
              Where<AutDepartment> autDepartmentWhere = new Where<AutDepartment>();
              autDepartmentWhere.setSqlSelect("id,dept_name");
              autDepartmentWhere.in("id", deptIdList);
              List<AutDepartment> departmentList =
                  autDepartmentService.selectList(autDepartmentWhere);
              if ((null != departmentList && !departmentList.isEmpty())) {
                for (AutDepartment department : departmentList) {
                  for (IoaReceiveReadUser user : readUserList) {
                    if (user.getReadDeptId().equals(department.getId())
                        && user.getReadDeptId().intValue() == department.getId().intValue()) {
                      user.setReadDeptName(StringUtils.isNotEmpty(department.getDeptName())
                          ? department.getDeptName() : "");
                    }
                    if (null == user.getReadDeptName()) {
                      user.setReadDeptName("");
                    }
                  }
                }
              }
            }
          }
          ioaReceiveReadUserService.insertBatch(readUserList);
        }
        for (IoaReceiveReadObject object : objectList) {
          ioaReceiveReadObjectService.physicsDeleteById(object.getId());
        }
      }
    }

    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  /**
   * 
   * @描述: 分类显示传阅者
   * 
   *      @return： List<IoaReceiveReadObjectDO>
   * 
   *      @author： sunlinan
   *
   * @date 2017年12月28日
   */
  public List<IoaReceiveReadObjectDO> loadReadObj(IoaReceiveFile obj) throws Exception {
    // Map<String, Object> map = new HashMap<String, Object>();
    List<SysDataDict> readObjectLeaderList =
        sysDataDictService.getByCode(WebConstant.DICT_READ_OBJECT_LEADER);
    List<SysDataDict> readObjectOfficeList =
        sysDataDictService.getByCode(WebConstant.DICT_READ_OBJECT_OFFICE);
    List<SysDataDict> readObjectOtherList =
        sysDataDictService.getByCode(WebConstant.DICT_READ_OBJECT_OTHER);
    List<SysDataDict> readObjectOfficeLeaderList =
        sysDataDictService.getByCode(WebConstant.DICT_READ_OBJECT_OFFICELEADER);
    // List<SysDataDict> allList =
    // sysDataDictService.getByCode(WebConstant.DICT_READ_OBJECT_ALL);
    List<IoaReceiveReadObjectDO> objectDOList = new ArrayList<IoaReceiveReadObjectDO>();
    String readObjectLeaderId = "";
    String readObjectOfficeId = "";
    String readObjectOfficeLeaderId = "";
    // 局领导
    if (null != readObjectLeaderList && !readObjectLeaderList.isEmpty()) {
      for (SysDataDict dataDict : readObjectLeaderList) {
        readObjectLeaderId += dataDict.getDictKey() + ";";
      }
      SysDataDict dict = readObjectLeaderList.get(0);
      if (null != dict) {
        IoaReceiveReadObjectDO receiveReadObjectDO = new IoaReceiveReadObjectDO();
        receiveReadObjectDO.setDictCode(dict.getDictCode());
        receiveReadObjectDO.setDictName(dict.getDictName());
        receiveReadObjectDO.setDictKey(dict.getDictKey());
        receiveReadObjectDO.setDictValue(readObjectLeaderId);
        receiveReadObjectDO.setText(dict.getDictName());
        receiveReadObjectDO.setId(dict.getId());
        objectDOList.add(receiveReadObjectDO);
      }
    }

    // 处室领导
    if (null != readObjectOfficeList && !readObjectOfficeList.isEmpty()) {
      for (SysDataDict dataDict : readObjectOfficeList) {
        readObjectOfficeId += dataDict.getDictKey() + ";";
      }
      SysDataDict dict = readObjectOfficeList.get(0);
      if (null != dict) {
        IoaReceiveReadObjectDO receiveReadObjectDO = new IoaReceiveReadObjectDO();
        receiveReadObjectDO.setDictCode(dict.getDictCode());
        receiveReadObjectDO.setDictName(dict.getDictName());
        receiveReadObjectDO.setDictKey(dict.getDictKey());
        receiveReadObjectDO.setDictValue(readObjectOfficeId);
        receiveReadObjectDO.setText(dict.getDictName());
        objectDOList.add(receiveReadObjectDO);
      }
    }

    // 局属单位领导
    if (null != readObjectOfficeLeaderList && !readObjectOfficeLeaderList.isEmpty()) {
      for (SysDataDict dataDict : readObjectOfficeLeaderList) {
        readObjectOfficeLeaderId += dataDict.getDictKey() + ";";
      }
      SysDataDict dict = readObjectOfficeLeaderList.get(0);
      if (null != dict) {
        IoaReceiveReadObjectDO receiveReadObjectDO = new IoaReceiveReadObjectDO();
        receiveReadObjectDO.setDictCode(dict.getDictCode());
        receiveReadObjectDO.setDictName(dict.getDictName());
        receiveReadObjectDO.setDictKey(dict.getDictKey());
        receiveReadObjectDO.setDictValue(readObjectOfficeLeaderId);
        receiveReadObjectDO.setText(dict.getDictName());
        receiveReadObjectDO.setId(dict.getId());
        objectDOList.add(receiveReadObjectDO);
      }
    }

    Set<Long> dictKeySet = new HashSet<Long>();
    if (null != readObjectOtherList && !readObjectOtherList.isEmpty()) {
      for (SysDataDict dataDict : readObjectOtherList) {
        if (null != dataDict) {
          if (Long.valueOf(dataDict.getDictKey()) > 0) {
            dictKeySet.add(Long.valueOf(dataDict.getDictKey()));
          }
          IoaReceiveReadObjectDO receiveReadObjectDO = new IoaReceiveReadObjectDO();
          receiveReadObjectDO.setDictCode(dataDict.getDictKey());
          receiveReadObjectDO.setDictName(dataDict.getDictValue());
          receiveReadObjectDO.setDictValue(dataDict.getDictValue());
          receiveReadObjectDO.setDictKey(dataDict.getDictKey());
          receiveReadObjectDO.setText(dataDict.getDictValue());
          objectDOList.add(receiveReadObjectDO);
        }
      }
    }

    /*
     * Where<AutDepartment> deptWhere = new Where<AutDepartment>();
     * deptWhere.setSqlSelect("dept_code"); deptWhere.in("id", dictKeySet); List<AutDepartment>
     * deptList = autDepartmentService.selectList(deptWhere); List<String> deptCodeList = new
     * ArrayList<String>(); for (AutDepartment dept : deptList) {
     * deptCodeList.add(dept.getDeptCode()); }
     */
    // if(deptCodeList.size()>0){
    // }


    List<AutDepartment> deptList = new ArrayList<AutDepartment>();
    if (dictKeySet.size() > 0) {
      Where<AutDepartment> deptWhere1 = new Where<AutDepartment>();
      deptWhere1.setSqlSelect("id,parent_id");
      deptWhere1.in("parent_id", dictKeySet);
      deptList = autDepartmentService.selectList(deptWhere1);
    }
    for (IoaReceiveReadObjectDO objectDO : objectDOList) {
      for (AutDepartment autDepartment : deptList) {
        if (autDepartment.getParentId().toString().equals(objectDO.getDictKey())) {
          if (null != objectDO.getDictKeyDetail()
              && StringUtils.isNotEmpty(objectDO.getDictKeyDetail())) {
            String keyDetail = objectDO.getDictKeyDetail() + ";" + autDepartment.getId();
            objectDO.setDictKeyDetail(keyDetail);
          } else {
            objectDO.setDictKeyDetail(autDepartment.getId() + "");
          }
        }
      }
    }
    if (null != obj && null != obj.getId()) {
      IoaReceiveFile ioaReceiveFile = selectById(obj.getId());
      if (null != ioaReceiveFile) {
        Where<IoaReceiveReadObject> objectWhere = new Where<IoaReceiveReadObject>();
        objectWhere
            .setSqlSelect("id,object_id,object_name,receive_file_id,read_user_ids,read_user_names");
        objectWhere.eq("receive_file_id", ioaReceiveFile.getId());
        List<IoaReceiveReadObject> objectList = ioaReceiveReadObjectService.selectList(objectWhere);
        if (null != objectList && !objectList.isEmpty()) {
          Set<Long> userIds = new HashSet<Long>();
          List<IoaReceiveReadUser> allUsers = new ArrayList<IoaReceiveReadUser>();
          List<IoaReceiveReadUser> allRankUsers = new ArrayList<IoaReceiveReadUser>();
          List<Long> objectIdList = new ArrayList<Long>();
          for (IoaReceiveReadObject object : objectList) {
            if (null != object.getReadUserIds()
                && !object.getReadUserIds().equals(WebConstant.DICT_READ_OBJECT_ALL)) {
              objectIdList.add(object.getId());
              object.setReadUserIds("");
              object.setReadUserNames("");
            }
          }
          if (objectIdList.size() > 0) {
            Where<IoaReceiveReadUser> userWhere = new Where<IoaReceiveReadUser>();
            userWhere.setSqlSelect(
                "id,receive_read_object_id,receive_file_id,read_user_id,read_user_full_name,read_dept_id,read_dept_name,is_read,read_time,read_opinion");
            if (objectIdList.size() > 1) {
              userWhere.in("receive_read_object_id", objectIdList);
            } else {
              userWhere.eq("receive_read_object_id", objectIdList.get(0));
            }
            userWhere.eq("receive_file_id", ioaReceiveFile.getId());
            allUsers = ioaReceiveReadUserService.selectList(userWhere);
            for (IoaReceiveReadUser user : allUsers) {
              userIds.add(user.getReadUserId());
            }
          }

          // 传阅者排序
          Where<AutUserRank> autUserRankWhere = new Where<AutUserRank>();
          autUserRankWhere.setSqlSelect("id,user_rank");
          autUserRankWhere.in("id", userIds);
          autUserRankWhere.orderBy("user_rank", true);
          List<AutUserRank> userRankList = autUserRankService.selectList(autUserRankWhere);

          for (AutUserRank autUserRank : userRankList) {
            for (IoaReceiveReadUser ioaReceiveReadUser : allUsers) {
              if (autUserRank.getId().equals(ioaReceiveReadUser.getReadUserId())) {
                allRankUsers.add(ioaReceiveReadUser);
              }
            }
          }

          for (IoaReceiveReadUser ioaReceiveReadUser : allRankUsers) {
            for (IoaReceiveReadObject object : objectList) {
              if (object.getId().equals(ioaReceiveReadUser.getReceiveReadObjectId())) {
                String readUserIds = object.getReadUserIds();
                String readUserNames = object.getReadUserNames();
                readUserNames += ioaReceiveReadUser.getReadUserFullName() + ";";
                readUserIds += ioaReceiveReadUser.getReadUserId() + ";";
                if (!object.getReadUserIds().equals(WebConstant.DICT_READ_OBJECT_ALL)) {
                  object.setReadUserIds(readUserIds);
                  object.setReadUserNames(readUserNames);
                }
              }
            }
          }

          if (null != objectDOList && !objectDOList.isEmpty()) {
            for (IoaReceiveReadObject object : objectList) {
              for (IoaReceiveReadObjectDO objectDO : objectDOList) {
                if (object.getObjectName().equals(objectDO.getDictName())) {
                  if (object.getObjectId() == 1) {
                    objectDO.setDictCode(WebConstant.DICT_READ_OBJECT_LEADER);
                  } else if (object.getObjectId() == 2) {
                    objectDO.setDictCode(WebConstant.DICT_READ_OBJECT_OFFICE);
                  } else if (object.getObjectId() == 3) {
                    objectDO.setDictCode(WebConstant.DICT_READ_OBJECT_OFFICELEADER);
                  } else {
                    objectDO.setDictCode(object.getObjectId() + "");
                  }
                  objectDO.setDictName(object.getObjectName());
                  objectDO.setText(object.getObjectName());
                  objectDO.setReadUserIds(object.getReadUserIds());
                  objectDO.setReadUserNames(object.getReadUserNames());
                  objectDO.setId(object.getId());
                }
              }

              // if (StringUtils.isNotEmpty(object.getReadUserIds())) {
              // IoaReceiveReadObjectDO receiveReadObjectDO = new IoaReceiveReadObjectDO();
              // receiveReadObjectDO.setDictCode(object.getObjectId() + "");
              // if(object.getObjectId() == 1){
              // receiveReadObjectDO.setDictCode(WebConstant.DICT_READ_OBJECT_LEADER);
              // }else if(object.getObjectId() == 2){
              // receiveReadObjectDO.setDictCode(WebConstant.DICT_READ_OBJECT_OFFICE);
              // }else if(object.getObjectId() == 3){
              // receiveReadObjectDO.setDictCode(WebConstant.DICT_READ_OBJECT_OFFICELEADER);
              // }else{
              // receiveReadObjectDO.setDictCode(object.getObjectId() + "");
              // }
              // receiveReadObjectDO.setDictName(object.getObjectName());
              // receiveReadObjectDO.setText(object.getObjectName());
              // receiveReadObjectDO.setReadUserIds(object.getReadUserIds());
              // receiveReadObjectDO.setReadUserNames(object.getReadUserNames());
              // receiveReadObjectDO.setId(object.getId());
              // receiveReadObjectDO.setIsCheck("1");
              // objectDOList.add(receiveReadObjectDO);
              // }

            }
          }
        }
      }
    }
    return objectDOList;
  }

  @Override
  public Map<String, Object> loadDictByCode() throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    List<String> dictTypeList = new ArrayList<String>();
    dictTypeList.add(WebConstant.DICT_RECEIVE_FILE_TYPE);
    dictTypeList.add(WebConstant.DICT_FROM_UNIT);
    dictTypeList.add(WebConstant.DICT_URGENT_LEVEL);
    dictTypeList.add(WebConstant.DICT_READ_OBJECT_ALL);
    Where<SysDataDict> where = new Where<SysDataDict>();
    where.in("dict_code", dictTypeList);
    where.setSqlSelect("id,dict_code,dict_name,dict_key,dict_value,dict_rank");
    where.orderBy("dict_rank");
    List<SysDataDict> allDict = sysDataDictService.selectList(where);
    // 文件类型
    List<SysDataDict> fileTypeList = new ArrayList<SysDataDict>();
    // 来文单位
    List<SysDataDict> fromUnitList = new ArrayList<SysDataDict>();
    // 紧急程度
    List<SysDataDict> urgentLevelList = new ArrayList<SysDataDict>();
    // 全局人员
    List<SysDataDict> allList = new ArrayList<SysDataDict>();
    for (SysDataDict sysDataDict : allDict) {
      if (sysDataDict.getDictCode().equals(WebConstant.DICT_RECEIVE_FILE_TYPE)) {
        fileTypeList.add(sysDataDict);
      } else if (sysDataDict.getDictCode().equals(WebConstant.DICT_FROM_UNIT)) {
        fromUnitList.add(sysDataDict);
      } else if (sysDataDict.getDictCode().equals(WebConstant.DICT_URGENT_LEVEL)) {
        urgentLevelList.add(sysDataDict);
      } else if (sysDataDict.getDictCode().equals(WebConstant.DICT_READ_OBJECT_ALL)) {
        allList.add(sysDataDict);
      }
    }
    if (!fileTypeList.isEmpty()) {
      map.put("fileType", fileTypeList);
    }
    if (!fromUnitList.isEmpty()) {
      map.put("fromUnit", fromUnitList);
    }
    if (!urgentLevelList.isEmpty()) {
      map.put("urgentLevel", urgentLevelList);
    }
    if (!allList.isEmpty()) {
      map.put("all", allList);
    }
    return map;
  }

  @Override
  public RetMsg checkNextTaskInfo(String processInstanceId, String receiveUserIds,
      String isCheckAllUser) throws Exception {
    RetMsg retMsg = new RetMsg();
    List<TaskDefinition> list = activitiService.getNextTaskInfo(processInstanceId, false);
    boolean isOrgn = false;
    Map<String, Object> map = new HashMap<String, Object>();
    if (null != list && !list.isEmpty()) {
      String assigneeId = "";
      String assigneeUserId = "";
      String assigneeGroupId = "";
      TaskDefinition definition = list.get(0);
      if (StringUtils.isNotEmpty(processInstanceId) && StringUtils.isNotEmpty(receiveUserIds)) {
        List<String> uIdList = Arrays.asList(receiveUserIds.split(";"));
        ExpressionManager expressionManager = processEngineConfigurationImpl.getExpressionManager();
        Set<Expression> set = new HashSet<Expression>();
        for (final String userId : uIdList) {
          Expression expression = expressionManager.createExpression(userId);
          set.add(expression);
        }
        // definition.setCandidateGroupIdExpressions(set);
        definition.setCandidateUserIdExpressions(set);
      } else if (StringUtils.isNotEmpty(processInstanceId) && StringUtils.isEmpty(receiveUserIds)
          && StringUtils.isNotEmpty(isCheckAllUser) && isCheckAllUser.equals("1")) {
        Where<IoaReceiveFile> filewhere = new Where<IoaReceiveFile>();
        filewhere.setSqlSelect("id");
        filewhere.eq("process_instance_id", processInstanceId);
        IoaReceiveFile file = selectOne(filewhere);
        Where<IoaReceiveReadUser> readUserWhere = new Where<IoaReceiveReadUser>();
        readUserWhere.eq("receive_file_id", file.getId());
        readUserWhere.setSqlSelect("read_user_id");
        List<IoaReceiveReadUser> readUserList = ioaReceiveReadUserService.selectList(readUserWhere);
        Set<Long> readUserIdSet = new HashSet<Long>();
        ExpressionManager expressionManager = processEngineConfigurationImpl.getExpressionManager();
        Set<Expression> set = new HashSet<Expression>();
        for (IoaReceiveReadUser ioaReceiveReadUser : readUserList) {
          readUserIdSet.add(ioaReceiveReadUser.getReadUserId());
        }
        for (Long readUserId : readUserIdSet) {
          Expression expression = expressionManager.createExpression(String.valueOf(readUserId));
          set.add(expression);
        }
        definition.setCandidateUserIdExpressions(set);
      }
      if (null != definition) {
        List<String> uIds = new ArrayList<String>(); // 所有候选用户id
        List<AutUser> uList = new ArrayList<AutUser>(); // 所有候选用户
        String assignee = String.valueOf(definition.getAssigneeExpression());
        if (null != definition.getAssigneeExpression() && assignee.indexOf("{") < 0) {
          if (StringUtils.isNotEmpty(assignee)) {
            assigneeId = assignee;
            // 受理人
            List<String> assineedIdList = new ArrayList<String>();
            if (assigneeId.endsWith(";")) {
              assineedIdList = Arrays.asList(assigneeId.split(";"));
            } else {
              assineedIdList.add(assigneeId);
            }
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.in("id", assineedIdList);
            userWhere.setSqlSelect("id,user_name,full_name");
            List<AutUser> assigneedList = autUserService.selectList(userWhere);
            if (null != assigneedList && !assigneedList.isEmpty()) {
              uIds.addAll(assineedIdList);
              uList.addAll(assigneedList);
            }
          }
        }
        // 候选用户列表assineedUserIdList
        if (null != definition.getCandidateUserIdExpressions()) {
          Iterator<Expression> it = definition.getCandidateUserIdExpressions().iterator();
          while (it.hasNext()) {
            assigneeUserId += String.valueOf(it.next()) + ";";
          }
        }
        List<String> assineedUserIdList = new ArrayList<String>();
        if (assigneeUserId.endsWith(";")) {
          assineedUserIdList = Arrays.asList(assigneeUserId.split(";"));
        } else {
          assineedUserIdList.add(assigneeUserId);
        }
        Where<AutUser> assigneedUserWhere = new Where<AutUser>();
        assigneedUserWhere.in("id", assineedUserIdList);
        assigneedUserWhere.setSqlSelect("id,user_name,full_name");
        uIds.addAll(assineedUserIdList);

        // 候选组
        if (null != definition.getCandidateGroupIdExpressions()) {
          Iterator<Expression> it = definition.getCandidateGroupIdExpressions().iterator();
          while (it.hasNext()) {
            assigneeGroupId += String.valueOf(it.next()) + ";";
          }
        }
        List<String> assineedGroupIdList = new ArrayList<String>();
        if (assigneeGroupId.endsWith(";")) {
          assineedGroupIdList = Arrays.asList(assigneeGroupId.split(";"));
        } else {
          assineedGroupIdList.add(assigneeGroupId);
        }

        // 候选岗位
        // List<Long> uIdList = new ArrayList<Long>();
        Where<AutUserPosition> positionWhere = new Where<AutUserPosition>();
        positionWhere.in("position_id", assineedGroupIdList);
        positionWhere.setSqlSelect("id,position_id,user_id");
        List<AutUserPosition> positionList = autUserPositionService.selectList(positionWhere);
        if (null != positionList && !positionList.isEmpty()) {
          for (AutUserPosition userPosition : positionList) {
            if (null != userPosition && null != userPosition.getUserId()) {
              uIds.add(String.valueOf(userPosition.getUserId()));
            }
          }
        }

        // 候选岗位
        Where<AutUserRole> roleWhere = new Where<AutUserRole>();
        roleWhere.in("role_id", assineedGroupIdList);
        roleWhere.setSqlSelect("id,role_id,user_id");
        List<AutUserRole> roleList = autUserRoleService.selectList(roleWhere);

        if (null != roleList && !roleList.isEmpty()) {
          for (AutUserRole userRole : roleList) {
            if (null != userRole && null != userRole.getUserId()) {
              uIds.add(String.valueOf(userRole.getUserId()));
            }
          }
        }
        if (null != uIds && !uIds.isEmpty()) {
          List<Long> idsL = new ArrayList<Long>();
          Where<AutUser> where = new Where<AutUser>();
          for (String string : uIds) {
            if (string != null && !"".equals(string)) {
              idsL.add(Long.valueOf(string));
            }
          }
          where.in("id", idsL);
          where.setSqlSelect("id,user_name,full_name");
          uList = autUserService.selectList(where);
        }
        if (uList.size() > 0) {
          Set<Long> userIdSet = new HashSet<Long>();
          for (AutUser user : uList) {
            userIdSet.add(user.getId());
          }
          Where<AutUserRank> autUserRankWhere = new Where<AutUserRank>();
          autUserRankWhere.setSqlSelect("id,user_rank");
          autUserRankWhere.in("id", userIdSet);
          autUserRankWhere.orderBy("user_rank", true);
          List<AutUserRank> autUserRanks = autUserRankService.selectList(autUserRankWhere);
          List<AutUser> userList = new ArrayList<AutUser>();
          for (AutUserRank autUserRank : autUserRanks) {
            for (AutUser autUser : uList) {
              if (autUser.getId().equals(autUserRank.getId())) {
                userList.add(autUser);
              }
            }
          }
          map.put("user", userList);
        }

        // 选择部门
        Where<AutDepartmentUser> departmentWhere = new Where<AutDepartmentUser>();
        departmentWhere.in("dept_id", assineedGroupIdList);
        departmentWhere.setSqlSelect("dept_id,id,dept_code,user_id");
        List<AutDepartmentUser> departmentList =
            autDepartmentUserService.selectList(departmentWhere);
        List<AutDepartmentUserVO> departmentUserList = new ArrayList<AutDepartmentUserVO>();
        if (null != departmentList && !departmentList.isEmpty()) {
          isOrgn = true;
          List<String> deptIdlist = new ArrayList<String>();
          List<Long> deptUIdList = new ArrayList<Long>();
          List<AutUser> deptUserList = new ArrayList<AutUser>();
          for (AutDepartmentUser department : departmentList) {
            if (null != department && null != department.getUserId()) {
              deptIdlist.add(String.valueOf(department.getId()));
              deptUIdList.add(department.getUserId());
              uIds.add(String.valueOf(department.getUserId()));
            }
          }
          if (null != deptUIdList && !deptUIdList.isEmpty()) {
            Where<AutUser> where = new Where<AutUser>();
            where.in("id", deptUIdList);
            where.setSqlSelect("id,user_name,full_name");
            deptUserList = autUserService.selectList(where);
            uList.addAll(deptUserList);
          }
          List<AutDepartmentUser> deptUser = new ArrayList<AutDepartmentUser>();
          if (null != uIds && !uIds.isEmpty()) {
            Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
            where.setSqlSelect("dept_id,id,dept_code,user_id");
            where.in("user_id", uIds);
            deptUser = autDepartmentUserService.selectList(where);
          }
          if (null != deptUser && !deptUser.isEmpty()) {
            for (AutDepartmentUser departmentUser : deptUser) {
              AutDepartmentUserVO departmentUserVO = new AutDepartmentUserVO();
              BeanUtils.copyProperties(departmentUser, departmentUserVO);
              departmentUserList.add(departmentUserVO);
            }
          }
          if (null != deptUserList && !deptUserList.isEmpty()) {
            for (AutUser user : uList) {
              for (AutDepartmentUser departmentUser : departmentList) {
                if (null != user && null != departmentUser && null != user.getId()
                    && null != departmentUser.getUserId()) {
                  if (user.getId().equals(departmentUser.getUserId())
                      && user.getId().intValue() == departmentUser.getUserId().intValue()) {
                    AutDepartmentUserVO departmentUserVO = new AutDepartmentUserVO();
                    BeanUtils.copyProperties(departmentUser, departmentUserVO);
                    departmentUserVO.setFullName(user.getFullName());
                    departmentUserList.add(departmentUserVO);
                  }
                }
              }
            }
          }
          if (null != departmentUserList && !departmentUserList.isEmpty()) {
            for (int i = 0; i <= departmentUserList.size() - 1; i++) {
              for (int j = departmentUserList.size() - 1; j > i; j--) {
                AutDepartmentUserVO autDepartmentUserVO = departmentUserList.get(i);
                AutDepartmentUserVO autDepartmentUserVO2 = departmentUserList.get(j);
                if (null != autDepartmentUserVO && null != autDepartmentUserVO2
                    && null != autDepartmentUserVO.getUserId()
                    && null != autDepartmentUserVO2.getUserId()) {
                  if (autDepartmentUserVO.getUserId().equals(autDepartmentUserVO2.getUserId())
                      && autDepartmentUserVO.getUserId().intValue() == autDepartmentUserVO2
                          .getUserId().intValue()) {
                    departmentUserList.remove(i);
                  }
                }
              }
            }
          }
          map.put("dept", departmentUserList);
        }
      }
    }
    List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    if (null != taskList && !taskList.isEmpty()) {
      Task task = taskList.get(0);
      // 判断当前节点是否可 回退
      List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(task.getId());
      String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
      List<TaskDefinition> preList =
          activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
      if (preList == null || preList.isEmpty()) {
        map.put("isNotBack", true);
      } else {
        map.put("isNotBack", false);
      }
    }
    map.put("isOrgn", isOrgn);
    retMsg.setCode(0);
    retMsg.setObject(map);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public RetMsg getNextTaskInfo(String processInstanceId, boolean withCondition) throws Exception {
    RetMsg retMsg = new RetMsg();
    List<TaskDefinition> list = activitiService.getNextTaskInfo(processInstanceId, withCondition);
    List<CustomerTaskDefinition> taskDefinitionList = new ArrayList<CustomerTaskDefinition>();
    if (null != list && !list.isEmpty()) {
      for (TaskDefinition definition : list) {
        if (null != definition) {
          CustomerTaskDefinition taskDefinition = new CustomerTaskDefinition();
          taskDefinition.setKey(definition.getKey());
          taskDefinition.setAssignee(String.valueOf(definition.getAssigneeExpression()));
          taskDefinition
              .setNextNodeName(String.valueOf(definition.getNameExpression()).equals("null") ? ""
                  : String.valueOf(definition.getNameExpression()));
          taskDefinitionList.add(taskDefinition);
        }
      }
    }
    retMsg.setCode(0);
    retMsg.setObject(taskDefinitionList);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  

  @Override
  public RetMsg checkIsClaim(String processInstanceId) throws Exception {
    RetMsg retMsg = new RetMsg();
    List<Task> tskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    if (null != tskList && !tskList.isEmpty()) {
      Task task = tskList.get(0);
      if (null != task) {
        Task tsk = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        if (null != tsk) {
          HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
          if (null != hisTask.getClaimTime() && StringUtils.isNotEmpty(hisTask.getAssignee())) {
            retMsg.setCode(0);// 未签收
          } else {
            retMsg.setCode(1);// 已签收
          }
        } else {
          retMsg.setCode(1);
        }
      }
    }
    return retMsg;
  }

  @SuppressWarnings("unchecked")
  @Override
  public RetMsg getPreTaskInfo(String processInstanceId) throws Exception {
    RetMsg retMsg = new RetMsg();
    List<Task> tskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    // List<TaskDefinition> preList = null;
    // List<CustomerTaskDefinition> list = new ArrayList<CustomerTaskDefinition>();
    if (null != tskList && !tskList.isEmpty()) {
      Task task = tskList.get(0);
      if (null != task) {
        List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(task.getId());
        String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
        if (StringUtils.isNotEmpty(currentTaskKey)) {
          retMsg = actFixedFormService.getPreTaskInfo2(task.getId());
          if (retMsg.getCode() != 0) {
        	  return retMsg;
          }
          List<CustomerTaskDefinition> defitionList =
              (List<CustomerTaskDefinition>) retMsg.getObject();
          List<String> asignee = new ArrayList<String>();
          if (!defitionList.isEmpty()) {
            for (CustomerTaskDefinition customerTaskDefinition : defitionList) {
              List<String> taskAssignee =
                  Arrays.asList(customerTaskDefinition.getAssignee().split(";"));
              asignee.addAll(taskAssignee);
            }
          }
          if (!asignee.isEmpty()) {
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.setSqlSelect("id,user_name,full_name");
            if (asignee.size() > 1) {
              userWhere.in("id", asignee);
            } else {
              userWhere.eq("id", asignee.get(0));
            }
            List<AutUser> userList = autUserService.selectList(userWhere);
            for (CustomerTaskDefinition customerTaskDefinition : defitionList) {
              for (AutUser autUser : userList) {
                if (customerTaskDefinition.getAssignee().equals(String.valueOf(autUser.getId()))) {
                  customerTaskDefinition.setAssigneeName(autUser.getFullName());
                }
              }
            }
          }
          retMsg.setObject(defitionList);
        }
      }
    }
    retMsg.setCode(0);
    // retMsg.setObject(list);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public IoaReceiveFileDO directorDetail(IoaReceiveFile obj, AutUser user) throws Exception {// 池主任审批详情
    IoaReceiveFileDO ioaReceiveFileVO = null;
    if (null != obj && null != obj.getId()) {
      IoaReceiveFile ioaReceiveFile = selectById(obj.getId());
      if (null != ioaReceiveFile) {
        List<Task> currentTaskList = taskService.createTaskQuery()
            .processInstanceId(ioaReceiveFile.getProcessInstanceId()).list();
        if (currentTaskList.size() == 1) {
          Task currentTask = currentTaskList.get(0);
          if (currentTask.getAssignee() == null || currentTask.getAssignee().isEmpty()) {
            // 首先判断当前用户是否是当前任务候选人
            Task task = taskService.createTaskQuery().taskCandidateUser(user.getId().toString())
                .taskId(currentTask.getId()).singleResult();
            if (task == null) {
              throw new Exception("您不是此任务的候选办理人，请刷新页面重新获取任务信息！");
            }
            // 如果当前用户是当前任务办理人-签收
            taskService.claim(task.getId(), String.valueOf(user.getId()));
            String userFullName = "";
            if (null != task.getAssignee()) {
              userFullName = autUserService.selectById(task.getAssignee()).getFullName();
              actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);
            }
          }
        } else {
          List<Task> taskList =
              taskService.createTaskQuery().taskCandidateOrAssigned(user.getId().toString())
                  .processInstanceId(ioaReceiveFile.getProcessInstanceId()).list();
          if (taskList.size() == 0 || taskList.size() > 1) {
            throw new Exception("流程信息受损，请通知创建人重新发起流程！");
          }
          Task task = taskList.get(0);
          HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
          if (null == hisTask.getClaimTime() || StringUtils.isEmpty(hisTask.getAssignee())) {
            taskService.claim(task.getId(), user.getId().toString());
            actAljoinQueryService.updateAssigneeName(task.getId(), user.getFullName());
          }
        }
        ioaReceiveFileVO = new IoaReceiveFileDO();
        BeanUtils.copyProperties(ioaReceiveFile, ioaReceiveFileVO);
        Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id", obj.getId());
        List<ResResource> resourceList = resResourceService.selectList(where);
        if (null != resourceList && !resourceList.isEmpty()) {
            ioaReceiveFileVO.setResResourceList(resourceList);
         }    
        
      }
    }
    List<IoaReceiveReadObjectDO> objectDOList = loadReadObj(obj);
    ioaReceiveFileVO.setObjectDOList(objectDOList);
    return ioaReceiveFileVO;
  }

  @Override
  public IoaReceiveFileDO directorDetails(IoaReceiveFile obj, AutUser user) throws Exception {// 池主任审批详情
    IoaReceiveFileDO ioaReceiveFileVO = null;
    if (null != obj && null != obj.getId()) {
      IoaReceiveFile ioaReceiveFile = selectById(obj.getId());
      if (null != ioaReceiveFile) {
        List<Task> currentTaskList = taskService.createTaskQuery()
            .processInstanceId(ioaReceiveFile.getProcessInstanceId()).list();
        if (currentTaskList.size() == 1) {
          Task currentTask = currentTaskList.get(0);
          if (currentTask.getAssignee() == null || currentTask.getAssignee().isEmpty()) {
            // 首先判断当前用户是否是当前任务候选人
            Task task = taskService.createTaskQuery().taskCandidateUser(user.getId().toString())
                .taskId(currentTask.getId()).singleResult();
            if (task == null) {
              throw new Exception("您不是此任务的候选办理人，请刷新页面重新获取任务信息！");
            }
            // 如果当前用户是当前任务办理人-签收
            taskService.claim(task.getId(), String.valueOf(user.getId()));
            String userFullName = "";
            if (null != task.getAssignee()) {
              userFullName = autUserService.selectById(task.getAssignee()).getFullName();
              actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);
            }
          }
        } else {
          List<Task> taskList =
              taskService.createTaskQuery().taskCandidateOrAssigned(user.getId().toString())
                  .processInstanceId(ioaReceiveFile.getProcessInstanceId()).list();
          if (taskList.size() == 0 || taskList.size() > 1) {
            throw new Exception("流程信息受损，请通知创建人重新发起流程！");
          }
          Task task = taskList.get(0);
          HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
          if (null == hisTask.getClaimTime() || StringUtils.isEmpty(hisTask.getAssignee())) {
            taskService.claim(task.getId(), user.getId().toString());
            actAljoinQueryService.updateAssigneeName(task.getId(), user.getFullName());
          }
        }

        ioaReceiveFileVO = new IoaReceiveFileDO();
        BeanUtils.copyProperties(ioaReceiveFile, ioaReceiveFileVO);
        Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id", obj.getId());
        List<ResResource> resourceList = resResourceService.selectList(where);
        if (null != resourceList && !resourceList.isEmpty()) {
            ioaReceiveFileVO.setResResourceList(resourceList);
        } 
      }
    }
    List<IoaReceiveReadObjectDO> objectDOList = loadReadObj(obj);
    ioaReceiveFileVO.setObjectDOList(objectDOList);
    return ioaReceiveFileVO;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<FixedFormProcessLog> getTaskLogInfo(String processInstanceId) throws Exception {
    // 日志不显示流程名称了（本来格子就小。。。还要显示那么多东西）
    List<FixedFormProcessLog> logList = null;
    Map<String, Object> map = actFixedFormService.getLog(null, processInstanceId);
    if (null != map && !map.isEmpty()) {
      List<AutUser> assigneeList = new ArrayList<AutUser>();
      List<AutUser> recevieList = new ArrayList<AutUser>();
      if (null != map.get("assigneeIdList")) {
        List<Long> assigneeIdList = (List<Long>) map.get("assigneeIdList");
        if (null != assigneeIdList && !assigneeIdList.isEmpty()) {
          Where<AutUser> assigneeWhere = new Where<AutUser>();
          assigneeWhere.in("id", new HashSet<Long>(assigneeIdList));
          assigneeWhere.setSqlSelect("id,user_name,full_name");
          assigneeList = autUserService.selectList(assigneeWhere);
        }
      }
      if (null != map.get("recevieUserIdList")) {
        List<Long> recevieUserIdList = (List<Long>) map.get("recevieUserIdList");
        if (null != recevieUserIdList && !recevieUserIdList.isEmpty()) {
          Where<AutUser> recevieWhere = new Where<AutUser>();
          recevieWhere.in("id", recevieUserIdList);
          recevieWhere.setSqlSelect("id,user_name,full_name");
          recevieList = autUserService.selectList(recevieWhere);
        }
      }

      if (null != map.get("logList")) {
        logList = (List<FixedFormProcessLog>) map.get("logList");
        if (null != logList && !logList.isEmpty() && null != assigneeList
            && !assigneeList.isEmpty()) {
          for (FixedFormProcessLog log : logList) {
            String receivedUserName = "";
            for (AutUser user : recevieList) {
              if (null != user && null != user.getId() && null != log
                  && null != log.getRecevieUserId()) {
                if (log.getRecevieUserId().contains(String.valueOf(user.getId()))) {
                  receivedUserName += user.getFullName() + "; ";
                }
              }
            }
            log.setRecevieUserName(receivedUserName);
          }
        }
        if (null != logList && !logList.isEmpty() && null != recevieList
            && !recevieList.isEmpty()) {
          for (FixedFormProcessLog log : logList) {
            for (AutUser user : assigneeList) {
              if (null != user && null != user.getId() && null != log
                  && null != log.getOperationId()) {
                if (String.valueOf(user.getId()).equals(log.getOperationId())) {
                  log.setOperationName(user.getFullName());
                }
              }
            }
          }
        }
      }
    }
    return logList;
  }

  @Override
  public IoaReceiveFileDO readDetail(IoaReceiveFile obj) throws Exception {
    // 1.详情
    IoaReceiveFileDO ioaReceiveFileVO = null;
    if (null != obj && null != obj.getId()) {
      IoaReceiveFile ioaReceiveFile = selectById(obj.getId());
      if (null != ioaReceiveFile) {
        ioaReceiveFileVO = new IoaReceiveFileDO();
        BeanUtils.copyProperties(ioaReceiveFile, ioaReceiveFileVO);
        Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id", obj.getId());
        List<ResResource> resourceList = resResourceService.selectList(where);
        if (null != resourceList && !resourceList.isEmpty()) {
            ioaReceiveFileVO.setResResourceList(resourceList);
        } 
        if (StringUtils.isNotEmpty(ioaReceiveFileVO.getReadUserIds())) {
          List<IoaReceiveReadObjectDO> objectDOList = loadReadObj(obj);
          ioaReceiveFileVO.setObjectDOList(objectDOList);
        }
        // 2.判断文件是否可撤回
        List<HistoricActivityInstance> activities =
            historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(ioaReceiveFile.getProcessInstanceId())
                .orderByHistoricActivityInstanceStartTime().desc().list();
        String thistaskId = activities.get(0).getTaskId(); // 当前环节id
        String pretaskId = activities.get(1).getTaskId(); // 上一环节id
        Task curtask = taskService.createTaskQuery().taskId(thistaskId).singleResult();// 当前环节是否被签收
        if (curtask != null) {
          if (StringUtils.isNotEmpty(curtask.getAssignee())) {
            ioaReceiveFileVO.setClaim(true);// 任务已被办理人签收不可撤回
          } else {
            List<TaskDefinition> preList = activitiService
                .getPreTaskInfo(curtask.getTaskDefinitionKey(), curtask.getProcessInstanceId());
            if (preList.size() <= 0) {
              ioaReceiveFileVO.setClaim(true);// 上一环节为空不能撤回
            } else {
              HistoricTaskInstance histask = historyService.createHistoricTaskInstanceQuery()
                  .processInstanceId(ioaReceiveFile.getProcessInstanceId()).taskId(pretaskId)
                  .taskAssignee(String.valueOf(obj.getCreateUserId())).singleResult();

              if (null == histask) {
                ioaReceiveFileVO.setClaim(true);// 当前用户不是上一环节办理人
              } else if (histask.getTaskDefinitionKey().equals(preList.get(0).getKey())) {
                if (ioaReceiveFileVO.getReadUserIds() != null
                    && StringUtils.isNotEmpty(ioaReceiveFileVO.getReadUserIds())) {// 传阅对象不为空-要么池主任签收了没批，要么到传阅对象那边了
                  Where<IoaReceiveReadUser> readUserWhere = new Where<IoaReceiveReadUser>();
                  readUserWhere.eq("receive_file_id", ioaReceiveFileVO.getId());
                  readUserWhere.eq("is_read", 1);
                  Integer count = ioaReceiveReadUserService.selectCount(readUserWhere);
                  if (count > 0) {
                    ioaReceiveFileVO.setClaim(true);// 传阅单位开始阅读-不能撤回
                  } else {
                    ioaReceiveFileVO.setClaim(false);// 可以撤回
                  }
                } else {
                  ioaReceiveFileVO.setClaim(false);// 可以撤回
                }
              } else {
                ioaReceiveFileVO.setClaim(true);
              }
            }
          }
        } else {
          ioaReceiveFileVO.setClaim(true);// 不可撤回
        }
      }
    }
    return ioaReceiveFileVO;
  }

  @Override
  public IoaReceiveFileDO readDetails(IoaReceiveFile obj) throws Exception {
    // 1.详情
    IoaReceiveFileDO ioaReceiveFileVO = null;
    if (null != obj && null != obj.getId()) {
      IoaReceiveFile ioaReceiveFile = selectById(obj.getId());
      if (null != ioaReceiveFile) {
        ioaReceiveFileVO = new IoaReceiveFileDO();
        BeanUtils.copyProperties(ioaReceiveFile, ioaReceiveFileVO);
        Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id", obj.getId());
        List<ResResource> resourceList = resResourceService.selectList(where);
        if (null != resourceList && !resourceList.isEmpty()) {
            ioaReceiveFileVO.setResResourceList(resourceList);
         } 
        if (StringUtils.isNotEmpty(ioaReceiveFileVO.getReadUserIds())) {
          List<IoaReceiveReadObjectDO> objectDOList = loadReadObj(obj);
          ioaReceiveFileVO.setObjectDOList(objectDOList);
        }
        // 2.判断文件是否可撤回
        List<HistoricActivityInstance> activities =
            historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(ioaReceiveFile.getProcessInstanceId())
                .orderByHistoricActivityInstanceStartTime().desc().list();
        String thistaskId = activities.get(0).getTaskId(); // 当前环节id
        String pretaskId = activities.get(1).getTaskId(); // 上一环节id
        Task curtask = taskService.createTaskQuery().taskId(thistaskId).singleResult();// 当前环节是否被签收
        if (curtask != null) {
          if (StringUtils.isNotEmpty(curtask.getAssignee())) {
            ioaReceiveFileVO.setClaim(true);// 任务已被办理人签收不可撤回
          } else {
            List<TaskDefinition> preList = activitiService
                .getPreTaskInfo(curtask.getTaskDefinitionKey(), curtask.getProcessInstanceId());
            if (preList.size() <= 0) {
              ioaReceiveFileVO.setClaim(true);// 上一环节为空不能撤回
            } else {
              HistoricTaskInstance histask = historyService.createHistoricTaskInstanceQuery()
                  .processInstanceId(ioaReceiveFile.getProcessInstanceId()).taskId(pretaskId)
                  .taskAssignee(String.valueOf(obj.getCreateUserId())).singleResult();

              if (null == histask) {
                ioaReceiveFileVO.setClaim(true);// 当前用户不是上一环节办理人
              } else if (histask.getTaskDefinitionKey().equals(preList.get(0).getKey())) {
                if (ioaReceiveFileVO.getReadUserIds() != null
                    && StringUtils.isNotEmpty(ioaReceiveFileVO.getReadUserIds())) {// 传阅对象不为空-要么池主任签收了没批，要么到传阅对象那边了
                  Where<IoaReceiveReadUser> readUserWhere = new Where<IoaReceiveReadUser>();
                  readUserWhere.eq("receive_file_id", ioaReceiveFileVO.getId());
                  readUserWhere.eq("is_read", 1);
                  Integer count = ioaReceiveReadUserService.selectCount(readUserWhere);
                  if (count > 0) {
                    ioaReceiveFileVO.setClaim(true);// 传阅单位开始阅读-不能撤回
                  } else {
                    ioaReceiveFileVO.setClaim(false);// 可以撤回
                  }
                } else {
                  ioaReceiveFileVO.setClaim(false);// 可以撤回
                }
              } else {
                ioaReceiveFileVO.setClaim(true);
              }
            }
          }
        } else {
          ioaReceiveFileVO.setClaim(true);// 不可撤回
        }
      }
    }
    return ioaReceiveFileVO;
  }

  @Override
  public IoaReceiveFileDO readHisDetail(IoaReceiveFile obj) throws Exception {
    // 1.详情
    IoaReceiveFileDO ioaReceiveFileVO = null;
    if (null != obj && null != obj.getId()) {
      IoaReceiveFile ioaReceiveFile = selectById(obj.getId());
      if (null != ioaReceiveFile) {
        ioaReceiveFileVO = new IoaReceiveFileDO();
        BeanUtils.copyProperties(ioaReceiveFile, ioaReceiveFileVO);
        Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id", obj.getId());
        List<ResResource> resourceList = resResourceService.selectList(where);
        if (null != resourceList && !resourceList.isEmpty()) {
            ioaReceiveFileVO.setResResourceList(resourceList);
        } 
        if (StringUtils.isNotEmpty(ioaReceiveFileVO.getReadUserIds())) {
          List<IoaReceiveReadObjectDO> objectDOList = loadReadObj(obj);
          ioaReceiveFileVO.setObjectDOList(objectDOList);
        }
      }
    }
    return ioaReceiveFileVO;
  }

  @Override
  public List<SysDataDict> memberList(String dictCode) throws Exception {
    List<SysDataDict> memberList = null;
    if (StringUtils.isNotEmpty(dictCode)) {
      memberList = sysDataDictService.getByCode(dictCode);
    }
    return memberList;
  }

  @Override
  public RetMsg toVoid(String processInstanceId, String bizId) throws Exception {
    RetMsg retMsg = new RetMsg();
    // runtimeService.deleteProcessInstance(processInstanceId, "收文阅件流程作废");
    runtimeService.deleteProcessInstance(processInstanceId, null);
    Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
    queryWhere.eq("process_instance_id", processInstanceId);
    actAljoinQueryService.delete(queryWhere);
    Where<ActAljoinQueryHis> queryHisWhere = new Where<ActAljoinQueryHis>();
    queryHisWhere.eq("process_instance_id", processInstanceId);
    actAljoinQueryHisService.delete(queryHisWhere);
    if (StringUtils.isNotEmpty(bizId)) {
      if (bizId.indexOf(";") > -1) {
        List<String> idList = Arrays.asList(bizId.split(";"));
        if (null != idList && !idList.isEmpty()) {
          Long id = Long.valueOf(idList.get(0));
          if (null != id) {
            deleteById(id);
          }
        }
      } else {
        deleteById(Long.valueOf(bizId));
      }
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  /**
   *
   * 待办列表排序.
   *
   *             @return：void
   *
   * @author：wangj
   *
   * @date：2017-12-02
   */
  public void sortList(List<IoaReceiveFileDO> list, IoaReceiveFileVO obj) {
    // 填报时间升序
    if (!StringUtils.isEmpty(obj.getFillingTimeIsAsc()) && obj.getFillingTimeIsAsc().equals("1")) {
      Comparator<IoaReceiveFileDO> comparator = new Comparator<IoaReceiveFileDO>() {
        @Override
        public int compare(IoaReceiveFileDO o1, IoaReceiveFileDO o2) {
          // 升序
          return o1.getFillingTime().compareTo(o2.getFillingTime());
        }
      };
      Collections.sort(list, comparator);
    }
  }

  @Override
  @Transactional
  public RetMsg revoke2Task(String processInstanceId, String bizId, AutUser user) throws Exception {
    RetMsg retMsg = new RetMsg();
    List<HistoricActivityInstance> activities =
        historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
            .orderByHistoricActivityInstanceStartTime().desc().list();
    if (!activities.isEmpty()) {
      if (!activities.get(0).getActivityType().equals("userTask")) {
        retMsg.setCode(1);// 排他网关不允许撤回
        retMsg.setMessage("当前任务节点不满足撤回条件");
        return retMsg;
      }
      String thistaskId = activities.get(0).getTaskId(); // 当前环节id
      String pretaskId = activities.get(1).getTaskId(); // 上一环节id
      HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(thistaskId).singleResult();
      if (null != hisTask.getClaimTime() && StringUtils.isNotEmpty(hisTask.getAssignee())) {
        // 任务已被办理人签收不可撤回
        retMsg.setCode(1);
        retMsg.setMessage("任务已被办理人签收不可撤回");
        return retMsg;
      }
      HistoricTaskInstance histask =
          historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
              .taskId(pretaskId).taskAssignee(user.getId().toString()).singleResult();
      if (null == histask) {
        // 当前用户不是上一环节办理人
        retMsg.setCode(1);
        retMsg.setMessage("当前用户不是上一环节办理人，不可撤回");
        return retMsg;
      } else {
        List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(thistaskId);
        String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
        List<String> assignees = new ArrayList<String>();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(processInstanceId)
            && null != currentTask && !currentTask.isEmpty()) {
          // 填写意见
          Authentication.setAuthenticatedUserId(user.getId().toString());
          taskService.addComment(thistaskId, processInstanceId, "注：由操作人撤回");
          if (org.apache.commons.lang3.StringUtils.isNotEmpty(currentTaskKey)) {
            List<TaskDefinition> preList =
                activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
            if (preList.size() > 0) {
              // 历史上级节点
              HistoricTaskInstance preTask =
                  historyService.createHistoricTaskInstanceQuery().taskId(pretaskId).singleResult();
              String hispreTaskDefKey = preTask.getTaskDefinitionKey();
              TaskDefinition taskDefinition = preList.get(0);
              String targetTaskKey = taskDefinition.getKey();
              if (!hispreTaskDefKey.equals(targetTaskKey)) {
                // 查找到的上一环节不是历史中的上一环节，说明该文件不是正常正向流转的，不能撤回
                retMsg.setCode(1);
                retMsg.setMessage("已退回的任务不可以撤回");
                return retMsg;
              }
              if (!StringUtils.isEmpty(targetTaskKey)) {
                actActivitiService.jump2Task2(targetTaskKey, processInstanceId);
                Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
                    .taskDefinitionKey(targetTaskKey).singleResult();

                assignees.add(preTask.getAssignee());
                // （原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
                actFixedFormService.deleteOrgnlTaskAuth(task);
                HistoricTaskInstance hisTsk = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                if (null != hisTsk.getClaimTime()) { // unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
                  taskService.unclaim(task.getId());
                }
                if (assignees.size() == 1) {
                  taskService.addCandidateUser(task.getId(), assignees.get(0));
                  taskService.claim(task.getId(), assignees.get(0));
                  String userFullName = "";
                  if (null != task.getAssignee()) {
                    userFullName = autUserService.selectById(task.getAssignee()).getFullName();
                  }
                  actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);
                } else {
                  for (String assignee : assignees) {
                    taskService.addUserIdentityLink(task.getId(), assignee, "candidate");
                  }
                }
                actAljoinQueryService.cleanQureyCurrentUser(task.getId());
              }
            }
          }
        }
      }
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<FixedFormProcessLog> getAppTaskLogInfo(String processInstanceId) throws Exception {
    // 日志不显示流程名称了（本来格子就小。。。还要显示那么多东西）
    List<FixedFormProcessLog> logList = null;
    Map<String, Object> map = actFixedFormService.getAppLog(null, processInstanceId);
    if (null != map && !map.isEmpty()) {
      List<AutUser> assigneeList = new ArrayList<AutUser>();
      List<AutUser> recevieList = new ArrayList<AutUser>();
      if (null != map.get("assigneeIdList")) {
        List<Long> assigneeIdList = (List<Long>) map.get("assigneeIdList");
        if (null != assigneeIdList && !assigneeIdList.isEmpty()) {
          Where<AutUser> assigneeWhere = new Where<AutUser>();
          assigneeWhere.in("id", assigneeIdList);
          assigneeWhere.setSqlSelect("id,user_name,full_name");
          assigneeList = autUserService.selectList(assigneeWhere);
        }
      }
      if (null != map.get("recevieUserIdList")) {
        List<Long> recevieUserIdList = (List<Long>) map.get("recevieUserIdList");
        if (null != recevieUserIdList && !recevieUserIdList.isEmpty()) {
          Where<AutUser> recevieWhere = new Where<AutUser>();
          recevieWhere.in("id", recevieUserIdList);
          recevieWhere.setSqlSelect("id,user_name,full_name");
          recevieList = autUserService.selectList(recevieWhere);
        }
      }

      if (null != map.get("logList")) {
        logList = (List<FixedFormProcessLog>) map.get("logList");
        if (null != logList && !logList.isEmpty() && null != assigneeList
            && !assigneeList.isEmpty()) {
          for (FixedFormProcessLog log : logList) {
            String receivedUserName = "";
            for (AutUser user : recevieList) {
              if (null != user && null != user.getId() && null != log
                  && null != log.getRecevieUserId()) {
                if (log.getRecevieUserId().contains(String.valueOf(user.getId()))) {
                  receivedUserName += user.getFullName() + "; ";
                }
              }
            }
            log.setRecevieUserName(receivedUserName);
          }
        }
        if (null != logList && !logList.isEmpty() && null != recevieList
            && !recevieList.isEmpty()) {
          for (FixedFormProcessLog log : logList) {
            for (AutUser user : assigneeList) {
              if (null != user && null != user.getId() && null != log
                  && null != log.getOperationId()) {
                if (String.valueOf(user.getId()).equals(log.getOperationId())) {
                  log.setOperationName(user.getFullName());
                }
              }
            }
          }
        }
      }
    }
    return logList;
  }

  @Override
  public String getTaskName(String processInstanceId) throws Exception {
    // HistoricProcessInstance
    // processInstance=historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    String taskName = "";
    /*
     * if(processInstance!=null){ String key=processInstance.getBusinessKey(); if(key!=null &&
     * !"".equals(key)){ String keys[]=key.split(":"); if(keys!=null && keys.length>0){ String
     * processId=keys[0]; if(processId!=null && !"".equals(processId)){ Where<ActAljoinBpmn>
     * abWhere=new Where<ActAljoinBpmn>(); abWhere.eq("process_id", processId);
     * actAljoinBpmnService.selectOne(abWhere); } } } }
     */
    return taskName;
  }

  @Override
  public Map<String, Object> appDetail(IoaReceiveFile obj) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    Boolean isAssignee = false; // 当前用户是否该文件签收人
    String taskName = "";
    IoaReceiveFileDO ioaReceiveFileVO = null;
    if (null != obj && null != obj.getId()) {
      IoaReceiveFile ioaReceiveFile = selectById(obj.getId());
      if (null != ioaReceiveFile) {
        ioaReceiveFileVO = new IoaReceiveFileDO();
        Long prID = ioaReceiveFile.getBpmnId();
        if (prID != null) {
          Where<ActAljoinBpmn> qwhere = new Where<ActAljoinBpmn>();
          qwhere.eq("id", prID);
          ActAljoinBpmn actAljoinBpmn = actAljoinBpmnService.selectOne(qwhere);
          taskName = actAljoinBpmn.getProcessName();
        }
        BeanUtils.copyProperties(ioaReceiveFile, ioaReceiveFileVO);
        Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id", obj.getId());
        List<ResResource> resourceList = resResourceService.selectList(where);
        if (null != resourceList && !resourceList.isEmpty()) {
            ioaReceiveFileVO.setResResourceList(resourceList);
        } 
        if (null != ioaReceiveFile.getProcessInstanceId()) {
          List<Task> tskList = taskService.createTaskQuery()
              .processInstanceId(ioaReceiveFile.getProcessInstanceId()).list();
          if (null != tskList && !tskList.isEmpty()) {
            Task task = tskList.get(0);
            if (null != task) {
              Task tsk = taskService.createTaskQuery().taskId(task.getId()).singleResult();
              if (null != tsk) {
                HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                if (null == hisTask.getClaimTime() || StringUtils.isEmpty(hisTask.getAssignee())) {
                  // 文件未被签收，被当前用户签收，当前用户是该环节办理人
                  isAssignee = true;
                  taskService.claim(task.getId(), obj.getCreateUserId() + "");
                  AutUser atUser = new AutUser();
                  String userFullName = "";
                  Long userId = 0L;
                  String userName = "";
                  if (null != task.getAssignee()) {
                    atUser = autUserService.selectById(task.getAssignee());
                    if (atUser != null) {
                      userFullName = atUser.getFullName();
                      userId = atUser.getId();
                      userName = atUser.getUserName();                    
                      actAljoinQueryService.appUpdateAssigneeName(task.getId(), userFullName, userId,
                              userName);
                    }                   
                  }                
                } else {
                  if (hisTask.getAssignee().equals(String.valueOf(obj.getCreateUserId()))) {
                    isAssignee = true;                    
                  }
                }
              }
            }
          }
        }
      }
    }
    map.put("taskName", taskName);
    map.put("isAssignee", isAssignee);
    map.put("ioaReceiveFileVO", ioaReceiveFileVO);
    return map;
  }

  @Override
  public RetMsg appDirectorAudit(IoaReceiveFileVO obj, Long userID) throws Exception {
    RetMsg retMsg = new RetMsg();
    AutUser autUser = autUserService.selectById(userID);
    if (null != obj && null != obj.getId()) {
      IoaReceiveFile ioaReceiveFile = selectById(obj.getId());
      if (null != ioaReceiveFile) {
        if (StringUtils.isNotEmpty(obj.getOfficeOpinion())) {// 办公室拟办意见officeOpinion
          ioaReceiveFile.setOfficeOpinion(obj.getOfficeOpinion());
        }
        Where<IoaReceiveReadObject> objectWhere = new Where<IoaReceiveReadObject>();
        objectWhere.setSqlSelect("id,receive_file_id");
        objectWhere.eq("receive_file_id", obj.getId());
        List<IoaReceiveReadObject> objectList = ioaReceiveReadObjectService.selectList(objectWhere);
        if (null != objectList && !objectList.isEmpty()) {
          // 旧的通通删掉
          List<Long> ids = new ArrayList<Long>();
          Where<IoaReceiveReadUser> where1 = new Where<IoaReceiveReadUser>();
          where1.in("object_id", ids);
          where1.eq("receive_file_id", obj.getId());
          ioaReceiveReadUserService.delete(where1);
        }
        // 配合前端修改（前端每次传的都是完整的所选择的所有人）
        List<IoaReceiveReadObject> readObjectList = obj.getReadObjectList();
        List<IoaReceiveReadObject> receiveReadObjectList = new ArrayList<IoaReceiveReadObject>();
        List<IoaReceiveReadUser> readUserList = new ArrayList<IoaReceiveReadUser>();
        Set<Long> userIdList = new HashSet<Long>();
        String receiveUserIds = "";
        List<String> readUserIdList = new ArrayList<String>();
        List<String> readUserNameList = new ArrayList<String>();
        if (null != readObjectList && !readObjectList.isEmpty()) {
          for (IoaReceiveReadObject ioaReceiveReadObject : readObjectList) {
            if (null != ioaReceiveReadObject
                && StringUtils.isNotEmpty(ioaReceiveReadObject.getReadUserNames())) {
              IoaReceiveReadObject object = new IoaReceiveReadObject();
              String readUserIds = "";
              if (StringUtils.isNotEmpty(ioaReceiveReadObject.getReadUserIds())) {
                readUserIds = ioaReceiveReadObject.getReadUserIds();
                receiveUserIds += ioaReceiveReadObject.getReadUserIds();
              }

              String readUserNames = "";
              if (StringUtils.isNotEmpty(ioaReceiveReadObject.getReadUserNames())) {
                readUserNames = ioaReceiveReadObject.getReadUserNames();
              }
              object.setObjectId(null != ioaReceiveReadObject.getObjectId()
                  ? ioaReceiveReadObject.getObjectId() : 0L);
              object.setObjectName(StringUtils.isNotEmpty(ioaReceiveReadObject.getObjectName())
                  ? ioaReceiveReadObject.getObjectName() : "");
              object.setReceiveFileId(ioaReceiveFile.getId());
              receiveReadObjectList.add(object);
              if (StringUtils.isNotEmpty(readUserIds) && StringUtils.isNotEmpty(readUserNames)) {
                object.setReadUserIds(readUserIds);
                object.setReadUserNames(readUserNames);
              }
              if (StringUtils.isNotEmpty(obj.getIsCheckAllUser())
                  && obj.getIsCheckAllUser().equals("1")) {
                object.setReadUserIds("");
                object.setReadUserNames("");
              }
              object.setCreateUserId(autUser.getId());
              object.setCreateUserName(autUser.getUserName());
              object.setLastUpdateUserId(autUser.getId());
              object.setLastUpdateUserName(autUser.getUserName());
              ioaReceiveReadObjectService.insert(object);
              if (readUserIds.indexOf(";") > -1 && readUserNames.indexOf(";") > -1) {
                readUserIdList = Arrays.asList(readUserIds.split(";"));
                readUserNameList = Arrays.asList(readUserNames.split(";"));
                if (null != readUserIdList && !readUserIdList.isEmpty()) {
                  int i = 0;
                  for (String readUserId : readUserIdList) {
                    IoaReceiveReadUser ioaReceiveReadUser = new IoaReceiveReadUser();
                    ioaReceiveReadUser.setIsRead(0);
                    ioaReceiveReadUser.setCreateUserId(autUser.getId());
                    ioaReceiveReadUser.setCreateUserName(autUser.getUserName());
                    ioaReceiveReadUser.setLastUpdateUserId(autUser.getId());
                    ioaReceiveReadUser.setLastUpdateUserName(autUser.getUserName());
                    ioaReceiveReadUser.setReceiveFileId(ioaReceiveFile.getId());
                    ioaReceiveReadUser.setReceiveReadObjectId(object.getId());
                    ioaReceiveReadUser.setReadUserId(Long.valueOf(readUserId));
                    userIdList.add(ioaReceiveReadUser.getReadUserId());
                    ioaReceiveReadUser.setReadUserFullName(readUserNameList.get(i));
                    readUserList.add(ioaReceiveReadUser);
                    i++;
                  }
                }
              }
              if (StringUtils.isNotEmpty(obj.getIsCheckAllUser())
                  && obj.getIsCheckAllUser().equals("1")) {
                Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
                departmentUserWhere.setSqlSelect("id,dept_id,user_id");
                List<AutDepartmentUser> departmentUserList =
                    autDepartmentUserService.selectList(departmentUserWhere);
                Set<Long> uIdList = new HashSet<Long>();
                if (null != departmentUserList && !departmentUserList.isEmpty()) {
                  for (AutDepartmentUser departmentUser : departmentUserList) {
                    uIdList.add(departmentUser.getUserId());
                  }
                }
                if (null != uIdList && !uIdList.isEmpty()) {
                  Where<AutUser> userWhere = new Where<AutUser>();
                  userWhere.setSqlSelect("id,user_name,full_name");
                  userWhere.in("id", uIdList);
                  List<AutUser> userList = autUserService.selectList(userWhere);
                  if (null != userList && !userList.isEmpty()) {
                    for (AutUser user : userList) {
                      IoaReceiveReadUser ioaReceiveReadUser = new IoaReceiveReadUser();
                      ioaReceiveReadUser.setIsRead(0);
                      ioaReceiveReadUser.setReceiveFileId(ioaReceiveFile.getId());
                      ioaReceiveReadUser.setReceiveReadObjectId(object.getId());
                      ioaReceiveReadUser.setReadUserId(user.getId());
                      userIdList.add(user.getId());
                      ioaReceiveReadUser.setCreateUserId(autUser.getId());
                      ioaReceiveReadUser.setCreateUserName(autUser.getCreateUserName());
                      ioaReceiveReadUser.setLastUpdateUserId(autUser.getId());
                      ioaReceiveReadUser.setLastUpdateUserName(autUser.getUserName());
                      ioaReceiveReadUser.setReadUserFullName(user.getFullName());
                      readUserList.add(ioaReceiveReadUser);
                    }
                  }
                }
              }
            }
          }
        }
        if (StringUtils.isNotEmpty(receiveUserIds)) {
          ioaReceiveFile.setReadUserIds(receiveUserIds);
        }
        ioaReceiveFile.setLastUpdateUserId(autUser.getId());
        ioaReceiveFile.setLastUpdateUserName(autUser.getUserName());
        // 修改 传阅文件
        updateById(ioaReceiveFile);
        // 批量插入传阅用户记录
        if (null != readUserList && !readUserList.isEmpty()) {
          if (null != userIdList && !userIdList.isEmpty()) {
            Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
            where.setSqlSelect("id,dept_id,user_id");
            where.in("user_id", userIdList);
            List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(where);
            List<Long> deptIdList = new ArrayList<Long>();
            if ((null != departmentUserList && !departmentUserList.isEmpty())) {
              for (AutDepartmentUser departmentUser : departmentUserList) {
                for (IoaReceiveReadUser user : readUserList) {
                  if (user.getReadUserId().equals(departmentUser.getUserId())
                      && user.getReadUserId().intValue() == departmentUser.getUserId().intValue()) {
                    user.setReadDeptId(
                        null != departmentUser.getDeptId() ? departmentUser.getDeptId() : 0L);
                    deptIdList.add(user.getReadDeptId());
                  }
                }
              }
            }
            if (null != deptIdList && !deptIdList.isEmpty()) {
              Where<AutDepartment> autDepartmentWhere = new Where<AutDepartment>();
              autDepartmentWhere.setSqlSelect("id,dept_name");
              autDepartmentWhere.in("id", deptIdList);
              List<AutDepartment> departmentList =
                  autDepartmentService.selectList(autDepartmentWhere);
              if ((null != departmentList && !departmentList.isEmpty())) {
                for (AutDepartment department : departmentList) {
                  for (IoaReceiveReadUser user : readUserList) {
                    if (user.getReadDeptId().equals(department.getId())
                        && user.getReadDeptId().intValue() == department.getId().intValue()) {
                      user.setReadDeptName(StringUtils.isNotEmpty(department.getDeptName())
                          ? department.getDeptName() : "");
                    }
                    if (null == user.getReadDeptName()) {
                      user.setReadDeptName("");
                    }
                  }
                }
              }
            }
          }
          ioaReceiveReadUserService.insertBatch(readUserList);
        }
        for (IoaReceiveReadObject object : objectList) {
          ioaReceiveReadObjectService.physicsDeleteById(object.getId());
        }
      }
    }

    retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
    retMsg.setMessage("操作成功");
    return retMsg;
  }
}
