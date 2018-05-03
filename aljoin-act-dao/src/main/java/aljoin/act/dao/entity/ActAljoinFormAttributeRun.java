package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 表单控属性件表(运行时)(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-28
 */
public class ActAljoinFormAttributeRun extends Entity<ActAljoinFormAttributeRun> {

    private static final long serialVersionUID = 1L;

    /**
     * 控件ID(指的是主键那个ID)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long widgetId;
    /**
     * 属性名称
     */
    private String attrName;
    /**
     * 属性值
     */
    private String attrValue;
    /**
     * 属性说明
     */
    private String attrDesc;
    /**
     * 是否激活
     */
    private Integer isActive;

    public Long getWidgetId() {
        return widgetId;
    }

    public ActAljoinFormAttributeRun setWidgetId(Long widgetId) {
        this.widgetId = widgetId;
        return this;
    }

    public String getAttrName() {
        return attrName;
    }

    public ActAljoinFormAttributeRun setAttrName(String attrName) {
        this.attrName = attrName;
        return this;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public ActAljoinFormAttributeRun setAttrValue(String attrValue) {
        this.attrValue = attrValue;
        return this;
    }

    public String getAttrDesc() {
        return attrDesc;
    }

    public ActAljoinFormAttributeRun setAttrDesc(String attrDesc) {
        this.attrDesc = attrDesc;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActAljoinFormAttributeRun setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
