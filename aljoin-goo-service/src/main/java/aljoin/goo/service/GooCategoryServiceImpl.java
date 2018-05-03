package aljoin.goo.service;

import aljoin.goo.dao.entity.GooCategory;
import aljoin.goo.dao.mapper.GooCategoryMapper;
import aljoin.goo.iservice.GooCategoryService;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import aljoin.dao.config.Where;
import aljoin.object.RetMsg;

/**
 * 
 * 办公用品分类表(服务实现类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-03
 */
@Service
public class GooCategoryServiceImpl extends ServiceImpl<GooCategoryMapper, GooCategory> implements GooCategoryService {

  @Resource
  private GooCategoryMapper mapper;
  @Resource
  private GooCategoryService gooCategoryService;

  @Override
  public List<GooCategory> list() throws Exception {
	Where<GooCategory> where = new Where<GooCategory>();
	where.setSqlSelect("id,name,is_active,category_rank,parent_id,category_level");
	where.orderBy("category_rank", true);
	List<GooCategory> list = selectList(where);
	return list;
  }	

  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


  @Override
  public void copyObject(GooCategory obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  public RetMsg add(GooCategory obj) {
    RetMsg retMsg = new RetMsg();
    if(obj.getParentId() == null) {
      obj.setParentId(0L);
    }
    if (null != obj) {
        if (null != obj.getName()) {
            Where<GooCategory> where = new Where<GooCategory>();
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
  public RetMsg update(GooCategory obj) {
    RetMsg retMsg = new RetMsg();
    if (null != obj && null != obj.getId()) {
      GooCategory orgnlObj = selectById(obj.getId());
        if (null != obj.getIsActive()) {
            orgnlObj.setIsActive(obj.getIsActive());
        }
        if (StringUtils.isNotEmpty(obj.getName())) {
            if (!obj.getName().equals(orgnlObj.getName())) {
                Where<GooCategory> where = new Where<GooCategory>();
                where.like("name", obj.getName(),SqlLike.CUSTOM);
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
  public GooCategory getById(Long id) {
    Where<GooCategory> where = new Where<GooCategory>();
    where.setSqlSelect("id,name,is_active,category_rank,parent_id,category_level");
    where.orderBy("category_rank", true);
    where.eq("id", id);
    GooCategory categoryBean = selectOne(where);
    return categoryBean;
  }
  
  @Override
	public List<GooCategory> getAllParentCategoryList(Long categoryid) throws Exception {
		List<GooCategory> retList = new ArrayList<GooCategory>();
		GooCategory gooCategory = gooCategoryService.selectById(categoryid);
		if (null != gooCategory) {
			retList.add(gooCategory);
			if (null != gooCategory.getCategoryLevel() && null != gooCategory.getParentId()) {
				int level = gooCategory.getCategoryLevel();
				Long tempCategoryId = gooCategory.getParentId();
				for (int i = 0; i < (level - 1); i++) {
					// 获取父级分类
					Where<GooCategory> where = new Where<GooCategory>();
					where.eq("id", tempCategoryId);
					where.setSqlSelect("id,category_rank,name");
					GooCategory tempCategory = gooCategoryService.selectOne(where);
					retList.add(tempCategory);
					tempCategoryId = tempCategory.getParentId();
				}
				Collections.reverse(retList);
			}
		}
		return retList;
	}

	@Override
	public List<GooCategory> getAllChildList(Long categoryid) throws Exception {
		List<GooCategory> actCateGory = new ArrayList<GooCategory>();
		List<GooCategory> returnList = new ArrayList<GooCategory>();
		Where<GooCategory> cgWhere = new Where<GooCategory>();
		cgWhere.setSqlSelect("id,category_rank,name");
		cgWhere.eq("is_delete", 0);
		cgWhere.eq("is_active", 1);
		cgWhere.eq("parent_id", categoryid);
		actCateGory = this.selectList(cgWhere);
		if (actCateGory != null && actCateGory.size() > 0) {
			returnList.addAll(actCateGory);
			for (GooCategory gooCategory : actCateGory) {
				List<GooCategory> returnLists = new ArrayList<GooCategory>();
				returnLists = getAllChildList(gooCategory.getId());
				if (returnLists != null && returnLists.size() > 0) {
					returnList.addAll(returnLists);
				}
			}
		}
		return returnList;
	}
}
