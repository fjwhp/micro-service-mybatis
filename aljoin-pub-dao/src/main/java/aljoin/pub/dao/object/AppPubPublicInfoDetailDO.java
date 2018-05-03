package aljoin.pub.dao.object;

import aljoin.res.dao.object.AppResResourceDO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 * 公共信息表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-12
 */
public class AppPubPublicInfoDetailDO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", required = false, hidden = false)
    public Long id;

    /**
     * 标题（公告名称）
     */
    @ApiModelProperty(value = "标题", required = false, hidden = false)
    private String title;

    /**
     * 创建日期
     */
    @ApiModelProperty(value = "发布时间", required = false, hidden = false)
    private String createDate;

    /**
     * 创建人ID
     */
    @ApiModelProperty(value = "创建人ID", required = false, hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    /**
     * 发布人
     */
    @ApiModelProperty(value = "发布人", required = false, hidden = false)
    private String publishName;
    /**
     * 分类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "分类ID", required = false, hidden = true)
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容", required = false, hidden = false)
    private String content;

    /**
     * 签收状态
     */
    private Integer claimStatus;

    /**
     * 有效期(单位为天)
     */
    @ApiModelProperty(value = "有效期(单位为天)", required = false, hidden = true)
    private Integer period;
    /**
     * 有效期开始时间
     */
    @ApiModelProperty(value = "有效期开始时间", required = false, hidden = true)
    private Date periodBeginTime;
    /**
     * 有效期结束时间
     */
    @ApiModelProperty(value = "有效期结束时间", required = false, hidden = true)
    private Date periodEndTime;
    /**
     * 有效期状态（0:有效期限 1:已失效）
     */
    @ApiModelProperty(value = "有效期状态（0:有效期限 1:已失效）", required = false, hidden = true)
    private Integer periodStatus;
    /**
     * 公告对象ID （多个用分号分隔）
     */
    @ApiModelProperty(value = "公告对象ID （多个用分号分隔）", required = false, hidden = true)
    private String noticeObjId;
    /**
     * 公告对象 （多个用分号分隔）
     */
    @ApiModelProperty(value = "公告对象 （多个用分号分隔）", required = false, hidden = true)
    private String noticeObjName;

    /**
     * 附件列表
     */
    @ApiModelProperty(value = "附件列表", required = false, hidden = false)
    private List<AppResResourceDO> resResourceList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getPublishName() {
        return publishName;
    }

    public void setPublishName(String publishName) {
        this.publishName = publishName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<AppResResourceDO> getResResourceList() {
        return resResourceList;
    }

    public void setResResourceList(List<AppResResourceDO> resResourceList) {
        this.resResourceList = resResourceList;
    }

    public Integer getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(Integer claimStatus) {
        this.claimStatus = claimStatus;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Date getPeriodBeginTime() {
        return periodBeginTime;
    }

    public void setPeriodBeginTime(Date periodBeginTime) {
        this.periodBeginTime = periodBeginTime;
    }

    public Date getPeriodEndTime() {
        return periodEndTime;
    }

    public void setPeriodEndTime(Date periodEndTime) {
        this.periodEndTime = periodEndTime;
    }

    public Integer getPeriodStatus() {
        return periodStatus;
    }

    public void setPeriodStatus(Integer periodStatus) {
        this.periodStatus = periodStatus;
    }

    public String getNoticeObjId() {
        return noticeObjId;
    }

    public void setNoticeObjId(String noticeObjId) {
        this.noticeObjId = noticeObjId;
    }

    public String getNoticeObjName() {
        return noticeObjName;
    }

    public void setNoticeObjName(String noticeObjName) {
        this.noticeObjName = noticeObjName;
    }
}
