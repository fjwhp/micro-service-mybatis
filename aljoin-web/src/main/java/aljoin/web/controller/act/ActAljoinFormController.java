package aljoin.web.controller.act;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.act.dao.entity.ActAljoinForm;
import aljoin.act.dao.object.ActAljoinFormCategoryVO;
import aljoin.act.dao.object.ActAljoinFormShowVO;
import aljoin.act.dao.object.ActAljoinFormVO;
import aljoin.act.dao.source.BaseSource;
import aljoin.act.iservice.ActAljoinFormService;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutPost;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.aut.dao.object.AutOrganVO;
import aljoin.aut.dao.object.AutPostVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutPostService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysCommonDict;
import aljoin.sys.dao.entity.SysDictCategory;
import aljoin.sys.dao.entity.SysDocumentNumber;
import aljoin.sys.dao.entity.SysSerialNumber;
import aljoin.sys.iservice.SysCommonDictService;
import aljoin.sys.iservice.SysDictCategoryService;
import aljoin.sys.iservice.SysDocumentNumberService;
import aljoin.sys.iservice.SysSerialNumberService;
import aljoin.web.controller.BaseController;
import aljoin.web.service.act.ActAljoinDataSourceService;

/**
 * 
 * 表单表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-08-31
 */
@Controller
@RequestMapping("/act/actAljoinForm")
public class ActAljoinFormController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinFormController.class);
	@Resource
	private ActAljoinFormService actAljoinFormService;
	@Resource
	private AutDepartmentUserService autDepartmentUserService;
	@Resource
	private AutDepartmentService autDepartmentService;
	@Resource 
	private AutPositionService autPositionService;
	@Resource
	private AutUserPositionService autUserPositionService;
	@Resource
	private ActAljoinDataSourceService actAljoinDataSourceService;
	@Resource
	private SysCommonDictService sysCommonDictService;
	@Resource
	private SysDictCategoryService sysDictCategoryService;
	@Resource
    private SysSerialNumberService sysSerialNumberService;
	@Resource
	private SysDocumentNumberService sysDocumentNumberService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private AutPostService autPostService;
	/**
	 * 
	 * 表单表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/actAljoinFormPage")
	public String actAljoinFormPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinFormPage";
	}
	
	/**
	 * 
	 * 表单表(分页列表).
	 *
	 * @return：Page<ActAljoinForm>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinFormCategoryVO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinForm obj) {
		Page<ActAljoinFormCategoryVO> page = null;
		try {
			page = actAljoinFormService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	/**
	 * 
	 * 表单表(分页列表).
	 *
	 * @return：Page<ActAljoinForm>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/retrunList")
	@ResponseBody
	public Page<ActAljoinFormCategoryVO> retrunList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinForm obj) {
		Page<ActAljoinFormCategoryVO> page = null;
		try {
			page = actAljoinFormService.retrunList(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 表单表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response,ActAljoinFormShowVO form) {
		RetMsg retMsg = new RetMsg();
		Map<String,Object> map=new HashMap<String,Object>();
		if(null  != form.getAf()){
			map.put("form_name", form.getAf().getFormName());
			//验证表单名是否唯一
			List<ActAljoinForm> list=actAljoinFormService.selectByMap(map);
			if (null != form.getAf().getId()) {
			    try {
                    actAljoinFormService.update(form);
                    retMsg.setCode(0);
                    retMsg.setObject(String.valueOf(form.getAf().getId()));
                    retMsg.setMessage("操作成功！");
                } catch (Exception e) {
                    retMsg.setCode(1);
                    logger.error("", e);
                }
            } else {
                if(list.isEmpty()){
                    try {
                        String formId = actAljoinFormService.add(form);
                        retMsg.setCode(0);
                        retMsg.setObject(formId);
                        retMsg.setMessage("操作成功！");
                    } catch (Exception e) {
                        retMsg.setCode(1);
                        logger.error("", e);
                    }
                }else{
                    retMsg.setCode(1);
                    retMsg.setMessage("表单名称重复！");
                }
            }
		}else{
			retMsg.setCode(1);
			retMsg.setMessage("表单数据不存在");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 表单表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinForm obj) {
		RetMsg retMsg = new RetMsg();
		try {
			actAljoinFormService.delete(obj);
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
	 * 表单表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/update") 
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinFormShowVO form) {
		RetMsg retMsg = new RetMsg();
		try {
			Where<ActAljoinForm> where = new Where<ActAljoinForm>();
			where.eq("form_name", form.getAf().getFormName());
			where.ne("id", form.getAf().getId());
			if(actAljoinFormService.selectCount(where) > 0){
				retMsg.setCode(1);
				retMsg.setMessage("表单名称重复！");
			}else{
				actAljoinFormService.update(form);
				retMsg.setCode(0);
				retMsg.setMessage("操作成功");
			}
		} catch (Exception e) {
			retMsg.setCode(1);
			logger.error("", e);
		}
		return retMsg;
	}
    
	/**
	 * 
	 * 表单表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinForm getById(HttpServletRequest request, HttpServletResponse response, ActAljoinForm obj) {
		return actAljoinFormService.selectById(obj.getId());
	}
	
	/**
	 * 
	 * 表单表(根据表单分类获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：pengsp
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/getAllForm")
	@ResponseBody
	public List<ActAljoinForm> getAllForm(ActAljoinForm obj) throws Exception {
		return actAljoinFormService.getAllForm(obj.getCategoryId());
	}
	
	/**
	 * 
	 * 表单表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/actAljoinFormUpdatePage") 
	public String actAljoinFormUpdatePage(HttpServletRequest request,HttpServletResponse response) {
		return "act/actAljoinFormUpdatePage";
	}
	
	/**
	 * 
	 * 表单表(根据表单ID预览所有信息).
	 *
	 * @return：AutUser
	 *
	 * @author：pengsp
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/getAllById")
	@ResponseBody
	public ActAljoinFormShowVO getAllById(ActAljoinForm obj) throws Exception {
		return actAljoinFormService.getAllById(obj.getId());
	}
	
	/**
	 * 
	 * 表单修改(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/formModifyPage")
	public String formModifyPage(HttpServletRequest request,HttpServletResponse response,ActAljoinForm obj) {
		request.setAttribute("orgnl_id", obj.getId());
		return "act/formModify";
	}
	
	/**
	 * 
	 * 系统数据源列表
	 *
	 * @return：List<BaseSource>
	 *
	 * @author：pengsp
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/getAllType")
	@ResponseBody
	public List<BaseSource> getAllType(HttpServletRequest request,HttpServletResponse response,String type) throws Exception{
		return actAljoinFormService.getAllType(type);
	}
	
	/**
	 * 
	 * 系统数据源数据
	 *
	 * @return：List<Object>
	 *
	 * @author：pengsp
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/getAllSource")
	@ResponseBody
	public List<Object> getAllSource(HttpServletRequest request,HttpServletResponse response,String type) throws Exception{
		Map<String, String> paramMap=new HashMap<String, String>();
		CustomUser customUser = getCustomDetail();
		//查询用户所在部门
		Where<AutDepartmentUser> where=new Where<AutDepartmentUser>();
		where.eq("user_id", customUser.getUserId());
		where.setSqlSelect("dept_id");
		where.groupBy("dept_id");
		List<AutDepartmentUser> autDepartmentUserList=autDepartmentUserService.selectList(where);

		List<String> deptIds=new ArrayList<String>();
		for(AutDepartmentUser autDepartmentUser:autDepartmentUserList){
			deptIds.add(autDepartmentUser.getDeptId().toString());
		}
		//查询部门名称
		if(deptIds.size() > 0){
		  List<AutDepartment> autDepartmentList=autDepartmentService.selectBatchIds(deptIds);
	        String deptNames="";
	        for(AutDepartment autDepartment:autDepartmentList){
	            deptNames=autDepartment.getDeptName()+","+deptNames;
	        }
	        if(StringUtils.isNotEmpty(deptNames)){
	        	deptNames = deptNames.substring(0,deptNames.length() - 1);
	        }
	        paramMap.put("deptName", deptNames);
		}
		//查询用户岗位
		Where<AutUserPosition> userPositionWhere = new Where<AutUserPosition>();
		userPositionWhere.eq("user_id", customUser.getUserId());
		userPositionWhere.setSqlSelect("position_id");
		userPositionWhere.groupBy("position_id");
        List<AutUserPosition> autUserPositionList = autUserPositionService.selectList(userPositionWhere);
        
        List<String> positionIds = new ArrayList<String>();
        for(AutUserPosition autUserPosition:autUserPositionList){
            positionIds.add(autUserPosition.getPositionId().toString());
        }
        //查询用户岗位名称
        if(positionIds.size() > 0){
            String positionNames="";
            List<AutPosition> autPositionList = autPositionService.selectBatchIds(positionIds);
            for(AutPosition autPosition : autPositionList){
                positionNames = autPosition.getPositionName()+","+positionNames;
            }
            if(StringUtils.isNotEmpty(positionNames)){
                positionNames = positionNames.substring(0,positionNames.length() - 1);
            }
            paramMap.put("positionNames", positionNames);
        }
		paramMap.put("userName", customUser.getNickName());
		return actAljoinDataSourceService.getAllSource(type,paramMap);
	}
	
	/**
	 * 表单表(复制).
	 *
	 * @return：RetMsg
	 *
	 * @author：caizx
	 *
	 * @date：2018-03-02
	 */
	@RequestMapping("/copy")
	@ResponseBody
	public RetMsg copy(HttpServletRequest request, HttpServletResponse response,ActAljoinForm obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = actAljoinFormService.copy(obj);
		} catch (Exception e) {
			retMsg.setCode(1);
		    retMsg.setMessage(e.getMessage());
		    logger.error("", e);
		}
		return retMsg;
	}
	
	/**
	 * 表单表(导出).
	 *
	 * @return：RetMsg
	 *
	 * @author：caizx
	 *
	 * @date：2018-03-03
	 */
	@RequestMapping(value="/export",method={RequestMethod.GET})
	@ResponseBody
	public void export(HttpServletRequest request, HttpServletResponse response,ActAljoinFormVO obj) {
	    try {
			actAljoinFormService.export(response, obj);
		} catch (Exception e) {
		    logger.error("", e);
		}
	}
	
	/**
     * 
     * 表单表(导入).
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-03-03
     */
    @RequestMapping("/fileImport")
    @ResponseBody
    public RetMsg fileImport(MultipartHttpServletRequest request) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = actAljoinFormService.fileImport(request);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }
    
    /**
     * 
     * 表单表(导入).
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-03-03
     */
    @RequestMapping("/fileSubmit")
    @ResponseBody
    public RetMsg fileSubmit(ActAljoinFormVO form) {
        RetMsg retMsg = new RetMsg();
        HashMap<String, Object> map = new HashMap<String, Object>();
        try { 
            map.put("form_name", form.getAf().getFormName());
            //验证表单名是否唯一
            List<ActAljoinForm> list=actAljoinFormService.selectByMap(map);
            if(!list.isEmpty()){
                retMsg.setCode(1);
                retMsg.setMessage("表单名称重复");
                return retMsg;
            }
            if (form.getAf().getFormName().length() > 100) {
                retMsg.setCode(1);
                retMsg.setMessage("表单名称过长");
                return retMsg;
            }
            retMsg = actAljoinFormService.fileSubmit(form);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }
    
    /**
     * 
     * 获取字典分类
     *
     * @return：List<SysDictCategory>
     *
     * @author：caizx
     *
     * @date：2018-03-31
     */
    @RequestMapping("/getDictCategoryList")
    @ResponseBody
    public List<SysDictCategory> getDictCategoryList(HttpServletRequest request, HttpServletResponse response, SysDictCategory obj) {
        return sysDictCategoryService.getCategoryList(obj);
    }
    
    /**
     * 
     * 获取常用字典分类下的字典名称
     *
     * @return：List<SysCommonDict>
     *
     * @author：caizx
     *
     * @date：2018-03-31
     */
    @RequestMapping("/getCommonListByCategory")
    @ResponseBody
    public List<SysCommonDict> getCommonListByCategory(HttpServletRequest request, HttpServletResponse response, SysCommonDict obj) {
        return sysCommonDictService.getListByCategory(obj);
    }
    
    /**
     * 
     * 获取常用字典内容
     *
     * @return：List<SysCommonDict>
     *
     * @author：caizx
     *
     * @date：2018-03-31
     */
    @RequestMapping("/getCommonContent")
    @ResponseBody
    public List<SysCommonDict> getCommonContent(HttpServletRequest request, HttpServletResponse response, SysCommonDict obj) {
        return sysCommonDictService.getListByDictCode(obj);
    }
    
    /**
     * 
     * 获取流水号列表
     *
     * @return：List<SysSerialNumber>
     *
     * @author：caizx
     *
     * @date：2018-03-31
     */
    @RequestMapping("/getSerialNumberList")
    @ResponseBody
    public List<SysSerialNumber> getSerialNumberList(HttpServletRequest request, HttpServletResponse response, SysSerialNumber obj) {
        return sysSerialNumberService.getList(obj);
    }
    
    /**
     * 
     * 通过流水号id获取流水号
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-03-31
     */
    @RequestMapping("/getSerialNumber")
    @ResponseBody
    public RetMsg getSerialNumber(HttpServletRequest request, HttpServletResponse response, SysSerialNumber obj) {
        return sysSerialNumberService.getSerialNumber(obj);
    }
    /**
     * 
     * 通过流水号id获取预览流水号
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-03-31
     */
    @RequestMapping("/getPreviewSerialNumber")
    @ResponseBody
    public RetMsg getPreviewSerialNumber(HttpServletRequest request, HttpServletResponse response, SysSerialNumber obj) {
        return sysSerialNumberService.getPreviewSerialNumber(obj);
    }
    
    /**
     * 
     * 获取文号列表
     *
     * @return：List<SysDocumentNumber>
     *
     * @author：caizx
     *
     * @date：2018-03-31
     */
    @RequestMapping("/getDocumentNumberList")
    @ResponseBody
    public List<SysDocumentNumber> getDocumentNumberList(HttpServletRequest request, HttpServletResponse response, SysDocumentNumber obj) {
        return sysDocumentNumberService.getList(obj);
    }
    
    /**
     * 
     * @描述：根据文号id获得文号
     *
     * @返回：RetMsg
     *
     * @作者：caizx
     *
     * @时间：2018-03-23
     */
    @RequestMapping("/getDocumentNumber")
    @ResponseBody
    public RetMsg getDocumentNumber(SysDocumentNumber obj) {
       return sysDocumentNumberService.getDocumentNumber(obj);
    }
    
    /**
     * 
     * @描述：根据文号id获得预览文号
     *
     * @返回：RetMsg
     *
     * @作者：caizx
     *
     * @时间：2018-03-23
     */
    @RequestMapping("/getPreviewDocumentNumber")
    @ResponseBody
    public RetMsg getPreviewDocumentNumber(SysDocumentNumber obj) {
       return sysDocumentNumberService.getPreviewDocumentNumber(obj);
    }
    /**
     * 获得用户部门列表
     *
     * @return：List<Map<Long, Object>>
     *
     * @author：caizx
     *
     * @date：2018年4月04日 下午15:23:37
     */
    @RequestMapping("/getUserDeptList")
    @ResponseBody
    public List<Map<Long, Object>> getUserDeptList(HttpServletRequest request, HttpServletResponse response) {
        List<Map<Long, Object>> userDeptList = autUserService.getUserDeptList();
        return userDeptList;
    }
    
    /**
     * 获得组织机构树
     *
     * @return：AutOrganVO
     *
     * @author：caizx
     *
     * @date：2018年4月04日 下午15:23:37
     */
    @RequestMapping("/organList")
    @ResponseBody
    public AutOrganVO organList(HttpServletRequest request, HttpServletResponse respons,AutDepartmentUserVO departmentUser) {
        AutOrganVO organVO  = null;
        try {
            organVO  = autDepartmentUserService.getOrganList(departmentUser);
        } catch (Exception e) {
          logger.error("", e);
        }
        return organVO;
    }
    
    /**
     * 获得部门列表
     *
     * @return：List<AutDepartment>
     *
     * @author：caizx
     *
     * @date：2018年4月04日 下午15:23:37
     */
    @RequestMapping("/getDepartmentList")
    @ResponseBody
    public List<AutDepartment> getDepartmentList(HttpServletRequest request, HttpServletResponse response) {
        List<AutDepartment> list = autDepartmentService.getDepartmentList();
        return list;
    }
    
    /**
     * 
     * @描述：岗位表(获得用户-岗位列表).
     *
     * @返回：List<AutPost>
     *
     * @作者：caizx
     *
     * @时间：2018-04-09
     */
    @RequestMapping("/getUserPostList")
    @ResponseBody
    public List<AutPostVO> getUserPostList(HttpServletRequest request, HttpServletResponse response) {
        List<AutPostVO> userPostList = autPostService.getUserPostList();
        return userPostList;
    }
    
    /**
     * 
     * @描述：岗位表(获得岗位列表).
     *
     * @返回：List<AutPost>
     *
     * @作者：caizx
     *
     * @时间：2018-04-09
     */
    @RequestMapping("/getPostList")
    @ResponseBody
    public List<AutPost> getPostList(HttpServletRequest request, HttpServletResponse response, AutPost obj) {
        Where<AutPost> where = new Where<AutPost>();
        where.orderBy("post_rank,id",true);
        return autPostService.selectList(where);
    }
}
