package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;


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