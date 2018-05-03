package aljoin.act.iservice;

import aljoin.act.dao.entity.ActRuExecution;
import aljoin.object.PageBean;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

/**
 * 执行流对象（服务类）
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public interface ActRuExecutionService extends IService<ActRuExecution> {

    /**
     * 分页列表
     *
     * @param pageBean
     *
     * @param obj
     *
     * @return
     *
     * @throws Exception
     */
    public Page<ActRuExecution> list(PageBean pageBean, ActRuExecution obj) throws Exception;

    /**
     * 根据ID删除对象(物理删除)
     *
     * @param id
     *
     * @throws Exception
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 根据ID删除对象(物理删除)
     *
     * @param obj
     *
     * @throws Exception
     */
    public void copyObject(ActRuExecution obj) throws Exception;
}
