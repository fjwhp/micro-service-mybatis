package aljoin.web.service.act.datasource;

import java.util.List;
import java.util.Map;

import aljoin.object.WebConstant;

/**
 * 
 * Select数据源(接口) --- 来文单位
 *
 * @author：pengsp
 * 
 * @date：2017年7月25日
 */
public class SelectReferenceUnit extends BaseFormSoure {
  // 厦门市人民政府、厦门市海洋与渔业局
  @Override
  public List<Object> execute(Map<String, String> paramMap) throws Exception {
    DataSourceUtils dataSourceUtils = new DataSourceUtils();
    return dataSourceUtils.packageDataSource(WebConstant.DICT_FROM_UNIT);
  }
}
