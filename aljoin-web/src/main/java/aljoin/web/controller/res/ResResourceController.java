package aljoin.web.controller.res;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.dao.entity.ResResourceType;
import aljoin.res.dao.object.ResResourceVO;
import aljoin.res.iservice.ResResourceService;
import aljoin.res.iservice.ResResourceTypeService;
import aljoin.sys.iservice.SysParameterService;
import aljoin.web.controller.BaseController;

/**
 * 
 * 资源管理表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-09-05
 */
@Controller
@RequestMapping(value = "/res/resResource")
public class ResResourceController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(ResResourceController.class);

	@Resource
	private ResResourceService resResourceService;
	@Resource
	private SysParameterService sysParameterService;
	@Resource
	private AutUserPubService autUserPubService;
	@Resource
    private AutUserService autUserService;
	@Resource
	private ResResourceTypeService resResourceTypeService;

	/**
	 * 
	 * 资源管理表(页面).
	 *
	 * @return：String
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-05
	 */
	@RequestMapping("/resResourcePage")
	public String resResourcePage(HttpServletRequest request, HttpServletResponse response) {

		return "res/resResourcePage";
	}

	/**
	 * 
	 * 资源管理表(分页列表).
	 *
	 * @return：Page<ResResource>
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-05
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ResResource> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			ResResource obj) {
		Page<ResResource> page = null;
		try {
			page = resResourceService.list(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	

	/**
	 * 
	 * 资源管理表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-05
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ResResource obj) {
		RetMsg retMsg = new RetMsg();
		try {
		    retMsg =resResourceService.delete(obj);
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage("删除失败");
        }
		return retMsg;
	}
	
	
	/**
	 * 
	 * 资源管理表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-05
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ResResource obj) {
		RetMsg retMsg = new RetMsg();
		try {
            retMsg = resResourceService.updateResource(obj);
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage("删除失败");
            logger.error("",e);
        }
		
		return retMsg;
	}

	/**
	 * 
	 * 资源管理表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-05
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ResResource getById(HttpServletRequest request, HttpServletResponse response, ResResource obj) {
		return resResourceService.selectById(obj.getId());
	}

	
	/**
	 *
	 * 资源管理-资源维护-保存上传文件(新增)
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月7日 下午4:19:58
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(ResResourceVO resourceVo) {

		RetMsg retMsg = new RetMsg();
		try {
			if (resourceVo != null) {
				retMsg = resResourceService.addResourceList(resourceVo);
			} else {
				retMsg.setCode(0);
				retMsg.setMessage("请选择要上传的文件");
			}
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 返回VO分页对象，包含ResResource相关字段及ResResourceType的typeName
	 *
	 * @return：Page<ResResourceVO>
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月20日 下午6:39:43
	 */
	@RequestMapping("/pageList")
	@ResponseBody
	public Page<ResResourceVO> pageList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			ResResourceVO obj) {

		Page<ResResourceVO> pageList = null;
		try {
		    Where<ResResource> w1 = new Where<ResResource>();
	        //判断对象非空
	        if(StringUtils.isNotEmpty(obj.getOrgnlFileName()) || StringUtils.isNotEmpty(obj.getFileDesc())){
                w1.like("orgnl_file_name", obj.getOrgnlFileName()); 
                w1.or("file_desc LIKE {0}","%" + obj.getFileDesc() +"%");
            }
	        w1.orderBy("create_time", false);
	        Page<ResResource> page = resResourceService.selectPage(new Page<ResResource>(pageBean.getPageNum(), pageBean.getPageSize()),w1);
	        List<ResResource> resList = page.getRecords();
	        //根据create_user_id查询aut_user表，以获得full_name
	        List<Long> autUserIdList = new ArrayList<Long>();
	        for(ResResource res : resList){
	            autUserIdList.add(res.getCreateUserId());
	        }
	        //根据userIdList，查询用户信息
	        Where<AutUser> w2 = new Where<AutUser>();
	        w2.setSqlSelect("id,user_name,full_name");
	        w2.in("id", autUserIdList);
	        List<AutUser> autUserList = autUserService.selectList(w2);
	        //比对是同一个用户的时候,把用户姓名set进resList的create_user_name用于页面显示
	        for(int i=0;i<resList.size();i++){
	            for(int j=0;j<autUserList.size();j++){
	                if(resList.get(i).getCreateUserId().equals(autUserList.get(j).getId())){
	                    resList.get(i).setCreateUserName(autUserList.get(j).getFullName());
	                }
	            }
	        }
	        //新建VO分页对象pageList
	        pageList = new Page<ResResourceVO>();
	        pageList.setCurrent(page.getCurrent());
	        pageList.setSize(page.getSize());
	        pageList.setTotal(page.getTotal());
	        //利用找到的typeId查询资源分类表
	        List<Long> typeIdList = new ArrayList<Long>();
	        for(ResResource res : resList){
	            typeIdList.add(res.getFileTypeId());
	        }
	        Where<ResResourceType> w3 = new Where<ResResourceType>();
	        w3.setSqlSelect("id,type_name");
	        w3.in("id", typeIdList);
	        List<ResResourceType> resTypeList = resResourceTypeService.selectList(w3);
	        //把res表和resType表查到的数据比对，当typeId相同的时候add进voList
	        List<ResResourceVO> voList = new ArrayList<ResResourceVO>();
	        for(int i=0;i<resList.size();i++){
	            for(int j=0;j<resTypeList.size();j++){
	                if(resList.get(i).getFileTypeId().equals(resTypeList.get(j).getId())){
	                    ResResourceVO vo = new ResResourceVO();
	                    ResResourceType resType = new ResResourceType();
	                    ResResource res = new ResResource();
	                    BeanUtils.copyProperties(resList.get(i), res);
	                    res.setCreateTime(resList.get(i).getCreateTime());
	                    resType.setId(resTypeList.get(j).getId());
	                    resType.setTypeName(resTypeList.get(j).getTypeName());
	                    vo.setResResource(res);
	                    vo.setResResourceType(resType);
	                    voList.add(vo);
	                }
	            }
	        }
	        
	        pageList.setRecords(voList);
		} catch (Exception e) {
			logger.error("", e);
		}
		return pageList;
	}

}
