package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActAljoinExecutionHis;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 流程实例(执行流)表(历史表)(Mapper接口).
 * 
 * @author：zhongjy.
 * 
 * @date： 2018-01-19
 */
public interface ActAljoinExecutionHisMapper extends BaseMapper<ActAljoinExecutionHis> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2018-01-19
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
     * @author：zhongjy
     *
     * @date：2018-01-19
     * 
     * @param obj
     * 
     * @throws Exception
     */
    public void copyObject(ActAljoinExecutionHis obj) throws Exception;
}