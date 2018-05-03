package aljoin.web.service.act;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActRuIdentitylink;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.iservice.ActRuIdentitylinkService;
import aljoin.act.iservice.ActRuTaskService;
import aljoin.aut.dao.entity.AutMsgOnline;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.dao.config.Where;
import aljoin.object.WebConstant;
import aljoin.sma.iservice.SysMsgModuleInfoService;
import aljoin.util.StringUtil;

/**
 * 
 * ClassName: ActAljoinFixedFormSendMsg
 *
 * @描述 所有流程涉及环节跳转-下一环节办理人收到在线消息调用的方法（异步）
 *
 * @作者 sunlinan
 *
 * @时间 2017年12月14日
 */
@Service
public class ActAljoinSendMsgWebService {

  @Resource
  private SysMsgModuleInfoService sysMsgModuleInfoService;
  @Resource
  private AutMsgOnlineService autMsgOnlineService;
  @Resource
  private ActAljoinQueryService actAljoinQueryService;
  @Resource
  private HistoryService historyService;
  @Resource
  private ActRuIdentitylinkService actRuIdentitylinkService;
  @Resource
  private TaskService taskService;
  @Resource
  private ActActivitiService actActivitiService;
  @Resource
  private ActRuTaskService actRuTaskService;

  /**
   * 
   * @描述: 发送在线消息给下一任务节点候选人（固定模块）确定下一环节只有一个节点不可能出现并行的流程使用(回退也调用这个)
   * 
   *      @return： void
   * 
   *      @author： sunlinan
   *
   * @date 2017年12月18日
   */
  public void sendOnlineMsg(String processInstanceId, String handle, AutUser user)
      throws Exception {
    // map内容与消息模板保持一致
    // 调用请参考：{handle}:当前办理人; {create}:创建人; {process}:流程名称;
    // {priorities}:缓急程度; {title}:标题;

    Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
    queryWhere.eq("process_instance_id", processInstanceId);
    queryWhere.setSqlSelect("id,process_name,create_full_user_name,urgent_status,process_title");
    ActAljoinQuery actAljoinQuery = actAljoinQueryService.selectOne(queryWhere);
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("handle", handle);
    map.put("create", actAljoinQuery.getCreateFullUserName());
    map.put("process", actAljoinQuery.getProcessName());
    map.put("priorities", actAljoinQuery.getUrgentStatus());// 缓急程度
    map.put("title", actAljoinQuery.getProcessTitle());// 标题
    List<String> templateList =
        sysMsgModuleInfoService.contextTemplate(map, WebConstant.TEMPLATE_TYPE_MSG,
            WebConstant.TEMPLATE_WORK_CODE, WebConstant.TEMPLATE_BEHAVIOR_DB);
    String handleUser = handle;

    // 构造出待办详情需要的参数
    HistoricProcessInstance hiInstance = historyService.createHistoricProcessInstanceQuery()
        .processInstanceId(processInstanceId).singleResult();
    String businessKey = hiInstance.getBusinessKey();
    String bpmnId = "";
    if (!StringUtils.isEmpty(businessKey)) {
      String[] key = businessKey.split(",");
      if (key.length >= 1) {
        bpmnId = key[0];
      }
    }
    // 流程待办环节
    HistoricActivityInstance hiActivityInstance =
        historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
            .unfinished().singleResult();
    HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
        .processInstanceId(processInstanceId).unfinished().singleResult();

    if (hiActivityInstance.getTaskId().equals(historicTaskInstance.getId())) {
      String taskId = historicTaskInstance.getTaskDefinitionKey();
      String acticityId = historicTaskInstance.getId();
      AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
      if (null != handleUser && StringUtils.isNotEmpty(handleUser)) {
        if (templateList != null && !templateList.isEmpty()) {
          requestVO.setMsgContent(templateList.get(0));
        } else {
          throw new Exception("消息模板未设置");
        }
        // 封装推送信息请求信息
        requestVO.setFromUserId(user.getId());
        requestVO.setFromUserFullName(user.getFullName());
        requestVO.setFromUserName(user.getFullName());
        String content = "../../act/modeler/openForm.html?id=" + bpmnId + "&activityId="
            + acticityId + "&taskId=" + taskId + "&businessKey=" + businessKey + "&signInTime=" + ""
            + "&wait=1";
        requestVO.setGoUrl(content);// 本来应该从他们那边传，但是现在查询放到里面，就不用传了，看看自己能不能构造一个
        // content :
        // '../../act/modeler/openForm.html?id='+bpmnId+'&activityId='+id+'&taskId='+taskDefKey+'&businessKey='+businessKey+'&signInTime='+signInTime+'&isWait=1',
        requestVO.setMsgType(WebConstant.ONLINE_MSG_TOGETHERWORK);
        List<String> receiveUserIdList = Arrays.asList(handleUser.split(";"));
        requestVO.setToUserId(receiveUserIdList);
        // 调用推送在线消息接口
        autMsgOnlineService.pushMessageToUserList(requestVO);
      }
    }
  }

