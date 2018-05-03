package aljoin.web.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.sys.iservice.SysParameterService;

@Controller
@RequestMapping("/util/utilController")
public class UtilController  extends BaseController {
	
	@Resource
	private SysParameterService sysParameterService;
	
	/**
	 * 
	 * 取得允许上传文件的类型、支持上传的最大文件
	 *
	 * @return：Map<String,String>
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月12日 下午2:44:00
	 */
	@RequestMapping("/getAllowType")
	@ResponseBody
	public Map<String,String> getAllowType() throws Exception{
		Map<String,String> map = sysParameterService.allowFileType();
		return map;
		
	}
	
}