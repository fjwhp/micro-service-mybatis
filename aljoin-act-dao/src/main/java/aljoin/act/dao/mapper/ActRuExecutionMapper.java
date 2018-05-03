package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActRuExecution;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 执行流对象（mapper接口）
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public interface ActRuExecutionMapper extends BaseMapper<ActRuExecution> {
    /**
     * 根据ID删除对象(物理删除)
     *
     * @param id
     *
     * @throws Exception
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @param obj
     *
     * @throws Exception
     */
    public void copyObject(ActRuExecution obj) throws Exception;
}