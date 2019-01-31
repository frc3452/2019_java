package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Straight_Curve_Left extends PathContainer {
    public Straight_Curve_Left() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(20, 48, 0, 0));
        sWaypoints.add(new Waypoint(120, 48, 0, 80));
        sWaypoints.add(new Waypoint(232, 43, 40, 80));
        sWaypoints.add(new Waypoint(232, 103, 1, 80));
        sWaypoints.add(new Waypoint(232, 190, 0, 0));
    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    public Rotation2d getStartRotation() {
        return Rotation2d.fromDegrees(0);
    }
}