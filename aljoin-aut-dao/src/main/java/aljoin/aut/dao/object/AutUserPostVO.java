package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutUserPost;

/**
 * 
 * 用户岗位对象
 *
 * @author：caizx
 *
 * @date：2018年4月9日 下午18:39:24
 */
public final class AutUserPostVO extends AutUserPost {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户id(多个用;分隔)
     */
    private String userIds;

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }
    
}
