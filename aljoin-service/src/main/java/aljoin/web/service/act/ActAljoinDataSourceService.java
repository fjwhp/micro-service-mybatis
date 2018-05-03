package aljoin.web.service.act;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import aljoin.web.service.act.datasource.BaseFormSoure;

/**
 * 
 * 表单数据源服务类
 * 
 * @author：zhongjy
 * 
 * @date： 2017-08-31
 */
@Service
public class ActAljoinDataSourceService {

  @SuppressWarnings("unchecked")
  public List<Object> getAllSource(String type, Map<String, String> paramMap) throws Exception {
    String className = "aljoin.web.service.act.datasource." + type;
    Class<BaseFormSoure> cls = (Class<BaseFormSoure>) Class.forName(className);
    // 生成实例，调用默认无参构造函数O
    BaseFormSoure formSource = cls.newInstance();
    return formSource.execute(paramMap);
  }
}
