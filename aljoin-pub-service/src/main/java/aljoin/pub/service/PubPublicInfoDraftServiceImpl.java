package aljoin.pub.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnRunService;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.pub.dao.entity.PubPublicInfoCategory;
import aljoin.pub.dao.entity.PubPublicInfoDraft;
import aljoin.pub.dao.mapper.PubPublicInfoDraftMapper;
import aljoin.pub.dao.object.PubPublicInfoDO;
import aljoin.pub.dao.object.PubPublicInfoDraftVO;
import aljoin.pub.iservice.PubPublicInfoCategoryService;
import aljoin.pub.iservice.PubPublicInfoDraftService;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;

/**
 * 
 * 公共信息草稿表(服务实现类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-12
 */
@Service
public class PubPublicInfoDraftServiceImpl extends ServiceImpl<PubPublicInfoDraftMapper, PubPublicInfoDraft>
    implements PubPublicInfoDraftService {

    @Resource
    private PubPublicInfoDraftMapper mapper;
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
    private ActAljoinBpmnRunService actAljoinBpmnRunService;

    @Override
    public Page<PubPublicInfoDO> list(PageBean pageBean, PubPublicInfoDraftVO obj) throws Exception {
        Where<PubPublicInfoDraft> where = new Where<PubPublicInfoDraft>();
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

            if (null != obj.getPeriodStatus()) {
                where.eq("period_status", obj.getPeriodStatus());
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                where.between("create_time", DateUtil.str2datetime(obj.getBegDate() + " 00:00:00"),
                    DateUtil.str2date(obj.getEndDate() + " 23:59:59"));
            }
            if (StringUtils.isNotEmpty(obj.getBegDate()) && StringUtils.isEmpty(obj.getEndDate())) {
                where.ge("create_time", DateUtil.str2datetime(obj.getBegDate() + " 00:00:00"));
            }
            if (StringUtils.isEmpty(obj.getBegDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
                where.le("create_time", DateUtil.str2datetime(obj.getEndDate() + " 23:59:59"));
            }
            if (StringUtils.isNotEmpty(obj.getSearchKey())) {
                String searchKey = obj.getSearchKey();
                where.andNew(" title like {0} or publish_name like {1}", "%" + searchKey + "%", "%" + searchKey + "%");
            }
        }
        where.andNew("create_user_id = {0}", obj.getCreateUserId());
        where.orderBy("create_time", false);
        where.setSqlSelect("id,title,publish_name,category_id,period,period_status,create_time");
        Page<PubPublicInfoDraft> oldPage
            = selectPage(new Page<PubPublicInfoDraft>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        Page<PubPublicInfoDO> page = new Page<PubPublicInfoDO>();
        List<PubPublicInfoDraft> pubPublicInfoList = oldPage.getRecords();
        List<PubPublicInfoDO> publicInfoDOList = new ArrayList<PubPublicInfoDO>();
        List<Long> categoryIdList = new ArrayList<Long>();
        if (null != pubPublicInfoList && !pubPublicInfoList.isEmpty()) {
            for (PubPublicInfoDraft pubPublicInfo : pubPublicInfoList) {
                if (null != pubPublicInfo) {
                    PubPublicInfoDO pubPublicInfoDO = new PubPublicInfoDO();
                    pubPublicInfoDO.setId(pubPublicInfo.getId());
                    pubPublicInfoDO.setCategoryId(pubPublicInfo.getCategoryId());
                    pubPublicInfoDO.setTitle(pubPublicInfo.getTitle());
                    DateTime dateTime = new DateTime(pubPublicInfo.getCreateTime());
                    pubPublicInfoDO.setCreateDate(dateTime.toString("yyyy-MM-dd"));
                    pubPublicInfoDO.setCreateUserId(pubPublicInfo.getCreateUserId());
                    String validate = "已失效";
                    if (null != pubPublicInfo.getPeriodStatus()) {
                        if (0 == pubPublicInfo.getPeriodStatus()) {
                            validate = "有效";
                        } else if (1 == pubPublicInfo.getPeriodStatus()) {
                            validate = "已失效";
                        }
                    }
                    pubPublicInfoDO.setValidate(validate);
                    pubPublicInfoDO.setPublishName(pubPublicInfo.getPublishName());
                    if (!categoryIdList.contains(pubPublicInfo.getCategoryId())) {
                        categoryIdList.add(pubPublicInfo.getCategoryId());
                    }
                    publicInfoDOList.add(pubPublicInfoDO);
                }
            }
            if (null != categoryIdList && !categoryIdList.isEmpty()) {
                Where<PubPublicInfoCategory> publicInfoCategoryWhere = new Where<PubPublicInfoCategory>();
                publicInfoCategoryWhere.setSqlSelect("id,name");
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
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(PubPublicInfoDraft obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    @Transactional
    public RetMsg add(PubPublicInfoDraftVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
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
            if (null == obj.getCategoryId()) {
                obj.setCategoryId(0L);
                retMsg.setCode(1);
                retMsg.setMessage("分类不能为空");
                return retMsg;
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
            if (StringUtils.isEmpty(obj.getNoticeObjId())) {
                obj.setNoticeObjId("");
            }
            if (StringUtils.isEmpty(obj.getNoticeObjName())) {
                obj.setNoticeObjName("");
            }
            if (StringUtils.isEmpty(obj.getContent())) {
                obj.setContent("");
                retMsg.setCode(1);
                retMsg.setMessage("内容不能为空");
                return retMsg;
            }
            PubPublicInfoDraft pubPublicInfo = new PubPublicInfoDraft();
            BeanUtils.copyProperties(obj, pubPublicInfo);
            if (null != pubPublicInfo) {
                DateTime begTime = new DateTime(new Date());
                pubPublicInfo.setPeriodBeginTime(begTime.toDate());
                if (null != obj.getPeriod()) {
                    DateTime endTime = begTime.plusDays(obj.getPeriod());
                    pubPublicInfo.setPeriodEndTime(endTime.toDate());
                }
                pubPublicInfo.setPeriodStatus(0);
                pubPublicInfo.setIsActive(1);
                insert(pubPublicInfo);
            }
            //保存附件
            List<ResResource> resResourceList = obj.getResResourceList();
            List<Long> ids = new ArrayList<Long>();
            if(null != resResourceList && !resResourceList.isEmpty()){
                for (ResResource resResource : resResourceList) {
                    ids.add(resResource.getId());
                }
                Where<ResResource> resResourceWhere = new Where<ResResource>();
                resResourceWhere.in("id", ids);
                resResourceList = resResourceService.selectList(resResourceWhere);
                for (ResResource resResource : resResourceList) {
                    resResource.setBizId(pubPublicInfo.getId());
                    resResource.setFileDesc("新增公共信息草稿附件上传");
                }
                resResourceService.updateBatchById(resResourceList);
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public PubPublicInfoDraftVO detail(PubPublicInfoDraft obj) throws Exception {
        PubPublicInfoDraftVO pubPublicInfoVO = null;
        if (null != obj && null != obj.getId()) {
            PubPublicInfoDraft pubPublicInfo = selectById(obj.getId());
            if (null != pubPublicInfo) {
                PubPublicInfoCategory pub = pubPublicInfoCategoryService.selectById(pubPublicInfo.getCategoryId());
                pubPublicInfoVO = new PubPublicInfoDraftVO();
                BeanUtils.copyProperties(pubPublicInfo, pubPublicInfoVO);
                if (pub != null) {
                    pubPublicInfoVO.setCategoryName(pub.getName());
                }
                /*Where<PubPublicInfoDraft> wh = new Where<PubPublicInfoDraft>();
                wh.eq("id", pubPublicInfoVO.getId());
                wh.setSqlSelect("id");
                PubPublicInfoDraft draft = pubPublicInfoDraftService.selectOne(pubPublicInfoVO);*/
                /*if (null != draft) {
                  PubPublicInfoCategory name = pubPublicInfoCategoryService.selectById(draft.getCategoryId());
                  pubPublicInfoVO.setCategoryName(name.getName());
                }*/
                Where<ResResource> where = new Where<ResResource>();
                where.eq("biz_id",pubPublicInfo.getId());
                List<ResResource> resourceList = resResourceService.selectList(where);
                if(null != resourceList && !resourceList.isEmpty()){
                    pubPublicInfoVO.setResResourceList(resourceList);
                }
            }
        }
        return pubPublicInfoVO;
    }

    @Override
    public RetMsg update(PubPublicInfoDraftVO obj) throws Exception {
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
            if (null == obj.getCategoryId()) {
                obj.setCategoryId(0L);
                retMsg.setCode(1);
                retMsg.setMessage("分类不能为空");
                return retMsg;
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
            PubPublicInfoDraft pubPublicInfo = selectById(obj.getId());
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
    public RetMsg delete(PubPublicInfoDraftVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj && null != obj.getIds()) {
            String ids = obj.getIds();
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
            if (null != idArr) {
                List<Long> idList = new ArrayList<Long>();
                Where<PubPublicInfoDraft> pubWhere = new Where<PubPublicInfoDraft>();
                pubWhere.in("id", idArr);
                List<PubPublicInfoDraft> pubPublicInfoList = selectList(pubWhere);
                if (null != pubPublicInfoList && !pubPublicInfoList.isEmpty()) {
                    for (PubPublicInfoDraft pubPublicInfo : pubPublicInfoList) {
                        if (null != pubPublicInfo) {
                            idList.add(pubPublicInfo.getId());
                        }
                    }
                }
                if (null != idList && !idList.isEmpty()) {
                    deleteBatchIds(idList);
                }
                // 删除附件(暂时不删除附件信息)
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

    @SuppressWarnings("unused")
    @Override
    public RetMsg addProcess(PubPublicInfoDraftVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        ActAljoinBpmn bpmn = null;
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
            if (null == obj.getCategoryId()) {
                obj.setCategoryId(0L);
                retMsg.setCode(1);
                retMsg.setMessage("分类不能为空");
                return retMsg;
            }
            if (null == obj.getPeriod()) {
                obj.setPeriod(0);
                retMsg.setCode(1);
                retMsg.setMessage("有效期不能为空");
                return retMsg;
            } else {
                if (obj.getPeriod() + "".length() > 10) {
                    retMsg.setCode(1);
                    retMsg.setMessage("有效期不能超过10位数");
                    return retMsg;
                }
            }
            PubPublicInfoDraft pubPublicInfo = selectById(obj.getId());
            if (null != pubPublicInfo) {
                if (StringUtils.isNotEmpty(obj.getTitle())) {
                    pubPublicInfo.setTitle(obj.getTitle());
                }
                if (null != obj.getCategoryId()) {
                    pubPublicInfo.setCategoryId(obj.getCategoryId());
                }
                if (null != obj.getPeriod()) {
                    pubPublicInfo.setPeriod(obj.getPeriod());
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
                Where<PubPublicInfoCategory> where = new Where<PubPublicInfoCategory>();
                where.eq("id", obj.getCategoryId());
                where.eq("is_use", 1);
                where.setSqlSelect("id,name,category_rank,process_id,process_name,is_use");
                PubPublicInfoCategory category = pubPublicInfoCategoryService.selectOne(where);
                if (null != category) {
                    Where<ActAljoinBpmn> bpmnWhere = new Where<ActAljoinBpmn>();
                    bpmnWhere.eq("process_id", category.getProcessId());
                    bpmnWhere.eq("is_deploy", 1);
                    bpmnWhere.eq("is_active", 1);
                    bpmnWhere.setSqlSelect("id,process_id,process_name");
                    bpmn = actAljoinBpmnService.selectOne(bpmnWhere);
                    if (null != bpmn) {
                        // 启动流程
                        if (null != pubPublicInfo.getCreateUserId()) {
                            Map<String, String> param = new HashMap<String, String>();
                            param.put("bizType", "PubPublicInfoDraft");
                            param.put("bizId", pubPublicInfo.getId() + "");
                            param.put("isUrgent", "1");
							
							// 如果此时流程版本有升级，在run表中插入一条新的记录
				            Where<ActAljoinBpmnRun> bpmnRunWhere = new Where<ActAljoinBpmnRun>();
				            bpmnRunWhere.eq("orgnl_id", bpmn.getId());
				            bpmnRunWhere.eq("version", bpmn.getVersion());
				            ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectOne(bpmnRunWhere);
				            if(null == bpmnRun){
				                bpmnRun = new ActAljoinBpmnRun();
				                BeanUtils.copyProperties(bpmn, bpmnRun);
				                bpmnRun.setId(null);
				                bpmnRun.setOrgnlId(bpmn.getId());
				                actAljoinBpmnRunService.insert(bpmnRun);
				            }
				            
							ProcessInstance instance = activitiService.startBpmn(bpmnRun, null, param,
									pubPublicInfo.getCreateUserId());
                        }
                    }
                }
                updateById(pubPublicInfo);
                //保存附件
                List<ResResource> resResourceList = obj.getResResourceList();
                List<Long> ids = new ArrayList<Long>();
                if(null != resResourceList && !resResourceList.isEmpty()){
                    for (ResResource resResource : resResourceList) {
                        ids.add(resResource.getId());
                    }
                    Where<ResResource> resResourceWhere = new Where<ResResource>();
                    resResourceWhere.in("id", ids);
                    resResourceList = resResourceService.selectList(resResourceWhere);
                    for (ResResource resResource : resResourceList) {
                        resResource.setBizId(pubPublicInfo.getId());
                        resResource.setFileDesc("新增公共信息草稿附件上传");
                    }
                    resResourceService.updateBatchById(resResourceList);
                }
            }
        }

        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }
}
