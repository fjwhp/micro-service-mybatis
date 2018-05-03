package aljoin.web.controller.ass;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.ass.dao.entity.AssInfo;
import aljoin.ass.dao.object.AssInfoVO;
import aljoin.ass.iservice.AssInfoService;
import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 固定资产信息表(控制器).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-11
 */
@Controller
@RequestMapping("/ass/assInfo")
public class AssInfoController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(AssInfoController.class);
	@Resource
	private AssInfoService assInfoService;
	
	/**
	 * 
	 * 固定资产信息表(页面).
	 *
	 * @return：String
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-11
	 */
	@RequestMapping("/assInfoPage")
	public String assInfoPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "ass/assInfoPage";
	}
	
	/**
	 * 
	 * 固定资产信息表(分页列表).
	 *
	 * @return：Page<AssInfo>
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-11
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<AssInfoVO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AssInfo obj) {
		Page<AssInfoVO> page = null;
		try {
			page = assInfoService.list(pageBean, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	

	/**
	 * 
	 * 固定资产信息表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-11
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AssInfo obj) {
		RetMsg retMsg = new RetMsg();
		try {
          retMsg = assInfoService.add(obj);
        }catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
	
	/**
	 * 
	 * 固定资产信息表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-11
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AssInfo obj) {
		RetMsg retMsg = new RetMsg();

		assInfoService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 固定资产信息表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-11
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AssInfo obj) {
		RetMsg retMsg = new RetMsg();

		try {
          retMsg = assInfoService.update(obj);
        } catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
        }
        return retMsg;
	}
    
	/**
	 * 
	 * 固定资产信息表(根据ID获取对象).
	 *
	 * @throws Exception 
	 * @return：AutUser
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-11
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public AssInfoVO getById(HttpServletRequest request, HttpServletResponse response, AssInfo obj) throws Exception {
		return assInfoService.getById(obj.getId());
	}
	
	/**
	 * 
	 * 固定资产盘点
	 *
	 * @return：void
	 *
	 * @author：xuc
	 *
	 * @date：2018年1月22日 上午8:55:59
	 */
	@RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletRequest request, HttpServletResponse response, AssInfoVO obj) {
      try {
          CustomUser user = getCustomDetail();
          if (null != user) {
              obj.setCreateUserId(user.getUserId());
          }
          assInfoService.export(response, obj);
      } catch (Exception e) {
          logger.error("", e);
      }
  }

}
