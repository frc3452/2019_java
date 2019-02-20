package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Figure_8_Yeah extends PathContainer {
    public Figure_8_Yeah() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(59, 259, 0, 0));
        sWaypoints.add(new Waypoint(60, 259, 0, 0));
        sWaypoints.add(new Waypoint(105, 258, 15, 60));
        sWaypoints.add(new Waypoint(146, 290, 15, 60));
        sWaypoints.add(new Waypoint(187, 258, 15, 60));
        sWaypoints.add(new Waypoint(146, 215, 15, 60));
        sWaypoints.add(new Waypoint(105, 174, 15, 60));
        sWaypoints.add(new Waypoint(146, 133, 15, 60));
        sWaypoints.add(new Waypoint(187, 174, 15, 60));

        sWaypoints.add(new Waypoint(105, 258, 15, 60));
        sWaypoints.add(new Waypoint(146, 290, 15, 60));
        sWaypoints.add(new Waypoint(187, 258, 15, 60));
        sWaypoints.add(new Waypoint(146, 215, 15, 60));
        sWaypoints.add(new Waypoint(105, 174, 15, 60));
        sWaypoints.add(new Waypoint(146, 133, 15, 60));
        sWaypoints.add(new Waypoint(187, 174, 15, 60));

        sWaypoints.add(new Waypoint(105, 258, 15, 60));
        sWaypoints.add(new Waypoint(146, 290, 15, 60));
        sWaypoints.add(new Waypoint(187, 258, 15, 60));
        sWaypoints.add(new Waypoint(146, 215, 15, 60));
        sWaypoints.add(new Waypoint(105, 174, 15, 60));
        sWaypoints.add(new Waypoint(146, 133, 15, 60));
        sWaypoints.add(new Waypoint(187, 174, 15, 60));

        sWaypoints.add(new Waypoint(105, 258, 15, 60));
        sWaypoints.add(new Waypoint(146, 290, 15, 60));
        sWaypoints.add(new Waypoint(187, 258, 15, 60));
        sWaypoints.add(new Waypoint(146, 215, 15, 60));
        sWaypoints.add(new Waypoint(105, 174, 15, 60));
        sWaypoints.add(new Waypoint(146, 133, 15, 60));
        sWaypoints.add(new Waypoint(187, 174, 15, 60));

    }

    @Override
    public boolean isReversed() {
        return false;
    }
}