package aljoin.goo.service;

import aljoin.goo.dao.entity.GooInOut;
import aljoin.goo.dao.entity.GooInfo;
import aljoin.goo.dao.mapper.GooInOutMapper;
import aljoin.goo.dao.object.AppGooInOut;
import aljoin.goo.dao.object.AppGooInOutVO;
import aljoin.goo.dao.object.GooInOutDO;
import aljoin.goo.dao.object.GooInOutVO;
import aljoin.goo.iservice.GooInOutService;
import aljoin.goo.iservice.GooInfoService;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.aut.dao.entity.AutDataStatistics;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.util.StringUtil;

/**
 * 
 * 办公用品出入库信息表(服务实现类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-04
 */
@Service
public class GooInOutServiceImpl extends ServiceImpl<GooInOutMapper, GooInOut> implements GooInOutService {

  @Resource
  private GooInOutMapper mapper;
  @Resource
  private AutDepartmentUserService autDepartmentUserService;
  @Resource
  private AutDepartmentService autDepartmentService;
  @Resource
  private ActAljoinQueryHisService actAljoinQueryHisService;
  @Resource
  private TaskService taskService;
  @Resource
  private RuntimeService runtimeService;
  @Resource
  private ActAljoinQueryService actAljoinQueryService;
  @Resource
  private AutDataStatisticsService autDataStatisticsService;
  @Resource
  private GooInfoService gooInfoService;
  @Resource
  private AutUserService autUserService;
  @Resource
  private ActActivitiService activitiService;
  @Resource
  private ActFixedFormServiceImpl actFixedFormService;

  @Override
  public Page<GooInOut> list(PageBean pageBean, GooInOut obj) throws Exception {
    Where<GooInOut> where = new Where<GooInOut>();
    where.orderBy("create_time", false);
    Page<GooInOut> page = selectPage(new Page<GooInOut>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    return page;
  } 

  @Override
  public void physicsDeleteById(Long id) throws Exception{
    mapper.physicsDeleteById(id);
  }


  @Override
  public void copyObject(GooInOut obj) throws Exception{
    mapper.copyObject(obj);
  }

  @Override
  public GooInOutDO getCurrent(AutUser autUser) {
    List<Long> ids = new ArrayList<Long>();
    String name = "";
    String deptId = "";
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
    String date1 = sdf.format(date);
    Where<AutDepartmentUser> dept = new Where<AutDepartmentUser>();
    dept.eq("user_id", autUser.getId());
    dept.setSqlSelect("dept_id");
    List<AutDepartmentUser> deptmentList = autDepartmentUserService.selectList(dept);
    if(null != deptmentList) {
      for (AutDepartmentUser autDepartmentUser : deptmentList) {
        ids.add(autDepartmentUser.getDeptId());
      }
    }
    Where<AutDepartment> dept1 = new Where<AutDepartment>();
    dept1.in("id", ids);
    dept1.setSqlSelect("id,dept_name");
    List<AutDepartment> deptments = autDepartmentService.selectList(dept1);
    for (AutDepartment autDepartment : deptments) {
      name += autDepartment.getDeptName() + ";";
      deptId += autDepartment.getId() + "";
    }
    GooInOutDO inOutDO = new GooInOutDO();
    inOutDO.setDept(name);
    inOutDO.setDeptId(deptId);
    inOutDO.setSubmitTime(date1);
    inOutDO.setUserName(autUser.getFullName());
    inOutDO.setUserId(autUser.getId().toString());
    return inOutDO;
  }

  @Override
  public List<GooInOut> getById(GooInOutVO obj) {
    Where<GooInOut> where = new Where<GooInOut>();
    String[] arr = obj.getIds().split(";");
    List<Long> ids = new ArrayList<Long>();
    for(int i = 0; i < arr.length; i++) {
      ids.add(Long.valueOf(arr[i]));
    }
    where.in("id",ids);
    List<GooInOut> list = selectList(where);
    return list;
  }

  @Override
  @Transactional
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
      // 修改原状态
      Date date = new Date();
      if (org.apache.commons.lang.StringUtils.isNotEmpty(bizId)) {
        if (bizId.indexOf(";") > -1) {
          List<String> bizIdList = Arrays.asList(bizId.split(";"));
          Where<GooInOut> signWhere = new Where<GooInOut>();
          signWhere.in("id", bizIdList);
          List<GooInOut> signInOutList = selectList(signWhere);

          for (GooInOut signInOut : signInOutList) {
              if (null != signInOut) {
                      if (1 == signInOut.getAuditStatus() && signInOut.getGooInOutProcInstId() != null) {
                          if (signInOut.getGooInOutProcInstId().equals(processInstanceId)) {
                              signInOut.setAuditTime(date);
                              signInOut.setAuditStatus(2);
                          }
                      }
                  }
              updateBatchById(signInOutList);
        }
      }
      // 环节办理人待办数量-1
      AutDataStatistics aut = new AutDataStatistics();
      aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
      aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
      aut.setBusinessKey(String.valueOf(userId));
      autDataStatisticsService.minus(aut);
    }
  }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public RetMsg getInOutList(AppGooInOutVO obj) {
    RetMsg retMsg = new RetMsg();
    List<AppGooInOut> vo = new ArrayList<AppGooInOut>();
    Integer claimStatus = 0;
    if (StringUtils.isNotEmpty(obj.getProcessInstanceId())) {
        Task task = taskService.createTaskQuery().processInstanceId(obj.getProcessInstanceId()).singleResult();
        if(StringUtils.isNotEmpty(task.getAssignee())){
            claimStatus = 1;
        }
    }
    if(obj.getIds() != null) {
      Where<GooInOut> where = new Where<GooInOut>();
      String[] arr = obj.getIds().split(";");
      List<Long> ids = new ArrayList<Long>();
      for(int i = 0; i < arr.length; i++) {
        ids.add(Long.valueOf(arr[i]));
      }
      where.in("id",ids);
      List<GooInOut> list = selectList(where);
      for (GooInOut gooInOut : list) {
        AppGooInOut inOut = new AppGooInOut();
        BeanUtils.copyProperties(gooInOut, inOut);
        inOut.setClaimStatus(claimStatus);
        vo.add(inOut);
      }
    }
    retMsg.setObject(vo);
    retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public RetMsg completeAppTask(Map<String, Object> variables, String taskId, String bizId,
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
        List<GooInOut> gooInOutList = selectList(where);
        
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
                updateBatchById(inOutList1);
                gooInfoService.updateBatchById(inOutList);
                map.put("isEnd", "1");
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
    retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
    retMsg.setMessage("操作成功!");
    return retMsg;
  }
}
