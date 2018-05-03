 package aljoin.aut.dao.object;

import java.util.List;

import aljoin.aut.dao.entity.AutPost;
import aljoin.aut.dao.entity.AutUser;

public class AutPostVO extends AutPost{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    private AutPost autPost;
    
    private List<AutUser> userList;

    public AutPost getAutPost() {
        return autPost;
    }

    public void setAutPost(AutPost autPost) {
        this.autPost = autPost;
    }

    public List<AutUser> getUserList() {
        return userList;
    }

    public void setUserList(List<AutUser> userList) {
        this.userList = userList;
    }
    
}
