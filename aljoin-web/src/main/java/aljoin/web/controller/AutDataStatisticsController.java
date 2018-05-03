package aljoin.web.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.act.dao.entity.ActAljoinFixedConfig;
import aljoin.act.iservice.ActAljoinFixedConfigService;
import aljoin.act.iservice.ActRuTaskService;
import aljoin.aut.dao.entity.AutDataStatistics;
import aljoin.aut.dao.entity.AutMsgOnline;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.ioa.dao.entity.IoaReceiveFile;
import aljoin.ioa.iservice.IoaReceiveFileService;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.pub.dao.entity.PubPublicInfoRead;
import aljoin.pub.iservice.PubPublicInfoReadService;
import aljoin.web.annotation.FuncObj;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 数据统计表(控制器).
 * 
 * @author：sln
 * 
 * @date： 2017-11-09
 */
@Controller
@RequestMapping(value = "/aut/autDataStatistics", method = RequestMethod.POST)
@Api(value = "数据统计controller", description = "首页->首页数据统计相关接口")
public class AutDataStatisticsController extends BaseController {

  private final static Logger logger = LoggerFactory.getLogger(AutDataStatisticsController.class);
  @Resource
  private AutDataStatisticsService autDataStatisticsService;
  @Resource
  private AutMsgOnlineService autMsgOnlineService;
  @Resource
  private ActRuTaskService actRuTaskService;
  @Resource
  private ActAljoinFixedConfigService actAljoinFixedConfigService;
  @Resource
  private TaskService taskService;
  @Resource
  private PubPublicInfoReadService pubpublicInfoReadService;
  @Resource
  private IoaReceiveFileService ioaReceiveFileService;

  /**
   * 
   * 数据统计表(页面).
   *
   * @return：String
   *
   * @author：sln
   *
   * @date：2017-11-09
   */
  @RequestMapping(value = "/autDataStatisticsPage", method = RequestMethod.GET)
  @ApiOperation(value = "没啥用的接口")
  public String autDataStatisticsPage(HttpServletRequest request, HttpServletResponse response) {

    return "aut/autDataStatisticsPage";
  }


  /**
   * 
   * 数据统计表(根据ID获取对象).
   *
   * @return：AutUser
   *
   * @author：sln
   *
   * @date：2017-11-09
   */
  @RequestMapping("/getById")
  @ResponseBody
  @ApiOperation(value = "详情接口")
  public AutDataStatistics getById(HttpServletRequest request, HttpServletResponse response,
      AutDataStatistics obj) {
    return autDataStatisticsService.selectById(obj.getId());
  }

