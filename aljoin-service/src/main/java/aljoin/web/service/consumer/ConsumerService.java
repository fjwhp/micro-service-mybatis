package aljoin.web.service.consumer;

import java.util.List;

import javax.annotation.Resource;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aljoin.config.RocketMQConfig;
import aljoin.rocket.factory.MQConsumerFactory;
import aljoin.rocket.object.MQConsumer;
import aljoin.rocket.object.MQTag;

/**
 * 广播消费者
 * 
 * @author zhongjy
 * @date 2018/04/02
 */
@Service
public class ConsumerService {

    private final static Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    @Resource
    private RocketMQConfig rocketMQConfig;

    /**
     * 初始化方法，系统启动后会调用本方法
     * 
     * @throws Exception
     */
    public void initConsumer() throws Exception {
        demo01();
        demo02();

        logger.info("所有消息消费者启动完成...");
    }

    /**
     * 正常消费者
     * 
     * @throws Exception
     */
    public void demo01() throws Exception {
        DefaultMQPushConsumer consumer = (DefaultMQPushConsumer)MQConsumerFactory
            .getMQConsumer(MQConsumer.MQ_CONSUMER_ALJOIN_ACT, rocketMQConfig, true);

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        /**
         * 广播，所有节点的消费者都可以消费:MessageModel.BROADCASTING(广播)，MessageModel.CLUSTERING(负载均衡),
         */
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.subscribe(rocketMQConfig.getTopic(), MQTag.MQ_TAG_ALJOIN_ACT.getValue());

        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf(Thread.currentThread().getName() + " 正常消费者，收到新消息: " + msgs + "%n");
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        System.out.printf("正常消费者启动.%n");
    }

    /**
     * 顺序消费者
     */
    public void demo02() throws Exception {
        DefaultMQPushConsumer consumer = (DefaultMQPushConsumer)MQConsumerFactory
            .getMQConsumer(MQConsumer.MQ_CONSUMER_ALJOIN_AUT, rocketMQConfig, true);

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        /**
         * 广播，所有节点的消费者都可以消费:MessageModel.BROADCASTING(广播)，MessageModel.CLUSTERING(负载均衡),
         */
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.subscribe(rocketMQConfig.getTopic(), MQTag.MQ_TAG_ALJOIN_ACT.getValue());

        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                context.setAutoCommit(false);
                System.out.printf(Thread.currentThread().getName() + " 顺序消费者，收到新消息: " + msgs + "%n");
                return ConsumeOrderlyStatus.SUCCESS;

            }
        });

        consumer.start();
        System.out.printf("顺序消费者启动.%n");
    }

}
