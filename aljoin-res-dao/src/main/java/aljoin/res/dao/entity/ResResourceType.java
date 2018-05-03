package aljoin.res.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 资源分类表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-09-05
 */
public class ResResourceType extends Entity<ResResourceType> {

    private static final long serialVersionUID = 1L;

    /**
     * 父级分类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    /**
     * 资源分类名称(唯一)
     */
    private String typeName;
    /**
     * 分类级别
     */
    private Integer typeLevel;
    /**
     * 分类排序
     */
    private Integer typeRank;
    /**
     * 是否激活
     */
    private Integer isActive;

    public Long getParentId() {
        return parentId;
    }

    public ResResourceType setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getTypeName() {
        return typeName;
    }

    public ResResourceType setTypeName(String typeName) {
        this.typeName = typeName;
        return this;
    }

    public Integer getTypeLevel() {
        return typeLevel;
    }

    public ResResourceType setTypeLevel(Integer typeLevel) {
        this.typeLevel = typeLevel;
        return this;
    }

    public Integer getTypeRank() {
        return typeRank;
    }

    public ResResourceType setTypeRank(Integer typeRank) {
        this.typeRank = typeRank;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ResResourceType setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
