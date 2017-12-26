package server;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
	//private static Thread thread ; //rate publisher thread
	
	/*static{
		//rate publisher thread, generates a new value for USD rate every 2 seconds.
		thread=new Thread(){
			public void run() {
				DecimalFormat df = new DecimalFormat("#.####");
				while(true) {
					double d=2+Math.random();     
					if( queue != null )
						sendAll("USD Rate: "+df.format(d));    
					try {
						sleep(2000);
					}
					catch (InterruptedException e) {      
					}
				}
			};
		} ;
		thread.start();
	}*/
	 
	@OnMessage
	public void onMessage(Session session, String message) {
	//provided for completeness, in out scenario clients don't send any messages.
		try {   
			System.out.println("received msg " + message + " from " + session.getId());
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
	 
	 private static void sendAll(String message) {
		 try {
			 /* Send the new rate to all open WebSocket sessions */  
			 ArrayList<Session> closedSessions= new ArrayList<>();
			 for ( Session session : queue ) {
				 if( !session.isOpen() ) {
					 closedSessions.add(session);
				 }
				 else {
					 session.getBasicRemote().sendText(message);
					 //session.getBasicRemote().sendObject(message);
					 //session.getBasicRemote().sendStream(message);
				 }    
			 }
			 queue.removeAll(closedSessions);
			 //System.out.println("Sending " + message + " to " + queue.size() + " clients");
		 }
		 catch (Throwable e) {
			 e.printStackTrace();
		 }
	 }
}