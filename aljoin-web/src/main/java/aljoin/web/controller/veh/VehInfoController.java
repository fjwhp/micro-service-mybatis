package aljoin.web.controller.veh;

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
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.veh.dao.entity.VehInfo;
import aljoin.veh.dao.object.VehInfoDO;
import aljoin.veh.dao.object.VehInfoVO;
import aljoin.veh.iservice.VehInfoService;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 车船信息表(控制器).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-08
 */
@Controller
@RequestMapping("/veh/vehInfo")
public class VehInfoController extends BaseController {
    
    private final static Logger logger = LoggerFactory.getLogger(VehInfoController.class);
    @Resource
	private VehInfoService vehInfoService;
	
	/**
	 * 
	 * 车船信息表(页面).
	 *
	 * @return：String
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/vehInfoPage")
	public String vehInfoPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "veh/vehInfoPage";
	}
	
	/**
	 * 
	 * 车船信息表(分页列表).
	 *
	 * @return：Page<VehInfo>
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/list")
	@ResponseBody
    @ApiOperation("车船信息分页（使用部门是本部门）")
	public Page<VehInfoDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, VehInfo obj) {
		Page<VehInfoDO> page = null;
		try {
          CustomUser user = getCustomDetail();
          if (null != user) {
              obj.setCreateUserId(user.getUserId());
          }
          page = vehInfoService.list(pageBean, obj);
      } catch (Exception e) {
          logger.error("", e);
      }
		return page;
	}
	

	/**
	 * 
	 * 车船信息表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, VehInfoVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
          retMsg = vehInfoService.add(obj);
        }catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
	
	/**
	 * 
	 * 车船信息表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, VehInfoVO obj) {
		RetMsg retMsg = new RetMsg();

		try {
          
		  retMsg = vehInfoService.deleteByIds(obj);
        } catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
	
	/**
	 * 
	 * 车船信息表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, VehInfoVO obj) {
		RetMsg retMsg = new RetMsg();

		try {
          retMsg = vehInfoService.update(obj);
        } catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
    
	/**
	 * 
	 * 车船信息表(根据ID获取对象).
	 *
	 * @throws Exception 
	 * @return：AutUser
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public VehInfo getById(HttpServletRequest request, HttpServletResponse response, VehInfo obj) throws Exception {
		return vehInfoService.getById(obj.getId());
	}
	
	/**
     * 
     * 车船信息表(分页列表).
     *
     * @return：Page<VehInfo>
     *
     * @author：xuc
     *
     * @date：2018-01-08
     */
    @RequestMapping("/allList")
    @ResponseBody
    public Page<VehInfoDO> allList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, VehInfo obj) {
        Page<VehInfoDO> page = null;
        try {
          CustomUser user = getCustomDetail();
          if (null != user) {
              obj.setCreateUserId(user.getUserId());
          }
          page = vehInfoService.allList(pageBean, obj);
      } catch (Exception e) {
          logger.error("", e);
      }
        return page;
    }
    
    /**
     * 
     * 获取所有的车船牌号
     *
     * @return：List<String>
     *
     * @author：xuc
     *
     * @date：2018年1月18日 上午11:30:15
     */
    @RequestMapping("/allCode")
    @ResponseBody
    public List<String> allCode(HttpServletRequest request, HttpServletResponse response) {
        List<String> list = new ArrayList<String>();
        list =  vehInfoService.allCode();
        return list;
    }

    /**
     * 
     * 通过牌号查车辆
     *
     * @return：VehInfo
     *
     * @author：xuc
     *
     * @date：2018年1月18日 下午1:41:56
     */
    @RequestMapping("/getCarByCode")
    @ResponseBody
    public VehInfo getCarByCode(HttpServletRequest request, HttpServletResponse response,VehInfo obj) {
      VehInfo info = new VehInfo();
      info = vehInfoService.getCarByCode(obj.getCarCode());
      return info;
    }
}
