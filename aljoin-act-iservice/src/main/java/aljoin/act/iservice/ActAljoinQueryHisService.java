package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 流程查询表(历史表)(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-02
 */
public interface ActAljoinQueryHisService extends IService<ActAljoinQueryHis> {

    /**
     * 
     * 流程查询表(历史表)(分页列表).
     *
     * @return：Page<ActAljoinQueryHis>
     *
     * @author：zhongjy
     *
     * @date：2017-11-02
     */
    public Page<ActAljoinQueryHis> list(PageBean pageBean, ActAljoinQueryHis obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-11-02
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
     * @date：2017-11-02
     */
    public void copyObject(ActAljoinQueryHis obj) throws Exception;
}
