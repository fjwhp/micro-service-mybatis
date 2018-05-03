package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinTaskAssignee;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 流程任务-授权表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2018-01-02
 */
public interface ActAljoinTaskAssigneeService extends IService<ActAljoinTaskAssignee> {

    /**
     * 
     * 流程任务-授权表(分页列表).
     *
     * @return：Page<ActAljoinTaskAssignee>
     *
     * @author：zhongjy
     *
     * @date：2018-01-02
     */
    public Page<ActAljoinTaskAssignee> list(PageBean pageBean, ActAljoinTaskAssignee obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2018-01-02
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
     * @date：2018-01-02
     */
    public void copyObject(ActAljoinTaskAssignee obj) throws Exception;
}
