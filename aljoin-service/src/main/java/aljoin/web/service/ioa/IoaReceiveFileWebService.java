package aljoin.web.service.ioa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.entity.ActAljoinFixedConfig;
import aljoin.act.dao.entity.ActAljoinQuery;
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
import aljoin.ioa.dao.entity.IoaReceiveFile;
import aljoin.ioa.dao.entity.IoaReceiveReadObject;
import aljoin.ioa.dao.entity.IoaReceiveReadUser;
import aljoin.ioa.dao.object.IoaReceiveFileDO;
import aljoin.ioa.dao.object.IoaReceiveFileVO;
import aljoin.ioa.iservice.IoaReceiveFileService;
import aljoin.ioa.iservice.IoaReceiveReadObjectService;
import aljoin.ioa.iservice.IoaReceiveReadUserService;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.util.DateUtil;
import aljoin.util.StringUtil;
import aljoin.web.service.act.ActActivitiWebService;

/**
 * 收文阅件表(服务实现类).
 * 
 * @author：zhongjy
 * @date： 2017-11-08
 */
@Service
public class IoaReceiveFileWebService {
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
	private AutDataStatisticsService autDataStatisticsService;
	@Resource
	private IoaReceiveFileService ioaReceiveFileService;
	@Resource
	private ActActivitiWebService actActivitiWebService;
	@Resource
	private ActAljoinQueryService actAljoinQueryService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private ActFixedFormServiceImpl actFixedFormService;
	@Resource
	private IoaReceiveReadUserService ioaReceiveReadUserService;
	@Resource
	private IoaReceiveReadObjectService ioaReceiveReadObjectService;
	@Resource
	private HistoryService historyService;
	@Resource
	private ActActivitiService actActivitiService;
	@Resource
    private ActAljoinBpmnRunService actAljoinBpmnRunService;

