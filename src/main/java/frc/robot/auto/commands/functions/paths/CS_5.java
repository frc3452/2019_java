package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;


public class CS_5 extends PathContainer {
    public CS_5() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(283, 277, 1, 0));
        sWaypoints.add(new Waypoint(283, 243, 1, 45));
        sWaypoints.add(new Waypoint(283, 209, 1, 35));
        // sWaypoints.add(PathAdapter.getHABBay(new Waypoint(283, 209, 1, 35), 2, true));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}