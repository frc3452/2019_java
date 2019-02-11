package frc.robot.util.drivers.motorcontrollers;

import com.ctre.phoenix.motorcontrol.IMotorController;

import frc.robot.subsystems.Health.AlertLevel;

public interface GZSmartSpeedController extends GZSpeedController {

	public final static int TIMEOUT = 10;
	public static final int LONG_TIMEOUT = 100;
	public final static int FIRMWARE = 1035;
	public final static AlertLevel FIRMWARE_ALERT_LEVEL = AlertLevel.WARNING;

    public static enum Side {
		LEFT, RIGHT, NO_INFO;
	}

	public static enum Master {
		MASTER, FOLLOWER, NO_INFO;
	}

	public static enum SmartController {
		TALON, VICTORSPX,
	}

	public Breaker getBreaker();
	public Side getSide();
	public Master getMaster();
	public void follow(IMotorController masterToFollow);
	
	public int getFirmware();
	public void checkFirmware();

	public int getPort();

	public SmartController getControllerType();
}