package aljoin.object;

/**
 * 
 * 单点登录数对象
 *
 * @author：zhongjy
 * 
 * @date：2017年11月21日 上午8:56:42
 */
public class SsoData {
    /**
     * 操作类型：1-新增，2-修改，3-查询
     */
    private String operationType;

    /**
     * 登录账号
     */
    private String loginAccount;
    /**
     * 登录密码(MD5大写)
     */
    private String loginPwd;
    /**
     * 签名(所有参与传参的参数按照accsii排序（升序,如a=1&b=2...,sign不需要参与签名,内容为空不需要参与签名）然后MD5大写)
     */
    private String sign;
    /**
     * 时间戳(毫秒)
     */
    private String timeStamp;

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
