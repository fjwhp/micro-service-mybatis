package aljoin.aut.dao.object;

import java.io.Serializable;

/**
 * 
 * 用户表(实体类).
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:02:31
 */
public class AppAutUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 账号(唯一)
     */
    private String userName;

    /**
     * 昵称(显示名称)
     */
    private String fullName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
