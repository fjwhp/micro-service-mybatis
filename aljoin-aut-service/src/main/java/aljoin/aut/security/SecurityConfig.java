package aljoin.aut.security;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.util.StringUtils;

import aljoin.config.AljoinSetting;

/**
 * 
 * security配置
 *
 * @author：zhongjy
 *
 * @date：2017年5月2日 下午9:43:01
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private CustomSuccessHandler customSuccessHandler;
    @Resource
    private CustomFailureHandler customFailureHandler;
    @Resource
    private CustomUserDetailsServiceImpl customUserDetailsService;
    @Resource
    private CustomPasswordEncoder customPasswordEncoder;
    @Resource
    private CustomAuthenticationDetailsSource customAuthenticationDetailsSource;
    @Resource
    private DataSource dataSource;
    @Resource
    private AljoinSetting aljoinSetting;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /**
         * 根据是否使用cas单点登录来配置权限
         */
        if ("1".equals(aljoinSetting.getIsUseSSO())) {
            /**
             * 使用单点登录
             */
            auth.authenticationProvider(casAuthenticationProvider());
        } else {
            /**
             * 使用系统自己的登录
             */
            auth.authenticationProvider(getAuthenticationProvider());
        }

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * 允许访问的页面
         */
        String[] limitVisitHtml = {"/login.html", "/validateCode/codeImg.html", "/app/**", "/poserver.zz",
            "/mobserver.zz", "*.doc", "*.jsp", "/aljoin/fastdfs/upload"};
        String[] limitSwaggerHtml = {"/swagger-ui.html", "/configuration/**", "/swagger-ui/**", "/swagger-resources/**",
            "/api-docs", "/api-docs/**", "/v2/api-docs/**"};
        /**
         * 允许访问的资源
         */
        String[] limitVisitResource
            = {"/**/*.js", "/**/*.css", "/**/*.woff", "/**/*.woff2", "/**/*.otf", "/**/*.eot", "/**/*.svg", "/**/*.ttf",
                "/**/*.png", "/**/*.jpg", "/**/*.gif", "/**/*.json"};
        /**
         * 根据是否使用cas单点登录来配置权限
         */
        if ("1".equals(aljoinSetting.getIsUseSSO())) {
            /**
             * 访问匹配的url无需认证
             */
            http.authorizeRequests().antMatchers(limitVisitHtml).permitAll()
                /**
                 * 不拦截静态资源
                 */
                .antMatchers(limitVisitResource).permitAll()
                /**
                 * 不拦截Swagger
                 */
                .antMatchers(limitSwaggerHtml).permitAll()
                /**
                 * 所有资源都需要认证，登陆后访问
                 */
                .anyRequest().authenticated()
                /**
                 * 登出请求url// 定义logout不需要验证
                 */
                .and().logout().permitAll().logoutUrl("/logout.do")
                /**
                 * 使用form表单登录
                 */
                .and().formLogin().loginProcessingUrl("/login.do")
                /**
                 * (3)---------------.启用跨站请求伪造(CSRF)保护,如果启用了CSRF，那么在登录或注销页面中必须包括_csrf.token
                 */
                /**
                 * 忽略app接口
                 */
                .and().csrf().ignoringAntMatchers("/app/**")
                /**
                 * 忽略在线编辑器
                 */
                .ignoringAntMatchers("/poserver.zz")
                /**
                 * 忽略在线编辑器
                 */
                .ignoringAntMatchers("/mobserver.zz").ignoringAntMatchers("*.doc")
                /**
                 * 忽略druid监控
                 */
                .ignoringAntMatchers("/druid/*")
                /**
                 * 忽略文件上传，但是需要签名验证
                 */
                .ignoringAntMatchers("*.jsp").ignoringAntMatchers("/aljoin/fastdfs/upload")
                /**
                 * 解决iframe加载问题（x-frame-options）
                 */
                .and().headers().defaultsDisabled().cacheControl();
            http.exceptionHandling().authenticationEntryPoint(casAuthenticationEntryPoint()).and()
                .addFilter(casAuthenticationFilter()).addFilterBefore(casLogoutFilter(), LogoutFilter.class)
                .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);
        } else {
            /**
             * 访问匹配的url无需认证
             */
            http.authorizeRequests().antMatchers(limitVisitHtml).permitAll()
                /**
                 * 不拦截静态资源
                 */
                .antMatchers(limitVisitResource).permitAll()
                /**
                 * 不拦截Swagger
                 */
                .antMatchers(limitSwaggerHtml).permitAll()
                /**
                 * 所有资源都需要认证，登陆后访问
                 */
                .anyRequest().authenticated()
                /**
                 * (1)---------------.登录表单配置
                 */
                .and().formLogin()
                /**
                 * 登录表单
                 */
                .loginPage("/login.html")
                /**
                 * 登录请求url
                 */
                .loginProcessingUrl("/login.do")
                /**
                 * 登录表单账户的name
                 */
                .usernameParameter("loginUserName")
                /**
                 * 登录表单密码的name
                 */
                .passwordParameter("loginUserPwd")
                /**
                 * 自定义登录成功处理
                 */
                .successHandler(customSuccessHandler)
                /**
                 * 自定义登录失败处理
                 */
                .failureHandler(customFailureHandler)
                /**
                 * 自定义额外表单数据
                 */
                .authenticationDetailsSource(customAuthenticationDetailsSource)
                /**
                 * 登录表单记住我name
                 */
                .and().rememberMe().rememberMeParameter("loginRememberMe")
                /**
                 * 记住我持久化并设定时间
                 */
                .tokenRepository(persistentTokenRepository()).tokenValiditySeconds(86400)
                /**
                 * (2)---------------.登出表单配置
                 */
                .and().logout()
                /**
                 * 退出成功跳转
                 */
                .logoutSuccessUrl("/login.html")
                /**
                 * 登出请求url
                 */
                .logoutUrl("/logout.do")
                /**
                 * (3)---------------.启用跨站请求伪造(CSRF)保护,如果启用了CSRF，那么在登录或注销页面中必须包括_csrf.token
                 */
                /**
                 * 忽略app接口
                 */
                .and().csrf().ignoringAntMatchers("/app/**")
                /**
                 * 忽略在线编辑器
                 */
                .ignoringAntMatchers("/poserver.zz")
                /**
                 * 忽略在线编辑器
                 */
                .ignoringAntMatchers("/mobserver.zz").ignoringAntMatchers("*.doc")
                /**
                 * 忽略druid监控
                 */
                .ignoringAntMatchers("/druid/*")
                /**
                 * 忽略文件上传，但是需要签名验证
                 */
                .ignoringAntMatchers("*.jsp").ignoringAntMatchers("/aljoin/fastdfs/upload")
                /**
                 * 解决iframe加载问题（x-frame-options）
                 */
                .and().headers().defaultsDisabled().cacheControl();
            /**
             * session非法过期跳到登录页面
             */
            http.sessionManagement().invalidSessionUrl("/login.html");
        }

    }

    /**
     * 
     * 自定义权限提供者
     *
     * @return：DaoAuthenticationProvider
     *
     * @author：zhongjy
     *
     * @date：2017年5月9日 下午9:09:38
     */
    public CustomDaoAuthenticationProvider getAuthenticationProvider() {
        CustomDaoAuthenticationProvider provider
            = new CustomDaoAuthenticationProvider(aljoinSetting.getIsUsePwdLogin()) {

                @Override
                public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                    if ("1".equals(aljoinSetting.getIsUseValidateCode())) {
                        /**
                         * 增加对验证码的判断
                         */
                        CustomWebAuthenticationDetails form
                            = (CustomWebAuthenticationDetails)authentication.getDetails();
                        if (StringUtils.isEmpty(form.getLoginValidateCode())) {
                            /**
                             * 判断是否为空
                             */
                            throw new UsernameNotFoundException("验证码不能为空");
                        } else {
                            /**
                             * 判断是否正确
                             */
                            if (StringUtils.isEmpty(form.getLoginSessionCode())) {
                                throw new UsernameNotFoundException("服务器验证码异常");
                            } else {
                                if (!(form.getLoginSessionCode()).equals(form.getLoginValidateCode())) {
                                    throw new UsernameNotFoundException("验证码错误");
                                }
                            }
                        }
                    }
                    return super.authenticate(authentication);
                }

            };
        /**
         * 自定义用户登录
         */
        provider.setUserDetailsService(customUserDetailsService);
        /**
         * 密码加密器
         */
        provider.setPasswordEncoder(customPasswordEncoder);
        /**
         * 是否隐藏用户不存在异常(提示)
         */
        provider.setHideUserNotFoundExceptions(false);
        /**
         * 制定本地提示消息文件
         */
        provider.setMessageSource(getMessageSource());
        return provider;
    }

    /**
     * 
     * 重定义消息文件
     *
     * @return：ReloadableResourceBundleMessageSource
     *
     * @author：zhongjy
     *
     * @date：2017年5月9日 下午10:32:54
     */
    public ReloadableResourceBundleMessageSource getMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:security_messages_zh_CN");
        return messageSource;
    }

    /**
     * 
     * 自定义remember me持久化
     *
     * @return：PersistentTokenRepository
     *
     * @author：zhongjy
     *
     * @date：2017年5月10日 下午9:52:25
     */
    public PersistentTokenRepository persistentTokenRepository() {
        CustomJdbcTokenRepositoryImpl tokenRepositoryImpl = new CustomJdbcTokenRepositoryImpl();
        tokenRepositoryImpl.setDataSource(dataSource);
        return tokenRepositoryImpl;
    }

    /**
     * remember-me必须指定UserDetailsService
     */
    @Override
    protected UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }

    /**
     * 
     * CAS认证 Provider
     *
     * @return：CasAuthenticationProvider
     *
     * @author：zhongjy
     *
     * @date：2017年12月20日 下午7:22:10
     */
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(customUserDetailsService);
        // casAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        /**
         * 这里只是接口类型，实现的接口不一样，都可以的。
         */
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        casAuthenticationProvider.setKey("casAuthenticationProviderKey");
        return casAuthenticationProvider;
    }

    /**
     * 
     * 设置客户端service的属性:主要设置请求cas服务端后的回调路径,一般为主页地址，不可为登录地址.
     *
     * @return：ServiceProperties
     *
     * @author：zhongjy
     *
     * @date：2017年12月20日 下午7:22:46
     */
    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        /**
         * 设置回调的service路径，此为主页路径
         */
        serviceProperties.setService(aljoinSetting.getAppUrl() + aljoinSetting.getAppLoginUrl());
        /**
         * 对所有的未拥有ticket的访问均需要验证
         */
        serviceProperties.setAuthenticateAllArtifacts(true);
        return serviceProperties;
    }

    /**
     * 
     * 配置ticket校验器
     *
     * @return：Cas20ServiceTicketValidator
     *
     * @author：zhongjy
     *
     * @date：2017年12月20日 下午8:07:05
     */
    @Bean
    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        /**
         * 配置上服务端的校验ticket地址
         */
        return new Cas20ServiceTicketValidator(aljoinSetting.getCasUrl());
    }

    /**
     * 
     * 认证入口
     *
     * @return：CasAuthenticationEntryPoint
     *
     * @author：zhongjy
     *
     * @date：2017年12月20日 下午8:09:50
     */
    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLoginUrl(aljoinSetting.getCasLoginUrl());
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }

    /**
     * 
     * cas校验Filter处理类，包括对含有token的请求或者指定的路径请求处理
     *
     * @return：CasAuthenticationFilter
     *
     * @author：zhongjy
     *
     * @date：2017年12月20日 下午8:10:55
     */
    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setFilterProcessesUrl(aljoinSetting.getAppLoginUrl());
        return casAuthenticationFilter;
    }

    /**
     * 
     * 登录退出Filter类，转发至cas服务端进行注销
     *
     * @return：LogoutFilter
     *
     * @author：zhongjy
     *
     * @date：2017年12月20日 下午8:11:38
     */
    @Bean
    public LogoutFilter casLogoutFilter() {
        /**
         * 设置回调地址，以免注销后页面不再跳转
         */
        LogoutFilter logoutFilter
            = new LogoutFilter(aljoinSetting.getCasLogoutUrl(), new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl(aljoinSetting.getAppLogoutUrl());
        return logoutFilter;
    }

    /**
     * 
     * 单点注销Filter类，接收cas服务端发出的注销session请求
     *
     * @return：SingleSignOutFilter
     *
     * @author：zhongjy
     *
     * @date：2017年12月20日 下午8:12:05
     */
    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        /**
         * 设置cas服务端路径前缀，应用于front channel的注销请求
         */
        singleSignOutFilter.setCasServerUrlPrefix(aljoinSetting.getCasUrl());
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

}
