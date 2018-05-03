package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinFormDataHis;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 历史表单数据表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-27
 */
public interface ActAljoinFormDataHisService extends IService<ActAljoinFormDataHis> {

    /**
     * 
     * 历史表单数据表(分页列表).
     *
     * @return：Page<ActAljoinFormDataHis>
     *
     * @author：zhongjy
     *
     * @date：2017-09-27
     */
    public Page<ActAljoinFormDataHis> list(PageBean pageBean, ActAljoinFormDataHis obj) throws Exception;

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
    public void copyObject(ActAljoinFormDataHis obj) throws Exception;
}
