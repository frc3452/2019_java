package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.pathadapter.PathAdapter;

public class To_Feeder_Station_Same extends PathContainer {
    public To_Feeder_Station_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(131, 280, 0, 60));
        sWaypoints.add(new Waypoint(94, 298.28, 15, 60).setFieldAdaption(PathAdapter.feederStation));
        sWaypoints.add(new Waypoint(67.35300000000001, 298.28, 1, 30).setFieldAdaption(PathAdapter.feederStation));
        sWaypoints.add(new Waypoint(19.5, 298.28, 0, 30).setFieldAdaption(PathAdapter.feederStation));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}