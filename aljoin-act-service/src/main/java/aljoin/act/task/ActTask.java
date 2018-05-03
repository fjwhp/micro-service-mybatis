package aljoin.act.task;

import javax.annotation.Resource;

import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.delegate.event.impl.ActivitiEntityWithVariablesEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import aljoin.act.iservice.ActAljoinDelegateService;

/**
 * 
 * 异步线程类
 *
 * @author：zhongjy
 * 
 * @date：2017年11月17日 下午12:35:01
 */
@Component
public class ActTask {

    private final static Logger logger = LoggerFactory.getLogger(ActTask.class);

    @Resource
    private ActAljoinDelegateService actAljoinDelegateService;

    /**
     * 
     * 穿件任务监听处理
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年6月5日 下午7:08:59
     */
    @Async("actAsyncPool")
    public void taskCreatedListenerTask(ActivitiEntityEventImpl activitiEntityEventImpl) throws Exception {
        logger.info("延迟5秒执行任务创建的监听事件方法(等等数据处理完毕)");
        Thread.sleep(5000);
        actAljoinDelegateService.eventCreateDelegateBiz(activitiEntityEventImpl);
    }

    /**
     * 
     * 穿件任务监听处理
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年6月5日 下午7:08:59
     */
    @Async("actAsyncPool")
    public void taskCompletedListenerTask(ActivitiEntityWithVariablesEventImpl activitiEntityWithVariablesEventImpl)
        throws Exception {
        logger.info("延迟5秒执行任务创建的监听事件方法(等等数据处理完毕)");
        Thread.sleep(5000);
        actAljoinDelegateService.eventCompletedDelegateBiz(activitiEntityWithVariablesEventImpl);
    }
}
