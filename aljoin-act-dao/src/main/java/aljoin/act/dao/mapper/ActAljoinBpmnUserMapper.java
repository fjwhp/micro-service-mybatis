package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActAljoinBpmnUser;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 流程-用户关系表(Mapper接口).
 * 
 * @author：pengsp.
 * 
 * @date： 2017-10-12
 */
public interface ActAljoinBpmnUserMapper extends BaseMapper<ActAljoinBpmnUser> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：pengsp
     *
     * @date：2017-10-12
     * 
     * @param id
     * 
     * @throws Exception
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：pengsp
     *
     * @date：2017-10-12
     * 
     * @param obj
     * 
     * @throws Exception
     */
    public void copyObject(ActAljoinBpmnUser obj) throws Exception;
}