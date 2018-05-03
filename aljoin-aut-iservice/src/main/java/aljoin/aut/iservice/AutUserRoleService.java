package aljoin.aut.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutUserRole;
import aljoin.object.PageBean;

/**
 * 
 * 用户-角色关联表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-27
 */
public interface AutUserRoleService extends IService<AutUserRole> {

    /**
     * 
     * 用户-角色关联表(分页列表).
     *
     * @return：Page<AutUserRole>
     *
     * @author：zhongjy
     *
     * @date：2017-05-27
     */
    public Page<AutUserRole> list(PageBean pageBean, AutUserRole obj) throws Exception;

}
