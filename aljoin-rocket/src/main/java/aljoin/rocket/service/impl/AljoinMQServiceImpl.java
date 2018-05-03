package aljoin.rocket.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aljoin.config.RocketMQConfig;
import aljoin.object.RetMsg;
import aljoin.rocket.factory.MQProducerFactory;
import aljoin.rocket.object.AljoinMQMsg;
import aljoin.rocket.object.MQProducer;
import aljoin.rocket.service.AljoinMQService;

/**
 * 消息队列实现类
 * 
 * @author zhongjy
 * @date 2018/03/23
 */
@Service
public class AljoinMQServiceImpl implements AljoinMQService {

    private final static Logger logger = LoggerFactory.getLogger(AljoinMQServiceImpl.class);

    @Resource
    private RocketMQConfig rocketMQConfig;

    @Override
    public RetMsg sendOneWay(List<AljoinMQMsg> messageList, MQProducer producerGroup) throws Exception {
        RetMsg retMsg = new RetMsg();
        retMsg.setCode(0);
        /**
         * 根据分组名称实例化消息生产者
         */
        DefaultMQProducer producer = (DefaultMQProducer)MQProducerFactory.getMQProducer(producerGroup, rocketMQConfig);
        /**
         * 发送失败重试
         */
        producer.setRetryTimesWhenSendFailed(3);
        /**
         * 运行实例
         */
        producer.start();
        /**
         * 发送消息并构造返回结果
         */
        for (AljoinMQMsg aljoinMQMsg : messageList) {
            /**
             * 创建消息：TOPIC TAG KEY BODY
             */
            Message msg = new Message(rocketMQConfig.getTopic(), aljoinMQMsg.getTags().getValue(),
                aljoinMQMsg.getKeys(), aljoinMQMsg.getFlag(),
                (aljoinMQMsg.getBody()).getBytes(RemotingHelper.DEFAULT_CHARSET), aljoinMQMsg.isWaitStoreMsgOK());
            producer.sendOneway(msg);
        }
        /**
         * 关闭生产者
         */
        producer.shutdown();
        return retMsg;
    }

    @Override
    public RetMsg sendSynch(List<AljoinMQMsg> messageList, MQProducer producerGroup) throws Exception {
        RetMsg retMsg = new RetMsg();
        retMsg.setCode(0);
        /**
         * 根据分组名称实例化消息生产者
         */
        DefaultMQProducer producer = (DefaultMQProducer)MQProducerFactory.getMQProducer(producerGroup, rocketMQConfig);
        /**
         * 发送失败重试
         */
        producer.setRetryTimesWhenSendFailed(3);
        /**
         * 运行实例
         */
        producer.start();
        /**
         * 发送消息并构造返回结果
         */
        List<SendResult> sendResultList = new ArrayList<SendResult>();
        for (AljoinMQMsg aljoinMQMsg : messageList) {
            /**
             * 创建消息：TOPIC TAG KEY BODY
             */
            Message msg = new Message(rocketMQConfig.getTopic(), aljoinMQMsg.getTags().getValue(),
                aljoinMQMsg.getKeys(), aljoinMQMsg.getFlag(),
                (aljoinMQMsg.getBody()).getBytes(RemotingHelper.DEFAULT_CHARSET), aljoinMQMsg.isWaitStoreMsgOK());
            SendResult sendResult = producer.send(msg);
            sendResultList.add(sendResult);
            logger.info(sendResult.toString());
        }
        retMsg.setObject(sendResultList);
        /**
         * 关闭生产者
         */
        producer.shutdown();
        return retMsg;
    }

    @Override
    public RetMsg sendAsynch(List<AljoinMQMsg> messageList, MQProducer producerGroup) throws Exception {
        RetMsg retMsg = new RetMsg();
        retMsg.setCode(0);
        /**
         * 根据分组名称实例化消息生产者
         */
        DefaultMQProducer producer = (DefaultMQProducer)MQProducerFactory.getMQProducer(producerGroup, rocketMQConfig);
        /**
         * 发送失败重试
         */
        producer.setRetryTimesWhenSendFailed(3);
        /**
         * 运行实例
         */
        producer.start();
        /**
         * 发送消息并构造返回结果
         */
        for (AljoinMQMsg aljoinMQMsg : messageList) {
            /**
             * 创建消息：TOPIC TAG KEY BODY
             */
            Message msg = new Message(rocketMQConfig.getTopic(), aljoinMQMsg.getTags().getValue(),
                aljoinMQMsg.getKeys(), aljoinMQMsg.getFlag(),
                (aljoinMQMsg.getBody()).getBytes(RemotingHelper.DEFAULT_CHARSET), aljoinMQMsg.isWaitStoreMsgOK());
            /**
             * 发送&回调
             */
            producer.send(msg, new SendCallback() {
                /**
                 * 成功回调
                 */
                @Override
                public void onSuccess(SendResult sendResult) {
                    logger.info(sendResult.toString());
                }

                /**
                 * 异常回调
                 */
                @Override
                public void onException(Throwable throwable) {
                    logger.error("消息发送失败", throwable);
                }
            });
        }
        /**
         * 关闭生产者
         */
        producer.shutdown();
        return retMsg;
    }

    @Override
    public RetMsg sendOrderly(List<AljoinMQMsg> messageList, MQProducer producerGroup) throws Exception {
        RetMsg retMsg = new RetMsg();
        retMsg.setCode(0);
        /**
         * 根据分组名称实例化消息生产者
         */
        DefaultMQProducer producer = (DefaultMQProducer)MQProducerFactory.getMQProducer(producerGroup, rocketMQConfig);
        /**
         * 发送失败重试
         */
        producer.setRetryTimesWhenSendFailed(3);
        /**
         * 运行实例
         */
        producer.start();
        /**
         * 发送消息并构造返回结果
         */
        for (AljoinMQMsg aljoinMQMsg : messageList) {
            /**
             * 创建消息：TOPIC TAG KEY BODY
             */
            Message msg = new Message(rocketMQConfig.getTopic(), aljoinMQMsg.getTags().getValue(),
                aljoinMQMsg.getKeys(), aljoinMQMsg.getFlag(),
                (aljoinMQMsg.getBody()).getBytes(RemotingHelper.DEFAULT_CHARSET), aljoinMQMsg.isWaitStoreMsgOK());
            /**
             * 发送&回调
             */
            producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer id = (Integer)arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, aljoinMQMsg.getSingleQueueKey());
        }
        /**
         * 关闭生产者
         */
        producer.shutdown();
        return retMsg;
    }

}
