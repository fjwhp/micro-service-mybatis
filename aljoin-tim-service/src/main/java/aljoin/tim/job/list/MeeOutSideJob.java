package aljoin.tim.job.list;

import aljoin.mee.iservice.MeeOutsideMeetingService;
import aljoin.tim.job.BaseJob;
import aljoin.util.SpringContextUtil;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 外部会议自动修改到期的会议为已完成(定时器).
 *
 * @author：wangj.
 *
 * @date： 2017-11-09
 */
public class MeeOutSideJob implements BaseJob {

    private final static Logger logger = LoggerFactory.getLogger(MeeOutSideJob.class);

    @Override
    public void execute(Map<String, Object> map) {
        try {
            MeeOutsideMeetingService meeOutsideMeetingService =
                (MeeOutsideMeetingService)SpringContextUtil.getBean("meeOutsideMeetingServiceImpl");
            meeOutsideMeetingService.autoComplete();
        } catch (Exception e) {
            logger.error("外部会议室自动完成会议定时任务执行异常", e);
        }
    }
}
