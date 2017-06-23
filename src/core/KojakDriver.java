package core;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.websocket.Session;

import com.integratedbiometrics.ibscanultimate.IBScan;
import com.integratedbiometrics.ibscanultimate.IBScanDevice;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.FingerCountState;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.FingerQualityState;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.ImageData;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.ImageType;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.PlatenState;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.SegmentPosition;
import com.integratedbiometrics.ibscanultimate.IBScanDeviceListener;
import com.integratedbiometrics.ibscanultimate.IBScanException;
import com.integratedbiometrics.ibscanultimate.IBScanListener;

public class KojakDriver implements IBScanListener, IBScanDeviceListener {
	private static final int IMAGE_WIDTH = 640;// up to 1500;
	private static final int IMAGE_HEIGHT = 600;// up to 1500

	protected Session session;
	protected ImageType imageType;
	boolean autoCapture;
	boolean run;

	protected static IBScan ibScan = null;
	protected static IBScanDevice ibScanDevice = null;
	private OpenDeviceThread openDeviceThread;

	public KojakDriver(Session s,ImageType t, boolean auto) {
		this.session = s;
		this.imageType = t;
		this.autoCapture=auto;
	}

	public void StartScanner() {
		run = true;
		this.ibScan = IBScan.getInstance();
		this.ibScan.setScanListener(this);
		openDeviceThread = new OpenDeviceThread(0);
		openDeviceThread.start();

	}

	public void takeSnapshot() {

		try {
			
			getIBScanDevice().captureImageManually();

		} catch (IBScanException e) {
			e.printStackTrace();
		}
	}

