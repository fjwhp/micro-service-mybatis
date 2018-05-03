package aljoin.aut.dao.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import aljoin.dao.entity.Entity;

/**
 * 
 * 控件-角色表(实体类).
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:03:04
 */
public class AutWidgetRole extends Entity<AutWidgetRole> {

    private static final long serialVersionUID = 1L;

    /**
     * 控件编码
     */
    private String widgetCode;
    /**
     * 控件id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long widgetId;
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
     * 角色名称(唯一)
     */
    private String roleName;
    /**
     * 是否激活
     */
    private Integer isActive;

    public String getWidgetCode() {
        return widgetCode;
    }

    public AutWidgetRole setWidgetCode(String widgetCode) {
        this.widgetCode = widgetCode;
        return this;
    }

    public Long getWidgetId() {
        return widgetId;
    }

    public AutWidgetRole setWidgetId(Long widgetId) {
        this.widgetId = widgetId;
        return this;
    }

    public Long getRoleId() {
        return roleId;
    }

    public AutWidgetRole setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public AutWidgetRole setRoleCode(String roleCode) {
        this.roleCode = roleCode;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public AutWidgetRole setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutWidgetRole setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
