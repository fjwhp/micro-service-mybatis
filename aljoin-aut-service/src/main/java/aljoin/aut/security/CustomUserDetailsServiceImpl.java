package aljoin.aut.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import aljoin.aut.dao.object.AutRoleVO;
import aljoin.aut.dao.object.AutUserVO;

/**
 * 
 * 自定义security的用户服务
 *
 * @author：zhongjy
 *
 * @date：2017年5月3日 下午5:15:49
 */
@Service
public class CustomUserDetailsServiceImpl
    implements UserDetailsService, AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    @Autowired
    private UserService userService;

    /**
     * 本系统登录接口
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(userName)) {
            throw new UsernameNotFoundException("账号不能为空");
        }
        AutUserVO user = userService.getUserByUserName(userName);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        CustomUser userDetail = new CustomUser(user.getUserName(), user.getUserPwd(), user.getIsActive() == 1,
            user.getIsAccountExpired() == 0, user.getIsCredentialsExpired() == 0, user.getIsAccountLocked() == 0,
            getGrantedAuthorities(user));
        userDetail.setUserId(user.getId());
        userDetail.setNickName(user.getFullName());
        userDetail.setMenuList(user.getMenuList());
        userDetail.setDeptIds(user.getAutDeptIds());
        userDetail.setDeptNames(user.getAutDeptNames());
        userDetail.setUserIcon(user.getUserIcon());
        return userDetail;
    }

    /**
     * 获取用户权限
     * 
     * @param user
     * @return
     */
    private List<GrantedAuthority> getGrantedAuthorities(AutUserVO user) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (AutRoleVO role : user.getRoleList()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()));
        }
        return authorities;
    }

    /**
     * 单点登录接口
     */
    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
        String userName = token.getName();
        if (StringUtils.isEmpty(userName)) {
            throw new UsernameNotFoundException("账号不能为空");
        }
        AutUserVO user = userService.getUserByUserName(userName);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        CustomUser userDetail = new CustomUser(user.getUserName(), user.getUserPwd(), user.getIsActive() == 1,
            user.getIsAccountExpired() == 0, user.getIsCredentialsExpired() == 0, user.getIsAccountLocked() == 0,
            getGrantedAuthorities(user));
        userDetail.setUserId(user.getId());
        userDetail.setNickName(user.getFullName());
        userDetail.setMenuList(user.getMenuList());
        return userDetail;
    }
}
