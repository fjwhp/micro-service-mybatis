package aljoin.mai.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * @描述：收件箱表(实体类).
 * 
 * @作者：zhongjy
 * 
 * @时间: 2018-03-30
 */
public class MaiReceiveBox extends Entity<MaiReceiveBox> {

    private static final long serialVersionUID = 1L;

    /**
     * 收件人用户ID(分号分隔)
     */
	private String receiveUserIds;
    /**
     * 收件人账号(分号分隔)
     */
	private String receiveUserNames;
    /**
     * 收件人名称(分号分隔)
     */
	private String receiveFullNames;
    /**
     * (单独)发件人账号
     */
	private String receiveUserName;
    /**
     * 发件人用户ID
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long sendUserId;
    /**
     * 发件人账号
     */
	private String sendUserName;
    
    /**
     * 抄送人用户ID(分号分隔)
     */
	private String copyUserIds;
    /**
     * 抄送人账号(分号分隔)
     */
	private String copyUserNames;
    /**
     * 抄送人名称(分号分隔)
     */
	private String copyFullNames;
    /**
     * 邮件正文
     */
	private String mailContent;
    /**
     * 邮件大小(KB,正文+附件的大小)
     */
	private Integer mailSize;
    /**
     * 是否撤销(回)
     */
	private Integer isRevoke;
    /**
     * 撤销(回)时间
     */
	private Date revokeTime;
    /**
     * 设置为废件时间
     */
	private Date scrapTime;
    
    /**
     * 阅读时间
     */
	private Date readTime;
    /**
     * 阅读日期是星期几(保存字符串)：星期一；星期二；星期三；星期四；星期五；星期六；星期日；
     */
	private String weekDay;
    /**
     * 发送件的主键ID
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long sendId;
	/**
     * (单独)发件人名称
     */
    private String receiveFullName;

	public String getReceiveUserIds() {
		return receiveUserIds;
	}

	public MaiReceiveBox setReceiveUserIds(String receiveUserIds) {
		this.receiveUserIds = receiveUserIds;
		return this;
	}

	public String getReceiveUserNames() {
		return receiveUserNames;
	}

	public MaiReceiveBox setReceiveUserNames(String receiveUserNames) {
		this.receiveUserNames = receiveUserNames;
		return this;
	}

	public String getReceiveFullNames() {
		return receiveFullNames;
	}

	public MaiReceiveBox setReceiveFullNames(String receiveFullNames) {
		this.receiveFullNames = receiveFullNames;
		return this;
	}

	public String getReceiveUserName() {
		return receiveUserName;
	}

	public MaiReceiveBox setReceiveUserName(String receiveUserName) {
		this.receiveUserName = receiveUserName;
		return this;
	}

	public Long getSendUserId() {
		return sendUserId;
	}

	public MaiReceiveBox setSendUserId(Long sendUserId) {
		this.sendUserId = sendUserId;
		return this;
	}

	public String getSendUserName() {
		return sendUserName;
	}

	public MaiReceiveBox setSendUserName(String sendUserName) {
		this.sendUserName = sendUserName;
		return this;
	}

	public String getCopyUserIds() {
		return copyUserIds;
	}

	public MaiReceiveBox setCopyUserIds(String copyUserIds) {
		this.copyUserIds = copyUserIds;
		return this;
	}

	public String getCopyUserNames() {
		return copyUserNames;
	}

	public MaiReceiveBox setCopyUserNames(String copyUserNames) {
		this.copyUserNames = copyUserNames;
		return this;
	}

	public String getCopyFullNames() {
		return copyFullNames;
	}

	public MaiReceiveBox setCopyFullNames(String copyFullNames) {
		this.copyFullNames = copyFullNames;
		return this;
	}

	public String getMailContent() {
		return mailContent;
	}

	public MaiReceiveBox setMailContent(String mailContent) {
		this.mailContent = mailContent;
		return this;
	}

	public Integer getMailSize() {
		return mailSize;
	}

	public MaiReceiveBox setMailSize(Integer mailSize) {
		this.mailSize = mailSize;
		return this;
	}

	public Integer getIsRevoke() {
		return isRevoke;
	}

	public MaiReceiveBox setIsRevoke(Integer isRevoke) {
		this.isRevoke = isRevoke;
		return this;
	}

	public Date getRevokeTime() {
		return revokeTime;
	}

	public MaiReceiveBox setRevokeTime(Date revokeTime) {
		this.revokeTime = revokeTime;
		return this;
	}

	public Date getScrapTime() {
		return scrapTime;
	}

	public MaiReceiveBox setScrapTime(Date scrapTime) {
		this.scrapTime = scrapTime;
		return this;
	}

	public Date getReadTime() {
		return readTime;
	}

	public MaiReceiveBox setReadTime(Date readTime) {
		this.readTime = readTime;
		return this;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public MaiReceiveBox setWeekDay(String weekDay) {
		this.weekDay = weekDay;
		return this;
	}

	public Long getSendId() {
		return sendId;
	}

	public MaiReceiveBox setSendId(Long sendId) {
		this.sendId = sendId;
		return this;
	}
    public String getReceiveFullName() {
        return receiveFullName;
    }

    public MaiReceiveBox setReceiveFullName(String receiveFullName) {
        this.receiveFullName = receiveFullName;
        return this;
    }
}
