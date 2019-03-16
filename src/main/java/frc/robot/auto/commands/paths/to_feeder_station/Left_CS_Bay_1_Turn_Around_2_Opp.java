package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Left_CS_Bay_1_Turn_Around_2_Opp extends PathContainer {
    public Left_CS_Bay_1_Turn_Around_2_Opp() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(280, 243, 0, 30));
        sWaypoints.add(new Waypoint(260.75, 243, 1, 30));
        sWaypoints.add(new Waypoint(221, 243, 15, 60));
        sWaypoints.add(new Waypoint(163, 247, 15, 60));
        sWaypoints.add(new Waypoint(153, 214, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}