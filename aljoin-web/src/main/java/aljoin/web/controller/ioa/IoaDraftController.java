package aljoin.web.controller.ioa;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.object.DraftTaskShowVO;
import aljoin.act.iservice.ActAljoinFormDataDraftService;
import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.web.controller.BaseController;

/**
 * 
 * 草稿箱
 *
 * @author：zhongjy
 * 
 * @date：2017年10月12日 上午8:43:11
 */
@Controller
@RequestMapping(value = "/ioa/ioaDraft")
public class IoaDraftController extends BaseController {
	
   private final static Logger logger = LoggerFactory.getLogger(IoaDraftController.class);
   @Resource
   private ActAljoinFormDataDraftService draftService;
  
  /**
   * 
   * 草稿箱页面
   *
   * @return：String
   *
   * @author：zhongjy
   *
   * @date：2017年10月12日 上午8:42:57
   */
  @RequestMapping("/ioaDraftPage")
  public String ioaDraftPage(HttpServletRequest request) {
    return "ioa/ioaDraftPage";
  }
  
  /**
   * 
   * 草稿箱列表分页
   *
   * @return：DraftTaskShowVO
   *
   * @author：huangw
   *
   * @date：2017年10月28日
   */
  @RequestMapping("/list")
  @ResponseBody
  public Page<DraftTaskShowVO> list(HttpServletRequest request, HttpServletResponse response,PageBean pageBean) {
	  Page<DraftTaskShowVO> page=new Page<DraftTaskShowVO>();
	  Map<String,Object> queryMap=new HashMap<String, Object>();
	  try {
	  String queryValue = request.getParameter("type");
	  if(queryValue!=null && !"".equals(queryValue)){
		  queryMap.put("type", queryValue);
	  }
	  queryValue = request.getParameter("typeName");
	  if(queryValue!=null && !"".equals(queryValue)){
		  queryMap.put("typeName", queryValue);
	  }
	  queryValue = request.getParameter("startTime");
	  if(queryValue!=null && !"".equals(queryValue)){
		  queryMap.put("StatrTime", queryValue);
	  }
	  queryValue = request.getParameter("endTime");
	  if(queryValue!=null && !"".equals(queryValue)){
		  queryMap.put("EndTime", queryValue);
	  }
	  CustomUser customUser = getCustomDetail();
	  queryMap.put("UserID", customUser.getUserId());
	  queryMap.put("UserName", customUser.getNickName());
	  page=draftService.listPage(pageBean, queryMap);
	  } catch (Exception e) {
		// TODO Auto-generated catch block
		//logger.error("", e);
		logger.error("查询草稿箱错误",e.getMessage());
	  }
      return page;
  }
  /**
   * 
   * 草稿箱删除
   *
   * @return：RetMsg
   *
   * @author：huangw
   *
   * @date：2017年10月28日
   */
  @RequestMapping("/delete")
  @ResponseBody
  public RetMsg delete(HttpServletRequest request, HttpServletResponse response) {
	  RetMsg rMsg=new RetMsg();
	  try {
		  
		String delID = request.getParameter("id");
		String[] delIDs=delID.split(",");
		for (int i = 0; i < delIDs.length; i++) {
			draftService.deleteDraftById(delIDs[i]);
		}
	    rMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
		rMsg.setMessage(WebConstant.RETMSG_OPERATION_SUCCESS);
		} catch (Exception e) {
			//logger.error("", e);
			logger.error("删除草稿箱内容错误",e.getMessage());
			rMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
			rMsg.setMessage(WebConstant.RETMSG_OPERATION_FAIL);	
			
		}
    return rMsg;
  }  
}
