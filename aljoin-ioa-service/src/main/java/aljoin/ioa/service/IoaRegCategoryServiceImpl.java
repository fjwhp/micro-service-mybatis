package aljoin.ioa.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.dao.config.Where;
import aljoin.ioa.dao.entity.IoaRegCategory;
import aljoin.ioa.dao.mapper.IoaRegCategoryMapper;
import aljoin.ioa.iservice.IoaRegCategoryService;
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
public class IoaRegCategoryServiceImpl extends ServiceImpl<IoaRegCategoryMapper, IoaRegCategory>
		implements IoaRegCategoryService {

	@Resource
	private IoaRegCategoryService IoaRegCategoryService;

	@Override
	public List<IoaRegCategory> getCateGoryList(String type) {
		// TODO Auto-generated method stub
		List<IoaRegCategory> cateGoryList = new ArrayList<IoaRegCategory>();
		IoaRegCategory cateGory = new IoaRegCategory();
		cateGory.setId(0L);
		cateGory.setCategoryName("");
		cateGoryList.add(cateGory);
		Where<IoaRegCategory> where = new Where<IoaRegCategory>();
		where.setSqlSelect("id,category_name,is_active,category_rank,parent_id,category_level");
		where.eq("reg_type", type);
		where.orderBy("category_rank", true);
		where.orderBy("category_name", true);
		List<IoaRegCategory> goryList = this.selectList(where);
		if (goryList != null && goryList.size() > 0) {
			cateGoryList.addAll(goryList);
		}
		return cateGoryList;
	}

	@Override
	public boolean validCategoryLevel(IoaRegCategory obj) throws Exception {
		// TODO Auto-generated method stub
		List<IoaRegCategory> categoryList = this.selectCategoryList(obj);
		boolean flag = true;
		if (!categoryList.isEmpty()) {
			if (categoryList.size() > 999) {
				flag = false;
			}
		}
		return flag;

	}

	@Override
	public List<IoaRegCategory> selectCategoryList(IoaRegCategory obj) throws Exception {
		Where<IoaRegCategory> where = new Where<IoaRegCategory>();
		if (null != obj.getIsActive()) {
			where.eq("is_active", obj.getIsActive());
		}

		if (null != obj.getParentId()) {
			where.eq("parent_id", obj.getParentId());
		}
		where.eq("reg_type", obj.getRegType());
		where.orderBy("category_rank", true);
		where.setSqlSelect("id,category_name,is_active,category_rank,parent_id,category_level,create_time");
		return selectList(where);
	}

	@Override
	public boolean validCategoryName(IoaRegCategory obj, boolean isAdd) throws Exception {
		boolean flag = false;
		if (null != obj) {
			if (null != obj.getCategoryName()) {
				Where<IoaRegCategory> where = new Where<IoaRegCategory>();
				where.eq("category_name", obj.getCategoryName());
				where.eq("reg_type", obj.getRegType());
				if (!isAdd) {
					// 如果是修改，需要排除自己
					where.ne("id", obj.getId());
				}
				obj = this.selectOne(where);
				if (null != obj) {
					flag = true;
				}
			}
		}
		return flag;
	}

	@Override
	public RetMsg addIoaRegCategory(IoaRegCategory obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		if(obj.getCategoryLevel() == null){
			if(obj.getParentId() == null){
				obj.setCategoryLevel(0);
			}else{
				IoaRegCategory parent = selectById(obj.getParentId());
			    int level = parent.getCategoryLevel() + 1;
			    obj.setCategoryLevel(level);
			}
			
		}
		this.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	@Override
	public List<IoaRegCategory> getAllChildCategoryList(Long parentId) throws Exception {
		List<IoaRegCategory> actCateGory = new ArrayList<IoaRegCategory>();
		List<IoaRegCategory> returnList = new ArrayList<IoaRegCategory>();
		Where<IoaRegCategory> cgWhere = new Where<IoaRegCategory>();
		cgWhere.setSqlSelect("id");
		cgWhere.eq("parent_id", parentId);
		actCateGory = this.selectList(cgWhere);
		if (actCateGory != null && actCateGory.size() > 0) {
			returnList.addAll(actCateGory);
			for (IoaRegCategory ioaRegCategory : actCateGory) {
				List<IoaRegCategory> returnLists = new ArrayList<IoaRegCategory>();
				returnLists = this.getAllChildCategoryList(ioaRegCategory.getId());
				if (returnLists != null && returnLists.size() > 0) {
					returnList.addAll(returnLists);
				}
			}
		}
		return returnList;
	}

}
