package aljoin.res.dao.mapper;

import aljoin.res.dao.entity.ResResource;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 资源管理表(Mapper接口).
 * 
 * @author：laijy.
 * 
 * @date： 2017-09-05
 */
public interface ResResourceMapper extends BaseMapper<ResResource> {
    /**
     * 
     * @描述：根据ID删除对象(物理删除)
     *
     * @返回：void
     *
     * @作者：caizx
     *
     * @时间：2018-04-17
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * @描述：复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @返回：void
     *
     * @作者：caizx
     *
     * @时间：2018-04-17
     */
    public void copyObject(ResResource obj) throws Exception;
}