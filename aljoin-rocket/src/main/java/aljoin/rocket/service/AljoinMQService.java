package aljoin.rocket.service;

import java.util.List;

import aljoin.object.RetMsg;
import aljoin.rocket.object.AljoinMQMsg;
import aljoin.rocket.object.MQProducer;

/**
 * 消息队列接口
 * 
 * @author zhongjy
 * @date 2018/03/23
 */
public interface AljoinMQService {

    /**
     * 单程发送
     * 
     * @param messageList 消息类表
     * @param mQPtype消息生产者分组名称
     * @return
     * @throws Exception
     */
    public RetMsg sendOneWay(List<AljoinMQMsg> messageList, MQProducer producerGroup) throws Exception;

    /**
     * 同步发送
     * 
     * @param messageList 消息类表
     * @param mQPtype消息生产者分组名称
     * @return
     * @throws Exception
     */
    public RetMsg sendSynch(List<AljoinMQMsg> messageList, MQProducer producerGroup) throws Exception;

    /**
     * 异步发送
     * 
     * @param messageList 消息类表
     * @param mQPtype消息生产者分组名称
     * @return
     * @throws Exception
     */
    public RetMsg sendAsynch(List<AljoinMQMsg> messageList, MQProducer producerGroup) throws Exception;

    /**
     * 发送顺序消息
     * 
     * @param messageList 消息类表
     * @param mQPtype消息生产者分组名称
     * @return
     * @throws Exception
     */
    public RetMsg sendOrderly(List<AljoinMQMsg> messageList, MQProducer producerGroup) throws Exception;
}
