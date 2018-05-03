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
 * @date： 2017-09-04
 */
public class AutDepartmentRole extends Entity<AutDepartmentRole> {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;
    /**
     * 角色ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long roleId;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 部门编号
     */
    private String deptCode;
    /**
     * 角色编号
     */
    private String roleCode;
    private Integer departmentRoleRank;

    public Integer getDepartmentRoleRank() {
        return departmentRoleRank;
    }

    public void setDepartmentRoleRank(Integer departmentRoleRank) {
        this.departmentRoleRank = departmentRoleRank;
    }

    public Long getDeptId() {
        return deptId;
    }

    public AutDepartmentRole setDeptId(Long deptId) {
        this.deptId = deptId;
        return this;
    }

    public Long getRoleId() {
        return roleId;
    }

    public AutDepartmentRole setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutDepartmentRole setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public AutDepartmentRole setDeptCode(String deptCode) {
        this.deptCode = deptCode;
        return this;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public AutDepartmentRole setRoleCode(String roleCode) {
        this.roleCode = roleCode;
        return this;
    }

}
