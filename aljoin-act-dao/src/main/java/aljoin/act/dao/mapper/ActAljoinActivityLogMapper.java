package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActAljoinActivityLog;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 流程操作日志表(Mapper接口).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-25
 */
public interface ActAljoinActivityLogMapper extends BaseMapper<ActAljoinActivityLog> {
    /**
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-12-25
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
     * @author：wangj
     *
     * @date：2017-12-25
     * @param obj
     * @throws Exception
     */
    public void copyObject(ActAljoinActivityLog obj) throws Exception;
}