  /**
   * 
   * @描述: 下一任务节点是有可能是多个任务的发送消息-不跟固定模块写在一起
   * 
   *      @return： void
   * 
   *      @author： sunlinan
   *
   * @date 2017年12月18日
   */
  public void sendOnlineMsg4MulTask(Map<String, String> param, AutUser user) throws Exception {
    String processInstanceId = param.get("processInstanceId");
    Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
    queryWhere.eq("process_instance_id", processInstanceId);
    queryWhere.setSqlSelect("id,process_name,create_full_user_name,urgent_status,process_title");
    ActAljoinQuery actAljoinQuery = actAljoinQueryService.selectOne(queryWhere);
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("create", actAljoinQuery.getCreateFullUserName());
    map.put("process", actAljoinQuery.getProcessName());
    map.put("priorities", actAljoinQuery.getUrgentStatus());// 缓急程度
    map.put("title", actAljoinQuery.getProcessTitle());// 标题

    String taskAuth = param.get("taskAuth");
    Map<String, List<String>> taskKeyUserMap = new HashMap<String, List<String>>();
    Set<String> allCandidateUserSet = new HashSet<String>();
    if (StringUtils.isNotEmpty(taskAuth)) {
      String[] taskAuthArr = taskAuth.split(";");
      for (int i = 0; i < taskAuthArr.length; i++) {
        String taskKey = taskAuthArr[i].split(",")[0];
        String tuserIds = taskAuthArr[i].split(",")[1];
        String[] userIdsArr = tuserIds.split("#");
        taskKeyUserMap.put(taskKey, Arrays.asList(userIdsArr));
        allCandidateUserSet.addAll(Arrays.asList(userIdsArr));
      }
    }
    String businessKey = param.get("businessKey");
    String bpmnId = "";
    if (!StringUtils.isEmpty(businessKey)) {
      String[] key = businessKey.split(",");
      if (key.length >= 1) {
        bpmnId = key[0];
      }
    }
    
    List<HistoricTaskInstance> historicTaskInstances = historyService
        .createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).unfinished().list();
    // 流程待办环节
    List<HistoricActivityInstance> hiActivityInstances =
        historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
            .unfinished().list();
    Set<String> taskIds = new HashSet<String>();
    Set<String> candidateTaskIds = new HashSet<String>();
    for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
      taskIds.add(historicTaskInstance.getId());
    }
    //延迟5秒执行任务创建的监听事件方法(等等数据处理完毕)
    Thread.sleep(6000);
    // 取出下级任务候选人
    Where<ActRuIdentitylink> identitylinkWhere = new Where<ActRuIdentitylink>();
    if(taskIds.size()>0){
      identitylinkWhere.in("TASK_ID_", taskIds);
      identitylinkWhere.eq("TYPE_", "candidate");
      identitylinkWhere.in("USER_ID_",allCandidateUserSet);
      List<ActRuIdentitylink> candidateUser = actRuIdentitylinkService.selectList(identitylinkWhere);
  //    Map<String,String> candidateMap = new HashMap<String,String>();
      Map<String,Set<String>> candidateMap = new HashMap<String,Set<String>>();
      for (ActRuIdentitylink actRuIdentitylink : candidateUser) {
        if(actRuIdentitylink.getTaskId()!=null){
          candidateTaskIds.add(actRuIdentitylink.getTaskId());
          if(candidateMap.get(actRuIdentitylink.getTaskId()) != null){
            Set<String> taskCandidateSet = candidateMap.get(actRuIdentitylink.getTaskId());
            taskCandidateSet.add(actRuIdentitylink.getUserId());
            candidateMap.put(actRuIdentitylink.getTaskId(), taskCandidateSet);
            
    //        String taskCandidateUser = candidateMap.get(actRuIdentitylink.getTaskId()) + ";" + actRuIdentitylink.getUserId();
    //        candidateMap.put(actRuIdentitylink.getTaskId(),taskCandidateUser);
          }else{
            Set<String> taskCandidateSet = new HashSet<String>();
            taskCandidateSet.add(actRuIdentitylink.getUserId());
            candidateMap.put(actRuIdentitylink.getTaskId(), taskCandidateSet);
    //        candidateMap.put(actRuIdentitylink.getTaskId(),actRuIdentitylink.getUserId());
          }
        }
      }
      List<AutMsgOnline> autMsgList = new ArrayList<AutMsgOnline>();
      // 消息的business_key是发送过提醒消息的taskId
      if(candidateTaskIds.size()>0){
        Where<AutMsgOnline> msgWhere = new Where<AutMsgOnline>();
        msgWhere.in("business_key", candidateTaskIds);
        msgWhere.setSqlSelect("business_key,id,receive_user_id");
        autMsgList = autMsgOnlineService.selectList(msgWhere);
      }
  //    List<String> existTaskId = new ArrayList<String>();
      
      for (String key : candidateMap.keySet()) {
        Set<String> taskCandidateSet = candidateMap.get(key);
        // 如果这个task已经通知过，就不再发送通知的消息
        if(autMsgList.size()>0){
          for(AutMsgOnline msg : autMsgList){
            if(msg.getBusinessKey().equals(key)){
  //            String taskUserStr = candidateMap.get(key);
              if(taskCandidateSet != null && taskCandidateSet.contains(msg.getReceiveUserId().toString())){
                taskCandidateSet.remove(msg.getReceiveUserId().toString());
              }
            }
          }
        }
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
          if (historicTaskInstance.getId().equals(key)) {
            if(taskCandidateSet.size()>0){
              List<String> taskCandidateList = new ArrayList<String>(taskCandidateSet);
  //              String taskUserStr = candidateMap.get(key);
                String taskUserStr = StringUtil.list2str(taskCandidateList, ";");// 下一环节候选人id
                map.put("handle", taskUserStr);
                // 调用消息模板
                List<String> templateList =
                    sysMsgModuleInfoService.contextTemplate(map, WebConstant.TEMPLATE_TYPE_MSG,
                        WebConstant.TEMPLATE_WORK_CODE, WebConstant.TEMPLATE_BEHAVIOR_DB);
                if (templateList.isEmpty()) {
                  throw new Exception("消息模板未设置");
                }
  
                for (HistoricActivityInstance activityInstance : hiActivityInstances) {
                  if (activityInstance.getTaskId().equals(historicTaskInstance.getId())) {
                    if (StringUtils.isNotEmpty(taskUserStr)) {
                      String acticityId = historicTaskInstance.getId();
                      String taskId = historicTaskInstance.getTaskDefinitionKey();
                      String url = "../../act/modeler/openForm.html?id=" + bpmnId + "&activityId="
                          + acticityId + "&taskId=" + taskId + "&businessKey=" + businessKey
                          + "&signInTime=" + "" + "&wait=1";
                      AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
                      requestVO.setFromUserId(user.getId());
                      requestVO.setFromUserFullName(user.getFullName());
                      requestVO.setFromUserName(user.getUserName());
                      requestVO.setGoUrl(url);// 本来应该从他们那边传，但是现在查询放到里面，就不用传了，看看自己能不能构造一个
                      requestVO.setMsgType(WebConstant.ONLINE_MSG_TOGETHERWORK);
  //                    List<String> taskUser = Arrays.asList(taskUserStr.split(";"));
                      requestVO.setToUserId(taskCandidateList);
                      requestVO.setMsgContent(templateList.get(0));
                      // 调用推送在线消息接口
                      requestVO.setBusinessKey(acticityId);
                      autMsgOnlineService.pushMessageToUserList(requestVO);
                    }
                  }
                }
            }
          }
        }
      }
    }
  }

  /**
   * 
   * @描述: 收文阅卷使用，因为收文阅卷详情页面跟待办不一样，单独写一个给他
   * 
   *      @return： void
   * 
   *      @author： sunlinan
   *
   * @date 2017年12月27日
   */
  public void sendOnlineMsg4IoaRead(Map<String, Object> param, AutUser user) throws Exception {
    // map内容与消息模板保持一致
    // 调用请参考：{handle}:当前办理人; {create}:创建人; {process}:流程名称;
    // {priorities}:缓急程度; {title}:标题;
    String processInstanceId = (String) param.get("processInstanceId");
    String handle = (String) param.get("handle");
    String bizId = (String) param.get("bizId");

    // String linkName = param.get("linkName");

    Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
    queryWhere.eq("process_instance_id", processInstanceId);
    queryWhere.setSqlSelect("id,process_name,create_full_user_name,urgent_status,process_title");
    ActAljoinQuery actAljoinQuery = actAljoinQueryService.selectOne(queryWhere);
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("handle", handle);
    map.put("create", actAljoinQuery.getCreateFullUserName());
    map.put("process", actAljoinQuery.getProcessName());
    map.put("priorities", actAljoinQuery.getUrgentStatus());// 缓急程度
    map.put("title", actAljoinQuery.getProcessTitle());// 标题
    List<String> templateList =
        sysMsgModuleInfoService.contextTemplate(map, WebConstant.TEMPLATE_TYPE_MSG,
            WebConstant.TEMPLATE_WORK_CODE, WebConstant.TEMPLATE_BEHAVIOR_DB);
    String handleUser = handle;

    @SuppressWarnings("unchecked")
    HashMap<String, Object> resultMap = (HashMap<String, Object>) param.get("taskList");
    if (templateList == null || templateList.isEmpty()) {
      throw new Exception("消息模板未设置");
    }
    AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
    requestVO.setMsgContent(templateList.get(0));
    requestVO.setFromUserId(user.getId());
    requestVO.setFromUserFullName(user.getFullName());
    requestVO.setFromUserName(user.getFullName());
    requestVO.setMsgType(WebConstant.ONLINE_MSG_TOGETHERWORK);
    if (resultMap == null || resultMap.size() == 1) {// 单人办理环节
      // 构造出收文阅卷详情需要的参数（bizId + linkName）
      String taskName = (String) param.get("linkName");
      if(resultMap != null){
        for (String key : resultMap.keySet()) {
          String linkName = key;
          taskName = linkName.substring(linkName.indexOf("|") + 1, linkName.length());
        }
      }
      if (null != handleUser && StringUtils.isNotEmpty(handleUser)) {
        // 封装推送信息请求信息
        String linkName = URLEncoder.encode(taskName, "utf-8");
        String content = "ioa/ioaReceiveWork/ioaReceiveWorkPageDetail.html?bizId=" + bizId
            + "&linkName=" + linkName + "&processInstanceId=" + processInstanceId;
        requestVO.setGoUrl(content);// 本来应该从他们那边传，但是现在查询放到里面，就不用传了，看看自己能不能构造一个
        List<String> receiveUserIdList = Arrays.asList(handleUser.split(";"));
        requestVO.setToUserId(receiveUserIdList);
        // 调用推送在线消息接口
        autMsgOnlineService.pushMessageToUserList(requestVO);
      }
    } else {// 会签环节
      if (null != handleUser && StringUtils.isNotEmpty(handleUser)) {
        String linkName = (String) param.get("linkName");
        String content = "ioa/ioaReceiveWork/ioaReceiveWorkPageDetail.html?bizId=" + bizId
            + "&linkName=" + linkName + "&processInstanceId=" + processInstanceId;
        requestVO.setGoUrl(content);
        List<String> receiveUserIdList = Arrays.asList(handleUser.split(";"));
        requestVO.setToUserId(receiveUserIdList);
        autMsgOnlineService.pushMessageToUserList(requestVO);
      }
    }
  }

  /**
   *
   * @描述: 特送在线消息提醒（被特送的人收到提醒）
   *
   *      @return： void
   *
   *      @author： wangj
   *
   * @date 2017年12月18日
   */
  public void sendMutilOnlineMsg4Delivery(String processInstanceIds, Map<String,String> handleMap, AutUser user)
      throws Exception {
    // map内容与消息模板保持一致
    // 调用请参考：{handle}:当前办理人; {create}:创建人; {process}:流程名称;
    // {priorities}:缓急程度; {title}:标题;

    List<String> processInstanceIdList = Arrays.asList(processInstanceIds.split(";"));
    Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
    queryWhere.in("process_instance_id", processInstanceIdList);
    queryWhere.setSqlSelect("id,process_name,create_full_user_name,urgent_status,process_title,process_instance_id");
    List<ActAljoinQuery> actAljoinQueryList = actAljoinQueryService.selectList(queryWhere);
    for(ActAljoinQuery actAljoinQuery : actAljoinQueryList){
      HashMap<String, String> map = new HashMap<String, String>();

      String handle = handleMap.get(actAljoinQuery.getProcessInstanceId());
      map.put("handle", handle);
      map.put("create", actAljoinQuery.getCreateFullUserName());
      map.put("process", actAljoinQuery.getProcessName());
      // 缓急程度
      map.put("priorities", actAljoinQuery.getUrgentStatus());
      // 标题
      map.put("title", actAljoinQuery.getProcessTitle());
      List<String> templateList =
          sysMsgModuleInfoService.contextTemplate(map, WebConstant.TEMPLATE_TYPE_MSG,
              WebConstant.TEMPLATE_WORK_CODE, WebConstant.TEMPLATE_BEHAVIOR_TS);
      String handleUser = handle;
      // 构造出待办详情需要的参数
      HistoricProcessInstance hiInstance = historyService.createHistoricProcessInstanceQuery()
          .processInstanceId(actAljoinQuery.getProcessInstanceId()).singleResult();
      String businessKey = hiInstance.getBusinessKey();
      String bpmnId = "";
      if (!StringUtils.isEmpty(businessKey)) {
        String[] key = businessKey.split(",");
        if (key.length >= 1) {
          bpmnId = key[0];
        }
      }
      List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(actAljoinQuery.getProcessInstanceId()).list();
      HistoricTaskInstance task = historicTaskInstanceList.get(0);
      AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
      String taskId = task.getTaskDefinitionKey();
      String acticityId = task.getId();
      if (null != handleUser && StringUtils.isNotEmpty(handleUser)) {
        if (templateList != null && !templateList.isEmpty()) {
          requestVO.setMsgContent(templateList.get(0));
        } else {
          throw new Exception("消息模板未设置");
        }
        // 封装推送信息请求信息
        requestVO.setFromUserId(user.getId());
        requestVO.setFromUserFullName(user.getFullName());
        requestVO.setFromUserName(user.getFullName());
        String content = "../../act/modeler/openForm.html?id=" + bpmnId + "&activityId="
            + acticityId + "&taskId=" + taskId + "&businessKey=" + businessKey + "&signInTime=" + ""
            + "&wait=1";
        requestVO.setGoUrl(content);
        requestVO.setMsgType(WebConstant.ONLINE_MSG_TOGETHERWORK);
        List<String> receiveUserIdList = Arrays.asList(handleUser.split(";"));
        requestVO.setToUserId(receiveUserIdList);
        // 调用推送在线消息接口
        autMsgOnlineService.pushMessageToUserList(requestVO);
      }
    }
  }
}
