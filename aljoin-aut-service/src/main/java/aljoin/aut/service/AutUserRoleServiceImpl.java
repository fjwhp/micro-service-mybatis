package aljoin.aut.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.mapper.AutUserRoleMapper;
import aljoin.aut.iservice.AutUserRoleService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 用户-角色关联表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-27
 */
@Service
public class AutUserRoleServiceImpl extends ServiceImpl<AutUserRoleMapper, AutUserRole> implements AutUserRoleService {

    @Resource
    private AutUserRoleService autUserRoleService;

    @Override
    public Page<AutUserRole> list(PageBean pageBean, AutUserRole obj) throws Exception {
        Where<AutUserRole> where = new Where<AutUserRole>();
        where.orderBy("create_time", false);
        Page<AutUserRole> page =
            selectPage(new Page<AutUserRole>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

}
