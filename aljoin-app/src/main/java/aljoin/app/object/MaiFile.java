package aljoin.app.object;

/**
 * 
 * 邮件列表返回数据对象
 *
 * @author：zhongjy
 * 
 * @date：2017年10月23日 下午2:54:19
 */
public class MaiFile {	
	
	 /**
     * 附件大小
     */
	private int limitSize; 
	 /**
     * 可上传类型
     */ 
	private String allowType; 
	/**
	 * 附件地址
	 */
	private String uploadPath;
	public int getLimitSize() {
		return limitSize;
	}
	public void setLimitSize(int limitSize) {
		this.limitSize = limitSize;
	}
	public String getAllowType() {
		return allowType;
	}
	public void setAllowType(String allowType) {
		this.allowType = allowType;
	}
	public String getUploadPath() {
		return uploadPath;
	}
	public void setUploadPath(String uploadPath) {
		this.uploadPath = uploadPath;
	}
}
