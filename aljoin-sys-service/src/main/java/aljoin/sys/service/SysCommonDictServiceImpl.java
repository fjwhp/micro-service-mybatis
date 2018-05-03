package aljoin.sys.service;

import aljoin.sys.dao.entity.SysCommonDict;
import aljoin.sys.dao.mapper.SysCommonDictMapper;
import aljoin.sys.iservice.SysCommonDictService;
import aljoin.util.StringUtil;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import aljoin.dao.config.Where;
import aljoin.object.RetMsg;

/**
 * 
 * @描述：常用字典表(服务实现类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-18
 */
@Service
public class SysCommonDictServiceImpl extends ServiceImpl<SysCommonDictMapper, SysCommonDict> implements SysCommonDictService {

  @Resource
  private SysCommonDictMapper mapper;
  
  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }

  @Override
  public void copyObject(SysCommonDict obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  @Transactional
  public RetMsg add(SysCommonDict obj, String[] dictContent, Integer[] dictContentRank) {
      RetMsg retMsg = new RetMsg();
      HashMap<String, Object> map = new HashMap<String, Object>();
      if (null != obj) {
          if (null == obj.getCategoryId()) {
              retMsg.setCode(1);
              retMsg.setMessage("请先选择一个分类");
              return retMsg;
          }
      }
      map.put("dict_name", obj.getDictName());
      map.put("category_id",obj.getCategoryId());
      //字典名称是否唯一
      List<SysCommonDict> list = selectByMap(map);
      if(!list.isEmpty()){
          retMsg.setCode(1);
          retMsg.setMessage("字典名称已存在");
          return retMsg;
      }
      //生成常用字典唯一值
      String uuid = StringUtil.getUUID();
      for (int i = 0; i < dictContent.length; i++) {
          SysCommonDict dataDict = new SysCommonDict();
          dataDict.setDictName(obj.getDictName());
          dataDict.setDictRank(obj.getDictRank());
          dataDict.setIsActive(1);
          dataDict.setDictContent(dictContent[i]);
          dataDict.setDictContentRank(dictContentRank[i]);
          dataDict.setCategoryId(obj.getCategoryId());
          dataDict.setDictCode(uuid);
          insert(dataDict);
      }
      retMsg.setCode(0);
      retMsg.setMessage("操作成功");
      return retMsg;
  }

  @Override
  @Transactional
  public RetMsg update(SysCommonDict obj, String[] dictContent, Integer[] dictContentRank, String[] commonDictId) {
      RetMsg retMsg = new RetMsg();
      HashMap<String, Object> map = new HashMap<String, Object>();
      SysCommonDict orgnlObj1 = selectById(commonDictId[0]);
      if (!orgnlObj1.getDictName().equals(obj.getDictName())) {
          map.put("dict_name", obj.getDictName());
          map.put("category_id",obj.getCategoryId());
          //字典名称是否唯一
          List<SysCommonDict> list = selectByMap(map);
          if(!list.isEmpty()){
              retMsg.setCode(1);
              retMsg.setMessage("字典名称已存在");
              return retMsg;
          }
      }
      List<Long> idList = new ArrayList<Long>();
      for (String s : commonDictId) {
        if (StringUtils.isNotEmpty(s)) {
          idList.add(Long.parseLong(s));
        }
      }
      // 删除
      Where<SysCommonDict> where = new Where<SysCommonDict>();
      where.eq("dict_code", obj.getDictCode());
      List<SysCommonDict> list = selectList(where);
      for (SysCommonDict sysCommonDict : list) {
        if (!idList.contains(sysCommonDict.getId())) {
          deleteById(sysCommonDict.getId());
        }
      }
      // 插入或修改
      for (int i = 0; i < commonDictId.length; i++) {
        if (StringUtils.isNotEmpty(commonDictId[i])) {
          // 修改
          SysCommonDict orgnlObj = selectById(Long.parseLong(commonDictId[i]));
          orgnlObj.setDictName(obj.getDictName());
          orgnlObj.setDictRank(obj.getDictRank());
          orgnlObj.setDictContent(dictContent[i]);
          orgnlObj.setDictContentRank(dictContentRank[i]);
          updateById(orgnlObj);
        } else {
          // 新增
          SysCommonDict orgnlObj = new SysCommonDict();
          orgnlObj.setDictName(obj.getDictName());
          orgnlObj.setDictRank(obj.getDictRank());
          orgnlObj.setCategoryId(obj.getCategoryId());
          orgnlObj.setDictContent(dictContent[i]);
          orgnlObj.setDictContentRank(dictContentRank[i]);
          orgnlObj.setDictCode(obj.getDictCode());
          orgnlObj.setIsActive(1);
          insert(orgnlObj);
        }
      }
      retMsg.setCode(0);
      retMsg.setMessage("操作成功");
      return retMsg;
  }

    @Override
    @Transactional
    public RetMsg deleteByDictCode(SysCommonDict obj) {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            Where<SysCommonDict> where = new Where<SysCommonDict>();
            where.eq("dict_code", obj.getDictCode());
            delete(where);
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public List<SysCommonDict> getListByCategory(SysCommonDict obj) {
        Where<SysCommonDict> where = new Where<SysCommonDict>();
        where.eq("category_id",obj.getCategoryId());
        where.groupBy("dict_code,dict_name");
        where.setSqlSelect("id,dict_name,dict_rank,category_id,dict_code");
        where.orderBy("dict_rank");
        List<SysCommonDict> list = selectList(where);
        return list;
    }

    @Override
    public List<SysCommonDict> getListByDictCode(SysCommonDict obj) {
        Where<SysCommonDict> where = new Where<SysCommonDict>();
        where.eq("dict_code", obj.getDictCode());
        where.setSqlSelect("id,dict_name,dict_rank,dict_content,dict_content_rank,category_id,dict_code");
        where.orderBy("dict_content_rank");
        List<SysCommonDict> list = selectList(where);
        return list;
    }
    
}
