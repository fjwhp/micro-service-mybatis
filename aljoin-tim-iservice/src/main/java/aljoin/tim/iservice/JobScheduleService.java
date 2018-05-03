package aljoin.tim.iservice;

import java.util.List;
import java.util.Map;

import aljoin.tim.dao.entity.TimSchedule;

/**
 * 
 * @author zhongjy.
 * 
 * @date 2018年2月9日
 */
public interface JobScheduleService {

    /**
     * 
     * 根据任务数据执行quartz任务，新任务会执行，旧任务会覆盖执行
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月26日 上午10:11:13
     */
    void doJob(List<TimSchedule> jobList, Map<String, Object> map);

    /**
     * 
     * 获取计划中的任务
     *
     * @return：List<TimSchedule>
     *
     * @author：zhongjy
     *
     * @date：2017年10月26日 上午10:10:44
     */
    List<TimSchedule> getPlanningJob();

    /**
     * 
     * 获取正在运行中的任务
     *
     * @return：List<TimSchedule>
     *
     * @author：zhongjy
     *
     * @date：2017年10月26日 上午10:10:37
     */
    List<TimSchedule> getRunningJob();

    /**
     * 
     * 暂停任务
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月26日 上午10:10:30
     */
    void pauseJob(String jobId);

    /**
     * 
     * 恢复任务
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月26日 上午10:10:24
     */
    void resumeJob(String jobId);

    /**
     * 
     * 删除任务，所对应的trigger也将被删除
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月26日 上午10:10:07
     */
    void stopJob(String jobId);

    /**
     * 
     * 获取任务状态
     *
     * @return：TimSchedule
     *
     * @author：zhongjy
     *
     * @date：2017年10月26日 上午10:09:58
     */
    TimSchedule getJobProps(TimSchedule timSchedule);

}
