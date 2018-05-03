package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActAljoinFormDataDraft;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 表单数据表(草稿)(Mapper接口).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-30
 */
public interface ActAljoinFormDataDraftMapper extends BaseMapper<ActAljoinFormDataDraft> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-09-30
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
     * @author：zhongjy
     *
     * @date：2017-09-30
     * 
     * @param obj
     * 
     * @throws Exception
     */
    public void copyObject(ActAljoinFormDataDraft obj) throws Exception;
}