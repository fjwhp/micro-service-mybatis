package aljoin.goo.dao.object;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class GooInOutDO {

    /**
     * 登录人姓名
     */
    private String userName;
    /**
     * 登录人id
     */
    private String userId;
    /**
     * 登录人部门
     */
    private String dept;
    /**
     * 登录人部门
     */
    private String deptId;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    /**
     * 当前时间
     */
    private String submitTime;

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

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

}
