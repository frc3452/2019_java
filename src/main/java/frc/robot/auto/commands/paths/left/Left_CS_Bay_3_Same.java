package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Left_CS_Bay_3_Same extends PathContainer {
    public Left_CS_Bay_3_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(133, 205, 15, 30));
        sWaypoints.add(new Waypoint(223, 252, 30, 60));
        sWaypoints.add(new Waypoint(304.25, 251.019, 20, 60).setFieldAdaption(PathAdapter.cargoShipBay3));
        sWaypoints.add(new Waypoint(304.25, 230.6945, 1, 30).setFieldAdaption(PathAdapter.cargoShipBay3));
        sWaypoints.add(new Waypoint(304.25, 210.37, 0, 30).setFieldAdaption(PathAdapter.cargoShipBay3));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}