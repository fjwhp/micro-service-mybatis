package aljoin.act.cmd;

import aljoin.act.service.ActTaskAddSignServiceImpl;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.runtime.AtomicOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 开始活动命令
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public class StartActivityCmd implements Command<Void> {
    private ActivityImpl activity;

    private String executionId;

    private final static Logger logger = LoggerFactory.getLogger(StartActivityCmd.class);

    public StartActivityCmd(String executionId, ActivityImpl activity) {
        this.activity = activity;
        this.executionId = executionId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        // 创建新任务
        logger.debug(String.format("executing activity: %s", activity.getId()));
        ExecutionEntity execution = commandContext.getExecutionEntityManager().findExecutionById(executionId);
        if (null != execution) {
            ExecutionEntity parentExecution
                = commandContext.getExecutionEntityManager().findExecutionById(execution.getParentId());
            if(null == parentExecution){
                parentExecution = execution;
            }
            ExecutionEntity executionEntity = new ExecutionEntity();
            executionEntity.setTenantId(parentExecution.getTenantId());
            executionEntity.setProcessInstance(parentExecution.getProcessInstance());
            executionEntity.setProcessDefinition(parentExecution.getProcessDefinition());
            executionEntity.setParent(parentExecution);
            executionEntity.setScope(false);
            commandContext.getExecutionEntityManager().insert(executionEntity);
            if (Context.getProcessEngineConfiguration() != null
                && Context.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
                Context.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
                    ActivitiEventBuilder.createEntityEvent(ActivitiEventType.ENTITY_CREATED, executionEntity));
                Context.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
                    ActivitiEventBuilder.createEntityEvent(ActivitiEventType.ENTITY_INITIALIZED, executionEntity));
            }

            executionEntity.setActivity(activity);

            executionEntity.performOperation(AtomicOperation.ACTIVITY_START);
        }
        return null;
    }
}