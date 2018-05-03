package aljoin.sys.service;

import aljoin.sys.dao.entity.SysCommonDict;
import aljoin.sys.dao.entity.SysDictCategory;
import aljoin.sys.dao.entity.SysDocumentNumber;
import aljoin.sys.dao.entity.SysSerialNumber;
import aljoin.sys.dao.mapper.SysDictCategoryMapper;
import aljoin.sys.iservice.SysCommonDictService;
import aljoin.sys.iservice.SysDictCategoryService;
import aljoin.sys.iservice.SysDocumentNumberService;
import aljoin.sys.iservice.SysSerialNumberService;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;

import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * @描述：数据字典分类表(服务实现类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-18
 */
@Service
public class SysDictCategoryServiceImpl extends ServiceImpl<SysDictCategoryMapper, SysDictCategory> implements SysDictCategoryService {

  @Resource
  private SysDictCategoryMapper mapper;
  @Resource
  private SysCommonDictService sysCommonDictService;
  @Resource
  private SysSerialNumberService sysDictCategoryService;
  @Resource
  private SysDocumentNumberService sysDocumentNumberService;

  @Override
  public Page<SysDictCategory> list(PageBean pageBean, SysDictCategory obj) throws Exception {
	Where<SysDictCategory> where = new Where<SysDictCategory>();
	where.orderBy("create_time", false);
	Page<SysDictCategory> page = selectPage(new Page<SysDictCategory>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	
  
  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }

  @Override
  public void copyObject(SysDictCategory obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  public List<SysDictCategory> getCategoryList(SysDictCategory obj) {
    List<SysDictCategory> categories = new ArrayList<SysDictCategory>();
    SysDictCategory sysDictCategory = new SysDictCategory();
    sysDictCategory.setCategoryName("全部");
    sysDictCategory.setParentId(0L);
    categories.add(sysDictCategory);
    Where<SysDictCategory> where = new Where<SysDictCategory>();
    where.eq("dict_type", obj.getDictType());
    where.setSqlSelect("id,category_name,parent_id");
    where.orderBy("category_rank", true);
    where.orderBy("category_name", true);
    List<SysDictCategory> selectList = this.selectList(where);  
    categories.addAll(selectList);
    return categories;
  }

  @Override
  @Transactional
  public RetMsg add(SysDictCategory obj) {
      RetMsg retMsg = new RetMsg();
      if (null != obj) {
          boolean flag = validCategoryName(obj,true);
          if (!flag) {
              if (null == obj.getParentId()) {
                  obj.setParentId(0L);
                  obj.setCategoryLevel(1);
                  //默认激活
                  obj.setIsActive(1);
              }
              insert(obj);
              retMsg.setCode(0);
              retMsg.setMessage("操作成功");
          } else {
              retMsg.setCode(1);
              retMsg.setMessage("分类名称已存在");
          }
      } 
      return retMsg;
  }

  @Override
  public boolean validCategoryName(SysDictCategory obj, boolean isAdd) {
      boolean flag = false;
      if (null != obj) {
          if (null != obj.getCategoryName()) {
              Where<SysDictCategory> where = new Where<SysDictCategory>();
              where.eq("category_name", obj.getCategoryName());
              where.eq("dict_type", obj.getDictType());
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
  @Transactional
  public RetMsg update(SysDictCategory obj) {
    RetMsg retMsg = new RetMsg();
    if (null != obj) {
        SysDictCategory original = selectById(obj.getId());
        boolean flag = validCategoryName(obj,false);
        if (!flag) {
            original.setCategoryName(obj.getCategoryName());
            original.setCategoryRank(obj.getCategoryRank());
            updateById(original);
        }
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

    @Override
    @Transactional
    public RetMsg delete(SysDictCategory obj) {
        RetMsg retMsg = new RetMsg();
        HashMap<String, Object> map = new HashMap<>();
        map.put("category_id", obj.getId());
        if (null != obj) {
            if (obj.getDictType() == 3) {
                List<SysCommonDict> list = sysCommonDictService.selectByMap(map);
                if(!list.isEmpty()){
                    retMsg.setCode(1);
                    retMsg.setMessage("删除失败，请移除分类中的数据后重新操作！");
                    return retMsg;
                }
            }
            if (obj.getDictType() == 2) {
                List<SysDocumentNumber> list = sysDocumentNumberService.selectByMap(map);
                if(!list.isEmpty()){
                    retMsg.setCode(1);
                    retMsg.setMessage("删除失败，请移除分类中的文号后重新操作！");
                    return retMsg;
                }
            }
            if (obj.getDictType() == 1) {
                List<SysSerialNumber> list = sysDictCategoryService.selectByMap(map);
                if(!list.isEmpty()){
                    retMsg.setCode(1);
                    retMsg.setMessage("删除失败，请移除分类中的流水号后重新操作！");
                    return retMsg;
                }
            }
            deleteById(obj.getId());
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }
    
    
}
