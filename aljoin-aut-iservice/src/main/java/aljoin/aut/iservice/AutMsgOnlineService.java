package aljoin.aut.iservice;

import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutDataStatistics;
import aljoin.aut.dao.entity.AutMsgOnline;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 在线消息表(服务类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-12
 */
public interface AutMsgOnlineService extends IService<AutMsgOnline> {

    /**
     * 
     * 在线消息表(分页列表).
     *
     * @return：Page<AutMsgOnline>
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    public Page<AutMsgOnline> list(PageBean pageBean, AutMsgOnline obj, Long customUserId) throws Exception;

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
    public void copyObject(AutMsgOnline obj) throws Exception;

    /**
     * 
     * 页面跳转
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017年10月12日 下午2:17:44
     */
    public void goToPage(HttpServletResponse response, AutMsgOnline obj) throws Exception;

    /**
     * 
     * 个人中心-在线消息-批量删除
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年10月25日 下午3:21:27
     */
    public RetMsg deleteMsgList(String autMsgOnlineId, Long userId) throws Exception;

    /**
     * 
     * 
     *
     * @return：RetMsg
     *
     * @author：wuhp
     *
     * @date：2017年10月28日 下午3:06:11
     */
    public RetMsg pushMessageToUserList(AutMsgOnlineRequestVO msgRequest) throws Exception;

    /**
     * 
     * 检查（某个模块-如邮件）对应的消息是否已读
     *
     * @return：RetMsg
     *
     * @author：sln
     *
     * @date：2017年11月24日 下午3:06:11
     */
    public void updateIsRead(AutMsgOnline obj) throws Exception;

    /**
     * 
     * @param long1
     * @return 消息从未读到已读，修改状态
     *
     * @return：RetMsg
     *
     * @author：sln
     *
     * @date：2017年11月24日 下午3:06:11
     */
    public RetMsg minus(AutDataStatistics obj, String msgId, Long long1) throws Exception;

}
