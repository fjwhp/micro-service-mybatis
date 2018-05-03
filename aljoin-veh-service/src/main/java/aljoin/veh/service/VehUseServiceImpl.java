package aljoin.veh.service;

import aljoin.veh.dao.entity.VehInfo;
import aljoin.veh.dao.entity.VehUse;
import aljoin.veh.dao.mapper.VehUseMapper;
import aljoin.veh.dao.object.AppVehInfoVO;
import aljoin.veh.dao.object.AppVehUseVO;
import aljoin.veh.dao.object.VehUseVO;
import aljoin.veh.iservice.VehInfoService;
import aljoin.veh.iservice.VehUseService;
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
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.util.StringUtil;

/**
 * 
 * 车船使用申请信息表(服务实现类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-08
 */
@Service
public class VehUseServiceImpl extends ServiceImpl<VehUseMapper, VehUse> implements VehUseService {

  @Resource
  private VehUseMapper mapper;
  @Resource
  private VehInfoService vehInfoService;
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
  private AutUserService autUserService;
  @Resource
  private ActActivitiService activitiService;
  @Resource
  private ActFixedFormServiceImpl actFixedFormService;

  @Override
  public Page<VehUse> list(PageBean pageBean, VehUse obj) throws Exception {
	Where<VehUse> where = new Where<VehUse>();
	where.orderBy("create_time", false);
	Page<VehUse> page = selectPage(new Page<VehUse>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	

  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


  @Override
  public void copyObject(VehUse obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  public VehUseVO getById(Long id) {
    VehUseVO vo = new VehUseVO();
    //使用信息
    Where<VehUse> useWhere = new Where<VehUse>();
    useWhere.eq("id", id);
    VehUse use = selectOne(useWhere);
    //车船信息
    Where<VehInfo> infoWhere = new Where<VehInfo>();
    infoWhere.eq("id", use.getCarId());
    VehInfo info = vehInfoService.selectOne(infoWhere);
    BeanUtils.copyProperties(use, vo);
    vo.setInfo(info);
    return vo;
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
      // 修改原文件状态
      Date date = new Date();
      if (org.apache.commons.lang.StringUtils.isNotEmpty(bizId)) {
        VehUse use = selectById(Long.parseLong(bizId));
        if (null != use) {
          use.setAuditStatus(2);// 审核不通过
          use.setAuditTime(date);
          updateById(use);
          Where<VehInfo> infoWhere = new Where<VehInfo>();
          infoWhere.eq("id", use.getCarId());
          VehInfo info = vehInfoService.selectOne(infoWhere);
          info.setCarStatus(0);
          vehInfoService.updateById(info);
        }
      }
      // 环节办理人待办数量-1
      AutDataStatistics aut = new AutDataStatistics();
      aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
      aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
      aut.setBusinessKey(String.valueOf(userId));
      autDataStatisticsService.minus(aut);
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public RetMsg appDetail(AppVehInfoVO obj) {
    RetMsg retMsg = new RetMsg();
    AppVehUseVO vo = new AppVehUseVO();
    Integer claimStatus = 0;
    if (StringUtils.isNotEmpty(obj.getProcessInstanceId())) {
        Task task = taskService.createTaskQuery().taskId(obj.getProcessInstanceId()).singleResult();
        if(StringUtils.isNotEmpty(task.getAssignee())){
            claimStatus = 1;
        }
    }
    if (null != obj && null != obj.getId()) {
      VehUse use = selectById(obj);
      //车船信息
      Where<VehInfo> infoWhere = new Where<VehInfo>();
      infoWhere.eq("id", use.getCarId());
      VehInfo info = vehInfoService.selectOne(infoWhere);
      BeanUtils.copyProperties(use, vo);
      vo.setInfo(info);
      vo.setClaimStatus(claimStatus);
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
        VehUse vehUse = selectById(Long.parseLong(bizId));
        if (null != vehUse) {
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                    .singleResult();
            if (null == pi) {
                // 如果是结束节点，则记录最后一个办理人
                actFixedFormService.updateLastAsignee(processInstanceId, createUser.getFullName());
                vehUse.setAuditStatus(3);// 审核通过
                vehUse.setAuditTime(date);
                updateById(vehUse);
                Where<VehInfo> vehInfoWhere = new Where<VehInfo>();
                vehInfoWhere.eq("id", vehUse.getCarId());
                VehInfo info = vehInfoService.selectOne(vehInfoWhere);
                info.setCarStatus(1);
                info.setUseUserId(vehUse.getUseUserId());
//                info.setUseUserId(vehUse.getUseUserId());
                vehInfoService.updateById(info);
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
