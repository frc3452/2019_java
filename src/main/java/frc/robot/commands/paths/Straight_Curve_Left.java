package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Straight_Curve_Left extends PathContainer {
    public Straight_Curve_Left() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(20, 48, 0, 0));
        sWaypoints.add(new Waypoint(120, 48, 1, 40));
        sWaypoints.add(new Waypoint(232, 43, 40, 40));
        sWaypoints.add(new Waypoint(232, 103, 1, 40));
        sWaypoints.add(new Waypoint(232, 190, 0, 40));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}