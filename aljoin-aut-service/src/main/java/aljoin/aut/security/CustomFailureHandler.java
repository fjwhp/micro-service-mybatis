package aljoin.aut.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

/**
 * 
 * 自定义失败处理
 *
 * @author：zhongjy
 *
 * @date：2017年5月3日 下午5:06:02
 */
@Service
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        // 保存错误信息
        request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception.getMessage());
        String targetUrl = "/login.html";
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    @Override
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    @Override
    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

}
