package frc.robot.auto.commands.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Curve_test_path extends PathContainer {
    public Curve_test_path() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(22, 205, 0, 0));
        sWaypoints.add(new Waypoint(133, 205, 30, 30));
        sWaypoints.add(new Waypoint(134, 312, 0, 30));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}