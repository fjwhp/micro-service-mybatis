package aljoin.ioa.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import aljoin.ioa.dao.entity.IoaRegClosed;

/**
 * 
 * 收文阅件表(Mapper接口).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public interface IoaRegClosedMapper extends BaseMapper<IoaRegClosed> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-11-08
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
     * @date：2017-11-08
     */
    public void copyObject(IoaRegClosed obj) throws Exception;
}