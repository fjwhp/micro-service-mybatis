package aljoin.act.listener.list;

import javax.annotation.Resource;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.impl.ActivitiEntityWithVariablesEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aljoin.act.listener.EventHandler;
import aljoin.act.task.ActTask;

/**
 * 
 * 任务被完成了。它会在ENTITY_DELETE事件之前触发。当任务是流程一部分时，事件会在流程继续运行之前， 后续事件将是ACTIVITY_COMPLETE，对应着完成任务的节点
 *
 * @author：zhongjy
 * 
 * @date：2017年11月6日 下午2:18:32
 */
@Service
public class TaskCompletedListener implements EventHandler {

    private final static Logger logger = LoggerFactory.getLogger(TaskCompletedListener.class);

    @Resource
    private ActTask actTask;

    @Override
    public void handle(ActivitiEvent event) {
        ActivitiEntityWithVariablesEventImpl activitiEntityWithVariablesEventImpl =
            (ActivitiEntityWithVariablesEventImpl)event;
        logger.info("完成任务监听触发");
        try {
            // 通过异步线程的方式，跳出事务的圈子，然后通过延迟执行的方式进行
            actTask.taskCompletedListenerTask(activitiEntityWithVariablesEventImpl);
        } catch (Exception e) {
            logger.error("完成任务监听触发异常", e);
        }

    }

}
