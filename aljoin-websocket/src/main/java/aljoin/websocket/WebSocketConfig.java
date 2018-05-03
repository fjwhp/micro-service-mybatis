package aljoin.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * 
 * websocket配置:开启使用STOMP协议来传输基于代理(message broker)的消息
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午3:58:03
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	/**
	 * 注册协议节点、并映射指定的URl、指定SockJS协议
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/endpointAljoinOA").withSockJS();
	}

	/**
	 * 广播式应配置一个/topic消息代理
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic", "/point2point");
	}

}
