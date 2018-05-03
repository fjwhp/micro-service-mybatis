package aljoin.rocket.object;

/**
 * 消息标签
 * 
 * @author zhongjy
 * @date 2018/03/26
 */
public enum MQTag {
    /**
     * 流程管理
     */
    MQ_TAG_ALJOIN_ACT("MQ_TAG_ALJOIN_ACT"),
    /**
     * 权限管理&个人中心
     */
    MQ_TAG_ALJOIN_AUT("MQ_TAG_ALJOIN_AUT"),
    /**
     * 协同办公
     */
    MQ_TAG_ALJOIN_IOA("MQ_TAG_ALJOIN_IOA"),
    /**
     * 工作计划
     */
    MQ_TAG_ALJOIN_OFF("MQ_TAG_ALJOIN_OFF"),
    /**
     * 考勤管理
     */
    MQ_TAG_ALJOIN_ATT("MQ_TAG_ALJOIN_ATT"),
    /**
     * 内部邮件
     */
    MQ_TAG_ALJOIN_MAI("MQ_TAG_ALJOIN_MAI"),
    /**
     * 手机短信
     */
    MQ_TAG_ALJOIN_SMS("MQ_TAG_ALJOIN_SMS"),
    /**
     * 公共信息
     */
    MQ_TAG_ALJOIN_PUB("MQ_TAG_ALJOIN_PUB"),
    /**
     * 会议管理
     */
    MQ_TAG_ALJOIN_MEE("MQ_TAG_ALJOIN_MEE"),
    /**
     * 办公用品
     */
    MQ_TAG_ALJOIN_GOO("MQ_TAG_ALJOIN_GOO"),
    /**
     * 车船管理
     */
    MQ_TAG_ALJOIN_VEH("MQ_TAG_ALJOIN_VEH"),
    /**
     * 固定资产
     */
    MQ_TAG_ALJOIN_ASS("MQ_TAG_ALJOIN_ASS"),
    /**
     * 资源管理
     */
    MQ_TAG_ALJOIN_RES("MQ_TAG_ALJOIN_RES"),
    /**
     * 领导看板
     */
    MQ_TAG_ALJOIN_LEA("MQ_TAG_ALJOIN_LEA"),
    /**
     * 系统维护
     */
    MQ_TAG_ALJOIN_SMA("MQ_TAG_ALJOIN_SMA"),
    /**
     * 系统管理
     */
    MQ_TAG_ALJOIN_SYS("MQ_TAG_ALJOIN_SYS"),
    /**
     * 任务调度
     */
    MQ_TAG_ALJOIN_TIM("MQ_TAG_ALJOIN_TIM");
    private String value;

    MQTag(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
