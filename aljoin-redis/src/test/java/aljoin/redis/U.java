package aljoin.redis;

import java.io.Serializable;

public class U implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5377788370348038623L;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
