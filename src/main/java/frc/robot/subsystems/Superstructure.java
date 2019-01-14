package frc.robot.subsystems;

import frc.robot.Constants.kIntake;
import frc.robot.Constants.kElevator.Heights;
import edu.wpi.first.wpilibj.Compressor;
import frc.robot.GZOI;
import frc.robot.util.GZFlag;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZSubsystemManager;

public class Superstructure extends GZSubsystem {

    private Elevator elev = Elevator.getInstance();
    private Intake intake = Intake.getInstance();

    private GZSubsystemManager subsystems;

    private class Flags {
        private GZFlag HPFromFeed = new GZFlag();
        private GZFlag HPFromFloorFlag1 = new GZFlag();
        private GZFlag HPFromFloorFlag2 = new GZFlag();
    }

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

    public enum Actions {
        OFF, IDLE, STOW, STOW_LOW, INTAKE_CARGO, HOLD_CARGO, TRNSFR_HP_FROM_FLOOR, GRAB_HP_FROM_FEED;
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
                    intake.runIntake(kIntake.INTAKE_SPEED);

                if (elev.isCargoSensorTripped())
                    runAction(Actions.HOLD_CARGO);

                break;
            case HOLD_CARGO:
                if (elev.isClawClosed() && elev.isCargoSensorTripped())
                    stow();
                break;
            case TRNSFR_HP_FROM_FLOOR:
                elev.setHeight(Heights.Floor_HP);
                if (elev.nearTarget()) {
                    intake.stow();
                    if (intake.isRaised()) {
                        if (!mFlags.HPFromFloorFlag1.get()) {
                            elev.extendSlides();
                            mFlags.HPFromFloorFlag1.tripFlag();
                        } else if (elev.areSlidesOut() && mFlags.HPFromFloorFlag1.get()) {
                            elev.openClaw();
                            mFlags.HPFromFloorFlag2.tripFlag();
                        } else if (elev.isClawOpen() && mFlags.HPFromFloorFlag2.get()) {
                            elev.retractSlides();
                        }
                        if (mFlags.HPFromFloorFlag2.get() && elev.areSlidesIn()) {
                            idle();
                        }
                    }
                }
                break;
            case GRAB_HP_FROM_FEED:
                if (intake.isRaised() && elev.nearTarget() && elev.isClawClosed()) {

                    if (!mFlags.HPFromFeed.get()) {
                        elev.extendSlides();
                        mFlags.HPFromFeed.tripFlag();
                    } else if (elev.areSlidesOut()) {
                        elev.openClaw();
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

    private void stow() {
        intake.stow();
        elev.retractSlides();
    }

    public void runAction(Actions action) {
        mAction = action;
        switch (action) {
        case INTAKE_CARGO:
            intake.lower();
            elev.goHome();
            break;
        case STOW:
            stow();
            break;
        case STOW_LOW:
            stow();
            elev.goHome();
            break;
        case HOLD_CARGO:
            elev.closeClaw();
            break;
        case TRNSFR_HP_FROM_FLOOR:
            mFlags.HPFromFloorFlag1.rst();
            mFlags.HPFromFloorFlag2.rst();
            elev.closeClaw();
            break;
        case GRAB_HP_FROM_FEED:
            mFlags.HPFromFeed.rst();
            stow();
            elev.setHeight(Heights.Feeder_HP);
            elev.closeClaw();
            break;
        }
    }

    // MANUAL
    public void openClaw() {
        idle();
        elev.openClaw();
    }

    public void closeClaw() {
        idle();
        elev.closeClaw();
    }

    public void extendSlides() {
        idle();
        elev.extendSlides();
    }

    public void retractSlides() {
        idle();
        elev.retractSlides();
    }

    public void raiseIntake() {
        idle();
        intake.raise();
    }

    public void lowerIntake() {
        idle();
        intake.lower();
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

    public boolean hasMotors() {
        return false;
    }

    public boolean hasAir() {
        return false;
    }

    public void addLoggingValues() {
    }

    protected void initDefaultCommand() {
    }

}