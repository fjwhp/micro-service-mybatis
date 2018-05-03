package aljoin.ioa.dao.mapper;

import aljoin.ioa.dao.entity.IoaReceiveReadObject;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 收文阅件-传阅对象表(分类)(Mapper接口).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public interface IoaReceiveReadObjectMapper extends BaseMapper<IoaReceiveReadObject> {
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
    public void copyObject(IoaReceiveReadObject obj) throws Exception;
}