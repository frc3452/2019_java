package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Feeder_Station_To_CS_Side_Bay_3 extends PathContainer {
    public Feeder_Station_To_CS_Side_Bay_3() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(282.5, 53, 0, 30));
        sWaypoints.add(new Waypoint(304.25, 82.822, 15, 60));
        sWaypoints.add(new Waypoint(304.25, 98.226, 1, 30));
        sWaypoints.add(new Waypoint(304.25, 113.63, 0, 30));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}