package aljoin.act.creator;

import aljoin.act.iservice.ActRuActivityDefinitionService;

import javax.annotation.Resource;
import java.util.List;

/**
 * RuntimeActivityDefinitionEntity的解释类（代理类） 主要用以解释properties字段的值，如为get("name")提供getName()方法
 * 
 * @author bluejoe2008@gmail.com
 * 
 */
public class RuActivityDefinitionIntepreter {
    @Resource
    private ActRuActivityDefinitionService actRuActivityDefinitionService;

    public RuActivityDefinitionIntepreter(ActRuActivityDefinitionService entity) {
        super();
        actRuActivityDefinitionService = entity;
    }

    public List<String> getAssignees() {
        return actRuActivityDefinitionService.getProperty("assignees");
    }

    public String getCloneActivityId() {
        return actRuActivityDefinitionService.getProperty("cloneActivityId");
    }

    public List<String> getCloneActivityIds() {
        return actRuActivityDefinitionService.getProperty("cloneActivityIds");
    }

    public String getNextActivityId() {
        return actRuActivityDefinitionService.getProperty("nextActivityId");
    }

    public String getPrototypeActivityId() {
        return actRuActivityDefinitionService.getProperty("prototypeActivityId");
    }

    public boolean getSequential() {
        return (Boolean)actRuActivityDefinitionService.getProperty("sequential");
    }

    public void setAssignees(List<String> assignees) {
        actRuActivityDefinitionService.setProperty("assignees", assignees);
    }

    public void setCloneActivityId(String cloneActivityId) {
        actRuActivityDefinitionService.setProperty("cloneActivityId", cloneActivityId);
    }

    public void setCloneActivityIds(List<String> cloneActivityIds) {
        actRuActivityDefinitionService.setProperty("cloneActivityIds", cloneActivityIds);
    }

    public void setNextActivityId(String nextActivityId) {
        actRuActivityDefinitionService.setProperty("nextActivityId", nextActivityId);
    }

    public void setPrototypeActivityId(String prototypeActivityId) {
        actRuActivityDefinitionService.setProperty("prototypeActivityId", prototypeActivityId);
    }

    public void setSequential(boolean sequential) {
        actRuActivityDefinitionService.setProperty("sequential", sequential);
    }
}
