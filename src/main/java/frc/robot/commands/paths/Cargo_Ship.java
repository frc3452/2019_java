package frc.robot.commands.paths;

import java.util.ArrayList;

import frc.robot.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Cargo_Ship extends PathContainer {

    public Cargo_Ship() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(67, 162, 0, 0));
        sWaypoints.add(new Waypoint(158, 162, 45, 60));
        sWaypoints.add(new Waypoint(198, 245, 32, 60));
        sWaypoints.add(new Waypoint(262, 245, 20, 60));
        sWaypoints.add(new Waypoint(262, 204, 0, 60));

    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    public Rotation2d getStartRotation() {
        return Rotation2d.fromDegrees(0);
    }
}