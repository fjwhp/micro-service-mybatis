package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinDelegateInfoHis;
import aljoin.act.dao.mapper.ActAljoinDelegateInfoHisMapper;
import aljoin.act.iservice.ActAljoinDelegateInfoHisService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 任务委托信息表（存放具体的委托任务数据）,历史表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
@Service
public class ActAljoinDelegateInfoHisServiceImpl extends
    ServiceImpl<ActAljoinDelegateInfoHisMapper, ActAljoinDelegateInfoHis> implements ActAljoinDelegateInfoHisService {

    @Resource
    private ActAljoinDelegateInfoHisMapper mapper;

    @Override
    public Page<ActAljoinDelegateInfoHis> list(PageBean pageBean, ActAljoinDelegateInfoHis obj) throws Exception {
        Where<ActAljoinDelegateInfoHis> where = new Where<ActAljoinDelegateInfoHis>();
        where.orderBy("create_time", false);
        Page<ActAljoinDelegateInfoHis> page =
            selectPage(new Page<ActAljoinDelegateInfoHis>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinDelegateInfoHis obj) throws Exception {
        mapper.copyObject(obj);
    }
}
