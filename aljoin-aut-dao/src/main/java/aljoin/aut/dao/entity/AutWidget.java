package aljoin.aut.dao.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import aljoin.dao.entity.Entity;

/**
 * 
 * 控件表(实体类).
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:02:55
 */
public class AutWidget extends Entity<AutWidget> {

    private static final long serialVersionUID = 1L;

    /**
     * 控件编码
     */
    private String widgetCode;
    /**
     * 控件名称
     */
    private String widgetName;
    /**
     * 控件归属菜单ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long menuId;
    /**
     * 控件归属菜单编码
     */
    private String menuCode;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 控件归属父级菜单ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentMenuId;
    /**
     * 控件归属父级菜单编码
     */
    private String parentMenuCode;

    public String getWidgetCode() {
        return widgetCode;
    }

    public AutWidget setWidgetCode(String widgetCode) {
        this.widgetCode = widgetCode;
        return this;
    }

    public String getWidgetName() {
        return widgetName;
    }

    public AutWidget setWidgetName(String widgetName) {
        this.widgetName = widgetName;
        return this;
    }

    public Long getMenuId() {
        return menuId;
    }

    public AutWidget setMenuId(Long menuId) {
        this.menuId = menuId;
        return this;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public AutWidget setMenuCode(String menuCode) {
        this.menuCode = menuCode;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutWidget setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Long getParentMenuId() {
        return parentMenuId;
    }

    public AutWidget setParentMenuId(Long parentMenuId) {
        this.parentMenuId = parentMenuId;
        return this;
    }

    public String getParentMenuCode() {
        return parentMenuCode;
    }

    public AutWidget setParentMenuCode(String parentMenuCode) {
        this.parentMenuCode = parentMenuCode;
        return this;
    }

}
