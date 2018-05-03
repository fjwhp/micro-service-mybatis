package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinActivityLog;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 流程操作日志表(服务类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-25
 */
public interface ActAljoinActivityLogService extends IService<ActAljoinActivityLog> {

    /**
     * 
     * 流程操作日志表(分页列表).
     *
     * @return：Page<ActAljoinActivityLog>
     *
     * @author：wangj
     *
     * @date：2017-12-25
     */
    public Page<ActAljoinActivityLog> list(PageBean pageBean, ActAljoinActivityLog obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-12-25
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
     * @date：2017-12-25
     */
    public void copyObject(ActAljoinActivityLog obj) throws Exception;
}
