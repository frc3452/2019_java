package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class To_Feeder_Station_Opp extends PathContainer {
    public To_Feeder_Station_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(19.5, 25.72, 0, 30));
        sWaypoints.add(new Waypoint(50, 25.72, 15, 30));
        sWaypoints.add(new Waypoint(82, 27, 15, 60));
        sWaypoints.add(new Waypoint(110, 55, 15, 60).setFieldAdaption(PathAdapter.feederStation));
        sWaypoints.add(new Waypoint(130, 131, 15, 60).setFieldAdaption(PathAdapter.feederStation));
        sWaypoints.add(new Waypoint(142, 177, 0, 60).setFieldAdaption(PathAdapter.feederStation));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}