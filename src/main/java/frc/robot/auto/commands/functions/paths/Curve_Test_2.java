package frc.robot.auto.commands.functions.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class Curve_Test_2 extends PathContainer {
    public Curve_Test_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(19, 268, 0, 0));
        sWaypoints.add(new Waypoint(155, 268, 40, 60));
        sWaypoints.add(new Waypoint(155, 84, 40, 60));
        sWaypoints.add(new Waypoint(235, 84, 1, 60));
        sWaypoints.add(new Waypoint(325, 84, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}