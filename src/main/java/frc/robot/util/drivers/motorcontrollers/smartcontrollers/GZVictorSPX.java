package frc.robot.util.drivers.motorcontrollers.smartcontrollers;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import frc.robot.subsystems.Health;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.drivers.GZAnalogInput;
import frc.robot.util.drivers.motorcontrollers.GZSpeedController;

public class GZVictorSPX extends VictorSPX implements GZSmartSpeedController {

	// BUILDER
	public static class Builder {
		private int mDeviceNumber;
		private Breaker mBreaker = Breaker.NO_INFO;
		private int mPDPChannel;
		private String mName = "GZ";

		private GZSubsystem mSub;

		private Side mSide = Side.NO_INFO;
		private Master mMaster = Master.NO_INFO;
		private int mTempSensorPort = -1;

		public Builder(int deviceNumber, GZSubsystem subsystem, String name, int PDPChannel) {
			this.mDeviceNumber = deviceNumber;
			this.mName = name;
			this.mSub = subsystem;
			this.mPDPChannel = PDPChannel;
		}

		// public abstract double getInternalVoltage();

		public Builder overrideBreaker(Breaker b) {
			this.mBreaker = b;
			return this;
		}

		public Builder setTempSensorPort(int port) {
			this.mTempSensorPort = port;
			return this;
		}

		public Builder setSide(Side s) {
			this.mSide = s;
			return this;
		}

		public Builder setMaster() {
			if (this.mMaster == Master.NO_INFO)
				this.mMaster = Master.MASTER;
			return this;
		}

		public Builder setFollower() {
			if (this.mMaster == Master.NO_INFO)
				this.mMaster = Master.FOLLOWER;

			return this;
		}

		public GZVictorSPX build() {
			GZVictorSPX g = new GZVictorSPX(this.mDeviceNumber, this.mSub, this.mName, this.mPDPChannel, this.mBreaker,
					this.mSide, this.mMaster, this.mTempSensorPort);
			return g;
		}

	}

	public final static int TIMEOUT = 10;
	public static final int LONG_TIMEOUT = 100;
	public final static int FIRMWARE = 1024; // 778 //1025
	private final static AlertLevel mFirmwareLevel = AlertLevel.WARNING;

	private Breaker mBreaker;
	private Breaker mActualBreaker;
	private Side mSide;
	private Master mMaster;
	private String mName;
	private int mPDPChannel;
	private int mFirmware = -1;

	private GZSubsystem mSubsystem;

	private GZAnalogInput mTemperatureSensor = null;
	private int mTemperatureSensorPort;

	private boolean mLockedOut = false;

	// Constructor for builder
	private GZVictorSPX(int deviceNumber, GZSubsystem subsystem, String name, int PDPChannel, Breaker breaker,
			Side side, Master master, int temperatureSensorPort) {
		super(deviceNumber);

		this.mPDPChannel = PDPChannel;
		this.mBreaker = breaker;
		this.mSide = side;
		this.mMaster = master;
		this.mSubsystem = subsystem;
		this.mName = name;
		this.mTemperatureSensorPort = temperatureSensorPort;

		this.mActualBreaker = GZSpeedController.setBreaker(this.mPDPChannel, this);

		// Overriden
		if (breaker != Breaker.NO_INFO)
			this.mBreaker = breaker;
		else // not overriden
			this.mBreaker = this.mActualBreaker;

		if (this.mTemperatureSensorPort != -1)
			this.mTemperatureSensor = new GZAnalogInput(this.mSubsystem, this.getGZName() + "'s temperature sensor",
					this.mTemperatureSensorPort);

		if (this.mBreaker != this.mActualBreaker)
			Health.getInstance().addAlert(this.mSubsystem, AlertLevel.WARNING, "Talon " + this.getGZName()
					+ " overridden to breaker " + this.mBreaker + ", plugged into " + this.mActualBreaker);

		subsystem.mSmartControllers.add(this);
	}

