package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * 数据统计表(实体类).
 * 
 * @author：sln.
 * 
 * @date： 2017-11-09
 */
public class AutDataStatistics extends Entity<AutDataStatistics> {

    private static final long serialVersionUID = 1L;

    /**
     * 对象编码
     */
    private String objectKey;
    /**
     * 对象名称
     */
    private String objectName;
    /**
     * 对象数量
     */
    private Integer objectCount;
    /**
     * 业务主键，通过本字段解析点击消息需要进行的操作
     */
    private String businessKey;
    /**
     * 是否激活
     */
    private Integer isActive;

    public String getObjectKey() {
        return objectKey;
    }

    public AutDataStatistics setObjectKey(String objectKey) {
        this.objectKey = objectKey;
        return this;
    }

    public String getObjectName() {
        return objectName;
    }

    public AutDataStatistics setObjectName(String objectName) {
        this.objectName = objectName;
        return this;
    }

    public Integer getObjectCount() {
        return objectCount;
    }

    public AutDataStatistics setObjectCount(Integer objectCount) {
        this.objectCount = objectCount;
        return this;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public AutDataStatistics setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutDataStatistics setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
