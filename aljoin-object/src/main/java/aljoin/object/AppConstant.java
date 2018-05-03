package aljoin.object;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AppConstant {

    /**
     * RetMsg返回码定义------------------------------
     */
    /**
     * 访问成功
     */
    public static final Integer RET_CODE_SUCCESS = 200;
    /**
     * 服务器异常
     */
    public static final Integer RET_CODE_ERROR = 500;
    /**
     * 无权访问
     */
    public static final Integer RET_CODE_NO_AUTH = 403;
    /**
     * 参数异常
     */
    public static final Integer RET_CODE_PARAM_ERR = 400;
    /**
     * 业务操作失败
     */
    public static final Integer RET_CODE_BIZ_ERR = 201;

    /**
     * 参数加密类型------------------------------
     */
    /**
     * RSA加密
     */
    public static final String PARAM_ENCRYPT_RSA = "RSA";
    /**
     * 
     */
    public static final String PARAM_ENCRYPT_DES = "DES";

    /**
     * 登录DES加密秘钥------------------------------
     */
    public static final String APP_DES_KEY = "7uj7815168fd*reWr23OPerfdWWdIkeure#1!2Q3A4Z5@6W7S8X9#0E11D12C";

    /**
     * 固定流程 类型------------------------------
     */
    /**
     * 月报流程
     */
    public static final String FIXED_BIZ_TYPE_OFF = "off";
    /**
     * 考勤补签流程
     */
    public static final String FIXED_BIZ_TYPE_ATT = "att";
    /**
     * 外部会议流程
     */
    public static final String FIXED_BIZ_TYPE_MEE = "mee";
    /**
     * 公共信息流程
     */
    public static final String FIXED_BIZ_TYPE_PUB = "pub";
    /**
     * 车船使用申请流程
     */
    public static final String FIXED_BIZ_TYPE_VEH = "veh";
    /**
     * 办公用品出入库流程
     */
    public static final String FIXED_BIZ_TYPE_GOO = "goo";
    /**
     * 办公用品申购流程
     */
    public static final String FIXED_BIZ_TYPE_PUR = "pur";
}
