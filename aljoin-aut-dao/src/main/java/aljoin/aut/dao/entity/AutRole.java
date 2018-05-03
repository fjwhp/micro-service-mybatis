package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * 角色表(实体类).
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:02:24
 */
public class AutRole extends Entity<AutRole> {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称(唯一)
     */
    private String roleName;
    /**
     * 角色代码(唯一)
     */
    private String roleCode;
    /**
     * 是否激活
     */
    private Integer isActive;

    /**
     * 角色排序
     */
    private Integer roleRank;

    public String getRoleName() {
        return roleName;
    }

    public AutRole setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public AutRole setRoleCode(String roleCode) {
        this.roleCode = roleCode;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutRole setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getRoleRank() {
        return roleRank;
    }

    public void setRoleRank(Integer roleRank) {
        this.roleRank = roleRank;
    }

}
