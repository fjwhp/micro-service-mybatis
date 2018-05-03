package aljoin.sys.service;

import aljoin.sys.dao.entity.SysSerialNumber;
import aljoin.sys.dao.mapper.SysSerialNumberMapper;
import aljoin.sys.iservice.SysSerialNumberService;
import aljoin.util.DateUtil;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.apache.commons.lang3.StringUtils;
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
 * @描述：流水号表(服务实现类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-23
 */
@Service
public class SysSerialNumberServiceImpl extends ServiceImpl<SysSerialNumberMapper, SysSerialNumber> implements SysSerialNumberService {

  @Resource
  private SysSerialNumberMapper mapper;

  @Override
  public String getSerialNum(SysSerialNumber sysSerialNumber) {
    String date= "";
    String prefix = sysSerialNumber.getPrefix();
    String sign = sysSerialNumber.getSign();
    String dateString = DateUtil.date2str(new Date());
    String[] dateArr = dateString.split("-");
    String yearStr = dateArr[0];
    String montnStr = dateArr[1];
    String dayStr = dateArr[2];
    if (2 == sysSerialNumber.getReignTitleRule()) {
        date = yearStr;
    }
    if (3 == sysSerialNumber.getReignTitleRule()) {
        date = yearStr + "-" + montnStr;
    }
    if (4 == sysSerialNumber.getReignTitleRule()) {
        date = yearStr + "-" + montnStr + "-" + dayStr;
    }
    String currentValueStr = "";
    Long currentValue = sysSerialNumber.getCurrentValue();
    Integer digit = sysSerialNumber.getDigit();
    if (null == sysSerialNumber.getIsFixLength() || 0 == sysSerialNumber.getIsFixLength()) {
        currentValueStr = String.valueOf(currentValue);
    } else {
        currentValueStr = String.format("%0"+digit+"d", currentValue);
    }
    String postfix = sysSerialNumber.getPostfix();
    String serialNumName = prefix+date+sign+currentValueStr+postfix;
    return serialNumName;
  }

  @Override
  public void physicsDeleteById(Long id) throws Exception{
    mapper.physicsDeleteById(id);
  }

  @Override
  public void copyObject(SysSerialNumber obj) throws Exception{
    mapper.copyObject(obj);
  }

  @Override
  @Transactional
  public RetMsg add(SysSerialNumber obj) {
    RetMsg retMsg = new RetMsg();
    HashMap<String, Object> map = new HashMap<String, Object>();
    if (null != obj) {
        map.put("serial_num_name", obj.getSerialNumName());
        map.put("category_id",obj.getCategoryId());
        //流水号名称是否唯一
        List<SysSerialNumber> list = selectByMap(map);
        if (!list.isEmpty()) {
            retMsg.setCode(1);
            retMsg.setMessage("流水号名称重复");
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
        if (null == obj.getIsFixLength()) {
            obj.setIsFixLength(0);
        }
        Long startValue = obj.getStartValue();
        obj.setCurrentValue(startValue);
        //同一分类里面流水号是否唯一
        map.clear();
        map.put("category_id",obj.getCategoryId());
        List<SysSerialNumber> list1 = selectByMap(map);
        String serialNum = getSerialNum(obj);  
        if (null != list1 && list1.size() > 0) {
            for (SysSerialNumber sysSerialNumber : list1) {
                if (serialNum.equals(getSerialNum(sysSerialNumber))) {
                    retMsg.setCode(1);
                    retMsg.setMessage("流水号重复");
                    return retMsg;
                }
            }
        }
        if (startValue.toString().length() > obj.getDigit()) {
            retMsg.setCode(1);
            retMsg.setMessage("起始值大于位数");
            return retMsg;
        }
        if (StringUtils.isEmpty(obj.getSerialNumName())) {
            retMsg.setCode(1);
            retMsg.setMessage("流程名称不能为空");
            return retMsg;
        }
        if (null == obj.getStartValue()) {
            retMsg.setCode(1);
            retMsg.setMessage("起始值不能为空");
            return retMsg;
        }
        if (null == obj.getDigit()) {
            retMsg.setCode(1);
            retMsg.setMessage("位数不能为空");
            return retMsg;
        }
        //默认停用
        obj.setStatus(0);
        if (StringUtils.isEmpty(obj.getPrefix())) {
            obj.setPrefix("");
        }
        if (StringUtils.isEmpty(obj.getSign())){
            obj.setSign("");
        }
        if (StringUtils.isEmpty(obj.getPostfix())) {
            obj.setPostfix("");
        }
        insert(obj);
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  @Transactional
  public void resetByYear() {
      Where<SysSerialNumber> where = new Where<SysSerialNumber>();
      where.eq("resetting_rule", 2);
      List<SysSerialNumber> serialNumbers = selectList(where);
      if (null != serialNumbers && !serialNumbers.isEmpty()) {
          for (SysSerialNumber sysSerialNumber : serialNumbers) {
              sysSerialNumber.setCurrentValue(sysSerialNumber.getStartValue());
          }
          updateBatchById(serialNumbers);
      }
  }

  @Override
  @Transactional
  public void resetByMonth() {
      Where<SysSerialNumber> where = new Where<SysSerialNumber>();
      where.eq("resetting_rule", 3);
      List<SysSerialNumber> serialNumbers = selectList(where);
      if (null != serialNumbers && !serialNumbers.isEmpty()) {
          for (SysSerialNumber sysSerialNumber : serialNumbers) {
              sysSerialNumber.setCurrentValue(sysSerialNumber.getStartValue());
          }
          updateBatchById(serialNumbers);
      }
  }
  
  @Override
  @Transactional
  public RetMsg update(SysSerialNumber obj) {
    RetMsg retMsg = new RetMsg();
    HashMap<String, Object> map = new HashMap<String, Object>();
    SysSerialNumber orgnlObj = selectById(obj.getId());
    if (null != orgnlObj) {
      if (1 == orgnlObj.getStatus()) {
         retMsg.setCode(1);
         retMsg.setMessage("流水号未停用");
         return retMsg;
      }
      if (null != obj) {
          if (!orgnlObj.getSerialNumName().equals(obj.getSerialNumName())) {
              map.put("serial_num_name", obj.getSerialNumName());
              map.put("category_id",obj.getCategoryId());
              //流水号名称是否唯一
              List<SysSerialNumber> list = selectByMap(map);
              if(!list.isEmpty()){
                  retMsg.setCode(1);
                  retMsg.setMessage("流水号名称重复");
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
          if (!getSerialNum(orgnlObj).equals(getSerialNum(obj))) {
              map.clear();
              map.put("category_id",orgnlObj.getCategoryId());
              List<SysSerialNumber> list1 = selectByMap(map);
              String documentNumberName = getSerialNum(obj);  
              if (null != list1 && list1.size() > 0) {
                  for (SysSerialNumber sysSerialNumber : list1) {
                      if (documentNumberName.equals(getSerialNum(sysSerialNumber))) {
                          retMsg.setCode(1);
                          retMsg.setMessage("流水号重复");
                          return retMsg;
                      }
                  }
              }
          }
          if (null != obj.getSerialNumName()) {
              orgnlObj.setSerialNumName(obj.getSerialNumName());
          }
          if (null != obj.getPrefix()) {
              orgnlObj.setPrefix(obj.getPrefix());
          } else {
              orgnlObj.setPrefix("");
          }
          orgnlObj.setReignTitleRule(obj.getReignTitleRule());
          if (null != obj.getSign()) {
              orgnlObj.setSign(obj.getSign());
          } else {
              orgnlObj.setSign("");
          }
          if (null != obj.getStartValue()) {
              orgnlObj.setStartValue(obj.getStartValue());
          }
          if (null != obj.getDigit()) {
              orgnlObj.setDigit(obj.getDigit());
          }
          if (null != obj.getPostfix()) {
              orgnlObj.setPostfix(obj.getPostfix());
          } else {
              orgnlObj.setPostfix("");
          }
          if (null == obj.getIsFixLength()) {
              orgnlObj.setIsFixLength(0);
          } else {
              orgnlObj.setIsFixLength(obj.getIsFixLength());
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
  public RetMsg updateStatus(SysSerialNumber obj) {
    RetMsg retMsg = new RetMsg();
    SysSerialNumber orgnlObj = selectById(obj.getId());
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
  @Transactional
  public RetMsg delete(SysSerialNumber obj) {
     RetMsg retMsg = new RetMsg();
     SysSerialNumber orgnlObj = selectById(obj.getId());
     if (null != orgnlObj) {
         if (StringUtils.isEmpty(orgnlObj.getBpmnIds())) {
             Where<SysSerialNumber> where = new Where<SysSerialNumber>();
             where.eq("id", orgnlObj.getId());
             delete(where);
         } else {
             retMsg.setCode(1);
             retMsg.setMessage("删除失败，请移除流水号关联的流程后重新操作！");
             return retMsg;
         }
     }
     retMsg.setCode(0);
     retMsg.setMessage("操作成功");
     return retMsg;
  }

  @Override
  @Transactional
  public RetMsg updateCurrentValue(SysSerialNumber obj) {
      RetMsg retMsg = new RetMsg();
      if (null != obj) {
          SysSerialNumber orgnlObj = selectById(obj.getId());
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
  public RetMsg getSerialNumber(SysSerialNumber obj) {
    RetMsg retMsg = new RetMsg();
    String serialNum = null;
    if (null != obj && null != obj.getId()) {
        SysSerialNumber orgnlObj = selectById(obj.getId());
        if (null != orgnlObj) {
            serialNum = getSerialNum(orgnlObj);
            Long currentValue = orgnlObj.getCurrentValue();
            if (0 == orgnlObj.getStatus()) {
                retMsg.setCode(1);
                retMsg.setMessage("流水号已停用");
                return retMsg;
            }
            if (currentValue.toString().length() == (orgnlObj.getDigit() + 1)) {
                retMsg.setCode(1);
                retMsg.setMessage("当前流水号值已使用完，请重新设置");
                return retMsg;
            }
            orgnlObj.setCurrentValue(currentValue + 1);
            updateById(orgnlObj);
        }
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    retMsg.setObject(serialNum);
    return retMsg;
  }

  @Override
  @Transactional
  public void bindBpmn(String bpmnId, List<String> serialNumIds) {
    if (null != serialNumIds && serialNumIds.size() > 0) {
        for (String serialNumId : serialNumIds) {
            SysSerialNumber serialNumber = selectById(serialNumId);
            String bpmnIds = serialNumber.getBpmnIds();
            if (StringUtils.isEmpty(bpmnIds)) {
                serialNumber.setBpmnIds(bpmnId);
                updateById(serialNumber);
            } else {
                String[] bpmnIdArr = bpmnIds.split(";");
                StringBuilder ids = new StringBuilder();
                if (null != bpmnIdArr) {
                    ids.append(bpmnId).append(";");
                    for (String id : bpmnIdArr) {
                        ids.append(id).append(";");
                    }
                }
                serialNumber.setBpmnIds(bpmnIds.toString());
                updateById(serialNumber);
            }
        }
    }
  }

  @Override
  @Transactional
  public void unBindBpmn(String bpmnId) {
    Where<SysSerialNumber> where = new Where<SysSerialNumber>();
    where.like("bpmn_ids", bpmnId);
    List<SysSerialNumber> selectList = selectList(where);
    //List<SysSerialNumber> serialNumbers = new ArrayList<SysSerialNumber>();
    /*for (SysSerialNumber serialNumber : selectList) {
        String bpmnIds = serialNumber.getBpmnIds();
        if (StringUtils.isNotEmpty(bpmnIds)) {
            String[] bpmnIdArr = bpmnIds.split(";");
            for (String id : bpmnIdArr) {
                if (id.equals(bpmnId)) {
                    serialNumbers.add(serialNumber);
                }
            }
        }
    }*/
   /* for (SysSerialNumber serialNumber : serialNumbers) {
        String bpmnIds = serialNumber.getBpmnIds();
        if (StringUtils.isNotEmpty(bpmnIds)) {
            String[] bpmnIdArr = bpmnIds.split(";");
            if (bpmnIdArr.length == 1) {
                serialNumber.setBpmnIds("");
            } else {
                bpmnIds = bpmnIds.replace(bpmnId+";", "");
                serialNumber.setBpmnIds(bpmnIds);
            }
        }
    }*/
    for (SysSerialNumber serialNumber : selectList) {
        String bpmnIds = serialNumber.getBpmnIds();
        if (StringUtils.isNotEmpty(bpmnIds)) {
            String[] bpmnIdArr = bpmnIds.split(";");
            if (bpmnIdArr.length == 1) {
                serialNumber.setBpmnIds("");
            } else {
                bpmnIds = bpmnIds.replace(bpmnId+";", "");
                serialNumber.setBpmnIds(bpmnIds);
            }
        }
    }
    if (null != selectList && selectList.size() > 0) {
        updateBatchById(selectList);
    }
  }

  @Override
  public List<SysSerialNumber> getList(SysSerialNumber obj) {
      ArrayList<SysSerialNumber> serialList = new ArrayList<SysSerialNumber>();
      SysSerialNumber sysSerialNumber = new SysSerialNumber();
      sysSerialNumber.setSerialNumName("全选");
      Where<SysSerialNumber> where = new Where<SysSerialNumber>();
      where.setSqlSelect("id,serial_num_name");
      where.eq("category_id", obj.getCategoryId());
      where.eq("status", 1);
      where.orderBy("create_time", false);
      List<SysSerialNumber> list = selectList(where);
      serialList.add(sysSerialNumber);
      serialList.addAll(list);
      return serialList;
  }

  @Override
  public RetMsg getPreviewSerialNumber(SysSerialNumber obj) {
      RetMsg retMsg = new RetMsg();
      String serialNum = null;
      if (null != obj && null != obj.getId()) {
          SysSerialNumber orgnlObj = selectById(obj.getId());
          orgnlObj.setCurrentValue(orgnlObj.getStartValue());
          serialNum = getSerialNum(orgnlObj);
      }
      retMsg.setCode(0);
      retMsg.setMessage("操作成功");
      retMsg.setObject(serialNum);
      return retMsg;
  }
  
}
