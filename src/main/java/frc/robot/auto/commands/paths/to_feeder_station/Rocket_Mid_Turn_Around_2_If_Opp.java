package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Rocket_Mid_Turn_Around_2_If_Opp extends PathContainer {
    public Rocket_Mid_Turn_Around_2_If_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(264, 246, 0, 60));
        sWaypoints.add(new Waypoint(229.28, 257, 15, 30));
        sWaypoints.add(new Waypoint(156, 233, 15, 60));
        sWaypoints.add(new Waypoint(148, 202, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}