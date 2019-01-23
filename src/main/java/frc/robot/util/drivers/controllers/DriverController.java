package frc.robot.util.drivers.controllers;

public class DriverController extends DeepSpaceController {

    public DriverController() {
        super(0);
        initButtons();
    }
    
    private void initButtons() {
        this.queueAction = new GZButton(this, () -> this.getRawButton(-1));
        this.elevatorHome = new GZButton(this, () -> this.getRawButton(-1));
        this.hatchPannel1 = new GZButton(this, () -> this.getRawButton(-1));
        this.hatchPanel2 = new GZButton(this, () -> this.getRawButton(-1));
        this.hatchPanel3 = new GZButton(this, () -> this.getRawButton(-1));
        this.cargo1 = new GZButton(this, () -> this.getRawButton(-1));
        this.cargo2 = new GZButton(this, () -> this.getRawButton(-1));
        this.cargo3 = new GZButton(this, () -> this.getRawButton(-1));

        this.cargoShip = new GZButton(this, () -> this.getRawButton(-1));

        this.intakeDown = new GZButton(this, () -> this.getRawButton(-1));
        this.intakeUp = new GZButton(this, () -> this.getRawButton(-1));
        this.slidesIn = new GZButton(this, () -> this.getRawButton(1));
        this.slidesOut = new GZButton(this, () -> this.getRawButton(4));
        this.clawOpen = new GZButton(this, () -> this.getRawButton(3));
        this.clawClosed = new GZButton(this, () -> this.getRawButton(2));

        this.stow = new GZButton(this, () -> this.getRawButton(-1));
        this.stowLow = new GZButton(this, () -> this.getRawButton(-1));
        this.intakeCargo = new GZButton(this, () -> this.getRawButton(-1));
        this.floorHatchToManip = new GZButton(this, () -> this.getRawButton(-1));
        this.hatchFromFeed = new GZButton(this, () -> this.getRawButton(-1));
    }
}