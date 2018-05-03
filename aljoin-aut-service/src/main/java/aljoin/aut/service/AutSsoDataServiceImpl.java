package aljoin.aut.service;

import aljoin.aut.dao.entity.AutSsoData;
import aljoin.aut.dao.mapper.AutSsoDataMapper;
import aljoin.aut.iservice.AutSsoDataService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 单点登录数据同步表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-12-11
 */
@Service
public class AutSsoDataServiceImpl extends ServiceImpl<AutSsoDataMapper, AutSsoData> implements AutSsoDataService {

    @Resource
    private AutSsoDataMapper mapper;

    @Override
    public Page<AutSsoData> list(PageBean pageBean, AutSsoData obj) throws Exception {
        Where<AutSsoData> where = new Where<AutSsoData>();
        where.orderBy("create_time", false);
        Page<AutSsoData> page = selectPage(new Page<AutSsoData>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(AutSsoData obj) throws Exception {
        mapper.copyObject(obj);
    }
}