	/**
	 * TO ONLY BE USED FOR TESTING
	 */
	public void set(ControlMode mode, double value, boolean overrideLockout) {
		// if locked out, only allow if overriden
		if (!mLockedOut || (mLockedOut && overrideLockout)) {
			super.set(mode, value);
		}
	}

	public void set(ControlMode mode, double demand0, DemandType demand1Type, double demand1) {
		if (!mLockedOut)
			super.set(mode, demand0, demand1Type, demand1);
	}

	@Override
	public void set(ControlMode mode, double value) {
		set(mode, value, false);
	}

	public void set(double speed, boolean overrideLockout) {
		set(ControlMode.PercentOutput, speed, overrideLockout);
	}

	public void set(double speed) {
		set(ControlMode.PercentOutput, speed, false);
	}

	public void lockOutController(boolean lockedOut) {
		mLockedOut = lockedOut;
	}

	public boolean hasTemperatureSensor() {
		return this.mTemperatureSensor != null;
	}

	public int getTemperatureSensorPort() {
		return this.mTemperatureSensorPort;
	}

	public Double getTemperatureSensor() {
		return GZUtil.readTemperatureFromAnalogInput(this.mTemperatureSensor);
	}

	public String getGZName() {
		return mName;
	}

	public Breaker getCalculatedBreaker() {
		return mActualBreaker;
	}

	public Breaker getBreaker() {
		return mBreaker;
	}

	public int getPDPChannel() {
		return this.mPDPChannel;
	}

	public Double getAmperage() {
		return this.getOutputCurrent();
	}

	public double getVoltage() {
		return this.getMotorOutputVoltage();
	}

	@Override
	public double getOutputPercentage() {
		return super.getMotorOutputPercent();
	}

	public int getPort() {
		return this.getDeviceID();
	}

	public static void logError(ErrorCode errorCode, GZSubsystem subsystem, AlertLevel level, String message) {
		if (errorCode != ErrorCode.OK)
			Health.getInstance().addAlert(subsystem, level, message);
	}

	public abstract static class TestLogError {

		private GZSubsystem subsystem;
		private AlertLevel level;
		private String message;
		private int retries;

		public TestLogError(GZSubsystem subsystem, AlertLevel level, String message, int retries) {
			this.subsystem = subsystem;
			this.level = level;
			this.message = message;
			this.retries = retries;
			test();
		}

		public TestLogError(GZSubsystem subsystem, AlertLevel level, String message) {
			this(subsystem, level, message, -1);
		}

		public abstract ErrorCode error();

		public void test() {
			boolean success = false;
			if (this.level == AlertLevel.WARNING)
				retries = 3;
			else
				retries = 6;

			for (int i = 0; i < retries && !success; i++) {
				if (error() == ErrorCode.OK)
					success = true;
			}

			if (!success)
				Health.getInstance().addAlert(this.subsystem, this.level, this.message);
		}
	}

	public int getFirmware() {
		// once we get the firmware version

		// Give it a few trys to pull the firmware, and once we get it, store it
		// (firmware be funky)
		int counter = 0;
		do {
			int firm = this.getFirmwareVersion();
			if (firm != -1)
				mFirmware = firm;

			if (counter > 0)
				System.out.println("Trying to get firmware for Talon " + this.getGZName() + ": try " + counter);
			counter++;
		} while (counter <= 3 && mFirmware == -1);

		return mFirmware;
	}

	public void checkFirmware() {
		int firm = this.getFirmware();

		if (firm != FIRMWARE) {
			Health.getInstance().addAlert(this.mSubsystem, mFirmwareLevel,
					"Talon " + this.getGZName() + " firmware is " + firm + ", should be " + FIRMWARE);
		}
	}

	public Side getSide() {
		return mSide;
	}

	public Master getMaster() {
		return mMaster;
	}
}
