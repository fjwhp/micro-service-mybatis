package aljoin.sma.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * 领导看板统计模块显示排序(实体类).
 * 
 * @author：huangw.
 * 
 * @date： 2017-12-25
 */
public class LeaStatisticsModule extends Entity<LeaStatisticsModule> {

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

    public LeaStatisticsModule setModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public LeaStatisticsModule setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
        return this;
    }

    public Integer getModuleRank() {
        return moduleRank;
    }

    public LeaStatisticsModule setModuleRank(Integer moduleRank) {
        this.moduleRank = moduleRank;
        return this;
    }

    public Integer getIsHide() {
        return isHide;
    }

    public LeaStatisticsModule setIsHide(Integer isHide) {
        this.isHide = isHide;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public LeaStatisticsModule setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
