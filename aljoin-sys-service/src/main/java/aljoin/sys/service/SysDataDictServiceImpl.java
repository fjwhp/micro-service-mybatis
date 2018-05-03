package aljoin.sys.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.sys.dao.entity.SysDataDict;
import aljoin.sys.dao.mapper.SysDataDictMapper;
import aljoin.sys.iservice.SysDataDictService;

/**
 * 
 * 数据字典表(服务实现类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-05-27
 */
@Service
public class SysDataDictServiceImpl extends ServiceImpl<SysDataDictMapper, SysDataDict>
    implements SysDataDictService {

	@Override
  public Page<SysDataDict> list(PageBean pageBean, SysDataDict obj) throws Exception {
    Where<SysDataDict> where = new Where<SysDataDict>();
    if (StringUtils.isNotEmpty(obj.getDictCode())) {
      where.like("dict_code", obj.getDictCode());
      where.or("dict_name LIKE {0}", "%" + obj.getDictCode() + "%");
    }
    where.setSqlSelect("DISTINCT dict_code,dict_name,dict_type");
    where.groupBy("dict_code,dict_name,dict_type");
   // where.orderBy("dict_rank", true);
    Page<SysDataDict> page =
        selectPage(new Page<SysDataDict>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    return page;
  }

  @Transactional
	@Override
  public void update(SysDataDict obj, String[] dictKey1, String[] dictValue1, String[] isActive1,
      String[] id1,Integer[] dictRank) throws Exception {
    List<Long> idList = new ArrayList<Long>();
    for (String s : id1) {
      if (StringUtils.isNotEmpty(s)) {
        idList.add(Long.parseLong(s));
      }
    }
    // 删除
    Where<SysDataDict> where = new Where<SysDataDict>();
    where.eq("dict_code", obj.getDictCode());
    List<SysDataDict> list = selectList(where);
    for (SysDataDict sysDataDict : list) {
      if (!idList.contains(sysDataDict.getId())) {
        deleteById(sysDataDict.getId());
      }
    }
    // 插入或修改
    for (int i = 0; i < id1.length; i++) {
      if (StringUtils.isNotEmpty(id1[i])) {
        // 修改
        SysDataDict dict = selectById(Long.parseLong(id1[i]));
        dict.setDictName(obj.getDictName());
        dict.setDictRank(dictRank[i]);
        dict.setDictType(obj.getDictType());
        dict.setDictKey(dictKey1[i]);
        dict.setDictValue(dictValue1[i]);
        dict.setIsActive(Integer.parseInt(isActive1[i]));
        updateById(dict);
      } else {
        // 新增
        SysDataDict dict = new SysDataDict();
        dict.setDictCode(obj.getDictCode());
        dict.setDictName(obj.getDictName());
        dict.setDictRank(dictRank[i]);
        dict.setDictType(obj.getDictType());
        dict.setDictKey(dictKey1[i]);
        dict.setDictValue(dictValue1[i]);
        dict.setIsActive(Integer.parseInt(isActive1[i]));
        insert(dict);
      }

    }
  }

  @Override
  public List<SysDataDict> getByCode(String code) throws Exception{
    List<SysDataDict> dataDictList =  null;
    Where<SysDataDict> where = new Where<SysDataDict>();
    where.setSqlSelect("id,dict_code,dict_name,dict_key,dict_value,dict_rank");
    where.eq("dict_code",code);
    where.orderBy("dict_rank");
    dataDictList = selectList(where);
    return dataDictList;
  }
}
