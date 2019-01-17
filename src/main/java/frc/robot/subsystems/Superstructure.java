package frc.robot.subsystems;

import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kIntake;
import frc.robot.GZOI;
import frc.robot.util.GZFlag;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZSubsystemManager;

public class Superstructure extends GZSubsystem {

    private Elevator elev = Elevator.getInstance();
    private Intake intake = Intake.getInstance();

    private GZSubsystemManager subsystems;

    private class Manual {
        private boolean mIntake = false;
        private boolean mIntakeDrop = false;
        private boolean mElevator = false;
        private boolean mClaw = false;
        private boolean mSlides = false;
    }

    private class Flags {
        private GZFlag HPFromFeed = new GZFlag();

        private GZFlag HPFromFloorFlag1 = new GZFlag();
        private GZFlag HPFromFloorFlag2 = new GZFlag();
        private GZFlag HPFromFloorFlag3 = new GZFlag();
        private GZFlag HPFromFloorFlag4 = new GZFlag();
    }

    private Manual mManual = new Manual();
    private Flags mFlags = new Flags();

    private static Superstructure mInstance = null;

    public static Superstructure getInstance() {
        if (mInstance == null)
            mInstance = new Superstructure();
        return mInstance;
    }

    private Superstructure() {
        subsystems = new GZSubsystemManager(Elevator.getInstance(), Intake.getInstance());
    }

    private Actions mAction = Actions.IDLE;
    private Actions mQueuedAction = Actions.IDLE;
    private Heights mQueuedHeight = Heights.Home;

    public enum Actions {
        OFF, IDLE, STOW, STOW_LOW, INTAKE_CARGO, HOLD_CARGO, TRNSFR_HP_FROM_FLOOR, GRAB_HP_FROM_FEED, GO_TO_QUEUED_HEIGHT;
        // MOVE CARGO ACROSS FLOOR
    }

    public Actions getCurrentAction() {
        return mAction;
    }

    // ACTIONS
    @Override
    public void loop() {
        if (mAction == Actions.OFF || (this.isSafetyDisabled() && !GZOI.getInstance().isFMS()))
            stop();
        else {

            switch (mAction) {
            case IDLE:
                break;
            case INTAKE_CARGO:
                if (intake.isLowered() && elev.nearTarget())
                    runIntake(kIntake.INTAKE_SPEED, false);

                if (elev.isCargoSensorTripped())
                    runAction(Actions.HOLD_CARGO);

                break;
            case HOLD_CARGO:
                if (elev.isClawClosed() && elev.isCargoSensorTripped())
                    stow();
                break;
            case TRNSFR_HP_FROM_FLOOR:
                setHeight(Heights.HP_Floor_Grab, false);
                if (elev.nearTarget()) {
                    if (!mFlags.HPFromFloorFlag1.get() && elev.areSlidesIn() && elev.isClawClosed()) {
                        stow();
                        mFlags.HPFromFloorFlag1.tripFlag();
                    } else if (!mFlags.HPFromFloorFlag2.get() && intake.isRaised()) {
                        extendSlides(false);
                        mFlags.HPFromFloorFlag2.tripFlag();
                    } else if (!mFlags.HPFromFloorFlag3.get() && elev.areSlidesOut()) {
                        openClaw(false);
                        mFlags.HPFromFloorFlag3.tripFlag();
                    } else if (!mFlags.HPFromFloorFlag4.get() && elev.isClawOpen()) {
                        retractSlides(false);
                        mFlags.HPFromFloorFlag4.tripFlag();
                    }
                }
                if (mFlags.HPFromFloorFlag4.get() && elev.areSlidesIn()) {
                    idle();
                }
                break;
            case GRAB_HP_FROM_FEED:
                if (intake.isRaised() && elev.nearTarget() && elev.isClawClosed()) {

                    if (!mFlags.HPFromFeed.get()) {
                        extendSlides(false);
                        mFlags.HPFromFeed.tripFlag();
                    } else if (elev.areSlidesOut()) {
                        openClaw(false);
                    }
                    // have automatically retract????
                } else if (elev.isClawOpen() && mFlags.HPFromFeed.get())
                    idle();
                break;
            }

        }
    }

    private void idle() {
        runAction(Actions.IDLE);
    }

    public void cancelAction() {
        idle();
        stopElevatorMovement(false);
    }

    public void runHeight(Heights h) {
        runHeight(h, false);
    }

