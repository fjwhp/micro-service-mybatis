package aljoin.rocket.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import aljoin.rocket.object.AljoinMQMsg;
import aljoin.rocket.object.MQProducer;
import aljoin.rocket.object.MQTag;
import aljoin.rocket.service.AljoinMQService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AljoinMQServiceImplTest {

    @Resource
    private AljoinMQService aljoinMQService;

    @Test
    public void testSendOneWay() {
        try {
            List<AljoinMQMsg> messageList = new ArrayList<AljoinMQMsg>();
            for (int i = 0; i < 1000; i++) {
                AljoinMQMsg msg = new AljoinMQMsg();
                msg.setTags(MQTag.MQ_TAG_ALJOIN_ACT);
                msg.setKeys("2018_33" + i);
                msg.setBody("唐伯虎终身编号[33" + i + "]");
                msg.setFlag(0);
                msg.setWaitStoreMsgOK(false);
                messageList.add(msg);
            }
            aljoinMQService.sendOneWay(messageList, MQProducer.MQ_PRODUCER_ALJOIN_ACT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendSynch() {
        try {
            List<AljoinMQMsg> messageList = new ArrayList<AljoinMQMsg>();
            for (int i = 0; i < 10; i++) {
                AljoinMQMsg msg = new AljoinMQMsg();
                msg.setTags(MQTag.MQ_TAG_ALJOIN_ACT);
                msg.setKeys("ww2018_9528");
                msg.setBody("ww唐伯虎终身编号[9528]");
                msg.setFlag(0);
                msg.setWaitStoreMsgOK(false);
                messageList.add(msg);
            }
            aljoinMQService.sendSynch(messageList, MQProducer.MQ_PRODUCER_ALJOIN_ACT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendAsynch() {
        try {
            List<AljoinMQMsg> messageList = new ArrayList<AljoinMQMsg>();
            for (int i = 0; i < 10000; i++) {
                AljoinMQMsg msg = new AljoinMQMsg();
                msg.setTags(MQTag.MQ_TAG_ALJOIN_ACT);
                msg.setKeys("aaa2018_33" + i);
                msg.setBody("aaa唐伯虎终身编号[33" + i + "]");
                msg.setFlag(0);
                msg.setWaitStoreMsgOK(false);
                messageList.add(msg);
            }
            aljoinMQService.sendAsynch(messageList, MQProducer.MQ_PRODUCER_ALJOIN_ACT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
