package frc.robot.subsystems;

import java.text.DecimalFormat;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.Constants.kElevator;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kPDP;
import frc.robot.Constants.kSolenoids;
import frc.robot.GZOI;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZLog.LogItem;
import frc.robot.util.GZPID;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZUtil;
import frc.robot.util.drivers.GZDigitalInput;
import frc.robot.util.drivers.motorcontrollers.GZSRX;
import frc.robot.util.drivers.motorcontrollers.GZSRX.LimitSwitchDirections;
import frc.robot.util.drivers.pneumatics.GZSolenoid;
import frc.robot.util.drivers.pneumatics.GZSolenoid.SolenoidState;

public class Elevator extends GZSubsystem {

    private ElevatorState mState = ElevatorState.MANUAL;
    private ElevatorState mWantedState = ElevatorState.NEUTRAL;
    public IO mIO = new IO();

    private GZSRX mElevator1, mElevator2;
    private GZDigitalInput mCargoSensor;

    private GZSolenoid mCarriageSlide, mClaw;

    private boolean mPrevMovingHP = true;
    private boolean mMovingHP = false;

    private Intake intake = Intake.getInstance();

    private static Elevator mInstance = null;

    private double mDesiredHeight = Heights.Home.inches;
    private double mLowestHeight = Heights.Home.inches;
    private double mHighestHeight = kElevator.TOP_LIMIT;

    private boolean mLimiting = false;
    private boolean mSpeedLimitOverride = false;

    // private GZNotifier printer = new GZNotifier(() -> {
    // System.out.println("INCHES: " + getHeightInches());
    // });

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

        mCargoSensor = new GZDigitalInput(kElevator.CARGO_SENSOR_CHANNEL);
        // https://www.adafruit.com/product/2168?gclid=Cj0KCQiAwc7jBRD8ARIsAKSUBHKNOcpO8nQJBBVObqKjU71c-izo_zdezWtJPa3hWee-fSHaXIrSUJUaAql6EALw_wcB

        talonInit();

        // REMOTE LIMIT SWITCHES
        // For applications where the Talon Tach is pointing to a non-reflective surface
        // or open air (LED is on) when motor
        // movement is allowed, the Talon Tach should be treated as a NC limit switch.
        // For applications where the Talon Tach is pointing to a reflective surface
        // when motor movement is allowed (LED is
        // off), it should be treated as a NO limit switch.

        // https://www.ctr-electronics.com/downloads/pdf/Talon%20Tach%20User's%20Guide.pdf

        // ABS
        // GZSRX.logError(() ->
        // mElevator1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute,
        // 0,
        // GZSRX.LONG_TIMEOUT), this, AlertLevel.ERROR, "Could not set up encoder");
        // mElevator1.setSensorPhase(Constants.kElevator.ENC_INVERT);

