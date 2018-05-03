package aljoin.act.dao.mapper;

import aljoin.act.dao.entity.ActRuTask;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;

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
public interface ActRuTaskMapper extends BaseMapper<ActRuTask> {
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
    public void copyObject(ActRuTask obj) throws Exception;

    /**
     *
     * 待办任务列表
     *
     * @return：List<ActRuTask>
     *
     * @author：wangj
     *
     * @date：2017-09-27
     * 
     * @param var1
     * 
     * @param var2
     * 
     * @throws Exception
     */
    public List<ActRuTask> selectWaitPage(RowBounds var1, ActRuTask var2) throws Exception;

    /**
     * 待办任务列表
     *
     * @param page
     *
     * @param wrapper
     *
     * @return List<ActRuTask>
     *
     * @throws Exception
     */
	public List<ActRuTask> selectWaitingPage(Page<ActRuTask> page, ActRuTask wrapper)throws Exception;
}