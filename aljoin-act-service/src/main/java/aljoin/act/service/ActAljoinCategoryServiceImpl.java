package aljoin.act.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinBpmnUser;
import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.dao.mapper.ActAljoinCategoryMapper;
import aljoin.act.dao.object.ActAljoinCategoryBpmnVO;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinBpmnUserService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 流程分类表(服务实现类).
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:01:40
 */
@Service
public class ActAljoinCategoryServiceImpl extends ServiceImpl<ActAljoinCategoryMapper, ActAljoinCategory>
		implements ActAljoinCategoryService {

	@Resource
	private ActAljoinCategoryService actAljoinCategoryService;
	@Resource
	private ActAljoinBpmnUserService actAljoinBpmnUserService;
	@Resource
	private ActAljoinBpmnService actAljoinBpmnService;

	@Override
	public Page<ActAljoinCategory> list(PageBean pageBean, ActAljoinCategory obj) throws Exception {
		Where<ActAljoinCategory> where = new Where<ActAljoinCategory>();
		where.setSqlSelect("id,category_name,is_active,category_rank,parent_id,category_level");
		where.orderBy("category_rank", true);
		Page<ActAljoinCategory> page = selectPage(
				new Page<ActAljoinCategory>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		return page;
	}

	@Override
	public List<ActAljoinCategory> getAllCategoryList() throws Exception {
		Where<ActAljoinCategory> where = new Where<ActAljoinCategory>();
		where.setSqlSelect("id,category_name,is_active,category_rank,parent_id,category_level");
		where.orderBy("category_rank", true);
		where.orderBy("category_name", true);
		List<ActAljoinCategory> list = selectList(where);
		return list;
	}

	@Override
	public Boolean compareCategoryName(ActAljoinCategory obj) throws Exception {

		List<ActAljoinCategory> list = actAljoinCategoryService.getAllCategoryList();

		Boolean judge = false;
		// 有从表中查到记录，再把前台传来的分类名称进行对比
		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				// 查询到同名的，则赋值false
				if (list.get(i).getCategoryName().equals(obj.getCategoryName())) {
					return false;
				} else {
					judge = true;
				}
			}
		} else {
			judge = true;
		}
		return judge;
	}

	@Override
	public ActAljoinCategory getById(ActAljoinCategory obj) {

		Where<ActAljoinCategory> where = new Where<ActAljoinCategory>();
		where.setSqlSelect("id,category_name,is_active,category_rank,parent_id,category_level");
		where.orderBy("category_rank", true);
		where.eq("id", obj.getId());
		ActAljoinCategory categoryBean = selectOne(where);
		return categoryBean;
	}

	@Override
	public Boolean outNumber(ActAljoinCategory obj) {

		int categoryNum = 0;
		Where<ActAljoinCategory> where = new Where<ActAljoinCategory>();
		if (obj.getCategoryLevel() > 1) {
			where.eq("id", obj.getParentId());
			where.eq("category_level", obj.getCategoryLevel());
			categoryNum = actAljoinCategoryService.selectCount(where);
		} else {
			where.eq("category_level", obj.getCategoryLevel());
			categoryNum = actAljoinCategoryService.selectCount(where);
		}
		// 子分类数量大于999则返回true，反之false
		return (categoryNum > 999);
	}

	@Override
	public List<ActAljoinCategory> getByParentId(ActAljoinCategory obj) throws Exception {
		Where<ActAljoinCategory> where = new Where<ActAljoinCategory>();
		where.setSqlSelect("id,parent_id,category_rank,category_name,is_active");
		where.eq("parent_id", obj.getParentId());
		where.eq("is_active", 1);
		where.orderBy("category_rank", true);
		List<ActAljoinCategory> categoryList = actAljoinCategoryService.selectList(where);
		return categoryList;
	}

	@Override
	public List<ActAljoinCategory> getAllParentCategoryList(Long categoryid) throws Exception {
		List<ActAljoinCategory> retList = new ArrayList<ActAljoinCategory>();
		ActAljoinCategory actAljoinCategory = actAljoinCategoryService.selectById(categoryid);
		if (null != actAljoinCategory) {
			retList.add(actAljoinCategory);
			if (null != actAljoinCategory.getCategoryLevel() && null != actAljoinCategory.getParentId()) {
				int level = actAljoinCategory.getCategoryLevel();
				Long tempCategoryId = actAljoinCategory.getParentId();
				for (int i = 0; i < (level - 1); i++) {
					// 获取父级分类
					ActAljoinCategory tempCategory = actAljoinCategoryService.selectById(tempCategoryId);
					retList.add(tempCategory);
					tempCategoryId = tempCategory.getParentId();
				}
				Collections.reverse(retList);
			}
		}
		return retList;
	}

	@Override
	public List<ActAljoinCategory> getAllChildList(Long categoryid) throws Exception {
		// TODO Auto-generated method stub
		List<ActAljoinCategory> actCateGory = new ArrayList<ActAljoinCategory>();
		List<ActAljoinCategory> returnList = new ArrayList<ActAljoinCategory>();
		Where<ActAljoinCategory> cgWhere = new Where<ActAljoinCategory>();
		cgWhere.setSqlSelect("id,category_rank,category_name");
		cgWhere.eq("is_delete", 0);
		cgWhere.eq("is_active", 1);
		cgWhere.eq("parent_id", categoryid);
		actCateGory = this.selectList(cgWhere);
		if (actCateGory != null && actCateGory.size() > 0) {
			returnList.addAll(actCateGory);
			for (ActAljoinCategory actAljoinCategory : actCateGory) {
				List<ActAljoinCategory> returnLists = new ArrayList<ActAljoinCategory>();
				returnLists = getAllChildList(actAljoinCategory.getId());
				if (returnLists != null && returnLists.size() > 0) {
					returnList.addAll(returnLists);
				}
			}
		}
		return returnList;
	}

	@Override
	public List<ActAljoinCategory> getUserBpmnList(String userID) throws Exception {
		// TODO Auto-generated method stub
		List<ActAljoinCategory> returnList = new ArrayList<ActAljoinCategory>();
		Where<ActAljoinCategory> cgWhere = new Where<ActAljoinCategory>();
		Where<ActAljoinBpmnUser> where = new Where<ActAljoinBpmnUser>();
		where.eq("is_delete", 0);
		where.eq("is_active", 1);
		where.eq("auth_type", 1);
		where.eq("user_id", userID);
		where.setSqlSelect("bpmn_id");
		List<ActAljoinBpmnUser> aabList = actAljoinBpmnUserService.selectList(where);
		if (aabList != null && aabList.size() > 0) {
			String bpmnIds = "";
			for (ActAljoinBpmnUser actAljoinBpmnUser : aabList) {
				bpmnIds += actAljoinBpmnUser.getBpmnId() + ",";
			}
			if (bpmnIds != "" && !"".equals(bpmnIds)) {
				Where<ActAljoinBpmn> abWhere = new Where<ActAljoinBpmn>();
				abWhere.in("id", bpmnIds);
				abWhere.eq("is_delete", 0);
				abWhere.eq("is_active", 1);
				abWhere.setSqlSelect("category_id");
				List<ActAljoinBpmn> abList = actAljoinBpmnService.selectList(abWhere);
				if (abList != null && abList.size() > 0) {
					String cgIds = "";
					for (ActAljoinBpmn actAljoinBpmn : abList) {
						cgIds += actAljoinBpmn.getCategoryId() + ",";
					}
					cgWhere.in("id", cgIds);
					cgWhere.eq("is_delete", 0);
					cgWhere.eq("is_active", 1);
					cgWhere.setSqlSelect("id,category_name,is_active,category_rank,parent_id,category_level");
					returnList = selectList(cgWhere);
				}
			}

		}
		if (returnList != null && returnList.size() > 0) {
			List<ActAljoinCategory> tmpList = new ArrayList<ActAljoinCategory>();
			for (int i = 0; i < returnList.size(); i++) {
				tmpList.addAll(this.getAllParentCategoryList(returnList.get(i).getId()));
			}
			returnList = tmpList;
			String ids = "";
			for (int i = 0; i < returnList.size(); i++) {
				String tmpId = returnList.get(i).getId().toString();
				if (ids.indexOf(tmpId) > -1) {
					returnList.remove(i);
					i--;
				} else {
					ids += returnList.get(i).getId().toString() + ",";
				}
			}

		}
		return returnList;
	}

	@Override
	public RetMsg getAllParentCategoryList(ActAljoinCategory obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		Where<ActAljoinCategory> categoryWhere = new Where<ActAljoinCategory>();
		categoryWhere.setSqlSelect("id,parent_id,category_name,is_active,category_level,category_rank");
		categoryWhere.eq("parent_id", 0);
		categoryWhere.eq("is_active", 1);
		List<ActAljoinCategory> categoryList = selectList(categoryWhere);
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功");
		retMsg.setObject(categoryList);
		return retMsg;
	}

	@Override
	public List<ActAljoinCategoryBpmnVO> getAllCategoryBpmnList() throws Exception {
		Where<ActAljoinCategory> where = new Where<ActAljoinCategory>();
		where.setSqlSelect("id,category_name,category_rank,parent_id");
		where.orderBy("category_rank", true);
		where.orderBy("category_name", true);
		List<ActAljoinCategory> list = selectList(where);
		List<ActAljoinCategoryBpmnVO> returnList = new ArrayList<ActAljoinCategoryBpmnVO>();
		if (list != null && list.size() > 0) {
			for (ActAljoinCategory actAljoinCategory : list) {
				ActAljoinCategoryBpmnVO vo = new ActAljoinCategoryBpmnVO();
				vo.setIsCategory("1");
				vo.setId(actAljoinCategory.getId().toString());
				if (actAljoinCategory.getParentId() != null) {
					vo.setpId(actAljoinCategory.getParentId().toString());
				}
				vo.setNoteName(actAljoinCategory.getCategoryName());
				returnList.add(vo);
			}
		}
		Where<ActAljoinBpmn> bpmnWhere = new Where<ActAljoinBpmn>();
		bpmnWhere.eq("is_active", 1);
		bpmnWhere.eq("is_fixed", 0);
		bpmnWhere.eq("is_deploy", 1);
		bpmnWhere.setSqlSelect("category_id,id,process_name");
		List<ActAljoinBpmn> bpmnList = actAljoinBpmnService.selectList(bpmnWhere);
		if (bpmnList != null && bpmnList.size() > 0) {
			for (ActAljoinBpmn actAljoinBpmn : bpmnList) {
				ActAljoinCategoryBpmnVO vo = new ActAljoinCategoryBpmnVO();
				vo.setIsCategory("0");
				vo.setId(actAljoinBpmn.getId().toString());
				if (actAljoinBpmn.getCategoryId() != null) {
					vo.setpId(actAljoinBpmn.getCategoryId().toString());
				}
				vo.setNoteName(actAljoinBpmn.getProcessName());
				returnList.add(vo);
			}
		}
		return returnList;
	}

	
}
