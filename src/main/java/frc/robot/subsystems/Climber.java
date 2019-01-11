package frc.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import frc.robot.Constants;
import frc.robot.Constants.kClimber;
import frc.robot.Constants.kPDP;
import frc.robot.GZOI;
import frc.robot.subsystems.Health.AlertLevel;
import frc.robot.util.GZSubsystem;
import frc.robot.util.drivers.GZSRX;
import frc.robot.util.drivers.GZSolenoid;


public class Climber extends GZSubsystem {

    private ClimberState mState = ClimberState.MANUAL;
    private ClimberState mWantedState = ClimberState.NEUTRAL;
    public IO mIO = new IO();


    private GZSRX mClimberFront, mClimberBack;
    private GZSolenoid mRampDrop;

    private static Climber mInstance = null;

    public static Climber getInstance() {
        if (mInstance == null)
            mInstance = new Climber();

        return mInstance;
    }

    private Climber() {
        mClimberFront = new GZSRX.Builder(kClimber.FRONT_MOTOR_ID, this, "Climber Front", kPDP.CLIMBER_FRONT).build();
        mClimberBack = new GZSRX.Builder(kClimber.BACK_MOTOR_ID, this, "Climber Back", kPDP.CLIMBER_FRONT).build();
        mRampDrop = new GZSolenoid(kClimber.RAMP_DROP_SOLENOID_CHANNEL, this, "Ramp Drop");

        talonInit();

        mClimberFront.setInverted(kClimber.CLIMBER_FRONT_INVERT);
        mClimberFront.setInverted(kClimber.CLIMBER_BACK_INVERT);

    }

    private void talonInit() {
        for (GZSRX s : mTalons) {

            new GZSRX.TestLogError(this, AlertLevel.ERROR, "Could not factory reset " + s.getGZName()) {
                @Override
                public ErrorCode error() {
                    return s.configFactoryDefault(GZSRX.LONG_TIMEOUT);
                }
            };

            GZSRX.logError(mClimberFront.configOpenloopRamp(kClimber.OPEN_RAMP_TIME, GZSRX.TIMEOUT), this,
                    AlertLevel.WARNING, "Could not set open loop ramp time for " + s.getGZName());

            s.setNeutralMode(NeutralMode.Brake);

            s.enableVoltageCompensation(true);

            GZSRX.logError(s.configContinuousCurrentLimit(kClimber.AMP_CONTINUOUS, GZSRX.TIMEOUT), this,
                    AlertLevel.WARNING, "Could not set current-limit continuous current limit for " + s.getGZName());

            GZSRX.logError(s.configPeakCurrentLimit(kClimber.AMP_PEAK, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
                    "Could not set current-limit peak for " + s.getGZName());

            GZSRX.logError(s.configPeakCurrentDuration(kClimber.AMP_TIME, GZSRX.TIMEOUT), this, AlertLevel.WARNING,
                    "Could not set current-limit duration for " + s.getGZName());

            s.enableCurrentLimit(true);

            s.setSubsystem("Climber");
        }
    }

    @Override
    public void stop() {
        setWantedState(ClimberState.NEUTRAL);
    }

    public void rampsDrop(){
        mRampDrop.set(true);
    };

    public void runClimber(double Frontspeed, double Backspeed){
        if(setWantedState(ClimberState.MANUAL)){
            mIO.front_desired_output = Frontspeed;
            mIO.back_desired_output = Backspeed;
     }
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
    }

    @Override
    public void loop() {
        handleStates();
        in();
        out();
    }

    private void handleStates() {
        boolean neutral = false;

        if (mWantedState == ClimberState.NEUTRAL) {
            neutral = true;
        }

        else if (this.isSafetyDisabled() && !GZOI.getInstance().isFMS()) {
            neutral = true;
        }

        if (neutral) {

            switchToState(ClimberState.NEUTRAL);

        } else {
            switchToState(mWantedState);
        }
    }

    private void switchToState(ClimberState s) {
        if (mState != s) {
            onStateExit(mState);
            mState = s;
            onStateStart(mState);
        }
    }

    private void onStateStart(ClimberState s) {
        switch (s) {
        case MANUAL:
            break;
        case NEUTRAL:
            break;
        default:
            break;
        }
    }

    private void onStateExit(ClimberState s) {
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

    }

    public class IO {
        // out
        private double front_output = 0;
        public Double front_desired_output = 0.0;
        private double back_output = 0;
        public Double back_desired_output = 0.0;
    }

    private void out() {

        if (mState != ClimberState.NEUTRAL) {
            mIO.front_output = mIO.front_desired_output;
            mIO.back_output = mIO.back_desired_output;
        } else {
            mIO.front_output = 0;
            mIO.back_output = 0;
        }

        mClimberFront.set(mState.controlMode, mIO.front_output);
        mClimberBack.set(mState.controlMode, mIO.back_output);
    }

    public synchronized void enableFollower() {
        // controller_2.follow(controller_1);
    }

    @Override
    protected void initDefaultCommand() {
    }

    public ClimberState getState() {
        return mState;
    }

    @Override
    public String getStateString() {
        return mState.toString();
    }

    public boolean setWantedState(ClimberState wantedState) {
        this.mWantedState = wantedState;

        return this.mWantedState == mState;
    }

    public enum ClimberState {
        NEUTRAL(ControlMode.Disabled), MANUAL(ControlMode.PercentOutput);

        public final ControlMode controlMode;

        private ClimberState(ControlMode controlMode) {
            this.controlMode = controlMode;
        }
    }

    @Override
    public String getSmallString() {
        return "CLMBR";
    }

}