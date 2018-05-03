package aljoin.act.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActIdMembership;
import aljoin.act.dao.mapper.ActIdMembershipMapper;
import aljoin.act.dao.mapper.ActRuIdentitylinkMapper;
import aljoin.act.iservice.ActIdMembershipService;
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
public class ActIdMembershipServiceImpl extends ServiceImpl<ActIdMembershipMapper, ActIdMembership>
    implements ActIdMembershipService {

    @Resource
    private ActRuIdentitylinkMapper mapper;

    @Override
    public Page<ActIdMembership> list(PageBean pageBean, ActIdMembership obj) throws Exception {
        Where<ActIdMembership> where = new Where<ActIdMembership>();
        // where.orderBy("create_time", false);
        Page<ActIdMembership> page =
            selectPage(new Page<ActIdMembership>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }
}
