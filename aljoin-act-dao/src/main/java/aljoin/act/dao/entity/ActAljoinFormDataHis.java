package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 历史表单数据表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-10-12
 */
public class ActAljoinFormDataHis extends Entity<ActAljoinFormDataHis> {

    private static final long serialVersionUID = 1L;

    /**
     * 流程ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bpmnId;
    /**
     * 操作用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long operateUserId;
    /**
     * 流程任务ID
     */
    private String procTaskId;
    /**
     * 流程实例ID
     */
    private String procInstId;
    /**
     * 流程定义ID
     */
    private String procDefId;
    /**
     * 归属表单ID（运行时）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long formId;
    /**
     * 控件主键ID（运行时）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long widgetId;
    /**
     * 表单控件ID（运行时）
     */
    private String formWidgetId;
    /**
     * 表单控件名称（运行时）
     */
    private String formWidgetName;
    /**
     * 控件值
     */
    private String formWidgetValue;
    /**
     * 是否读取数据源
     */
    private Integer isRead;
    /**
     * 数据源
     */
    private String dataResource;

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public String getDataResource() {
        return dataResource;
    }

    public void setDataResource(String dataResource) {
        this.dataResource = dataResource;
    }

    public Long getBpmnId() {
        return bpmnId;
    }

    public ActAljoinFormDataHis setBpmnId(Long bpmnId) {
        this.bpmnId = bpmnId;
        return this;
    }

    public Long getOperateUserId() {
        return operateUserId;
    }

    public ActAljoinFormDataHis setOperateUserId(Long operateUserId) {
        this.operateUserId = operateUserId;
        return this;
    }

    public String getProcTaskId() {
        return procTaskId;
    }

    public ActAljoinFormDataHis setProcTaskId(String procTaskId) {
        this.procTaskId = procTaskId;
        return this;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public ActAljoinFormDataHis setProcInstId(String procInstId) {
        this.procInstId = procInstId;
        return this;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public ActAljoinFormDataHis setProcDefId(String procDefId) {
        this.procDefId = procDefId;
        return this;
    }

    public Long getFormId() {
        return formId;
    }

    public ActAljoinFormDataHis setFormId(Long formId) {
        this.formId = formId;
        return this;
    }

    public Long getWidgetId() {
        return widgetId;
    }

    public ActAljoinFormDataHis setWidgetId(Long widgetId) {
        this.widgetId = widgetId;
        return this;
    }

    public String getFormWidgetId() {
        return formWidgetId;
    }

    public ActAljoinFormDataHis setFormWidgetId(String formWidgetId) {
        this.formWidgetId = formWidgetId;
        return this;
    }

    public String getFormWidgetName() {
        return formWidgetName;
    }

    public ActAljoinFormDataHis setFormWidgetName(String formWidgetName) {
        this.formWidgetName = formWidgetName;
        return this;
    }

    public String getFormWidgetValue() {
        if (formWidgetValue == null) {
            formWidgetValue = "";
        }
        return formWidgetValue;
    }

    public ActAljoinFormDataHis setFormWidgetValue(String formWidgetValue) {
        this.formWidgetValue = formWidgetValue;
        return this;
    }

}
