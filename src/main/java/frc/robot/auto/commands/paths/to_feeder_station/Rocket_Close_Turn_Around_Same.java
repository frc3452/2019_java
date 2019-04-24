package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.AutoModeBuilder.EncoderMovement;
import frc.robot.auto.commands.functions.drive.EncoderMovementCommand;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.pathadapter.PathAdapter;
import frc.robot.poofs.util.math.Rotation2d;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;

public class Rocket_Close_Turn_Around_Same extends PathContainer {
    public Rocket_Close_Turn_Around_Same() {
    
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(183, 288, 0, 30));
        sWaypoints.add(new Waypoint(157, 272, 15, 30));
        sWaypoints.add(new Waypoint(66.116, 298.28, 15, 60));
        sWaypoints.add(new Waypoint(32, 298.28, 0, 30).setFieldAdaption(PathAdapter.feederStation));
    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    public Rotation2d getStartGyroMovement() {
        return Rotation2d.fromDegrees(90 + 61.0);
    }

    @Override
    public EncoderMovement getStartEncoderMovement() {
        return new EncoderMovement(-10);
    }
}