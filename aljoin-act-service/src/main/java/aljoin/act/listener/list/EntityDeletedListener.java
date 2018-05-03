package aljoin.act.listener.list;

import javax.annotation.Resource;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aljoin.act.dao.entity.ActAljoinExecutionHis;
import aljoin.act.iservice.ActAljoinExecutionHisService;
import aljoin.act.listener.EventHandler;

/**
 * 
 * 实体删除监听器
 *
 * @author：zhongjy
 * 
 * @date：2018年1月19日 上午11:37:19
 */
@Service
public class EntityDeletedListener implements EventHandler {

    private final static Logger logger = LoggerFactory.getLogger(EntityDeletedListener.class);

    @Resource
    private ActAljoinExecutionHisService actAljoinExecutionHisService;

    @Override
    public void handle(ActivitiEvent event) {
        System.out.println("触发实体删除监听器。。。。");
        ActivitiEntityEventImpl eventImpl = (ActivitiEntityEventImpl)event;
        if (eventImpl.getEntity() instanceof ExecutionEntity) {
            ExecutionEntity entity = (ExecutionEntity)eventImpl.getEntity();
            ActAljoinExecutionHis his = new ActAljoinExecutionHis();
            his.setExecId(entity.getId());
            his.setRev(entity.getRevision());
            his.setProcInstId(entity.getProcessInstanceId());
            his.setBusinessKey(entity.getBusinessKey());
            his.setParentId(entity.getParentId());
            his.setProcDefId(entity.getProcessDefinitionId());
            his.setSuperExec(entity.getSuperExecutionId());
            his.setActId(entity.getActivityId());
            his.setIsActive(entity.isActive() ? 1 : 0);
            his.setIsConcurrent(entity.isConcurrent() ? 1 : 0);
            his.setIsScope(entity.isScope() ? 1 : 0);
            his.setIsEventScope(entity.isEventScope() ? 1 : 0);
            his.setSuspensionState(entity.getSuspensionState());
            his.setCachedEntState(entity.getCachedEntityState());
            his.setTenantId(entity.getTenantId());
            his.setName(entity.getName());
            his.setLockTime(entity.getLockTime());
            try {
                actAljoinExecutionHisService.insert(his);
            } catch (Exception e) {
                logger.error("实体删除监听器", e);
            }
        }
    }

}
