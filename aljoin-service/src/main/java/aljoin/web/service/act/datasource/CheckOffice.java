package aljoin.web.service.act.datasource;

import java.util.List;
import java.util.Map;

import aljoin.object.WebConstant;

/**
 * 
 * 办公室人员选择
 *
 * @author：wuhp
 * 
 * @date：2017年11月28日 上午11:40:22
 */
public class CheckOffice extends BaseFormSoure {

	@Override
	public List<Object> execute(Map<String, String> paramMap) throws Exception {
		DataSourceUtils dataSourceUtils = new DataSourceUtils();
		return dataSourceUtils.packageDataSource(WebConstant.DICT_READ_OBJECT_OFFICE);
	}
}
