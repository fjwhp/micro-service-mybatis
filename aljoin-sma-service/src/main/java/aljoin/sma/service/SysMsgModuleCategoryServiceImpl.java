package aljoin.sma.service;

import aljoin.sma.dao.entity.SysMsgModuleCategory;
import aljoin.sma.dao.entity.SysMsgModuleInfo;
import aljoin.sma.dao.mapper.SysMsgModuleCategoryMapper;
import aljoin.sma.iservice.SysMsgModuleCategoryService;
import aljoin.sma.iservice.SysMsgModuleInfoService;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;

/**
 * 
 * 消息模板分类表(服务实现类).
 * 
 * @author：huangw
 * 
 * @date： 2017-11-14
 */
@Service
public class SysMsgModuleCategoryServiceImpl extends ServiceImpl<SysMsgModuleCategoryMapper, SysMsgModuleCategory> implements SysMsgModuleCategoryService {

  @Resource
  private SysMsgModuleCategoryMapper mapper;
  @Resource
  private SysMsgModuleInfoService sysMsgModuleInfoService;

  @Override
  public Page<SysMsgModuleCategory> list(PageBean pageBean, SysMsgModuleCategory obj) throws Exception {
	Where<SysMsgModuleCategory> where = new Where<SysMsgModuleCategory>();
	where.orderBy("create_time", false);
	Page<SysMsgModuleCategory> page = selectPage(new Page<SysMsgModuleCategory>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	

	@Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


	@Override
  public void copyObject(SysMsgModuleCategory obj) throws Exception{
  	mapper.copyObject(obj);
  }

   @Override
   public RetMsg classification(String gname) throws Exception {
	// TODO Auto-generated method stub
	RetMsg retMsg=new RetMsg();
	Where<SysMsgModuleCategory> gWhere=new Where<SysMsgModuleCategory>();
	gWhere.eq("module_name", gname);
	gWhere.eq("is_active",1);
	gWhere.eq("is_delete", 0);
	gWhere.setSqlSelect("id");
	List<SysMsgModuleCategory> cList= this.selectList(gWhere);
	if(cList!=null && cList.size()>0){
		Long gID=cList.get(0).getId();	
		Where<SysMsgModuleInfo> mWhere=new Where<SysMsgModuleInfo>();
		mWhere.eq("is_delete", 0);
		mWhere.eq("is_active",1);
		mWhere.eq("module_category_id", gID);
		List<SysMsgModuleInfo> infoList=sysMsgModuleInfoService.selectList(mWhere);
		retMsg.setObject(infoList);
		}else{
		SysMsgModuleCategory sc=new SysMsgModuleCategory();
		sc.setIsActive(1);
		sc.setModuleName(gname);
		sc.setModuleCode(gname);
		insert(sc);
	}
	retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
	retMsg.setMessage("操作成功");
	return retMsg;
   }
}
