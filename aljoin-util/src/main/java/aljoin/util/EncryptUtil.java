package aljoin.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

/**
 * 
 * 加密解密类
 *
 * @author：zhongjy
 *
 * @date：2017年5月3日 下午10:42:56
 */
public class EncryptUtil {

    private final static Logger logger = LoggerFactory.getLogger(EncryptUtil.class);
    /**
     * 加密密钥
     */
    private static final String SITE_WIDE_SECRET = "http://www.aljoin.com/";
    private static final PasswordEncoder ENCODER = new StandardPasswordEncoder(SITE_WIDE_SECRET);

    public static final String ALGORITHM_MODE = "DES";
    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
    private static byte[] ivs = {1, 2, 3, 4, 5, 6, 7, 8};
    public static final String CODE_TYPE = "utf-8";

    /**
     * 
     * 功能描述: 密码类 加密方法：采用SHA-256算法，迭代1024次，使用一个密钥(site-widesecret) 以及8位随机盐对原密码进行加密。 随机盐确保相同的密码使用多次时，产生的哈希都不同。
     * 密钥应该与密码区别开来存放，加密时使用一个密钥即可；对hash算法迭代执行1024次增强了安全性，使暴力破解变得更困难些。
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月3日 下午10:43:25
     */
    public static String encrypt(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 
     * 密码匹配，登录时候调用
     *
     * @return：boolean
     *
     * @author：zhongjy
     *
     * @date：2017年5月3日 下午10:44:26
     */
    public static boolean match(String rawPassword, String password) {
        return ENCODER.matches(rawPassword, password);
    }

    /**
     * 
     * BASE64编码
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月3日 下午10:44:35
     */
    public static String encryptBASE64(String key) {
        byte[] byteKey = key.getBytes();
        byte[] encodeKey = Base64.encode(byteKey);
        String retKey = new String(encodeKey);
        /* 转16进制输出 */
        retKey = str2hex(retKey);
        return retKey;
    }

    /**
     * 
     * BASE64解码
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月3日 下午10:44:44
     */
    public static String decryptBASE64(String key) {
        /**
         * 转回一般的字符串再解码
         */
        key = hex2str(key);
        byte[] byteKey = key.getBytes();
        byte[] encodeKey = Base64.decode(byteKey);
        String retKey = new String(encodeKey);
        return retKey;
    }

    /**
     * 
     * MD5加密（信息摘要算法），返回加密后的十六进制串(长度32).
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月3日 下午10:45:04
     */
    public static String encryptMD5(String key) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(key.getBytes());
        } catch (NoSuchAlgorithmException e) {
            logger.error("", e);
        }
        byte[] keyByte = md.digest();
        /* 转十六进制串 */
        String result = "";
        for (int i = 0; i < keyByte.length; i++) {
            String tmp = Integer.toHexString(keyByte[i] & 0xFF);
            if (tmp.length() == 1) {
                result += "0" + tmp;
            } else {
                result += tmp;
            }
        }
        return result;
    }

    /**
     * 
     * SHA加密（安全散列算法），返回加密后的十六进制串(长度40).
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月3日 下午10:45:53
     */
    public static String encryptSHA(String key) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
            md.update(key.getBytes());
        } catch (NoSuchAlgorithmException e) {
            logger.error("", e);
        }
        byte[] keyByte = md.digest();
        /* 转十六进制串 */
        String result = "";
        for (int i = 0; i < keyByte.length; i++) {
            String tmp = Integer.toHexString(keyByte[i] & 0xFF);
            if (tmp.length() == 1) {
                result += "0" + tmp;
            } else {
                result += tmp;
            }
        }
        return result;
    }

    /**
     * 
     * DES加密（数据加密算法）.
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月3日 下午10:46:12
     */
    public static String encryptDES(String data, String keyStr) throws Exception {
        try {
            DESKeySpec dks = new DESKeySpec(keyStr.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_MODE);
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(ivs);
            // AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes(CODE_TYPE));
            // return new String(Base64.encode(bytes),"utf-8") ;
            /* 加密后base64编码后返回 */
            // Log.info("加密成功...");
            byte[] encodeKey = Base64.encode(bytes);
            String retKey = new String(encodeKey);
            /* 转16进制后再输出 */
            String retStr = str2hex(retKey);
            return retStr;
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    /**
     * 
     * DES解密（数据加密算法）.
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月3日 下午10:46:34
     */
    public static String decryptDES(String data, String keyStr) throws Exception {
        /* 转回一般的字符串再解码 */
        data = hex2str(data);
        DESKeySpec dks = new DESKeySpec(keyStr.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_MODE);
        // key的长度不能够小于8位字节
        Key secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
        IvParameterSpec iv = new IvParameterSpec(ivs);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        return new String(cipher.doFinal(Base64.decode(data.getBytes())), CODE_TYPE);
    }

    /**
     * 
     * 字符串转十六进制串
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月3日 下午10:48:57
     */
    public static String str2hex(String str) {
        byte[] keyByte = str.getBytes();
        String result = "";
        for (int i = 0; i < keyByte.length; i++) {
            String tmp = Integer.toHexString(keyByte[i] & 0xFF);
            if (tmp.length() == 1) {
                result += "0" + tmp;
            } else {
                result += tmp;
            }
        }
        return result;
    }

    /**
     * 
     * 十六进制串转字符串
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月3日 下午10:46:55
     */
    public static String hex2str(String str) {
        byte[] arrB = str.getBytes();
        int iLen = arrB.length;
        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte)Integer.parseInt(strTmp, 16);
        }
        return new String(arrOut);
    }

    /**
     * 
     * 获取rsa公私钥对
     *
     * @return：Map<String,Object>
     *
     * @author：zhongjy
     *
     * @date：2017年7月25日 上午9:19:25
     */
    public static Map<String, String> getRsaKeyPair() {
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
            // 公钥私钥：->byte[]->Base64->String
            // map.put("public", publicKey.getEncoded());
            // map.put("private", privateKey.getEncoded());
            map.put("public", new String(Base64.encode(publicKey.getEncoded())));
            map.put("private", new String(Base64.encode(privateKey.getEncoded())));
            return map;
        } catch (NoSuchAlgorithmException e) {
            logger.error("", e);
        }
        return map;
    }

    /**
     * 
     * 还原公钥,X509EncodedKeySpec用于构建公钥的规范
     *
     * @return：PublicKey
     *
     * @author：zhongjy
     *
     * @date：2017年7月25日 上午10:55:52
     */
    public static PublicKey restorePublicKey(byte[] keyBytes) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = factory.generatePublic(x509EncodedKeySpec);
            return publicKey;
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * 
     * 还原私钥,PKCS8EncodedKeySpec用于构建私钥的规范
     *
     * @return：PrivateKey
     *
     * @author：zhongjy
     *
     * @date：2017年7月25日 上午10:58:24
     */
    public static PrivateKey restorePrivateKey(byte[] keyBytes) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
            return privateKey;
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * 
     * RSA加密
     *
     * @return：byte[]
     *
     * @author：zhongjy
     *
     * @date：2017年7月25日 上午11:02:33
     */
    public static String encryptRSA(String publicKey, String plainText) {
        // 倒序实现：公钥私钥：->byte[]->Base64->String
        PublicKey pk = restorePublicKey(Base64.decode(publicKey.getBytes()));
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            byte[] byteText = cipher.doFinal(plainText.getBytes());
            return new String(Base64.encode(byteText));
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * 
     * RSA解密
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年7月25日 上午11:03:49
     */
    public static String decryptRSA(String privateKey, String encodedText) throws Exception {
        // 倒序实现：公钥私钥：->byte[]->Base64->String
        PrivateKey pk = restorePrivateKey(Base64.decode(privateKey.getBytes()));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, pk);
        return new String(cipher.doFinal(Base64.decode(encodedText.getBytes())));
    }

    public static void main(String[] args) throws Exception {
        /*
         * System.out.println(encrypt("123")); System.out.println(encrypt("123"));
         * System.out.println(match("123",
         * "8758d202579ef2872f1e3992c52777a960f32d9f160e66e3f8d0737f5640d534abe7cb3380bed976"));
         * System.out.println(match("123",
         * "919721bbf712ec410c5a5fd3db990bbe6ca3cc855128bd7890a33821eac910f6188e0e1fed82f3a4"));
         */
        /*
         * 8758d202579ef2872f1e3992c52777a960f32d9f160e66e3f8d0737f5640d534abe7cb3380bed976
         * 919721bbf712ec410c5a5fd3db990bbe6ca3cc855128bd7890a33821eac910f6188e0e1fed82f3a4
         */
        /*
         * System.out.println(encryptBASE64("123456你好@#￥A"));
         * System.out.println(decryptBASE64("4d54497a4e445532354c3267356157395143507676365642"));
         * 
         * System.out.println(encryptMD5("adfdfdf"));
         * System.out.println(encryptSHA("adfdfdf222222222222"));
         * 
         * System.out.println(encryptDES("中国你好", "chk_12"));
         * System.out.println(decryptDES("6a7231427933367436683574753654375a7169444b673d3d", "chk_12"));
         */

        Map<String, String> keyMap = getRsaKeyPair(); // System.out.println(keyMap);
        String gy = keyMap.get("public");
        String sy = keyMap.get("private");
        System.out.println("RSA公钥：" + gy);
        System.out.println("RSA私钥：" + sy); // 加密
        String encodedText = encryptRSA(gy, "aljoin-123_@合强软件");
        System.out.println("RSA密文: " + encodedText); // 解密 PrivateKey
        System.out.println("RSA明文: " + decryptRSA(sy, encodedText));
        /*
         * System.out.println("DES加密zhongjy：" + encryptDES("zhongjy", "1!2Q3A4Z5@6W7S8X9#0E11D12C"));
         * System.out .println("DES加密zhongjianyu：" + encryptDES("zhongjianyu",
         * "1!2Q3A4Z5@6W7S8X9#0E11D12C"));
         * 
         * System.out.println(System.currentTimeMillis());
         * 
         * System.out.println(StringUtil.isAllUpperCase("AVC2434343"));
         * 
         * 
         * System.out.println(str2hex("text_RzJk3WbHRGtJELp中国@11"));
         * System.out.println(hex2str("746578745f527a4a6b335762485247744a454c70e4b8ade59bbd403131"));
         * 
         */

        // System.out.println("DES加密zhongjy：" + encryptDES("admin", "1!2Q3A4Z5@6W7S8X9#0E11D12C"));
        // System.out.println("DES加密zhongjianyu：" + encryptDES("123456", "1!2Q3A4Z5@6W7S8X9#0E11D12C"));
        // System.out.println(decryptDES("5148382b526b457872476f3d", "1!2Q3A4Z5@6W7S8X9#0E11D12C"));
    }
}
