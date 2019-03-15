package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Rocket_Close_Turn_Around_Same extends PathContainer {
    public Rocket_Close_Turn_Around_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(192, 294, 0, 30));
        sWaypoints.add(new Waypoint(173.71399489541494, 283.9270093044256, 15, 30));
        sWaypoints.add(new Waypoint(200, 266, 15, 60));
        sWaypoints.add(new Waypoint(220, 257, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}