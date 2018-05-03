package aljoin.aut.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutWidgetRole;
import aljoin.object.PageBean;

/**
 * 
 * 控件-角色表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-27
 */
public interface AutWidgetRoleService extends IService<AutWidgetRole> {

    /**
     * 
     * 控件-角色表(分页列表).
     *
     * @return：Page<AutWidgetRole>
     *
     * @author：zhongjy
     *
     * @date：2017-05-27
     */
    public Page<AutWidgetRole> list(PageBean pageBean, AutWidgetRole obj) throws Exception;
}
