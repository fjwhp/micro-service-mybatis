package aljoin.act.dao.object;

/**
 * 
 * 委托数据对象
 *
 * @author：zhongjy
 * 
 * @date：2017年11月13日 上午9:49:13
 */
public class DelegateDO {

    /**
     * 用户ID
     */
    private Long id;
    /**
     * 姓名
     */
    private String userFullName;

    private String delegateUserNames;
    private String delegateUserIds;
    private String delegateIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getDelegateUserNames() {
        return delegateUserNames;
    }

    public void setDelegateUserNames(String delegateUserNames) {
        this.delegateUserNames = delegateUserNames;
    }

    public String getDelegateUserIds() {
        return delegateUserIds;
    }

    public void setDelegateUserIds(String delegateUserIds) {
        this.delegateUserIds = delegateUserIds;
    }

    public String getDelegateIds() {
        return delegateIds;
    }

    public void setDelegateIds(String delegateIds) {
        this.delegateIds = delegateIds;
    }

    @Override
    public String toString() {
        return "DelegateDO [id=" + id + ", userFullName=" + userFullName + ", delegateUserNames=" + delegateUserNames
            + ", delegateUserIds=" + delegateUserIds + ", delegateIds=" + delegateIds + "]";
    }

}
