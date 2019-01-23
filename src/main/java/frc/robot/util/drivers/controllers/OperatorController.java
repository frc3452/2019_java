package frc.robot.util.drivers.controllers;

import java.util.ArrayList;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.util.LatchedBoolean;

public class OperatorController extends DeepSpaceController {
    private boolean isButtonBoard = true;
    private boolean p_isButtonBoard = false;

    public OperatorController() {
        this(1);
    }

    public OperatorController(int port) {
        super(port);

        this.queueAction = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.elevatorHome = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.hatchPannel1 = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.hatchPanel2 = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.hatchPanel3 = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.cargo1 = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.cargo2 = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.cargo3 = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));

        this.cargoShip = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));

        this.intakeDown = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.intakeUp = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.slidesIn = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.slidesOut = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.clawOpen = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.clawClosed = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));

        this.stow = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.stowLow = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.intakeCargo = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.floorHatchToManip = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
        this.hatchFromFeed = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
    }

    public void setButtonBoard(boolean isButtonBoard) {
        this.isButtonBoard = isButtonBoard;

        if (this.isButtonBoard != this.p_isButtonBoard) {
            System.out.println("WARNING Operator controller selected: "
                    + (this.isButtonBoard ? "Button board" : "Xbox controller"));
            for (GZButton b : allButtons)
                b.useSupplier1(isButtonBoard);
        }

        p_isButtonBoard = this.isButtonBoard;
    }

    public void setButtonBoard() {
        setButtonBoard(true);
    }

    public boolean isButtonBoard() {
        return isButtonBoard;
    }

    public void setXboxController() {
        setButtonBoard(false);
    }
}