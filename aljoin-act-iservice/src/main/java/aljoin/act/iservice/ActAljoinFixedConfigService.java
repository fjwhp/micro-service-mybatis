package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinFixedConfig;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 固定流程配置表(服务类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-11-07
 */
public interface ActAljoinFixedConfigService extends IService<ActAljoinFixedConfig> {

    /**
     * 
     * 固定流程配置表(分页列表).
     *
     * @return：Page<ActAljoinFixedConfig>
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public Page<ActAljoinFixedConfig> list(PageBean pageBean, ActAljoinFixedConfig obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public void copyObject(ActAljoinFixedConfig obj) throws Exception;
}
