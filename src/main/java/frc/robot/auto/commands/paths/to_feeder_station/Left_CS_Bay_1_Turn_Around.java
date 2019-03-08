package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class Left_CS_Bay_1_Turn_Around extends PathContainer {
    public Left_CS_Bay_1_Turn_Around() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(260.75, 210.37, 0, 30).setFieldAdaption(PathAdapter.cargoShipBay1));
        sWaypoints.add(new Waypoint(260.75, 243, 15, 30).setFieldAdaption(PathAdapter.cargoShipBay1));
        sWaypoints.add(new Waypoint(290, 243, 0, 30).setFieldAdaption(PathAdapter.cargoShipBay1));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}