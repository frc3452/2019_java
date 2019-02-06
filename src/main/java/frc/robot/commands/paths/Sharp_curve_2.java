package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Sharp_curve_2 extends PathContainer {
    public Sharp_curve_2() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67, 162, 0, 60));
        sWaypoints.add(new Waypoint(135, 162, 15, 60));
        sWaypoints.add(new Waypoint(127, 196, 15, 60));
        sWaypoints.add(new Waypoint(155, 188, 15, 60));
        sWaypoints.add(new Waypoint(177, 177, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}