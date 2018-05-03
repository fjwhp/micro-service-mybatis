package aljoin.ass.service;

import aljoin.ass.dao.entity.AssCategory;
import aljoin.ass.dao.mapper.AssCategoryMapper;
import aljoin.ass.iservice.AssCategoryService;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 固定资产分类表(服务实现类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-09
 */
@Service
public class AssCategoryServiceImpl extends ServiceImpl<AssCategoryMapper, AssCategory> implements AssCategoryService {

  @Resource
  private AssCategoryMapper mapper;
  @Resource
  private AssCategoryService assCategoryService;

  @Override
  public List<AssCategory> list(PageBean pageBean, AssCategory obj) throws Exception {
	Where<AssCategory> where = new Where<AssCategory>();
    where.setSqlSelect("id,name,is_active,category_rank,parent_id,category_level");
    where.orderBy("category_rank", true);
	List<AssCategory> list = selectList(where);
	return list;
  }	

  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }

  @Override
  public void copyObject(AssCategory obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  public RetMsg add(AssCategory obj) {
    RetMsg retMsg = new RetMsg();
    if(obj.getParentId() == null) {
      obj.setParentId(0L);
    }
    if (null != obj) {
        if (null != obj.getName()) {
            Where<AssCategory> where = new Where<AssCategory>();
            where.like("name", obj.getName(),SqlLike.CUSTOM);
            int count = selectCount(where);
            if (count > 0) {
                retMsg.setCode(1);
                retMsg.setMessage("分类名称已存在，不能重复增加");
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
  public RetMsg update(AssCategory obj) {
    RetMsg retMsg = new RetMsg();
    if (null != obj && null != obj.getId()) {
      AssCategory orgnlObj = selectById(obj.getId());
        if (null != obj.getIsActive()) {
            orgnlObj.setIsActive(obj.getIsActive());
        }
        if (StringUtils.isNotEmpty(obj.getName())) {
            if (!obj.getName().equals(orgnlObj.getName())) {
                Where<AssCategory> where = new Where<AssCategory>();
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
        updateById(orgnlObj);
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public AssCategory getById(Long id) {
    Where<AssCategory> where = new Where<AssCategory>();
    where.setSqlSelect("id,name,is_active,category_rank,parent_id,category_level");
    where.orderBy("category_rank", true);
    where.eq("id", id);
    AssCategory categoryBean = selectOne(where);
    return categoryBean;
  }
  
  @Override
	public List<AssCategory> getAllParentCategoryList(Long categoryid) throws Exception {
		List<AssCategory> retList = new ArrayList<AssCategory>();
		AssCategory assCategory = assCategoryService.selectById(categoryid);
		if (null != assCategory) {
			retList.add(assCategory);
			if (null != assCategory.getCategoryLevel() && null != assCategory.getParentId()) {
				int level = assCategory.getCategoryLevel();
				Long tempCategoryId = assCategory.getParentId();
				for (int i = 0; i < (level - 1); i++) {
					// 获取父级分类

					Where<AssCategory> where = new Where<AssCategory>();
					where.eq("id", tempCategoryId);
					where.setSqlSelect("id,category_rank,name");
					AssCategory tempCategory = assCategoryService.selectOne(where);
					retList.add(tempCategory);
					tempCategoryId = tempCategory.getParentId();
				}
				Collections.reverse(retList);
			}
		}
		return retList;
	}

	@Override
	public List<AssCategory> getAllChildList(Long categoryid) throws Exception {
		List<AssCategory> actCateGory = new ArrayList<AssCategory>();
		List<AssCategory> returnList = new ArrayList<AssCategory>();
		Where<AssCategory> cgWhere = new Where<AssCategory>();
		cgWhere.setSqlSelect("id,category_rank,name");
		cgWhere.eq("is_delete", 0);
		cgWhere.eq("is_active", 1);
		cgWhere.eq("parent_id", categoryid);
		actCateGory = this.selectList(cgWhere);
		if (actCateGory != null && actCateGory.size() > 0) {
			returnList.addAll(actCateGory);
			for (AssCategory assCategory : actCateGory) {
				List<AssCategory> returnLists = new ArrayList<AssCategory>();
				returnLists = getAllChildList(assCategory.getId());
				if (returnLists != null && returnLists.size() > 0) {
					returnList.addAll(returnLists);
				}
			}
		}
		return returnList;
	}
}
