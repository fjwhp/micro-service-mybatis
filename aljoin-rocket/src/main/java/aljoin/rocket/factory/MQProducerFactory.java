package aljoin.rocket.factory;

import java.util.HashMap;
import java.util.Map;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;

import aljoin.config.RocketMQConfig;

/**
 * 消息生产者工厂(一个分组名称在应用的上下文中只有一个)
 * 
 * @author zhongjy
 * @date 2018/03/28
 */
public class MQProducerFactory {

    public static Map<String, MQProducer> MQ_PRODUCER_MAP = new HashMap<String, MQProducer>();

    /**
     * 获取消息生产者，如果不存在则创建并发到map中然后返回结果，如果存在则直接获取然后返回结果
     * 
     * @param producer
     * @return
     * @throws Exception
     */
    public static MQProducer getMQProducer(aljoin.rocket.object.MQProducer producer, RocketMQConfig config)
        throws Exception {

        MQProducer retObject = null;
        if (producer != null) {
            if (MQ_PRODUCER_MAP.containsKey(producer.getValue())) {
                /**
                 * 已经存在，直接返回
                 */
                retObject = MQ_PRODUCER_MAP.get(producer.getValue());
            } else {
                DefaultMQProducer defaultProducer = new DefaultMQProducer(producer.getValue());
                defaultProducer.setNamesrvAddr(config.getNamesrvAddr());
                MQ_PRODUCER_MAP.put(producer.getValue(), defaultProducer);
                retObject = defaultProducer;
            }
        }
        return retObject;
    }

    /**
     * 拒绝new对象呢
     */
    private MQProducerFactory() {

    }

}
