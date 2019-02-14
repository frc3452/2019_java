package frc.robot.auto.commands.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Curve_Test extends PathContainer {
    public Curve_Test() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(19, 268, 0, 0));
        sWaypoints.add(new Waypoint(144, 268, 40, 60));
        sWaypoints.add(new Waypoint(144, 34, 15, 60));
        sWaypoints.add(new Waypoint(268, 34, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}