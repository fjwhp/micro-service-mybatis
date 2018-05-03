package aljoin.sys.service;

import aljoin.sys.dao.entity.SysDocumentNumber;
import aljoin.sys.dao.mapper.SysDocumentNumberMapper;
import aljoin.sys.dao.object.SysDocumentNumberVO;
import aljoin.sys.iservice.SysDictCategoryService;
import aljoin.sys.iservice.SysDocumentNumberService;
import aljoin.util.DateUtil;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import aljoin.dao.config.Where;
import aljoin.object.RetMsg;

/**
 * 
 * @描述：公文文号表(服务实现类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-26
 */
@Service
public class SysDocumentNumberServiceImpl extends ServiceImpl<SysDocumentNumberMapper, SysDocumentNumber> implements SysDocumentNumberService {

  @Resource
  private SysDocumentNumberMapper mapper;
  @Resource
  private SysDictCategoryService sysDictCategoryService;

  
  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }

  @Override
  public void copyObject(SysDocumentNumber obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  @Transactional
  public RetMsg add(SysDocumentNumberVO obj) {
      RetMsg retMsg = new RetMsg();
      HashMap<String, Object> map = new HashMap<String, Object>();
      if (null != obj) {
          SysDocumentNumber documentNumber = new SysDocumentNumber();
          BeanUtils.copyProperties(obj, documentNumber);
          map.put("document_num_name", documentNumber.getDocumentNumName());
          map.put("category_id",documentNumber.getCategoryId());
          //文号名称是否唯一
          List<SysDocumentNumber> list = selectByMap(map);
          if(!list.isEmpty()){
              retMsg.setCode(1);
              retMsg.setMessage("文号名称已存在");
              return retMsg;
          }
          if (0 == obj.getDigit()) {
              retMsg.setCode(1);
              retMsg.setMessage("位数不能为0");
              return retMsg;
          }
          if (obj.getDigit() > 10) {
              retMsg.setCode(1);
              retMsg.setMessage("位数不能超过10位");
              return retMsg;
          }
          if (null == documentNumber.getIsFixLength()) {
              documentNumber.setIsFixLength(0);
          }
          Long startValue = documentNumber.getStartValue();
          documentNumber.setCurrentValue(startValue);
          if (null != obj.getDocNumPattern() && obj.getDocNumPattern().length > 0) {
              String[] docNumPattern = obj.getDocNumPattern();
              StringBuilder documentNumPattern = new StringBuilder();
              for (String pattern : docNumPattern) {
                  documentNumPattern.append(pattern).append("&");
              }
              documentNumber.setDocumentNumPattern(documentNumPattern.toString());
          }
          //同一分类里面文号是否唯一
          map.clear();
          map.put("category_id",documentNumber.getCategoryId());
          List<SysDocumentNumber> list1 = selectByMap(map);
          String documentNum = getDocumentNum(documentNumber);  
          if (null != list1 && list1.size() > 0) {
              for (SysDocumentNumber sysDocumentNumber : list1) {
                  if (documentNum.equals(getDocumentNum(sysDocumentNumber))) {
                      retMsg.setCode(1);
                      retMsg.setMessage("文号重复");
                      return retMsg;
                  }
              }
          }
          if (startValue.toString().length() > documentNumber.getDigit()) {
              retMsg.setCode(1);
              retMsg.setMessage("起始值大于位数");
              return retMsg;
          }
          if (StringUtils.isEmpty(documentNumber.getDocumentNumName())) {
              retMsg.setCode(1);
              retMsg.setMessage("文号名称不能为空");
              return retMsg;
          }
          if (null == documentNumber.getStartValue()) {
              retMsg.setCode(1);
              retMsg.setMessage("起始值不能为空");
              return retMsg;
          }
          if (null == documentNumber.getDigit()) {
              retMsg.setCode(1);
              retMsg.setMessage("位数不能为空");
              return retMsg;
          }
          if (StringUtils.isEmpty(documentNumber.getAgencyCode())) {
              documentNumber.setAgencyCode("");
          }
          //默认停用
          documentNumber.setStatus(0);
          insert(documentNumber);
      }
      retMsg.setCode(0);
      retMsg.setMessage("操作成功");
      return retMsg;
  }
  
  @Override
  public String getDocumentNum(SysDocumentNumber sysDocumentNumber) {
    String date= "";
    String agencyCode = sysDocumentNumber.getAgencyCode();
    String dateString = DateUtil.date2str(new Date());
    String[] dateArr = dateString.split("-");
    String yearStr = dateArr[0];
    if (2 == sysDocumentNumber.getReignTitleRule()) {
        date = yearStr;
    }
    String currentValueStr = "";
    Long currentValue = sysDocumentNumber.getCurrentValue();
    Integer digit = sysDocumentNumber.getDigit();
    if (sysDocumentNumber.getIsFixLength() == 1) {
        currentValueStr = String.format("%0"+digit+"d", currentValue);
    } else {
        currentValueStr = String.valueOf(currentValue);
    }
    String[] docNumPatterns = null;
    String documentNumPattern = sysDocumentNumber.getDocumentNumPattern();
    if (StringUtils.isNotEmpty(documentNumPattern)) {
       docNumPatterns = documentNumPattern.trim().split("&");
    }
    String documentNumName = null;
    if (docNumPatterns.length == 1) {
        documentNumName = agencyCode+docNumPatterns[0]+date+currentValueStr;
    } else if (docNumPatterns.length == 2) {
        documentNumName = agencyCode+docNumPatterns[0]+date+docNumPatterns[1]+currentValueStr;
    } else if (docNumPatterns.length == 3) {
        documentNumName = agencyCode+docNumPatterns[0]+date+docNumPatterns[1]+currentValueStr+docNumPatterns[2];
    } else {
        documentNumName = agencyCode+date+currentValueStr;
    }
    return documentNumName;
  }
  
  
  @Override
  @Transactional
  public void resetByYear() {
      Where<SysDocumentNumber> where = new Where<SysDocumentNumber>();
      where.eq("resetting_rule", 2);
      List<SysDocumentNumber> documentNumbers = selectList(where);
      if (null != documentNumbers && !documentNumbers.isEmpty()) {
          for (SysDocumentNumber documentNumber : documentNumbers) {
              documentNumber.setCurrentValue(documentNumber.getStartValue());
          }
          updateBatchById(documentNumbers);
      }
  }

  @Override
  @Transactional
  public RetMsg update(SysDocumentNumberVO obj) {
      RetMsg retMsg = new RetMsg();
      HashMap<String, Object> map = new HashMap<String, Object>();
      SysDocumentNumber orgnlObj = selectById(obj.getId());
      if (null != orgnlObj) {
        if (1 == orgnlObj.getStatus()) {
           retMsg.setCode(1);
           retMsg.setMessage("文号未停用");
           return retMsg;
        }
        if (null != obj) {
            if (!orgnlObj.getDocumentNumName().equals(obj.getDocumentNumName())) {
                map.put("document_num_name", obj.getDocumentNumName());
                map.put("category_id",obj.getCategoryId());
                //流水号名称是否唯一
                List<SysDocumentNumber> list = selectByMap(map);
                if(!list.isEmpty()){
                    retMsg.setCode(1);
                    retMsg.setMessage("文号名称已存在");
                    return retMsg;
                }
            }
            if (0 == obj.getDigit()) {
                retMsg.setCode(1);
                retMsg.setMessage("位数不能为0");
                return retMsg;
            }
            if (obj.getDigit() > 10) {
                retMsg.setCode(1);
                retMsg.setMessage("位数不能超过10位");
                return retMsg;
            }
            if (obj.getStartValue().toString().length() > obj.getDigit()) {
                retMsg.setCode(1);
                retMsg.setMessage("起始值大于位数");
                return retMsg;
            }
            if (obj.getStartValue() > obj.getCurrentValue()) {
                retMsg.setCode(1);
                retMsg.setMessage("起始值大于当前值");
                return retMsg;
            }
            StringBuilder documentNumPattern = new StringBuilder();
            if (null != obj.getDocNumPattern() && obj.getDocNumPattern().length > 0) {
                String[] docNumPattern = obj.getDocNumPattern();
                for (String pattern : docNumPattern) {
                    documentNumPattern.append(pattern).append("&");
                }
                obj.setDocumentNumPattern(documentNumPattern.toString());
            }
            if (null == obj.getIsFixLength()) {
                obj.setIsFixLength(0);
            }
            //判断同一分类中文号是否重复
            if (!getDocumentNum(orgnlObj).equals(getDocumentNum(obj))) {
                map.clear();
                map.put("category_id",orgnlObj.getCategoryId());
                List<SysDocumentNumber> list1 = selectByMap(map);
                String documentNum = getDocumentNum(obj);  
                if (null != list1 && list1.size() > 0) {
                    for (SysDocumentNumber sysDocumentNumber : list1) {
                        if (documentNum.equals(getDocumentNum(sysDocumentNumber))) {
                            retMsg.setCode(1);
                            retMsg.setMessage("文号重复");
                            return retMsg;
                        }
                    }
                }
            }
            orgnlObj.setIsFixLength(obj.getIsFixLength());
            orgnlObj.setDocumentNumPattern(documentNumPattern.toString());
            if (null != obj.getDocumentNumName()) {
                orgnlObj.setDocumentNumName(obj.getDocumentNumName());
            }
            if (null != obj.getAgencyCode()) {
                orgnlObj.setAgencyCode(obj.getAgencyCode());
            }
            orgnlObj.setReignTitleRule(obj.getReignTitleRule());
            if (null != obj.getStartValue()) {
                orgnlObj.setStartValue(obj.getStartValue());
            }
            if (null != obj.getDigit()) {
                orgnlObj.setDigit(obj.getDigit());
            }
            orgnlObj.setResettingRule(obj.getResettingRule());
            if (null != obj.getCurrentValue()) {
                orgnlObj.setCurrentValue(obj.getCurrentValue());
            }
        }
        updateById(orgnlObj);
      }
      retMsg.setCode(0);
      retMsg.setMessage("操作成功");
      return retMsg;
  }

  @Override
  @Transactional
  public RetMsg updateCurrentValue(SysDocumentNumber obj) {
     RetMsg retMsg = new RetMsg();
     if (null != obj) {
         SysDocumentNumber orgnlObj = selectById(obj.getId());
         if (obj.getCurrentValue() < orgnlObj.getStartValue()) {
             retMsg.setCode(1);
             retMsg.setMessage("当前值小于起始值");
             return retMsg;
         }
         if (obj.getCurrentValue().toString().length() > orgnlObj.getDigit()) {
             retMsg.setCode(1);
             retMsg.setMessage("起始值超出位数");
             return retMsg;
         }
         orgnlObj.setCurrentValue(obj.getCurrentValue());
         updateById(orgnlObj);
     }
     retMsg.setCode(0);
     retMsg.setMessage("操作成功");
     return retMsg;
  }

  @Override
  @Transactional
  public RetMsg delete(SysDocumentNumber obj) {
      RetMsg retMsg = new RetMsg();
      SysDocumentNumber orgnlObj = selectById(obj.getId());
      if (null != orgnlObj) {
          if (StringUtils.isEmpty(orgnlObj.getBpmnIds())) {
              Where<SysDocumentNumber> where = new Where<SysDocumentNumber>();
              where.eq("id", orgnlObj.getId());
              delete(where);
          } else {
              retMsg.setCode(1);
              retMsg.setMessage("删除失败，请移除文号关联的流程后重新操作！");
              return retMsg;
          }
      }
      retMsg.setCode(0);
      retMsg.setMessage("操作成功");
      return retMsg;
  }

  @Override
  @Transactional
  public RetMsg updateStatus(SysDocumentNumber obj) {
      RetMsg retMsg = new RetMsg();
      SysDocumentNumber orgnlObj = selectById(obj.getId());
      if (null != obj) {
          if (obj.getStatus() == 1) {
              orgnlObj.setStatus(0);
          } else {
              orgnlObj.setStatus(1);
          }
      }
      updateById(orgnlObj);
      retMsg.setCode(0);
      retMsg.setMessage("操作成功");
      return retMsg;
  }
  
  @Override
  public RetMsg getDocumentNumber(SysDocumentNumber obj) {
      RetMsg retMsg = new RetMsg();
      String documentNum = null;
      if (null != obj && null != obj.getId()) {
          SysDocumentNumber orgnlObj = selectById(obj.getId());
          if (null != orgnlObj) {
              documentNum = getDocumentNum(orgnlObj);
              Long currentValue = orgnlObj.getCurrentValue();
              if (0 == orgnlObj.getStatus()) {
                  retMsg.setCode(1);
                  retMsg.setMessage("文号已停用");
                  return retMsg;
              }
              if (currentValue.toString().length() == (orgnlObj.getDigit() + 1)) {
                  retMsg.setCode(1);
                  retMsg.setMessage("当前文号值已使用完，请重新设置");
                  return retMsg;
              }
              orgnlObj.setCurrentValue(currentValue + 1);
              updateById(orgnlObj);
          }
      }
      retMsg.setCode(0);
      retMsg.setMessage("操作成功");
      retMsg.setObject(documentNum);
      return retMsg;
  }

  @Override
  @Transactional
  public void bindBpmn(String bpmnId, List<String> documentNumIds) {
      if (null != documentNumIds && documentNumIds.size() > 0) {
          for (String documentNumId : documentNumIds) {
              SysDocumentNumber documentNumber = selectById(documentNumId);
              String bpmnIds = documentNumber.getBpmnIds();
              if (StringUtils.isEmpty(bpmnIds)) {
                  documentNumber.setBpmnIds(bpmnId);
                  updateById(documentNumber);
              } else {
                  String[] bpmnIdArr = bpmnIds.split(";");
                  StringBuilder ids = new StringBuilder();
                  if (null != bpmnIdArr) {
                      ids.append(bpmnId).append(";");
                      for (String id : bpmnIdArr) {
                          ids.append(id).append(";");
                      }
                  }
                  documentNumber.setBpmnIds(bpmnIds.toString());
                  updateById(documentNumber);
              }
          }
      }
  }
  
  @Override
  @Transactional
  public void unBindBpmn(String bpmnId) {
      Where<SysDocumentNumber> where = new Where<SysDocumentNumber>();
      where.like("bpmn_ids", bpmnId);
      List<SysDocumentNumber> selectList = selectList(where);
      for (SysDocumentNumber documentNumber : selectList) {
          String bpmnIds = documentNumber.getBpmnIds();
          if (StringUtils.isNotEmpty(bpmnIds)) {
              String[] bpmnIdArr = bpmnIds.split(";");
              if (bpmnIdArr.length == 1) {
                  documentNumber.setBpmnIds("");
              } else {
                  bpmnIds = bpmnIds.replace(bpmnId+";", "");
                  documentNumber.setBpmnIds(bpmnIds);
              }
          }
      }
      if (null != selectList && selectList.size() > 0) {
          updateBatchById(selectList);
      }
  }

  @Override
  public List<SysDocumentNumber> getList(SysDocumentNumber obj) {
     List<SysDocumentNumber> documentNumberList = new ArrayList<SysDocumentNumber>();
     SysDocumentNumber sysDocumentNumber = new SysDocumentNumber();
     sysDocumentNumber.setDocumentNumName("全选");
     Where<SysDocumentNumber> where = new Where<SysDocumentNumber>();
     where.eq("category_id", obj.getCategoryId());
     where.eq("status", 1);
     where.setSqlSelect("id,document_num_name");
     where.orderBy("create_time", false);
     List<SysDocumentNumber> list = selectList(where);
     documentNumberList.add(sysDocumentNumber);
     documentNumberList.addAll(list);
     return documentNumberList;
  }

  @Override
  public RetMsg getPreviewDocumentNumber(SysDocumentNumber obj) {
    RetMsg retMsg = new RetMsg();
    String documentNum = null;
    if (null != obj && null != obj.getId()) {
        SysDocumentNumber orgnlObj = selectById(obj.getId());
        orgnlObj.setCurrentValue(orgnlObj.getStartValue());
        documentNum = getDocumentNum(orgnlObj);
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    retMsg.setObject(documentNum);
    return retMsg;
  }

}
