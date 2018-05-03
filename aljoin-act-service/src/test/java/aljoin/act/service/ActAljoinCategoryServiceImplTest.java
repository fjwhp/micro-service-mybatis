package aljoin.act.service;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.iservice.ActAljoinCategoryService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActAljoinCategoryServiceImplTest {

    @Resource
    private ActAljoinCategoryService actAljoinCategoryService;

    @Test
    public void test() {
        ActAljoinCategory actAljoinCategory = new ActAljoinCategory();
        actAljoinCategory.setCategoryName("请假类审批流程");
        actAljoinCategory.setIsActive(1);
        actAljoinCategory.setCategoryRank(1);
        actAljoinCategoryService.insert(actAljoinCategory);
    }

}
