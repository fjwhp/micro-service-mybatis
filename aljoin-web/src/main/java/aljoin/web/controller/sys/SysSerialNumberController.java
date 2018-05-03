package aljoin.web.controller.sys;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysDictCategory;
import aljoin.sys.dao.entity.SysSerialNumber;
import aljoin.sys.dao.object.SysSerialNumberDO;
import aljoin.sys.dao.object.SysSerialNumberVO;
import aljoin.sys.iservice.SysDictCategoryService;
import aljoin.sys.iservice.SysSerialNumberService;
import aljoin.web.controller.BaseController;

/**
 * 
 * @描述：流水号表(控制器).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-23
 */
@Controller
@RequestMapping("/sys/sysSerialNumber")
public class SysSerialNumberController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(SysSerialNumberController.class);
	@Resource
	private SysSerialNumberService sysSerialNumberService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private SysDictCategoryService sysDictCategoryService;
	@Resource
	private ActAljoinBpmnService actAljoinBpmnService;
	
	/**
	 * 
	 * @描述：流水号表(页面).
	 *
	 * @返回：String
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-23
	 */
	@RequestMapping("/sysSerialNumberPage")
	public String sysSerialNumberPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "sys/sysSerialNumberPage";
	}
	
	/**
	 * 
	 * @描述：流水号表(分页列表).
	 *
	 * @返回：Page<SysSerialNumber>
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-23
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<SysSerialNumberDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, SysSerialNumber obj) {
		Page<SysSerialNumberDO> newPage = null;
		try {
		    Where<SysSerialNumber> where = new Where<SysSerialNumber>();
		    if (null != obj && obj.getCategoryId() != null) {
		        where.eq("category_id", obj.getCategoryId());
		    }
		    if (null != obj && obj.getSerialNumName() != null) {
		        where.andNew();
		        where.like("serial_num_name", obj.getSerialNumName());
		    }
		    where.orderBy("create_time", false);
		    Page<SysSerialNumber> page = sysSerialNumberService.selectPage(new Page<SysSerialNumber>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		    List<SysSerialNumber> records = page.getRecords();
		    ArrayList<Long> categories = new ArrayList<Long>();
		    ArrayList<Long> userIds = new ArrayList<Long>();
		    HashMap<Long,String> serialNums = new HashMap<Long,String>();
		    if (null != records && !records.isEmpty()) {
		        for (SysSerialNumber sysSerialNumber : records) {
		            categories.add(sysSerialNumber.getCategoryId());
		            userIds.add(sysSerialNumber.getCreateUserId());
		            String serialNum = sysSerialNumberService.getSerialNum(sysSerialNumber);
		            serialNums.put(sysSerialNumber.getId(), serialNum);
		        }
		    }
		    //查询用户名称
		    List<AutUser> autUserList = null;
		    if(null != userIds && !userIds.isEmpty()){
		        Where<AutUser> userWhere = new Where<AutUser>();
		        userWhere.setSqlSelect("id,full_name");
		        userWhere.in("id", userIds);
		        autUserList = autUserService.selectList(userWhere);
		    }
		    //查询分类名称
		    List<SysDictCategory> sysDictCategoryList = null;
		    if(null != categories && !categories.isEmpty()){
		        Where<SysDictCategory> categoryWhere = new Where<SysDictCategory>();
		        categoryWhere.setSqlSelect("id,category_name");
		        categoryWhere.in("id", categories);
		        sysDictCategoryList = sysDictCategoryService.selectList(categoryWhere);
		    }
		    ArrayList<SysSerialNumberDO> list = new ArrayList<SysSerialNumberDO>();
		    for (SysSerialNumber sysSerialNumber : records) {
		        SysSerialNumberDO sysSerialNumberDO = new SysSerialNumberDO();
		        if (null != autUserList) {
		            for (AutUser autUser : autUserList) {
		                if (sysSerialNumber.getCreateUserId().longValue() == autUser.getId().longValue()) {
		                    sysSerialNumberDO.setFullName(autUser.getFullName());
		                }
		            }
		            for (SysDictCategory sysDictCategory : sysDictCategoryList) {
		                if (sysSerialNumber.getCategoryId().longValue() == sysDictCategory.getId().longValue()) {
		                    sysSerialNumberDO.setCategoryName(sysDictCategory.getCategoryName());
		                    sysSerialNumberDO.setCategoryId(sysSerialNumber.getCategoryId());
		                } 
		            }
		            sysSerialNumberDO.setSerialNum(serialNums.get(sysSerialNumber.getId()));
		            sysSerialNumberDO.setCreateTime(sysSerialNumber.getCreateTime());
		            sysSerialNumberDO.setId(sysSerialNumber.getId());
		            sysSerialNumberDO.setSerialNumName(sysSerialNumber.getSerialNumName());
		            if (1 == sysSerialNumber.getStatus()) {
		                sysSerialNumberDO.setStatusName("启用");
		                sysSerialNumberDO.setStatus(1);
		            }
		            if (0 == sysSerialNumber.getStatus()) {
		                sysSerialNumberDO.setStatusName("停用");
		                sysSerialNumberDO.setStatus(0);
		            }
		            list.add(sysSerialNumberDO);
		        }
		    }
		    newPage = new Page<SysSerialNumberDO>();
		    newPage.setRecords(list);
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
	 * @描述：流水号表(新增).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-23
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, SysSerialNumber obj) {
		RetMsg retMsg = new RetMsg();
		try {
            retMsg = sysSerialNumberService.add(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
	
	/**
	 * 
	 * @描述：流水号表(根据ID删除对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-23
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, SysSerialNumber obj) {
		RetMsg retMsg = new RetMsg();
		try {
            retMsg = sysSerialNumberService.delete(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
	
	/**
	 * 
	 * @描述：流水号表(根据ID修改对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-23
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, SysSerialNumber obj) {
		RetMsg retMsg = new RetMsg();
		try {
            retMsg = sysSerialNumberService.update(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
    
	/**
	 * 
	 * @描述：流水号表(根据ID获取对象).
	 *
	 * @返回：AutUser
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-23
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public SysSerialNumberVO getById(HttpServletRequest request, HttpServletResponse response, SysSerialNumber obj) {
	    SysSerialNumberVO sysSerialNumberVO = new SysSerialNumberVO();
	    if (null != obj) {
	        SysSerialNumber sysSerialNumber = sysSerialNumberService.selectById(obj.getId());
	        String serialNum = sysSerialNumberService.getSerialNum(sysSerialNumber);
	        BeanUtils.copyProperties(sysSerialNumber,sysSerialNumberVO);
	        sysSerialNumberVO.setSerialNum(serialNum);
	        String bpmnIds = sysSerialNumber.getBpmnIds();
	        if (StringUtils.isNotEmpty(bpmnIds) && !"null".equals(bpmnIds)) {
	            String[] bpmnIdArr = bpmnIds.split(";");
	            List<String> idList = Arrays.asList(bpmnIdArr);
	            List<ActAljoinBpmn> bpmns = actAljoinBpmnService.selectBatchIds(idList);
	            StringBuilder processNames = new StringBuilder();
	            if (null != bpmns && bpmns.size() > 0) {
	                for (ActAljoinBpmn bpmn : bpmns) {
	                    processNames.append(bpmn.getProcessName()).append(";");
	                }
	            }
	            sysSerialNumberVO.setProcessNames(processNames.toString());
	        }
	        if (StringUtils.isEmpty(bpmnIds) || "null".equals(bpmnIds)) {
	            sysSerialNumberVO.setProcessNames("无");
	        }
	    }
	    return sysSerialNumberVO;
	}
	
	/**
     * 
     * @描述：流水号表(根据ID修改对象).
     *
     * @返回：RetMsg
     *
     * @作者：caizx
     *
     * @时间：2018-03-23
     */
    @RequestMapping("/updateStatus")
    @ResponseBody
    public RetMsg updateStatus(HttpServletRequest request, HttpServletResponse response, SysSerialNumber obj) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = sysSerialNumberService.updateStatus(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }
    
    /**
     * 
     * @描述：修改当前值
     *
     * @返回：RetMsg
     *
     * @作者：caizx
     *
     * @时间：2018-03-23
     */
    @RequestMapping("/updateCurrentValue")
    @ResponseBody
    public RetMsg updateCurrentValue(HttpServletRequest request, HttpServletResponse response, SysSerialNumber obj) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = sysSerialNumberService.updateCurrentValue(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }
}
