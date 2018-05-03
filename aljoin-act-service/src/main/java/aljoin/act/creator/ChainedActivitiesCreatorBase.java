package aljoin.act.creator;

import aljoin.act.iservice.ActRuActivityDefinitionService;
import aljoin.act.util.BaseProcessDefinitionUtils;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建活动链接
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public class ChainedActivitiesCreatorBase extends BaseRuntimeActivityCreatorSupport implements RuntimeActivityCreator {
    @Override
    public ActivityImpl[] createActivities(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
        ActRuActivityDefinitionService info) {
        info.setFactoryName(ChainedActivitiesCreatorBase.class.getName());
        RuActivityDefinitionIntepreter radei = new RuActivityDefinitionIntepreter(info);

        if (radei.getCloneActivityIds() == null) {
            radei.setCloneActivityIds(CollectionUtils.arrayToList(new String[radei.getAssignees().size()]));
        }

        return createActivities(processEngine, processDefinition, info.getProcessInstanceId(),
            radei.getPrototypeActivityId(), radei.getNextActivityId(), radei.getAssignees(),
            radei.getCloneActivityIds());
    }

    private ActivityImpl[] createActivities(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
        String processInstanceId, String prototypeActivityId, String nextActivityId, List<String> assignees,
        List<String> activityIds) {
        ActivityImpl prototypeActivity
            = BaseProcessDefinitionUtils.getActivity(processEngine, processDefinition.getId(), prototypeActivityId);

        List<ActivityImpl> activities = new ArrayList<ActivityImpl>();
        for (int i = 0; i < assignees.size(); i++) {
            if (activityIds.get(i) == null) {
                String activityId = createUniqueActivityId(processInstanceId, prototypeActivityId);
                activityIds.set(i, activityId);
            }

            ActivityImpl clone = createActivity(processEngine, processDefinition, prototypeActivity, activityIds.get(i),
                assignees.get(i));
            activities.add(clone);
        }

        ActivityImpl nextActivity
            = BaseProcessDefinitionUtils.getActivity(processEngine, processDefinition.getId(), nextActivityId);
        createActivityChain(activities, nextActivity);

        return activities.toArray(new ActivityImpl[0]);
    }
}
