package frc.robot.subsystems;

import frc.robot.GZOI;
import frc.robot.Constants.kIntake;
import frc.robot.Constants.kPDP;
import frc.robot.Constants.kSolenoids;
import frc.robot.util.GZSubsystem;
import frc.robot.util.drivers.motorcontrollers.GZVictorSPX;
import frc.robot.util.drivers.pneumatics.GZSolenoid;

public class Intake extends GZSubsystem {

    private DesiredDropState mDesiredDropState = DesiredDropState.UP;
    private IntakeState mState = IntakeState.MANUAL;
    private IntakeState mWantedState = IntakeState.NEUTRAL;

    private GZVictorSPX mIntakeLeft, mIntakeRight;
    private GZSolenoid mIntakeDrop, mIntakeFold;

    public IO mIO = new IO();

    private static Intake mInstance = null;

    private Intake() {
        // mIntakeLeft = null;
        // mIntakeRight = null;
        mIntakeLeft = new GZVictorSPX.Builder(kIntake.INTAKE_LEFT, this, "Left", kPDP.INTAKE_LEFT).build();
        mIntakeRight = new GZVictorSPX.Builder(kIntake.INTAKE_RIGHT, this, "Right", kPDP.INTAKE_RIGHT).build();

        mIntakeLeft.setInverted(kIntake.INTAKE_L_INVERT);
        mIntakeRight.setInverted(kIntake.INTAKE_R_INVERT);

        mIntakeDrop = new GZSolenoid(kSolenoids.INTAKE_DROP, this, "Intake Drop");
        mIntakeFold = new GZSolenoid(kSolenoids.INTAKE_FOLD, this, "Intake Fold");
    }

    public static Intake getInstance() {
        if (mInstance == null)
            mInstance = new Intake();

        return mInstance;
    }

    protected void stow() {
        raise();
    }

    private void handleDrop() {
        if (mDesiredDropState == DesiredDropState.UP) {
            stop();
        }

        if (Elevator.getInstance().safeForIntakeMovement())
            switch (mDesiredDropState) {
            case DOWN:
                mIntakeDrop.on();
                if (mIntakeDrop.isOn())
                    mIntakeFold.set(true);
                break;
            case UP:
                mIntakeFold.off();
                if (mIntakeFold.isOff())
                    mIntakeDrop.set(false);
                break;
            case PREP_FOR_UP:
                mIntakeDrop.on();
                mIntakeFold.off();
                break;
            default:
                System.out.println("ERROR Unhandled Intake Drop State: " + mDesiredDropState);
                break;
            }
    }

    protected DropState getDropState() {
        if (mIntakeFold.isOn() && mIntakeDrop.isOn())
            return DropState.DOWN;
        else if (mIntakeFold.isOff() && mIntakeDrop.isOff())
            return DropState.UP;

        return DropState.IN_MOTION;
    }

    protected void lower() {
        mDesiredDropState = DesiredDropState.DOWN;
    }

    protected void raise() {
        mDesiredDropState = DesiredDropState.UP;
    }

    protected void prepToRaise() {
        mDesiredDropState = DesiredDropState.PREP_FOR_UP;
    }

    public enum DropState {
        UP, DOWN, IN_MOTION
    }

    public enum DesiredDropState {
        UP, DOWN, PREP_FOR_UP,
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

    public boolean armWantsDown() {
        return mDesiredDropState == DesiredDropState.DOWN;
    }

    public boolean armWantsUp() {
        return mDesiredDropState == DesiredDropState.UP;
    }

    public boolean armWantsToMove() {
        return (mDesiredDropState == DesiredDropState.UP && !mIntakeDrop.isOff())
                || (mDesiredDropState == DesiredDropState.DOWN && !mIntakeDrop.isOn());
    }

    public boolean isRaised() {
        return getDropState() == DropState.UP;
    }

    public boolean isLowered() {
        return getDropState() == DropState.DOWN;
    }

    public boolean isUp() {
        return mIntakeDrop.isOff();
    }

    public boolean isOff() {
        return mIntakeDrop.isOn();
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
        if (GZOI.getInstance().isDisabled())
            mDesiredDropState = DesiredDropState.UP;

        handleStates();
        in();
        out();
    }

    private void handleStates() {
        boolean neutral = false;

        if (mWantedState == IntakeState.NEUTRAL) {
            neutral = true;
        } else if (!isLowered()) {
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
        return mIntakeDrop.getChangeCounts();
    }

    public int getIntakeTotalOpens() {
        return mIntakeFold.getChangeCounts();
    }

    @Override
    protected void initDefaultCommand() {
    }
}