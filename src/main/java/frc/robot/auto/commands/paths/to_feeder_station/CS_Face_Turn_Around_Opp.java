package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.pathadapter.PathAdapter;

public class CS_Face_Turn_Around_Opp extends PathContainer {
    public CS_Face_Turn_Around_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(190.75, 172.88, 0, 30).setFieldAdaption(PathAdapter.cargoShipFace));
        sWaypoints.add(new Waypoint(154, 172, 15, 30).setFieldAdaption(PathAdapter.cargoShipFace));
        sWaypoints.add(new Waypoint(154, 140, 15, 60));
        sWaypoints.add(new Waypoint(183, 114, 15, 60));
        sWaypoints.add(new Waypoint(193, 133, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}