package aljoin.act.dao.object;

/**
 * 
 * 两两委托数据对象
 *
 * @author：zhongjy
 * 
 * @date：2017年11月13日 上午9:49:13
 */
public class DelegateSingleDO {

    /**
     * 委托人
     */
    private Long ownerUserId;
    /**
     * 受理人（被委托人）
     */
    private Long assigneeUserId;

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Long getAssigneeUserId() {
        return assigneeUserId;
    }

    public void setAssigneeUserId(Long assigneeUserId) {
        this.assigneeUserId = assigneeUserId;
    }

    @Override
    public String toString() {
        return "DelegateSingleDO [ownerUserId=" + ownerUserId + ", assigneeUserId=" + assigneeUserId + "]";
    }

}
