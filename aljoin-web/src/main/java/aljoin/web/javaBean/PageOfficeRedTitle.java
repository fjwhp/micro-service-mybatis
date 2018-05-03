package aljoin.web.javaBean;

/**
 * 
 * 红头文，填充，约定的名称 红头模板书签约定 PO_(本类属性名称) 如：红头模板文件书签 设置书签 PO_title 标识改位置将会 被本类下PageOfficeRedTitle
 *                  类的title属性值替换
 * 
 *
 * @author：wuhp
 * 
 * @date：2017年11月22日 下午6:35:06
 */
public class PageOfficeRedTitle {
  /**
   * 单位标题
   */
  private String unitTitle;
  /**
   * 公文标题
   */
  private String title;
  /**
   * 发文文号
   */
  private String docNum;
  /**
   * 正文
   */
  private String mainContent;
  /**
   * 签发时间
   */
  private String issueDate;

  /**
   * 抄送份数
   */
  private String copies;

  /**
   * 主题词
   */
  private String topicWords;

  /**
   * 发文单位
   */
  private String issueDept;

  // ########################## setter getter ################################
  public String getUnitTitle() {
    return unitTitle;
  }

  public void setUnitTitle(String unitTitle) {
    this.unitTitle = unitTitle;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDocNum() {
    return docNum;
  }

  public void setDocNum(String docNum) {
    this.docNum = docNum;
  }

  public String getMainContent() {
    return mainContent;
  }

  public void setMainContent(String mainContent) {
    this.mainContent = mainContent;
  }

  public String getIssueDate() {
    return issueDate;
  }

  public void setIssueDate(String issueDate) {
    this.issueDate = issueDate;
  }

  public String getCopies() {
    return copies;
  }

  public void setCopies(String copies) {
    this.copies = copies;
  }

  public String getTopicWords() {
    return topicWords;
  }

  public void setTopicWords(String topicWords) {
    this.topicWords = topicWords;
  }

  public String getIssueDept() {
    return issueDept;
  }

  public void setIssueDept(String issueDept) {
    this.issueDept = issueDept;
  }



}
