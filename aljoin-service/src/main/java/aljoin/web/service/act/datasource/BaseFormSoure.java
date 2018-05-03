package aljoin.web.service.act.datasource;

import java.util.List;
import java.util.Map;

public abstract class BaseFormSoure {
	/**
	 * 
	 * 获取数据源
	 *
	 * @return：List<Object>
	 *
	 * @author：pengsp
	 *
	 * @date：2017-08-31
	 */
	public abstract List<Object> execute(Map<String, String> paramMap) throws Exception;
}
