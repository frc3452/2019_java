package frc.robot.auto.commands.paths;

import java.util.ArrayList;

import frc.robot.auto.commands.AutoModeBuilder.EncoderMovement;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.ConfigurableDrive.Rotation2d;

public class Curve_test_path extends PathContainer {
    public Curve_test_path() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(22, 205, 0, 0));
        sWaypoints.add(new Waypoint(133, 205, 30, 25));
        sWaypoints.add(new Waypoint(134, 312, 0, 25));
    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    public boolean doesNeedZero() {
        return true;
    }


    @Override
    public Rotation2d getEndGyroMovement() {
        return Rotation2d.fromDegrees(10);
    }

    @Override
    public EncoderMovement getEndEncoderMovement() {
        return new EncoderMovement(-10);
    }
}