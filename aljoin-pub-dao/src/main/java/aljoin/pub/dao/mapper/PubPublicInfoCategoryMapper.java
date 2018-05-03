package aljoin.pub.dao.mapper;

import aljoin.pub.dao.entity.PubPublicInfoCategory;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 公共信息分类表(Mapper接口).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-16
 */
public interface PubPublicInfoCategoryMapper extends BaseMapper<PubPublicInfoCategory> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-10-16
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
     * @date：2017-10-16
     */
    public void copyObject(PubPublicInfoCategory obj) throws Exception;
}