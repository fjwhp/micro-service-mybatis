package aljoin.aut.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.aut.dao.entity.AutDataStatistics;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 数据统计表(服务类).
 * 
 * @author：sln.
 * 
 * @date： 2017-11-09
 */
public interface AutDataStatisticsService extends IService<AutDataStatistics> {

    /**
     * 
     * 数据统计表(分页列表).
     *
     * @return：Page<AutDataStatistics>
     *
     * @author：sln
     *
     * @date：2017-11-09
     */
    public Page<AutDataStatistics> list(PageBean pageBean, AutDataStatistics obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017-11-09
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017-11-09
     */
    public void copyObject(AutDataStatistics obj) throws Exception;

    /**
     * 
     * @描述:新增或更新对象(单个,不许用了，影响数据准确)
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017-11-09
     */
    // public void addOrUpdate(AutDataStatistics obj) throws Exception;

    /**
     * 
     * @描述:对象数量减1
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017-11-09
     */
    public void minus(AutDataStatistics obj) throws Exception;

    /**
     * 
     * @描述:首页4个数据统计
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017-11-09
     */
    public List<AutDataStatistics> getUserCount(AutDataStatistics obj) throws Exception;

    /**
     * 
     * @描述:首页检查未读消息
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017-11-09
     */
    public Integer getUserMsgCount(AutDataStatistics obj) throws Exception;

    /**
     * 
     * @描述:新增或更新对象(批量操作)
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017-11-09
     */
    public void addOrUpdateList(AutDataStatistics obj, List<String> userId) throws Exception;

    /**
     * 
     * @描述:对象数量减1（批量操作）
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017-11-09
     */
    public void minusList(AutDataStatistics obj, List<String> userIds) throws Exception;

    /**
     * 
     * @描述:新增或更新对象(单个用户)
     *
     * @return：void
     *
     * @author：sln
     *
     * @date：2017-11-09
     */
    public void addOrUpdate(AutDataStatistics obj) throws Exception;

    public void pushData2Statistics() throws Exception;
}
