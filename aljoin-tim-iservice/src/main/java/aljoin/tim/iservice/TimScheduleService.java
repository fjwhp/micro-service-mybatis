package aljoin.tim.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.tim.dao.entity.TimSchedule;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 任务调度表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-10-26
 */
public interface TimScheduleService extends IService<TimSchedule> {

    /**
     * 
     * 任务调度表(分页列表).
     *
     * @return：Page<TimSchedule>
     *
     * @author：zhongjy
     *
     * @date：2017-10-26
     */
    public Page<TimSchedule> list(PageBean pageBean, TimSchedule obj) throws Exception;

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

    /**
     *
     * 自动启动定时任务
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-11-09
     */
    public void autoStart() throws Exception;
}
