package aljoin.act.iservice;

import java.util.HashMap;
import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.object.PageBean;

/**
 * 
 * 流程查询表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-02
 */
public interface ActAljoinQueryService extends IService<ActAljoinQuery> {

    /**
     * 
     * 流程查询表(分页列表).
     *
     * @return：Page<ActAljoinQuery>
     *
     * @author：zhongjy
     *
     * @date：2017-11-02
     */
    public Page<ActAljoinQuery> list(PageBean pageBean, ActAljoinQuery obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-11-02
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
     * @date：2017-11-02
     */
    public void copyObject(ActAljoinQuery obj) throws Exception;

    /**
     * 
     * 清空流程实例的当前办理人（含历史数据）
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年12月7日 下午1:49:40
     */
    public void cleanQureyCurrentUser(String taskId) throws Exception;

    /**
     * 
     * 更新当前办理人姓名
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017年12月7日 下午1:49:40
     */
    public void updateAssigneeName(String taskId, String userName) throws Exception;

    /**
     * 
     * 批量更新当前办理人姓名（用于批量更新签收）
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017年12月7日 下午1:49:40
     */
    public void updateAssigneeNameBatch(HashMap<String, String> map, List<String> processInstanceIds) throws Exception;

    /**
     * 
     * APP更新当前办理人姓名
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年12月26日 下午1:49:40
     */
    public void appUpdateAssigneeName(String taskId, String userName, Long userID, String names) throws Exception;

    /**
     * 
     * 清空流程实例的当前办理人（含历史数据）
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年12月26日 下午1:49:40
     */
    public void appCleanQureyCurrentUser(String taskId, Long userid, String username) throws Exception;

    /**
     * 
     * 批量更新当候选人姓名（环节跳转后，候选用户展示在当前办理人中）
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017年12月7日 下午1:49:40
     */
    public void updateCurrentUserName(String taskId, String userName) throws Exception;

}
