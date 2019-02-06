package frc.robot.util.drivers.motorcontrollers.smartcontrollers;

import java.util.function.Supplier;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import frc.robot.subsystems.Health;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.drivers.GZAnalogInput;
import frc.robot.util.drivers.motorcontrollers.GZSpeedController;

public class GZSRX extends WPI_TalonSRX implements GZSmartSpeedController {

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

		public GZSRX build() {
			GZSRX g = new GZSRX(this.mDeviceNumber, this.mSub, this.mName, this.mPDPChannel, this.mBreaker, this.mSide,
					this.mMaster, this.mTempSensorPort);
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

	private double mTotalEncoderRotations = 0;
	private double mPrevEncoderRotations = 0;

	private GZAnalogInput mTemperatureSensor = null;
	private int mTemperatureSensorPort;

	private boolean mLockedOut = false;

	private GZSRX mRemoteEncoderTalon = null;
	private GZSRX mRemoteLimitSwitchTalon = null;

	// Constructor for builder
	private GZSRX(int deviceNumber, GZSubsystem subsystem, String name, int PDPChannel, Breaker breaker, Side side,
			Master master, int temperatureSensorPort) {
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

		subsystem.mTalons.add(this);
		subsystem.mSmartControllers.add(this);
	}

	public SmartController getControllerType() {
		return SmartController.TALON;
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

	/**
	 * Only valid if called as fast as possible
	 */
	public double getTotalEncoderRotations(Double currentRotationValue) {
		if (currentRotationValue.isNaN())
			return -3452;

		double change = Math.abs(currentRotationValue - mPrevEncoderRotations);
		mTotalEncoderRotations += change;
		mPrevEncoderRotations = currentRotationValue;
		return mTotalEncoderRotations;
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

	@Override
	public Side getSide() {
		return mSide;
	}

	@Override
	public Master getMaster() {
		return mMaster;
	}

	@Override
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

	private boolean encoderPresent() {
		return this.getSensorCollection().getPulseWidthRiseToRiseUs() != 0;
	}

	public void setUsingRemoteLimitSwitchOnTalon(GZSubsystem sub, GZSRX t, LimitSwitchNormal normal) {
		mRemoteEncoderTalon = t;

		logError(
				() -> this.configForwardLimitSwitchSource(RemoteLimitSwitchSource.RemoteTalonSRX, normal,
						mRemoteEncoderTalon.getPort()),
				sub, AlertLevel.ERROR, "Could not configure forward limit switch source for Talon " + this.getGZName());

		logError(() -> 
				this.configReverseLimitSwitchSource(RemoteLimitSwitchSource.RemoteTalonSRX, normal,
						mRemoteEncoderTalon.getPort()),
				sub, AlertLevel.ERROR, "Could not configure reverse limit switch source for Talon " + this.getGZName());
	}

	public void setUsingRemoteEncoderOnTalon(GZSubsystem sub, GZSRX t) {
		setUsingRemoteEncoderOnTalon(sub, t, 0);
	}

	public String getRemoteSensorName() {
		if (usingRemoteEncoder())
			return mRemoteEncoderTalon.getGZName();

		return "N/A";
	}

	public void setUsingRemoteEncoderOnTalon(GZSubsystem sub, GZSRX t, int pidSlot) {
		mRemoteEncoderTalon = t;
		logError(
				() -> this.configRemoteFeedbackFilter(mRemoteEncoderTalon.getDeviceID(),
						RemoteSensorSource.TalonSRX_SelectedSensor, 0, TIMEOUT),
				sub, AlertLevel.ERROR, "Could not configure " + this.getGZName() + "'s remote sensor feedback filter!");
		logError(() -> this.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor0, pidSlot, TIMEOUT), sub,
				AlertLevel.ERROR, "Could not select " + this.getGZName() + "'s remote sensor!");
	}

	public boolean usingRemoteEncoder() {
		return mRemoteEncoderTalon != null;
	}

	public boolean isEncoderValid() {
		if (usingRemoteEncoder()) {
			return mRemoteEncoderTalon.encoderPresent();
		}

		return this.encoderPresent();
	}

	private void zeroSensor() {
		this.setSelectedSensorPosition(0);
	}

	public void zero() {
		if (usingRemoteEncoder())
			mRemoteEncoderTalon.zeroSensor();
		else
			this.zeroSensor();
	}

	public int getPort() {
		return this.getDeviceID();
	}

	public static void logError(ErrorCode errorCode, GZSubsystem subsystem, AlertLevel level, String message) {
		if (errorCode != ErrorCode.OK)
			Health.getInstance().addAlert(subsystem, level, message);
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
			Health.getInstance().addAlert(this.mSubsystem, GZSmartSpeedController.FIRMWARE_ALERT_LEVEL,
					"Talon " + this.getGZName() + " firmware is " + firm + ", should be " + FIRMWARE);
		}
	}

	public static boolean logError(Supplier<ErrorCode> errorCode, GZSubsystem subsystem, AlertLevel level,
			String message, int retries) {
		boolean success = false;
		if (level == AlertLevel.WARNING)
			retries = 3;
		else
			retries = 6;

		for (int i = 0; i < retries && !success; i++) {
			if (errorCode.get() == ErrorCode.OK)
				success = true;
		}

		if (!success)
			Health.getInstance().addAlert(subsystem, level, message);

		return success;
	}

	public static boolean logError(Supplier<ErrorCode> errorCode, GZSubsystem subsystem, AlertLevel level,
			String message) {
		return logError(errorCode, subsystem, level, message, 0);
	}

}
