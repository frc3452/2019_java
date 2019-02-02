package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;

public class Figure_8_2 extends PathContainer {
    public Figure_8_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(90, 213, 1, 0));
        sWaypoints.add(new Waypoint(121, 213, 1, 30));
        sWaypoints.add(new Waypoint(145, 217, 15, 30));
        sWaypoints.add(new Waypoint(182, 252, 30, 30));
        sWaypoints.add(new Waypoint(220, 215, 1, 30));
        sWaypoints.add(new Waypoint(257, 176, 30, 30));
        sWaypoints.add(new Waypoint(305, 220, 27, 30));
        sWaypoints.add(new Waypoint(257, 251, 15, 30));
        sWaypoints.add(new Waypoint(182, 176, 15, 30));
        sWaypoints.add(new Waypoint(145, 217, 15, 30));
        sWaypoints.add(new Waypoint(182, 252, 30, 30));
        sWaypoints.add(new Waypoint(220, 215, 1, 30));
        sWaypoints.add(new Waypoint(257, 176, 30, 30));
        sWaypoints.add(new Waypoint(305, 220, 27, 30));
        sWaypoints.add(new Waypoint(257, 251, 15, 30));
        sWaypoints.add(new Waypoint(182, 176, 15, 30));
        sWaypoints.add(new Waypoint(145, 217, 15, 30));
        sWaypoints.add(new Waypoint(182, 252, 30, 30));
        sWaypoints.add(new Waypoint(220, 215, 1, 30));
        sWaypoints.add(new Waypoint(257, 176, 30, 30));
        sWaypoints.add(new Waypoint(305, 220, 27, 30));
        sWaypoints.add(new Waypoint(257, 251, 15, 30));
        sWaypoints.add(new Waypoint(182, 176, 15, 30));
        sWaypoints.add(new Waypoint(145, 217, 15, 30));
        sWaypoints.add(new Waypoint(182, 252, 30, 30));
        sWaypoints.add(new Waypoint(220, 215, 1, 30));
        sWaypoints.add(new Waypoint(257, 176, 30, 30));
        sWaypoints.add(new Waypoint(305, 220, 27, 30));
        sWaypoints.add(new Waypoint(257, 251, 15, 30));
        sWaypoints.add(new Waypoint(182, 176, 15, 30));
        sWaypoints.add(new Waypoint(145, 217, 15, 30));
        sWaypoints.add(new Waypoint(182, 252, 30, 30));
        sWaypoints.add(new Waypoint(220, 215, 1, 30));
        sWaypoints.add(new Waypoint(257, 176, 30, 30));
        sWaypoints.add(new Waypoint(305, 220, 27, 30));
        sWaypoints.add(new Waypoint(257, 251, 15, 30));
        sWaypoints.add(new Waypoint(182, 176, 15, 30));
        sWaypoints.add(new Waypoint(145, 217, 15, 30));
        sWaypoints.add(new Waypoint(182, 252, 30, 30));
        sWaypoints.add(new Waypoint(220, 215, 1, 30));
        sWaypoints.add(new Waypoint(257, 176, 30, 30));
        sWaypoints.add(new Waypoint(305, 220, 27, 30));
        sWaypoints.add(new Waypoint(257, 251, 15, 30));
        sWaypoints.add(new Waypoint(182, 176, 15, 30));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}