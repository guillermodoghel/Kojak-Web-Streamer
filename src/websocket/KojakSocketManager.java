package websocket;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


import com.integratedbiometrics.ibscanultimate.IBScanDevice;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.ImageType;

import core.KojakDriver;


@ServerEndpoint("/KojakSocketManager")
public class KojakSocketManager {
	public static String STATUS;
	public KojakDriver kojakDriver;
	private static  KojakDriver kojakScanner;

	@OnOpen
	public void open(Session session) {
		System.out.println("open");
		
	}

	@OnClose
	public void close(Session session) {
		System.out.println("close");
		kojakScanner.StopScanner();
	}

	@OnError
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		System.out.println("message: " + message);
			
		switch (message) {
        case "action:start:footPrint":
        	streamScanner(session,IBScanDevice.ImageType.FLAT_FOUR_FINGERS,false);
            break;
        case "action:start:oneThumb":
        	streamScanner(session,IBScanDevice.ImageType.FLAT_SINGLE_FINGER,true);
            break;
        case "action:start:twoThumbs":
        	streamScanner(session,IBScanDevice.ImageType.FLAT_TWO_FINGERS,true);
            break;
        case "action:start:leftFour":
        	streamScanner(session,IBScanDevice.ImageType.FLAT_FOUR_FINGERS,true);
            break;
        case "action:start:rightFour":
        	streamScanner(session,IBScanDevice.ImageType.FLAT_FOUR_FINGERS,true);
            break;
        default:
            throw new IllegalArgumentException("Invalid message: " + message);
    }
		
		
		
	}

	public void streamScanner(Session session,ImageType imageType ,boolean autoCapture) throws IOException {
		kojakScanner = new KojakDriver(session,imageType,autoCapture);
		kojakScanner.StartScanner();
		
	}
	
	public static void takeSnapshot(){
		kojakScanner.takeSnapshot();
		kojakScanner = null;
	}
	
	
	
	

}
