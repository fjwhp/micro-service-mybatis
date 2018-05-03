package aljoin.act.dao.entity;

import com.baomidou.mybatisplus.annotations.TableField;

/**
 * 
 * (实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-12-12
 */
public class ActIdMembership {

    @TableField("USER_ID_")
    private String userId;
    @TableField("GROUP_ID_")
    private String groupId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
