package aljoin.aut.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutWidgetRole;
import aljoin.aut.dao.mapper.AutWidgetRoleMapper;
import aljoin.aut.iservice.AutWidgetRoleService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 控件-角色表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-27
 */
@Service
public class AutWidgetRoleServiceImpl extends ServiceImpl<AutWidgetRoleMapper, AutWidgetRole>
    implements AutWidgetRoleService {

    @Override
    public Page<AutWidgetRole> list(PageBean pageBean, AutWidgetRole obj) throws Exception {
        Where<AutWidgetRole> where = new Where<AutWidgetRole>();
        where.orderBy("create_time", false);
        Page<AutWidgetRole> page =
            selectPage(new Page<AutWidgetRole>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }
}
