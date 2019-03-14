package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Rocket_Far_Turn_Around_2 extends PathContainer {
    public Rocket_Far_Turn_Around_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(293, 242, 0, 60));
        sWaypoints.add(new Waypoint(288, 271, 15, 30));
        sWaypoints.add(new Waypoint(250, 271, 15, 60));
        sWaypoints.add(new Waypoint(218, 262, 15, 60));
        sWaypoints.add(new Waypoint(195, 269, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}