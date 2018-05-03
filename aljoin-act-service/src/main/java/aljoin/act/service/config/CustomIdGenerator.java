package aljoin.act.service.config;

import org.activiti.engine.impl.cfg.IdGenerator;

import com.baomidou.mybatisplus.toolkit.IdWorker;

/**
 * 
 * 更改activiti表id生成器
 *
 * @author：zhongjy
 * 
 * @date：2018年1月3日 下午4:34:16
 */
public class CustomIdGenerator implements IdGenerator {

    @Override
    public String getNextId() {
        return String.valueOf(IdWorker.getId());
    }
}
