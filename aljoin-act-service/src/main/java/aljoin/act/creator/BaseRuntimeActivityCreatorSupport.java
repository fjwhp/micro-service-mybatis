package aljoin.act.creator;

import aljoin.act.util.BaseCloneUtils;
import aljoin.act.util.BaseProcessDefinitionUtils;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 创建运行活动
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public abstract class BaseRuntimeActivityCreatorSupport {
    private static int SEQUNCE_NUMBER = 0;

    protected ActivityImpl cloneActivity(ProcessDefinitionEntity processDefinition, ActivityImpl prototypeActivity,
        String newActivityId, String... fieldNames) {
        ActivityImpl clone = processDefinition.createActivity(newActivityId);
        BaseCloneUtils.copyFields(prototypeActivity, clone, fieldNames);

        return clone;
    }

    protected TaskDefinition cloneTaskDefinition(TaskDefinition taskDefinition) {
        TaskDefinition newTaskDefinition = new TaskDefinition(taskDefinition.getTaskFormHandler());
        BeanUtils.copyProperties(taskDefinition, newTaskDefinition);
        return newTaskDefinition;
    }

    protected ActivityImpl createActivity(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
        ActivityImpl prototypeActivity, String cloneActivityId, String assignee) {
        ActivityImpl clone
            = cloneActivity(processDefinition, prototypeActivity, cloneActivityId, "executionListeners", "properties");

        // 获取该节点下一个节点信息
        TaskDefinition taskDefinition = null;
        if (prototypeActivity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
            // 一般用户节点
            taskDefinition = ((UserTaskActivityBehavior)prototypeActivity.getActivityBehavior()).getTaskDefinition();
        } else if (prototypeActivity.getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
            // 多实例并行
            taskDefinition
                = ((UserTaskActivityBehavior)(((ParallelMultiInstanceBehavior)prototypeActivity.getActivityBehavior())
                    .getInnerActivityBehavior())).getTaskDefinition();
        } else if (prototypeActivity.getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
            // 多实例串行
            taskDefinition
                = ((UserTaskActivityBehavior)(((SequentialMultiInstanceBehavior)prototypeActivity.getActivityBehavior())
                    .getInnerActivityBehavior())).getTaskDefinition();
        }

        cloneActivityId = cloneActivityId.substring(cloneActivityId.indexOf(":")+1,cloneActivityId.lastIndexOf(":"));
        taskDefinition.setKey(cloneActivityId);

        // 设置assignee
        if (assignee != null) {
            taskDefinition.setAssigneeExpression(new FixedValue(assignee));
        }

        UserTaskActivityBehavior cloneActivityBehavior
            = new UserTaskActivityBehavior(prototypeActivity.getId(), taskDefinition);
        clone.setActivityBehavior(cloneActivityBehavior);

        return clone;
    }

    protected ActivityImpl createActivity(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
        String prototypeActivityId, String cloneActivityId, String assignee) {
        ActivityImpl prototypeActivity
            = BaseProcessDefinitionUtils.getActivity(processEngine, processDefinition.getId(), prototypeActivityId);

        return createActivity(processEngine, processDefinition, prototypeActivity, cloneActivityId, assignee);
    }

    protected void createActivityChain(List<ActivityImpl> activities, ActivityImpl nextActivity) {
        for (int i = 0; i < activities.size(); i++) {
            // 设置各活动的下线
            activities.get(i).getOutgoingTransitions().clear();
            activities.get(i).createOutgoingTransition("flow" + (i + 1))
                .setDestination(i == activities.size() - 1 ? nextActivity : activities.get(i + 1));
        }
    }

    protected String createUniqueActivityId(String processInstanceId, String prototypeActivityId) {
        return processInstanceId + ":" + prototypeActivityId + ":" + System.currentTimeMillis() + "-"
            + (SEQUNCE_NUMBER++);
    }
}
