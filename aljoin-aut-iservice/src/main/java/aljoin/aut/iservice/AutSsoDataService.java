package aljoin.aut.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.aut.dao.entity.AutSsoData;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 单点登录数据同步表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-12-11
 */
public interface AutSsoDataService extends IService<AutSsoData> {

    /**
     * 
     * 单点登录数据同步表(分页列表).
     *
     * @return：Page<AutSsoData>
     *
     * @author：zhongjy
     *
     * @date：2017-12-11
     */
    public Page<AutSsoData> list(PageBean pageBean, AutSsoData obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-11
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-11
     */
    public void copyObject(AutSsoData obj) throws Exception;
}
