package aljoin.mee.dao.mapper;

import aljoin.mee.dao.entity.MeeInsideMeeting;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 内部会议表(Mapper接口).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public interface MeeInsideMeetingMapper extends BaseMapper<MeeInsideMeeting> {
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
    public void copyObject(MeeInsideMeeting obj) throws Exception;
}