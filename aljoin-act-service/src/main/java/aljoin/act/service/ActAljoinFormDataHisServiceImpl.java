package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinFormDataHis;
import aljoin.act.dao.mapper.ActAljoinFormDataHisMapper;
import aljoin.act.iservice.ActAljoinFormDataHisService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 历史表单数据表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-27
 */
@Service
public class ActAljoinFormDataHisServiceImpl extends ServiceImpl<ActAljoinFormDataHisMapper, ActAljoinFormDataHis>
    implements ActAljoinFormDataHisService {

    @Resource
    private ActAljoinFormDataHisMapper mapper;

    @Override
    public Page<ActAljoinFormDataHis> list(PageBean pageBean, ActAljoinFormDataHis obj) throws Exception {
        Where<ActAljoinFormDataHis> where = new Where<ActAljoinFormDataHis>();
        where.orderBy("create_time", false);
        Page<ActAljoinFormDataHis> page =
            selectPage(new Page<ActAljoinFormDataHis>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinFormDataHis obj) throws Exception {
        mapper.copyObject(obj);
    }
}
