package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Feeder_Station_To_Rocket_Far_2_Opp extends PathContainer {
    public Feeder_Station_To_Rocket_Far_2_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(295, 107, 0, 60));
        sWaypoints.add(new Waypoint(274, 107, 15, 60));
        sWaypoints.add(new Waypoint(289.866, 42.992, 15, 60).setFieldAdaption(PathAdapter.rocketFar));
        sWaypoints.add(new Waypoint(275.3280759396338, 35.01586158188823, 1, 30).setFieldAdaption(PathAdapter.rocketFar));
        sWaypoints.add(new Waypoint(260.79, 27.04, 0, 30).setFieldAdaption(PathAdapter.rocketFar));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}