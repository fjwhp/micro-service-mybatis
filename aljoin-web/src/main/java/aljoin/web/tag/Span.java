package aljoin.web.tag;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import aljoin.aut.dao.entity.AutWidgetRole;
import aljoin.aut.iservice.AutWidgetRoleService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
/**
 *
 * 自定义freemarker的button标签(加入权限控制).
 *
 * @author：wangj
 *
 * @date：2017年08月30日
 */
public class Span implements TemplateDirectiveModel {
	private AutWidgetRoleService autWidgetRoleService;
	public Span(AutWidgetRoleService autWidgetRoleService) {
		this.autWidgetRoleService = autWidgetRoleService;
	}
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment environment, Map map, TemplateModel[] model, TemplateDirectiveBody body) throws TemplateException, IOException {
		Writer out = environment.getOut();
		// 获取标签属性
		Object codeObj = map.get("code");
		Object titleObj = map.get("title");
		Object aclassObj = map.get("class");
		Object idObj = map.get("id");
		Object onclickObj = map.get("onclick");
		Object iclassObj = map.get("iclass");
		Object iconObj = map.get("icon");
		String code = codeObj == null ? null : ((SimpleScalar) codeObj).getAsString();
		String title = titleObj == null ? null : ((SimpleScalar) titleObj).getAsString();
		String aclass = aclassObj == null ? null : ((SimpleScalar) aclassObj).getAsString();
		String id = idObj == null ? null : ((SimpleScalar) idObj).getAsString();
		String onclick = onclickObj == null ? null : ((SimpleScalar) onclickObj).getAsString();
		String iclass = iclassObj == null ? null : ((SimpleScalar) iclassObj).getAsString();
		String icon = iconObj == null ? null : ((SimpleScalar) iconObj).getAsString();
		// 构造页面需要显示的元素
		StringBuffer button = new StringBuffer();
		button.append("<span title=\"" + title + "\" class=\"" + aclass + "\" id=\"" + id + "\" onclick=\"" + onclick + "\">");
		if (iclass != null) {
			button.append("<i class=\"" + iclass + "\">" + icon + "</i> ");
		}
		button.append("</span>");
		// 不需要权限控制
		if (code == null || "".equals(code)) {
			out.write(button.toString());
		} else {
			// 获取用户角色，然后判断该code有没有分配到用户已拥有的角色中
			List<String> roleCodeList = new ArrayList<String>();
			CustomUser userDetails = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Collection<GrantedAuthority> roleList = userDetails.getAuthorities();
			for (GrantedAuthority grantedAuthority : roleList) {
				roleCodeList.add(grantedAuthority.getAuthority().substring(5));
			}
			if (roleCodeList.size() > 0) {
				Where<AutWidgetRole> where = new Where<AutWidgetRole>();
				where.eq("is_active", 1);
				where.eq("widget_code", code);
				where.in("role_code", roleCodeList);
				if (autWidgetRoleService.selectCount(where) > 0) {
					out.write(button.toString());
				}
			}
		}
	}
}