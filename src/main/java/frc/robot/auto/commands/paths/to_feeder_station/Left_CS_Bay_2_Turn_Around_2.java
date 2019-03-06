package frc.robot.auto.commands.paths.to_feeder_station;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Left_CS_Bay_2_Turn_Around_2 extends PathContainer {
    public Left_CS_Bay_2_Turn_Around_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(282.5, 275, 0, 0));
        sWaypoints.add(new Waypoint(282.5, 244, 15, 60));
        sWaypoints.add(new Waypoint(210, 266, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}