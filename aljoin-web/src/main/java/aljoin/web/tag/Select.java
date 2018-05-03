package aljoin.web.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import aljoin.dao.config.Where;
import aljoin.sys.dao.entity.SysDataDict;
import aljoin.sys.iservice.SysDataDictService;
import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * 自定义freemarker的select标签(根据code自动获取数据字典的值).
 *
 * @author：zhongjy
 * 
 * @date：2017年5月24日 下午5:17:42
 */
public class Select implements TemplateDirectiveModel {

	private SysDataDictService sysDataDictService;

	public Select(SysDataDictService sysDataDictService) {
		this.sysDataDictService = sysDataDictService;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment environment, Map map, TemplateModel[] model, TemplateDirectiveBody body) throws TemplateException, IOException {
		Writer out = environment.getOut();
		// 获取标签属性
		Object dictObj = map.get("dict");
		Object idObj = map.get("id");
		Object nameObj = map.get("name");
		Object defObj = map.get("def");
		Object sclassObj = map.get("class");
		Object layVerifyObject = map.get("layVerify");

		String dict = dictObj == null ? null : ((SimpleScalar) dictObj).getAsString();
		String id = idObj == null ? null : ((SimpleScalar) idObj).getAsString();
		String name = nameObj == null ? null : ((SimpleScalar) nameObj).getAsString();
		String def = defObj == null ? null : ((SimpleScalar) defObj).getAsString();
		String sclass = sclassObj == null ? null : ((SimpleScalar) sclassObj).getAsString();
		String layVerify = layVerifyObject == null ? null : ((SimpleScalar) layVerifyObject).getAsString();

		// 构造页面需要显示的元素
		StringBuffer select = new StringBuffer();
		select.append("	<div class=\""+sclass+"\">");
		select.append("<select ");
		if (name != null) {
			select.append("name=\"" + name + "\" ");
		}
		if (name != null) {
			select.append("id=\"" + id + "\" lay-filter=\"" + id + "\" ");
		}
		if(layVerify != null){
			select.append("lay-verify=\""+layVerify+"\" ");
		}
		select.append(">");
		select.append("	 		<option value=\" \"></option>");
		// 根据dictCode获取数据
		Where<SysDataDict> where = new Where<SysDataDict>();
		where.eq("is_active", 1);
		where.eq("dict_code", dict);
		where.setSqlSelect("dict_key,dict_value");
		List<SysDataDict> dictList = sysDataDictService.selectList(where);
		for (SysDataDict sysDataDict : dictList) {
			if (sysDataDict.getDictKey().equals(def)) {
				select.append("	 		<option value=\"" + sysDataDict.getDictKey() + "\" selected=\"selected\">" + sysDataDict.getDictValue()
						+ "</option>");
			} else {
				select.append("	 		<option value=\"" + sysDataDict.getDictKey() + "\">" + sysDataDict.getDictValue() + "</option>");
			}
		}
		select.append(" 	</select>");
		select.append("	</div>");
		out.write(select.toString());
	}

}
