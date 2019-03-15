package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Feeder_Station_To_Rocket_Mid_1_Same extends PathContainer {
    public Feeder_Station_To_Rocket_Mid_1_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(25, 299, 0, 30).setFieldAdaption(PathAdapter.feederStation));
        sWaypoints.add(new Waypoint(66.1565, 299, 15, 30).setFieldAdaption(PathAdapter.feederStation));
        sWaypoints.add(new Waypoint(116, 290, 15, 60));
        sWaypoints.add(new Waypoint(229.28, 252, 15, 60));
        sWaypoints.add(new Waypoint(229.28, 213, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}