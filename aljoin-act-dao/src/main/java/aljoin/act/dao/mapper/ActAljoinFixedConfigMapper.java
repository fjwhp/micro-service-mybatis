package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActAljoinFixedConfig;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 固定流程配置表(Mapper接口).
 * 
 * @author：wangj.
 * 
 * @date： 2017-11-07
 */
public interface ActAljoinFixedConfigMapper extends BaseMapper<ActAljoinFixedConfig> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-11-07
     * 
     * @param id
     * 
     * @throws Exception
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
     * 
     * @param obj
     * 
     * @throws Exception
     */
    public void copyObject(ActAljoinFixedConfig obj) throws Exception;
}