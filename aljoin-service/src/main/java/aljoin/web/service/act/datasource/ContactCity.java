package aljoin.web.service.act.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import aljoin.act.dao.source.ContactSelectSource;

/**
 * 
 * Contact数据源(接口)
 *
 * @author：pengsp
 * 
 * @date：2017年7月25日
 */
public class ContactCity extends BaseFormSoure {
	@Override
	public List<Object> execute(Map<String, String> paramMap) throws Exception {
		List<Object> contactSelect = new ArrayList<Object>();

		ContactSelectSource source = new ContactSelectSource();
		source.setId("1");
		source.setText("fujian");
		source.setLevel("1");
		source.setParent("");
		source.setCheck(false);

		ContactSelectSource source2 = new ContactSelectSource();
		source2.setId("2");
		source2.setText("xiamen");
		source2.setLevel("2");
		source2.setParent("1");
		source2.setCheck(false);

		contactSelect.add(source);
		contactSelect.add(source2);

		return contactSelect;
	}
}
