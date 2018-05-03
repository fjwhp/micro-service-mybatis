package aljoin.act.listener.list;

import javax.annotation.Resource;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aljoin.act.listener.EventHandler;
import aljoin.act.task.ActTask;

/**
 * 
 * 创建了新任务。它位于ENTITY_CREATE事件之后。当任务是由流程创建时， 这个事件会在TaskListener执行之前被执行
 *
 * @author：zhongjy
 * 
 * @date：2017年11月6日 下午2:18:32
 */
@Service
public class TaskCreatedListener implements EventHandler {

    private final static Logger logger = LoggerFactory.getLogger(TaskCreatedListener.class);
    @Resource
    private ActTask actTask;

    @Override
    public void handle(ActivitiEvent event) {
        ActivitiEntityEventImpl activitiEntityEventImpl = (ActivitiEntityEventImpl)event;
        logger.info("创建任务任务监听触发");
        try {
            /**
             * 通过异步线程的方式，跳出事务的圈子，然后通过延迟执行的方式进行
             */
            actTask.taskCreatedListenerTask(activitiEntityEventImpl);
        } catch (Exception e) {
            logger.error("穿件任务监听触发异常", e);
        }
    }

}
