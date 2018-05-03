package aljoin.aut.iservice;

import aljoin.aut.dao.entity.AutUserRank;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 人员排序表(服务类).
 * 
 * @author：huanghz.
 * 
 * @date： 2017-12-13
 */
public interface AutUserRankService extends IService<AutUserRank> {

    /**
     * 
     * 人员排序表(分页列表).
     *
     * @return：Page<AutUserRank>
     *
     * @author：huanghz
     *
     * @date：2017-12-13
     */
    public Page<AutUserRank> list(PageBean pageBean, AutUserRank obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：huanghz
     *
     * @date：2017-12-13
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：huanghz
     *
     * @date：2017-12-13
     */
    public void copyObject(AutUserRank obj) throws Exception;

    /**
     * 取人员id新增人员的默认排序号。
     *
     * @return：void
     *
     * @author：huanghz
     *
     * @date：2017-12-15
     */
    public RetMsg add(Long id) throws Exception;
}
