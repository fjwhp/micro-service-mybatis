package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * (实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-08-15
 */
public class AutDepartment extends Entity<AutDepartment> {

    private static final long serialVersionUID = 1L;

    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 部门编号(唯一)
     */
    private String deptCode;
    /**
     * 部门级别
     */
    private Integer deptLevel;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 部门(同级)排序
     */
    private Integer deptRank;
    /**
     * 父级部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    /**
     * 父级部门编号
     */
    private String parentCode;

    public String getDeptName() {
        return deptName;
    }

    public AutDepartment setDeptName(String deptName) {
        this.deptName = deptName;
        return this;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public AutDepartment setDeptCode(String deptCode) {
        this.deptCode = deptCode;
        return this;
    }

    public Integer getDeptLevel() {
        return deptLevel;
    }

    public AutDepartment setDeptLevel(Integer deptLevel) {
        this.deptLevel = deptLevel;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutDepartment setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getDeptRank() {
        return deptRank;
    }

    public AutDepartment setDeptRank(Integer deptRank) {
        this.deptRank = deptRank;
        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public AutDepartment setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getParentCode() {
        return parentCode;
    }

    public AutDepartment setParentCode(String parentCode) {
        this.parentCode = parentCode;
        return this;
    }

}
