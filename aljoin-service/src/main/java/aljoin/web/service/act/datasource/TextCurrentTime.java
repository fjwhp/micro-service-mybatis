package aljoin.web.service.act.datasource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import aljoin.act.dao.source.TextSource;

/**
 * 
 * text数据源(接口)
 *
 * @author：pengsp
 * 
 * @date：2017年7月25日
 */
public class TextCurrentTime extends BaseFormSoure{

	@Override
	public List<Object> execute(Map<String, String> paramMap) throws Exception {
		List<Object> text = new ArrayList<Object>();
		TextSource source =new TextSource();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String curdate=formatter.format(new Date());
		source.setValue(curdate);
		text.add(source);
		return text;
	}
}
