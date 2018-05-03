package aljoin.aut.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutMenuRole;
import aljoin.aut.dao.mapper.AutMenuRoleMapper;
import aljoin.aut.iservice.AutMenuRoleService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 菜单-角色表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-27
 */
@Service
public class AutMenuRoleServiceImpl extends ServiceImpl<AutMenuRoleMapper, AutMenuRole> implements AutMenuRoleService {

    @Override
    public Page<AutMenuRole> list(PageBean pageBean, AutMenuRole obj) throws Exception {
        Where<AutMenuRole> where = new Where<AutMenuRole>();
        where.orderBy("create_time", false);
        Page<AutMenuRole> page =
            selectPage(new Page<AutMenuRole>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }
}
