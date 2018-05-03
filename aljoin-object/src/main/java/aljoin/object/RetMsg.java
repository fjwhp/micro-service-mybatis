package aljoin.object;

/**
 * 
 * ajax访问统一返回对象
 *
 * @author：zhongjy
 *
 * @date：2017年5月25日 下午8:24:41
 */
public class RetMsg {

    /**
     * 返回码 0 正常 1 异常
     */
    private Integer code;
    /**
     * 返回消息
     */
    private String message;
    /**
     * 返回对象
     */
    private Object object;

    /**
     * 
     * 快捷获取 操作失败的retmsg 对象
     *
     * @return：RetMsg
     *
     * @author：wuhp
     *
     * @date：2017年10月28日 下午12:54:13
     */
    public static RetMsg getFailRetMsg() {
        RetMsg msg = new RetMsg();
        msg.setCode(WebConstant.RETMSG_FAIL_CODE);
        msg.setMessage(WebConstant.RETMSG_OPERATION_FAIL);
        return msg;
    }

    /**
     * 
     * 快捷获取 操作失败的retmsg 对象，指定错误信息
     *
     * @return：RetMsg
     *
     * @author：wuhp
     *
     * @date：2017年10月31日 上午10:19:03
     */
    public static RetMsg getFailRetMsg(String errorMsg) {
        RetMsg msg = new RetMsg();
        msg.setCode(WebConstant.RETMSG_FAIL_CODE);
        msg.setMessage(errorMsg);
        return msg;
    }

    /**
     * 
     * 快捷获取 操作成功的retmsg 对象
     *
     * @return：RetMsg
     *
     * @author：wuhp
     *
     * @date：2017年10月28日 下午12:53:31
     */
    public static RetMsg getSuccessRetMsg() {
        RetMsg msg = new RetMsg();
        msg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
        msg.setMessage(WebConstant.RETMSG_OPERATION_SUCCESS);
        return msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

}
