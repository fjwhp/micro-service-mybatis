package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * 流程-用户关系表(实体类).
 * 
 * @author：pengsp.
 * 
 * @date： 2017-10-12
 */
public class ActAljoinBpmnUser extends Entity<ActAljoinBpmnUser> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 流程ID
     */
    private Long bpmnId;
    /**
     * 是否激活
     */
    private Integer isActive;

    /**
     * 
     */
    private Integer authType;

    public Integer getAuthType() {
        return authType;
    }

    public void setAuthType(Integer authType) {
        this.authType = authType;
    }

    public Long getUserId() {
        return userId;
    }

    public ActAljoinBpmnUser setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getBpmnId() {
        return bpmnId;
    }

    public ActAljoinBpmnUser setBpmnId(Long bpmnId) {
        this.bpmnId = bpmnId;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActAljoinBpmnUser setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
