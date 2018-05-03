package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActHiTaskinst;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 
 * (Mapper接口).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
public interface ActHiTaskinstMapper extends BaseMapper<ActHiTaskinst> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-12-17
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
     * @date：2017-12-17
     * 
     * @param obj
     * 
     * @throws Exception
     */
    public void copyObject(ActHiTaskinst obj) throws Exception;

    /**
     *
     * 待办任务列表
     *
     * @return：List<ActHiTaskinst>
     *
     * @author：wangj
     *
     * @date：2017-12-18
     * 
     * @param var1
     * 
     * @param var2
     * 
     * @throws Exception
     */
    public List<ActHiTaskinst> selectDoingPage(RowBounds var1, ActHiTaskinst var2) throws Exception;
}