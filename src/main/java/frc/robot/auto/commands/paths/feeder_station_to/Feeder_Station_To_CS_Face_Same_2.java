package frc.robot.auto.commands.paths.feeder_station_to;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Feeder_Station_To_CS_Face_Same_2 extends PathContainer {
    public Feeder_Station_To_CS_Face_Same_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(165, 298.28, 0, 60));
        sWaypoints.add(new Waypoint(131, 298.28, 15, 60));
        sWaypoints.add(new Waypoint(117, 230, 35, 60));
        sWaypoints.add(new Waypoint(130.073, 172.88, 25, 60).setFieldAdaption(PathAdapter.cargoShipFace));
        sWaypoints.add(new Waypoint(165.4115, 172.88, 1, 30).setFieldAdaption(PathAdapter.cargoShipFace));
        sWaypoints.add(new Waypoint(200.75, 172.88, 0, 30).setFieldAdaption(PathAdapter.cargoShipFace));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}