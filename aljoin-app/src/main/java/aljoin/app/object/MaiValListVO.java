package aljoin.app.object;

import java.util.Date;

/**
 * 
 * 邮件列表返回数据对象
 *
 * @author：zhongjy
 * 
 * @date：2017年10月23日 下午2:54:19
 */
public class MaiValListVO {	
	
	 /**
     * 收件人名称(分号分隔)
     */
	private String maiID; 
	 /**
     * 收件人名称(分号分隔)
     */ 
	private String receiveFullNames; 
	  /**
     * 主题
     */
	private String subjectText;
	/**
	 * 是否紧急
	 */
	private Integer isUrgent;
	
	  /**
	   * 修改时间
	   */
	
	private Date lastUpdateTime;
	 /**
	   * 是否读取
	   */
	private Integer isRead;
	/**
	   * 是否重要
	   */	
	private Integer isImportant;
	public String getMaiID() {
		return maiID;
	}
	public void setMaiID(String maiID) {
		this.maiID = maiID;
	}
	public String getReceiveFullNames() {
		return receiveFullNames;
	}
	public void setReceiveFullNames(String receiveFullNames) {
		this.receiveFullNames = receiveFullNames;
	}
	public String getSubjectText() {
		return subjectText;
	}
	public void setSubjectText(String subjectText) {
		this.subjectText = subjectText;
	}
	public Integer getIsUrgent() {
		return isUrgent;
	}
	public void setIsUrgent(Integer isUrgent) {
		this.isUrgent = isUrgent;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Integer getIsRead() {
		return isRead;
	}
	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}
	public Integer getIsImportant() {
		return isImportant;
	}
	public void setIsImportant(Integer isImportant) {
		this.isImportant = isImportant;
	}

}
