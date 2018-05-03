package aljoin.aut.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutDataDetail;
import aljoin.aut.dao.mapper.AutDataDetailMapper;
import aljoin.aut.iservice.AutDataDetailService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * (服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-12-14
 */
@Service
public class AutDataDetailServiceImpl implements AutDataDetailService {

    @Resource
    private AutDataDetailMapper mapper;

    @Override
    public Page<AutDataDetail> list(PageBean pageBean, AutDataDetail obj) throws Exception {
        Where<AutDataDetail> where = new Where<AutDataDetail>();
        where.orderBy("create_time", false);
        Page<AutDataDetail> page = new Page<AutDataDetail>();
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(AutDataDetail obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public List<AutDataDetail> selectList() throws Exception {
        return mapper.getList();
    }

    @Override
    public void insert(AutDataDetail autDataDetail) throws Exception {
        mapper.add(autDataDetail);
    }

    @Override
    public void insertBatch(List<AutDataDetail> autDataDetails) throws Exception {
        mapper.insertBatch(autDataDetails);
    }

    @Override
    public void physicsDeleteBatchById(List<Long> ids) throws Exception {
        mapper.physicsDeleteBatchById(ids);
    }

}
