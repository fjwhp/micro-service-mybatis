package aljoin.aut.dao.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * 
 * 部门App(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-11-02
 */
public class AppAutDepartment implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;
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
     * 父级部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    /**
     * 父级部门编号
     */
    private String parentCode;

    /**
     * 部门(同级)排序
     */
    private Integer deptRank;

    public String getDeptName() {
        return deptName;
    }

    public AppAutDepartment setDeptName(String deptName) {
        this.deptName = deptName;
        return this;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public AppAutDepartment setDeptCode(String deptCode) {
        this.deptCode = deptCode;
        return this;
    }

    public Integer getDeptLevel() {
        return deptLevel;
    }

    public AppAutDepartment setDeptLevel(Integer deptLevel) {
        this.deptLevel = deptLevel;
        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public AppAutDepartment setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getParentCode() {
        return parentCode;
    }

    public AppAutDepartment setParentCode(String parentCode) {
        this.parentCode = parentCode;
        return this;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Integer getDeptRank() {
        return deptRank;
    }

    public void setDeptRank(Integer deptRank) {
        this.deptRank = deptRank;
    }
}
