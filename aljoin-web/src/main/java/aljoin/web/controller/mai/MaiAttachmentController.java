package aljoin.web.controller.mai;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.iservice.MaiAttachmentService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 邮箱附件表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-20
 */
@Controller
@RequestMapping("/mai/maiAttachment")
@Api(value = "附件Controller",description = "附件接口")
public class MaiAttachmentController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(MaiAttachmentController.class);
	@Resource
	private MaiAttachmentService maiAttachmentService;
	
	/**
	 * 
	 * 邮箱附件表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/maiAttachmentPage")
	public String maiAttachmentPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "mai/maiAttachmentPage";
	}
	
	/**
	 * 
	 * 邮箱附件表(分页列表).
	 *
	 * @return：Page<MaiAttachment>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<MaiAttachment> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, MaiAttachment obj) {
		Page<MaiAttachment> page = null;
		try {
			page = maiAttachmentService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 邮箱附件表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, MaiAttachment obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		maiAttachmentService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 邮箱附件表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@ApiOperation(value = "删除附件接口")
	@ApiImplicitParam(name = "id",value = "主键ID",required = true,dataType = "int",paramType = "query")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, MaiAttachment obj) {
		RetMsg retMsg = new RetMsg();

		maiAttachmentService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 邮箱附件表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, MaiAttachment obj) {
		RetMsg retMsg = new RetMsg();

		MaiAttachment orgnlObj = maiAttachmentService.selectById(obj.getId());
		// orgnlObj.set...

		maiAttachmentService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 邮箱附件表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public MaiAttachment getById(HttpServletRequest request, HttpServletResponse response, MaiAttachment obj) {
		return maiAttachmentService.selectById(obj.getId());
	}

}
