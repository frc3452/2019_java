package frc.robot.auto.commands.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Curve_Test_2 extends PathContainer {
    public Curve_Test_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(19, 268, 0, 0));
        sWaypoints.add(new Waypoint(121, 268, 40, 60));
        sWaypoints.add(new Waypoint(121, 36, 15, 60));
        sWaypoints.add(new Waypoint(184, 36, 15, 60));
        sWaypoints.add(new Waypoint(184, 137, 15, 60));
        sWaypoints.add(new Waypoint(184, 215, 15, 60));
        sWaypoints.add(new Waypoint(155, 243, 15, 60));
        sWaypoints.add(new Waypoint(155, 302, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}