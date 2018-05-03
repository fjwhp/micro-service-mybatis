package aljoin.act.service;

import aljoin.act.dao.entity.ActRuIdentitylink;
import aljoin.act.dao.mapper.ActRuIdentitylinkMapper;
import aljoin.act.iservice.ActRuIdentitylinkService;
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
 * @author：zhongjy.
 * 
 * @date： 2017-12-12
 */
@Service
public class ActRuIdentitylinkServiceImpl extends ServiceImpl<ActRuIdentitylinkMapper, ActRuIdentitylink>
    implements ActRuIdentitylinkService {

    @Resource
    private ActRuIdentitylinkMapper mapper;

    @Override
    public Page<ActRuIdentitylink> list(PageBean pageBean, ActRuIdentitylink obj) throws Exception {
        Where<ActRuIdentitylink> where = new Where<ActRuIdentitylink>();
        // where.orderBy("create_time", false);
        Page<ActRuIdentitylink> page =
            selectPage(new Page<ActRuIdentitylink>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(String id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActRuIdentitylink obj) throws Exception {
        mapper.copyObject(obj);
    }
}
