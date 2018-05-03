package aljoin.aut.dao.mapper;

import aljoin.aut.dao.entity.AutUserRank;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 人员排序表(Mapper接口).
 * 
 * @author：huanghz.
 * 
 * @date： 2017-12-13
 */
public interface AutUserRankMapper extends BaseMapper<AutUserRank> {
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
}