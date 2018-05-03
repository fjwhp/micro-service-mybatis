package aljoin.dao.config;

/**
 * 
 * 数据库上下文(线程安全).
 *
 * @author：zhongjy
 * 
 * @date：2017年6月15日 下午3:18:32
 */
public class DbContextHolder {

    private static final ThreadLocal<Object> CONTEXT_HOLDER = new ThreadLocal<Object>();

    /**
     * 
     * 设置数据源
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年6月15日 下午3:15:04
     */
    public static void setDbType(DBTypeEnum dbTypeEnum) {
        CONTEXT_HOLDER.set(dbTypeEnum.getValue());
    }

    /**
     * 
     * 取得当前数据源
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年6月15日 下午3:15:10
     */
    public static Object getDbType() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 
     * 清除上下文数据
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年6月15日 下午3:15:17
     */
    public static void clearDbType() {
        CONTEXT_HOLDER.remove();
    }
}
