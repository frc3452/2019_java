package frc.robot.auto.commands.paths.center;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.pathadapter.PathAdapter;

public class Center_CS_Bay_1_Left extends PathContainer {
    public Center_CS_Bay_1_Left() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 162, 0, 0));
        sWaypoints.add(new Waypoint(172, 162, 30, 30));
        sWaypoints.add(new Waypoint(196, 247.191, 30, 60));
        sWaypoints.add(new Waypoint(260.75, 247.191, 15, 60));
        sWaypoints.add(new Waypoint(260.75, 228.78050000000002, 1, 30));
        sWaypoints.add(new Waypoint(260.75, 210.37, 0, 30).setFieldAdaption(PathAdapter.cargoShipBay1));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}