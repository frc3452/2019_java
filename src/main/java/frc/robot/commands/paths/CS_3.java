package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class CS_3 extends PathContainer {

    public CS_3() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(216, 263, 0, 0));
        sWaypoints.add(new Waypoint(155, 263, 25, 70));
        sWaypoints.add(new Waypoint(110, 292, 20, 70));
        sWaypoints.add(new Waypoint(72, 294, 15, 70));
        sWaypoints.add(new Waypoint(51, 294, 1, 70));
        sWaypoints.add(new Waypoint(30, 294, 0, 35));

    }

    @Override
    public boolean isReversed() {
        return false;
    }
}