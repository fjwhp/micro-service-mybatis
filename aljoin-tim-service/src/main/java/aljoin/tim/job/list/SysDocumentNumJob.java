 package aljoin.tim.job.list;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aljoin.sys.iservice.SysDocumentNumberService;
import aljoin.tim.job.BaseJob;
import aljoin.util.SpringContextUtil;

/**
 *
 * 文号恢复起始值(定时器).
 *
 * @author：caizx
 *
 * @date： 2018-03-27
 */
public class SysDocumentNumJob implements BaseJob {
    private final static Logger logger = LoggerFactory.getLogger(SysDocumentNumJob.class);

    @Override
    public void execute(Map<String, Object> map) {
        try {
            SysDocumentNumberService sysDocumentNumberService =
                (SysDocumentNumberService)SpringContextUtil.getBean("sysDocumentNumberServiceImpl");
            sysDocumentNumberService.resetByYear();
        } catch (Exception e) {
            logger.error("文号定时任务执行异常", e);
        }
    }
}
