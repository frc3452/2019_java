package frc.robot.util.drivers.controllers;

public class DriverController extends DeepSpaceController {

    public DriverController(double deadband) {
        super(0, deadband);
        initButtons();
    }
    
    private void initButtons() {
        this.queueAction = new GZButton(this, () -> false);
        this.elevatorHome = new GZButton(this, () -> false);
        this.hatchPannel1 = new GZButton(this, () -> false);
        this.hatchPanel2 = new GZButton(this, () -> false);
        this.hatchPanel3 = new GZButton(this, () -> false);
        this.cargo1 = new GZButton(this, () -> false);
        this.cargo2 = new GZButton(this, () -> false);
        this.cargo3 = new GZButton(this, () -> false);

        this.cargoShip = new GZButton(this, () -> false);

        this.intakeDown = new GZButton(this, () -> false);
        this.intakeUp = new GZButton(this, () -> false);
        this.slidesIn = new GZButton(this, () -> false);
        this.slidesOut = new GZButton(this, () -> false);
        this.clawOpen = new GZButton(this, () -> false);
        this.clawClosed = new GZButton(this, () -> false);

        this.stow = new GZButton(this, () -> false);
        this.stowLow = new GZButton(this, () -> false);
        this.intakeCargo = new GZButton(this, () -> false);
        this.floorHatchToManip = new GZButton(this, () -> false);
        this.hatchFromFeed = new GZButton(this, () -> false);
    }
}