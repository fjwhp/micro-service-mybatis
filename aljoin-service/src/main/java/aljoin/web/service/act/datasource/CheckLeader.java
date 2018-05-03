package aljoin.web.service.act.datasource;

import java.util.List;
import java.util.Map;

import aljoin.object.WebConstant;

/**
 * 
 * 领导人 , 数据源设置
 *
 * @author：wuhp
 * 
 * @date：2017年11月28日 上午11:27:25
 */
public class CheckLeader extends BaseFormSoure {

  private DataSourceUtils dataSourceUtils;

  @Override
  public List<Object> execute(Map<String, String> paramMap) throws Exception {
    dataSourceUtils=new DataSourceUtils();
    return dataSourceUtils.packageDataSource(WebConstant.DICT_READ_OBJECT_LEADER);
  }
}
