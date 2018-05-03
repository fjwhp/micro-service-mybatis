package aljoin.aut.dao.mapper;

import aljoin.aut.dao.entity.AutSsoData;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 单点登录数据同步表(Mapper接口).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-12-11
 */
public interface AutSsoDataMapper extends BaseMapper<AutSsoData> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-11
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
     * @date：2017-12-11
     */
    public void copyObject(AutSsoData obj) throws Exception;
}