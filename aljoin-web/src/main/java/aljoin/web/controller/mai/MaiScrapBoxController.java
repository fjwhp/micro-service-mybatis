package aljoin.web.controller.mai;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.security.CustomUser;
import aljoin.mai.dao.entity.MaiScrapBox;
import aljoin.mai.dao.object.MaiScrapBoxVO;
import aljoin.mai.iservice.MaiScrapBoxService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 废件箱表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-20
 */
@Controller
@RequestMapping("/mai/maiScrapBox")
public class MaiScrapBoxController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(MaiScrapBoxController.class);
	@Resource
	private MaiScrapBoxService maiScrapBoxService;
	
	/**
	 * 
	 * 废件箱表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/maiScrapBoxPage")
	public String maiScrapBoxPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "mai/maiScrapBoxPage";
	}
	
	/**
	 * 
	 * 废件箱表(分页列表).
	 *
	 * @return：Page<MaiScrapBox>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/list")
	@ResponseBody
	@FuncObj(desc = "[内部邮件]-[废件箱]-[列表搜索]")
	public Page<MaiScrapBox> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, MaiScrapBox obj,String time1,String time2,String orderByTime) {
		Page<MaiScrapBox> page = null;
		try {
			//获得使用者id,用于过滤，废件箱仅显示登录用户创建的废件
			CustomUser user = getCustomDetail();
			Long userId = user.getUserId();
			page = maiScrapBoxService.list(pageBean, obj,userId,time1,time2,orderByTime);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 废件箱表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/add")
	@ResponseBody
	@FuncObj(desc = "[内部邮件]-[废件箱]-[新增]")
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, MaiScrapBox obj) {
		
		RetMsg retMsg = new RetMsg();
		maiScrapBoxService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 废件箱表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@FuncObj(desc = "[内部邮件]-[废件箱]-[删除]")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, MaiScrapBoxVO obj) {
		
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = new RetMsg();
			List<MaiScrapBox> maiScrapBoxList = obj.getMaiScrapBoxList();
			List<Long> idList = new ArrayList<Long>();
			for(MaiScrapBox box : maiScrapBoxList){
				idList.add(box.getId());
			}
			maiScrapBoxService.deleteBatchIds(idList);
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 废件箱表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/update")
	@ResponseBody
	@FuncObj(desc = "[内部邮件]-[废件箱]-[编辑]")
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, MaiScrapBox obj) {
		RetMsg retMsg = new RetMsg();

		MaiScrapBox orgnlObj = maiScrapBoxService.selectById(obj.getId());
		// orgnlObj.set...

		maiScrapBoxService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 废件箱表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@FuncObj(desc = "[内部邮件]-[废件箱]-[详情]")
	public MaiScrapBox getById(HttpServletRequest request, HttpServletResponse response, MaiScrapBox obj) {
		return maiScrapBoxService.selectById(obj.getId());
	}
	
	/**
	 * 
	 * 内部邮件-废件箱-恢复
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月22日 下午5:19:03
	 */
	@RequestMapping("/recover")
	@ResponseBody
	@FuncObj(desc = "[内部邮件]-[废件箱]-[恢复]")
	public RetMsg recover(HttpServletRequest request, HttpServletResponse response, MaiScrapBoxVO obj){
		
		RetMsg regMsg = new RetMsg();
		try {
			regMsg = maiScrapBoxService.recover(obj);
		} catch (Exception e) {
		  logger.error("", e);
			regMsg.setCode(1);
			regMsg.setMessage(e.getMessage());
		}
		return regMsg;
	}

	/**
	 *
	 * 废件箱编辑(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/maiDraftBoxPageEdit")
	@ApiOperation("废件箱编辑(页面)")
	public String maiDraftBoxPageEdit(HttpServletRequest request,HttpServletResponse response) {
		return "mai/maiDraftBoxPageEdit";
	}

}
