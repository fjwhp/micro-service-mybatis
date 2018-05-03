package aljoin.tim.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aljoin.tim.dao.entity.TimSchedule;
import aljoin.tim.iservice.JobScheduleService;
import aljoin.tim.iservice.TimScheduleService;
import aljoin.tim.job.JobFactory;
import aljoin.util.DateUtil;
import aljoin.util.StringUtil;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
@Service
public class JobScheduleServiceImpl implements JobScheduleService {
    private final static Logger logger = LoggerFactory.getLogger(JobScheduleServiceImpl.class);

    @Resource
    private Scheduler scheduler;
    @Resource
    private TimScheduleService timScheduleService;

    /**
     * 启动定时器
     */
    @Override
    public void doJob(List<TimSchedule> jobList, Map<String, Object> map) {
        for (TimSchedule sj : jobList) {
            // 对于一次性执行的定时器（任务委托定时器），由于项目启动的时间差造成启发时间变成了一个过去的时间，这种数据需要做特殊处理
            // 这里变先把定时器记录进行挂起处理（is_active置0）
            if ("act_aljoin_delegate".equals(sj.getBizType())) {
                Date executeTime = StringUtil.cron2date(sj.getCronExpression());
                if (DateUtil.getPeriod(executeTime, new Date(), DateUtil.UNIT_MINUTE) > 0) {
                    // executeTime是一个过去的时间
                    TimSchedule ts = timScheduleService.selectById(sj.getId());
                    ts.setIsActive(0);
                    timScheduleService.updateById(ts);
                    // 本条定时器不启动
                    continue;
                }
            }

            /* 根据任务id获取触发器 */
            TriggerKey tk = TriggerKey.triggerKey(sj.getId().toString());
            CronTrigger ct = null;
            try {
                ct = (CronTrigger)scheduler.getTrigger(tk);
            } catch (SchedulerException e) {
                logger.error("", e);
            }
            if (ct == null) {
                /* 根据jobId新建JobDetail */
                JobDetail jd = JobBuilder.newJob(JobFactory.class).withIdentity(sj.getId().toString()).build();
                jd.getJobDataMap().put("timSchedule", sj);
                jd.getJobDataMap().put("paramMap", map);
                /* 表达式调度构建器 */
                CronScheduleBuilder scheduleBuilder = null;
                try {
                    scheduleBuilder = CronScheduleBuilder.cronSchedule(sj.getCronExpression());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                /* 新建触发器 */
                ct = TriggerBuilder.newTrigger().withIdentity(sj.getId().toString()).withSchedule(scheduleBuilder)
                    .build();
                try {
                    /* 执行job */
                    logger.info("开始执行新任务:jobId-[" + sj.getId() + "]jobName-[" + sj.getJobName() + "]......");
                    scheduler.scheduleJob(jd, ct);
                } catch (SchedulerException e) {
                    logger.error("启动定时器异常", e);
                }
            } else {
                /* 触发器已存在，那么更新相应的定时设置 */
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(sj.getCronExpression());
                /* 按新的cronExpression表达式重新构建触发器 */
                ct = ct.getTriggerBuilder().withIdentity(tk).withSchedule(scheduleBuilder).build();
                try {
                    /* 按照新的触发器执行job */
                    logger.info("重新执行新任务:jobId-[" + sj.getId() + "]jobName-[" + sj.getJobName() + "]......");
                    scheduler.rescheduleJob(tk, ct);
                } catch (SchedulerException e) {
                    logger.error("", e);
                }
            }
        }
    }

    /**
     * 获取计划中的定时器
     */
    @Override
    public List<TimSchedule> getPlanningJob() {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = null;
        try {
            jobKeys = scheduler.getJobKeys(matcher);
        } catch (SchedulerException e) {
            logger.error("", e);
        }
        List<TimSchedule> jobList = new ArrayList<TimSchedule>();
        for (JobKey jobKey : jobKeys) {
            List<? extends Trigger> triggers = null;
            try {
                triggers = scheduler.getTriggersOfJob(jobKey);
            } catch (SchedulerException e) {
                logger.error("", e);
            }
            for (Trigger trigger : triggers) {
                TimSchedule timSchedule = timScheduleService.selectById(jobKey.getName());

                TriggerState state = null;
                try {
                    state = scheduler.getTriggerState(trigger.getKey());
                } catch (SchedulerException e) {
                    logger.error("", e);
                }
                timSchedule.setJobStatus(state.name());
                /*
                 * if (trigger instanceof CronTrigger) { CronTrigger cronTrigger = (CronTrigger) trigger;
                 * String cronExpression = cronTrigger.getCronExpression();
                 * jobSchedule.setCronExpression(cronExpression); }
                 */
                jobList.add(timSchedule);
            }
        }
        return jobList;
    }

    /**
     * 获取正在运行的定时器
     */
    @Override
    public List<TimSchedule> getRunningJob() {
        List<TimSchedule> jobList = new ArrayList<TimSchedule>();
        List<JobExecutionContext> executingJobs = null;
        try {
            executingJobs = scheduler.getCurrentlyExecutingJobs();
        } catch (SchedulerException e) {
            logger.error("", e);
        }
        for (JobExecutionContext executingJob : executingJobs) {
            JobDetail jobDetail = executingJob.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            Trigger trigger = executingJob.getTrigger();
            TimSchedule job = timScheduleService.selectById(jobKey.getName());
            TriggerState triggerState = null;
            try {
                triggerState = scheduler.getTriggerState(trigger.getKey());
            } catch (SchedulerException e) {
                logger.error("", e);
            }
            job.setJobStatus(triggerState.name());
            jobList.add(job);
        }
        return jobList;
    }

    /**
     * 定时器暂停
     */
    @Override
    public void pauseJob(String jobId) {
        JobKey jobKey = JobKey.jobKey(jobId);
        try {
            logger.info("暂停任务[" + jobId + "]......");
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            logger.error("", e);
        }
    }

    /**
     * 定时器恢复
     */
    @Override
    public void resumeJob(String jobId) {
        JobKey jobKey = JobKey.jobKey(jobId);
        try {
            logger.info("恢复任务[" + jobId + "]......");
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            logger.error("", e);
        }
    }

    /**
     * 停止定时器
     */
    @Override
    public void stopJob(String jobId) {
        JobKey jobKey = JobKey.jobKey(jobId);
        try {
            logger.info("删除任务[" + jobId + "]......");
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            logger.error("", e);
        }
    }

    /**
     * 获取定时器属性
     */
    @Override
    public TimSchedule getJobProps(TimSchedule timSchedule) {
        TriggerKey tk = TriggerKey.triggerKey(timSchedule.getId().toString());
        TriggerState triggerState = null;
        try {
            triggerState = scheduler.getTriggerState(tk);
            timSchedule.setJobStatus(triggerState.name());
            Trigger trigger = scheduler.getTrigger(tk);
            if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger)trigger;
                timSchedule.setBegTime(cronTrigger.getStartTime());
                timSchedule.setEndTime(cronTrigger.getEndTime());
                timSchedule.setPreviousTime(cronTrigger.getPreviousFireTime());
                timSchedule.setNextTime(cronTrigger.getNextFireTime());
            }
        } catch (SchedulerException e) {
            logger.error("", e);
        }
        return timSchedule;
    }
}
