package aljoin.att.dao.object;

/**
 * 
 * 签到统计(实体类).
 *
 * @author：laijy
 * 
 * @date：2017年9月27日 下午8:12:26
 */
public class AttSignInCount {
    private String no;
    private String orderByPatch;

    public String getOrderByPatch() {
        return orderByPatch;
    }

    public void setOrderByPatch(String orderByPatch) {
        this.orderByPatch = orderByPatch;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    // 处室
    private String department;
    // 用户名
    private String signUserName;
    // 用户id
    private Long signUserId;
    // 未签到次数
    private int noneSignInNums;
    // 未签退次数
    private int noneSignOutNums;
    // 迟到次数
    private int lateNums;
    // 早退次数
    private int leaveEarlyNums;
    // 补签次数
    private int signPatchNums;
    // 本周，值-true时，签到统计的查询结果为本周
    private String thisWeek;
    // 本月，值-true时，签到统计的查询结果为本月
    private String thisMonth;
    // 统计时间1:
    private String time1;
    // 统计时间2
    private String time2;
    // 值-ture:根据未签到次数降序
    private String orderByNoneSignIn;
    // 值-ture:根据未签退次数降序
    private String orderByNoneSignOut;
    // 值-ture:根据迟到次数降序
    private String orderByLate;
    // 值-ture:根据早退次数降序
    private String orderByLeaveEarly;
    // 要查询签到信息的部门Id
    private Long deptId;
    // 要查询签到信息的部门名称
    private String deptName;
    private String showDate;

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSignUserName() {
        return signUserName;
    }

    public void setSignUserName(String signUserName) {
        this.signUserName = signUserName;
    }

    public Long getSignUserId() {
        return signUserId;
    }

    public void setSignUserId(Long signUserId) {
        this.signUserId = signUserId;
    }

    public int getNoneSignInNums() {
        return noneSignInNums;
    }

    public void setNoneSignInNums(int noneSignInNums) {
        this.noneSignInNums = noneSignInNums;
    }

    public int getNoneSignOutNums() {
        return noneSignOutNums;
    }

    public void setNoneSignOutNums(int noneSignOutNums) {
        this.noneSignOutNums = noneSignOutNums;
    }

    public int getLateNums() {
        return lateNums;
    }

    public void setLateNums(int lateNums) {
        this.lateNums = lateNums;
    }

    public int getLeaveEarlyNums() {
        return leaveEarlyNums;
    }

    public void setLeaveEarlyNums(int leaveEarlyNums) {
        this.leaveEarlyNums = leaveEarlyNums;
    }

    public String getThisWeek() {
        return thisWeek;
    }

    public void setThisWeek(String thisWeek) {
        this.thisWeek = thisWeek;
    }

    public String getThisMonth() {
        return thisMonth;
    }

    public void setThisMonth(String thisMonth) {
        this.thisMonth = thisMonth;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public String getOrderByNoneSignIn() {
        return orderByNoneSignIn;
    }

    public void setOrderByNoneSignIn(String orderByNoneSignIn) {
        this.orderByNoneSignIn = orderByNoneSignIn;
    }

    public String getOrderByNoneSignOut() {
        return orderByNoneSignOut;
    }

    public void setOrderByNoneSignOut(String orderByNoneSignOut) {
        this.orderByNoneSignOut = orderByNoneSignOut;
    }

    public String getOrderByLate() {
        return orderByLate;
    }

    public void setOrderByLate(String orderByLate) {
        this.orderByLate = orderByLate;
    }

    public String getOrderByLeaveEarly() {
        return orderByLeaveEarly;
    }

    public void setOrderByLeaveEarly(String orderByLeaveEarly) {
        this.orderByLeaveEarly = orderByLeaveEarly;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public int getSignPatchNums() {
        return signPatchNums;
    }

    public void setSignPatchNums(int signPatchNums) {
        this.signPatchNums = signPatchNums;
    }

}
