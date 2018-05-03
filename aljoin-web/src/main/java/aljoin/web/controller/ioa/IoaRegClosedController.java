package aljoin.web.controller.ioa;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.entity.ActAljoinFormDataRun;
import aljoin.act.iservice.ActAljoinFormDataRunService;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.ioa.dao.entity.IoaRegClosed;
import aljoin.ioa.dao.object.IoaRegClosedVO;
import aljoin.ioa.iservice.IoaRegCategoryService;
import aljoin.ioa.iservice.IoaRegClosedService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 收发文登记分类
 *
 * @author：zhongjy
 * 
 * @date：2017年10月12日 上午8:43:11
 */
@Controller
@RequestMapping(value = "/ioa/ioaRegClosed", method = RequestMethod.POST)
@Api(value = "收发文登记分类操作Controller", description = "流程->收发文登记分类")
public class IoaRegClosedController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(IoaRegClosedController.class);
    @Resource
    private IoaRegCategoryService ioaRegCategoryService;
    @Resource
    private IoaRegClosedService ioaRegClosedService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private TaskService taskService;
    @Resource
    private ActAljoinFormDataRunService actAljoinFormDataRunService;
    /**
     * 
     * @描述：(页面).
     *
     * @返回：String
     *
     * @作者：zhongjy
     *
     * @时间：2017-12-13
     */
    @RequestMapping(value = "/ioaRegClosedPage", method = RequestMethod.GET)
    @ApiOperation("收文登记页面")
    public String ioaRegClosedPage(HttpServletRequest request, HttpServletResponse response) {
        return "ioa/ioaRegClosedPage";
    }

    /**
     * 
     * @描述：收文登记分页列表(页面).
     *
     * @返回： Page<IoaRegClosed>
     *
     * @作者： huangw
     *
     * @时间：2018-03-29
     */
    @RequestMapping("/list")
    @ApiOperation(value = "收文登记分页列表", notes = "ischange = 0时隐藏删除按钮")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "registrationName", value = "登记人姓名", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "toUnit", value = "来文单位", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "title", value = "查询标题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "每页数据条数", required = true, dataType = "integer",
            paramType = "query"),
        @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "integer", paramType = "query"),
        @ApiImplicitParam(name = "toNo", value = "来文文号", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "startTime", value = "查询的开始时间", required = false, dataType = "Date",
            paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, dataType = "Date",
            paramType = "query"),
        @ApiImplicitParam(name = "isClosedNoAsc", value = "是否根据收文文号正序", required = false, dataType = "string",
        paramType = "query"),
        @ApiImplicitParam(name = "istoNoAsc", value = "是否根据来文文号正序", required = false, dataType = "string",
        paramType = "query"),
        @ApiImplicitParam(name = "isRegistrationTimeAsc", value = "是否根据登记时间正序", required = false, dataType = "string",
        paramType = "query"),
        @ApiImplicitParam(name = "closedNo", value = "收文文号", required = false, dataType = "string",
            paramType = "query")})
    @ResponseBody
    public Page<IoaRegClosed> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        IoaRegClosedVO obj) {
        Page<IoaRegClosed> page = null;
        try {
            CustomUser customUser = getCustomDetail();
            Long userId = customUser.getUserId();
            page = ioaRegClosedService.list(pageBean, obj, userId);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * @描述：收文登记添加
     *
     *            @返回： RetMsg
     *
     *            @作者： huangw
     *
     * @时间：2018-03-29
     */
    @RequestMapping("/addRegClosed")
    @ApiOperation(value = "收文登记", notes = "流程详情页面进行收文登记")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "toUnit", value = "来文单位", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "toType", value = "来文类型", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "title", value = "查询标题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "priorities", value = "缓急：中文一般，紧急，加急", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "toNo", value = "来文文号", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "prioritiesLevel", value = "缓急：标识1，2，3", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "closedDate", value = "收文日期", required = false, dataType = "Date",
            paramType = "query"),
        @ApiImplicitParam(name = "category", value = "归属分类ID", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "closedNo", value = "收文文号", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "closedNumber", value = "份数", required = false, dataType = "Integer",
            paramType = "query"),
        @ApiImplicitParam(name = "secretLevel", value = "密级：1，2，3", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "level", value = "密级：保密，加密，不保密", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "registrationTime", value = "登记日期", required = false, dataType = "Date",
            paramType = "query"),
        @ApiImplicitParam(name = "registrationName", value = "登记", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "isChange", value = "是否手动登记(收文登记页面新增为1,流程新增为0)", required = false, dataType = "string",
            paramType = "query")})
    @ResponseBody
    public RetMsg addRegClosed(HttpServletRequest request, HttpServletResponse response, IoaRegClosed obj) {
        RetMsg retMsg = new RetMsg();
        obj.setClosedDate(new Date());
        obj.setRegistrationTime(new Date());
        try {
            retMsg = ioaRegClosedService.addRegClosed(obj);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.toString());
        }
        return retMsg;
    }

    /**
     * 
     * @描述：收文登记修改
     *
     *            @返回： RetMsg
     *
     *            @作者： huangw
     *
     * @时间：2018-03-29
     */
    @RequestMapping("/upDateRegClosed")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "登记ID", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "toUnit", value = "来文单位", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "toType", value = "来文类型", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "title", value = "查询标题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "priorities", value = "缓急：中文一般，紧急，加急", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "toNo", value = "来文文号", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "prioritiesLevel", value = "缓急：标识1，2，3", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "closedDate", value = "收文日期", required = false, dataType = "Date",
            paramType = "query"),
        @ApiImplicitParam(name = "category", value = "归属分类ID", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "closedNo", value = "收文文号", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "closedNumber", value = "份数", required = false, dataType = "Integer",
            paramType = "query"),
        @ApiImplicitParam(name = "secretLevel", value = "密级：1，2，3", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "level", value = "密级：保密，加密，不保密", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "registrationTime", value = "登记日期", required = false, dataType = "Date",
            paramType = "query"),
        @ApiImplicitParam(name = "registrationName", value = "登记人名", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "isChange", value = "是否手动登记(收文登记页面新增为1,流程新增为0)", required = false, dataType = "string",
            paramType = "query")

    })
    @ResponseBody
    public RetMsg upDataRegClosed(HttpServletRequest request, HttpServletResponse response, IoaRegClosed obj) {
        RetMsg retMsg = new RetMsg();

        try {
            if (obj.getId() != null) {
                ioaRegClosedService.updateById(obj);
                retMsg.setCode(0);
                retMsg.setMessage("修改成功");
            } else {
                retMsg.setCode(1);
                retMsg.setMessage("修改失败，数据参数有误！");
            }

        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.toString());
        }
        return retMsg;
    }

    /**
     * 
     * @描述：收文登记删除
     *
     *            @返回： RetMsg
     *
     *            @作者： huangw
     *
     * @时间：2018-03-29
     */
    @RequestMapping("/delRegClosed")
    @ApiOperation(value = "删除", notes = "ischange = 1时可删除,在页面要进行判断")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "登记id", required = false, dataType = "string", paramType = "query")})
    @ResponseBody
    public RetMsg delRegClosed(HttpServletRequest request, HttpServletResponse response, IoaRegClosed obj) {
        RetMsg retMsg = new RetMsg();

        try {
            if (obj.getId() != null) {
                ioaRegClosedService.deleteById(obj.getId());
                retMsg.setCode(0);
                retMsg.setMessage("删除成功");
            } else {
                retMsg.setCode(1);
                retMsg.setMessage("删除失败，数据参数有误！");
            }

        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.toString());
        }
        return retMsg;
    }

    /**
     * 
     * @描述：收文登记获取名字及登记时间
     *
     *                   @返回： RetMsg
     *
     *                   @作者： huangw
     *
     * @时间：2018-03-29
     */
    @RequestMapping("/getDocumentInfo")
    @ApiOperation(value = "收文登记", notes = "流程详情页面进行收文登记")
    @ResponseBody
    public RetMsg getDocumentInfo(HttpServletRequest request, HttpServletResponse response, String taskId) {
        RetMsg retMsg = new RetMsg();
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            CustomUser customUser = getCustomDetail();
            
            // 获取公文控件内容
            Task task = taskService.createTaskQuery().taskId(taskId).taskAssignee(customUser.getUserId().toString()).singleResult();
            if(task == null){
                throw new Exception("任务被他人签收或您尚未签收该任务");
            }
            // 收文类型
//            List<IoaRegCategory> categoryList = ioaRegCategoryService.getCateGoryList("0");
//            map.put("categoryList", categoryList);
            // 公文控件内容
            Where<ActAljoinFormDataRun> where = new Where<ActAljoinFormDataRun>();
            where.eq("proc_inst_id", task.getProcessInstanceId());
            where.eq("proc_task_id", task.getTaskDefinitionKey());
            List<ActAljoinFormDataRun> allFormDataList = actAljoinFormDataRunService.selectList(where);
            // 筛选公文控件(根据控件id是公文控件的)
            List<ActAljoinFormDataRun> formDataList = new ArrayList<ActAljoinFormDataRun>();
            for (ActAljoinFormDataRun data : allFormDataList) {
                if(data.getFormWidgetId().startsWith("aljoin_form_writing_")){
                    // 公文文号
                    formDataList.add(data);
                }else if(data.getFormWidgetId().startsWith("aljoin_form_dispatch_date_")){
                    // 发文日期
                    formDataList.add(data);
                }else if(data.getFormWidgetId().startsWith("aljoin_form_size_number_")){
                    // 份数
                    formDataList.add(data);
                }else if(data.getFormWidgetId().startsWith("aljoin_form_select_file_type_")){
                    // 文件类型
                    formDataList.add(data);
                }else if(data.getFormWidgetId().startsWith("aljoin_form_come_text_")){
                    // 来文文号
                    formDataList.add(data);
                }else if(data.getFormWidgetId().startsWith("aljoin_form_select_urgency_")){
                    // 缓急
                    formDataList.add(data);
                }else if(data.getFormWidgetId().startsWith("aljoin_form_in_date_")){
                    // 收文日期
                    formDataList.add(data);
                }else if(data.getFormWidgetId().startsWith("aljoin_form_company_")){
                    // 来文单位
                    formDataList.add(data);
                }else if(data.getFormWidgetId().startsWith("aljoin_form_biz_title_")){
                    // 公文标题
                }else if(data.getFormWidgetId().startsWith("aljoin_form_select_rank_")){
                    // 密级
                }
                
            }
            map.put("formDataList", formDataList);
            // 获取当前操作信息
            IoaRegClosed document = new IoaRegClosed();
            document.setRegistrationTime(new Date());
            document.setRegistrationName(customUser.getNickName());
            map.put("document", document);
            retMsg.setCode(0);
            retMsg.setObject(map);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.toString());
        }
        return retMsg;
    }
    
    /**
     * 
     * @描述：收文登记获取名字及登记时间
     *
     *                   @返回： RetMsg
     *
     *                   @作者： huangw
     *
     * @时间：2018-03-29
     */
    @RequestMapping("/getRegisterInfo")
    @ApiOperation(value = "收文登记", notes = "收文登记页面进行收文登记")
    @ResponseBody
    public RetMsg getRegisterInfo(HttpServletRequest request, HttpServletResponse response, String taskId) {
        RetMsg retMsg = new RetMsg();
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            CustomUser customUser = getCustomDetail();
            // 收文类型
//            List<IoaRegCategory> categoryList = ioaRegCategoryService.getCateGoryList("0");
//            map.put("categoryList", categoryList);
            // 获取当前操作信息
            IoaRegClosed document = new IoaRegClosed();
            document.setRegistrationTime(new Date());
            document.setRegistrationName(customUser.getNickName());
            map.put("document", document);
            retMsg.setCode(0);
            retMsg.setObject(map);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.toString());
        }
        return retMsg;
    }
    
    /**
     * 
     * @描述：导出
     *
     *        @返回： RetMsg
     *
     *        @作者： huangw
     *
     * @时间：2018-03-29
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ApiOperation(value = "导出", notes = "导出(可带查询条件)")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "toUnit", value = "来文单位", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "toType", value = "来文类型", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "title", value = "查询标题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "toNo", value = "来文文号", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "priorities_level", value = "缓急", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "createTime", value = "查询的开始时间", required = false, dataType = "Date",
            paramType = "query"),
        @ApiImplicitParam(name = "closedDate", value = "结束时间", required = false, dataType = "Date",
            paramType = "query"),
        @ApiImplicitParam(name = "category", value = "归属分类ID", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "closedNo", value = "收文文号", required = false, dataType = "string",
            paramType = "query")})
    public void export(HttpServletRequest request, HttpServletResponse response, IoaRegClosed obj) {
        try {
            ioaRegClosedService.export(response, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 
     * (根据ID获取对象).
     *
     * @return：
     *
     * @author：
     *
     * @date：2017-08-31
     */
    @RequestMapping(value = "/getById")
    @ResponseBody
    @ApiOperation(value = "详情", notes = "详情接口")
    @ApiImplicitParam(name = "id", value = "ID", required = true, dataType = "int", paramType = "query")
    public IoaRegClosed getById(HttpServletRequest request, HttpServletResponse response, IoaRegClosed obj) {
        IoaRegClosed ioa = ioaRegClosedService.selectById(obj.getId());
        return ioa;
    }
}