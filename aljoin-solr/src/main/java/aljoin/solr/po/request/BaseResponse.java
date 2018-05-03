package aljoin.solr.po.request;

import java.io.Serializable;

public class BaseResponse implements Serializable {
  
  private static final long serialVersionUID = -1864470087731542406L;

  private String errorCode;

  private String errorMsg;

  private String subCode;

  private String subMsg;
  
  /**
   * @return the errorCode
   */
  public String getErrorCode() {
      return errorCode;
  }

  /**
   * @param errorCode
   *            the errorCode to set
   */
  public void setErrorCode(String errorCode) {
      this.errorCode = errorCode;
  }

  /**
   * @return the errorMsg
   */
  public String getErrorMsg() {
      return errorMsg;
  }

  /**
   * @param errorMsg
   *            the errorMsg to set
   */
  public void setErrorMsg(String errorMsg) {
      this.errorMsg = errorMsg;
  }

  /**
   * @return the subCode
   */
  public String getSubCode() {
      return subCode;
  }

  /**
   * @param subCode
   *            the subCode to set
   */
  public void setSubCode(String subCode) {
      this.subCode = subCode;
  }

  /**
   * @return the subMsg
   */
  public String getSubMsg() {
      return subMsg;
  }

  /**
   * @param subMsg
   *            the subMsg to set
   */
  public void setSubMsg(String subMsg) {
      this.subMsg = subMsg;
  }

  public boolean isSuccess() {
      return this.errorCode == null && this.subCode == null && this.errorMsg == null && this.subMsg == null;
  }

 
}