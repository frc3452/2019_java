package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;


public class CS_4 extends PathContainer {
    public CS_4() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(22, 298, 0, 0));
        sWaypoints.add(new Waypoint(63, 298, 15, 60));
        sWaypoints.add(new Waypoint(120, 286, 20, 60));
        sWaypoints.add(new Waypoint(155, 228, 25, 60));
        sWaypoints.add(new Waypoint(283, 228, 20, 60));
        sWaypoints.add(new Waypoint(283, 277, 0, 50));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}