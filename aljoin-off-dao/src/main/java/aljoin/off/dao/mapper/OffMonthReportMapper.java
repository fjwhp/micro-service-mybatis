package aljoin.off.dao.mapper;

import aljoin.off.dao.entity.OffMonthReport;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 工作月报表(Mapper接口).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-11
 */
public interface OffMonthReportMapper extends BaseMapper<OffMonthReport> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-10-11
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
     * @date：2017-10-11
     */
    public void copyObject(OffMonthReport obj) throws Exception;
}