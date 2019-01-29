package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Straight_Path_Backwards extends PathContainer {

    public Straight_Path_Backwards() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(130.0, 205, 0, 60));
        sWaypoints.add(new Waypoint(65.0, 205, 15, 60));
        sWaypoints.add(new Waypoint(0, 205, 0, 0));
    }

    @Override
    public Rotation2d getStartRotation() {
        return Rotation2d.fromDegrees(0);
    }

    @Override
    public boolean isReversed() {
        return true;
    }

  
}