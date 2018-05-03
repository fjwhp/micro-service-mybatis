package aljoin.mai.dao.entity;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import aljoin.dao.entity.Entity;

/**
 * 
 * @描述：收件箱表(实体类).
 * 
 * @作者：zhongjy
 * 
 * @时间: 2018-03-30
 */
public class MaiReceiveBoxSearch extends Entity<MaiReceiveBoxSearch> {

    private static final long serialVersionUID = 1L;

    /**
     * 主题
     */
	private String subjectText;
    /**
     * (单独)发件人用户ID
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long receiveUserId;
    /**
     * 是否设置为废件（即删除操作）
     */
	private Integer isScrap;
    /**
     * 发送时间
     */
	private Date sendTime;
    /**
     * 是否已读
     */
	private Integer isRead;
    /**
     * 是否紧急（发件方设定）
     */
	private Integer isUrgent;
    /**
     * 是否重要（收件方自己设定）
     */
	private Integer isImportant;
	/**
     * 附件个数
     */
    private Integer attachmentCount;
    /**
     * 发件人名称
     */
    private String sendFullName;


	public String getSubjectText() {
		return subjectText;
	}

	public MaiReceiveBoxSearch setSubjectText(String subjectText) {
		this.subjectText = subjectText;
		return this;
	}

	public Long getReceiveUserId() {
		return receiveUserId;
	}

	public MaiReceiveBoxSearch setReceiveUserId(Long receiveUserId) {
		this.receiveUserId = receiveUserId;
		return this;
	}

	public Integer getIsScrap() {
		return isScrap;
	}

	public MaiReceiveBoxSearch setIsScrap(Integer isScrap) {
		this.isScrap = isScrap;
		return this;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public MaiReceiveBoxSearch setSendTime(Date sendTime) {
		this.sendTime = sendTime;
		return this;
	}

	public Integer getIsRead() {
		return isRead;
	}

	public MaiReceiveBoxSearch setIsRead(Integer isRead) {
		this.isRead = isRead;
		return this;
	}

	public Integer getIsUrgent() {
		return isUrgent;
	}

	public MaiReceiveBoxSearch setIsUrgent(Integer isUrgent) {
		this.isUrgent = isUrgent;
		return this;
	}

	public Integer getIsImportant() {
		return isImportant;
	}

	public MaiReceiveBoxSearch setIsImportant(Integer isImportant) {
		this.isImportant = isImportant;
		return this;
	}
	public Integer getAttachmentCount() {
        return attachmentCount;
    }

    public MaiReceiveBoxSearch setAttachmentCount(Integer attachmentCount) {
        this.attachmentCount = attachmentCount;
        return this;
    }
    public String getSendFullName() {
        return sendFullName;
    }

    public MaiReceiveBoxSearch setSendFullName(String sendFullName) {
        this.sendFullName = sendFullName;
        return this;
    }
}
