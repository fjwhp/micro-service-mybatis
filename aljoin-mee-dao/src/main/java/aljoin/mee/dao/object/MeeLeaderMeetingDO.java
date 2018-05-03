package aljoin.mee.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class MeeLeaderMeetingDO {
    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String fullName;

    /**
     * 次数
     */
    private Integer useCount;
    /**
     * 内部会议次数
     */
    private Integer insideCount;
    /**
     * 外部会议次数次数
     */
    private Integer ousideCount;

    private String userDepart;

    private Integer no;

    private String thisWeek;

    private String thisMonth;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Integer getOusideCount() {
        return ousideCount;
    }

    public void setOusideCount(Integer ousideCount) {
        this.ousideCount = ousideCount;
    }

    public String getUserDepart() {
        return userDepart;
    }

    public void setUserDepart(String userDepart) {
        this.userDepart = userDepart;
    }

    public Integer getInsideCount() {
        return insideCount;
    }

    public void setInsideCount(Integer insideCount) {
        this.insideCount = insideCount;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
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
}
