package frc.robot.util.drivers.controllers;

public class DriverController extends DeepSpaceController {

    public DriverController(double deadband) {
        super(0, deadband);
        initButtons();
    }

    private void initButtons() {
    }
}