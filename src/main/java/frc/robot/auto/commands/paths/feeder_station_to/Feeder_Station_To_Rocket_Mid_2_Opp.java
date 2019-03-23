package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Feeder_Station_To_Rocket_Mid_2_Opp extends PathContainer {
    public Feeder_Station_To_Rocket_Mid_2_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(273, 101, 0, 60));
        sWaypoints.add(new Waypoint(254, 101, 15, 60));
        sWaypoints.add(new Waypoint(229.28, 78.008, 15, 60).setFieldAdaption(PathAdapter.rocketMid));
        sWaypoints.add(new Waypoint(229.28, 62.474, 1, 30).setFieldAdaption(PathAdapter.rocketMid));
        sWaypoints.add(new Waypoint(229.28, 46.94, 0, 30).setFieldAdaption(PathAdapter.rocketMid));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}