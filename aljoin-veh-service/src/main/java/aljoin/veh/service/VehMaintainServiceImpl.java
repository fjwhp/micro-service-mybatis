package aljoin.veh.service;

import aljoin.veh.dao.entity.VehMaintain;
import aljoin.veh.dao.mapper.VehMaintainMapper;
import aljoin.veh.dao.object.VehMaintainVO;
import aljoin.veh.iservice.VehMaintainService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.util.DateUtil;

/**
 * 
 * 车船维护信息表(服务实现类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-08
 */
@Service
public class VehMaintainServiceImpl extends ServiceImpl<VehMaintainMapper, VehMaintain> implements VehMaintainService {

  @Resource
  private VehMaintainMapper mapper;
  @Resource
  private AutUserService autUserService;

  @Override
  public Page<VehMaintain> list(PageBean pageBean, VehMaintainVO obj) throws Exception {
	Where<VehMaintain> where = new Where<VehMaintain>();
	if(null != obj) {
      if(StringUtils.isNotEmpty(obj.getCarCode())) {
        where.like("car_code", obj.getCarCode());
      }
      if(StringUtils.isNotEmpty(obj.getAgentName())) {
        where.like("agent_name", obj.getAgentName());
      }
      if(null != obj.getMaintainType() && "" != obj.getMaintainType()) {
        where.eq("maintain_type", obj.getMaintainType());
      }
      if (StringUtils.isNotEmpty(obj.getBegTime()) && StringUtils.isEmpty(obj.getEndTime())) {
        where.ge("maintain_time", DateUtil.str2date(obj.getBegTime()));
      }
      if (StringUtils.isEmpty(obj.getBegTime()) && StringUtils.isNotEmpty(obj.getEndTime())) {
        where.le("maintain_time", DateUtil.str2date(obj.getEndTime()));
      }
      if (StringUtils.isNotEmpty(obj.getBegTime()) && StringUtils.isNotEmpty(obj.getEndTime())) {
        where.between("maintain_time", DateUtil.str2dateOrTime(obj.getBegTime()), DateUtil.str2dateOrTime(obj.getEndTime()));
      }
    }
	where.orderBy("maintain_time", false);
	where.setSqlSelect("id,car_code,agent_name,maintain_type,maintain_time,maintain_cost");
	Page<VehMaintain> page = selectPage(new Page<VehMaintain>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	

  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


  @Override
  public void copyObject(VehMaintain obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  public RetMsg add(VehMaintain obj) {
    RetMsg retMsg = new RetMsg();
    if (obj.getContent() == null) {
      obj.setContent("");
    }
    if(null != obj && null != obj.getAgentId()) {
      Where<AutUser> userWhere = new Where<AutUser>();
      userWhere.eq("id", obj.getAgentId());
      userWhere.setSqlSelect("full_name");
      AutUser user = autUserService.selectOne(userWhere);
      if(null != user) {
        obj.setAgentName(user.getFullName());
      }
    }
    insert(obj);
      retMsg.setCode(0);
      retMsg.setMessage("操作成功");
      return retMsg;
  }

  @Override
  public RetMsg update(VehMaintain obj) {
    RetMsg retMsg = new RetMsg();
    if (null != obj && null != obj.getId()) {
      VehMaintain orgnlObj = selectById(obj.getId());
      if (null != obj.getContent()) {
        orgnlObj.setContent(obj.getContent());
      }
      if (null != obj.getCarCode()) {
        orgnlObj.setCarCode(obj.getCarCode());
      }
      if (null != obj.getMaintainTime()) {
        orgnlObj.setMaintainTime(obj.getMaintainTime());
      }
      if (null != obj.getAgentId()) {
        orgnlObj.setAgentId(obj.getAgentId());
      }
      if (null != obj.getAgentName()) {
        orgnlObj.setAgentName(obj.getAgentName());
      }
      if (null != obj.getMaintainType()) {
        orgnlObj.setMaintainType(obj.getMaintainType());
      }
      if (null != obj.getMaintainCost()) {
        orgnlObj.setMaintainCost(obj.getMaintainCost());
      }
      updateById(orgnlObj);
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }
}
