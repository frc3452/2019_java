package frc.robot.util;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.MotorChecker.AmperageChecker;
import frc.robot.util.MotorChecker.AmperageChecker.CheckerConfig;
import frc.robot.util.drivers.motorcontrollers.GZSpeedController;
import frc.robot.util.drivers.motorcontrollers.smartcontrollers.GZSRX;
import frc.robot.util.drivers.motorcontrollers.smartcontrollers.GZSmartSpeedController;
import frc.robot.util.drivers.pneumatics.GZDoubleSolenoid;
import frc.robot.util.drivers.pneumatics.GZSolenoid;

public abstract class GZSubsystem extends Subsystem {

	// Set to neutral
	public abstract void stop();

	//Talons
	public ArrayList<GZSRX> mTalons = new ArrayList<GZSRX>();

	//Talons, VictorSPXs, etc.
	public ArrayList<GZSmartSpeedController> mSmartControllers = new ArrayList<GZSmartSpeedController>();

	//PWM, Sparks, Etc.
	public ArrayList<GZSpeedController> mDumbControllers = new ArrayList<GZSpeedController>();

	public ArrayList<GZSolenoid> mSingleSolenoids = new ArrayList<GZSolenoid>();
	public ArrayList<GZDoubleSolenoid> mDoubleSolenoids = new ArrayList<GZDoubleSolenoid>();

	public void printState() {
		System.out.println(getStateString());
	}

	public abstract String getSmallString();

	public void addLoggingValuesTalons() {
		for (GZSRX s : mTalons) {
			final String name = getSmallString() + "-" + s.getGZName() + "-";

			// Amperage
			new LogItem(name + "AMP") {
				public String val() {
					return "" + s.getOutputCurrent();
				}
			};

			// Amperage average
			new LogItem(name + "AMP-AVG", true) {
				public String val() {
					return LogItem.Average_Left_Formula;
				}
			};

			// Temperature sensors
			if (s.hasTemperatureSensor()) {
				new LogItem(name + "TEMP") {

					public String val() {
						return String.valueOf(s.getTemperatureSensor());
					}
				};

				new LogItem(name + "TEMP-AVG", true) {

					public String val() {
						return LogItem.Average_Left_Formula;
					}
				};
			}
		}

		// VOLTAGE
		for (GZSRX s : mTalons) {
			final String name = getSmallString() + "-" + s.getGZName() + "-";
			new LogItem(name + "VOLT") {

				@Override
				public String val() {
					return "" + s.getMotorOutputVoltage();
				}
			};
		}
	}

	public void addLoggingItemsDumbControllers() {
		for (GZSpeedController s : mDumbControllers) {
			final String name = getSmallString() + "-" + s.getGZName() + "-";
			new LogItem(name + "AMP") {

				@Override
				public String val() {
					return "" + s.getAmperage();
				}
			};
			new LogItem(name + "AMP-AVG", true) {
				@Override
				public String val() {
					return LogItem.Average_Left_Formula;
				}
			};
		}

		for (GZSpeedController s : mDumbControllers) {
			final String name = getSmallString() + "-" + s.getGZName() + "-";
			new LogItem(name + "PRCNT") {

				@Override
				public String val() {
					return "" + s.getOutputPercentage();
				}
			};
		}
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}

	// Motors testing
	public abstract boolean hasMotors();
	public abstract boolean hasAir();

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

	public void addMotorsForTesting() {
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
