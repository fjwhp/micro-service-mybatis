package aljoin.aut.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * 
 * 自定义额外表单数据
 *
 * @author：zhongjy
 *
 * @date：2017年5月9日 下午11:27:31
 */
public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

    /**
     * 
     */
    private static final long serialVersionUID = 7241654791403589332L;

    /**
     * 登录验证码
     */
    private String loginValidateCode;
    /**
     * session中的验证码
     */
    private String loginSessionCode;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        loginValidateCode = request.getParameter("loginValidateCode");
        loginSessionCode = (String)request.getSession().getAttribute("USER_LOGIN_SESSION_CODE");

    }

    public String getLoginValidateCode() {
        return loginValidateCode;
    }

    public void setLoginValidateCode(String loginValidateCode) {
        this.loginValidateCode = loginValidateCode;
    }

    public String getLoginSessionCode() {
        return loginSessionCode;
    }

    public void setLoginSessionCode(String loginSessionCode) {
        this.loginSessionCode = loginSessionCode;
    }

}
