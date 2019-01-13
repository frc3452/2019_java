package frc.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import frc.robot.Constants;
import frc.robot.Constants.kElevator;
import frc.robot.Constants.kPDP;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.GZOI;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZPID;
import frc.robot.util.GZSubsystem;
import frc.robot.util.drivers.GZAnalogInput;
import frc.robot.util.drivers.motorcontrollers.smartcontrollers.GZSRX;
import frc.robot.util.drivers.pneumatics.GZSolenoid;
import frc.robot.util.drivers.pneumatics.GZSolenoid.SolenoidState;

public class Elevator extends GZSubsystem {

    private ElevatorState mState = ElevatorState.MANUAL;
    private ElevatorState mWantedState = ElevatorState.NEUTRAL;
    public IO mIO = new IO();

    private GZSRX mElevator1;
    private GZAnalogInput mCargoSensor;

    private GZSolenoid mCarriageSlide, mClaw;

    private ElevatorPIDConfig mCurrentMotionMode = ElevatorPIDConfig.EMPTY;
    private ElevatorPIDConfig mPreviousMotionMode = ElevatorPIDConfig.CARGO;

    private static Elevator mInstance = null;

    public static Elevator getInstance() {
        if (mInstance == null)
            mInstance = new Elevator();

        return mInstance;
    }

