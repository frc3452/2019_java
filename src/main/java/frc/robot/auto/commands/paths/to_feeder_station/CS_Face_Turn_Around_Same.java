package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;

import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;

public class CS_Face_Turn_Around_Same extends PathContainer {
    public CS_Face_Turn_Around_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(190.75, 172.88, 0, 30));
        sWaypoints.add(new Waypoint(169, 175, 15, 30));
        sWaypoints.add(new Waypoint(169, 227, 30, 60));
        sWaypoints.add(new Waypoint(203, 198, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}