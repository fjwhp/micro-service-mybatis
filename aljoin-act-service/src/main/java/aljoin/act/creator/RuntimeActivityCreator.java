package aljoin.act.creator;

import aljoin.act.iservice.ActRuActivityDefinitionService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

/**
 * 创建运行活动
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public interface RuntimeActivityCreator {

    /**
     * 创建活动
     *
     * @param processEngine
     *
     * @param processDefinition
     *
     * @param info
     *
     * @return
     */
    public ActivityImpl[] createActivities(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
        ActRuActivityDefinitionService info);
}