package frc.robot.auto.commands.paths;

import java.util.ArrayList;
import frc.robot.auto.commands.drive.pathfollowing.PathBuilder.Waypoint;
import frc.robot.auto.commands.drive.pathfollowing.PathContainer;
import frc.robot.poofs.util.math.Rotation2d;

public class Straight_Path extends PathContainer {
    public Straight_Path() {
        this.sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(0, 205, 0, 0));
        sWaypoints.add(new Waypoint(75, 205, 0, 50));
        sWaypoints.add(new Waypoint(145, 205, 0, 50));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
}