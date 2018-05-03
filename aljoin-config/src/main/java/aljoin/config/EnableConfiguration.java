package aljoin.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 关联配置文件
 * 
 * @author zhongjy
 * @date 2018/04/03
 */
@Configuration
@EnableConfigurationProperties({AljoinSetting.class, DatabaseDefaultConfig.class, DatabaseBiz001Config.class,
    FastDFSConfig.class, RocketMQConfig.class, TaskThreadPoolConfig.class})
public class EnableConfiguration {

}
