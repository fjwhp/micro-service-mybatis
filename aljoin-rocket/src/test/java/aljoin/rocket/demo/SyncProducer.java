package aljoin.rocket.demo;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 同步例子d
 * 
 * @author zhongjy
 * @date 2018/03/21
 */
public class SyncProducer {
    public static void main(String[] args) throws Exception {
        // Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("T_139Test");
        producer.setNamesrvAddr("192.168.0.223:9876");
        // Launch the instance.
        producer.start();
        for (int i = 0; i < 100; i++) {
            // Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("CHINA_001" /* Topic */, "TagA" /* Tag */,
                ("春天的故事"+i+" RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            // Call send message to deliver message to one of brokers.
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        // Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }
}
