package frc.robot.util;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.MotorChecker.AmperageChecker;
import frc.robot.util.MotorChecker.AmperageChecker.CheckerConfig;
import frc.robot.util.drivers.GZSRX;
import frc.robot.util.drivers.GZSpeedController;

//thx 254
public abstract class GZSubsystem extends Subsystem {

	// Set to neutral
	public abstract void stop();

	public Map<Integer, GZSRX> mTalons = new HashMap<Integer, GZSRX>();
	public Map<Integer, GZSpeedController> mDumbControllers = new HashMap<Integer, GZSpeedController>();

	public void printState()
	{
		System.out.println(getStateString());
	}


	public String toString()
	{
		return this.getClass().getSimpleName();
	}

	// Motors testing
	public abstract boolean hasMotors();

	private boolean mMotorTestingFails = false;

	public void clearMotorTestingFails() {
		mMotorTestingFails = false;
	}

	public void setMotorTestingFail() {
		mMotorTestingFails = true;
	}

	public boolean hasMotorTestingFail() {
		return mMotorTestingFails;
	}

	public void addMotorsForTesting(){
		AmperageChecker.getInstance().addTalonGroups(CheckerConfig.getFromFile(this));
	}

	/**
	 * Disabling each subsystem
	 */
	private boolean mIsDisabled = false;
	public void safetyDisable(boolean toDisable) {
		mIsDisabled = toDisable;
		if (mIsDisabled)
			stop();
	}
	public Boolean isSafetyDisabled() {
		return mIsDisabled;
	}

	public abstract void addLoggingValues();

	public abstract void loop();

	// Each subsystem is able to report its current state as a string
	public abstract String getStateString();

	// For Health generater
	private AlertLevel mHighestAlert = AlertLevel.NONE;

	public void setHighestAlert(AlertLevel level) {
		mHighestAlert = level;
	}

	public AlertLevel getHighestAlert() {
		return mHighestAlert;
	}

	public void enableFollower() {
	}

	// Zero sensors
	public void zeroSensors() {
	}

	// Write values to smart dashboard
	public void outputSmartDashboard() {
	}
}
