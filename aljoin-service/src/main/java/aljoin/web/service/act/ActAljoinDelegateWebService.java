package aljoin.web.service.act;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.delegate.event.impl.ActivitiEntityWithVariablesEventImpl;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aljoin.act.dao.entity.ActAljoinDelegate;
import aljoin.act.dao.object.ActAljoinDelegateVO;
import aljoin.act.iservice.ActAljoinDelegateService;
import aljoin.aut.dao.entity.AutUser;
import aljoin.dao.config.Where;
import aljoin.tim.dao.entity.TimSchedule;
import aljoin.tim.iservice.JobScheduleService;
import aljoin.tim.iservice.TimScheduleService;
import aljoin.util.StringUtil;

/**
 * 
 * 任务委托服务
 *
 * @author：zhongjy
 * 
 * @date：2017年11月14日 下午4:22:22
 */
@Service
public class ActAljoinDelegateWebService {

  @Resource
  private ActAljoinDelegateService actAljoinDelegateService;
  @Resource
  private TaskService taskService;
  @Resource
  private TimScheduleService timScheduleService;
  @Resource
  private JobScheduleService jobScheduleService;

  /**
   * 
   * 添加委托业务
   *
   * @return：void
   *
   * @author：pengsp
   *
   * @date：2017-10-24
   */
  @Transactional
  public void addDelegateBiz(ActAljoinDelegate obj) throws Exception {
    // 生成所有委托数据
    List<Task> taskList = taskService.createTaskQuery()
        .taskCandidateOrAssigned(obj.getOwnerUserId().toString()).list();
    int falg = actAljoinDelegateService.addDelegateBiz(obj, taskList);
    // 生成并启动任务委托定时器
    if (falg == 1) {
      // 结束定时器
      TimSchedule timSchedule = new TimSchedule();
      timSchedule.setCronExpression(StringUtil.date2cron(obj.getEndTime()));
      timSchedule.setJobDesc("任务委托-结束定时器");
      timSchedule.setJobGroup("act");
      timSchedule.setJobName("任务委托-结束定时器");
      timSchedule.setJobStatus("NORMAL");
      timSchedule.setExeClassName("DeleteDelegateTaskJob");
      timSchedule.setIsActive(1);
      timSchedule.setBizType("act_aljoin_delegate");
      timSchedule.setBizId(obj.getId());
      timSchedule.setIsAuto(1);
      timSchedule.setCreateUserId(obj.getCreateUserId());
      timSchedule.setCreateUserName(obj.getCreateUserName());
      timScheduleService.insert(timSchedule);

      List<TimSchedule> jobList = new ArrayList<TimSchedule>();
      jobList.add(timSchedule);
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("actAljoinDelegate", obj);
      map.put("timSchedule", timSchedule);
      jobScheduleService.doJob(jobList, map);
      // 执行完结束定时器后，销毁定时器(在定时器类中执行)

    } else if (falg == 2) {
      // 开始和结束定时器
      // 开始定时器
      TimSchedule timScheduleBeg = new TimSchedule();
      timScheduleBeg.setCronExpression(StringUtil.date2cron(obj.getBegTime()));
      timScheduleBeg.setJobDesc("任务委托-开始定时器");
      timScheduleBeg.setJobGroup("act");
      timScheduleBeg.setJobName("任务委托-开始定时器");
      timScheduleBeg.setJobStatus("NORMAL");
      timScheduleBeg.setCreateUserId(obj.getCreateUserId());
      timScheduleBeg.setCreateUserName(obj.getCreateUserName());
      timScheduleBeg.setExeClassName("CreateDelegateTaskJob");
      timScheduleBeg.setIsActive(1);
      timScheduleBeg.setBizType("act_aljoin_delegate");
      timScheduleBeg.setBizId(obj.getId());
      timScheduleBeg.setIsAuto(1);
      // 结束定时器
      TimSchedule timScheduleEnd = new TimSchedule();
      timScheduleEnd.setCronExpression(StringUtil.date2cron(obj.getEndTime()));
      timScheduleEnd.setJobDesc("任务委托-结束定时器");
      timScheduleEnd.setJobGroup("act");
      timScheduleEnd.setJobName("任务委托-结束定时器");
      timScheduleEnd.setJobStatus("NORMAL");
      timScheduleEnd.setExeClassName("DeleteDelegateTaskJob");
      timScheduleEnd.setIsActive(1);
      timScheduleEnd.setBizType("act_aljoin_delegate");
      timScheduleEnd.setBizId(obj.getId());
      timScheduleEnd.setIsAuto(1);
      timScheduleEnd.setCreateUserId(obj.getCreateUserId());
      timScheduleEnd.setCreateUserName(obj.getCreateUserName());
      List<TimSchedule> timScheduleList = new ArrayList<TimSchedule>();
      timScheduleList.add(timScheduleBeg);
      timScheduleList.add(timScheduleEnd);
      timScheduleService.insertBatch(timScheduleList);

      // 开始定时器
      List<TimSchedule> jobList1 = new ArrayList<TimSchedule>();
      jobList1.add(timScheduleBeg);
      Map<String, Object> map1 = new HashMap<String, Object>();
      map1.put("actAljoinDelegate", obj);
      map1.put("timSchedule", timScheduleBeg);
      jobScheduleService.doJob(jobList1, map1);

      // 结束定时器
      List<TimSchedule> jobList2 = new ArrayList<TimSchedule>();
      jobList2.add(timScheduleEnd);
      Map<String, Object> map2 = new HashMap<String, Object>();
      map2.put("actAljoinDelegate", obj);
      map2.put("timSchedule", timScheduleEnd);
      jobScheduleService.doJob(jobList2, map2);

      // 执行完结束定时器后，销毁定时器(在定时器类中执行)

    }
  }

