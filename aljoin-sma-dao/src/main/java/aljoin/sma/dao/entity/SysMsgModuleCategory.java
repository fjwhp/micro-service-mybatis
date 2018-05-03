package aljoin.sma.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * 消息模板分类表(实体类).
 * 
 * @author：huangw.
 * 
 * @date： 2017-11-14
 */
public class SysMsgModuleCategory extends Entity<SysMsgModuleCategory> {

    private static final long serialVersionUID = 1L;

    /**
     * 模板分类名称
     */
    private String moduleName;
    /**
     * 模板分类编码
     */
    private String moduleCode;
    /**
     * 是否激活
     */
    private Integer isActive;

    public String getModuleName() {
        return moduleName;
    }

    public SysMsgModuleCategory setModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public SysMsgModuleCategory setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public SysMsgModuleCategory setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
