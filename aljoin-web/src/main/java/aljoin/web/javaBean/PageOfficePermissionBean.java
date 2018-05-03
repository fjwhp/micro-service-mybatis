package aljoin.web.javaBean;

/**
 * 
 * pageOffice 调用pageOffice 的权限设置
 *
 * @author：wuhp
 * 
 * @date：2017年11月15日 下午1:20:35
 */
public class PageOfficePermissionBean {

	/**
	 * 是否显示工具条
	 */
	private boolean isShowOfficeToolbars;
	/**
	 * 是否先显示自定义工具栏
	 */
	private boolean isShowCustomToolbar;

	/**
	 * 打开模式 1 只读模式 docReadOnly 2 word编辑模式 docNormalEdit 3 痕迹保留模式 docRevisionOnly
	 */
	private String openModeType;
	/**
	 * 文档的相对路径
	 */
	private String relativePath;
	/**
	 * 是否有导入按钮
	 */
	private String isImportButton;

	/**
	 * 是否有提交按钮
	 */
	private String isSubmitButton;
	/**
	 * 
	 */
	private String edit;
	/**
	 * 是否是新建文件
	 */
	private String isNewWord;
	/**
	 * 资源id 用于插入附件表的resourceId
	 */
	private String resourceId;
	/**
	 * 附件表主键
	 */
	private String attachId;
	/**
	 * 是否有红头按钮
	 */
	private String redTitle;
	/**
	 * 是否有手写批注按钮
	 */
	private String handSign;

	/**
	 * 文件主题，也是附件表 attach_name
	 */
	private String fileNameDesc;
	/**
	 * 模板类型  pag_print 套打  page_red_title 套红
	 */
	private String pageType;
	
	/**
	 * 表单id
	 */
	private String formId;
	
	/**
	 * 是否有套打按钮
	 */
	private String pagePrint;
	/**
	 * 域名和值 的 key-value 字符串
	 */
	private String fieldValues;
	
	private String fields;
	// setter,getter
	// ##############################################################

	public boolean isShowOfficeToolbars() {
		return isShowOfficeToolbars;
	}

	public void setShowOfficeToolbars(boolean isShowOfficeToolbars) {
		this.isShowOfficeToolbars = isShowOfficeToolbars;
	}

	public boolean isShowCustomToolbar() {
		return isShowCustomToolbar;
	}

	public void setShowCustomToolbar(boolean isShowCustomToolbar) {
		this.isShowCustomToolbar = isShowCustomToolbar;
	}

	public String getOpenModeType() {
		return openModeType;
	}

	public void setOpenModeType(String openModeType) {
		this.openModeType = openModeType;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}

	public String getIsImportButton() {
		return isImportButton;
	}

	public void setIsImportButton(String isImportButton) {
		this.isImportButton = isImportButton;
	}

	public String getIsSubmitButton() {
		return isSubmitButton;
	}

	public void setIsSubmitButton(String isSubmitButton) {
		this.isSubmitButton = isSubmitButton;
	}

	public String getIsNewWord() {
		return isNewWord;
	}

	public void setIsNewWord(String isNewWord) {
		this.isNewWord = isNewWord;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getAttachId() {
		return attachId;
	}

	public void setAttachId(String attachId) {
		this.attachId = attachId;
	}

	public String getRedTitle() {
		return redTitle;
	}

	public void setRedTitle(String redTitle) {
		this.redTitle = redTitle;
	}

	public String getHandSign() {
		return handSign;
	}

	public void setHandSign(String handSign) {
		this.handSign = handSign;
	}

	public String getFileNameDesc() {
		return fileNameDesc;
	}

	public void setFileNameDesc(String fileNameDesc) {
		this.fileNameDesc = fileNameDesc;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getPagePrint() {
		return pagePrint;
	}

	public void setPagePrint(String pagePrint) {
		this.pagePrint = pagePrint;
	}

	public String getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(String fieldValues) {
		this.fieldValues = fieldValues;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}
	

}
