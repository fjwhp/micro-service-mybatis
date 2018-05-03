package aljoin.act.service;

import aljoin.act.dao.entity.ActHiProcinst;
import aljoin.act.dao.mapper.ActHiProcinstMapper;
import aljoin.act.iservice.ActHiProcinstService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * (服务实现类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
@Service
public class ActHiProcinstServiceImpl extends ServiceImpl<ActHiProcinstMapper, ActHiProcinst>
    implements ActHiProcinstService {

    @Resource
    private ActHiProcinstMapper mapper;

    @Override
    public Page<ActHiProcinst> list(PageBean pageBean, ActHiProcinst obj) throws Exception {
        Where<ActHiProcinst> where = new Where<ActHiProcinst>();
        where.orderBy("create_time", false);
        Page<ActHiProcinst> page =
            selectPage(new Page<ActHiProcinst>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActHiProcinst obj) throws Exception {
        mapper.copyObject(obj);
    }
}
