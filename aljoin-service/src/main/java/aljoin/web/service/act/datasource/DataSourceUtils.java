package aljoin.web.service.act.datasource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;

import aljoin.act.dao.source.CheckBoxSource;
import aljoin.sys.dao.entity.SysDataDict;
import aljoin.sys.iservice.SysDataDictService;
import aljoin.util.SpringContextUtil;

@Service
public class DataSourceUtils {

  public List<Object> packageDataSource(String dataType) throws Exception {
    List<Object> list = new ArrayList<Object>();
    SysDataDictService sysDataDictService =
        (SysDataDictService) SpringContextUtil.getBean("sysDataDictServiceImpl");
    List<SysDataDict> datas = sysDataDictService.getByCode(dataType);
    Collections.sort(datas, new Comparator<SysDataDict>() {


      @Override
      public int compare(SysDataDict data1, SysDataDict data2) {
        int i = 0;
        if(null != data1.getDictRank() && null != data2.getDictRank()){
          i = data1.getDictRank()-data2.getDictRank();
        }
        return i;
      }
    });

    if (CollectionUtils.isNotEmpty(datas)) {
      for (SysDataDict sysDataDict : datas) {
        CheckBoxSource source = new CheckBoxSource();
        source.setText(sysDataDict.getDictValue());
        source.setValue(sysDataDict.getDictKey());
        list.add(source);
      }
    } else {
      throw new Exception("[系统管理]-->[数据字典]-->字典编码为" + dataType + "不能为空,请联系管理员");
    }
    return list;
  }

  /**
   * 
   * 封装 获取单个数据字典值 取 字典内容
   *
   * @return：List<Object>
   *
   * @author：wuhp
   *
   * @date：2017年11月28日 下午7:17:20
   */
  public List<Object> packageLabelDataSource(String dataType) throws Exception {
    List<Object> list = new ArrayList<Object>();
    SysDataDictService sysDataDictService =
        (SysDataDictService) SpringContextUtil.getBean("sysDataDictServiceImpl");
    List<SysDataDict> datas = sysDataDictService.getByCode(dataType);
    if (CollectionUtils.isNotEmpty(datas)) {
      CheckBoxSource source = new CheckBoxSource();
      source.setValue(datas.get(0).getDictValue());
      list.add(source);
    } else {
      throw new Exception("[系统管理]-->[数据字典]-->字典编码为" + dataType + "不能为空,请联系管理员");
    }
    return list;

  }



}
