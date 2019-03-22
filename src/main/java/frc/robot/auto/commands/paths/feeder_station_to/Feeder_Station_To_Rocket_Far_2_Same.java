package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Feeder_Station_To_Rocket_Far_2_Same extends PathContainer {
    public Feeder_Station_To_Rocket_Far_2_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(287, 219, 0, 60));
        sWaypoints.add(new Waypoint(287, 247, 15, 60));
        sWaypoints.add(new Waypoint(284, 284, 15, 60).setFieldAdaption(PathAdapter.rocketFar));
        sWaypoints.add(new Waypoint(268, 293, 0, 30).setFieldAdaption(PathAdapter.rocketFar));
        sWaypoints.add(new Waypoint(260.79, 296.96, 0, 30).setFieldAdaption(PathAdapter.rocketFar));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}