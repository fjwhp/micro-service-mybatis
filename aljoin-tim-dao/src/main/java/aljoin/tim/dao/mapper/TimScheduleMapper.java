package aljoin.tim.dao.mapper;

import aljoin.tim.dao.entity.TimSchedule;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 任务调度表(Mapper接口).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-10-26
 */
public interface TimScheduleMapper extends BaseMapper<TimSchedule> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-10-26
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
     * @date：2017-10-26
     */
    public void copyObject(TimSchedule obj) throws Exception;
}