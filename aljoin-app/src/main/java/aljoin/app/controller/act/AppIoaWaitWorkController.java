package aljoin.app.controller.act;

import aljoin.act.dao.entity.*;
import aljoin.act.dao.object.SimpleDeptVO;
import aljoin.act.dao.object.SimpleUserVO;
import aljoin.act.dao.object.TaskVO;
import aljoin.act.iservice.*;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.app.controller.BaseController;
import aljoin.app.task.WebTask;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.aut.dao.object.AutOrganVO;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.ioa.dao.object.AppWaitTaskVO;
import aljoin.ioa.iservice.IoaWaitingWorkService;
import aljoin.object.*;
import aljoin.web.service.act.ActAljoinBpmnWebService;
import aljoin.web.service.act.ActAljoinTaskSignInfoWebService;
import aljoin.web.service.act.ActFixedFormWebService;
import aljoin.web.service.ioa.IoaWaitingWorkWebService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * 待办工作
 * @author：wangj
 * @date：2017年12月19日
 */
@Controller
@RequestMapping(value = "/app/ioa/ioaWaitWork", method = RequestMethod.POST)
@Api(value = "App待办工作流程操作Controller", description = "协同办公 -> 待办工作")
public class AppIoaWaitWorkController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AppIoaWaitWorkController.class);

  @Resource
  private IoaWaitingWorkService ioaWaitingWorkService;
  @Resource
  private TaskService taskService;
  @Resource
  private ActActivitiService actActivitiService;
  @Resource
  private AutUserService autUserService;
  @Resource
  private ActAljoinBpmnRunService actAljoinBpmnRunService;
  @Resource
  private ActAljoinBpmnWebService actAljoinBpmnWebService;
  @Resource
  private ActAljoinFormRunService actAljoinFormRunService;
  @Resource
  private ActAljoinTaskAssigneeService actAljoinTaskAssigneeService;
  @Resource
  private ActFixedFormServiceImpl actFixedFormService;
  @Resource
  private AutDepartmentUserService autDepartmentUserService;
  @Resource
  private ActAljoinQueryService actAljoinQueryService;
  @Resource
  private WebTask webTask;
  @Resource
  private ActFixedFormWebService actFixedFormWebService;
  @Resource
  private IoaWaitingWorkWebService ioaWaitingWorkWebService;
  @Resource
  private ActAljoinTaskSignInfoWebService actAljoinTaskSignInfoWebService;

  /**
   * 根据当前用户获取待办工作流程
   * @return：Page<AppWaitTaskDO>
   * @author：wangj
   * @date：2017年12月19日
   */
  @RequestMapping(value = "/selectWaitTask")
  @ResponseBody
  @ApiOperation(value = "根据当前用户获取待办工作分页列表")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "title", value = "标题", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "formTypeId", value = "表单类型ID", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "founder", value = "创建人", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "urgency", value = "缓急", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "receiveBegTime", value = "开始收件时间", required = false, dataType = "date", paramType = "query"),
    @ApiImplicitParam(name = "receiveEndTime", value = "结束收件时间", required = false, dataType = "date", paramType = "query"),
    @ApiImplicitParam(name = "urgencyIsAsc", value = "缓急排序(0:倒序 1:升序)", required = false, dataType = "date", paramType = "query"),
    @ApiImplicitParam(name = "receiveTimeIsAsc", value = "收件时间排序(0:倒序 1:升序)", required = false, dataType = "date", paramType = "query")
  })
  public RetMsg selectWaitTask(HttpServletRequest request,
                               HttpServletResponse response, PageBean pageBean, AppWaitTaskVO obj) {
    RetMsg retMsg = new RetMsg();
    //Page<AppWaitTaskDO> page = new Page<AppWaitTaskDO>();
    try {
      AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
      String userId = autAppUserLogin.getUserId().toString();
      retMsg = ioaWaitingWorkService.waitList(pageBean, userId, obj);
    } catch (Exception e) {
      logger.error("", e);
      retMsg.setCode(AppConstant.RET_CODE_ERROR);
      retMsg.setMessage("操作异常");
    }
    return retMsg;
  }

  /**
	 * 
	 * 签收
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017年12月19日
	 */
	@RequestMapping(value = "/claimTask")
	@ResponseBody
	@ApiOperation(value = "任务签收")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "ids", value = "任务ID(多个用逗号分隔，单个可以用逗号分隔也可以不用逗号分隔)", required = false, dataType = "string", paramType = "query")
  })
	public RetMsg claimTask(HttpServletRequest request, HttpServletResponse response, String ids) {
		RetMsg retMsg = new RetMsg();
		if (StringUtils.isEmpty(ids)) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("请选择要签收的任务文件");
			return retMsg;
		}
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			String userId = autAppUserLogin.getUserId().toString();
			retMsg = ioaWaitingWorkService.appClaimTask(userId, ids);
		} catch (ActivitiTaskAlreadyClaimedException e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("任务已被他人签收");
			return retMsg;
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;
	}

  /**
   * 输出流程图
   * @return：void
   * @author：wangj
   * @date：2017年12月21日
   */
  @RequestMapping(value = "/showImg")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "taskId", value = "任务ID(待办必传)", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "processInstanceId", value = "流程实例ID(在办必传)", required = false, dataType = "string", paramType = "query")
  })
  @ApiOperation("流程图")
  public void showImg(HttpServletRequest request, HttpServletResponse response) {
    try {
      String taskId = request.getParameter("taskId");
      String processInstanceId = request.getParameter("processInstanceId");
      if(StringUtils.isNotEmpty(taskId) && StringUtils.isEmpty(processInstanceId)){
        processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
      }
      response.addHeader("Pragma", "No-cache");
      response.addHeader("Cache-Control", "no-cache");
      response.addDateHeader("expires", 0);
      InputStream is =
        actActivitiService.getRuImageInputStream(processInstanceId, true);
      OutputStream os = response.getOutputStream();
      BufferedImage image = ImageIO.read(is);
      ImageIO.write(image, "PNG", os);
      os.flush();
      os.close();
    } catch (Exception e) {
      logger.error("", e);
    }
  }


  /**
   * 根据任务ID查询下一个流程节点信息
   * @return：RetMsg
   * @author：wangj
   * @date：2017-12-22
   */
  @SuppressWarnings({"unchecked", "unused"})
  @RequestMapping(value = "/getNextTaskInfo")
  @ResponseBody
  @ApiOperation("根据任务ID查询流程下一个环节信息")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "bpmnId", value = "流程主键ID", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "formId", value = "表单ID(固定流程不用传)", required = false, dataType = "string", paramType = "query")
  })
  public RetMsg getNextTaskInfo(HttpServletRequest request, HttpServletResponse response,String taskId,String formId,String bpmnId) {
    RetMsg retMsg = new RetMsg();
    Map<String, Object> paramMap = new HashMap<String, Object>();
    Map<String, Object> runMap = new HashMap<String, Object>();
    try {
      if(StringUtils.isEmpty(formId) && StringUtils.isNotEmpty(taskId)){//表单ID为空时 则为固定流程
        retMsg = actFixedFormService.getAppNextTaskInfo(taskId);
        return retMsg;
      }
      ActAljoinBpmnRun actAljoinBpmnRun = actAljoinBpmnRunService.selectById(bpmnId);
      if(StringUtils.isNotEmpty(formId) && StringUtils.isNotEmpty(taskId) && null != actAljoinBpmnRun){
        ActAljoinFormRun actAljoinFormRun = actAljoinFormRunService.selectById(Long.valueOf(formId));
        //解析HTMLCODE 编码
        byte[] byteKey = actAljoinFormRun.getHtmlCode().getBytes();
        AutAppUserLogin user = getAppUserLogin(request);
        Map<String,String> formWidgetMap = actAljoinBpmnWebService.bulidFormWidgetValue(actAljoinBpmnRun.getOrgnlId().toString(),taskId,actAljoinFormRun,byteKey,user.getUserId(),user.getUserName());
        for(String key : formWidgetMap.keySet()){
          runMap.put(key,formWidgetMap.get(key));
        }
      }
      runMap.put("isFree", actAljoinBpmnRun.getIsFree());

      Map<String, Object> map = ioaWaitingWorkService.getNextAppTaskInfo(taskId, runMap);

      paramMap.put("taskKeyList", map.get("taskKeyList"));
      paramMap.put("taskNameList", map.get("taskNameList"));
      paramMap.put("isTask", map.get("isTask"));
      paramMap.put("isFinishMergeTask", map.get("isFinishMergeTask"));
      paramMap.put("taskId",taskId);
      paramMap.put("isBackOwner", map.get("isBackOwner"));
      paramMap.put("isSubmit", map.get("isSubmit"));
      // 是否多实例节点
      paramMap.put("isMultiTask", map.get("isMultiTask"));
      // 该任务完成后是否满足进入下一个节点的条件（控制是否让其选择下一节点意见下一节点的班里人）
      paramMap.put("isMultiTaskCondition", map.get("isMultiTaskCondition"));

      // 1.任务类型
      Map<String, String> taskTypeMap = (Map<String, String>) map.get("taskTypeMap");
      // 2.打开类型(放到上层处理)
      // Map<String, String> openTypeMap = map.get("isFinishMergeTask");
      // 3.受理人
      Map<String, String> assigneeIdMap = (Map<String, String>) map.get("assigneeIdMap");
      // 4.当前任务部门列表（仅含部门下的用户，不包括用户和分组）
      Map<String, Map<String, List<String>>> deptListMap =
        (Map<String, Map<String, List<String>>>) map.get("deptListMap");

      // 所有分组ID(含部门，角色，岗位)
      Map<String, Set<String>> allGroupSetMap =
        (Map<String, Set<String>>) map.get("allGroupSetMap");
      // 所有候选用户ID（仅含候选用户，不含办理人）
      Map<String, Set<String>> candidateUserIdSetMap =
        (Map<String, Set<String>>) map.get("candidateUserIdSetMap");

      // 分为四类：1-自由选择节点(逗号分隔-构造成下拉)，2-排他节点(节点列表)，3-自由节点(逗号分隔-构造成下拉)，4-并行节点(节点列表)
      paramMap.put("processType", map.get("processType"));
      int processType = (int) map.get("processType");

      // 非部门用户ID列表
      Map<String, Set<String>> unDeptUserIdSetMap = (Map<String, Set<String>>) map.get("unDeptUserIdSetMap");

      // 对于非自由流程（不可选择节点类的）返回以下数据
      if (processType == 2) {
        List<TaskVO> taskVOList = new ArrayList<TaskVO>();
        List<String> taskKeyList = (List<String>)map.get("taskKeyList");
        List<String> taskNameList = (List<String>) map.get("taskNameList");

        Map<String, Set<String>> userIdsSetMap =
          (Map<String, Set<String>>) map.get("userIdsSetMap");

        //流程实例id
        String currentProcessInstanceId = (String) map.get("currentProcessInstanceId");
        //获取所有流程节点，判断是否配置返回流程发起人
        Set<String> returnCreaterTaskKeySet = new HashSet<String>();
        AutUser processCreater = null;
        Where<ActAljoinTaskAssignee> assigneeWhere = new Where<ActAljoinTaskAssignee>();
        assigneeWhere.in("task_id", taskKeyList);
        assigneeWhere.eq("is_active", 1);
        assigneeWhere.eq("bpmn_id", actAljoinBpmnRun.getOrgnlId());
        assigneeWhere.eq("is_return_creater", 1);
        assigneeWhere.eq("version", actAljoinBpmnRun.getTaskAssigneeVersion());
        assigneeWhere.setSqlSelect("task_id");
        List<ActAljoinTaskAssignee> actAljoinTaskAssigneeList = actAljoinTaskAssigneeService.selectList(assigneeWhere);
        for (ActAljoinTaskAssignee actAljoinTaskAssignee : actAljoinTaskAssigneeList) {
          returnCreaterTaskKeySet.add(actAljoinTaskAssignee.getTaskId());
        }

        //到流程查询表，查询流程发起人(如果下一步节点含有返回发起人的配置才去查询流程的发起人)
        if(returnCreaterTaskKeySet.size() > 0){
          Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
          queryWhere.eq("process_instance_id", currentProcessInstanceId);
          ActAljoinQuery query = actAljoinQueryService.selectOne(queryWhere);
          if(query != null){
            processCreater = new AutUser();
            processCreater.setId(query.getCreateUserId());
            processCreater.setFullName(query.getCreateFullUserName());
            Integer uegency = 1;
            if (query.getUrgentStatus().equals(WebConstant.COMMONLY)) {
              uegency = 1;
            } else if (query.getUrgentStatus().equals(WebConstant.URGENT)) {
              uegency = 2;
            } else if (query.getUrgentStatus().equals(WebConstant.ADD_URENT)) {
              uegency = 3;
            }
            paramMap.put("isUrgent",uegency);
          }
        }

        for (int i = 0; i < taskKeyList.size(); i++) {
          TaskVO tvo = new TaskVO();
          tvo.setTaskKey(taskKeyList.get(i));
          tvo.setTaskName(taskNameList.get(i));

          Set<String> userIdSet = userIdsSetMap.get(taskKeyList.get(i));
          Where<AutUser> autUserWhere = new Where<AutUser>();
          autUserWhere.in("id", userIdSet.size() == 0 ? Arrays.asList(-1) : userIdSet);
          autUserWhere.eq("is_active", 1);
          autUserWhere.setSqlSelect("id,full_name");
          List<AutUser> autUserList = autUserService.selectList(autUserWhere);
          // 查询用户id和名称
          List<SimpleUserVO> suvoList = new ArrayList<SimpleUserVO>();
          SimpleUserVO suvo = null;
          Map<Long, SimpleUserVO> allUserMap = new HashMap<Long, SimpleUserVO>();
          //标志候选用户中是否已经含有流程发起人
          int flag = 0;
          for (AutUser autUser : autUserList) {
            SimpleUserVO vo = new SimpleUserVO();
            vo.setUserId(autUser.getId().toString());
            ;
            vo.setUserName(autUser.getFullName());
            suvoList.add(vo);

            // 检查办理人是否在查询出来的所有处理人的集合中（其实检查其是否合法）
            String assigneeUserId = assigneeIdMap.get(taskKeyList.get(i));
            if (StringUtils.isNotEmpty(assigneeUserId)
              && autUser.getId().toString().equals(assigneeUserId)) {
              suvo = vo;
            }
            // 构造map，下面需要用到
            allUserMap.put(autUser.getId(), vo);
          }

          Set<String> uset = unDeptUserIdSetMap.get(taskKeyList.get(i));

          int hasOtherUser = 0;
          if(flag == 0 && returnCreaterTaskKeySet.contains(taskKeyList.get(i)) && processCreater != null){
            //如果在候选人中没有流程发起人，并且当前节点是有配置返回流程发起人，并且能找到流程发起人
            SimpleUserVO vo = new SimpleUserVO();
            vo.setUserId(processCreater.getId().toString());;
            vo.setUserName(processCreater.getFullName());
            vo.setIsCreater("1");
            suvoList.add(vo);
            uset.add(processCreater.getId().toString());
            hasOtherUser = 1;
          }

          tvo.setUserList(suvoList);
          tvo.setUnDeptUserSet(uset);

          // 构造deptList
          Map<String, List<String>> taskDeptMap = deptListMap.get(taskKeyList.get(i));
          List<SimpleDeptVO> deptList = new ArrayList<SimpleDeptVO>();
          for (Map.Entry<String, List<String>> entry : taskDeptMap.entrySet()) {
            // 部门ID
            String key = entry.getKey();
            // 部门下的用户id列表
            List<String> value = entry.getValue();

            List<SimpleUserVO> deptUserList = new ArrayList<SimpleUserVO>();
            for (String s : value) {
              if (allUserMap.get(Long.parseLong(s)) != null) {
                deptUserList.add(allUserMap.get(Long.parseLong(s)));
              }
            }
            SimpleDeptVO sdv = new SimpleDeptVO();
            sdv.setDeptId(key);
            sdv.setUserList(deptUserList);

            deptList.add(sdv);
          }

          String openType = null;
          if (allUserMap.size() == 0 && hasOtherUser == 0) {
            // 没有用户处理，弹出机构树
            openType = "1";
          } else if (suvo != null && allGroupSetMap.get(taskKeyList.get(i)).size() == 0
            && candidateUserIdSetMap.get(taskKeyList.get(i)).size() == 0 && hasOtherUser == 0) {
            // 有受理人,灭有选择部门角色岗位,也没有选择候选人
            openType = "2";
          } else if (taskDeptMap.size() > 0) {
            // 有选择部门，弹出机构树
            openType = "3";
          } else {
            openType = "4";
          }

          tvo.setOpenType(openType);
          tvo.setTaskType(taskTypeMap.get(taskKeyList.get(i)));
          tvo.setDefaultAssigneeUser(suvo);
          tvo.setDeptList(deptList);

          taskVOList.add(tvo);
        }

        SimpleUserVO defaultAssigneeUser = new SimpleUserVO();
        List<SimpleUserVO> userList = new ArrayList<SimpleUserVO>();
        List<SimpleDeptVO> deptList = new ArrayList<SimpleDeptVO>();
        Set<String> unDeptUserSet = new HashSet<String>();

        for(TaskVO taskVO : taskVOList){
          defaultAssigneeUser = taskVO.getDefaultAssigneeUser();
          userList = taskVO.getUserList();
          deptList = taskVO.getDeptList();
          unDeptUserSet = taskVO.getUnDeptUserSet();
        }
        if(null == defaultAssigneeUser && userList.size() == 0 && deptList.size() == 0 && unDeptUserSet.size() == 0){
          AutOrganVO organVO = new AutOrganVO();
          AutDepartmentUserVO departmentUserVO = new AutDepartmentUserVO();
          organVO = autDepartmentUserService.getOrganList(departmentUserVO);
          paramMap.put("organ",organVO);
        }
        if(deptList.size()>0){//组织机构树
          AutOrganVO organVO = new AutOrganVO();
          AutDepartmentUserVO departmentUserVO = new AutDepartmentUserVO();
          String deptIds = "";
          String userIds = "";
          List<SimpleUserVO> deptUserList = new ArrayList<SimpleUserVO>();
          for(SimpleDeptVO simpleDeptVO : deptList){
            deptIds += simpleDeptVO.getDeptId()+";";
          }
         /* if(deptUserList.size()>0){
            for(SimpleUserVO simpleUserVO : deptUserList){
              userIds += simpleUserVO.getUserId()+";";
            }
          }*/
          if(userList.size()>0){
            for(SimpleUserVO simpleUserVO : userList){
              userIds += simpleUserVO.getUserId()+";";
            }
          }
          if(StringUtils.isNotEmpty(deptIds)){
            departmentUserVO.setDepartmentIds(deptIds);
          }
          if(StringUtils.isNotEmpty(userIds)){
            departmentUserVO.setUserIds(userIds);
          }
          organVO = autDepartmentUserService.getOrganList(departmentUserVO);
          paramMap.put("organ",organVO);
        }else{//用户列表
          if(null != defaultAssigneeUser){
            paramMap.put("defaultAssigneeUser",defaultAssigneeUser);
          }
          List<SimpleUserVO> deptUserList = new ArrayList<SimpleUserVO>();
          if(userList.size()>0 && unDeptUserSet.size()>0){
            Iterator<String> it = unDeptUserSet.iterator();
            while (it.hasNext()){
              SimpleUserVO simpleUserVO = new SimpleUserVO();
              simpleUserVO.setUserId(it.next());
              deptUserList.add(simpleUserVO);
            }
            List<Long> userIdList = new ArrayList<Long>();
            for(SimpleUserVO simpleUserVO : deptUserList){
              userIdList.add(Long.valueOf(simpleUserVO.getUserId()));
            }
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.setSqlSelect("id,user_name,full_name");
            userWhere.in("id",userIdList);
            List<AutUser> userList1 = autUserService.selectList(userWhere);
            for(SimpleUserVO simpleUserVO : deptUserList){
              for(AutUser user : userList1){
                if(String.valueOf(user.getId()).equals(simpleUserVO.getUserId())){
                  simpleUserVO.setUserName(user.getFullName());
                }
              }
            }
            userList.addAll(deptUserList);
          }

          if(userList.size()>0){
            paramMap.put("userList",userList);
          }
        }
      }else{
        List<String> taskKeyList = (List<String>)map.get("taskKeyList");
        Map<String,Object> resultMap = new HashMap<String,Object>();
        Map<String,Object> taskMap = new HashMap<String,Object>();
        List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
        for(String taskKey : taskKeyList){
          Map<String, Object> userMap =  ioaWaitingWorkService.getAppTaskUser(taskId, taskKey);
          resultMap.put(taskKey,userMap);

        }
        for(String key : resultMap.keySet()){
          Map<String,Object> objMap = (Map<String,Object>)resultMap.get(key);
          Map<String,Object> valueMap = new HashMap<String,Object>();
          valueMap.put("nextTaskKey",key);
          for(String objKey : objMap.keySet()){
            valueMap.put(objKey,objMap.get(objKey));
          }
          mapList.add(valueMap);
        }
        paramMap.put("other",mapList);
      }

      //退回 上个节点信息
      Map<String,Object> preTaskInfoMap =  ioaWaitingWorkService.getAppPreTaskInfo(taskId,actAljoinBpmnRun.getId().toString());
      List<CustomerTaskDefinition> preTaskInfoList = (List<CustomerTaskDefinition>)preTaskInfoMap.get("preTaskInfo");
      String retFlag = (String)preTaskInfoMap.get("retFlag");
      paramMap.put("preTaskInfo",preTaskInfoList);
      paramMap.put("retFlag",retFlag);
      retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
      retMsg.setObject(paramMap);
    } catch (Exception e) {
      logger.error("", e);
      retMsg.setCode(AppConstant.RET_CODE_ERROR);
      retMsg.setMessage("操作失败");
    }
    return retMsg;
  }

  /**
   * 回退
   * @return：RetMsg
   * @author：pengsp
   * @date：2017-11-10
   */
  @RequestMapping(value = "/jumpTask")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "taskId", value = "任务ID(当前任务)", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "targetTaskKey", value = "目标任务key", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "targetUserId", value = "目标任务办理人ID", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "comment", value = "意见", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "isTask", value = "是否自由流程(用来标记自由流程，当isTask=false时是自由流程)", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "formId", value = "表单ID(固定流程不用传)", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "bpmnId", value = "流程主键ID", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "nextTaskKey", value = "下个任务节点Key", required = true, dataType = "string", paramType = "query")
  })
  @ResponseBody
  @ApiOperation("回退")
  public RetMsg jumpTask(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataRun entity) {
    RetMsg retMsg = new RetMsg();
    try {
      String taskId = request.getParameter("taskId");
      String targetTaskKey = request.getParameter("targetTaskKey");
      String targetUserId = request.getParameter("targetUserId");
      String comment = request.getParameter("comment");

      String isTask = request.getParameter("isTask");
      String nextNode = request.getParameter("nextTaskKey");
      String formId = request.getParameter("formId");
      String bpmnId = request.getParameter("bpmnId");
      AutAppUserLogin user = getAppUserLogin(request);
      Map<String, String> paramMap = new HashMap<String, String>();
      ActAljoinFormRun actAljoinFormRun = actAljoinFormRunService.selectById(Long.valueOf(formId));
      //解析HTMLCODE 编码
      byte[] byteKey = actAljoinFormRun.getHtmlCode().getBytes();
      Map<String,String> formWidgetMap = actAljoinBpmnWebService.bulidFormWidgetValue(bpmnId,taskId,actAljoinFormRun,byteKey,user.getUserId(),user.getUserName());
      for(String key : formWidgetMap.keySet()){
        paramMap.put(key,formWidgetMap.get(key));
      }
      entity.setOperateUserId(getAppUserLogin(request).getUserId());
      retMsg = ioaWaitingWorkWebService.jumpAppTask(taskId, targetTaskKey, targetUserId,
        comment,user.getUserId() , paramMap, entity, isTask, nextNode);
      if (null != retMsg.getObject()) {
        String processInstanceId = (String) retMsg.getObject();
        Where<AutUser> autUserWhere = new Where<AutUser>();
        autUserWhere.setSqlSelect("id,user_name,full_name");
        autUserWhere.eq("id",user.getUserId());
        AutUser autUser = autUserService.selectOne(autUserWhere);
        webTask.sendOnlineMsg(processInstanceId, targetUserId, autUser);
      }
    } catch (Exception e) {
      logger.error("", e);
      retMsg.setCode(AppConstant.RET_CODE_ERROR);
      retMsg.setMessage("操作失败");
    }
    return retMsg;
  }


  /**
   *
   * 撤回
   *
   * @return：RetMsg
   *
   * @author：wangj
   *
   * @date：2017-11-07
   */
  @RequestMapping(value = "/jump2Task2")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "taskId", value = "当前任务ID", required = true, dataType = "string", paramType = "query") })
  @ResponseBody
  @ApiOperation("撤回")
  public RetMsg jump2Task2(HttpServletRequest request,String processInstanceId, String taskId) {
    RetMsg retMsg = new RetMsg();
    try {
      retMsg = actFixedFormWebService.appRevoke(processInstanceId, taskId, getAppUserLogin(request).getUserId().toString());
//      retMsg = ioaWaitingWorkWebService.appJump2Task2(processInstanceId, getAppUserLogin(request).getUserId());
    } catch (Exception e) {
      logger.error("", e);
      retMsg.setCode(AppConstant.RET_CODE_ERROR);
      retMsg.setMessage("操作失败");
    }
    return retMsg;
  }

  /** 根据任务ID和流程实例ID获得流转日志
   * @return：RetMsg
   * @author：zhongjy
   * @date：2017年11月28日 下午8:18:14
    */
  @RequestMapping(value = "/getLog")
  @ResponseBody
  @ApiImplicitParams({
    @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "taskId", value = "任务ID(待办传该参数)", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "processInstanceId", value = "流程实例Id(在办传该参数)", required = false, dataType = "string", paramType = "query")
  })
  @ApiOperation("流转日志")
 public RetMsg getLog(HttpServletRequest request,String taskId,String processInstanceId){
    RetMsg retMsg = new RetMsg();
    try {
      List<FixedFormProcessLog> fixedFormProcessLogList = actFixedFormService.getActivityLog(taskId,processInstanceId);
      retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
      retMsg.setObject(fixedFormProcessLogList);
      retMsg.setMessage("操作成功");
    }catch (Exception e){
      logger.error("",e);
      retMsg.setCode(AppConstant.RET_CODE_ERROR);
      retMsg.setMessage("操作异常");
    }
    return  retMsg;
 }

  /**
   *
   * App根据任务ID或流程实例ID查询流程是否被签收
   *
   * @return：RetMsg
   *
   * @author：wangj
   *
   * @date：2017-11-06
   */
  @RequestMapping(value = "/isClaim")
  @ResponseBody
  @ApiOperation("app根据任务ID或流程实例ID查询流程是否被签收")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "taskId", value = "任务ID(待办传该参数)", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "processInstanceId", value = "流程实例Id(在办传该参数)", required = false, dataType = "string", paramType = "query")
  })
  public RetMsg isClaim(HttpServletRequest request, HttpServletResponse response, String taskId,
                        String processInstanceId) {
    RetMsg retMsg = new RetMsg();
    try {
      AutAppUserLogin user = getAppUserLogin(request);
//      retMsg = actFixedFormService.appIsClaim(taskId, processInstanceId, user.getUserId() + "");
      String message = actFixedFormService.appIsClaim(taskId, processInstanceId, user.getUserId() + "");
      retMsg.setMessage(message);
      retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
    } catch (Exception e) {
      logger.error("", e);
      retMsg.setCode(AppConstant.RET_CODE_ERROR);
      retMsg.setMessage("操作失败");
    }
    return retMsg;
  }

  /**
   *
   * 流程日志
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017年9月11日 下午5:34:17
   */
  @RequestMapping(value="/getFixedLog")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "taskId", value = "任务ID(待办传该参数)", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "processInstanceId", value = "流程实例Id(在办传该参数)", required = false, dataType = "string", paramType = "query")
  })
  @ResponseBody
  @ApiOperation("固定流程流程日志")
  public RetMsg getFixedLog(String taskId, String processName,String processInstanceId){
    RetMsg retMsg = new RetMsg();
    List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
    try {
      list = actFixedFormWebService.getFixedLog(taskId,processInstanceId);
    }catch (Exception e){
      logger.error("", e);
      retMsg.setCode(AppConstant.RET_CODE_ERROR);
      retMsg.setMessage("操作失败");
    }
    retMsg.setObject(list);
    retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @ResponseBody
  @ApiOperation("加签组织机构树")
  @RequestMapping(value="/getAddSignOrgn")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "taskId", value = "任务ID(待办传该参数)", required = false, dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "bpmnId", value = "流程部署表ID", required = false, dataType = "long", paramType = "query")
  })
  public RetMsg getAddSignOrgn(HttpServletRequest request,String taskId,String bpmnId){
    RetMsg retMsg = new RetMsg();
    AutOrganVO autOrganVO = null;
    String isTask = "";
    String nextTaskKey = "";
    String nextTaskName = "";
    try {
      AutAppUserLogin user =  getAppUserLogin(request);
      retMsg = actAljoinTaskSignInfoWebService.getSignedUserIds(user.getUserId(),taskId);
      Map<String,Object> retMap = (Map<String,Object>)retMsg.getObject();
      String userIds = (String)retMap.get("userIds");
      isTask = (String)retMap.get("isTask");
      nextTaskKey = (String)retMap.get("nextTaskKey");
      nextTaskName = (String)retMap.get("nextTaskName");
      AutDepartmentUserVO deptUser = new AutDepartmentUserVO();
      deptUser.setUserIds(userIds);
      autOrganVO = autDepartmentUserService.getOrganList(deptUser);
    }catch (Exception e){
      logger.error("", e);
      retMsg.setCode(AppConstant.RET_CODE_ERROR);
      retMsg.setMessage("操作失败");
    }
    Map<String,Object> resultMap = new HashMap<String,Object>();
    resultMap.put("orgn",autOrganVO);
    resultMap.put("isTask",isTask);
    resultMap.put("nextTaskKey",nextTaskKey);
    resultMap.put("nextTaskName",nextTaskName);
    retMsg.setObject(resultMap);
    retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

}
