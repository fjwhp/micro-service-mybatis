package aljoin.act.service;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.entity.ActRuIdentitylink;
import aljoin.act.iservice.ActRuIdentitylinkService;
import aljoin.object.PageBean;
import aljoin.util.JsonUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActRuIdentitylinkServiceImplTest {

    @Resource
    private ActRuIdentitylinkService actRuIdentitylinkService;

    @Test
    public void testList() throws Exception {
        PageBean pageBean = new PageBean();
        pageBean.setPageNum(1);
        pageBean.setPageSize(10);
        Page<ActRuIdentitylink> pageList = actRuIdentitylinkService.list(pageBean, null);
        System.out.println(JsonUtil.obj2str(pageList));
    }

    @Test
    public void testPhysicsDeleteById() {
        fail("Not yet implemented");
    }

    @Test
    public void testCopyObject() {
        fail("Not yet implemented");
    }

}
