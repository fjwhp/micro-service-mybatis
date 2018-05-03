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
public class MeeMeetingCountVO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;
    /**
     * 用户名
     */
    private String fullName;
    /**
     * 统计开始时间
     */
    private String begTime;
    /**
     * 统计结束时间
     */
    private String endTime;
    /**
     * 内部会议排序（1：降序 其它：升序）
     */
    private String inSort;
    /**
     * 外部会议排序（1：降序 其它：升序）
     */
    private String outSort;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getBegTime() {
        return begTime;
    }

    public void setBegTime(String begTime) {
        this.begTime = begTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getInSort() {
        return inSort;
    }

    public void setInSort(String inSort) {
        this.inSort = inSort;
    }

    public String getOutSort() {
        return outSort;
    }

    public void setOutSort(String outSort) {
        this.outSort = outSort;
    }
}