  /**
   * 
   * 终止委托业务
   *
   * @return：void
   *
   * @author：pengsp
   *
   * @date：2017-10-24
   */
  @Transactional
  public void stopDelegateBiz(ActAljoinDelegate obj,boolean isDelete) throws Exception {
    
    Where<TimSchedule> timScheduleWhere = new Where<TimSchedule>();
    timScheduleWhere.eq("biz_type", "act_aljoin_delegate");
    timScheduleWhere.eq("biz_id", obj.getId());
    List<TimSchedule> timScheduleList = timScheduleService.selectList(timScheduleWhere);
    //删除内存定时器
    List<Long> deleteIdList = new ArrayList<Long>();
    for (TimSchedule timSchedule : timScheduleList) {
      deleteIdList.add(timSchedule.getId());
      jobScheduleService.stopJob(timSchedule.getId().toString());
    }
    //删除定时器数据
    if(deleteIdList.size() > 0){
      timScheduleService.deleteBatchIds(deleteIdList);
    }
//    if(isDelete){
//      //删除
//      actAljoinDelegateService.stopDelegateBiz(obj, 3);
//    }else{
//      //终止
//      actAljoinDelegateService.stopDelegateBiz(obj, 2);
//    }
  }
  /**
   * 
   * App终止委托业务
   *
   * @return：void
   *
   * @author：pengsp
   *
   * @date：2017-10-24
   */
  @Transactional
  public void appStopDelegateBiz(ActAljoinDelegate obj,AutUser autUser,boolean isDelete) throws Exception {
    
    Where<TimSchedule> timScheduleWhere = new Where<TimSchedule>();
    timScheduleWhere.eq("biz_type", "act_aljoin_delegate");
    timScheduleWhere.eq("biz_id", obj.getId());
    List<TimSchedule> timScheduleList = timScheduleService.selectList(timScheduleWhere);
    //删除内存定时器
    List<Long> deleteIdList = new ArrayList<Long>();
    for (TimSchedule timSchedule : timScheduleList) {
      deleteIdList.add(timSchedule.getId());
      jobScheduleService.stopJob(timSchedule.getId().toString());
    }
    //删除定时器数据
    if(deleteIdList.size() > 0){
      timScheduleService.deleteBatchIds(deleteIdList);
    }
    if(isDelete){
      //删除
      actAljoinDelegateService.appStopDelegateBiz(obj,autUser.getId().toString(),autUser.getUserName(), 3);
    }else{
      //终止
      actAljoinDelegateService.appStopDelegateBiz(obj,autUser.getId().toString(),autUser.getUserName(),2);
    }
  }

  /**
   * 
   * 委托定时器业务
   *
   * @return：void
   *
   * @author：pengsp
   *
   * @date：2017-10-24
   */
  @Transactional
  public void timerDelegateBiz(ActAljoinDelegate obj, boolean isAdd) throws Exception {

  }

  /**
   * 
   * 事件创建委托业务
   *
   * @return：void
   *
   * @author：pengsp
   *
   * @date：2017-10-24
   */
  @Transactional
  public void eventCreateDelegateBiz(ActivitiEntityEventImpl activitiEntityEventImpl)
      throws Exception {

  }

  /**
   * 
   * 事件完成委托业务
   *
   * @return：void
   *
   * @author：pengsp
   *
   * @date：2017-10-24
   */
  @Transactional
  public void eventCompletedDelegateBiz(
      ActivitiEntityWithVariablesEventImpl activitiEntityWithVariablesEventImpl) throws Exception {

  }
  
  /**
   * 
   * 新增委托入口
   *
   * @return：void
   *
   * @author：zhongjy
   *
   * @date：2017年11月16日 下午1:51:00
   */
  @Transactional
  public void add(ActAljoinDelegateVO obj) throws Exception {
    // 插入基础业务表
    ActAljoinDelegate actAljoinDelegate = actAljoinDelegateService.add(obj);
    // 进行真正的委托处理
    addDelegateBiz(actAljoinDelegate);
  }
}
