package aljoin.tim.job.list;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.tim.job.BaseJob;
import aljoin.util.SpringContextUtil;

/**
 *
 * 用户数据刷新(10s一次，把detail表的内容往data表里插).
 *
 * @author：sln.
 *
 * @date： 2017-11-09
 */
public class indexDataRefreshJob implements BaseJob {

    private final static Logger logger = LoggerFactory.getLogger(indexDataRefreshJob.class);

    @Override
    public void execute(Map<String, Object> map) {
        try {
            long beg = System.currentTimeMillis();
            AutDataStatisticsService autDataStatisticsService =
                (AutDataStatisticsService)SpringContextUtil.getBean("autDataStatisticsServiceImpl");
            autDataStatisticsService.pushData2Statistics();
            long end = System.currentTimeMillis();
            long period = end - beg;
            logger.info("用户数据维护更新耗时-----" + period + "ms");
        } catch (Exception e) {
            logger.error("用户数据维护更新定时器运行错误", e);
        }
    }
}