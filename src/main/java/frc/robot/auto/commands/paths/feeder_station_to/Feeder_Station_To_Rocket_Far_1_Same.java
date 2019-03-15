package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Feeder_Station_To_Rocket_Far_1_Same extends PathContainer {
    public Feeder_Station_To_Rocket_Far_1_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(30, 298.28, 0, 30).setFieldAdaption(PathAdapter.feederStation));
        sWaypoints.add(new Waypoint(66.1565, 298.28, 15, 30).setFieldAdaption(PathAdapter.feederStation));
        sWaypoints.add(new Waypoint(248, 263, 15, 60));
        sWaypoints.add(new Waypoint(287, 247, 15, 60));
        sWaypoints.add(new Waypoint(287, 214, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}