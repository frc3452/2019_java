package frc.robot.subsystems;

import frc.robot.Constants.kIntake;
import frc.robot.GZOI;
import frc.robot.util.GZSubsystem;
import frc.robot.util.GZSubsystemManager;

public class Superstructure extends GZSubsystem {

    private Elevator elev = Elevator.getInstance();
    private Intake intake = Intake.getInstance();

    private GZSubsystemManager subsystems;

    private static Superstructure mInstance = null;
    public static Superstructure getInstance()
    {
        if (mInstance == null)
            mInstance = new Superstructure();
        return mInstance;
    }
    private Superstructure()
    {
        subsystems = new GZSubsystemManager(Elevator.getInstance(), Intake.getInstance());
    }


    private Actions mAction = Actions.IDLE;

    public enum Actions {
        OFF, IDLE, STOW, STOW_LOW, INTAKE_CARGO, HOLD_CARGO, TRNSFR_HP, GRAB_HP_FROM_FEED;
    }

    public Actions getCurrentAction()
    {
        return mAction;
    }

    @Override
    public void loop() {
        if (mAction == Actions.OFF || (this.isSafetyDisabled() && !GZOI.getInstance().isFMS()))
            stop();
        else {

            switch (mAction) {
            case IDLE:
                break;
            case INTAKE_CARGO:
                if (intake.isLowered() && elev.isHome())
                    intake.runIntake(kIntake.INTAKE_SPEED);

                if (elev.isCargoSensorTripped())
                    runAction(Actions.HOLD_CARGO);
                break;
            case HOLD_CARGO:
                if (elev.isClawClamped() && elev.isCargoSensorTripped()) {
                    intake.raise(true);
                    intake.stop();
                }
                break;
            }

        }
    }



    public void runAction(Actions action) {
        mAction = action;
        switch (action) {
        case INTAKE_CARGO:
            intake.raise(false);
            elev.goHome();
            break;
        case HOLD_CARGO:
            elev.setClaw(true);
            break;
        default:
            System.out.println("ERROR Unknown superstructure action:\t" + action.toString());
            break;
        }
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