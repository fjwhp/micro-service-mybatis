package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 流程元素-表单关系(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-15
 */
public class ActAljoinBpmnForm extends Entity<ActAljoinBpmnForm> {

    private static final long serialVersionUID = 1L;

    /**
     * bpmn流程表ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bpmnId;
    /**
     * 表单ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long formId;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 表单所在元素类型：startEvent-开始事件节点,userTask-用户任务节点,...
     */
    private String elementType;
    /**
     * 表单所在元素ID
     */
    private String elementId;
    /**
     * 流程ID
     */
    private String processId;

    public Long getBpmnId() {
        return bpmnId;
    }

    public ActAljoinBpmnForm setBpmnId(Long bpmnId) {
        this.bpmnId = bpmnId;
        return this;
    }

    public Long getFormId() {
        return formId;
    }

    public ActAljoinBpmnForm setFormId(Long formId) {
        this.formId = formId;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActAljoinBpmnForm setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getElementType() {
        return elementType;
    }

    public ActAljoinBpmnForm setElementType(String elementType) {
        this.elementType = elementType;
        return this;
    }

    public String getElementId() {
        return elementId;
    }

    public ActAljoinBpmnForm setElementId(String elementId) {
        this.elementId = elementId;
        return this;
    }

    public String getProcessId() {
        return processId;
    }

    public ActAljoinBpmnForm setProcessId(String processId) {
        this.processId = processId;
        return this;
    }

}
