package aljoin.act.dao.object;

/**
 * 
 * 简单部门对象
 *
 * @author：zhongjy
 * 
 * @date：2017年11月27日 下午9:33:34
 */
public class SimpleUserVO {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 部门ID（非选择部门产生的用户的部门ID）
     */
    private String deptId;
    /**
     * 是否流程发起人
     */
    private String isCreater;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getIsCreater() {
        return isCreater;
    }

    public void setIsCreater(String isCreater) {
        this.isCreater = isCreater;
    }

    @Override
    public String toString() {
        return "SimpleUserVO [userId=" + userId + ", userName=" + userName + ", deptId=" + deptId + ", isCreater="
            + isCreater + "]";
    }

}
