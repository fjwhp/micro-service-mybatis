package aljoin.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestRedis {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @SuppressWarnings("unchecked")
    @Test
    public void testString() {

        stringRedisTemplate.opsForValue().set("aaa", "12345a", 30, TimeUnit.SECONDS);
        System.out.println(stringRedisTemplate.opsForValue().get("aaa"));

        U user1 = new U();
        user1.setUserName("admin1");
        U user2 = new U();
        user2.setUserName("admin2");
        U user3 = new U();
        user3.setUserName("admin3");
        List<U> userList = new ArrayList<U>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        redisTemplate.opsForValue().set("aaaList", userList, 30, TimeUnit.SECONDS);
        List<U> userList2 = (List<U>)redisTemplate.opsForValue().get("aaaList");
        for (U autUser : userList2) {
            System.out.println(autUser.getUserName());
        }

    }

}
