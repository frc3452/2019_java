package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import frc.robot.Constants;
import frc.robot.Constants.kElevator;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kPDP;
import frc.robot.Constants.kSolenoids;
import frc.robot.GZOI;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZFlag;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZPID;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.drivers.GZAnalogInput;
import frc.robot.util.drivers.motorcontrollers.GZSRX;
import frc.robot.util.drivers.motorcontrollers.GZSRX.LimitSwitchDirections;
import frc.robot.util.drivers.pneumatics.GZSolenoid;
import frc.robot.util.drivers.pneumatics.GZSolenoid.SolenoidState;

public class Elevator extends GZSubsystem {

    private ElevatorState mState = ElevatorState.MANUAL;
    private ElevatorState mWantedState = ElevatorState.NEUTRAL;
    public IO mIO = new IO();

    private GZSRX mElevator1, mElevator2;
    private GZAnalogInput mCargoSensor;

    private GZSolenoid mCarriageSlide, mClaw;

    private boolean mPrevMovingHP = true;
    private boolean mMovingHP = false;

    private GZFlag mZeroed = new GZFlag();

    private static Elevator mInstance = null;

    public static Elevator getInstance() {
        if (mInstance == null)
            mInstance = new Elevator();

        return mInstance;
    }

    // INIT AND LIFT
    private Elevator() {
        mElevator1 = new GZSRX.Builder(kElevator.ELEVATOR_1_ID, this, "Elevator 1", kPDP.ELEVATOR_1).setMaster()
                .build();
        mElevator2 = new GZSRX.Builder(kElevator.ELEVATOR_2_ID, this, "Elevator 2", kPDP.ELEVATOR_2).setFollower()
                .build();

        mCarriageSlide = new GZSolenoid(kSolenoids.SLIDES, this, "Carriage slides");
        mClaw = new GZSolenoid(kSolenoids.CLAW, this, "Carriage claw");

        mCargoSensor = new GZAnalogInput(this, "Cargo sensor", kElevator.CARGO_SENSOR_CHANNEL,
                kElevator.CARGO_SENSOR_VOLT);

        talonInit();

        // REMOTE LIMIT SWITCHES
        // For applications where the Talon Tach is pointing to a non-reflective surface
        // or open air (LED is on) when motor
        // movement is allowed, the Talon Tach should be treated as a NC limit switch.
        // For applications where the Talon Tach is pointing to a reflective surface
        // when motor movement is allowed (LED is
        // off), it should be treated as a NO limit switch.

        // https://www.ctr-electronics.com/downloads/pdf/Talon%20Tach%20User's%20Guide.pdf

        GZSRX.logError(() -> mElevator1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0,
                GZSRX.LONG_TIMEOUT), this, AlertLevel.ERROR, "Could not set up encoder");
        mElevator1.setSensorPhase(Constants.kElevator.ENC_INVERT);

        mElevator1.setUsingRemoteLimitSwitchOnTalon(this, mElevator2, LimitSwitchNormal.NormallyClosed,
                LimitSwitchDirections.REV);

        GZSRX.logError(
                () -> mElevator1.configForwardSoftLimitThreshold(
                        (int) ((kElevator.TOP_SOFT_LIMIT_INCHES - kElevator.HOME_INCHES) * kElevator.TICKS_PER_INCH),
                        GZSRX.TIMEOUT),
                this, AlertLevel.ERROR,
                "Could not set top limit to " + kElevator.TOP_SOFT_LIMIT_INCHES + " inches from ground");

        GZSRX.logError(() -> mElevator1.configForwardSoftLimitEnable(true, GZSRX.TIMEOUT), this, AlertLevel.ERROR,
                "Could not enable top limit!");

