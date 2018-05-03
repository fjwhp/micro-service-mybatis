package aljoin.web.service.act.datasource;

import java.util.List;
import java.util.Map;

import aljoin.object.WebConstant;

/**
 * 
 * Select数据源(接口)
 *
 *                   @author：pengsp
 * 
 * @date：2017年7月25日
 */
public class SelectConfidentialityPeriod extends BaseFormSoure {
  // 一般、永久、长期、短期
  @Override
  public List<Object> execute(Map<String, String> paramMap) throws Exception {
    DataSourceUtils dataSourceUtils = new DataSourceUtils();
    return dataSourceUtils.packageDataSource(WebConstant.DATA_SOURCE_SELECT_PERIOD);

  }
}
