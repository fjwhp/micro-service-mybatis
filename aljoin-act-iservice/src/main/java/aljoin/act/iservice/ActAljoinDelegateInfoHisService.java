package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinDelegateInfoHis;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 任务委托信息表（存放具体的委托任务数据）,历史表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public interface ActAljoinDelegateInfoHisService extends IService<ActAljoinDelegateInfoHis> {

    /**
     * 
     * 任务委托信息表（存放具体的委托任务数据）,历史表(分页列表).
     *
     * @return：Page<ActAljoinDelegateInfoHis>
     *
     * @author：zhongjy
     *
     * @date：2017-11-08
     */
    public Page<ActAljoinDelegateInfoHis> list(PageBean pageBean, ActAljoinDelegateInfoHis obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-11-08
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
     * @date：2017-11-08
     */
    public void copyObject(ActAljoinDelegateInfoHis obj) throws Exception;
}
