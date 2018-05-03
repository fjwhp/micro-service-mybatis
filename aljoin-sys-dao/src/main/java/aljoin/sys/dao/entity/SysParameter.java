package aljoin.sys.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * 系统参数表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-10-12
 */
public class SysParameter extends Entity<SysParameter> {

    private static final long serialVersionUID = 1L;

    /**
     * 参数key
     */
    private String paramKey;
    /**
     * 参数value
     */
    private String paramValue;
    /**
     * 参数描述
     */
    private String paramDesc;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 是否显示
     */
    private Integer isShow;

    public String getParamKey() {
        return paramKey;
    }

    public SysParameter setParamKey(String paramKey) {
        this.paramKey = paramKey;
        return this;
    }

    public String getParamValue() {
        return paramValue;
    }

    public SysParameter setParamValue(String paramValue) {
        this.paramValue = paramValue;
        return this;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public SysParameter setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public SysParameter setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public SysParameter setIsShow(Integer isShow) {
        this.isShow = isShow;
        return this;
    }

}
