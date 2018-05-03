package aljoin.util;

import org.springframework.context.ApplicationContext;

/**
 * 
 * spring上下文工具类
 *
 * @author：zhongjy
 *
 * @date：2017年4月29日 上午8:45:02
 */
public class SpringContextUtil {
    private static ApplicationContext applicationContext;

    /**
     * 
     * 获取上下文
     *
     * @return：ApplicationContext
     *
     * @author：zhongjy
     *
     * @date：2017年4月29日 上午8:44:54
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 
     * 设置上下文
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年4月29日 上午8:45:37
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 
     * 通过名字获取上下文中的bean
     *
     * @return：Object
     *
     * @author：zhongjy
     *
     * @date：2017年4月29日 上午8:45:53
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 
     * 通过类型获取上下文中的bean
     *
     * @return：Object
     *
     * @author：zhongjy
     *
     * @date：2017年4月29日 上午8:46:05
     */
    public static Object getBean(Class<?> requiredType) {
        return applicationContext.getBean(requiredType);
    }
}