    @Transactional
    public RetMsg submit(IoaReceiveFileVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        ActAljoinBpmn bpmn = null;
        if (null != obj) {
            IoaReceiveFile ioaReceiveFile = new IoaReceiveFile();
            BeanUtils.copyProperties(obj, ioaReceiveFile);
            Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
            configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_READ_RECEIPT);
            configWhere.setSqlSelect("id,process_code,process_id,process_name,is_active");
            ActAljoinFixedConfig config = actAljoinFixedConfigService.selectOne(configWhere);
            if (null != config) {
                Where<ActAljoinBpmn> bpmnWhere = new Where<ActAljoinBpmn>();
                bpmnWhere.eq("process_id", config.getProcessId());
                bpmnWhere.eq("is_deploy", 1);
                bpmnWhere.eq("is_active", 1);
                bpmn = actAljoinBpmnService.selectOne(bpmnWhere);
                if (null != bpmn) {
                    ioaReceiveFile.setBpmnId(bpmn.getId());
                    ioaReceiveFile.setProcessId(bpmn.getProcessId());
                }
            }
            // 保存收文阅件
            ioaReceiveFileService.insert(ioaReceiveFile);
            IoaReceiveFile receiveFile = null;
            // 启动流程
            if (null != bpmn) {
                Map<String, String> param = new HashMap<String, String>();
                param.put("bizType", "ioaReceiveFile");
                param.put("bizId", ioaReceiveFile.getId() + "");
                // param.put("isUrgent","1");
                
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
                
                ProcessInstance instance =
                    activitiService.startBpmn(bpmnRun, null, param, ioaReceiveFile.getCreateUserId());
                if (null != ioaReceiveFile && null != ioaReceiveFile.getId()) {
                    receiveFile = ioaReceiveFileService.selectById(ioaReceiveFile.getId());
                    receiveFile.setProcessInstanceId(instance.getProcessInstanceId());
                    ioaReceiveFileService.updateById(receiveFile);
                }
                Where<AutUser> userWhere = new Where<AutUser>();
                userWhere.setSqlSelect("id,user_name,full_name");
                userWhere.eq("id", ioaReceiveFile.getCreateUserId());
                AutUser user = autUserService.selectOne(userWhere);
                actActivitiWebService.insertProcessQuery(instance, "1", null, ioaReceiveFile.getFileTitle(),
                    user.getFullName(), user.getFullName(), bpmn.getCategoryId(), true);
                Task task =
                    taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId()).singleResult();
                HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                if (null == hisTask.getClaimTime() || StringUtils.isEmpty(hisTask.getAssignee())) {
                    taskService.claim(task.getId(), String.valueOf(ioaReceiveFile.getCreateUserId()));
                    // 清空当前办理人，更新aljoin_query
                    String userFullName = "";
                    if (null != task.getAssignee()) {
                        userFullName = autUserService.selectById(task.getAssignee()).getFullName();
                    }
                    actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);
                }
                // 记录流程日志
                actActivitiWebService.insertOrUpdateActivityLog(task.getProcessInstanceId(),
                    WebConstant.PROCESS_OPERATE_STATUS_1, ioaReceiveFile.getCreateUserId(),null);
            }
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
                    resResource.setFileDesc("收文阅件新增保存附件上传");
                }
                resResourceService.updateBatchById(addResource);
            }
            retMsg.setObject(receiveFile);
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        } else {
            // 更新收文阅卷业务表内容
            ioaReceiveFileService.update(obj);
            // 提交
            retMsg = updateSubmit(obj);
        }
        return retMsg;
    }

	@Transactional
	public RetMsg updateSubmit(IoaReceiveFileVO obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (null != obj && null != obj.getId()) {
			IoaReceiveFile ioaReceiveFile = ioaReceiveFileService.selectById(obj.getId());
			if (null != ioaReceiveFile) {
				Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
				queryWhere.setSqlSelect("id");
				queryWhere.eq("process_instance_id",ioaReceiveFile.getProcessInstanceId());
				int count = actAljoinQueryService.selectCount(queryWhere);
				if(0 == count){
					ActAljoinBpmn bpmn = null;
					Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
					configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_READ_RECEIPT);
					configWhere.setSqlSelect("id,process_code,process_id,process_name,is_active");
					ActAljoinFixedConfig config = actAljoinFixedConfigService.selectOne(configWhere);

					Where<ActAljoinBpmn> bpmnWhere = new Where<ActAljoinBpmn>();
					bpmnWhere.eq("process_id", config.getProcessId());
					bpmnWhere.eq("is_deploy", 1);
					bpmnWhere.eq("is_active", 1);
					bpmn = actAljoinBpmnService.selectOne(bpmnWhere);
					if (null != bpmn) {
						ioaReceiveFile.setBpmnId(bpmn.getId());
						ioaReceiveFile.setProcessId(bpmn.getProcessId());
						Map<String, String> param = new HashMap<String, String>();
						param.put("bizType", "ioaReceiveFile");
						param.put("bizId", ioaReceiveFile.getId() + "");
						
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
			            
						// param.put("isUrgent","1");
						ProcessInstance instance = activitiService.startBpmn(bpmnRun, null, param,
							ioaReceiveFile.getCreateUserId());
						if (null != ioaReceiveFile && null != ioaReceiveFile.getId()) {
							ioaReceiveFile = ioaReceiveFileService.selectById(ioaReceiveFile.getId());
							ioaReceiveFile.setProcessInstanceId(instance.getProcessInstanceId());
							ioaReceiveFileService.updateById(ioaReceiveFile);
						}
						Where<AutUser> userWhere = new Where<AutUser>();
						userWhere.setSqlSelect("id,user_name,full_name");
						userWhere.eq("id", ioaReceiveFile.getCreateUserId());
						AutUser user = autUserService.selectOne(userWhere);
						actActivitiWebService.insertProcessQuery(instance, "1", null, ioaReceiveFile.getFileTitle(),
							user.getFullName(), user.getFullName(), bpmn.getCategoryId(),false);
						Task task = taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId())
							.singleResult();
						String isClaim = taskService.createTaskQuery().taskId(task.getId()).singleResult().getAssignee();
						if (StringUtils.isEmpty(isClaim)) {
							taskService.setAssignee(task.getId(), String.valueOf(ioaReceiveFile.getCreateUserId()));
						}
						// 记录流程日志
						actActivitiWebService.insertOrUpdateActivityLog(instance.getId(), WebConstant.PROCESS_OPERATE_STATUS_1,ioaReceiveFile.getCreateUserId(),null);
					}else{
					  retMsg.setCode(1);
				      retMsg.setMessage("找不到流程");
					}
					
				}
			}
			retMsg.setObject(ioaReceiveFile);
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	@Transactional
	public RetMsg add(IoaReceiveFileVO obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		ActAljoinBpmn bpmn = null;
		if (null != obj) {
		    if (null != obj.getId()){
		      //更新收文阅卷业务表内容
		      retMsg = ioaReceiveFileService.update(obj);
		    }else{
		      if (StringUtils.isEmpty(obj.getFileTitle())) {
                obj.setFileTitle("");
                retMsg.setCode(1);
                retMsg.setMessage("来文标题不能为空!");
                return retMsg;
            }
            if (StringUtils.isEmpty(obj.getFileType())) {
                obj.setFileType("");
                retMsg.setCode(1);
                retMsg.setMessage("来文类型不能为空!");
                return retMsg;
            }
            if (StringUtils.isEmpty(obj.getFileTypeName())) {
                obj.setFileTypeName("");
                retMsg.setCode(1);
                retMsg.setMessage("来文类型名称不能为空!");
                return retMsg;
            }
            if (StringUtils.isEmpty(obj.getFromUnit())) {
                obj.setFromUnit("");
                retMsg.setCode(1);
                retMsg.setMessage("来文单位不能为空!");
                return retMsg;
            }
            if (StringUtils.isEmpty(obj.getFromUnitName())) {
                /*obj.setFromUnitName("");
                retMsg.setCode(1);
                retMsg.setMessage("来文单位名称不能为空!");
                return retMsg;*/
                obj.setFromUnitName(obj.getFromUnit());
            }
            if (StringUtils.isEmpty(obj.getFromFileCode())) {
                obj.setFromFileCode("");
                retMsg.setCode(1);
                retMsg.setMessage("来文文号不能为空!");
                return retMsg;
            }

            Date date = new Date();
            if (null == obj.getOrgnlFileTime()) {
                obj.setOrgnlFileTime(date);
                retMsg.setCode(1);
                retMsg.setMessage("原文日期不能为空!");
                return retMsg;
            }
            if (null == obj.getReceiveFileTime()) {
                obj.setReceiveFileTime(date);
                retMsg.setCode(1);
                retMsg.setMessage("收文日期不能为空!");
                return retMsg;
            }

            if (StringUtils.isEmpty(obj.getOfficeOpinion())) {
                obj.setOfficeOpinion("");
            }
            if (null == obj.getIsClose()) {
                obj.setIsClose(0);
            }
            if (StringUtils.isEmpty(obj.getReadUserIds())) {
                obj.setReadUserIds("");
            }
            if (StringUtils.isEmpty(obj.getProcessId())) {
                obj.setProcessId("");
            }
            if (null == obj.getBpmnId()) {
                obj.setBpmnId(0L);
            }
            if (StringUtils.isEmpty(obj.getProcessInstanceId())) {
                obj.setProcessInstanceId("");
            }
            if (null == obj.getHandleLimitTime()) {
                obj.setHandleLimitTime(new Date());
            }
            IoaReceiveFile ioaReceiveFile = new IoaReceiveFile();
            BeanUtils.copyProperties(obj, ioaReceiveFile);
            Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
            configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_READ_RECEIPT);
            configWhere.setSqlSelect("id,process_code,process_id,process_name,is_active");
            ActAljoinFixedConfig config = actAljoinFixedConfigService.selectOne(configWhere);
            if (null != config) {
                Where<ActAljoinBpmn> bpmnWhere = new Where<ActAljoinBpmn>();
                bpmnWhere.eq("process_id", config.getProcessId());
                bpmnWhere.eq("is_deploy", 1);
                bpmnWhere.eq("is_active", 1);
                bpmn = actAljoinBpmnService.selectOne(bpmnWhere);
                if (null != bpmn) {
                    ioaReceiveFile.setBpmnId(bpmn.getId());
                    ioaReceiveFile.setProcessId(bpmn.getProcessId());
                }
            }
            // 保存收文阅件
            ioaReceiveFileService.insert(ioaReceiveFile);
            IoaReceiveFile receiveFile = null;
            // 启动流程
            if (null != bpmn) {
                Map<String, String> param = new HashMap<String, String>();
                param.put("bizType", "ioaReceiveFile");
                param.put("bizId", ioaReceiveFile.getId() + "");
                // param.put("isUrgent","1");
                
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
                ProcessInstance instance = activitiService.startBpmn(bpmnRun, null, param,
                        ioaReceiveFile.getCreateUserId());
                if (null != ioaReceiveFile && null != ioaReceiveFile.getId()) {
                    receiveFile = ioaReceiveFileService.selectById(ioaReceiveFile.getId());
                    receiveFile.setProcessInstanceId(instance.getProcessInstanceId());
                    ioaReceiveFileService.updateById(receiveFile);
                }
                Where<AutUser> userWhere = new Where<AutUser>();
                userWhere.setSqlSelect("id,user_name,full_name");
                userWhere.eq("id", ioaReceiveFile.getCreateUserId());
                AutUser user = autUserService.selectOne(userWhere);
                actActivitiWebService.insertProcessQuery(instance, "1", null, ioaReceiveFile.getFileTitle(),
                        user.getFullName(), user.getFullName(), bpmn.getCategoryId(),true);
                Task task = taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId())
                        .singleResult();
                HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                if (null == hisTask.getClaimTime() || StringUtils.isEmpty(hisTask.getAssignee())) {
                    taskService.claim(task.getId(), String.valueOf(ioaReceiveFile.getCreateUserId()));
                    String userFullName = "";
                    if(null != task.getAssignee()){
                        userFullName = autUserService.selectById(task.getAssignee()).getFullName();
                    }
                    actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);
                }
                // 记录流程日志
                actActivitiWebService.insertOrUpdateActivityLog(task.getProcessInstanceId(), WebConstant.PROCESS_OPERATE_STATUS_1,ioaReceiveFile.getCreateUserId(),null);
            }
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
                    resResource.setFileDesc("收文阅件新增保存附件上传");
                }
                resResourceService.updateBatchById(addResource);
            }

            List<String> userIdList = new ArrayList<String>();
            userIdList.add(String.valueOf(ioaReceiveFile.getCreateUserId()));
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
	 * 			@return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-12-02
	 */
	public void sortList(List<IoaReceiveFileDO> list, IoaReceiveFileVO obj) {
		// 填报时间升序
		if (!StringUtils.isEmpty(obj.getFillingTimeIsAsc()) && "1".equals(obj.getFillingTimeIsAsc())) {
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
	
	  @Transactional
	  public RetMsg completeTask(Map<String, Object> variables, String processInstanceId,
	      String receiveFileId, String readUserIds, String message, String currentUserId, String isEnd,
	      String isOffice) throws Exception {
	    RetMsg retMsg = new RetMsg();
	    AutUser autUser = autUserService.selectById(Long.valueOf(currentUserId));

	    List<IoaReceiveReadUser> readUserList = new ArrayList<IoaReceiveReadUser>();
	    List<Long> list = new ArrayList<Long>();
	    Map<String, Long> map = new HashMap<String, Long>();
	    // 当前任务
	    List<Task> tskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
	    if (null != tskList && !tskList.isEmpty()) {
	      // 普通用户任务
	      if (tskList.size() == 1) {
	        Task t = tskList.get(0);
	        // 当前环节办理人是否签收
	        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(t.getId()).singleResult();
	        if (null != hisTask.getClaimTime() && StringUtils.isNotEmpty(hisTask.getAssignee())) {
	          // 当前环节办理人不是当前用户，说明提交前被其他人签收
	          if (!hisTask.getAssignee().equals(currentUserId)) {
	            retMsg.setCode(1);
	            retMsg.setMessage("提交失败，文件被撤回或被其他人签收");
	            return retMsg;
	          }
	        } else {
	          Boolean istaskCandidate = false;
	          List<IdentityLink> candidateList = taskService.getIdentityLinksForTask(t.getId());
	          for (IdentityLink identityLink : candidateList) {
	            if (identityLink.getUserId().equals(String.valueOf(currentUserId))) {
	              // 没签收帮他签收一下
	              istaskCandidate = true;
	              taskService.claim(t.getId(), String.valueOf(currentUserId));
	              break;
	            }
	          }
	          // 在当前任务候选人中没找到当前用户，说明操作时，环节有改变
	          if (!istaskCandidate) {
	            retMsg.setCode(1);
	            retMsg.setMessage("提交失败，环节信息已改变，请刷新页面重新获取");
	            return retMsg;
	          }
	        }
	        Authentication.setAuthenticatedUserId(currentUserId);
	        Comment comment = taskService.addComment(t.getId(), processInstanceId, message);
	        if (isOffice != null && StringUtils.isNotEmpty(isOffice)) {// 是否是池主任审批环节
	          Where<IoaReceiveFile> filewhere = new Where<IoaReceiveFile>();
	          filewhere.eq("process_instance_id", processInstanceId);
	          IoaReceiveFile file = ioaReceiveFileService.selectOne(filewhere);
	          if (null != file) {
	            String officeOpinion = message + "(" + autUser.getFullName() + " "
	                + DateUtil.datetime2str(comment.getTime()) + ")";
	            file.setOfficeOpinion(officeOpinion);
	            ioaReceiveFileService.updateById(file);
	          }
	        }

	        if (StringUtils.isNotEmpty(receiveFileId)) {
	          if (StringUtils.isNotEmpty(readUserIds)) {
	            if (readUserIds.indexOf(";") > -1) {
	              List<String> userIdList = Arrays.asList(readUserIds.split(";"));
	              if (null != userIdList && !userIdList.isEmpty()) {
	                for (String userId : userIdList) {
	                  map.put(userId, Long.valueOf(userId));
	                }
	              }
	            }
	          }
	          if (StringUtils.isEmpty(isEnd)) {
	            for (String key : map.keySet()) {
	              list.add(map.get(key));
	            }
	            Map<String, Object> varMap = new HashMap<String, Object>();
	            varMap.put("assigneeList", list);
	            runtimeService.setVariables(processInstanceId, varMap);
	          }
	        }
	        activitiService.completeTask(variables, t.getId());
	      } else {
	        List<Task> taskList = taskService.createTaskQuery().taskCandidateOrAssigned(currentUserId)
	            .processInstanceId(processInstanceId).list();
	        if (taskList.size() == 0 || taskList.size() > 1) {
	          retMsg.setCode(1);
	          retMsg.setMessage("提交失败,流程信息受损，请通知创建人重新发起流程！");
	          return retMsg;
	        }
	        Task task = taskList.get(0);
	        Authentication.setAuthenticatedUserId(currentUserId);
	        taskService.addComment(task.getId(), processInstanceId, message);
	        activitiService.completeTask(variables, task.getId());
	      }
	    }
	    // 下一环节办理人处理
	    List<String> userIdList = new ArrayList<String>();
	    ProcessInstance pi = runtimeService.createProcessInstanceQuery()
	        .processInstanceId(processInstanceId).singleResult();
	    String linkName = "";
	    Map<String, List<String>> returnMap = new HashMap<String, List<String>>();
	    if (!readUserIds.isEmpty()) {
	      userIdList = Arrays.asList(readUserIds.split(";"));
	      List<Task> taskList =
	          taskService.createTaskQuery().processInstanceId(processInstanceId).list();
	      if (null != userIdList && !userIdList.isEmpty()) {
	        if (taskList.size() == 1) {
	          // 下一个节点是普通用户节点
	          Task task = taskList.get(0);
	          linkName = task.getName();
	          actFixedFormService.deleteOrgnlTaskAuth(task);
	          HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
	          if (null != hisTask.getClaimTime()) {
	            taskService.unclaim(task.getId());
	          }
	          for (String userId : userIdList) {
	            taskService.addCandidateUser(task.getId(), userId);
	          }
	          actAljoinQueryService.cleanQureyCurrentUser(task.getId());
	        } else {
	          // 下一个节点是会签节点
	          for (int i = 0; i < taskList.size(); i++) {
	            // 解签收
	            // taskService.unclaim(taskList.get(i).getId());
	            // 删除原来候选人信息(含分组和候选人)
	            actFixedFormService.deleteOrgnlTaskAuth(taskList.get(i));
	            // 重新添加候选人和分组
	            taskService.addUserIdentityLink(taskList.get(i).getId(), userIdList.get(i),
	                "candidate");
	            returnMap.put(taskList.get(i).getId() + "|" + taskList.get(i).getName(), userIdList);
	          }
	        }
	      }
	    }
	    if (StringUtils.isNotEmpty(isEnd) && isEnd.equals("1")) {
	      // 是结束节点时 查询传阅用户记录
	      Where<IoaReceiveReadUser> where = new Where<IoaReceiveReadUser>();
	      where.setSqlSelect(
	          "id,version,is_delete,create_time,create_user_id,create_user_name,receive_read_object_id,receive_file_id,read_user_id,is_read,read_time,read_opinion");
	      where.eq("receive_file_id", receiveFileId);

	      readUserList = ioaReceiveReadUserService.selectList(where);
	      // 修改 传阅用户记录为已读
	      // String readUserName = "";
	      for (IoaReceiveReadUser readUser : readUserList) {
	        if (String.valueOf(readUser.getReadUserId()).equals(currentUserId)) {
	          // readUserName = readUser.getReadUserFullName();
	          readUser.setIsRead(1);
	          readUser.setReadTime(new Date());
	          readUser.setReadOpinion(message);
	          readUser.setLastUpdateUserId(autUser.getId());
	          readUser.setLastUpdateUserName(autUser.getUserName());
	          ioaReceiveReadUserService.updateById(readUser);
	        }
	      }
	      if (null == pi) {
	        actFixedFormService.updateLastAsignee(processInstanceId, autUser.getFullName());
	        List<Integer> readStatusList = new ArrayList<Integer>();
	        for (IoaReceiveReadUser readUser : readUserList) {
	          if (readUser.getIsRead() == 1) {
	            readStatusList.add(readUser.getIsRead());
	          }
	        }
	        // 所有人员都已阅读 则归档
	        if ((null != readStatusList && !readStatusList.isEmpty())
	            && readStatusList.size() == readUserList.size()) {
	          IoaReceiveFile ioaReceiveFile = ioaReceiveFileService.selectById(Long.valueOf(receiveFileId));
	          if (null != ioaReceiveFile) {
	            ioaReceiveFile.setIsClose(1);
	            ioaReceiveFile.setLastUpdateUserId(autUser.getId());
	            ioaReceiveFile.setLastUpdateUserName(autUser.getUserName());
	            ioaReceiveFileService.updateById(ioaReceiveFile);
	          }
	        }
	      }
	    }
	    if (StringUtils.isEmpty(receiveFileId)) {
	      Where<IoaReceiveFile> filewhere = new Where<IoaReceiveFile>();
	      filewhere.eq("process_instance_id", processInstanceId);
	      filewhere.setSqlSelect("id,read_user_ids");
	      IoaReceiveFile file = ioaReceiveFileService.selectOne(filewhere);
	      if (null != file) {
	        receiveFileId = String.valueOf(file.getId());
	      } else {
	        throw new Exception("收文阅卷id为空，发送在线消息失败");
	      }
	    }
	    // 记录流程日志
	    actActivitiWebService.insertOrUpdateActivityLog(processInstanceId, WebConstant.PROCESS_OPERATE_STATUS_1,Long.valueOf(currentUserId),null);

	    // 返回信息给异步线程发送在线消息bizId + linkName
	    if (StringUtils.isNotEmpty(readUserIds)) {
	      if (StringUtils.isEmpty(linkName)) {
	        linkName = "传阅";
	      }
	      HashMap<String, Object> resultMap = new HashMap<String, Object>();
	      resultMap.put("linkName", linkName);
	      resultMap.put("bizId", receiveFileId);
	      resultMap.put("processInstanceId", processInstanceId);// 流程实例id
	      resultMap.put("handle", readUserIds);// 下一级办理人
	      resultMap.put("taskList", returnMap);
	      retMsg.setObject(resultMap);// 返回给controller异步调用在线消息
	    }
	    retMsg.setCode(0);
	    retMsg.setMessage("操作成功!");
	    return retMsg;
	  }

	  @Transactional
	  public RetMsg appCompleteTask(Map<String, Object> variables, String processInstanceId,
	      String receiveFileId, String readUserIds, String message, String currentUserId, String isEnd,
	      Long userid, String username, String isOffice) throws Exception {
	    RetMsg retMsg = new RetMsg();

	    AutUser autUser = autUserService.selectById(Long.valueOf(currentUserId));
	    List<Task> tskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
	    List<IoaReceiveReadUser> readUserList = new ArrayList<IoaReceiveReadUser>();
	    List<Long> list = new ArrayList<Long>();
	    Map<String, Long> map = new HashMap<String, Long>();
	    if (null != tskList && !tskList.isEmpty()) {
	      if (tskList.size() == 1) {
	        Task t = tskList.get(0);
	        // 当前环节办理人是否签收
	       HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(t.getId()).singleResult();
	        if (null != hisTask.getClaimTime() && StringUtils.isNotEmpty(hisTask.getAssignee())) {
	          // 当前环节办理人不是当前用户，说明提交前被其他人签收
	          if (!hisTask.getAssignee().equals(currentUserId)) {
	            retMsg.setCode(1);
	            retMsg.setMessage("提交失败，当前环节被其他人签收");
	            return retMsg;
	          }
	        } else {
	          Boolean istaskCandidate = false;
	          List<IdentityLink> candidateList = taskService.getIdentityLinksForTask(t.getId());
	          for (IdentityLink identityLink : candidateList) {
	            if (identityLink.getUserId().equals(String.valueOf(currentUserId))) {
	              istaskCandidate = true;
	              taskService.claim(t.getId(), String.valueOf(currentUserId));
	              break;
	            }
	          }
	          // 在当前任务候选人中没找到当前用户，说明操作时，环节有改变
	          if (!istaskCandidate) {
	            retMsg.setCode(AppConstant.RET_CODE_ERROR);
	            retMsg.setMessage("提交失败，环节信息已改变，请刷新页面重新获取");
	            return retMsg;
	          }
	        }
	        Authentication.setAuthenticatedUserId(String.valueOf(currentUserId));
	        Comment comment = taskService.addComment(t.getId(), processInstanceId, message);
	        if (isOffice != null && StringUtils.isNotEmpty(isOffice)) {// 是否是池主任审批环节
	          Where<IoaReceiveFile> filewhere = new Where<IoaReceiveFile>();
	          filewhere.eq("process_instance_id", processInstanceId);
	          IoaReceiveFile file = ioaReceiveFileService.selectOne(filewhere);
	          if (null != file) {
	            String officeOpinion = message + "(" + autUser.getFullName() + " "
	                + DateUtil.datetime2str(comment.getTime()) + ")";
	            file.setOfficeOpinion(officeOpinion);
	            ioaReceiveFileService.updateById(file);
	          }
	        }

	        if (StringUtils.isNotEmpty(receiveFileId)) {
	          if (StringUtils.isNotEmpty(readUserIds)) {
	            if (readUserIds.indexOf(";") > -1) {
	              List<String> userIdList = Arrays.asList(readUserIds.split(";"));
	              if (null != userIdList && !userIdList.isEmpty()) {
	                for (String userId : userIdList) {
	                  map.put(userId, Long.valueOf(userId));
	                }
	              }
	            }
	          }
	          if (StringUtils.isEmpty(isEnd)) {
	            for (String key : map.keySet()) {
	              list.add(map.get(key));
	            }
	            Map<String, Object> varMap = new HashMap<String, Object>();
	            varMap.put("assigneeList", list);
	            runtimeService.setVariables(processInstanceId, varMap);
	          }
	        }
	        activitiService.completeTask(variables, t.getId());
	      } else {
	        List<Task> taskList = taskService.createTaskQuery().taskCandidateOrAssigned(currentUserId)
	            .processInstanceId(processInstanceId).list();
	        if (taskList.size() == 0 || taskList.size() > 1) {
	          retMsg.setCode(AppConstant.RET_CODE_ERROR);
	          retMsg.setMessage("提交失败,流程信息受损，请通知创建人重新发起流程！");
	          return retMsg;
	        }
	        Task task = taskList.get(0);
	        Authentication.setAuthenticatedUserId(currentUserId);
	        taskService.addComment(task.getId(), processInstanceId, message);
	        activitiService.completeTask(variables, task.getId());
	      }
	    }
	    List<String> userIdList = new ArrayList<String>();
	    ProcessInstance pi = runtimeService.createProcessInstanceQuery()
	        .processInstanceId(processInstanceId).singleResult();
	    String linkName = "";
	    Map<String, List<String>> returnMap = new HashMap<String, List<String>>();


	    if (receiveFileId != null && !"".equals(receiveFileId) && readUserIds != null
	        && !"".equals(readUserIds)) {
	      userIdList = Arrays.asList(readUserIds.split(";"));
	      List<Task> taskList =
	          taskService.createTaskQuery().processInstanceId(processInstanceId).list();
	      if (null != userIdList && userIdList.size() > 0) {
	        // （原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选
	        if (taskList.size() == 1) {
	          // 下一个节点是普通用户节点
	          Task task = taskList.get(0);
	          linkName = task.getName();
	          actFixedFormService.deleteOrgnlTaskAuth(task);

	          HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
	          if (null != hisTask.getClaimTime()) {
	            taskService.unclaim(task.getId());
	          }
	          for (String userId : userIdList) {
	            taskService.addCandidateUser(task.getId(), userId);
	          }
	          actAljoinQueryService.cleanQureyCurrentUser(task.getId());
	        } else {
	          for (int i = 0; i < taskList.size(); i++) {
	            // 删除原来候选人信息(含分组和候选人)
	            actFixedFormService.deleteOrgnlTaskAuth(taskList.get(i));
	            // 重新添加候选人和分组
	            taskService.addUserIdentityLink(taskList.get(i).getId(), userIdList.get(i),
	                "candidate");
	            returnMap.put(taskList.get(i).getId() + "|" + taskList.get(i).getName(), userIdList);
	          }
	        }
	      }
	    }

	    if (isEnd != null && !"".equals(isEnd) && isEnd.equals("1")) {
	      // 是结束节点时 查询传阅用户记录
	      Where<IoaReceiveReadUser> where = new Where<IoaReceiveReadUser>();
	      where.setSqlSelect(
	          "id,version,is_delete,create_time,create_user_id,create_user_name,receive_read_object_id,receive_file_id,read_user_id,is_read,read_time,read_opinion");
	      where.eq("receive_file_id", receiveFileId);
	      readUserList = ioaReceiveReadUserService.selectList(where);
	      // 修改 传阅用户记录为已读
	      // String readUserName = "";
	      for (IoaReceiveReadUser readUser : readUserList) {
	        if (String.valueOf(readUser.getReadUserId()).equals(currentUserId)) {
	          readUser.setIsRead(1);
	          readUser.setReadTime(new Date());
	          readUser.setReadOpinion(message);
	          readUser.setLastUpdateUserId(autUser.getId());
	          readUser.setLastUpdateUserName(autUser.getUserName());
	          ioaReceiveReadUserService.updateById(readUser);
	        }
	      }
	      if (null == pi) {
	        actFixedFormService.updateLastAsignee(processInstanceId, autUser.getFullName());
	        List<Integer> readStatusList = new ArrayList<Integer>();
	        for (IoaReceiveReadUser readUser : readUserList) {
	          if (readUser.getIsRead() == 1) {
	            readStatusList.add(readUser.getIsRead());
	          }
	        }
	        // 所有人员都已阅读 则归档
	        if ((null != readStatusList && !readStatusList.isEmpty())
	            && readStatusList.size() == readUserList.size()) {
	          IoaReceiveFile ioaReceiveFile = ioaReceiveFileService.selectById(Long.valueOf(receiveFileId));
	          if (null != ioaReceiveFile) {
	            ioaReceiveFile.setIsClose(1);
	            ioaReceiveFile.setLastUpdateUserId(autUser.getId());
	            ioaReceiveFile.setLastUpdateUserName(autUser.getUserName());
	            ioaReceiveFileService.updateById(ioaReceiveFile);
	          }
	        }
	      }
	    }
	    if (receiveFileId == null || "".equals(receiveFileId)) {
	      Where<IoaReceiveFile> filewhere = new Where<IoaReceiveFile>();
	      filewhere.eq("process_instance_id", processInstanceId);
	      filewhere.setSqlSelect("id");
	      IoaReceiveFile file = ioaReceiveFileService.selectOne(filewhere);
	      if (null != file) {
	        receiveFileId = String.valueOf(file.getId());
	      } else {
	        throw new Exception("收文阅卷id为空，发送在线消息失败");
	      }
	    }
	    // 记录流程日志
        actActivitiWebService.insertOrUpdateActivityLog(processInstanceId, WebConstant.PROCESS_OPERATE_STATUS_1,Long.valueOf(currentUserId),null);
	    // 返回信息给异步线程发送在线消息bizId + linkName
	    if (readUserIds != null && !"".equals(readUserIds)) {
	      if (linkName == null || "".equals(linkName)) {
	        linkName = "传阅";
	      }
	      HashMap<String, Object> resultMap = new HashMap<String, Object>();
	      resultMap.put("linkName", linkName);
	      resultMap.put("bizId", receiveFileId);
	      resultMap.put("processInstanceId", processInstanceId);// 流程实例id
	      resultMap.put("handle", readUserIds);// 下一级办理人
	      resultMap.put("taskList", returnMap);
	      retMsg.setObject(resultMap);// 返回给controller异步调用在线消息
	    }
	    retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
	    retMsg.setMessage("操作成功!");
	    return retMsg;
	  }

	  @Transactional
	  public RetMsg jump2Task2(String processInstanceId, String bizId, String message,
	      Long createUserId) throws Exception {
	    String linkName = "";
	    String go2taskId = "";
	    if (null == message) {
	      message = "";
	    }
	    RetMsg retMsg = new RetMsg();
	    // String processInstanceId =
	    // taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
	    Task curTask =
	        taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
	    String taskId = curTask.getId();
	    List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(taskId);
	    List<String> assignees = new ArrayList<String>();
	    if (org.apache.commons.lang3.StringUtils.isNotEmpty(processInstanceId) && null != currentTask
	        && !currentTask.isEmpty()) {
	      String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
	      // 填写意见
	      Authentication.setAuthenticatedUserId(String.valueOf(createUserId));
	      taskService.addComment(taskId, processInstanceId, message);
	      if (org.apache.commons.lang3.StringUtils.isNotEmpty(currentTaskKey)) {
	        List<TaskDefinition> preList =
	            activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
	        if (preList.size() > 0) {
	          TaskDefinition taskDefinition = preList.get(0);
	          String targetTaskKey = taskDefinition.getKey();
	          if (!StringUtils.isEmpty(targetTaskKey)) {
	            List<HistoricTaskInstance> historicList =
	                historyService.createHistoricTaskInstanceQuery()
	                    .processInstanceId(processInstanceId).taskDefinitionKey(targetTaskKey)
	                    .orderByHistoricTaskInstanceEndTime().desc().finished().list();
	            if (historicList.isEmpty()) {
	              retMsg.setCode(1);
	              retMsg.setMessage("历史节点获取失败");
	              return retMsg;
	            }
	            // 理论上historicList永远不为空，为了避免勿操作，判断一下
	            HistoricTaskInstance historic = historicList.get(0);
	            assignees.add(historic.getAssignee());

	            actActivitiService.jump2Task2(targetTaskKey, processInstanceId);
	            Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
	                .taskDefinitionKey(targetTaskKey).singleResult();
	            linkName = task.getName();
	            go2taskId = task.getId();
	            // （原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
	            actFixedFormService.deleteOrgnlTaskAuth(task);

	            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
	            if (null != hisTask.getClaimTime()) { // unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
	              taskService.unclaim(task.getId());
	            }
	            for (String assignee : assignees) {
	              taskService.addUserIdentityLink(task.getId(), assignee, "candidate");
	            }
	            actAljoinQueryService.cleanQureyCurrentUser(task.getId());
	            actActivitiWebService.insertOrUpdateActivityLog(processInstanceId, WebConstant.PROCESS_OPERATE_STATUS_2, createUserId,null);
	          }
	        }
	      }
	    }
	    Where<IoaReceiveFile> filewhere = new Where<IoaReceiveFile>();
	    filewhere.eq("process_instance_id", processInstanceId);
	    IoaReceiveFile file = ioaReceiveFileService.selectOne(filewhere);
	    // 回退时，如果发现文件的传阅人员不为空，则置空
	    Where<IoaReceiveReadObject> objectWhere = new Where<IoaReceiveReadObject>();
	    objectWhere.setSqlSelect("id,receive_file_id");
	    objectWhere.eq("receive_file_id", file.getId());
	    List<IoaReceiveReadObject> objectList = ioaReceiveReadObjectService.selectList(objectWhere);
	    if (null != objectList && !objectList.isEmpty()) {
	      // 旧的通通删掉
	      List<Long> ids = new ArrayList<Long>();
	      Where<IoaReceiveReadUser> where1 = new Where<IoaReceiveReadUser>();
	      where1.in("object_id", ids);
	      where1.eq("receive_file_id", file.getId());
	      ioaReceiveReadUserService.delete(where1);
	    }
	    for (IoaReceiveReadObject object : objectList) {
	      ioaReceiveReadObjectService.physicsDeleteById(object.getId());
	    }
	    file.setReadUserIds("");
	    file.setOfficeOpinion("");
	    ioaReceiveFileService.updateById(file);
	    HashMap<String, String> resultMap = new HashMap<String, String>();
	    String handle = StringUtil.list2str(assignees, ";");
	    resultMap.put("linkName", linkName);
	    if (StringUtils.isEmpty(bizId)) {
	      bizId = file.getId().toString();
	    }
	    resultMap.put("bizId", bizId);
	    resultMap.put("processInstanceId", processInstanceId);// 流程实例id
	    resultMap.put("handle", handle);// 下一级办理人
	    resultMap.put("taskId", go2taskId);
	    retMsg.setObject(resultMap);// 返回给controller异步调用在线消息
	    retMsg.setCode(0);
	    retMsg.setMessage("操作成功");
	    return retMsg;
	  }
}
