package aljoin.pub.dao.mapper;

import aljoin.pub.dao.entity.PubPublicInfoDraft;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 公共信息草稿表(Mapper接口).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-12
 */
public interface PubPublicInfoDraftMapper extends BaseMapper<PubPublicInfoDraft> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017-10-12
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
     * @date：2017-10-12
     */
    public void copyObject(PubPublicInfoDraft obj) throws Exception;
}