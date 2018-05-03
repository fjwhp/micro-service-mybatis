package aljoin.tim.job.list;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aljoin.act.dao.entity.ActAljoinDelegate;
import aljoin.act.iservice.ActAljoinDelegateService;
import aljoin.tim.dao.entity.TimSchedule;
import aljoin.tim.iservice.JobScheduleService;
import aljoin.tim.iservice.TimScheduleService;
import aljoin.tim.job.BaseJob;
import aljoin.util.SpringContextUtil;

/**
 * 
 * 增加委托任务定时器
 *
 * @author：zhongjy
 * 
 * @date：2017年11月13日 下午2:29:44
 */
public class CreateDelegateTaskJob implements BaseJob {

    private final static Logger logger = LoggerFactory.getLogger(CreateDelegateTaskJob.class);

    @Override
    public void execute(Map<String, Object> map) {
        logger.info("开始执行[(开始时间)新增委托任务定时器]");
        try {
            // (开始时间)增加委托
            ActAljoinDelegateService actAljoinDelegateService =
                (ActAljoinDelegateService)SpringContextUtil.getBean("actAljoinDelegateServiceImpl");
            ActAljoinDelegate actAljoinDelegate = (ActAljoinDelegate)map.get("actAljoinDelegate");

            actAljoinDelegateService.timerDelegateBiz(actAljoinDelegate, true);

            // (开始时间)删除(开始时间)定时器(此处存在事务不一致的风险)
            TimSchedule timSchedule = (TimSchedule)map.get("timSchedule");
            JobScheduleService jobScheduleService =
                (JobScheduleService)SpringContextUtil.getBean("jobScheduleServiceImpl");
            jobScheduleService.stopJob(timSchedule.getId().toString());

            TimScheduleService timScheduleService =
                (TimScheduleService)SpringContextUtil.getBean("timScheduleServiceImpl");
            timScheduleService.deleteById(timSchedule.getId());
        } catch (Exception e) {
            logger.error("开始执行[(开始时间)新增委托任务定时器]", e);
        }
    }

}
