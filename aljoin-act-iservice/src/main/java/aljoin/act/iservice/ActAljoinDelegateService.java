package aljoin.act.iservice;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.delegate.event.impl.ActivitiEntityWithVariablesEventImpl;
import org.activiti.engine.task.Task;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.act.dao.entity.ActAljoinDelegate;
import aljoin.act.dao.object.ActAljoinDelegateVO;
import aljoin.act.dao.object.AllTaskDataShowVO;
import aljoin.act.dao.object.DelegateDO;
import aljoin.object.PageBean;

/**
 * 
 * 任务委托表(服务类).
 * 
 * @author：pengsp.
 * 
 * @date： 2017-10-24
 */
public interface ActAljoinDelegateService extends IService<ActAljoinDelegate> {

    /**
     * 
     * 任务委托表(分页列表).
     *
     * @return：Page<ActAljoinDelegate>
     *
     * @author：pengsp
     *
     * @date：2017-10-24
     */
    public Page<ActAljoinDelegate> list(PageBean pageBean, ActAljoinDelegate obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：pengsp
     *
     * @date：2017-10-24
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
     * @date：2017-10-24
     */
    public void copyObject(ActAljoinDelegate obj) throws Exception;

    /**
     * 
     * 委托新增
     *
     * @return：void
     *
     * @author：pengsp
     *
     * @date：2017-10-24
     */
    public ActAljoinDelegate add(ActAljoinDelegateVO obj) throws Exception;

    /**
     * 
     * 查询所有委托工作
     *
     * @return：Page<ActAljoinDelegateVO>
     *
     * @author：pengsp
     *
     * @date：2017-10-24
     */
    public Page<ActAljoinDelegateVO> getAllEntrustWork(PageBean pageBean, ActAljoinDelegateVO obj, Long userId)
        throws Exception;

    /**
     * 
     * 查询所有委托工作
     *
     * @return：Page<ActAljoinDelegateVO>
     *
     * @author：huangw
     *
     * @date：2017-10-24
     */
    public Page<AllTaskDataShowVO> getAllEntrustWorkData(PageBean pageBean, ActAljoinDelegateVO obj, Long userId)
        throws Exception;

    /**
     * 
     * 根据当前委托获取所有被委托用户委托的叶子用户(递归实现).
     *
     * @return：List<Long>
     *
     * @author：zhongjy
     *
     * @date：2017年11月10日 上午10:39:20
     */
    public Map<Long, DelegateDO> getDelegateLinks(ActAljoinDelegate obj, Date currentDate, String delegateUserNames,
        String delegateUserIds, String delegateIds) throws Exception;

    /**
     * 
     * 添加委托业务
     *
     * @return：void
     *
     * @author：pengsp
     *
     * @date：2017-10-24
     */
    public int addDelegateBiz(ActAljoinDelegate obj, List<Task> taskList) throws Exception;

    /**
     * 
     * 终止委托业务
     *
     * @return：void
     *
     * @author：pengsp
     *
     * @date：2017-10-24
     */
    public void stopDelegateBiz(ActAljoinDelegate obj, int stopType) throws Exception;

    /**
     * 
     * App终止委托业务
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017-12-19
     */
    public void appStopDelegateBiz(ActAljoinDelegate obj, String userId, String userName, int stopType)
        throws Exception;

    /**
     * 
     * 委托定时器业务
     *
     * @return：void
     *
     * @author：pengsp
     *
     * @date：2017-10-24
     */
    public void timerDelegateBiz(ActAljoinDelegate obj, boolean isAdd) throws Exception;

    /**
     * 
     * 事件创建委托业务
     *
     * @return：void
     *
     * @author：pengsp
     *
     * @date：2017-10-24
     */
    public void eventCreateDelegateBiz(ActivitiEntityEventImpl activitiEntityEventImpl) throws Exception;

    /**
     * 
     * 事件完成委托业务
     *
     * @return：void
     *
     * @author：pengsp
     *
     * @date：2017-10-24
     */
    public void eventCompletedDelegateBiz(ActivitiEntityWithVariablesEventImpl activitiEntityWithVariablesEventImpl)
        throws Exception;

    /**
     * 
     * 获取底层依赖链
     *
     * @return：Map<Long,DelegateDO>
     *
     * @author：zhongjy
     *
     * @date：2017年11月14日 下午8:21:52
     */
    public Map<Long, DelegateDO> getBottomLevel(ActAljoinDelegate obj, String delegateUserIds) throws Exception;

    /**
     * 
     * 获取顶层依赖链
     *
     * @return：Map<Long,DelegateDO>
     *
     * @author：zhongjy
     *
     * @date：2017年11月14日 下午8:21:52
     */
    public Map<Long, DelegateDO> getTopLevel(ActAljoinDelegate obj, String delegateUserIds) throws Exception;

    /**
     * 
     * 根据将要建立的委托关系获取委托链,参数obj是将要委托的关系（还没有进行委托）.
     *
     * @return：Map<Long,List<DelegateDO>>
     *
     * @author：zhongjy
     *
     * @date：2017年12月1日 下午12:57:46
     */
    public Set<String> getAllDelegateLinks(ActAljoinDelegate obj) throws Exception;

}