    //INIT AND LIFT
    private Elevator() {
        mElevator1 = new GZSRX.Builder(kElevator.ELEVATOR_MOTOR_ID, this, "Elevator 1", kPDP.ELEVATOR_MOTOR).build();
        
        mCarriageSlide = new GZSolenoid(kElevator.SLIDES, this, "Carriage slides");
        mCarriageSlide.set(false);
        mClaw = new GZSolenoid(kElevator.CLAW, this, "Carriage claw");
        mClaw.set(false);

        mCargoSensor = new GZAnalogInput(kElevator.CARGO_SENSOR_CHANNEL, kElevator.CARGO_SENSOR_LOW_VOLT,
                kElevator.CARGO_SENSOR_HIGH_VOLT);
        

        talonInit();

        mElevator1.setInverted(Constants.kElevator.E_1_INVERT);

        GZSRX.logError(
                mElevator1.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
                        LimitSwitchNormal.NormallyOpen),
                this, AlertLevel.WARNING, "Could not configure forward limit switch source");
        GZSRX.logError(
                mElevator1.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
                        LimitSwitchNormal.NormallyOpen),
                this, AlertLevel.WARNING, "Could not configure reverse limit switch source");

        GZSRX.logError(mElevator1.configOpenloopRamp(Constants.kElevator.OPEN_RAMP_TIME, GZSRX.TIMEOUT), this,
                AlertLevel.WARNING, "Could not set open loop ramp time");
        GZSRX.logError(mElevator1.configClearPositionOnLimitF(true, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
                "Could not set encoder zero on bottom limit");

        configPID(kElevator.PID);
        configPID(kElevator.PID2);
        selectProfileSlot(ElevatorPIDConfig.EMPTY);
        configAccelInchesPerSec(6 * 12 * .5); //3 fps 
        configCruiseInchesPerSec(6 * 12);  //6 fps
    }

 

    private void selectProfileSlot(ElevatorPIDConfig e) {
        selectProfileSlot(e.slot);
    }

    private void selectProfileSlot(int slot) {
        mElevator1.selectProfileSlot(slot, 0);
    }

    private void configPID(GZPID gains) {
        GZSRX.logError(mElevator1.config_kF(gains.parameterSlot, gains.F, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
                "Could not set 'F' Gain for slot" + gains.parameterSlot);

        GZSRX.logError(mElevator1.config_kP(gains.parameterSlot, gains.P, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
                "Could not set 'P' Gain for slot" + gains.parameterSlot);

        GZSRX.logError(mElevator1.config_kI(gains.parameterSlot, gains.I, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
                "Could not set 'I' Gain for slot" + gains.parameterSlot);
        GZSRX.logError(mElevator1.config_kD(gains.parameterSlot, gains.D, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
                "Could not set 'D' Gain for slot" + gains.parameterSlot);

        GZSRX.logError(mElevator1.config_IntegralZone(gains.parameterSlot, gains.iZone, GZSRX.TIMEOUT), this,
                AlertLevel.WARNING, "Could not set 'iZone' Gain for slot" + gains.parameterSlot);
    }

    private void talonInit() {
        for (GZSRX s : mTalons) {

            new GZSRX.TestLogError(this, AlertLevel.ERROR, "Could not factory reset " + s.getGZName()) {
                @Override
                public ErrorCode error() {
                    return s.configFactoryDefault(GZSRX.LONG_TIMEOUT);
                }
            };

            s.setNeutralMode(NeutralMode.Brake);

            s.enableVoltageCompensation(true);

            GZSRX.logError(s.configContinuousCurrentLimit(kElevator.AMP_CONTINUOUS, GZSRX.TIMEOUT), this,
                    AlertLevel.WARNING, "Could not set current-limit continuous current limit for " + s.getGZName());

            GZSRX.logError(s.configPeakCurrentLimit(Constants.kElevator.AMP_PEAK, GZSRX.TIMEOUT), this,
                    AlertLevel.WARNING, "Could not set current-limit peak for " + s.getGZName());

            GZSRX.logError(s.configPeakCurrentDuration(Constants.kElevator.AMP_TIME, GZSRX.TIMEOUT), this,
                    AlertLevel.WARNING, "Could not set current-limit duration for " + s.getGZName());

            new GZSRX.TestLogError(this, AlertLevel.ERROR, "Could not set up encoder") {
                public ErrorCode error() {
                    return mElevator1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0,
                            GZSRX.LONG_TIMEOUT);
                }
            };

            mElevator1.setSensorPhase(Constants.kElevator.ENC_INVERT);

            s.enableCurrentLimit(true);

            s.setSubsystem("Elevator");
        }
    }

    @Override
    public void stop() {
        setWantedState(ElevatorState.NEUTRAL);
    }

    @Override
    public boolean hasMotors() {
        return true;
    }

    @Override
    public boolean hasAir() {
        return true;
    }

    @Override
    public void addLoggingValues() {
        new LogItem(getSmallString() + "-HEIGHT") {
            public String val() {
                return "" + getHeightInches();
            }
        };

        new LogItem(getSmallString() + "-ENC-VALID") {
            public String val() {
                return "" + mIO.encoders_valid;
            }

        };

        this.addLoggingValuesTalons();
    }

    protected void goHome()
    {
        setHeight(Heights.Home);
    }

    protected void setHeight(Heights height)
    {
        setHeight(height.inches);
    }

    protected void setHeight(double heightInInches) {
        setWantedState(ElevatorState.MOTION_MAGIC);
        mIO.desired_output = 4096 * kElevator.TICKS_PER_INCH * heightInInches;
    }

    protected void jogHeight(double jogHeightInches) {
        setHeight(getHeightInches() + jogHeightInches);
    }

    protected void setRotations(double rotations)
    {
        setWantedState(ElevatorState.MOTION_MAGIC);
        mIO.desired_output = 4096 * rotations;
    }

    private void configAccelInchesPerSec(double inchesPerSecond) {
        configAccel(inchesPerSecondToNativeUnits(inchesPerSecond));
    }

    private void configCruiseInchesPerSec(double inchesPerSecond) {
        configCruise(inchesPerSecondToNativeUnits(inchesPerSecond));
    }

    private int inchesPerSecondToNativeUnits(double inchesPerSecond) {
        int sensorUnitsPer100ms;
        sensorUnitsPer100ms = (int) Math.rint(inchesPerSecond * kElevator.TICKS_PER_INCH);
        return sensorUnitsPer100ms;
    }

    private void configAccel(int sensorUnitsPer100msPerSec) {
        mElevator1.configMotionAcceleration(sensorUnitsPer100msPerSec);
    }

    private void configCruise(int sensorUnitsPer100msPerSec) {
        mElevator1.configMotionCruiseVelocity(sensorUnitsPer100msPerSec);
    }

    public double getRotations() {
        return mIO.ticks_position / 4096;
    }

    public double getHeightInches() {
        return mIO.ticks_position / kElevator.TICKS_PER_INCH;
    }

    public boolean isCargoSensorTripped() {
        return this.mCargoSensor.isWithinRange();
    }

    public boolean isHome() {
        return getHeightInches() < 3;
    }

    /**
     * if we want to do something with multiple pid tuning slots
     */
    private void handlePID() {
        // if (cargoSensorTripped() && isHome())
        // {

        // }

        if (mCurrentMotionMode != mPreviousMotionMode) {
            mCurrentMotionMode = ElevatorPIDConfig.EMPTY;
            selectProfileSlot(mCurrentMotionMode);
            mPreviousMotionMode = mCurrentMotionMode;
        }
    }

    @Override
    public void loop() {
        handlePID();
        handleStates();
        in();
        out();
    }


    // MANIPULATOR
    protected void setClaw(boolean clamp)
    {
        mClaw.set(clamp);
    }

    protected void setSlides(boolean extended)
    {
        mCarriageSlide.set(extended);
    }

    public boolean isClawClamped()
    {
        return mClaw.getSolenoidState() == SolenoidState.EXTENDED; 
    }

    public boolean isClawOpen()
    {
        return mClaw.getSolenoidState() == SolenoidState.RETRACTED;
    }

    public SolenoidState getClawState()
    {
        return mClaw.getSolenoidState();
    }

    public boolean isSlidesExtended()
    {
        return mCarriageSlide.getSolenoidState() == SolenoidState.EXTENDED;
    }

    public boolean isSlidesRetracted()
    {
        return mCarriageSlide.getSolenoidState() == SolenoidState.RETRACTED;
    }

    public SolenoidState getSlidesState()
    {
        return mCarriageSlide.getSolenoidState();
    }

    //GZ SUBSYSTEM STUFF
    private void handleStates() {
        boolean neutral = false;

        boolean lockSolenoids = false;

        if (mWantedState == ElevatorState.NEUTRAL) {
            neutral = true;
        } else if (this.isSafetyDisabled() && !GZOI.getInstance().isFMS()) {
            neutral = true;
            lockSolenoids = true;
        } else if (!mIO.encoders_valid && (mWantedState.usesClosedLoop || mState.usesClosedLoop)) {
            neutral = true;
        }

        this.lockSolenoids(lockSolenoids);

        if (neutral) {
            switchToState(ElevatorState.NEUTRAL);
        } else {
            switchToState(mWantedState);
        }
    }

    private void switchToState(ElevatorState s) {
        if (mState != s) {
            onStateExit(mState);
            mState = s;
            onStateStart(mState);
        }
    }

    private void onStateStart(ElevatorState s) {
        switch (s) {
        case MANUAL:
            break;
        case NEUTRAL:
            break;
        default:
            break;
        }
    }

    private void onStateExit(ElevatorState s) {
        switch (s) {
        case MANUAL:
            break;
        case NEUTRAL:
            break;
        default:
            break;
        }
    }

    private void in() {
        mIO.encoders_valid = mElevator1.isEncoderValid();

        if (mIO.encoders_valid) {
            mIO.ticks_position = (double) mElevator1.getSelectedSensorPosition();
            mIO.ticks_velocity = (double) mElevator1.getSelectedSensorVelocity();
        } else {
            mIO.ticks_position = Double.NaN;
            mIO.ticks_velocity = Double.NaN;
        }

        mIO.elevator_total_rotations = mElevator1.getTotalEncoderRotations(getRotations());
        // mElevator1.getTotalEncoderRotations(currentRotationValue)
    }

    public boolean getTopLimit() {
        return mIO.fwd_limit_switch; // TODO TUNE
    }

    public boolean getBottomLimit() {
        return mIO.rev_limit_switch; // TODO TUNE
    }

    public class IO {
        public Object elevator_total_rotations;
        // In
        public Double ticks_velocity = Double.NaN;
        public Double ticks_position = Double.NaN;

        public Boolean fwd_limit_switch = false;
        public Boolean rev_limit_switch = false;

        public Boolean encoders_valid = false;

        // out
        private double output = 0;
        public Double desired_output = 0.0;
    }

    private void out() {
        if (mState != ElevatorState.NEUTRAL) {
            mIO.output = mIO.desired_output;
        } else {
            mIO.output = 0;
        }

        mElevator1.set(mState.controlMode, mIO.output);
    }

    public synchronized void enableFollower() {
        // controller_2.follow(controller_1);
    }

    @Override
    protected void initDefaultCommand() {
    }

    public ElevatorState getState() {
        return mState;
    }

    @Override
    public String getStateString() {
        return mState.toString();
    }

    protected boolean setWantedState(ElevatorState wantedState) {
        this.mWantedState = wantedState;

        return this.mWantedState == mState;
    }

    public enum ElevatorState {
        NEUTRAL(false, ControlMode.Disabled), MANUAL(false, ControlMode.PercentOutput),
        MOTION_MAGIC(true, ControlMode.MotionMagic);

        public final boolean usesClosedLoop;
        public final ControlMode controlMode;

        private ElevatorState(boolean closed, ControlMode controlMode) {
            this.usesClosedLoop = closed;
            this.controlMode = controlMode;
        }
    }

    @Override
    public String getSmallString() {
        return "ELV";
    }

    private enum ElevatorPIDConfig {
        EMPTY(0), CARGO(1);

        private int slot;

        private ElevatorPIDConfig(int slot) {
            this.slot = slot;
        }
    }

}