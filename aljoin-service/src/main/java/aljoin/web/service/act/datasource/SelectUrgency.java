package aljoin.web.service.act.datasource;

import java.util.List;
import java.util.Map;

import aljoin.object.WebConstant;

/**
 * 
 * Select数据源(接口) ---- 缓急程度
 *
 * @author：wuhp
 * 
 * @date：2017年11月28日 下午1:38:04
 */
public class SelectUrgency extends BaseFormSoure {
  // 一般、紧急、加急
  @Override
  public List<Object> execute(Map<String, String> paramMap) throws Exception {
    DataSourceUtils dataSourceUtils = new DataSourceUtils();
    return dataSourceUtils.packageDataSource(WebConstant.DICT_URGENT_LEVEL);

  }
}
