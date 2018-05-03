package aljoin.aut.dao.object;

/**
 * 
 * 在线消息接收对象，必须的信息
 *
 * @author：wuhp
 * 
 * @date：2017年10月28日 下午2:45:36
 */
public class AutMsgOnlineReviceVo {

    private Long userId;
    private String userName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
