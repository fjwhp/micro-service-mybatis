package aljoin.act.iservice;

import aljoin.act.dao.entity.ActHiTaskinst;
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
public interface ActHiTaskinstService extends IService<ActHiTaskinst> {

    /**
     * 
     * (分页列表).
     *
     * @return：Page<ActHiTaskinst>
     *
     * @author：wangj
     *
     * @date：2017-12-17
     */
    public Page<ActHiTaskinst> list(PageBean pageBean, ActHiTaskinst obj) throws Exception;

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
    public void copyObject(ActHiTaskinst obj) throws Exception;

    /**
     *
     * 在办任务(分页列表).
     *
     * @return：Page<ActHiTaskinst>
     *
     * @author：wangj
     *
     * @date：2017-12-18
     */
    Page<ActHiTaskinst> selectDoingPage(Page<ActHiTaskinst> var1, ActHiTaskinst var2) throws Exception;
}
