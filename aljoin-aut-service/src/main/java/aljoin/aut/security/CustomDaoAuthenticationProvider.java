package aljoin.aut.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

/**
 * 
 * 重写DaoAuthenticationProvider
 *
 * @author：zhongjy
 * 
 * @date：2017年12月21日 上午9:19:19
 */
@SuppressWarnings("deprecation")
public class CustomDaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

    private PasswordEncoder passwordEncoder;

    private String userNotFoundEncodedPassword;

    private SaltSource saltSource;

    private UserDetailsService userDetailsService;

    private String isUsePwdLogin;

    public String getIsUsePwdLogin() {
        return isUsePwdLogin;
    }

    public void setIsUsePwdLogin(String isUsePwdLogin) {
        this.isUsePwdLogin = isUsePwdLogin;
    }

    public CustomDaoAuthenticationProvider() {
        setPasswordEncoder(new PlaintextPasswordEncoder());
    }

    public CustomDaoAuthenticationProvider(String isUsePwdLogin) {
        setPasswordEncoder(new PlaintextPasswordEncoder());
        this.isUsePwdLogin = isUsePwdLogin;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
        UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        Object salt = null;

        if (this.saltSource != null) {
            salt = this.saltSource.getSalt(userDetails);
        }

        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(
                messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();

        if (!passwordEncoder.isPasswordValid(userDetails.getPassword(), presentedPassword, salt)) {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(
                messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    @Override
    protected void doAfterPropertiesSet() throws Exception {
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
    }

    @Override
    protected final UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {
        UserDetails loadedUser;

        try {
            loadedUser = this.getUserDetailsService().loadUserByUsername(username);
        } catch (UsernameNotFoundException notFound) {
            if (authentication.getCredentials() != null) {
                String presentedPassword = authentication.getCredentials().toString();
                passwordEncoder.isPasswordValid(userNotFoundEncodedPassword, presentedPassword, null);
            }
            throw notFound;
        } catch (Exception repositoryProblem) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
        }

        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException(
                "UserDetailsService returned null, which is an interface contract violation");
        }
        return loadedUser;
    }

    public void setPasswordEncoder(Object passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");

        if (passwordEncoder instanceof PasswordEncoder) {
            setPasswordEncoder((PasswordEncoder)passwordEncoder);
            return;
        }

        if (passwordEncoder instanceof org.springframework.security.crypto.password.PasswordEncoder) {
            final org.springframework.security.crypto.password.PasswordEncoder delegate =
                (org.springframework.security.crypto.password.PasswordEncoder)passwordEncoder;
            setPasswordEncoder(new PasswordEncoder() {
                @Override
                public String encodePassword(String rawPass, Object salt) {
                    checkSalt(salt);
                    return delegate.encode(rawPass);
                }

                @Override
                public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
                    if ("0".equals(getIsUsePwdLogin())) {
                        return true;
                    }
                    checkSalt(salt);
                    return delegate.matches(rawPass, encPass);
                }

                private void checkSalt(Object salt) {
                    Assert.isNull(salt, "Salt value must be null when used with crypto module PasswordEncoder");
                }
            });

            return;
        }

        throw new IllegalArgumentException("passwordEncoder must be a PasswordEncoder instance");
    }

    private void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");

        this.userNotFoundEncodedPassword = passwordEncoder.encodePassword(USER_NOT_FOUND_PASSWORD, null);
        this.passwordEncoder = passwordEncoder;
    }

    protected PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setSaltSource(SaltSource saltSource) {
        this.saltSource = saltSource;
    }

    protected SaltSource getSaltSource() {
        return saltSource;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    protected UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

}
