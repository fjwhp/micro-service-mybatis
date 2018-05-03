package aljoin.aut.dao.mapper;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import aljoin.aut.dao.entity.AutDataStatistics;

/**
 * 
 * 数据统计表(Mapper接口).
 * 
 * @author：sln.
 * 
 * @date： 2017-11-09
 */
public interface AutDataStatisticsMapper extends BaseMapper<AutDataStatistics> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017-11-09
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017-11-09
     */
    public void copyObject(AutDataStatistics obj) throws Exception;

    /**
     * 
     * 批量删除
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017-11-09
     */
    public void physicsDeleteBatchById(List<String> ids) throws Exception;
}