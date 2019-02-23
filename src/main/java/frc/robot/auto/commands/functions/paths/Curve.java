package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Curve extends PathContainer {
    public Curve() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(66.625, 205, 0, 0));
        sWaypoints.add(new Waypoint(154, 205.357, 30, 20));
        sWaypoints.add(new Waypoint(154, 115.557, 15, 60));
        sWaypoints.add(new Waypoint(154, 52.872, 15, 60));
        sWaypoints.add(new Waypoint(249, 52.872, 15, 60));
        sWaypoints.add(new Waypoint(249, 78.936, 15, 60));
        sWaypoints.add(new Waypoint(249, 105, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}