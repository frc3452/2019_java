package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Feeder_Station_To_CS_Side_Bay_1 extends PathContainer {
    public Feeder_Station_To_CS_Side_Bay_1() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(282.5, 53, 0, 30));
        sWaypoints.add(new Waypoint(260.75, 82.822, 15, 60).setFieldAdaption(PathAdapter.cargoShipBay1));
        sWaypoints.add(new Waypoint(260.75, 98.226, 1, 30).setFieldAdaption(PathAdapter.cargoShipBay1));
        sWaypoints.add(new Waypoint(260.75, 113.63, 0, 30).setFieldAdaption(PathAdapter.cargoShipBay1));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}