	public void StopScanner() {
		run = false;
		try {
			openDeviceThread.stop();
			this.ibScanDevice.cancelCaptureImage();

		} catch (IBScanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void deviceAcquisitionBegun(IBScanDevice arg0, ImageType arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceAcquisitionCompleted(IBScanDevice arg0, ImageType arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceCommunicationBroken(IBScanDevice arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceFingerCountChanged(IBScanDevice arg0, FingerCountState arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceFingerQualityChanged(IBScanDevice arg0, FingerQualityState[] arg1) {
		// TODO Auto-generated method stub

	}

	public static byte[] BufferedImageToByte(BufferedImage img) {
		byte[] imageInBytes = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "jpg", baos);
			baos.flush();
			imageInBytes = baos.toByteArray();
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageInBytes;
	}

	@Override
	public void deviceImagePreviewAvailable(IBScanDevice device, ImageData image) throws IBScanException {
		// TODO Auto-generated method stub
		int destWidth = IMAGE_WIDTH;
		int destHeight = IMAGE_HEIGHT;
		int outImageSize = destWidth * destHeight;

		byte[] outImage = new byte[outImageSize];
		Arrays.fill(outImage, (byte) 255);

		try {
			ibScanDevice.generateZoomOutImageEx(image.buffer, image.width, image.height, outImage, destWidth,
					destHeight, (byte) 255);

			session.getBasicRemote().sendBinary(
					ByteBuffer.wrap(BufferedImageToByte(image.toImage(outImage, IMAGE_WIDTH, IMAGE_HEIGHT))));

		} catch (IBScanException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void deviceImageResultAvailable(IBScanDevice arg0, ImageData arg1, ImageType arg2, ImageData[] arg3) {
		System.out.println("deviceImageResultAvailable");

	}

	@Override
	public void deviceImageResultExtendedAvailable(IBScanDevice device, IBScanException imageStatus, ImageData image,
			ImageType imageType, int detectedFingerCount, ImageData[] segmentImageArray,
			SegmentPosition[] segmentPositionArray) {
		int destWidth = IMAGE_WIDTH;
		int destHeight = IMAGE_HEIGHT;
		int outImageSize = destWidth * destHeight;

		byte[] outImage = new byte[outImageSize];
		Arrays.fill(outImage, (byte) 255);

		try {
			ibScanDevice.generateZoomOutImageEx(image.buffer, image.width, image.height, outImage, destWidth,
					destHeight, (byte) 255);
			
			
			Graphics2D g = (Graphics2D) image.toImage().getGraphics();
			g.setColor(new Color(0, 128, 0));
			g.setStroke(new BasicStroke(1));

			if (segmentPositionArray.length > 0) {

				int x1, x2, x3, x4;
				int y1, y2, y3, y4;
				int leftMargin=0, topMargin=0;
				double ratio_width, ratio_height, scaleFactor;
				ratio_width = (double)IMAGE_WIDTH / IMAGE_WIDTH;
				ratio_height = (double)IMAGE_HEIGHT / IMAGE_HEIGHT;
				
				
				scaleFactor = ratio_width;
				if (ratio_width < ratio_height) {
					scaleFactor = ratio_height;
				}
				
				for (int i = 0; i < segmentPositionArray.length; i++) {
					x1 = leftMargin + (int)(segmentPositionArray[i].x1 * scaleFactor);
					x2 = leftMargin + (int)(segmentPositionArray[i].x2 * scaleFactor);
					x3 = leftMargin + (int)(segmentPositionArray[i].x3 * scaleFactor);
					x4 = leftMargin + (int)(segmentPositionArray[i].x4 * scaleFactor);
					y1 = topMargin +  (int)(segmentPositionArray[i].y1 * scaleFactor);
					y2 = topMargin +  (int)(segmentPositionArray[i].y2 * scaleFactor);
					y3 = topMargin +  (int)(segmentPositionArray[i].y3 * scaleFactor);
					y4 = topMargin +  (int)(segmentPositionArray[i].y4 * scaleFactor);

					g.drawLine(x1, y1, x2, y2);
					g.drawLine(x2, y2, x3, y3);
					g.drawLine(x3, y3, x4, y4);
					g.drawLine(x4, y4, x1, y1);
				}
			}
			
			
	
			
			
			//session.getBasicRemote().sendBinary(
			//		ByteBuffer.wrap(BufferedImageToByte(image.toImage(outImage, IMAGE_WIDTH, IMAGE_HEIGHT))));
			session.getBasicRemote().sendText("action:snapshot:final");
			session.getBasicRemote().sendBinary(
					ByteBuffer.wrap(BufferedImageToByte(image.toImage())));

			
			session.getBasicRemote().sendText("action:snapshot");
			session.getBasicRemote().sendText("action:nfiqScore:" + getIBScanDevice().calculateNfiqScore(image));
			this.ibScanDevice.setBeeper(IBScanDevice.BeepPattern.BEEP_PATTERN_GENERIC, 2, 4, 0, 0);

		} catch (IBScanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("IBScan.closeDevice() returned exception " + e1.getType().toString() + ".");
			System.out.println("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	@Override
	public void devicePlatenStateChanged(IBScanDevice arg0, PlatenState arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void devicePressedKeyButtons(IBScanDevice arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceWarningReceived(IBScanDevice arg0, IBScanException arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scanDeviceCountChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scanDeviceInitProgress(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scanDeviceOpenComplete(int arg0, IBScanDevice arg1, IBScanException arg2) {
		// TODO Auto-generated method stub

	}

	/********************* GETTERS & SETTERS *****************************/

	// Get IBScan.
	protected IBScan getIBScan() {
		return (this.ibScan);
	}

	// Get opened or null IBScanDevice.
	protected IBScanDevice getIBScanDevice() {
		return (this.ibScanDevice);
	}

	// Set IBScanDevice.
	protected void setIBScanDevice(IBScanDevice ibScanDevice) {
		this.ibScanDevice = ibScanDevice;
		if (ibScanDevice != null) {
			ibScanDevice.setScanDeviceListener(this);
		}
	}

	/**
	 * Thread del IBS abierto
	 */

	class OpenDeviceThread extends Thread {
		private int deviceIndexTemp;

		OpenDeviceThread(int deviceIndexTemp) {
			this.deviceIndexTemp = deviceIndexTemp;
		}

		@Override
		public void run() {
			try {
				// Open device for the device index specified in
				// "Device Index" field and save
				// device.
				if(KojakDriver.this.getIBScanDevice()== null){
					IBScanDevice ibScanDeviceNew = KojakDriver.this.getIBScan().openDevice(this.deviceIndexTemp);
					KojakDriver.this.setIBScanDevice(ibScanDeviceNew);
					System.out.println("IBScan.openDevice() successful");
				}else{
					 if(!KojakDriver.this.getIBScanDevice().isOpened()){
						 IBScanDevice ibScanDeviceNew = KojakDriver.this.getIBScan().openDevice(this.deviceIndexTemp);
							KojakDriver.this.setIBScanDevice(ibScanDeviceNew);
							System.out.println("IBScan.openDevice() successful");
					 }
				}
				
				

				// Begin capturing image for active device.
				int captureOptions = 0;
				if(autoCapture){
					captureOptions |=IBScanDevice.OPTION_AUTO_CAPTURE;
				}

			
				IBScanDevice.ImageResolution imageResolution = IBScanDevice.ImageResolution.RESOLUTION_500;

				ibScanDevice.beginCaptureImage(imageType, imageResolution, captureOptions);

			} catch (IBScanException ibse) {
				ibse.printStackTrace();
				System.out.println("IBScan.closeDevice() returned exception " + ibse.getType().toString() + ".");
				System.out.println("");
			}
		}
	}
	/***
	 * Fin del thread del IBS abierto
	 */

}
