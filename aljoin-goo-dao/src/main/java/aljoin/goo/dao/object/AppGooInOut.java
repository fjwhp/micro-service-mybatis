package aljoin.goo.dao.object;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 办公用品出入库信息表(实体类).
 * 
 * @author：xuc.
 * 
 * @date： 2018-01-15
 */
public class AppGooInOut extends Entity<AppGooInOut> {

    private static final long serialVersionUID = 1L;

    /**
     * 单证日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date submitTime;
    /**
     * 标题（入库单标题）
     */
    private String title;
    /**
     * 流程名称
     */
    private String processName;
    /**
     * 签收状态
     */
    private Integer claimStatus;

    public Integer getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(Integer claimStatus) {
        this.claimStatus = claimStatus;
    }

    /**
     * 操作人
     */
    private String publishName;
    /**
     * 物品ID （办公用品表ID）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long gooId;
    /**
     * 物品名称
     */
    private String gooName;
    /**
     * 物品分类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
    /**
     * 物品分类名称
     */
    private String categoryName;
    /**
     * 出入库状态（1:入库 2:报溢 3:领用 4:报损）
     */
    private Integer inOutStatus;
    /**
     * 描述
     */
    private String content;
    /**
     * 审核状态（1:审核中 2：审核失败 3：审核通过）
     */
    private Integer auditStatus;
    /**
     * 审核时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    /**
     * 流程ID
     */
    private String processId;
    /**
     * 出入库物品数量
     */
    private Integer number;
    /**
     * 单证编号编号
     */
    private String listCode;
    /**
     * 出入库流程实例ID
     */
    private String gooInOutProcInstId;
    /**
     * 单位
     */
    private String unit;
    /**
     * 物品数量
     */
    private Integer gooNumber;

    /**
     * 部门
     */
    private String deptName;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public AppGooInOut setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public AppGooInOut setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getProcessName() {
        return processName;
    }

    public AppGooInOut setProcessName(String processName) {
        this.processName = processName;
        return this;
    }

    public String getPublishName() {
        return publishName;
    }

    public AppGooInOut setPublishName(String publishName) {
        this.publishName = publishName;
        return this;
    }

    public Long getGooId() {
        return gooId;
    }

    public AppGooInOut setGooId(Long gooId) {
        this.gooId = gooId;
        return this;
    }

    public String getGooName() {
        return gooName;
    }

    public AppGooInOut setGooName(String gooName) {
        this.gooName = gooName;
        return this;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public AppGooInOut setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public AppGooInOut setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public Integer getInOutStatus() {
        return inOutStatus;
    }

    public AppGooInOut setInOutStatus(Integer inOutStatus) {
        this.inOutStatus = inOutStatus;
        return this;
    }

    public String getContent() {
        return content;
    }

    public AppGooInOut setContent(String content) {
        this.content = content;
        return this;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public AppGooInOut setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
        return this;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public AppGooInOut setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
        return this;
    }

    public String getProcessId() {
        return processId;
    }

    public AppGooInOut setProcessId(String processId) {
        this.processId = processId;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public AppGooInOut setNumber(Integer number) {
        this.number = number;
        return this;
    }

    public String getListCode() {
        return listCode;
    }

    public void setListCode(String listCode) {
        this.listCode = listCode;
    }

    public String getGooInOutProcInstId() {
        return gooInOutProcInstId;
    }

    public AppGooInOut setGooInOutProcInstId(String gooInOutProcInstId) {
        this.gooInOutProcInstId = gooInOutProcInstId;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public AppGooInOut setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public Integer getGooNumber() {
        return gooNumber;
    }

    public AppGooInOut setGooNumber(Integer gooNumber) {
        this.gooNumber = gooNumber;
        return this;
    }

}
