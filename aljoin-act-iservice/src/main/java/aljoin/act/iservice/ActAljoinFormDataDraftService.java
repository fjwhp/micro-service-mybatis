package aljoin.act.iservice;

import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.act.dao.entity.ActAljoinFormDataDraft;
import aljoin.act.dao.entity.ActAljoinFormDataRun;
import aljoin.act.dao.object.DraftTaskShowVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 表单数据表(草稿)(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-30
 */
public interface ActAljoinFormDataDraftService extends IService<ActAljoinFormDataDraft> {

    /**
     * 
     * 表单数据表(草稿)(分页列表).
     *
     * @return：Page<ActAljoinFormDataDraft>
     *
     * @author：zhongjy
     *
     * @date：2017-09-30
     */
    public Page<ActAljoinFormDataDraft> list(PageBean pageBean, ActAljoinFormDataDraft obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-09-30
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-09-30
     */
    public void copyObject(ActAljoinFormDataDraft obj) throws Exception;

    /**
     *
     * 存稿（首页数据维护-->方法放到web里去了）
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月13日 上午9:21:48
     */
    // public Map<String, String> saveDraft(ActAljoinFormDataDraft entity, Map<String, String> paramMap,
    // Map<String, String> param, Long userId, String saveDraft) throws Exception;

    /**
     * 
     * 提交
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月13日 上午10:26:29
     */
    public Map<String, Object> doCreateWork(ActAljoinFormDataRun entity, Map<String, String> paramMap,
        Map<String, String> param, Long userId, String fullName) throws Exception;

    /**
     * 
     * 通过
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月13日 上午10:26:29
     */
    public void doPass(ActAljoinFormDataRun entity, Map<String, String> paramMap) throws Exception;

    /**
     * 
     * 回退
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月13日 上午10:26:29
     */
    public void doBack(ActAljoinFormDataRun entity, Map<String, String> paramMap) throws Exception;

    /**
     * 
     * 下一步
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月13日 上午10:26:29
     */
    public RetMsg doNext(ActAljoinFormDataRun entity, Map<String, String> paramMap) throws Exception;

    /**
     * 
     * 关闭
     *
     * @return：void
     *
     * @author：pengsp
     *
     * @date：2017年10月13日 上午10:26:29
     */
    public void closeTask(String activityId) throws Exception;

    /**
     * 
     * 表单数据表(草稿)(分页列表).
     *
     * @return：Page<ActAljoinFormDataDraft>
     *
     * @author：huangw
     *
     * @date：2017-10-28
     */
    public Page<DraftTaskShowVO> listPage(PageBean pageBean, Map<String, Object> obj) throws Exception;

    /**
     * 
     * 根据ID删除对象
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017-10-28
     */
    public void deleteDraftById(String id) throws Exception;

    /**
     *
     * 保存
     *
     * @return：void
     *
     * @author：pengsp
     *
     * @date：2017-10-28
     */
    public void addDraft(ActAljoinFormDataDraft entity, Map<String, String> paramMap, Map<String, String> param,
        Long userId) throws Exception;

    /**
     * 
     * @param paramMap
     * @param instance
     * @param entity
     * @param isTask
     * @param nextNode
     * @throws Exception
     */
    public void updateFormData(Map<String, String> paramMap, ProcessInstance instance, ActAljoinFormDataRun entity,
        String isTask, String nextNode, String currentNode) throws Exception;

    /**
     *
     * app提交
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月13日 上午10:26:29
     */
    public Map<String, Object> doAppCreateWork(ActAljoinFormDataRun entity, Map<String, String> paramMap,
        Map<String, String> param, Long userId, String fullName) throws Exception;

}
