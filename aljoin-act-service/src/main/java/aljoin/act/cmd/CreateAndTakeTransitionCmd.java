package aljoin.act.cmd;

import aljoin.act.service.ActTaskAddSignServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.runtime.AtomicOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建和转换命令
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public class CreateAndTakeTransitionCmd implements Command<Void> {
    private ActivityImpl activity;

    private String executionId;

    private final static Logger logger = LoggerFactory.getLogger(CreateAndTakeTransitionCmd.class);

    public CreateAndTakeTransitionCmd(String executionId, ActivityImpl activity) {
        this.executionId = executionId;
        this.activity = activity;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        // 创建新任务
        logger.debug(String.format("executing activity: %s", activity.getId()));

        ExecutionEntity execution = commandContext.getExecutionEntityManager().findExecutionById(executionId);
        execution.setActivity(activity);
        execution.performOperation(AtomicOperation.TRANSITION_CREATE_SCOPE);
        return null;
    }
}