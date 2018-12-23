package frc.robot.util;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Constants.kLoop;
import frc.robot.subsystems.Health.AlertLevel;

//thx 254
public abstract class GZSubsystem extends Subsystem {

	// Set to neutral
	public abstract void stop();

	private AlertLevel mHighestAlert = AlertLevel.NONE;

	private boolean mTalonTestingHasFail = false;

	protected Map<Integer, GZSRX> mTalons = new HashMap<Integer, GZSRX>();
	protected Map<Integer, GZSpark> mSparks = new HashMap<Integer, GZSpark>();

	public abstract boolean hasMotors();

	public void clearMotorTestingFails()
	{
		mTalonTestingHasFail = false;
	}

	public void setMotorTestingFail()
	{
		mTalonTestingHasFail = true;
	}

	public boolean hasMotorTestingFail()
	{
		return mTalonTestingHasFail;
	}

	public abstract void addMotorTestingGroups();
	public abstract void addPDPTestingMotors();
	

	/**
	 * Disabling each subsystem
	 */
	private boolean mIsDisabled = false;

	public void safetyDisable(boolean toDisable) {
		mIsDisabled = toDisable;
		if (mIsDisabled)
			stop();
	}

	public abstract void addLoggingValues();

	public Boolean isSafetyDisabled() {
		return mIsDisabled;
	}

	/**
	 * Looping
	 */
	private Notifier loopNotifier = new Notifier(new Runnable() {
		public void run() {
			loop();
		}
	});

	public abstract void loop();

	public void startLooping() {
		loopNotifier.startPeriodic(kLoop.LOOP_SPEED);
	}

	// Each subsystem is able to report its current state as a string
	public abstract String getStateString();

	// For Health generater
	public void setHighestAlert(AlertLevel level) {
		mHighestAlert = level;
	}
	public AlertLevel getHighestAlert() {
		return mHighestAlert;
	}

	// Read all inputs
	protected abstract void in();

	// Write all outputs
	protected abstract void out();

	public void enableFollower() {
	}

	// Zero sensors
	public void zeroSensors() {
	}

	// Write values to smart dashboard
	public void outputSmartDashboard() {
	}
}
