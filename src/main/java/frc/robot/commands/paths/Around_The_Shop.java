package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Around_The_Shop extends PathContainer {
    public Around_The_Shop() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(0, 305, 0, 0));
        sWaypoints.add(new Waypoint(75, 305, 0, 100));
        sWaypoints.add(new Waypoint(195, 305, 15, 100));
        sWaypoints.add(new Waypoint(195, 182, 15, 100));
        sWaypoints.add(new Waypoint(215, 159, 15, 100));
        sWaypoints.add(new Waypoint(215, 117, 0, 100));
        sWaypoints.add(new Waypoint(220, 102, 0, 100));
        sWaypoints.add(new Waypoint(220, 88, 0, 100));
    }

    @Override
    public boolean isReversed() {
        return false;
    }

}