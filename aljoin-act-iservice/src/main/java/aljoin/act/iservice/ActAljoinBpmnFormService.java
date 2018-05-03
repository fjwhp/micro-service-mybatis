package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinBpmnForm;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 流程元素-表单关系(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-15
 */
public interface ActAljoinBpmnFormService extends IService<ActAljoinBpmnForm> {

    /**
     * 
     * 流程元素-表单关系(分页列表).
     *
     * @return：Page<ActAljoinBpmnForm>
     *
     * @author：zhongjy
     *
     * @date：2017-09-15
     */
    public Page<ActAljoinBpmnForm> list(PageBean pageBean, ActAljoinBpmnForm obj) throws Exception;
}
