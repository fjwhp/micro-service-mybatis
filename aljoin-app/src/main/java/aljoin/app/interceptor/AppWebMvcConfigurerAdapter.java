package aljoin.app.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 
 * APP WEB配置
 *
 * @author：zhongjy
 * 
 * @date：2017年10月19日 上午10:51:57
 */
@Configuration
public class AppWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {

  /**
   * 拦截器链
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 拦截所有/app/***,/app/aut/autUser/appLogin除外
    registry.addInterceptor(new APIHandlerInterceptor()).addPathPatterns("/app/**")
        .excludePathPatterns("/app/aut/autUser/appLogin");
  }

}
