package aljoin.act.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.delegate.event.impl.ActivitiEntityWithVariablesEventImpl;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.dao.entity.ActAljoinDelegate;
import aljoin.act.dao.entity.ActAljoinDelegateInfo;
import aljoin.act.dao.entity.ActAljoinDelegateInfoHis;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.dao.entity.ActHiActinst;
import aljoin.act.dao.entity.ActRuIdentitylink;
import aljoin.act.dao.mapper.ActAljoinDelegateMapper;
import aljoin.act.dao.object.ActAljoinDelegateVO;
import aljoin.act.dao.object.AllTaskDataShowVO;
import aljoin.act.dao.object.DelegateDO;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.act.iservice.ActAljoinDelegateInfoHisService;
import aljoin.act.iservice.ActAljoinDelegateInfoService;
import aljoin.act.iservice.ActAljoinDelegateService;
import aljoin.act.iservice.ActAljoinExecutionHisService;
import aljoin.act.iservice.ActAljoinFixedConfigService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.iservice.ActHiActinstService;
import aljoin.act.iservice.ActHiTaskinstService;
import aljoin.act.iservice.ActRuIdentitylinkService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.util.DateUtil;
import aljoin.util.StringUtil;

/**
 * 
 * 任务委托表(服务实现类).
 * 
 * @author：pengsp.
 * 
 * @date： 2017-10-24
 */
