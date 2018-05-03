package aljoin.web.service.act.datasource;

import java.util.List;
import java.util.Map;

import aljoin.object.WebConstant;

/**
 * 
 * Select数据源(接口) 公开登记
 *
 * @author：pengsp
 * 
 * @date：2017年7月25日
 */
public class SelectOpenType extends BaseFormSoure {
  // 此件主动公开、此件依申请公开、此件不公开
  @Override
  public List<Object> execute(Map<String, String> paramMap) throws Exception {
    DataSourceUtils dataSourceUtils = new DataSourceUtils();
    return dataSourceUtils.packageDataSource(WebConstant.DATA_SOURCE_SELECT_OPEN_TYPE);
  }
}
