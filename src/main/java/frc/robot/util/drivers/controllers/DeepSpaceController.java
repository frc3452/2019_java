package frc.robot.util.drivers.controllers;

import java.util.ArrayList;

import frc.robot.util.drivers.GZJoystick;

public class DeepSpaceController extends GZJoystick {
    public DeepSpaceController(int port) {
        super(port);
    }

    public DeepSpaceController(int port, double deadband) {
        super(port, deadband);
    }

    protected void addButton(GZButton b) {
        allButtons.add(b);
    }

    public GZButton queueAction;
    public GZButton elevatorHome;

    public GZButton hatchPannel1;
    public GZButton hatchPanel2;
    public GZButton hatchPanel3;
    public GZButton cargo1;
    public GZButton cargo2;
    public GZButton cargo3;

    public GZButton cargoShip;

    public GZButton intakeDown;
    public GZButton intakeUp;
    public GZButton slidesOut;
    public GZButton slidesIn;
    public GZButton clawOpen;
    public GZButton clawClosed;

    public GZButton stow;
    public GZButton stowLow;
    public GZButton intakeCargo;
    public GZButton floorHatchToManip;
    public GZButton hatchFromFeed;

    public GZButton it;

    protected ArrayList<GZButton> allButtons = new ArrayList<GZButton>();
}