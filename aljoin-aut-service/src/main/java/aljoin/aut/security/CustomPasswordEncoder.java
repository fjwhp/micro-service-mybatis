package aljoin.aut.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 
 * 自定义密码加密器
 *
 * @author：zhongjy
 *
 * @date：2017年5月4日 下午12:27:37
 */
@Service
public class CustomPasswordEncoder extends BCryptPasswordEncoder {

    /**
     * 加密强度设置为10(强度越高，时间越长)
     */
    public CustomPasswordEncoder() {
        super(10);
    }

    /**
     * 加密
     */
    @Override
    public String encode(CharSequence rawPassword) {
        return super.encode(rawPassword);
    }

    /**
     * 校验 rawPassword-原密码(明文) encodedPassword-密文(数据库取出的加密密码)
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return super.matches(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        CustomPasswordEncoder cpe = new CustomPasswordEncoder();
        System.out.println(cpe.encode("sadmin"));
        System.out.println(cpe.encode("sadmin").length());
    }

}
