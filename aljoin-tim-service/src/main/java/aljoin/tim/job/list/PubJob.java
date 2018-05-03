package aljoin.tim.job.list;

import aljoin.pub.iservice.PubPublicInfoService;
import aljoin.tim.job.BaseJob;
import aljoin.util.SpringContextUtil;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 公共信息定时修改有效期状态(定时器).
 *
 * @author：wangj.
 *
 * @date： 2017-11-09
 */
public class PubJob implements BaseJob {

    private final static Logger logger = LoggerFactory.getLogger(PubJob.class);

    @Override
    public void execute(Map<String, Object> map) {
        try {
            PubPublicInfoService pubPublicInfoService =
                (PubPublicInfoService)SpringContextUtil.getBean("pubPublicInfoServiceImpl");
            pubPublicInfoService.autoUpdateStatus();
        } catch (Exception e) {
            logger.error("公共信息定时任务执行异常", e);
        }
    }
}
