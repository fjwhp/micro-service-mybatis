package aljoin.websocket.service;

import java.util.List;

import aljoin.websocket.WebSocketMsg;

/**
 * 
 * websocket(接口).
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午3:57:41
 */
public interface WebSocketService {

	/**
	 * 
	 * 广播式消息推送
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年6月12日 下午12:58:18
	 * 
	 * @param msg
	 */
	public void pushMessage(WebSocketMsg msg);

	/**
	 * 
	 * 点对点消息推送(多用户).
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年6月12日 下午12:58:18
	 * 
	 * @param msg
	 * 
	 * @param userNames
	 */
	public void pushMessageToUserList(WebSocketMsg msg, List<String> userNames);

	/**
	 * 
	 * 点对点消息推送(单用户).
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年6月12日 下午12:58:18
	 * 
	 * @param msg
	 * 
	 * @param userName
	 */
	public void pushMessageToUser(WebSocketMsg msg, String userName);
}
