package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.pathadapter.PathAdapter;

public class Left_CS_Bay_1_Opp extends PathContainer {
    public Left_CS_Bay_1_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(165, 205, 15, 30));
        sWaypoints.add(new Waypoint(165, 80, 15, 60));
        sWaypoints.add(new Waypoint(260.75, 79.925, 15, 60));
        sWaypoints.add(new Waypoint(260.75, 96.7775, 1, 30));
        sWaypoints.add(new Waypoint(260.75, 113.63, 0, 30).setFieldAdaption(PathAdapter.cargoShipBay1));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}