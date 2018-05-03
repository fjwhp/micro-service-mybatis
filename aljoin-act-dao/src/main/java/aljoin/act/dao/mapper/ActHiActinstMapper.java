package aljoin.act.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import aljoin.act.dao.entity.ActHiActinst;

/**
 * 
 * (Mapper接口).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
public interface ActHiActinstMapper extends BaseMapper<ActHiActinst> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-12-17
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
     * @date：2017-12-17
     * 
     * @param obj
     * 
     * @throws Exception
     */
    public void copyObject(ActHiActinst obj) throws Exception;
}