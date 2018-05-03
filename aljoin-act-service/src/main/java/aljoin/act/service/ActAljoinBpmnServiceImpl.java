package aljoin.act.service;

import aljoin.act.dao.entity.*;
import aljoin.act.dao.mapper.ActAljoinBpmnMapper;
import aljoin.act.dao.object.ActAljoinBpmnVO;
import aljoin.act.dao.object.ActSeetingExpressionVO;
import aljoin.act.iservice.*;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sys.iservice.SysDocumentNumberService;
import aljoin.sys.iservice.SysSerialNumberService;
import aljoin.util.FileUtil;
import aljoin.util.StringUtil;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 *
 * 自定义流程bpmn表(服务实现类).
 *
 * @author：zhongjy
 *
 * @date：2017年7月25日 下午4:01:32
 */
@Service
public class ActAljoinBpmnServiceImpl extends ServiceImpl<ActAljoinBpmnMapper, ActAljoinBpmn>
    implements ActAljoinBpmnService {
    private final static String PROCESS_BATCH_DOWN_NAME = WebConstant.PROCESS_BATCH_DOWNLOAD_NAME;
    @Resource
    private ActActivitiService activitiService;
    @Resource
    private ActAljoinBpmnFormService actAljoinBpmnFormService;
    @Resource
    private ActAljoinTaskAssigneeService actAljoinTaskAssigneeService;
    @Resource
    private ActAljoinFormService actAljoinFormService;
    @Resource
    private ActAljoinFormWidgetService actAljoinFormWidgetService;
    @Resource
    private ActAljoinFormAttributeService actAljoinFormAttributeService;
    @Resource
    private ActAljoinFormRunService actAljoinFormRunService;
    @Resource
    private ActAljoinFormWidgetRunService actAljoinFormWidgetRunService;
    @Resource
    private ActAljoinFormAttributeRunService actAljoinFormAttributeRunService;
    @Resource
    private ActAljoinCategoryService actAljoinCategoryService;
    @Resource
    private ActAljoinBpmnUserService actAljoinBpmnUserService;
    @Resource
    private ActAljoinBpmnService actAljoinBpmnService;
    @Resource
    private SysSerialNumberService sysSerialNumberService;
    @Resource
    private SysDocumentNumberService sysDocumentNumberService;
    @Override
    public Page<ActAljoinBpmnVO> list(PageBean pageBean, ActAljoinBpmnVO obj) throws Exception {
        Page<ActAljoinBpmnVO> page = new Page<ActAljoinBpmnVO>();
        List<ActAljoinBpmnVO> bpmnVOList = new ArrayList<ActAljoinBpmnVO>();
        Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
        if (StringUtils.isNotEmpty(obj.getProcessName())) {
            where.like("process_name", obj.getProcessName());
        }
        if (StringUtils.isNotEmpty(obj.getCategoryName())) {
            // where.eq("category_id", obj.getCategoryName());
            List<ActAljoinCategory> categorys =
                actAljoinCategoryService.getAllChildList(Long.valueOf(obj.getCategoryName()));
            if (categorys.size() == 0) {
                where.eq("category_id", obj.getCategoryName());
            } else {
                Set<Long> categoryIds = new HashSet<Long>();
                for (ActAljoinCategory category : categorys) {
                    categoryIds.add(category.getId());
                }
                where.in("category_id", categoryIds);
            }
        }
        where.orderBy("create_time", false);
        where.setSqlSelect(
            "id,process_id,category_id,process_name,is_deploy,is_deploy_after_edit,is_form_edit,last_deploy_time,is_active,is_free,is_fixed");
        Page<ActAljoinBpmn> bpmnPage =
            selectPage(new Page<ActAljoinBpmn>(pageBean.getPageNum(), pageBean.getPageSize()), where);

        // 优化，一次性取出所有分类
        List<Long> categoryIdList = new ArrayList<Long>();
        for (ActAljoinBpmn actAljoinBpmn : bpmnPage.getRecords()) {
            categoryIdList.add(actAljoinBpmn.getCategoryId());
        }
        Where<ActAljoinCategory> actAljoinCategoryWhere = new Where<ActAljoinCategory>();
        actAljoinCategoryWhere.in("id", categoryIdList);
        actAljoinCategoryWhere.setSqlSelect("id,category_name");
        List<ActAljoinCategory> actAljoinCategoryList = actAljoinCategoryService.selectList(actAljoinCategoryWhere);
        Map<Long, String> map = new HashMap<Long, String>();
        for (ActAljoinCategory actAljoinCategory : actAljoinCategoryList) {
            map.put(actAljoinCategory.getId(), actAljoinCategory.getCategoryName());
        }
        for (ActAljoinBpmn actAljoinBpmn : bpmnPage.getRecords()) {
            ActAljoinBpmnVO actAljoinBpmnVO = new ActAljoinBpmnVO();
            actAljoinBpmnVO.setId(actAljoinBpmn.getId().toString());
            actAljoinBpmnVO.setProcessId(actAljoinBpmn.getProcessId());
            actAljoinBpmnVO.setIsActive(actAljoinBpmn.getIsActive());
            actAljoinBpmnVO.setProcessName(actAljoinBpmn.getProcessName());
            actAljoinBpmnVO.setCategoryName(map.get(actAljoinBpmn.getCategoryId()));
            actAljoinBpmnVO.setIsDeployAfterEdit(actAljoinBpmn.getIsDeployAfterEdit());
            actAljoinBpmnVO.setIsDeploy(actAljoinBpmn.getIsDeploy());
            actAljoinBpmnVO.setIsFormEdit(actAljoinBpmn.getIsFormEdit());
            actAljoinBpmnVO.setLastDeployTime(actAljoinBpmn.getLastDeployTime());
            actAljoinBpmnVO.setIsFixed(actAljoinBpmn.getIsFixed());
            actAljoinBpmnVO.setIsFree(actAljoinBpmn.getIsFree());
            bpmnVOList.add(actAljoinBpmnVO);
        }
        page.setCurrent(bpmnPage.getCurrent());
        page.setRecords(bpmnVOList);
        page.setSize(bpmnPage.getSize());
        page.setTotal(bpmnPage.getTotal());
        return page;
    }

    @Override
    @Transactional
    public void deploy(Long id, Long userId, String userName) throws Exception {
        // 部署
        ActAljoinBpmn actAljoinBpmn = selectById(id);
        activitiService.deployBpmn(actAljoinBpmn);
        // 修改部署状态
        actAljoinBpmn.setIsDeploy(1);
        actAljoinBpmn.setIsDeployAfterEdit(1);
        actAljoinBpmn.setLastDeployTime(new Date());
        actAljoinBpmn.setLastDeployUserId(userId);
        actAljoinBpmn.setLastDeployUserName(userName);

        updateById(actAljoinBpmn);
    }

    @Override
    @Transactional
    public RetMsg saveXmlCode(Map<String, String> param) throws Exception {

        String xmlCode = param.get("xmlCode");
        String processCategory = param.get("processCategory");
        String processId = param.get("processId");
        String processName = param.get("processName");
        String processDesc = param.get("processDesc");
        String formIds = param.get("formIds");
        String elementTypes = param.get("elementTypes");
        String elementIds = param.get("elementIds");
        String taskIds = param.get("taskIds");
        String assigneeDepartmentIds = param.get("assigneeDepartmentIds");
        String assigneePositionIds = param.get("assigneePositionIds");
        String assigneeUserIds = param.get("assigneeUserIds");
        String assigneeCandidateIds = param.get("assigneeCandidateIds");
        String assigneeGroupIds = param.get("assigneeGroupIds");
        String backupHtml = param.get("backupHtml");
        String showWidgetIds = param.get("showWidgetIds");
        String editWidgetIds = param.get("editWidgetIds");
        String notNullWidgetIds = param.get("notNullWidgetIds");
        String aljoinTaskOperateAuthIds = param.get("aljoinTaskOperateAuthIds");
        String commentWidgetIds = param.get("commentWidgetIds");
        String aljoinProcessIsfixed = param.get("aljoinProcessIsfixed");
        String aljoinProcessIsfree = param.get("aljoinProcessIsfree");
        String redHeadWidgetIds = param.get("redHeadWidgetIds");
        String saveMarkWidgetIds = param.get("saveMarkWidgetIds");
        String isReturnCreaters = param.get("isReturnCreaters");
        String signCommentWidgetIds = param.get("signCommentWidgetIds");
        String staffMembersDepartments = param.get("staffMembersDepartments");
        String lastlinkDepartments = param.get("lastlinkDepartments");
        String createPersonsJobs = param.get("createPersonsJobs");
        RetMsg retMsg = new RetMsg();
        // 首先根据流程ID或流程名称判断流程是否已经
        Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
        where.eq("process_id", processId);
        // where.or("process_name", processName);
        where.or("process_name = {0}", processName);
        if (selectCount(where) > 0) {
            retMsg.setCode(1);
            retMsg.setMessage("流程ID或名称已经存在");
        } else {
            // 插入任务授权信息表(构造数据)
            String[] taskIdsArr = taskIds.split(";");
            String[] assigneeDepartmentIdsArr = assigneeDepartmentIds.split(";");
            String[] assigneePositionIdsArr = assigneePositionIds.split(";");
            String[] assigneeUserIdsArr = assigneeUserIds.split(";");
            String[] assigneeCandidateIdsArr = assigneeCandidateIds.split(";");
            String[] assigneeGroupIdsArr = assigneeGroupIds.split(";");
            String[] showWidgetIdsArr = showWidgetIds.split(";");
            String[] editWidgetIdsArr = editWidgetIds.split(";");
            String[] notNullWidgetIdsArr = notNullWidgetIds.split(";");
            String[] commentWidgetIdsArr = commentWidgetIds.split(";");
            String[] aljoinTaskOperateAuthIdsArr = aljoinTaskOperateAuthIds.split(";");
            String[] redHeadWidgetIdsArr = redHeadWidgetIds.split(";");
            String[] saveMarkWidgetIdsArr = saveMarkWidgetIds.split(";");
            String[] isReturnCreatersArr = isReturnCreaters.split(";");
            String[] signCommentWidgetIdsArr  = signCommentWidgetIds.split(";");
            String[] staffMembersDepartmentsArr = staffMembersDepartments.split(";");
            String[] lastlinkDepartmentsArr = lastlinkDepartments.split(";");
            String[] createPersonsJobsArr = createPersonsJobs.split(";");
            List<ActAljoinTaskAssignee> actAljoinTaskAssigneeList = new ArrayList<ActAljoinTaskAssignee>();
            for (int i = 0; i < taskIdsArr.length; i++) {
                ActAljoinTaskAssignee actAljoinTaskAssignee = new ActAljoinTaskAssignee();
                // actAljoinTaskAssignee.setBpmnId(bpmn.getId());
                actAljoinTaskAssignee.setProcessId(processId);
                actAljoinTaskAssignee.setTaskId(taskIdsArr[i]);
                actAljoinTaskAssignee.setAssigneeDepartmentIds(assigneeDepartmentIdsArr[i]);
                actAljoinTaskAssignee.setAssigneePositionIds(assigneePositionIdsArr[i]);
                actAljoinTaskAssignee.setAssigneeUserIds(assigneeUserIdsArr[i]);
                actAljoinTaskAssignee.setAssigneeCandidateIds(assigneeCandidateIdsArr[i]);
                actAljoinTaskAssignee.setAssigneeGroupIds(assigneeGroupIdsArr[i]);
                actAljoinTaskAssignee.setIsActive(1);
                actAljoinTaskAssignee.setShowWidgetIds(showWidgetIdsArr[i]);
                actAljoinTaskAssignee.setEditWidgetIds(editWidgetIdsArr[i]);
                actAljoinTaskAssignee.setNotNullWidgetIds(notNullWidgetIdsArr[i]);
                actAljoinTaskAssignee.setCommentWidgetIds(commentWidgetIdsArr[i]);
                actAljoinTaskAssignee.setOperateAuthIds(aljoinTaskOperateAuthIdsArr[i]);
                actAljoinTaskAssignee.setRedHeadWidgetIds(redHeadWidgetIdsArr[i]);
                actAljoinTaskAssignee.setSaveMarkWidgetIds(saveMarkWidgetIdsArr[i]);
                if (null != isReturnCreatersArr[i] && !(isReturnCreatersArr[i].length() == 0)) {
                    actAljoinTaskAssignee.setIsReturnCreater(Integer.parseInt(isReturnCreatersArr[i]));
                }
                actAljoinTaskAssignee.setSignCommentWidgetIds(signCommentWidgetIdsArr[i]);
                if (null != staffMembersDepartmentsArr[i] && !(staffMembersDepartmentsArr[i].length() == 0)) {
                    actAljoinTaskAssignee.setStaffMembersDepartment(Integer.parseInt(staffMembersDepartmentsArr[i]));
                }
                if (null != lastlinkDepartmentsArr[i] && !(lastlinkDepartmentsArr[i].length() == 0)) {
                    actAljoinTaskAssignee.setLastlinkDepartment(Integer.parseInt(lastlinkDepartmentsArr[i]));
                }
                if (null != createPersonsJobsArr[i] && !(createPersonsJobsArr[i].length() == 0)) {
                    actAljoinTaskAssignee.setCreatePersonsJob(Integer.parseInt(createPersonsJobsArr[i]));
                }
                actAljoinTaskAssigneeList.add(actAljoinTaskAssignee);

                // 修改流程xml文件的：activiti:assignee，activiti:candidateUsers，activiti:candidateGroups
                // 以下通过字符串进行替换
                String assignee = "null".equals(assigneeUserIdsArr[i]) ? "" : assigneeUserIdsArr[i];
                String candidateUsers = "null".equals(assigneeCandidateIdsArr[i]) ? "" : assigneeCandidateIdsArr[i];
                String candidateGroups = "";
                // 部门
                if (!"null".equals(assigneeDepartmentIdsArr[i])) {
                    candidateGroups = assigneeDepartmentIdsArr[i];
                }
                // 岗位
                if (!"null".equals(assigneePositionIdsArr[i])) {
                    if ("".equals(candidateGroups)) {
                        candidateGroups = assigneePositionIdsArr[i];
                    } else {
                        candidateGroups += "," + assigneePositionIdsArr[i];
                    }
                }
                // 角色
                if (!"null".equals(assigneeGroupIdsArr[i])) {
                    if ("".equals(candidateGroups)) {
                        candidateGroups = assigneeGroupIdsArr[i];
                    } else {
                        candidateGroups += "," + assigneeGroupIdsArr[i];
                    }
                }

                String oldChar = "id=\"" + taskIdsArr[i] + "\"";
                String newChar = oldChar + " activiti:assignee=\"" + assignee + "\" activiti:candidateUsers=\""
                    + candidateUsers + "\" activiti:candidateGroups=\"" + candidateGroups + "\"";
                xmlCode = xmlCode.replace(oldChar, newChar);
            }
            // 把camunda:替换成activiti:
            xmlCode = xmlCode.replaceAll("camunda:", "activiti:");

            // <bpmn2:multiInstanceLoopCharacteristics isSequential="true" />---------默认情况（串行）
            while (xmlCode.contains("<bpmn2:multiInstanceLoopCharacteristics isSequential=\"true\" />")) {
                String randomStr = "ASSIGNEE_C_" + StringUtil.randomString(10);
                String isSequentialFullMultiInstance =
                    "<bpmn2:multiInstanceLoopCharacteristics isSequential=\"true\" activiti:collection=\"\\$\\{"
                        + randomStr + "\\}\">\n";
                isSequentialFullMultiInstance +=
                    "  <bpmn2:completionCondition xsi:type=\"bpmn2:tFormalExpression\">\\$\\{nrOfCompletedInstances/nrOfInstances == 1\\}</bpmn2:completionCondition>\n";
                isSequentialFullMultiInstance += "</bpmn2:multiInstanceLoopCharacteristics>\n";
                xmlCode = xmlCode.replaceFirst("<bpmn2:multiInstanceLoopCharacteristics isSequential=\"true\" />",
                    isSequentialFullMultiInstance);
            }

            // <bpmn2:multiInstanceLoopCharacteristics />---------默认情况（并行）
            while (xmlCode.contains("<bpmn2:multiInstanceLoopCharacteristics />")) {
                String randomStr = "ASSIGNEE_B_" + StringUtil.randomString(10);
                String unSequentialFullMultiInstance =
                    "<bpmn2:multiInstanceLoopCharacteristics activiti:collection=\"\\$\\{" + randomStr + "\\}\">\n";
                unSequentialFullMultiInstance +=
                    "  <bpmn2:completionCondition xsi:type=\"bpmn2:tFormalExpression\">\\$\\{nrOfCompletedInstances/nrOfInstances == 1\\}</bpmn2:completionCondition>\n";
                unSequentialFullMultiInstance += "</bpmn2:multiInstanceLoopCharacteristics>\n";
                xmlCode =
                    xmlCode.replaceFirst("<bpmn2:multiInstanceLoopCharacteristics />", unSequentialFullMultiInstance);
            }

            // 插入流程表
            ActAljoinBpmn bpmn = new ActAljoinBpmn();
            bpmn.setXmlCode(xmlCode);
            bpmn.setCategoryId(Long.parseLong(processCategory));
            bpmn.setIsActive(1);
            bpmn.setProcessId(processId);
            bpmn.setProcessName(processName);
            bpmn.setProcessDesc(processDesc);
            bpmn.setHtmlCode(backupHtml);
            bpmn.setHasForm("".equals(formIds) ? 0 : 1);
            bpmn.setIsFixed(Integer.parseInt(aljoinProcessIsfixed));
            bpmn.setIsFree(Integer.parseInt(aljoinProcessIsfree));
            bpmn.setIsDeploy(0);
            bpmn.setIsDeployAfterEdit(0);
            bpmn.setIsFormEdit(0);
            insert(bpmn);
            
            //判断表中是否有流水号
            List<String> serialNumIds = actAljoinFormService.validateSerialNum(formIds);
            if (null != serialNumIds && serialNumIds.size() > 0) {
              //流水号绑定流程
               sysSerialNumberService.bindBpmn(bpmn.getId().toString(),serialNumIds);
            } else {
              //判断表中是否有文号
               List<String> documentNumIds = actAljoinFormService.validateDocumentNum(formIds);
               if (null != documentNumIds && documentNumIds.size() > 0) {
                   //文号绑定流程
                   sysDocumentNumberService.bindBpmn(bpmn.getId().toString(),documentNumIds);
               }
            }
            // 插入流程-表单(运行时)关系

            if (!"".equals(formIds)) {
                String[] formIdsArr = formIds.split(",");
                String[] elementTypesArr = elementTypes.split(",");
                String[] elementIdsArr = elementIds.split(",");
                List<ActAljoinBpmnForm> actAljoinBpmnFormList = new ArrayList<ActAljoinBpmnForm>();
                // 插入运行时表单以及控件数据以及把运行时的数据转移到历史表

                for (int i = 0; i < formIdsArr.length; i++) {

                    Long formId = Long.parseLong(formIdsArr[i]);// 这个ID是源表（表单表的主键ID）

                    ActAljoinForm actAljoinForm = actAljoinFormService.selectById(formId);
                    Where<ActAljoinFormRun> actAljoinFormRunWhere = new Where<ActAljoinFormRun>();
                    actAljoinFormRunWhere.eq("orgnl_id", formId);
                    actAljoinFormRunWhere.eq("version", actAljoinForm.getVersion());
                    ActAljoinFormRun actAljoinFormRun = actAljoinFormRunService.selectOne(actAljoinFormRunWhere);

                    Long runFormId = IdWorker.getId();

                    ActAljoinBpmnForm actAljoinBpmnForm = new ActAljoinBpmnForm();
                    actAljoinBpmnForm.setBpmnId(bpmn.getId());
                    actAljoinBpmnForm.setFormId(actAljoinFormRun == null ? runFormId : actAljoinFormRun.getId());// 这里的ID应该是运行时表的表单ID（其中里面的orgnl_Id对应源表的ID）
                    actAljoinBpmnForm.setIsActive(1);
                    actAljoinBpmnForm.setElementType(elementTypesArr[i]);
                    actAljoinBpmnForm.setElementId(elementIdsArr[i]);
                    actAljoinBpmnForm.setProcessId(processId);
                    actAljoinBpmnFormList.add(actAljoinBpmnForm);
                    // 如果该表单没有进入运行时（即之前的流程没有管理到该表单）需要插入运行时表，如果已经在运行时有表单数据，则需要判断版本号，
                    // 如果版本号和源数据一致，则该表单不需要插入，否则要把运行时的数据转移到历史表，在把源表的数据更新的运行时表
                    // 源-运行时-历史表，这三表的数据是进行完整copy,包含公共字段，所有查询的时候可以直接根据主键ID对运行时进行查找
                    if (actAljoinFormRun == null) {
                        actAljoinFormRun = new ActAljoinFormRun();
                        BeanUtils.copyProperties(actAljoinForm, actAljoinFormRun);
                        actAljoinFormRun.setOrgnlId(actAljoinFormRun.getId());
                        actAljoinFormRun.setId(runFormId);
                        actAljoinFormRunService.copyObject(actAljoinFormRun);
                        // 查询表单下控件并插入
                        Where<ActAljoinFormWidget> actAljoinFormWidgetWhere = new Where<ActAljoinFormWidget>();
                        actAljoinFormWidgetWhere.eq("form_Id", formId);
                        List<ActAljoinFormWidget> actAljoinFormWidgetList =
                            actAljoinFormWidgetService.selectList(actAljoinFormWidgetWhere);
                        for (ActAljoinFormWidget actAljoinFormWidget : actAljoinFormWidgetList) {
                            ActAljoinFormWidgetRun actAljoinFormWidgetRun = new ActAljoinFormWidgetRun();
                            BeanUtils.copyProperties(actAljoinFormWidget, actAljoinFormWidgetRun);
                            actAljoinFormWidgetRunService.copyObject(actAljoinFormWidgetRun);
                            // 查询控件属性并插入
                            Where<ActAljoinFormAttribute> actAljoinFormAttributeWhere =
                                new Where<ActAljoinFormAttribute>();
                            actAljoinFormAttributeWhere.eq("widget_id", actAljoinFormWidgetRun.getId());
                            List<ActAljoinFormAttribute> actAljoinFormAttributeList =
                                actAljoinFormAttributeService.selectList(actAljoinFormAttributeWhere);
                            for (ActAljoinFormAttribute actAljoinFormAttribute : actAljoinFormAttributeList) {
                                ActAljoinFormAttributeRun actAljoinFormAttributeRun = new ActAljoinFormAttributeRun();
                                BeanUtils.copyProperties(actAljoinFormAttribute, actAljoinFormAttributeRun);
                                actAljoinFormAttributeRunService.copyObject(actAljoinFormAttributeRun);
                            }
                        }
                    }
                }
                actAljoinBpmnFormService.insertBatch(actAljoinBpmnFormList);
            }
            // 插入任务授权信息表(保存数据)
            for (ActAljoinTaskAssignee obj : actAljoinTaskAssigneeList) {
                obj.setBpmnId(bpmn.getId());
            }
            actAljoinTaskAssigneeService.insertBatch(actAljoinTaskAssigneeList);
            retMsg.setCode(0);
            retMsg.setMessage("保存成功");
        }
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg updateXmlCode(Map<String, String> param) throws Exception {
        String xmlCode = param.get("xmlCode");
        String processCategory = param.get("processCategory");
        String processId = param.get("processId");
        String processName = param.get("processName");
        String processDesc = param.get("processDesc");
        String formIds = param.get("formIds");
        String elementTypes = param.get("elementTypes");
        String elementIds = param.get("elementIds");
        String taskIds = param.get("taskIds");
        String assigneeDepartmentIds = param.get("assigneeDepartmentIds");
        String assigneePositionIds = param.get("assigneePositionIds");
        String assigneeUserIds = param.get("assigneeUserIds");
        String assigneeCandidateIds = param.get("assigneeCandidateIds");
        String assigneeGroupIds = param.get("assigneeGroupIds");
        String backupHtml = param.get("backupHtml");
        String id = param.get("id");
        String showWidgetIds = param.get("showWidgetIds");
        String editWidgetIds = param.get("editWidgetIds");
        String notNullWidgetIds = param.get("notNullWidgetIds");
        String aljoinTaskOperateAuthIds = param.get("aljoinTaskOperateAuthIds");
        String commentWidgetIds = param.get("commentWidgetIds");
        String aljoinProcessIsfixed = param.get("aljoinProcessIsfixed");
        String aljoinProcessIsfree = param.get("aljoinProcessIsfree");
        String redHeadWidgetIds = param.get("redHeadWidgetIds");
        String saveMarkWidgetIds = param.get("saveMarkWidgetIds");
        String isReturnCreaters = param.get("isReturnCreaters");
        String signCommentWidgetIds = param.get("signCommentWidgetIds");
        String staffMembersDepartments = param.get("staffMembersDepartments");
        String lastlinkDepartments = param.get("lastlinkDepartments");
        String createPersonsJobs = param.get("createPersonsJobs");
        RetMsg retMsg = new RetMsg();

        // 获取原有流程数据
        ActAljoinBpmn orgnlBpmn = selectById(Long.parseLong(id));

        // 首先根据流程ID或流程名称判断流程是否已经存在（自己除外）
        Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
        where.ne("id", orgnlBpmn.getId());
        where.andNew("process_id = {0}", processId).or("process_name = {0}", processName);
        if (selectCount(where) > 0) {
            retMsg.setCode(1);
            retMsg.setMessage("流程ID或名称已经存在");
        } else {
            //解绑文号或流水号
            sysSerialNumberService.unBindBpmn(orgnlBpmn.getId().toString());
            sysDocumentNumberService.unBindBpmn(orgnlBpmn.getId().toString());
            List<String> serialNumIds = actAljoinFormService.validateSerialNum(formIds);
            if (null != serialNumIds && serialNumIds.size() > 0) {
              //流水号绑定流程
               sysSerialNumberService.bindBpmn(orgnlBpmn.getId().toString(),serialNumIds);
            } else {
              //判断表中是否有文号
                List<String> documentNumIds = actAljoinFormService.validateDocumentNum(formIds);
                if (null != documentNumIds && documentNumIds.size() > 0) {
                    //文号绑定流程
                    sysDocumentNumberService.bindBpmn(orgnlBpmn.getId().toString(),documentNumIds);
                }
            }
            // 获取根对象
            Document xmlDocument = DocumentHelper.parseText(xmlCode);
            // 获取bpmn2:process
            Element processElement = (Element)xmlDocument.getRootElement().elements().get(0);
            // 遍历bpmn2:process下的节点
            for (Object obj : processElement.elements()) {
                Element element = ((Element)obj);
                // 删除任务节点的权限信息
                if ("userTask".equals(element.getName())) {
                    Attribute assigneeAttr = element.attribute("assignee");
                    if (assigneeAttr != null) {
                        element.remove(assigneeAttr);
                    }
                    Attribute candidateUsersAttr = element.attribute("candidateUsers");
                    if (candidateUsersAttr != null) {
                        element.remove(candidateUsersAttr);
                    }
                    Attribute candidateGroupsAttr = element.attribute("candidateGroups");
                    if (candidateGroupsAttr != null) {
                        element.remove(candidateGroupsAttr);
                    }
                }
            }
            xmlCode = xmlDocument.asXML();
            // 插入任务授权信息表(构造数据)
            String[] taskIdsArr = taskIds.split(";");
            String[] assigneeDepartmentIdsArr = assigneeDepartmentIds.split(";");
            String[] assigneePositionIdsArr = assigneePositionIds.split(";");
            String[] assigneeUserIdsArr = assigneeUserIds.split(";");
            String[] assigneeCandidateIdsArr = assigneeCandidateIds.split(";");
            String[] assigneeGroupIdsArr = assigneeGroupIds.split(";");
            String[] showWidgetIdsArr = showWidgetIds.split(";");
            String[] editWidgetIdsArr = editWidgetIds.split(";");
            String[] notNullWidgetIdsArr = notNullWidgetIds.split(";");
            String[] commentWidgetIdsArr = commentWidgetIds.split(";");
            String[] aljoinTaskOperateAuthIdsArr = aljoinTaskOperateAuthIds.split(";");
            String[] redHeadWidgetIdsArr = redHeadWidgetIds.split(";");
            String[] saveMarkWidgetIdsArr = saveMarkWidgetIds.split(";");
            String[] isReturnCreatersArr = isReturnCreaters.split(";");
            String[] signCommentWidgetIdsArr = signCommentWidgetIds.split(";");
            String[] staffMembersDepartmentsArr = staffMembersDepartments.split(";");
            String[] lastlinkDepartmentsArr = lastlinkDepartments.split(";");
            String[] createPersonsJobsArr = createPersonsJobs.split(";");
            List<ActAljoinTaskAssignee> actAljoinTaskAssigneeList = new ArrayList<ActAljoinTaskAssignee>();
            for (int i = 0; i < taskIdsArr.length; i++) {
                ActAljoinTaskAssignee actAljoinTaskAssignee = new ActAljoinTaskAssignee();
                actAljoinTaskAssignee.setBpmnId(orgnlBpmn.getId());
                actAljoinTaskAssignee.setProcessId(processId);
                actAljoinTaskAssignee.setTaskId(taskIdsArr[i]);
                actAljoinTaskAssignee.setAssigneeDepartmentIds(assigneeDepartmentIdsArr[i]);
                actAljoinTaskAssignee.setAssigneePositionIds(assigneePositionIdsArr[i]);
                actAljoinTaskAssignee.setAssigneeUserIds(assigneeUserIdsArr[i]);
                actAljoinTaskAssignee.setAssigneeCandidateIds(assigneeCandidateIdsArr[i]);
                actAljoinTaskAssignee.setAssigneeGroupIds(assigneeGroupIdsArr[i]);
                actAljoinTaskAssignee.setIsActive(1);
                actAljoinTaskAssignee.setShowWidgetIds(showWidgetIdsArr[i]);
                actAljoinTaskAssignee.setEditWidgetIds(editWidgetIdsArr[i]);
                actAljoinTaskAssignee.setNotNullWidgetIds(notNullWidgetIdsArr[i]);
                actAljoinTaskAssignee.setCommentWidgetIds(commentWidgetIdsArr[i]);
                actAljoinTaskAssignee.setOperateAuthIds(aljoinTaskOperateAuthIdsArr[i]);
                actAljoinTaskAssignee.setRedHeadWidgetIds(redHeadWidgetIdsArr[i]);
                actAljoinTaskAssignee.setSaveMarkWidgetIds(saveMarkWidgetIdsArr[i]);
                if (null != isReturnCreatersArr[i] && !(isReturnCreatersArr[i].length() == 0)) {
                    actAljoinTaskAssignee.setIsReturnCreater(Integer.parseInt(isReturnCreatersArr[i]));
                }
                actAljoinTaskAssignee.setSignCommentWidgetIds(signCommentWidgetIdsArr[i]);
                if (null != staffMembersDepartmentsArr[i] && !(staffMembersDepartmentsArr[i].length() == 0)) {
                    actAljoinTaskAssignee.setStaffMembersDepartment(Integer.parseInt(staffMembersDepartmentsArr[i]));
                }
                if (null != lastlinkDepartmentsArr[i] && !(lastlinkDepartmentsArr[i].length() == 0)) {
                    actAljoinTaskAssignee.setLastlinkDepartment(Integer.parseInt(lastlinkDepartmentsArr[i]));
                }
                if (null != createPersonsJobsArr[i] && !(createPersonsJobsArr[i].length() == 0)) {
                    actAljoinTaskAssignee.setCreatePersonsJob(Integer.parseInt(createPersonsJobsArr[i]));
                }
                actAljoinTaskAssigneeList.add(actAljoinTaskAssignee);

                // 修改流程xml文件的：activiti:assignee，activiti:candidateUsers，activiti:candidateGroups
                // 以下通过字符串进行替换
                String assignee = "null".equals(assigneeUserIdsArr[i]) ? "" : assigneeUserIdsArr[i];
                String candidateUsers = "null".equals(assigneeCandidateIdsArr[i]) ? "" : assigneeCandidateIdsArr[i];
                String candidateGroups = "";
                // 部门
                if (!"null".equals(assigneeDepartmentIdsArr[i])) {
                    candidateGroups = assigneeDepartmentIdsArr[i];
                }
                // 岗位
                if (!"null".equals(assigneePositionIdsArr[i])) {
                    if ("".equals(candidateGroups)) {
                        candidateGroups = assigneePositionIdsArr[i];
                    } else {
                        candidateGroups += "," + assigneePositionIdsArr[i];
                    }
                }
                // 角色
                if (!"null".equals(assigneeGroupIdsArr[i])) {
                    if ("".equals(candidateGroups)) {
                        candidateGroups = assigneeGroupIdsArr[i];
                    } else {
                        candidateGroups += "," + assigneeGroupIdsArr[i];
                    }
                }
                // 新增加的节点
                String oldChar = "id=\"" + taskIdsArr[i] + "\"";
                String newChar = oldChar + " activiti:assignee=\"" + assignee + "\" activiti:candidateUsers=\""
                    + candidateUsers + "\" activiti:candidateGroups=\"" + candidateGroups + "\"";
                // 在替换
                xmlCode = xmlCode.replace(oldChar, newChar);
            }

            // 把camunda:替换成activiti:
            xmlCode = xmlCode.replaceAll("camunda:", "activiti:");

            // <bpmn2:multiInstanceLoopCharacteristics isSequential="true" />---------默认情况（串行）
            while (xmlCode.contains("<bpmn2:multiInstanceLoopCharacteristics isSequential=\"true\"/>")) {
                String randomStr = "ASSIGNEE_C_" + StringUtil.randomString(10);
                String isSequentialFullMultiInstance =
                    "<bpmn2:multiInstanceLoopCharacteristics isSequential=\"true\" activiti:collection=\"\\$\\{"
                        + randomStr + "\\}\">\n";
                isSequentialFullMultiInstance +=
                    "  <bpmn2:completionCondition xsi:type=\"bpmn2:tFormalExpression\">\\$\\{nrOfCompletedInstances/nrOfInstances == 1\\}</bpmn2:completionCondition>\n";
                isSequentialFullMultiInstance += "</bpmn2:multiInstanceLoopCharacteristics>\n";
                xmlCode = xmlCode.replaceFirst("<bpmn2:multiInstanceLoopCharacteristics isSequential=\"true\"/>",
                    isSequentialFullMultiInstance);
            }

            // <bpmn2:multiInstanceLoopCharacteristics />---------默认情况（并行）
            while (xmlCode.contains("<bpmn2:multiInstanceLoopCharacteristics/>")) {
                String randomStr = "ASSIGNEE_B_" + StringUtil.randomString(10);
                String unSequentialFullMultiInstance =
                    "<bpmn2:multiInstanceLoopCharacteristics activiti:collection=\"\\$\\{" + randomStr + "\\}\">\n";
                unSequentialFullMultiInstance +=
                    "  <bpmn2:completionCondition xsi:type=\"bpmn2:tFormalExpression\">\\$\\{nrOfCompletedInstances/nrOfInstances == 1\\}</bpmn2:completionCondition>\n";
                unSequentialFullMultiInstance += "</bpmn2:multiInstanceLoopCharacteristics>\n";
                xmlCode =
                    xmlCode.replaceFirst("<bpmn2:multiInstanceLoopCharacteristics/>", unSequentialFullMultiInstance);
            }

            // 插入流程表
            orgnlBpmn.setXmlCode(xmlCode);
            orgnlBpmn.setCategoryId(Long.parseLong(processCategory));
            orgnlBpmn.setProcessId(processId);
            orgnlBpmn.setProcessName(processName);
            orgnlBpmn.setProcessDesc(processDesc);
            orgnlBpmn.setHtmlCode(backupHtml);
            orgnlBpmn.setIsDeployAfterEdit(0);// 编辑后没部署
            orgnlBpmn.setIsFormEdit(0);// 修改后表被更新
            orgnlBpmn.setHasForm("".equals(formIds) ? 0 : 1);
            orgnlBpmn.setIsFixed(Integer.parseInt(aljoinProcessIsfixed));
            orgnlBpmn.setIsFree(Integer.parseInt(aljoinProcessIsfree));
            updateById(orgnlBpmn);

            // 插入流程-表单(运行时)关系(先删除在建立)
            Where<ActAljoinBpmnForm> formWhere = new Where<ActAljoinBpmnForm>();
            formWhere.eq("bpmn_id", orgnlBpmn.getId());
            actAljoinBpmnFormService.delete(formWhere);

            if (!"".equals(formIds)) {
                String[] formIdsArr = formIds.split(",");
                String[] elementTypesArr = elementTypes.split(",");
                String[] elementIdsArr = elementIds.split(",");
                List<ActAljoinBpmnForm> actAljoinBpmnFormList = new ArrayList<ActAljoinBpmnForm>();
                for (int i = 0; i < formIdsArr.length; i++) {
                    Long runFormId = IdWorker.getId();
                    Long formId = Long.parseLong(formIdsArr[i]);// 这个ID是源表（表单表的主键ID）
                    // 先查询运行时表是否有当前同版本的表单
                    ActAljoinForm actAljoinForm = actAljoinFormService.selectById(formId);
                    Where<ActAljoinFormRun> actAljoinFormRunWhere = new Where<ActAljoinFormRun>();
                    actAljoinFormRunWhere.eq("orgnl_id", formId);
                    actAljoinFormRunWhere.eq("version", actAljoinForm.getVersion());
                    ActAljoinFormRun actAljoinFormRun = actAljoinFormRunService.selectOne(actAljoinFormRunWhere);

                    ActAljoinBpmnForm actAljoinBpmnForm = new ActAljoinBpmnForm();
                    actAljoinBpmnForm.setBpmnId(orgnlBpmn.getId());
                    // 如果运行时已经有存在当前添加的同版本的表单，则可以直接使用，如果没有在需要插入对应版本的表单数据
                    actAljoinBpmnForm.setFormId(actAljoinFormRun == null ? runFormId : actAljoinFormRun.getId());
                    actAljoinBpmnForm.setIsActive(1);
                    actAljoinBpmnForm.setElementType(elementTypesArr[i]);
                    actAljoinBpmnForm.setElementId(elementIdsArr[i]);
                    actAljoinBpmnForm.setProcessId(processId);
                    actAljoinBpmnFormList.add(actAljoinBpmnForm);
                    // 如果该表单没有进入运行时（即之前的流程没有管理到该表单）需要插入运行时表，如果已经在运行时有表单数据，则需要判断版本号，
                    // 如果版本号和源数据一致，则该表单不需要插入，否则要把运行时的数据转移到历史表，在把源表的数据更新的运行时表
                    // 源-运行时-历史表，这三表的数据是进行完整copy,包含公共字段，所有查询的时候可以直接根据主键ID对运行时进行查找
                    if (actAljoinFormRun == null) {
                        actAljoinFormRun = new ActAljoinFormRun();
                        BeanUtils.copyProperties(actAljoinForm, actAljoinFormRun);
                        actAljoinFormRun.setOrgnlId(actAljoinFormRun.getId());
                        actAljoinFormRun.setId(runFormId);
                        actAljoinFormRunService.copyObject(actAljoinFormRun);
                        // 查询表单下控件并插入
                        Where<ActAljoinFormWidget> actAljoinFormWidgetWhere = new Where<ActAljoinFormWidget>();
                        actAljoinFormWidgetWhere.eq("form_Id", formId);
                        List<ActAljoinFormWidget> actAljoinFormWidgetList =
                            actAljoinFormWidgetService.selectList(actAljoinFormWidgetWhere);
                        for (ActAljoinFormWidget actAljoinFormWidget : actAljoinFormWidgetList) {
                            ActAljoinFormWidgetRun actAljoinFormWidgetRun = new ActAljoinFormWidgetRun();
                            BeanUtils.copyProperties(actAljoinFormWidget, actAljoinFormWidgetRun);
                            actAljoinFormWidgetRunService.copyObject(actAljoinFormWidgetRun);
                            // 查询控件属性并插入
                            Where<ActAljoinFormAttribute> actAljoinFormAttributeWhere =
                                new Where<ActAljoinFormAttribute>();
                            actAljoinFormAttributeWhere.eq("widget_id", actAljoinFormWidgetRun.getId());
                            List<ActAljoinFormAttribute> actAljoinFormAttributeList =
                                actAljoinFormAttributeService.selectList(actAljoinFormAttributeWhere);
                            for (ActAljoinFormAttribute actAljoinFormAttribute : actAljoinFormAttributeList) {
                                ActAljoinFormAttributeRun actAljoinFormAttributeRun = new ActAljoinFormAttributeRun();
                                BeanUtils.copyProperties(actAljoinFormAttribute, actAljoinFormAttributeRun);
                                actAljoinFormAttributeRunService.copyObject(actAljoinFormAttributeRun);
                            }
                        }
                    }
                }
                if (actAljoinBpmnFormList.size() > 0) {
                    actAljoinBpmnFormService.insertBatch(actAljoinBpmnFormList);
                }
            }
            // 插入任务授权信息表(保存数据),先删除原有数据
            if (actAljoinTaskAssigneeList.size() > 0) {
                Where<ActAljoinTaskAssignee> assigneeWhere = new Where<ActAljoinTaskAssignee>();
                assigneeWhere.eq("bpmn_id", orgnlBpmn.getId());
                assigneeWhere.setSqlSelect("version,id");
                assigneeWhere.orderBy("version", false);
                List<ActAljoinTaskAssignee> taskAssigneeList = actAljoinTaskAssigneeService.selectList(assigneeWhere);
                Integer version = 0;
                if(taskAssigneeList.size()>0){
                    version = taskAssigneeList.get(0).getVersion() + 1;
                }
                for (ActAljoinTaskAssignee actAljoinTaskAssignee : actAljoinTaskAssigneeList) {
                    actAljoinTaskAssignee.setVersion(version);
                }
                actAljoinTaskAssigneeService.insertBatch(actAljoinTaskAssigneeList);
            }
            retMsg.setCode(0);
            retMsg.setMessage("保存成功");
        }
        return retMsg;
    }

    @Override
    public void updateFormProcess(Long formId) throws Exception {
        // 根据表单ID查询关联的流程
        Where<ActAljoinFormRun> actAljoinFormRunWhere = new Where<ActAljoinFormRun>();
        actAljoinFormRunWhere.eq("orgnl_id", formId);
        actAljoinFormRunWhere.setSqlSelect("id");
        List<ActAljoinFormRun> actAljoinFormRunList = actAljoinFormRunService.selectList(actAljoinFormRunWhere);
        if (actAljoinFormRunList.size() > 0) {
            List<Long> runFormIdList = new ArrayList<Long>();
            for (ActAljoinFormRun actAljoinFormRun : actAljoinFormRunList) {
                runFormIdList.add(actAljoinFormRun.getId());
            }
            // 查询运行时表单关联的流程
            Where<ActAljoinBpmnForm> actAljoinBpmnFormWhere = new Where<ActAljoinBpmnForm>();
            actAljoinBpmnFormWhere.in("form_id", runFormIdList);
            actAljoinBpmnFormWhere.setSqlSelect("bpmn_id");
            List<ActAljoinBpmnForm> actAljoinBpmnFormList = actAljoinBpmnFormService.selectList(actAljoinBpmnFormWhere);
            if (actAljoinBpmnFormList.size() > 0) {
                List<Long> bpmnIdList = new ArrayList<Long>();
                for (ActAljoinBpmnForm bpmnForm : actAljoinBpmnFormList) {
                    bpmnIdList.add(bpmnForm.getBpmnId());
                }
                Where<ActAljoinBpmn> actAljoinBpmnWhere = new Where<ActAljoinBpmn>();
                actAljoinBpmnWhere.in("id", bpmnIdList);
                List<ActAljoinBpmn> actAljoinBpmnList = selectList(actAljoinBpmnWhere);
                for (ActAljoinBpmn actAljoinBpmn : actAljoinBpmnList) {
                    actAljoinBpmn.setIsFormEdit(1);
                }
                updateBatchById(actAljoinBpmnList);
            }
        }
    }

    @Override
    public RetMsg exportXmlCode(String xmlCode, String processId, String processName, String taskIds,
        String assigneeDepartmentIds, String assigneePositionIds, String assigneeUserIds, String assigneeCandidateIds,
        String assigneeGroupIds) throws Exception {
        RetMsg retMsg = new RetMsg();
        // 首先根据流程ID或流程名称判断流程是否已经
        Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
        where.eq("process_id", processId);
        // where.or("process_name", processName);
        where.or("process_name = {0}", processName);
        if (selectCount(where) > 0) {
            retMsg.setCode(1);
            retMsg.setMessage("流程ID或名称已经存在");
            return retMsg;
        } else {
            // 插入任务授权信息表(构造数据)
            String[] taskIdsArr = taskIds.split(";");
            String[] assigneeDepartmentIdsArr = assigneeDepartmentIds.split(";");
            String[] assigneePositionIdsArr = assigneePositionIds.split(";");
            String[] assigneeUserIdsArr = assigneeUserIds.split(";");
            String[] assigneeCandidateIdsArr = assigneeCandidateIds.split(";");
            String[] assigneeGroupIdsArr = assigneeGroupIds.split(";");
            for (int i = 0; i < taskIdsArr.length; i++) {
                // 修改流程xml文件的：activiti:assignee，activiti:candidateUsers，activiti:candidateGroups
                // 以下通过字符串进行替换
                String assignee = "null".equals(assigneeUserIdsArr[i]) ? "" : assigneeUserIdsArr[i];
                String candidateUsers = "null".equals(assigneeCandidateIdsArr[i]) ? "" : assigneeCandidateIdsArr[i];
                String candidateGroups = "";
                // 部门
                if (!"null".equals(assigneeDepartmentIdsArr[i])) {
                    candidateGroups = assigneeDepartmentIdsArr[i];
                }
                // 岗位
                if (!"null".equals(assigneePositionIdsArr[i])) {
                    if ("".equals(candidateGroups)) {
                        candidateGroups = assigneePositionIdsArr[i];
                    } else {
                        candidateGroups += "," + assigneePositionIdsArr[i];
                    }
                }
                // 角色
                if (!"null".equals(assigneeGroupIdsArr[i])) {
                    if ("".equals(candidateGroups)) {
                        candidateGroups = assigneeGroupIdsArr[i];
                    } else {
                        candidateGroups += "," + assigneeGroupIdsArr[i];
                    }
                }

                String oldChar = "id=\"" + taskIdsArr[i] + "\"";
                String newChar = oldChar + " activiti:assignee=\"" + assignee + "\" activiti:candidateUsers=\""
                    + candidateUsers + "\" activiti:candidateGroups=\"" + candidateGroups + "\"";
                xmlCode = xmlCode.replace(oldChar, newChar);
            }
            retMsg.setObject(xmlCode);
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        }
        return retMsg;
    }

    @Override
    public Page<ActAljoinBpmnVO> userBpmnlist(PageBean pageBean, String typeID, String usrID) throws Exception {
        Page<ActAljoinBpmnVO> page = new Page<ActAljoinBpmnVO>();
        List<ActAljoinBpmnVO> bpmnVOList = new ArrayList<ActAljoinBpmnVO>();
        String cgIds = "";
        // 类别ID存在返回对应流程分类ID及下级类别ID
        if (typeID != null && !"".equals(typeID)) {
            cgIds = typeID + ",";
            List<ActAljoinCategory> cgList = actAljoinCategoryService.getAllChildList(Long.valueOf(typeID));
            for (ActAljoinCategory actAljoinCategory : cgList) {
                cgIds += actAljoinCategory.getId() + ",";
            }
        }
        Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
        Where<ActAljoinBpmnUser> bUWhere = new Where<ActAljoinBpmnUser>();
        bUWhere.eq("user_id", usrID);
        bUWhere.eq("is_active", 1);
        bUWhere.eq("is_delete", 0);
        bUWhere.eq("auth_type", 1);
        bUWhere.setSqlSelect("bpmn_id");
        List<ActAljoinBpmnUser> uBpmnList = actAljoinBpmnUserService.selectList(bUWhere);
        String bpmnIds = "";
        if (uBpmnList != null && uBpmnList.size() > 0) {
            for (ActAljoinBpmnUser actAljoinBpmnUser : uBpmnList) {
                bpmnIds += actAljoinBpmnUser.getBpmnId() + ",";
            }
            where.in("id", bpmnIds);
        } else {
            page.setRecords(bpmnVOList);
            page.setSize(pageBean.getPageSize());
            page.setTotal(0);
            return page;
        }
        where.eq("is_delete", 0);
        where.eq("is_deploy", 1);
        where.eq("is_active", 1);
        if (cgIds != null && !"".equals(cgIds)) {
            where.in("category_id", cgIds);
        }
        bUWhere.setSqlSelect("id,process_id,process_name,category_id");
        Page<ActAljoinBpmn> bpmnPage =
            selectPage(new Page<ActAljoinBpmn>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        List<ActAljoinBpmn> retrunList = bpmnPage.getRecords();
        if (retrunList != null && retrunList.size() > 0) {
            for (ActAljoinBpmn actAljoinBpmn : retrunList) {
                ActAljoinBpmnVO actAljoinBpmnVO = new ActAljoinBpmnVO();
                actAljoinBpmnVO.setId(actAljoinBpmn.getId().toString());
                actAljoinBpmnVO.setProcessId(actAljoinBpmn.getProcessId());
                actAljoinBpmnVO.setIsActive(actAljoinBpmn.getIsActive());
                actAljoinBpmnVO.setProcessName(actAljoinBpmn.getProcessName());
                actAljoinBpmnVO.setCategoryName(
                    actAljoinCategoryService.selectById(actAljoinBpmn.getCategoryId()).getCategoryName());
                actAljoinBpmnVO.setIsDeployAfterEdit(actAljoinBpmn.getIsDeployAfterEdit());
                actAljoinBpmnVO.setIsDeploy(actAljoinBpmn.getIsDeploy());
                actAljoinBpmnVO.setIsFormEdit(actAljoinBpmn.getIsFormEdit());
                actAljoinBpmnVO.setLastDeployTime(actAljoinBpmn.getLastDeployTime());
                bpmnVOList.add(actAljoinBpmnVO);
            }
        }

        page.setRecords(bpmnVOList);
        page.setSize(bpmnPage.getSize());
        page.setTotal(bpmnPage.getTotal());
        return page;
    }

    @Override
    public List<ActAljoinBpmn> setCategoryBpmn(String cateGoryId) throws Exception {
        // TODO Auto-generated method stub
        /*
         * ActAljoinCategory cate=actAljoinCategoryService.selectById(Long.valueOf(cateGoryId));
         * if(cate!=null){ }
         */
        String parentId = "";
        String tmpId = cateGoryId + ",";
        // 获取所有子类流程
        do {
            parentId += tmpId;
            Where<ActAljoinCategory> cwhere = new Where<ActAljoinCategory>();
            cwhere.in("parent_id", tmpId);
            cwhere.eq("is_active", 1);
            cwhere.setSqlSelect("id");
            List<ActAljoinCategory> list = actAljoinCategoryService.selectList(cwhere);
            tmpId = "";
            if (list != null && list.size() > 0) {
                for (ActAljoinCategory actAljoinCategory : list) {
                    tmpId += actAljoinCategory.getId() + ",";
                }
            }
        } while (!"".equals(tmpId));
        parentId = parentId.substring(0, parentId.length() - 1);
        Where<ActAljoinBpmn> bpmnWhere = new Where<ActAljoinBpmn>();
        bpmnWhere.eq("is_active", 1);
        bpmnWhere.in("category_id", parentId);
        bpmnWhere.setSqlSelect("id,process_name");
        List<ActAljoinBpmn> bpmnList = actAljoinBpmnService.selectList(bpmnWhere);
        return bpmnList;
    }
    
    @Override
    @Transactional
    public RetMsg delete(ActAljoinBpmn obj) {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            //解绑文号或流水号
            sysSerialNumberService.unBindBpmn(obj.getId().toString());
            sysDocumentNumberService.unBindBpmn(obj.getId().toString());
            deleteById(obj.getId());
            //当流程删除时，对应的流程用户关系也删除
            //Where<ActAljoinBpmnUser> where=new Where<ActAljoinBpmnUser>();
            //where.eq("bpmn_id", obj.getId());
            //actAljoinBpmnUserService.delete(where);
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg copy(ActAljoinBpmn bpmn) {
        RetMsg retMsg = new RetMsg();
        if (null != bpmn) {
            ActAljoinBpmn original = selectById(bpmn.getId());
            //生成流程id
            String processId = "Process_"+StringUtil.randomNumAndLetter(10);
            // 首先根据流程ID或流程名称判断流程是否已经
            Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
            where.eq("process_id", processId);
            // where.or("process_name", processName);
            where.or("process_name = {0}", original.getProcessName()+"-副本");
            if (selectCount(where) > 0) {
                retMsg.setCode(1);
                retMsg.setMessage("流程ID或名称已经存在");
                return retMsg;
            }
           //插入流程表数据
           ActAljoinBpmn actAljoinBpmn = new ActAljoinBpmn();
           String xmlCode = original.getXmlCode();
           xmlCode = xmlCode.replace("id=\""+original.getProcessId()+"\" name=\""+original.getProcessName()+"\"", "id=\""+processId+"\" name=\""+original.getProcessName()+"-副本"+"\"");
           actAljoinBpmn.setXmlCode(xmlCode);
           actAljoinBpmn.setCategoryId(original.getCategoryId());
           actAljoinBpmn.setIsActive(1);
           actAljoinBpmn.setProcessId(processId);
           actAljoinBpmn.setProcessName(original.getProcessName()+"-副本");
           actAljoinBpmn.setProcessDesc(original.getProcessDesc());
           actAljoinBpmn.setHtmlCode(original.getHtmlCode());
           actAljoinBpmn.setHasForm(original.getHasForm());
           actAljoinBpmn.setIsFixed(original.getIsFixed());
           actAljoinBpmn.setIsFree(original.getIsFree());
           actAljoinBpmn.setIsDeploy(0);
           actAljoinBpmn.setIsDeployAfterEdit(0);
           actAljoinBpmn.setIsFormEdit(original.getIsFormEdit());
           insert(actAljoinBpmn);
           Long bpmnId =actAljoinBpmn.getId();
           Where<ActAljoinBpmnForm> bpmnFormWhere = new Where<ActAljoinBpmnForm>();
           bpmnFormWhere.eq("process_id",original.getProcessId());
           bpmnFormWhere.setSqlSelect("id,form_id,bpmn_id,form_id,element_type,element_id,process_id");
           List<ActAljoinBpmnForm> list = actAljoinBpmnFormService.selectList(bpmnFormWhere);
           String formIds = "";
           if (null != list && list.size() > 0) {
               //插入流程表单表
               List<ActAljoinBpmnForm> newBpmnFormList = new ArrayList<ActAljoinBpmnForm>();
               for (ActAljoinBpmnForm bpmnForm : list) {
                   formIds = formIds + bpmnForm.getFormId() + ";";
                   ActAljoinBpmnForm newBpmnForm = new ActAljoinBpmnForm();
                   newBpmnForm.setBpmnId(bpmnId);
                   newBpmnForm.setFormId(bpmnForm.getFormId());
                   newBpmnForm.setIsActive(1);
                   newBpmnForm.setElementId(bpmnForm.getElementId());
                   newBpmnForm.setElementType(bpmnForm.getElementType());
                   newBpmnForm.setProcessId(processId);
                   newBpmnFormList.add(newBpmnForm);
               }
               actAljoinBpmnFormService.insertBatch(newBpmnFormList);
           }
           if (StringUtils.isNotEmpty(formIds)) {
               //判断表中是否有流水号
               List<String> serialNumIds = actAljoinFormService.validateSerialNum(formIds);
               if (null != serialNumIds && serialNumIds.size() > 0) {
                 //流水号绑定流程
                  sysSerialNumberService.bindBpmn(original.getProcessName()+"-副本",serialNumIds);
               } else {
                 //判断表中是否有文号
                  List<String> documentNumIds = actAljoinFormService.validateDocumentNum(formIds);
                  if (null != documentNumIds && documentNumIds.size() > 0) {
                      //文号绑定流程
                      sysDocumentNumberService.bindBpmn(original.getProcessName()+"-副本",documentNumIds);
                  }
               }
           }
           //插入任务授权信息表
           List<ActAljoinTaskAssignee> newTaskAssigneeList = new ArrayList<ActAljoinTaskAssignee>();
           Where<ActAljoinTaskAssignee> taskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
           taskAssigneeWhere.eq("process_id",original.getProcessId());
           List<ActAljoinTaskAssignee> taskAssigneeList = actAljoinTaskAssigneeService.selectList(taskAssigneeWhere);
           if (null != taskAssigneeList && taskAssigneeList.size() > 0) {
               for (ActAljoinTaskAssignee taskAssignee : taskAssigneeList) {
                   ActAljoinTaskAssignee newTaskAssignee = new ActAljoinTaskAssignee();
                   newTaskAssignee.setBpmnId(bpmnId);
                   newTaskAssignee.setProcessId(processId);
                   newTaskAssignee.setTaskId(taskAssignee.getTaskId());
                   newTaskAssignee.setAssigneeDepartmentIds(taskAssignee.getAssigneeDepartmentIds());
                   newTaskAssignee.setAssigneePositionIds(taskAssignee.getAssigneePositionIds());
                   newTaskAssignee.setAssigneeUserIds(taskAssignee.getAssigneeUserIds());
                   newTaskAssignee.setAssigneeCandidateIds(taskAssignee.getAssigneeCandidateIds());
                   newTaskAssignee.setAssigneeGroupIds(taskAssignee.getAssigneeGroupIds());
                   newTaskAssignee.setShowWidgetIds(taskAssignee.getShowWidgetIds());
                   newTaskAssignee.setEditWidgetIds(taskAssignee.getEditWidgetIds());
                   newTaskAssignee.setCommentWidgetIds(taskAssignee.getCommentWidgetIds());
                   newTaskAssignee.setNotNullWidgetIds(taskAssignee.getNotNullWidgetIds());
                   newTaskAssignee.setOperateAuthIds(taskAssignee.getOperateAuthIds());
                   newTaskAssignee.setRedHeadWidgetIds(taskAssignee.getRedHeadWidgetIds());
                   newTaskAssignee.setSaveMarkWidgetIds(taskAssignee.getSaveMarkWidgetIds());
                   newTaskAssignee.setSignCommentWidgetIds(taskAssignee.getSignCommentWidgetIds());
                   newTaskAssignee.setStaffMembersDepartment(taskAssignee.getStaffMembersDepartment());
                   newTaskAssignee.setLastlinkDepartment(taskAssignee.getLastlinkDepartment());
                   newTaskAssignee.setCreatePersonsJob(taskAssignee.getCreatePersonsJob());
                   newTaskAssignee.setIsReturnCreater(taskAssignee.getIsReturnCreater());
                   newTaskAssignee.setIsActive(1);
                   newTaskAssigneeList.add(newTaskAssignee);
               }
               actAljoinTaskAssigneeService.insertBatch(newTaskAssigneeList);
           }
           
        }
        retMsg.setCode(0);
        retMsg.setMessage("复制成功");
        return retMsg;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void export(HttpServletResponse response, ActAljoinBpmnVO bpmn) throws Exception {
        if (bpmn != null) {
            String[] arr = bpmn.getIds().split(";");
            List<Long> ids = new ArrayList<Long>();
            for(int i = 0; i < arr.length; i++) {
              ids.add(Long.valueOf(arr[i]));
            }
            //获取流程信息
            Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
            where.in("id", ids);
            where.setSqlSelect("id,html_code,xml_code,process_desc,is_free,is_fixed,is_form_edit,process_id,process_name");
            List<ActAljoinBpmn> bpmnList = selectList(where);
            //用于批量下载
            ZipOutputStream zos = null;
            if (bpmnList != null && bpmnList.size() > 1) {
                String zipFileName = PROCESS_BATCH_DOWN_NAME;
                zos = new ZipOutputStream(response.getOutputStream());
                response.reset();
                response.setContentType("application/force-download");
                response.setHeader("content-Disposition",
                        "attachment;filename=" + URLEncoder.encode(zipFileName, "utf-8"));
            }
            if (bpmnList != null && bpmnList.size() > 0 ) {
                for (ActAljoinBpmn actAljoinBpmn : bpmnList) {
                    StringBuilder html = new StringBuilder();
                    //获得流程表数据
                  //修改流程xml文件的：activiti:assignee，activiti:candidateUsers，activiti:candidateGroups
                    Document xmlCodeDoc = DocumentHelper.parseText(actAljoinBpmn.getXmlCode());
                    Element root = xmlCodeDoc.getRootElement();
                    Iterator<Element> iter = root.elementIterator("process"); 
                    if (iter.hasNext()) {
                        Element next = iter.next();
                        Iterator<Element> userTaskIterator = next.elementIterator("userTask"); 
                        while (userTaskIterator.hasNext()) {
                            Element taskEle = (Element) userTaskIterator.next();
                            Attribute attribute1 = taskEle.attribute("assignee");
                            attribute1.setValue("");
                            Attribute attribute2 = taskEle.attribute("candidateUsers");
                            attribute2.setValue("");
                            Attribute attribute3 = taskEle.attribute("candidateGroups");
                            attribute3.setValue("");
                        }
                    }
                    String gzipXmlCode = FileUtil.gzip(xmlCodeDoc.asXML());
                    html.append("<div id=\"bpmnId\" xml_code=\""+gzipXmlCode+"\" html_code=\""+actAljoinBpmn.getHtmlCode()+"\" "
                        + "process_id=\""+actAljoinBpmn.getProcessId()+"\" process_name=\""+actAljoinBpmn.getProcessName()+"\""
                        + "process_desc=\""+actAljoinBpmn.getProcessDesc()+"\" is_free=\""+actAljoinBpmn.getIsFree()+"\" is_fixed=\""+actAljoinBpmn.getIsFixed()
                        +"\" is_form_edit=\""+actAljoinBpmn.getIsFormEdit()+"\"></div>");
                    //获得任务授权信息
                    Where<ActAljoinTaskAssignee> taskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
                    taskAssigneeWhere.eq("process_id",actAljoinBpmn.getProcessId());
                    List<ActAljoinTaskAssignee> taskAssigneeList = actAljoinTaskAssigneeService.selectList(taskAssigneeWhere);
                    if (null != taskAssigneeList && taskAssigneeList.size() > 0) {
                        for (int i=0; i<taskAssigneeList.size(); i++) {
                            html.append("<div id=\"taskAssigneeId["+i+"]\" task_id=\""+taskAssigneeList.get(i).getTaskId()+"\" "
                                + "operate_auth_ids=\""+taskAssigneeList.get(i).getOperateAuthIds()+"\" staff_members_department=\""+taskAssigneeList.get(i).getStaffMembersDepartment()+"\" "
                                + "lastlink_department=\""+taskAssigneeList.get(i).getLastlinkDepartment()+"\"" + "create_persons_job=\""+taskAssigneeList.get(i).getCreatePersonsJob()+"\" "
                                + "is_return_creater=\""+taskAssigneeList.get(i).getIsReturnCreater()+"\"></div>");
                        }
                    }
                    //读取流程模板文件
                    ClassPathResource classPathResource = new ClassPathResource("bpmnExportTemplate.html");
                    InputStream is = classPathResource .getInputStream();
                    File templateFile = FileUtil.byte2file("bpmnExportTemplate",FileUtil.toByteArray(is));
                    org.jsoup.nodes.Document doc = Jsoup.parse(templateFile, "UTF-8", "http://www.baidu.com");
                    //插入预览内容
                    doc.select("#actAljoinBpmnXmlCode").attr("value",actAljoinBpmn.getXmlCode());
                    //插入导入内容
                    doc.select("#exportData").attr("value",FileUtil.gzip(html.toString()));
                    //插入文件标记
                    doc.select("#flagId").html("aljoinBpmnExportFile");
                    String exportStr = doc.toString();
                    //下载单个文件
                    if (bpmnList.size() == 1) {
                        response.reset();
                        response.setContentType("application/force-download");
                        String bpmnName = new String((actAljoinBpmn.getProcessName()+".html").getBytes(), "ISO-8859-1"); 
                        response.setHeader("content-Disposition",
                            String.format("attachment; filename=\"%s\"", bpmnName));
                        ServletOutputStream out = response.getOutputStream();
                        try {
                            out.write(exportStr.getBytes());
                        } finally {
                            if (out != null) {
                                out.close();
                            }
                        } 
                        return;
                    } else {
                       //批量下载
                       String fileName = actAljoinBpmn.getProcessName() + ".html";
                       File file = FileUtil.byte2file(fileName, exportStr.getBytes());
                       FileUtil.doZip(file, zos);
                       FileUtil.deleteFile(file);
                    }
                }
            }
            zos.close();
        }
    }

    @Override
    public RetMsg fileImport(MultipartHttpServletRequest request) throws Exception {
        RetMsg retMsg = new RetMsg();
        HashMap<String, Object> map = new HashMap<>();
        MultipartFile mFile = request.getFile("file");
        String filename = mFile.getOriginalFilename();
        if (!filename.substring(filename.lastIndexOf(".")+1).equals("html")) {
            retMsg.setCode(1);
            retMsg.setMessage("只能上传html文件");
            return retMsg;
        }
        InputStream is = mFile.getInputStream();
        byte[] bs = FileUtil.toByteArray(is);
        File file = FileUtil.byte2file(mFile.getOriginalFilename(), bs);
        String bpmnName = mFile.getOriginalFilename().substring(0,mFile.getOriginalFilename().lastIndexOf("."));
        if (bpmnName.length()<3) {
            bpmnName = bpmnName + "导入";
        }
        File tempFile = File.createTempFile(bpmnName, ".html");
        File file2 = new File(bpmnName+".html");
        FileUtil.copyFile(file,tempFile);
        FileUtil.deleteFile(file2);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        map.put("path", tempFile.getAbsolutePath());
        map.put("fileName",bpmnName);
        retMsg.setObject(map);
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg fileSubmit(ActAljoinBpmnVO bpmn) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (StringUtils.isEmpty(bpmn.getPath())) {
            retMsg.setCode(1);
            retMsg.setMessage("文件还未上传");
            return retMsg;
        }
        //生成流程id
        String processId = "Process_"+StringUtil.randomNumAndLetter(10);
        // 首先根据流程ID或流程名称判断流程是否已经
        Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
        where.eq("process_id", processId);
        // where.or("process_name", processName);
        where.or("process_name = {0}", bpmn.getProcessName());
        if (selectCount(where) > 0) {
            retMsg.setCode(1);
            retMsg.setMessage("流程ID或名称已经存在");
            return retMsg;
        }
        Long bpmnId = null;
        String filePath = bpmn.getPath();
        File file = new File(filePath);
        //解析临时文件
        org.jsoup.nodes.Document doc = Jsoup.parse(file, "UTF-8", "http://www.baidu.com");
        if (doc != null) {
            if (doc.select("#flagId") == null  || !doc.select("#flagId").text().equals("aljoinBpmnExportFile"))  {
                retMsg.setCode(1);
                retMsg.setMessage("非本系统导出的流程文件");
                return retMsg;
            }
            //读取要导入的数据
            String importStr = doc.select("#exportData").attr("value");
            //去除空格换行符
            String dataStr = FileUtil.replaceBlank(importStr);
            //解压字符串
            String gunzip = FileUtil.gunzip(dataStr);
            org.jsoup.nodes.Document importDoc = Jsoup.parse(gunzip);
            org.jsoup.nodes.Element bpmnEle = importDoc.getElementById("bpmnId");
            if (bpmnEle != null) {
                //插入流程表数据
                ActAljoinBpmn actAljoinBpmn = new ActAljoinBpmn();
                String xmlCode = bpmnEle.attr("xml_code");
                xmlCode = FileUtil.gunzip(xmlCode);
                xmlCode = xmlCode.replace("id=\""+bpmnEle.attr("process_id")+"\" name=\""+bpmnEle.attr("process_name")+"\"", 
                    "id=\""+processId+"\" name=\""+bpmn.getProcessName()+"\"");
                
                actAljoinBpmn.setXmlCode(xmlCode);
                actAljoinBpmn.setCategoryId(Long.parseLong(bpmn.getCategoryId()));
                actAljoinBpmn.setIsActive(1);
                actAljoinBpmn.setProcessId(processId);
                actAljoinBpmn.setProcessName(bpmn.getProcessName());
                actAljoinBpmn.setProcessDesc(bpmnEle.attr("process_desc"));
                //还原htmlCode的备份数据
                String htmlCode = new String(Base64.decode(bpmnEle.attr("html_code").getBytes()));
                org.jsoup.nodes.Document htmlCodeDoc = Jsoup.parse(htmlCode);
                Elements inputs = htmlCodeDoc.getElementsByTag("input").not("input[id^='aljoin-task']");
                for (org.jsoup.nodes.Element element : inputs) {
                  element.attr("value","");
                }
                Elements options = htmlCodeDoc.getElementsByTag("option");
                for (org.jsoup.nodes.Element element : options) {
                  element.attr("value","");
                  element.removeAttr("selected");
                }
                Elements textareas = htmlCodeDoc.getElementsByTag("textarea");
                for (org.jsoup.nodes.Element element : textareas) {
                  element.html("");
                }
                htmlCode = htmlCodeDoc.body().toString();
                actAljoinBpmn.setHtmlCode(new String(Base64.encode(htmlCode.getBytes()),"UTF-8"));
                actAljoinBpmn.setHasForm(0);
                actAljoinBpmn.setIsFixed(Integer.parseInt(bpmnEle.attr("is_fixed")));
                actAljoinBpmn.setIsFree(Integer.parseInt(bpmnEle.attr("is_free")));
                actAljoinBpmn.setIsDeploy(0);
                actAljoinBpmn.setIsDeployAfterEdit(0);
                actAljoinBpmn.setIsFormEdit(Integer.parseInt(bpmnEle.attr("is_form_edit")));
                insert(actAljoinBpmn);
                bpmnId = actAljoinBpmn.getId();
                int i = 0;
                List<ActAljoinTaskAssignee> taskAssigneeList = new ArrayList<ActAljoinTaskAssignee>();
                do {
                    org.jsoup.nodes.Element taskAssigneeEle = importDoc.getElementById("taskAssigneeId["+i+"]");
                    ActAljoinTaskAssignee taskAssignee = new ActAljoinTaskAssignee();
                    taskAssignee.setTaskId(taskAssigneeEle.attr("task_id"));
                    taskAssignee.setOperateAuthIds(taskAssigneeEle.attr("operate_auth_ids"));
                    if (!"null".equals(taskAssigneeEle.attr("staff_members_department"))) {
                        taskAssignee.setStaffMembersDepartment(Integer.parseInt(taskAssigneeEle.attr("staff_members_department")));
                    }
                    if (!"null".equals(taskAssigneeEle.attr("lastlink_department"))) {
                        taskAssignee.setLastlinkDepartment(Integer.parseInt(taskAssigneeEle.attr("lastlink_department")));
                    }
                    if (!"null".equals(taskAssigneeEle.attr("create_persons_job"))) {
                        taskAssignee.setCreatePersonsJob(Integer.parseInt(taskAssigneeEle.attr("create_persons_job")));
                    }
                    if (!"null".equals(taskAssigneeEle.attr("is_return_creater"))) {
                        taskAssignee.setIsReturnCreater(Integer.parseInt(taskAssigneeEle.attr("is_return_creater")));
                    }
                    taskAssignee.setIsActive(1);
                    taskAssignee.setSignCommentWidgetIds("");
                    taskAssignee.setBpmnId(bpmnId);
                    taskAssignee.setProcessId(processId);
                    taskAssigneeList.add(taskAssignee);
                    i++;
                } while(null != importDoc.getElementById("taskAssigneeId["+i+"]"));
                actAljoinTaskAssigneeService.insertBatch(taskAssigneeList);
            }
        }
        retMsg.setCode(0);
        retMsg.setObject(String.valueOf(bpmnId));
        retMsg.setMessage("操作成功");
        //删除临时文件
        FileUtil.deleteFile(file);
        return retMsg;
    }

    @Override
    public RetMsg genExpression(String targetKey,String targetName, Integer selectType, List<ActSeetingExpressionVO> expressionList)
        throws Exception {
        RetMsg retMsg = new RetMsg();
        if(StringUtils.isEmpty(targetKey)){
            retMsg.setMessage("目标节点不能为空");
            retMsg.setCode(1);
        }
        if(null == selectType){
            retMsg.setMessage("选择类型不能为空");
            retMsg.setCode(1);
        }
        Map<String,String> resultMap = new HashMap<String,String>();
        String expression = "${";
        String displayName = "";
        String displayValue = "";

        if(1 == selectType){
            expression += "${targetTask_ =="+targetKey;
            if(StringUtils.isNotEmpty(expression)){
                displayName = "目标任务等于"+targetName;
            }
        }else{
            for(ActSeetingExpressionVO expressionVO : expressionList){
                expression += expressionVO.getId()+" "+expressionVO.getComparisonValue()+" "+expressionVO.getValue() +" "+ expressionVO.getRelationShipValue() + " ";
                displayName += expressionVO.getName()+" "+expressionVO.getComparisonName()+" "+expressionVO.getValue()+" "+expressionVO.getRelationShipName() +" ";
            }
        }

        expression +=  "}";
        displayValue = expression;

        resultMap.put("displayName",displayName);
        resultMap.put("displayValue",displayValue);
        retMsg.setObject(resultMap);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }
}
