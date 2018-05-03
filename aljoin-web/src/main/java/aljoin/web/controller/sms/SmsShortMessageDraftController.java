package aljoin.web.controller.sms;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.sms.dao.entity.SmsShortMessageDraft;
import aljoin.sms.iservice.SmsShortMessageDraftService;
import aljoin.sms.iservice.SmsShortMessageService;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;

/**
 * 
 * 短信草稿表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-10
 */
@Controller
@RequestMapping("/sms/smsShortMessageDraft")
public class SmsShortMessageDraftController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(SmsShortMessageDraftController.class);
	@Resource
	private SmsShortMessageDraftService smsShortMessageDraftService;
	@Resource
	private SmsShortMessageService smsShortMessageService;

	/**
	 * 
	 * 短信草稿表(页面).
	 *
	 * @return：String
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/smsDraftPage")
	public String smsShortMessageDraftPage(HttpServletRequest request, HttpServletResponse response) {

		return "sms/smsDraftPage";
	}

	/**
	 * 
	 * 短信草稿表(分页列表).
	 *
	 * @return：Page<SmsShortMessageDraft>
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<SmsShortMessageDraft> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			SmsShortMessageDraft obj,String time1,String time2) {
		Page<SmsShortMessageDraft> page = null;
		try {
			page = smsShortMessageDraftService.list(pageBean, obj,time1,time2);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 短信草稿表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, SmsShortMessageDraft obj) {

		RetMsg retMsg = new RetMsg();

		try {
			// 默认激活
			obj.setIsActive(1);
			smsShortMessageDraftService.insert(obj);
			retMsg.setCode(0);
			retMsg.setMessage("保存成功");
		} catch (Exception e) {
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("保存失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 发送草稿短信
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2017年12月5日 下午4:30:36
	 */
	@RequestMapping("/sendDraft")
    @ResponseBody
    @FuncObj(desc = "[短信]-[草稿箱]-[发送]")
    public RetMsg sendDraft(HttpServletRequest request, HttpServletResponse response, SmsShortMessage obj) {
        RetMsg retMsg = new RetMsg();
        try {
          //删除草稿箱中的该记录 
          smsShortMessageDraftService.deleteById(obj.getId());
          obj.setId(null);
          obj.setSendStatus(1);
            smsShortMessageService.add(obj,null);
            retMsg.setCode(0);
            retMsg.setMessage("发送成功");
        } catch (Exception e) {
          logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }

	/**
	 * 
	 * 短信草稿表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, SmsShortMessageDraft obj) {
		RetMsg retMsg = new RetMsg();

		smsShortMessageDraftService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 短信草稿表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, SmsShortMessageDraft obj) {
		RetMsg retMsg = new RetMsg();
        try {
			
		
		//SmsShortMessageDraft orgnlObj = smsShortMessageDraftService.selectById(obj.getId());
		SmsShortMessageDraft orgnlObj=smsShortMessageDraftService.selectById(obj.getId());

		orgnlObj.setReceiverId(obj.getReceiverId());
		orgnlObj.setReceiverName(obj.getReceiverName());
		orgnlObj.setSendNumber(obj.getSendNumber());
		orgnlObj.setTheme(obj.getTheme());
		orgnlObj.setContent(obj.getContent());
		smsShortMessageDraftService.updateById(orgnlObj);
		retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
		retMsg.setMessage("操作成功");
        } catch (Exception e) {
			// TODO: handle exception
        	retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
        	retMsg.setMessage("更新失败"+e.getMessage());
		}
		return retMsg;
	}

	/**
	 * 
	 * 短信草稿表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public SmsShortMessageDraft getById(HttpServletRequest request, HttpServletResponse response,
			SmsShortMessageDraft obj) throws Exception {
	  SmsShortMessageDraft sms = null;
	  sms = smsShortMessageDraftService.detail(obj.getId());
	  return sms;
	}
	
	/**
	 * 
	 * 手机短信-草稿箱-批量删除
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月26日 下午4:07:01
	 */
	@RequestMapping("/deleteShortMessageDraftList")
	@ResponseBody
	public RetMsg deleteShortMessageDraftList(String ids) throws Exception {

		RetMsg retMsg = new RetMsg();
		retMsg = smsShortMessageDraftService.deleteShortMessageDraftList(ids);
		return retMsg;

	}

}
