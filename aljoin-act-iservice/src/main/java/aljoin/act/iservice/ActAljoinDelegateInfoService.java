package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinDelegateInfo;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 任务委托信息表（存放具体的委托任务数据）(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public interface ActAljoinDelegateInfoService extends IService<ActAljoinDelegateInfo> {

    /**
     * 
     * 任务委托信息表（存放具体的委托任务数据）(分页列表).
     *
     * @return：Page<ActAljoinDelegateInfo>
     *
     * @author：zhongjy
     *
     * @date：2017-11-08
     */
    public Page<ActAljoinDelegateInfo> list(PageBean pageBean, ActAljoinDelegateInfo obj) throws Exception;

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
    public void copyObject(ActAljoinDelegateInfo obj) throws Exception;
}
