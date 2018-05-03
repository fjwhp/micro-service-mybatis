package aljoin.aut.dao.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import aljoin.dao.entity.Entity;

/**
 * 
 * 用户-角色关联表(实体类).
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:02:46
 */
public class AutUserRole extends Entity<AutUserRole> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 角色ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long roleId;
    /**
     * 角色代码(唯一)
     */
    private String roleCode;
    /**
     * 用户名(唯一)
     */
    private String userName;
    /**
     * 角色名称(唯一)
     */
    private String roleName;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 用户角色排序
     */
    private Integer userRoleRank;

    public Long getUserId() {
        return userId;
    }

    public AutUserRole setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getRoleId() {
        return roleId;
    }

    public AutUserRole setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public AutUserRole setRoleCode(String roleCode) {
        this.roleCode = roleCode;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public AutUserRole setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public AutUserRole setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutUserRole setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getUserRoleRank() {
        return userRoleRank;
    }

    public void setUserRoleRank(Integer userRoleRank) {
        this.userRoleRank = userRoleRank;
    }
}
