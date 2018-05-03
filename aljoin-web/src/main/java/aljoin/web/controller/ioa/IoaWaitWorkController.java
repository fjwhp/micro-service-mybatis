package aljoin.web.controller.ioa;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aljoin.act.dao.entity.*;
import aljoin.act.iservice.*;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.object.SimpleDeptVO;
import aljoin.act.dao.object.SimpleUserVO;
import aljoin.act.dao.object.TaskVO;
import aljoin.aut.dao.entity.AutUsefulOpinion;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUsefulOpinionService;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.file.object.AljoinFile;
import aljoin.file.object.UploadParam;
import aljoin.file.service.AljoinFileService;
import aljoin.ioa.dao.object.AllTaskShowVO;
import aljoin.ioa.dao.object.FillOpinionShowVO;
import aljoin.ioa.dao.object.WaitTaskShowVO;
import aljoin.ioa.iservice.IoaWaitingWorkService;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;
import aljoin.util.StringUtil;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;
import aljoin.web.service.ioa.IoaWaitingWorkWebService;
import aljoin.web.task.WebTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 待办工作
 *
 * @author：zhongjy
 * 
 * @date：2017年10月12日 上午8:43:11
 */
@Controller
@RequestMapping(value = "/ioa/ioaWaitWork", method = RequestMethod.POST)
@Api(value = "待办工作流程操作Controller", description = "流程->待办工作")
public class IoaWaitWorkController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(IoaWaitWorkController.class);

    @Resource
    private IoaWaitingWorkService ioaWaitingWorkService;
    @Resource
    private TaskService taskService;
    @Resource
    private ActActivitiService actActivitiService;
    @Resource
    private AutUsefulOpinionService autUsefulOpinionService;
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private ActAljoinBpmnRunService actAljoinBpmnRunService;
    @Resource
    private HistoryService historyService;
    @Resource
    private WebTask webTask;
    @Resource
    private ActAljoinTaskAssigneeService actAljoinTaskAssigneeService;
    @Resource
    private ActAljoinQueryService actAljoinQueryService;
    @Resource
    private IoaWaitingWorkWebService ioaWaitingWorkWebService;
    @Resource
    private ActAljoinActivityLogService actAljoinActivityLogService;
    @Resource
    private ResResourceService resResourceService;
    @Resource
    private ActAljoinTaskSignInfoService actAljoinTaskSignInfoService;

    /**
     * 
     * 待办工作页面
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping(value = "/ioaWaitWorkPage", method = RequestMethod.GET)
    public String ioaWaitWorkPage(HttpServletRequest request) {
        return "ioa/ioaWaitWorkPage";
    }

    /**
     * 
     * 在办工作页面
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping(value = "/ioaWaitingWorkPage", method = RequestMethod.GET)
    public String ioaWaitWorkingPage(HttpServletRequest request) {
        return "ioa/ioaWaitingWorkPage";
    }

    /**
     * 
     * 待办列表(分页列表).
     *
     * @return：Page<AutUser>
     *
     * @author：zhongjy
     *
     * @date：2017年5月27日 下午4:30:09
     */
    @RequestMapping("/list")
    @ResponseBody
    @FuncObj(desc = "[协同办公]-[待办工作]-[搜索]")
    public Page<AutUser> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        AutUser obj) {
        Page<AutUser> page = null;
        try {
            // page = autUserService.list(pageBean, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 在办列表(分发).
     *
     * @return：Page<AutUser>
     *
     * @author：zhongjy
     *
     * @date：2017年5月27日 下午4:30:09
     */
    @RequestMapping("/distribution")

    @ApiImplicitParams({
        @ApiImplicitParam(name = "processInstanceId", value = "实例ID", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "htmlCode", value = "分发页面HTML代码", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "sendIds", value = "接收者ID", required = true, dataType = "string",
            paramType = "query")})
    @ResponseBody
    @FuncObj(desc = "[协同办公]-[在办工作]-[分发]")
    public RetMsg distribution(HttpServletRequest request, HttpServletResponse response, String htmlCode, String sendIds, String taskId) {
        RetMsg retMsg = new RetMsg();
        try {
            if (htmlCode == null || "".equals(htmlCode)) {
                retMsg.setCode(1);
                retMsg.setMessage("表单为空");
                return retMsg;
            }
            if (sendIds == null || "".equals(sendIds)) {
                retMsg.setCode(1);
                retMsg.setMessage("分发对象为空");
                return retMsg;
            }
            CustomUser customUser = getCustomDetail();
            Map<String, Object> map = ioaWaitingWorkWebService.distribution(htmlCode,sendIds,customUser.getUserId(),taskId);
            MaiWriteVO obj = (MaiWriteVO)map.get("maiWriteVO");
            AutUser user = new AutUser();
            user.setId(customUser.getUserId());
            user.setFullName(customUser.getNickName());
            user.setUserName(customUser.getUsername());
            webTask.maiSendTask(obj, user);
            retMsg.setCode(0);
            retMsg.setMessage("分发成功");
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.toString());
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     * 
     * 待办列表(传阅).
     *
     * @return：Page<AutUser>
     *
     * @author：huangw
     *
     * @date：2018年3月21日 下午4:30:09
     */
    @RequestMapping("/circula")

    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskId", value = "任务ID", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "processInstanceId", value = "实例ID", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "sendIds", value = "接收者ID", required = true, dataType = "string",
            paramType = "query")})
    @ResponseBody
    @FuncObj(desc = "[协同办公]-[在办工作]-[传阅]")
    public RetMsg circula(HttpServletRequest request, HttpServletResponse response, String taskId,
        String processInstanceId, String sendIds) {
        RetMsg retMsg = new RetMsg();
        try {
            if ((processInstanceId == null || "".equals(processInstanceId)) && (taskId == null || "".equals(taskId))) {
                retMsg.setCode(1);
                retMsg.setMessage("任务为空！");
                return retMsg;
            }
            if (sendIds == null || "".equals(sendIds)) {
                retMsg.setCode(1);
                retMsg.setMessage("没有选择传阅人员！");
                return retMsg;
            }
            CustomUser customUser = getCustomDetail();
            AutUser user = new AutUser();
            user.setId(customUser.getUserId());
            user.setFullName(customUser.getNickName());
            ioaWaitingWorkWebService.circulate(taskId, processInstanceId, sendIds, user);
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.toString());
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     * 
     * 打开待办工作判断.
     *
     * @return：RETMSG
     *
     * @author：huangw
     *
     * @date：2017年12月31日 下午11:30:09
     */
    @RequestMapping("/isMyTask")
    @ResponseBody
    @FuncObj(desc = "[协同办公]-[待办工作]-[是否自己的工作]")
    public RetMsg isMyTask(HttpServletRequest request, HttpServletResponse response, String taskId) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            retMsg = ioaWaitingWorkService.isMyTask(taskId, customUser.getUserId().toString());
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }

    /**
     * 
     * 打开在办工作判断.
     *
     * @return：RETMSG
     *
     * @author：huangw
     *
     * @date：2017年12月31日 下午11:30:09
     */
    @RequestMapping("/isOverTask")
    @ResponseBody
    @FuncObj(desc = "[协同办公]-[待办工作]-[是否自己的工作]")
    public RetMsg isOverTask(HttpServletRequest request, HttpServletResponse response, String pid) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            // retMsg = ioaWaitingWorkService.isMyTask(taskId,
            // customUser.getUserId().toString());
            retMsg = ioaWaitingWorkService.isOverTask(pid, customUser.getUserId().toString());
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }

    /**
     * 
     * 签收
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年5月27日 下午4:44:27
     */
    @RequestMapping("/doClaim")
    @ResponseBody
    @FuncObj(desc = "[协同办公]-[待办工作]-[签收]")
    public RetMsg doClaim(HttpServletRequest request, HttpServletResponse response, AutUser autUser) {
        RetMsg retMsg = new RetMsg();
        try {
            // autUserService.delete(autUser);
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     * 
     * 根据当前用户获取待办工作流程
     *
     * @return：List
     *
     * @author：pengsp
     *
     * @date：2017年10月16日
     */
    @RequestMapping(value = "/selectWaitTask", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据当前用户获取待办工作流程")
    public Page<WaitTaskShowVO> selectWaitTask(HttpServletRequest request, HttpServletResponse response,
        PageBean pageBean, WaitTaskShowVO obj) {
        Page<WaitTaskShowVO> page = new Page<WaitTaskShowVO>();
        try {
            CustomUser customUser = getCustomDetail();
            String userId = customUser.getUserId().toString();
            page = ioaWaitingWorkService.list(pageBean, userId, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 根据当前用户获取在办工作流程
     *
     * @return：List
     *
     * @author：pengsp
     *
     * @date：2017年10月16日
     */
    @RequestMapping(value = "/selectWaitingTask", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "根据当前用户获取待办工作流程")
    public Page<WaitTaskShowVO> selectWaitingTask(HttpServletRequest request, HttpServletResponse response,
        PageBean pageBean, WaitTaskShowVO obj) {
        Page<WaitTaskShowVO> page = new Page<WaitTaskShowVO>();
        try {
            CustomUser customUser = getCustomDetail();
            String userId = customUser.getUserId().toString();
            page = ioaWaitingWorkService.waitingList(pageBean, userId, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 签收
     *
     * @return：List
     *
     * @author：pengsp
     *
     * @date：2017年10月16日
     */
    @RequestMapping(value = "/claimTask", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "任务签收")
    public RetMsg claimTask(HttpServletRequest request, HttpServletResponse response, String ids) {
        RetMsg retMsg = new RetMsg();
        if (StringUtils.isEmpty(ids)) {
            retMsg.setCode(1);
            retMsg.setMessage("请选择要签收的任务文件");
            return retMsg;
        }

        try {
            CustomUser customUser = getCustomDetail();
            List<String> taskIds = Arrays.asList(ids.split(","));
            if (taskIds != null) {
                for (String string : taskIds) {
                    retMsg = ioaWaitingWorkService.isMyTask(string, customUser.getUserId().toString());
                    if (retMsg.getCode() != 0) {
                        return retMsg;
                    }
                }

            }
            String userId = customUser.getUserId().toString();
            retMsg = ioaWaitingWorkService.claimTask(userId, ids);
        } catch (ActivitiTaskAlreadyClaimedException e) {
            retMsg.setCode(1);
            retMsg.setMessage("任务已被他人签收");
            return retMsg;
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     * 
     * 输出流程图
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年9月11日 下午5:34:17
     */
    @RequestMapping(value = "/showImg", method = RequestMethod.GET)
    @FuncObj(desc = "[协同办公]-[待办工作]-[流程图]")
    public void showImg(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.addHeader("Pragma", "No-cache");
            response.addHeader("Cache-Control", "no-cache");
            response.addDateHeader("expires", 0);
            // InputStream is =
            // actActivitiService.getImageInputStream(obj.getProcessId(), 0);
            InputStream is = actActivitiService.getRuImageInputStream(request.getParameter("processInstanceId"), true);
            OutputStream os = response.getOutputStream();
            BufferedImage image = ImageIO.read(is);
            ImageIO.write(image, "PNG", os);
            os.flush();
            os.close();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 
     * 输出流程图
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年9月11日 下午5:34:17
     */
    @RequestMapping(value = "/showHisImg", method = RequestMethod.GET)
    @FuncObj(desc = "[协同办公]-[待办工作]-[流程图]")
    public void showHisImg(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.addHeader("Pragma", "No-cache");
            response.addHeader("Cache-Control", "no-cache");
            response.addDateHeader("expires", 0);
            // InputStream is =
            // actActivitiService.getImageInputStream(obj.getProcessId(), 0);
            InputStream is = actActivitiService.getHisImageInputStream(request.getParameter("processInstanceId"), "");
            OutputStream os = response.getOutputStream();
            BufferedImage image = ImageIO.read(is);
            ImageIO.write(image, "PNG", os);
            os.flush();
            os.close();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 
     * 
     * 意见列表
     *
     * @return：String
     *
     * @author：pengsp
     *
     * @date：2017年9月11日 下午5:34:17
     */
    @RequestMapping(value = "/fillOpinion")
    @ResponseBody
    @FuncObj(desc = "[协同办公]-[意见]")
    public List<FillOpinionShowVO> fillOpinion(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String isAll = request.getParameter("isAll");

        CustomUser customUser = getCustomDetail();

        List<FillOpinionShowVO> result = new ArrayList<FillOpinionShowVO>();
        Where<AutUsefulOpinion> where = new Where<AutUsefulOpinion>();
        where.setSqlSelect("content");
        if ("0".equals(isAll)) {
            where.eq("user_id", customUser.getUserId());
        }
        where.orderBy("content_rank,id", true);
        List<AutUsefulOpinion> list = autUsefulOpinionService.selectList(where);

        SimpleDateFormat str = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currDate = str.format(new Date());

        String fullName = autUserService.selectById(customUser.getUserId()).getFullName();
        for (AutUsefulOpinion autUsefulOpinion : list) {
            FillOpinionShowVO fillOpinionShowVO = new FillOpinionShowVO();
            fillOpinionShowVO.setContent(autUsefulOpinion.getContent());
            fillOpinionShowVO.setCurrDate(currDate);
            fillOpinionShowVO.setFullName(fullName);
            result.add(fillOpinionShowVO);
        }
        return result;
    }

    /**
     * 
     * 综合查询
     *
     * @return：Page<AllTaskShowVO>
     *
     * @author：pengsp
     *
     * @date：2017-10-28
     */
    @RequestMapping("/getAllTask")
    @ResponseBody
    public Page<AllTaskShowVO> getAllTask(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        AllTaskShowVO obj, String startTime, String endTime, String qBpmn) {
        Page<AllTaskShowVO> page = new Page<AllTaskShowVO>();
        try {
            Map<String, String> map = new HashMap<String, String>();
            CustomUser customUser = getCustomDetail();
            if (customUser == null) {
                return page;
            }
            map.put("userId", customUser.getUserId().toString());
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            if (qBpmn != null && qBpmn.length() > 0) {
                obj.setFormType("");
            }
            map.put("qBpmn", qBpmn);
            page = ioaWaitingWorkService.getAllTask(pageBean, obj, map);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 表单文件上传
     *
     * @return：RetMsg
     *
     * @author：pengsp
     *
     * @date：2017-11-01
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public RetMsg upload(MultipartHttpServletRequest request,String fileModuleName) {
        RetMsg retMsg = new RetMsg();
        try {
            List<MultipartFile> files = request.getFiles(request.getParameter("name"));
            // 设置文件
            List<AljoinFile> fileList = new ArrayList<AljoinFile>();
            // 构造参数对象
            UploadParam uploadParam = new UploadParam();
            // 调通用接口，获取参数
            Map<String, String> map = sysParameterService.allowFileType();
            // 最大允许上传的文件大小（单位是kb 1024byte=1KB）
            Long limitSize = Long.parseLong(map.get("limitSize"));
            uploadParam.setMaxSize(limitSize*1024);
            //允许上传的类型
            String allowType = map.get("allowType");
            String[] typeArr = allowType.split("\\|");
            uploadParam.setAllowTypeList(Arrays.asList(typeArr));
            for (MultipartFile file : files) {
                fileList.add(new AljoinFile(file.getBytes(), file.getOriginalFilename(), file.getSize()));
            }
            uploadParam.setFileList(fileList);
            retMsg = resResourceService.upload(uploadParam,fileModuleName);
            
            List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
            if (null != retMsg && null != retMsg.getCode()) {
                if (retMsg.getCode() == 0 && null != retMsg.getObject()) {
                    List<ResResource> resultList = (ArrayList<ResResource>)retMsg.getObject();
                    if (null != resultList && !resultList.isEmpty()) {
                        for (ResResource result : resultList) {
                            Map<String, Object> resultMap = new HashMap<String, Object>();
                            resultMap.put("type", result.getFileType());
                            resultMap.put("groupName", result.getGroupName());
                            resultMap.put("fileName", result.getFileName());
                            result.setFileDesc("协同办公表单附件上传");
                            resultMap.put("waitWorkAttachId", String.valueOf(result.getId()));
                            listMap.add(resultMap);
                        }
                    }
                } else if (retMsg.getCode() == 1 && null != retMsg.getMessage()) {
                    retMsg.setCode(retMsg.getCode());
                    retMsg.setMessage(retMsg.getMessage());
                    return retMsg;
                }
            }
            retMsg.setCode(0);
            retMsg.setObject(listMap);
            retMsg.setMessage("操作成功");
        } catch (Exception e) {
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     *
     * 根据任务ID查询下一个流程节点信息
     *
     * @return：RetMsg
     *
     * @author：pengsp
     *
     * @date：2017-11-06
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getNextTaskInfo", method = RequestMethod.GET)
    @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query")
    @ResponseBody
    @ApiOperation("根据任务ID查询下一个流程提交信息")
    public RetMsg getNextTaskInfo(HttpServletRequest request, HttpServletResponse response) {
        RetMsg retMsg = new RetMsg();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Map<String, Object> runMap = new HashMap<String, Object>();
        try {
            String taskId = request.getParameter("taskId");
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if(null == task){
                retMsg.setCode(1);
                retMsg.setMessage("任务已被处理");
                return retMsg;
            }
            String formId = request.getParameter("formId");
            String bpmnId = request.getParameter("bpmnId");
            String formWidgetIds = request.getParameter("formWidgetIds");
            String[] formWidgetIdsArr = formWidgetIds.split(",");
            String result = "";
            // 表单参数检查
            for (int i = 0; i < formWidgetIdsArr.length; i++) {
                if (StringUtils.isNotEmpty(formWidgetIdsArr[i])) {
                    String paramValue = request.getParameter(formWidgetIdsArr[i]);
                    if (paramValue == null) {
                        throw new Exception("传参有问题：没有" + formWidgetIdsArr[i] + "参数");
                    }
                    result += formWidgetIdsArr[i] + "=" + URLEncoder.encode(paramValue, "UTF-8") + "&";
                    runMap.put(formWidgetIdsArr[i], paramValue);
                }
            }
            result = "formId=" + formId + "&" + "bpmnId=" + bpmnId + "&" + result + "formWidgetIds=" + formWidgetIds;
            paramMap.put("result", result);

            ActAljoinBpmnRun actAljoinBpmnRun = actAljoinBpmnRunService.selectById(bpmnId);
            runMap.put("isFree", actAljoinBpmnRun.getIsFree());
            runMap.put("actAljoinBpmnRun",actAljoinBpmnRun);
            Map<String, Object> map = ioaWaitingWorkService.getNextTaskInfo(taskId, runMap);
            paramMap.put("taskKey", map.get("taskKey"));
            paramMap.put("taskName", map.get("taskName"));
            paramMap.put("isTask", map.get("isTask"));
            paramMap.put("isFinishMergeTask", map.get("isFinishMergeTask"));
            paramMap.put("isBackOwner", map.get("isBackOwner"));
            paramMap.put("isSubmit", map.get("isSubmit"));
            paramMap.put("isAddSign",map.get("isAddSign"));
            paramMap.put("isPass",map.get("isPass"));

            String handler = (String)map.get("handler");
            if(!StringUtils.isEmpty(handler)){
                Where<AutUser> autUserWhere = new Where<AutUser>();
                autUserWhere.eq("id",Long.valueOf(handler));
                autUserWhere.eq("is_active", 1);
                autUserWhere.setSqlSelect("id,full_name");
                AutUser user = autUserService.selectOne(autUserWhere);
                handler = task.getName() + " [ 办理人："+user.getFullName()+" ]";
            }
            paramMap.put("handler", handler);
            // 是否多实例节点
            paramMap.put("isMultiTask", map.get("isMultiTask"));
            // 该任务完成后是否满足进入下一个节点的条件（控制是否让其选择下一节点意见下一节点的班里人）
            paramMap.put("isMultiTaskCondition", map.get("isMultiTaskCondition"));

            // 1.任务类型
            Map<String, String> taskTypeMap = (Map<String, String>)map.get("taskTypeMap");
            // 2.打开类型(放到上层处理)
            // Map<String, String> openTypeMap = map.get("isFinishMergeTask");
            // 3.受理人
            Map<String, String> assigneeIdMap = (Map<String, String>)map.get("assigneeIdMap");
            // 4.当前任务部门列表（仅含部门下的用户，不包括用户和分组）
            Map<String, Map<String, List<String>>> deptListMap =
                (Map<String, Map<String, List<String>>>)map.get("deptListMap");

            // 所有分组ID(含部门，角色，岗位)
            Map<String, Set<String>> allGroupSetMap = (Map<String, Set<String>>)map.get("allGroupSetMap");
            // 所有候选用户ID（仅含候选用户，不含办理人）
            Map<String, Set<String>> candidateUserIdSetMap = (Map<String, Set<String>>)map.get("candidateUserIdSetMap");

            // 分为四类：1-自由选择节点(逗号分隔-构造成下拉)，2-排他节点(节点列表)，3-自由节点(逗号分隔-构造成下拉)，4-并行节点(节点列表)
            paramMap.put("processType", map.get("processType"));
            int processType = (int)map.get("processType");

            // 非部门用户ID列表
            Map<String, Set<String>> unDeptUserIdSetMap = (Map<String, Set<String>>)map.get("unDeptUserIdSetMap");
            // 查询用户部门

            List<TaskVO> taskVOList = new ArrayList<TaskVO>();
            String taskKeyStr = (String)map.get("taskKey");
            String taskNameStr = (String)map.get("taskName");
            String[] taskKeyStrArr = taskKeyStr.split(",");
            String[] taskNameStrArr = taskNameStr.split(",");

            // 获取所有流程节点，判断是否配置返回流程发起人
            Set<String> returnCreaterTaskKeySet = new HashSet<String>();
            // 创建人所在部门人员
            Set<String> staffMembersDepartmentTaskKeySet = new HashSet<String>();
            // 上一个环节办理人人所在部门人员
            Set<String> lastlinkDepartmentTaskKeySet = new HashSet<String>();
            // 创建人所在岗位人员办理
            Set<String> createPersonsJobTaskKeySet = new HashSet<String>();
            AutUser processCreater = null;
            Where<ActAljoinTaskAssignee> assigneeWhere = new Where<ActAljoinTaskAssignee>();
            assigneeWhere.in("task_id", taskKeyStrArr);
            assigneeWhere.eq("is_active", 1);
            assigneeWhere.eq("bpmn_id", actAljoinBpmnRun.getOrgnlId());
            assigneeWhere.eq("version", actAljoinBpmnRun.getTaskAssigneeVersion());
            assigneeWhere.setSqlSelect(
                "task_id,is_return_creater,staff_members_department,lastlink_department,create_persons_job");
            List<ActAljoinTaskAssignee> actAljoinTaskAssigneeList =
                actAljoinTaskAssigneeService.selectList(assigneeWhere);
            for (ActAljoinTaskAssignee actAljoinTaskAssignee : actAljoinTaskAssigneeList) {
                if (null != actAljoinTaskAssignee) {

                    if (null != actAljoinTaskAssignee.getIsReturnCreater()
                        && actAljoinTaskAssignee.getIsReturnCreater().equals(1)) {
                        returnCreaterTaskKeySet.add(actAljoinTaskAssignee.getTaskId());
                    }
                    if (null != actAljoinTaskAssignee.getStaffMembersDepartment()
                        && actAljoinTaskAssignee.getStaffMembersDepartment().equals(1)) {
                        staffMembersDepartmentTaskKeySet.add(actAljoinTaskAssignee.getTaskId());
                    }
                    if (null != actAljoinTaskAssignee.getLastlinkDepartment()
                        && actAljoinTaskAssignee.getLastlinkDepartment().equals(1)) {
                        lastlinkDepartmentTaskKeySet.add(actAljoinTaskAssignee.getTaskId());
                    }
                    if (null != actAljoinTaskAssignee.getCreatePersonsJob()
                        && actAljoinTaskAssignee.getCreatePersonsJob().equals(1)) {
                        createPersonsJobTaskKeySet.add(actAljoinTaskAssignee.getTaskId());
                    }
                }
            }

            // 对于非自由流程（不可选择节点类的）返回以下数据
            if (processType == 2 || processType == 4 ) {
                Map<String, Set<String>> userIdsSetMap = (Map<String, Set<String>>)map.get("userIdsSetMap");

                // 流程实例id
                String currentProcessInstanceId = (String)map.get("currentProcessInstanceId");

                //记录当前流程发起人ID
                Long createId = null;
                // 到流程查询表，查询流程发起人(如果下一步节点含有返回发起人的配置才去查询流程的发起人)
                if (returnCreaterTaskKeySet.size() > 0) {
                    Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
                    queryWhere.eq("process_instance_id", currentProcessInstanceId);
                    ActAljoinQuery query = actAljoinQueryService.selectOne(queryWhere);
                    if (query != null) {
                        processCreater = new AutUser();
                        processCreater.setId(query.getCreateUserId());
                        processCreater.setFullName(query.getCreateFullUserName());
                    }
                    createId = query.getCreateUserId();
                }

                for (int i = 0; i < taskKeyStrArr.length; i++) {
                    TaskVO tvo = new TaskVO();
                    tvo.setTaskKey(taskKeyStrArr[i]);
                    tvo.setTaskName(taskNameStrArr[i]);

                    Set<String> userIdSet = userIdsSetMap.get(taskKeyStrArr[i]);
                    Where<AutUser> autUserWhere = new Where<AutUser>();
                    autUserWhere.in("id", userIdSet.size() == 0 ? Arrays.asList(-1) : userIdSet);
                    autUserWhere.eq("is_active", 1);
                    autUserWhere.setSqlSelect("id,full_name");
                    List<AutUser> autUserList = autUserService.selectList(autUserWhere);
                    // 查询用户id和名称
                    List<SimpleUserVO> suvoList = new ArrayList<SimpleUserVO>();
                    SimpleUserVO suvo = null;
                    Map<Long, SimpleUserVO> allUserMap = new HashMap<Long, SimpleUserVO>();

                    // 标志候选用户中是否已经含有流程发起人
                    int flag = 0;
                    for (AutUser autUser : autUserList) {
                        SimpleUserVO vo = new SimpleUserVO();
                        vo.setUserId(autUser.getId().toString());
                        vo.setUserName(autUser.getFullName());
                        if (returnCreaterTaskKeySet.contains(taskKeyStrArr[i]) && processCreater != null
                            && processCreater.getId().longValue() == autUser.getId().longValue()) {
                            // 如果当前节点是有配置返回流程发起人,并且当前用户是流程发起人，则设置标记用户为流程发起人
                            vo.setIsCreater("1");
                            flag = 1;
                        } else {
                            vo.setIsCreater("0");
                        }
                        suvoList.add(vo);

                        // 检查办理人是否在查询出来的所有处理人的集合中（其实检查其是否合法）
                        String assigneeUserId = assigneeIdMap.get(taskKeyStrArr[i]);
                        if (StringUtils.isNotEmpty(assigneeUserId)
                            && autUser.getId().toString().equals(assigneeUserId)) {
                            suvo = vo;
                        }
                        // 构造map，下面需要用到
                        allUserMap.put(autUser.getId(), vo);
                    }
                    
                    Set<String> uset = unDeptUserIdSetMap.get(taskKeyStrArr[i]);
                    int hasOtherUser = 0;
                    if (flag == 0 && returnCreaterTaskKeySet.contains(taskKeyStrArr[i]) && processCreater != null) {
                        // 如果在候选人中没有流程发起人，并且当前节点是有配置返回流程发起人，并且能找到流程发起人
                        SimpleUserVO vo = new SimpleUserVO();
                        vo.setUserId(processCreater.getId().toString());
                        vo.setUserName(processCreater.getFullName());
                        vo.setIsCreater("1");
                        suvoList.add(vo);
                        uset.add(processCreater.getId().toString());
                        hasOtherUser = 1;
                        allUserMap.put(processCreater.getId(), vo);
                    }
                    List<SimpleDeptVO> deptList = new ArrayList<SimpleDeptVO>();
                    Long currentUserId = getCustomDetail().getUserId();
                    // 拓展环节选择方式
                    List<Long> userIdList = ioaWaitingWorkWebService.getSelectUserIdList(currentProcessInstanceId,taskId,staffMembersDepartmentTaskKeySet,lastlinkDepartmentTaskKeySet,createPersonsJobTaskKeySet,currentUserId);
                    suvoList = ioaWaitingWorkWebService.extSelectionForUserList(suvoList,userIdList,createId);

                    tvo.setUserList(suvoList);

                    // 构造deptList
                    Map<String, List<String>> taskDeptMap = deptListMap.get(taskKeyStrArr[i]);

                    for (Map.Entry<String, List<String>> entry : taskDeptMap.entrySet()) {
                        // 部门ID
                        String key = entry.getKey();
                        // 部门下的用户id列表
                        List<String> value = entry.getValue();

                        List<SimpleUserVO> deptUserList = new ArrayList<SimpleUserVO>();
                        for (String s : value) {
                            if (allUserMap.get(Long.parseLong(s)) != null) {
                                deptUserList.add(allUserMap.get(Long.parseLong(s)));
                            }
                        }
                        SimpleDeptVO sdv = new SimpleDeptVO();
                        sdv.setDeptId(key);
                        sdv.setUserList(deptUserList);

                        deptList.add(sdv);
                    }
                    String openType = null;

                    // 拓展环节选择方式
                    Map<String,Object> deptMap = ioaWaitingWorkWebService.getSelectDeptIdList(currentProcessInstanceId,taskId,staffMembersDepartmentTaskKeySet,lastlinkDepartmentTaskKeySet,createPersonsJobTaskKeySet,createId,currentUserId);
                    deptList =  ioaWaitingWorkWebService.extSelectionForDeptList(deptList,deptMap);
                    tvo.setUnDeptUserSet(null);
                    tvo.setDeptList(deptList);

                    if (allUserMap.size() == 0 && hasOtherUser == 0) {
                        // 没有用户处理，弹出机构树
                        openType = "1";

                    } else if (suvo != null && allGroupSetMap.get(taskKeyStrArr[i]).size() == 0
                        && candidateUserIdSetMap.get(taskKeyStrArr[i]).size() == 0 && hasOtherUser == 0) {
                        // 有受理人,灭有选择部门角色岗位,也没有选择候选人,也没有选择返回流程发起人
                        openType = "2";
                    } else if (taskDeptMap.size() > 0 || deptList.size() > 0) {
                        // 有选择部门，弹出机构树
                        openType = "3";
                    } else {
                        openType = "4";
                    }

                    tvo.setOpenType(openType);
                    tvo.setTaskType(taskTypeMap.get(taskKeyStrArr[i]));
                    tvo.setDefaultAssigneeUser(suvo);


                    taskVOList.add(tvo);
                }
                paramMap.put("taskVOList", taskVOList);
            }
            retMsg.setCode(0);
            retMsg.setObject(paramMap);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }

    /**
     *
     * 回退
     *
     * @return：RetMsg
     *
     * @author：pengsp
     *
     * @date：2017-11-10
     */
    @RequestMapping(value = "/jumpTask")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskId", value = "任务ID(当前任务)", required = true, dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = "targetTaskKey", value = "目标任务key", required = true, dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = "targetUserId", value = "目标任务办理人ID", required = false, dataType = "string",
            paramType = "query")

    })
    @ResponseBody
    @ApiOperation("回退")
    public RetMsg jumpTask(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataRun entity) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            String taskId = request.getParameter("taskId");
            String targetTaskKey = request.getParameter("targetTaskKey");
            String targetUserId = request.getParameter("targetUserId");
            String thisTaskUserComment = request.getParameter("thisTaskUserComment");

            String isTask = request.getParameter("isTask");
            String nextNode = request.getParameter("nextNode");

            // 获取表单参数
            String formWidgetIds = request.getParameter("formWidgetIds");
            String[] formWidgetIdsArr = formWidgetIds.split(",");
            Map<String, String> paramMap = new HashMap<String, String>();
            for (int i = 0; i < formWidgetIdsArr.length; i++) {
                String paramValue = request.getParameter(formWidgetIdsArr[i]);
                if (paramValue == null) {
                    throw new Exception("传参有问题：没有" + formWidgetIdsArr[i] + "参数");
                }
                entity.setOperateUserId(customUser.getUserId());
                paramMap.put(formWidgetIdsArr[i], paramValue);
            }
            retMsg = ioaWaitingWorkWebService.jumpTask(taskId, targetTaskKey, targetUserId, thisTaskUserComment,
                getCustomDetail(), paramMap, entity, isTask, nextNode);
            if (null != retMsg.getObject()) {
                String processInstanceId = (String)retMsg.getObject();
                AutUser user = new AutUser();
                user.setId(customUser.getUserId());
                user.setUserName(customUser.getUsername());
                user.setFullName(customUser.getNickName());
                webTask.sendOnlineMsg(processInstanceId, targetUserId, user);
            }
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }

    /**
     *
     * 根据任务id获取用户
     *
     * @return：RetMsg
     *
     * @author：pengsp
     *
     * @date：2017-11-10
     */
    @RequestMapping(value = "/getTaskUser")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "nextTaskKey", value = "下个任务KEY", required = true, dataType = "string",
            paramType = "query")})
    @ResponseBody
    @ApiOperation("根据任务id获取用户")
    public RetMsg getTaskUser(HttpServletRequest request, HttpServletResponse response) {
        String taskId = request.getParameter("taskId");
        String nextTaskKey = request.getParameter("nextTaskKey");
        RetMsg retMsg = new RetMsg();
        Map<String, Object> userMap = new HashMap<String, Object>();
        try {
            userMap = ioaWaitingWorkService.getTaskUser(taskId, nextTaskKey,getCustomDetail().getUserId());
            retMsg.setCode(0);
            retMsg.setObject(userMap);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }

    /**
     * 
     * 退回操作获得上一节点信息：可以退回的允许情况如下： （1）自由流程可以退回（2）对于非自由流程， 当前活动节点只有一个并且上级节点只有一个的可以退回
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年11月28日 下午8:18:14
     */
    @RequestMapping(value = "/getPreTaskInfoOld")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query")})
    @ResponseBody
    @ApiOperation("获得上一节点信息(通过流程轨迹)")
    public RetMsg getPreTaskInfoOld(HttpServletRequest request, HttpServletResponse response) {
        String taskId = request.getParameter("taskId");
        String bpmnId = request.getParameter("bpmnId");
        RetMsg retMsg = new RetMsg();
        Map<String, String> retMap = new HashMap<String, String>();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        try {
            if (StringUtils.isEmpty(taskId)) {
                throw new Exception("无上级节点，不能退回");
            }
            ActAljoinBpmnRun actAljoinBpmnRun = actAljoinBpmnRunService.selectById(bpmnId);
            // ActAljoinBpmn bpmn = actAljoinBpmnService.selectById(bpmnId);
            if (actAljoinBpmnRun.getIsFree() == 1) {
                // （1）自由流程可以退回
                List<HistoricActivityInstance> activities =
                    historyService.createHistoricActivityInstanceQuery().processInstanceId(task.getProcessInstanceId())
                        .finished().orderByHistoricActivityInstanceEndTime().desc().list();
                if (activities.size() > 0) {
                    // 如果第一个意见完成的节点没有办理人（被撤回的操作）
                    int i = 0;
                    // 避免出现连续的两次撤回操作上级节点错误
                    while (StringUtils.isEmpty(activities.get(i).getAssignee())
                        && !activities.get(i).getActivityId().startsWith("StartEvent_")
                        && !activities.get(i).getTaskId().equals(task.getId())) {
                        i += 2;
                    }
                    if (activities.size() > i) {
                        retMap.put("activityKey", activities.get(i).getActivityId());
                        retMap.put("activityName", activities.get(i).getActivityName());
                        retMap.put("userId", activities.get(i).getAssignee());
                        if (StringUtils.isNotEmpty(activities.get(i).getAssignee())) {
                            AutUser user = autUserService.selectById(Long.parseLong(activities.get(i).getAssignee()));
                            retMap.put("userFullName", user.getFullName());
                        }
                    }
                } else {
                    throw new Exception("无上级节点，不能退回");
                }
            } else {
                // （2）对于非自由流程，当前活动节点只有一个并且上级节点只有一个的可以退回
                // 获取上一个节点(有可能有多个)
                List<TaskDefinition> defList =
                    actActivitiService.getPreTaskInfo2(task.getTaskDefinitionKey(), task.getProcessInstanceId());
                if (defList.size() != 1) {
                    throw new Exception("流程不满足退回条件:无或含有多个上级任务节点");
                } else {
                    // 在判断当前流程实例是否同时存在活动节点
                    // List<Task> taskList =
                    // taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
                    List<Task> taskList = taskService.createTaskQuery().executionId(task.getExecutionId()).list();
                    if (taskList.size() == 1) {
                        // 满足条件
                        retMap.put("activityKey", defList.get(0).getKey());
                        retMap.put("activityName", defList.get(0).getNameExpression().getExpressionText());
                        retMap.put("userId", defList.get(0).getAssigneeExpression().getExpressionText());
                        if (null != defList.get(0).getAssigneeExpression()) {
                            AutUser user =
                                autUserService.selectById(defList.get(0).getAssigneeExpression().getExpressionText());
                            retMap.put("userFullName", user.getFullName());
                        }
                        // 查询出是谁办理了的--本来是查流程图中上一节点，再查上一节点办理人，在撤回操作过后，这里会出错，改为直接查上一步办理人，下面的代码就不要
                        // List<HistoricTaskInstance> hisTaskList =
                        // historyService
                        // .createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId())
                        // .taskDefinitionKey(defList.get(0).getKey()).orderByHistoricTaskInstanceEndTime()
                        // .desc().list();
                        // if (hisTaskList.size() == 0) {
                        // // 如果是任务重找不到通过连线关系的上级节点，就到历史活动节点查新
                        // List<HistoricActivityInstance> activityInstanceList =
                        // historyService.createHistoricActivityInstanceQuery()
                        // .processInstanceId(task.getProcessInstanceId()).activityType("userTask")
                        // .finished().orderByHistoricActivityInstanceEndTime().desc().list();
                        // if (activityInstanceList.size() > 0) {
                        // HistoricActivityInstance activityInstance =
                        // activityInstanceList.get(0);
                        // retMap.put("activityKey",
                        // activityInstance.getActivityId());
                        // retMap.put("activityName",
                        // activityInstance.getActivityName());
                        // retMap.put("userId", activityInstance.getAssignee());
                        // if
                        // (StringUtils.isNotEmpty(activityInstance.getAssignee()))
                        // {
                        // AutUser user =
                        // autUserService.selectById(activityInstance.getAssignee());
                        // retMap.put("userFullName", user.getFullName());
                        // }
                        // }
                        // } else {
                        // // 通过连线获取的上级任务
                        // HistoricTaskInstance hisTask = hisTaskList.get(0);
                        // retMap.put("userId", hisTask.getAssignee());
                        // if (StringUtils.isNotEmpty(hisTask.getAssignee())) {
                        // AutUser user =
                        // autUserService.selectById(hisTask.getAssignee());
                        // retMap.put("userFullName", user.getFullName());
                        // }
                        // }
                    } else if (taskList.size() > 1) {
                        throw new Exception("流程不满足退回条件:含有多个真正办理的任务节点");
                    } else {
                        throw new Exception("无上级节点，不能退回");
                    }
                }

            }
            if (retMap.get("activityKey") != null && retMap.get("activityKey").startsWith("StartEvent_")) {
                throw new Exception("不允许退回到开始");
            }
            // 对于多实例节点：当前节点是多实例不能退回，上个节点是多实例，也不能退回
            MultiInstanceLoopCharacteristics currentMulti =
                actActivitiService.getMultiInstance(task.getProcessInstanceId(), task.getTaskDefinitionKey());
            MultiInstanceLoopCharacteristics preMulti = null;
            if (retMap.get("activityKey") != null) {
                preMulti = actActivitiService.getMultiInstance(task.getProcessInstanceId(), retMap.get("activityKey"));
            }
            if (currentMulti != null || preMulti != null) {
                throw new Exception("多实例(会签)节点不满足退回条件");
            }

            retMsg.setCode(0);
            retMsg.setObject(retMap);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    @SuppressWarnings("rawtypes")
    public SortedMap getSign(String fileNames, String fileModuleName) {
        // 模块名+文件名+UPLOAD_KEY进行数据签名，校验文件上传的合法性
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("fileModuleName", fileModuleName);
        parameters.put("fileName", fileNames);
        parameters.put("date", DateUtil.date2str(new Date()));
        String calculationSign = StringUtil.getSign(parameters, WebConstant.UPLOAD_SECRET);
        parameters.put("calculationSign", calculationSign);
        return parameters;
    }

    /**
     * 
     * @描述：退回操作获得上一节点信息：可以退回的允许情况如下： （1）自由流程可以退回（2）对于非自由流程， 当前活动节点只有一个并且上级节点只有一个的可以退回
     *
     * @返回：RetMsg
     *
     * @作者：zhongjy
     *
     * @时间：2017年2月28日 下午8:18:14
     */
    @RequestMapping(value = "/getPreTaskInfo")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query")})
    @ResponseBody
    @ApiOperation("获得上一节点信息(通过流程轨迹)")
    public RetMsg getPreTaskInfo(HttpServletRequest request, HttpServletResponse response) {
        // 当前任务节点id
        String taskId = request.getParameter("taskId");
        String bpmnId = request.getParameter("bpmnId");
        RetMsg retMsg = new RetMsg();
        Map<String, String> retMap = new HashMap<String, String>();
        try {
            if (StringUtils.isEmpty(taskId)) {
                throw new Exception("无上级节点，不能退回");
            }
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            // 当前任务key
            String currentTaskKey = task.getTaskDefinitionKey();
            String currentExcutionId = task.getExecutionId();
            // 判断该节点是否是多实例节点：当前节点是多实例不能退回，上个节点是多实例，也不能退回
            MultiInstanceLoopCharacteristics currentMulti =
                actActivitiService.getMultiInstance(task.getProcessInstanceId(), task.getTaskDefinitionKey());
            if (currentMulti != null) {
                throw new Exception("当前节点是多实例(会签)节点不满足退回条件");
            }

            ActAljoinBpmnRun actAljoinBpmnRun = actAljoinBpmnRunService.selectById(bpmnId);
            List<HistoricActivityInstance> activities =
                historyService.createHistoricActivityInstanceQuery().processInstanceId(task.getProcessInstanceId())
                    .finished().orderByHistoricActivityInstanceEndTime().desc().list();
            // 检查是否满足退回条件
            if (activities.size() == 0) {
                throw new Exception("无上级节点，不能退回");
            }
            Where<ActAljoinActivityLog> logWhere = new Where<ActAljoinActivityLog>();
            // logWhere.setSqlSelect(
            // "id,operate_user_id,operate_full_name,operate_time,operate_status,receive_user_ids,receive_full_names,comment,last_task_name,last_task_id,current_task_name,current_task_def_key,current_task_id,next_task_def_key,next_task_name,proc_inst_id");
            logWhere.eq("proc_inst_id", task.getProcessInstanceId());
            logWhere.ne("operate_status", WebConstant.PROCESS_OPERATE_STATUS_6).ne("operate_status", WebConstant.PROCESS_OPERATE_STATUS_7).ne("operate_status", WebConstant.PROCESS_OPERATE_STATUS_5);
            logWhere.orderBy("operate_time", false);
            List<ActAljoinActivityLog> actAljoinActivityLogList = actAljoinActivityLogService.selectList(logWhere);
            if (actAljoinActivityLogList.size() == 0) {
                throw new Exception("无上级节点，不能退回");
            }

            // 上一节点信息
            ActAljoinActivityLog lastLog = null;
            // 已回退节点
            if (actAljoinBpmnRun.getIsFree() == 1) {
                for (ActAljoinActivityLog actAljoinActivityLog : actAljoinActivityLogList) {
                    if (actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_1)
                        && actAljoinActivityLog.getCurrentTaskDefKey().equals(currentTaskKey)) {
                        // preTaskKey = actAljoinActivityLog.getLastTaskDefKey();
                        // preTaskId = actAljoinActivityLog.getLastTaskId();
                        // preExcutionId = actAljoinActivityLog.getExecutionId();
                        lastLog = new ActAljoinActivityLog();
                        lastLog = actAljoinActivityLog;
                        Integer count = 0;
                        for (ActAljoinActivityLog activityLog : actAljoinActivityLogList) {
                            // 在提交动作之后
                            if (activityLog.getOperateTime().after(actAljoinActivityLog.getOperateTime())) {
                                // 该节点提交到当前节点
                                if (activityLog.getLastTaskDefKey().equals(lastLog.getLastTaskDefKey())
                                    && activityLog.getCurrentTaskDefKey().equals(currentTaskKey)
                                    && activityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_1)) {
                                    count += 1;
                                }
                                // 当前节点退回或撤回到该节点
                                if (activityLog.getLastTaskDefKey().equals(currentTaskKey)
                                    && activityLog.getCurrentTaskDefKey().equals(lastLog.getLastTaskDefKey())
                                    && !activityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_1)) {
                                    count -= 1;
                                }
                            }
                        }
                        if (count >= 0) {
                            break;
                        }
                    }
                }
            } else {
                for (ActAljoinActivityLog actAljoinActivityLog : actAljoinActivityLogList) {
                    if (actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_1)
                        && actAljoinActivityLog.getCurrentTaskDefKey().equals(currentTaskKey)) {
                        // preTaskKey = actAljoinActivityLog.getLastTaskDefKey();
                        // preTaskId = actAljoinActivityLog.getLastTaskId();
                        lastLog = actAljoinActivityLog;
                        break;
                    }
                }
            }
            if (null == lastLog) {
                throw new Exception("没有找到可以回退的历史环节");
            }

            String lastExcutionId = "";
            if (actAljoinBpmnRun.getIsFree() != 1) {
                for (ActAljoinActivityLog actAljoinActivityLog : actAljoinActivityLogList) {
                    if (actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_1)
                        && actAljoinActivityLog.getCurrentTaskDefKey().equals(lastLog.getLastTaskDefKey())) {
                        lastExcutionId = actAljoinActivityLog.getExecutionId();
                        break;
                    }
                }
                if (!lastExcutionId.equals(currentExcutionId)) {
                    throw new Exception("不满足条件，不能回退");
                }
            }
            if (lastLog.getLastTaskDefKey().startsWith("StartEvent_")) {
                throw new Exception("不允许退回到开始");
            }
            List<HistoricTaskInstance> hisTaskList =
                historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId())
                    .taskId(lastLog.getLastTaskId()).finished().orderByHistoricTaskInstanceEndTime().desc().list();
            if (activities.size() == 0) {
                activities = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).finished().list();
            }
            for (HistoricTaskInstance historicTaskInstance : hisTaskList) {
                for (HistoricActivityInstance activity : activities) {
                    if (activity.getTaskId() != null && activity.getTaskId().equals(historicTaskInstance.getId())) {
                        MultiInstanceLoopCharacteristics preMulti =
                            actActivitiService.getMultiInstance(task.getProcessInstanceId(), activity.getActivityId());
                        if (preMulti != null) {
                            throw new Exception("不允许退回到多实例(会签)节点");
                        }
                        retMap.put("activityKey", activity.getActivityId());
                        retMap.put("activityName", activity.getActivityName());
                        retMap.put("userId", activity.getAssignee());
                        if (StringUtils.isNotEmpty(activity.getAssignee())) {
                            AutUser user = autUserService.selectById(Long.parseLong(activity.getAssignee()));
                            retMap.put("userFullName", user.getFullName());
                        }
                        retMsg.setCode(0);
                        retMsg.setObject(retMap);
                        return retMsg;
                    }
                }
            }

        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    /**
     *
     * @描述：退回操作获得起点信息：可以退回的允许情况如下： （1）自由流程可以退回（2）对于非自由流程， 当前活动节点只有一个并且起始节点只有一个的可以退回
     *
     * @返回：RetMsg
     *
     * @作者：zhongjy
     *
     * @时间：2017年2月28日 下午8:18:14
     */
    @RequestMapping(value = "/getFirstTaskInfo")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query")})
    @ResponseBody
    @ApiOperation("获得上一节点信息(通过流程轨迹)")
    public RetMsg getFirstTaskInfo(HttpServletRequest request, HttpServletResponse response) {
        // 当前任务节点id
        String taskId = request.getParameter("taskId");
        String bpmnId = request.getParameter("bpmnId");
        RetMsg retMsg = new RetMsg();
        Map<String, String> retMap = new HashMap<String, String>();
        try {
            if (StringUtils.isEmpty(taskId)) {
                throw new Exception("无上级节点，不能退回");
            }
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

            // 判断该节点是否是多实例节点：当前节点是多实例不能退回，上个节点是多实例，也不能退回
            MultiInstanceLoopCharacteristics currentMulti =
                actActivitiService.getMultiInstance(task.getProcessInstanceId(), task.getTaskDefinitionKey());
            if (currentMulti != null) {
                throw new Exception("当前节点是多实例(会签)节点不满足退回条件");
            }

            ActAljoinBpmnRun actAljoinBpmnRun = actAljoinBpmnRunService.selectById(bpmnId);
            List<HistoricActivityInstance> activities = new ArrayList<HistoricActivityInstance>();
            if (actAljoinBpmnRun.getIsFree() == 1) {
                // （1）自由流程可以退回
                activities = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).finished().list();
                // 检查是否满足退回条件
                if (activities.size() == 0) {
                    throw new Exception("无上级节点，不能退回");
                }
            } else {
                // 在判断当前流程实例是否同时存在活动节点（多个执行流）
                List<Task> taskList = taskService.createTaskQuery().executionId(task.getExecutionId()).list();
                if (taskList.size() > 1) {
                    throw new Exception("流程不满足退回条件:含有多个真正办理的任务节点");
                } else if (taskList.size() == 0) {
                    throw new Exception("无上级节点，不能退回");
                }
            }
            // 从日志表中抓取开始节点提交到的节点
            Where<ActAljoinActivityLog> logWhere = new Where<ActAljoinActivityLog>();
            logWhere.eq("proc_inst_id", task.getProcessInstanceId());
            logWhere.like("last_task_def_key", "StartEvent", SqlLike.RIGHT);
            logWhere.orderBy("operate_time", false);
            List<ActAljoinActivityLog> actAljoinActivityLogList = actAljoinActivityLogService.selectList(logWhere);
            if (actAljoinActivityLogList.size() == 0) {
                throw new Exception("无上级节点，不能退回");
            }
            if (actAljoinActivityLogList.size() > 1) {
                throw new Exception("多个开始节点，不能退回");
            }
            // 起点信息
            ActAljoinActivityLog log = actAljoinActivityLogList.get(0);
            String startTaskKey = log.getCurrentTaskDefKey();
            String startTaskId = log.getCurrentTaskId();

            if (StringUtils.isEmpty(startTaskKey)) {
                throw new Exception("没有找到可以回退的历史环节");
            }

            if (startTaskKey.startsWith("StartEvent_")) {
                throw new Exception("不允许退回到开始");
            }

            if (taskId.equals(startTaskId)) {
                throw new Exception("起始环节不允许回退");
            }

            if (!task.getExecutionId().equals(log.getExecutionId())){
                throw new Exception("条件限制不允许退回到起始点");
            }
            if (task.getTaskDefinitionKey().equals(startTaskKey)) {
                throw new Exception("当前位于起始点不允许退回到起始点,如有需要请管理员特送此流程");
            }
            List<HistoricTaskInstance> hisTaskList =
                historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId())
                    .taskId(startTaskId).finished().orderByHistoricTaskInstanceEndTime().desc().list();
            if (activities.size() == 0) {
                activities = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).finished().list();
            }
            for (HistoricTaskInstance historicTaskInstance : hisTaskList) {
                for (HistoricActivityInstance activity : activities) {
                    if (activity.getTaskId() != null && activity.getTaskId().equals(historicTaskInstance.getId())) {

                        MultiInstanceLoopCharacteristics preMulti =
                            actActivitiService.getMultiInstance(task.getProcessInstanceId(), activity.getActivityId());
                        if (preMulti != null) {
                            throw new Exception("不允许退回到多实例(会签)节点");
                        }
                        retMap.put("activityKey", activity.getActivityId());
                        retMap.put("activityName", activity.getActivityName());
                        retMap.put("userId", activity.getAssignee());
                        if (StringUtils.isNotEmpty(activity.getAssignee())) {
                            AutUser user = autUserService.selectById(Long.parseLong(activity.getAssignee()));
                            retMap.put("userFullName", user.getFullName());
                        }
                        retMsg.setCode(0);
                        retMsg.setObject(retMap);
                        return retMsg;
                    }
                }
            }

        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }
}
