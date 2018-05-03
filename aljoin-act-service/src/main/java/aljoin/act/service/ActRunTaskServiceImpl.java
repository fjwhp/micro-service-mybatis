package aljoin.act.service;

import aljoin.act.dao.mapper.ActRunTaskMapper;
import aljoin.act.iservice.ActRunIdentitylinkService;
import aljoin.act.iservice.ActRunTaskService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * (服务实现类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
@Service
public class ActRunTaskServiceImpl  implements ActRunTaskService {

    @Resource
    private ActRunTaskMapper mapper;
    @Resource
    private TaskService taskService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private ActRunIdentitylinkService actRunIdentitylinkService;
    @Override
    public void deleteByTaskId(String id,String currentUserId) throws Exception {
        List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(id);
        if(identityLinkList.size() > 0){
            for(IdentityLink identityLink : identityLinkList){
                if(StringUtils.isNotEmpty(identityLink.getType()) && StringUtils.isNotEmpty(identityLink.getUserId())){
                    if(("candidate".equals(identityLink.getType())  && currentUserId.equals(identityLink.getUserId()))){
                        taskService.deleteUserIdentityLink(id,identityLink.getUserId(),"candidate");
                    }
                }
            }
        }
        actRunIdentitylinkService.deleteByTaskId(id);
        Task task = taskService.createTaskQuery().taskId(id).singleResult();
        //总数
        int nrOfInstances
            = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfInstances")).intValue();
        //已完成数
        int nrOfCompletedInstances
            = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfCompletedInstances")).intValue();
        //未完成数
        int nrOfActiveInstances
            = ((Integer)runtimeService.getVariable(task.getExecutionId(), "nrOfActiveInstances")).intValue();
        //当前循环次数
        int loopCounter
            = ((Integer)runtimeService.getVariable(task.getExecutionId(), "loopCounter")).intValue();
        Map<String, Object> vars = new HashMap<String, Object>();
        // 给集合流程变量设值
        vars.put("nrOfCompletedInstances", nrOfCompletedInstances + 1);
        vars.put("nrOfActiveInstances", nrOfActiveInstances - 1);
        vars.put("loopCounter", loopCounter + 1);
        //修改流程变量
        runtimeService.setVariables(task.getExecutionId(), vars);
        mapper.deleteByTaskId(id);
    }

}
