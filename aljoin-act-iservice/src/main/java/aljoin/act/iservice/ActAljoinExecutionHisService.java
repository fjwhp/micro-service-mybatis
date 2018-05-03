package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinExecutionHis;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 流程实例(执行流)表(历史表)(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2018-01-19
 */
public interface ActAljoinExecutionHisService extends IService<ActAljoinExecutionHis> {

    /**
     * 
     * 流程实例(执行流)表(历史表)(分页列表).
     *
     * @return：Page<ActAljoinExecutionHis>
     *
     * @author：zhongjy
     *
     * @date：2018-01-19
     */
    public Page<ActAljoinExecutionHis> list(PageBean pageBean, ActAljoinExecutionHis obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2018-01-19
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
     * @date：2018-01-19
     */
    public void copyObject(ActAljoinExecutionHis obj) throws Exception;
}
