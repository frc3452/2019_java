package frc.robot.util.drivers.controllers;

import frc.robot.subsystems.Intake;
import frc.robot.util.drivers.GZJoystick;

public class OperatorController extends GZJoystick {

    public OperatorController() {
        this(1);
    }

    public ButtonCheck hatchPanel1, hatchPanel2, hatchPanel3, cargo1, cargo2, cargo3;

    public OperatorController(int port) {
        super(port);

        hatchPanel1 = new ButtonCheck(() -> this.aButton.isBeingPressed() && !this.leftCenterClick.isBeingPressed());
        hatchPanel2 = new ButtonCheck(() -> this.bButton.isBeingPressed() && !this.leftCenterClick.isBeingPressed());
        hatchPanel3 = new ButtonCheck(() -> this.yButton.isBeingPressed() && !this.leftCenterClick.isBeingPressed());

        cargo1 = new ButtonCheck(() -> this.aButton.isBeingPressed() && this.leftCenterClick.isBeingPressed());
        cargo2 = new ButtonCheck(() -> this.bButton.isBeingPressed() && this.leftCenterClick.isBeingPressed());
        cargo3 = new ButtonCheck(() -> this.yButton.isBeingPressed() && this.leftCenterClick.isBeingPressed());

        // this.intakeReverse = new GZButton(this, () -> false, () -> getDLeft());
        // this.intakeToggle = new GZButton(this, () -> false,
        // () -> getButton(Buttons.BACK) && Intake.getInstance().isExtended());
        // this.intakeCargo = new GZButton(this, () -> false,
        // () -> getButton(Buttons.BACK) && Intake.getInstance().isRetracted());

    }
}
