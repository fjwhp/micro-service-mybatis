package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActRunTimeExecution;

import java.util.List;

/**
 * 执行流对象（mapper接口）
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public interface ActRunTimeExecutionMapper {
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
    public void copyObject(ActRunTimeExecution obj) throws Exception;

    /**
     * 批量新增
     *
     * @param runTimeExecutionList
     *
     * @throws Exception
     */
    public void insertExecutionBatch(List<ActRunTimeExecution> runTimeExecutionList) throws Exception;

    /**
     *
     * 修改execution IS_CONCURRENT_ IS_SCOPE_
     *
     * @param runTimeExecution
     */
    public void updateExecution(ActRunTimeExecution runTimeExecution);

}