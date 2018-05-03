package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 表单控属性件表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-08-31
 */
public class ActAljoinFormAttribute extends Entity<ActAljoinFormAttribute> {

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

    public ActAljoinFormAttribute setWidgetId(Long widgetId) {
        this.widgetId = widgetId;
        return this;
    }

    public String getAttrName() {
        return attrName;
    }

    public ActAljoinFormAttribute setAttrName(String attrName) {
        this.attrName = attrName;
        return this;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public ActAljoinFormAttribute setAttrValue(String attrValue) {
        this.attrValue = attrValue;
        return this;
    }

    public String getAttrDesc() {
        return attrDesc;
    }

    public ActAljoinFormAttribute setAttrDesc(String attrDesc) {
        this.attrDesc = attrDesc;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActAljoinFormAttribute setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
