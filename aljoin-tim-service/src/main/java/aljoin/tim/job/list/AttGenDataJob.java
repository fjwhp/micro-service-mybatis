package aljoin.tim.job.list;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aljoin.att.iservice.AttSignInOutService;
import aljoin.tim.job.BaseJob;
import aljoin.util.SpringContextUtil;

/**
 *
 * 自动生成下月考勤数据(定时器).
 *
 * @author：wangj.
 *
 * @date： 2017-11-09
 */
public class AttGenDataJob implements BaseJob {

    private final static Logger logger = LoggerFactory.getLogger(AttGenDataJob.class);

    @Override
    public void execute(Map<String, Object> map) {
        try {
            AttSignInOutService attSignInOutService =
                (AttSignInOutService)SpringContextUtil.getBean("attSignInOutServiceImpl");
            attSignInOutService.add();
        } catch (Exception e) {
            logger.error("自动生成考勤数据定时任务执行异常", e);
        }
    }
}
