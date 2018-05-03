package aljoin.pub.iservice;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.aut.dao.entity.AutUser;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.pub.dao.entity.PubPublicInfo;
import aljoin.pub.dao.object.AppPubPublicInfo;
import aljoin.pub.dao.object.AppPubPublicInfoDO;
import aljoin.pub.dao.object.AppPubPublicInfoVO;
import aljoin.pub.dao.object.PubPublicInfoDO;
import aljoin.pub.dao.object.PubPublicInfoVO;

/**
 * 
 * 公共信息表(服务类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-12
 */
public interface PubPublicInfoService extends IService<PubPublicInfo> {

    /**
     * 
     * 公共信息表(分页列表).
     *
     * @return：Page<PubPublicInfoDO>
     *
     * @author：wangj
     *
     * @date：2017-10-16
     */
    public Page<PubPublicInfoDO> list(PageBean pageBean, PubPublicInfoVO obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    public void copyObject(PubPublicInfo obj) throws Exception;

    /**
     *
     * 公共信息详情
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-10-16
     */
    public PubPublicInfoVO detail(PubPublicInfo obj) throws Exception;

    /**
     *
     * 公共信息编辑
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-10-16
     */
    public RetMsg update(PubPublicInfoVO obj) throws Exception;

    /**
     *
     * 公共信息删除
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-10-16
     */
    public RetMsg delete(PubPublicInfoVO obj) throws Exception;

    /**
     *
     * 最新公共信息分页列表.
     *
     * @return：Page<PubPublicInfoDO>
     *
     * @author：wangj
     *
     * @date：2017-10-16
     */
    public Page<PubPublicInfoDO> lastList(PageBean pageBean, PubPublicInfoVO obj) throws Exception;

    /**
     *
     * 综合公共信息分页列表.
     *
     * @return：Page<PubPublicInfoDO>
     *
     * @author：wangj
     *
     * @date：2017-10-16
     */
    public Page<PubPublicInfoDO> multipleList(PageBean pageBean, PubPublicInfoVO obj) throws Exception;

    /**
     *
     * 公共信息提交流程.
     *
     * @return：ActAljoinBpmn
     *
     * @author：wangj
     *
     * @date：2017-10-16
     */
    public ActAljoinBpmn submitProcess() throws Exception;

    /**
     *
     * 根据流程分类ID获取对应的流程列表.
     *
     * @return：ActAljoinBpmn
     *
     * @author：wangj
     *
     * @date：2017-10-20
     */
    public List<ActAljoinBpmn> getBpmnByCategroyId(ActAljoinCategory obj);


    /**
     *
     * 公共信息审批页面跳转.
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-10-23
     */
    public void process(HttpServletRequest request, String bizId, String bizType) throws Exception;

    /**
     *
     * 查询前面所有过期的记录 修改为已过期 1
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017年11月3日
     */
    public void autoUpdateStatus() throws Exception;

    /**
     *
     * 检查当前节点的下一个节点办理人是 个人、多个人、部门、岗位
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-06
     */
    public RetMsg checkNextTaskInfo(String taskId) throws Exception;

    /**
     *
     * 获取所有节点信息
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-06
     */
    public List<FixedFormProcessLog> getAllTaskInfo(String taskId, String processInstanceId) throws Exception;

    /**
     *
     * 流程审批.
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-09
     */
    // public RetMsg completeTask(Map<String, Object> variables, String taskId, String bizId, String userId, String
    // message,
    // AutUser createUser) throws Exception;

    /**
     *
     * 回退到上一个节点.
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-09
     */
    // public RetMsg jump2Task2(String taskId, String bizId) throws Exception;
    // public RetMsg jump2Task2(String taskId, String bizId, String message,Long createUserId) throws Exception;

    /**
     *
     * 最新公共信息分页列表（APP）.
     *
     * @return：Page<AppPubPublicInfoDO>
     *
     * @author：wangj
     *
     * @date：2017-11-09
     */
    public Page<AppPubPublicInfoDO> lastList(PageBean pageBean, AppPubPublicInfoVO obj) throws Exception;

    /**
     *
     * 最新公共信息不分页列表（APP）.
     *
     * @return：List<AppPubPublicInfoDO>
     *
     * @author：wangj
     *
     * @date：2017-11-09
     */
    public RetMsg lastList(AppPubPublicInfoVO obj) throws Exception;

    /**
     *
     * 公共信息详情(APP)
     *
     * @return：AppPubPublicInfoDetailDO
     *
     * @author：wangj
     *
     * @date：2017-11-09
     */
    public RetMsg getDetailById(PubPublicInfo obj) throws Exception;

    /**
     *
     * 公共信息监管(15下午新增-真想把阙工打死)
     *
     * @return：Page<PubPublicInfoDO>
     *
     * @author：sln
     *
     * @date：2017-11-15
     */
    public Page<PubPublicInfoDO> manageList(PageBean pageBean, PubPublicInfoVO obj) throws Exception;

    /**
     *
     * 流程作废
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-20
     */
    public RetMsg toVoid(String taskId, String bizId, Long userId) throws Exception;

    /**
     *
     * 获取上个节点信息
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-20
     */
    public RetMsg getPreTaskInfo(String taskId) throws Exception;

    /**
     *
     * @throws Exception 最新信息（包含已读/未读信息）
     *
     * @return：RetMsg
     *
     * @author：sln
     *
     * @date：2017-11-20
     */
    public Page<PubPublicInfoDO> lastListRead(PageBean pageBean, PubPublicInfoVO obj) throws ParseException, Exception;

    public List<FixedFormProcessLog> getAllPinsInfo(String taskId) throws Exception;

    /**
     *
     * @param user （用户读取详情）异步处理已读未读信息（包含已读/未读信息）
     *
     * @return：RetMsg
     *
     * @author：sln
     *
     * @date：2017-11-20
     */
    public void isReadPub(PubPublicInfo pubPublicInfo, AutUser user) throws Exception;

    /**
     *
     * App公共信息详情
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-12-21
     */
    public RetMsg pubDetail(AppPubPublicInfo obj) throws Exception;

    /**
     *
     * app检查当前节点的下一个节点办理人是 个人、多个人、部门、岗位
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-06
     */
    public RetMsg checkAppNextTaskInfo(String taskId) throws Exception;

    /**
     *
     * app流程审批.
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-09
     */
    // public RetMsg completeAppTask(Map<String, Object> variables, String taskId, String bizId, String userId, String
    // message,
    // AutUser createUser) throws Exception;

    /**
     *
     * App回退到上一个节点.
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-09
     */
    // public RetMsg jump2Task2(String taskId, String bizId) throws Exception;
    // public RetMsg jump2AppTask2(String taskId, String message,Long createUserId) throws Exception;
}
