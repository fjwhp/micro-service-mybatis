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
import aljoin.sys.dao.entity.SysDocumentNumber;
import aljoin.sys.dao.object.SysDocumentNumberDO;
import aljoin.sys.dao.object.SysDocumentNumberVO;
import aljoin.sys.iservice.SysDictCategoryService;
import aljoin.sys.iservice.SysDocumentNumberService;
import aljoin.web.controller.BaseController;

/**
 * 
 * @描述：公文文号表(控制器).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-26
 */
@Controller
@RequestMapping("/sys/sysDocumentNumber")
public class SysDocumentNumberController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(SysDocumentNumberController.class);
	@Resource
	private SysDocumentNumberService sysDocumentNumberService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private SysDictCategoryService sysDictCategoryService;
	@Resource
    private ActAljoinBpmnService actAljoinBpmnService;
	
	/**
	 * 
	 * @描述：公文文号表(页面).
	 *
	 * @返回：String
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-26
	 */
	@RequestMapping("/sysDocumentNumberPage")
	public String sysDocumentNumberPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "sys/sysDocumentNumberPage";
	}
	
	/**
	 * 
	 * @描述：公文文号表(分页列表).
	 *
	 * @返回：Page<SysDocumentNumber>
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-26
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<SysDocumentNumberDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, SysDocumentNumber obj) {
		Page<SysDocumentNumberDO> newpage = null;
		try {
		    Where<SysDocumentNumber> where = new Where<SysDocumentNumber>();
		      if (null != obj && obj.getCategoryId() != null) {
		          where.eq("category_id", obj.getCategoryId());
		      }
		      if (null != obj && obj.getDocumentNumName() != null) {
		          where.andNew();
		          where.like("document_num_name", obj.getDocumentNumName());
		      }
		      where.orderBy("create_time", false);
		      Page<SysDocumentNumber> page = sysDocumentNumberService.selectPage(new Page<SysDocumentNumber>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		      List<SysDocumentNumber> records = page.getRecords();
		      ArrayList<Long> categories = new ArrayList<Long>();
		      ArrayList<Long> userIds = new ArrayList<Long>();
		      HashMap<Long,String> documentNums = new HashMap<Long,String>();
		      if (null != records && !records.isEmpty()) {
		          for (SysDocumentNumber sysDocumentNumber : records) {
		              categories.add(sysDocumentNumber.getCategoryId());
		              userIds.add(sysDocumentNumber.getCreateUserId());
		              String documentNum = sysDocumentNumberService.getDocumentNum(sysDocumentNumber);
		              documentNums.put(sysDocumentNumber.getId(), documentNum);
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
		      ArrayList<SysDocumentNumberDO> list = new ArrayList<SysDocumentNumberDO>();
		      for (SysDocumentNumber sysDocumentNumber : records) {
		          SysDocumentNumberDO sysDocumentNumberDO = new SysDocumentNumberDO();
		          if (null != autUserList) {
		              for (AutUser autUser : autUserList) {
		                  if (sysDocumentNumber.getCreateUserId().longValue() == autUser.getId().longValue()) {
		                      sysDocumentNumberDO.setFullName(autUser.getFullName());
		                  }
		              }
		              for (SysDictCategory sysDictCategory : sysDictCategoryList) {
		                  if (sysDocumentNumber.getCategoryId().longValue() == sysDictCategory.getId().longValue()) {
		                      sysDocumentNumberDO.setCategoryName(sysDictCategory.getCategoryName());
		                      sysDocumentNumberDO.setCategoryId(sysDocumentNumber.getCategoryId());
		                  } 
		              }
		              sysDocumentNumberDO.setDocumentNum(documentNums.get(sysDocumentNumber.getId()));
		              sysDocumentNumberDO.setCreateTime(sysDocumentNumber.getCreateTime());
		              sysDocumentNumberDO.setId(sysDocumentNumber.getId());
		              sysDocumentNumberDO.setDocumentNumName(sysDocumentNumber.getDocumentNumName());
		              if (1 == sysDocumentNumber.getStatus()) {
		                  sysDocumentNumberDO.setStatusName("启用");
		                  sysDocumentNumberDO.setStatus(1);
		              }
		              if (0 == sysDocumentNumber.getStatus()) {
		                  sysDocumentNumberDO.setStatusName("停用");
		                  sysDocumentNumberDO.setStatus(0);
		              }
		              list.add(sysDocumentNumberDO);
		          }
		      }
		      Page<SysDocumentNumberDO> newPage = new Page<SysDocumentNumberDO>();
		      newPage.setRecords(list);
		      newPage.setCurrent(page.getCurrent());
		      newPage.setSize(page.getSize());
		      newPage.setTotal(page.getTotal());
		      return newPage;
		} catch (Exception e) {
			logger.error("",e);
		}
		return newpage;
	}
	

	/**
	 * 
	 * @描述：公文文号表(新增).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-26
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, SysDocumentNumberVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
            retMsg = sysDocumentNumberService.add(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
	
	/**
	 * 
	 * @描述：公文文号表(根据ID删除对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-26
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, SysDocumentNumber obj) {
	    RetMsg retMsg = new RetMsg();
        try {
            retMsg = sysDocumentNumberService.delete(obj);
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
	 * @时间：2018-03-26
	 */
	@RequestMapping("/updateCurrentValue")
	@ResponseBody
	public RetMsg updateCurrentValue(HttpServletRequest request, HttpServletResponse response, SysDocumentNumber obj) {
		RetMsg retMsg = new RetMsg();
        try {
            retMsg = sysDocumentNumberService.updateCurrentValue(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
	}
    
	/**
     * 
     * @描述：公文文号表(根据ID修改对象).
     *
     * @返回：RetMsg
     *
     * @作者：caizx
     *
     * @时间：2018-03-26
     */
    @RequestMapping("/update")
    @ResponseBody
    public RetMsg update(HttpServletRequest request, HttpServletResponse response, SysDocumentNumberVO obj) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = sysDocumentNumberService.update(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }
	/**
	 * 
	 * @描述：公文文号表(根据ID获取对象).
	 *
	 * @返回：SysDocumentNumberVO
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-26
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public SysDocumentNumberVO getById(HttpServletRequest request, HttpServletResponse response, SysDocumentNumber obj) {
	    SysDocumentNumberVO sysDocumentNumberVO = new SysDocumentNumberVO();
	      if (null != obj) {
	          SysDocumentNumber documentNumber = sysDocumentNumberService.selectById(obj.getId());
	          //文号
	          String documentNum = sysDocumentNumberService.getDocumentNum(documentNumber);
	          BeanUtils.copyProperties(documentNumber,sysDocumentNumberVO);
	          sysDocumentNumberVO.setDocumentNum(documentNum);
	          String bpmnIds = sysDocumentNumberVO.getBpmnIds();
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
	              sysDocumentNumberVO.setProcessNames(processNames.toString());
	          }
	          if (StringUtils.isEmpty(bpmnIds) || "null".equals(bpmnIds)) {
	              sysDocumentNumberVO.setProcessNames("无");
	          }
	          String documentNumPattern = documentNumber.getDocumentNumPattern();
	          String[] docNumPatterns = null;
	          if (StringUtils.isNotEmpty(documentNumPattern)) {
	             docNumPatterns = documentNumPattern.trim().split("&");
	          }
	          String[] docNumPattern = new String[3];
	          if (docNumPatterns.length == 1) {
	              docNumPattern[0] = docNumPatterns[0];
	              docNumPattern[1] = "";
	              docNumPattern[2] = "";
	          } else if (docNumPatterns.length == 2) {
	              docNumPattern[0] = docNumPatterns[0];
	              docNumPattern[1] = docNumPatterns[1];
	              docNumPattern[2] = "";
	          } else if (docNumPatterns.length == 3) {
	              docNumPattern[0] = docNumPatterns[0];
	              docNumPattern[1] = docNumPatterns[1];
	              docNumPattern[2] = docNumPatterns[2];
	          } else {
	              docNumPattern[0] = "";
	              docNumPattern[1] = "";
	              docNumPattern[2] = "";
	          }
	          sysDocumentNumberVO.setDocNumPattern(docNumPattern);
	      }
	      return sysDocumentNumberVO;
	}
	
	/**
     * 
     * @描述：修改文号状态
     *
     * @返回：RetMsg
     *
     * @作者：caizx
     *
     * @时间：2018-03-26
     */
    @RequestMapping("/updateStatus")
    @ResponseBody
    public RetMsg updateStatus(HttpServletRequest request, HttpServletResponse response, SysDocumentNumber obj) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = sysDocumentNumberService.updateStatus(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }
    

}
