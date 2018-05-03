package aljoin.act.iservice;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import aljoin.act.dao.object.ActSeetingExpressionVO;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.object.ActAljoinBpmnVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 自定义流程bpmn表(服务类 ).
 *
 * @author：zhongjy.
 * 
 *                  @date：2017年7月25日 下午4:00:15
 */
public interface ActAljoinBpmnService extends IService<ActAljoinBpmn> {

    /**
     * 
     * 自定义流程bpmn表(分页列表).
     *
     * @return：Page<ActAljoinBpmn>
     *
     * @author：zhongjy
     *
     * @date：2017-07-07
     */
    public Page<ActAljoinBpmnVO> list(PageBean pageBean, ActAljoinBpmnVO obj) throws Exception;

    /**
     * 
     * 流程部署
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年9月11日 下午4:41:05
     */
    public void deploy(Long id, Long userId, String userName) throws Exception;

    /**
     * 
     * 保存流程
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年9月15日 下午2:04:57
     */
    public RetMsg saveXmlCode(Map<String, String> param) throws Exception;

    /**
     * 
     * 修改流程
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年9月22日 上午10:28:25
     */
    public RetMsg updateXmlCode(Map<String, String> param) throws Exception;

    /**
     * 
     * 修改流程中的表单状态
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年9月29日 下午1:25:47
     */
    public void updateFormProcess(Long formId) throws Exception;

    /**
     * 
     * 导出xml代码
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年9月30日 下午3:05:23
     */
    public RetMsg exportXmlCode(String xmlCode, String processId, String processName, String taskIds,
        String assigneeDepartmentIds, String assigneePositionIds, String assigneeUserIds, String assigneeCandidateIds,
        String assigneeGroupIds) throws Exception;

    /**
     * 
     * 个人管理流程列表(分页列表).
     *
     * @return：Page<ActAljoinBpmn>
     *
     * @author：huangw
     *
     * @date：2017-11-10
     */
    public Page<ActAljoinBpmnVO> userBpmnlist(PageBean pageBean, String typeID, String usrID) throws Exception;

    /**
     * 
     * 流程元素-获取流程分类下的流程
     *
     * @return：List<ActAljoinBpmnForm>
     *
     * @author：zhongjy
     *
     * @date：2017-09-15
     */
    public List<ActAljoinBpmn> setCategoryBpmn(String cateGoryId) throws Exception;
    
    /**
     * 
     * 自定义流程bpmn表(根据ID删除对象).
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017-09-01
     */
    public RetMsg delete(ActAljoinBpmn obj);
    
    /**
     * 
     * 流程复制
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-04-10
     */
    public RetMsg copy(ActAljoinBpmn bpmn);
    
    /**
     * 
     * 流程导出
     * @throws Exception 
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-04-10
     */
    public void export(HttpServletResponse response, ActAljoinBpmnVO bpmn) throws Exception;
    
    /**
     * 
     * 流程导入
     * @throws Exception 
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-04-12
     */
    public RetMsg fileImport(MultipartHttpServletRequest request) throws Exception;
    
    /**
     * 
     * 流程导入文件提交
     * @throws Exception 
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018-04-12
     */
    public RetMsg fileSubmit(ActAljoinBpmnVO bpmn) throws Exception;

    /**
     * 生成条件表达式
     * @param targetKey         目标任务KEY
     * @param targetName        目标任务名称
     * @param selectType        选择类型（1.手动选择 2.自动选择）
     * @param expressionList    自动选择配置列表
     * @return
     * @throws Exception
     */
    public RetMsg genExpression(String targetKey,String targetName,Integer selectType,List<ActSeetingExpressionVO> expressionList) throws Exception;
}
