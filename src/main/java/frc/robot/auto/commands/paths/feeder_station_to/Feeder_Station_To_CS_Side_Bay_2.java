package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Feeder_Station_To_CS_Side_Bay_2 extends PathContainer {
    public Feeder_Station_To_CS_Side_Bay_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(282.5, 50.525, 0, 30));
        sWaypoints.add(new Waypoint(282.5, 82.0775, 0, 30).setFieldAdaption(PathAdapter.cargoShipBay2));
        sWaypoints.add(new Waypoint(282.5, 96.63, 0, 30).setFieldAdaption(PathAdapter.cargoShipBay2));
        sWaypoints.add(new Waypoint(282.5, 113.63, 0, 30).setFieldAdaption(PathAdapter.cargoShipBay2));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}