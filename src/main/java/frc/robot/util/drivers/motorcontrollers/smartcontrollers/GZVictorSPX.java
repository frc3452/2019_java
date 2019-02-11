package frc.robot.util.drivers.motorcontrollers.smartcontrollers;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import frc.robot.subsystems.Health;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZPDP;
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
		subsystem.mVictors.add(this);
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

		
	public SmartController getControllerType(){
		return SmartController.VICTORSPX;
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
		if (mTemperatureSensor == null)
			return -3452.0;
		return mTemperatureSensor.getTranslatedValue();
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
		return GZPDP.getInstance().getCurrent(getPDPChannel());
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
				System.out.println("Trying to get firmware for Controller " + this.getGZName() + ": try " + counter);
			counter++;
		} while (counter <= 3 && mFirmware == -1);

		return mFirmware;
	}

	public void checkFirmware() {
		int firm = this.getFirmware();

		if (firm != GZSRX.FIRMWARE) {
			Health.getInstance().addAlert(this.mSubsystem, GZSRX.FIRMWARE_ALERT_LEVEL,
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
