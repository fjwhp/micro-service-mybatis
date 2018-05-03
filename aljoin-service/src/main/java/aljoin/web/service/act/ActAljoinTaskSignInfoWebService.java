package aljoin.web.service.act;

import aljoin.act.dao.entity.ActAljoinActivityLog;
import aljoin.act.dao.entity.ActAljoinTaskSignInfo;
import aljoin.act.dao.entity.ActRuTask;
import aljoin.act.iservice.ActAljoinActivityLogService;
import aljoin.act.iservice.ActAljoinTaskSignInfoService;
import aljoin.act.iservice.ActRuTaskService;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.RetMsg;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 
 * @描述：加签信息表(服务实现类).
 * 
 * @作者：wangj
 * 
 * @时间: 2018-02-05
 */
@Service
public class ActAljoinTaskSignInfoWebService {

  @Resource
  private ActAljoinTaskSignInfoService actAljoinTaskSignInfoService;
  @Resource
  private AutUserService autUserService;
  @Resource
  private TaskService taskService;
  @Resource
  private ActAljoinActivityLogService actAljoinActivityLogService;
  @Resource
  private ActRuTaskService actRuTaskService;

  public RetMsg getSignedUserIds(Long currentUserId,String taskId) throws Exception {
    RetMsg retMsg = new RetMsg();
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    Where<ActAljoinActivityLog> logWhere = new Where<ActAljoinActivityLog>();
    logWhere.setSqlSelect("id,receive_user_ids,proc_inst_id,current_task_def_key,last_task_def_key,operate_user_id");
    logWhere.eq("proc_inst_id",task.getProcessInstanceId());
    logWhere.eq("current_task_def_key",task.getTaskDefinitionKey());
    logWhere.andNew(" proc_inst_id = {0} and current_task_def_key = {1} or operate_status = 1 or operate_status = 4",task.getProcessInstanceId(),task.getTaskDefinitionKey());
    logWhere.orderBy("id",true);
    List<ActAljoinActivityLog> logList = actAljoinActivityLogService.selectList(logWhere);
    List<Long> userIdList = new ArrayList<Long>();

    String userIds = "";
    for(ActAljoinActivityLog log :logList){
      if(StringUtils.isNotEmpty(log.getReceiveUserIds())){
        List<String> receiveUserIdList = Arrays.asList(log.getReceiveUserIds().split(";"));
        for(String signUser : receiveUserIdList){
          if(StringUtils.isNotEmpty(signUser)){
            userIdList.add(Long.valueOf(signUser));
          }
        }
        userIdList.add(Long.valueOf(log.getOperateUserId()));
      }
    }

    Where<AutUser> userWhere = new Where<AutUser>();
    userWhere.setSqlSelect("id,user_name,full_name");
    userWhere.notIn("id",userIdList);
    List<AutUser> userList = autUserService.selectList(userWhere);

    for(AutUser user : userList){
      userIds += user.getId() +";";
    }
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("userIds",userIds);
    retMsg.setObject(map);
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  public Map<String, Object> getLastSignTaskIdList(String taskId, String taskDefKey, String processInstanceId,
      String assignee) throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    Where<ActAljoinTaskSignInfo> taskSignInfoWhere = new Where<ActAljoinTaskSignInfo>();
    taskSignInfoWhere.setSqlSelect("id");
    taskSignInfoWhere.eq("task_id", taskId);
    ActAljoinTaskSignInfo taskSignInfo = actAljoinTaskSignInfoService.selectOne(taskSignInfoWhere);
    List<String> allSignTaskIdList = new ArrayList<String>();
    List<String> signRuTaskIdList = new ArrayList<String>();
    Map<Long,Object> signTaskInfoMap = getAllSignTaskInfoByTaskOwnerId(taskDefKey,processInstanceId);
    Task tsk = taskService.createTaskQuery().taskId(taskId).singleResult();
    String handler = "";

    // 查询出最原始的加签入口
    Where<ActAljoinActivityLog> activityLogWhere = new Where<ActAljoinActivityLog>();
    activityLogWhere.setSqlSelect(
        "id,operate_user_id,operate_full_name,operate_status,receive_user_ids,current_task_def_key,proc_inst_id,operate_status");
    activityLogWhere.eq("current_task_def_key", taskDefKey);
    activityLogWhere.eq("proc_inst_id", processInstanceId);
    activityLogWhere.like("receive_user_ids", String.valueOf(taskSignInfo.getTaskOwnerId()));
    ActAljoinActivityLog actAljoinActivityLog = actAljoinActivityLogService.selectOne(activityLogWhere);

    if(null != taskSignInfo){
      Where<AutUser> userWhere = new Where<AutUser>();
      userWhere.setSqlSelect("id,full_name");
      userWhere.eq("id",taskSignInfo.getTaskOwnerId());
      AutUser user = autUserService.selectOne(userWhere);
      handler = tsk.getName() +  " [ 原办理人："+user.getFullName()+" ]";
      List<ActAljoinTaskSignInfo> signInfoList = (List<ActAljoinTaskSignInfo>)signTaskInfoMap.get(taskSignInfo.getTaskOwnerId());
      for(ActAljoinTaskSignInfo signInfo : signInfoList){
        allSignTaskIdList.add(signInfo.getSignTaskId());
      }

      if (null != actAljoinActivityLog) {
        List<String> receiveUserIdList = Arrays.asList(actAljoinActivityLog.getReceiveUserIds().split(";"));
        for (String receiveUserId : receiveUserIdList) {
          List<Task> taskList = taskService.createTaskQuery().taskCandidateOrAssigned(receiveUserId).taskDefinitionKey(taskDefKey).processInstanceId(processInstanceId).active().list();
          for(Task task :taskList){
            if (null != task) {
              allSignTaskIdList.add(task.getId());
            }
          }
        }
      }
    }else{
      handler = tsk.getName() +  " [ 原办理人："+actAljoinActivityLog.getOperateFullName()+" ]";
      allSignTaskIdList.add(taskId);
      for(Long taskOwnerId : signTaskInfoMap.keySet()){
        List<ActAljoinTaskSignInfo> signInfoList = (List<ActAljoinTaskSignInfo>)signTaskInfoMap.get(taskOwnerId);
        for(ActAljoinTaskSignInfo signInfo : signInfoList){
          allSignTaskIdList.add(signInfo.getSignTaskId());
        }
      }
    }

    //查询该流程所有运行时任务
    Where<ActRuTask> taskWhere = new Where<ActRuTask>();
    taskWhere.setSqlSelect("id_,proc_inst_id_,task_def_key_,name_");
    taskWhere.in("id_", allSignTaskIdList);
    List<ActRuTask> ruTaskList = actRuTaskService.selectList(taskWhere);
    for(ActRuTask ruTask : ruTaskList){
      signRuTaskIdList.add(ruTask.getId());
    }

    Where<ActAljoinTaskSignInfo> signInfoWhere = new Where<ActAljoinTaskSignInfo>();
    signInfoWhere.setSqlSelect(
        "id,task_key,process_instance_id,task_owner_id,task_owner_name,sign_task_id,is_back_owner,task_sign_user_id,task_signed_user_id");
    signInfoWhere.eq("task_key", taskDefKey);
    signInfoWhere.eq("process_instance_id", processInstanceId);
    ActAljoinTaskSignInfo signInfo = actAljoinTaskSignInfoService.selectOne(signInfoWhere);
    // 同一批所有加签任务ID
    map.put("allSignTaskIdList", allSignTaskIdList);
    // 加签运行中任务ID
    map.put("signRuTaskIdList", signRuTaskIdList);
    //加签方式 0：直接提交下一环节 1:返回原办理人
    map.put("signInfo", signInfo);
    map.put("handler", handler);
    return map;
  }