        GZSRX.logError(
                () -> mElevator2.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
                        LimitSwitchNormal.NormallyClosed),
                this, AlertLevel.ERROR, "Could not configure reverse switch on follower controller!");

        mElevator2.disabledLimitSwitch(this, LimitSwitchDirections.FWD);

        mElevator1.setSensorPhase(kElevator.ENC_INVERT);

        configPID(kElevator.PID);
        // configPID(kElevator.PID2);
        selectProfileSlot(0);
        configAccelInchesPerSec(7 * 12);
        configCruiseInchesPerSec(11 * 12);

        brake();
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

    public void brake() {
        for (GZSRX s : mTalons)
            s.setNeutralMode(NeutralMode.Brake);
    }

    public void coast() {
        for (GZSRX s : mTalons)
            s.setNeutralMode(NeutralMode.Coast);
    }

    private void talonInit() {
        for (GZSRX s : mTalons) {

            GZSRX.logError(() -> s.configFactoryDefault(GZSRX.LONG_TIMEOUT), this, AlertLevel.ERROR,
                    "Could not factory reset " + s.getGZName());

            GZSRX.logError(s.configOpenloopRamp(Constants.kElevator.OPEN_LOOP_RAMP_TIME, GZSRX.TIMEOUT), this,
                    AlertLevel.WARNING, "Could not set open loop ramp time for " + s.getGZName());

            // s.configVoltageCompSaturation(12);
            // s.enableVoltageCompensation(true);

            s.setInverted(kElevator.ELEVATOR_INVERT);

            GZSRX.logError(s.configContinuousCurrentLimit(kElevator.AMP_CONTINUOUS, GZSRX.TIMEOUT), this,
                    AlertLevel.WARNING, "Could not set current-limit continuous current limit for " + s.getGZName());

            GZSRX.logError(s.configPeakCurrentLimit(Constants.kElevator.AMP_PEAK, GZSRX.TIMEOUT), this,
                    AlertLevel.WARNING, "Could not set current-limit peak for " + s.getGZName());

            GZSRX.logError(s.configPeakCurrentDuration(Constants.kElevator.AMP_TIME, GZSRX.TIMEOUT), this,
                    AlertLevel.WARNING, "Could not set current-limit duration for " + s.getGZName());

            s.enableCurrentLimit(true);

            s.setSubsystem("Elevator");
        }
    }

    @Override
    public void stop() {
        setWantedState(ElevatorState.NEUTRAL);
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
    }

    protected void setHeight(Heights height) {
        setHeight(height.inches, height.moving_hp);
    }

    protected void goHome() {
        setHeight(Heights.Home);
    }

    protected void setHeight(double heightInInches) {
        setHeight(heightInInches, false);
    }

    protected void setHeight(double heightInInches, boolean movingHp) {
        setWantedState(ElevatorState.MOTION_MAGIC);
        mIO.desired_output = kElevator.TICKS_PER_INCH * (heightInInches - kElevator.HOME_INCHES);
        mMovingHP = movingHp;
    }

    protected void stopMovement() {
        setHeight(getHeightInches(), mMovingHP);
    }

    protected void jogHeight(double jogHeightInches) {
        setHeight(getHeightInches() + jogHeightInches);
    }

    public void manual(double speedPercent) {
        if (setWantedState(ElevatorState.MANUAL)) {
            mIO.desired_output = speedPercent;
        }
    }

    private void configAccelInchesPerSec(double inchesPerSecond) {
        configAccel(inchesPerSecondToNativeUnits(inchesPerSecond));
    }

    private void configCruiseInchesPerSec(double inchesPerSecond) {
        configCruise(inchesPerSecondToNativeUnits(inchesPerSecond));
    }

    private int inchesPerSecondToNativeUnits(double inchesPerSecond) {
        int sensorUnitsPer100ms;
        sensorUnitsPer100ms = (int) Math.rint((inchesPerSecond * kElevator.TICKS_PER_INCH) / 10);
        return sensorUnitsPer100ms;
    }

    private double getInchesPerSecond() {
        return nativeUnitsToInchesPerSecond(mIO.ticks_velocity);
    }

    private double nativeUnitsToInchesPerSecond(double nativeUnits) {
        double inchesPerSecond;
        inchesPerSecond = ((double) nativeUnits / (double) kElevator.TICKS_PER_INCH) * 10;
        return inchesPerSecond;
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
        return (mIO.ticks_position / kElevator.TICKS_PER_INCH) + kElevator.HOME_INCHES;
    }

    public boolean isCargoSensorTripped() {
        return mIO.mCargoSensorLoopCounter > kElevator.CARGO_SENSOR_LOOPS_FOR_VALID;
    }

    /**
     * if we want to do something with multiple pid tuning slots
     */
    private void handlePID() {
        if (mPrevMovingHP != mMovingHP)
            selectProfileSlot((mMovingHP ? ElevatorPIDConfig.HP : ElevatorPIDConfig.EMPTY));

        mPrevMovingHP = mMovingHP;
    }

    @Override
    public void loop() {
        // handleCoast();
        handlePID();
        handleStates();
        in();
        out();
    }

    // MANIPULATOR
    public boolean nearTarget() {
        return nearTarget(kElevator.TARGET_TOLERANCE);
    }

    public boolean nearTarget(double with_Inches_Tolerance) {
        double tar = mElevator1.getClosedLoopTarget();
        tar /= kElevator.TICKS_PER_INCH;

        return GZUtil.epsilonEquals(getHeightInches(), tar, with_Inches_Tolerance);
    }

    protected void openClaw() {
        mClaw.set(false);
    }

    protected void closeClaw() {
        mClaw.set(true);
    }

    protected void extendSlides() {
        mCarriageSlide.set(true);
    }

    protected void retractSlides() {
        mCarriageSlide.set(false);
    }

    public boolean isClawClosed() {
        return mClaw.getSolenoidState() == SolenoidState.ON;
    }

    public boolean isClawOpen() {
        return mClaw.getSolenoidState() == SolenoidState.OFF;
    }

    public boolean areSlidesOut() {
        return mCarriageSlide.getSolenoidState() == SolenoidState.ON;
    }

    public boolean areSlidesIn() {
        return mCarriageSlide.getSolenoidState() == SolenoidState.OFF;
    }

    public SolenoidState getSlidesState() {
        return mCarriageSlide.getSolenoidState();
    }

    private void handleCoast() {
        if (GZOI.getInstance().isDisabled() && !GZOI.getInstance().isFMS()) {
            System.out.println(getInchesPerSecond());
            if (getInchesPerSecond() > 1)
                coast();
            return;
        }
        brake();
    }

    private void handleNonZero() {
        switchToState(ElevatorState.MANUAL);
        mIO.desired_output = -.15;
        if (this.mIO.bottom_limit_switch) {
            mElevator1.zero();
            this.mZeroed.tripFlag();
            // stop();
        }
    }

    // GZ SUBSYSTEM STUFF
    private void handleStates() {
        boolean neutral = false;

        if (mWantedState == ElevatorState.NEUTRAL) {
            neutral = true;
        } else if (this.isSafetyDisabled()) {
            neutral = true;
        } else if (!mZeroed.get()) {
            handleNonZero();
        } else if (!mIO.encoders_valid && (mWantedState.usesClosedLoop || mState.usesClosedLoop)) {
            neutral = true;
        }

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

        if (mCargoSensor.isTripped())
            mIO.mCargoSensorLoopCounter++;
        else
            mIO.mCargoSensorLoopCounter = 0;

        if (mIO.encoders_valid) {
            mIO.ticks_position = (double) mElevator1.getSelectedSensorPosition();
            mIO.ticks_velocity = (double) mElevator1.getSelectedSensorVelocity();
        } else {
            mIO.ticks_position = Double.NaN;
            mIO.ticks_velocity = Double.NaN;
        }

        mIO.bottom_limit_switch = !mElevator1.getREVLimit();

        mIO.elevator_total_rotations = mElevator1.getTotalEncoderRotations(getRotations());
    }

    public boolean getBottomLimit() {
        return mIO.bottom_limit_switch;
    }

    public int getSlidesTotalCounts() {
        return mCarriageSlide.getChangeCounts();
    }

    public int getClawTotalCounts() {
        return mClaw.getChangeCounts();
    }

    public class IO {
        public Double elevator_total_rotations = 0.0;
        // In
        public Double ticks_velocity = Double.NaN;
        public Double ticks_position = Double.NaN;

        public Boolean bottom_limit_switch = false;

        public Boolean encoders_valid = false;

        // out
        private double output = 0;
        public Double desired_output = 0.0;

        private int mCargoSensorLoopCounter = 0;
    }

    private void out() {
        if (mState != ElevatorState.NEUTRAL) {
            mIO.output = mIO.desired_output;
        } else {
            mIO.output = 0;
        }

        ControlMode mode = (mZeroed.get() ? mState.controlMode : ControlMode.PercentOutput);
        mElevator1.set(mode, mIO.output);
        System.out.println(mode + "\t" + mIO.output);
    }

    public synchronized void enableFollower() {
        for (GZSRX s : mTalons)
            s.follow(mElevator1);
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
        EMPTY(0), HP(1);

        private int slot;

        private ElevatorPIDConfig(int slot) {
            this.slot = slot;
        }
    }

}