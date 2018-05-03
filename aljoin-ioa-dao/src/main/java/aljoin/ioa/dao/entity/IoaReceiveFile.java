package aljoin.ioa.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * 收文阅件表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public class IoaReceiveFile extends Entity<IoaReceiveFile> {

    private static final long serialVersionUID = 1L;

    /**
     * 来文标题
     */
    @ApiModelProperty(hidden = true)
    private String fileTitle;
    /**
     * 来文类型（数据字典DICT_RECEIVE_FILE_TYPE）
     */
    @ApiModelProperty(hidden = true)
    private String fileType;
    /**
     * 来文类型名称（数据字典DICT_RECEIVE_FILE_TYPE）
     */
    @ApiModelProperty(hidden = true)
    private String fileTypeName;
    /**
     * 来文单位（数据字典DICT_FROM_UNIT）
     */
    @ApiModelProperty(hidden = true)
    private String fromUnit;
    /**
     * 来文单位名称（数据字典DICT_FROM_UNIT）
     */
    @ApiModelProperty(hidden = true)
    private String fromUnitName;
    /**
     * 来文文号
     */
    @ApiModelProperty(hidden = true)
    private String fromFileCode;
    /**
     * 收文编号
     */
    @ApiModelProperty(hidden = true)
    private String receiveFileCode;
    /**
     * 原文日期
     */
    @ApiModelProperty(hidden = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date orgnlFileTime;
    /**
     * 收文日期
     */
    @ApiModelProperty(hidden = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date receiveFileTime;
    /**
     * 办理时限
     */
    @ApiModelProperty(hidden = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date handleLimitTime;
    /**
     * 紧急程度（数据字典DICT_URGENT_LEVEL）
     */
    @ApiModelProperty(hidden = true)
    private String urgentLevel;
    /**
     * 紧急程度（数据字典DICT_URGENT_LEVEL）
     */
    @ApiModelProperty(hidden = true)
    private String urgentLevelName;
    /**
     * 办公室拟办意见
     */
    @ApiModelProperty(hidden = true)
    private String officeOpinion;
    /**
     * 流程ID
     */
    @ApiModelProperty(hidden = true)
    private String processId;
    /**
     * bpmn表主键ID
     */
    @ApiModelProperty(hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bpmnId;
    /**
     * 流程实例ID
     */
    @ApiModelProperty(hidden = true)
    private String processInstanceId;
    /**
     * 是否归档
     */
    @ApiModelProperty(hidden = true)
    private Integer isClose;
    /**
     * 所有传阅对象的用户id
     */
    @ApiModelProperty(hidden = true)
    private String readUserIds;

    public String getFileTitle() {
        return fileTitle;
    }

    public IoaReceiveFile setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public IoaReceiveFile setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public String getFileTypeName() {
        return fileTypeName;
    }

    public IoaReceiveFile setFileTypeName(String fileTypeName) {
        this.fileTypeName = fileTypeName;
        return this;
    }

    public String getFromUnit() {
        return fromUnit;
    }

    public IoaReceiveFile setFromUnit(String fromUnit) {
        this.fromUnit = fromUnit;
        return this;
    }

    public String getFromUnitName() {
        return fromUnitName;
    }

    public IoaReceiveFile setFromUnitName(String fromUnitName) {
        this.fromUnitName = fromUnitName;
        return this;
    }

    public String getFromFileCode() {
        return fromFileCode;
    }

    public IoaReceiveFile setFromFileCode(String fromFileCode) {
        this.fromFileCode = fromFileCode;
        return this;
    }

    public String getReceiveFileCode() {
        return receiveFileCode;
    }

    public IoaReceiveFile setReceiveFileCode(String receiveFileCode) {
        this.receiveFileCode = receiveFileCode;
        return this;
    }

    public Date getOrgnlFileTime() {
        return orgnlFileTime;
    }

    public IoaReceiveFile setOrgnlFileTime(Date orgnlFileTime) {
        this.orgnlFileTime = orgnlFileTime;
        return this;
    }

    public Date getReceiveFileTime() {
        return receiveFileTime;
    }

    public IoaReceiveFile setReceiveFileTime(Date receiveFileTime) {
        this.receiveFileTime = receiveFileTime;
        return this;
    }

    public Date getHandleLimitTime() {
        return handleLimitTime;
    }

    public IoaReceiveFile setHandleLimitTime(Date handleLimitTime) {
        this.handleLimitTime = handleLimitTime;
        return this;
    }

    public String getUrgentLevel() {
        return urgentLevel;
    }

    public IoaReceiveFile setUrgentLevel(String urgentLevel) {
        this.urgentLevel = urgentLevel;
        return this;
    }

    public String getUrgentLevelName() {
        return urgentLevelName;
    }

    public IoaReceiveFile setUrgentLevelName(String urgentLevelName) {
        this.urgentLevelName = urgentLevelName;
        return this;
    }

    public String getOfficeOpinion() {
        return officeOpinion;
    }

    public IoaReceiveFile setOfficeOpinion(String officeOpinion) {
        this.officeOpinion = officeOpinion;
        return this;
    }

    public String getProcessId() {
        return processId;
    }

    public IoaReceiveFile setProcessId(String processId) {
        this.processId = processId;
        return this;
    }

    public Long getBpmnId() {
        return bpmnId;
    }

    public IoaReceiveFile setBpmnId(Long bpmnId) {
        this.bpmnId = bpmnId;
        return this;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public IoaReceiveFile setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public Integer getIsClose() {
        return isClose;
    }

    public IoaReceiveFile setIsClose(Integer isClose) {
        this.isClose = isClose;
        return this;
    }

    public String getReadUserIds() {
        return readUserIds;
    }

    public IoaReceiveFile setReadUserIds(String readUserIds) {
        this.readUserIds = readUserIds;
        return this;
    }

}
