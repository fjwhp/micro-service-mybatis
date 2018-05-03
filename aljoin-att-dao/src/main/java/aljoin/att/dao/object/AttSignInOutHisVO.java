package aljoin.att.dao.object;

import aljoin.att.dao.entity.AttSignInOut;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * 
 * 签到、退表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-27
 */
public class AttSignInOutHisVO extends AttSignInOut {

    private static final long serialVersionUID = 1L;

    /**
     * 本周
     */
    @ApiModelProperty(value = "创建用户账号", required = false, hidden = true)
    private String thisWeek;

    /**
     * 本月
     */
    @ApiModelProperty(hidden = true)
    private String thisMonth;

    /**
     * 上周
     */
    @ApiModelProperty(value = "创建用户账号", required = false, hidden = true)
    private String lastWeek;

    /**
     * 下周
     */
    @ApiModelProperty(value = "创建用户账号", required = false, hidden = true)
    private String nextWeek;

    /**
     * 用户ID(多个分号分隔)
     */
    @ApiModelProperty(hidden = true)
    private String userIds;

    /**
     * 开始月份
     */
    @ApiModelProperty(hidden = true)
    private String monthBeg;

    /**
     * 结束月份
     */
    @ApiModelProperty(hidden = true)
    private String monthEnd;

    /**
     * 上月
     */
    @ApiModelProperty(hidden = true)
    private String lastMonth;

    /**
     * 下月
     */
    @ApiModelProperty(hidden = true)
    private String nextMonth;

    /**
     * 实体类List
     */
    @ApiModelProperty(value = "创建用户账号", required = false, hidden = true)
    private List<AttSignInOut> attSignInOutList;

    /**
     * 存放表头List
     */
    @ApiModelProperty(value = "创建用户账号", required = false, hidden = true)
    private List<String> theadList;

    @ApiModelProperty(value = "", required = false, hidden = true)
    List<Map<String, Map<String, Object>>> tdMaps;
    @ApiModelProperty(hidden = true)
    List<Map<String, Object>> tdMap;

    /**
     * 用户ID(多个分号分隔)
     */
    @ApiModelProperty(hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    /**
     * 主键ID(多个分号分隔)
     */
    @ApiModelProperty(hidden = true)
    private String ids;

    /**
     * 补签状态(上午签到补签 上午签退补签 上午签到补签 下午签退补签)
     */
    @ApiModelProperty(hidden = true)
    private String patchStatus;

    /**
     * 签到日期
     */
    @ApiModelProperty(hidden = true)
    private String signDateStr;

    /**
     * 原因
     */
    @ApiModelProperty(hidden = true)
    private String patchReason;

    /**
     * 补签日期
     */
    @ApiModelProperty(hidden = true)
    private String signPatchDate;

    @ApiModelProperty(hidden = true)
    private String taskId;

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

    public String getLastWeek() {
        return lastWeek;
    }

    public void setLastWeek(String lastWeek) {
        this.lastWeek = lastWeek;
    }

    public String getNextWeek() {
        return nextWeek;
    }

    public void setNextWeek(String nextWeek) {
        this.nextWeek = nextWeek;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public List<AttSignInOut> getAttSignInOutList() {
        return attSignInOutList;
    }

    public void setAttSignInOutList(List<AttSignInOut> attSignInOutList) {
        this.attSignInOutList = attSignInOutList;
    }

    public String getMonthBeg() {
        return monthBeg;
    }

    public void setMonthBeg(String monthBeg) {
        this.monthBeg = monthBeg;
    }

    public String getMonthEnd() {
        return monthEnd;
    }

    public void setMonthEnd(String monthEnd) {
        this.monthEnd = monthEnd;
    }

    public List<String> getTheadList() {
        return theadList;
    }

    public void setTheadList(List<String> theadList) {
        this.theadList = theadList;
    }

    public List<Map<String, Map<String, Object>>> getTdMaps() {
        return tdMaps;
    }

    public void setTdMaps(List<Map<String, Map<String, Object>>> tdMaps) {
        this.tdMaps = tdMaps;
    }

    public String getLastMonth() {
        return lastMonth;
    }

    public void setLastMonth(String lastMonth) {
        this.lastMonth = lastMonth;
    }

    public String getNextMonth() {
        return nextMonth;
    }

    public void setNextMonth(String nextMonth) {
        this.nextMonth = nextMonth;
    }

    public List<Map<String, Object>> getTdMap() {
        return tdMap;
    }

    public void setTdMap(List<Map<String, Object>> tdMap) {
        this.tdMap = tdMap;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getSignDateStr() {
        return signDateStr;
    }

    public void setSignDateStr(String signDateStr) {
        this.signDateStr = signDateStr;
    }

    public String getPatchStatus() {
        return patchStatus;
    }

    public void setPatchStatus(String patchStatus) {
        this.patchStatus = patchStatus;
    }

    public String getPatchReason() {
        return patchReason;
    }

    public void setPatchReason(String patchReason) {
        this.patchReason = patchReason;
    }

    public String getSignPatchDate() {
        return signPatchDate;
    }

    public void setSignPatchDate(String signPatchDate) {
        this.signPatchDate = signPatchDate;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
