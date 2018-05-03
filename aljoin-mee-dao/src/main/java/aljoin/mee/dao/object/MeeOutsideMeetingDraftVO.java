package aljoin.mee.dao.object;


import aljoin.mee.dao.entity.MeeOutsideMeetingDraft;

/**
 * 
 * 外部会议表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public class MeeOutsideMeetingDraftVO extends MeeOutsideMeetingDraft {

    private static final long serialVersionUID = 1L;

    /**
     * 本周
     */
    private String thisWeek;

    /**
     * 本月
     */
    private String thisMonth;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;


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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
