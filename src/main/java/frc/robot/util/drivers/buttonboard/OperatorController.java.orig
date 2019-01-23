package frc.robot.util.drivers.buttonboard;

import java.util.ArrayList;

import javax.management.QueryEval;

import edu.wpi.first.wpilibj.Joystick;

public class OperatorController extends Joystick {
    private boolean isButtonBoard = true;
    private boolean p_isButtonBoard = false;

    private ArrayList<GZButton> allButtons = new ArrayList<GZButton>();

    public GZButton queueAction = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
    public GZButton elevatorHome = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));

    public GZButton hatchPannel1 = new GZButton(this, () -> this.getRawButton(1), () -> this.getRawButton(1));
    public GZButton hatchPanel2 = new GZButton(this, () -> this.getRawButton(2), () -> this.getRawButton(2));
    public GZButton hatchPanel3 = new GZButton(this, () -> this.getRawButton(3), () -> this.getRawButton(4));
    public GZButton cargo1 = new GZButton(this, () -> this.getRawButton(4),
            () -> this.getRawButton(5) && this.getRawButton(1));
    public GZButton cargo2 = new GZButton(this, () -> this.getRawButton(5),
            () -> this.getRawButton(5) && this.getRawButton(2));
    public GZButton cargo3 = new GZButton(this, () -> this.getRawButton(6),
            () -> this.getRawButton(5) && this.getRawButton(4));

    public GZButton cargoShip = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));

    public GZButton intakeDown = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
    public GZButton slidesOut = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
    public GZButton slidesIn = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
    public GZButton intakeUp = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
    public GZButton clawOpen = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
    public GZButton clawClosed = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));

    public GZButton stow = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
    public GZButton stowLow = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
    public GZButton intakeCargo = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
    public GZButton floorHatchToManip = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));
    public GZButton hatchFromFeed = new GZButton(this, () -> this.getRawButton(-1), () -> this.getRawButton(-1));

    protected void addButton(GZButton b) {
        allButtons.add(b);
    }

    public OperatorController() {
        this(1);
    }

    public void setButtonBoard(boolean buttonBoard) {
        this.isButtonBoard = buttonBoard;

        // System.out.println
        if (this.isButtonBoard != this.p_isButtonBoard) {
            System.out.println("WARNING Operator controller selected: "
                    + (this.isButtonBoard ? "Button board" : "Xbox controller"));
            for (GZButton b : allButtons)
                b.useSupplier1(this.isButtonBoard);
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

    public OperatorController(int port) {
        super(port);
    }

}