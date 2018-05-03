package aljoin.web.controller;

import javax.annotation.Resource;

import org.springframework.security.core.context.SecurityContextHolder;

import aljoin.aut.security.CustomUser;
import aljoin.sys.iservice.SysParameterService;

/**
 * 
 * 基础控制器类
 *
 * @author：zhongjy
 *
 * @date：2017年5月25日 下午8:30:31
 */
public class BaseController {
	@Resource
	private SysParameterService sysParameterService;

	/**
	 * 
	 * 获取当前用户信息
	 *
	 * @return：CustomUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年5月25日 下午8:49:45
	 */
	public CustomUser getCustomDetail() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
