package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;


public class CS_2 extends PathContainer {
    public CS_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(203, 173, 0, 0));
        sWaypoints.add(new Waypoint(172, 173, 10, 45));
        sWaypoints.add(new Waypoint(179, 209, 10, 45));
        sWaypoints.add(new Waypoint(204, 194, 0, 45));
    }

    @Override
    public boolean isReversed() {
        return true;
    }
}