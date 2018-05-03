package aljoin.web.service.act.datasource;

import java.util.List;
import java.util.Map;

import aljoin.object.WebConstant;

/**
 * 
 * 标签接口数据源
 *
 * @author：pengsp
 * 
 * @date：2017年7月25日
 */
public class LabelName extends BaseFormSoure {

  @Override
  public List<Object> execute(Map<String, String> paramMap) throws Exception {
    DataSourceUtils dataSourceUtils = new DataSourceUtils();
    return dataSourceUtils.packageLabelDataSource(WebConstant.DATA_SOURCE_LABELNAME);
  }
}
