package aljoin.dao.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.MybatisConfiguration;
import com.baomidou.mybatisplus.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.mapper.LogicSqlInjector;
import com.baomidou.mybatisplus.mapper.MetaObjectHandler;
import com.baomidou.mybatisplus.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.plugins.PerformanceInterceptor;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;

import aljoin.config.AljoinSetting;
import aljoin.config.DatabaseBiz001Config;
import aljoin.config.DatabaseDefaultConfig;

/**
 * 
 * mybatisplus配置
 *
 * @author：zhongjy
 *
 * @date：2017年4月24日 下午12:26:23
 */
@Configuration
@MapperScan("aljoin.*.dao.mapper*")
@EnableConfigurationProperties({MybatisProperties.class})
public class MybatisPlusConfig {

    @Autowired
    private MybatisProperties properties;
    @Autowired
    private AljoinSetting aljoinSetting;
    @Autowired
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    @Autowired(required = false)
    private Interceptor[] interceptors;
    @Autowired(required = false)
    private DatabaseIdProvider databaseIdProvider;
    @Autowired
    private DatabaseDefaultConfig databaseDefaultConfig;
    @Autowired
    private DatabaseBiz001Config databaseBiz001Config;

    /**
     * 
     * mybatis-plus分页插件
     *
     * @return：PaginationInterceptor
     *
     * @author：zhongjy
     *
     * @date：2017年4月24日 下午12:26:37
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor page = new PaginationInterceptor();
        page.setDialectType(aljoinSetting.getDbType());
        page.setOverflowCurrent(true);
        return page;
    }

    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        OptimisticLockerInterceptor locker = new OptimisticLockerInterceptor();
        return locker;
    }

    /**
     * 
     * 性能分析插件(生产环境要去掉本插件，直接注释掉即可).
     *
     * @return：PerformanceInterceptor
     *
     * @author：zhongjy
     *
     * @date：2017年4月24日 下午12:26:37
     */
    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        // SQL 执行最大时长(毫秒)，超过自动停止运行
        performanceInterceptor.setMaxTime(10000);
        // SQL是否格式化，默认false
        performanceInterceptor.setFormat(true);
        return performanceInterceptor;
    }

    /**
     * 
     * 这里全部使用mybatis-autoconfigure 已经自动加载的资源。不手动指定 配置文件和mybatis-boot的配置文件同步
     *
     * @return：MybatisSqlSessionFactoryBean
     *
     * @author：zhongjy
     *
     * @date：2017年4月24日 下午12:26:53
     */
    @Bean
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean() {
        MybatisSqlSessionFactoryBean mybatisPlus = new MybatisSqlSessionFactoryBean();
        mybatisPlus.setDataSource(dynamicDataSource());
        mybatisPlus.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            mybatisPlus.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        mybatisPlus.setConfiguration(properties.getConfiguration());
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            mybatisPlus.setPlugins(this.interceptors);
        }
        // MP 全局配置，更多内容进入类看注释
        GlobalConfiguration globalConfig = new GlobalConfiguration();
        globalConfig.setDbType(aljoinSetting.getDbType());// 数据库类型
        // ID 策略 AUTO->`0`("数据库ID自增") INPUT->`1`(用户输入ID")
        // ID_WORKER->`2`("全局唯一ID") UUID->`3`("全局唯一ID")
        globalConfig.setIdType(2);
        // MP 属性下划线 转 驼峰 , 如果原生配置 mc.setMapUnderscoreToCamelCase(true)
        // 开启，该配置可以无。
        globalConfig.setDbColumnUnderline(true);
        // 自定义填充字段
        MetaObjectHandler metaObjectHandler = new MetaObjectHandlerImpl();
        globalConfig.setMetaObjectHandler(metaObjectHandler);

        // 逻辑删除配置
        LogicSqlInjector logicSqlInjector = new LogicSqlInjector();
        globalConfig.setSqlInjector(logicSqlInjector);
        // 逻辑删除全局值
        globalConfig.setLogicDeleteValue("1");
        // 逻辑未删除全局值
        globalConfig.setLogicNotDeleteValue("0");

        mybatisPlus.setGlobalConfig(globalConfig);
        MybatisConfiguration mc = new MybatisConfiguration();
        // 对于完全自定义的mapper需要加此项配置，才能实现下划线转驼峰
        mc.setMapUnderscoreToCamelCase(true);
        mc.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        mybatisPlus.setConfiguration(mc);
        if (this.databaseIdProvider != null) {
            mybatisPlus.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            mybatisPlus.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            mybatisPlus.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            mybatisPlus.setMapperLocations(this.properties.resolveMapperLocations());
        }
        return mybatisPlus;
    }

    /**
     * 
     * 动态数据源
     *
     * @return：DynamicDataSource
     *
     * @author：zhongjy
     *
     * @date：2017年6月15日 下午3:21:43
     */
    @Bean
    public DynamicDataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        // 候选数据源
        DataSource dataSourceDefault = DSUtil.getDataSource(databaseDefaultConfig);
        DataSource dataSourceBiz001 = DSUtil.getDataSource(databaseBiz001Config);
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("dataSource_default", dataSourceDefault);
        map.put("dataSource_biz001", dataSourceBiz001);
        dynamicDataSource.setTargetDataSources(map);
        // 默认数据源
        dynamicDataSource.setDefaultTargetDataSource(dataSourceDefault);
        return dynamicDataSource;
    }
}
