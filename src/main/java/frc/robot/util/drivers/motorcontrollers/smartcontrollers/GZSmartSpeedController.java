package frc.robot.util.drivers.motorcontrollers.smartcontrollers;

import com.ctre.phoenix.motorcontrol.IMotorController;

import frc.robot.util.drivers.motorcontrollers.GZSpeedController;

public interface GZSmartSpeedController extends GZSpeedController {
    public static enum Side {
		LEFT, RIGHT, NO_INFO;
	}

	public static enum Master {
		MASTER, FOLLOWER, NO_INFO;
	}

	public Breaker getBreaker();
	public Side getSide();
	public Master getMaster();
    public void follow(IMotorController masterToFollow);
}