package frc.robot.util.drivers.controllers;

import java.util.ArrayList;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.util.LatchedBoolean;

public class OperatorController extends DeepSpaceController {
    private boolean firstSet = false;

    private boolean isButtonBoard = true;
    private boolean p_isButtonBoard = false;

    public OperatorController() {
        this(1);
    }

    public OperatorController(int port) {
        super(port);

        this.queueAction = new GZButton(this, () -> false, () -> false);
        this.elevatorHome = new GZButton(this, () -> false, () -> false);
        this.hatchPannel1 = new GZButton(this, () -> false, () -> false);
        this.hatchPanel2 = new GZButton(this, () -> false, () -> false);
        this.hatchPanel3 = new GZButton(this, () -> false, () -> false);
        this.cargo1 = new GZButton(this, () -> false, () -> false);
        this.cargo2 = new GZButton(this, () -> false, () -> false);
        this.cargo3 = new GZButton(this, () -> false, () -> false);

        this.cargoShip = new GZButton(this, () -> false, () -> false);

        this.intakeDown = new GZButton(this, () -> false, () -> false);
        this.intakeUp = new GZButton(this, () -> false, () -> false);
        this.slidesIn = new GZButton(this, () -> false, () -> false);
        this.slidesOut = new GZButton(this, () -> false, () -> false);
        this.clawOpen = new GZButton(this, () -> false, () -> false);
        this.clawClosed = new GZButton(this, () -> false, () -> false);

        this.stow = new GZButton(this, () -> false, () -> false);
        this.stowLow = new GZButton(this, () -> false, () -> false);
        this.intakeCargo = new GZButton(this, () -> false, () -> false);
        this.floorHatchToManip = new GZButton(this, () -> false, () -> false);
        this.hatchFromFeed = new GZButton(this, () -> false, () -> false);
    }

    public void setButtonBoard(boolean isButtonBoard) {
        this.isButtonBoard = isButtonBoard;

        if (this.isButtonBoard != this.p_isButtonBoard || !firstSet) {
            System.out.println("WARNING Operator controller selected: "
                    + (this.isButtonBoard ? "Button board" : "Xbox controller"));
            for (GZButton b : allButtons)
                b.useSupplier1(isButtonBoard);
        }

        p_isButtonBoard = this.isButtonBoard;
        firstSet = true;
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