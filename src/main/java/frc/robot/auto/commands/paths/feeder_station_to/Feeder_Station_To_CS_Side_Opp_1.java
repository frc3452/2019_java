package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Feeder_Station_To_CS_Side_Opp_1 extends PathContainer {
    public Feeder_Station_To_CS_Side_Opp_1() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(29.5, 298.28, 0, 0).setFieldAdaption(PathAdapter.feederStation));
        sWaypoints.add(new Waypoint(66.1565, 298.28, 15, 30).setFieldAdaption(PathAdapter.feederStation));
        sWaypoints.add(new Waypoint(134, 244, 15, 60));
        sWaypoints.add(new Waypoint(182, 124, 15, 60));
        sWaypoints.add(new Waypoint(282.5, 95, 15, 60));
        sWaypoints.add(new Waypoint(282.5, 51, 0, 30));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}