  public Map<Long,Object> getAllSignTaskInfoByTaskOwnerId(String taskDefKey, String processInstanceId) throws Exception {
    Map<Long,Object> map = new HashMap<Long,Object>();
    //查询所有的加签信息
    Where<ActAljoinTaskSignInfo> signInfoWhere = new Where<ActAljoinTaskSignInfo>();
    signInfoWhere.setSqlSelect(
        "id,task_key,process_instance_id,task_owner_id,task_owner_name,sign_task_id,is_back_owner,task_sign_user_id,task_signed_user_id");
    signInfoWhere.eq("task_key", taskDefKey);
    signInfoWhere.eq("process_instance_id", processInstanceId);
    List<ActAljoinTaskSignInfo> signInfoList = actAljoinTaskSignInfoService.selectList(signInfoWhere);
    List<ActAljoinTaskSignInfo> taskSignInfoList = new ArrayList<ActAljoinTaskSignInfo>();
    //根据根据taskOwnerId分组
    for(ActAljoinTaskSignInfo signInfo : signInfoList){
      if(map.containsKey(signInfo.getTaskOwnerId())){
        taskSignInfoList = ( List<ActAljoinTaskSignInfo>)map.get(signInfo.getTaskOwnerId());
        taskSignInfoList.add(signInfo);
      }else {
        taskSignInfoList.add(signInfo);
      }
      map.put(signInfo.getTaskOwnerId(),taskSignInfoList);
    }

    return map;
  }

