package aljoin.off.dao.object;

/**
 * 领导会议实体类--app端使用
 * 
 * @author sunlinan
 *
 */
public class MeetScheduleDO {

    /**
     * 领导id
     */
    private Long leaderid;
    /**
     * 月份
     */
    private String month;
    /**
     * 日期
     */
    private String date;
    /**
     * 会议类型
     */
    private String bizType;
    /**
     * 会议主键
     */
    private Long bizId;

    public Long getLeaderid() {
        return leaderid;
    }

    public void setLeaderid(Long leaderid) {
        this.leaderid = leaderid;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

}
