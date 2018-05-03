package aljoin.ioa.dao.object;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class ActRegulationListVO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private String fullName;
    private String weeks;
    private String time1;
    private String time2;
    private String deptId;
    private int orderByNo;
    private int orderBySTH;
    private int orderByPSTH;
    private int orderByTotalTime;
    private int orderByPTotalTime;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
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

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public int getOrderByNo() {
        return orderByNo;
    }

    public void setOrderByNo(int orderByNo) {
        this.orderByNo = orderByNo;
    }

    public int getOrderBySTH() {
        return orderBySTH;
    }

    public void setOrderBySTH(int orderBySTH) {
        this.orderBySTH = orderBySTH;
    }

    public int getOrderByPSTH() {
        return orderByPSTH;
    }

    public void setOrderByPSTH(int orderByPSTH) {
        this.orderByPSTH = orderByPSTH;
    }

    public int getOrderByTotalTime() {
        return orderByTotalTime;
    }

    public void setOrderByTotalTime(int orderByTotalTime) {
        this.orderByTotalTime = orderByTotalTime;
    }

    public int getOrderByPTotalTime() {
        return orderByPTotalTime;
    }

    public void setOrderByPTotalTime(int orderByPTotalTime) {
        this.orderByPTotalTime = orderByPTotalTime;
    }

}
