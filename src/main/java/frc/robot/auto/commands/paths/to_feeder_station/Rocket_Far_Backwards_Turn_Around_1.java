package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Rocket_Far_Backwards_Turn_Around_1 extends PathContainer {
    public Rocket_Far_Backwards_Turn_Around_1() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(275, 287, 0, 0));
        sWaypoints.add(new Waypoint(295, 275, 0, 90));
    }

    @Override
    public boolean isReversed() {
        return true;
    }

    @Override
    public Rotation2d getStartGyroMovement() {
        return Rotation2d.fromDegrees(180);
    }
}