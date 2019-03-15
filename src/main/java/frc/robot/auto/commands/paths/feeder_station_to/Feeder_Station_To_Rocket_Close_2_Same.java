package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Feeder_Station_To_Rocket_Close_2_Same extends PathContainer {
    public Feeder_Station_To_Rocket_Close_2_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(81, 265, 0, 60));
        sWaypoints.add(new Waypoint(148.89, 270.308, 15, 60));
        sWaypoints.add(new Waypoint(173.17997905314655, 283.6340381810009, 1, 30));
        sWaypoints.add(new Waypoint(197.47, 296.96, 0, 30));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}