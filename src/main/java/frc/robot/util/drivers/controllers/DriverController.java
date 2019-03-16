package frc.robot.util.drivers.controllers;

public class DriverController extends DeepSpaceController {

    public DriverController(double deadband) {
        super(0, deadband);
        initButtons();
    }

    private void initButtons() {
        this.queueAction = new GZButton(this, () -> false);
        this.idle = new GZButton(this, () -> false);
        this.cancel = new GZButton(this, () -> false);

        this.elevatorJogUp = new GZButton(this, () -> getDUp());
        this.elevatorJogDown = new GZButton(this, () -> getDDown());

        this.cargo1 = new GZButton(this, () -> getButton(Buttons.RB) && getButton(Buttons.X));
        this.cargo2 = new GZButton(this, () -> getButton(Buttons.RB) && getButton(Buttons.B));
        this.cargo3 = new GZButton(this, () -> getButton(Buttons.RB) && getButton(Buttons.Y));
        this.hatchPanel1 = new GZButton(this, () -> getButton(Buttons.X) && getButton(Buttons.LB));
        this.hatchPanel2 = new GZButton(this, () -> getButton(Buttons.B) && getButton(Buttons.LB));
        this.hatchPanel3 = new GZButton(this, () -> getButton(Buttons.Y) && getButton(Buttons.LB));

        this.cargoShip = new GZButton(this, () -> false);

        this.intakeDown = new GZButton(this, () -> false);
        this.intakeUp = new GZButton(this, () -> getButtons(Buttons.RB) && getDLeft());

        this.clawToggle = new GZButton(this,
                () -> getButtons(Buttons.B) && !getButton(Buttons.LB) && !getButton(Buttons.RB));
        this.slidesToggle = new GZButton(this,
                () -> getButtons(Buttons.X) && !getButton(Buttons.LB) && !getButton(Buttons.RB));
        this.stow = new GZButton(this, () -> false);
        this.intakeCargo = new GZButton(this, () -> getDRight() && getButtons(Buttons.RB));
        this.floorHatchToManip = new GZButton(this, () -> false);
        this.hatchFromFeed = new GZButton(this, () -> false);
        this.dropCrawler = new GZButton(this, () -> false);
    }
}