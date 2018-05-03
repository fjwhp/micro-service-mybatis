package aljoin.sys.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * 数据字典表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-24
 */
public class SysDataDict extends Entity<SysDataDict> {

    private static final long serialVersionUID = 1L;

    /**
     * 数据字典编码(类型)
     */
    private String dictCode;
    /**
     * 数据字典名称
     */
    private String dictName;
    /**
     * 数据字典-key
     */
    private String dictKey;
    /**
     * 数据字典-value
     */
    private String dictValue;
    /**
     * 是否激活
     */
    private Integer isActive;

    /**
     * 数据字典排序
     */
    private Integer dictRank;

    /**
     * 字典类型:0-系统数据（开发级别），1-业务数据（功能级别）
     */
    private Integer dictType;

    public String getDictCode() {
        return dictCode;
    }

    public SysDataDict setDictCode(String dictCode) {
        this.dictCode = dictCode;
        return this;
    }

    public String getDictName() {
        return dictName;
    }

    public SysDataDict setDictName(String dictName) {
        this.dictName = dictName;
        return this;
    }

    public String getDictKey() {
        return dictKey;
    }

    public SysDataDict setDictKey(String dictKey) {
        this.dictKey = dictKey;
        return this;
    }

    public String getDictValue() {
        return dictValue;
    }

    public SysDataDict setDictValue(String dictValue) {
        this.dictValue = dictValue;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public SysDataDict setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getDictRank() {
        return dictRank;
    }

    public void setDictRank(Integer dictRank) {
        this.dictRank = dictRank;
    }

    public Integer getDictType() {
        return dictType;
    }

    public void setDictType(Integer dictType) {
        this.dictType = dictType;
    }

}
