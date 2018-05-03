package aljoin.websocket.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import aljoin.websocket.WebSocketMsg;
import aljoin.websocket.service.WebSocketService;

/**
 * 
 * websocket(实现).
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午3:57:29
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {

	@Resource
	private SimpMessagingTemplate template;

	@Override
	public void pushMessage(WebSocketMsg msg) {
		template.convertAndSend("/topic/getResponse", msg);
	}

	@Override
	public void pushMessageToUserList(WebSocketMsg msg, List<String> userNames) {
		for (String userName : userNames) {
			template.convertAndSendToUser(userName, "/point2point/getResponse", msg);
		}
	}

	@Override
	public void pushMessageToUser(WebSocketMsg msg, String userName) {
		template.convertAndSendToUser(userName, "/point2point/getResponse", msg);
	}

}
