package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Straight_Path extends PathContainer {

    public Straight_Path() {
        this.sWaypoints = new ArrayList<Waypoint>();
        // sWaypoints.add(new Waypoint(0, 205, 0, 0));
        // sWaypoints.add(new Waypoint(20,205,15,60));
        // sWaypoints.add(new Waypoint(50,205,0,60));

        sWaypoints.add(new Waypoint(22,205,0,0));
        sWaypoints.add(new Waypoint(107,205,15,60));
        sWaypoints.add(new Waypoint(175,211,15,60));
        sWaypoints.add(new Waypoint(174,268,0,60));
        sWaypoints.add(new Waypoint(174,269,0,60));
    }

    @Override
    public Rotation2d getStartRotation() {
        return Rotation2d.fromDegrees(0);
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}