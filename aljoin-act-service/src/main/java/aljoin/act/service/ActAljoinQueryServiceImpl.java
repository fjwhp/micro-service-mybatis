package aljoin.act.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.dao.mapper.ActAljoinQueryMapper;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 流程查询表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-02
 */
@Service
public class ActAljoinQueryServiceImpl extends ServiceImpl<ActAljoinQueryMapper, ActAljoinQuery>
    implements ActAljoinQueryService {

    @Resource
    private ActAljoinQueryMapper mapper;
    @Resource
    private TaskService taskService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;

    @Override
    public Page<ActAljoinQuery> list(PageBean pageBean, ActAljoinQuery obj) throws Exception {
        Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
        where.orderBy("create_time", false);
        Page<ActAljoinQuery> page =
            selectPage(new Page<ActAljoinQuery>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinQuery obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public void cleanQureyCurrentUser(String taskId) throws Exception {
        // 更新当前表
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
        where.eq("process_instance_id", task.getProcessInstanceId());
        ActAljoinQuery actAljoinQuery = selectOne(where);
        if (actAljoinQuery != null) {
            actAljoinQuery.setCurrentHandleFullUserName("");
            updateById(actAljoinQuery);
            // 更新历史表
            Where<ActAljoinQueryHis> where2 = new Where<ActAljoinQueryHis>();
            where2.eq("process_instance_id", task.getProcessInstanceId());
            ActAljoinQueryHis hisQuery = actAljoinQueryHisService.selectOne(where2);
            hisQuery.setCurrentHandleFullUserName("");
            actAljoinQueryHisService.updateById(hisQuery);
        }
    }

    @Override
    public void appCleanQureyCurrentUser(String taskId, Long userid, String username) throws Exception {
        // 更新当前表
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
        where.eq("process_instance_id", task.getProcessInstanceId());
        ActAljoinQuery actAljoinQuery = selectOne(where);
        if (actAljoinQuery != null) {
            actAljoinQuery.setCurrentHandleFullUserName("");
            actAljoinQuery.setLastUpdateUserId(userid);
            actAljoinQuery.setLastUpdateUserName(username);
            updateById(actAljoinQuery);
            // 更新历史表
            Where<ActAljoinQueryHis> where2 = new Where<ActAljoinQueryHis>();
            where2.eq("process_instance_id", task.getProcessInstanceId());
            ActAljoinQueryHis hisQuery = actAljoinQueryHisService.selectOne(where2);
            hisQuery.setCurrentHandleFullUserName("");
            hisQuery.setLastUpdateUserId(userid);
            hisQuery.setLastUpdateUserName(username);
            actAljoinQueryHisService.updateById(hisQuery);
        }
    }

    @Override
    public void updateAssigneeName(String taskId, String userName) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
        where.eq("process_instance_id", task.getProcessInstanceId());
        ActAljoinQuery actAljoinQuery = selectOne(where);
        if (actAljoinQuery != null) {
            if (!StringUtils.isEmpty(task.getAssignee())) {
                actAljoinQuery.setCurrentHandleFullUserName(userName);
            }
            updateById(actAljoinQuery);
            Where<ActAljoinQueryHis> where2 = new Where<ActAljoinQueryHis>();
            where2.eq("process_instance_id", task.getProcessInstanceId());
            ActAljoinQueryHis hisQuery = actAljoinQueryHisService.selectOne(where2);
            hisQuery.setCurrentHandleFullUserName(userName);
            actAljoinQueryHisService.updateById(hisQuery);
        }
    }

    @Override
    public void appUpdateAssigneeName(String taskId, String userName, Long userID, String names) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
        where.eq("process_instance_id", task.getProcessInstanceId());
        ActAljoinQuery actAljoinQuery = selectOne(where);
        if (actAljoinQuery != null) {
            if (!StringUtils.isEmpty(task.getAssignee())) {
                actAljoinQuery.setCurrentHandleFullUserName(userName);
            }
            updateById(actAljoinQuery);
            Where<ActAljoinQueryHis> where2 = new Where<ActAljoinQueryHis>();
            where2.eq("process_instance_id", task.getProcessInstanceId());
            ActAljoinQueryHis hisQuery = actAljoinQueryHisService.selectOne(where2);
            hisQuery.setCurrentHandleFullUserName(userName);
            hisQuery.setLastUpdateUserId(userID);
            hisQuery.setLastUpdateUserName(names);
            actAljoinQueryHisService.updateById(hisQuery);
        }
    }

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
    @Override
    public void updateAssigneeNameBatch(HashMap<String, String> map, List<String> processInstanceIds) throws Exception {
        Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
        if (processInstanceIds.size() > 1) {
            where.in("process_instance_id", processInstanceIds);
        } else {
            where.eq("process_instance_id", processInstanceIds.get(0));
        }
        List<ActAljoinQuery> actAljoinQuerys = selectList(where);
        if (actAljoinQuerys != null && !actAljoinQuerys.isEmpty()) {
            for (ActAljoinQuery actAljoinQuery : actAljoinQuerys) {
                if (null != map.get(actAljoinQuery.getProcessInstanceId())) {
                    actAljoinQuery.setCurrentHandleFullUserName(map.get(actAljoinQuery.getProcessInstanceId()));
                }
            }
            updateBatchById(actAljoinQuerys);
            Where<ActAljoinQueryHis> where2 = new Where<ActAljoinQueryHis>();
            if (processInstanceIds.size() > 1) {
                where2.in("process_instance_id", processInstanceIds);
            } else {
                where2.eq("process_instance_id", processInstanceIds.get(0));
            }
            List<ActAljoinQueryHis> hisActAljoinQuerys = actAljoinQueryHisService.selectList(where2);
            for (ActAljoinQueryHis actAljoinQueryhis : hisActAljoinQuerys) {
                if (null != map.get(actAljoinQueryhis.getProcessInstanceId())) {
                    actAljoinQueryhis.setCurrentHandleFullUserName(map.get(actAljoinQueryhis.getProcessInstanceId()));
                }
            }
            actAljoinQueryHisService.updateBatchById(hisActAljoinQuerys);
        }

    }

    @Override
    public void updateCurrentUserName(String taskId, String userName) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
        where.eq("process_instance_id", task.getProcessInstanceId());
        ActAljoinQuery actAljoinQuery = selectOne(where);
        if (actAljoinQuery != null && StringUtils.isEmpty(task.getAssignee())) {// 办理人为空时，把当前办理人设置为该任务的所有候选人
            if (userName.endsWith(";")) {// 拿掉名字最后的分号
                userName = userName.substring(0, userName.length() - 1);
            }
            actAljoinQuery.setCurrentHandleFullUserName(userName);
            updateById(actAljoinQuery);
            Where<ActAljoinQueryHis> where2 = new Where<ActAljoinQueryHis>();
            where2.eq("process_instance_id", task.getProcessInstanceId());
            ActAljoinQueryHis hisQuery = actAljoinQueryHisService.selectOne(where2);
            hisQuery.setCurrentHandleFullUserName(userName);
            actAljoinQueryHisService.updateById(hisQuery);
        }
    }
}
