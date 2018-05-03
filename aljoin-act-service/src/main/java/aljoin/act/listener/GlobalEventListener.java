package aljoin.act.listener;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aljoin.util.SpringContextUtil;

/**
 * 
 * 全局监听器
 *
 * @author：zhongjy
 * 
 * @date：2017年11月6日 上午10:34:49
 */
public class GlobalEventListener implements ActivitiEventListener {

    private final static Logger logger = LoggerFactory.getLogger(GlobalEventListener.class);
    /**
     * 事件监听器列表
     */
    private Map<String, String> handlers = new HashMap<String, String>();

    @Override
    public void onEvent(ActivitiEvent event) {
        logger.info("事件类型：" + event.getType().name() + ",流程定义ID：" + event.getProcessDefinitionId() + ",流程实例ID："
            + event.getProcessInstanceId() + ",执行流ID：" + event.getExecutionId());

        /**
         * 根据事件的类型ID,找到对应的事件处理器
         */
        String beanId = handlers.get(event.getType().name());
        if (StringUtils.isNotEmpty(beanId)) {
            EventHandler handler = (EventHandler)SpringContextUtil.getBean(beanId);
            handler.handle(event);
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    public Map<String, String> getHandlers() {
        return handlers;
    }

    public void setHandlers(Map<String, String> handlers) {
        this.handlers = handlers;
    }
}
