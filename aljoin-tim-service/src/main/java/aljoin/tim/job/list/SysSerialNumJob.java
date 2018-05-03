 package aljoin.tim.job.list;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aljoin.sys.iservice.SysSerialNumberService;
import aljoin.tim.job.BaseJob;
import aljoin.util.SpringContextUtil;

/**
 *
 * 流水号恢复起始值(定时器).
 *
 * @author：caizx
 *
 * @date： 2018-03-24
 */
public class SysSerialNumJob implements BaseJob {
    private final static Logger logger = LoggerFactory.getLogger(SysSerialNumJob.class);

    @Override
    public void execute(Map<String, Object> map) {
        try {
            SysSerialNumberService sysSerialNumberService =
                (SysSerialNumberService)SpringContextUtil.getBean("sysSerialNumberServiceImpl");
            sysSerialNumberService.resetByYear();
            sysSerialNumberService.resetByMonth();
        } catch (Exception e) {
            logger.error("流水号定时任务执行异常", e);
        }
    }
}
