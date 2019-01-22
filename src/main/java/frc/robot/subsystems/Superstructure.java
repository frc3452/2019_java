package frc.robot.subsystems;

import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kIntake;
import frc.robot.GZOI;
import frc.robot.util.GZFlag;
import frc.robot.util.GZFlagMultiple;
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

    private GZFlagMultiple HPFromFloor = new GZFlagMultiple(4);

    private GZFlagMultiple HPFromFeed = new GZFlagMultiple(5);

    private Manual mManual = new Manual();

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
        OFF, IDLE, STOW, STOW_LOW, INTAKE_CARGO, HOLD_CARGO, TRNSFR_HP_FROM_FLOOR, GRAB_HP_FROM_FEED,
        GO_TO_QUEUED_HEIGHT;
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
            case STOW_LOW:
                if (isStowed())
                    setHeight(Heights.Home, false);
                else
                    stopElevatorMovement(false);
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

                if (elev.isClawClosed() && elev.isCargoSensorTripped() && isStowed())
                    idle();
                break;
            case TRNSFR_HP_FROM_FLOOR:
                setHeight(Heights.HP_Floor_Grab, false);
                if (elev.nearTarget()) {
                    if (!HPFromFloor.get(1) && elev.areSlidesIn() && elev.isClawClosed()) {
                        stow();
                        HPFromFloor.trip(1);
                    } else if (!HPFromFloor.getNext() && intake.isRaised()) {
                        extendSlides(false);
                        HPFromFloor.tripNext();
                    } else if (!HPFromFloor.getNext() && elev.areSlidesOut()) {
                        openClaw(false);
                        HPFromFloor.tripNext();
                    } else if (!HPFromFloor.getNext() && elev.isClawOpen()) {
                        retractSlides(false);
                        HPFromFloor.tripNext();
                    }
                }
                if (HPFromFloor.allFlagsTripped() && elev.areSlidesIn()) {
                    idle();
                }
                break;
            case GRAB_HP_FROM_FEED:
                if (intake.isRaised() && elev.nearTarget() && elev.isClawClosed()) {
                    HPFromFeed.trip(1);
                }
                if (HPFromFeed.get(1)) {
                    if (!HPFromFeed.get(2)) {
                        extendSlides(false);
                        HPFromFeed.tripNext();
                    } else if (!HPFromFeed.getNext() && elev.areSlidesOut()) {
                        openClaw(false);
                        HPFromFeed.tripNext();
                    } else if (!HPFromFeed.getNext() && elev.isClawOpen()) {
                        setHeight(Heights.HP_Feeder_Jog, false);
                        HPFromFeed.tripNext();
                    } else if (!HPFromFeed.getNext() && elev.nearTarget()) {
                        retractSlides(false);
                        HPFromFeed.tripNext();
                    }
                    if (HPFromFeed.allFlagsTripped() && elev.areSlidesIn())
                        idle();
                }
                break;
            }
        }
    }

    private boolean isStowed() {
        return elev.areSlidesIn() && intake.isRaised();
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
            if (mQueuedHeight != h)
            {
                mQueuedHeight = h;
                queueAction(Actions.GO_TO_QUEUED_HEIGHT);
                System.out.println("Queued height: " + mQueuedHeight);
            }
            return;
        }
        setHeight(h, true);
    }

    private void queueAction(Actions action) {
        if (mQueuedAction != action) {
            mQueuedAction = action;
            System.out.println("Queued action " + mQueuedAction);
        }
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
        case IDLE:
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
        case STOW_LOW:
            stow();
            break;
        case HOLD_CARGO:
            closeClaw(false);
            break;
        case TRNSFR_HP_FROM_FLOOR:
            HPFromFloor.reset();
            closeClaw(false);
            retractSlides(false);
            break;
        case GRAB_HP_FROM_FEED:
            HPFromFeed.reset();
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
        stopIntake(false);
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
            mManual.mIntake = true;
            intake.runIntake(speed);
        } else if (!mManual.mIntake)
            intake.runIntake(speed);
    }

    public void stopIntake(boolean manual) {
        if (manual) {
            mManual.mIntake = true;
            intake.stop();
        } else if (!mManual.mIntake)
            intake.stop();
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