package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinFormDataRun;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 运行时表单数据表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-27
 */
public interface ActAljoinFormDataRunService extends IService<ActAljoinFormDataRun> {

    /**
     * 
     * 运行时表单数据表(分页列表).
     *
     * @return：Page<ActAljoinFormDataRun>
     *
     * @author：zhongjy
     *
     * @date：2017-09-27
     */
    public Page<ActAljoinFormDataRun> list(PageBean pageBean, ActAljoinFormDataRun obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-09-27
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
     * @date：2017-09-27
     */
    public void copyObject(ActAljoinFormDataRun obj) throws Exception;
}
