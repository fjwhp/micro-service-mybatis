package aljoin.veh.service;

import aljoin.veh.dao.entity.VehInfo;
import aljoin.veh.dao.entity.VehMaintain;
import aljoin.veh.dao.mapper.VehInfoMapper;
import aljoin.veh.dao.object.VehInfoDO;
import aljoin.veh.dao.object.VehInfoVO;
import aljoin.veh.iservice.VehInfoService;
import aljoin.veh.iservice.VehMaintainService;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.iservice.SysParameterService;

/**
 * 
 * 车船信息表(服务实现类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-08
 */
@Service
public class VehInfoServiceImpl extends ServiceImpl<VehInfoMapper, VehInfo> implements VehInfoService {

  @Resource
  private VehInfoMapper mapper;
  @Resource
  private AutDepartmentService autDepartmentService;
  @Resource
  private AutUserService autUserService;
  @Resource
  private AutDepartmentUserService autDepartmentUserService;
  @Resource
  private ResResourceService resResourceService;
  @Resource
  private SysParameterService sysParameterService;
  @Resource
  private VehMaintainService vehMaintainService;

  @Override
  public Page<VehInfoDO> list(PageBean pageBean, VehInfo obj) throws Exception {
	Where<VehInfo> where = new Where<VehInfo>();
	if(null != obj) {
	  if(StringUtils.isNotEmpty(obj.getCarCode())) {
	    where.like("car_code", obj.getCarCode());
	  }
	  if(StringUtils.isNotEmpty(obj.getCarModle())) {
	    where.like("car_modle", obj.getCarModle());
      }
	  if(null != obj.getCarCondition()) {
	    where.eq("car_condition", obj.getCarCondition());
	  }
	  if(null != obj.getCarStatus()) {
	    where.eq("car_status", obj.getCarStatus());
	  }
	}
	String deptIds = "";
	Where<AutDepartmentUser> deptUserWhere = new Where<AutDepartmentUser>();
	deptUserWhere.eq("user_id", obj.getCreateUserId());
	deptUserWhere.setSqlSelect("dept_id");
	List<AutDepartmentUser> deptUser = autDepartmentUserService.selectList(deptUserWhere);
	for (AutDepartmentUser autDepartmentUser : deptUser) {
	  deptIds += autDepartmentUser.getDeptId() + ";";
    }
	where.like("use_department_id",deptIds);
	where.setSqlSelect(
        "id,car_modle,car_ship,car_code,use_department_name,car_condition,purchase_time,car_status,use_user_id");
	where.orderBy("purchase_time", false);
	Page<VehInfo> oldPage = selectPage(new Page<VehInfo>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	Page<VehInfoDO> page = new Page<VehInfoDO>();
	  List<VehInfo> vehInfoList = oldPage.getRecords();
	  List<VehInfoDO> vehInfoDOList = new ArrayList<VehInfoDO>();
//	  List<Long> deptIdList = new ArrayList<Long>();
      List<Long> userIdList = new ArrayList<Long>();
	  if(vehInfoList != null && !vehInfoList.isEmpty()) {
	    int i= 0;
	    for (VehInfo vehInfo : vehInfoList) {
	      if(vehInfo != null) {
	        VehInfoDO infoDo = new VehInfoDO();
	        infoDo.setId(vehInfo.getId());
	        infoDo.setCarCode(vehInfo.getCarCode());
	        infoDo.setCarShip(vehInfo.getCarShip());
	        infoDo.setCarModle(vehInfo.getCarModle());
	        infoDo.setCarCondition(vehInfo.getCarCondition());
	        infoDo.setStatus(vehInfo.getCarStatus());
	        infoDo.setPurchaseTime(vehInfo.getPurchaseTime());
	        infoDo.setUserId(vehInfo.getUseUserId());
	        infoDo.setNo(i);
	        infoDo.setDept(vehInfo.getUseDepartmentName());
//	        deptIdList.add(vehInfo.getUseDepartmentId());
	        if(vehInfo.getUseUserId() != null || !"0".equals(vehInfo.getUseUserId())) {
	          userIdList.add(vehInfo.getUseUserId());
	        }
	        vehInfoDOList.add(infoDo);
	        i++;
	      }
	    }
	    if (null != userIdList && !userIdList.isEmpty()) {
	      Where<AutUser> userWhere = new Where<AutUser>();
	      userWhere.eq("is_active", 1);
	      userWhere.in("id", userIdList);
	      List<AutUser> userList = autUserService
	              .selectList(userWhere);
	      if (null != userList && !userList.isEmpty()) {
	        for (AutUser user : userList) {
	          for (VehInfoDO vehInfoDO : vehInfoDOList) {
	                  if (null != user && null != vehInfoDO) {
	                      if (StringUtils.isNotEmpty(user.getFullName())) {
	                          if ((null != user.getId() && null != vehInfoDO.getUserId())) {
	                              if (user.getId().equals(vehInfoDO.getUserId()) && user.getId()
	                                      .intValue() == vehInfoDO.getUserId().intValue()) {
	                                  vehInfoDO.setUserName(user.getFullName());
	                              }
	                          }
	                      }
	                  }
	              }
	          }
	      }
	    }
	    /*if (null != deptIdList && !deptIdList.isEmpty()) {
          Where<AutDepartment> departmentWhere = new Where<AutDepartment>();
          departmentWhere.eq("is_active", 1);
          departmentWhere.in("id", deptIdList);
          List<AutDepartment> categoryList = autDepartmentService
                  .selectList(departmentWhere);
          if (null != categoryList && !categoryList.isEmpty()) {
              for (VehInfoDO vehInfoDO : vehInfoDOList) {
                  for (AutDepartment category : categoryList) {
                      if (null != category && null != vehInfoDO) {
                          if (StringUtils.isNotEmpty(category.getDeptName())) {
                              if ((null != category.getId() && null != vehInfoDO.getDeptId())) {
                                  if (category.getId().equals(vehInfoDO.getDeptId()) && category.getId()
                                          .intValue() == vehInfoDO.getDeptId().intValue()) {
                                      vehInfoDO.setDept(category.getDeptName());
                                  }
                              }
                          }
                      }
                  }
              }
          }
        }*/
	    page.setRecords(vehInfoDOList);
	    page.setSize(oldPage.getSize());
	    page.setTotal(oldPage.getTotal());
	    page.setCurrent(oldPage.getCurrent());
	  }
	return page;
  }	
  @Override
  public Page<VehInfoDO> allList(PageBean pageBean, VehInfo obj) throws Exception {
    Where<VehInfo> where = new Where<VehInfo>();
    if(null != obj) {
      if(StringUtils.isNotEmpty(obj.getCarCode())) {
        where.like("car_code", obj.getCarCode());
      }
      if(StringUtils.isNotEmpty(obj.getCarModle())) {
        where.like("car_modle", obj.getCarModle());
      }
      if(null != obj.getCarCondition()) {
        where.eq("car_condition", obj.getCarCondition());
      }
      if(null != obj.getCarStatus()) {
        where.eq("car_status", obj.getCarStatus());
      }
    }
    /*List<Long> deptIds = new ArrayList<Long>();
    Where<AutDepartmentUser> deptUserWhere = new Where<AutDepartmentUser>();
    deptUserWhere.eq("user_id", obj.getCreateUserId());
    deptUserWhere.setSqlSelect("dept_id");
    List<AutDepartmentUser> deptUser = autDepartmentUserService.selectList(deptUserWhere);
    for (AutDepartmentUser autDepartmentUser : deptUser) {
      deptIds.add(autDepartmentUser.getDeptId());
    }
    where.in("use_department_id",deptIds);*/
    where.setSqlSelect(
        "id,car_modle,car_ship,car_code,car_condition,use_department_name,purchase_time,car_status,use_user_id");
    where.orderBy("purchase_time", false);
    Page<VehInfo> oldPage = selectPage(new Page<VehInfo>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    Page<VehInfoDO> page = new Page<VehInfoDO>();
      List<VehInfo> vehInfoList = oldPage.getRecords();
      List<VehInfoDO> vehInfoDOList = new ArrayList<VehInfoDO>();
//      List<Long> deptIdList = new ArrayList<Long>();
      List<Long> userIdList = new ArrayList<Long>();
      if(vehInfoList != null && !vehInfoList.isEmpty()) {
        int i= 0;
        for (VehInfo vehInfo : vehInfoList) {
          if(vehInfo != null) {
            VehInfoDO infoDo = new VehInfoDO();
            infoDo.setId(vehInfo.getId());
            infoDo.setCarCode(vehInfo.getCarCode());
            infoDo.setCarShip(vehInfo.getCarShip());
            infoDo.setCarModle(vehInfo.getCarModle());
            infoDo.setCarCondition(vehInfo.getCarCondition());
            infoDo.setStatus(vehInfo.getCarStatus());
            infoDo.setPurchaseTime(vehInfo.getPurchaseTime());
            infoDo.setUserId(vehInfo.getUseUserId());
            infoDo.setNo(i);
            infoDo.setDept(vehInfo.getUseDepartmentName());
//            deptIdList.add(vehInfo.getUseDepartmentId());
            if(vehInfo.getUseUserId() != null) {
              userIdList.add(vehInfo.getUseUserId());
            }
            vehInfoDOList.add(infoDo);
            i++;
          }
        }
        if (null != userIdList && !userIdList.isEmpty()) {
          Where<AutUser> userWhere = new Where<AutUser>();
          userWhere.eq("is_active", 1);
          userWhere.in("id", userIdList);
          List<AutUser> userList = autUserService
                  .selectList(userWhere);
          if (null != userList && !userList.isEmpty()) {
              for (VehInfoDO vehInfoDO : vehInfoDOList) {
                  for (AutUser user : userList) {
                      if (null != user && null != vehInfoDO) {
                          if (StringUtils.isNotEmpty(user.getFullName())) {
                              if ((null != user.getId() && null != vehInfoDO.getUserId())) {
                                  if (user.getId().equals(vehInfoDO.getUserId()) && user.getId()
                                          .intValue() == vehInfoDO.getUserId().intValue()) {
                                      vehInfoDO.setUserName(user.getFullName());
                                  }
                              }
                          }
                      }
                  }
              }
          }
        }
        /*if (null != deptIdList && !deptIdList.isEmpty()) {
          Where<AutDepartment> departmentWhere = new Where<AutDepartment>();
          departmentWhere.eq("is_active", 1);
          departmentWhere.in("id", deptIdList);
          List<AutDepartment> categoryList = autDepartmentService
                  .selectList(departmentWhere);
          if (null != categoryList && !categoryList.isEmpty()) {
              for (VehInfoDO vehInfoDO : vehInfoDOList) {
                  for (AutDepartment category : categoryList) {
                      if (null != category && null != vehInfoDO) {
                          if (StringUtils.isNotEmpty(category.getDeptName())) {
                              if ((null != category.getId() && null != vehInfoDO.getDeptId())) {
                                  if (category.getId().equals(vehInfoDO.getDeptId()) && category.getId()
                                          .intValue() == vehInfoDO.getDeptId().intValue()) {
                                      vehInfoDO.setDept(category.getDeptName());
                                  }
                              }
                          }
                      }
                  }
              }
          }
        }*/
        page.setRecords(vehInfoDOList);
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
  public void copyObject(VehInfo obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  @Transactional
  public RetMsg add(VehInfoVO obj) {
    RetMsg retMsg = new RetMsg();
    VehInfo vehInfo = null;
    if (obj.getUseUserId() == null) {
      obj.setUseUserId(0L);
    } 
    if (obj.getContent() == null) {
      obj.setContent("");
    } 
    if (null == obj.getCarStatus()) {
        obj.setCarStatus(0);
    }
      if (null != obj) {
          if (null != obj.getCarCode()) {
              Where<VehInfo> where = new Where<VehInfo>();
              where.eq("car_code", obj.getCarCode());
              int count = selectCount(where);
              if (count > 0) {
                  retMsg.setCode(1);
                  retMsg.setMessage("该牌号重复");
                  return retMsg;
              }
          }

          vehInfo = new VehInfo();
          BeanUtils.copyProperties(obj, vehInfo);
          insert(vehInfo);
      
          // 图片
          List<ResResource> resResourceList = obj.getResResourceList();
          if(null != resResourceList && !resResourceList.isEmpty()){
              ResResource resource = resResourceService.selectById(resResourceList.get(0).getId());
              resource.setBizId(vehInfo.getId());
              resource.setFileDesc("车船信息管理图片上传");
              resResourceService.updateById(resource);
          }
      }
      retMsg.setCode(0);
      retMsg.setMessage("操作成功");
      return retMsg;
  }

  @Override
  public VehInfoVO getById(Long id) throws Exception {
    VehInfoVO vehInfovo = new VehInfoVO();
    if(id != null) {
      VehInfo vehInfo = selectById(id);
      if(vehInfo != null){
//        vehInfo = new VehInfo();
        BeanUtils.copyProperties(vehInfo, vehInfovo);
        /*Where<AutDepartment> departmentWhere = new Where<AutDepartment>();
        departmentWhere.eq("is_active", 1);
        departmentWhere.in("id", vehInfo.getUseDepartmentId());
        AutDepartment dept = autDepartmentService
                .selectOne(departmentWhere);
        if(null != dept){
          vehInfovo.setDeptName(dept.getDeptName());
        }*/
        //图片
        Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id",vehInfo.getId());
        List<ResResource> resourceList = resResourceService.selectList(where);
        if(null != resourceList && !resourceList.isEmpty()){
            vehInfovo.setResResourceList(resourceList);
        }
      }
    }
    return vehInfovo;
  }

  @Override
  public RetMsg update(VehInfoVO obj) throws Exception {
    RetMsg retMsg = new RetMsg();
    if (null != obj && null != obj.getId()) {
      VehInfo orgnlObj = selectById(obj.getId());
        if (null != obj.getIsActive()) {
            orgnlObj.setIsActive(obj.getIsActive());
        }
        if (null != obj.getCarModle()) {
          orgnlObj.setCarModle(obj.getCarModle());
        }
        if (null != obj.getContent()) {
          orgnlObj.setContent(obj.getContent());
        }
        if (null != obj.getCarCode()) {
          if (!obj.getCarCode().equals(orgnlObj.getCarCode())) {
            Where<VehInfo> where = new Where<VehInfo>();
            where.eq("car_code", obj.getCarCode());
            int count = selectCount(where);
            if (count > 0) {
                retMsg.setCode(1);
                retMsg.setMessage("牌号重复，请重新填写");
                return retMsg;
            }
        }
          orgnlObj.setCarCode(obj.getCarCode());
        }
        if (null != obj.getPurchaseTime()) {
          orgnlObj.setPurchaseTime(obj.getPurchaseTime());
        }
        if (null != obj.getCarShip()) {
          orgnlObj.setCarShip(obj.getCarShip());
        }
        if (null != obj.getCarStatus()) {
          orgnlObj.setCarStatus(obj.getCarStatus());
          if(obj.getCarStatus() == 0) {
            orgnlObj.setUseUserId(0L);
          }
        }
        if (StringUtils.isNotEmpty(obj.getDriverName())) {
            orgnlObj.setDriverName(obj.getDriverName());
        }
        if (null != obj.getUseDepartmentId()) {
            orgnlObj.setUseDepartmentId(obj.getUseDepartmentId());
        }
        if (null != obj.getUseDepartmentName()) {
          orgnlObj.setUseDepartmentName(obj.getUseDepartmentName());
        }
        if (null != obj.getCardCode()) {
          orgnlObj.setCardCode(obj.getCardCode());
        }
        if (null != obj.getConsume()) {
          orgnlObj.setConsume(obj.getConsume());
        }
        if (null != obj.getCarType()) {
          orgnlObj.setCarType(obj.getCarType());
        }
        if (null != obj.getAlreadyRun()) {
          orgnlObj.setAlreadyRun(obj.getAlreadyRun());
        }
        if (null != obj.getAlreadyUseTime()) {
          orgnlObj.setAlreadyUseTime(obj.getAlreadyUseTime());
        }
        if (null != obj.getCarCondition()) {
          orgnlObj.setCarCondition(obj.getCarCondition());
        }
        updateById(orgnlObj);
        
        // 图片
        List<ResResource> resResourceList = obj.getResResourceList();
        if(null != resResourceList && !resResourceList.isEmpty()){
            //删除原来的图片
            Where<ResResource> where = new Where<ResResource>();
            where.eq("biz_id",orgnlObj.getId());
            resResourceService.delete(where);
            //新增图片
            ResResource resource = resResourceService.selectById(resResourceList.get(0).getId());
            resource.setBizId(orgnlObj.getId());
            resource.setFileDesc("车船信息管理图片上传");
            resResourceService.updateById(resource);
        }
     }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }
  @Override
  @Transactional
  public RetMsg deleteByIds(VehInfoVO obj) throws Exception {
    RetMsg retMsg = new RetMsg();
    String ids = obj.getIds();
    String carCodes = obj.getCarCodes();
    if (null != ids) {
        String idArr[] = null;
        if (ids.indexOf(";") > -1) {
            if (ids.endsWith(";")) {
                idArr = ids.substring(0, ids.lastIndexOf(";")).split(";");
            } else {
                idArr = ids.split(";");
            }
        } else {
            idArr = new String[1];
            idArr[0] = ids;
        }
        if (null != ids) {
            List<Long> idList = new ArrayList<Long>();
            Where<VehInfo> infoWhere = new Where<VehInfo>();
            infoWhere.in("id", idArr);
            List<VehInfo> vehInfoList = selectList(infoWhere);
            if (null != vehInfoList && !vehInfoList.isEmpty()) {
                for (VehInfo pvehInfo : vehInfoList) {
                    if (null != pvehInfo) {
                        idList.add(pvehInfo.getId());
                    }
                }
            }
            if (null != idList && !idList.isEmpty()) {
                deleteBatchIds(idList);  
            }
        }
    }
    if (null != carCodes) {
      String codeArr[] = null;
      if (carCodes.indexOf(";") > -1) {
          if (carCodes.endsWith(";")) {
            codeArr = carCodes.substring(0, carCodes.lastIndexOf(";")).split(";");
          } else {
            codeArr = carCodes.split(";");
          }
      } else {
        codeArr = new String[1];
        codeArr[0] = carCodes;
      }
      if (null != carCodes) {
          List<Long> codeList = new ArrayList<Long>();
          Where<VehMaintain> mainWhere = new Where<VehMaintain>();
          mainWhere.in("car_code", codeArr);
          List<VehMaintain> mainList = vehMaintainService.selectList(mainWhere);
          if (null != mainList && !mainList.isEmpty()) {
              for (VehMaintain pvehInfo : mainList) {
                  if (null != pvehInfo) {
                    codeList.add(pvehInfo.getId());
                  }
              }
          }
          if (null != codeList && !codeList.isEmpty()) {
            vehMaintainService.deleteBatchIds(codeList);  
          }
          //删除图片(暂时不删资源)
          /*Where<ResResource> where = new Where<ResResource>();
          where.in("biz_id",codeList);
          List<ResResource> resourcesList = resResourceService.selectList(where);
          List<Long> resourcesIds = new ArrayList<Long>();
          for (ResResource resResource : resourcesList) {
              resourcesIds.add(resResource.getId());
          }
          if(null != resourcesList && !resourcesList.isEmpty()){
              resResourceService.deleteBatchById(resourcesIds);
          }*/
      }
  }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }
  @Override
  public List<String> allCode() {
    Where<VehInfo> infoWhere = new Where<VehInfo>();
    infoWhere.setSqlSelect("car_code");
    infoWhere.orderBy("car_code");
    List<VehInfo> infoList = selectList(infoWhere);
    List<String> list = new ArrayList<String>();
    for (VehInfo info : infoList) {
      list.add(info.getCarCode());
    }
    return list;
  }
  @Override
  public VehInfo getCarByCode(String carCode) {
    Where<VehInfo> infoWhere = new Where<VehInfo>();
    infoWhere.eq("car_code", carCode);
    infoWhere.setSqlSelect("car_code,car_modle,purchase_time,car_ship,driver_name,car_status");
    VehInfo info = selectOne(infoWhere);
    return info;
  }
}
