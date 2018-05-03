package aljoin.sms.iservice;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sms.dao.entity.SmsShortMessage;

/**
 * 
 * 短信表(服务类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-10
 */
public interface SmsShortMessageService extends IService<SmsShortMessage> {

    /**
     * 
     * 短信表(分页列表).
     *
     * @return：Page<SmsShortMessage>
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    public Page<SmsShortMessage> list(PageBean pageBean, SmsShortMessage obj, Long customUserId, String time1,
        String time2) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017-10-10
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
     * @date：2017-10-10
     */
    public void copyObject(SmsShortMessage obj) throws Exception;

    /**
     * 
     * @param user 发送短信
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年10月17日 上午10:11:36
     */
    public RetMsg add(SmsShortMessage obj, AutUser user) throws Exception;

    /**
     * 
     * 手机短信-已发短信-删除（批量）
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年10月26日 下午3:51:28
     */
    public RetMsg deleteShortMessageList(String ids) throws Exception;

    /**
     * 
     * 手机短信 发送数据接口
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年10月26日 下午3:51:28
     */
    public List<Map<String, String>> sendSms() throws Exception;

    /**
     * 
     * 短信详情
     *
     * @return：SmsShortMessage
     *
     * @author：xuc
     *
     * @date：2017年12月20日 上午10:26:27
     */
    public SmsShortMessage detail(Long id) throws Exception;
    /**
     * 
     * 发送
     *
     * @return：SmsShortMessage
     *
     * @author：xuc
     *
     * @date：2017年12月20日 上午10:26:27
     */
    public RetMsg sendSms(SmsShortMessage obj, String userId) throws Exception;

}
