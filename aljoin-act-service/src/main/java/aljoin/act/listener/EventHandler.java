package aljoin.act.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;

/**
 * 
 * 自定义事件处理器
 *
 * @author：zhongjy
 * 
 * @date：2017年11月6日 下午2:16:48
 */
public interface EventHandler {
    /**
     * 
     * 自定义事件处理器
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年11月6日 下午2:17:12
     */
    public void handle(ActivitiEvent event);
}
