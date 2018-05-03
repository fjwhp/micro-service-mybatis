 package aljoin.web.service.act.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import aljoin.act.dao.source.TextSource;

/**
  * 
  * text数据源(接口)
  *
  * @author：caizx
  * 
  * @date：2018年4月02日
  */
public class TextUserPosition extends BaseFormSoure {
    @Override
    public List<Object> execute(Map<String, String> paramMap) throws Exception {
        List<Object> text = new ArrayList<Object>();
        TextSource source =new TextSource();
        source.setValue(paramMap.get("positionNames"));
        text.add(source);
        return text;
    }
}
