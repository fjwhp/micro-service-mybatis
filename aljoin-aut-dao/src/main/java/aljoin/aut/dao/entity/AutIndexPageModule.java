package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * 首页模块定制表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-12
 */
public class AutIndexPageModule extends Entity<AutIndexPageModule> {

    private static final long serialVersionUID = 1L;

    /**
     * 模块名称
     */
    private String moduleName;
    /**
     * 模块编码
     */
    private String moduleCode;
    /**
     * 模块排序
     */
    private Integer moduleRank;
    /**
     * 是否隐藏
     */
    private Integer isHide;
    /**
     * 是否激活
     */
    private Integer isActive;

    public String getModuleName() {
        return moduleName;
    }

    public AutIndexPageModule setModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public AutIndexPageModule setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
        return this;
    }

    public Integer getModuleRank() {
        return moduleRank;
    }

    public AutIndexPageModule setModuleRank(Integer moduleRank) {
        this.moduleRank = moduleRank;
        return this;
    }

    public Integer getIsHide() {
        return isHide;
    }

    public AutIndexPageModule setIsHide(Integer isHide) {
        this.isHide = isHide;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutIndexPageModule setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