        // REL
        GZSRX.logError(() -> mElevator1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0,
                GZSRX.LONG_TIMEOUT), this, AlertLevel.ERROR, "Could not set up encoder");
        mElevator1.setSensorPhase(Constants.kElevator.ENC_INVERT);

        mElevator1.setUsingRemoteLimitSwitchOnTalon(this, mElevator2, LimitSwitchNormal.NormallyClosed,
                LimitSwitchDirections.REV);

        GZSRX.logError(
                () -> mElevator1.configForwardSoftLimitThreshold(
                        (int) ((kElevator.TOP_LIMIT - kElevator.Heights.Zero.inches) * kElevator.TICKS_PER_INCH),
                        GZSRX.TIMEOUT),
                this, AlertLevel.ERROR, "Could not set top limit to " + kElevator.TOP_LIMIT + " inches from ground");

        GZSRX.logError(() -> mElevator1.configForwardSoftLimitEnable(true, GZSRX.TIMEOUT), this, AlertLevel.ERROR,
                "Could not enable top limit!");

        GZSRX.logError(
                () -> mElevator2.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
                        LimitSwitchNormal.NormallyClosed),
                this, AlertLevel.ERROR, "Could not configure reverse switch on follower controller!");

        mElevator2.disabledLimitSwitch(this, LimitSwitchDirections.FWD);

        // mElevator1.configAllowableClosedloopError(0, (int)
        // kElevator.ALLOWABLE_CLOED_LOOP_ERROR);

        mElevator1.setSensorPhase(kElevator.ENC_INVERT);

        configPID(kElevator.PID);
        // configPID(kElevator.PID2);
        selectProfileSlot(0);

        // configAccelInchesPerSec(7 * 12);
        // configCruiseInchesPerSec(11 * 12);

        configAccelInchesPerSec(kElevator.ACCEL_INCHES_PER_SECOND);
        configCruiseInchesPerSec(kElevator.VEL_INCHES_PER_SECOND);

        brake();

        mElevator1.setSelectedSensorPosition(0);
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

    public void zero() {
        setWantedState(ElevatorState.ZEROING);
    }

    @Override
    public void addLoggingValues() {
        new LogItem(getSmallString() + "-HEIGHT") {
            public String val() {
                return "" + getHeightInches();
            }
        };

        new LogItem(getSmallString() + "-DESIRED-HEIGHT") {
            public String val() {
                return "" + mDesiredHeight;
            }
        };
        new LogItem(getSmallString() + "-LWST-HEIGHT") {
            public String val() {
                return "" + mLowestHeight;
            }
        };
        new LogItem(getSmallString() + "-HIGH-HEIGHT") {
            public String val() {
                return "" + mHighestHeight;
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

    public void setHeight(double heightInInches) {
        setHeight(heightInInches, mMovingHP);
    }

    protected void setHeight(double heightInInches, boolean movingHp) {
        setWantedState(ElevatorState.MOTION_MAGIC);
        mDesiredHeight = heightInInches;
        mMovingHP = movingHp;
    }

    protected void stopMovement() {
        setHeight(getHeightInches(), mMovingHP);
    }

    protected void jogHeight(double jogHeightInches) {
        setHeight(mDesiredHeight + jogHeightInches);
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

    public boolean isMovingHP() {
        return mMovingHP;
    }

    public double getRotations() {
        return mIO.ticks_position / 4096;
    }

    public double getHeightInches() {
        return (mIO.ticks_position / kElevator.TICKS_PER_INCH) + kElevator.Heights.Zero.inches;
    }

    public boolean isCargoSensorTripped() {
        return mIO.mCargoSensorLoopCounter > kElevator.CARGO_SENSOR_LOOPS_FOR_VALID;
    }

    public void setHasHatchPanel(boolean hp) {
        this.mMovingHP = hp;
    }

    /**
     * if we want to do something with multiple pid tuning slots
     */
    private void handlePID() {
        if (mPrevMovingHP != mMovingHP)
            selectProfileSlot((mMovingHP ? ElevatorPIDConfig.HP : ElevatorPIDConfig.EMPTY));

        mPrevMovingHP = mMovingHP;
    }

    private static final DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public void loop() {
        SmartDashboard.putBoolean("Limiting", !mSpeedLimitOverride);

        // handleCoast();
        // handlePID();
        handleStates();
        in();
        out();
    }

    // MANIPULATOR
    public boolean nearTarget() {
        return nearTarget(kElevator.TARGET_TOLERANCE);
    }

    public boolean targetAbove() {
        return mDesiredHeight > getHeightInches();
    }

    public boolean nearTarget(double with_Inches_Tolerance) {
        return GZUtil.epsilonEquals(getHeightInches(), mDesiredHeight, with_Inches_Tolerance);
    }

    public boolean near(double height) {
        return GZUtil.epsilonEquals(getHeightInches(), height, kElevator.TARGET_TOLERANCE);
    }

    protected void openClaw() {
        mClaw.wantOff();
    }

    protected void closeClaw() {
        mClaw.wantOn();
    }

    public boolean slidesAtDesired() {
        return !mCarriageSlide.wantsStateChange();
    }

    protected void extendSlides() {
        mCarriageSlide.wantOn();
    }

    protected void retractSlides() {
        mCarriageSlide.wantOff();
    }

    public SolenoidState getClawState() {
        return mClaw.getSolenoidState();
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

    // GZ SUBSYSTEM STUFF
    private void handleStates() {
        boolean neutral = false;

        if (mWantedState == ElevatorState.NEUTRAL) {
            neutral = true;
        } else if (this.isSafetyDisabled()) {
            neutral = true;
        } else if (!mIO.encoders_valid && (mWantedState.usesClosedLoop || mState.usesClosedLoop)) {
            neutral = true;
        }

        if (neutral) {
            switchToState(ElevatorState.NEUTRAL);
        } else {
            switchToState(mWantedState);
        }
    }

    public boolean isLimiting() {
        return mLimiting;
    }

    public synchronized double getSpeedLimiting() {
        // Double pos = mDesiredHeight;
        Double pos = getHeightInches();

        // if not in demo and not overriding, limit
        if (!isSpeedOverriden()) {

            mLimiting = true;

            // Encoder not present or too high
            if (!mIO.encoders_valid || pos > kElevator.TOP_LIMIT || pos.isNaN()) {
                return kElevator.SPEED_LIMIT_SLOWEST_SPEED;

                // Encoder value good, limit
            } else if (pos > kElevator.SPEED_LIMIT_STARTING_INCHES) {
                return 1 - (pos / kElevator.TOP_LIMIT) + kElevator.SPEED_LIMIT_SLOWEST_SPEED;
                // Encoder value lower than limit
            } else {
                mLimiting = false;
                return 1;
            }
        } else {
            mLimiting = false;
            return 1;
        }
    }

    public boolean isSpeedOverriden() {
        return mSpeedLimitOverride;
    }

    public void toggleSpeedOverride() {
        setSpeedOverride(!isSpeedOverriden());
    }

    public void setSpeedOverride(boolean on) {
        mSpeedLimitOverride = on;
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
        if (Constants.COMP_BOT) {
            mIO.encoders_valid = mElevator1.isEncoderValid();
        } else {
            mIO.encoders_valid = true;
        }

        if (!mIO.encoders_valid)
            mIO.encoder_invalid_loops++;

        if (mIO.encoder_invalid_loops >= mIO.encoder_loop_printout) {
            System.out.println("ERROR Elevator Encoder not found!!!");
            mIO.encoder_invalid_loops = 0;
        }

        if (mCargoSensor.get())
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

    public void toggleClaw() {
        mClaw.toggleWanted();
    }

    public void toggleSlides() {
        mCarriageSlide.toggleWanted();
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
        public final double encoder_loop_printout = 20;
        public double encoder_invalid_loops = 0;

        // out
        private double output = 0;
        public Double desired_output = 0.0;

        private int mCargoSensorLoopCounter = 0;
    }

    public boolean safeForIntakeMovement() {
        // if (!mIO.encoders_valid)
        // return true;

        final double height = getHeightInches();
        if (mCarriageSlide.isOff())
            return true;

        if (intake.wantsIn())
            return true;

        // Carriage is out
        if (height > kElevator.SLIDES_MIN_HEIGHT_INTAKE_MOVING + (kElevator.SLIDES_TOLERANCE / 2.0))
            return true;
        return false;

    }

    private boolean slidesNotIn() {
        return !mCarriageSlide.isOff() || mCarriageSlide.getWantOn();
    }

    private static class ElevatorMovement {
        public final double height;
        public final boolean shouldRaise;

        public ElevatorMovement(double heightInches, boolean shouldRaise) {
            this.height = heightInches;
            this.shouldRaise = shouldRaise;
        }
    }

    private ElevatorMovement getLowestHeight() {

        final double height = getHeightInches();
        double low = kElevator.Heights.Home.inches;
        boolean raise = false;

        // needs to go up IF ✔
        // ~~~~~~~~~~~~~~~~~~~~
        // slides are out and intake wants to move
        // too low and claw needs to move
        // too low and slides need to move

        // can't go down if
        // ~~~~~~~~~~~~~~~~~~~~
        // slides are out (two different heights if intake in or out)

        if (mCarriageSlide.wantsStateChange() || !mCarriageSlide.isOff() || mClaw.getWantOn() || !mClaw.isOff()) {

            // Changed to allow intake to pull in
            if (intake.wantsOut()) {
                low = Math.max(kElevator.SLIDES_MIN_HEIGHT_INTAKE_MOVING, low);
                raise = true;
            } else if (intake.isExtended()) {
                if (isClawClosed())
                    low = Math.max(kElevator.SLIDES_MIN_HEIGHT_INTAKE_EXTENDED_CLAW_CLOSED, low);
                else
                    low = Math.max(kElevator.SLIDES_MIN_HEIGHT_INTAKE_EXTENDED_CLAW_OPEN, low);
            } else if (intake.isRetracted()) {
                if (isClawClosed())
                    low = Math.max(kElevator.SLIDES_MIN_HEIGHT_INTAKE_RETRACTED_CLAW_CLOSED, low);
                else
                    low = Math.max(kElevator.SLIDES_MIN_HEIGHT_INTAKE_RETRACTED_CLAW_OPEN, low);
            }

            if (mCarriageSlide.wantsStateChange())
                raise = true;
        }

        // if (mClaw.wantsStateChange()) {
        // if (!mCarriageSlide.isOn()) {
        // if (intake.isRetracted()) {
        // low = Math.max(kElevator.CLAW_MIN_HEIGHT_FOR_MOVE_INTAKE_IN, low);
        // } else {
        // low = Math.max(kElevator.CLAW_MIN_HEIGHT_FOR_MOVE_INTAKE_IN, low);
        // }
        // raise = true;
        // }
        // }

        return new ElevatorMovement(low, false);
        // return new ElevatorMovement(low, raise);

        // if (!mCarriageSlide.isOff() || mCarriageSlide.getWantOn()) {

        // // if our carriage wants to be extended or is extended
        // if (intake.armWantsToMove()) {
        // return kElevator.SLIDES_MIN_HEIGHT_INTAKE_MOVING;
        // } else if (intake.isRetracted()) {
        // return kElevator.SLIDES_MIN_HEIGHT_INTAKE_RETRACTED;
        // } else {
        // return kElevator.SLIDES_MIN_HEIGHT_INTAKE_EXTENDED;
        // }
        // } else {
        // return kElevator.Heights.Home.inches;
        // }
    }

    private boolean slidesSafeToMove() {

        // if (getHeightInches() > kElevator.SLIDES_MIN_HEIGHT_INTAKE_MOVING +
        // (kElevator.SLIDES_TOLERANCE / 2.0)) {
        // return true;
        // } else {
        // if (!intake.wantsToMove()
        // && getHeightInches() > getLowestHeight().height + (kElevator.SLIDES_TOLERANCE
        // / 2.0))
        // return true;
        // }
        // return false;

        return true;
        // return !intake.wantsToMove()
        // && getHeightInches() > getLowestHeight().height + (kElevator.SLIDES_TOLERANCE
        // / 2.0);
    }

    private boolean clawSafeToMove() {
        return true;
        // return !intake.wantsToMove()
        // && getHeightInches() > (mCarriageSlide.isOff() ?
        // kElevator.CLAW_MIN_HEIGHT_FOR_MOVE_INTAKE_IN
        // : kElevator.CLAW_MIN_HEIGHT_FOR_MOVE_INTAKE_OUT);
    }

    private double getLowestHeightSetpoint() {
        final ElevatorMovement val = getLowestHeight();

        if (val.shouldRaise) {
            return val.height + kElevator.SLIDES_TOLERANCE;
        }

        return val.height;
    }

    private void out() {
        if (slidesSafeToMove()) {
            mCarriageSlide.stateChange();
        }

        if (clawSafeToMove())
            mClaw.stateChange();

        mLowestHeight = getLowestHeightSetpoint();
        // System.out.println(clawSafeToMove() + "\t" + df.format(getHeightInches()));
        // System.out.println(df.format(getHeightInches()) + "\t" +
        // df.format(mDesiredHeight));
        // System.out.println(df.format(getHeightInches()) + "\t" +
        // safeForIntakeMovement());
        // System.out.println("Lowest height: " + mLowestHeight);
        // System.out.println(df.format(getHeightInches()) + "\t" +
        // df.format(mLowestHeight));
        mHighestHeight = kElevator.TOP_LIMIT;

        if (mState == ElevatorState.MOTION_MAGIC) {
            double temp = GZUtil.limit(mDesiredHeight, mLowestHeight, mHighestHeight);
            mIO.desired_output = (temp - Heights.Zero.inches) * kElevator.TICKS_PER_INCH;
        } else if (mState == ElevatorState.MANUAL) {
            if (getHeightInches() > (kElevator.TOP_LIMIT - 1)) {
                mIO.desired_output = Math.min(mIO.desired_output, 0);
            }
        } else if (mState == ElevatorState.ZEROING) {
            mIO.desired_output = -.1;
            if (this.mIO.bottom_limit_switch) {
                mElevator1.zero();
                stop();
            }
        }

        if (mState != ElevatorState.NEUTRAL) {
            mIO.output = mIO.desired_output;
        } else {
            mIO.output = 0;
        }

        mElevator1.set(mState.controlMode, mIO.output);
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
        MOTION_MAGIC(true, ControlMode.MotionMagic), ZEROING(false, ControlMode.PercentOutput);

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