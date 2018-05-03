package aljoin.pub.dao.mapper;

import aljoin.pub.dao.entity.PubPublicInfoRead;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 公共信息阅读表(Mapper接口).
 * 
 * @author：sln.
 * 
 * @date： 2017-11-15
 */
public interface PubPublicInfoReadMapper extends BaseMapper<PubPublicInfoRead> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017-11-15
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
     * @date：2017-11-15
     */
    public void copyObject(PubPublicInfoRead obj) throws Exception;
}