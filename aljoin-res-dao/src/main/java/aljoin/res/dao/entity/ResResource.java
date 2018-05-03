package aljoin.res.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * @描述：资源表(实体类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-04-23
 */
public class ResResource extends Entity<ResResource> {

    private static final long serialVersionUID = 1L;

    /**
     * 分组名称
     */
	private String groupName;
    /**
     * 文件名称
     */
	private String fileName;
    /**
     * 原文件名称
     */
	private String orgnlFileName;
    /**
     * 文件类型
     */
	private String fileType;
    /**
     * 上传时间
     */
	private Date uploadTime;
    /**
     * 自定义文件类型id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long fileTypeId;
    /**
     * 文件大小KB
     */
	private Integer fileSize;
    /**
     * 是否激活
     */
	private Integer isActive;
    /**
     * 资源描述
     */
	private String fileDesc;
    /**
     * 来源ID
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long bizId;
    /**
     * 类型(对应模块)
     */
	private String type;


	public String getGroupName() {
		return groupName;
	}

	public ResResource setGroupName(String groupName) {
		this.groupName = groupName;
		return this;
	}

	public String getFileName() {
		return fileName;
	}

	public ResResource setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public String getOrgnlFileName() {
		return orgnlFileName;
	}

	public ResResource setOrgnlFileName(String orgnlFileName) {
		this.orgnlFileName = orgnlFileName;
		return this;
	}

	public String getFileType() {
		return fileType;
	}

	public ResResource setFileType(String fileType) {
		this.fileType = fileType;
		return this;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public ResResource setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
		return this;
	}

	public Long getFileTypeId() {
		return fileTypeId;
	}

	public ResResource setFileTypeId(Long fileTypeId) {
		this.fileTypeId = fileTypeId;
		return this;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public ResResource setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
		return this;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public ResResource setIsActive(Integer isActive) {
		this.isActive = isActive;
		return this;
	}

	public String getFileDesc() {
		return fileDesc;
	}

	public ResResource setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
		return this;
	}

	public Long getBizId() {
		return bizId;
	}

	public ResResource setBizId(Long bizId) {
		this.bizId = bizId;
		return this;
	}

	public String getType() {
		return type;
	}

	public ResResource setType(String type) {
		this.type = type;
		return this;
	}

}
