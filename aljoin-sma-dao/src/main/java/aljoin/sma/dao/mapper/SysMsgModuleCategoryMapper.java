package aljoin.sma.dao.mapper;

import aljoin.sma.dao.entity.SysMsgModuleCategory;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 消息模板分类表(Mapper接口).
 * 
 * @author：huangw.
 * 
 * @date： 2017-11-14
 */
public interface SysMsgModuleCategoryMapper extends BaseMapper<SysMsgModuleCategory> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017-11-14
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017-11-14
     */
    public void copyObject(SysMsgModuleCategory obj) throws Exception;
}