package aljoin.aut.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import aljoin.sys.dao.entity.SysOperateLog;
import aljoin.sys.iservice.SysOperateLogService;

/**
 * 
 * 自定义成功处理
 *
 * @author：zhongjy
 *
 * @date：2017年5月3日 下午5:06:02
 */
@Service
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Resource
    private SysOperateLogService sysOperateLogService;

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {
        // 获取用户角色
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for (GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }

        if (response.isCommitted()) {
            System.out.println("不能重定向...");
            return;
        }
        String targetUrl = "/";

        // 登录成功，插入操作日志(以后改成异步插入)
        SysOperateLog sysOperateLog = new SysOperateLog();
        CustomUser customUser = (CustomUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Date date = new Date();
        sysOperateLog.setOperateUserId(customUser.getUserId());
        sysOperateLog.setOperateUserName(customUser.getUsername());
        sysOperateLog.setOperateUserIp(request.getRemoteAddr());
        sysOperateLog.setOperateTime(date);
        sysOperateLog.setRequestAddress(request.getRequestURL().toString());
        sysOperateLog.setOperateLogName("[登录操作]");
        sysOperateLog.setOperateDetailDesc("[登录操作]");
        sysOperateLog.setMethodName("auto.deploy.security.CustomUserDetailsService.loadUserByUsername");
        sysOperateLogService.insert(sysOperateLog);
        // 登录成功，插入pageoffice相关文件
        // setPageOfficeConfig(request);
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

    @SuppressWarnings("unused")
    private void setPageOfficeConfig(HttpServletRequest request) {
        try {
            String licenseTargetPath = "/WEB-INF/lib/license.lic";
            String licenseSourcePath = "web-config/license.lic";
            if (request.getServletContext().getResourceAsStream(licenseTargetPath) != null) {
                return;
            }
            String license = request.getServletContext().getRealPath(licenseTargetPath);
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(licenseSourcePath);
            File file = new File(license);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
                OutputStream os = new FileOutputStream(file);
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                is.close();
            }
        } catch (Exception e) {
            logger.error("导入pageoffice配置文件异常：", e);
        }
    }

}