@Service
public class ActAljoinDelegateServiceImpl extends ServiceImpl<ActAljoinDelegateMapper, ActAljoinDelegate>
    implements ActAljoinDelegateService {

    @Resource
    private ActAljoinDelegateMapper mapper;
    @Resource
    private ActActivitiService actActivitiService;
    @Resource
    private TaskService taskService;
    @Resource
    private ActAljoinCategoryService actAljoinCategoryService;
    @Resource
    private ActAljoinQueryService actAljoinQueryService;
    @Resource
    private HistoryService historyService;
    @Resource
    private ActAljoinDelegateInfoHisService actAljoinDelegateInfoHisService;
    @Resource
    private ActAljoinDelegateInfoService actAljoinDelegateInfoService;
    @Resource
    private IdentityService identityService;
    @Resource
    private ActAljoinBpmnService actAljoinBpmnService;
    @Resource
    private ActAljoinFixedConfigService actAljoinFixedConfigService;
    @Resource
    private ActHiTaskinstService actHiTaskinstService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;
    @Resource
    private ActRuIdentitylinkService actRuIdentitylinkService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private ActAljoinExecutionHisService actAljoinExecutionHisService;
    @Resource
    private ActHiActinstService actHiActinstService;

    @Override
    public Page<ActAljoinDelegate> list(PageBean pageBean, ActAljoinDelegate obj) throws Exception {
        Where<ActAljoinDelegate> where = new Where<ActAljoinDelegate>();
        where.orderBy("create_time", false);
        Page<ActAljoinDelegate> page =
            selectPage(new Page<ActAljoinDelegate>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinDelegate obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    @Transactional
    public ActAljoinDelegate add(ActAljoinDelegateVO obj) throws Exception {
        // 构造要新增的委托数据
        ActAljoinDelegate actAljoinDelegate = new ActAljoinDelegate();

        if (!StringUtils.isEmpty(obj.getOwnerUserId().toString())) {
            actAljoinDelegate.setOwnerUserId(Long.parseLong(obj.getOwnerUserId()));
        }
        if (!StringUtils.isEmpty(obj.getOwnerUserName())) {
            actAljoinDelegate.setOwnerUserName(obj.getOwnerUserName());
        }
        if (!StringUtils.isEmpty(obj.getOwnerUserFullname())) {
            actAljoinDelegate.setOwnerUserFullname(obj.getOwnerUserFullname());
        }

        if (!StringUtils.isEmpty(obj.getAssigneeUserIds())) {
            actAljoinDelegate.setAssigneeUserIds(obj.getAssigneeUserIds());
        }
        if (!StringUtils.isEmpty(obj.getAssigneeUserNames())) {
            actAljoinDelegate.setAssigneeUserNames(obj.getAssigneeUserNames());
        }
        if (!StringUtils.isEmpty(obj.getAssigneeUserFullnames())) {
            actAljoinDelegate.setAssigneeUserFullnames(obj.getAssigneeUserFullnames());
        }

        if (obj.getBegTime() != null) {
            actAljoinDelegate.setBegTime(obj.getBegTime());
        }

        if (obj.getEndTime() != null) {
            actAljoinDelegate.setEndTime(obj.getEndTime());
        }

        if (!StringUtils.isEmpty(obj.getDelegateDesc())) {
            actAljoinDelegate.setDelegateDesc(obj.getDelegateDesc());
        }
        actAljoinDelegate.setCreateUserId(Long.valueOf(obj.getOwnerUserId()));
        actAljoinDelegate.setCreateUserName(obj.getOwnerUserName());
        actAljoinDelegate.setDelegateBpmnIds("");
        actAljoinDelegate.setIsDelegateAll(1);
        actAljoinDelegate.setIsWaitDelegate(1);
        actAljoinDelegate.setIsAssigneeDelegate(1);

        // 委托状态：1-未开始（当前时间少于开发时间），2-代理中（时间内），3-已结束（时间结束），4-已终止（人为终止）
        Date date = new Date();
        if (obj.getEndTime().getTime() < date.getTime()) {
            throw new Exception("结束时间不能少于当前时间");
        } else if (obj.getBegTime().getTime() < date.getTime() && obj.getEndTime().getTime() > date.getTime()) {
            actAljoinDelegate.setDelegateStatus(2);
        } else if (obj.getBegTime().getTime() > date.getTime()) {
            actAljoinDelegate.setDelegateStatus(1);
        }

        Where<ActAljoinDelegate> where = new Where<ActAljoinDelegate>();
        where.ge("end_time", obj.getBegTime());
        where.le("beg_time", obj.getEndTime());
        where.eq("owner_user_id", obj.getOwnerUserId());
        where.ne("delegate_status", 3);
        where.ne("delegate_status", 4);
        where.setSqlSelect("id");
        List<ActAljoinDelegate> delegateList = selectList(where);
        // 在事件区间内是否已经有委托数据
        if (delegateList.size() > 0) {
            throw new Exception("委托时间和原有委托存在冲突，请修改委托时间区间");
        }
        // 不能选择自己左右受托人
        if (obj.getOwnerUserId().equals(obj.getAssigneeUserIds())) {
            throw new Exception("委托人与受托人冲突，请修改受托用户");
        }

        // 判断是否超过委托层次以及是否存在循环委托
        Set<String> deleteLinks = getAllDelegateLinks(actAljoinDelegate);
        for (String link : deleteLinks) {
            String[] linkArr = link.split(",");
            // 闭环检查
            Set<String> tempSet = new HashSet<String>();
            for (String s : linkArr) {
                tempSet.add(s);
            }
            if (linkArr.length != tempSet.size()) {
                throw new Exception("委托关系存在闭环");
            }
            // 层次检查
            if (linkArr.length > 4) {
                throw new Exception("最多只能进行3层委托");
            }

        }

        insert(actAljoinDelegate);

        return actAljoinDelegate;
    }

    @Override
    public Page<ActAljoinDelegateVO> getAllEntrustWork(PageBean pageBean, ActAljoinDelegateVO obj, Long userId)
        throws Exception {
        Page<ActAljoinDelegateVO> page = new Page<ActAljoinDelegateVO>();
        List<ActAljoinDelegateVO> result = new ArrayList<ActAljoinDelegateVO>();

        Where<ActAljoinDelegate> where = new Where<ActAljoinDelegate>();
        if (obj.getDelegateStatus() != null) {
            where.eq("delegate_status", obj.getDelegateStatus());
        }

        if (StringUtils.isNotEmpty(obj.getAssigneeUserFullnames())) {
            where.like("assignee_user_fullnames", obj.getAssigneeUserFullnames());
        }

        if (obj.getEndTime() != null) {
            where.le("beg_time", obj.getEndTime());
        }

        if (obj.getBegTime() != null) {
            where.ge("end_time", obj.getBegTime());
        }

        where.eq("owner_user_id", userId);
        where.orderBy("id", false);
        where.setSqlSelect(
            "id,assignee_user_fullnames,assignee_user_ids,beg_time,end_time,delegate_status,delegate_desc");
//        List<ActAljoinDelegate> actAljoinDelegateList = selectList(where);
        Page<ActAljoinDelegate> pageOld =
            selectPage(new Page<ActAljoinDelegate>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        List<ActAljoinDelegate> actAljoinDelegateList = pageOld.getRecords();
        for (ActAljoinDelegate actAljoinDelegate : actAljoinDelegateList) {
            ActAljoinDelegateVO actAljoinDelegateVO = new ActAljoinDelegateVO();
            actAljoinDelegateVO.setId(actAljoinDelegate.getId().toString());
            actAljoinDelegateVO.setAssigneeUserFullnames(actAljoinDelegate.getAssigneeUserFullnames());
            actAljoinDelegateVO.setAssigneeUserIds(actAljoinDelegate.getAssigneeUserIds());
            actAljoinDelegateVO.setDelegateStatus(actAljoinDelegate.getDelegateStatus());
            actAljoinDelegateVO.setBegTime(actAljoinDelegate.getBegTime());
            actAljoinDelegateVO.setEndTime(actAljoinDelegate.getEndTime());
            actAljoinDelegateVO.setDelegateDesc(actAljoinDelegate.getDelegateDesc());
            result.add(actAljoinDelegateVO);
        }
        page.setRecords(result);
        page.setSize(pageOld.getSize());
        page.setCurrent(pageOld.getCurrent());
        page.setTotal(pageOld.getTotal());
        return page;
    }

    /**
     * 
     * 根据当前(当前时间在委托区间内)委托获取所有被委托用户委托的叶子用户(递归实现).
     *
     * @return：List<Long>
     *
     * @author：zhongjy
     *
     * @date：2017年11月10日 上午9:53:02
     */
    @Override
    public Map<Long, DelegateDO> getDelegateLinks(ActAljoinDelegate obj, Date currentDate, String delegateUserNames,
        String delegateUserIds, String delegateIds) throws Exception {
        Map<Long, DelegateDO> retultMap = new HashMap<Long, DelegateDO>();
        // 受理人IDs
        String assigneeUserIds = obj.getAssigneeUserIds();
        String[] assigneeUserIdsArr = assigneeUserIds.split(",");
        // 受理人姓名
        String assigneeUserFullnames = obj.getAssigneeUserFullnames();
        String[] assigneeUserFullnamesArr = assigneeUserFullnames.split(",");

        /**
         * 初始化委派链信息
         */
        // 委派(源头)链如：张三,李四,王五
        if (delegateUserNames == null) {
            delegateUserNames = obj.getOwnerUserFullname();
        }
        // 委派(源头)链如：张三id,李四id,王五id(逗号分隔)
        if (delegateUserIds == null) {
            delegateUserIds = obj.getOwnerUserId().toString();
        }
        // 引起委托的任务委托表的主键ID链(逗号分隔)
        if (delegateIds == null) {
            delegateIds = obj.getId().toString();
        }

        for (int i = 0; i < assigneeUserIdsArr.length; i++) {
            Long assigneeUserId = Long.parseLong(assigneeUserIdsArr[i]);
            String assigneeUserFullName = assigneeUserFullnamesArr[i];
            Where<ActAljoinDelegate> actAljoinDelegateWhere = new Where<ActAljoinDelegate>();
            // 被委托人作为拥有者的身份
            actAljoinDelegateWhere.eq("owner_user_id", assigneeUserId);
            // 开始时间少于等于当前时间
            actAljoinDelegateWhere.le("beg_time", currentDate);
            // 结束时间大于等于当前时间
            actAljoinDelegateWhere.ge("end_time", currentDate);
            // 处于代理中的状态
            actAljoinDelegateWhere.eq("delegate_status", 2);
            actAljoinDelegateWhere.setSqlSelect("id,owner_user_id,owner_user_name,owner_user_fullname,assignee_user_ids,assignee_user_names,assignee_user_fullnames");
            List<ActAljoinDelegate> tempList = selectList(actAljoinDelegateWhere);
            if (tempList.size() > 0) {
                // 受托人也有进行委托（并且满足时间条件）
                int flag = 0;
                for (ActAljoinDelegate actAljoinDelegate : tempList) {
                    if (flag == 0) {
                        // 委派(源头)链如：张三,李四,王五
                        delegateUserNames += "," + actAljoinDelegate.getOwnerUserFullname();
                        // 委派(源头)链如：张三id,李四id,王五id(逗号分隔)
                        delegateUserIds += "," + actAljoinDelegate.getOwnerUserId();
                        // 引起委托的任务委托表的主键ID链(逗号分隔)
                        delegateIds += "," + actAljoinDelegate.getId();
                    } else {
                        delegateUserIds = delegateUserIds.substring(0, delegateUserIds.lastIndexOf(",") + 1)
                            + actAljoinDelegate.getOwnerUserId();
                        delegateUserNames = delegateUserNames.substring(0, delegateUserNames.lastIndexOf(",") + 1)
                            + actAljoinDelegate.getOwnerUserFullname();
                        delegateIds =
                            delegateIds.substring(0, delegateIds.lastIndexOf(",") + 1) + actAljoinDelegate.getId();
                    }
                    flag++;
                    retultMap.putAll(getDelegateLinks(actAljoinDelegate, currentDate, delegateUserNames,
                        delegateUserIds, delegateIds));
                }
            } else {
                if (retultMap.containsKey(assigneeUserId)) {
                    // 委托关系形成了闭环
                    throw new Exception("委托异常：委托关系形成了闭环");
                } else {
                    DelegateDO delegateDO = new DelegateDO();
                    delegateDO.setId(assigneeUserId);
                    delegateDO.setUserFullName(assigneeUserFullName);
                    delegateDO.setDelegateUserNames(delegateUserNames);
                    delegateDO.setDelegateUserIds(delegateUserIds);
                    delegateDO.setDelegateIds(delegateIds);
                    retultMap.put(assigneeUserId, delegateDO);
                }
            }
        }
        return retultMap;
    }

    @Override
    @Transactional
    public int addDelegateBiz(ActAljoinDelegate obj, List<Task> taskList) throws Exception {
        // 返回0-不用创建定时器（结束时间在当前时间之前）
        // 返回1-创建结束定时器(当前时间在所选定的时间区间之内)
        // 返回2-创建开始和结束定时器（开始时间在当前时间之后，时间还没到）
        int flag = 0;
        // 1.如果如果当前时间在设定的时间区间内,获取委托数据链：如建立委托a->b，需要把b->c->d...在满足委托时间要求的都要取出来，把a的任务委托给d(最后最后一层--叶子节点,设计上是可以委托给多个人)
        Date currentDate = new Date();
        boolean isInPeriod = DateUtil.isInPeriod(obj.getBegTime(), obj.getEndTime(), currentDate);
        Map<Long, DelegateDO> userMap = new HashMap<Long, DelegateDO>();
        if (isInPeriod) {
            // 当前时间在所选择的区域内
            userMap = getDelegateLinks(obj, currentDate, null, null, null);
            // 2.查询出当前用户可以办理的任务（含已经签收的任务），然后把1中查询出来的用户也设置为这些任务的候选人(对于已经签收的任务需要做特殊处理)
            if (userMap.size() > 0) {
                /*
                 * List<Task> taskList = taskService.createTaskQuery().taskInvolvedUser(obj.
                 * getOwnerUserId().toString()).list();
                 */
                List<String> taskIds = new ArrayList<String>();

                for (Task task : taskList) {
                    if (userMap.size() == 1) {
                        // 需要判断当前的任务是否已经是别人委托给当前用户的委托用户，如果是则需要清空当前办理人的候选权限，把任务转交给自己的受托人（自己就没有了）
                        Where<ActAljoinDelegateInfo> infoWhere = new Where<ActAljoinDelegateInfo>();
                        infoWhere.eq("task_id", task.getId());
                        infoWhere.eq("task_key", task.getTaskDefinitionKey());
                        infoWhere.eq("process_instance_id", task.getProcessInstanceId());
                        infoWhere.eq("assignee_user_id", obj.getOwnerUserId());
                        infoWhere.eq("is_self_task", 0);
                        infoWhere.setSqlSelect("id,owner_user_id,owner_user_fullname,last_delegate_id,delegate_ids,delegate_user_ids,delegate_user_names,first_delegate_id,assignee_user_id,assignee_user_fullname");
                        ActAljoinDelegateInfo orgnlDeleteInfo = actAljoinDelegateInfoService.selectOne(infoWhere);
                        if (orgnlDeleteInfo != null) {
                            // 本身这个任务也是委托任务
                        }
                        // 增加候选关系
                        Set<Long> keys = userMap.keySet();
                        DelegateDO ddo = null;
                        for (Long key : keys) {
                            ddo = userMap.get(key);
                        }
                        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                        if (null != hisTask.getAssignee() && null != hisTask.getClaimTime() ) {
                            // 对将要委派的任务进行解签收
                            taskService.unclaim(task.getId());
                            // 原来已经签收的用户也要成为候选人（前提是受理人是当前用户的拥有者，本任务本省就是自己的，不是别人委派给自己的）
                            if (orgnlDeleteInfo == null) {
                                if (!isCandidateUser(task.getId(), task.getAssignee())) {
                                    taskService.addCandidateUser(task.getId(), task.getAssignee());// 从签收到未签收状态，不用更新用户首页数据
                                }
                            } else {
                                // 删除非拥有者的候选权限
                                taskService.deleteUserIdentityLink(task.getId(), task.getAssignee(), "candidate");
                                // 给真正的拥有者设置候选权限
                                if (!isCandidateUser(task.getId(), orgnlDeleteInfo.getOwnerUserId().toString())) {
                                    taskService.addCandidateUser(task.getId(),
                                        orgnlDeleteInfo.getOwnerUserId().toString());
                                }
                            }
                        } else {
                            if (orgnlDeleteInfo == null) {
                                if (!isCandidateUser(task.getId(), obj.getOwnerUserId().toString())) {
                                    taskService.addCandidateUser(task.getId(), obj.getOwnerUserId().toString());
                                }
                            } else {
                                // 删除非真正拥有者的候选权限
                                taskService.deleteUserIdentityLink(task.getId(), obj.getOwnerUserId().toString(),
                                    "candidate");
                                // 给真正的拥有者设置候选权限
                                if (!isCandidateUser(task.getId(), orgnlDeleteInfo.getOwnerUserId().toString())) {
                                    taskService.addCandidateUser(task.getId(),
                                        orgnlDeleteInfo.getOwnerUserId().toString());
                                }

                            }
                        }
                        // 任务委托前被委托人是否是办理人
                        Integer isSelfTask = 1;
                        if (!isCandidateUser(task.getId(), ddo.getId().toString())) {
                            isSelfTask = 0;
                            taskService.addCandidateUser(task.getId(), ddo.getId().toString());
                        }

                        // 增加委托信息数据
                        ActAljoinDelegateInfo info = new ActAljoinDelegateInfo();
                        info.setIsSelfTask(isSelfTask);
                        // 顶级委托人信息（任务的真正拥有者）
                        if (orgnlDeleteInfo == null) {
                            info.setOwnerUserId(obj.getOwnerUserId());
                            info.setOwnerUserFullname(obj.getOwnerUserFullname());
                        } else {
                            info.setOwnerUserId(orgnlDeleteInfo.getOwnerUserId());
                            info.setOwnerUserFullname(orgnlDeleteInfo.getOwnerUserFullname());
                        }

                        // 直接委托人信息
                        String[] delegateUserIdsArr = ddo.getDelegateUserIds().split(",");
                        String[] delegateUserNamesArr = ddo.getDelegateUserNames().split(",");
                        info.setLastUserId(Long.parseLong(delegateUserIdsArr[delegateUserIdsArr.length - 1]));
                        info.setLastUserFullname(delegateUserNamesArr[delegateUserNamesArr.length - 1]);

                        info.setAssigneeUserId(ddo.getId());
                        info.setAssigneeUserFullname(ddo.getUserFullName());
                        info.setTaskId(task.getId());
                        info.setTaskKey(task.getTaskDefinitionKey());
                        info.setTaskName(
                            StringUtils.isEmpty(task.getName()) ? "无名" + StringUtil.getNRandom(4) : task.getName());
                        info.setProcessId(task.getProcessDefinitionId());
                        info.setProcessInstanceId(task.getProcessInstanceId());
                        info.setHasDo(0);
                        info.setIsActive(1);

                        // 如果任务不是自己的，还要添加上层委托链信息
                        if (orgnlDeleteInfo == null) {
                            info.setDelegateUserNames(ddo.getDelegateUserNames());
                            info.setDelegateUserIds(ddo.getDelegateUserIds());
                            info.setDelegateIds(ddo.getDelegateIds());
                        } else {
                            info.setDelegateUserNames(
                                orgnlDeleteInfo.getDelegateUserNames() + "," + ddo.getDelegateUserNames());
                            info.setDelegateUserIds(
                                orgnlDeleteInfo.getDelegateUserIds() + "," + ddo.getDelegateUserIds());
                            info.setDelegateIds(orgnlDeleteInfo.getDelegateIds() + "," + ddo.getDelegateIds());
                        }

                        // 最有一个delegateId就是他的直接ID
                        String[] delegateIdsArr = ddo.getDelegateIds().split(",");
                        // 最后一级委托ID
                        info.setLastDelegateId(Long.parseLong(delegateIdsArr[delegateIdsArr.length - 1]));
                        // 顶级委托ID
                        if (orgnlDeleteInfo == null) {
                            info.setFirstDelegateId(Long.parseLong(delegateIdsArr[0]));
                        } else {
                            info.setFirstDelegateId(orgnlDeleteInfo.getFirstDelegateId());
                        }

                        // 获取流程查询对象
                        Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
                        actAljoinQueryWhere.eq("process_instance_id", task.getProcessInstanceId());
                        ActAljoinQuery actAljoinQuery = actAljoinQueryService.selectOne(actAljoinQueryWhere);
                        if(actAljoinQuery != null){
                            String[] processCategoryIdsArr = actAljoinQuery.getProcessCategoryIds().split(",");
                            String lastCategoryId = processCategoryIdsArr[processCategoryIdsArr.length - 1];
                            ActAljoinCategory actAljoinCategory = actAljoinCategoryService
                                .selectById(Long.parseLong(StringUtils.isNotEmpty(lastCategoryId) ? lastCategoryId : "0"));

                            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                                .processInstanceId(task.getProcessInstanceId()).singleResult();

                            info.setProcessCategoryName(actAljoinCategory.getCategoryName());
                            info.setProcessCategoryId(actAljoinCategory.getId());
                            info.setUrgentStatus(actAljoinQuery.getUrgentStatus());
                            info.setProcessTitle(actAljoinQuery.getProcessTitle());
                            info.setHandleTime(null);
                            info.setStartTime(actAljoinQuery.getStartTime());
                            info.setProcessStarterId(Long.parseLong(processInstance.getStartUserId()));
                            info.setProcessStarterFullName(actAljoinQuery.getCreateFullUserName());

                            // 委托后需要修改查询表的当前办理人(当前)
                            // List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(task.getId());
                            // 获取流程实例的所有任务的的候选人,如果是会签任务还需要把意见办理过的人也要取出来放到当前处理人里面
                            List<Task> tList =
                                taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
                            Set<String> multiParentExeIdSet = new HashSet<String>();
                            Set<String> multiExeIdSet = new HashSet<String>();
                            Set<String> tIdSet = new HashSet<String>();
                            for (Task t : tList) {
                                tIdSet.add(t.getId());
                                if (actActivitiService.getMultiInstance(t.getProcessInstanceId(),
                                    t.getTaskDefinitionKey()) != null) {
                                    // 会签节点，获取其父execId,用于查找其他已经完成的会签节点的办理人
                                    Execution currentExec = runtimeService.createExecutionQuery()
                                        .executionId(t.getExecutionId()).singleResult();
                                    if (currentExec != null && StringUtils.isNotEmpty(currentExec.getParentId())) {
                                        multiParentExeIdSet.add(currentExec.getParentId());
                                    }
                                }
                            }
                            // 根据用户ID获取当前班里人
                            Where<ActRuIdentitylink> linkWhere = new Where<ActRuIdentitylink>();
                            linkWhere.in("TASK_ID_", tIdSet);
                            linkWhere.eq("TYPE_", "candidate");
                            linkWhere.isNotNull("USER_ID_");
                            List<ActRuIdentitylink> identityLinkList = actRuIdentitylinkService.selectList(linkWhere);
                            // 把已经办理过的多实例用户的节点的办理人也要添加进来
                            if (multiParentExeIdSet.size() > 0) {
                                List<Execution> excutionList = runtimeService.createExecutionQuery()
                                    .processInstanceId(task.getProcessInstanceId()).list();
                                for (Execution execution : excutionList) {
                                    if (multiParentExeIdSet.contains(execution.getParentId())
                                        || multiParentExeIdSet.contains(execution.getId())) {
                                        multiExeIdSet.add(execution.getId());
                                    }

                                }
                                /*Where<ActAljoinExecutionHis> exeHisWhere = new Where<ActAljoinExecutionHis>();
                                exeHisWhere.in("parent_id", multiParentExeIdSet);
                                List<ActAljoinExecutionHis> exeHisList = actAljoinExecutionHisService.selectList(exeHisWhere);
                                for (ActAljoinExecutionHis actAljoinExecutionHis : exeHisList) {
                                  multiExeIdSet.add(actAljoinExecutionHis.getExecId());
                                }*/
                                if (multiExeIdSet.size() > 0) {
                                    Where<ActHiActinst> actHiActinstWhere = new Where<ActHiActinst>();
                                    actHiActinstWhere.in("EXECUTION_ID_", multiExeIdSet);
                                    List<ActHiActinst> actHiActinstList = actHiActinstService.selectList(actHiActinstWhere);
                                    for (ActHiActinst actHiActinst : actHiActinstList) {
                                        if (StringUtils.isNotEmpty(actHiActinst.getAssignee())) {
                                            ActRuIdentitylink link = new ActRuIdentitylink();
                                            link.setUserId(actHiActinst.getAssignee());
                                            identityLinkList.add(link);
                                        }
                                    }
                                }
                            }

                            String currentHandleUserNames = "";
                            for (ActRuIdentitylink identityLink : identityLinkList) {
                                User actUser =
                                    identityService.createUserQuery().userId(identityLink.getUserId()).singleResult();
                                if (actUser != null) {
                                    currentHandleUserNames += actUser.getFirstName() + ",";
                                }
                            }
                            if (StringUtils.isNotEmpty(currentHandleUserNames)) {
                                currentHandleUserNames =
                                    currentHandleUserNames.substring(0, currentHandleUserNames.length() - 1);
                            }
                            actAljoinQuery.setCurrentHandleFullUserName(currentHandleUserNames);
                            actAljoinQueryService.updateById(actAljoinQuery);
                            // 委托后需要修改查询表的当前办理人(历史)
                            Where<ActAljoinQueryHis> actAljoinQueryHisWhere = new Where<ActAljoinQueryHis>();
                            actAljoinQueryHisWhere.eq("process_instance_id", task.getProcessInstanceId());
                            ActAljoinQueryHis actAljoinQueryHis =
                                actAljoinQueryHisService.selectOne(actAljoinQueryHisWhere);
                            actAljoinQueryHis.setCurrentHandleFullUserName(currentHandleUserNames);
                            actAljoinQueryHisService.updateById(actAljoinQueryHis);

                            ActAljoinDelegateInfoHis infoHis = new ActAljoinDelegateInfoHis();
                            // 3.往委托信息表插入数据（含历史表）
                            actAljoinDelegateInfoService.insert(info);
                            BeanUtils.copyProperties(info, infoHis);
                            actAljoinDelegateInfoHisService.insert(infoHis);
                            // 4.删除原来的记录
                            if (orgnlDeleteInfo != null) {
                                actAljoinDelegateInfoService.deleteById(orgnlDeleteInfo.getId());
                                actAljoinDelegateInfoHisService.deleteById(orgnlDeleteInfo.getId());
                            }
                            taskIds.add(task.getId());
                        }
                    } else {
                        // 如果是多有委托用户，多个用户过户后都变成未签收（因为多个用户不知道帮谁签收）--------目前不会用到，只能委托单用户
                        for (Map.Entry<Long, DelegateDO> entry : userMap.entrySet()) {
                            taskService.unclaim(task.getId());
                            taskService.addCandidateUser(task.getId(), entry.getKey().toString());
                            // 增加委托信息数据
                            ActAljoinDelegateInfo info = new ActAljoinDelegateInfo();
                            info.setOwnerUserId(obj.getOwnerUserId());
                            info.setOwnerUserFullname(obj.getOwnerUserFullname());

                            String[] delegateUserNamesArr =
                                userMap.get(entry.getKey()).getDelegateUserNames().split(",");
                            String[] delegateUserIdsArr = userMap.get(entry.getKey()).getDelegateUserIds().split(",");
                            info.setLastUserId(Long.parseLong(delegateUserIdsArr[delegateUserIdsArr.length - 1]));
                            info.setLastUserFullname(delegateUserNamesArr[delegateUserNamesArr.length - 1]);

                            info.setAssigneeUserId(userMap.get(entry.getKey()).getId());
                            info.setAssigneeUserFullname(userMap.get(entry.getKey()).getUserFullName());
                            info.setTaskId(task.getId());
                            info.setTaskKey(task.getTaskDefinitionKey());
                            info.setTaskName(task.getName());
                            info.setProcessId(task.getProcessDefinitionId());
                            info.setProcessInstanceId(task.getProcessInstanceId());
                            info.setHasDo(0);
                            info.setIsActive(1);
                            info.setDelegateUserNames(userMap.get(entry.getKey()).getDelegateUserNames());
                            info.setDelegateUserIds(userMap.get(entry.getKey()).getDelegateUserIds());
                            info.setDelegateIds(userMap.get(entry.getKey()).getDelegateIds());
                            // 最有一个deleteId就是他的直接ID
                            String[] delegateIdsArr = userMap.get(entry.getKey()).getDelegateIds().split(",");
                            info.setLastDelegateId(Long.parseLong(delegateIdsArr[delegateIdsArr.length - 1]));
                            info.setFirstDelegateId(Long.parseLong(delegateIdsArr[0]));

                            // 获取流程查询对象
                            Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
                            actAljoinQueryWhere.eq("process_instance_id", task.getProcessInstanceId());
                            ActAljoinQuery actAljoinQuery = actAljoinQueryService.selectOne(actAljoinQueryWhere);
                            String[] processCategoryIdsArr = actAljoinQuery.getProcessCategoryIds().split(",");
                            String lastCategoryId = processCategoryIdsArr[processCategoryIdsArr.length - 1];
                            ActAljoinCategory actAljoinCategory =
                                actAljoinCategoryService.selectById(Long.parseLong(lastCategoryId));

                            HistoricProcessInstance processInstance =
                                historyService.createHistoricProcessInstanceQuery()
                                    .processInstanceId(task.getProcessInstanceId()).singleResult();

                            info.setProcessCategoryName(actAljoinCategory.getCategoryName());
                            info.setProcessCategoryId(actAljoinCategory.getId());
                            info.setUrgentStatus(actAljoinQuery.getUrgentStatus());
                            info.setProcessTitle(actAljoinQuery.getProcessTitle());
                            info.setHandleTime(null);
                            info.setStartTime(actAljoinQuery.getStartTime());
                            info.setProcessStarterId(Long.parseLong(processInstance.getStartUserId()));
                            info.setProcessStarterFullName(actAljoinQuery.getCreateFullUserName());

                            ActAljoinDelegateInfoHis infoHis = new ActAljoinDelegateInfoHis();
                            // 3.往委托信息表插入数据（含历史表）
                            actAljoinDelegateInfoService.insert(info);
                            BeanUtils.copyProperties(info, infoHis);
                            actAljoinDelegateInfoHisService.insert(infoHis);
                        }
                    }
                }
            }

            // 4.如果结束时间小于当前时间，不需要创建定时器，如果大于当前时间，需要创建定时器，定时删除任务的办理权限，并销毁定时器
            // 创建结束定时器
            flag = 1;
            obj.setDelegateStatus(2);
            updateById(obj);
        } else {
            // 判断是是否同时创建开始（那个时刻查询委托人的任务到被委托人）和结束定时器(开始时间大于当前时间)
            if (DateUtil.getPeriod(currentDate, obj.getBegTime(), DateUtil.UNIT_SECOND) > 0) {
                flag = 2;
            }
        }
        return flag;
    }

    @Override
    @Transactional
    public void stopDelegateBiz(ActAljoinDelegate obj, int stopType) throws Exception {
        // （大前提是任务还没有办理）如终止b->c,则b委托给c以及c的下级的任务全部被收回
        // (注意：只会收回b委托的[委托链中delegate_ids含有b用户的ID]，非b委托的不能收回)，那些任务归到
        // b的名下
        // 实现方式是查询通过本条obj委托产生的委托数据(包括直接产生和间接产生的数据)
        Where<ActAljoinDelegateInfo> actAljoinDelegateInfoWhere = new Where<ActAljoinDelegateInfo>();
        actAljoinDelegateInfoWhere.eq("has_do", 0);
        actAljoinDelegateInfoWhere.like("delegate_ids", obj.getId().toString());
        List<ActAljoinDelegateInfo> actAljoinDelegateInfoList =
            actAljoinDelegateInfoService.selectList(actAljoinDelegateInfoWhere);

        List<String> taskIds = new ArrayList<String>();

        for (ActAljoinDelegateInfo actAljoinDelegateInfo : actAljoinDelegateInfoList) {
            // 删除任务权限
            Task task = taskService.createTaskQuery().taskId(actAljoinDelegateInfo.getTaskId()).singleResult();
            if(task == null){
                //目前有一种情况，委托没正常结束，又发起了新的委托，写这句避免，但是后续要解决
                continue;   
            }
            taskIds.add(task.getId());
            if ((actAljoinDelegateInfo.getAssigneeUserId().toString()).equals(task.getAssignee())) {
                taskService.unclaim(actAljoinDelegateInfo.getTaskId());
            }

            // 如果任务委托前被委托人不是他的候选人
            if (actAljoinDelegateInfo.getIsSelfTask().equals(0)) {
                taskService.deleteCandidateUser(actAljoinDelegateInfo.getTaskId(),
                    actAljoinDelegateInfo.getAssigneeUserId().toString());
            }
            // 删除委托数据
            actAljoinDelegateInfoService.deleteById(actAljoinDelegateInfo.getId());
            actAljoinDelegateInfoHisService.deleteById(actAljoinDelegateInfo.getId());

            if (!(actAljoinDelegateInfo.getFirstDelegateId().toString()).equals(obj.getId().toString())) {
                // 如果不是直接直接产生，不用再生成新的委托数据分配给其他用户,直接删除用户委托出去的数据以及删除定时器即可(删除以及触发定时器需要房子web层的service进行处理)
                // 间接产生,回收自己委托非自己直接产生的任务到自己名下
                // 增加任务权限
                if (!isCandidateUser(actAljoinDelegateInfo.getTaskId(),
                    actAljoinDelegateInfo.getLastUserId().toString())) {
                    taskService.addCandidateUser(actAljoinDelegateInfo.getTaskId(),
                        actAljoinDelegateInfo.getLastUserId().toString());
                }
                // 增加委托数据
                ActAljoinDelegateInfo newInfo = new ActAljoinDelegateInfo();
                ActAljoinDelegateInfoHis newInfoHis = new ActAljoinDelegateInfoHis();
                BeanUtils.copyProperties(actAljoinDelegateInfo, newInfo);
                // 设置需要重新设置的值
                newInfo.setId(null);
                newInfo.setCreateTime(null);
                newInfo.setLastUpdateTime(null);
                newInfo.setVersion(null);
                newInfo.setIsDelete(null);
                newInfo.setLastUpdateUserId(null);
                newInfo.setLastUpdateUserName(null);
                newInfo.setCreateUserId(null);
                newInfo.setCreateUserName(null);

                // 通过截取获取新数据
                String[] delegateUserNamesArr = newInfo.getDelegateUserNames().split(",");
                String[] delegateUserIdsArr = newInfo.getDelegateUserIds().split(",");

                // 定位当前的委托数据是位于委托链中的位置
                List<String> delegateIdsList = Arrays.asList(newInfo.getDelegateIds().split(","));

                String newDelegateUserNames = "";
                String newDelegateUserIds = "";
                String newDelegateIds = "";
                int falg = 0;
                for (String s : delegateIdsList) {
                    if (s.equals(actAljoinDelegateInfo.getLastDelegateId())) {
                        break;
                    }
                    // 新的委托信息人员链条比原来少一级
                    if (falg < (delegateIdsList.size() - 1)) {
                        newDelegateIds += s + ",";
                        newDelegateUserNames += delegateUserNamesArr[falg] + ",";
                        newDelegateUserIds += delegateUserIdsArr[falg] + ",";
                        falg++;
                    }
                }
                newDelegateUserNames = (!"".equals(newDelegateUserNames)
                    ? newDelegateUserNames.substring(0, newDelegateUserNames.length() - 1) : "");
                newDelegateUserIds = (!"".equals(newDelegateUserIds)
                    ? newDelegateUserIds.substring(0, newDelegateUserIds.length() - 1) : "");
                newDelegateIds =
                    (!"".equals(newDelegateIds) ? newDelegateIds.substring(0, newDelegateIds.length() - 1) : "");

                String[] newDelegateUserNamesArr = newDelegateUserNames.split(",");
                String[] newDelegateUserIdsArr = newDelegateUserIds.split(",");
                String[] newDelegateIdsArr = newDelegateIds.split(",");

                newInfo.setLastUserId(Long.parseLong(newDelegateUserIdsArr[newDelegateUserIdsArr.length - 1]));
                newInfo.setLastUserFullname(newDelegateUserNamesArr[newDelegateUserNamesArr.length - 1]);

                newInfo.setAssigneeUserId(actAljoinDelegateInfo.getLastUserId());
                newInfo.setAssigneeUserFullname(actAljoinDelegateInfo.getLastUserFullname());

                newInfo.setDelegateUserNames(newDelegateUserNames);
                newInfo.setDelegateUserIds(newDelegateUserIds);
                newInfo.setDelegateIds(newDelegateIds);

                newInfo.setLastDelegateId(Long.parseLong(newDelegateIdsArr[newDelegateIdsArr.length - 1]));

                actAljoinDelegateInfoService.insert(newInfo);
                BeanUtils.copyProperties(newInfo, newInfoHis);
                actAljoinDelegateInfoHisService.insert(newInfoHis);
            } else {
                if (!isCandidateUser(actAljoinDelegateInfo.getTaskId(),
                    actAljoinDelegateInfo.getOwnerUserId().toString())) {
                    taskService.addCandidateUser(actAljoinDelegateInfo.getTaskId(),
                        actAljoinDelegateInfo.getOwnerUserId().toString());
                }
            }
            // 修改流程查询表的当前办理人
            // 委托后需要修改查询表的当前办理人(当前)
            Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
            actAljoinQueryWhere.eq("process_instance_id", task.getProcessInstanceId());
            ActAljoinQuery actAljoinQuery = actAljoinQueryService.selectOne(actAljoinQueryWhere);

            // List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(task.getId());
            ///////////////
            // 获取流程实例的所有任务的的候选人,如果是会签任务还需要把意见办理过的人也要取出来放到当前处理人里面
            List<Task> tList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
            Set<String> multiParentExeIdSet = new HashSet<String>();
            Set<String> multiExeIdSet = new HashSet<String>();
            Set<String> tIdSet = new HashSet<String>();
            for (Task t : tList) {
                tIdSet.add(t.getId());
                if (actActivitiService.getMultiInstance(t.getProcessInstanceId(), t.getTaskDefinitionKey()) != null) {
                    // 会签节点，获取其父execId,用于查找其他已经完成的会签节点的办理人
                    Execution currentExec =
                        runtimeService.createExecutionQuery().executionId(t.getExecutionId()).singleResult();
                    if (currentExec != null && StringUtils.isNotEmpty(currentExec.getParentId())) {
                        multiParentExeIdSet.add(currentExec.getParentId());
                    }
                }
            }
            // 根据用户ID获取当前班里人
            Where<ActRuIdentitylink> linkWhere = new Where<ActRuIdentitylink>();
            linkWhere.in("TASK_ID_", tIdSet);
            linkWhere.eq("TYPE_", "candidate");
            linkWhere.isNotNull("USER_ID_");
            List<ActRuIdentitylink> identityLinkList = actRuIdentitylinkService.selectList(linkWhere);
            // 把已经办理过的多实例用户的节点的办理人也要添加进来
            if (multiParentExeIdSet.size() > 0) {
                List<Execution> excutionList =
                    runtimeService.createExecutionQuery().processInstanceId(task.getProcessInstanceId()).list();
                for (Execution execution : excutionList) {
                    if (multiParentExeIdSet.contains(execution.getParentId())
                        || multiParentExeIdSet.contains(execution.getId())) {
                        multiExeIdSet.add(execution.getId());
                    }
                }
                /*Where<ActAljoinExecutionHis> exeHisWhere = new Where<ActAljoinExecutionHis>();
                exeHisWhere.in("parent_id", multiParentExeIdSet);
                List<ActAljoinExecutionHis> exeHisList = actAljoinExecutionHisService.selectList(exeHisWhere);
                for (ActAljoinExecutionHis actAljoinExecutionHis : exeHisList) {
                  multiExeIdSet.add(actAljoinExecutionHis.getExecId());
                }*/
                if (multiExeIdSet.size() > 0) {
                    Where<ActHiActinst> actHiActinstWhere = new Where<ActHiActinst>();
                    actHiActinstWhere.in("EXECUTION_ID_", multiExeIdSet);
                    List<ActHiActinst> actHiActinstList = actHiActinstService.selectList(actHiActinstWhere);
                    for (ActHiActinst actHiActinst : actHiActinstList) {
                        if (StringUtils.isNotEmpty(actHiActinst.getAssignee())) {
                            ActRuIdentitylink link = new ActRuIdentitylink();
                            link.setUserId(actHiActinst.getAssignee());
                            identityLinkList.add(link);
                        }
                    }
                }
            }

            String currentHandleUserNames = "";
            for (ActRuIdentitylink identityLink : identityLinkList) {
                User actUser = identityService.createUserQuery().userId(identityLink.getUserId()).singleResult();
                if (actUser != null) {
                    currentHandleUserNames += actUser.getFirstName() + ",";
                }
            }
            if (StringUtils.isNotEmpty(currentHandleUserNames)) {
                currentHandleUserNames = currentHandleUserNames.substring(0, currentHandleUserNames.length() - 1);
            }

            actAljoinQuery.setCurrentHandleFullUserName(currentHandleUserNames);
            actAljoinQueryService.updateById(actAljoinQuery);
            // 委托后需要修改查询表的当前办理人(历史)
            Where<ActAljoinQueryHis> actAljoinQueryHisWhere = new Where<ActAljoinQueryHis>();
            actAljoinQueryHisWhere.eq("process_instance_id", task.getProcessInstanceId());
            ActAljoinQueryHis actAljoinQueryHis = actAljoinQueryHisService.selectOne(actAljoinQueryHisWhere);
            actAljoinQueryHis.setCurrentHandleFullUserName(currentHandleUserNames);
            actAljoinQueryHisService.updateById(actAljoinQueryHis);

        }
        if (stopType == 1) {
            // 定时器执行的时候停止，属于已结束
            obj.setDelegateStatus(3);
            updateById(obj);
        } else if (stopType == 2) {
            // 手工停止停止，属于已终止
            obj.setDelegateStatus(4);
            updateById(obj);
        } else {
            Where<ActAljoinDelegateInfoHis> where = new Where<ActAljoinDelegateInfoHis>();
            where.eq("has_do", 1);
            where.eq("owner_user_id", obj.getOwnerUserId());
            where.andNew().eq("assignee_user_id", obj.getAssigneeUserIds()).or().like("delegate_user_ids", obj.getAssigneeUserIds().toString());
            where.andNew().between("create_time", obj.getBegTime(),obj.getEndTime());
            where.like("delegate_ids", obj.getId().toString());
            int count = actAljoinDelegateInfoHisService.selectCount(where);
            if(count == 0){
                // 没有委托记录的委托可以删除
                deleteById(obj.getId());
            }else{
                obj.setDelegateStatus(4);
                updateById(obj);
            }
        }
    }

    @Override
    @Transactional
    public void appStopDelegateBiz(ActAljoinDelegate obj, String userId, String userName, int stopType)
        throws Exception {
        // （大前提是任务还没有办理）如终止b->c,则b委托给c以及c的下级的任务全部被收回
        // (注意：只会收回b委托的[委托链中delegate_ids含有b用户的ID]，非b委托的不能收回)，那些任务归到
        // b的名下
        // 实现方式是查询通过本条obj委托产生的委托数据(包括直接产生和间接产生的数据)
        Where<ActAljoinDelegateInfo> actAljoinDelegateInfoWhere = new Where<ActAljoinDelegateInfo>();
        actAljoinDelegateInfoWhere.eq("has_do", 0);
        actAljoinDelegateInfoWhere.like("delegate_ids", obj.getId().toString());
        List<ActAljoinDelegateInfo> actAljoinDelegateInfoList =
            actAljoinDelegateInfoService.selectList(actAljoinDelegateInfoWhere);
        for (ActAljoinDelegateInfo actAljoinDelegateInfo : actAljoinDelegateInfoList) {
            // 删除任务权限
            Task task = taskService.createTaskQuery().taskId(actAljoinDelegateInfo.getTaskId()).singleResult();
            if ((actAljoinDelegateInfo.getAssigneeUserId().toString()).equals(task.getAssignee())) {
                taskService.unclaim(actAljoinDelegateInfo.getTaskId());
            }

            taskService.deleteCandidateUser(actAljoinDelegateInfo.getTaskId(),
                actAljoinDelegateInfo.getAssigneeUserId().toString());
            // 删除委托数据
            actAljoinDelegateInfoService.deleteById(actAljoinDelegateInfo.getId());
            actAljoinDelegateInfoHisService.deleteById(actAljoinDelegateInfo.getId());

            if (!(actAljoinDelegateInfo.getFirstDelegateId().toString()).equals(obj.getId().toString())) {
                // 如果不是直接直接产生，不用再生成新的委托数据分配给其他用户,直接删除用户委托出去的数据以及删除定时器即可(删除以及触发定时器需要房子web层的service进行处理)
                // 间接产生,回收自己委托非自己直接产生的任务到自己名下
                // 增加任务权限
                taskService.addCandidateUser(actAljoinDelegateInfo.getTaskId(),
                    actAljoinDelegateInfo.getLastUserId().toString());
                // 增加委托数据
                ActAljoinDelegateInfo newInfo = new ActAljoinDelegateInfo();
                ActAljoinDelegateInfoHis newInfoHis = new ActAljoinDelegateInfoHis();
                BeanUtils.copyProperties(actAljoinDelegateInfo, newInfo);
                // 设置需要重新设置的值
                newInfo.setId(null);
                newInfo.setCreateTime(null);
                newInfo.setLastUpdateTime(null);
                newInfo.setVersion(null);
                newInfo.setIsDelete(null);
                newInfo.setLastUpdateUserId(null);
                newInfo.setLastUpdateUserName(null);
                newInfo.setCreateUserId(Long.valueOf(userId));
                newInfo.setCreateUserName(userName);
                // 通过截取获取新数据
                String[] delegateUserNamesArr = newInfo.getDelegateUserNames().split(",");
                String[] delegateUserIdsArr = newInfo.getDelegateUserIds().split(",");
                // 定位当前的委托数据是位于委托链中的位置
                List<String> delegateIdsList = Arrays.asList(newInfo.getDelegateIds().split(","));
                String newDelegateUserNames = "";
                String newDelegateUserIds = "";
                String newDelegateIds = "";
                int falg = 0;
                for (String s : delegateIdsList) {
                    if (s.equals(actAljoinDelegateInfo.getLastDelegateId())) {
                        break;
                    }
                    // 新的委托信息人员链条比原来少一级
                    if (falg < (delegateIdsList.size() - 1)) {
                        newDelegateIds += s + ",";
                        newDelegateUserNames += delegateUserNamesArr[falg] + ",";
                        newDelegateUserIds += delegateUserIdsArr[falg] + ",";
                        falg++;
                    }
                }
                newDelegateUserNames = (!"".equals(newDelegateUserNames)
                    ? newDelegateUserNames.substring(0, newDelegateUserNames.length() - 1) : "");
                newDelegateUserIds = (!"".equals(newDelegateUserIds)
                    ? newDelegateUserIds.substring(0, newDelegateUserIds.length() - 1) : "");
                newDelegateIds =
                    (!"".equals(newDelegateIds) ? newDelegateIds.substring(0, newDelegateIds.length() - 1) : "");

                String[] newDelegateUserNamesArr = newDelegateUserNames.split(",");
                String[] newDelegateUserIdsArr = newDelegateUserIds.split(",");
                String[] newDelegateIdsArr = newDelegateIds.split(",");

                newInfo.setLastUserId(Long.parseLong(newDelegateUserIdsArr[newDelegateUserIdsArr.length - 1]));
                newInfo.setLastUserFullname(newDelegateUserNamesArr[newDelegateUserNamesArr.length - 1]);

                newInfo.setAssigneeUserId(actAljoinDelegateInfo.getLastUserId());
                newInfo.setAssigneeUserFullname(actAljoinDelegateInfo.getLastUserFullname());

                newInfo.setDelegateUserNames(newDelegateUserNames);
                newInfo.setDelegateUserIds(newDelegateUserIds);
                newInfo.setDelegateIds(newDelegateIds);

                newInfo.setLastDelegateId(Long.parseLong(newDelegateIdsArr[newDelegateIdsArr.length - 1]));

                actAljoinDelegateInfoService.insert(newInfo);
                BeanUtils.copyProperties(newInfo, newInfoHis);
                actAljoinDelegateInfoHisService.insert(newInfoHis);
            } else {
                taskService.addCandidateUser(actAljoinDelegateInfo.getTaskId(),
                    actAljoinDelegateInfo.getOwnerUserId().toString());
            }
        }
        obj.setLastUpdateUserId(Long.valueOf(userId));
        obj.setLastUpdateUserName(userName);
        if (stopType == 1) {
            // 定时器执行的时候停止，属于已结束
            obj.setDelegateStatus(3);
            updateById(obj);
        } else if (stopType == 2) {
            // 手工停止停止，属于已终止
            obj.setDelegateStatus(4);
            updateById(obj);
        } else {
            // 删除
            deleteById(obj.getId());
        }
    }

    @Override
    @Transactional
    synchronized public void timerDelegateBiz(ActAljoinDelegate obj, boolean isAdd) throws Exception {
        // 参数isAdd用来标记是增加委托还是终止（删除委托）
        List<Task> taskList =
            taskService.createTaskQuery().taskCandidateOrAssigned(obj.getOwnerUserId().toString()).list();
        if (isAdd) {
            addDelegateBiz(obj, taskList);
        } else {
            stopDelegateBiz(obj, 1);
        }
    }

    @Override
    @Transactional
    public synchronized void eventCreateDelegateBiz(ActivitiEntityEventImpl activitiEntityEventImpl) throws Exception {
        // 获取当前任务的信息，包括当然任务的候选人和办理人，以及通过候选组获取到的用户班里人信息
        List<Long> userIdList = new ArrayList<Long>();
        TaskEntity taskEntity = (TaskEntity)activitiEntityEventImpl.getEntity();
        Task task = taskService.createTaskQuery().taskId(taskEntity.getId()).singleResult();
        if (null != task) {
            if (StringUtils.isNotEmpty(task.getAssignee())) {
                userIdList.add(Long.parseLong(task.getAssignee()));
            }
            List<IdentityLink> linkList = taskService.getIdentityLinksForTask(task.getId());
            for (IdentityLink identityLinkEntity : linkList) {
                if (identityLinkEntity.getType().equals("candidate")) {
                    if (StringUtils.isNotEmpty(identityLinkEntity.getUserId())) {
                        userIdList.add(Long.parseLong(identityLinkEntity.getUserId()));
                    }
                    if (StringUtils.isNotEmpty(identityLinkEntity.getGroupId())) {
                        // 根据分组ID获取用户ID
                        List<User> userList =
                            identityService.createUserQuery().memberOfGroup(identityLinkEntity.getGroupId()).list();
                        for (User user : userList) {
                            userIdList.add(Long.parseLong(user.getId()));
                        }
                    }
                }
            }
        }
        // 判断有权限进行签收和办理的用户在当前时间段里是否有进行流程委托
        Date currentDate = new Date();
        Where<ActAljoinDelegate> actAljoinDelegateWhere = new Where<ActAljoinDelegate>();
        actAljoinDelegateWhere.in("owner_user_id", userIdList);
        actAljoinDelegateWhere.eq("delegate_status", 2);
        // 开始时间少于等于当前时间
        actAljoinDelegateWhere.le("beg_time", currentDate);
        // 结束时间大于等于当前时间
        actAljoinDelegateWhere.ge("end_time", currentDate);
        List<ActAljoinDelegate> actAljoinDelegateList = selectList(actAljoinDelegateWhere);

        List<Task> taskList = new ArrayList<Task>();
        // System.out.println(JsonUtil.obj2str(taskEntity.get));
        // Task task = taskEntity;
        // Task task =
        // taskService.createTaskQuery().processInstanceId(taskEntity.getProcessInstanceId()).singleResult();
        if (task != null) {
            taskList.add(task);
        }
        for (ActAljoinDelegate actAljoinDelegate : actAljoinDelegateList) {
            addDelegateBiz(actAljoinDelegate, taskList);
        }

    }

    @Override
    @Transactional
    public void eventCompletedDelegateBiz(ActivitiEntityWithVariablesEventImpl activitiEntityWithVariablesEventImpl)
        throws Exception {
        TaskEntity taskEntity = (TaskEntity)activitiEntityWithVariablesEventImpl.getEntity();
        // 获取当前完成任务的ID，流程实例等信息判断当前用户是否委托任务
        String taskId = taskEntity.getId();
        String processInstanceId = taskEntity.getProcessInstanceId();
        String assigneeUserId = taskEntity.getAssignee();
        Where<ActAljoinDelegateInfo> actAljoinDelegateInfoWhere = new Where<ActAljoinDelegateInfo>();
        actAljoinDelegateInfoWhere.eq("process_instance_id", processInstanceId);
        actAljoinDelegateInfoWhere.eq("task_id", taskId);
        // actAljoinDelegateInfoWhere.eq("assignee_user_id", assigneeUserId);
        List<ActAljoinDelegateInfo> infoList = actAljoinDelegateInfoService.selectList(actAljoinDelegateInfoWhere);
        if (infoList.size() > 0) {
            // 是委托任务，但是办理人不一定是被委托人
            for (ActAljoinDelegateInfo actAljoinDelegateInfo : infoList) {
                if ((actAljoinDelegateInfo.getAssigneeUserId().toString()).equals(assigneeUserId)) {
                    // 是委托人干的
                    // 如果是委托任务，需要更改流程委托信息表的数据
                    HistoricTaskInstance hisTask =
                        historyService.createHistoricTaskInstanceQuery().taskId(taskEntity.getId()).singleResult();
                    // 是委托人办理的任务
                    actAljoinDelegateInfo.setHasDo(1);
                    actAljoinDelegateInfo.setHandleTime(hisTask.getEndTime());
                    actAljoinDelegateInfoService.updateById(actAljoinDelegateInfo);

                    ActAljoinDelegateInfoHis infoHis =
                        actAljoinDelegateInfoHisService.selectById(actAljoinDelegateInfo.getId());
                    infoHis.setHasDo(1);
                    infoHis.setHandleTime(hisTask.getEndTime());
                    actAljoinDelegateInfoHisService.updateById(infoHis);
                } else {
                    // 不是委托人干的,但是本任务是有处于被委托的状态，需要删除委托信息
                    actAljoinDelegateInfoService.deleteById(actAljoinDelegateInfo.getId());
                    actAljoinDelegateInfoHisService.deleteById(actAljoinDelegateInfo.getId());
                }
            }
        }
    }

    /**
     * 
     * 根据当前(当前时间在委托区间内)委托获取所有被委托用户委托的叶子用户(递归实现).
     *
     * @return：List<Long>
     *
     * @author：zhongjy
     *
     * @date：2017年11月10日 上午9:53:02
     */
    @Override
    public Map<Long, DelegateDO> getBottomLevel(ActAljoinDelegate obj, String delegateUserIds) throws Exception {
        Map<Long, DelegateDO> retultMap = new HashMap<Long, DelegateDO>();
        // 受理人IDs
        String assigneeUserIds = obj.getAssigneeUserIds();
        String[] assigneeUserIdsArr = assigneeUserIds.split(",");

        // 委派(源头)链如：张三id,李四id,王五id(逗号分隔)
        if (delegateUserIds == null) {
            delegateUserIds = obj.getOwnerUserId().toString();
        }

        for (int i = 0; i < assigneeUserIdsArr.length; i++) {
            Long assigneeUserId = Long.parseLong(assigneeUserIdsArr[i]);
            Where<ActAljoinDelegate> actAljoinDelegateWhere = new Where<ActAljoinDelegate>();
            // 被委托人作为拥有者的身份
            actAljoinDelegateWhere.eq("owner_user_id", assigneeUserId);
            // 处于代理中的状态
            actAljoinDelegateWhere.eq("delegate_status", 2);
            List<ActAljoinDelegate> tempList = selectList(actAljoinDelegateWhere);
            if (tempList.size() > 0) {
                // 受托人也有进行委托（并且满足时间条件）
                int flag = 0;
                for (ActAljoinDelegate actAljoinDelegate : tempList) {
                    // 委派(源头)链如：张三id,李四id,王五id(逗号分隔)
                    if (flag == 0) {
                        delegateUserIds += "," + actAljoinDelegate.getOwnerUserId();
                    } else {
                        delegateUserIds = delegateUserIds.substring(0, delegateUserIds.lastIndexOf(",") + 1)
                            + actAljoinDelegate.getOwnerUserId();
                    }
                    flag++;
                    retultMap.putAll(getBottomLevel(actAljoinDelegate, delegateUserIds));
                }
            } else {
                if (retultMap.containsKey(assigneeUserId)) {
                    // 委托关系形成了闭环
                    throw new Exception("委托异常：委托关系形成了闭环");
                } else {
                    DelegateDO delegateDO = new DelegateDO();
                    delegateDO.setDelegateUserIds(delegateUserIds);
                    retultMap.put(assigneeUserId, delegateDO);
                }
            }
        }
        return retultMap;
    }

    @Override
    public Map<Long, DelegateDO> getTopLevel(ActAljoinDelegate obj, String delegateUserIds) throws Exception {
        Map<Long, DelegateDO> retultMap = new HashMap<Long, DelegateDO>();
        // 受理人IDs
        String assigneeUserIds = obj.getOwnerUserId().toString();
        String[] assigneeUserIdsArr = assigneeUserIds.split(",");

        // 委派(源头)链如：张三id,李四id,王五id(逗号分隔)
        if (delegateUserIds == null) {
            delegateUserIds = obj.getOwnerUserId().toString();
        }

        for (int i = 0; i < assigneeUserIdsArr.length; i++) {
            Long assigneeUserId = Long.parseLong(assigneeUserIdsArr[i]);
            Where<ActAljoinDelegate> actAljoinDelegateWhere = new Where<ActAljoinDelegate>();
            // 拥有者作为被委托
            // actAljoinDelegateWhere.eq("owner_user_id", assigneeUserId);
            actAljoinDelegateWhere.like("assignee_user_ids", assigneeUserId.toString());
            // 处于代理中的状态
            actAljoinDelegateWhere.in("delegate_status", Arrays.asList(1, 2));
            List<ActAljoinDelegate> tempList = selectList(actAljoinDelegateWhere);
            if (tempList.size() > 0) {
                // 受托人也有进行委托（并且满足时间条件）
                int flag = 0;
                for (ActAljoinDelegate actAljoinDelegate : tempList) {
                    // 委派(源头)链如：张三id,李四id,王五id(逗号分隔)
                    if (flag == 0) {
                        delegateUserIds += "," + actAljoinDelegate.getOwnerUserId();
                    } else {
                        delegateUserIds = delegateUserIds.substring(0, delegateUserIds.lastIndexOf(",") + 1)
                            + actAljoinDelegate.getOwnerUserId();
                    }
                    flag++;
                    retultMap.putAll(getTopLevel(actAljoinDelegate, delegateUserIds));
                }
            } else {
                if (retultMap.containsKey(assigneeUserId)) {
                    // 委托关系形成了闭环
                    throw new Exception("委托异常：委托关系形成了闭环");
                } else {
                    DelegateDO delegateDO = new DelegateDO();
                    delegateDO.setDelegateUserIds(delegateUserIds);
                    retultMap.put(assigneeUserId, delegateDO);
                }
            }
        }
        return retultMap;
    }

    @Override
    public Set<String> getAllDelegateLinks(ActAljoinDelegate obj) throws Exception {

        String newOwnerUserId = obj.getOwnerUserId().toString();
        String newAssigneeUserIds = obj.getAssigneeUserIds();
        String[] newAssigneeUserIdsArr = newAssigneeUserIds.split(",");
        if (newAssigneeUserIdsArr.length > 1) {
            throw new Exception("暂不支持同时进行多用户委托");
        }
        Set<String> singleDOSet = new HashSet<String>();
        // 获取所有处于代理中或者为进行代理的数据(系统中原有的数据)
        Where<ActAljoinDelegate> actAljoinDelegateWhere = new Where<ActAljoinDelegate>();
        actAljoinDelegateWhere.setSqlSelect("owner_user_id,assignee_user_ids");
        actAljoinDelegateWhere.in("delegate_status", Arrays.asList(1, 2));
        List<ActAljoinDelegate> actAljoinDelegateList = selectList(actAljoinDelegateWhere);
        for (ActAljoinDelegate actAljoinDelegate : actAljoinDelegateList) {
            Long ownerUserId = actAljoinDelegate.getOwnerUserId();
            String assigneeUserIds = actAljoinDelegate.getAssigneeUserIds();
            String[] assigneeUserIdsArr = assigneeUserIds.split(",");
            for (String assigneeUserId : assigneeUserIdsArr) {
                singleDOSet.add(ownerUserId.toString() + "," + assigneeUserId.toString());
            }
        }

        // 对于新的被委托人，需要查询它是否有作为委托的人的委托数据(获取下级)
        Set<String> bottomDeleteLinkSet = new HashSet<String>();
        for (String newAssigneeUserId : newAssigneeUserIdsArr) {
            // 第一层
            for (String singleDOStr1 : singleDOSet) {
                String firstTemp = "";
                if (singleDOStr1.startsWith(newAssigneeUserId)) {
                    firstTemp = singleDOStr1;
                    bottomDeleteLinkSet.add(firstTemp);
                    String temp1 = singleDOStr1.split(",")[1];

                    // 第二层
                    List<String> secondTempList = new ArrayList<String>();
                    for (String singleDOStr2 : singleDOSet) {
                        String secondTemp = "";
                        if (singleDOStr2.startsWith(temp1)) {
                            String temp2 = singleDOStr2.split(",")[1];
                            secondTemp += "," + temp2;
                            secondTempList.add(temp2);
                            bottomDeleteLinkSet.remove(firstTemp);
                            bottomDeleteLinkSet.add(firstTemp + secondTemp);
                            // 第三层
                            List<String> thirdTempList = new ArrayList<String>();
                            for (String singleDOStr3 : singleDOSet) {
                                for (String secondTop : secondTempList) {
                                    String thirdTemp = "";
                                    if (singleDOStr3.startsWith(secondTop)) {
                                        String temp3 = singleDOStr3.split(",")[1];
                                        thirdTemp += "," + temp3;
                                        thirdTempList.add(temp3);
                                        bottomDeleteLinkSet.remove(firstTemp + secondTemp);
                                        bottomDeleteLinkSet.add(firstTemp + secondTemp + thirdTemp);
                                        // 第四层

                                        for (String singleDOStr4 : singleDOSet) {
                                            for (String thirdTop : thirdTempList) {
                                                String fourthTemp = "";
                                                if (singleDOStr4.startsWith(thirdTop)) {
                                                    String temp4 = singleDOStr4.split(",")[1];
                                                    fourthTemp += "," + temp4;
                                                    bottomDeleteLinkSet.remove(firstTemp + secondTemp + thirdTemp);
                                                    bottomDeleteLinkSet
                                                        .add(firstTemp + secondTemp + thirdTemp + fourthTemp);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // System.out.println(JsonUtil.obj2str(bottomDeleteLinkSet));
        // 对于新的委托人，需要查询有谁委托给我的胃痛数据（获取上级）
        newAssigneeUserIdsArr = new String[] {newOwnerUserId};
        Set<String> topDeleteLinkSet = new HashSet<String>();
        // System.out.println("上级。。。。。");
        for (String newAssigneeUserId : newAssigneeUserIdsArr) {
            // 第一层
            for (String singleDOStr1 : singleDOSet) {
                String firstTemp = "";
                if (singleDOStr1.endsWith(newAssigneeUserId)) {
                    firstTemp = singleDOStr1;
                    topDeleteLinkSet.add(firstTemp);
                    String temp1 = singleDOStr1.split(",")[0];

                    // 第二层
                    List<String> secondTempList = new ArrayList<String>();
                    for (String singleDOStr2 : singleDOSet) {
                        String secondTemp = "";
                        if (singleDOStr2.endsWith(temp1)) {
                            String temp2 = singleDOStr2.split(",")[0];
                            secondTemp += temp2 + ",";
                            secondTempList.add(temp2);
                            topDeleteLinkSet.remove(firstTemp);
                            topDeleteLinkSet.add(secondTemp + firstTemp);
                            // 第三层
                            List<String> thirdTempList = new ArrayList<String>();
                            for (String singleDOStr3 : singleDOSet) {
                                for (String secondTop : secondTempList) {
                                    String thirdTemp = "";
                                    if (singleDOStr3.endsWith(secondTop)) {
                                        String temp3 = singleDOStr3.split(",")[0];
                                        thirdTemp += temp3 + ",";
                                        thirdTempList.add(temp3);
                                        topDeleteLinkSet.remove(secondTemp + firstTemp);
                                        topDeleteLinkSet.add(thirdTemp + secondTemp + firstTemp);
                                        // 第四层

                                        for (String singleDOStr4 : singleDOSet) {
                                            for (String thirdTop : thirdTempList) {
                                                String fourthTemp = "";
                                                if (singleDOStr4.endsWith(thirdTop)) {
                                                    String temp4 = singleDOStr4.split(",")[0];
                                                    fourthTemp += temp4 + ",";
                                                    topDeleteLinkSet.remove(fourthTemp + thirdTemp + secondTemp);
                                                    topDeleteLinkSet
                                                        .add(fourthTemp + thirdTemp + secondTemp + firstTemp);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // System.out.println(JsonUtil.obj2str(topDeleteLinkSet));
        // 上下级拼接到一起(笛卡尔积)
        Set<String> allDeleteLinkSet = new HashSet<String>();
        if (topDeleteLinkSet.size() == 0) {
            topDeleteLinkSet.add(obj.getOwnerUserId().toString());
        }
        if (bottomDeleteLinkSet.size() == 0) {
            bottomDeleteLinkSet.add(obj.getAssigneeUserIds().split(",")[0]);
        }

        for (String top : topDeleteLinkSet) {
            for (String bottom : bottomDeleteLinkSet) {
                allDeleteLinkSet.add(top + "," + bottom);
            }
        }
        // System.out.println(JsonUtil.obj2str(allDeleteLinkSet));
        return allDeleteLinkSet;
    }

    @Override
    public Page<AllTaskDataShowVO> getAllEntrustWorkData(PageBean pageBean, ActAljoinDelegateVO obj, Long userId)
        throws Exception {
        Page<AllTaskDataShowVO> page = new Page<AllTaskDataShowVO>();
        Where<ActAljoinDelegateInfoHis> hisWhere = new Where<ActAljoinDelegateInfoHis>();
        String category = "";
        hisWhere.eq("has_do", 1);
        if (obj.getAssigneeUserNames() != null && !"".equals(obj.getAssigneeUserNames())) {
            obj.setDelegateDesc("");
            // 获取流程id
            String bpmnId = obj.getAssigneeUserNames();
            ActAljoinBpmn actAljoinBpmn = actAljoinBpmnService.selectById(bpmnId);
            if (actAljoinBpmn != null) {
                hisWhere.like("process_id", actAljoinBpmn.getProcessId() + ":");
            } else {
                hisWhere.like("process_id", obj.getAssigneeUserNames());
            }
        }
        if (obj.getDelegateDesc() != null && !"".equals(obj.getDelegateDesc())) {
            category += obj.getDelegateDesc() + ",";
            // 获取所有下级的分组
            List<ActAljoinCategory> cateList =
                actAljoinCategoryService.getAllChildList(Long.valueOf(obj.getDelegateDesc().trim()));
            if (cateList != null && cateList.size() > 0) {
                for (ActAljoinCategory actAljoinCategory : cateList) {
                    category += actAljoinCategory.getId() + ",";
                }
            }
            // -----------
            hisWhere.in("process_category_id", category);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ActAljoinDelegate delegate = this.selectById(obj.getId());
        // hisWhere.where("last_user_id=assignee_user_id and 1={0}", 1);

        hisWhere.andNew();
        hisWhere.eq("owner_user_id", userId);
        String ids = delegate.getAssigneeUserIds();
        hisWhere.andNew();
        hisWhere.like("delegate_user_ids", ids).or("assignee_user_id ={0}", ids);
        hisWhere.andNew();
        if (obj.getAssigneeUserFullnames() != null && !"".equals(obj.getAssigneeUserFullnames())) {
            hisWhere.like("process_title", obj.getAssigneeUserFullnames());
        }
        // -----委托时间内判断-------
        hisWhere.between("create_time", format.parse(format.format(delegate.getBegTime())),
            format.parse(format.format(delegate.getEndTime())));
        // ---------------------
        if (null != obj.getBegTime() && obj.getEndTime() == null){
            hisWhere.ge("create_time", obj.getBegTime());
        }
        if (null == obj.getBegTime() && obj.getEndTime() != null){
            hisWhere.le("create_time", obj.getEndTime());
        }
        if (obj.getBegTime() != null && obj.getEndTime() != null) {
            hisWhere.between("start_time", format.parse(format.format(obj.getBegTime())),
                format.parse(format.format(obj.getEndTime())));
        }
        hisWhere.like("delegate_ids", obj.getId());
        hisWhere.orderBy("handle_time", false);
        hisWhere.setSqlSelect(
            "handle_time,process_title,urgent_status,assignee_user_fullname,process_category_name,start_time,process_starter_full_name");
        Page<ActAljoinDelegateInfoHis> tmpPage = actAljoinDelegateInfoHisService
            .selectPage(new Page<ActAljoinDelegateInfoHis>(pageBean.getPageNum(), pageBean.getPageSize()), hisWhere);
        List<ActAljoinDelegateInfoHis> list = tmpPage.getRecords();
        List<AllTaskDataShowVO> returnList = new ArrayList<AllTaskDataShowVO>();
        if (list != null && list.size() > 0) {
            for (ActAljoinDelegateInfoHis his : list) {
                AllTaskDataShowVO vo = new AllTaskDataShowVO();
                vo.setEndTime(format.format(his.getStartTime()));
                vo.setFormType(his.getProcessCategoryName());
                vo.setTitle(his.getProcessTitle());
                vo.setHtime(format.format(his.getHandleTime()));
                vo.setOperator(his.getAssigneeUserFullname());
                vo.setFounder(his.getProcessStarterFullName());
                vo.setUrgentStatus(his.getUrgentStatus());
                returnList.add(vo);
            }
        }
        page.setRecords(returnList);
        page.setTotal(tmpPage.getTotal());
        page.setSize(tmpPage.getSize());
        return page;
    }

    /**
     * 
     * 检查用户是否已经在候选人的位置上
     *
     * @return：boolean
     *
     * @author：zhongjy
     *
     * @date：2018年1月11日 上午10:58:17
     */
    private boolean isCandidateUser(String taskId, String userId) {
        List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(taskId);
        for (IdentityLink identityLink : identityLinkList) {
            if ("candidate".equals(identityLink.getType()) && userId.equals(identityLink.getUserId())) {
                return true;
            }
        }
        return false;
    }

}
