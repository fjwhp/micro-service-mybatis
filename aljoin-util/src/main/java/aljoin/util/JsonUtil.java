package aljoin.util;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * json相关工具类
 *
 * @author：zhongjy
 * 
 * @date：2017年5月25日 下午1:50:27
 */
public class JsonUtil {

    /**
     * 
     * 对象转json字符串
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月25日 下午1:54:34
     */
    public static String obj2str(Object object) {
        if (object == null) {
            return null;
        } else {
            return JSON.toJSONString(object, true);
        }
    }

    /**
     * 
     * 根据对象字符串返回对象
     *
     * @return：T.
     *
     * @author：zhongjy
     *
     * @date：2017年5月25日 下午2:01:41
     */
    public static <T> T str2obj(String str, Class<T> c) {
        if (str == null) {
            return null;
        } else {
            return JSON.parseObject(str, c);
        }
    }

    /**
     * 
     * 根据对象字符串返回list对象
     *
     * @return：List<T>
     *
     * @author：zhongjy
     *
     * @date：2017年5月25日 下午2:08:50
     */
    public static <T> List<T> str2list(String str, Class<T> c) {
        if (str == null) {
            return null;
        } else {
            return JSON.parseArray(str, c);
        }
    }

    /**
     * 
     * 字符串转json对象
     *
     * @return：JSONObject
     *
     * @author：zhongjy
     *
     * @date：2017年5月25日 下午2:11:04
     */
    public static JSONObject str2json(String str) {
        if (str == null) {
            return null;
        } else {
            return JSONObject.parseObject(str);
        }
    }

    /**
     * 
     * 字符串转json对象数组
     *
     * @return：JSONArray
     *
     * @author：zhongjy
     *
     * @date：2017年5月25日 下午2:11:52
     */
    public static JSONArray str2jsonArr(String str) {
        if (str == null) {
            return null;
        } else {
            return JSONObject.parseArray(str);
        }
    }

    /**
     * 
     * 对象转json对象
     *
     * @return：Object
     *
     * @author：zhongjy
     *
     * @date：2017年5月25日 下午2:14:05
     */
    public Object obj2json(Object obj) {
        if (obj == null) {
            return null;
        } else {
            return JSON.toJSON(obj);
        }
    }

}
