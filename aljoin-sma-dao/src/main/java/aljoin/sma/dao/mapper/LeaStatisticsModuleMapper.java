package aljoin.sma.dao.mapper;

import aljoin.sma.dao.entity.LeaStatisticsModule;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 领导看板统计模块显示排序(Mapper接口).
 * 
 * @author：huangw.
 * 
 * @date： 2017-12-25
 */
public interface LeaStatisticsModuleMapper extends BaseMapper<LeaStatisticsModule> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017-12-25
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017-12-25
     */
    public void copyObject(LeaStatisticsModule obj) throws Exception;
}