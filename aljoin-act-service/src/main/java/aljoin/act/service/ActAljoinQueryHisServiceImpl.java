package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.dao.mapper.ActAljoinQueryHisMapper;
import aljoin.act.iservice.ActAljoinQueryHisService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 流程查询表(历史表)(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-02
 */
@Service
public class ActAljoinQueryHisServiceImpl extends ServiceImpl<ActAljoinQueryHisMapper, ActAljoinQueryHis>
    implements ActAljoinQueryHisService {

    @Resource
    private ActAljoinQueryHisMapper mapper;

    @Override
    public Page<ActAljoinQueryHis> list(PageBean pageBean, ActAljoinQueryHis obj) throws Exception {
        Where<ActAljoinQueryHis> where = new Where<ActAljoinQueryHis>();
        where.orderBy("create_time", false);
        Page<ActAljoinQueryHis> page =
            selectPage(new Page<ActAljoinQueryHis>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinQueryHis obj) throws Exception {
        mapper.copyObject(obj);
    }
}
