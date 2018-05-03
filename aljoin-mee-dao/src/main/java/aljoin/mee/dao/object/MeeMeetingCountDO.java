package aljoin.mee.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 内部会议表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public class MeeMeetingCountDO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 序号
     */
    private Integer no;
    /**
     * 用户名
     */
    private String fullName;
    /**
     * 所属机构
     */
    private String deptName;
    /**
     * 内部会议
     */
    private Integer inSide;
    /**
     * 外部会议
     */
    private Integer outSide;

    /**
     * 岗位名称
     */
    private String positionNames;

    private String showDate;

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getPositionNames() {
        return positionNames;
    }

    public void setPositionNames(String positionNames) {
        this.positionNames = positionNames;
    }

    private String createTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Integer getInSide() {
        return inSide;
    }

    public void setInSide(Integer inSide) {
        this.inSide = inSide;
    }

    public Integer getOutSide() {
        return outSide;
    }

    public void setOutSide(Integer outSide) {
        this.outSide = outSide;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
