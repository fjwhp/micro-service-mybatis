package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActAljoinTaskAssignee;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 流程任务-授权表(Mapper接口).
 * 
 * @author：zhongjy.
 * 
 * @date： 2018-01-02
 */
public interface ActAljoinTaskAssigneeMapper extends BaseMapper<ActAljoinTaskAssignee> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2018-01-02
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
     * @author：zhongjy
     *
     * @date：2018-01-02
     * 
     * @param obj
     * 
     * @throws Exception
     */
    public void copyObject(ActAljoinTaskAssignee obj) throws Exception;
}