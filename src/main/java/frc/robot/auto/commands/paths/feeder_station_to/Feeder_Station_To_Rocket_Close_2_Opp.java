package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Feeder_Station_To_Rocket_Close_2_Opp extends PathContainer {
    public Feeder_Station_To_Rocket_Close_2_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(82, 64, 0, 60));
        sWaypoints.add(new Waypoint(117, 64, 15, 60));
        sWaypoints.add(new Waypoint(167, 43, 15, 60).setFieldAdaption(PathAdapter.rocketNear));
        sWaypoints.add(new Waypoint(185.9990869109826, 33.33315842039668, 0, 30).setFieldAdaption(PathAdapter.rocketNear));
        sWaypoints.add(new Waypoint(197.47, 27.04, 0, 30).setFieldAdaption(PathAdapter.rocketNear));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}