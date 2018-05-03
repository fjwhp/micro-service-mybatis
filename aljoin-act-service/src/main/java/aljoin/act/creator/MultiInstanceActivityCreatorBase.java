package aljoin.act.creator;

import aljoin.act.iservice.ActRuActivityDefinitionService;
import aljoin.act.util.BaseProcessDefinitionUtils;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.bpmn.behavior.*;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import java.util.List;

/**
 * 创建多实例活动
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public class MultiInstanceActivityCreatorBase extends BaseRuntimeActivityCreatorSupport
    implements RuntimeActivityCreator {

    /**
     * 创建活动
     *
     * @param processEngine
     * @param processDefinition
     * @param info
     * @return ActivityImpl[]
     */
    @Override
    public ActivityImpl[] createActivities(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
        ActRuActivityDefinitionService info) {
        info.setFactoryName(MultiInstanceActivityCreatorBase.class.getName());
        RuActivityDefinitionIntepreter radei = new RuActivityDefinitionIntepreter(info);

        if (radei.getCloneActivityId() == null) {
            String cloneActivityId
                = createUniqueActivityId(info.getProcessInstanceId(), radei.getPrototypeActivityId());
            radei.setCloneActivityId(cloneActivityId);
        }

        return new ActivityImpl[] {createMultiInstanceActivity(processEngine, processDefinition,
            radei.getPrototypeActivityId(), radei.getCloneActivityId(), radei.getSequential(), radei.getAssignees())};
    }

    /**
     * 创建多实例活动
     * 
     * @param processEngine
     * @param processDefinition
     * @param prototypeActivityId
     * @param cloneActivityId
     * @param isSequential
     * @param assignees
     * @return ActivityImpl
     */
    private ActivityImpl createMultiInstanceActivity(ProcessEngine processEngine,
        ProcessDefinitionEntity processDefinition, String prototypeActivityId, String cloneActivityId,
        boolean isSequential, List<String> assignees) {
        ActivityImpl prototypeActivity
            = BaseProcessDefinitionUtils.getActivity(processEngine, processDefinition.getId(), prototypeActivityId);

        // 拷贝listener，executionListeners会激活历史记录的保存
        ActivityImpl clone
            = cloneActivity(processDefinition, prototypeActivity, cloneActivityId, "executionListeners", "properties");

        // 拷贝所有后向链接
        for (PvmTransition trans : prototypeActivity.getOutgoingTransitions()) {
            clone.createOutgoingTransition(trans.getId()).setDestination((ActivityImpl)trans.getDestination());
        }

        MultiInstanceActivityBehavior multiInstanceBehavior = null;
        if (prototypeActivity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
            multiInstanceBehavior = isSequential
                ? new SequentialMultiInstanceBehavior(clone,
                    (TaskActivityBehavior)prototypeActivity.getActivityBehavior())
                : new ParallelMultiInstanceBehavior(clone,
                    (TaskActivityBehavior)prototypeActivity.getActivityBehavior());
        } else {
            multiInstanceBehavior = isSequential
                ? new SequentialMultiInstanceBehavior(clone,
                    ((TaskActivityBehavior)((SequentialMultiInstanceBehavior)prototypeActivity.getActivityBehavior())
                        .getInnerActivityBehavior()))
                : new ParallelMultiInstanceBehavior(clone,
                    ((TaskActivityBehavior)((ParallelMultiInstanceBehavior)(prototypeActivity.getActivityBehavior()))
                        .getInnerActivityBehavior()));
        }

        clone.setActivityBehavior(multiInstanceBehavior);
        clone.setScope(true);
        clone.setProperty("multiInstance", isSequential ? "sequential" : "parallel");
        // 设置多实例节点属性
        multiInstanceBehavior.setLoopCardinalityExpression(new FixedValue(assignees.size()));
        multiInstanceBehavior.setCollectionExpression(new FixedValue(assignees));
        return clone;
    }
}
