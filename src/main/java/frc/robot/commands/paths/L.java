package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class L extends PathContainer {
    public L() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67, 162, 0, 0));
        sWaypoints.add(new Waypoint(149, 163, 15, 15));
        sWaypoints.add(new Waypoint(206, 100, 45, 60));
        sWaypoints.add(new Waypoint(278, 161, 45, 60));
        sWaypoints.add(new Waypoint(206, 226, 45, 60));
        sWaypoints.add(new Waypoint(127, 186, 45, 60));
 
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}