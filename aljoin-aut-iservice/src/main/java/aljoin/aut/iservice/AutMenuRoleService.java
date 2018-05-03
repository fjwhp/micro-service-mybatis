package aljoin.aut.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutMenuRole;
import aljoin.object.PageBean;

/**
 * 
 * 菜单-角色表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-27
 */
public interface AutMenuRoleService extends IService<AutMenuRole> {

    /**
     * 
     * 菜单-角色表(分页列表).
     *
     * @return：Page<AutMenuRole>
     *
     * @author：zhongjy
     *
     * @date：2017-05-27
     */
    public Page<AutMenuRole> list(PageBean pageBean, AutMenuRole obj) throws Exception;
}
