package aljoin.web.controller.sys;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;

/**
 * 
 * 系统参数表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-06-04
 */
@Controller
@RequestMapping("/sys/sysParameter")
public class SysParameterController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(SysParameterController.class);
	@Resource
	private SysParameterService sysParameterService;

	/**
	 * 
	 * 系统参数表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-06-04
	 */
	@RequestMapping("/sysParameterPage")
	public String sysParameterPage(HttpServletRequest request, HttpServletResponse response) {

		return "sys/sysParameterPage";
	}

	/**
	 * 
	 * 系统参数表(分页列表).
	 *
	 * @return：Page<SysParameter>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-06-04
	 */
	@RequestMapping("/list")
	@ResponseBody
	@FuncObj(desc = "[系统管理]-[系统参数]-[搜索]")
	public Page<SysParameter> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, SysParameter obj) {
		Page<SysParameter> page = null;
		try {
			page = sysParameterService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 系统参数表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-06-04
	 */
	@RequestMapping("/add")
	@ResponseBody
	@FuncObj(desc = "[系统管理]-[系统参数]-[新增]")
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, SysParameter obj) {
		RetMsg retMsg = new RetMsg();

		// 判断key是否已被使用
		Where<SysParameter> where = new Where<SysParameter>();
		where.eq("param_key", obj.getParamKey());
		if (sysParameterService.selectCount(where) > 0) {
			retMsg.setCode(1);
			retMsg.setMessage("参数key已经存在");
		} else {
			sysParameterService.insert(obj);
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		}
		return retMsg;
	}

	/**
	 * 
	 * 系统参数表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-06-04
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@FuncObj(desc = "[系统管理]-[系统参数]-[删除]")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, SysParameter obj) {
		RetMsg retMsg = new RetMsg();

		sysParameterService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 系统参数表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-06-04
	 */
	@RequestMapping("/update")
	@ResponseBody
	@FuncObj(desc = "[系统管理]-[系统参数]-[修改]")
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, SysParameter obj) {
		RetMsg retMsg = new RetMsg();
		// 判断key是否已经被占用
		Where<SysParameter> where = new Where<SysParameter>();
		where.eq("param_key", obj.getParamKey());
		where.ne("id", obj.getId());
		if (sysParameterService.selectCount(where) > 0) {
			retMsg.setCode(1);
			retMsg.setMessage("参数key已经存在");
		} else {
			SysParameter orgnlObj = sysParameterService.selectById(obj.getId());
			orgnlObj.setParamDesc(obj.getParamDesc());
			orgnlObj.setParamKey(obj.getParamKey());
			orgnlObj.setParamValue(obj.getParamValue());
			orgnlObj.setIsActive(obj.getIsActive());
			sysParameterService.updateById(orgnlObj);
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		}
		return retMsg;
	}

	/**
	 * 
	 * 系统参数表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-06-04
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@FuncObj(desc = "[系统管理]-[系统参数]-[详情]")
	public SysParameter getById(HttpServletRequest request, HttpServletResponse response, SysParameter obj) {
		return sysParameterService.selectById(obj.getId());
	}

}
