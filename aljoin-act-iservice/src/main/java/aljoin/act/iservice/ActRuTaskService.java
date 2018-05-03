package aljoin.act.iservice;

import aljoin.act.dao.entity.ActRuTask;
import aljoin.object.PageBean;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * (服务类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
public interface ActRuTaskService extends IService<ActRuTask> {

    /**
     * 
     * (分页列表).
     *
     * @return：Page<ActRuTask>
     *
     * @author：wangj
     *
     * @date：2017-12-17
     */
    public Page<ActRuTask> list(PageBean pageBean, ActRuTask obj) throws Exception;
    
    /**
     * 
     * (分页列表).
     *
     * @return：Page<ActRuTask>
     *
     * @author：wangj
     *
     * @date：2017-12-17
     */
    public Page<ActRuTask> waitingList(PageBean pageBean, ActRuTask obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-12-17
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
     * @date：2017-12-17
     */
    public void copyObject(ActRuTask obj) throws Exception;

    /**
     *
     * 待办任务(分页列表).
     *
     * @return：Page<ActRuTask>
     *
     * @author：wangj
     *
     * @date：2017-12-18
     */
    Page<ActRuTask> selectWaitPage(Page<ActRuTask> var1, ActRuTask var2) throws Exception;

    Page<ActRuTask> selectWaitingPage(Page<ActRuTask> page, ActRuTask wrapper) throws Exception;
} 
