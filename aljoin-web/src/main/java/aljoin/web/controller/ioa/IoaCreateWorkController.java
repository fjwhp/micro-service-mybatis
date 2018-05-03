package aljoin.web.controller.ioa;

import aljoin.act.dao.entity.*;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinBpmnUserService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.act.iservice.ActAljoinFormDataDraftService;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.object.MsgConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import aljoin.web.service.act.ActAljoinFormDataDraftWebService;
import aljoin.web.service.ioa.IoaCreateWorkWebService;
import aljoin.web.task.WebTask;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/ioa/ioaCreateWork", method = RequestMethod.POST)
@Api(value = "公共表单数据操作Controller", description = "流程->公共表单数据操作")
public class IoaCreateWorkController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(IoaCreateWorkController.class);

    @Resource
    private ActAljoinFormDataDraftService actAljoinFormDataDraftService;
    @Resource
    private ActAljoinBpmnUserService actAljoinBpmnUserService;
    @Resource
    private ActAljoinBpmnService actAljoinBpmnService;
    @Resource
    private ActAljoinCategoryService actAljoinCategoryService;
    @Resource
    private IoaCreateWorkWebService ioaCreateWorkWebService;
    @Resource
    private ActAljoinFormDataDraftWebService actAljoinFormDataDraftWebService;
    @Resource
    private WebTask webTask;

    /**
     *
     * 新建工作页面
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping(value = "/ioaCreateWorkPage", method = RequestMethod.GET)
    public String ioaCreateWorkPage(HttpServletRequest request) {
        return "ioa/ioaCreateWorkPage";
    }

    /**
     *
     * 存稿
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping(value = "/saveDraft", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "存稿")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "bpmnId", value = "流程ID", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "formId", value = "表单ID", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "formWidgetIds", value = "表单控件ID（运行时）,多个逗号分隔,注意：对应表单的值也要传到后台，名称就是其对应的ID",
            required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query")})
    public RetMsg saveDraft(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataDraft entity) {
        RetMsg retMsg = new RetMsg();
        try {
            String formWidgetIds = request.getParameter("formWidgetIds");
            Map<String, String> paramMap = new HashMap<String, String>();
            if (StringUtils.isNotEmpty(formWidgetIds)) {
                String[] formWidgetIdsArr = formWidgetIds.split(",");
                for (int i = 0; i < formWidgetIdsArr.length; i++) {
                    String paramValue = request.getParameter(formWidgetIdsArr[i]);
                    if (paramValue == null) {
                        throw new Exception("传参有问题：没有" + formWidgetIdsArr[i] + "参数");
                    }
                    entity.setOperateUserId(getCustomDetail().getUserId());
                    paramMap.put(formWidgetIdsArr[i], paramValue);
                }
            }

            Map<String, String> param = new HashMap<String, String>();
            if (!StringUtils.isEmpty(request.getParameter("isUrgent"))) {
                param.put("isUrgent", request.getParameter("isUrgent"));
            } else {
                param.put("isUrgent", "1");
            }

            CustomUser customUser = getCustomDetail();
            Map<String, String> retMap = actAljoinFormDataDraftWebService.saveDraft(entity, paramMap, param,
                customUser.getUserId(), customUser.getNickName());
            retMsg.setCode(0);
            retMsg.setMessage("存稿成功");
            // 返回流程实例ID和当然任务ID
            String instanceIdAndTaskId = retMap.get("bpmnId") + "," + retMap.get("formId") + "," + retMap.get("taskId");
            retMsg.setObject(instanceIdAndTaskId);

        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     *
     * 新建工作(并发起流程).
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping("/doCreateWork")
    @ResponseBody
    @ApiOperation(value = "提交")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "bpmnId", value = "流程ID", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "formId", value = "表单ID", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "formWidgetIds", value = "表单控件ID（运行时）,多个逗号分隔,注意：对应表单的值也要传到后台，名称就是其对应的ID",
            required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query")})
    public RetMsg doCreateWork(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataRun entity) {
        RetMsg retMsg = new RetMsg();
        try {
            String formWidgetIds = request.getParameter("formWidgetIds");
            String[] formWidgetIdsArr = formWidgetIds.split(",");
            Map<String, String> paramMap = new HashMap<String, String>();
            for (int i = 0; i < formWidgetIdsArr.length; i++) {
                if (StringUtils.isNotEmpty(formWidgetIdsArr[i])) {
                    String paramValue = request.getParameter(formWidgetIdsArr[i]);
                    if (paramValue == null) {
                        throw new Exception("传参有问题：没有" + formWidgetIdsArr[i] + "参数");
                    }
                    entity.setOperateUserId(getCustomDetail().getUserId());
                    paramMap.put(formWidgetIdsArr[i], paramValue);
                }
            }
            Map<String, String> param = new HashMap<String, String>();
            if (!StringUtils.isEmpty(request.getParameter("isUrgent"))) {
                param.put("isUrgent", request.getParameter("isUrgent"));
            } else {
                param.put("isUrgent", "1");
            }
            param.put("activityId", request.getParameter("activityId"));
            param.put("isTask", request.getParameter("isTask"));
            param.put("nextNode", request.getParameter("nextNode"));
            param.put("userId", request.getParameter("userId"));
            param.put("thisTaskUserComment", request.getParameter("thisTaskUserComment"));
            param.put("taskAuth", request.getParameter("taskAuth"));
            param.put("topButtonComment", request.getParameter("topButtonComment"));
            CustomUser customUser = getCustomDetail();
            Map<String, String> resultMap = ioaCreateWorkWebService.doCreateWork(entity, paramMap, param,
                customUser.getUserId(), customUser.getNickName());
            if (null != resultMap) {
                AutUser user = new AutUser();
                user.setId(customUser.getUserId());
                user.setUserName(customUser.getUsername());
                user.setFullName(customUser.getNickName());
                resultMap.put("taskAuth", request.getParameter("taskAuth"));
                webTask.sendOnlineMsg4MulTask(resultMap, user);
            }

            retMsg.setCode(0);
            retMsg.setMessage("提交成功");
        } catch (Exception e) {
            if (MsgConstant.NEXT_NODE_NOT_NULL.equals(e.getMessage())) {
                retMsg.setCode(2);
            } else if (MsgConstant.MULTI_PROCESSING.equals(e.getMessage())) {
                retMsg.setCode(3);
            } else {
                retMsg.setCode(1);
            }
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     *
     * 通过
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping("/doPass")
    @ResponseBody
    public RetMsg doPass(HttpServletRequest request) {
        return null;
    }

    /**
     *
     * 退回
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping("/doBack")
    @ResponseBody
    public RetMsg doBack(HttpServletRequest request) {
        return null;
    }

    /**
     *
     * 打印
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping("/doPrint")
    @ResponseBody
    public RetMsg doPrint(HttpServletRequest request) {
        return null;
    }

    /**
     *
     * 分发
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping("/doDistribute")
    @ResponseBody
    public RetMsg doDistribute(HttpServletRequest request) {
        return null;
    }

    /**
     *
     * 传阅
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping("/doCirculation")
    @ResponseBody
    public RetMsg doCirculation(HttpServletRequest request) {
        return null;
    }

    /**
     *
     * @描述：加签
     *
     * @返回：String
     *
     * @作者：zhongjy
     *
     * @时间：2017年10月12日 上午8:42:57
     */
    @RequestMapping("/doAddSign")
    @ResponseBody
    @ApiOperation(value = "加签")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "bpmnId", value = "流程ID", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "formId", value = "表单ID", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "formWidgetIds", value = "表单控件ID（运行时）,多个逗号分隔,注意：对应表单的值也要传到后台，名称就是其对应的ID",
            required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query")})
    public RetMsg doAddSign(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataRun entity) {
        RetMsg retMsg = new RetMsg();
        try {
            String formWidgetIds = request.getParameter("formWidgetIds");
            String[] formWidgetIdsArr = formWidgetIds.split(",");
            Map<String, String> paramMap = new HashMap<String, String>();
            for (int i = 0; i < formWidgetIdsArr.length; i++) {
                if (StringUtils.isNotEmpty(formWidgetIdsArr[i])) {
                    String paramValue = request.getParameter(formWidgetIdsArr[i]);
                    if (paramValue == null) {
                        throw new Exception("传参有问题：没有" + formWidgetIdsArr[i] + "参数");
                    }
                    entity.setOperateUserId(getCustomDetail().getUserId());
                    paramMap.put(formWidgetIdsArr[i], paramValue);
                }
            }

            Map<String, String> param = new HashMap<String, String>();
            param.put("activityId", request.getParameter("activityId"));
            param.put("isTask", request.getParameter("isTask"));
            param.put("signUserIdList", request.getParameter("targetUserId"));
            param.put("thisTaskUserComment", request.getParameter("thisTaskUserComment"));
            CustomUser customUser = getCustomDetail();
            Map<String, String> resultMap = ioaCreateWorkWebService.doAddSign(entity, paramMap, param,
                customUser.getUserId(), customUser.getNickName());
            if (null != resultMap) {
                AutUser user = new AutUser();
                user.setId(customUser.getUserId());
                user.setUserName(customUser.getUsername());
                user.setFullName(customUser.getNickName());
                webTask.sendOnlineMsg4MulTask(resultMap, user);
            }
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        } catch (Exception e) {
            if (MsgConstant.NEXT_NODE_NOT_NULL.equals(e.getMessage())) {
                retMsg.setCode(2);
            } else {
                retMsg.setCode(1);
            }
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     *
     * 根据用户权限获取流程分类列表
     *
     * @return：List<ActAljoinCategory>
     *
     * @author：pengsp
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping(value = "/getCategoryTree", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据用户权限获取流程分类列表接口")
    public List<ActAljoinCategory> getCategoryTree(HttpServletRequest request, HttpServletResponse response) {
        // 获取当前用户信息
        CustomUser customUser = getCustomDetail();
        Where<ActAljoinBpmnUser> bpmnUserWhere = new Where<ActAljoinBpmnUser>();
        bpmnUserWhere.eq("user_id", customUser.getUserId());
        bpmnUserWhere.eq("auth_type", 0);
        List<ActAljoinBpmnUser> bpmnUserList = actAljoinBpmnUserService.selectList(bpmnUserWhere);
        List<ActAljoinCategory> categoryList = new ArrayList<ActAljoinCategory>();
        String bpmnIds = "";// 为所有授权流程ID附默认查询
        for (ActAljoinBpmnUser bpmnUser : bpmnUserList) {
            bpmnIds += bpmnUser.getBpmnId() + ",";
        }
        if (!"".equals(bpmnIds)) {
            Where<ActAljoinBpmn> actAljoinBpmnWhere = new Where<ActAljoinBpmn>();
            actAljoinBpmnWhere.in("id", bpmnIds.substring(0, bpmnIds.length() - 1));
            actAljoinBpmnWhere.eq("is_fixed", 0);
            actAljoinBpmnWhere.eq("is_active", 1);
            actAljoinBpmnWhere.eq("has_form", 1);
            actAljoinBpmnWhere.eq("is_deploy", 1);
            actAljoinBpmnWhere.setSqlSelect("category_id");
            List<ActAljoinBpmn> bpmnList = actAljoinBpmnService.selectList(actAljoinBpmnWhere);
            if (bpmnList != null && bpmnList.size() > 0) {
                bpmnIds = "";
                for (ActAljoinBpmn actAljoinBpmn : bpmnList) {
                    if (bpmnIds.indexOf(actAljoinBpmn.getCategoryId().toString()) > -1) {
                    } else {
                        bpmnIds += actAljoinBpmn.getCategoryId() + ",";
                    }
                }
                if (!"".equals(bpmnIds)) {
                    String tmpId = bpmnIds;
                    String parentId = "";
                    Where<ActAljoinCategory> cwheres = new Where<ActAljoinCategory>();
                    cwheres.eq("is_active", 1);
                    cwheres.setSqlSelect("id,parent_id");
                    List<ActAljoinCategory> lists = actAljoinCategoryService.selectList(cwheres);
                    Map<String, ActAljoinCategory> catesMap = new HashMap<String, ActAljoinCategory>();
                    if (lists != null && lists.size() > 0) {
                        for (ActAljoinCategory actAljoinCategory : lists) {
                            catesMap.put(actAljoinCategory.getId().toString(), actAljoinCategory);
                        }
                        do {
                            String subId[] = tmpId.substring(0, tmpId.length() - 1).split(",");
                            tmpId = "";
                            // List<Long> subList=new ArrayList<Long>();
                            for (String string : subId) {
                                // subList.add(Long.valueOf(string));
                                if (catesMap != null && catesMap.containsKey(string)) {
                                    ActAljoinCategory actAljoinCategory = catesMap.get(string);
                                    if (parentId.indexOf(actAljoinCategory.getParentId().toString()) > -1) {
                                    } else {
                                        tmpId += actAljoinCategory.getParentId() + ",";
                                    }
                                    if (parentId.indexOf(string) > -1) {
                                    } else {
                                        parentId += actAljoinCategory.getId() + ",";
                                        // categoryList.add(actAljoinCategory);
                                    }
                                }
                            }

                        } while (!"".equals(tmpId));
                        if (!"".equals(parentId) && parentId.length() > 0) {
                            Where<ActAljoinCategory> cwhere = new Where<ActAljoinCategory>();
                            cwhere.eq("is_active", 1);
                            cwhere.in("id", parentId.substring(0, parentId.length() - 1));
                            // cwhere.setSqlSelect("id,parent_id");
                            cwhere.orderBy("category_rank");
                            categoryList = actAljoinCategoryService.selectList(cwhere);
                        }

                    }
                }
            }
        }
        return categoryList;
    }

    /**
     *
     * 根据流程分类以及用户权限获取流程
     *
     * @return：List<ActAljoinBpmn>
     *
     * @author：pengsp
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping(value = "/getBpmnList", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据流程分类以及用户权限获取流程接口")
    @ApiImplicitParam(name = "categoryId", value = "查询流程名称字段", required = true, dataType = "string",
        paramType = "query")
    public Page<ActAljoinBpmn> getBpmnList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        String categoryId) {
        Page<ActAljoinBpmn> page = new Page<ActAljoinBpmn>();
        List<ActAljoinBpmn> result = new ArrayList<ActAljoinBpmn>();
        List<Long> bpmnIds = new ArrayList<Long>();
        CustomUser customUser = getCustomDetail();
        // 获取授权流程
        Where<ActAljoinBpmnUser> bpmnUserWhere = new Where<ActAljoinBpmnUser>();
        bpmnUserWhere.eq("user_id", customUser.getUserId());
        bpmnUserWhere.eq("auth_type", 0);
        bpmnUserWhere.eq("is_active", 1);
        bpmnUserWhere.setSqlSelect("bpmn_id,user_id");
        List<ActAljoinBpmnUser> bpmnUserList = actAljoinBpmnUserService.selectList(bpmnUserWhere);

        if (bpmnUserList != null && bpmnUserList.size() > 0) {
            for (ActAljoinBpmnUser bpmnUser : bpmnUserList) {
                bpmnIds.add(bpmnUser.getBpmnId());
            }
        }
        // 获取流程
        Where<ActAljoinBpmn> bpmnWhere = new Where<ActAljoinBpmn>();
        if (bpmnIds != null && bpmnIds.size() > 0) {
            bpmnWhere.in("id", bpmnIds);
        } else {
            bpmnWhere.eq("id", 123123123123L);
        }
        if (categoryId != null && !"".equals(categoryId)) {
            bpmnWhere.like("process_name", categoryId);
        }
        bpmnWhere.eq("is_active", 1);
        bpmnWhere.eq("is_fixed", 0);
        bpmnWhere.eq("is_deploy", 1);
        bpmnWhere.setSqlSelect("category_id,id,process_name");
        page = actAljoinBpmnService.selectPage(new Page<ActAljoinBpmn>(pageBean.getPageNum(), pageBean.getPageSize()),
            bpmnWhere);

        if (page.getRecords() != null) {
            List<ActAljoinBpmn> bpmnList = page.getRecords();
            String categoryIds = "";
            for (ActAljoinBpmn bpmns : bpmnList) {
                categoryIds += bpmns.getCategoryId() + ",";
            }
            if (categoryIds.length() > 0) {
                Where<ActAljoinCategory> cWhere = new Where<ActAljoinCategory>();
                cWhere.in("id", categoryIds);
                cWhere.setSqlSelect("id,category_name");
                List<ActAljoinCategory> cList = actAljoinCategoryService.selectList(cWhere);
                Map<String, String> cNameMap = new HashMap<String, String>();
                if (cList != null && cList.size() > 0) {
                    for (ActAljoinCategory actAljoinCategory : cList) {
                        cNameMap.put(actAljoinCategory.getId().toString(), actAljoinCategory.getCategoryName());
                    }
                }
                for (ActAljoinBpmn bpmns : bpmnList) {
                    if (cNameMap.containsKey(bpmns.getCategoryId().toString())) {
                        bpmns.setProcessDesc(cNameMap.get(bpmns.getCategoryId().toString()));
                        result.add(bpmns);
                    }
                }
                page.setRecords(result);
            }
        }
        return page;
    }

    /**
     *
     * 根据流程分类以及用户权限获取流程
     *
     * @return：List<ActAljoinBpmn>
     *
     * @author：pengsp
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping(value = "/getBpmn", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据流程分类以及用户权限获取流程接口")
    @ApiImplicitParam(name = "categoryId", value = "流程分类ID", required = true, dataType = "string", paramType = "query")
    public Page<ActAljoinBpmn> getBpmn(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        String categoryId) {
        Page<ActAljoinBpmn> page = new Page<ActAljoinBpmn>();
        List<ActAljoinBpmn> result = new ArrayList<ActAljoinBpmn>();
        List<Long> bpmnIds = new ArrayList<Long>();

        String parentId = "";
        String tmpId = categoryId + ",";
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
        // 获取流程分类下所有流程
        Where<ActAljoinBpmn> bpmnWhere = new Where<ActAljoinBpmn>();
        bpmnWhere.in("category_id", parentId);
        bpmnWhere.eq("is_fixed", 0);
        bpmnWhere.eq("is_active", 1);
        bpmnWhere.eq("is_deploy", 1);
        bpmnWhere.setSqlSelect("id");
        List<ActAljoinBpmn> bpmnList = actAljoinBpmnService.selectList(bpmnWhere);

        // 根据用户权限对流程进行筛选
        if (bpmnList != null && bpmnList.size() > 0) {
            String bpmnID = "";
            for (ActAljoinBpmn bpmn : bpmnList) {
                bpmnID += bpmn.getId() + ",";
            }
            CustomUser customUser = getCustomDetail();
            Where<ActAljoinBpmnUser> bpmnUserWhere = new Where<ActAljoinBpmnUser>();
            bpmnUserWhere.eq("user_id", customUser.getUserId());
            if ("".equals(bpmnID)) {
                bpmnID = "12312312312312312";// 默认查找无数据
            }
            bpmnUserWhere.in("bpmn_id", bpmnID);
            bpmnUserWhere.eq("auth_type", 0);
            bpmnUserWhere.eq("is_active", 1);
            bpmnUserWhere.setSqlSelect("bpmn_id,user_id");
            List<ActAljoinBpmnUser> bpmnUserList = actAljoinBpmnUserService.selectList(bpmnUserWhere);

            if (bpmnUserList != null && bpmnUserList.size() > 0) {
                for (ActAljoinBpmnUser bpmnUser : bpmnUserList) {
                    bpmnIds.add(bpmnUser.getBpmnId());
                }
            }
            Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
            if (bpmnIds != null && bpmnIds.size() > 0) {
                where.in("id", bpmnIds);
            } else {
                where.eq("id", 123123123123L);
            }
            where.orderBy("id", false);
            where.setSqlSelect("id,process_name,category_id");
            page = actAljoinBpmnService
                .selectPage(new Page<ActAljoinBpmn>(pageBean.getPageNum(), pageBean.getPageSize()), where);
            if (page.getRecords() != null) {
                List<ActAljoinBpmn> bpmnsList = page.getRecords();
                String categoryIds = "";
                for (ActAljoinBpmn bpmns : bpmnsList) {
                    categoryIds += bpmns.getCategoryId() + ",";
                }
                if (categoryIds.length() > 0) {
                    Where<ActAljoinCategory> cWhere = new Where<ActAljoinCategory>();
                    cWhere.in("id", categoryIds);
                    cWhere.setSqlSelect("id,category_name");
                    List<ActAljoinCategory> cList = actAljoinCategoryService.selectList(cWhere);
                    Map<String, String> cNameMap = new HashMap<String, String>();
                    if (cList != null && cList.size() > 0) {
                        for (ActAljoinCategory actAljoinCategory : cList) {
                            cNameMap.put(actAljoinCategory.getId().toString(), actAljoinCategory.getCategoryName());
                        }
                    }
                    for (ActAljoinBpmn bpmns : bpmnsList) {
                        if (cNameMap.containsKey(bpmns.getCategoryId().toString())) {
                            bpmns.setProcessDesc(cNameMap.get(bpmns.getCategoryId().toString()));
                            result.add(bpmns);
                        }
                    }
                    page.setRecords(result);
                }
            }
        }
        page.setRecords(result);
        return page;
    }

    /**
     *
     * 保存
     *
     * @return：String
     *
     * @author：pengsp
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping(value = "/addDraft", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "保存")
    public RetMsg addDraft(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataDraft entity) {
        RetMsg retMsg = new RetMsg();
        try {
            String formWidgetIds = request.getParameter("formWidgetIds");
            String[] formWidgetIdsArr = formWidgetIds.split(",");
            Map<String, String> paramMap = new HashMap<String, String>();
            for (int i = 0; i < formWidgetIdsArr.length; i++) {
                String paramValue = request.getParameter(formWidgetIdsArr[i]);
                if (paramValue == null) {
                    throw new Exception("传参有问题：没有" + formWidgetIdsArr[i] + "参数");
                }
                entity.setOperateUserId(getCustomDetail().getUserId());
                paramMap.put(formWidgetIdsArr[i], paramValue);
            }

            Map<String, String> param = new HashMap<String, String>();
            param.put("activityId", request.getParameter("activityId"));
            CustomUser customUser = getCustomDetail();
            actAljoinFormDataDraftService.addDraft(entity, paramMap, param, customUser.getUserId());
            retMsg.setCode(0);
            retMsg.setMessage("保存成功");
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     *
     * 附件表(根据ID删除对象).
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-12-05
     */
    @RequestMapping(value = "/deleteAttach")
    @ResponseBody
    @ApiOperation("附件删除")
    @ApiImplicitParam(name = "id", value = "附件ID", required = true, dataType = "long", paramType = "query")
    public RetMsg deleteAttach(HttpServletRequest request, HttpServletResponse response, String id) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser user = getCustomDetail();
            retMsg = actAljoinFormDataDraftWebService.delAttach(id, user.getUserId());
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }
}
