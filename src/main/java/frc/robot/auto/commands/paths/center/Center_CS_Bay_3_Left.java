package frc.robot.auto.commands.paths.center;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Center_CS_Bay_3_Left extends PathContainer {
    public Center_CS_Bay_3_Left() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 162, 0, 0));
        sWaypoints.add(new Waypoint(141, 162, 15, 30));
        sWaypoints.add(new Waypoint(231, 254, 20, 60));
        sWaypoints.add(new Waypoint(304.25, 248.021, 17, 60).setFieldAdaption(PathAdapter.cargoShipBay3));
        sWaypoints.add(new Waypoint(304.25, 229.19549999999998, 1, 30).setFieldAdaption(PathAdapter.cargoShipBay3));
        sWaypoints.add(new Waypoint(304.25, 210.37, 0, 30).setFieldAdaption(PathAdapter.cargoShipBay3));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}