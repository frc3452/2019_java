package frc.robot.auto.commands.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Left_CS_Bay_1_Same extends PathContainer {
    public Left_CS_Bay_1_Same() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67.5, 205, 0, 0));
        sWaypoints.add(new Waypoint(136, 205, 15, 60));
        sWaypoints.add(new Waypoint(160.961, 248.199, 15, 60));
        sWaypoints.add(new Waypoint(210.8555, 248.199, 1, 30));
        sWaypoints.add(new Waypoint(260.75, 248.199, 15, 60));
        sWaypoints.add(new Waypoint(260.75, 229.2845, 1, 30));
        sWaypoints.add(new Waypoint(260.75, 210.37, 0, 30));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}