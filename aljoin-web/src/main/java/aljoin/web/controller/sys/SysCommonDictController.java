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
import aljoin.sys.dao.entity.SysCommonDict;
import aljoin.sys.dao.object.SysCommonDictVO;
import aljoin.sys.iservice.SysCommonDictService;
import aljoin.web.controller.BaseController;

/**
 * 
 * @描述：常用字典表(控制器).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-18
 */
@Controller
@RequestMapping("/sys/sysCommonDict")
public class SysCommonDictController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(SysCommonDictController.class);
	@Resource
	private SysCommonDictService sysCommonDictService;
	@Resource
    private AutUserService autUserService;
	
	/**
	 * 
	 * @描述：常用字典表(页面).
	 *
	 * @返回：String
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-18
	 */
	@RequestMapping("/sysCommonDictPage")
	public String sysCommonDictPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "sys/sysCommonDictPage";
	}
	
	/**
	 * 
	 * @描述：常用字典表(分页列表).
	 *
	 * @返回：Page<SysCommonDict>
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-18
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<SysCommonDictVO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, SysCommonDict obj) {
		Page<SysCommonDictVO> newPage = null;
		try {
		    Where<SysCommonDict> where = new Where<SysCommonDict>();
		    if (obj != null && obj.getCategoryId() != null) {
		        where.eq("category_id", obj.getCategoryId());
		    }
		    if (obj.getDictName() != null) {
		        where.like("dict_name", obj.getDictName());
		    }
		    where.orderBy("dict_rank,id", true);
		    where.setSqlSelect("distinct id,dict_name,create_user_id,create_time,category_id,dict_rank,dict_content_rank,dict_code");
		    where.groupBy("dict_code,dict_name");
		    Page<SysCommonDict> page = sysCommonDictService.selectPage(new Page<SysCommonDict>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		    List<SysCommonDict> sysCommonDicts = page.getRecords();
		    ArrayList<Long> idList = new ArrayList<Long>();
		    for (SysCommonDict sysCommonDict : sysCommonDicts) {
		        idList.add(sysCommonDict.getCreateUserId());
		    }
		    //查询用户名称
		    List<AutUser> autUserList = null;
		    if(null != idList && !idList.isEmpty()){
		        Where<AutUser> userWhere = new Where<AutUser>();
		        userWhere.setSqlSelect("id,full_name");
		        userWhere.in("id", idList);
		        autUserList = autUserService.selectList(userWhere);
		    }
		    
		    //拼接常用意见和创建用户名称
		    ArrayList<SysCommonDictVO> voList = new ArrayList<SysCommonDictVO>();
		    for (SysCommonDict sysCommonDict : sysCommonDicts) {
		        if (!autUserList.isEmpty()) {
		            for(AutUser user : autUserList){
		                if(sysCommonDict.getCreateUserId().longValue() == user.getId().longValue()){
		                    SysCommonDictVO vo = new SysCommonDictVO();
		                    vo.setSysCommonDict(sysCommonDict);
		                    vo.setAutUser(user);
		                    voList.add(vo);
		                }
		            }
		        }
		    }
		    
		    newPage = new Page<SysCommonDictVO>();
		    newPage.setRecords(voList);
		    newPage.setCurrent(page.getCurrent());
		    newPage.setSize(page.getSize());
		    newPage.setTotal(page.getTotal());
		    return newPage;
		} catch (Exception e) {
		    logger.error("",e);
		}
		return newPage;
	}
	

	/**
	 * 
	 * @描述：常用字典表(新增).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-18
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, SysCommonDict obj, String[] dictContent, Integer[] dictContentRank) {
	    RetMsg retMsg = new RetMsg();
	    try {
            retMsg = sysCommonDictService.add(obj,dictContent,dictContentRank);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
	}
	
	/**
	 * 
	 * @描述：常用字典表(根据ID删除对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-18
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, SysCommonDict obj) {
		RetMsg retMsg = new RetMsg();
        sysCommonDictService.deleteById(obj.getId());
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
     * 
     * @描述：常用字典表(根据ID删除对象).
     *
     * @返回：RetMsg
     *
     * @作者：caizx
     *
     * @时间：2018-03-18
     */
    @RequestMapping("/deleteByDictCode")
    @ResponseBody
    public RetMsg deleteByDictCode(HttpServletRequest request, HttpServletResponse response, SysCommonDict obj) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = sysCommonDictService.deleteByDictCode(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }
	
	/**
	 * 
	 * @描述：常用字典表(根据ID修改对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-18
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, SysCommonDict obj, 
	    String[] dictContent, Integer[] dictContentRank,String[] commonDictId) {
		RetMsg retMsg = new RetMsg();
		try {
            retMsg = sysCommonDictService.update(obj,dictContent,dictContentRank,commonDictId);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
    
	/**
	 * 
	 * @描述：常用字典表(根据ID获取对象).
	 *
	 * @返回：SysCommonDict
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-18
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public SysCommonDict getById(HttpServletRequest request, HttpServletResponse response, SysCommonDict obj) {
		return sysCommonDictService.selectById(obj.getId());
	}
	
	/**
     * 
     * @描述：常用字典(根据分类名类及字典名称获取字典内容)
     *
     * @return：List<SysCommonDict>
     *
     * @author：caizx
     *
     * @date：2018-03-21
     */
    @RequestMapping("/getListByDictCode")
    @ResponseBody
    public List<SysCommonDict> getListByDictCode(HttpServletRequest request, HttpServletResponse response, SysCommonDict obj) {
        List<SysCommonDict> list = sysCommonDictService.getListByDictCode(obj);
        return list;
    }
}
