package aljoin.rocket.factory;

import java.util.HashMap;
import java.util.Map;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQConsumer;

import aljoin.config.RocketMQConfig;

/**
 * 消息消费者工厂(一个分组名称在应用的上下文中只有一个)
 * 
 * @author zhongjy
 * @date 2018/03/28
 */
public class MQConsumerFactory {
    public static Map<String, MQConsumer> MQ_CONSUMER_MAP = new HashMap<String, MQConsumer>();

    /**
     * 获取消息生产者，如果不存在则创建并发到map中然后返回结果，如果存在则直接获取然后返回结果
     * 
     * @param consumer
     * @return
     * @throws Exception
     */
    public static MQConsumer getMQConsumer(aljoin.rocket.object.MQConsumer consumer, RocketMQConfig config,
        boolean isPush) throws Exception {

        MQConsumer retObject = null;
        if (consumer != null) {
            if (MQ_CONSUMER_MAP.containsKey(consumer.getValue())) {
                /**
                 * 已经存在，直接返回
                 */
                retObject = MQ_CONSUMER_MAP.get(consumer.getValue());
                if (isPush) {
                    /**
                     * 如果在获取推送消息的时候，取到的是拉取消息，抛异常
                     */
                    if (retObject instanceof DefaultMQPullConsumer) {
                        throw new Exception("已经存在消费者分组[" + consumer.getValue() + "]是pull类型，不能再获取该分组的push类型");
                    }
                } else {
                    /**
                     * 如果在获取拉取消息的时候，取到的是推送消息，抛异常
                     */
                    if (retObject instanceof DefaultMQPushConsumer) {
                        throw new Exception("已经存在消费者分组[" + consumer.getValue() + "]是push类型，不能再获取该分组的pull类型");
                    }
                }

            } else {
                if (isPush) {
                    /**
                     * 推送消息
                     */
                    DefaultMQPushConsumer defaultConsumer = new DefaultMQPushConsumer(consumer.getValue());
                    defaultConsumer.setNamesrvAddr(config.getNamesrvAddr());
                    MQ_CONSUMER_MAP.put(consumer.getValue(), defaultConsumer);
                    retObject = defaultConsumer;
                } else {
                    /**
                     * 拉取消息(比较少用)
                     */
                    DefaultMQPullConsumer pullConsumer = new DefaultMQPullConsumer(consumer.getValue());
                    pullConsumer.setNamesrvAddr(config.getNamesrvAddr());
                    MQ_CONSUMER_MAP.put(consumer.getValue(), pullConsumer);
                    retObject = pullConsumer;
                }

            }
        }
        return retObject;
    }

    /**
     * 拒绝new对象呢
     */
    private MQConsumerFactory() {

    }
}
