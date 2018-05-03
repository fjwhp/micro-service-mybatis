package aljoin.act.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActAljoinFormCategory;
import aljoin.act.dao.mapper.ActAljoinFormCategoryMapper;
import aljoin.act.iservice.ActAljoinFormCategoryService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 表单分类表(服务实现类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-08-31
 */
@Service
public class ActAljoinFormCategoryServiceImpl extends ServiceImpl<ActAljoinFormCategoryMapper, ActAljoinFormCategory>
    implements ActAljoinFormCategoryService {

    @Override
    public Page<ActAljoinFormCategory> list(PageBean pageBean, ActAljoinFormCategory obj) throws Exception {
        Where<ActAljoinFormCategory> where = new Where<ActAljoinFormCategory>();
        if (null != obj.getCategoryName()) {
            where.like("category_name", obj.getCategoryName());
        }
        if (null != obj.getIsActive()) {
            where.eq("is_active", obj.getIsActive());
        }
        if (null != obj.getCategoryLevel()) {
            where.eq("category_level", obj.getCategoryLevel());
        }
        where.orderBy("create_time", false);
        where.setSqlSelect("id,category_name,is_active,category_rank,parent_id,category_level,create_time");
        Page<ActAljoinFormCategory> page =
            selectPage(new Page<ActAljoinFormCategory>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public Page<ActAljoinFormCategory> selectListByParentId(PageBean pageBean, ActAljoinFormCategory obj)
        throws Exception {
        Where<ActAljoinFormCategory> where = new Where<ActAljoinFormCategory>();
        if (null != obj.getParentId()) {
            where.eq("parent_id", obj.getParentId());
        }
        if (null != obj.getIsActive()) {
            where.eq("is_active", obj.getIsActive());
        }
        where.orderBy("create_time", false);
        where.setSqlSelect("id,category_name,is_active,category_rank,parent_id,category_level,create_time");
        Page<ActAljoinFormCategory> page =
            selectPage(new Page<ActAljoinFormCategory>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public List<ActAljoinFormCategory> selectCategoryList(ActAljoinFormCategory obj) throws Exception {
        Where<ActAljoinFormCategory> where = new Where<ActAljoinFormCategory>();

        if (null != obj.getIsActive()) {
            where.eq("is_active", obj.getIsActive());
        }

        if (null != obj.getParentId()) {
            where.eq("parent_id", obj.getParentId());
        }
        where.orderBy("category_rank", true);
        where.setSqlSelect("id,category_name,is_active,category_rank,parent_id,category_level,create_time");

        return selectList(where);
    }

    @Override
    public boolean validCategoryName(ActAljoinFormCategory obj, boolean isAdd) throws Exception {
        boolean flag = false;
        if (null != obj) {
            if (null != obj.getCategoryName()) {
                Where<ActAljoinFormCategory> where = new Where<ActAljoinFormCategory>();
                where.eq("category_name", obj.getCategoryName());
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
    public boolean validCategoryLevel(ActAljoinFormCategory obj) throws Exception {
        List<ActAljoinFormCategory> categoryList = this.selectCategoryList(obj);
        boolean flag = true;
        if (!categoryList.isEmpty()) {
            if (categoryList.size() > 999) {
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public List<ActAljoinFormCategory> getAllChildCategoryList(Long parentId) throws Exception {
        List<ActAljoinFormCategory> actCateGory = new ArrayList<ActAljoinFormCategory>();
        List<ActAljoinFormCategory> returnList = new ArrayList<ActAljoinFormCategory>();
        Where<ActAljoinFormCategory> cgWhere = new Where<ActAljoinFormCategory>();
        cgWhere.setSqlSelect("id");
        cgWhere.eq("parent_id", parentId);
        actCateGory = this.selectList(cgWhere);
        if (actCateGory != null && actCateGory.size() > 0) {
            returnList.addAll(actCateGory);
            for (ActAljoinFormCategory actAljoinCategory : actCateGory) {
                List<ActAljoinFormCategory> returnLists = new ArrayList<ActAljoinFormCategory>();
                returnLists = getAllChildCategoryList(actAljoinCategory.getId());
                if (returnLists != null && returnLists.size() > 0) {
                    returnList.addAll(returnLists);
                }
            }
        }
        return returnList;
    }
    @Override
	public List<ActAljoinFormCategory> getCateGoryList() {
		// TODO Auto-generated method stub
		List<ActAljoinFormCategory> cateGoryList=new ArrayList<ActAljoinFormCategory>();
		ActAljoinFormCategory cateGory=new ActAljoinFormCategory();
		cateGory.setId(0L);
		cateGory.setCategoryName("全部");		
		cateGoryList.add(cateGory);
		Where<ActAljoinFormCategory> where = new Where<ActAljoinFormCategory>();
		where.setSqlSelect("id,category_name,is_active,category_rank,parent_id,category_level");
		where.orderBy("category_rank", true);
		where.orderBy("category_name", true);
		List<ActAljoinFormCategory> goryList = this.selectList(where);		
		if(goryList!=null && goryList.size()>0){
			cateGoryList.addAll(goryList);
			
		}
		return cateGoryList;
	}
}
