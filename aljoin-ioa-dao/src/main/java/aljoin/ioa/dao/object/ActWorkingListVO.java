package aljoin.ioa.dao.object;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class ActWorkingListVO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private String time1;
    private String time2;
    private String weeks;
    private String deptId;
    private String bpmnName;
    private String isOne;
    private String oderByNew;
    private String oderByStay;
    private String oderByHandle;

    public String getOderByNew() {
        return oderByNew;
    }

    public void setOderByNew(String oderByNew) {
        this.oderByNew = oderByNew;
    }

    public String getOderByStay() {
        return oderByStay;
    }

    public void setOderByStay(String oderByStay) {
        this.oderByStay = oderByStay;
    }

    public String getOderByHandle() {
        return oderByHandle;
    }

    public void setOderByHandle(String oderByHandle) {
        this.oderByHandle = oderByHandle;
    }

    public String getIsOne() {
        return isOne;
    }

    public void setIsOne(String isOne) {
        this.isOne = isOne;
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

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public String getBpmnName() {
        return bpmnName;
    }

    public void setBpmnName(String bpmnName) {
        this.bpmnName = bpmnName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }
}
