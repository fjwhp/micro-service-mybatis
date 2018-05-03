package aljoin.dao.config;

/**
 * 
 * 数据源枚举
 *
 * @author：zhongjy
 *
 * @date：2017年6月16日 下午9:13:43
 */
public enum DBTypeEnum {
    /**
     * 默认数据源
     */
    DS_DEF("dataSource_default"),
    /**
     * 业务数据源001
     */
    DS_BIZ001("dataSource_biz001");

    private String value;

    DBTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
