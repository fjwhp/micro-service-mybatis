package aljoin.coder;

import com.baomidou.mybatisplus.generator.config.StrategyConfig;

/**
 * 
 * 自定义代码生成策略器(重写其中的includeSuperEntityColumns方法).
 *
 * @author：zhongjy
 *
 * @date：2017年5月21日 上午11:22:59
 */
public class CustomStrategyConfig extends StrategyConfig {

    @Override
    public boolean includeSuperEntityColumns(String fieldName) {
        if (null != getSuperEntityColumns()) {
            for (String column : getSuperEntityColumns()) {
                if (column.equals(fieldName)) {
                    return true;
                }
            }
        }
        return false;
    }

}
