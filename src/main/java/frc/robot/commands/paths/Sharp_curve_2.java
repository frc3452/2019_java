package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Sharp_curve_2 extends PathContainer {
    public Sharp_curve_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(57, 163, 0, 0));
        sWaypoints.add(new Waypoint(120, 163, 0, 40));
        sWaypoints.add(new Waypoint(156, 163, 0, 10));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}