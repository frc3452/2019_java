package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Feeder_Station_To_Rocket_Close_1_Opp extends PathContainer {
    public Feeder_Station_To_Rocket_Close_1_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(25, 298.28, 0, 30).setFieldAdaption(PathAdapter.feederStation));
        sWaypoints.add(new Waypoint(66.1565, 298.28, 15, 30).setFieldAdaption(PathAdapter.feederStation));
        sWaypoints.add(new Waypoint(124, 257, 15, 60));
        sWaypoints.add(new Waypoint(124, 98, 15, 60));
        sWaypoints.add(new Waypoint(117, 64, 15, 60));
        sWaypoints.add(new Waypoint(72, 64, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}