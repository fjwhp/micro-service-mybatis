package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActRuIdentitylink;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * (Mapper接口).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-12-12
 */
public interface ActRuIdentitylinkMapper extends BaseMapper<ActRuIdentitylink> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-12
     * 
     * @param id
     * 
     * @throws Exception
     */
    public void physicsDeleteById(String id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-12
     * 
     * @param obj
     * 
     * @throws Exception
     */
    public void copyObject(ActRuIdentitylink obj) throws Exception;
}
