package aljoin.goo.service;

import aljoin.goo.dao.entity.GooCategory;
import aljoin.goo.dao.entity.GooInOut;
import aljoin.goo.dao.entity.GooInfo;
import aljoin.goo.dao.mapper.GooInfoMapper;
import aljoin.goo.dao.object.GooInfoDO;
import aljoin.goo.dao.object.GooInfoVO;
import aljoin.goo.iservice.GooCategoryService;
import aljoin.goo.iservice.GooInOutService;
import aljoin.goo.iservice.GooInfoService;

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

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.plugins.Page;

import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.util.ExcelUtil;

/**
 * 
 * 办公用品信息表(服务实现类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-04
 */
@Service
public class GooInfoServiceImpl extends ServiceImpl<GooInfoMapper, GooInfo> implements GooInfoService {

  @Resource
  private GooInfoMapper mapper;
  @Resource
  private GooInOutService gooInOutService;
  @Resource
  private GooCategoryService gooCategoryService;

  @Override
  public Page<GooInfoDO> list(PageBean pageBean, GooInfoVO obj) throws Exception {
	Where<GooInfo> where = new Where<GooInfo>();
	if (null != obj) {
      if (null != obj.getCategoryId()) {
//          where.eq("category_id", obj.getCategoryId());
    	  List<GooCategory> categorys = gooCategoryService.getAllChildList(obj.getCategoryId());
          if(categorys.size() == 0){
            where.eq("category_id", obj.getCategoryId());
          }else{
            Set<Long> categoryIds = new HashSet<Long>();
            for (GooCategory category : categorys) {
              categoryIds.add(category.getId());
            }
            categoryIds.add(obj.getCategoryId());
            where.in("category_id", categoryIds);
          }
      }
      if (StringUtils.isNotEmpty(obj.getName())) {
          where.like("name", obj.getName());
      }
      if (null != obj.getStatus()) {
          where.eq("status", obj.getStatus());
      }
  }
  where.setSqlSelect(
          "id,name,category_id,status,content,number,emer_num,unit,goo_code");
  where.orderBy("goo_rank", true);
  Page<GooInfo> oldPage = selectPage(new Page<GooInfo>(pageBean.getPageNum(), pageBean.getPageSize()),
          where);

  Page<GooInfoDO> page = new Page<GooInfoDO>();
  List<GooInfo> gooInfoList = oldPage.getRecords();
  List<GooInfoDO> gooInfoDOList = new ArrayList<GooInfoDO>();
  List<Long> categoryIdList = new ArrayList<Long>();
  if(gooInfoList != null && !gooInfoList.isEmpty()) {
    int i= 0;
    for (GooInfo gooInfo : gooInfoList) {
      if(gooInfo != null) {
        GooInfoDO infoDo = new GooInfoDO();
        infoDo.setId(gooInfo.getId());
        infoDo.setUnit(gooInfo.getUnit());
        infoDo.setCategoryId(gooInfo.getCategoryId());
        infoDo.setGooCode(gooInfo.getGooCode());
        infoDo.setGooName(gooInfo.getName());
        infoDo.setNumber(gooInfo.getNumber());
        infoDo.setEmerNum(gooInfo.getEmerNum());
        infoDo.setNo(i);
        infoDo.setStatus(gooInfo.getStatus());
        categoryIdList.add(gooInfo.getCategoryId());
        gooInfoDOList.add(infoDo);
        i++;
      }
    }
    if (null != categoryIdList && !categoryIdList.isEmpty()) {
      Where<GooCategory> categoryWhere = new Where<GooCategory>();
      categoryWhere.eq("is_active", 1);
      categoryWhere.in("id", categoryIdList);
      List<GooCategory> categoryList = gooCategoryService
              .selectList(categoryWhere);
      if (null != categoryList && !categoryList.isEmpty()) {
          for (GooInfoDO publicInfoDO : gooInfoDOList) {
              for (GooCategory category : categoryList) {
                  if (null != category && null != publicInfoDO) {
                      if (StringUtils.isNotEmpty(category.getName())) {
                          if ((null != category.getId() && null != publicInfoDO.getCategoryId())) {
                              if (category.getId().equals(publicInfoDO.getCategoryId()) && category.getId()
                                      .intValue() == publicInfoDO.getCategoryId().intValue()) {
                                  publicInfoDO.setCategoryName(category.getName());
                              }
                          }
                      }
                  }
              }
          }
      }
    }
    page.setRecords(gooInfoDOList);
    page.setSize(oldPage.getSize());
    page.setTotal(oldPage.getTotal());
    page.setCurrent(oldPage.getCurrent());
  }
	return page;
  }	

  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


