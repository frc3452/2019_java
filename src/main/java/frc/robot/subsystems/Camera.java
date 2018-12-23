package frc.robot.subsystems;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.wpilibj.CameraServer;

/**
 * <h1>Camera subsystem</h1> Handles camera exposure change and server switching
 * 
 * @author max
 *
 */
public class Camera {
	private UsbCamera mCamera0, mCamera1;
	private VideoSink server;

	private static Camera mInstance = null;

	public synchronized static Camera getInstance() {
		if (mInstance == null)
			mInstance = new Camera();

		return mInstance;
	}

	public Camera() {
		mCamera0 = CameraServer.getInstance().startAutomaticCapture(0);
		mCamera0.setResolution(640, 480);
		mCamera0.setFPS(15);
		mCamera0.setExposureManual(40);

		// mCamera1 = CameraServer.getInstance().startAutomaticCapture(1);
		// mCamera1.setResolution(180, 120);
		// mCamera1.setFPS(30);
		// mCamera1.setExposureAuto();

		server = CameraServer.getInstance().getServer();
		server.setSource(mCamera0);
	}

	public void camSwitch(int cameraswitch) {
		switch (cameraswitch) {
		case 0:
			server.setSource(mCamera0);
			break;
		case 1:
			server.setSource(mCamera1);
			break;
		default:
			System.out.println("Invalid camera selection");
			break;
		}
	}

	public void camExposure(int camera, int exposure) {
		switch (camera) {
		case 0:
			mCamera0.setExposureManual(exposure);
			break;
		case 1:
			mCamera1.setExposureManual(exposure);
			break;
		default:
			System.out.println("Invalid camera selection");
			break;
		}
	}

}
