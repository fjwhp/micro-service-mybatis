var stompClient = null;
/**
 * 建立连接
 * 
 * @param topic--广播回调
 * @param point2point--点对点回调
 */
function connect(topic, point2point) {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	// 连接SockJS的endpoint，名称为"/endpointAljoinOA"
	var socket = new SockJS('/endpointAljoinOA');
	// 使用stomp子协议的WebSocket 客户端
	stompClient = Stomp.over(socket);
	// 链接Web Socket的服务端
	stompClient.connect({}, function(frame) {
		// console.log('连接已建立: ' + frame);
		// 订阅/topic/getResponse
		stompClient.subscribe('/topic/getResponse', function(respnose) {
			// 消息推送响应(广播式)
			topic(JSON.parse(respnose.body));
		});
		// 订阅/user/point2point/getResponse
		stompClient.subscribe('/user/point2point/getResponse', function(respnose) {
			// 消息推送响应(点对点)
			point2point(JSON.parse(respnose.body));
		});
	});
}
/**
 * 断开连接
 */
function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
}