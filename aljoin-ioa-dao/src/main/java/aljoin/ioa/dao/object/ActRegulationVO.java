package aljoin.ioa.dao.object;

import java.math.BigDecimal;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class ActRegulationVO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private int no;
    private String fullName;
    private String jobsName;
    private int sumNo;
    private int sthNo;
    private int totalNo;
    private BigDecimal sth;
    private BigDecimal psth;
    private BigDecimal totalTime;
    private BigDecimal pTotalTime;

    private BigDecimal returnsth;
    private BigDecimal returntotalTime;
    private String showDate;

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public int getSthNo() {
        return sthNo;
    }

    public void setSthNo(int sthNo) {
        this.sthNo = sthNo;
    }

    public int getTotalNo() {
        return totalNo;
    }

    public void setTotalNo(int totalNo) {
        this.totalNo = totalNo;
        if (this.totalNo > 0) {
            this.pTotalTime = this.returntotalTime.divide(new BigDecimal(this.totalNo), 2, BigDecimal.ROUND_FLOOR);
        } else {
            this.pTotalTime = new BigDecimal(0);
        }
    }

    public BigDecimal getReturnsth() {
        return returnsth;
    }

    public BigDecimal getReturntotalTime() {
        return returntotalTime;
    }

    public int getSumNo() {
        return sumNo;
    }

    public void setSumNo(int sumNo) {
        this.sumNo = sumNo;
        if (this.sumNo > 0) {
            this.psth = this.returnsth.divide(new BigDecimal(this.sumNo), 2, BigDecimal.ROUND_FLOOR);
        } else {
            this.psth = new BigDecimal(0);
        }
    }

    public BigDecimal getSth() {
        if (sth == null) {
            sth = new BigDecimal(0);
        }
        return sth;
    }

    public void setSth(BigDecimal sth) {
        this.sth = sth;
        this.returnsth = this.sth.divide(new BigDecimal(60000), 2, BigDecimal.ROUND_FLOOR);
    }

    public BigDecimal getPsth() {
        return psth;
    }

    public void setPsth(BigDecimal psth) {
        this.psth = psth;
    }

    public BigDecimal getTotalTime() {
        if (totalTime == null) {
            totalTime = new BigDecimal(0);
        }
        return totalTime;
    }

    public void setTotalTime(BigDecimal totalTime) {
        this.totalTime = totalTime;
        this.returntotalTime = this.totalTime.divide(new BigDecimal(60000), 2, BigDecimal.ROUND_FLOOR);
    }

    public BigDecimal getpTotalTime() {

        return pTotalTime;
    }

    public void setpTotalTime(BigDecimal pTotalTime) {
        this.pTotalTime = pTotalTime;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

}
