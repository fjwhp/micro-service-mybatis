package aljoin.web.controller.aut;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.aut.dao.entity.AutUserRank;
import aljoin.aut.iservice.AutUserRankService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

import com.baomidou.mybatisplus.plugins.Page;

/**
 * 
 * 人员排序表(控制器).
 * 
 * @author：huanghz
 * 
 * @date： 2017-12-13
 */
@Controller
@RequestMapping("/aut/autUserRank")
public class AutUserRankController extends BaseController {
	@Resource
	private AutUserRankService autUserRankService;
	
	/**
	 * 
	 * 人员排序表(页面).
	 *
	 * @return：String
	 *
	 * @author：huanghz
	 *
	 * @date：2017-12-13
	 */
	@RequestMapping("/autUserRankPage")
	public String autUserRankPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "aut/autUserRankPage";
	}
	
	/**
	 * 
	 * 人员排序表(分页列表).
	 *
	 * @return：Page<AutUserRank>
	 *
	 * @author：huanghz
	 *
	 * @date：2017-12-13
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<AutUserRank> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AutUserRank obj) {
		Page<AutUserRank> page = null;
		try {
			page = autUserRankService.list(pageBean, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	
	
	
	

	/**
	 * 
	 * 人员排序表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：huanghz
	 *
	 * @date：2017-12-13
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutUserRank obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		autUserRankService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 人员排序表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：huanghz
	 *
	 * @date：2017-12-13
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutUserRank obj) {
		RetMsg retMsg = new RetMsg();

		autUserRankService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 人员排序表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：huanghz
	 *
	 * @date：2017-12-13
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutUserRank obj) {
		RetMsg retMsg = new RetMsg();

		AutUserRank orgnlObj = autUserRankService.selectById(obj.getId());
		// orgnlObj.set...

		autUserRankService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 人员排序表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：huanghz
	 *
	 * @date：2017-12-13
	 */
	@RequestMapping("/updateRank")
	@ResponseBody
	public RetMsg updateRank(HttpServletRequest request, HttpServletResponse response, AutUserRank obj) {
		RetMsg retMsg = new RetMsg();
		Integer iCode = 0 ;
		String msgInfo = "操作成功";
		try {
			AutUserRank orgnlObj = autUserRankService.selectById(obj.getId());
			orgnlObj.setUserRank(obj.getUserRank());
			autUserRankService.updateById(orgnlObj);
		} catch (Exception e) {
			iCode = -1;
			msgInfo="请检查输入值，最多允许5位整数，2位小数";
			e.printStackTrace();
		}
		retMsg.setCode(iCode);
		retMsg.setMessage(msgInfo);
		return retMsg;
	}
	
    
	/**
	 * 
	 * 人员排序表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：huanghz
	 *
	 * @date：2017-12-13
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public AutUserRank getById(HttpServletRequest request, HttpServletResponse response, AutUserRank obj) {
		return autUserRankService.selectById(obj.getId());
	}

}
