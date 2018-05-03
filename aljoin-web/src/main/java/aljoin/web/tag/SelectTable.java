package aljoin.web.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import aljoin.dao.config.DB;
import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * 自定义freemarker的selectTable标签(根据表名，value,option后去数据库对应值).
 *
 * @author：zhongjy
 * 
 * @date：2017年5月24日 下午5:17:42
 */
public class SelectTable implements TemplateDirectiveModel {

	private DataSource dataSource;

	public SelectTable(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment environment, Map map, TemplateModel[] model, TemplateDirectiveBody body) throws TemplateException, IOException {
		Writer out = environment.getOut();
		// 获取标签属性
		Object tableObj = map.get("table");
		Object keyObj = map.get("key");
		Object textObj = map.get("text");
		Object idObj = map.get("id");
		Object nameObj = map.get("name");
		Object defObj = map.get("def");
		Object sclassObj = map.get("class");
		Object layVerifyObject = map.get("layVerify");
		Object whereObj = map.get("where");
		Object isSearchObj = map.get("isSearch");

		String table = tableObj == null ? null : ((SimpleScalar) tableObj).getAsString();
		String key = keyObj == null ? null : ((SimpleScalar) keyObj).getAsString();
		String text = textObj == null ? null : ((SimpleScalar) textObj).getAsString();
		String id = idObj == null ? null : ((SimpleScalar) idObj).getAsString();
		String name = nameObj == null ? null : ((SimpleScalar) nameObj).getAsString();
		String def = defObj == null ? null : ((SimpleScalar) defObj).getAsString();
		String sclass = sclassObj == null ? null : ((SimpleScalar) sclassObj).getAsString();
		String layVerify = layVerifyObject == null ? null : ((SimpleScalar) layVerifyObject).getAsString();
		String where = whereObj == null ? null : ((SimpleScalar) whereObj).getAsString();
		String isSearch = isSearchObj == null ? null : ((SimpleScalar) isSearchObj).getAsString();

		// 构造页面需要显示的元素
		StringBuffer select = new StringBuffer();
		select.append("	<div class=\"" + sclass + "\">");
		select.append("<select ");
		if (name != null) {
			select.append("name=\"" + name + "\" ");
		}
		if (name != null) {
			select.append("id=\"" + id + "\" lay-filter=\"" + id + "\" ");
		}
		if (layVerify != null) {
			select.append("lay-verify=\"" + layVerify + "\" ");
		}
		if (isSearch != null) {
			select.append("lay-search ");
		}
		select.append(">");
		if (isSearch != null) {
			select.append("	 		<option value=\"\">搜索</option>");
		}else{
			select.append("	 		<option value=\" \"></option>");
		}
		// 构造sql获取数据
		String sql = "SELECT " + key + "," + text + " FROM " + table + " WHERE is_delete = 0 " + where;
		List<Map<String, String>> list = DB.queryForList(sql, dataSource);
		for (Map<String, String> m : list) {
			if (m.get(key).equals(def)) {
				select.append("	 		<option value=\"" + m.get(key) + "\" selected=\"selected\">" + m.get(text) + "</option>");
			} else {
				select.append("	 		<option value=\"" + m.get(key) + "\">" + m.get(text) + "</option>");
			}
		}
		select.append(" 	</select>");
		select.append("	</div>");
		out.write(select.toString());
	}

}
