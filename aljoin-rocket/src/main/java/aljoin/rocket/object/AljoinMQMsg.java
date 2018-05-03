package aljoin.rocket.object;

/**
 * 消息参数对象
 * 
 * @author zhongjy
 * @date 2018/03/23
 */
public class AljoinMQMsg {
    /**
     * 消息标签
     */
    private MQTag tags;
    /**
     * 业务主键
     */
    private String keys;

    /**
     * 业务主表
     */
    private String keysTable;
    /**
     * 标记（暂时无用）
     */
    private int flag;
    /**
     * 消息体
     */
    private String body;
    /**
     * 消息是否在服务器落盘后才返回应答只有同步刷盘的时候，这个配置才有效
     */
    private boolean waitStoreMsgOK;

    /**
     * 单队列关键字(标记需要放在同一个队列的消息，同一个队列能保证消息是按顺序的，本设置只顺序消息有效)
     */
    private String singleQueueKey;

    public MQTag getTags() {
        return tags;
    }

    public void setTags(MQTag tags) {
        this.tags = tags;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isWaitStoreMsgOK() {
        return waitStoreMsgOK;
    }

    public void setWaitStoreMsgOK(boolean waitStoreMsgOK) {
        this.waitStoreMsgOK = waitStoreMsgOK;
    }

    public String getKeysTable() {
        return keysTable;
    }

    public void setKeysTable(String keysTable) {
        this.keysTable = keysTable;
    }

    public String getSingleQueueKey() {
        return singleQueueKey;
    }

    public void setSingleQueueKey(String singleQueueKey) {
        this.singleQueueKey = singleQueueKey;
    }

}
