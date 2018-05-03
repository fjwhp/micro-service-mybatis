package aljoin.tim.job.list;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aljoin.att.iservice.AttSignInOutService;
import aljoin.tim.job.BaseJob;
import aljoin.util.SpringContextUtil;

/**
 *
 * 定时推送打卡(定时器).
 *
 * @author：wangj.
 *
 * @date： 2017-11-09
 */
public class AttPushPatchJob implements BaseJob {

    private final static Logger logger = LoggerFactory.getLogger(AttPushPatchJob.class);

    @Override
    public void execute(Map<String, Object> map) {
        try {
            AttSignInOutService attSignInOutService =
                (AttSignInOutService)SpringContextUtil.getBean("attSignInOutServiceImpl");
            attSignInOutService.pushMessageToUserList(null);
        } catch (Exception e) {
            logger.error("推送打卡定时任务执行异常", e);
        }
    }
}
