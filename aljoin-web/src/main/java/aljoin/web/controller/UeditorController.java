package aljoin.web.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import aljoin.web.ueditor.ActionEnter;

/**
 * 
 * 百度编辑器控制器
 *
 * @author：zhongjy
 *
 * @date：2017年4月24日 下午12:31:49
 */
@Controller
@RequestMapping("/ueditor")
public class UeditorController extends BaseController {



  /**
   * 
   * 编辑器图片上传接口
   *
   * @return：String
   *
   * @author：zhongjy
   *
   * @date：2017年5月22日 下午11:05:27
   */
  @RequestMapping("/check")
  public void check(HttpServletRequest request, HttpServletResponse response) throws Exception {

    request.setCharacterEncoding("utf-8");
    response.setHeader("Content-Type", "text/html");
    String rootPath = request.getSession().getServletContext().getRealPath("/");;
    //rootPath = rootPath+"/aljoin-act/ueditor/jsp";
    ///aljoin-web/src/main/resources/static/aljoin-act/ueditor/jsp/config.json
    String exec = new ActionEnter(request, rootPath).exec();

    PrintWriter writer = response.getWriter();
    writer.write(exec);
    writer.flush();
    writer.close();
  }

}
