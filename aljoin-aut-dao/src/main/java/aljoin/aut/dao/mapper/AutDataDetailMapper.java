package aljoin.aut.dao.mapper;

import java.util.List;

import aljoin.aut.dao.entity.AutDataDetail;

/**
 * 
 * (Mapper接口).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-12-14
 */
public interface AutDataDetailMapper {
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
     * 批量物理删除
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-14
     */
    public void physicsDeleteBatchById(List<Long> ids) throws Exception;

    /**
     * 
     * 批量新增
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
     * 批量新增
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-14
     */
    public void add(AutDataDetail autDataDetails) throws Exception;

    /**
     * 
     * 取数据
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-14
     */
    public List<AutDataDetail> getList() throws Exception;
}