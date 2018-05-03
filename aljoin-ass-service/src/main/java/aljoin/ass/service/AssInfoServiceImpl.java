package aljoin.ass.service;

import aljoin.ass.dao.entity.AssCategory;
import aljoin.ass.dao.entity.AssInfo;
import aljoin.ass.dao.mapper.AssInfoMapper;
import aljoin.ass.dao.object.AssInfoVO;
import aljoin.ass.iservice.AssCategoryService;
import aljoin.ass.iservice.AssInfoService;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.util.ExcelUtil;

/**
 * 
 * 固定资产信息表(服务实现类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-11
 */
@Service
public class AssInfoServiceImpl extends ServiceImpl<AssInfoMapper, AssInfo> implements AssInfoService {

  @Resource
  private AssInfoMapper mapper;
  @Resource
  private AssCategoryService assCategoryService;

  @Override
  public Page<AssInfoVO> list(PageBean pageBean, AssInfo obj) throws Exception {
	Where<AssInfo> where = new Where<AssInfo>();
	if (null != obj) {
      if (null != obj.getCategoryId()) {
//          where.eq("category_id", obj.getCategoryId());
    	  List<AssCategory> categorys = assCategoryService.getAllChildList(obj.getCategoryId());
          if(categorys.size() == 0){
            where.eq("category_id", obj.getCategoryId());
          }else{
            Set<Long> categoryIds = new HashSet<Long>();
            for (AssCategory category : categorys) {
              categoryIds.add(category.getId());
            }
            categoryIds.add(obj.getCategoryId());
            where.in("category_id", categoryIds);
          }
      }
      if (null != obj.getDepartmentName() && "" != obj.getDepartmentName()) {
        where.like("department_name", obj.getDepartmentName());
      }
      if (StringUtils.isNotEmpty(obj.getAssName())) {
          where.like("ass_name", obj.getAssName());
      }
      if (StringUtils.isNotEmpty(obj.getAgentName())) {
        where.like("agent_name", obj.getAgentName());
    }
  }
	where.orderBy("ass_code", true);
	where.setSqlSelect("id,ass_code,ass_name,category_name,category_id,agent_id,agent_name,department_id,department_name,ass_type,ass_number,in_place,already_use_time");
	Page<AssInfo> oldPage = selectPage(new Page<AssInfo>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	
	Page<AssInfoVO> page = new Page<AssInfoVO>();
	  List<AssInfo> assInfoList = oldPage.getRecords();
	  List<AssInfoVO> assInfoVOList = new ArrayList<AssInfoVO>();
	  if(null != assInfoList && !assInfoList.isEmpty()) {
	    int i = 0;
	    for (AssInfo assInfo : assInfoList) {
	      if(assInfo != null) {
	        AssInfoVO infoVo = new AssInfoVO();
	        infoVo.setId(assInfo.getId());
	        infoVo.setAssCode(assInfo.getAssCode());
	        infoVo.setAssName(assInfo.getAssName());
	        infoVo.setCategoryName(assInfo.getCategoryName());
	        infoVo.setAgentName(assInfo.getAgentName());
	        infoVo.setDepartmentName(assInfo.getDepartmentName());
	        infoVo.setAssType(assInfo.getAssType());
	        infoVo.setAssNumber(assInfo.getAssNumber());
	        infoVo.setInPlace(assInfo.getInPlace());
	        infoVo.setAlreadyUseTime(assInfo.getAlreadyUseTime());
	        infoVo.setNo(i);
	        assInfoVOList.add(infoVo);
	        i++;
	      }
	    }
	  }
	    page.setRecords(assInfoVOList);
	    page.setSize(oldPage.getSize());
	    page.setTotal(oldPage.getTotal());
	    page.setCurrent(oldPage.getCurrent());
	return page;
  }	
  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }

  @Override
  public void copyObject(AssInfo obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  public RetMsg add(AssInfo obj) {
    RetMsg retMsg = new RetMsg();
    if (null != obj) {
      if (null != obj.getAssName()) {
          Where<AssInfo> where = new Where<AssInfo>();
          where.eq("ass_name", obj.getAssName());
          int count = selectCount(where);
          Where<AssInfo> where1 = new Where<AssInfo>();
          where1.eq("ass_code", obj.getAssCode());
          int count1 = selectCount(where1);
          if (count > 0) {
              retMsg.setCode(1);
              retMsg.setMessage("固定资产已存在，不能重复增加");
              return retMsg;
          }
          if (count1 > 0) {
            retMsg.setCode(1);
            retMsg.setMessage("编号重复，请重新填写");
            return retMsg;
        }
      }
      insert(obj);
  }
  retMsg.setCode(0);
  retMsg.setMessage("操作成功");
  return retMsg;
  }

  @Override
  public RetMsg update(AssInfo obj) {
    RetMsg retMsg = new RetMsg();
    if (null != obj && null != obj.getId()) {
      AssInfo orgnlObj = selectById(obj.getId());
      if (null != obj.getAssName()) {
        orgnlObj.setAssName(obj.getAssName());
      }
      if (null != obj.getCategoryId()) {
        orgnlObj.setCategoryId(obj.getCategoryId());
      }
      if (null != obj.getCategoryName()) {
        orgnlObj.setCategoryName(obj.getCategoryName());
      }
      if (null != obj.getAssType()) {
        orgnlObj.setAssType(obj.getAssType());
      }
      if (null != obj.getAssCode()) {
        orgnlObj.setAssCode(obj.getAssCode());
      }
      if (null != obj.getUnit()) {
        orgnlObj.setUnit(obj.getUnit());
      }
      if (null != obj.getAssNumber()) {
        orgnlObj.setAssNumber(obj.getAssNumber());
      }
      if (null != obj.getInPlace()) {
        orgnlObj.setInPlace(obj.getInPlace());
      }
      if (null != obj.getAddTime()) {
        orgnlObj.setAddTime(obj.getAddTime());
      }
      if (null != obj.getDepartmentId()) {
        orgnlObj.setDepartmentId(obj.getDepartmentId());
      }
      if (null != obj.getDepartmentName()) {
        orgnlObj.setDepartmentName(obj.getDepartmentName());
      }
      if (null != obj.getAgentId()) {
        orgnlObj.setAgentId(obj.getAgentId());
      }
      if (null != obj.getAlreadyUseTime()) {
        orgnlObj.setAlreadyUseTime(obj.getAlreadyUseTime());
      }
      if (null != obj.getAgentName()) {
        orgnlObj.setAgentName(obj.getAgentName());
      }
      updateById(orgnlObj);
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public void export(HttpServletResponse response, AssInfoVO obj) throws Exception {
    if (null != obj) {
      Where<AssInfo> infoWhere = new Where<AssInfo>();
      infoWhere.setSqlSelect("id,ass_code,ass_name,ass_number,in_place,agent_name");
      String[] arr = obj.getIds().split(";");
      List<Long> ids = new ArrayList<Long>();
      for(int i = 0; i < arr.length; i++) {
        ids.add(Long.valueOf(arr[i]));
      }
      infoWhere.in("id", ids);
      List<AssInfo> gooInfoList = selectList(infoWhere);
      String[][] headers = {{ "AssCode", "编号" } , { "AssName", "名称" }, { "AssNumber", "剩余数量" }, 
              { "InPlace", "存放地点" },  { "AgentName", "负责人" }};
      List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();

      if (null != gooInfoList && !gooInfoList.isEmpty()) {
          if (!gooInfoList.isEmpty()) {
              for (AssInfo entity : gooInfoList) {
                  Map<String, Object> map = new HashMap<String, Object>();
                  for (int i = 0; i < headers.length; i++) {
                    AssInfo info = entity;
                      String methodName = "get" + headers[i][0];
                      Class<?> clazz = AssInfo.class;
                      Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
                      if (null != method) {
                          map.put(headers[i][0], method.invoke(info));
                      }
                  }
                  dataset.add(map);
              }
              ExcelUtil.exportExcel("固定资产盘点", headers, dataset, response);
          }
      }
  }
    
  }

@Override
public AssInfoVO getById(Long id) throws Exception {
	AssInfoVO gooInfovo = null;
    if(id != null) {
    	AssInfo gooInfo = selectById(id);
      if(gooInfo != null){
        gooInfovo = new AssInfoVO();
        BeanUtils.copyProperties(gooInfo, gooInfovo);
        List<AssCategory> categorys = assCategoryService.getAllParentCategoryList(gooInfo.getCategoryId());
        gooInfovo.setCategoryIds(categorys);
      }
    }
    return gooInfovo;
}
}
