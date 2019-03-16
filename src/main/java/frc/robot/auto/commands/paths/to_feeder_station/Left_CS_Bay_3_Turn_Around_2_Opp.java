package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Left_CS_Bay_3_Turn_Around_2_Opp extends PathContainer {
    public Left_CS_Bay_3_Turn_Around_2_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(304.25, 240, 0, 0));
        sWaypoints.add(new Waypoint(304.25, 220, 15, 60));
        sWaypoints.add(new Waypoint(231, 220, 15, 60));
        sWaypoints.add(new Waypoint(154, 223, 15, 60));
        sWaypoints.add(new Waypoint(145, 188, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}