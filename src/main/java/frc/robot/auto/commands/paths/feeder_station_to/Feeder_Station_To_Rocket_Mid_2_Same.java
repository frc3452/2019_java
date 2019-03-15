package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Feeder_Station_To_Rocket_Mid_2_Same extends PathContainer {
    public Feeder_Station_To_Rocket_Mid_2_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(229.28, 219, 0, 60));
        sWaypoints.add(new Waypoint(229.28, 251.896, 0, 60));
        sWaypoints.add(new Waypoint(229.28, 264.478, 0, 30));
        sWaypoints.add(new Waypoint(229.28, 277.06, 0, 30));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}