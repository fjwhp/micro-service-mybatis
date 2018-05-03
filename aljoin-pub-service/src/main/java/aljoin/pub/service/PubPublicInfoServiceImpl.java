package aljoin.pub.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.aut.dao.entity.AutDataStatistics;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.aut.dao.object.AutOrganVO;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserRoleService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.CustomerTaskDefinition;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.pub.dao.entity.PubPublicInfo;
import aljoin.pub.dao.entity.PubPublicInfoCategory;
import aljoin.pub.dao.entity.PubPublicInfoRead;
import aljoin.pub.dao.mapper.PubPublicInfoMapper;
import aljoin.pub.dao.object.AppPubPublicInfo;
import aljoin.pub.dao.object.AppPubPublicInfoDO;
import aljoin.pub.dao.object.AppPubPublicInfoDetailDO;
import aljoin.pub.dao.object.AppPubPublicInfoVO;
import aljoin.pub.dao.object.PubPublicInfoDO;
import aljoin.pub.dao.object.PubPublicInfoVO;
import aljoin.pub.iservice.PubPublicInfoCategoryService;
import aljoin.pub.iservice.PubPublicInfoReadService;
import aljoin.pub.iservice.PubPublicInfoService;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.dao.object.AppResResourceDO;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;

/**
 * 
 * 公共信息表(服务实现类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-12
 */
