package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Rocket_Close_Turn_Around_2_If_Opp extends PathContainer {
    public Rocket_Close_Turn_Around_2_If_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(220, 257, 0, 60));
        sWaypoints.add(new Waypoint(200, 266, 15, 60));
        sWaypoints.add(new Waypoint(160, 249, 15, 60));
        sWaypoints.add(new Waypoint(153, 222, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}