package aljoin.web.controller.act;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.object.ActAljoinBpmnVO;
import aljoin.act.dao.object.ActSeetingExpressionVO;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinBpmnUserService;
import aljoin.aut.security.CustomUser;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;
import aljoin.web.service.act.ActAljoinBpmnWebService;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * 自定义流程bpmn表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-09-01
 */
@Controller
@RequestMapping("/act/actAljoinBpmn")
public class ActAljoinBpmnController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinBpmnController.class);
  @Resource
  private ActAljoinBpmnService actAljoinBpmnService;
  @Resource
  private ActAljoinBpmnUserService actAljoinBpmnUserService;
  @Resource
  private ActActivitiService activitiService;
  @Resource
  private ActAljoinBpmnWebService actAljoinBpmnWebService;

  /**
   * 
   * 自定义流程bpmn表(页面).
   *
   * @return：String
   *
   * @author：zhongjy
   *
   * @date：2017-09-01
   */
  @RequestMapping("/actAljoinBpmnPage")
  public String actAljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {

    return "act/actAljoinBpmnPage";
  }

  /**
   * 
   * 自定义流程bpmn表(分页列表).
   *
   * @return：Page<ActAljoinBpmn>
   *
   * @author：zhongjy
   *
   * @date：2017-09-01
   */
  @RequestMapping("/list")
  @ResponseBody
  @FuncObj(desc = "[流程管理]-[流程设计]-[搜索]")
  public Page<ActAljoinBpmnVO> list(HttpServletRequest request, HttpServletResponse response,
      PageBean pageBean, ActAljoinBpmnVO obj) {
    Page<ActAljoinBpmnVO> page = null;
    try {
      page = actAljoinBpmnService.list(pageBean, obj);
    } catch (Exception e) {
      logger.error("", e);
    }
    return page;
  }


  /**
   * 
   * 自定义流程bpmn表(新增).
   *
   * @return：RetMsg
   *
   * @author：zhongjy
   *
   * @date：2017-09-01
   */
  @RequestMapping("/add")
  @ResponseBody
  public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinBpmn obj) {
    RetMsg retMsg = new RetMsg();

    // obj.set...

    actAljoinBpmnService.insert(obj);
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  /**
   * 
   * 自定义流程bpmn表(根据ID删除对象).
   *
   * @return：RetMsg
   *
   * @author：zhongjy
   *
   * @date：2017-09-01
   */
  @RequestMapping("/delete")
  @ResponseBody
  @FuncObj(desc = "[流程管理]-[流程设计]-[删除]")
  public RetMsg delete(HttpServletRequest request, HttpServletResponse response,
      ActAljoinBpmn obj) {
    RetMsg retMsg = new RetMsg();
    try {
        retMsg = actAljoinBpmnService.delete(obj);
    } catch (Exception e) {
        retMsg.setCode(1);
        retMsg.setMessage(e.getMessage());
        logger.error("", e);
    }
    return retMsg;
  }

  /**
   * 
   * 自定义流程bpmn表(根据ID删除对象).
   *
   * @return：RetMsg
   *
   * @author：zhongjy
   *
   * @date：2017-09-01
   */
  @RequestMapping("/setActive")
  @ResponseBody
  @FuncObj(desc = "[流程管理]-[流程设计]-[激活/冻结]")
  public RetMsg setActive(HttpServletRequest request, HttpServletResponse response,
      ActAljoinBpmn obj) {
    RetMsg retMsg = new RetMsg();

    ActAljoinBpmn orgnl = actAljoinBpmnService.selectById(obj.getId());
    orgnl.setIsActive(obj.getIsActive());
    actAljoinBpmnService.updateById(orgnl);

    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  /**
   * 
   * 自定义流程bpmn表(根据ID修改对象).
   *
   * @return：RetMsg
   *
   * @author：zhongjy
   *
   * @date：2017-09-01
   */
  @RequestMapping("/update")
  @ResponseBody
  public RetMsg update(HttpServletRequest request, HttpServletResponse response,
      ActAljoinBpmn obj) {
    RetMsg retMsg = new RetMsg();

    ActAljoinBpmn orgnlObj = actAljoinBpmnService.selectById(obj.getId());
    // orgnlObj.set...

    actAljoinBpmnService.updateById(orgnlObj);
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  /**
   * 
   * 自定义流程bpmn表(根据ID修改对象).
   *
   * @return：RetMsg
   *
   * @author：zhongjy
   *
   * @date：2017-09-01
   */
  @RequestMapping("/deploy")
  @ResponseBody
  @FuncObj(desc = "[流程管理]-[流程设计]-[部署]")
  public RetMsg deploy(HttpServletRequest request, HttpServletResponse response,
      ActAljoinBpmn obj) {
    RetMsg retMsg = new RetMsg();

    try {
      CustomUser cuser = getCustomDetail();
      actAljoinBpmnService.deploy(obj.getId(),cuser.getUserId(),cuser.getUsername());
      retMsg.setCode(0);
      retMsg.setMessage("部署成功");
    } catch (Exception e) {
      retMsg.setCode(1);
      retMsg.setMessage(e.getMessage());
      logger.error("", e);
    }

    return retMsg;
  }

  /**
   * 
   * 自定义流程bpmn表(根据ID获取对象).
   *
   * @return：AutUser
   *
   * @author：zhongjy
   *
   * @date：2017-09-01
   */
  @RequestMapping("/getById")
  @ResponseBody
  public ActAljoinBpmn getById(HttpServletRequest request, HttpServletResponse response,
      ActAljoinBpmn obj) {
    return actAljoinBpmnService.selectById(obj.getId());
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
  @RequestMapping("/showImg")
  @FuncObj(desc = "[流程管理]-[流程设计]-[流程图]")
  public void showImg(HttpServletRequest request, HttpServletResponse response, ActAljoinBpmn obj) {
    try {
      response.addHeader("Pragma", "No-cache");
      response.addHeader("Cache-Control", "no-cache");
      response.addDateHeader("expires", 0);
      InputStream is = activitiService.getImageInputStream(obj.getProcessId(), 0);
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
   * 流程日志
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017年12月09日
   */
  @RequestMapping(value="/getLog")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "taskId",value = "任务ID",required = false,dataType = "string",paramType = "query"),
    @ApiImplicitParam(name = "processInstanceId",value = "流程实例ID",required = false,dataType = "string",paramType = "query")
  })
  @ResponseBody
  @ApiOperation("流程日志")
  public List<FixedFormProcessLog> getLog(String taskId,String processInstanceId){
    List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
    try {
      list = actAljoinBpmnWebService.getLog(taskId,processInstanceId);
    }catch (Exception e){
      logger.error("", e);
    }
    return list;
  }

  /**
   *
   * 综合查询流程日志
   *
   * @return：void
   *
   * @author：黄威
   *
   * @date：2018年01月02日
   */
  @RequestMapping(value="/getHisLog")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "taskId",value = "任务ID",required = false,dataType = "string",paramType = "query"),
    @ApiImplicitParam(name = "processInstanceId",value = "流程实例ID",required = false,dataType = "string",paramType = "query")
  })
  @ResponseBody
  @ApiOperation("综合查询流程日志")
  public List<FixedFormProcessLog> getHisLog(String taskId,String processInstanceId){
    List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
    try {
      list = actAljoinBpmnWebService.getHisLog(processInstanceId);
    }catch (Exception e){
      logger.error("", e);
    }
    return list;
  }
  /**
	 * 
	 * 流程元素(根据分类ID获取对象).
	 *
	 * @return：List<ActAljoinBpmn>
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-14
	 */
	@RequestMapping("/setCategoryBpmn")
	@ResponseBody
	public List<ActAljoinBpmn> setCategoryBpmn(HttpServletRequest request, HttpServletResponse response, String cateGoryId) {
		List<ActAljoinBpmn> list=new ArrayList<ActAljoinBpmn>();
		try {
			list=actAljoinBpmnService.setCategoryBpmn(cateGoryId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("",e);
		}
		return list;
		
	}
	
	/**
     * 流程复制
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-04-10
     */
    @RequestMapping("/copy")
    @ResponseBody
    public RetMsg copy(HttpServletRequest request, HttpServletResponse response,ActAljoinBpmn bpmn) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = actAljoinBpmnService.copy(bpmn);
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }
    
    /**
     * 流程导出
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-04-11
     */
    @RequestMapping(value="/exportHtml",method={RequestMethod.GET})
    @ResponseBody
    public RetMsg exportHtml(HttpServletRequest request, HttpServletResponse response,ActAljoinBpmnVO bpmn) {
        RetMsg retMsg = new RetMsg();
        try {
            actAljoinBpmnService.export(response, bpmn);
        } catch (Exception e) {
            logger.error("", e);
        }
        return retMsg;
    }
    
    /**
     * 
     * 表单表(导入).
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-04-12
     */
    @RequestMapping("/fileImport")
    @ResponseBody
    public RetMsg fileImport(MultipartHttpServletRequest request) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = actAljoinBpmnService.fileImport(request);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }
    
    /**
     * 
     * 表单表(导入).
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-04-12
     */
    @RequestMapping("/fileSubmit")
    @ResponseBody
    public RetMsg fileSubmit(ActAljoinBpmnVO bpmn) {
        RetMsg retMsg = new RetMsg();
        HashMap<String, Object> map = new HashMap<String, Object>();
        try { 
            map.put("process_name", bpmn.getProcessName());
            //验证表单名是否唯一
            List<ActAljoinBpmn> list=actAljoinBpmnService.selectByMap(map);
            if(!list.isEmpty()){
                retMsg.setCode(1);
                retMsg.setMessage("流程名称重复");
                return retMsg;
            }
            if (bpmn.getProcessName().length() > 100) {
                retMsg.setCode(1);
                retMsg.setMessage("流程名称过长");
                return retMsg;
            }
            retMsg = actAljoinBpmnService.fileSubmit(bpmn);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    @RequestMapping(value = "/genExpression")
    @ResponseBody
    @ApiOperation(value = "生成条件表达式")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "targetKey", value = "目标任务key", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "targetName", value = "目标任务名称", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "selectType", value = "选择类型（1：手动选择 2：自动选择）", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "expressionList[0].name", value = "域名称", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "expressionList[0].comparison", value = "比较符", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "expressionList[0].comparisonValue", value = " 比较值", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "expressionList[0].relationShip", value = "关系", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "expressionList[1].name", value = "域名称", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "expressionList[1].comparison", value = "比较符", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "expressionList[1].comparisonValue", value = " 比较值", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "expressionList[1].relationShip", value = "关系", required = false, dataType = "string", paramType = "query")
    })
    public RetMsg genExpression(HttpServletRequest request, HttpServletResponse response,String targetKey,String targetName,Integer selectType,List<ActSeetingExpressionVO> expressionList) {
        RetMsg retMsg = new RetMsg();
        try{
            retMsg = actAljoinBpmnService.genExpression(targetKey,targetName,selectType,expressionList);
        }catch (Exception e){
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return  retMsg;
    }

    @RequestMapping(value = "/genExpressionDictList")
    @ResponseBody
    @ApiOperation(value = "获取条件表达式比较符字典列表")
    public RetMsg genExpressionDictList() {
        RetMsg retMsg = new RetMsg();
        try{
            retMsg = actAljoinBpmnWebService.getDictListByCode(WebConstant.EXPRESSION_OPERATOR);
        }catch (Exception e){
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return  retMsg;
    }

    @RequestMapping(value = "/getRelationDictList")
    @ResponseBody
    @ApiOperation(value = "获取条件表达式关系字典列表")
    public RetMsg getRelationDictList() {
        RetMsg retMsg = new RetMsg();
        try{
            retMsg = actAljoinBpmnWebService.getDictListByCode(WebConstant.RELATION_SHIP);
        }catch (Exception e){
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return  retMsg;
    }
}
