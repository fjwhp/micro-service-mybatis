package aljoin.mee.dao.mapper;

import aljoin.mee.dao.entity.MeeInsideMeetingDraft;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 内部会议草稿表(Mapper接口).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public interface MeeInsideMeetingDraftMapper extends BaseMapper<MeeInsideMeetingDraft> {
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
    public void copyObject(MeeInsideMeetingDraft obj) throws Exception;
}