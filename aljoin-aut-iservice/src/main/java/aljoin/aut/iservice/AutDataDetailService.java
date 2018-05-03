package aljoin.aut.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutDataDetail;
import aljoin.object.PageBean;

/**
 * 
 * (服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-12-14
 */
public interface AutDataDetailService {

    /**
     * 
     * (分页列表).
     *
     * @return：Page<AutDataDetail>
     *
     * @author：zhongjy
     *
     * @date：2017-12-14
     */
    public Page<AutDataDetail> list(PageBean pageBean, AutDataDetail obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-14
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
     * @date：2017-12-14
     */
    public void copyObject(AutDataDetail obj) throws Exception;

    /**
     * 
     * 查数据
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-14
     */
    public List<AutDataDetail> selectList() throws Exception;

    /**
     * 
     * 插入对象
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-14
     */
    public void insert(AutDataDetail autDataDetail) throws Exception;

    /**
     * 
     * 批量插入
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-14
     */
    public void insertBatch(List<AutDataDetail> autDataDetails) throws Exception;

    /**
     * 
     * 批量物理删除
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-14
     */
    public void physicsDeleteBatchById(List<Long> ids) throws Exception;

}
