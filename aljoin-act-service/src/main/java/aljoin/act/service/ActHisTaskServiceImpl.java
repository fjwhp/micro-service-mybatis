package aljoin.act.service;

import aljoin.act.dao.entity.ActHisTask;
import aljoin.act.dao.mapper.ActHisTaskMapper;
import aljoin.act.iservice.ActHisTaskService;
import aljoin.act.iservice.ActRunTaskService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 
 * (服务实现类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
@Service
public class ActHisTaskServiceImpl implements ActHisTaskService {

    @Resource
    private ActHisTaskMapper mapper;
    @Resource
    private ActRunTaskService actRuTaskService;

    @Override
    @Transactional
    public void updateHisTask(String taskId,String currentUserId) throws Exception {
        ActHisTask hiTask = new ActHisTask();
        hiTask.setId(taskId);
        hiTask.setEndTime(new Date());
        hiTask.setDeleteReason("completed");
        mapper.updateHisTask(hiTask);

        actRuTaskService.deleteByTaskId(taskId,currentUserId);
    }
}
