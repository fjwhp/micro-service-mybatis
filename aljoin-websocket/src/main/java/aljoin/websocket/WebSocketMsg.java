package aljoin.websocket;

/**
 * 
 * websocket消息类
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午3:57:55
 */
public class WebSocketMsg {

	/**
	 * 返回码
	 */
	private String code;
	/**
	 * 返回消息
	 */
	private String message;
	/**
	 * 返回对象
	 */
	private Object object;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
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
