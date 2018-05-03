package aljoin.rocket.object;

/**
 * 消息消费者
 * 
 * @author zhongjy
 * @date 2018/03/26
 */
public enum MQConsumer {
    /**
     * 流程管理
     */
    MQ_CONSUMER_ALJOIN_ACT("MQ_CONSUMER_ALJOIN_ACT"),
    /**
     * 权限管理&个人中心
     */
    MQ_CONSUMER_ALJOIN_AUT("MQ_CONSUMER_ALJOIN_AUT"),
    /**
     * 协同办公
     */
    MQ_CONSUMER_ALJOIN_IOA("MQ_CONSUMER_ALJOIN_IOA"),
    /**
     * 工作计划
     */
    MQ_CONSUMER_ALJOIN_OFF("MQ_CONSUMER_ALJOIN_OFF"),
    /**
     * 考勤管理
     */
    MQ_CONSUMER_ALJOIN_ATT("MQ_CONSUMER_ALJOIN_ATT"),
    /**
     * 内部邮件
     */
    MQ_CONSUMER_ALJOIN_MAI("MQ_CONSUMER_ALJOIN_MAI"),
    /**
     * 手机短信
     */
    MQ_CONSUMER_ALJOIN_SMS("MQ_CONSUMER_ALJOIN_SMS"),
    /**
     * 公共信息
     */
    MQ_CONSUMER_ALJOIN_PUB("MQ_CONSUMER_ALJOIN_PUB"),
    /**
     * 会议管理
     */
    MQ_CONSUMER_ALJOIN_MEE("MQ_CONSUMER_ALJOIN_MEE"),
    /**
     * 办公用品
     */
    MQ_CONSUMER_ALJOIN_GOO("MQ_CONSUMER_ALJOIN_GOO"),
    /**
     * 车船管理
     */
    MQ_CONSUMER_ALJOIN_VEH("MQ_CONSUMER_ALJOIN_VEH"),
    /**
     * 固定资产
     */
    MQ_CONSUMER_ALJOIN_ASS("MQ_CONSUMER_ALJOIN_ASS"),
    /**
     * 资源管理
     */
    MQ_CONSUMER_ALJOIN_RES("MQ_CONSUMER_ALJOIN_RES"),
    /**
     * 领导看板
     */
    MQ_CONSUMER_ALJOIN_LEA("MQ_CONSUMER_ALJOIN_LEA"),
    /**
     * 系统维护
     */
    MQ_CONSUMER_ALJOIN_SMA("MQ_CONSUMER_ALJOIN_SMA"),
    /**
     * 系统管理
     */
    MQ_CONSUMER_ALJOIN_SYS("MQ_CONSUMER_ALJOIN_SYS"),
    /**
     * 任务调度
     */
    MQ_CONSUMER_ALJOIN_TIM("MQ_CONSUMER_ALJOIN_TIM");
    private String value;

    MQConsumer(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
