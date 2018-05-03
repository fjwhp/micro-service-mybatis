package aljoin.act.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActHiActinst;
import aljoin.act.dao.entity.ActHiProcinst;
import aljoin.act.dao.mapper.ActHiActinstMapper;
import aljoin.act.iservice.ActHiActinstService;
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
public class ActHiActinstServiceImpl extends ServiceImpl<ActHiActinstMapper, ActHiActinst>
    implements ActHiActinstService {

    @Resource
    private ActHiActinstMapper mapper;

    @Override
    public Page<ActHiActinst> list(PageBean pageBean, ActHiProcinst obj) throws Exception {
        Where<ActHiActinst> where = new Where<ActHiActinst>();
        where.orderBy("create_time", false);
        Page<ActHiActinst> page =
            selectPage(new Page<ActHiActinst>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActHiActinst obj) throws Exception {
        mapper.copyObject(obj);
    }
}
