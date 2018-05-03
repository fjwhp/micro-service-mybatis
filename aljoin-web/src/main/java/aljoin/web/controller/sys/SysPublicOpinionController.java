package aljoin.web.controller.sys;

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

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysPublicOpinion;
import aljoin.sys.dao.object.SysPublicOpinionVO;
import aljoin.sys.iservice.SysPublicOpinionService;
import aljoin.web.controller.BaseController;

/**
 * @description:公共意见表(控制器).
 * 
 * @author:caizx
 * 
 * @date:2018/03/14
 */
@Controller
@RequestMapping("/sys/sysPublicOpinion")
public class SysPublicOpinionController extends BaseController {
	
	private final static Logger logger = LoggerFactory.getLogger(SysPublicOpinionController.class);
	@Resource
	private SysPublicOpinionService sysPublicOpinionService;
	@Resource
    private AutUserService autUserService;
	
	/**
	 * 
	 * @描述：公共意见表(页面).
	 *
	 * @返回：String
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-14
	 */
	@RequestMapping("/sysPublicOpinionPage")
	public String sysPublicOpinionPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "sys/sysPublicOpinionPage";
	}
	
	/**
	 * 
	 * @描述：公共意见表(分页列表).
	 *
	 * @返回：Page<SysPublicOpinion>
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-14
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<SysPublicOpinionVO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, SysPublicOpinion obj) {
		Page<SysPublicOpinionVO> newPage = null;
		try {
		    Where<SysPublicOpinion> where = new Where<SysPublicOpinion>();
		    where.orderBy("content_rank,id", true);
		    where.setSqlSelect("id,content,create_user_id");
		    Page<SysPublicOpinion> page = sysPublicOpinionService.selectPage(new Page<SysPublicOpinion>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		    List<SysPublicOpinion> sysPublicOpinionList = page.getRecords();
		    ArrayList<Long> idList = new ArrayList<Long>();
		    for (SysPublicOpinion sysPublicOpinion : sysPublicOpinionList) {
		        idList.add(sysPublicOpinion.getCreateUserId());
		    }
		    //查询用户名称
		    List<AutUser> autUserList = null;
		    if(null != idList && !idList.isEmpty()){
		        Where<AutUser> userWhere = new Where<AutUser>();
		        userWhere.setSqlSelect("id,full_name");
		        userWhere.in("id", idList);
		        autUserList = autUserService.selectList(userWhere);
		    }
		    
		    //拼接公共意见和创建用户名称
		    ArrayList<SysPublicOpinionVO> voList = new ArrayList<SysPublicOpinionVO>();
		    for (SysPublicOpinion optioin : sysPublicOpinionList) {
		        if (!autUserList.isEmpty()) {
		            for(AutUser user : autUserList){
		                if(optioin.getCreateUserId().longValue() == user.getId().longValue()){
		                    SysPublicOpinionVO vo = new SysPublicOpinionVO();
		                    vo.setSysPublicOpinion(optioin);
		                    vo.setAutUser(user);
		                    voList.add(vo);
		                }
		            }
		        }
		    }
		    newPage = new Page<SysPublicOpinionVO>();
		    newPage.setRecords(voList);
		    newPage.setCurrent(page.getCurrent());
		    newPage.setSize(page.getSize());
		    newPage.setTotal(page.getTotal());
		} catch (Exception e) {
			logger.error("",e);
		}
		return newPage;
	}
	

	/**
	 * 
	 * @描述：公共意见表(新增).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-14
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, SysPublicOpinion obj) {
		RetMsg retMsg = new RetMsg();
		try {
		    if (obj.getContentRank() < 0 ) {
		        retMsg.setCode(1);
                retMsg.setMessage("排序不能为负值");
                return retMsg;
            }
			retMsg = sysPublicOpinionService.add(obj);
		} catch (Exception e) {
		    retMsg.setCode(1);
            retMsg.setMessage("操作失败");
            logger.error("", e);
		}
		return retMsg;
	}
	
	/**
	 * 
	 * @描述：公共意见表(根据ID删除对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-14
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, SysPublicOpinion obj) {
		RetMsg retMsg = new RetMsg();
		try {
		    retMsg = sysPublicOpinionService.delete(obj);
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
            logger.error("", e);
        }
		return retMsg;
	}
	
	/**
	 * 
	 * @描述：公共意见表(根据ID修改对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-14
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, SysPublicOpinion obj) {
		RetMsg retMsg = new RetMsg();
		try {
		    if (obj.getContentRank() < 0 ) {
                retMsg.setCode(1);
                retMsg.setMessage("排序不能为负值");
                return retMsg;
            }
		    retMsg = sysPublicOpinionService.update(obj);
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
            logger.error("", e);
        }
        return retMsg;
	}
    
	/**
	 * 
	 * @描述：公共意见表(根据ID获取对象).
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-14
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public SysPublicOpinion getById(HttpServletRequest request, HttpServletResponse response, SysPublicOpinion obj) {
		return sysPublicOpinionService.selectById(obj.getId());
	}
	
	/**
     * 
     * 公共意见批量删除
     *
     * @author：caizx
     *
     * @date：2018-03-15
     */
    @RequestMapping(value = "/deleteByIdList")
    @ResponseBody
    public RetMsg deleteByIdList(HttpServletRequest request, HttpServletResponse response, SysPublicOpinionVO obj) {
        RetMsg retMsg = null;
        try {
            retMsg = sysPublicOpinionService.deleteByIds(obj);
        }catch (Exception e){
           retMsg.setCode(1);
           retMsg.setMessage("操作失败");
           logger.error("", e);
        }
        return retMsg;
    }

}
