package aljoin.act.cmd;

import aljoin.object.WebConstant;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 删除正在执行的任务命令
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public class DeleteRunningTaskCmd implements Command<Void> {
    private TaskEntity currentTaskEntity;

    private final static Logger logger = LoggerFactory.getLogger(DeleteRunningTaskCmd.class);

    public DeleteRunningTaskCmd(TaskEntity currentTaskEntity) {
        this.currentTaskEntity = currentTaskEntity;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        // 删除当前的任务
        // 不能删除当前正在执行的任务，所以要先清除掉关联
        if (currentTaskEntity != null) {
            logger.debug(String.format("deleting task: %s [id=%s]", currentTaskEntity.getName(), currentTaskEntity.getId()));

            Context.getCommandContext().getTaskEntityManager().deleteTask(currentTaskEntity,
                TaskEntity.DELETE_REASON_DELETED, false);

        }

        return null;
    }
}