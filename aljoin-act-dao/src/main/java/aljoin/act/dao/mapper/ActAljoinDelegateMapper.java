package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActAljoinDelegate;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 任务委托表(Mapper接口).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public interface ActAljoinDelegateMapper extends BaseMapper<ActAljoinDelegate> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-11-08
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
     * @date：2017-11-08
     * 
     * @param obj
     * 
     * @throws Exception
     */
    public void copyObject(ActAljoinDelegate obj) throws Exception;

    /**
     * 批量插入数据
     * 
     * @param autDataList
     * @throws Exception
     */
    public void insertAutDataBatch(List<Map<String, Object>> autDataList) throws Exception;
}