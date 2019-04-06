package frc.robot.subsystems;

import frc.robot.Constants.kElevator;
import frc.robot.Constants.kElevator.Heights;
import frc.robot.Constants.kIntake;

import java.util.concurrent.locks.Condition;

import frc.robot.GZOI;
import frc.robot.subsystems.Drive.DriveState;
import frc.robot.util.GZFlag;
import frc.robot.util.GZFlagMultiple;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZSubsystemManager;

public class Superstructure extends GZSubsystem {
    
    private Elevator elev = Elevator.getInstance();
    private Intake intake = Intake.getInstance();
    
    private GZSubsystemManager subsystems;
    
    private boolean mIntakingCargo = false;
    private boolean mHasAutoScored = false;
    private boolean mHasAutoFeeder = false;
    
    private GZFlag mActionDone = new GZFlag();
    private GZFlagMultiple ScoreHP = new GZFlagMultiple(6);
    private GZFlagMultiple HPFromFeed = new GZFlagMultiple(7);
    
    private GZFlagMultiple CargoFromFeed = new GZFlagMultiple(3);
    
    private GZFlagMultiple IntakeCargo = new GZFlagMultiple(2);
    private GZFlagMultiple ScootCargoOnGround = new GZFlagMultiple(2);
    private GZFlagMultiple GrabCargoDuringIntake = new GZFlagMultiple(6);
    private GZFlagMultiple ThrowCargo = new GZFlagMultiple(4);

