package aljoin.ass.dao.mapper;

import aljoin.ass.dao.entity.AssCategory;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 固定资产分类表(Mapper接口).
 * 
 * @author：xuc.
 * 
 * @date： 2018-01-09
 */
public interface AssCategoryMapper extends BaseMapper<AssCategory> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：xuc
     *
     * @date：2018-01-09
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
     * @date：2018-01-09
     */
    public void copyObject(AssCategory obj) throws Exception;
}