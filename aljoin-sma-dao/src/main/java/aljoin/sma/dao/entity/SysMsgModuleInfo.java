package aljoin.sma.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 消息模板信息表(实体类).
 * 
 * @author：huangw.
 * 
 * @date： 2017-11-14
 */
public class SysMsgModuleInfo extends Entity<SysMsgModuleInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 属性名称
     */
    private String attrName;
    /**
     * 属性编码
     */
    private String attrCode;
    /**
     * 属性值
     */
    private String attrValue;
    /**
     * 属性描述
     */
    private String attrDesc;
    /**
     * 模板分类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleCategoryId;
    /**
     * 是否激活
     */
    private Integer isActive;

    public String getAttrName() {
        return attrName;
    }

    public SysMsgModuleInfo setAttrName(String attrName) {
        this.attrName = attrName;
        return this;
    }

    public String getAttrCode() {
        return attrCode;
    }

    public SysMsgModuleInfo setAttrCode(String attrCode) {
        this.attrCode = attrCode;
        return this;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public SysMsgModuleInfo setAttrValue(String attrValue) {
        this.attrValue = attrValue;
        return this;
    }

    public String getAttrDesc() {
        return attrDesc;
    }

    public SysMsgModuleInfo setAttrDesc(String attrDesc) {
        this.attrDesc = attrDesc;
        return this;
    }

    public Long getModuleCategoryId() {
        return moduleCategoryId;
    }

    public SysMsgModuleInfo setModuleCategoryId(Long moduleCategoryId) {
        this.moduleCategoryId = moduleCategoryId;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public SysMsgModuleInfo setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