@Service
public class PubPublicInfoServiceImpl extends ServiceImpl<PubPublicInfoMapper, PubPublicInfo>
    implements PubPublicInfoService {

    @Resource
    private PubPublicInfoMapper mapper;
    @Resource
    private PubPublicInfoCategoryService pubPublicInfoCategoryService;
    @Resource
    private ResResourceService resResourceService;
    @Resource
    private ActActivitiService activitiService;
    @Resource
    private ActAljoinBpmnService actAljoinBpmnService;
    @Resource
    private ActAljoinCategoryService actAljoinCategoryService;
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private TaskService taskService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private AutUserPositionService autUserPositionService;
    @Resource
    private ActFixedFormServiceImpl actFixedFormService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private PubPublicInfoReadService pubPublicInfoReadService;
    @Resource
    private AutDepartmentService autDepartmentService;
    @Resource
    private AutUserRoleService autUserRoleService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;
    @Resource
    private ActAljoinQueryService actAljoinQueryService;
    @Resource
    private AutDataStatisticsService autDataStatisticsService;

    @Override
    public Page<PubPublicInfoDO> list(PageBean pageBean, PubPublicInfoVO obj) throws Exception {
        Where<PubPublicInfo> where = new Where<PubPublicInfo>();
        if (null != obj) {
            if (null != obj.getCategoryId()) {
                where.eq("category_id", obj.getCategoryId());
            }
            if (StringUtils.isNotEmpty(obj.getTitle())) {
                where.like("title", obj.getTitle());
            }
            if (StringUtils.isNotEmpty(obj.getPublishName())) {
                where.like("publish_name", obj.getPublishName());
            }
            if (null != obj.getAuditStatus()) {
                where.eq("audit_status", obj.getAuditStatus());
            }
            if (null != obj.getPeriodStatus()) {
                where.eq("period_status", obj.getPeriodStatus());
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                DateTime time = new DateTime(obj.getEndDate());
                String endDate = time.plusDays(1).toString("yyyy-MM-dd");
                where.between("create_time", DateUtil.str2date(obj.getBegDate()), DateUtil.str2date(endDate));
                // where.and("create_time >= {0} and create_time <=
                // {1}",obj.getBegDate(),obj.getEndDate());
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isEmpty(obj.getEndDate())) {
                where.ge("create_time", DateUtil.str2date(obj.getBegDate()));
            }
            if (StringUtils.isEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                DateTime time = new DateTime(obj.getEndDate());
                String endDate = time.plusDays(1).toString("yyyy-MM-dd");
                where.le("create_time", DateUtil.str2date(endDate));
            }
            if (StringUtils.isNotEmpty(obj.getSearchKey())) {
                String searchKey = obj.getSearchKey();
                where.andNew(" title like {0}", "%" + searchKey + "%");
            }
        }
        where.andNew("create_user_id = {0}", obj.getCreateUserId());
        where.setSqlSelect(
            "id,create_time,create_user_id,title,publish_name,category_id,period,period_status,notice_obj_id,notice_obj_name,content,audit_status,audit_time,audit_reason");
        where.orderBy("create_time", false);
        Page<PubPublicInfo> oldPage
            = selectPage(new Page<PubPublicInfo>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        Page<PubPublicInfoDO> page = new Page<PubPublicInfoDO>();
        List<PubPublicInfo> pubPublicInfoList = oldPage.getRecords();
        List<PubPublicInfoDO> publicInfoDOList = new ArrayList<PubPublicInfoDO>();
        List<Long> categoryIdList = new ArrayList<Long>();
        if (null != pubPublicInfoList && !pubPublicInfoList.isEmpty()) {
            int i = 1;
            for (PubPublicInfo pubPublicInfo : pubPublicInfoList) {
                if (null != pubPublicInfo) {
                    PubPublicInfoDO pubPublicInfoDO = new PubPublicInfoDO();
                    pubPublicInfoDO.setId(pubPublicInfo.getId());
                    pubPublicInfoDO.setNo(i);
                    pubPublicInfoDO
                        .setCategoryId(null != pubPublicInfo.getCategoryId() ? pubPublicInfo.getCategoryId() : 0L);
                    pubPublicInfoDO.setTitle(pubPublicInfo.getTitle());
                    String auditStauts = "";
                    if (null != pubPublicInfo.getCategoryId()) {
                        Where<PubPublicInfoCategory> categoryWherewhere = new Where<PubPublicInfoCategory>();
                        categoryWherewhere.eq("id", pubPublicInfo.getCategoryId());
                        categoryWherewhere.setSqlSelect("id,name,category_rank,process_id,process_name,is_use");
                        // PubPublicInfoCategory category =
                        // pubPublicInfoCategoryService.selectOne(categoryWherewhere);
                        if (null != pubPublicInfo.getAuditStatus()) {
                            if (1 == pubPublicInfo.getAuditStatus()) {
                                auditStauts = "审核中";
                            } else if (2 == pubPublicInfo.getAuditStatus()) {
                                auditStauts = "审核失败";
                            } else if (3 == pubPublicInfo.getAuditStatus()) {
                                auditStauts = "已发布";
                            }
                        }
                        /*
                         * if (null != category && null != category.getIsUse())
                         * { if (1 == category.getIsUse()) { } else {
                         * auditStauts = "已发布"; } }
                         */
                    } else {
                        auditStauts = "已发布";
                    }
                    pubPublicInfoDO.setAuditStatus(auditStauts);
                    DateTime dateTime = new DateTime(pubPublicInfo.getCreateTime());
                    String createDateStr = dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                    /*
                     * String auditTimeStr = ""; if(null !=
                     * pubPublicInfo.getAuditTime()){
                     * 
                     * DateTime auditTime = new
                     * DateTime(pubPublicInfo.getAuditTime()); auditTimeStr =
                     * auditTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                     * }
                     */
                    pubPublicInfoDO.setCreateDate(createDateStr);
                    String validate = "已失效";
                    if (null != pubPublicInfo.getPeriodStatus()) {
                        if (0 == pubPublicInfo.getPeriodStatus()) {
                            validate = "有效";
                        } else if (1 == pubPublicInfo.getPeriodStatus()) {
                            validate = "已失效";
                        }
                    }
                    categoryIdList.add(pubPublicInfo.getCategoryId());
                    pubPublicInfoDO.setValidate(validate);
                    pubPublicInfoDO.setPublishName(pubPublicInfo.getPublishName());
                    publicInfoDOList.add(pubPublicInfoDO);
                    i++;
                }
            }
            if (null != categoryIdList && !categoryIdList.isEmpty()) {
                Where<PubPublicInfoCategory> publicInfoCategoryWhere = new Where<PubPublicInfoCategory>();
                publicInfoCategoryWhere.eq("is_active", 1);
                publicInfoCategoryWhere.in("id", categoryIdList);
                List<PubPublicInfoCategory> categoryList
                    = pubPublicInfoCategoryService.selectList(publicInfoCategoryWhere);
                if (null != categoryList && !categoryList.isEmpty()) {
                    for (PubPublicInfoDO publicInfoDO : publicInfoDOList) {
                        for (PubPublicInfoCategory category : categoryList) {
                            if (null != category && null != publicInfoDO) {
                                if (StringUtils.isNotEmpty(category.getName())) {
                                    if ((null != category.getId() && null != publicInfoDO.getCategoryId())) {
                                        if (category.getId().equals(publicInfoDO.getCategoryId())
                                            && category.getId().intValue() == publicInfoDO.getCategoryId().intValue()) {
                                            publicInfoDO.setCategoryName(category.getName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        page.setRecords(publicInfoDOList);
        page.setSize(oldPage.getSize());
        page.setTotal(oldPage.getTotal());
        page.setCurrent(oldPage.getCurrent());
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(PubPublicInfo obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public PubPublicInfoVO detail(PubPublicInfo obj) throws Exception {
        PubPublicInfoVO pubPublicInfoVO = null;
        if (null != obj && null != obj.getId()) {
            PubPublicInfo pubPublicInfo = selectById(obj.getId());
            if (null != pubPublicInfo) {
                // 人员排序（收件人）
                List<String> tmpList = new ArrayList<String>();
                String[] ids = pubPublicInfo.getNoticeObjId().split(";");
                List<Long> receiveIds = new ArrayList<Long>();
                String allId = "";
                for (int i = 0; i < ids.length; i++) {
                    receiveIds.add(Long.valueOf(ids[i]));
                }
                Where<AutUser> where1 = new Where<AutUser>();
                where1.in("id", ids);
                where1.setSqlSelect("id", "user_name", "full_name");
                List<AutUser> userList = autUserService.selectList(where1);
                Map<Long, Integer> rankList = autUserService.getUserRankList(receiveIds, null);
                if (rankList.size() > 0) {
                    for (Long entry : rankList.keySet()) {
                        String usrid = entry.toString();
                        allId += usrid + ";";
                        for (AutUser autUser : userList) {
                            String tmp = autUser.getId().toString();
                            if (tmp.equals(usrid)) {
                                tmpList.add(autUser.getFullName());
                            }
                        }
                    }
                }
                String names = "";
                for (String name : tmpList) {
                    names += name + ";";
                }
                pubPublicInfo.setNoticeObjName(names.substring(0, names.length() - 1));
                pubPublicInfo.setNoticeObjId(allId);
                pubPublicInfoVO = new PubPublicInfoVO();
                BeanUtils.copyProperties(pubPublicInfo, pubPublicInfoVO);
                Where<PubPublicInfoCategory> categoryWhere = new Where<PubPublicInfoCategory>();
                categoryWhere.eq("id", pubPublicInfoVO.getCategoryId());
                categoryWhere.setSqlSelect("id,name");
                PubPublicInfoCategory category = pubPublicInfoCategoryService.selectOne(categoryWhere);
                if (null != category) {
                    pubPublicInfoVO.setCategoryName(category.getName());
                }
                Where<ResResource> where = new Where<ResResource>();
                where.eq("biz_id", pubPublicInfo.getId());
                List<ResResource> resourceList = resResourceService.selectList(where);
                if (null != resourceList && !resourceList.isEmpty()) {
                    pubPublicInfoVO.setResResourceList(resourceList);
                }
            }
        }
        return pubPublicInfoVO;
    }

    @Override
    public void isReadPub(PubPublicInfo pubPublicInfo, AutUser user) throws Exception {

        if (pubPublicInfo.getProcessId() == null
            || pubPublicInfo.getProcessId() != null && pubPublicInfo.getAuditStatus() == 3) {
            // 当公告不需审核，或审核通过时，用户查看记录是否已读
            if (pubPublicInfo.getCreateUserId() != null) {
                pubPublicInfoReadService.update4User(user.getId(), pubPublicInfo.getId());
            }
        }
    }

    @Override
    public RetMsg pubDetail(AppPubPublicInfo obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        AppPubPublicInfoDetailDO pubPublicInfoVO = null;
        Integer claimStatus = 0;
        if (StringUtils.isNotEmpty(obj.getProcessInstanceId())) {
            Task task = taskService.createTaskQuery().taskId(obj.getProcessInstanceId()).singleResult();
            if (StringUtils.isNotEmpty(task.getAssignee())) {
                claimStatus = 1;
            }
        }
        if (null != obj && null != obj.getId()) {
            PubPublicInfo pubPublicInfo = selectById(obj.getId());
            if (null != pubPublicInfo) {
                // 人员排序（收件人）
                List<String> tmpList = new ArrayList<String>();
                String[] ids = pubPublicInfo.getNoticeObjId().split(";");
                List<Long> receiveIds = new ArrayList<Long>();
                String allId = "";
                for (int i = 0; i < ids.length; i++) {
                    receiveIds.add(Long.valueOf(ids[i]));
                }
                Where<AutUser> where1 = new Where<AutUser>();
                where1.in("id", ids);
                where1.setSqlSelect("id", "user_name", "full_name");
                List<AutUser> userList = autUserService.selectList(where1);
                Map<Long, Integer> rankList = autUserService.getUserRankList(receiveIds, null);
                if (rankList.size() > 0) {
                    for (Long entry : rankList.keySet()) {
                        String usrid = entry.toString();
                        allId += usrid + ";";
                        for (AutUser autUser : userList) {
                            String tmp = autUser.getId().toString();
                            if (tmp.equals(usrid)) {
                                tmpList.add(autUser.getFullName());
                            }
                        }
                    }
                }
                String names = "";
                for (String name : tmpList) {
                    names += name + ";";
                }
                pubPublicInfo.setNoticeObjName(names.substring(0, names.length() - 1));
                pubPublicInfo.setNoticeObjId(allId);
                pubPublicInfoVO = new AppPubPublicInfoDetailDO();
                BeanUtils.copyProperties(pubPublicInfo, pubPublicInfoVO);
                Where<PubPublicInfoCategory> categoryWhere = new Where<PubPublicInfoCategory>();
                categoryWhere.eq("id", pubPublicInfoVO.getCategoryId());
                categoryWhere.setSqlSelect("id,name");
                PubPublicInfoCategory category = pubPublicInfoCategoryService.selectOne(categoryWhere);
                if (null != category) {
                    pubPublicInfoVO.setCategoryName(category.getName());
                }
                Where<ResResource> where = new Where<ResResource>();
                where.eq("biz_id", pubPublicInfo.getId());
                List<ResResource> resourceList = resResourceService.selectList(where);
                List<AppResResourceDO> appResourceList = new ArrayList<AppResResourceDO>();

                if (null != resourceList && !resourceList.isEmpty()) {
                    for (ResResource resResource : resourceList) {
                        AppResResourceDO resResourceDO = new AppResResourceDO();
                        resResourceDO.setId(resResource.getId());
                        resResourceDO.setFileSize(resResource.getFileSize());
                        resResourceDO.setFileName(resResource.getFileName());
                        resResourceDO.setGroupName(resResource.getGroupName());
                        resResourceDO.setOrgnlFileName(resResource.getOrgnlFileName());
                        appResourceList.add(resResourceDO);
                    }
                    pubPublicInfoVO.setResResourceList(appResourceList);
                }
                pubPublicInfoVO.setClaimStatus(claimStatus);
            }
        }
        retMsg.setObject(pubPublicInfoVO);
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg update(PubPublicInfoVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj && null != obj.getId()) {
            if (StringUtils.isEmpty(obj.getTitle())) {
                obj.setTitle("");
                retMsg.setCode(1);
                retMsg.setMessage("标题不能为空");
                return retMsg;
            } else {
                if (obj.getTitle().length() > 200) {
                    retMsg.setCode(1);
                    retMsg.setMessage("标题长度不能超过200个字符");
                    return retMsg;
                }
            }
            if (StringUtils.isEmpty(obj.getPublishName())) {
                obj.setPublishName("");
                retMsg.setCode(1);
                retMsg.setMessage("发布人不能为空");
                return retMsg;
            } else {
                if (obj.getPublishName().length() > 200) {
                    retMsg.setCode(1);
                    retMsg.setMessage("发布人长度不能超过200个字符");
                    return retMsg;
                }
            }

            if (null == obj.getPeriod()) {
                obj.setPeriod(0);
                retMsg.setCode(1);
                retMsg.setMessage("有效期不能为空");
                return retMsg;
            } else {
                if (String.valueOf(obj.getPeriod()).length() > 10) {
                    retMsg.setCode(1);
                    retMsg.setMessage("有效期不能超过10位数");
                    return retMsg;
                }
            }
            PubPublicInfo pubPublicInfo = selectById(obj.getId());
            if (null != pubPublicInfo) {
                if (StringUtils.isNotEmpty(obj.getTitle())) {
                    pubPublicInfo.setTitle(obj.getTitle());
                }
                if (null != obj.getCategoryId()) {
                    pubPublicInfo.setCategoryId(obj.getCategoryId());
                }

                if (null != pubPublicInfo.getPeriodBeginTime() && null != pubPublicInfo.getPeriodEndTime()) {
                    if (null != obj.getPeriod()) {
                        pubPublicInfo.setPeriod(obj.getPeriod());
                        DateTime begTime = new DateTime(pubPublicInfo.getPeriodBeginTime());
                        DateTime endTime = new DateTime(pubPublicInfo.getPeriodEndTime());
                        DateTime calEndTime = begTime.plusDays(obj.getPeriod());
                        if (endTime.getMillis() < calEndTime.getMillis()) {
                            pubPublicInfo.setPeriodStatus(1);
                        }
                    } else {
                        DateTime begTime = new DateTime(pubPublicInfo.getPeriodBeginTime());
                        DateTime endTime = new DateTime(pubPublicInfo.getPeriodEndTime());
                        DateTime calEndTime = begTime.plusDays(pubPublicInfo.getPeriod());
                        if (endTime.getMillis() < calEndTime.getMillis()) {
                            pubPublicInfo.setPeriodStatus(1);
                        }
                    }
                }

                if (StringUtils.isNotEmpty(obj.getNoticeObjId())) {
                    pubPublicInfo.setNoticeObjId(obj.getNoticeObjId());
                }
                if (StringUtils.isNotEmpty(obj.getNoticeObjName())) {
                    pubPublicInfo.setNoticeObjName(obj.getNoticeObjName());
                }
                if (StringUtils.isNotEmpty(obj.getContent())) {
                    pubPublicInfo.setContent(obj.getContent());
                }
                if (StringUtils.isNotEmpty(obj.getPublishName())) {
                    pubPublicInfo.setPublishName(obj.getPublishName());
                }
                updateById(pubPublicInfo);
                //会议草稿新上传附件
                List<ResResource> newResourceList = obj.getResResourceList();
                List<Long> newResourceIds = new ArrayList<Long>();
                if (null != newResourceList && newResourceList.size() > 0) {
                    for (ResResource resResource : newResourceList) {
                        newResourceIds.add(resResource.getId());
                    }
                }
                if (null != newResourceIds && newResourceIds.size() > 0) {
                    List<ResResource> addResource = resResourceService.selectBatchIds(newResourceIds);
                    for (ResResource resResource : addResource) {
                        resResource.setBizId(pubPublicInfo.getId());
                        resResource.setFileDesc("公共信息编辑附件上传");
                    }
                    resResourceService.updateBatchById(addResource);
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg delete(PubPublicInfoVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj && null != obj.getId()) {
            String ids = obj.getId() + ";";
            String idArr[] = null;
            if (ids.indexOf(";") > -1) {
                if (ids.endsWith(";")) {
                    idArr = ids.substring(0, ids.lastIndexOf(";")).split(";");
                } else {
                    idArr = ids.split(";");
                }
            } else {
                idArr = new String[1];
                idArr[0] = obj.getIds();
            }
            if (null != ids) {
                List<Long> idList = new ArrayList<Long>();
                Where<PubPublicInfo> pubWhere = new Where<PubPublicInfo>();
                pubWhere.in("id", idArr);
                List<PubPublicInfo> pubPublicInfoList = selectList(pubWhere);
                if (null != pubPublicInfoList && !pubPublicInfoList.isEmpty()) {
                    for (PubPublicInfo pubPublicInfo : pubPublicInfoList) {
                        if (null != pubPublicInfo) {
                            idList.add(pubPublicInfo.getId());
                        }
                    }
                }
                // 删除选中的公共信息
                if (null != idList && !idList.isEmpty()) {
                    deleteBatchIds(idList);
                    // this.deleteBatchIds(idList);
                    // 删除公共信息的已读未读信息 + 个人数据统计中有这条某条公告的数据更新
                    for (Long objId : idList) {
                        pubPublicInfoReadService.deletePubRead(objId);
                    }
                }
                // 删除附件(暂时不删除)
                /*Where<ResResource> where = new Where<ResResource>();
                where.in("biz_id",idList);
                List<ResResource> resourcesList = resResourceService.selectList(where);
                List<Long> resourcesIds = new ArrayList<Long>();
                for (ResResource resResource : resourcesList) {
                    resourcesIds.add(resResource.getId());
                }
                if(null != resourcesList && !resourcesList.isEmpty()){
                    resResourceService.deleteBatchById(resourcesIds);
                }*/
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    @Transactional
    public Page<PubPublicInfoDO> lastList(PageBean pageBean, PubPublicInfoVO obj) throws Exception {
        Where<PubPublicInfo> where = new Where<PubPublicInfo>();
        if (null != obj) {
            where.like("notice_obj_id", obj.getCreateUserId() + "");
            where.eq("period_status", 0);
            where.eq("audit_status", 3);
            /*if (null != obj.getCategoryId()) {
            	where.andNew("category_id = {0}", obj.getCategoryId());
            }
            if (null != obj.getTitle() && StringUtils.isNotEmpty(obj.getTitle())) {
            	where.andNew("title like {0}", "%" + obj.getTitle() + "%");
            }
            if (null != obj.getAuditStatus()) {
            	where.andNew("audit_status = {0}", obj.getAuditStatus());
            }
            if (null != obj.getPeriodStatus()) {
            	where.andNew("period_status = {0}", obj.getPeriodStatus());
            }
            if (null != obj.getPublishName() && StringUtils.isNotEmpty(obj.getPublishName())) {
            	where.andNew("publish_name like {0}", "%" + obj.getPublishName() + "%");
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
            	DateTime time = new DateTime(obj.getEndDate());
            	String endDate = time.plusDays(1).toString("yyyy-MM-dd");
            	where.between("create_time", obj.getBegDate(), endDate);
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isEmpty(obj.getEndDate())) {
            	where.ge("create_time", obj.getBegDate());
            }
            if (StringUtils.isEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
            	where.le("create_time", obj.getEndDate());
            }
            if (StringUtils.isNotEmpty(obj.getSearchKey())) {
            	String searchKey = obj.getSearchKey();
            	where.andNew(" title like {0} or publish_name like {1}", "%" + searchKey + "%", "%" + searchKey + "%");
            }*/
        }
        // where.andNew("period_status = {0} and audit_status = {1}","0","3");
        where.orderBy("create_time", false);
        where.setSqlSelect("id,category_id,create_time,title,publish_name");
        Page<PubPublicInfo> pageCondition = new Page<PubPublicInfo>(pageBean.getPageNum(), pageBean.getPageSize());
        pageCondition.setSearchCount(pageBean.getIsSearchCount().intValue() == 1 ? true : false);
        Page<PubPublicInfo> oldPage = selectPage(pageCondition, where);
        Page<PubPublicInfoDO> page = new Page<PubPublicInfoDO>();
        List<PubPublicInfo> pubPublicInfoList = oldPage.getRecords();
        List<PubPublicInfoDO> publicInfoDOList = new ArrayList<PubPublicInfoDO>();
        List<Long> categoryIdList = new ArrayList<Long>();
        if (null != pubPublicInfoList && !pubPublicInfoList.isEmpty()) {
            int i = 1;
            for (PubPublicInfo pubPublicInfo : pubPublicInfoList) {
                if (null != pubPublicInfo) {
                    PubPublicInfoDO pubPublicInfoDO = new PubPublicInfoDO();
                    pubPublicInfoDO.setId(pubPublicInfo.getId());
                    pubPublicInfoDO.setNo(i);
                    pubPublicInfoDO.setCategoryId(pubPublicInfo.getCategoryId());
                    pubPublicInfoDO.setCreateDate(pubPublicInfo.getCreateTime().toString());
                    if (pubPublicInfo.getCategoryId() != null) {
                        categoryIdList.add(pubPublicInfo.getCategoryId());
                    }
                    pubPublicInfoDO.setTitle(pubPublicInfo.getTitle());
                    String auditStauts = "审核中";
                    if (null != pubPublicInfo.getAuditStatus()) {
                        if (1 == pubPublicInfo.getAuditStatus()) {
                            auditStauts = "审核中";
                        } else if (2 == pubPublicInfo.getAuditStatus()) {
                            auditStauts = "审核失败";
                        } else if (3 == pubPublicInfo.getAuditStatus()) {
                            auditStauts = "审核通过";
                        }
                    }
                    pubPublicInfoDO.setAuditStatus(auditStauts);
                    DateTime dateTime = new DateTime(pubPublicInfo.getCreateTime());
                    String createDateStr = dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                    String auditTimeStr = "";
                    if (null != pubPublicInfo.getAuditTime()) {

                        DateTime auditTime = new DateTime(pubPublicInfo.getAuditTime());
                        auditTimeStr = auditTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                    }
                    pubPublicInfoDO.setCreateDate(StringUtils.isNotEmpty(auditTimeStr) ? auditTimeStr : createDateStr);
                    String validate = "已失效";
                    if (null != pubPublicInfo.getPeriodStatus()) {
                        if (0 == pubPublicInfo.getPeriodStatus()) {
                            validate = "有效期限";
                        } else if (1 == pubPublicInfo.getPeriodStatus()) {
                            validate = "已失效";
                        }
                    }
                    pubPublicInfoDO.setValidate(validate);
                    pubPublicInfoDO.setPublishName(pubPublicInfo.getPublishName());
                    publicInfoDOList.add(pubPublicInfoDO);
                    i++;
                }
            }
            if (null != categoryIdList && !categoryIdList.isEmpty()) {
                Where<PubPublicInfoCategory> publicInfoCategoryWhere = new Where<PubPublicInfoCategory>();
                publicInfoCategoryWhere.eq("is_active", 1);
                publicInfoCategoryWhere.in("id", categoryIdList);
                List<PubPublicInfoCategory> categoryList
                    = pubPublicInfoCategoryService.selectList(publicInfoCategoryWhere);
                if (null != categoryList && !categoryList.isEmpty()) {
                    for (PubPublicInfoDO publicInfoDO : publicInfoDOList) {
                        for (PubPublicInfoCategory category : categoryList) {
                            if (null != category && null != publicInfoDO) {
                                if (StringUtils.isNotEmpty(category.getName())) {
                                    if ((null != category.getId() && null != publicInfoDO.getCategoryId())) {
                                        if (category.getId().equals(publicInfoDO.getCategoryId())
                                            && category.getId().intValue() == publicInfoDO.getCategoryId().intValue()) {
                                            publicInfoDO.setCategoryName(category.getName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        page.setRecords(publicInfoDOList);
        page.setSize(oldPage.getSize());
        page.setTotal(oldPage.getTotal());
        page.setCurrent(oldPage.getCurrent());
        return page;
    }

    @Override
    @Transactional
    public ActAljoinBpmn submitProcess() throws Exception {
        Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
        where.eq("process_id", "Process_YzL2tlND2E");
        where.eq("is_deploy", 1);
        where.eq("is_active", 1);
        where.setSqlSelect("id,category_id,process_id,process_name");
        ActAljoinBpmn bpmn = actAljoinBpmnService.selectOne(where);
        if (null != bpmn && null != bpmn.getProcessId()) {
            // 启动流程
            @SuppressWarnings("unused")
            ProcessInstance instance = activitiService.startBpmn(bpmn, null, null);
        }
        return bpmn;
    }

    @Override
    public List<ActAljoinBpmn> getBpmnByCategroyId(ActAljoinCategory obj) {
        List<ActAljoinBpmn> actAljoinBpmnList = null;
        if (null != obj && null != obj.getId()) {
            ActAljoinCategory category = actAljoinCategoryService.selectById(obj.getId());
            if (null != category) {
                Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
                where.eq("category_id", category.getId());
                where.eq("is_active", 1);
                where.eq("is_deploy", 1);
                actAljoinBpmnList = actAljoinBpmnService.selectList(where);
            }
        }
        return actAljoinBpmnList;
    }

    @Override
    @Transactional
    public void process(HttpServletRequest request, String bizId, String bizType) throws Exception {
        if (StringUtils.isNotEmpty(bizId) && StringUtils.isNotEmpty(bizType)) {
            PubPublicInfo publicInfo = selectById(Long.parseLong(bizId));
            if (null != publicInfo) {
                PubPublicInfoVO publicInfoVO = detail(publicInfo);
                request.setAttribute("publicInfo", publicInfoVO);
            }
        }
    }

    @Override
    public void autoUpdateStatus() throws Exception {
        Where<PubPublicInfo> where = new Where<PubPublicInfo>();
        where.eq("period_status", 0);
        Date date = new Date();
        where.and().le("period_end_time", date);
        where.setSqlSelect(
            "id,period_status,version,create_time,is_delete,create_user_id,create_user_name,last_update_time,last_update_user_id,last_update_user_name");
        List<PubPublicInfo> pubPublicInfoList = selectList(where);
        if (null != pubPublicInfoList && !pubPublicInfoList.isEmpty()) {
            for (PubPublicInfo publicInfo : pubPublicInfoList) {
                publicInfo.setPeriodStatus(1);
            }
            updateBatchById(pubPublicInfoList);
        }
    }

    @Override
    @Transactional
    public RetMsg checkNextTaskInfo(String taskId) throws Exception {
        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        List<TaskDefinition> list = activitiService.getNextTaskInfo(processInstanceId, false);
        boolean isOrgn = false;
        String openType = "";
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != list && !list.isEmpty()) {
            String assigneeId = "";
            String assigneeUserId = "";
            String assigneeGroupId = "";
            TaskDefinition definition = list.get(0);

            if (null != definition) {
                if (definition.getAssigneeExpression() == null
                    && (definition.getCandidateGroupIdExpressions() == null
                        || definition.getCandidateGroupIdExpressions().size() == 0)
                    && (definition.getCandidateUserIdExpressions() == null
                        || definition.getCandidateUserIdExpressions().size() == 0)) {
                    // 选项为空时弹出整棵组织机构树
                    openType = "1";
                    isOrgn = true;
                }
                Set<String> uIds = new HashSet<String>(); // 所有候选用户id
                List<AutUser> uList = new ArrayList<AutUser>(); // 所有候选用户
                if (null != definition.getAssigneeExpression()) {
                    assigneeId = String.valueOf(definition.getAssigneeExpression());
                    // 受理人
                    List<String> assineedIdList = new ArrayList<String>();
                    if (assigneeId.endsWith(";")) {
                        assineedIdList = Arrays.asList(assigneeId.split(";"));
                    } else {
                        assineedIdList.add(assigneeId);
                    }
//                    Where<AutUser> userWhere = new Where<AutUser>();
//                    userWhere.in("id", assineedIdList);
//                    userWhere.setSqlSelect("id,user_name,full_name");
//                    List<AutUser> assigneedList = autUserService.selectList(userWhere);
                    if (null != assineedIdList && !assineedIdList.isEmpty()) {
                        uIds.addAll(assineedIdList);
//                        uList.addAll(assigneedList);
                    }
                }

                // 候选用户列表assineedUserIdList
                if (null != definition.getCandidateUserIdExpressions()) {
                    Iterator<Expression> it = definition.getCandidateUserIdExpressions().iterator();
                    while (it.hasNext()) {
                        assigneeUserId += String.valueOf(it.next()) + ";";
                    }
                }
                List<String> assineedUserIdList = new ArrayList<String>();
                if (assigneeUserId.endsWith(";")) {
                    assineedUserIdList = Arrays.asList(assigneeUserId.split(";"));
                } else {
                    assineedUserIdList.add(assigneeUserId);
                }
                Where<AutUser> assigneedUserWhere = new Where<AutUser>();
                assigneedUserWhere.in("id", assineedUserIdList);
                assigneedUserWhere.setSqlSelect("id,user_name,full_name");
                uIds.addAll(assineedUserIdList);

                // 候选组
                if (null != definition.getCandidateGroupIdExpressions()) {
                    Iterator<Expression> it = definition.getCandidateGroupIdExpressions().iterator();
                    while (it.hasNext()) {
                        assigneeGroupId += String.valueOf(it.next()) + ";";
                    }
                }
                List<String> assineedGroupIdList = new ArrayList<String>();
                if (assigneeGroupId.endsWith(";")) {
                    assineedGroupIdList = Arrays.asList(assigneeGroupId.split(";"));
                } else {
                    assineedGroupIdList.add(assigneeGroupId);
                }

                // 候选岗位
                // List<Long> uIdList = new ArrayList<Long>();
                Where<AutUserPosition> positionWhere = new Where<AutUserPosition>();
                positionWhere.in("position_id", assineedGroupIdList);
                positionWhere.setSqlSelect("id,position_id,user_id");
                List<AutUserPosition> positionList = autUserPositionService.selectList(positionWhere);
                if (null != positionList && !positionList.isEmpty()) {
                    for (AutUserPosition userPosition : positionList) {
                        if (null != userPosition && null != userPosition.getUserId()) {
                            uIds.add(String.valueOf(userPosition.getUserId()));
                        }
                    }
                }

                // 候选岗位
                Where<AutUserRole> roleWhere = new Where<AutUserRole>();
                roleWhere.in("role_id", assineedGroupIdList);
                roleWhere.setSqlSelect("id,role_id,user_id");
                List<AutUserRole> roleList = autUserRoleService.selectList(roleWhere);

                if (null != roleList && !roleList.isEmpty()) {
                    for (AutUserRole userRole : roleList) {
                        if (null != userRole && null != userRole.getUserId()) {
                            uIds.add(String.valueOf(userRole.getUserId()));
                        }
                    }
                }
                if (null != uIds && !uIds.isEmpty()) {
                    Where<AutUser> where = new Where<AutUser>();
                    where.in("id", uIds);
                    where.setSqlSelect("id,user_name,full_name");
                    uList.addAll(autUserService.selectList(where));
                }
                map.put("user", uList);

                if (uList.size() == 0) {
                    isOrgn = true;
                    openType = "3";
                    map.put("assineedGroupIdList", assineedGroupIdList);
                } else {
                    // 选择部门
                    Where<AutDepartmentUser> departmentWhere = new Where<AutDepartmentUser>();
                    departmentWhere.in("dept_id", assineedGroupIdList);
                    departmentWhere.setSqlSelect("dept_id,id,dept_code,user_id");
                    List<AutDepartmentUser> departmentList = autDepartmentUserService.selectList(departmentWhere);
                    List<AutDepartmentUserVO> departmentUserList = new ArrayList<AutDepartmentUserVO>();
                    if (null != departmentList && !departmentList.isEmpty()) {
                        isOrgn = true;
                        openType = "3";
                        Set<String> deptIdlist = new HashSet<String>();
                        Set<Long> deptUIdList = new HashSet<Long>();
                        List<AutUser> deptUserList = new ArrayList<AutUser>();
                        for (AutDepartmentUser department : departmentList) {
                            if (null != department && null != department.getUserId()) {
                                deptIdlist.add(String.valueOf(department.getId()));
                                deptUIdList.add(department.getUserId());
                                uIds.add(String.valueOf(department.getUserId()));
                            }
                        }
                        if (null != deptUIdList && !deptUIdList.isEmpty()) {
                            Where<AutUser> where = new Where<AutUser>();
                            where.in("id", deptUIdList);
                            where.setSqlSelect("id,user_name,full_name");
                            deptUserList = autUserService.selectList(where);
                            uList.addAll(deptUserList);
                        }
                        List<AutDepartmentUser> deptUser = new ArrayList<AutDepartmentUser>();
                        if (null != uIds && !uIds.isEmpty()) {
                            Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
                            where.setSqlSelect("dept_id,id,dept_code,user_id");
                            where.in("user_id", uIds);
                            deptUser = autDepartmentUserService.selectList(where);
                        }
                        if (null != deptUser && !deptUser.isEmpty()) {
                            for (AutDepartmentUser departmentUser : deptUser) {
                                AutDepartmentUserVO departmentUserVO = new AutDepartmentUserVO();
                                BeanUtils.copyProperties(departmentUser, departmentUserVO);
                                departmentUserList.add(departmentUserVO);
                            }
                        }
                        if (null != deptUserList && !deptUserList.isEmpty()) {
                            for (AutUser user : uList) {
                                for (AutDepartmentUser departmentUser : departmentList) {
                                    if (null != user && null != departmentUser && null != user.getId()
                                        && null != departmentUser.getUserId()) {
                                        if (user.getId().equals(departmentUser.getUserId())
                                            && user.getId().intValue() == departmentUser.getUserId().intValue()) {
                                            AutDepartmentUserVO departmentUserVO = new AutDepartmentUserVO();
                                            BeanUtils.copyProperties(departmentUser, departmentUserVO);
                                            departmentUserVO.setFullName(user.getFullName());
                                            departmentUserList.add(departmentUserVO);
                                        }
                                    }
                                }
                            }
                        }
                        if (null != departmentUserList && !departmentUserList.isEmpty()) {
                            for (int i = 0; i <= departmentUserList.size() - 1; i++) {
                                for (int j = departmentUserList.size() - 1; j > i; j--) {
                                    AutDepartmentUserVO autDepartmentUserVO = departmentUserList.get(i);
                                    AutDepartmentUserVO autDepartmentUserVO2 = departmentUserList.get(j);
                                    if (null != autDepartmentUserVO && null != autDepartmentUserVO2
                                        && null != autDepartmentUserVO.getUserId()
                                        && null != autDepartmentUserVO2.getUserId()) {
                                        if (autDepartmentUserVO.getUserId().equals(autDepartmentUserVO2.getUserId())
                                            && autDepartmentUserVO.getUserId().intValue() == autDepartmentUserVO2
                                                .getUserId().intValue()) {
                                            departmentUserList.remove(i);
                                        }
                                    }
                                }
                            }
                        }
                        map.put("dept", new HashSet<AutDepartmentUser>(departmentUserList));
                    }
                }
            }
        }
        // 判断当前节点是否可回退
        List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(taskId);
        String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
        List<TaskDefinition> preList = activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
        if (preList == null || preList.isEmpty()) {
            map.put("isNotBack", true);
        } else {
            map.put("isNotBack", false);
        }
        map.put("openType", openType);
        map.put("isOrgn", isOrgn);
        retMsg.setCode(0);
        retMsg.setObject(map);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FixedFormProcessLog> getAllTaskInfo(String taskId, String processInstanceId) throws Exception {
        // String processName = "公共信息";
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isEmpty(processInstanceId)) {
            map = actFixedFormService.getLog(taskId, null);
        } else {
            map = actFixedFormService.getLog(taskId, processInstanceId);
        }

        List<FixedFormProcessLog> logList = null;
        if (null != map && !map.isEmpty()) {
            List<AutUser> assigneeList = new ArrayList<AutUser>();
            List<AutUser> recevieList = new ArrayList<AutUser>();
            if (null != map.get("assigneeIdList")) {
                List<Long> assigneeIdList = (List<Long>)map.get("assigneeIdList");
                if (null != assigneeIdList && !assigneeIdList.isEmpty()) {
                    Where<AutUser> assigneeWhere = new Where<AutUser>();
                    assigneeWhere.in("id", new HashSet<Long>(assigneeIdList));
                    assigneeWhere.setSqlSelect("id,user_name,full_name");
                    assigneeList = autUserService.selectList(assigneeWhere);
                }
            }
            if (null != map.get("recevieUserIdList")) {
                List<Long> recevieUserIdList = (List<Long>)map.get("recevieUserIdList");
                if (null != recevieUserIdList && !recevieUserIdList.isEmpty()) {
                    Where<AutUser> recevieWhere = new Where<AutUser>();
                    recevieWhere.in("id", recevieUserIdList);
                    recevieWhere.setSqlSelect("id,user_name,full_name");
                    recevieList = autUserService.selectList(recevieWhere);
                }
            }

            if (null != map.get("logList")) {
                logList = (List<FixedFormProcessLog>)map.get("logList");
                if (null != logList && !logList.isEmpty() && null != assigneeList && !assigneeList.isEmpty()) {
                    for (FixedFormProcessLog log : logList) {
                        for (AutUser user : assigneeList) {
                            if (null != user && null != user.getId() && null != log && null != log.getOperationId()) {
                                if (String.valueOf(user.getId()).equals(log.getOperationId())) {
                                    log.setOperationName(user.getFullName());
                                }
                            }
                        }
                    }
                }
                if (null != logList && !logList.isEmpty() && null != recevieList && !recevieList.isEmpty()) {
                    for (FixedFormProcessLog log : logList) {
                        String recUserName = "";
                        for (AutUser user : recevieList) {
                            if (null != user && null != user.getId() && null != log && null != log.getRecevieUserId()) {
                                if (log.getRecevieUserId().contains(String.valueOf(user.getId()))) {
                                    recUserName += user.getFullName() + ";";

                                }
                            }
                        }
                        log.setRecevieUserName(recUserName);
                    }
                }
            }
        }
        return logList;
    }

    @Override
    public Page<AppPubPublicInfoDO> lastList(PageBean pageBean, AppPubPublicInfoVO obj) throws Exception {
        Where<PubPublicInfo> where = new Where<PubPublicInfo>();
        if (null != obj) {
            where.eq("period_status", 0);
            where.eq("audit_status", 3);
            if (null != obj.getCategoryId() && StringUtils.isNotEmpty(obj.getCategoryId())) {
                where.andNew("category_id = {0}", obj.getCategoryId());
            }
            if (StringUtils.isNotEmpty(obj.getTitle())) {
                where.andNew("title like {0}", "%" + obj.getTitle() + "%");
            }
            if (StringUtils.isNotEmpty(obj.getPublishName())) {
                where.andNew("publish_name like {0}", "%" + obj.getPublishName() + "%");
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                DateTime time = new DateTime(obj.getEndDate());
                String endDate = time.plusDays(1).toString("yyyy-MM-dd");
                where.between("create_time", DateUtil.str2date(obj.getBegDate()), DateUtil.str2date(endDate));
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isEmpty(obj.getEndDate())) {
                where.ge("create_time", obj.getBegDate());
            }
            if (StringUtils.isEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                where.le("create_time", DateUtil.str2dateOrTime(obj.getEndDate()));
            }
            if (StringUtils.isNotEmpty(obj.getSearchKey())) {
                String searchKey = obj.getSearchKey();
                where.andNew(" title like {0} or publish_name like {1}", "%" + searchKey + "%", "%" + searchKey + "%");
            }
        }
        where.like("notice_obj_id", "%" + obj.getCreateUserId() + "%");
        where.orderBy("create_time", false);
        where.setSqlSelect(
            "id,create_time,create_user_id,title,publish_name,category_id,period,period_status,notice_obj_id,notice_obj_name,content,audit_status,audit_time,audit_reason");
        Page<PubPublicInfo> oldPage
            = selectPage(new Page<PubPublicInfo>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        Page<AppPubPublicInfoDO> page = new Page<AppPubPublicInfoDO>();
        List<PubPublicInfo> pubPublicInfoList = oldPage.getRecords();
        List<AppPubPublicInfoDO> publicInfoDOList = new ArrayList<AppPubPublicInfoDO>();
        List<Long> categoryIdList = new ArrayList<Long>();
        if (null != pubPublicInfoList && !pubPublicInfoList.isEmpty()) {
            for (PubPublicInfo pubPublicInfo : pubPublicInfoList) {
                if (null != pubPublicInfo) {
                    AppPubPublicInfoDO pubPublicInfoDO = new AppPubPublicInfoDO();
                    Where<PubPublicInfoRead> w2 = new Where<PubPublicInfoRead>();
                    w2.eq("info_id", pubPublicInfo.getId());
                    w2.eq("read_user_id", obj.getCreateUserId());
                    PubPublicInfoRead pubPublicInfoRead = pubPublicInfoReadService.selectOne(w2);
                    if (pubPublicInfoRead != null) {

                        pubPublicInfoDO.setIsRead(pubPublicInfoRead.getIsRead());
                    }
                    pubPublicInfoDO.setId(pubPublicInfo.getId());
                    categoryIdList.add(pubPublicInfo.getCategoryId());
                    pubPublicInfoDO.setTitle(pubPublicInfo.getTitle());
                    pubPublicInfoDO.setCreateUserId(pubPublicInfo.getCreateUserId());
                    DateTime dateTime = new DateTime(pubPublicInfo.getCreateTime());
                    String createDateStr = dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                    String auditTimeStr = "";
                    if (null != pubPublicInfo.getAuditTime()) {

                        DateTime auditTime = new DateTime(pubPublicInfo.getAuditTime());
                        auditTimeStr = auditTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                    }
                    pubPublicInfoDO.setCreateDate(StringUtils.isNotEmpty(auditTimeStr) ? auditTimeStr : createDateStr);
                    pubPublicInfoDO.setPublishName(pubPublicInfo.getPublishName());
                    pubPublicInfoDO.setCategoryId(pubPublicInfo.getCategoryId());
                    publicInfoDOList.add(pubPublicInfoDO);
                }
            }
        }
        page.setRecords(publicInfoDOList);
        page.setSize(oldPage.getSize());
        page.setTotal(oldPage.getTotal());
        return page;
    }

    @Override
    public RetMsg lastList(AppPubPublicInfoVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        Where<PubPublicInfo> where = new Where<PubPublicInfo>();
        if (null != obj) {
            where.eq("period_status", 0);
            where.eq("audit_status", 3);
            if (null != obj.getCategoryId()) {
                where.andNew("category_id = {0}", obj.getCategoryId());
            }
            if (StringUtils.isNotEmpty(obj.getTitle())) {
                where.andNew("title like {0}", "%" + obj.getTitle() + "%");
            }
            if (StringUtils.isNotEmpty(obj.getPublishName())) {
                where.andNew("publish_name like {0}", "%" + obj.getPublishName() + "%");
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                DateTime time = new DateTime(obj.getEndDate());
                String endDate = time.plusDays(1).toString("yyyy-MM-dd");
                where.between("create_time", DateUtil.str2date(obj.getBegDate()), DateUtil.str2date(endDate));
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isEmpty(obj.getEndDate())) {
                where.ge("create_time", DateUtil.str2date(obj.getBegDate()));
            }
            if (StringUtils.isEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                where.le("create_time", DateUtil.str2dateOrTime(obj.getEndDate()));
            }
            if (StringUtils.isNotEmpty(obj.getSearchKey())) {
                String searchKey = obj.getSearchKey();
                where.andNew(" title like {0} or publish_name like {1}", "%" + searchKey + "%", "%" + searchKey + "%");
            }
        }
        where.or();
        where.like("notice_obj_id", obj.getCreateUserId() + "");
        where.eq("period_status", 0);
        where.eq("audit_status", 3);
        where.orderBy("create_time", false);
        where.setSqlSelect(
            "id,create_time,create_user_id,title,publish_name,category_id,period,period_status,notice_obj_id,notice_obj_name,content,audit_status,audit_time,audit_reason");
        List<PubPublicInfo> pubPublicInfoList = selectList(where);
        List<AppPubPublicInfoDO> publicInfoDOList = new ArrayList<AppPubPublicInfoDO>();
        List<Long> categoryIdList = new ArrayList<Long>();
        if (null != pubPublicInfoList && !pubPublicInfoList.isEmpty()) {
            // int i = 1;
            for (PubPublicInfo pubPublicInfo : pubPublicInfoList) {
                if (null != pubPublicInfo) {
                    AppPubPublicInfoDO pubPublicInfoDO = new AppPubPublicInfoDO();
                    pubPublicInfoDO.setId(pubPublicInfo.getId());
                    categoryIdList.add(pubPublicInfo.getCategoryId());
                    pubPublicInfoDO.setTitle(pubPublicInfo.getTitle());
                    pubPublicInfoDO.setCreateUserId(pubPublicInfo.getCreateUserId());
                    DateTime dateTime = new DateTime(pubPublicInfo.getCreateTime());
                    String createDateStr = dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                    String auditTimeStr = "";
                    if (null != pubPublicInfo.getAuditTime()) {

                        DateTime auditTime = new DateTime(pubPublicInfo.getAuditTime());
                        auditTimeStr = auditTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                    }
                    pubPublicInfoDO.setCreateDate(StringUtils.isNotEmpty(auditTimeStr) ? auditTimeStr : createDateStr);
                    pubPublicInfoDO.setPublishName(pubPublicInfo.getPublishName());
                    pubPublicInfoDO.setCategoryId(pubPublicInfo.getCategoryId());
                    publicInfoDOList.add(pubPublicInfoDO);
                }
            }
        }
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setObject(publicInfoDOList);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public RetMsg getDetailById(PubPublicInfo obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        AppPubPublicInfoDetailDO pubPublicInfoVO = null;
        if (null != obj && null != obj.getId()) {
            PubPublicInfo pubPublicInfo = selectById(obj.getId());
            if (null != pubPublicInfo) {
                pubPublicInfoVO = new AppPubPublicInfoDetailDO();
                if (null != pubPublicInfo.getCreateTime()) {
                    DateTime dateTime = new DateTime(pubPublicInfo.getCreateTime());
                    pubPublicInfoVO.setCreateDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
                }
                BeanUtils.copyProperties(pubPublicInfo, pubPublicInfoVO);
                Where<PubPublicInfoCategory> categoryWhere = new Where<PubPublicInfoCategory>();
                categoryWhere.eq("id", pubPublicInfoVO.getCategoryId());
                categoryWhere.setSqlSelect("id,name");
                PubPublicInfoCategory category = pubPublicInfoCategoryService.selectOne(categoryWhere);
                if (null != category) {
                    pubPublicInfoVO.setCategoryName(category.getName());
                }
                Where<ResResource> where = new Where<ResResource>();
                where.eq("biz_id", pubPublicInfo.getId());
                List<ResResource> resourceList = resResourceService.selectList(where);
                List<AppResResourceDO> resourceDOList = new ArrayList<AppResResourceDO>();
                if (null != resourceList && !resourceList.isEmpty()) {
                    for (ResResource resResource : resourceList) {
                        AppResResourceDO resResourceDO = new AppResResourceDO();
                        resResourceDO.setId(resResource.getId());
                        resResourceDO.setFileSize(resResource.getFileSize());
                        resResourceDO.setFileName(resResource.getFileName());
                        resResourceDO.setGroupName(resResource.getGroupName());
                        resResourceDO.setOrgnlFileName(resResource.getOrgnlFileName());
                        resourceDOList.add(resResourceDO);
                    }
                    pubPublicInfoVO.setResResourceList(resourceDOList);
                }
                if (pubPublicInfo.getProcessId() == null
                    || (pubPublicInfo.getProcessId() != null && pubPublicInfo.getAuditStatus() == 3)) {
                    // 当公告不需审核，或审核通过时，用户查看记录是否已读
                    if (pubPublicInfo.getCreateUserId() != null) {
                        pubPublicInfoReadService.update4User(obj.getCreateUserId(), pubPublicInfo.getId());
                    }
                }
            }

        }
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setObject(pubPublicInfoVO);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public Page<PubPublicInfoDO> multipleList(PageBean pageBean, PubPublicInfoVO obj) throws Exception {
        Where<PubPublicInfo> where = new Where<PubPublicInfo>();
        if (null != obj) {
            // where.eq("period_status",1);
            where.eq("audit_status", 3);
            where.like("notice_obj_id", obj.getCreateUserId() + "");
            if (null != obj.getCategoryId()) {
                where.andNew("category_id = {0}", obj.getCategoryId());
            }
            if (StringUtils.isNotEmpty(obj.getSearchKey())) {
                String searchKey = obj.getSearchKey();
                where.andNew(" title like {0} or publish_name like {1}", "%" + searchKey + "%", "%" + searchKey + "%");
            }
            if (StringUtils.isNotEmpty(obj.getTitle())) {
                where.like("title", obj.getTitle());
            }
            if (null != obj.getAuditStatus()) {
                where.andNew("audit_status = {0}", obj.getAuditStatus());
            }
            if (null != obj.getPeriodStatus()) {
                where.andNew("period_status = {0}", obj.getPeriodStatus());
            }
            if (StringUtils.isNotEmpty(obj.getPublishName())) {
                where.andNew("publish_name like {0}", "%" + obj.getPublishName() + "%");
            }
            /*
             * if (StringUtils.isNotEmpty(obj.getSearchKey())) { where.orNew(
             * "publish_name like {0}", "%" + obj.getSearchKey() + "%");
             * where.andNew("title like {0}", "%" + obj.getSearchKey() + "%"); }
             */
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                DateTime time = new DateTime(obj.getEndDate());
                String endDate = time.plusDays(1).toString("yyyy-MM-dd");
                where.between("create_time", DateUtil.str2date(obj.getBegDate()), DateUtil.str2date(endDate));
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isEmpty(obj.getEndDate())) {
                where.ge("create_time", DateUtil.str2date(obj.getBegDate()));
            }
            if (StringUtils.isEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                DateTime time = new DateTime(obj.getEndDate());
                String endDate = time.plusDays(1).toString("yyyy-MM-dd");
                where.le("create_time", DateUtil.str2date(endDate));
            }
            // if(StringUtils.isNotEmpty(obj.getSearchKey())){
            // String searchKey = obj.getSearchKey();
            // where.andNew(" title like {0} or publish_name like
            // {1}","%"+searchKey+"%","%"+searchKey+"%");
            // }
        }
        // where.or();

        // where.eq("period_status",1);
        where.setSqlSelect(
            "id,create_time,create_user_id,title,publish_name,category_id,period,period_status,notice_obj_id,notice_obj_name,content,audit_status,audit_time,audit_reason");
        where.orderBy("create_time", false);
        Page<PubPublicInfo> oldPage
            = selectPage(new Page<PubPublicInfo>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        Page<PubPublicInfoDO> page = new Page<PubPublicInfoDO>();
        List<PubPublicInfo> pubPublicInfoList = oldPage.getRecords();
        List<PubPublicInfoDO> publicInfoDOList = new ArrayList<PubPublicInfoDO>();
        List<Long> categoryIdList = new ArrayList<Long>();
        if (null != pubPublicInfoList && !pubPublicInfoList.isEmpty()) {
            int i = 1;
            for (PubPublicInfo pubPublicInfo : pubPublicInfoList) {
                if (null != pubPublicInfo) {
                    PubPublicInfoDO pubPublicInfoDO = new PubPublicInfoDO();
                    pubPublicInfoDO.setId(pubPublicInfo.getId());
                    pubPublicInfoDO.setNo(i);
                    pubPublicInfoDO.setCategoryId(pubPublicInfo.getCategoryId());
                    categoryIdList.add(pubPublicInfo.getCategoryId());
                    pubPublicInfoDO.setTitle(pubPublicInfo.getTitle());
                    String auditStauts = "审核中";
                    if (null != pubPublicInfo.getAuditStatus()) {
                        if (1 == pubPublicInfo.getAuditStatus()) {
                            auditStauts = "审核中";
                        } else if (2 == pubPublicInfo.getAuditStatus()) {
                            auditStauts = "审核失败";
                        } else if (3 == pubPublicInfo.getAuditStatus()) {
                            auditStauts = "审核通过";
                        }
                    }
                    pubPublicInfoDO.setAuditStatus(auditStauts);
                    DateTime dateTime = new DateTime(pubPublicInfo.getCreateTime());
                    String createDateStr = dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                    String auditTimeStr = "";
                    if (null != pubPublicInfo.getAuditTime()) {

                        DateTime auditTime = new DateTime(pubPublicInfo.getAuditTime());
                        auditTimeStr = auditTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                    }
                    pubPublicInfoDO.setCreateDate(StringUtils.isNotEmpty(auditTimeStr) ? auditTimeStr : createDateStr);
                    String validate = "已失效";
                    if (null != pubPublicInfo.getPeriodStatus()) {
                        if (0 == pubPublicInfo.getPeriodStatus()) {
                            validate = "有效期限";
                        } else if (1 == pubPublicInfo.getPeriodStatus()) {
                            validate = "已失效";
                        }
                    }
                    pubPublicInfoDO.setValidate(validate);
                    pubPublicInfoDO.setPublishName(pubPublicInfo.getPublishName());
                    publicInfoDOList.add(pubPublicInfoDO);
                    i++;
                }
            }
            if (null != categoryIdList && !categoryIdList.isEmpty()) {
                Where<PubPublicInfoCategory> publicInfoCategoryWhere = new Where<PubPublicInfoCategory>();
                publicInfoCategoryWhere.eq("is_active", 1);
                publicInfoCategoryWhere.in("id", categoryIdList);
                List<PubPublicInfoCategory> categoryList
                    = pubPublicInfoCategoryService.selectList(publicInfoCategoryWhere);
                if (null != categoryList && !categoryList.isEmpty()) {
                    for (PubPublicInfoDO publicInfoDO : publicInfoDOList) {
                        for (PubPublicInfoCategory category : categoryList) {
                            if (null != category && null != publicInfoDO) {
                                if (StringUtils.isNotEmpty(category.getName())) {
                                    if ((null != category.getId() && null != publicInfoDO.getCategoryId())) {
                                        if (category.getId().equals(publicInfoDO.getCategoryId())
                                            && category.getId().intValue() == publicInfoDO.getCategoryId().intValue()) {
                                            publicInfoDO.setCategoryName(category.getName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        page.setRecords(publicInfoDOList);
        page.setSize(oldPage.getSize());
        page.setTotal(oldPage.getTotal());
        page.setCurrent(oldPage.getCurrent());
        return page;
    }

    @Override
    public Page<PubPublicInfoDO> manageList(PageBean pageBean, PubPublicInfoVO obj) throws Exception {
        Where<PubPublicInfo> where = new Where<PubPublicInfo>();
        if (null != obj) {
            /*
             * if (null != obj.getCategoryId()) { where.eq("category_id",
             * obj.getCategoryId()); } if
             * (StringUtils.isNotEmpty(obj.getTitle())) { where.like("title",
             * obj.getTitle()); } if
             * (StringUtils.isNotEmpty(obj.getPublishName())) {
             * where.like("publish_name", obj.getPublishName()); } if (null !=
             * obj.getAuditStatus()) { where.eq("audit_status",
             * obj.getAuditStatus()); } if (null != obj.getPeriodStatus()) {
             * where.eq("period_status", obj.getPeriodStatus()); }
             */
            where.eq("audit_status", 3);
            /* where.like("create_user_id", obj.getCreateUserId() + ""); */
            if (null != obj.getCategoryId()) {
                where.andNew("category_id = {0}", obj.getCategoryId());
            }
            if (StringUtils.isNotEmpty(obj.getTitle())) {
                where.like("title", obj.getTitle());
            }
            if (null != obj.getAuditStatus()) {
                where.andNew("audit_status = {0}", obj.getAuditStatus());
            }
            if (null != obj.getPeriodStatus()) {
                where.andNew("period_status = {0}", obj.getPeriodStatus());
            }
            if (StringUtils.isNotEmpty(obj.getPublishName())) {
                where.andNew("publish_name like {0}", "%" + obj.getPublishName() + "%");
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                DateTime time = new DateTime(obj.getEndDate());
                String endDate = time.plusDays(1).toString("yyyy-MM-dd");
                where.between("create_time", DateUtil.str2date(obj.getBegDate()), DateUtil.str2date(endDate));
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isEmpty(obj.getEndDate())) {
                where.ge("create_time", DateUtil.str2date(obj.getBegDate()));
            }
            if (StringUtils.isEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                DateTime time = new DateTime(obj.getEndDate());
                String endDate = time.plusDays(1).toString("yyyy-MM-dd");
                where.le("create_time", DateUtil.str2date(endDate));
            }
            if (StringUtils.isNotEmpty(obj.getSearchKey())) {
                String searchKey = obj.getSearchKey();
                where.andNew(" title like {0} or publish_name like {1}", "%" + searchKey + "%", "%" + searchKey + "%");
            }
        }
        /* where.isNull("process_id"); */
        /*
         * where.or("audit_status = {0}",3); where.andNew("create_user_id = {0}"
         * , obj.getCreateUserId());
         */

        where.setSqlSelect(
            "id,create_time,create_user_id,title,publish_name,category_id,period,period_status,notice_obj_id,notice_obj_name,content,audit_status,audit_time,audit_reason");
        where.orderBy("create_time", false);
        Page<PubPublicInfo> oldPage
            = selectPage(new Page<PubPublicInfo>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        Page<PubPublicInfoDO> page = new Page<PubPublicInfoDO>();
        List<PubPublicInfo> pubPublicInfoList = oldPage.getRecords();
        List<PubPublicInfoDO> publicInfoDOList = new ArrayList<PubPublicInfoDO>();
        List<Long> categoryIdList = new ArrayList<Long>();
        if (null != pubPublicInfoList && !pubPublicInfoList.isEmpty()) {
            int i = 1;
            for (PubPublicInfo pubPublicInfo : pubPublicInfoList) {
                if (null != pubPublicInfo) {
                    PubPublicInfoDO pubPublicInfoDO = new PubPublicInfoDO();
                    pubPublicInfoDO.setId(pubPublicInfo.getId());
                    pubPublicInfoDO.setNo(i);
                    pubPublicInfoDO.setCreateDate(pubPublicInfo.getCreateTime().toString());
                    pubPublicInfoDO
                        .setCategoryId(null != pubPublicInfo.getCategoryId() ? pubPublicInfo.getCategoryId() : 0L);
                    pubPublicInfoDO.setTitle(pubPublicInfo.getTitle());
                    String auditStauts = "";
                    if (null != pubPublicInfo.getCategoryId()) {
                        Where<PubPublicInfoCategory> categoryWherewhere = new Where<PubPublicInfoCategory>();
                        categoryWherewhere.eq("id", pubPublicInfo.getCategoryId());
                        categoryWherewhere.setSqlSelect("id,name,category_rank,process_id,process_name,is_use");
                        // PubPublicInfoCategory category =
                        // pubPublicInfoCategoryService.selectOne(categoryWherewhere);
                        if (null != pubPublicInfo.getAuditStatus()) {
                            if (1 == pubPublicInfo.getAuditStatus()) {
                                auditStauts = "审核中";
                            } else if (2 == pubPublicInfo.getAuditStatus()) {
                                auditStauts = "审核失败";
                            } else if (3 == pubPublicInfo.getAuditStatus()) {
                                auditStauts = "已发布";
                            }
                        }
                        /*
                         * if (null != category && null != category.getIsUse())
                         * { if (1 == category.getIsUse()) { } else {
                         * auditStauts = "已发布"; } }
                         */
                    } else {
                        auditStauts = "已发布";
                    }
                    pubPublicInfoDO.setAuditStatus(auditStauts);
                    DateTime dateTime = new DateTime(pubPublicInfo.getCreateTime());
                    String createDateStr = dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                    String auditTimeStr = "";
                    if (null != pubPublicInfo.getAuditTime()) {

                        DateTime auditTime = new DateTime(pubPublicInfo.getAuditTime());
                        auditTimeStr = auditTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                    }
                    pubPublicInfoDO.setCreateDate(StringUtils.isNotEmpty(auditTimeStr) ? auditTimeStr : createDateStr);
                    String validate = "已失效";
                    if (null != pubPublicInfo.getPeriodStatus()) {
                        if (0 == pubPublicInfo.getPeriodStatus()) {
                            validate = "有效";
                        } else if (1 == pubPublicInfo.getPeriodStatus()) {
                            validate = "已失效";
                        }
                    }
                    categoryIdList.add(pubPublicInfo.getCategoryId());
                    pubPublicInfoDO.setValidate(validate);
                    pubPublicInfoDO.setPublishName(pubPublicInfo.getPublishName());
                    publicInfoDOList.add(pubPublicInfoDO);
                    i++;
                }
            }
            if (null != categoryIdList && !categoryIdList.isEmpty()) {
                Where<PubPublicInfoCategory> publicInfoCategoryWhere = new Where<PubPublicInfoCategory>();
                publicInfoCategoryWhere.eq("is_active", 1);
                publicInfoCategoryWhere.in("id", categoryIdList);
                List<PubPublicInfoCategory> categoryList
                    = pubPublicInfoCategoryService.selectList(publicInfoCategoryWhere);
                if (null != categoryList && !categoryList.isEmpty()) {
                    for (PubPublicInfoDO publicInfoDO : publicInfoDOList) {
                        for (PubPublicInfoCategory category : categoryList) {
                            if (null != category && null != publicInfoDO) {
                                if (StringUtils.isNotEmpty(category.getName())) {
                                    if ((null != category.getId() && null != publicInfoDO.getCategoryId())) {
                                        if (category.getId().equals(publicInfoDO.getCategoryId())
                                            && category.getId().intValue() == publicInfoDO.getCategoryId().intValue()) {
                                            publicInfoDO.setCategoryName(category.getName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        page.setRecords(publicInfoDOList);
        page.setSize(oldPage.getSize());
        page.setTotal(oldPage.getTotal());
        page.setCurrent(oldPage.getCurrent());
        return page;
    }

    @Override
    @Transactional
    public RetMsg toVoid(String taskId, String bizId, Long userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (StringUtils.isNotEmpty(taskId)) {
            String processInstanceId
                = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
            runtimeService.deleteProcessInstance(processInstanceId, null);
            Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
            queryWhere.eq("process_instance_id", processInstanceId);
            actAljoinQueryService.delete(queryWhere);
            Where<ActAljoinQueryHis> queryHisWhere = new Where<ActAljoinQueryHis>();
            queryHisWhere.eq("process_instance_id", processInstanceId);
            actAljoinQueryHisService.delete(queryHisWhere);
            Date date = new Date();
            if (org.apache.commons.lang.StringUtils.isNotEmpty(bizId)) {
                PubPublicInfo publicInfo = selectById(Long.parseLong(bizId));
                if (null != publicInfo) {
                    publicInfo.setAuditStatus(2);// 审核通过
                    publicInfo.setAuditTime(date);
                    publicInfo.setAuditReason("审核不通过");
                    updateById(publicInfo);
                }
            }
            // 环节办理人待办数量-1
            AutDataStatistics aut = new AutDataStatistics();
            aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
            aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
            aut.setBusinessKey(String.valueOf(userId));
            autDataStatisticsService.minus(aut);
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public RetMsg getPreTaskInfo(String taskId) throws Exception {
        RetMsg retMsg = actFixedFormService.getPreTaskInfo(taskId);
        @SuppressWarnings("unchecked")
        List<CustomerTaskDefinition> list = (List<CustomerTaskDefinition>)retMsg.getObject();
        if (null != list && !list.isEmpty()) {
            for (CustomerTaskDefinition taskDefinition : list) {
                if (null != taskDefinition) {
                    if (StringUtils.isNotEmpty(taskDefinition.getAssignee())) {
                        String userId = taskDefinition.getAssignee();
                        Where<AutUser> userWhere = new Where<AutUser>();
                        userWhere.setSqlSelect("id,user_name,full_name");
                        if (userId.indexOf(";") > -1) {
                            List<String> userIdList = Arrays.asList(userId.split(";"));
                            userWhere.in("id", userIdList);
                        } else {
                            userWhere.eq("id", userId);
                        }
                        List<AutUser> userList = autUserService.selectList(userWhere);
                        String assigneeName = "";
                        if (null != userList && !userList.isEmpty()) {
                            for (AutUser user : userList) {
                                assigneeName += user.getFullName() + ";";
                            }
                        }
                        taskDefinition.setAssigneeName(assigneeName);
                    }
                    if (StringUtils.isNotEmpty(taskDefinition.getDeptId())) {
                        String deptId = taskDefinition.getDeptId();
                        Where<AutDepartment> deptWhere = new Where<AutDepartment>();
                        if (deptId.indexOf(";") > -1) {
                            List<String> deptIdList = Arrays.asList(deptId.split(";"));
                            deptWhere.in("id", deptIdList);
                        } else {
                            deptWhere.eq("id", deptId);
                        }
                        deptWhere.setSqlSelect("id,dept_name");
                        List<AutDepartment> deptList = autDepartmentService.selectList(deptWhere);
                        String deptName = "";
                        if (null != deptList && !deptList.isEmpty()) {
                            for (AutDepartment dept : deptList) {
                                deptName += dept.getDeptName() + ";";
                            }
                            taskDefinition.setDeptName(deptName);
                        }
                    }
                }
            }
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
            retMsg.setObject(list);
        }
        return retMsg;
    }

    @Override
    public Page<PubPublicInfoDO> lastListRead(PageBean pageBean, PubPublicInfoVO obj) throws Exception {
        Where<PubPublicInfo> where = new Where<PubPublicInfo>();
        if (null != obj) {
            where.like("notice_obj_id", obj.getCreateUserId() + "");
            where.eq("period_status", 0);
            where.eq("audit_status", 3);
            if (null != obj.getCategoryId()) {
                where.andNew("category_id = {0}", obj.getCategoryId());
            }
            if (null != obj.getTitle() && StringUtils.isNotEmpty(obj.getTitle())) {
                where.andNew("title like {0}", "%" + obj.getTitle() + "%");
            }
            if (null != obj.getAuditStatus()) {
                where.andNew("audit_status = {0}", obj.getAuditStatus());
            }
            if (null != obj.getPeriodStatus()) {
                where.andNew("period_status = {0}", obj.getPeriodStatus());
            }
            /*
             * if (null != obj.getIsRead()) { where.andNew("period_status = {0}"
             * , obj.getPeriodStatus()); }
             */
            if (null != obj.getPublishName() && StringUtils.isNotEmpty(obj.getPublishName())) {
                where.andNew("publish_name like {0}", "%" + obj.getPublishName() + "%");
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                DateTime time = new DateTime(obj.getEndDate());
                String endDate = time.plusDays(1).toString("yyyy-MM-dd");
                where.between("create_time", DateUtil.str2date(obj.getBegDate()), DateUtil.str2date(endDate));
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isEmpty(obj.getEndDate())) {
                where.ge("create_time", DateUtil.str2date(obj.getBegDate()));
            }
            if (StringUtils.isEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                DateTime time = new DateTime(obj.getEndDate());
                String endDate = time.plusDays(1).toString("yyyy-MM-dd");
                where.le("create_time", DateUtil.str2date(endDate));
            }
            if (StringUtils.isNotEmpty(obj.getSearchKey())) {
                String searchKey = obj.getSearchKey();
                where.andNew(" title like {0} or publish_name like {1}", "%" + searchKey + "%", "%" + searchKey + "%");
            }
        }
        // where.andNew("period_status = {0} and audit_status = {1}","0","3");
        where.orderBy("create_time", false);
        where.setSqlSelect(
            "id,create_time,create_user_id,title,publish_name,category_id,period,period_status,notice_obj_id,notice_obj_name,content,audit_status,audit_time,audit_reason");
        /*
         * Where<PubPublicInfoRead> w2 = new Where<PubPublicInfoRead>();
         * List<PubPublicInfo> list = selectList(where);
         */

        Page<PubPublicInfo> oldPage
            = selectPage(new Page<PubPublicInfo>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        Page<PubPublicInfoDO> page = new Page<PubPublicInfoDO>();
        List<PubPublicInfo> pubPublicInfoList = oldPage.getRecords();
        List<Long> pubInfoIdList = new ArrayList<Long>();
        List<PubPublicInfoDO> publicInfoDOList = new ArrayList<PubPublicInfoDO>();
        List<Long> categoryIdList = new ArrayList<Long>();
        if (null != pubPublicInfoList && !pubPublicInfoList.isEmpty()) {
            int i = 1;
            for (PubPublicInfo pubPublicInfo : pubPublicInfoList) {
                if (null != pubPublicInfo) {
                    PubPublicInfoDO pubPublicInfoDO = new PubPublicInfoDO();
                    pubPublicInfoDO.setId(pubPublicInfo.getId());
                    pubInfoIdList.add(pubPublicInfo.getId());
                    pubPublicInfoDO.setNo(i);
                    pubPublicInfoDO.setCategoryId(pubPublicInfo.getCategoryId());
                    if (!categoryIdList.contains(pubPublicInfo.getCategoryId())) {
                        categoryIdList.add(pubPublicInfo.getCategoryId());
                    }
                    pubPublicInfoDO.setTitle(pubPublicInfo.getTitle());
                    String auditStauts = "审核中";
                    if (null != pubPublicInfo.getAuditStatus()) {
                        if (1 == pubPublicInfo.getAuditStatus()) {
                            auditStauts = "审核中";
                        } else if (2 == pubPublicInfo.getAuditStatus()) {
                            auditStauts = "审核失败";
                        } else if (3 == pubPublicInfo.getAuditStatus()) {
                            auditStauts = "审核通过";
                        }
                    }
                    pubPublicInfoDO.setAuditStatus(auditStauts);
                    DateTime dateTime = new DateTime(pubPublicInfo.getCreateTime());
                    String createDateStr = dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                    String auditTimeStr = "";
                    if (null != pubPublicInfo.getAuditTime()) {

                        DateTime auditTime = new DateTime(pubPublicInfo.getAuditTime());
                        auditTimeStr = auditTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM);
                    }
                    pubPublicInfoDO.setCreateDate(StringUtils.isNotEmpty(auditTimeStr) ? auditTimeStr : createDateStr);
                    String validate = "已失效";
                    if (null != pubPublicInfo.getPeriodStatus()) {
                        if (0 == pubPublicInfo.getPeriodStatus()) {
                            validate = "有效期限";
                        } else if (1 == pubPublicInfo.getPeriodStatus()) {
                            validate = "已失效";
                        }
                    }
                    pubPublicInfoDO.setValidate(validate);
                    pubPublicInfoDO.setPublishName(pubPublicInfo.getPublishName());
                    publicInfoDOList.add(pubPublicInfoDO);
                    i++;
                }
            }
            if (null != categoryIdList && !categoryIdList.isEmpty()) {
                // 查询公共信息分类
                Where<PubPublicInfoCategory> publicInfoCategoryWhere = new Where<PubPublicInfoCategory>();
                publicInfoCategoryWhere.setSqlSelect("id,name");
                publicInfoCategoryWhere.eq("is_active", 1);
                publicInfoCategoryWhere.in("id", categoryIdList);
                List<PubPublicInfoCategory> categoryList
                    = pubPublicInfoCategoryService.selectList(publicInfoCategoryWhere);
                // 查询已读未读信息
                Where<PubPublicInfoRead> w2 = new Where<PubPublicInfoRead>();
                w2.setSqlSelect("id,info_id,read_user_id,is_read");
                w2.in("info_id", pubInfoIdList);
                w2.eq("read_user_id", obj.getCreateUserId());
                List<PubPublicInfoRead> pubPublicInfoReadList = pubPublicInfoReadService.selectList(w2);
                for (PubPublicInfoDO publicInfoDO : publicInfoDOList) {
                    if (categoryIdList.size() > 0) {
                        for (PubPublicInfoCategory category : categoryList) {
                            if (StringUtils.isNotEmpty(category.getName())) {
                                if (category.getId().equals(publicInfoDO.getCategoryId())
                                    && category.getId().intValue() == publicInfoDO.getCategoryId().intValue()) {
                                    publicInfoDO.setCategoryName(category.getName());
                                }
                            }
                        }
                    }
                    if (pubPublicInfoReadList.size() > 0) {
                        for (PubPublicInfoRead pubPublicInfoRead : pubPublicInfoReadList) {
                            if (pubPublicInfoRead.getInfoId().equals(publicInfoDO.getId())) {
                                publicInfoDO.setIsRead(pubPublicInfoRead.getIsRead());
                            }
                        }
                    }

                }

            }
        }
        page.setRecords(publicInfoDOList);
        page.setSize(oldPage.getSize());
        page.setTotal(oldPage.getTotal());
        page.setCurrent(oldPage.getCurrent());
        return page;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FixedFormProcessLog> getAllPinsInfo(String pId) throws Exception {
        Map<String, Object> map = actFixedFormService.getLogInfoByPins(pId, "月报");
        List<FixedFormProcessLog> logList = null;
        if (null != map && !map.isEmpty()) {
            List<AutUser> assigneeList = new ArrayList<AutUser>();
            List<AutUser> recevieList = new ArrayList<AutUser>();
            if (null != map.get("assigneeIdList")) {
                List<Long> assigneeIdList = (List<Long>)map.get("assigneeIdList");
                if (null != assigneeIdList && !assigneeIdList.isEmpty()) {
                    Where<AutUser> assigneeWhere = new Where<AutUser>();
                    assigneeWhere.in("id", assigneeIdList);
                    assigneeWhere.setSqlSelect("id,user_name,full_name");
                    assigneeList = autUserService.selectList(assigneeWhere);
                }
            }
            if (null != map.get("recevieUserIdList")) {
                List<Long> recevieUserIdList = (List<Long>)map.get("recevieUserIdList");
                if (null != recevieUserIdList && !recevieUserIdList.isEmpty()) {
                    Where<AutUser> recevieWhere = new Where<AutUser>();
                    recevieWhere.in("id", recevieUserIdList);
                    recevieWhere.setSqlSelect("id,user_name,full_name");
                    recevieList = autUserService.selectList(recevieWhere);
                }
            }

            if (null != map.get("logList")) {
                logList = (List<FixedFormProcessLog>)map.get("logList");
                if (null != logList && !logList.isEmpty() && null != assigneeList && !assigneeList.isEmpty()) {
                    for (FixedFormProcessLog log : logList) {
                        for (AutUser user : assigneeList) {
                            if (null != user && null != user.getId() && null != log && null != log.getOperationId()) {
                                if (String.valueOf(user.getId()).equals(log.getOperationId())) {
                                    log.setRecevieUserName(user.getFullName());
                                }
                            }
                        }
                    }
                }
                if (null != logList && !logList.isEmpty() && null != recevieList && !recevieList.isEmpty()) {
                    for (FixedFormProcessLog log : logList) {
                        for (AutUser user : recevieList) {
                            if (null != user && null != user.getId() && null != log && null != log.getOperationId()) {
                                if (String.valueOf(user.getId()).equals(log.getOperationId())) {
                                    log.setOperationName(user.getFullName());
                                }
                            }
                        }
                    }
                }
            }
        }
        return logList;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public RetMsg checkAppNextTaskInfo(String taskId) throws Exception {
        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        List<TaskDefinition> list = activitiService.getNextTaskInfo(processInstanceId, false);
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != list && !list.isEmpty()) {
            String assigneeId = "";
            String assigneeUserId = "";
            String assigneeGroupId = "";
            TaskDefinition definition = list.get(0);

            if (null != definition) {
                if (definition.getAssigneeExpression() == null
                    && definition.getCandidateGroupIdExpressions().size() == 0
                    && definition.getCandidateUserIdExpressions().size() == 0) {
                    // 选项为空时弹出整棵组织机构树
                    AutDepartmentUserVO autDepartmentUser = new AutDepartmentUserVO();
                    AutOrganVO organVO = autDepartmentUserService.getOrganList(autDepartmentUser);
                    map.put("organ", organVO);
                }
                if (null != definition.getAssigneeExpression()) {
                    assigneeId = String.valueOf(definition.getAssigneeExpression());
                    // 受理人
                    List<String> assineedIdList = new ArrayList<String>();
                    if (assigneeId.endsWith(";")) {
                        assineedIdList = Arrays.asList(assigneeId.split(";"));
                    } else {
                        assineedIdList.add(assigneeId);
                    }
                    Where<AutUser> userWhere = new Where<AutUser>();
                    userWhere.in("id", assineedIdList);
                    userWhere.setSqlSelect("id,user_name,full_name");
                    List<AutUser> assigneedList = autUserService.selectList(userWhere);
                    // 受理人不为空-就只返回受理人
                    if (null != assigneedList && !assigneedList.isEmpty()) {
                        map.put("user", assigneedList);
                        map.put("isassigneed", true);
                    }
                } else {
                    List<String> uIds = new ArrayList<String>(); // 所有候选用户id
                    List<AutUser> uList = new ArrayList<AutUser>(); // 所有候选用户

                    // 候选用户列表assineedUserIdList
                    if (null != definition.getCandidateUserIdExpressions()) {
                        Iterator<Expression> it = definition.getCandidateUserIdExpressions().iterator();
                        while (it.hasNext()) {
                            assigneeUserId += String.valueOf(it.next()) + ";";
                        }
                    }
                    List<String> assineedUserIdList = new ArrayList<String>();
                    if (assigneeUserId.endsWith(";")) {
                        assineedUserIdList = Arrays.asList(assigneeUserId.split(";"));
                    } else {
                        assineedUserIdList.add(assigneeUserId);
                    }
                    Where<AutUser> assigneedUserWhere = new Where<AutUser>();
                    assigneedUserWhere.in("id", assineedUserIdList);
                    assigneedUserWhere.setSqlSelect("id,user_name,full_name");
                    uIds.addAll(assineedUserIdList);

                    // 候选组
                    if (null != definition.getCandidateGroupIdExpressions()) {
                        Iterator<Expression> it = definition.getCandidateGroupIdExpressions().iterator();
                        while (it.hasNext()) {
                            assigneeGroupId += String.valueOf(it.next()) + ";";
                        }
                    }
                    List<String> assineedGroupIdList = new ArrayList<String>();
                    if (assigneeGroupId.endsWith(";")) {
                        assineedGroupIdList = Arrays.asList(assigneeGroupId.split(";"));
                    } else {
                        assineedGroupIdList.add(assigneeGroupId);
                    }

                    // 候选岗位
                    // List<Long> uIdList = new ArrayList<Long>();
                    Where<AutUserPosition> positionWhere = new Where<AutUserPosition>();
                    positionWhere.in("position_id", assineedGroupIdList);
                    positionWhere.setSqlSelect("id,position_id,user_id");
                    List<AutUserPosition> positionList = autUserPositionService.selectList(positionWhere);
                    if (null != positionList && !positionList.isEmpty()) {
                        for (AutUserPosition userPosition : positionList) {
                            if (null != userPosition && null != userPosition.getUserId()) {
                                uIds.add(String.valueOf(userPosition.getUserId()));
                            }
                        }
                    }

                    // 候选岗位
                    Where<AutUserRole> roleWhere = new Where<AutUserRole>();
                    roleWhere.in("role_id", assineedGroupIdList);
                    roleWhere.setSqlSelect("id,role_id,user_id");
                    List<AutUserRole> roleList = autUserRoleService.selectList(roleWhere);

                    if (null != roleList && !roleList.isEmpty()) {
                        for (AutUserRole userRole : roleList) {
                            if (null != userRole && null != userRole.getUserId()) {
                                uIds.add(String.valueOf(userRole.getUserId()));
                            }
                        }
                    }
                    if (null != uIds && !uIds.isEmpty()) {
                        Where<AutUser> where = new Where<AutUser>();
                        where.in("id", uIds);
                        where.setSqlSelect("id,user_name,full_name");
                        uList = autUserService.selectList(where);
                    }
                    map.put("user", uList);
                    // 选择部门
                    Where<AutDepartmentUser> departmentWhere = new Where<AutDepartmentUser>();
                    departmentWhere.in("dept_id", assineedGroupIdList);
                    departmentWhere.setSqlSelect("dept_id,id,dept_code,user_id");
                    List<AutDepartmentUser> departmentList = autDepartmentUserService.selectList(departmentWhere);

                    Where<AutDepartment> deptWhere = new Where<AutDepartment>();
                    deptWhere.in("id", assineedGroupIdList);
                    deptWhere.setSqlSelect("id,dept_code");
                    List<AutDepartment> departList = autDepartmentService.selectList(deptWhere);

                    String deptIds = "";
                    String userIds = "";

                    for (AutDepartment department : departList) {
                        deptIds += department.getId() + ";";
                    }

                    Where<AutUser> userWhere = new Where<AutUser>();
                    userWhere.in("id", assineedUserIdList);
                    userWhere.setSqlSelect("id");
                    userWhere.eq("is_active", 1);
                    List<AutUser> userList = autUserService.selectList(userWhere);

                    for (AutUser user : userList) {
                        userIds += user.getId() + ";";
                    }

                    List<AutDepartmentUserVO> departmentUserList = new ArrayList<AutDepartmentUserVO>();
                    if (null != departmentList && !departmentList.isEmpty()) {

                        List<String> deptIdlist = new ArrayList<String>();
                        List<Long> deptUIdList = new ArrayList<Long>();
                        List<AutUser> deptUserList = new ArrayList<AutUser>();
                        for (AutDepartmentUser department : departmentList) {
                            if (null != department && null != department.getUserId()) {
                                deptIdlist.add(String.valueOf(department.getId()));
                                deptUIdList.add(department.getUserId());
                                uIds.add(String.valueOf(department.getUserId()));
                            }
                        }
                        if (null != deptUIdList && !deptUIdList.isEmpty()) {
                            Where<AutUser> where = new Where<AutUser>();
                            where.in("id", deptUIdList);
                            where.setSqlSelect("id,user_name,full_name");
                            deptUserList = autUserService.selectList(where);
                            uList.addAll(deptUserList);
                        }
                        List<AutDepartmentUser> deptUser = new ArrayList<AutDepartmentUser>();
                        if (null != uIds && !uIds.isEmpty()) {
                            Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
                            where.setSqlSelect("dept_id,id,dept_code,user_id");
                            where.in("user_id", uIds);
                            deptUser = autDepartmentUserService.selectList(where);
                        }
                        if (null != deptUser && !deptUser.isEmpty()) {
                            for (AutDepartmentUser departmentUser : deptUser) {
                                AutDepartmentUserVO departmentUserVO = new AutDepartmentUserVO();
                                BeanUtils.copyProperties(departmentUser, departmentUserVO);
                                departmentUserList.add(departmentUserVO);
                            }
                        }
                        if (null != deptUserList && !deptUserList.isEmpty()) {
                            for (AutUser user : uList) {
                                for (AutDepartmentUser departmentUser : departmentList) {
                                    if (null != user && null != departmentUser && null != user.getId()
                                        && null != departmentUser.getUserId()) {
                                        if (user.getId().equals(departmentUser.getUserId())
                                            && user.getId().intValue() == departmentUser.getUserId().intValue()) {
                                            AutDepartmentUserVO departmentUserVO = new AutDepartmentUserVO();
                                            BeanUtils.copyProperties(departmentUser, departmentUserVO);
                                            departmentUserVO.setFullName(user.getFullName());
                                            departmentUserList.add(departmentUserVO);
                                        }
                                    }
                                }
                            }
                        }
                        if (null != departmentUserList && !departmentUserList.isEmpty()) {
                            for (int i = 0; i <= departmentUserList.size() - 1; i++) {
                                for (int j = departmentUserList.size() - 1; j > i; j--) {
                                    AutDepartmentUserVO autDepartmentUserVO = departmentUserList.get(i);
                                    AutDepartmentUserVO autDepartmentUserVO2 = departmentUserList.get(j);
                                    if (null != autDepartmentUserVO && null != autDepartmentUserVO2
                                        && null != autDepartmentUserVO.getUserId()
                                        && null != autDepartmentUserVO2.getUserId()) {
                                        if (autDepartmentUserVO.getUserId().equals(autDepartmentUserVO2.getUserId())
                                            && autDepartmentUserVO.getUserId().intValue() == autDepartmentUserVO2
                                                .getUserId().intValue()) {
                                            departmentUserList.remove(i);
                                        }
                                    }
                                }
                            }
                        }

                        AutDepartmentUserVO autDepartmentUser = new AutDepartmentUserVO();
                        autDepartmentUser.setDepartmentIds(deptIds);
                        autDepartmentUser.setUserIds(userIds);
                        AutOrganVO organVO = autDepartmentUserService.getOrganList(autDepartmentUser);
                        map.put("organ", organVO);
                        map.remove("user");
                    }
                }
            }
        }
        List<CustomerTaskDefinition> definitionList = getAppPreTaskInfo(taskId);
        map.put("preTaskInfo", definitionList);
        retMsg = actFixedFormService.getNextTaskInfo(taskId);
        List<CustomerTaskDefinition> nextTaskList = (List<CustomerTaskDefinition>)retMsg.getObject();
        map.put("nextTask", nextTaskList);
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setObject(map);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @SuppressWarnings("unchecked")
    public List<CustomerTaskDefinition> getAppPreTaskInfo(String taskId) throws Exception {
        RetMsg retMsg = actFixedFormService.getPreTaskInfo2(taskId);
        List<CustomerTaskDefinition> defitionList = new ArrayList<CustomerTaskDefinition>();
        if (retMsg.getCode() == 0) {
            defitionList = (List<CustomerTaskDefinition>)retMsg.getObject();
            List<String> asignee = new ArrayList<String>();
            if (!defitionList.isEmpty()) {
                for (CustomerTaskDefinition customerTaskDefinition : defitionList) {
                    List<String> taskAssignee = Arrays.asList(customerTaskDefinition.getAssignee().split(";"));
                    asignee.addAll(taskAssignee);
                }
            }
            if (!asignee.isEmpty()) {
                Where<AutUser> userWhere = new Where<AutUser>();
                userWhere.setSqlSelect("id,user_name,full_name");
                if (asignee.size() > 1) {
                    userWhere.in("id", asignee);
                } else {
                    userWhere.eq("id", asignee.get(0));
                }
                List<AutUser> userList = autUserService.selectList(userWhere);
                for (CustomerTaskDefinition customerTaskDefinition : defitionList) {
                    for (AutUser autUser : userList) {
                        if (customerTaskDefinition.getAssignee().equals(String.valueOf(autUser.getId()))) {
                            customerTaskDefinition.setAssigneeName(autUser.getFullName());
                        }
                    }
                }
            }
        }
        return defitionList;
    }

}
