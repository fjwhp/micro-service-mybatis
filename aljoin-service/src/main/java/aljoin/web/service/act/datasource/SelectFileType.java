package aljoin.web.service.act.datasource;

import java.util.List;
import java.util.Map;

import aljoin.object.WebConstant;

/**
 * 
 * 文件类型设置
 *
 * @author：wuhp
 * 
 * @date：2017年11月28日 下午1:18:27
 */
public class SelectFileType extends BaseFormSoure {
  // 行政收文、党政文件、市长专线、信访、会议通知、其他
  @Override
  public List<Object> execute(Map<String, String> paramMap) throws Exception {
    DataSourceUtils dataSourceUtils = new DataSourceUtils();
    return dataSourceUtils.packageDataSource(WebConstant.DATA_SOURCE_SELECT_FILE_TYPE);
  }
}
