package frc.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import frc.robot.Constants;
import frc.robot.Constants.kElevator;
import frc.robot.Constants.kPDP;
import frc.robot.GZOI;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZPID;
import frc.robot.util.GZSubsystem;
import frc.robot.util.drivers.GZSRX;

public class Elevator extends GZSubsystem {

    private ElevatorState mState = ElevatorState.MANUAL;
    private ElevatorState mWantedState = ElevatorState.NEUTRAL;
    public IO mIO = new IO();
    private GZSRX elevator_1;

    private static Elevator mInstance = null;

    public static Elevator getInstance() {
        if (mInstance == null)
            mInstance = new Elevator();

        return mInstance;
    }

    private Elevator() {
        elevator_1 = new GZSRX.Builder(kElevator.ELEVATOR_MOTOR_ID, this, "Elevator_Motor", kPDP.ELEVATOR_MOTOR)
                .build();

        talonInit();

        elevator_1.setInverted(Constants.kElevator.E_1_INVERT);

        new GZSRX.TestLogError(this, AlertLevel.WARNING, "Could not set open loop ramp time") {
            @Override
            public ErrorCode error() {
                return elevator_1.configOpenloopRamp(Constants.kElevator.OPEN_RAMP_TIME, GZSRX.TIMEOUT);
            }
        };

        new GZSRX.TestLogError(this, AlertLevel.WARNING, "Could not set encoder zero on bottom limit") {
            public ErrorCode error() {
                return elevator_1.configClearPositionOnLimitF(true, GZSRX.TIMEOUT);
            }
        };

        configPID(kElevator.PID);
        configPID(kElevator.PID2);

    }

    private void configPID(GZPID gains) {
        new GZSRX.TestLogError(this, AlertLevel.WARNING, "Could not set 'F' Gain for slot" + gains.parameterSlot) {
            @Override
            public ErrorCode error() {
                return elevator_1.config_kF(gains.parameterSlot, gains.F, GZSRX.TIMEOUT);
            }
        };

        new GZSRX.TestLogError(this, AlertLevel.WARNING, "Could not set 'P' Gain for slot" + gains.parameterSlot) {
            @Override
            public ErrorCode error() {
                return elevator_1.config_kP(gains.parameterSlot, gains.P, GZSRX.TIMEOUT);
            }
        };

        new GZSRX.TestLogError(this, AlertLevel.WARNING, "Could not set 'I' Gain for slot" + gains.parameterSlot) {
            @Override
            public ErrorCode error() {
                return elevator_1.config_kI(gains.parameterSlot, gains.I, GZSRX.TIMEOUT);
            }
        };

        new GZSRX.TestLogError(this, AlertLevel.WARNING, "Could not set 'D' Gain for slot" + gains.parameterSlot) {
            @Override
            public ErrorCode error() {
                return elevator_1.config_kD(gains.parameterSlot, gains.D, GZSRX.TIMEOUT);
            }
        };

        new GZSRX.TestLogError(this, AlertLevel.WARNING, "Could not set 'iZone' Gain for slot" + gains.parameterSlot) {
            @Override
            public ErrorCode error() {
                return elevator_1.config_IntegralZone(gains.parameterSlot, gains.iZone, GZSRX.TIMEOUT);
            }
        };

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
                    return elevator_1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0,
                            GZSRX.LONG_TIMEOUT);
                }
            };

            elevator_1.setSensorPhase(Constants.kElevator.ENC_INVERT);

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
        return false;
    }

    @Override
    public void addLoggingValues() {

    }

    @Override
    public void loop() {
        handleStates();
        in();
        out();
    }

    private void handleStates() {
        boolean neutral = false;

        if (mWantedState == ElevatorState.NEUTRAL) {
            neutral = true;
        }

        else if (this.isSafetyDisabled() && !GZOI.getInstance().isFMS()) {
            neutral = true;
        }

        else if (!mIO.encoders_valid && (mWantedState.usesClosedLoop || mState.usesClosedLoop)) {
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
        mIO.encoders_valid = elevator_1.isEncoderValid();

        if (mIO.encoders_valid) {
            mIO.ticks_position = (double) elevator_1.getSelectedSensorPosition();
            mIO.ticks_velocity = (double) elevator_1.getSelectedSensorVelocity();
        } else {
            mIO.ticks_position = Double.NaN;
            mIO.ticks_velocity = Double.NaN;
        }
    }

    public class IO {
        // In
        public Double ticks_velocity = Double.NaN;
        public Double ticks_position = Double.NaN;

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

        elevator_1.set(mState.controlMode, mIO.output);
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

    public boolean setWantedState(ElevatorState wantedState) {
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

}