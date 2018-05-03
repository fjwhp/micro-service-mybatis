package aljoin.act.service;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import aljoin.act.dao.entity.ActAljoinDelegate;
import aljoin.act.dao.object.DelegateDO;
import aljoin.act.iservice.ActAljoinDelegateService;
import aljoin.util.JsonUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActAljoinDelegateServiceImplTest {

    @Resource
    private ActAljoinDelegateService actAljoinDelegateService;

    @Test
    public void testGetLeafUserIds() throws Exception {
        ActAljoinDelegate actAljoinDelegate = actAljoinDelegateService.selectById(930633962713763840L);
        Map<Long, DelegateDO> idList =
            actAljoinDelegateService.getDelegateLinks(actAljoinDelegate, new Date(), null, null, null);
        System.out.println(JsonUtil.obj2str(idList));
    }

    @Test
    public void testAddDelegateBiz() throws Exception {
        ActAljoinDelegate actAljoinDelegate = actAljoinDelegateService.selectById(928817704290152448L);
        actAljoinDelegateService.addDelegateBiz(actAljoinDelegate, null);
    }

    @Test
    public void testStopDelegateBiz() throws Exception {
        fail("Not yet implemented");
    }

    @Test
    public void testTimerDelegateBiz() throws Exception {
        fail("Not yet implemented");
    }

    @Test
    public void testEventCreateDelegateBiz() throws Exception {
        fail("Not yet implemented");
    }

    @Test
    public void testEventCompletedDelegateBiz() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetBottomLevel() throws Exception {
        ActAljoinDelegate actAljoinDelegate = actAljoinDelegateService.selectById(930633962713763840L);
        Map<Long, DelegateDO> idList = actAljoinDelegateService.getBottomLevel(actAljoinDelegate, null);
        System.out.println(JsonUtil.obj2str(idList));
    }

    @Test
    public void testGetTopLevel() throws Exception {
        ActAljoinDelegate actAljoinDelegate = actAljoinDelegateService.selectById(930633962713763840L);
        Map<Long, DelegateDO> idList = actAljoinDelegateService.getTopLevel(actAljoinDelegate, null);
        System.out.println(JsonUtil.obj2str(idList));
    }

    @Test
    public void testGetAllDelegateLinks() throws Exception {
        ActAljoinDelegate obj = new ActAljoinDelegate();
        obj.setOwnerUserId(903158809152704512L);
        obj.setAssigneeUserIds("903157814179909632");
        actAljoinDelegateService.getAllDelegateLinks(obj);
    }

}
