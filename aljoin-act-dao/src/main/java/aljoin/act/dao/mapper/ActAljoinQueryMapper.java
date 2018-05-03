package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActAljoinQuery;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 流程查询表(Mapper接口).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-02
 */
public interface ActAljoinQueryMapper extends BaseMapper<ActAljoinQuery> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-11-02
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
     * @date：2017-11-02
     * 
     * @param obj
     * 
     * @throws Exception
     */
    public void copyObject(ActAljoinQuery obj) throws Exception;
}