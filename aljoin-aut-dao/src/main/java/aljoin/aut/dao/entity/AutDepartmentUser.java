package aljoin.aut.dao.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import aljoin.dao.entity.Entity;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * (实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-08-21
 */
public class AutDepartmentUser extends Entity<AutDepartmentUser> {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(hidden = true)
    private Long deptId;
    /**
     * 部门编号
     */
    @ApiModelProperty(hidden = true)
    private String deptCode;
    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(hidden = true)
    private Long userId;
    /**
     * 用户名
     */
    @ApiModelProperty(hidden = true)
    private String userName;
    /**
     * 是否激活
     */
    @ApiModelProperty(hidden = true)
    private Integer isActive;

    /**
     * 部门用户的排序
     */
    @ApiModelProperty(hidden = true)
    private Integer departmentUserRank;

    /**
     * 是否本部门领导
     */
    @ApiModelProperty(hidden = true)
    private Integer isLeader;

    public Integer getDepartmentUserRank() {
        return departmentUserRank;
    }

    public void setDepartmentUserRank(Integer departmentUserRank) {
        this.departmentUserRank = departmentUserRank;
    }

    public Long getDeptId() {
        return deptId;
    }

    public AutDepartmentUser setDeptId(Long deptId) {
        this.deptId = deptId;
        return this;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public AutDepartmentUser setDeptCode(String deptCode) {
        this.deptCode = deptCode;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public AutDepartmentUser setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public AutDepartmentUser setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutDepartmentUser setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getIsLeader() {
        return isLeader;
    }

    public void setIsLeader(Integer isLeader) {
        this.isLeader = isLeader;
    }

}
