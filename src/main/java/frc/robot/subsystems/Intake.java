package frc.robot.subsystems;

import frc.robot.Constants.kIntake;
import frc.robot.Constants.kPDP;
import frc.robot.Constants.kSolenoids;
import frc.robot.util.GZSubsystem;
import frc.robot.util.drivers.motorcontrollers.GZVictorSPX;
import frc.robot.util.drivers.pneumatics.GZSolenoid;

public class Intake extends GZSubsystem {

    private IntakeState mState = IntakeState.MANUAL;
    private IntakeState mWantedState = IntakeState.NEUTRAL;

    private GZVictorSPX mIntakeLeft, mIntakeRight;
    private GZSolenoid mIntakeExtend;

    private boolean mWantsIn = true;

    public IO mIO = new IO();

    private static Intake mInstance = null;

    private Intake() {
        // mIntakeLeft = null;
        // mIntakeRight = null;
        mIntakeLeft = new GZVictorSPX.Builder(kIntake.INTAKE_LEFT, this, "Left", kPDP.INTAKE_LEFT).build();
        mIntakeRight = new GZVictorSPX.Builder(kIntake.INTAKE_RIGHT, this, "Right", kPDP.INTAKE_RIGHT).build();

        mIntakeLeft.setInverted(kIntake.INTAKE_L_INVERT);
        mIntakeRight.setInverted(kIntake.INTAKE_R_INVERT);

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

    private void handleDrop() {
        // if (mDesiredDropState == DesiredDropState.UP) {
        // stop();
        // }

        if (Elevator.getInstance().safeForIntakeMovement()) {

        }
    }

    protected void extend() {
        mWantsIn = true;
    }

    protected void retract() {
        mWantsIn = false;
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

    public boolean armWantsOut() {
        return !mWantsIn;
    }

    public boolean armWantsIn() {
        return mWantsIn;
    }

    public boolean armWantsToMove() {
        return (mWantsIn && !isRetracted()) || (!mWantsIn && !isExtended());
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
        mIO.right_desired_output = right;
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
        private double right_output = 0;
        public Double left_desired_output = 0.0;
        public Double right_desired_output = 0.0;

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
            mIO.right_desired_output = 0.0;
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
            mIO.right_output = mIO.right_desired_output;
        } else {
            mIO.left_output = 0;
            mIO.right_output = 0;
        }

        if (!this.isSafetyDisabled()) {
            handleDrop();
        }

        if (mIntakeLeft != null && mIntakeRight != null) {
            mIntakeLeft.set(mIO.left_output);
            mIntakeRight.set(mIO.right_output);
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
}