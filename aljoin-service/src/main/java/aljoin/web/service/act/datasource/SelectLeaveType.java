package aljoin.web.service.act.datasource;

import java.util.List;
import java.util.Map;

import aljoin.object.WebConstant;

/**
 * 
 * Select数据源(接口)---请假类型
 *
 * @author：pengsp
 * 
 * @date：2017年7月25日
 */
public class SelectLeaveType extends BaseFormSoure {
  // 请假类型
  @Override
  public List<Object> execute(Map<String, String> paramMap) throws Exception {

    DataSourceUtils dataSourceUtils = new DataSourceUtils();
    return dataSourceUtils.packageDataSource(WebConstant.DICT_LEAVE_TYPE);
  }
}
