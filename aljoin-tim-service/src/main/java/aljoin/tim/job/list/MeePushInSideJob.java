package aljoin.tim.job.list;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aljoin.mee.iservice.MeeInsideMeetingService;
import aljoin.mee.iservice.MeeOutsideMeetingService;
import aljoin.tim.job.BaseJob;
import aljoin.util.SpringContextUtil;

/**
 *
 * 会议提前通知(定时器).
 *
 * @author：sln.
 *
 * @date： 2017-11-09
 */
public class MeePushInSideJob implements BaseJob {

    private final static Logger logger = LoggerFactory.getLogger(MeePushInSideJob.class);

    @Override
    public void execute(Map<String, Object> map) {
        try {
            MeeInsideMeetingService meeInsideMeetingService =
                (MeeInsideMeetingService)SpringContextUtil.getBean("meeInsideMeetingServiceImpl");
            meeInsideMeetingService.pushInsideMeeting();
            MeeOutsideMeetingService meeOutsideMeetingService =
                (MeeOutsideMeetingService)SpringContextUtil.getBean("meeOutsideMeetingServiceImpl");
            meeOutsideMeetingService.pushOutsideMeeting();
        } catch (Exception e) {
            logger.error("会议提前通知定时任务执行异常", e);
        }
    }
}