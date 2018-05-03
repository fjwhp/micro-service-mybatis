package aljoin.act.service;

import aljoin.act.dao.entity.ActHiTaskinst;
import aljoin.act.dao.mapper.ActHiTaskinstMapper;
import aljoin.act.iservice.ActHiTaskinstService;
import aljoin.object.PageBean;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 
 * (服务实现类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
@Service
public class ActHiTaskinstServiceImpl extends ServiceImpl<ActHiTaskinstMapper, ActHiTaskinst>
    implements ActHiTaskinstService {

    @Resource
    private ActHiTaskinstMapper mapper;

    @Override
    public Page<ActHiTaskinst> list(PageBean pageBean, ActHiTaskinst obj) throws Exception {
        Page<ActHiTaskinst> page =
            selectDoingPage(new Page<ActHiTaskinst>(pageBean.getPageNum(), pageBean.getPageSize()), obj);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActHiTaskinst obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public Page<ActHiTaskinst> selectDoingPage(Page<ActHiTaskinst> page, ActHiTaskinst wrapper) throws Exception {
        page.setRecords(this.mapper.selectDoingPage(page, wrapper));
        return page;
    }
}
