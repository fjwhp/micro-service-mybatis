package aljoin.aut.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import aljoin.aut.dao.object.AutMenuVO;

/**
 * 
 * 自定义security用户对象
 *
 * @author：zhongjy
 *
 * @date：2017年5月21日 下午12:06:32
 */
public class CustomUser extends User {

    private Long userId;
    private String nickName;
    private List<AutMenuVO> menuList;
    private String deptNames;
    private String deptIds;
    private String userIcon;
    

    /**
     * 
     */
    private static final long serialVersionUID = -2117593108771034827L;

    public CustomUser(String username, String password, boolean enabled, boolean accountNonExpired,
        boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public List<AutMenuVO> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<AutMenuVO> menuList) {
        this.menuList = menuList;
    }

    public String getDeptNames() {
        return deptNames;
    }

    public void setDeptNames(String deptNames) {
        this.deptNames = deptNames;
    }

    public String getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String deptIds) {
        this.deptIds = deptIds;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

}
