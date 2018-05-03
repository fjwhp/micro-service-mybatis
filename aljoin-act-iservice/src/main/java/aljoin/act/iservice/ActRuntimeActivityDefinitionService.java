package aljoin.act.iservice;

import java.util.List;


/**
 *
 * @author：wangj
 *
 * @date：2018年04月17日
 */
public interface ActRuntimeActivityDefinitionService {
    /**
     * 获取所有的活动定义信息，引擎会在启动的时候加载这些活动定义并进行注册
     * 
     * @return
     * @throws Exception
     */
    List<ActRuActivityDefinitionService> list() throws Exception;

    /**
     * 删除所有活动定义
     * 
     * @throws Exception
     */
    void removeAll() throws Exception;

    /**
     * 新增一条活动定义的信息
     * 
     * @param entity
     * @throws Exception
     */
    void save(ActRuActivityDefinitionService entity) throws Exception;
}
