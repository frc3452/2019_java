package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Sharp_curve extends PathContainer {
    public Sharp_curve() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67, 162, 0, 0));
        sWaypoints.add(new Waypoint(169, 162, 25, 30));
        sWaypoints.add(new Waypoint(207, 240, 25, 60));
        sWaypoints.add(new Waypoint(165, 298, 25, 60));
        sWaypoints.add(new Waypoint(74, 298, 1, 60));
        sWaypoints.add(new Waypoint(19, 298, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}