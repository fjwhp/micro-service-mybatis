package aljoin.act.dao.object;

/**
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
public class ActHolidayVO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private String no;
    private String startTimeholi;
    private String endTimeholi;
    private String weeks;
    private String deptId;
    /**
     * 事假
     */
    private String oderByThings;
    /**
     * 病假
     */
    private String oderByDisease;
    /**
     * 年假
     */
    private String oderByYear;
    /**
     * 婚假
     */
    private String oderByMarriage;
    /**
     * 产假
     */
    private String oderByMaternity;
    /**
     * 公假
     */
    private String oderByAllocated;
    /**
     * 丧假
     */
    private String orderByDie;
    /**
     * 丧假
     */
    private String orderByOther;

    public String getOrderByOther() {
        return orderByOther;
    }

    public void setOrderByOther(String orderByOther) {
        this.orderByOther = orderByOther;
    }

    public String getOderByThings() {
        return oderByThings;
    }

    public void setOderByThings(String oderByThings) {
        this.oderByThings = oderByThings;
    }

    public String getOderByDisease() {
        return oderByDisease;
    }

    public void setOderByDisease(String oderByDisease) {
        this.oderByDisease = oderByDisease;
    }

    public String getOderByYear() {
        return oderByYear;
    }

    public void setOderByYear(String oderByYear) {
        this.oderByYear = oderByYear;
    }

    public String getOderByMarriage() {
        return oderByMarriage;
    }

    public void setOderByMarriage(String oderByMarriage) {
        this.oderByMarriage = oderByMarriage;
    }

    public String getOderByMaternity() {
        return oderByMaternity;
    }

    public void setOderByMaternity(String oderByMaternity) {
        this.oderByMaternity = oderByMaternity;
    }

    public String getOderByAllocated() {
        return oderByAllocated;
    }

    public void setOderByAllocated(String oderByAllocated) {
        this.oderByAllocated = oderByAllocated;
    }

    public String getOrderByDie() {
        return orderByDie;
    }

    public void setOrderByDie(String orderByDie) {
        this.orderByDie = orderByDie;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getStartTimeholi() {
        return startTimeholi;
    }

    public void setStartTimeholi(String startTimeholi) {
        this.startTimeholi = startTimeholi;
    }

    public String getEndTimeholi() {
        return endTimeholi;
    }

    public void setEndTimeholi(String endTimeholi) {
        this.endTimeholi = endTimeholi;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    @Override
    public String toString() {
        return "ActHolidayVO [no=" + no + ", startTimeholi=" + startTimeholi + ", endTimeholi=" + endTimeholi
            + ", weeks=" + weeks + ", deptId=" + deptId + ", oderByThings=" + oderByThings + ", oderByDisease="
            + oderByDisease + ", oderByYear=" + oderByYear + ", oderByMarriage=" + oderByMarriage + ", oderByMaternity="
            + oderByMaternity + ", oderByAllocated=" + oderByAllocated + ", orderByDie=" + orderByDie
            + ", orderByOther=" + orderByOther + "]";
    }

}
