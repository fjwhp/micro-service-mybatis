package aljoin.aut.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutIndexPageModule;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 首页模块定制表(服务类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-12
 */
public interface AutIndexPageModuleService extends IService<AutIndexPageModule> {

    /**
     * 
     * 首页模块定制表(分页列表).
     *
     * @return：Page<AutIndexPageModule>
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    public Page<AutIndexPageModule> list(PageBean pageBean, AutIndexPageModule obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    public void copyObject(AutIndexPageModule obj) throws Exception;

    /**
     * 
     * 根据用户ID返回配置列表
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017-11-02
     */
    public List<AutIndexPageModule> selectUser(String userid) throws Exception;

    /**
     * 
     * 修改更新
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017-11-02
     */
    public RetMsg updateModule(AutIndexPageModule key, Long customUserId) throws Exception;

    /**
     * 
     * 首页移动修改更新
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017-11-02
     */
    public RetMsg removeUpdate(String codes, String isType, Long customUserId) throws Exception;

    public RetMsg init(Long customUserId) throws Exception;
}
