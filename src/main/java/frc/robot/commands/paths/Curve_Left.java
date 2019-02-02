package frc.robot.commands.paths;

import java.util.ArrayList;
import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Curve_Left extends PathContainer {
    public Curve_Left() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(0, 0, 0, 0));
        sWaypoints.add(new Waypoint(89, 0, 15, 60));
        sWaypoints.add(new Waypoint(90, 79, 0, 60));
    }

    @Override
    public boolean isReversed() {
        return false;
    }

}