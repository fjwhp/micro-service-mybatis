package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 表单控件表(运行时)(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-28
 */
public class ActAljoinFormWidgetRun extends Entity<ActAljoinFormWidgetRun> {

    private static final long serialVersionUID = 1L;

    /**
     * 控件类型
     */
    private String widgetType;
    /**
     * 控件ID
     */
    private String widgetId;
    /**
     * 控件NAME
     */
    private String widgetName;
    /**
     * 归属表单ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long formId;
    /**
     * 是否激活
     */
    private Integer isActive;

    public String getWidgetType() {
        return widgetType;
    }

    public ActAljoinFormWidgetRun setWidgetType(String widgetType) {
        this.widgetType = widgetType;
        return this;
    }

    public String getWidgetId() {
        return widgetId;
    }

    public ActAljoinFormWidgetRun setWidgetId(String widgetId) {
        this.widgetId = widgetId;
        return this;
    }

    public String getWidgetName() {
        return widgetName;
    }

    public ActAljoinFormWidgetRun setWidgetName(String widgetName) {
        this.widgetName = widgetName;
        return this;
    }

    public Long getFormId() {
        return formId;
    }

    public ActAljoinFormWidgetRun setFormId(Long formId) {
        this.formId = formId;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActAljoinFormWidgetRun setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
