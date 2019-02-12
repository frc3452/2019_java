package frc.robot.auto.commands.paths; import java.util.ArrayList;

import frc.robot.auto.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.drive.pathfollowing.PathContainer;

public class CS_1 extends PathContainer {
    public CS_1() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(66.625, 162, 0, 0));
        sWaypoints.add(new Waypoint(134, 162, 15, 50));
        sWaypoints.add(new Waypoint(172, 173, 15, 60));
        sWaypoints.add(new Waypoint(203, 173, 0, 60));
    }   

    @Override
    public boolean isReversed() {
        return false;
    }
}