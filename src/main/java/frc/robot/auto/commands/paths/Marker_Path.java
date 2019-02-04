package frc.robot.auto.commands.paths;

import java.util.ArrayList;

import frc.robot.auto.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Marker_Path extends PathContainer {

    public Marker_Path() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(0,205,0,0));
        sWaypoints.add(new Waypoint(75,205,15,60, "Marker1"));
        sWaypoints.add(new Waypoint(145, 205, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}