    public void runHeight(Heights h, boolean queue) {
        if (queue) {
            mQueuedHeight = h;
            queueAction(Actions.GO_TO_QUEUED_HEIGHT);
            System.out.println("Queued action: " + Actions.GO_TO_QUEUED_HEIGHT);
            return;
        }
        setHeight(h, true);
    }

    private void queueAction(Actions action) {
        mQueuedAction = action;
    }

    public void runQueuedAction() {
        if (mQueuedAction != Actions.IDLE) {
            runAction(mQueuedAction);
            mQueuedAction = Actions.IDLE;
        }
    }

    public void runAction(Actions action) {
        runAction(action, false);
    }

    public void runAction(Actions action, boolean queue) {
        if (queue) {
            queueAction(action);
            return;
        }

        mAction = action;
        switch (action) {
        case OFF:
            stopElevatorMovement(false);
            break;

        case GO_TO_QUEUED_HEIGHT:
            setHeight(mQueuedHeight, true);
            break;
        case INTAKE_CARGO:
            lowerIntake(false);
            setHeight(Heights.Home, false);
            break;
        case STOW:
            stow();
            break;
        case STOW_LOW:
            stow();
            setHeight(Heights.Home, false);
            break;
        case HOLD_CARGO:
            closeClaw(false);
            break;
        case TRNSFR_HP_FROM_FLOOR:
            mFlags.HPFromFloorFlag1.rst();
            mFlags.HPFromFloorFlag2.rst();
            mFlags.HPFromFloorFlag3.rst();
            closeClaw(false);
            retractSlides(false);
            break;
        case GRAB_HP_FROM_FEED:
            mFlags.HPFromFeed.rst();
            stow();
            setHeight(Heights.HP_1, false);
            closeClaw(false);
            break;
        }
    }

    // MANUAL
    public void intakeNoManual() {
        mManual.mIntake = false;
    }

    public void intakeDropNoManual() {
        mManual.mIntakeDrop = false;
    }

    public void elevatorNoManual() {
        mManual.mElevator = false;
    }

    public void clawNoManual() {
        mManual.mClaw = false;
    }

    public void slidesNoManual() {
        mManual.mSlides = false;
    }

    public void stow() {
        raiseIntake(false);
        retractSlides(false);
    }

    private void stopElevatorMovement(boolean manual) {
        if (manual) {
            elev.stopMovement();
        } else if (!mManual.mElevator)
            elev.stopMovement();
    }

    private synchronized void setHeight(Heights h, boolean manual) {
        if (manual) {
            mManual.mElevator = true;
            elev.setHeight(h);
        } else if (!mManual.mElevator)
            elev.setHeight(h);
    }

    public synchronized void openClaw(boolean manual) {
        if (manual) {
            mManual.mClaw = true;
            elev.openClaw();
        } else if (!mManual.mClaw)
            elev.openClaw();

    }

    public synchronized void closeClaw(boolean manual) {
        if (manual) {
            mManual.mClaw = true;
            elev.closeClaw();
        } else if (!mManual.mClaw)
            elev.closeClaw();
    }

    public synchronized void extendSlides(boolean manual) {
        if (manual) {
            mManual.mSlides = true;
            elev.extendSlides();
        } else if (!mManual.mSlides)
            elev.extendSlides();
    }

    public synchronized void retractSlides(boolean manual) {
        if (manual) {
            mManual.mSlides = true;
            elev.retractSlides();
        } else if (!mManual.mSlides)
            elev.retractSlides();

    }

    public synchronized void raiseIntake(boolean manual) {
        if (manual) {
            mManual.mIntakeDrop = true;
            intake.raise();
        } else if (!mManual.mIntakeDrop)
            intake.raise();
    }

    public synchronized void lowerIntake(boolean manual) {
        if (manual) {
            mManual.mIntakeDrop = true;
            intake.lower();
        } else if (!mManual.mIntakeDrop)
            intake.lower();
    }

    public void runIntake(double speed, boolean manual) {
        if (manual) {
            intake.runIntake(speed);
        } else if (!mManual.mIntake)
            intake.runIntake(speed);
    }

    public String getStateString() {
        return mAction.toString();
    }

    @Override
    public void stop() {
        subsystems.stop();
    }

    @Override
    public String getSmallString() {
        return "SPR-STRCT";
    }

    public void addLoggingValues() {
    }

    protected void initDefaultCommand() {
    }

}