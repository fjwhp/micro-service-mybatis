package aljoin.aut.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AutUserServiceImplTest {

    @Resource
    private AutUserService autUserService;

    @Test
    public void testGetMyUserList() throws Exception {
        List<AutUser> list = autUserService.getMyUserList();
        for (AutUser autUser : list) {
            System.out.println(autUser.getUserName());
        }
    }

}
