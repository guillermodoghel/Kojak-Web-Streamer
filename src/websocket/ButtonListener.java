package websocket;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import core.BFSingleton;


@ServerEndpoint("/buttonListener")
public class ButtonListener {
	
	public static String STATUS;

	@OnOpen
	public void open(Session session) {
		BFSingleton.getInstance().setButtonListenerStatus("OK");

	}

	@OnClose
	public void close(Session session) {
		BFSingleton.getInstance().setButtonListenerStatus("CERRAR");
	}

	@OnError
	public void onError(Throwable error) {
		BFSingleton.getInstance().setButtonListenerStatus("FAIL");;
	}

	@OnMessage
	public void handleMessage(String message, Session session) {
		System.out.println("ButtonListener:pressedButton");
		KojakSocketManager.takeSnapshot();
	}
	
}
