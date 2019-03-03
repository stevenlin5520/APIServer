package com.steven.demo.controller;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * 请求地址：http://192.168.33.199:8080/APIServer/index.jsp
 * @author Steven
 *
 */
@ServerEndpoint("/websocket")
public class WebSocketDemo {

	// 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;

	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
	private static CopyOnWriteArraySet<WebSocketDemo> webSocketSet = new CopyOnWriteArraySet<WebSocketDemo>();

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private static Session session;

	/**
	 * 连接建立成功调用的方法
	 * 
	 * @param session
	 *            可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	@SuppressWarnings("static-access")
	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		webSocketSet.add(this); // 加入set中
		addOnlineCount(); // 在线数加1
		System.out.println("有新连接"+session.getId()+"加入！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		webSocketSet.remove(this); // 从set中删除
		subOnlineCount(); // 在线数减1
		System.out.println("有一连接"+session.getId()+"关闭！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 收到客户端消息后调用的方法
	 * 
	 * @param message
	 *            客户端发送过来的消息
	 * @param session
	 *            可选的参数
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("来自客户端"+session.getId()+"的消息:" + message);
		// 群发消息
		for (WebSocketDemo item : webSocketSet) {
			try {
//				for (int i = 0; i < 5; i++) {
//					item.sendMessage("你好，小安利"+onlineCount+"号！");
//					Thread.sleep(1000);
//				}
				execute(5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发生错误时调用
	 * 
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		System.out.println("发生错误");
		error.printStackTrace();
	}

	/**
	 * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
	 * 
	 * @param message
	 * @throws IOException
	 */
	public static void sendMessage(String message) throws IOException {
		session.getBasicRemote().sendText(message);
	}

	public static void execute(int count){
		for (int i = 0; i < count; i++) {
			try {
				Thread.sleep(1000);
				sendMessage("你好，小安丽"+onlineCount+"号！,session为"+session.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		WebSocketDemo.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		WebSocketDemo.onlineCount--;
	}
}
