package aljoin.websocket;

import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.session.ExpiringSession;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * 
 * websocket配置:开启使用STOMP协议来传输基于代理(message broker)的消息(支持集群)
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午3:58:03
 */
//@Configuration
//@EnableWebSocketMessageBroker
public class SessionWebSocketConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<ExpiringSession> {

    /**
     * 注册协议节点、并映射指定的URl、指定SockJS协议
     */
    //@Override
    protected void configureStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/endpointAljoinOA").withSockJS();
    }

    /**
     * 广播式应配置一个/topic消息代理
     */
    //@Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/point2point");
    }

}
