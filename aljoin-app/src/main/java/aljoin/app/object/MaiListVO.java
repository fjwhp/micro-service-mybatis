package aljoin.app.object;

/**
 * 
 * 邮件列表接收数据对象
 *
 * @author：zhongjy
 * 
 * @date：2017年10月23日 下午2:54:19
 */
public class MaiListVO {	
	
	 /**
     * 邮件显示数
     */
	private Integer pageSize; 
	 /**
     * 邮件页数
     */ 
	private Integer pageNum; 
	  /**
     * 邮件标题
     */
	private String title;
	  /**
     * 邮件收件人/收件人
     */	
	private String queryName;
	/**
	 * 是否紧急
	 */
	private Integer isUrgent;
	
	  /**
	   * 开始时间
	   */
	
	private String staTime;
	/**
	   * 结束时间
	   */
	
	private String endTime;
	 /**
	   * 是否读取
	   */
	private Integer isRead;
	/**
	  * 是否重要
	  */	
	private Integer isImportant;
	/**
	  * 是否有附件
	  */	
	private Integer  isccessory;
	
	public String getQueryName() {
		return queryName;
	}
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageNum() {
		return pageNum;
	}
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getIsUrgent() {
		return isUrgent;
	}
	public void setIsUrgent(Integer isUrgent) {
		this.isUrgent = isUrgent;
	}
	public String getStaTime() {
		return staTime;
	}
	public void setStaTime(String staTime) {
		this.staTime = staTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
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
	public Integer getIsccessory() {
		return isccessory;
	}
	public void setIsccessory(Integer isccessory) {
		this.isccessory = isccessory;
	}

}
