package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinBpmnForm;
import aljoin.act.dao.mapper.ActAljoinBpmnFormMapper;
import aljoin.act.iservice.ActAljoinBpmnFormService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 流程元素-表单关系(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-15
 */
@Service
public class ActAljoinBpmnFormServiceImpl extends ServiceImpl<ActAljoinBpmnFormMapper, ActAljoinBpmnForm>
    implements ActAljoinBpmnFormService {

    @Override
    public Page<ActAljoinBpmnForm> list(PageBean pageBean, ActAljoinBpmnForm obj) throws Exception {
        Where<ActAljoinBpmnForm> where = new Where<ActAljoinBpmnForm>();
        where.orderBy("create_time", false);
        Page<ActAljoinBpmnForm> page =
            selectPage(new Page<ActAljoinBpmnForm>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }
}