  @Override
  public void copyObject(GooInfo obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  public RetMsg add(GooInfo obj) {
    RetMsg retMsg = new RetMsg();
    
    if (StringUtils.isEmpty(obj.getContent())) {
      obj.setContent("");
      retMsg.setCode(1);
      retMsg.setMessage("简介不能为空");
      return retMsg;
  }
  if (null == obj.getCategoryId()) {
      obj.setCategoryId(0L);
      retMsg.setCode(1);
      retMsg.setMessage("分类不能为空");
      return retMsg;
  }
  if (StringUtils.isEmpty(obj.getName())) {
      obj.setName("");
      retMsg.setCode(1);
      retMsg.setMessage("物品名称不能为空");
      return retMsg;
  } 
  if (obj.getNumber() == null) {
    obj.setNumber(0);
} 
  if (obj.getStatus() == null) {
    obj.setStatus(0);
} 
  if(obj.getNumber() <= obj.getEmerNum()) {
    obj.setStatus(1);
  }
  if (null == obj.getGooCode()) {
      obj.setGooCode("");
      retMsg.setCode(1);
      retMsg.setMessage("编号不能为空");
      return retMsg;
  }
  if (null == obj.getEmerNum()) {
    obj.setEmerNum(0);
    retMsg.setCode(1);
    retMsg.setMessage("预警值不能为空");
    return retMsg;
  }
  if (null == obj.getUnit()) {
    obj.setUnit("");
    retMsg.setCode(1);
    retMsg.setMessage("单位不能为空");
    return retMsg;
  }
  if (null == obj.getGooRank()) {
    obj.setGooRank(0);
    retMsg.setCode(1);
    retMsg.setMessage("排序不能为空");
    return retMsg;
  }
    if (null != obj) {
        if (null != obj.getName()) {
            Where<GooInfo> where = new Where<GooInfo>();
            where.like("name", obj.getName(),SqlLike.CUSTOM);
            int count = selectCount(where);
            Where<GooInfo> where1 = new Where<GooInfo>();
            where1.eq("goo_code", obj.getGooCode());
            int count1 = selectCount(where1);
            if (count > 0) {
                retMsg.setCode(1);
                retMsg.setMessage("办公用品已存在，不能重复增加");
                return retMsg;
            }
            if (count1 > 0) {
              retMsg.setCode(1);
              retMsg.setMessage("办公用品编号重复，请重新填写");
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
  public GooInfoVO getById(Long id) throws Exception {
    GooInfoVO gooInfovo = null;
    if(id != null) {
      GooInfo gooInfo = selectById(id);
      if(gooInfo != null){
        gooInfovo = new GooInfoVO();
        BeanUtils.copyProperties(gooInfo, gooInfovo);
        Where<GooInOut> inOutWhere = new Where<GooInOut>();
        inOutWhere.eq("goo_id",gooInfovo.getId());
        inOutWhere.setSqlSelect("id,list_code,process_name,number,audit_time,publish_name,in_out_status");
        inOutWhere.eq("audit_status", 3);
        inOutWhere.orderBy("audit_time",false);
        List<GooInOut> inOutList = gooInOutService.selectList(inOutWhere);
        List<GooInOut> inList = new ArrayList<GooInOut>();
        List<GooInOut> outList = new ArrayList<GooInOut>();
        List<GooInOut> intOutList1 = new ArrayList<GooInOut>();
        if(inOutList != null && !inOutList.isEmpty() && inOutList.size() > 10) {
          for (GooInOut gooInOut : inOutList) {
            //入库出库
            if(gooInOut.getInOutStatus() == 1 || gooInOut.getInOutStatus() == 2) {
              inList.add(gooInOut);
            }else {
              outList.add(gooInOut);
            }
          }
          //判断出入库条数，如果大于十条，就截取
          if(inList.size() <= 10) {
            intOutList1.addAll(inList);
          }else {
            List<GooInOut> newInList = inList.subList(0, 10);
            intOutList1.addAll(newInList);
          }
          if(outList.size() <= 10) {
            intOutList1.addAll(outList);
          }else {
            List<GooInOut> newOutList = outList.subList(0, 10);
            intOutList1.addAll(newOutList);
          }
          gooInfovo.setGooInOut(intOutList1);
        }else if(inOutList != null && !inOutList.isEmpty() && inOutList.size() <= 10) {
          gooInfovo.setGooInOut(inOutList);
        }
        List<GooCategory> categorys = gooCategoryService.getAllParentCategoryList(gooInfo.getCategoryId());
        gooInfovo.setCategoryIds(categorys);
      }
    }
    return gooInfovo;
  }

  @Override
  public RetMsg update(GooInfo obj) {
    RetMsg retMsg = new RetMsg();
    if (null != obj && null != obj.getId()) {
      GooInfo orgnlObj = selectById(obj.getId());
        if (null != obj.getIsActive()) {
            orgnlObj.setIsActive(obj.getIsActive());
        }
        if (null != obj.getCategoryId()) {
          orgnlObj.setCategoryId(obj.getCategoryId());
        }
        if (null != obj.getContent()) {
          orgnlObj.setContent(obj.getContent());
        }
        if (null != obj.getGooCode()) {
          if (!obj.getGooCode().equals(orgnlObj.getGooCode())) {
            Where<GooInfo> where = new Where<GooInfo>();
            where.eq("goo_code", obj.getGooCode());
            int count = selectCount(where);
            if (count > 0) {
                retMsg.setCode(1);
                retMsg.setMessage("办公用品编号重复，请重新填写");
                return retMsg;
            }
        }
          orgnlObj.setGooCode(obj.getGooCode());
        }
        if (null != obj.getUnit()) {
          orgnlObj.setUnit(obj.getUnit());
        }
        if (null != obj.getEmerNum()) {
          orgnlObj.setEmerNum(obj.getEmerNum());
        }
        if (StringUtils.isNotEmpty(obj.getName())) {
            if (!obj.getName().equals(orgnlObj.getName())) {
                Where<GooInfo> where = new Where<GooInfo>();
                where.like("name", obj.getName(),SqlLike.CUSTOM);
                int count = selectCount(where);
                if (count > 0) {
                    retMsg.setCode(1);
                    retMsg.setMessage("办公用品已存在，不能重复增加");
                    return retMsg;
                }
            }
            orgnlObj.setName(obj.getName());
        }
        if (null != obj.getGooRank()) {
            orgnlObj.setGooRank(obj.getGooRank());
        }
        updateById(orgnlObj);
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public void export(HttpServletResponse response, GooInfoVO obj) throws Exception {
    if (null != obj) {
      Where<GooInfo> infoWhere = new Where<GooInfo>();
      infoWhere.setSqlSelect("id,goo_code,name,number,emer_num,unit");
      String[] arr = obj.getIds().split(";");
      List<Long> ids = new ArrayList<Long>();
      for(int i = 0; i < arr.length; i++) {
        ids.add(Long.valueOf(arr[i]));
      }
      infoWhere.in("id", ids);
      List<GooInfo> gooInfoList = selectList(infoWhere);
      String[][] headers = {{ "GooCode", "编号" } , { "Name", "名称" }, { "Number", "剩余数量" }, 
              { "EmerNum", "预警值" },  { "Unit", "单位" }};
      List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();

      if (null != gooInfoList && !gooInfoList.isEmpty()) {
          if (!gooInfoList.isEmpty()) {
              for (GooInfo entity : gooInfoList) {
                  Map<String, Object> map = new HashMap<String, Object>();
                  for (int i = 0; i < headers.length; i++) {
                    GooInfo info = entity;
                      String methodName = "get" + headers[i][0];
                      Class<?> clazz = GooInfo.class;
                      Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
                      if (null != method) {
                          map.put(headers[i][0], method.invoke(info));
                      }
                  }
                  dataset.add(map);
              }
              ExcelUtil.exportExcel("办公用品盘点", headers, dataset, response);
          }
      }
  }
  }
}
