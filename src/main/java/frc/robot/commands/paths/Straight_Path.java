package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;

public class Straight_Path extends PathContainer {

    public Straight_Path() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(0,205,0,0));
        sWaypoints.add(new Waypoint(75,205,15,60));
        sWaypoints.add(new Waypoint(145, 205, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}