  public boolean isSame(String taskDefKey, String processInstanceId) throws Exception {
    boolean isSame = true;
    Map<Long,Long> map = new HashMap<Long,Long>();
    Where<ActAljoinTaskSignInfo> signInfoWhere = new Where<ActAljoinTaskSignInfo>();
    signInfoWhere.setSqlSelect(
        "id,task_key,process_instance_id,task_owner_id,task_owner_name,sign_task_id,is_back_owner,task_sign_user_id,task_signed_user_id");
    signInfoWhere.eq("task_key", taskDefKey);
    signInfoWhere.eq("process_instance_id", processInstanceId);
    List<ActAljoinTaskSignInfo> signInfoList = actAljoinTaskSignInfoService.selectList(signInfoWhere);
    for (ActAljoinTaskSignInfo signInfo : signInfoList) {
      map.put(signInfo.getTaskOwnerId(),signInfo.getTaskOwnerId());
    }
    if(map.size() > 0){
      isSame = false;
    }
    return isSame;
  }

  public Map<String, Object> getSignParam(Task task) throws Exception {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    // 加签标识：0:提交下一环节 1：返回原办理人 2：继续流转
    int isBackOwner = 0;
    // 加签之后是否可以提交 0：否 1：是
    int isSubmit = 1;
    // 查询出最后一级加签任务
    Map<String, Object> signInfoMap = getLastSignTaskIdList(task.getId(), task.getTaskDefinitionKey(),
        task.getProcessInstanceId(), task.getAssignee());
    if (signInfoMap.size() > 0) {
      List<String> signRuTaskIdList = (List<String>)signInfoMap.get("signRuTaskIdList");
      ActAljoinTaskSignInfo signInfo = (ActAljoinTaskSignInfo)signInfoMap.get("signInfo");

      if (signRuTaskIdList.size() == 1) {
        String ruTaskId = signRuTaskIdList.get(0);
        if(null != signInfo){
          if (signInfo.getIsBackOwner() == 1 && ruTaskId.equals(task.getId())) {
            // 返回办理人
            isBackOwner = 1;
          }
          if (task.getAssignee().equals(String.valueOf(signInfo.getTaskOwnerId()))) {
            isBackOwner = 0;
          }
        }

      } else {
        // 继续流转
        isBackOwner = 2;
      }
    }
    paramMap.put("isSubmit", isSubmit);
    paramMap.put("isBackOwner", isBackOwner);
    return paramMap;
  }
}
