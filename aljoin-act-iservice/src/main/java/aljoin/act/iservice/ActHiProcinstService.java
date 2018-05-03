package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActHiProcinst;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * (服务类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
public interface ActHiProcinstService extends IService<ActHiProcinst> {

    /**
     * 
     * (分页列表).
     *
     * @return：Page<ActHiProcinst>
     *
     * @author：wangj
     *
     * @date：2017-12-17
     */
    public Page<ActHiProcinst> list(PageBean pageBean, ActHiProcinst obj) throws Exception;

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
    public void copyObject(ActHiProcinst obj) throws Exception;
}
