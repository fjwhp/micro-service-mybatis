package aljoin.tim.job.list;

import aljoin.mee.iservice.MeeInsideMeetingService;
import aljoin.tim.job.BaseJob;
import aljoin.util.SpringContextUtil;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 内部会议自动修改到期的会议为已完成(定时器).
 *
 * @author：wangj.
 *
 * @date： 2017-11-09
 */
public class MeeInSideJob implements BaseJob {

    private final static Logger logger = LoggerFactory.getLogger(MeeInSideJob.class);

    @Override
    public void execute(Map<String, Object> map) {
        try {
            MeeInsideMeetingService meeInsideMeetingService =
                (MeeInsideMeetingService)SpringContextUtil.getBean("meeInsideMeetingServiceImpl");
            meeInsideMeetingService.autoComplete();
        } catch (Exception e) {
            logger.error("内部会议室自动完成会议定时任务执行异常", e);
        }
    }
}