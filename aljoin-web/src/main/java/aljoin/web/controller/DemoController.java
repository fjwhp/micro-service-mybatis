package aljoin.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.object.PageBean;

/**
 * 
 * demo控制器
 *
 * @author：zhongjy
 *
 * @date：2017年4月24日 下午12:31:49
 */
@Controller
@RequestMapping("/demo")
public class DemoController {
  private final static Logger logger = LoggerFactory.getLogger(DemoController.class);

	@Resource
	private AutUserService autUserService;

	@RequestMapping("/demo")
	public String index(HttpServletRequest request) {
		return "demo/demo";
	}

	@RequestMapping("/list")
	@ResponseBody
	public Page<AutUser> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean) {
		Page<AutUser> page = null;
		try {
			page = autUserService.list(pageBean,new AutUser());
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
}
