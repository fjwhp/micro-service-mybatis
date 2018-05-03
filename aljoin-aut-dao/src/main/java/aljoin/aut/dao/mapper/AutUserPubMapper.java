package aljoin.aut.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import aljoin.aut.dao.entity.AutUserPub;

/**
 * 
 * 用户公共信息表(Mapper接口).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-10
 */
public interface AutUserPubMapper extends BaseMapper<AutUserPub> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017-10-10
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
     * @date：2017-10-10
     */
    public void copyObject(AutUserPub obj) throws Exception;
}