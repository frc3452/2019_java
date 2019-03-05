package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;

public class To_Feeder_Station_Same extends PathContainer {
    public To_Feeder_Station_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(163, 251, 0, 30));
        sWaypoints.add(new Waypoint(115.206, 298.28, 10, 60));
        sWaypoints.add(new Waypoint(67.35300000000001, 298.28, 1, 30));

        sWaypoints.add(new Waypoint(19.5, 298.28, 0, 30));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}