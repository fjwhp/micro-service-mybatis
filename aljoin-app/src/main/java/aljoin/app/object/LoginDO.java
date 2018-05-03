package aljoin.app.object;

/**
 * 
 * 登录返回数据对象
 *
 * @author：zhongjy
 * 
 * @date：2017年10月23日 下午2:54:19
 */
public class LoginDO {

  /**
   * 登录票据
   */
  private String token;
  /**
   * 加密秘钥
   */
  private String secret;
  /**
   * 合强ras公钥
   */
  private String aljoinPubKey;
  /**
   * 用户名称
   */
  private String userFullName;
  /**
   * 用户账号
   */
  private String userName;
  /**
   * 用户头像
   */
  private String userIcon;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getAljoinPubKey() {
    return aljoinPubKey;
  }

  public void setAljoinPubKey(String aljoinPubKey) {
    this.aljoinPubKey = aljoinPubKey;
  }

  public String getUserFullName() {
    return userFullName;
  }

  public void setUserFullName(String userFullName) {
    this.userFullName = userFullName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserIcon() {
    return userIcon;
  }

  public void setUserIcon(String userIcon) {
    this.userIcon = userIcon;
  }


}
