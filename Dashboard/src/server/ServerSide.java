package server;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
 
@ServerEndpoint(value = "/test")
public class ServerSide {
	
	// Queue of all opened sessions.
	private static Queue<Session> queue = new ConcurrentLinkedQueue<Session>();
	 
	@OnMessage
	public void onMessage(Session session, String msg) {
	//provided for completeness, in out scenario clients don't send any msg.
		try {   
			System.out.println("received msg " + msg + " from " + session.getId());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	@OnOpen
	public void open(Session session) {
		queue.add(session);
		System.out.println("New session opened: " + session.getId());
	}
	
	 @OnError
	 public void error(Session session, Throwable t) {
		 queue.remove(session);
		 System.err.println("Error on session " + session.getId());  
	 }
	  
	 @OnClose
	 public void closedConnection(Session session) { 
		 queue.remove(session);
		 System.out.println("session closed: " + session.getId());
	 }
}
