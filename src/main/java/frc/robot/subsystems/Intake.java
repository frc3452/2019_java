package frc.robot.subsystems;

import frc.robot.Constants.kIntake;
import frc.robot.Constants.kPDP;
import frc.robot.Constants.kSolenoids;
import frc.robot.util.GZSubsystem;
import frc.robot.util.drivers.motorcontrollers.GZVictorSPX;
import frc.robot.util.drivers.pneumatics.GZSolenoid;
import frc.robot.util.drivers.pneumatics.GZSolenoid.SolenoidState;

public class Intake extends GZSubsystem {

    private IntakeState mState = IntakeState.MANUAL;
    private IntakeState mWantedState = IntakeState.NEUTRAL;

    private GZVictorSPX mIntakeRoller;
    private GZSolenoid mIntakeExtend;

    public IO mIO = new IO();

    private static Intake mInstance = null;

    private Intake() {
        // mIntakeLeft = null;
        // mIntakeRight = null;
        mIntakeRoller = new GZVictorSPX.Builder(kIntake.INTAKE_LEFT, this, "Left", kPDP.INTAKE_LEFT).build();

        mIntakeRoller.setInverted(kIntake.INTAKE_ROLLER_INVERT);

        mIntakeExtend = new GZSolenoid(kSolenoids.INTAKE_EXTEND, this, "Intake Drop");
    }

    public static Intake getInstance() {
        if (mInstance == null)
            mInstance = new Intake();

        return mInstance;
    }

    protected void stow() {
        retract();
        stop();
    }

    public void swapDirection() {
        if (mIO.left_desired_output != 0) {
            mIO.held_desired_output = mIO.left_desired_output;
            mIO.left_desired_output = 0.0;
        } else {
            mIO.left_desired_output = mIO.held_desired_output;
        }
    }

    private void handleMovement() {
        if (Elevator.getInstance().safeForIntakeMovement()) {
            mIntakeExtend.stateChange();
        }
        // if (mIntakeExtend.isOn())
        // runIntake(kIntake.INTAKE_SPEED);
        // else
        // stop();
    }

    protected void extend() {
        mIntakeExtend.wantOn();
    }

    protected void retract() {
        mIntakeExtend.wantOff();
    }

    public enum IntakeState {
        NEUTRAL, MANUAL
    }

    public boolean setWantedState(IntakeState wantedState) {
        this.mWantedState = wantedState;

        return this.mWantedState == mState;
    }

    @Override
    public void stop() {
        setWantedState(IntakeState.NEUTRAL);
    }

    public boolean wantsOut() {
        return mIntakeExtend.getWantOn() && !mIntakeExtend.isOn();
    }

    public SolenoidState getSolenoidState() {
        return mIntakeExtend.getSolenoidState();
    }

    public boolean wantsIn() {
        return mIntakeExtend.getWantOff() && !mIntakeExtend.isOff();
    }

    public boolean wantsToMove() {
        return wantsIn() || wantsOut();
    }

    public boolean isMoving() {
        return mIntakeExtend.isMoving();
    }

    public boolean isRetracted() {
        return mIntakeExtend.isOff();
    }

    public boolean isExtended() {
        return mIntakeExtend.isOn();
    }

    protected void runIntake(double left, double right) {
        setWantedState(IntakeState.MANUAL);
        mIO.left_desired_output = left;
    }

    protected void runIntake(double speed) {
        runIntake(speed, speed);
    }

    @Override
    public String getSmallString() {
        return "INTK";
    }

    @Override
    public void addLoggingValues() {
    }

    @Override
    public void loop() {
        if (!isExtended())
            stop();

        handleMovement();
        handleStates();
        in();
        out();
    }

    private void handleStates() {
        boolean neutral = false;

        if (mWantedState == IntakeState.NEUTRAL) {
            neutral = true;
        } else if (!mIntakeExtend.isOn()) {
            neutral = true;
        } else if (this.isSafetyDisabled()) {
            neutral = true;
        }

        if (neutral) {
            switchToState(IntakeState.NEUTRAL);
        } else {
            switchToState(mWantedState);
        }
    }

    public class IO {
        // out
        private double left_output = 0;
        public Double left_desired_output = 0.0;
        public Double held_desired_output = 0.0;
    }

    private void switchToState(IntakeState s) {
        if (mState != s) {
            onStateExit(mState);
            mState = s;
            onStateStart(mState);
        }
    }

    private void onStateStart(IntakeState s) {
        switch (s) {
        case MANUAL:
            break;
        case NEUTRAL:
            mIO.left_desired_output = 0.0;
            break;
        default:
            break;
        }
    }

    private void onStateExit(IntakeState s) {
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

    private void out() {
        if (mState != IntakeState.NEUTRAL) {
            mIO.left_output = mIO.left_desired_output;
        } else {
            mIO.left_output = 0;
        }

        if (!this.isSafetyDisabled()) {
            handleMovement();
        }

        if (mIntakeRoller != null) {
            mIntakeRoller.set(mIO.left_output);
        }

    }

    public IntakeState getState() {
        return mState;
    }

    @Override
    public String getStateString() {
        return mState.toString();
    }

    public int getIntakeTotalFlips() {
        return mIntakeExtend.getChangeCounts();
    }

    @Override
    protected void initDefaultCommand() {
    }

    public void toggle() {
        mIntakeExtend.toggleWanted();
    }
}