  /**
   * 
   * 数据统计表(首页-根据用户id获取改用户各类型数据).
   *
   * @return：AutUser
   *
   * @author：sln
   *
   * @date：2017-11-09
   */
  @RequestMapping(value = "/indexCount")
  @ResponseBody
  @ApiOperation(value = "首页统计")
  @FuncObj(desc = "[首页]-[分类统计]")
  public RetMsg indexCount(HttpServletRequest request, HttpServletResponse response) {
    RetMsg retMsg = new RetMsg();
    try {
      // AutDataStatistics autData = new AutDataStatistics();
      // if(user!=null){
      // autData.setBusinessKey(String.valueOf(user.getUserId()));
      // }
      // list = autDataStatisticsService.getUserCount(autData);
      CustomUser user = getCustomDetail();
      Long userId = user.getUserId();
      List<AutDataStatistics> list = new ArrayList<AutDataStatistics>();
      String processId = "";
      int count = 0;
      // 查询收文阅件流程ID
      Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
      configWhere.setSqlSelect("process_id");
      configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_READ_RECEIPT);
      List<ActAljoinFixedConfig> configList = actAljoinFixedConfigService.selectList(configWhere);
      // 我的待办
      TaskQuery tq = taskService.createTaskQuery().taskCandidateOrAssigned(userId.toString());
      int toDioCount = (int) tq.count();
      if (null != configList && !configList.isEmpty()) {
        // 我的待阅
        processId = configList.get(0).getProcessId();
        List<Task> readTask = tq.processDefinitionKeyLike(processId).list();
        count = readTask.size();
        
        Set<String> idset = new HashSet<String>();
        for (Task readtask : readTask) {
          idset.add(readtask.getProcessInstanceId());
        }
        AutDataStatistics autData = new AutDataStatistics();
        int readCount = 0;
        if (idset.size() > 0) {
          Where<IoaReceiveFile> where = new Where<IoaReceiveFile>();
          where.in("process_instance_id", idset);
          where.eq("is_close", 0);
          readCount = ioaReceiveFileService.selectCount(where);
        }else{
          readCount = 0;
        }
        autData.setObjectCount(readCount);
        autData.setObjectKey(WebConstant.AUTDATA_TOREAD_CODE);
        autData.setObjectName(WebConstant.AUTDATA_TOREAD_NAME);
        list.add(autData);
      } else {
        // 待阅流程未设置就是没有待阅
        AutDataStatistics autData = new AutDataStatistics();
        autData.setObjectCount(count);
        autData.setObjectKey(WebConstant.AUTDATA_TOREAD_CODE);
        autData.setObjectName(WebConstant.AUTDATA_TOREAD_NAME);
        list.add(autData);
      }
      if (count != 0) {
        // 总的办理条数-待阅 = 正常待办数量
        toDioCount = toDioCount - count;
      }
      AutDataStatistics autData = new AutDataStatistics();
      autData.setObjectCount(toDioCount);
      autData.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
      autData.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
      list.add(autData);

      // 在线消息
      Where<AutMsgOnline> where = new Where<AutMsgOnline>();
      where.like("receive_user_id", userId.toString());
      where.eq("is_read", 0);
      int msgCount = autMsgOnlineService.selectCount(where);
      AutDataStatistics msgAutData = new AutDataStatistics();
      msgAutData.setObjectCount(msgCount);
      msgAutData.setObjectKey(WebConstant.AUTDATA_ONLINE_CODE);
      msgAutData.setObjectName(WebConstant.AUTDATA_ONLINE_NAME);
      list.add(msgAutData);

      // 公共信息
      Where<PubPublicInfoRead> pubwhere = new Where<PubPublicInfoRead>();
      pubwhere.eq("read_user_id", userId.toString());
      pubwhere.eq("is_read", 0);
      int pubCount = pubpublicInfoReadService.selectCount(pubwhere);
      AutDataStatistics pubAutData = new AutDataStatistics();
      pubAutData.setObjectCount(pubCount);
      pubAutData.setObjectKey(WebConstant.AUTDATA_PUBNOTICE_CODE);
      pubAutData.setObjectName(WebConstant.AUTDATA_PUBNOTICE_NAME);
      list.add(pubAutData);

      retMsg.setCode(0);
      retMsg.setObject(list);
    } catch (Exception e) {
      retMsg.setCode(1);
      retMsg.setMessage("操作错误");
      logger.error("首页数据统计错误", e);
    }
    return retMsg;
  }

  /**
   * 
   * 数据统计表(首页-根据用户id获取改用户各类型数据).
   *
   * @return：AutUser
   *
   * @author：sln
   *
   * @date：2017-11-09
   */
  @RequestMapping(value = "/minus")
  @ResponseBody
  @ApiOperation(value = "未读消息数量减1")
  public RetMsg minus(HttpServletRequest request, HttpServletResponse response, String msgId) {
    RetMsg retMsg = new RetMsg();
    try {
      CustomUser user = getCustomDetail();
      AutDataStatistics autData = new AutDataStatistics();
      if (user != null) {
        autData.setBusinessKey(String.valueOf(user.getUserId()));
      }
      autData.setObjectKey(WebConstant.AUTDATA_ONLINE_CODE);
      retMsg = autMsgOnlineService.minus(autData, msgId, user.getUserId());
    } catch (Exception e) {
      retMsg.setCode(1);
      retMsg.setMessage("操作失败");
      logger.error("首页数据统计错误", e);
    }
    return retMsg;
  }

  /**
   * 
   * 数据统计表(首页-根据用户id获取改用户各类型数据).
   *
   * @return：AutUser
   *
   * @author：sln
   *
   * @date：2017-11-09
   */
  @RequestMapping(value = "/checkMsg")
  @ResponseBody
  @ApiOperation(value = "检查未读消息数量")
  @FuncObj(desc = "[首页]-[检查未读消息数量]")
  public RetMsg checkMsg(HttpServletRequest request, HttpServletResponse response) {
    RetMsg retMsg = new RetMsg();
    try {
      CustomUser user = getCustomDetail();
      // Integer msgCount = autDataStatisticsService.getUserMsgCount(autData);
      Where<AutMsgOnline> where = new Where<AutMsgOnline>();
      where.like("receive_user_id",  user.getUserId().toString());
      where.eq("is_read", 0);
      Integer msgCount = autMsgOnlineService.selectCount(where);

      retMsg.setObject(msgCount);
      retMsg.setMessage("操作成功");
      retMsg.setCode(0);
    } catch (Exception e) {
      retMsg.setCode(1);
      retMsg.setMessage("操作失败");
      logger.error("检查未读消息数量", e);
    }
    return retMsg;
  }
}
