package frc.robot.util.drivers.controllers;

import frc.robot.subsystems.Intake;
import frc.robot.util.drivers.GZJoystick;

public class OperatorController extends GZJoystick {

    public OperatorController() {
        this(1);
    }

    public OperatorController(int port) {
        super(port);
    }
}
