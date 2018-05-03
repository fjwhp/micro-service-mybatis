package aljoin.servlet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.util.StringUtils;
import com.zhuozhengsoft.pageoffice.poserver.Server;


/**
 * 
 * servlet 配置
 *
 * @author：wuhp
 * 
 * @date：2017年11月4日 上午9:32:37
 */
@Configuration
public class ServletConfigure {

  @Value("${aljoin.pageoffice.posyspath}")
  private String poSysPath;
  private static Logger log = LoggerFactory.getLogger(ServletConfigure.class);

  /**
   * 
   * pc端的pageoffice servlet配置
   *
   * @return：ServletRegistrationBean
   *
   * @author：wuhp
   *
   * @date：2017年11月4日 上午9:32:59
   */
  @Bean
  public ServletRegistrationBean pageOfficeServlet() {
    try {

      if (poSysPath == null || StringUtils.isEmpty(poSysPath)) {
        throw new Exception("配置文件application.properties 的aljoin.pageoffice.posyspath属性没有配置");
      }
      if (!new File(poSysPath).exists()) {
        new File(poSysPath).mkdirs();
      }
      Server poserver = new Server();
      poserver.setSysPath(poSysPath);// 设置PageOffice注册成功后,license.lic文件存放的目录
      ServletRegistrationBean servletBean = new ServletRegistrationBean();
      servletBean.addUrlMappings("/poserver.zz", "/sealsetup.exe", "/posetup.exe", "/pageoffice.js",
          "/jquery.min.js", "/pobstyle.css");
      servletBean.setServlet(poserver);
      servletBean.setName("poserver");
      return servletBean;
    } catch (Exception e) {
      log.error("", e);
    }
    return null;

  }

  /**
   * 
   * 移动端servlet 配置
   *
   * @return：ServletRegistrationBean
   *
   * @author：wuhp
   *
   * @date：2017年11月4日 上午9:34:34
   */
  @Bean
  public ServletRegistrationBean mobofficeServlet() {
    ServletRegistrationBean servletBean = new ServletRegistrationBean();
    servletBean.addUrlMappings("/mobserver.zz");
    servletBean.setServlet(new com.zhuozhengsoft.moboffice.Server());
    servletBean.setName("pomobserver");
    return servletBean;
  }

  /**
   * 
   * TODO
   *
   * @return：ServletRegistrationBean
   *
   * @author：wuhp
   *
   * @date：2017年11月21日 下午6:33:57
   */
  @Bean
  public ServletRegistrationBean druidStatViewServletBean() {
    // 后台的路径
    ServletRegistrationBean statViewServletRegistrationBean =
        new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
    Map<String, String> params = new HashMap<>();
    // 账号密码，是否允许重置数据
    params.put("loginUsername", "admin");
    params.put("loginPassword", "1qaz@WSX");
    params.put("resetEnable", "true");
    statViewServletRegistrationBean.setInitParameters(params);
    return statViewServletRegistrationBean;
  }

}
