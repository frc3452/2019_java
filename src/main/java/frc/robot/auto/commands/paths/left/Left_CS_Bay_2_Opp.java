package frc.robot.auto.commands.paths.left;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Left_CS_Bay_2_Opp extends PathContainer {
    public Left_CS_Bay_2_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(133, 205, 15, 30));
        sWaypoints.add(new Waypoint(223, 72, 30, 60));
        sWaypoints.add(new Waypoint(282.5, 71.757, 20, 60).setFieldAdaption(PathAdapter.cargoShipBay2));
        sWaypoints.add(new Waypoint(282.5, 92.6935, 1, 30).setFieldAdaption(PathAdapter.cargoShipBay2));
        sWaypoints.add(new Waypoint(282.5, 113.63, 0, 30).setFieldAdaption(PathAdapter.cargoShipBay2));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}