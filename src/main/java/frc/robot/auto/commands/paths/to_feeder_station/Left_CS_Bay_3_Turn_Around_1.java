package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Left_CS_Bay_3_Turn_Around_1 extends PathContainer {
    public Left_CS_Bay_3_Turn_Around_1() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(304.25, 210.37, 0, 0).setFieldAdaption(PathAdapter.cargoShipBay3));
        sWaypoints.add(new Waypoint(304.25, 247, 0, 60).setFieldAdaption(PathAdapter.cargoShipBay3));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}