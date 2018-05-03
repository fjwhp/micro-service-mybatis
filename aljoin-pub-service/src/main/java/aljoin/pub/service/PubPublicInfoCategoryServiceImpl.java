package aljoin.pub.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.pub.dao.entity.PubPublicInfoCategory;
import aljoin.pub.dao.mapper.PubPublicInfoCategoryMapper;
import aljoin.pub.dao.object.AppPubPublicInfoCategoryDO;
import aljoin.pub.dao.object.PubPublicInfoCategoryDO;
import aljoin.pub.dao.object.PubPublicInfoCategoryVO;
import aljoin.pub.iservice.PubPublicInfoCategoryService;

/**
 * 
 * 公共信息分类表(服务实现类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-16
 */
@Service
public class PubPublicInfoCategoryServiceImpl extends ServiceImpl<PubPublicInfoCategoryMapper, PubPublicInfoCategory>
		implements PubPublicInfoCategoryService {

	@Resource
	private PubPublicInfoCategoryMapper mapper;
	@Resource
	private ActAljoinBpmnService actAljoinBpmnService;
	@Resource
	private ActAljoinCategoryService actAljoinCategoryService;
	@Resource
	private AutUserService autUserService;

	@Override
	public Page<PubPublicInfoCategoryDO> list(PageBean pageBean, PubPublicInfoCategory obj) throws Exception {
		Where<PubPublicInfoCategory> where = new Where<PubPublicInfoCategory>();
		if (null != obj) {
			if (StringUtils.isNotEmpty(obj.getName())) {
			  where.andNew(" name like {0} or create_user_name like {1}", "%" + obj.getName() + "%", "%" + obj.getName() + "%");
			}
			if (null != obj.getIsActive()) {
			  where.andNew();
			  where.eq("is_active", obj.getIsActive());
			}
		}
		// where.eq("create_user_id",obj.getCreateUserId());
		where.orderBy("category_rank", true);
		Page<PubPublicInfoCategory> oldPage = selectPage(
				new Page<PubPublicInfoCategory>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		Page<PubPublicInfoCategoryDO> page = new Page<PubPublicInfoCategoryDO>();
		List<PubPublicInfoCategory> categoryList = oldPage.getRecords();
		List<PubPublicInfoCategoryDO> categoryDOList = new ArrayList<PubPublicInfoCategoryDO>();
		if (null != categoryList && !categoryList.isEmpty()) {
			int i = 1;
			for (PubPublicInfoCategory category : categoryList) {
				if (null != category) {
				    Where<AutUser> userWhere = new Where<AutUser>();
		            userWhere.eq("is_active", 1);
		            userWhere.eq("id", category.getCreateUserId());
		            AutUser user = autUserService.selectOne(userWhere);
					PubPublicInfoCategoryDO categoryDO = new PubPublicInfoCategoryDO();
					categoryDO.setId(category.getId() + "");
					categoryDO.setNo(i);
					categoryDO.setName(category.getName());
					categoryDO.setCreateUserFullName(user.getFullName());
					if(category.getCreateUserName() != null) {
					  categoryDO.setCreateUserName(category.getCreateUserName());
					}
					if(category.getCreateTime() != null) {
					  categoryDO.setCreateTime(category.getCreateTime());
					}
					categoryDO.setRank(category.getCategoryRank());
					if (category.getProcessName() != null && StringUtils.isNotEmpty(category.getProcessName())) {
						categoryDO.setProcessName(category.getProcessName());
					} else {
						categoryDO.setProcessName("");
					}
					String status = "无效";
					if (null != category.getIsActive()) {
						if (0 == category.getIsActive()) {
							status = "无效";
						} else if (1 == category.getIsActive()) {
							status = "有效";
						}
					}
					categoryDO.setStatus(status);
					categoryDOList.add(categoryDO);
					i++;
				}
			}
		}
		page.setRecords(categoryDOList);
		page.setTotal(oldPage.getTotal());
		page.setSize(oldPage.getSize());
		return page;
	}

	@Override
	public void physicsDeleteById(Long id) throws Exception {
		mapper.physicsDeleteById(id);
	}

	@Override
	public void copyObject(PubPublicInfoCategory obj) throws Exception {
		mapper.copyObject(obj);
	}

	@Override
	public RetMsg update(PubPublicInfoCategory obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (null != obj && null != obj.getId()) {
			PubPublicInfoCategory orgnlObj = selectById(obj.getId());
			if (null != obj.getProcessId()) {
				orgnlObj.setProcessId(obj.getProcessId());
			}
			if (StringUtils.isNotEmpty(obj.getProcessName())) {
				orgnlObj.setProcessName(obj.getProcessName());
			}
			if (null != obj.getIsActive()) {
				orgnlObj.setIsActive(obj.getIsActive());
			}
			if (null != obj.getIsUse()) {
			  if(obj.getIsUse() == 0) {
			    orgnlObj.setProcessId("null");
			    orgnlObj.setProcessName("");
                orgnlObj.setIsUse(obj.getIsUse());
			  }else {
			    orgnlObj.setIsUse(obj.getIsUse());
			  }
            }
			if (StringUtils.isNotEmpty(obj.getName())) {
				if (!obj.getName().equals(orgnlObj.getName())) {
					Where<PubPublicInfoCategory> where = new Where<PubPublicInfoCategory>();
					where.eq("name", obj.getName());
					int count = selectCount(where);
					if (count > 0) {
						retMsg.setCode(1);
						retMsg.setMessage("分类名称已存在，不能重复增加");
						return retMsg;
					}
				}
				orgnlObj.setName(obj.getName());
			}
			if (null != obj.getCategoryRank()) {
				orgnlObj.setCategoryRank(obj.getCategoryRank());
			}
			if (StringUtils.isNotEmpty(obj.getUseGroupId())) {
				orgnlObj.setUseGroupId(obj.getUseGroupId());
			}
			if (StringUtils.isNotEmpty(obj.getUseGroupName())) {
				orgnlObj.setUseGroupName(obj.getUseGroupName());
			}
			updateById(orgnlObj);
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	@Override
	public RetMsg add(PubPublicInfoCategory obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (null != obj) {
			if (null != obj.getName()) {
				Where<PubPublicInfoCategory> where = new Where<PubPublicInfoCategory>();
				where.like("name", obj.getName(),SqlLike.CUSTOM);
				int count = selectCount(where);
				if (count > 0) {
					retMsg.setCode(1);
					retMsg.setMessage("分类名称已存在，不能重复增加");
					return retMsg;
				}
			}
			if (null == obj.getProcessId()) {
				obj.setProcessId("");
			}
			if (StringUtils.isEmpty(obj.getProcessName())) {
				obj.setProcessName("");
			}
			insert(obj);
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	@Override
	public List<PubPublicInfoCategory> validList(PubPublicInfoCategory obj) throws Exception {
		List<PubPublicInfoCategory> pubPublicInfoCategoryList = null;
		Where<PubPublicInfoCategory> where = new Where<PubPublicInfoCategory>();
		where.eq("is_active", 1);
		where.like("use_group_id", String.valueOf(obj.getCreateUserId()));
		where.orderBy("category_rank", true);
		where.setSqlSelect("id,name");
		pubPublicInfoCategoryList = selectList(where);
		return pubPublicInfoCategoryList;
	}
	
	@Override
    public List<PubPublicInfoCategory> allList(PubPublicInfoCategory obj) throws Exception {
        List<PubPublicInfoCategory> pubPublicInfoCategoryList = null;
        Where<PubPublicInfoCategory> where = new Where<PubPublicInfoCategory>();
        where.eq("is_active", 1); 
        where.orderBy("category_rank", true);
        where.setSqlSelect("id,name");
        pubPublicInfoCategoryList = selectList(where);
        return pubPublicInfoCategoryList;
    }

	@Override
	public RetMsg categoryList(PubPublicInfoCategory obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		List<AppPubPublicInfoCategoryDO> pubPublicInfoCategoryList = new ArrayList<AppPubPublicInfoCategoryDO>();
		Where<PubPublicInfoCategory> where = new Where<PubPublicInfoCategory>();
		where.eq("is_active", 1);
		where.like("use_group_id", String.valueOf(obj.getCreateUserId()));
		where.orderBy("category_rank", true);
		where.setSqlSelect("id,name");
		List<PubPublicInfoCategory> categoryList = selectList(where);
		if (null != categoryList && !categoryList.isEmpty()) {
			for (PubPublicInfoCategory category : categoryList) {
				AppPubPublicInfoCategoryDO appPubPublicInfoCategoryDO = new AppPubPublicInfoCategoryDO();
				appPubPublicInfoCategoryDO.setId(category.getId());
				appPubPublicInfoCategoryDO.setName(category.getName());
				pubPublicInfoCategoryList.add(appPubPublicInfoCategoryDO);
			}
		}
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setObject(pubPublicInfoCategoryList);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	@Override
	public PubPublicInfoCategoryVO detail(PubPublicInfoCategory obj) throws Exception{
		PubPublicInfoCategoryVO categoryDO = new PubPublicInfoCategoryVO();
		if(obj!=null && null != obj.getId()){
			PubPublicInfoCategory publicInfoCategory = selectById(obj.getId());
			BeanUtils.copyProperties(publicInfoCategory, categoryDO);
			if(publicInfoCategory.getIsUse() != 0 && publicInfoCategory.getProcessId() != null){
				Where<ActAljoinBpmn> actwhere = new Where<ActAljoinBpmn>();
				actwhere.setSqlSelect("id,category_id,process_id,process_name");
				actwhere.eq("process_id", publicInfoCategory.getProcessId());
				actwhere.eq("is_active", 1);
				ActAljoinBpmn ac = actAljoinBpmnService.selectOne(actwhere);
				
				if(null != ac){
					List<String> category=new ArrayList<String>();
					ActAljoinCategory acc = actAljoinCategoryService.selectById(ac.getCategoryId());
					category.add(ac.getCategoryId()+"");
					for(int i = 0;i<=acc.getCategoryLevel();i++){
						if(acc.getParentId() != 0L){
							acc=actAljoinCategoryService.selectById(acc.getParentId());
							category.add(acc.getId()+"");
						}
						
					}
					Collections.reverse(category);
					categoryDO.setProcessCategory(category);
				}
			}
		}
		return categoryDO;
	}
}
