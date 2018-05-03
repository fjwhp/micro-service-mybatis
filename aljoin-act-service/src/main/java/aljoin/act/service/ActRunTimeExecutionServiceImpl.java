package aljoin.act.service;

import aljoin.act.dao.entity.ActRunTimeExecution;
import aljoin.act.dao.mapper.ActRunTimeExecutionMapper;
import aljoin.act.iservice.ActRunTimeExecutionService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 执行流对象（服务实现类）
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
@Service
public class ActRunTimeExecutionServiceImpl implements ActRunTimeExecutionService {

    @Resource
    private ActRunTimeExecutionMapper mapper;

    @Override
    public Page<ActRunTimeExecution> list(PageBean pageBean, ActRunTimeExecution obj) throws Exception {
        Where<ActRunTimeExecution> where = new Where<ActRunTimeExecution>();
        where.orderBy("create_time", false);
        Page<ActRunTimeExecution> page = new Page<ActRunTimeExecution>();
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActRunTimeExecution obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public void insertExecutionBatch(List<ActRunTimeExecution> runTimeExecutionList) throws Exception {
        mapper.insertExecutionBatch(runTimeExecutionList);
    }

    @Override
    public void updateExecution(ActRunTimeExecution runTimeExecution) {
        mapper.updateExecution(runTimeExecution);
    }

}
