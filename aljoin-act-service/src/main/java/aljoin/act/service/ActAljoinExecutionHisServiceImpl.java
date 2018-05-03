package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinExecutionHis;
import aljoin.act.dao.mapper.ActAljoinExecutionHisMapper;
import aljoin.act.iservice.ActAljoinExecutionHisService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 流程实例(执行流)表(历史表)(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2018-01-19
 */
@Service
public class ActAljoinExecutionHisServiceImpl extends ServiceImpl<ActAljoinExecutionHisMapper, ActAljoinExecutionHis>
    implements ActAljoinExecutionHisService {

    @Resource
    private ActAljoinExecutionHisMapper mapper;

    @Override
    public Page<ActAljoinExecutionHis> list(PageBean pageBean, ActAljoinExecutionHis obj) throws Exception {
        Where<ActAljoinExecutionHis> where = new Where<ActAljoinExecutionHis>();
        where.orderBy("create_time", false);
        Page<ActAljoinExecutionHis> page =
            selectPage(new Page<ActAljoinExecutionHis>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinExecutionHis obj) throws Exception {
        mapper.copyObject(obj);
    }
}
