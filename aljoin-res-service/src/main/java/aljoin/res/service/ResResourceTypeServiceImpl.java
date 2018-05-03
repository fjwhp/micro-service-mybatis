package aljoin.res.service;

import aljoin.res.dao.entity.ResResource;
import aljoin.res.dao.entity.ResResourceType;
import aljoin.res.dao.mapper.ResResourceTypeMapper;
import aljoin.res.iservice.ResResourceService;
import aljoin.res.iservice.ResResourceTypeService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.plugins.Page;

import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 资源分类表(服务实现类).
 * 
 * @author：laijy
 * 
 * @date： 2017-09-05
 */
@Service
public class ResResourceTypeServiceImpl extends ServiceImpl<ResResourceTypeMapper, ResResourceType>
		implements ResResourceTypeService {

	@Resource
	ResResourceTypeService resResourceTypeService;
	@Resource
	ResResourceService resResourceService;

	@Override
	public Page<ResResourceType> list(PageBean pageBean, ResResourceType obj) throws Exception {
		Where<ResResourceType> where = new Where<ResResourceType>();
		where.orderBy("type_rank", true);
		Page<ResResourceType> page = selectPage(
				new Page<ResResourceType>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		return page;
	}

	@Override
	public Boolean outNumber(ResResourceType obj) {
		int resourceNum = 0;
		Where<ResResourceType> where = new Where<ResResourceType>();
		if (obj.getTypeLevel() > 1) {
			where.eq("id", obj.getParentId());
			where.eq("type_level", obj.getTypeLevel());
			resourceNum = resResourceTypeService.selectCount(where);
		} else {
			where.eq("type_level", obj.getTypeLevel());
			resourceNum = resResourceTypeService.selectCount(where);
		}
		return (resourceNum > 999);
	}

	@Override
	public Boolean compareTypeName(ResResourceType obj) throws Exception {

		List<ResResourceType> list = resResourceTypeService.getAllResourceTypeList();

		Boolean judge = false;
		// 有从表中查到记录，再把前台传来的分类名称进行对比
		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				// 查询到同名的，则赋值false
				if (list.get(i).getTypeName().equals(obj.getTypeName())) {
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
	public List<ResResourceType> getAllResourceTypeList() throws Exception {
		Where<ResResourceType> where = new Where<ResResourceType>();
		where.setSqlSelect("id,type_name,is_active,type_rank,parent_id,type_level");
		where.orderBy("type_rank", true);
		List<ResResourceType> list = selectList(where);
		return list;
	}

	@Override
	public ResResourceType getById(ResResourceType obj) {
		
		Where<ResResourceType> where = new Where<ResResourceType>();
		where.setSqlSelect("id,type_name,is_active,type_rank,parent_id,type_level");
		where.orderBy("type_rank", true);
		where.eq("id",obj.getId());
		ResResourceType resourceType = selectOne(where);
		return resourceType;
	}

	@Override
	public List<ResResourceType> getResourceListByPid(ResResourceType obj) {
		List<ResResourceType> resourceChildList = new ArrayList<ResResourceType>();
		//没有Pid则返回1级分类
		if(obj.getParentId()==null){
			Where<ResResourceType> where = new Where<ResResourceType>();
			where.setSqlSelect("id,type_name,is_active,type_rank,parent_id,type_level");
			where.orderBy("type_rank", true);
			where.eq("type_level",1);
			resourceChildList = resResourceTypeService.selectList(where);
		}else{//有则根据Pid返回子分类List
			Where<ResResourceType> where = new Where<ResResourceType>();
			where.setSqlSelect("id,type_name,is_active,type_rank,parent_id,type_level");
			where.orderBy("type_rank", true);
			where.eq("parent_id",obj.getParentId());
			resourceChildList = resResourceTypeService.selectList(where);
		}
		return resourceChildList;
	}

    @Override
    public RetMsg delete(ResResourceType obj) {
        RetMsg retMsg = new RetMsg();
        Where<ResResource> resourceWhere = new Where<ResResource>();
        resourceWhere.setSqlSelect("id,file_type_id");
        resourceWhere.eq("file_type_id", obj.getId());
        List<ResResource> resources = resResourceService.selectList(resourceWhere);
        if (null != resources && resources.size() > 0) {
            retMsg.setCode(1);
            retMsg.setMessage("未移除分类下文件");
            return retMsg;
        }
        deleteById(obj.getId());
        retMsg.setCode(0);
        retMsg.setMessage("删除成功");
        return null;
    }
}
