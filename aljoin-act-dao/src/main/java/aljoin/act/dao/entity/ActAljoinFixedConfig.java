package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * 固定流程配置表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-11-07
 */
public class ActAljoinFixedConfig extends Entity<ActAljoinFixedConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 流程代码，唯一，通过流程代码固定流程
     */
    private String processCode;
    /**
     * 流程ID
     */
    private String processId;
    /**
     * 流程名称
     */
    private String processName;
    /**
     * 是否激活
     */
    private Integer isActive;

    public String getProcessCode() {
        return processCode;
    }

    public ActAljoinFixedConfig setProcessCode(String processCode) {
        this.processCode = processCode;
        return this;
    }

    public String getProcessId() {
        return processId;
    }

    public ActAljoinFixedConfig setProcessId(String processId) {
        this.processId = processId;
        return this;
    }

    public String getProcessName() {
        return processName;
    }

    public ActAljoinFixedConfig setProcessName(String processName) {
        this.processName = processName;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActAljoinFixedConfig setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
