package aljoin.act.dao.object;

/**
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
public class ActHolidayListVO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private String no;
    private String userName;
    private String jobsName;
    /**
     * 事假
     */
    private Double thingsLeave;
    /**
     * 病假
     */
    private Double diseaseLeave;
    /**
     * 年假
     */
    private Double annualLeave;
    /**
     * 婚假
     */
    private Double marriageLeave;
    /**
     * 产假
     */
    private Double maternityLeave;
    /**
     * 公假
     */
    private Double allocatedLeave;
    /**
     * 丧假
     */
    private Double dieLeave;
    /**
     * 其他假
     */
    private Double otherLeave;
    private String showDate;

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public Double getOtherLeave() {
        if (this.otherLeave == null) {
            this.otherLeave = new Double(0);
        }
        return otherLeave;
    }

    public void setOtherLeave(Double otherLeave) {
        this.otherLeave = otherLeave;
    }

    public Double getThingsLeave() {
        return thingsLeave;
    }

    public void setThingsLeave(Double thingsLeave) {
        this.thingsLeave = thingsLeave;
    }

    public Double getDiseaseLeave() {
        return diseaseLeave;
    }

    public void setDiseaseLeave(Double diseaseLeave) {
        this.diseaseLeave = diseaseLeave;
    }

    public Double getAnnualLeave() {
        return annualLeave;
    }

    public void setAnnualLeave(Double annualLeave) {
        this.annualLeave = annualLeave;
    }

    public Double getMarriageLeave() {
        return marriageLeave;
    }

    public void setMarriageLeave(Double marriageLeave) {
        this.marriageLeave = marriageLeave;
    }

    public Double getMaternityLeave() {
        return maternityLeave;
    }

    public void setMaternityLeave(Double maternityLeave) {
        this.maternityLeave = maternityLeave;
    }

    public Double getAllocatedLeave() {
        return allocatedLeave;
    }

    public void setAllocatedLeave(Double allocatedLeave) {
        this.allocatedLeave = allocatedLeave;
    }

    public Double getDieLeave() {
        return dieLeave;
    }

    public void setDieLeave(Double dieLeave) {
        this.dieLeave = dieLeave;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJobsName() {
        if (jobsName == null) {
            jobsName = "";
        }
        return jobsName;
    }

    public void setJobsName(String jobsName) {
        this.jobsName = jobsName;
    }

    @Override
    public String toString() {
        return "ActHolidayListVO [no=" + no + ", userName=" + userName + ", jobsName=" + jobsName + ", thingsLeave="
            + thingsLeave + ", diseaseLeave=" + diseaseLeave + ", annualLeave=" + annualLeave + ", marriageLeave="
            + marriageLeave + ", maternityLeave=" + maternityLeave + ", allocatedLeave=" + allocatedLeave
            + ", dieLeave=" + dieLeave + ", otherLeave=" + otherLeave + ", showDate=" + showDate + "]";
    }

}