    private final Heights mDefaultHeight = Heights.HP_1;

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
        OFF, IDLE, STOW, STOW_LOW, INTAKE_CARGO, GRAB_CARGO_DURING_INTAKE, SCOOT_CARGO_ON_GROUND, GRAB_HP_FROM_FEED,
        GRAB_CARGO_FROM_FEED, GO_TO_QUEUED_HEIGHT, THROW_CARGO, SCORE_HATCH;
    }

    public Actions getCurrentAction() {
        return mAction;
    }

    public boolean isIntakingCargo() {
        return mIntakingCargo;
    }
    
    public boolean hasAutoScored () {
		return mHasAutoScored;
    }
    
    public boolean hasAutoFeeder () {
		return mHasAutoFeeder;
	}

    public boolean fakeAutoScore() {
        mHasAutoScored = true;
        return true;
    }

    public boolean fakeAutoFeeder() {
        mHasAutoFeeder = true;
        return true;
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
        if (mAction == Actions.SCORE_HATCH || mAction == Actions.THROW_CARGO) {
            mHasAutoScored = true;
        }


        if (mAction != Actions.IDLE && mAction != Actions.OFF)
            mActionDone.rst();

        switch (action) {
        case OFF:
            break;
        case IDLE:
            // elev.stopMovement();
            // intake.stop();
            break;
        case GO_TO_QUEUED_HEIGHT:
            elev.setHeight(mQueuedHeight);
            break;
        case INTAKE_CARGO:
            IntakeCargo.reset();
            intake.extend();
            elev.setHeight(Heights.Cargo_Intake);
            elev.openClaw();
            // elev.extendSlides();
            elev.retractSlides();
            break;
        case SCOOT_CARGO_ON_GROUND:
            ScootCargoOnGround.reset();
            break;
        case GRAB_CARGO_DURING_INTAKE:
            GrabCargoDuringIntake.reset();
            break;
        case STOW:
            stow();
            break;
        case SCORE_HATCH:
            ScoreHP.reset();
            break;
        case STOW_LOW:
            stow();
            elev.setHeight(Heights.Home);
            break;
        case THROW_CARGO:
            ThrowCargo.reset();
            break;
        case GRAB_CARGO_FROM_FEED:
            CargoFromFeed.reset();
            break;
        case GRAB_HP_FROM_FEED:
            HPFromFeed.reset();
            intake.retract();
            intake.stop();
            // elev.setHeight(Heights.HP_1);
            elev.closeClaw();
            break;
        }
    }

    @Override
    public void loop() {
        mIntakingCargo = Intake.getInstance().isExtended();
        System.out.println(hasAutoScored() + "\t" + hasAutoFeeder());


        if (Drive.getInstance().getState() == DriveState.CLIMB && Drive.getInstance().getRearBottomLimit()) {
            System.out.println("Auto Drop crawler!");
            Pneumatics.getInstance().forceDropCrawler();
        }

        if (GZOI.getInstance().isDisabled() && mAction != Actions.IDLE)
            runAction(Actions.IDLE);

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
            case GO_TO_QUEUED_HEIGHT:
                if (elev.nearTarget())
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
                    ScoreHP.tripNext();
                } else if (!ScoreHP.getNext()) {
                    if (elev.nearTarget())
                        ScoreHP.tripNext();
                } else if (!ScoreHP.getNext()) {
                    elev.retractSlides();

                    if (elev.areSlidesIn())
                        ScoreHP.tripNext();
                } else if (!ScoreHP.getNext()) {
                    elev.setHeight(mDefaultHeight);
                    if (elev.nearTarget())
                        done();
                }

                break;
            case SCOOT_CARGO_ON_GROUND:
                if (ScootCargoOnGround.not(1)) {
                    intake.extend();
                    if (intake.isExtended())
                        ScootCargoOnGround.tripNext();
                } else if (ScootCargoOnGround.notNext()) {
                    intake.runIntake(kIntake.SHOOTING_SPEED);
                    done();
                }
                break;
            case GRAB_CARGO_FROM_FEED:

                if (CargoFromFeed.not(1)) {
                    closeClaw();
                    if (elev.isClawClosed()) {
                        CargoFromFeed.tripNext();
                    }
                } else if (CargoFromFeed.notNext()) {
                    retractSlides();
                    if (elev.areSlidesIn()) {
                        CargoFromFeed.tripNext();
                    }
                } else if (CargoFromFeed.notNext()) {
                    elev.setHeight(mDefaultHeight);
                    if (elev.nearTarget()) {
                        done();
                    }
                }

                break;
            case THROW_CARGO:
                if (ThrowCargo.not(1)) {
                    elev.retractSlides();
                    if (elev.areSlidesIn())
                        ThrowCargo.tripNext();
                } else if (ThrowCargo.notNext()) {
                    elev.extendSlides();
                    elev.openClaw();
                    if (elev.areSlidesOut() && elev.isClawOpen()) {
                        ThrowCargo.tripNext();
                    }
                } else if (ThrowCargo.notNext()) {
                    elev.retractSlides();
                    if (elev.areSlidesOut()) {
                        done();
                        // ThrowCargo.tripNext();
                    }
                } else if (ThrowCargo.notNext()) {
                    // elev.setHeight(mDefaultHeight);
                    // if (elev.nearTarget()) {
                    // done();
                    // }
                }

                break;
            case STOW_LOW:
                if (isStowed() && elev.nearTarget())
                    done();
                break;
            case INTAKE_CARGO:
                if (IntakeCargo.not(1)) {
                    if (elev.isClawOpen() && elev.areSlidesIn() && intake.isExtended()) {
                        IntakeCargo.tripNext();
                    }
                } else if (!IntakeCargo.getNext()) {
                    runIntake(kIntake.INTAKE_SPEED);
                    IntakeCargo.tripNext();
                }
                break;
            case GRAB_CARGO_DURING_INTAKE:
                if (GrabCargoDuringIntake.not(1)) {
                    intake.retract();
                    if (intake.isRetracted())
                        GrabCargoDuringIntake.tripNext();
                } else if (GrabCargoDuringIntake.notNext()) {
                    closeClaw();
                    if (elev.isClawClosed())
                        GrabCargoDuringIntake.tripNext();
                } else if (GrabCargoDuringIntake.notNext()) {
                    GrabCargoDuringIntake.tripNext();
                    // elev.setHeight(Heights.Cargo_1);
                    // if (elev.nearTarget())
                    // GrabCargoDuringIntake.tripNext();
                } else if (GrabCargoDuringIntake.notNext()) {
                    elev.setHeight(Heights.HP_1);
                    if (elev.nearTarget())
                        done();
                }
                // if (GrabCargoDuringIntake.not(1)) {
                // intake.stop();
                // intake.retract();
                // if (intake.isRetracted())
                // GrabCargoDuringIntake.tripNext();

                // } else if (GrabCargoDuringIntake.notNext()) {
                // closeClaw();
                // if (elev.isClawClosed())
                // GrabCargoDuringIntake.tripNext();
                // } else if (GrabCargoDuringIntake.notNext()) {
                // intake.extend();
                // if (intake.isExtended())
                // GrabCargoDuringIntake.tripNext();
                // } else if (GrabCargoDuringIntake.notNext()) {
                // elev.setHeight(Heights.HP_1.inches + kElevator.CARGO_TRANSFER_JOG);
                // if (elev.nearTarget())
                // GrabCargoDuringIntake.tripNext();
                // } else if (GrabCargoDuringIntake.notNext()) {
                // intake.retract();
                // if (intake.isRetracted())
                // GrabCargoDuringIntake.tripNext();
                // } else if (GrabCargoDuringIntake.notNext()) {
                // elev.setHeight(Heights.HP_1);
                // if (elev.nearTarget())
                // done();
                // }
                break;
            case GRAB_HP_FROM_FEED:

                if (!HPFromFeed.get(1)) {
                    if (elev.isClawClosed()) {
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
                    retractSlides();
                    if (elev.areSlidesIn())
                        HPFromFeed.tripNext();
                } else if (!HPFromFeed.getNext()) {
                    elev.setHeight(Heights.HP_1);

                    if (elev.nearTarget())
                        done();
                } else {
                    done();
                }
                break;
            }
        }
    }

    private boolean isStowed() {

        return elev.areSlidesIn() /** && intake.isRaised() */
        ;
    }

    public void idle() {
        runAction(Actions.IDLE);
    }

    public void retrieveGamePiece(boolean queue) {

        if (Elevator.getInstance().isMovingHP() && !Superstructure.getInstance().isIntakingCargo()) {
            runAction(Actions.GRAB_HP_FROM_FEED, queue);
        } else if (!Elevator.getInstance().isMovingHP() && !Superstructure.getInstance().isIntakingCargo()) {
            runAction(Actions.GRAB_CARGO_FROM_FEED, queue);
        } else if (Superstructure.getInstance().isIntakingCargo()) {
            runAction(Actions.GRAB_CARGO_DURING_INTAKE, queue);
        }

    }

    public void scoreGamePiece(boolean queue) {

        if (Elevator.getInstance().isMovingHP()) {
            runAction(Actions.SCORE_HATCH, queue);
        } else if (!Elevator.getInstance().isMovingHP()) {
            runAction(Actions.THROW_CARGO, queue);
        }
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
        // intake.raise();
        elev.retractSlides();
        intake.stop();
    }

    public void zeroElevator() {
        elev.zero();
    }

    public void toggleIntake() {
        intake.toggle();
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

    public void pauseIntake() {
        intake.pauseIntake();
    }

}