package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Hard_Curve extends PathContainer {
    public Hard_Curve() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67, 162, 0, 0));
        sWaypoints.add(new Waypoint(135, 162, 5, 30));
        sWaypoints.add(new Waypoint(135, 235, 5, 30));
        sWaypoints.add(new Waypoint(212, 235, 5, 30));
        sWaypoints.add(new Waypoint(211, 245, 5, 30));
        sWaypoints.add(new Waypoint(180, 245, 15, 30));
        sWaypoints.add(new Waypoint(133, 266, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}