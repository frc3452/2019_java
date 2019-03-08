package frc.robot.subsystems;

import frc.robot.Constants.kElevator;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kIntake;
import frc.robot.util.GZFlag;
import frc.robot.util.GZFlagMultiple;
import frc.robot.util.GZNotifier;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZSubsystemManager;

public class Superstructure extends GZSubsystem {

    private Elevator elev = Elevator.getInstance();
    private Intake intake = Intake.getInstance();

    private GZNotifier mThrowCargo = new GZNotifier(() -> {
        {
            openClaw();
        }
    });

    private GZSubsystemManager subsystems;

    private GZFlag mActionDone = new GZFlag();
    private GZFlagMultiple ScoreHP = new GZFlagMultiple(4);
    private GZFlagMultiple HPFromFloor = new GZFlagMultiple(4);
    private GZFlagMultiple HPFromFeed = new GZFlagMultiple(7);
    private GZFlagMultiple IntakeCargo = new GZFlagMultiple(8);

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
        OFF, IDLE, STOW, STOW_LOW, INTAKE_CARGO, TRNSFR_HP_FROM_FLOOR, GRAB_HP_FROM_FEED, GO_TO_QUEUED_HEIGHT,
        THROW_CARGO, SCORE_HATCH;

        // MOVE CARGO ACROSS FLOOR
    }

    public Actions getCurrentAction() {
        return mAction;
    }

    // ACTIONS
    
    public void runAction(Actions action, boolean queue) {
        if (queue) {
            queueAction(action);
            return;
        }

        // if (mAction == action)
        // return;

        mAction = action;

        if (mAction != Actions.IDLE && mAction != Actions.OFF)
            mActionDone.rst();

        switch (action) {
        case OFF:
            break;
        case IDLE:
            elev.stopMovement();
            intake.stop();
            break;
        case GO_TO_QUEUED_HEIGHT:
            elev.setHeight(mQueuedHeight);
            break;
        case INTAKE_CARGO:
            IntakeCargo.reset();
            // intake.lower();
            // elev.setHeight(Heights.Home);
            break;
        case STOW:
            stow();
            break;
        case STOW_LOW:
            stow();
            elev.setHeight(Heights.Home);
            break;
        case THROW_CARGO:
            elev.extendSlides();
            mThrowCargo.startSingle(kElevator.THROW_CARGO_DELAY);
            break;
        case TRNSFR_HP_FROM_FLOOR:
            done();
            // HPFromFloor.reset();
            // elev.closeClaw();
            // elev.retractSlides();
            break;
        case GRAB_HP_FROM_FEED:
            HPFromFeed.reset();
            stow();
            elev.setHeight(Heights.HP_1);
            elev.closeClaw();
            elev.retractSlides();
            break;
        }
    }

    @Override
    public void loop() {
        if (mAction == Actions.OFF || this.isSafetyDisabled())
            stop();
        else {

            switch (mAction) {
            case IDLE:
                break;
            case STOW:
                if (isStowed())
                    done();
                break;

            case SCORE_HATCH:
                if (!ScoreHP.get(1)) {
                    extendSlides();

                    if (elev.areSlidesOut())
                        ScoreHP.trip(1);

                } else if (!ScoreHP.getNext()) {
                    closeClaw();

                    if (elev.isClawClosed())
                        ScoreHP.tripNext();
                } else if (!ScoreHP.getNext()) {
                    elev.jogHeight(kElevator.HATCH_PLACING_JOG);
                    if (elev.nearTarget())
                        ScoreHP.tripNext();
                } else if (!ScoreHP.getNext()) {
                    elev.retractSlides();

                    if (elev.areSlidesIn())
                        done();
                }

                break;
            case THROW_CARGO:
                if (elev.areSlidesOut() && elev.isClawOpen())
                    done();
                break;
            case STOW_LOW:
                if (isStowed() && elev.nearTarget())
                    done();
                break;
            case INTAKE_CARGO:
                if (!IntakeCargo.get(1)) {
                    if (!elev.isAboveIntakeSaftey()) {
                        elev.retractSlides();
                    }
                    IntakeCargo.trip(1);
                } else if (!IntakeCargo.getNext()) {
                    if (elev.slidesAtDesired()) {
                        elev.setHeight(kElevator.LOWEST_WITH_SLIDES_OUT);
                        intake.lower();
                        IntakeCargo.tripNext();
                    }
                } else if (!IntakeCargo.getNext()) {
                    if (elev.nearTarget() && intake.isLowered()) {
                        extendSlides();
                        elev.goHome();
                        IntakeCargo.tripNext();
                    }
                } else if (!IntakeCargo.getNext()) {
                    if (elev.areSlidesOut()) {
                        intake.runIntake(kIntake.INTAKE_SPEED);
                        IntakeCargo.tripNext();
                    }
                } else if (!IntakeCargo.getNext()) {
                    if (elev.isCargoSensorTripped()) {
                        elev.closeClaw();
                        IntakeCargo.tripNext();
                    }
                } else if (!IntakeCargo.getNext()) {
                    if (elev.isClawClosed()) {
                        elev.retractSlides();
                        intake.prepToRaise();
                        IntakeCargo.tripNext();
                    }
                } else if (!IntakeCargo.getNext()) {
                    if (elev.areSlidesIn()) {
                        intake.raise();
                        IntakeCargo.tripNext();
                    }
                } else if (!IntakeCargo.getNext()) {
                    if (intake.isRaised()) {
                        done();
                    }
                }

                break;
            case TRNSFR_HP_FROM_FLOOR:
                done();
                if (true)
                    return;
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
                // HPFromFeed.print();

                if (!HPFromFeed.get(1)) {
                    if (intake.isRaised() && elev.nearTarget() && elev.isClawClosed()) {
                        HPFromFeed.trip(1);
                    }
                } else if (!HPFromFeed.getNext()) {
                    elev.extendSlides();
                    HPFromFeed.tripNext();
                } else if (!HPFromFeed.getNext()) {
                    if (elev.areSlidesOut())
                        HPFromFeed.tripNext();
                } else if (!HPFromFeed.getNext()) {
                    openClaw();
                    HPFromFeed.tripNext();
                } else if (!HPFromFeed.getNext()) {
                    if (elev.isClawOpen())
                        HPFromFeed.tripNext();
                } else if (!HPFromFeed.getNext()) {
                    elev.setHeight(Heights.HP_Feeder_Jog);
                    HPFromFeed.tripNext();
                } else if (!HPFromFeed.getNext()) {
                    if (elev.nearTarget())
                        retractSlides();
                    if (elev.areSlidesIn())
                        done();
                } else {
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

    public boolean isActionDone() {
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

    public void jog(double inches) {
        elev.jogHeight(inches);
    }

    public void runIntake(double speed) {
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


    public void stow() {
        intake.raise();
        elev.retractSlides();
        intake.stop();
    }

    public void raiseIntake() {
        intake.raise();
    }

    public void lowerIntake() {
        intake.lower();
    }

    public void dropCrawler() {
        Pneumatics.getInstance().dropCrawler();
    }

    public void openClaw() {
        elev.openClaw();
    }

    public void toggleClaw() {
        elev.toggleClaw();
    }

    public void toggleSlides() {
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

	public void elevManual(Double leftAnalogY) {
        elev.manual(leftAnalogY);
	}

}