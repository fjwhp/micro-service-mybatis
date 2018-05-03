package aljoin.mai.dao.object;

import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiSendBox;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 
 * 发件箱表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-20
 */
public class MaiSendBoxVO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    /**
     * 发件箱
     */
    @ApiModelProperty(hidden = true)
    private MaiSendBox maiSendBox;

    /**
     * 附件
     */
    @ApiModelProperty(hidden = true)
    private List<MaiAttachment> maiAttachmentList;
    
    /**
     * 当前登录用户ID
     */
    @ApiModelProperty(hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 当前登录用户ID
     */
    @ApiModelProperty(hidden = true)
    private String userName;

    /**
     * 当前登录用户ID
     */
    @ApiModelProperty(hidden = true)
    private String fullName;

    /**
     * 选中记录的id(用逗号分隔)
     */
    @ApiModelProperty(hidden = true)
    private String ids;

    /**
     * 开始发送时间
     */
    @ApiModelProperty(hidden = true)
    private String begSendTime;

    /**
     * 结束发送时间
     */
    @ApiModelProperty(hidden = true)
    private String endSendTime;

    /**
     * 开始统计时间
     */
    @ApiModelProperty(hidden = true)
    private String begTime;

    /**
     * 结束统计时间
     */
    @ApiModelProperty(hidden = true)
    private String endTime;
    /**
     * 部门ID
     */
    @ApiModelProperty(hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    /**
     * 本周
     */
    @ApiModelProperty(hidden = true)
    private String thisWeek;
    /**
     * aa 本月
     */
    @ApiModelProperty(hidden = true)
    private String thisMonth;

    /**
     * 部门名称
     */
    @ApiModelProperty(hidden = true)
    private String deptName;

    /**
     * 收件人总数排序(1:降序 其它：升序)
     */
    @ApiModelProperty(hidden = true)
    private String recevieSort;

    /**
     * 总附件大小排序(1:降序 其它：升序)
     */
    @ApiModelProperty(hidden = true)
    private String attachmentSort;
    /**
     * 发件数量排序(1:降序 其它：升序)
     */
    @ApiModelProperty(hidden = true)
    private String sendSort;

    /**
     * 发送时间排序(1:降序 其它：升序)
     */
    @ApiModelProperty(hidden = true)
    private String sendTimeSort;

    /**
     * 附件大小排序(1:降序 其它：升序)
     */
    @ApiModelProperty(hidden = true)
    private String maiSizeSort;

    /**
     * 收件人数排序(1:降序 其它：升序)
     */
    @ApiModelProperty(hidden = true)
    private String receviceUserSort;

    public MaiSendBox getMaiSendBox() {
        return maiSendBox;
    }

    public void setMaiSendBox(MaiSendBox maiSendBox) {
        this.maiSendBox = maiSendBox;
    }

    public List<MaiAttachment> getMaiAttachmentList() {
        return maiAttachmentList;
    }

    public void setMaiAttachmentList(List<MaiAttachment> maiAttachmentList) {
        this.maiAttachmentList = maiAttachmentList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getBegSendTime() {
        return begSendTime;
    }

    public void setBegSendTime(String begSendTime) {
        this.begSendTime = begSendTime;
    }

    public String getEndSendTime() {
        return endSendTime;
    }

    public void setEndSendTime(String endSendTime) {
        this.endSendTime = endSendTime;
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

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
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

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getRecevieSort() {
        return recevieSort;
    }

    public void setRecevieSort(String recevieSort) {
        this.recevieSort = recevieSort;
    }

    public String getAttachmentSort() {
        return attachmentSort;
    }

    public void setAttachmentSort(String attachmentSort) {
        this.attachmentSort = attachmentSort;
    }

    public String getSendSort() {
        return sendSort;
    }

    public void setSendSort(String sendSort) {
        this.sendSort = sendSort;
    }

    public String getSendTimeSort() {
        return sendTimeSort;
    }

    public void setSendTimeSort(String sendTimeSort) {
        this.sendTimeSort = sendTimeSort;
    }

    public String getMaiSizeSort() {
        return maiSizeSort;
    }

    public void setMaiSizeSort(String maiSizeSort) {
        this.maiSizeSort = maiSizeSort;
    }

    public String getReceviceUserSort() {
        return receviceUserSort;
    }

    public void setReceviceUserSort(String receviceUserSort) {
        this.receviceUserSort = receviceUserSort;
    }

}
