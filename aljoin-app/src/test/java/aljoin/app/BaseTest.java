package aljoin.app;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import aljoin.object.InterfaceObject;
import aljoin.object.InterfaceParam;

/**
 * 
 * 基础测试类
 *
 * @author：zhongjyy
 * 
 * @date：2017年8月7日 上午11:16:13
 */
public class BaseTest {
  /**
   * 
   * 生成接口文档
   *
   * @return：void
   *
   * @author：zhongjy
   *
   * @date：2017年8月7日 上午11:16:25
   */
  protected void makeItable(InterfaceObject obj) {
    System.out.println("###################构造table-beg###################");
    StringBuffer table = new StringBuffer();
    table.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
    table.append("<tr>");
    table.append("    <td style=\"font-weight: bold;\">接口名称</td>");
    table.append("    <td colspan=\"2\">" + obj.getInterfaceName() + "</td>");
    table.append("    <td style=\"font-weight: bold;\">接口版本</td>");
    table.append("    <td>" + obj.getInterfaceVersion() + "</td>");
    table.append("</tr>");
    table.append("<tr>");
    table.append("    <td style=\"font-weight: bold;\">接口地址</td>");
    table.append("    <td colspan=\"4\">" + obj.getInterfaceAddress() + "</td>");
    table.append("</tr>");
    table.append("<tr style=\"font-weight: bold;\">");
    table.append("    <td rowspan=\"" + (obj.getParamList().size() + 1)
        + "\">参数列表<br><font style=\"color:blue;\">(红色加密)</font></td>");
    table.append("    <td>参数</td><td>参数说明</td><td>参数类型</td><td>是否可空</td>");
    table.append("</tr>");
    for (InterfaceParam param : obj.getParamList()) {
      String isEncrypt = "加密".equals(param.getIsEncrypt()) ? "style=\"color:red;\"" : "";
      table.append("<tr " + isEncrypt + ">");
      table.append("    <td>" + param.getParamName() + "</td><td>" + param.getParamDesc()
          + "</td><td>" + param.getParamType() + "</td><td>" + param.getAllowNull() + "</td>");
      table.append("</tr>");
    }
    table.append("<tr style=\"font-weight: bold;\">");
    table.append("    <td rowspan=\"2\">返回结果</td>");
    if(obj.getRetObj() instanceof JSONObject){
    	JSONObject jo = (JSONObject) obj.getRetObj();
    	if(jo.get("records") == null){
    		table.append("    <td colspan=\"4\" id=\"json-str\">" + jo + "</td>");
    	}else{
    		table.append("    <td colspan=\"4\" id=\"json-str\">" + jo.get("records") + "</td>");
    	}
    }else if(obj.getRetObj() instanceof JSONArray){
    	JSONArray ja = (JSONArray) obj.getRetObj();
        table.append("    <td colspan=\"4\" id=\"json-str\">" + ja + "</td>");
    }
    table.append("</tr>");
    table.append("<tr>");
    table.append("    <td colspan=\"4\"><font style=\"color:blue\">加密字段：</font><font style=\"color:red\">" + obj.getRetEncryptProps() + "</font></td>");
    table.append("</tr>");
    table.append("</table>");
    System.out.println(table.toString());
    System.out.println("###################构造table-end###################");

  }
}
