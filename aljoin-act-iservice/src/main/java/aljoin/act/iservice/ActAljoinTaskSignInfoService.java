package aljoin.act.iservice;

import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.entity.ActAljoinTaskSignInfo;
import aljoin.object.PageBean;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * 加签信息表(服务类).
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public interface ActAljoinTaskSignInfoService extends IService<ActAljoinTaskSignInfo> {

    /**
     * 加签信息表(分页列表).
     *
     * @param pageBean
     *
     * @param obj
     *
     * @return
     *
     * @throws Exception
     */
    public Page<ActAljoinTaskSignInfo> list(PageBean pageBean, ActAljoinTaskSignInfo obj) throws Exception;

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
    public void copyObject(ActAljoinTaskSignInfo obj) throws Exception;

    /**
     * 根据第一级加签用户ID获得最后一级所有的加签信息
     *
     * @param taskDefKey              任务key
     * @param processInstanceId       流程实例ID
     * @param assignee                当前任务办理人ID
     * @return
     */
    public Map<String,Object> getLastSignTaskIdList(String taskId,String taskDefKey,String processInstanceId,String assignee) throws Exception;

    /**
     * 判断所有加签记录的原始加签人是否是同一个
     * @param taskDefKey          任务key
     * @param processInstanceId   流程实例ID
     * @return
     * @throws Exception
     */
    public boolean isSame(String taskDefKey,String processInstanceId) throws Exception;

    /**
     * 控制提交界面显示
     *
     * @param task                  任务
     * @return
     */
    public Map<String,Object> getSignParam(Task task) throws Exception;

    /**
     * 插入加签信息
     * @param uidList       用户ID列表
     * @param task          当前任务
     * @param bpmnRun        运行时流程定义对象
     * @param userMap       用户Map
     * @throws Exception
     */
    public void insertSignTaskInfo(List<Long> uidList,Task task,ActAljoinBpmnRun bpmnRun,Map<Long,String> userMap,Integer finshType) throws Exception;

    /**
     * 根据任务ID查询是否存在加签记录
     * @param taskId  任务ID
     * @return
     * @throws Exception
     */
    public ActAljoinTaskSignInfo getSignTaskInfo(String taskId) throws Exception;
}
