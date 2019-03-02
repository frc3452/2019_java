package frc.robot.subsystems;

import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kIntake;
import frc.robot.util.GZFlag;
import frc.robot.util.GZFlagMultiple;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZSubsystemManager;

public class Superstructure extends GZSubsystem {

    private Elevator elev = Elevator.getInstance();
    private Intake intake = Intake.getInstance();

    private GZSubsystemManager subsystems;

    private GZFlag mActionDone = new GZFlag();
    private GZFlagMultiple HPFromFloor = new GZFlagMultiple(4);
    private GZFlagMultiple HPFromFeed = new GZFlagMultiple(5);

    private static Superstructure mInstance = null;

    public static Superstructure getInstance() {
        if (mInstance == null)
            mInstance = new Superstructure();
        return mInstance;
    }

    private Superstructure() {
        subsystems = new GZSubsystemManager(elev, intake); // intake
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
        if (mAction == Actions.OFF || this.isSafetyDisabled())
            stop();
        else {

            switch (mAction) {
            case IDLE:
                break;
            case STOW_LOW:
                if (isStowed())
                    elev.setHeight(Heights.Home);
                else
                    break;
            case INTAKE_CARGO:
                if (intake.isLowered() && elev.nearTarget())
                    intake.runIntake(kIntake.INTAKE_SPEED);

                if (elev.isCargoSensorTripped())
                    runAction(Actions.HOLD_CARGO);

                break;
            case HOLD_CARGO:
                if (elev.isClawClosed() && elev.isCargoSensorTripped())
                    stow();

                if (elev.isClawClosed() && elev.isCargoSensorTripped() && isStowed())
                    done();
                break;
            case TRNSFR_HP_FROM_FLOOR:
                elev.setHeight(Heights.HP_Floor_Grab);
                if (elev.nearTarget()) {
                    if (!HPFromFloor.get(1) && elev.areSlidesIn() && elev.isClawClosed()) {
                        stow();
                        HPFromFloor.trip(1);
                    } else if (!HPFromFloor.getNext() && intake.isRaised()) {
                        elev.extendSlides();
                        HPFromFloor.tripNext();
                    } else if (!HPFromFloor.getNext() && elev.areSlidesOut()) {
                        elev.openClaw();
                        HPFromFloor.tripNext();
                    } else if (!HPFromFloor.getNext() && elev.isClawOpen()) {
                        elev.retractSlides();
                        HPFromFloor.tripNext();
                    }
                }
                if (HPFromFloor.allFlagsTripped() && elev.areSlidesIn()) {
                    done();
                }
                break;
            case GRAB_HP_FROM_FEED:
                HPFromFeed.print();

                if (intake.isRaised() && elev.nearTarget() && elev.isClawClosed()) {
                    HPFromFeed.trip(1);
                }
                if (HPFromFeed.get(1)) {
                    if (!HPFromFeed.get(2)) {
                        elev.extendSlides();
                        HPFromFeed.tripNext();
                    } else if (!HPFromFeed.getNext() && elev.areSlidesOut()) {
                        elev.openClaw();
                        HPFromFeed.tripNext();
                    } else if (!HPFromFeed.getNext() && elev.isClawOpen()) {
                        elev.setHeight(Heights.HP_Feeder_Jog);
                        HPFromFeed.tripNext();
                    } else if (!HPFromFeed.getNext() && elev.nearTarget()) {
                        elev.retractSlides();
                        HPFromFeed.tripNext();
                    }
                    if (HPFromFeed.allFlagsTripped() && elev.areSlidesIn())
                        done();
                }
                break;
            }
        }
    }

    private boolean isStowed() {
        return elev.areSlidesIn() && intake.isRaised();
    }

    public void idle() {
        runAction(Actions.IDLE);
    }

    public void done() {
        mActionDone.tripFlag();
        idle();
    }

    public void runHeight(Heights h) {
        runHeight(h, false);
    }

    public boolean isActionDone()
    {
        return mActionDone.get();
    }

    public void runHeight(Heights h, boolean queue) {
        if (queue) {
            if (mQueuedHeight != h) {
                mQueuedHeight = h;
                queueAction(Actions.GO_TO_QUEUED_HEIGHT);
                System.out.println("Queued height: " + mQueuedHeight);
            }
            return;
        }
        elev.setHeight(h);
    }
    
    public void jog(double inches)
    {
        elev.jogHeight(inches);
    }

    public void runIntake(double speed)
    {
        intake.runIntake(speed);
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

        if (mAction == action)
            return;

        mAction = action;

        if (mAction != Actions.IDLE && mAction != Actions.OFF)
            mActionDone.rst();

        switch (action) {
        case OFF:
        case IDLE:
            elev.stopMovement();
            intake.stop();
            break;
        case GO_TO_QUEUED_HEIGHT:
            elev.setHeight(mQueuedHeight);
            break;
        case INTAKE_CARGO:
            intake.lower();
            elev.setHeight(Heights.Home);
            break;
        case STOW:
        case STOW_LOW:
            stow();
            break;
        case HOLD_CARGO:
            elev.closeClaw();
            break;
        case TRNSFR_HP_FROM_FLOOR:
            HPFromFloor.reset();
            elev.closeClaw();
            elev.retractSlides();
            break;
        case GRAB_HP_FROM_FEED:
            HPFromFeed.reset();
            stow();
            elev.setHeight(Heights.HP_1);
            elev.closeClaw();
            break;
        }
    }

    public void stow() {
        intake.raise();
        elev.retractSlides();
        intake.stop();
    }

    public void openClaw() {
        elev.openClaw();
    }

    public void toggleClaw()
    {
        elev.toggleClaw();
    }

    public void toggleSlides()
    {
        elev.toggleSlides();
    }

    public void closeClaw() {
        elev.closeClaw();
    }

    public void extendSlides() {
        elev.extendSlides();
    }

    public void retractSlides() {
        elev.retractSlides();
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