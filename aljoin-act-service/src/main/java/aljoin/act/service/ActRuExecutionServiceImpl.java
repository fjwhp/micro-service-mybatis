package aljoin.act.service;

import aljoin.act.dao.entity.ActRuExecution;
import aljoin.act.dao.mapper.ActRuExecutionMapper;
import aljoin.act.iservice.ActRuExecutionService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 执行流对象（服务实现类）
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
@Service
public class ActRuExecutionServiceImpl extends ServiceImpl<ActRuExecutionMapper, ActRuExecution>
    implements ActRuExecutionService {

    @Resource
    private ActRuExecutionMapper mapper;

    @Override
    public Page<ActRuExecution> list(PageBean pageBean, ActRuExecution obj) throws Exception {
        Where<ActRuExecution> where = new Where<ActRuExecution>();
        where.orderBy("create_time", false);
        Page<ActRuExecution> page
            = selectPage(new Page<ActRuExecution>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActRuExecution obj) throws Exception {
        mapper.copyObject(obj);
    }
}
