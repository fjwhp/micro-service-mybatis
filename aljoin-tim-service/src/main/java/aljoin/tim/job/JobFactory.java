package aljoin.tim.job;

import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aljoin.tim.dao.entity.TimSchedule;

/**
 * 
 * 任务工厂
 *
 * @author：zhongjy
 * 
 * @date：2017年10月26日 上午9:58:33
 */
@DisallowConcurrentExecution
public class JobFactory implements Job {
    private final static Logger logger = LoggerFactory.getLogger(JobFactory.class);

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        TimSchedule timSchedule = (TimSchedule)context.getMergedJobDataMap().get("timSchedule");
        Map<String, Object> paramMap = null;
        if (context.getMergedJobDataMap().get("paramMap") != null) {
            paramMap = (Map<String, Object>)context.getMergedJobDataMap().get("paramMap");
        }
        String cn = "aljoin.tim.job.list." + timSchedule.getExeClassName();
        Class c = null;
        try {
            c = Class.forName(cn);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        BaseJob baseJob = null;
        try {
            baseJob = (BaseJob)c.newInstance();
        } catch (Exception e) {
            logger.error("", e);
        }
        logger.info("开始执行任务[" + timSchedule.getJobName() + "][" + cn + "]......");
        baseJob.execute(paramMap);
    }
}
