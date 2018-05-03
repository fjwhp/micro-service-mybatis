package aljoin.web.controller.ioa;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * 收文阅件
 *
 * @author：zhongjy
 * 
 * @date：2017年10月12日 上午8:43:11
 */
@Controller
@RequestMapping(value = "/ioa/ioaReceiveWork")
public class IoaReceiveWorkController {


  /**
   * 
   * 收文阅件页面
   *
   * @return：String
   *
   * @author：zhongjy
   *
   * @date：2017年10月12日 上午8:42:57
   */
  @RequestMapping("/ioaReceiveWorkPage")
  public String ioaReceiveWorkPage(HttpServletRequest request) {
    return "ioa/ioaReceiveWorkPage";
  }

  /**
   * 
   * 收文阅件详情页
   *
   * @return：String
   *
   * @author：zhengls
   *
   * @date：2017年10月12日 上午8:42:57
   */
  @RequestMapping("/ioaReceiveWorkPageDetail")
  public String ioaReceiveWorkPageDetail(HttpServletRequest request) {
	    String bizId = request.getParameter("bizId");
		String linkName = request.getParameter("linkName");
		String isMsgOnline = request.getParameter("isMsgOnline");
		String titleTx = request.getParameter("titleTx");
		request.setAttribute("t_bizId", bizId);
		request.setAttribute("t_linkName", linkName);
		request.setAttribute("isMsgOnline", isMsgOnline);
		request.setAttribute("titleTx", titleTx);
	    return "ioa/ioaReceiveWorkPageDetail";
  }

  

}
