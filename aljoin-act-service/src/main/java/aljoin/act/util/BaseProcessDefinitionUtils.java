package aljoin.act.util;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 流程定义相关操作的封装
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public abstract class BaseProcessDefinitionUtils {
	private final static Logger logger = LoggerFactory.getLogger(BaseProcessDefinitionUtils.class);

	public static ActivityImpl getActivity(ProcessEngine processEngine, String processDefId, String activityId) {
		ProcessDefinitionEntity pde = getProcessDefinition(processEngine, processDefId);
		return (ActivityImpl) pde.findActivity(activityId);
	}

	public static ProcessDefinitionEntity getProcessDefinition(ProcessEngine processEngine, String processDefId) {
		return (ProcessDefinitionEntity) ((RepositoryServiceImpl) processEngine.getRepositoryService())
				.getDeployedProcessDefinition(processDefId);
	}

	public static void grantPermission(ActivityImpl activity, String assigneeExpression,
			String candidateGroupIdExpressions, String candidateUserIdExpressions) throws Exception {
		TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activity.getActivityBehavior()).getTaskDefinition();
		taskDefinition.setAssigneeExpression(assigneeExpression == null ? null : new FixedValue(assigneeExpression));
		FieldUtils.writeField(taskDefinition, "candidateUserIdExpressions",
			BaseExpressionUtils.stringToExpressionSet(candidateUserIdExpressions), true);
		FieldUtils.writeField(taskDefinition, "candidateGroupIdExpressions",
			BaseExpressionUtils.stringToExpressionSet(candidateGroupIdExpressions), true);

		logger.info(String.format("granting previledges for [%s, %s, %s] on [%s, %s]", assigneeExpression,
				candidateGroupIdExpressions, candidateUserIdExpressions, activity.getProcessDefinition().getKey(),
				activity.getProperty("name")));
	}
}
