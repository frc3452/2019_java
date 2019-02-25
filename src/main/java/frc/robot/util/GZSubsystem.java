package frc.robot.util;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.GZOI;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.MotorChecker.AmperageChecker;
import frc.robot.util.MotorChecker.AmperageChecker.CheckerConfig;
import frc.robot.util.drivers.motorcontrollers.GZSRX;
import frc.robot.util.drivers.motorcontrollers.GZSmartSpeedController;
import frc.robot.util.drivers.motorcontrollers.GZSpeedController;
import frc.robot.util.drivers.motorcontrollers.GZVictorSPX;
import frc.robot.util.drivers.pneumatics.GZDoubleSolenoid;
import frc.robot.util.drivers.pneumatics.GZSolenoid;

public abstract class GZSubsystem extends Subsystem {

	// Set to neutral
	public abstract void stop();

	// Talons
	public ArrayList<GZSRX> mTalons = new ArrayList<GZSRX>();

	public ArrayList<GZVictorSPX> mVictors = new ArrayList<GZVictorSPX>();

	// Talons, VictorSPXs, etc.
	public ArrayList<GZSmartSpeedController> mSmartControllers = new ArrayList<GZSmartSpeedController>();

	// PWM, Sparks, Etc.
	public ArrayList<GZSpeedController> mDumbControllers = new ArrayList<GZSpeedController>();

	public ArrayList<GZSolenoid> mSingleSolenoids = new ArrayList<GZSolenoid>();
	public ArrayList<GZDoubleSolenoid> mDoubleSolenoids = new ArrayList<GZDoubleSolenoid>();

	public ArrayList<GZRPMSupplier> mRPMSuppliers = new ArrayList<GZRPMSupplier>();

	public void printState() {
		System.out.println(getStateString());
	}

	public abstract String getSmallString();

	public void addLogSmartMotors() {
		for (GZSmartSpeedController s : mSmartControllers) {
			final String name = getSmallString() + "-" + s.getGZName() + "-";

			// Amperage
			new LogItem(name + "AMP") {
				public String val() {
					return "" + s.getAmperage();
				}
			};

			GZLog.addAverageLeft(name + "AMP-AVG");

			// Temperature sensors
			if (s.hasTemperatureSensor()) {
				new LogItem(name + "TEMP") {

					public String val() {
						return String.valueOf(s.getTemperatureSensor());
					}
				};

				GZLog.addAverageLeft(name + "TEMP-AVG");
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

	public void addLogItems()
	{
		addLoggingValues();
		addLogSmartMotors();
		addLogDumbMotors();
		addLogAir();
	}

	public void addLogAir()
	{
		for (GZSolenoid s : mSingleSolenoids) {
			final String name = getSmallString() + "-" + s.getGZName() + "-";
			new LogItem(name + "STATE") {
				@Override
				public String val() {
					return "" + s.getSolenoidState();
				}
			};
		}

		for (GZDoubleSolenoid s : mDoubleSolenoids) {
			final String name = getSmallString() + "-" + s.getGZName() + "-";
			new LogItem(name + "STATE") {
				@Override
				public String val() {
					return "" + s.getValue();
				}
			};
		}
	}

	public void addLogDumbMotors() {
		for (GZSpeedController s : mDumbControllers) {
			final String name = getSmallString() + "-" + s.getGZName() + "-";
			new LogItem(name + "AMP") {

				@Override
				public String val() {
					return "" + s.getAmperage();
				}
			};

			GZLog.addAverageLeft(name + "AMP-AVG");

			if (s.hasTemperatureSensor()) {
				new LogItem(name + "TEMP") {

					public String val() {
						return String.valueOf(s.getTemperatureSensor());
					}
				};

				GZLog.addAverageLeft(name + "TEMP-AVG");
			}
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
	public boolean hasMotors() {
		return mSmartControllers.size() != 0 || mDumbControllers.size() != 0;
	}

	public boolean hasAir() {
		return mSingleSolenoids.size() != 0 || mDoubleSolenoids.size() != 0;
	}

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
		return mIsDisabled && !GZOI.getInstance().isFMS();
	}

	public abstract void addLoggingValues();

	public void superLoop() {
		for (GZSolenoid s : mSingleSolenoids)
			s.shouldForceOutputOff();

		loop();
	}

	protected abstract void loop();

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

}