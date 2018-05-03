package aljoin.aut.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

/**
 * 
 * 自定义额外表单数据(源)，登录表单配置额外字段
 *
 * @author：zhongjy
 *
 * @date：2017年5月9日 下午11:27:43
 */
@Service
public class CustomAuthenticationDetailsSource
    implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest request) {
        return new CustomWebAuthenticationDetails(request);
    }

}
