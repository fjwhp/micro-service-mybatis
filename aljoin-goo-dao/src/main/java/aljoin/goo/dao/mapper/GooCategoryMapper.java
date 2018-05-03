package aljoin.goo.dao.mapper;

import aljoin.goo.dao.entity.GooCategory;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 办公用品分类表(Mapper接口).
 * 
 * @author：xuc.
 * 
 * @date： 2018-01-03
 */
public interface GooCategoryMapper extends BaseMapper<GooCategory> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：xuc
     *
     * @date：2018-01-03
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：xuc
     *
     * @date：2018-01-03
     */
    public void copyObject(GooCategory obj) throws Exception;
}