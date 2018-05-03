package aljoin.mee.dao.mapper;

import aljoin.mee.dao.entity.MeeOutsideMeetingDraft;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 外部会议草稿表(Mapper接口).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public interface MeeOutsideMeetingDraftMapper extends BaseMapper<MeeOutsideMeetingDraft> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：wangj
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
     * @author：wangj
     *
     * @date：2017-10-12
     */
    public void copyObject(MeeOutsideMeetingDraft obj) throws Exception;
}