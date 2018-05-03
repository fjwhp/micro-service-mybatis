package aljoin.aut.dao.object;

import java.util.List;

/**
 * 
 * 调用 在线消息需要的请求对象
 *
 * @author：wuhp
 * 
 * @date：2017年10月28日 下午1:49:40
 */
public class AutMsgOnlineRequestVO {

    /**
     * 消息类型 ，取值WebConstant ---在线消息通知类型定义 ---在线消息类型
     */
    private String msgType;
    /**
     * 通知内容 (标题和内容，调用前分装好)
     */
    private String msgContent;
    /**
     * 消息来自账号id
     */
    private Long fromUserId;
    /**
     * 消息来自账号
     */
    private String fromUserName;
    /**
     * 消息来自账号姓名
     */
    private String fromUserFullName;
    /**
     * 接收消息的账号 list
     */
    private List<String> toUserIds;

    /**
     * 跳转连接
     */
    private String goUrl;
    private String businessKey;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public List<String> getToUserIds() {
        return toUserIds;
    }

    public void setToUserId(List<String> toUserIds) {
        this.toUserIds = toUserIds;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserFullName() {
        return fromUserFullName;
    }

    public void setFromUserFullName(String fromUserFullName) {
        this.fromUserFullName = fromUserFullName;
    }

    public String getGoUrl() {
        return goUrl;
    }

    public void setGoUrl(String goUrl) {
        this.goUrl = goUrl;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

}
