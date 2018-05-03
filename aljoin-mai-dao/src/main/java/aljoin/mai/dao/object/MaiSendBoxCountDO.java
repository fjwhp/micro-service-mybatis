package aljoin.mai.dao.object;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 发件箱表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-20
 */
public class MaiSendBoxCountDO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    /**
     * 序号
     */
    private Integer no;
    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 用户名
     */
    private String fullName;

    /**
     * 所属岗位
     */
    private String deptName;

    /**
     * 所属岗位
     */
    private String positionName;

    /**
     * 发件数量
     */
    private Integer sendNnumber;

    /**
     * 收件人数
     */
    private Integer receviceNumber;
    /**
     * 总附件
     */
    private Integer totalSize;
    /**
     * 附件转
     */
    private BigDecimal mtotalSize;
    /**
     * 显示时间
     */
    private String showDate;;

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public BigDecimal getMtotalSize() {
        if (this.totalSize > 0) {
            this.mtotalSize = new BigDecimal(this.totalSize);
            this.mtotalSize = mtotalSize.divide(new BigDecimal(1024), 2, BigDecimal.ROUND_FLOOR);
        } else {
            this.mtotalSize = new BigDecimal(0);
        }
        return mtotalSize;
    }

    public void setMtotalSize(BigDecimal mtotalSize) {
        this.mtotalSize = mtotalSize;
    }

    /**
     * 发送时间
     */
    private String sendTime;

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

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public Integer getSendNnumber() {
        return sendNnumber;
    }

    public void setSendNnumber(Integer sendNnumber) {
        this.sendNnumber = sendNnumber;
    }

    public Integer getReceviceNumber() {
        return receviceNumber;
    }

    public void setReceviceNumber(Integer receviceNumber) {
        this.receviceNumber = receviceNumber;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
